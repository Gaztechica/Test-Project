package test.herokuTables;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class HerokuTablesPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public HerokuTablesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public boolean isElementExistingAndClickable(String xpathExpression) {
        try {
            By locator = By.xpath(xpathExpression);
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            wait.until(ExpectedConditions.elementToBeClickable(element));
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
