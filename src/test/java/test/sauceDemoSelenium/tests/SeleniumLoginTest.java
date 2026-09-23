//package test.sauceDemoSelenium.tests;
//
//import org.junit.jupiter.api.Test;
//import test.sauceDemoSelenium.baseTest.SeleniumBaseTest;
//import test.sauceDemoSelenium.steps.SauceDemoSteps;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class SeleniumLoginTest extends SeleniumBaseTest {
//
//    @Test
//    public void testSuccessfulLoginRedirect() {
//        SauceDemoSteps steps = new SauceDemoSteps(driver);
//
//        steps.openLoginPage();
//        steps.loginAsStandardUser();
//
//        assertTrue(steps.isUserOnCatalogPage(), "Пользователь не перенаправлен на страницу каталога товаров!");
//        assertTrue(steps.getCurrentPageUrl().contains("/inventory.html"), "URL страницы не соответствует ожидаемому каталогу!");
//    }
//}
//
