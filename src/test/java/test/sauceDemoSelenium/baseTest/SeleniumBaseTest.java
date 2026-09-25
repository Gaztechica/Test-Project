package test.sauceDemoSelenium.baseTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class SeleniumBaseTest {

    public static WebDriver driver;

    // 1. Общий метод инициализации (теперь вызывается и в Hooks, и в JUnit)
    public static void initDriver() {
        if (driver == null) {
            driver = new ChromeDriver();
            driver.manage().window().maximize();
        }
    }

    // 2. Общий метод закрытия (теперь вызывается и в Hooks, и в JUnit)
    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null; // Сбрасываем ссылку для изоляции тестов
        }
    }

    // 3. Сохраняем хук для обычных JUnit 5 тестов
    @BeforeEach
    public void setUp() {
        initDriver();
    }

    // 4. Сохраняем хук для обычных JUnit 5 тестов
    @AfterEach
    public void tearDown() {
        quitDriver();
    }
}
