package test.herokuTables;

import org.openqa.selenium.WebDriver;

public class HerokuTablesSteps {
    private final WebDriver driver;
    private final HerokuTablesPage tablesPage;

    public HerokuTablesSteps(WebDriver driver) {
        this.driver = driver;
        this.tablesPage = new HerokuTablesPage(driver);
    }

    public void openTablesPage() {
        driver.get("http://the-internet.herokuapp.com/tables");
    }

    public boolean checkTargetElementStatus(String xpath) {
        return tablesPage.isElementExistingAndClickable(xpath);
    }
}

