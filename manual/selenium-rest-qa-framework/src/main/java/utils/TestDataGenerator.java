package utils;

import models.User;

import java.util.UUID;

/**
 * Генератор тестовых данных.
 *
 * ЗАЧЕМ НУЖЕН ЭТОТ КЛАСС?
 * В тестах часто нужны уникальные данные (email, имя).
 * Вместо хардкода "test123@test.com" используем UUID —
 * это гарантирует, что каждый запуск теста получит уникальные данные.
 *
 * ПОЧЕМУ ВАЖНА УНИКАЛЬНОСТЬ?
 * Если запускать тест 10 раз с одним email, API может вернуть
 * ошибку "пользователь уже существует". Уникальные данные
 * гарантируют независимость тестов друг от друга.
 *
 * В реальном проекте можно подключить библиотеку javafaker
 * для генерации реалистичных имен и адресов.
 */
public final class TestDataGenerator {

    private TestDataGenerator() {}

    public static User generateUser() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return User.builder()
            .name("Test User " + uniqueId)
            .job("QA Engineer " + uniqueId)
            .build();
    }

    public static String generateEmail() {
        return "test_" + UUID.randomUUID().toString().substring(0, 8) + "@qa.com";
    }

    public static String generateFirstName() {
        return "First_" + UUID.randomUUID().toString().substring(0, 5);
    }

    public static String generateLastName() {
        return "Last_" + UUID.randomUUID().toString().substring(0, 5);
    }
}
