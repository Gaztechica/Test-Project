package producer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class TestConsumer implements AutoCloseable {

    private static final String UPSERT_ORDER_SQL =
            "INSERT INTO orders (order_id, status) VALUES (?, ?) " +
                    "ON CONFLICT (order_id) DO UPDATE SET status = EXCLUDED.status";

    private final KafkaConsumer<String, String> consumer;
    private final Connection connection;

    public TestConsumer(String bootstrapServers, Connection connection) {
        this.connection = connection;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        this.consumer = new KafkaConsumer<>(props);
    }

    public void consumeAndSave(String topic) throws SQLException {
        consumer.subscribe(Collections.singletonList(topic));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(2000));

        try (PreparedStatement pstmt = connection.prepareStatement(UPSERT_ORDER_SQL)) {
            for (ConsumerRecord<String, String> record : records) {
                String orderId = record.key();
                String status = record.value().contains("\"status\": \"PAID\"") ? "PAID" : "UNKNOWN";

                pstmt.setString(1, orderId);
                pstmt.setString(2, status);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    @Override
    public void close() {
        if (consumer != null) {
            consumer.close();
        }
    }
}