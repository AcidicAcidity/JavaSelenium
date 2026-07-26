# 📖 Подробное описание кода проекта

> Этот файл — твой учебник. Здесь расписано ЗАЧЕМ нужен каждый файл, КАК он работает и КУДА смотреть, чтобы что-то изменить.

---

## 🏗 Общая архитектура

Проект построен на **Layered Architecture** (многоуровневая архитектура):

```
┌─────────────────────────────────────────┐
│  Тесты (tests.ui / tests.api / tests.e2e) │  ← ЧТО тестируем
├─────────────────────────────────────────┤
│  Page Objects (pages)                    │  ← КАК взаимодействуем с UI
│  API Clients (api)                       │  ← КАК взаимодействуем с API
├─────────────────────────────────────────┤
│  Базовые классы (base)                   │  ← Инфраструктура (WebDriver)
│  Утилиты (utils)                         │  ← Вспомогательные функции
│  Конфиги (config)                        │  ← Настройки окружения
│  Модели (models)                         │  ← Структуры данных
├─────────────────────────────────────────┤
│  Listeners (listeners)                   │  ← Обработка событий
└─────────────────────────────────────────┘
```

**Принцип:** Тесты НЕ должны знать, как работает Selenium или REST Assured. Они работают с абстракциями: `loginPage.loginAs(...)`, `apiClient.createUser(...)`.

---

## 📦 Пакет `config` — Конфигурации

### `TestConfig.java`

**Что это:** Интерфейс, который превращает `config.properties` в типобезопасный Java-объект.

**Зачем нужен:** Без Owner пришлось бы писать так:
```java
Properties props = new Properties();
props.load(new FileInputStream("config.properties"));
String url = props.getProperty("base.url");
int timeout = Integer.parseInt(props.getProperty("implicit.wait"));
```
С Owner пишем так:
```java
TestConfig config = ConfigManager.getConfig();
String url = config.baseUrl();  // уже String!
int timeout = config.implicitWait();  // уже int!
```

**Ключевые аннотации:**
- `@Config.Sources` — откуда читать файл (сначала файл на диске, потом в classpath)
- `@Key("base.url")` — имя свойства в .properties файле
- `@DefaultValue("...")` — значение по умолчанию, если свойство не найдено

**Куда смотреть, чтобы изменить:**
- Добавить новое свойство → добавь метод с `@Key` в `TestConfig.java`
- Изменить значение → правь `src/test/resources/config.properties`

---

### `ConfigManager.java`

**Что это:** Singleton-фабрика для создания `TestConfig`.

**Зачем нужен:** Чтобы не создавать конфиг в каждом тесте заново. Один раз создали — используем везде.

**Как работает Singleton:**
```
Поток 1 хочет getConfig() → config == null → создаём объект
Поток 2 хочет getConfig() → config != null → возвращаем готовый
```

`volatile` + `synchronized` — защита от "глюков" при параллельном запуске.

**Куда смотреть:** Не трогай, если не понимаешь. Это инфраструктура.

---

## 📦 Пакет `models` — Модели данных

### `User.java`

**Что это:** POJO (Plain Old Java Object) — простой класс для хранения данных пользователя.

**Зачем нужен:** Когда API возвращает JSON, Jackson автоматически превращает его в объект `User`:
```java
// JSON от сервера:
// {"id": 2, "email": "janet@reqres.in", "first_name": "Janet"}

User user = response.as(User.class);
System.out.println(user.getFirstName());  // "Janet"
```

**Аннотации Lombok:**
- `@Data` — создаёт геттеры, сеттеры, toString(), equals(), hashCode()
- `@Builder` — позволяет писать `User.builder().name("John").build()`
- `@NoArgsConstructor` — конструктор без параметров (нужен Jackson)
- `@AllArgsConstructor` — конструктор со всеми параметрами

**Аннотации Jackson:**
- `@JsonProperty("first_name")` — маппит JSON-поле `first_name` на Java-поле `firstName`
- `@JsonIgnoreProperties(ignoreUnknown = true)` — если в JSON есть поля, которых нет в классе — не падаем

