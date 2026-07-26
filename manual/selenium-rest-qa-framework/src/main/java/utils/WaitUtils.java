package utils;

import config.ConfigManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Утилитарный класс для "умных" ожиданий в Selenium.
 *
 * ЗАЧЕМ НУЖЕН ЭТОТ КЛАСС?
 * В Selenium есть два типа ожиданий:
 * 1. Неявные (implicit) — ждут указанное время ПЕРЕД каждым поиском элемента.
 *    Настраиваются один раз: driver.manage().timeouts().implicitlyWait(...)
 * 2. Явные (explicit) — ждут конкретное УСЛОВИЕ (не просто время).
 *    Например: ждать, пока элемент станет кликабельным.
 *
 * Явные ожидания ПРЕДПОЧТИТЕЛЬНЕЕ, потому что:
 * - Если элемент появился через 200мс — тест идёт дальше
 * - Если не появился за 15 сек — падает с понятной ошибкой
 * - Можно ждать разные условия: видимость, кликабельность, исчезновение
 */
public final class WaitUtils {

    private WaitUtils() {
        // Утилитарный класс — не создаём экземпляры
    }

    /**
     * Ждёт, пока элемент станет видимым (displayed = true).
     * Используй для полей ввода, текста, кнопок.
     */
    public static WebElement waitForVisibility(WebDriver driver, WebElement element) {
        return new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getConfig().explicitWait()))
            .until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Ждёт, пока элемент станет кликабельным (visible + enabled).
     * Используй ПЕРЕД click() на кнопках и ссылках.
     */
    public static WebElement waitForClickable(WebDriver driver, WebElement element) {
        return new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getConfig().explicitWait()))
            .until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Ждёт, пока элемент исчезнет со страницы.
     * Используй для спиннеров загрузки, модальных окон.
     */
    public static boolean waitForInvisibility(WebDriver driver, WebElement element) {
        return new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getConfig().explicitWait()))
            .until(ExpectedConditions.invisibilityOf(element));
    }

    /**
     * Ждёт, пока URL содержит ожидаемый текст.
     * Используй после навигации/редиректа.
     */
    public static boolean waitForUrlContains(WebDriver driver, String expectedText) {
        return new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.getConfig().explicitWait()))
            .until(ExpectedConditions.urlContains(expectedText));
    }
}
