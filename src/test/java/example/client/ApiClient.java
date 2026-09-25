package example.client;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.qameta.allure.restassured.AllureRestAssured;

public class ApiClient {

    private final RequestSpecification requestSpec;

        public ApiClient() {
            RestAssuredConfig sslConfig = RestAssuredConfig.config()
                    .sslConfig(SSLConfig.sslConfig()
                            .relaxedHTTPSValidation()
                            .allowAllHostnames());

            this.requestSpec = new RequestSpecBuilder()
                    .setBaseUri("https://restful-booker.herokuapp.com")
                    .setContentType(ContentType.JSON)
                    .addHeader("Host", "restful-booker.herokuapp.com")
                    .setConfig(sslConfig)
                    .addFilter(new AllureRestAssured())
                    .build();
        }

        /**
         * Универсальный метод отправки запроса на авторизацию
         */
        public Response sendAuthRequest(String endpoint, Object authBody) {
            return RestAssured.given()
                    .spec(requestSpec)
                    .body(authBody)
                    .when()
                    .post(endpoint);
        }
    }
