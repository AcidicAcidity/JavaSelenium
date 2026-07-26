package tests.ui;

import base.BaseTest;
import config.ConfigManager;
import config.TestConfig;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DashboardPage;
import pages.LoginPage;

/**
 * UI-тесты страницы авторизации.
 *
 * АННОТАЦИИ ALLURE (для красивого отчёта):
 * @Epic        — высокоуровневая группировка (например, "Авторизация")
 * @Feature     — фича внутри эпика ("Форма логина")
 * @Story       — конкретная пользовательская история
 * @Description — подробное описание теста
 * @Severity    — важность теста (BLOCKER, CRITICAL, NORMAL, MINOR, TRIVIAL)
 * @Step        — шаг внутри теста (можно ставить на методы Page Object)
 *
 * НАСЛЕДОВАНИЕ ОТ BaseTest:
 * Каждый тест получает:
 * - Свежий WebDriver (браузер открывается перед тестом, закрывается после)
 * - Готовые конфиги через ConfigManager
 * - Скриншот при падении (через AllureListener)
 */
@Epic("Авторизация")
@Feature("Форма входа")
public class LoginTest extends BaseTest {

    private final TestConfig config = ConfigManager.getConfig();

    @Test(description = "Успешная авторизация с валидными кредами")
    @Story("Позитивные сценарии")
    @Description("Проверяем, что пользователь с валидными кредами попадает на Dashboard")
    @Severity(SeverityLevel.BLOCKER)
    @Issue("AUTH-123")      // Ссылка на задачу в Jira
    @TmsLink("TC-001")      // Ссылка на тест-кейс в TestRail
    public void successfulLogin() {
        // Arrange: создаём Page Object и открываем страницу
        LoginPage loginPage = new LoginPage(getDriver());

        // Act: выполняем действия
        DashboardPage dashboard = loginPage
            .open()
            .loginAs(config.testUsername(), config.testPassword());

        // Assert: проверяем результат
        Assert.assertTrue(dashboard.isLoggedIn(),
            "Пользователь должен быть авторизован и видеть сообщение об успехе");
    }

    @Test(description = "Ошибка при неверном пароле")
    @Story("Негативные сценарии")
    @Description("Проверяем сообщение об ошибке при вводе неверного пароля")
    @Severity(SeverityLevel.CRITICAL)
    public void invalidPassword() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.open().loginAs(config.testUsername(), "wrong_password");

        String message = loginPage.getFlashMessageText();
        Assert.assertTrue(message.contains("Your password is invalid!"),
            "Должно отображаться сообщение о неверном пароле");
    }

    @Test(description = "Ошибка при неверном логине")
    @Story("Негативные сценарии")
    @Description("Проверяем сообщение об ошибке при вводе несуществующего логина")
    @Severity(SeverityLevel.CRITICAL)
    public void invalidUsername() {
        LoginPage loginPage = new LoginPage(getDriver());

        loginPage.open().loginAs("nonexistent_user", config.testPassword());

        String message = loginPage.getFlashMessageText();
        Assert.assertTrue(message.contains("Your username is invalid!"),
            "Должно отображаться сообщение о неверном логине");
    }

    @Test(description = "Logout возвращает на страницу логина")
    @Story("Выход из системы")
    @Description("Проверяем, что после Logout пользователь попадает на страницу логина")
    @Severity(SeverityLevel.NORMAL)
    public void logoutReturnsToLoginPage() {
        LoginPage loginPage = new LoginPage(getDriver());

        LoginPage afterLogout = loginPage
            .open()
            .loginAs(config.testUsername(), config.testPassword())
            .clickLogout();

        Assert.assertTrue(afterLogout.getFlashMessageText().contains("You logged out"),
            "После логаута должно отображаться сообщение об успешном выходе");
    }
}
