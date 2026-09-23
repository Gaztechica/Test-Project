package test.sauceDemoSelenide.steps;

import test.sauceDemoSelenide.pages.CartPage;
import test.sauceDemoSelenide.pages.CheckoutPage;
import test.sauceDemoSelenide.pages.ProductsPage;
import test.sauceDemoSelenide.pages.SelenideLoginPage;

public class SauceDemoSteps {
    private final SelenideLoginPage loginPage = new SelenideLoginPage();
    private final ProductsPage productsPage = new ProductsPage();
    private final CartPage cartPage = new CartPage();
    private final CheckoutPage checkoutPage = new CheckoutPage();

    public void login(String username, String password) {
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();
    }

    public void addTwoItemsToCartAndGoToCart() {
        productsPage.addBackpackToCart();
        productsPage.addBikeLightToCart();
        productsPage.clickCart();
    }

    public void proceedToCheckout() {
        cartPage.clickCheckout();
    }

    public void fillCheckoutDataAndFinish(String firstName, String lastName, String zip) {
        checkoutPage.enterFirstName(firstName);
        checkoutPage.enterLastName(lastName);
        checkoutPage.enterPostalCode(zip);
        checkoutPage.clickContinue();
        checkoutPage.clickFinish();
    }

    public void verifyUserIsOnProductsPage() {
        productsPage.getPageTitle().shouldHave(com.codeborne.selenide.Condition.text("Products"));
    }

    public void verifyOrderIsSuccessfullyCompleted() {
        checkoutPage.getCompleteHeader().shouldHave(com.codeborne.selenide.Condition.text("Thank you for your order!"));
    }

}
