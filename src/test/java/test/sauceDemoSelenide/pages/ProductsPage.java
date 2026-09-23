package test.sauceDemoSelenide.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class ProductsPage {
    private final SelenideElement backpackAddToCartButton = $("#add-to-cart-sauce-labs-backpack");
    private final SelenideElement bikeLightAddToCartButton = $("#add-to-cart-sauce-labs-bike-light");
    private final SelenideElement cartButton = $(".shopping_cart_link");
    private final SelenideElement pageTitle = $(".title");

    public void addBackpackToCart() { backpackAddToCartButton.click(); }
    public void addBikeLightToCart() { bikeLightAddToCartButton.click(); }
    public void clickCart() { cartButton.click(); }
    public SelenideElement getPageTitle() { return pageTitle; }
}

