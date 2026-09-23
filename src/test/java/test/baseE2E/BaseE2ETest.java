//package test.baseE2E;
//
//import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;
//import com.sun.net.httpserver.HttpServer;
//import io.restassured.RestAssured;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.Executors;
//
////@Slf4j
//@Testcontainers
//public abstract class BaseE2ETest {
//    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BaseE2ETest.class);
//
//    static {
//        System.setProperty("docker.host", "npipe:////./pipe/docker_engine");
//    }
//
//
//    @Container
//    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
//            .withDatabaseName("testdb")
//            .withUsername("test")
//            .withPassword("test")
//            .withInitScript("init.sql"); // Выполнение скрипта CREATE TABLE при старте
//
//    private HttpServer server;
//
//    @BeforeEach
//    void startInfrastructure() throws Exception {
//        // Очищаем таблицу перед каждым тестом для изоляции тестов
//        try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
//             PreparedStatement pstmt = conn.prepareStatement("TRUNCATE TABLE events")) {
//            pstmt.executeUpdate();
//        }
//
//        // Поднимаем HTTP-сервер на случайном свободном порту
//        server = HttpServer.create(new InetSocketAddress(0), 0);
//
//        // Регистрируем эмулятор "черного ящика" AsyncBlackBox
//        server.createContext("/trigger", new AsyncBlackBoxHandler());
//        server.setExecutor(Executors.newFixedThreadPool(4));
//        server.start();
//
//        // Настраиваем порт по умолчанию для RestAssured
//        RestAssured.port = server.getAddress().getPort();
//        log.info("Инфраструктура готова. HTTP сервер запущен на порту: {}", RestAssured.port);
//    }
//
//    @AfterEach
//    void stopInfrastructure() {
//        if (server != null) {
//            server.stop(0);
//        }
//    }
//
//    private static class AsyncBlackBoxHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange exchange) throws IOException {
//            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
//                exchange.sendResponseHeaders(405, -1);
//                return;
//            }
//
//            // Извлечение обязательного HTTP-заголовка X-Request-Id
//            String requestId = exchange.getRequestHeaders().getFirst("X-Request-Id");
//
//            // Имитация валидации: если заголовка нет — отдаем 400 Bad Request
//            if (requestId == null || requestId.trim().isEmpty()) {
//                log.warn("Валидация провалена: Отсутствует заголовок X-Request-Id");
//                exchange.sendResponseHeaders(400, -1);
//                exchange.close();
//                return;
//            }
//
//            // Имитация мгновенного ответа от REST-контроллера (HTTP 20X)
//            exchange.sendResponseHeaders(200, -1);
//            exchange.close();
//
//            // Эмулируем асинхронную цепочку: HTTP -> Kafka -> Worker
//            CompletableFuture.runAsync(() -> {
//                try {
//                    log.info("[Worker] Получено сообщение из Kafka. Начинаем обработку для ID: {}", requestId);
//                    Thread.sleep(1500); // Имитация задержки передачи по Kafka и обработки воркером
//
//                    // Эмулируем запись "воркером" в БД events
//                    String sql = "INSERT INTO events (request_id, status) VALUES (?, 'PROCESSED')";
//                    try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
//                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
//                        pstmt.setString(1, requestId);
//                        pstmt.executeUpdate();
//                        log.info("[Worker] Данные успешно зафиксированы в PostgreSQL для ID: {}", requestId);
//                    }
//                } catch (Exception e) {
//                    log.error("[Worker] Критическая ошибка асинхронного воркера", e);
//                }
//            });
//        }
//    }
//}
