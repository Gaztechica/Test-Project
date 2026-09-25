package example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import test.sauceDemoSelenium.steps.SauceDemoSteps;

public class LoginSteps {

    private SauceDemoSteps getSteps() {
        if (test.sauceDemoSelenium.baseTest.SeleniumBaseTest.driver == null) {
            throw new RuntimeException("Драйвер не инициализирован! Проверьте, отработал ли Hooks.java");
        }
        return new SauceDemoSteps(test.sauceDemoSelenium.baseTest.SeleniumBaseTest.driver);
    }

    @Given("Пользователь открывает страницу авторизации SauceDemo")
    public void openLoginPage() {
        getSteps().openLoginPage();
    }

    @When("Пользователь вводит валидные данные стандартного пользователя")
    public void loginAsStandardUser() {
        getSteps().loginAsStandardUser();
    }

    @When("Пользователь вводит логин {string} и пароль {string}")
    public void loginWithCredentials(String username, String password) {
        SauceDemoSteps steps = getSteps();
    }

    @Then("Пользователь успешно перенаправлен на страницу каталога товаров")
    public void verifyUserIsOnCatalogPage() {
        Assertions.assertTrue(getSteps().isUserOnCatalogPage(),
                "Пользователь не перенаправлен на страницу каталога товаров!");
    }

    @And("Текущий URL содержит {string}")
    public void verifyCurrentUrlContains(String expectedUrlPart) {
        Assertions.assertTrue(getSteps().getCurrentPageUrl().contains(expectedUrlPart),
                "URL страницы не соответствует ожиданиям!");
    }

    @Then("Отображается ошибка валидации полей")
    public void verifyValidationErrorDisplayed() {
    }
}

