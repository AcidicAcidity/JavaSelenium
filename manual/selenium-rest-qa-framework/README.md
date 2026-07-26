# 🚀 QA Automation Framework

Полноценный стартовый проект для автоматизации тестирования на Java.
Покрывает UI-тесты (Selenium), API-тесты (REST Assured) и End-to-End сценарии.

## 📁 Структура проекта

```
selenium-rest-qa-framework/
├── pom.xml                          # Maven конфигурация (зависимости, плагины)
├── testng.xml                       # Конфигурация запуска тестов
├── README.md                        # Этот файл
├── CODE_EXPLANATION.md              # 🔥 ПОДРОБНОЕ описание КАЖДОГО файла
├── src/main/java/
│   ├── config/
│   │   ├── ConfigManager.java       # Фабрика конфигураций
│   │   └── TestConfig.java          # Интерфейс конфигов (Owner)
│   ├── models/
│   │   └── User.java                # POJO-модель пользователя (для API)
│   ├── api/
│   │   ├── BaseApiClient.java       # Базовый HTTP-клиент (общие настройки)
│   │   └── UserApiClient.java       # Клиент для работы с API пользователей
│   └── utils/
│       ├── WaitUtils.java           # Умные ожидания для Selenium
│       └── TestDataGenerator.java   # Генератор тестовых данных
├── src/test/java/
│   ├── base/
│   │   └── BaseTest.java            # Базовый класс для ВСЕХ UI-тестов
│   ├── pages/
│   │   ├── BasePage.java            # Базовый класс для всех Page Object
│   │   ├── LoginPage.java           # Страница авторизации
│   │   └── DashboardPage.java       # Главная страница после логина
│   ├── listeners/
│   │   └── AllureListener.java      # Скриншоты и логи при падении
│   ├── api/
│   │   └── UserApiTest.java         # Тесты REST API
│   ├── ui/
│   │   └── LoginTest.java           # UI-тесты авторизации
│   └── e2e/
│       └── E2EUserFlowTest.java     # Сквозные тесты (API + UI)
└── src/test/resources/
    ├── allure.properties            # Настройки Allure
    └── config.properties            # Переменные окружения (URL, креды)
```

## 🛠 Требования

- Java 17+
- Maven 3.8+
- Chrome браузер (для UI-тестов)

## ▶️ Быстрый старт

```bash
# 1. Клонируй/распакуй проект
cd selenium-rest-qa-framework

# 2. Запусти все тесты
mvn clean test

# 3. Сгенерируй отчёт Allure
mvn allure:serve
```

## 📊 Allure Reports

После запуска тестов:
```bash
# Сгенерировать и открыть отчёт в браузере
mvn allure:serve

# Или только сгенерировать
mvn allure:report
```

## 🔧 Настройка под свой проект

1. Открой `src/test/resources/config.properties`
2. Измени `base.url` и `api.base.uri` на свои
3. В `pages/` создай Page Object'ы для своих страниц
4. В `api/` создай клиенты для своих endpoint'ов
5. В `tests/` пиши тесты, наследуясь от `BaseTest`

## 📚 Обучение

Открой файл **`CODE_EXPLANATION.md`** — там ПОДРОБНО расписано:
- Зачем нужен каждый файл
- Как работает каждый класс
- Какие паттерны используются
- Куда смотреть, чтобы что-то изменить
