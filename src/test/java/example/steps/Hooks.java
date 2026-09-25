package example.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import test.sauceDemoSelenium.baseTest.SeleniumBaseTest;
import example.tests.api.BasesTest;

import java.io.ByteArrayInputStream;

public class Hooks {

    @BeforeEach
    public void globalSetup() {
        RestAssured.baseURI = "https://herokuapp.com";

        RestAssured.config = RestAssuredConfig.config()
                .sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation());
    }

    @Before("@ui")
    public void setupUITests() {
        SeleniumBaseTest.initDriver();
    }

    @Before("@api")
    public void setupApiTests() {
        BasesTest basesTest = new BasesTest();
        basesTest.globalSetup();
    }

    @After("@ui")
    public void tearDownUITests(Scenario scenario) {
        if (scenario.isFailed() && SeleniumBaseTest.driver != null) {
            byte[] screenshot = ((TakesScreenshot) SeleniumBaseTest.driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Failure Screenshot", new ByteArrayInputStream(screenshot));
        }
        SeleniumBaseTest.quitDriver();
    }
}
