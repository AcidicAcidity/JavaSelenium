package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.User;

import static io.restassured.RestAssured.given;

/**
 * Клиент для работы с API пользователей (reqres.in).
 *
 * ЗАЧЕМ НУЖЕН API-КЛИЕНТ?
 * Вместо того чтобы в каждом тесте писать длинные given-when-then,
 * мы создаём методы-обёртки: createUser(), getUser(), deleteUser().
 * Тесты становятся читаемыми: apiClient.createUser(user) вместо
 * 10 строк REST Assured кода.
 *
 * @Step — аннотация Allure. Каждый вызов метода будет отображаться
 *         в отчёте как отдельный шаг с параметрами.
 */
public class UserApiClient extends BaseApiClient {

    private static final String USERS_ENDPOINT = "/users";

    @Step("Создание пользователя: name={user.name}, job={user.job}")
    public Response createUser(User user) {
        return given()
            .spec(requestSpec)
            .body(user)
        .when()
            .post(USERS_ENDPOINT)
        .then()
            .extract().response();
    }

    @Step("Получение пользователя по ID: {userId}")
    public Response getUserById(int userId) {
        return given()
            .spec(requestSpec)
        .when()
            .get(USERS_ENDPOINT + "/{id}", userId)
        .then()
            .extract().response();
    }

    @Step("Обновление пользователя ID={userId}")
    public Response updateUser(int userId, User user) {
        return given()
            .spec(requestSpec)
            .body(user)
        .when()
            .put(USERS_ENDPOINT + "/{id}", userId)
        .then()
            .extract().response();
    }

    @Step("Удаление пользователя ID={userId}")
    public Response deleteUser(int userId) {
        return given()
            .spec(requestSpec)
        .when()
            .delete(USERS_ENDPOINT + "/{id}", userId)
        .then()
            .extract().response();
    }

    @Step("Получение списка пользователей, страница {page}")
    public Response getUsersList(int page) {
        return given()
            .spec(requestSpec)
            .queryParam("page", page)
        .when()
            .get(USERS_ENDPOINT)
        .then()
            .extract().response();
    }
}