**Куда смотреть, чтобы добавить модель:**
1. Создай новый файл в `src/main/java/models/`
2. Объяви поля, соответствующие JSON-ответу API
3. Добавь `@Data @Builder @NoArgsConstructor @AllArgsConstructor`
4. Используй `@JsonProperty` для snake_case полей

---

## 📦 Пакет `api` — HTTP-клиенты

### `BaseApiClient.java`

**Что это:** Базовый класс для ВСЕХ API-клиентов.

**Зачем нужен:** Общие настройки для всех HTTP-запросов:
- Базовый URI и path (из конфига)
- Content-Type: application/json
- Allure-фильтр (автоматически логирует запросы в отчёт)
- Логирование (видим, что отправляем и получаем)

**RequestSpecification** — это "шаблон" запроса. Настроил один раз — используешь везде:
```java
given()
    .spec(requestSpec)  // ← подставляем все общие настройки
    .body(user)
.when()
    .post("/users");
```

**Куда смотреть:**
- Добавить общий header (например, Authorization) → правь конструктор `BaseApiClient`
- Изменить base URI → правь `config.properties`

---

### `UserApiClient.java`

**Что это:** Клиент для работы с endpoint'ами пользователей.

**Зачем нужен:** Вместо 5-10 строк REST Assured кода в каждом тесте пишем одну строку:
```java
// Без клиента:
given().header(...).body(...).when().post("/users").then().statusCode(201);

// С клиентом:
apiClient.createUser(user);
```

**Паттерн:** API Client = обёртка над REST Assured. Каждый endpoint = один метод.

**Аннотация `@Step`:** Каждый вызов метода отображается в Allure-отчёте как шаг с параметрами.

**Куда смотреть, чтобы добавить endpoint:**
1. Создай метод в `UserApiClient` (или новый класс `OrderApiClient`)
2. Используй `given().spec(requestSpec)` для общих настроек
3. Возвращай `Response` — тест сам решит, что проверять

---

## 📦 Пакет `utils` — Утилиты

### `WaitUtils.java`

**Что это:** "Умные" ожидания для Selenium.

**Проблема:** Сайты загружаются асинхронно. Элемент может появиться через 100мс или 5 секунд.

**Решение:** Explicit Wait — ждём не время, а КОНКРЕТНОЕ УСЛОВИЕ:
- `visibilityOf(element)` — элемент виден (displayed = true)
- `elementToBeClickable(element)` — элемент виден И кликабелен
- `invisibilityOf(element)` — элемент исчез

**Почему НЕ Thread.sleep(5000):**
- `sleep(5000)` всегда ждёт 5 секунд, даже если элемент появился через 200мс
- Explicit Wait ждёт до 15 секунд, но продолжает сразу, как условие выполнено

**Куда смотреть:**
- Добавить новое ожидание → создай метод с `ExpectedConditions.XXX`
- Изменить таймаут → правь `explicit.wait` в `config.properties`

---

### `TestDataGenerator.java`

**Что это:** Генератор уникальных тестовых данных.

**Зачем нужен:**
- Если 10 тестов создают пользователя с email "test@test.com" — API вернёт ошибку "уже существует"
- UUID гарантирует уникальность: `test_a7f3b2c1@qa.com`

**Куда смотреть:** Добавляй новые методы для генерации данных под свои нужды.

---

## 📦 Пакет `base` — Базовые классы тестов

### `BaseTest.java`

**Что это:** Родительский класс для ВСЕХ UI-тестов.

**Что делает `@BeforeMethod`:**
1. Создаёт WebDriver (Chrome/Firefox)
2. Настраивает неявные ожидания
3. Разворачивает окно на весь экран

**Что делает `@AfterMethod`:**
1. Закрывает браузер (`driver.quit()`)
2. Очищает ThreadLocal

