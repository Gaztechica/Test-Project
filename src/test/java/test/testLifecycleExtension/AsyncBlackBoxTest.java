//package test.testLifecycleExtension;
//
//import io.qameta.allure.Description;
//import io.qameta.allure.Step;
//import io.restassured.RestAssured;
//import lombok.extern.slf4j.Slf4j;
//import org.awaitility.Awaitility;
//import org.awaitility.core.ConditionTimeoutException;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.slf4j.MDC;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.time.Duration;
//import java.util.UUID;
//
//@Slf4j
//public class AsyncBlackBoxTest extends BaseE2ETest {
//
//    private boolean isEventProcessed(String requestId) {
//        String sql = "SELECT COUNT(*) FROM events WHERE request_id = ? AND status = 'PROCESSED'";
//        try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, requestId);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt(1) == 1;
//                }
//            }
//        } catch (Exception e) {
//            return false;
//        }
//        return false;
//    }
//
//    @Test
//    @Description("Успешный E2E сценарий с валидацией сквозного ID трассировки")
//    void testPositiveE2E_ShouldAsyncProcessEventInDatabase() {
//        String uniqueRequestId = UUID.randomUUID().toString();
//
//        // Шаг 1: Извлекаем сгенерированный traceId из MDC текущего теста
//        String currentTraceId = MDC.get("traceId");
//        log.info("Старт теста. Сгенерированный Trace ID: {}", currentTraceId);
//
//        // Вызов обернутого метода-шага
//        sendPostTriggerRequest(uniqueRequestId, currentTraceId);
//
//        log.info("Запрос принят. Запуск Awaitility...");
//
//        Awaitility.await()
//                .atMost(Duration.ofSeconds(3))
//                .pollInterval(Duration.ofMillis(200))
//                .until(() -> isEventProcessed(uniqueRequestId));
//
//        log.info("Событие успешно обработано.");
//    }
//
//    @Test
//    @Description("Тест падения по таймауту для проверки генерации вложений Allure при сбоях")
//    void testNegative_ShouldThrowTimeoutExceptionWhenWorkerFailsToDeliver() {
//        String uniqueRequestId = UUID.randomUUID().toString();
//        String currentTraceId = MDC.get("traceId");
//
//        sendPostTriggerRequest(uniqueRequestId, currentTraceId);
//
//        // Намеренно заставляем упасть с ошибкой таймаута для демонстрации Листнера Allure
//        Assertions.assertThrows(ConditionTimeoutException.class, () -> {
//            Awaitility.await()
//                    .atMost(Duration.ofMillis(100)) // Провоцируем падение теста
//                    .until(() -> isEventProcessed(uniqueRequestId));
//        });
//    }
//
//    //  Проброс X-Trace-Id в заголовки вызовов
//    @Step("Отправка HTTP POST запроса на /trigger с X-Trace-Id = {traceId}")
//    private void sendPostTriggerRequest(String requestId, String traceId) {
//        RestAssured.given()
//                .header("X-Request-Id", requestId)
//                .header("X-Trace-Id", traceId)
//                .when()
//                .post("/trigger")
//                .then()
//                .statusCode(200);
//    }
//}
//
