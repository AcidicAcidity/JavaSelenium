package base;

import config.ConfigManager;
import config.TestConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Базовый класс для ВСЕХ UI-тестов.
 *
 * ЗАЧЕМ НУЖЕН ЭТОТ КЛАСС?
 * Каждый UI-тест должен наследоваться от BaseTest.
 * Здесь происходит "подготовка сцены" перед тестом и "уборка" после:
 * 1. Создание WebDriver (Chrome/Firefox)
 * 2. Настройка таймаутов и размера окна
 * 3. Закрытие браузера после теста
 *
 * ThreadLocal<WebDriver>:
 * Когда тесты запускаются параллельно (parallel="methods"),
 * каждый поток должен иметь СВОЙ браузер. ThreadLocal создаёт
 * отдельную "коробку" WebDriver для каждого потока.
 * Без этого потоки будут делить один браузер и тесты упадут.
 */
public abstract class BaseTest {

    protected final TestConfig config = ConfigManager.getConfig();

    // ThreadLocal = отдельный WebDriver для каждого потока (для параллельного запуска)
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    @BeforeMethod
    public void setUp() {
        WebDriver webDriver = createDriver();
        driver.set(webDriver);

        // Неявное ожидание: перед каждым поиском элемента ждём до N секунд
        webDriver.manage().timeouts().implicitlyWait(
            java.time.Duration.ofSeconds(config.implicitWait())
        );

        // Установка таймаута загрузки страницы
        webDriver.manage().timeouts().pageLoadTimeout(
            java.time.Duration.ofSeconds(config.pageLoadTimeout())
        );

        // Разворачивание окна на весь экран
        if (config.windowMaximize()) {
            webDriver.manage().window().maximize();
        }
    }

    @AfterMethod
    public void tearDown() {
        WebDriver webDriver = driver.get();
        if (webDriver != null) {
            webDriver.quit();  // Закрывает ВСЕ вкладки и процесс браузера
            driver.remove();   // Очищаем ThreadLocal для GC
        }
    }

    /**
     * Возвращает WebDriver текущего потока.
     * Используй в тестах: getDriver().get(...)
     */
    public static WebDriver getDriver() {
        return driver.get();
    }

    /**
     * Фабрика драйверов. Создаёт Chrome или Firefox в зависимости от конфига.
     * WebDriverManager автоматически скачивает нужный chromedriver/geckodriver.
     */
    private WebDriver createDriver() {
        String browser = config.browser().toLowerCase();

        switch (browser) {
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (config.headless()) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
                return new ChromeDriver(options);
            }
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (config.headless()) {
                    options.addArguments("--headless");
                }
                return new FirefoxDriver(options);
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }
}
