package example.steps;

import example.client.ApiClient;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

public class ApiAuthSteps {

    private final ApiClient apiClient = new ApiClient();
    private Response response;

    @Given("API сервис аутентификации запущен и доступен")
    public void verifyApiIsAvailable() {
        io.restassured.RestAssured.baseURI = "https://herokuapp.com";
    }

    @When("Отправляется POST запрос на {string} с логином {string} и паролем {string}")
    public void sendPostAuthRequest(String path, String login, String password) {

        RestAssured.baseURI = "https://herokuapp.com";

        this.response = RestAssured.given()
                .header("Host", "://herokuapp.com")
                .contentType(io.restassured.http.ContentType.JSON)
                .body("{\"username\":\"" + login + "\", \"password\":\"" + password + "\"}")
                .when()
                .post(path)
                .then()
                .extract().response();
    }

    @Then("Сервер возвращает код ответа {int}")
    public void verifyStatusCode(int expectedStatusCode) {
        Assertions.assertEquals(expectedStatusCode, response.getStatusCode(),
                "Статус-код ответа не соответствует ожиданиям!");
    }

    @And("Ответ соответствует JSON схеме {string}")
    public void validateResponseSchema(io.restassured.response.Response response, String schemaFileName) {
        response.then()
                .body(io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaFileName));
    }
}
