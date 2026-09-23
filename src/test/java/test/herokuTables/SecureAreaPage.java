package test.herokuTables;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import example.pages.LoginPage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class SecureAreaPage {

    // Локаторы, которые есть ТОЛЬКО в личном кабинете
    private final SelenideElement successFlashMessage = $("#flash");
    private final SelenideElement pageHeader = $("h2");
    private final SelenideElement logoutButton = $(".button.secondary.radius");

    @Step("Проверить, что отображается сообщение об успешном входе: '{expectedMessage}'")
    public SecureAreaPage verifySuccessMessage(String expectedMessage) {
        successFlashMessage.shouldBe(Condition.visible)
                .shouldHave(Condition.text(expectedMessage));
        return this; // Остаемся на этой же странице
    }

    @Step("Проверить, что заголовок личного кабинета равен '{expectedHeader}'")
    public SecureAreaPage verifyHeader(String expectedHeader) {
        pageHeader.shouldBe(Condition.visible)
                .shouldHave(Condition.text(expectedHeader));
        return this;
    }

    @Step("Нажать кнопку 'Logout' для выхода из системы")
    public LoginPage clickLogout() {
        logoutButton.click();
        return new LoginPage(); // Бесшовно возвращаем пользователя обратно на страницу логина!
    }
}

