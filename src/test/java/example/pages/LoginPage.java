package example.pages;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import test.herokuTables.SecureAreaPage;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class LoginPage {

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement submitButton = $("#login button[type='submit']");
    private final SelenideElement flashMessage = $("#flash");

    @Step("Открыть страницу авторизации")
    public LoginPage openPage() {
        open("http://the-internet.herokuapp.com/login");
        return this;
    }

    @Step("Ввести логин: '{username}' и пароль: '{password}'")
    public LoginPage loginWith(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return this;
    }

    @Step("Проверить, что отображается бизнес-ошибка: '{expectedErrorMessage}'")
    public LoginPage verifyErrorMessage(String expectedErrorMessage) {
        flashMessage.shouldBe(Condition.visible)
                .shouldHave(Condition.text(expectedErrorMessage));
        return this;
    }

    @Step("Проверить, что цвет сообщения об ошибке строго равен '{expectedRgbColor}'")
    public LoginPage verifyErrorColor(String expectedRgbColor) {
        // Кастомное условие Selenide с защитой от недогрузившихся стилей
        flashMessage.should(textColorIs(expectedRgbColor), Duration.ofSeconds(10));
        return this;
    }

    @Step("Выполнить успешный вход с валидным логином: '{username}'")
    public SecureAreaPage loginWithValidData(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();

        return new SecureAreaPage();
    }

    /**
     * 3. Кастомное условие (Condition) для проверки цвета
     */
    public static Condition textColorIs(String expectedRgbColor) {
        return new Condition("textColorIs") {
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                if (!element.isDisplayed()) {
                    return CheckResult.rejected("Элемент еще не отображается", element.getAttribute("outerHTML"));
                }
                String actualColor = element.getCssValue("color");
                boolean met = actualColor.equalsIgnoreCase(expectedRgbColor);
                return new CheckResult(met, String.format("Ожидался цвет: %s, но был: %s", expectedRgbColor, actualColor));
            }
        };
    }
}
