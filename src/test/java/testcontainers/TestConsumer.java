package testcontainers;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class TestConsumer {
    private final KafkaConsumer<String, String> consumer;
    private final String jdbcUrl;
    private final String dbUser;
    private final String dbPassword;

    public TestConsumer(String bootstrapServers, String topic, String jdbcUrl, String dbUser, String dbPassword) {
        this.jdbcUrl = jdbcUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        this.consumer = new KafkaConsumer<>(props);
        this.consumer.subscribe(Collections.singletonList(topic));
    }

    // Читает накопившиеся сообщения и обрабатывает их с защитой от дублей
    public void consumeAndProcess() {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(2000));

        String sql = "INSERT INTO processed_orders (order_id, status) VALUES (?, ?) " +
                "ON CONFLICT (order_id) DO NOTHING"; // Идемпотентная вставка

        try (Connection conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (ConsumerRecord<String, String> record : records) {
                String orderId = record.key();
                // В реальной жизни здесь бы парсился JSON: {"orderId": "123", "status": "PAID"}
                String status = "PAID";

                pstmt.setString(1, orderId);
                pstmt.setString(2, status);
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка работы с БД при консюминге", e);
        }
    }

    public void close() {
        consumer.close();
    }
}

