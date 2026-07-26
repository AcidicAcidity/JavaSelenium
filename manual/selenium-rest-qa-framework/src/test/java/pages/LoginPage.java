package pages;

import config.ConfigManager;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.WaitUtils;

/**
 * Page Object для страницы авторизации.
 *
 * ЧТО ТАКОЕ PAGE OBJECT MODEL (POM)?
 * Это паттерн, при котором каждая страница сайта = отдельный Java-класс.
 * В классе хранятся:
 * - Локаторы элементов (@FindBy)
 * - Методы взаимодействия с элементами (ввод текста, клики)
 *
 * ПОЧЕМУ ЭТО ВАЖНО?
 * 1. Если изменился локатор — правим в ОДНОМ месте, а не в 50 тестах
 * 2. Тесты читаются как бизнес-логика: loginAs("user", "pass")
 * 3. Разделение ответственности: тесты — ЧТО тестируем, Page — КАК взаимодействуем
 *
 * @FindBy — аннотация Selenium. Указывает, как найти элемент на странице.
 * Поддерживает: id, name, css, xpath, className, linkText, partialLinkText, tagName.
 */
public class LoginPage extends BasePage {

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;

    @FindBy(id = "flash")
    private WebElement flashMessage;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Открывает страницу логина.
     * @return this — позволяет вызывать методы цепочкой (fluent interface):
     *         new LoginPage(driver).open().loginAs("user", "pass")
     */
    @Step("Открытие страницы логина")
    public LoginPage open() {
        driver.get(ConfigManager.getConfig().baseUrl() + "/login");
        return this;
    }

    /**
     * Вводит логин и пароль, нажимает кнопку входа.
     * @return DashboardPage — после успешного логина открывается другая страница
     */
    @Step("Авторизация как {username}")
    public DashboardPage loginAs(String username, String password) {
        WaitUtils.waitForVisibility(driver, usernameInput).sendKeys(username);
        WaitUtils.waitForVisibility(driver, passwordInput).sendKeys(password);
        WaitUtils.waitForClickable(driver, loginButton).click();
        return new DashboardPage(driver);
    }

    @Step("Получение текста сообщения об ошибке/успехе")
    public String getFlashMessageText() {
        return WaitUtils.waitForVisibility(driver, flashMessage).getText();
    }

    @Step("Проверка, что сообщение об успехе отображается")
    public boolean isSuccessMessageDisplayed() {
        String text = getFlashMessageText();
        return text.contains("You logged into a secure area!");
    }
}
