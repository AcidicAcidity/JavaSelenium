package tests.api;

import api.UserApiClient;
import io.qameta.allure.*;
import io.restassured.response.Response;
import models.User;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.TestDataGenerator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты REST API для работы с пользователями.
 *
 * ИСПОЛЬЗУЕМ assertj ВМЕСТО TestNG Assert:
 * AssertJ даёт fluent API — цепочку методов для проверок:
 *   assertThat(response.getStatusCode()).isEqualTo(201);
 *   assertThat(user.getName()).isNotNull().startsWith("Test");
 *
 * Это читается почти как английский язык и даёт понятные сообщения об ошибках.
 *
 * reqres.in — это публичный тестовый API. Он не сохраняет данные
 * между запросами (createUser не создаёт реального пользователя),
 * поэтому тесты независимы и безопасны.
 */
@Epic("API")
@Feature("Управление пользователями")
public class UserApiTest {

    private UserApiClient apiClient;

    @BeforeClass
    public void setUp() {
        apiClient = new UserApiClient();
    }

    @Test(description = "Создание пользователя через POST /users")
    @Story("Создание")
    @Description("Отправляем POST с данными пользователя, проверяем 201 и наличие id/createdAt")
    @Severity(SeverityLevel.BLOCKER)
    public void createUser() {
        // Arrange
        User newUser = TestDataGenerator.generateUser();

        // Act
        Response response = apiClient.createUser(newUser);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getString("name")).isEqualTo(newUser.getName());
        assertThat(response.jsonPath().getString("job")).isEqualTo(newUser.getJob());
        assertThat(response.jsonPath().getString("id")).isNotNull();
        assertThat(response.jsonPath().getString("createdAt")).isNotNull();
    }

    @Test(description = "Получение пользователя по ID")
    @Story("Чтение")
    @Description("Запрашиваем пользователя ID=2, проверяем структуру ответа")
    @Severity(SeverityLevel.CRITICAL)
    public void getUserById() {
        Response response = apiClient.getUserById(2);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("data.id")).isEqualTo("2");
        assertThat(response.jsonPath().getString("data.email")).contains("@");
        assertThat(response.jsonPath().getString("data.first_name")).isNotEmpty();
        assertThat(response.jsonPath().getString("data.last_name")).isNotEmpty();
    }

    @Test(description = "Обновление пользователя через PUT")
    @Story("Обновление")
    @Description("Отправляем PUT, проверяем 200 и обновлённые данные")
    @Severity(SeverityLevel.CRITICAL)
    public void updateUser() {
        User updatedUser = User.builder()
            .name("Updated Name")
            .job("Senior QA")
            .build();

        Response response = apiClient.updateUser(2, updatedUser);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("name")).isEqualTo("Updated Name");
        assertThat(response.jsonPath().getString("job")).isEqualTo("Senior QA");
        assertThat(response.jsonPath().getString("updatedAt")).isNotNull();
    }

    @Test(description = "Удаление пользователя")
    @Story("Удаление")
    @Description("Отправляем DELETE, ожидаем 204 No Content")
    @Severity(SeverityLevel.NORMAL)
    public void deleteUser() {
        Response response = apiClient.deleteUser(2);
        assertThat(response.getStatusCode()).isEqualTo(204);
    }

    @Test(description = "Получение списка пользователей с пагинацией")
    @Story("Чтение")
    @Description("Запрашиваем страницу 2, проверяем наличие массива data")
    @Severity(SeverityLevel.NORMAL)
    public void getUsersList() {
        Response response = apiClient.getUsersList(2);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("data")).isNotEmpty();
        assertThat(response.jsonPath().getInt("page")).isEqualTo(2);
    }

    @Test(description = "Попытка получить несуществующего пользователя")
    @Story("Негативные сценарии")
    @Description("Запрашиваем ID=999, ожидаем 404")
    @Severity(SeverityLevel.MINOR)
    public void getNonExistentUser() {
        Response response = apiClient.getUserById(999);
        assertThat(response.getStatusCode()).isEqualTo(404);
    }
}
