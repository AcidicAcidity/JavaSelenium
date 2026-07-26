package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.WaitUtils;

/**
 * Page Object для страницы, которая открывается после успешного логина.
 *
 * Здесь хранятся элементы и методы для работы с "защищённой" зоной приложения.
 * Каждая новая страница сайта = новый класс в пакете pages.
 */
public class DashboardPage extends BasePage {

    @FindBy(css = "h2")
    private WebElement pageHeading;

    @FindBy(css = "a[href='/logout']")
    private WebElement logoutButton;

    @FindBy(id = "flash")
    private WebElement flashMessage;

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    @Step("Получение заголовка страницы")
    public String getHeadingText() {
        return WaitUtils.waitForVisibility(driver, pageHeading).getText();
    }

    @Step("Нажатие кнопки Logout")
    public LoginPage clickLogout() {
        WaitUtils.waitForClickable(driver, logoutButton).click();
        return new LoginPage(driver);
    }

    @Step("Проверка, что отображается сообщение об успешном входе")
    public boolean isLoggedIn() {
        String text = WaitUtils.waitForVisibility(driver, flashMessage).getText();
        return text.contains("You logged into a secure area!");
    }
}
