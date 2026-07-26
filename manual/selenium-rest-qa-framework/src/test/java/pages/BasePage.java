package pages;

import config.ConfigManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Базовый класс для всех Page Object'ов.
 *
 * ЗАЧЕМ НУЖЕН ЭТОТ КЛАСС?
 * Все страницы (LoginPage, DashboardPage и т.д.) наследуются от BasePage.
 * Здесь хранится общая логика:
 * - Ссылка на WebDriver
 * - WebDriverWait для явных ожиданий
 * - Инициализация элементов через PageFactory
 *
 * PageFactory.initElements(driver, this):
 * Автоматически находит элементы на странице по аннотациям @FindBy
 * и "привязывает" их к полям класса. Элементы ищутся лениво
 * (только при первом обращении), а не при создании объекта.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getConfig().explicitWait()));
        PageFactory.initElements(driver, this);
    }

    /**
     * Возвращает текущий URL страницы.
     * Полезно для проверок после навигации.
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Возвращает заголовок страницы (тег <title>).
     */
    public String getPageTitle() {
        return driver.getTitle();
    }
}
