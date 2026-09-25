package example.client;

import example.tests.api.models.AuthRequest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiClient {

    private final RequestSpecification requestSpec;

    public ApiClient() {
        this.requestSpec = new io.restassured.builder.RequestSpecBuilder()
                .setBaseUri("https://reqres.in")
                .setContentType(io.restassured.http.ContentType.JSON)
                .setConfig(io.restassured.config.RestAssuredConfig.config()
                        .sslConfig(io.restassured.config.SSLConfig.sslConfig()
                                .relaxedHTTPSValidation()
                                .allowAllHostnames()))
                .addFilter(new io.qameta.allure.restassured.AllureRestAssured())
                .build();
    }

    public Response sendAuthRequest(String endpoint, AuthRequest authBody) {
        return RestAssured.given()
                .config(io.restassured.config.RestAssuredConfig.config()
                        .sslConfig(io.restassured.config.SSLConfig.sslConfig().relaxedHTTPSValidation()))
                .contentType(io.restassured.http.ContentType.JSON)
                .body(authBody)
                .when()
                .post(endpoint);
    }
}
