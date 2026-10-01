package testcontainers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IdempotencyTest extends BaseIntegrationTest {

    private static final String TOPIC = "order-events";
    private TestProducer producer;
    private TestConsumer consumer;

    @BeforeEach
    void setUp() throws Exception {
        // 1. Инициализируем таблицу в поднятом Postgres контейнере
        try (Connection conn = DriverManager.getConnection(getDbJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS processed_orders (" +
                    "order_id VARCHAR(50) PRIMARY KEY, " +
                    "status VARCHAR(50));");
            stmt.execute("TRUNCATE TABLE processed_orders;"); // Очищаем перед тестом
        }

        // 2. Создаем клиентов
        producer = new TestProducer(getKafkaBootstrapServers(), TOPIC);
        consumer = new TestConsumer(getKafkaBootstrapServers(), TOPIC, getDbJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    }

    @AfterEach
    void tearDown() {
        if (producer != null) producer.close();
        if (consumer != null) consumer.close();
    }

    @Test
    void testConsumerIdempotencyWithDuplicateMessages() throws Exception {
        String orderId = "123";
        String jsonMessage = "{\"orderId\": \"123\", \"status\": \"PAID\"}";

        // Действие: Отправляем ОДНО И ТО ЖЕ сообщение 3 раза подряд (симулируем сетевой сбой / повтор)
        producer.sendEvent(orderId, jsonMessage);
        producer.sendEvent(orderId, jsonMessage);
        producer.sendEvent(orderId, jsonMessage);

        // Действие: Вызываем вычитку и обработку консьюмером
        consumer.consumeAndProcess();

        // Проверка: Убеждаемся, что в базе данных создана только 1 запись, несмотря на 3 дубля
        try (Connection conn = DriverManager.getConnection(getDbJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM processed_orders WHERE order_id = '123'")) {

            if (rs.next()) {
                int count = rs.getInt(1);
                // дублирования в БД не произошло
                assertEquals(1, count, "Данные задублировались! Защита от дублей не сработала.");
            }
        }
    }
}

