package test.producer;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class IdempotencyTest {
    private static final String TOPIC = "order-events";
    private Connection dbConnection;
    private TestProducer producer;
    private TestConsumer consumer;

    static {
        System.setProperty("docker.host", "tcp://127.0.0.1:2375");
        System.setProperty("testcontainers.ryuk.disabled", "true");
    }

    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS orders (order_id VARCHAR(50) PRIMARY KEY, status VARCHAR(20));";
    private static final String TRUNCATE_TABLE_SQL =
            "TRUNCATE TABLE orders;";
    private static final String SELECT_COUNT_SQL =
            "SELECT COUNT(*) AS row_count FROM orders WHERE order_id = '123'";
    
    private static final GenericContainer<?> redpanda = new GenericContainer<>(DockerImageName.parse("://redpanda.com"))
            .withCommand("redpanda start --mode dev-container --kafka-addr internal://0.0.0.0:9092,external://0.0.0.0:19092 --advertise-kafka-addr internal://127.0.0.1:9092,external://localhost:19092")
            .withExposedPorts(19092);

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @BeforeAll
    static void startContainers() {
        postgres.start();
        redpanda.start();
    }

    @AfterAll
    static void stopContainers() {
        postgres.stop();
        redpanda.stop();
    }

    @BeforeEach
    void setUp() throws Exception {
        String bootstrapServers = redpanda.getHost() + ":" + redpanda.getMappedPort(19092);
        dbConnection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

        try (Statement stmt = dbConnection.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
            stmt.execute(TRUNCATE_TABLE_SQL);
        }

        producer = new TestProducer(bootstrapServers);
        consumer = new TestConsumer(bootstrapServers, dbConnection);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (producer != null) {
            producer.close();
        }
        if (consumer != null) {
            consumer.close();
        }
        if (dbConnection != null && !dbConnection.isClosed()) {
            dbConnection.close();
        }
    }

    @Test
    @DisplayName("Тест на идемпотентность: отправка сообщения 3 раза не должна дублировать запись в БД")
    void testIdempotentOrderProcessing() throws Exception {
        String orderId = "123";
        String jsonMessage = "{\"orderId\": \"123\", \"status\": \"PAID\"}";

        producer.sendOrderEvent(TOPIC, orderId, jsonMessage);
        producer.sendOrderEvent(TOPIC, orderId, jsonMessage);
        producer.sendOrderEvent(TOPIC, orderId, jsonMessage);

        consumer.consumeAndSave(TOPIC);

        try (Statement stmt = dbConnection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_COUNT_SQL)) {

            Assertions.assertTrue(rs.next(), "Результат агрегатного запроса COUNT не должен быть пустым");
            int rowCount = rs.getInt("row_count");

            Assertions.assertEquals(1, rowCount,
                    "Критическая ошибка: Защита от дублей не сработала! В БД создано более одной записи.");
        }
    }
}