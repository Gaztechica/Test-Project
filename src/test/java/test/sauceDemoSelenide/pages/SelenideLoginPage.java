package test.sauceDemoSelenide.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class SelenideLoginPage {
    private final SelenideElement usernameField = $("#user-name");
    private final SelenideElement passwordField = $("#password");
    private final SelenideElement loginButton = $("#login-button");

    public void enterUsername(String username) {
        usernameField.setValue(username);
    }

    public void enterPassword(String password) {
        passwordField.setValue(password);
    }

    public void clickLogin() {
        loginButton.click();
    }
}


