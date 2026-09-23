package test.herokuTables;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebDriver;


import static org.junit.jupiter.api.Assertions.assertTrue;

public class HerokuTablesTest extends BaseTest {

    private WebDriver driver;

    @ParameterizedTest(name = "Проверка локатора: {0}")
    @ValueSource(strings = {
//            "//table[@id='table1']/tbody/tr[td[1][text()='Smith']]/td[contains(.,'jsmith@gmail.com')]",
            "//table[@id='table1']/tbody/tr[td[1][text()='Smith']]/td",
            "//table[@id='table1']//tr[td[normalize-space(text())='$100.00']]//a[normalize-space(text())='edit']",
            "//table[@id='table1']//td[starts-with(text(), 'http://')]"
    })

    public void testTableElementsAreClickable(String xpathLocator) {
        HerokuTablesSteps steps = new HerokuTablesSteps(driver);

        steps.openTablesPage();

        assertTrue(steps.checkTargetElementStatus(xpathLocator),
                "Элемент по локатору [" + xpathLocator + "] не найден или не кликабелен!");
    }
}

