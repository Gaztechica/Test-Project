package test.herokuTables;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static example.pages.LoginPage.textColorIs;

public class DynamicLoadingPage {

    private final SelenideElement startButton = $("#start button");
    private final SelenideElement loadingResult = $("#finish");

    @Step("Открыть страницу динамической загрузки элементов")
    public DynamicLoadingPage openPage() {
        open("http://the-internet.herokuapp.com/dynamic_loading/2");
        return this;
    }

    @Step("Нажать кнопку 'Start'")
    public DynamicLoadingPage clickStart() {
        startButton.click();
        return this;
    }

//    @Step("Проверить, что текст элемента стал черным (цвет {expectedColor})")
//    public DynamicLoadingPage verifyTextColor(String expectedColor) {
//        loadingResult.should(textColorIs(expectedColor), Duration.ofSeconds(10));
//        return this;
//    }

//    @Step("Проверить, что отображается текст '{expectedText}'")
//    public DynamicLoadingPage verifyResultText(String expectedText) {
//        loadingResult.shouldHave(Condition.text(expectedText));
//        return this;
//    }

    // 1. Проверка цвета текста (Заменили кастомный textColorIs на встроенный cssValue)
    @Step("Проверить, что текст элемента стал черным (цвет {expectedColor})")
    public DynamicLoadingPage verifyTextColor(String expectedColor) {
        loadingResult.shouldHave(Condition.cssValue("color", expectedColor), Duration.ofSeconds(10));
        return this;
    }

    // 2. Проверка самого текста (Исправили опечатку Condition.text вместо Condition.text(text))
    @Step("Проверить, что отображается текст '{expectedText}'")
    public DynamicLoadingPage verifyResultText(String expectedText) {
        loadingResult.shouldHave(Condition.text(expectedText), Duration.ofSeconds(10));
        return this;
    }

    /**
     * Кастомное условие Selenide (Condition) для проверки цвета текста.
     */
//    private static Condition textColorIs(String expectedRgbColor) {
//        return new Condition("textColorIs") {
//            @Override
//            public CheckResult check(Driver driver, WebElement element) {
//                if (!element.isDisplayed()) {
//                    return CheckResult.rejected("Элемент еще не отображается", element.getAttribute("outerHTML"));
//                }
//                String actualColor = element.getCssValue("color");
//                boolean met = actualColor.equalsIgnoreCase(expectedRgbColor);
//                return new CheckResult(met, String.format("Ожидался цвет: %s, но был: %s", expectedRgbColor, actualColor));
//            }
//        };
//    }
}
