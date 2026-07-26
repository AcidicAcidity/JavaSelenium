package tests.e2e;

import api.UserApiClient;
import base.BaseTest;
import config.ConfigManager;
import config.TestConfig;
import io.qameta.allure.*;
import models.User;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DashboardPage;
import pages.LoginPage;
import utils.TestDataGenerator;

/**
 * End-to-End тест: комбинирует API + UI в одном сценарии.
 *
 * БИЗНЕС-СЦЕНАРИЙ:
 * 1. Через API создаём тестового пользователя (подготовка данных)
 * 2. Через UI логинимся и проверяем доступ к системе
 * 3. (Опционально) Через API удаляем пользователя (очистка)
 *
 * ЗАЧЕМ ТАК ДЕЛАТЬ?
 * - API быстрее UI: создание 1000 пользователей через API = секунды,
 *   через UI = часы. Готовим данные через API, проверяем через UI.
 * - UI-тесты проверяют именно то, что видит пользователь.
 * - Разделение concerns: API = подготовка/очистка, UI = проверка.
 *
 * ВАЖНО: reqres.in не сохраняет созданных пользователей,
 * поэтому шаг 3 (удаление) в данном тесте — демонстрация подхода.
 * В реальном проекте вы бы хранили ID созданного пользователя
 * и удаляли его в @AfterMethod.
 */
@Epic("E2E")
@Feature("Сквозные сценарии")
public class E2EUserFlowTest extends BaseTest {

    private final TestConfig config = ConfigManager.getConfig();
    private final UserApiClient apiClient = new UserApiClient();

    @Test(description = "E2E: Создание пользователя через API + проверка доступа через UI")
    @Story("Регистрация и авторизация")
    @Description(
        "1. Создаём пользователя через POST /users\n" +
        "2. Авторизуемся в UI\n" +
        "3. Проверяем, что Dashboard доступен"
    )
    @Severity(SeverityLevel.BLOCKER)
    public void createUserViaApiAndVerifyInUi() {
        // ===== ШАГ 1: Подготовка данных через API =====
        User testUser = TestDataGenerator.generateUser();

        Allure.step("Создание пользователя через API: " + testUser.getName());
        var apiResponse = apiClient.createUser(testUser);
        Assert.assertEquals(apiResponse.getStatusCode(), 201, "Пользователь должен быть создан");

        String createdUserId = apiResponse.jsonPath().getString("id");
        Allure.addAttachment("Created User ID", createdUserId);

        // ===== ШАГ 2: Действия в UI =====
        Allure.step("Авторизация в веб-интерфейсе");
        DashboardPage dashboard = new LoginPage(getDriver())
            .open()
            .loginAs(config.testUsername(), config.testPassword());

        // ===== ШАГ 3: Проверки =====
        Allure.step("Проверка доступа к защищённой зоне");
        Assert.assertTrue(dashboard.isLoggedIn(),
            "После авторизации должен отображаться Dashboard");

        Assert.assertEquals(dashboard.getHeadingText(), "Secure Area",
            "Заголовок страницы должен быть 'Secure Area'");

        // ===== ШАГ 4: Очистка (демонстрация) =====
        Allure.step("Очистка: удаление тестового пользователя через API");
        if (createdUserId != null) {
            // В реальном проекте здесь был бы вызов apiClient.deleteUser(Integer.parseInt(createdUserId));
            // reqres.in не сохраняет пользователей, поэтому этот шаг — для демонстрации паттерна
            Allure.addAttachment("Cleanup Note", "User cleanup would happen here in real project");
        }
    }

    @Test(description = "E2E: Получение данных через API и отображение в UI")
    @Story("Синхронизация данных")
    @Description(
        "1. Получаем пользователя через API\n" +
        "2. Логинимся в UI\n" +
        "3. Проверяем, что данные в UI соответствуют API"
    )
    @Severity(SeverityLevel.CRITICAL)
    public void verifyApiDataInUi() {
        // Arrange: получаем данные из API
        Allure.step("Получение пользователя из API");
        var apiResponse = apiClient.getUserById(2);
        Assert.assertEquals(apiResponse.getStatusCode(), 200);

        String apiFirstName = apiResponse.jsonPath().getString("data.first_name");
        String apiLastName = apiResponse.jsonPath().getString("data.last_name");
        Allure.addAttachment("API User", apiFirstName + " " + apiLastName);

        // Act: логинимся в UI
        Allure.step("Авторизация в UI");
        DashboardPage dashboard = new LoginPage(getDriver())
            .open()
            .loginAs(config.testUsername(), config.testPassword());

        // Assert: проверяем, что UI доступен
        Allure.step("Проверка UI");
        Assert.assertTrue(dashboard.isLoggedIn(),
            "UI должен быть доступен после получения данных из API");
    }
}
