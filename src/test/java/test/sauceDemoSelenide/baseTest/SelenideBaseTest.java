package test.sauceDemoSelenide.baseTest;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import test.sauceDemoSelenide.steps.SauceDemoSteps;
import test.sauceDemoSelenide.utils.ConfigReader;
import org.openqa.selenium.chrome.ChromeOptions;

import static com.codeborne.selenide.Selenide.open;

public class SelenideBaseTest {
    @BeforeAll
    public static void globalSetup() {
        Configuration.browser = "chrome";
        Configuration.timeout = 5000;
        Configuration.holdBrowserOpen = true; // для отладки
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-features=PasswordLeakDetection");
        Configuration.browserCapabilities = options;
        Configuration.headless = true;

        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
    }

    @BeforeEach
    public void setUpLogin() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-features=PasswordLeakDetection");
        Configuration.browserCapabilities = options;
        open(ConfigReader.get("saucedemo.url"));
        Configuration.headless = true;

        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        SauceDemoSteps steps = new SauceDemoSteps();
        steps.login(
                ConfigReader.get("saucedemo.username"),
                ConfigReader.get("saucedemo.password")
        );
    }
}
