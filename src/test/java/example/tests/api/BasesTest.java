package example.tests.api;

import example.config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;


import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class BasesTest {

    public static String authToken;

    @BeforeEach
    public void globalSetup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", ConfigReader.get("auth.username"));
        credentials.put("password", ConfigReader.get("auth.password"));

        authToken = given()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}

