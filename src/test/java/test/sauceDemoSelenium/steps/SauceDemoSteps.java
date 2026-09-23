package test.sauceDemoSelenium.steps;

import org.openqa.selenium.WebDriver;
import test.sauceDemoSelenium.pages.SeleniumLoginPage;
import test.sauceDemoSelenium.utils.ConfigReader;

public class SauceDemoSteps {
    private final WebDriver driver;
    private final SeleniumLoginPage loginPage;

    public SauceDemoSteps(WebDriver driver) {
        this.driver = driver;
        this.loginPage = new SeleniumLoginPage(driver);
    }

    public void openLoginPage() {
        driver.get(ConfigReader.get("saucedemo.url"));
    }

    public void loginAsStandardUser() {
        loginPage.enterUsername(ConfigReader.get("saucedemo.username"));
        loginPage.enterPassword(ConfigReader.get("saucedemo.password"));
        loginPage.clickLoginButton();
    }

    public boolean isUserOnCatalogPage() {
        return loginPage.isProductsTitleDisplayed();
    }

    public String getCurrentPageUrl() {
        return driver.getCurrentUrl();
    }
}

