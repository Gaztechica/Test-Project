package test.baseE2E;

import io.restassured.RestAssured;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.UUID;

//@Slf4j
public class AsyncBlackBoxTest extends BaseE2ETest {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AsyncBlackBoxTest.class);

    private boolean isEventProcessed(String requestId) {
        String sql = "SELECT COUNT(*) FROM events WHERE request_id = ? AND status = 'PROCESSED'";
        try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, requestId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 1;
                }
            }
        } catch (Exception e) {
            log.warn("Опрос БД вернул ошибку связи (нормально для асинхронных тестов): {}", e.getMessage());
            return false;
        }
        return false;
    }

    /**
     * ✅ 2. Позитивный E2E-тест:
     * Отправляет корректный запрос с заголовком, получает 200, а затем с помощью Awaitility
     * не блокирующе дожидается появления статуса PROCESSED в базе данных.
     */
    @Test
    void testPositiveE2E_ShouldAsyncProcessEventInDatabase() {
        String uniqueRequestId = UUID.randomUUID().toString();
        log.info("=== Старт позитивного E2E-теста для ID: {} ===", uniqueRequestId);

        // Отправка HTTP-запроса через RestAssured
        RestAssured.given()
                .header("X-Request-Id", uniqueRequestId)
                .when()
                .post("/trigger")
                .then()
                .statusCode(200); // Ожидаем успешный код 20X от REST-слоя

        log.info("HTTP статус 200 получен. Начинаем неблокирующее ожидание через Awaitility...");

        // Использование конструкции Awaitility.await().atMost(...).until(...)
        Awaitility.await()
                .atMost(Duration.ofSeconds(3))       // Лимит ожидания (воркеру нужно 1.5с)
                .pollInterval(Duration.ofMillis(200)) // Частота неблокирующего опроса СУБД
                .conditionEvaluationListener(condition ->
                        log.info("Проверка записи в БД. Найдено: {}. Прошло времени: {} мс",
                                condition.isSatisfied(), condition.getElapsedTimeInMS())
                )
                .until(() -> isEventProcessed(uniqueRequestId));

        log.info("=== Позитивный E2E-тест успешно пройден! Запись найдена ===");
    }

    /**
     * ❌ 3. Негативный тест (Вариант А — Сбой воркера / таймаут):
     * Эмулирует ситуацию, когда REST ответил 200, но данные до БД не дошли.
     * Проверяет, что тест падает строго по таймауту ConditionTimeoutException и не зависает навсегда.
     */
    @Test
    void testNegative_ShouldThrowTimeoutExceptionWhenWorkerFailsToDeliver() {
        String uniqueRequestId = UUID.randomUUID().toString();
        log.info("=== Старт негативного теста таймаута для ID: {} ===", uniqueRequestId);

        RestAssured.given()
                .header("X-Request-Id", uniqueRequestId)
                .when()
                .post("/trigger")
                .then()
                .statusCode(200);

        // Убеждаемся, что Awaitility корректно выбрасывает ConditionTimeoutException,
        // если ограничить лимит времени (atMost) значением меньше, чем задержка воркера (1500мс)
        Assertions.assertThrows(ConditionTimeoutException.class, () -> {
            Awaitility.await()
                    .atMost(Duration.ofMillis(500)) // Заведомо невыполнимый для воркера лимит времени
                    .until(() -> isEventProcessed(uniqueRequestId));
        }, "Тест должен был выбросить ConditionTimeoutException из-за жесткого лимита времени");

        log.info("=== Негативный тест таймаута пройден: Awaitility успешно прервал тест ===");
    }

    /**
     * ❌ 3. Негативный тест (Вариант Б — Ошибка входных данных):
     * Намеренно не передаем обязательный заголовок X-Request-Id.
     * Проверяет моментальную реакцию RestAssured на ошибку валидации 400 Bad Request.
     */
    @Test
    void testNegative_ShouldFailOnRestAssuredValidationWhenHeaderIsMissing() {
        log.info("=== Старт негативного теста валидации заголовков ===");

        RestAssured.given()
                // Намеренно НЕ передаем заголовок X-Request-Id
                .when()
                .post("/trigger")
                .then()
                .statusCode(400); // Проверка падения/прохождения на уровне RestAssured Response

        log.info("=== Негативный тест валидации пройден: получен статус 400 ===");
    }
}
