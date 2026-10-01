package test.baseE2E;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

public class AsyncBlackBoxHandler implements HttpHandler {
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public AsyncBlackBoxHandler(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        // Извлекаем кастомный HTTP-заголовок
        String requestId = exchange.getRequestHeaders().getFirst("X-Request-Id");

        // Негативный сценарий: если заголовка нет, возвращаем 400 Bad Request
        if (requestId == null || requestId.trim().isEmpty()) {
            exchange.sendResponseHeaders(400, -1);
            return;
        }

        // Возвращаем HTTP 200 OK немедленно, закрывая соединение (имитация асинхронного REST)
        exchange.sendResponseHeaders(200, -1);
        exchange.close();

        // Эмулируем асинхронную цепочку (HTTP -> Kafka -> Worker -> DB) вне основного потока ответа
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1500); // Имитация задержки передачи по Kafka и обработки воркером

                String sql = "INSERT INTO events (request_id, status) VALUES (?, 'PROCESSED')";
                try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, requestId);
                    pstmt.executeUpdate();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