**ThreadLocal<WebDriver>:**
Когда тесты идут параллельно (4 потока), каждый поток получает СВОЙ браузер. Без ThreadLocal все потоки делили бы один драйвер — тесты падали бы.

```
Поток 1 → ThreadLocal[1] = ChromeDriver #1
Поток 2 → ThreadLocal[2] = ChromeDriver #2
Поток 3 → ThreadLocal[3] = ChromeDriver #3
```

**Фабрика драйверов:**
Метод `createDriver()` создаёт Chrome или Firefox в зависимости от `config.properties`:
- `browser=chrome` → ChromeDriver
- `browser=firefox` → FirefoxDriver
- `headless=true` → безголовый режим (для CI/CD)

**Куда смотреть:**
- Добавить новый браузер (Edge, Safari) → расширь `createDriver()`
- Добавить опции Chrome → правь `ChromeOptions`

---

## 📦 Пакет `pages` — Page Object Model

### `BasePage.java`

**Что это:** Родительский класс для всех Page Object'ов.

**PageFactory.initElements(driver, this):**
Это магия Selenium. Она сканирует поля класса, ищет `@FindBy` аннотации и "привязывает" элементы к полям. Элементы ищутся лениво — только при первом обращении.

**WebDriverWait:** Общий wait для всех страниц. Не создаём новый в каждом Page Object.

---

### `LoginPage.java`

**Что это:** Page Object для страницы авторизации.

**Паттерн Page Object Model:**
- Каждая страница сайта = отдельный Java-класс
- В классе: локаторы (`@FindBy`) + методы действий (`loginAs`, `open`)
- Тесты НЕ знают про CSS/XPath — они вызывают методы Page Object

**Fluent Interface:**
Методы возвращают `this` или новый Page Object, позволяя вызывать методы цепочкой:
```java
new LoginPage(driver)
    .open()                           // возвращает LoginPage
    .loginAs("user", "pass");         // возвращает DashboardPage
```

**Куда смотреть, чтобы добавить страницу:**
1. Создай класс в `pages/`, наследуй от `BasePage`
2. Объяви элементы через `@FindBy`
3. Напиши методы действий
4. Возвращай `this` для цепочек или новый Page Object при навигации

---

### `DashboardPage.java`

**Что это:** Page Object для страницы после логина.

**Важно:** При навигации на новую страницу возвращай новый Page Object:
```java
public LoginPage clickLogout() {
    logoutButton.click();
    return new LoginPage(driver);  // ← новая страница = новый объект
}
```

---

## 📦 Пакет `listeners` — Слушатели событий

### `AllureListener.java`

**Что это:** "Слушает" события TestNG и реагирует на падения тестов.

**Как работает:**
1. TestNG вызывает `onTestFailure()` когда тест падает
2. Listener берёт WebDriver из `BaseTest.getDriver()`
3. Делает скриншот: `((TakesScreenshot) driver).getScreenshotAs(...)`
4. Прикрепляет скриншот и стектрейс к Allure-отчёту через `@Attachment`

**Куда смотреть:**
- Добавить логирование в консоль → допиши в `onTestFailure()`
- Добавить запись видео → используй библиотеку вроде Monte Screen Recorder

---

## 📦 Пакет `tests` — Сами тесты

### `LoginTest.java`

**Что это:** UI-тесты авторизации.

**Структура теста (AAA):**
```java
// Arrange — подготовка
LoginPage loginPage = new LoginPage(getDriver());

// Act — действие
DashboardPage dashboard = loginPage.open().loginAs("user", "pass");

// Assert — проверка
Assert.assertTrue(dashboard.isLoggedIn());
```

**Аннотации Allure:**
- `@Epic`, `@Feature`, `@Story` — иерархия в отчёте
- `@Severity(BLOCKER)` — важность (влияет на цвет в отчёте)
- `@Issue("AUTH-123")` — ссылка на баг-трекер
- `@TmsLink("TC-001")` — ссылка на тест-кейс

**Куда смотреть:** Пиши новые тесты по этому шаблону. Наследуй от `BaseTest`.

---

