package test.sauceDemoSelenide.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class CheckoutPage {
    private final SelenideElement firstNameField = $("#first-name");
    private final SelenideElement lastNameField = $("#last-name");
    private final SelenideElement postalCodeField = $("#postal-code");
    private final SelenideElement continueButton = $("#continue");
    private final SelenideElement finishButton = $("#finish");
    private final SelenideElement completeHeader = $(".complete-header");

    public void enterFirstName(String firstName) { firstNameField.setValue(firstName); }
    public void enterLastName(String lastName) { lastNameField.setValue(lastName); }
    public void enterPostalCode(String postalCode) { postalCodeField.setValue(postalCode); }
    public void clickContinue() { continueButton.click(); }
    public void clickFinish() { finishButton.click(); }
    public SelenideElement getCompleteHeader() { return completeHeader; }
}

