package testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseIntegrationTest {

    @Container
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    protected static final GenericContainer<?> redpanda = new GenericContainer<>("redpandadata/redpanda:v23.3.5")
            // Передаем аргументы раздельно, чтобы Docker корректно их распарсил
            .withCommand(
                    "redpanda", "start",
                    "--smp", "1",
                    "--memory", "256M",
                    "--reserve-memory", "0M",
                    "--overprovisioned",
                    "--node-id", "0",
                    "--kafka-addr", "PLAINTEXT://0.0.0.0:29092,OUTSIDE://0.0.0.0:9092",
                    "--advertise-kafka-addr", "PLAINTEXT://localhost:29092,OUTSIDE://localhost:9092"
            )
            .withExposedPorts(9092);

    protected static String getKafkaBootstrapServers() {
        // Redpanda прокидывает OUTSIDE (9092) порт наружу на случайный свободный порт хоста
        return "localhost:" + redpanda.getMappedPort(9092);
    }

    protected static String getDbJdbcUrl() {
        return postgres.getJdbcUrl();
    }
}
