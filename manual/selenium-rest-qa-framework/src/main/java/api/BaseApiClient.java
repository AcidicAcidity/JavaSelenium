package api;

import config.ConfigManager;
import config.TestConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Базовый HTTP-клиент для всех API-запросов.
 *
 * ЗАЧЕМ НУЖЕН ЭТОТ КЛАСС?
 * Все API-клиенты (UserApiClient и др.) наследуются от BaseApiClient.
 * Здесь настраиваются общие параметры для ВСЕХ запросов:
 * - Базовый URI и path
 * - Content-Type: application/json
 * - Логирование запросов/ответов
 * - Allure-фильтр (автоматически добавляет запросы в отчёт)
 *
 * RequestSpecification — это "шаблон" запроса. Один раз настроил —
 * используешь во всех тестах через given().spec(requestSpec).
 */
public abstract class BaseApiClient {

    protected final TestConfig config = ConfigManager.getConfig();
    protected final RequestSpecification requestSpec;

    protected BaseApiClient() {
        // Настраиваем базовые параметры RestAssured
        RestAssured.baseURI = config.apiBaseUri();
        RestAssured.basePath = config.apiBasePath();

        // Создаём "шаблон" запроса
        this.requestSpec = new RequestSpecBuilder()
            // Автоматически прикреплять запросы к Allure-отчёту
            .addFilter(new AllureRestAssured())
            // Всегда отправлять Content-Type: application/json
            .setContentType(ContentType.JSON)
            // Всегда принимать JSON
            .setAccept(ContentType.JSON)
            // Логировать ВСЁ: URI, headers, body, status, response
            .log(LogDetail.ALL)
            .build();
    }
}