### `UserApiTest.java`

**Что это:** API-тесты CRUD операций с пользователями.

**AssertJ vs TestNG Assert:**
```java
// TestNG Assert — функционально, но скучно:
Assert.assertEquals(response.getStatusCode(), 201);
Assert.assertNotNull(response.jsonPath().getString("id"));

// AssertJ — читается как английский:
assertThat(response.getStatusCode()).isEqualTo(201);
assertThat(response.jsonPath().getString("id")).isNotNull().hasSizeGreaterThan(0);
```

**Каждый тест независим:** Не полагается на результаты других тестов. Создаёт свои данные, проверяет, не оставляет мусора.

---

### `E2EUserFlowTest.java`

**Что это:** Сквозные тесты, комбинирующие API и UI.

**Сценарий:**
1. **API** создаёт данные (быстро, надёжно)
2. **UI** проверяет то, что видит пользователь (точно)
3. **API** очищает данные (быстро, независимо от UI)

**Почему так:**
- Создать 100 пользователей через UI = 30 минут
- Создать 100 пользователей через API = 5 секунд
- Но проверить, что кнопка "Создать" работает — только через UI

**Куда смотреть:** Это твой шаблон для бизнес-сценариев. Копируй и адаптируй.

---

## 📋 Как добавить новый тест

### UI-тест:
```java
package tests.ui;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.SomePage;

public class SomeFeatureTest extends BaseTest {

    @Test(description = "Проверка фичи X")
    public void testFeatureX() {
        SomePage page = new SomePage(getDriver());
        page.open();

        // Действия...

        Assert.assertTrue(page.isSomethingVisible());
    }
}
```

### API-тест:
```java
package tests.api;

import api.SomeApiClient;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SomeApiTest {

    private SomeApiClient apiClient;

    @BeforeClass
    public void setUp() {
        apiClient = new SomeApiClient();
    }

    @Test
    public void testEndpoint() {
        var response = apiClient.doSomething();
        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
```

### E2E-тест:
```java
package tests.e2e;

import base.BaseTest;
import org.testng.annotations.Test;

public class SomeE2ETest extends BaseTest {
    @Test
    public void fullFlow() {
        // 1. API: подготовка
        // 2. UI: проверка
        // 3. API: очистка
    }
}
```

---

## 🔧 Частые вопросы

**Q: Почему тесты падают с "NoSuchElementException"?**
A: Скорее всего, элемент ещё не загрузился. Проверь:
1. Используешь ли `WaitUtils.waitForVisibility()` перед взаимодействием?
2. Правильный ли локатор в `@FindBy`?
3. Не находится ли элемент внутри iframe?

**Q: Как запустить один тест, а не все?**
A: В IDE (IntelliJ IDEA) кликни правой кнопкой на метод → "Run".
Или через Maven: `mvn test -Dtest=LoginTest#successfulLogin`

**Q: Как запустить в headless-режиме?**
A: В `config.properties` установи `headless=true`.

**Q: Где смотреть отчёт Allure?**
A: После `mvn test` выполни `mvn allure:serve` — откроется в браузере.

**Q: Как добавить новый браузер?**
A: В `BaseTest.createDriver()` добавь case для нового браузера. Подключи зависимость в `pom.xml` если нужно.

**Q: Почему используется ThreadLocal?**
A: Для параллельного запуска. Без него 4 потока делили бы один браузер и мешали друг другу.

---

## 🎓 Что учить дальше

1. **Page Object → Page Factory → Fluent Page Object** — углубись в паттерны
2. **Cucumber / Gherkin** — пиши тесты на почти-английском языке
3. **Docker + Selenium Grid** — запускай тесты в контейнерах
4. **Jenkins / GitHub Actions** — настрой CI/CD pipeline
5. **Database testing** — добавь JDBC для проверки данных в БД
6. **Kafka testing** — тестируй event-driven системы
7. **Mobile (Appium)** — тот же подход для мобильных приложений

---

*Удачи в автоматизации! 🚀*
