package example.tests.ui;

import example.pages.LoginPage;
import example.tests.BaseTest;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class LoginParametrizedTest extends BaseTest {

    private final LoginPage loginPage = new LoginPage();

    private static Stream<Arguments> loginInvalidDataProvider() {
        String targetError = "Your username is invalid!";
        String redColor = "rgba(255, 255, 255, 1)";

        return Stream.of(
                arguments("", "", targetError, redColor, "Пустые поля"),
                arguments("a", "b", targetError, redColor, "1 символ"),
                arguments("!@#$%^", "!@#$%^", targetError, redColor, "Спецсимволы"),
                arguments("' OR 1=1 --", "' OR 1=1 --", targetError, redColor, "SQL-инъекция")
        );
    }

    @ParameterizedTest(name = "{index} ==> Тест-кейс: {4}")
    @MethodSource("loginInvalidDataProvider")
    public void testInvalidLoginShowsBusinessError(String username, String password, String expectedError, String expectedColor, String caseDescription) {
        loginPage.openPage()
                .loginWith(username, password)
                .verifyErrorMessage(expectedError)
                .verifyErrorColor(expectedColor);
    }

    @Test
    @Description("Позитивный сценарий авторизации с валидными данными")
    public void testSuccessfulLoginShowsSecureArea() {
        //  высокоуровневая цепочка шагов (Fluent API)
        loginPage.openPage()
                .loginWithValidData("tomsmith", "SuperSecretPassword!")
                .verifySuccessMessage("You logged into a secure area!")
                .verifyHeader("Secure Area")
                .clickLogout()
                .verifyErrorMessage("You logged out of the secure area!");
    }
}
