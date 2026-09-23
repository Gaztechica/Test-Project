package test.sauceDemoSelenide.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class CartPage {
    private final SelenideElement checkoutButton = $("#checkout");

    public void clickCheckout() { checkoutButton.click(); }
}

