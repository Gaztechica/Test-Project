package example.tests.api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ApiTest extends BasesTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("authMatrixProvider")
    @DisplayName("Параметризованный тест эндпоинта аутентификации")
    void testAuthEndpointMatrix(String testCaseName, Object username, Object password, int expectedStatusCode) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("password", password);

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/auth")
                .then()
                .statusCode(expectedStatusCode)
                .body(matchesJsonSchemaInClasspath("response-token.json"));
    }

    private static Stream<Arguments> authMatrixProvider() {
        return Stream.of(
                // 1. Эквивалентное разбиение
                arguments("AUTH-001 | Валидные креды", "admin", "password123", 200),
                arguments("AUTH-002 | Невалидный логин", "wrong_admin", "password123", 401),
                arguments("AUTH-003 | Невалидный пароль", "admin", "wrong_pass", 401),
                // 3. Граничные значения
                arguments("AUTH-004 | Пустой username", "", "password123", 400),
                arguments("AUTH-005 | Пустой password", "admin", "", 400),
                arguments("AUTH-006 | Длинное username", "a".repeat(1000), "password123", 400),
                // 4. Попарное тестирование аномальных типов данных
                arguments("AUTH-007 | Username равен null", null, "password123", 400),
                arguments("AUTH-008 | Password равен null", "admin", null, 400),
                arguments("AUTH-009 | Username типа Integer", 12345, "password123", 400, "reason", "Bad credentials"),
                arguments("AUTH-010 | Password типа Boolean", "admin", true, 400, "reason", "Bad credentials"),
                arguments("AUTH-011 | Username и Password типа массива", "[admin]", "[password123]", 400, "reason", "Bad credentials"),
                arguments("AUTH-012 | SQL-инъекций", "' OR '1'='1", "' OR '1'='1", 400, "reason", "Bad credentials"),
                arguments("AUTH-013 | XSS-инъекция в логине", "<script>alert(1)</script>", "' OR '1'='1", 400, "reason", "Bad credentials"),
                arguments("AUTH-014 | Неверный регистр логина", "ADMIN", "password123", 401, "reason", "Bad credentials"),
                arguments("AUTH-015 | Пропущено поле password", "admin", 401, "reason", "Bad credentials"));
    }

    @Test
    @DisplayName("AUTH-016 Проверка  Неверный HTTP-метод GET")
    public void testInvalidMethodGet() {

        given()
                .when()
                .get("/auth")
                .then()
                .statusCode(anyOf(is(400), is(405)));
    }

    @Test
    @DisplayName("AUTH-017 Проверка на неверный заголовок Content-Type text/plain")
    public void testInvalidContentType() {

        given()
                .contentType(ContentType.TEXT)
                .when()
                .post("/auth")
                .then()
                .statusCode(500);
    }
}
