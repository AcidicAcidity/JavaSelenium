package listeners;

import base.BaseTest;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Allure Listener — "слушает" события TestNG и реагирует на них.
 *
 * ЗАЧЕМ НУЖЕН LISTENER?
 * TestNG во время выполнения тестов генерирует события:
 * - onTestStart — тест начался
 * - onTestSuccess — тест прошёл
 * - onTestFailure — тест упал ← нас интересует это!
 * - onTestSkipped — тест пропущен
 *
 * Мы переопределяем onTestFailure: когда тест падает,
 * автоматически делаем скриншот и прикрепляем к Allure-отчёту.
 *
 * @Attachment — аннотация Allure. Метод, помеченный ею, автоматически
 * прикрепляет возвращаемое значение к отчёту.
 * type = "image/png" — Allure покажет это как картинку.
 */
public class AllureListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        // Получаем экземпляр тестового класса (например, LoginTest)
        Object testClass = result.getInstance();

        // Берём WebDriver из BaseTest через getDriver()
        WebDriver driver = BaseTest.getDriver();

        if (driver != null) {
            // Делаем скриншот и прикрепляем к отчёту
            saveScreenshot(driver);
        }

        // Прикрепляем стектрейс ошибки
        saveLogs(result.getThrowable());
    }

    @Attachment(value = "📸 Screenshot on failure", type = "image/png")
    private byte[] saveScreenshot(WebDriver driver) {
        // TakesScreenshot — интерфейс Selenium для создания скриншотов
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    @Attachment(value = "📋 Stacktrace", type = "text/plain")
    private String saveLogs(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append("Exception: ").append(throwable.getClass().getName()).append("
");
        sb.append("Message: ").append(throwable.getMessage()).append("

");
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("    at ").append(element.toString()).append("
");
        }
        return sb.toString();
    }
}
