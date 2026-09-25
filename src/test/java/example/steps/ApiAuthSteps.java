package example.steps;

import example.client.ApiClient;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Map;

public class ApiAuthSteps {

    private final ApiClient apiClient = new ApiClient();
    private Response response;

    @Given("API сервис аутентификации запущен и доступен")
    public void verifyApiIsAvailable() {
        io.restassured.RestAssured.baseURI = "https://herokuapp.com";
    }

    @When("Отправляется POST запрос на {string} с логином {string} и паролем {string}")
    public void sendPostAuthRequest(String path, String login, String password) {

        Map<String, String> authBody = new HashMap<>();
        authBody.put("username", login);
        authBody.put("password", password);

        this.response = apiClient.sendAuthRequest(path, authBody);
    }

    @Then("Сервер возвращает код ответа {int}")
    public void verifyStatusCode(int expectedStatusCode) {
        Assertions.assertEquals(expectedStatusCode, response.getStatusCode(),
                "Статус-код ответа не соответствует ожиданиям!");
    }

    @And("Ответ соответствует JSON схеме {string}")
    public void validateResponseSchema(String schemaFileName) {
        this.response.then()
                .body(io.restassured.module.jsv.JsonSchemaValidator
                        .matchesJsonSchemaInClasspath(schemaFileName));
    }
}
