package testLifecycleExtension;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Slf4j
@Testcontainers
@ExtendWith(TestLifecycleExtension.class) //  Регистрируем наш Allure Listener и MDC TraceId генератор
public abstract class BaseE2ETest {

    @Container
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init.sql");

    private HttpServer server;

    @BeforeEach
    void startInfrastructure() throws Exception {
        try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             PreparedStatement pstmt = conn.prepareStatement("TRUNCATE TABLE events")) {
            pstmt.executeUpdate();
        }

        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/trigger", new AsyncBlackBoxHandler());
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        RestAssured.port = server.getAddress().getPort();

        //  Автоматическая отправка логов запросов в Allure и подстановка Trace-Id по умолчанию
        RestAssured.filters(new AllureRestAssured());
    }

    @AfterEach
    void stopInfrastructure() {
        if (server != null) {
            server.stop(0);
        }
    }

    private static class AsyncBlackBoxHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String requestId = exchange.getRequestHeaders().getFirst("X-Request-Id");
            // Читаем проброшенный сквозной traceId из HTTP-заголовка
            String incomingTraceId = exchange.getRequestHeaders().getFirst("X-Trace-Id");

            if (requestId == null || requestId.trim().isEmpty()) {
                exchange.sendResponseHeaders(400, -1);
                exchange.close();
                return;
            }

            exchange.sendResponseHeaders(200, -1);
            exchange.close();

            // Передаем контекст traceId внутрь асинхронного потока воркера
            CompletableFuture.runAsync(() -> {
                // Переносим traceId воркера в его MDC, чтобы логи воркера размечались тем же ID
                if (incomingTraceId != null) {
                    MDC.put("traceId", incomingTraceId);
                }
                try {
                    log.info("[Worker] Получен сигнал. Эмуляция Kafka-транспорта...");
                    Thread.sleep(1500);

                    String sql = "INSERT INTO events (request_id, status) VALUES (?, 'PROCESSED')";
                    try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, requestId);
                        pstmt.executeUpdate();
                        log.info("[Worker] Данные успешно сохранены в PostgreSQL.");
                    }
                } catch (Exception e) {
                    log.error("[Worker] Ошибка работы воркера", e);
                } finally {
                    MDC.clear();
                }
            });
        }
    }
}

