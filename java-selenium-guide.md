# Java + Selenium: полный конспект для автоматизатора тестирования

> Конспект рассчитан на человека, который уже умеет программировать (PHP, C++, Python), поэтому базовый синтаксис Java даётся быстро, с акцентом на отличия и идиомы, а основное внимание уделено Selenium, REST API тестированию, интеграционным тестам и очередям.

---

## Содержание

1. [Java: быстрый старт для опытного разработчика](#1-java-быстрый-старт-для-опытного-разработчика)
2. [Инструменты сборки: Maven и Gradle](#2-инструменты-сборки-maven-и-gradle)
3. [Юнит-тестирование: JUnit 5 и TestNG](#3-юнит-тестирование-junit-5-и-testng)
4. [Selenium WebDriver: основы](#4-selenium-webdriver-основы)
5. [Локаторы и поиск элементов](#5-локаторы-и-поиск-элементов)
6. [Ожидания (Waits)](#6-ожидания-waits)
7. [Действия с элементами и Actions API](#7-действия-с-элементами-и-actions-api)
8. [Работа с Chrome/Chromium: опции, headless, профили](#8-работа-с-chromechromium-опции-headless-профили)
9. [Page Object Model и архитектура фреймворка](#9-page-object-model-и-архитектура-фреймворка)
10. [Тестирование REST API (REST Assured)](#10-тестирование-rest-api-rest-assured)
11. [Интеграционное тестирование (БД, Testcontainers)](#11-интеграционное-тестирование-бд-testcontainers)
12. [Тестирование очередей (Kafka, RabbitMQ)](#12-тестирование-очередей-kafka-rabbitmq)
13. [Параллельный запуск, отчёты, CI/CD](#13-параллельный-запуск-отчёты-cicd)
14. [Частые теоретические вопросы на собеседовании](#14-частые-теоретические-вопросы-на-собеседовании)
15. [Сводка ресурсов для углублённого изучения](#15-сводка-ресурсов-для-углублённого-изучения)

---

## 1. Java: быстрый старт для опытного разработчика

### 1.1 Ключевые отличия от Python/PHP/C++

| Особенность | Java |
|---|---|
| Компиляция | Компилируется в байткод (`.class`), исполняется JVM — "write once, run anywhere" |
| Типизация | Статическая, строгая (как C++, в отличие от Python/PHP) |
| Память | Автоматическая сборка мусора (Garbage Collector), как в Python, в отличие от C++ |
| Всё — объект | Кроме примитивов (`int`, `double`, `boolean`...), всё остальное — объект класса |
| Точка входа | Обязательный метод `public static void main(String[] args)` внутри класса |
| Один публичный класс на файл | Имя файла `.java` должно совпадать с именем `public class` |

### 1.2 Синтаксис в двух словах

```java
public class Main {
    public static void main(String[] args) {
        int age = 25;                 // примитив
        String name = "Ivan";         // объект (immutable, как str в Python)
        double price = 19.99;
        boolean isActive = true;

        System.out.println("Hello, " + name + "! Age: " + age);

        // Условия
        if (age >= 18) {
            System.out.println("Adult");
        } else {
            System.out.println("Minor");
        }

        // Циклы
        for (int i = 0; i < 5; i++) {
            System.out.println(i);
        }

        for (String item : List.of("a", "b", "c")) {  // foreach, как в PHP/Python
            System.out.println(item);
        }

        int i = 0;
        while (i < 3) {
            i++;
        }
    }
}
```

### 1.3 ООП: классы, интерфейсы, наследование

Java — строго объектно-ориентированный язык. Множественного наследования классов нет (в отличие от C++), но есть множественная реализация интерфейсов.

```java
// Интерфейс — контракт (аналог abstract class в Python с ABC, или interface в PHP)
public interface Animal {
    void makeSound();               // абстрактный метод
    default void sleep() {          // default-метод (появился в Java 8)
        System.out.println("Zzz");
    }
}

public class Dog implements Animal {
    private String name;            // инкапсуляция: приватное поле

    public Dog(String name) {       // конструктор
        this.name = name;
    }

    @Override
    public void makeSound() {
        System.out.println(name + " says Woof");
    }
}

// Наследование классов
public class Puppy extends Dog {
    public Puppy(String name) {
        super(name);                // вызов конструктора родителя
    }
}
```

Ключевые модификаторы доступа: `public`, `protected`, `private`, package-private (без модификатора).
`final` — аналог `const`/неизменяемости: финальный класс нельзя наследовать, финальный метод — переопределить, финальная переменная — переприсвоить.

### 1.4 Коллекции (`java.util`)

Аналог `list`/`dict`/`set` в Python.

```java
import java.util.*;

List<String> names = new ArrayList<>();   // как list в Python, динамический массив
names.add("Anna");
names.add("Ivan");

Map<String, Integer> ages = new HashMap<>(); // как dict в Python
ages.put("Anna", 30);
int annaAge = ages.get("Anna");

Set<String> uniqueNames = new HashSet<>();   // как set в Python
uniqueNames.add("Anna");

// Java — generic-типизированный язык: List<String> хранит ТОЛЬКО строки,
// это проверяется на этапе компиляции (в отличие от списков Python).
```

Часто используемые реализации:
- `ArrayList` — динамический массив, быстрый доступ по индексу.
- `LinkedList` — двусвязный список, быстрая вставка/удаление.
- `HashMap` — неупорядоченная хэш-таблица.
- `LinkedHashMap` — сохраняет порядок вставки.
- `TreeMap` — отсортированная по ключу.

### 1.5 Stream API (аналог list comprehensions / функционального стиля Python)

Это одна из самых важных тем — стримы активно используются в тестовом коде для обработки данных API-ответов, списков веб-элементов и т.д.

```java
import java.util.List;
import java.util.stream.Collectors;

List<String> names = List.of("Anna", "Ivan", "Petr", "Alex");

List<String> longNames = names.stream()
        .filter(n -> n.length() > 3)      // аналог filter() в Python
        .map(String::toUpperCase)          // аналог map()
        .sorted()
        .collect(Collectors.toList());

long count = names.stream().filter(n -> n.startsWith("A")).count();

boolean anyMatch = names.stream().anyMatch(n -> n.equals("Ivan"));
```

### 1.6 Лямбды и функциональные интерфейсы

```java
// Лямбда — аналог lambda в Python, но с явной типизацией через интерфейс
Runnable task = () -> System.out.println("Running");

Comparator<String> byLength = (a, b) -> a.length() - b.length();

// Часто используется в Selenium-ожиданиях:
wait.until(driver -> driver.findElement(By.id("submit")).isDisplayed());
```

### 1.7 Исключения

```java
try {
    int result = 10 / 0;
} catch (ArithmeticException e) {
    System.out.println("Error: " + e.getMessage());
} finally {
    System.out.println("Cleanup");
}

// Checked vs Unchecked exceptions — важное отличие от Python/PHP:
// Checked (например, IOException) ОБЯЗАТЕЛЬНО объявлять в сигнатуре метода (throws)
// или оборачивать в try/catch. Unchecked (RuntimeException и наследники) — не обязательно.

public void readFile() throws IOException {
    // ...
}
```

### 1.8 Опционалы (`Optional`) — защита от NullPointerException

```java
Optional<String> maybeName = Optional.ofNullable(getNameOrNull());
String result = maybeName.orElse("default");
maybeName.ifPresent(System.out::println);
```

### 1.9 Что не нужно учить "с нуля"

Так как вы уже знаете PHP/C++/Python, можно быстро пробежаться по:
- `switch` конструкции (похожа на C++);
- работе со строками (`String`, `StringBuilder` — аналог мутабельной строки, как `io.StringIO` в Python);
- многопоточности (`Thread`, `ExecutorService`) — пригодится для параллельного запуска тестов, но глубоко не обязательно;
- дженерикам (`<T>`) — аналог шаблонов C++ (template), но с ограничениями (type erasure).

---

## 2. Инструменты сборки: Maven и Gradle

В Java-мире нет `pip`/`composer` — вместо них используются системы сборки, которые одновременно управляют зависимостями и жизненным циклом сборки/тестов.

### 2.1 Maven

Конфигурация в `pom.xml`. Стандарт де-факто в enterprise QA-командах.

```xml
<project>
    <dependencies>
        <dependency>
            <groupId>org.seleniumhq.selenium</groupId>
            <artifactId>selenium-java</artifactId>
            <version>4.22.0</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.2</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>io.rest-assured</groupId>
            <artifactId>rest-assured</artifactId>
            <version>5.4.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

Основные команды: `mvn clean test`, `mvn clean install`, `mvn test -Dtest=LoginTest`.

### 2.2 Gradle

Альтернатива Maven, конфигурация в `build.gradle` (Groovy/Kotlin DSL), быстрее за счёт инкрементальной сборки и кэша.

```groovy
dependencies {
    testImplementation 'org.seleniumhq.selenium:selenium-java:4.22.0'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'
    testImplementation 'io.rest-assured:rest-assured:5.4.0'
}
```

**Что выбрать:** для старта Maven проще и понятнее (декларативный XML), Gradle — гибче, быстрее и чаще встречается в новых проектах. Знать стоит оба на уровне "прочитать чужой pom.xml/build.gradle".

---

## 3. Юнит-тестирование: JUnit 5 и TestNG

Это фундамент — на этих фреймворках строятся Selenium-тесты.

### 3.1 JUnit 5

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class LoginTest {

    @BeforeAll
    static void setUpAll() {
        // выполняется один раз перед всеми тестами класса
    }

    @BeforeEach
    void setUp() {
        // выполняется перед КАЖДЫМ тестом (аналог setUp() в unittest Python)
    }

    @Test
    @DisplayName("Успешный логин с валидными данными")
    void shouldLoginSuccessfully() {
        assertEquals(200, 200);
        assertTrue(true);
        assertNotNull("value");
    }

    @Test
    @Disabled("Флакающий тест, чинится в JIRA-123")
    void shouldFailOnInvalidPassword() { }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "user", "guest"})
    void testMultipleRoles(String role) {
        assertNotNull(role);
    }

    @AfterEach
    void tearDown() {
        // закрытие драйвера и т.п.
    }
}
```

### 3.2 TestNG

Более старый, но по-прежнему очень популярный в Selenium-проектах фреймворк (гибче в управлении зависимостями тестов и группами).

```java
import org.testng.annotations.*;
import static org.testng.Assert.*;

public class LoginTest {

    @BeforeMethod
    public void setUp() { }

    @Test(priority = 1, groups = "smoke")
    public void shouldLoginSuccessfully() {
        assertEquals(actual, expected);
    }

    @Test(dependsOnMethods = "shouldLoginSuccessfully")
    public void shouldSeeDashboard() { }

    @DataProvider(name = "credentials")
    public Object[][] credentials() {
        return new Object[][] {
            {"user1", "pass1"},
            {"user2", "pass2"}
        };
    }

    @Test(dataProvider = "credentials")
    public void testLogin(String user, String pass) { }

    @AfterMethod
    public void tearDown() { }
}
```

**JUnit 5 vs TestNG:** JUnit 5 — более современный, лучше интегрирован со Spring/Gradle "из коробки". TestNG — удобнее для сложных сьютов с зависимостями, группами и параллелизацией "из коробки" (через `testng.xml`). На собеседованиях часто спрашивают про оба.

---

## 4. Selenium WebDriver: основы

### 4.1 Что такое Selenium и как он работает

Selenium WebDriver — это протокол (стандартизован как **W3C WebDriver Protocol**), который позволяет программно управлять реальным браузером через HTTP-команды.

Архитектура:
```
Тестовый код (Java) → WebDriver (Java-биндинг) → HTTP-запросы (JSON Wire / W3C протокол)
      → Browser Driver (chromedriver.exe) → сам браузер (Chrome/Chromium)
```

- **WebDriver** — интерфейс в коде (`org.openqa.selenium.WebDriver`).
- **ChromeDriver** — отдельный исполняемый файл-посредник между кодом и браузером Chrome/Chromium.
- С Selenium 4.6+ используется **Selenium Manager** — он сам скачивает и подбирает нужную версию chromedriver, вручную скачивать бинарник обычно не нужно.

### 4.2 Первый тест

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class FirstTest {
    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        driver.get("https://example.com");
        System.out.println(driver.getTitle());
        driver.quit(); // ОБЯЗАТЕЛЬНО закрывать драйвер — иначе процессы chromedriver "утекают"
    }
}
```

`driver.close()` закрывает текущую вкладку, `driver.quit()` закрывает браузер и завершает сессию драйвера целиком — в тестах почти всегда нужен `quit()` в `@AfterEach`/`tearDown`.

### 4.3 Основные методы WebDriver

```java
driver.get("https://example.com");       // открыть URL (ждёт полной загрузки)
driver.navigate().to("https://...");     // то же самое, но с историей навигации
driver.navigate().back();
driver.navigate().forward();
driver.navigate().refresh();

String title = driver.getTitle();
String url = driver.getCurrentUrl();
String source = driver.getPageSource();

driver.manage().window().maximize();
driver.manage().window().fullscreen();
driver.manage().deleteAllCookies();
```

---

## 5. Локаторы и поиск элементов

### 5.1 Стратегии локаторов (`By`)

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

driver.findElement(By.id("username"));
driver.findElement(By.name("email"));
driver.findElement(By.className("btn-primary"));
driver.findElement(By.tagName("button"));
driver.findElement(By.linkText("Sign in"));
driver.findElement(By.partialLinkText("Sign"));
driver.findElement(By.cssSelector("div.card > button.submit"));
driver.findElement(By.xpath("//div[@class='card']//button[text()='Submit']"));

List<WebElement> allButtons = driver.findElements(By.tagName("button"));
```

**Приоритет выбора локаторов** (общепринятая практика, часто спрашивают на собесе):
1. `id` — самый быстрый и стабильный, если он статичный.
2. `name` / data-атрибуты (`data-testid`) — стабильны, не зависят от вёрстки.
3. `css selector` — быстрее xpath, читаемее.
4. `xpath` — самый гибкий (умеет искать по тексту, идти "вверх" по DOM `..`), но медленнее и более хрупкий.

### 5.2 CSS Selector — шпаргалка

```
#id                  → By id
.class               → By className
div.card             → tag + class
div > p              → прямой потомок
div p                → любой потомок
[data-testid='btn']  → по атрибуту
input[type='text']   → тег + атрибут
li:nth-child(2)      → второй элемент
a:contains('text')   → НЕ поддерживается стандартным CSS (это jQuery), в Selenium так нельзя
```

### 5.3 XPath — шпаргалка

```
//tag[@attr='value']            абсолютный путь по атрибуту
//div[contains(@class,'card')]  частичное совпадение класса
//button[text()='Submit']       точный текст
//button[contains(text(),'Sub')] частичный текст
//div[@id='parent']//button     любой потомок
//label[text()='Email']/following-sibling::input   поиск "соседа"
//button/parent::div            поиск родителя (уникальная фича XPath, в CSS невозможно)
(//button)[2]                   второй элемент из списка
```

### 5.4 Частая ошибка: `NoSuchElementException` vs `StaleElementReferenceException`

- `NoSuchElementException` — элемент не найден в DOM на момент поиска (неверный локатор либо элемент ещё не отрисовался — обычно решается ожиданиями).
- `StaleElementReferenceException` — элемент был найден, но DOM изменился (страница перерисовалась/элемент удалился), и старая ссылка на `WebElement` стала "протухшей". Решение: искать элемент заново перед взаимодействием.

---

## 6. Ожидания (Waits)

Это один из самых важных разделов теории Selenium — почти всегда спрашивают на собеседовании.

### 6.1 Implicit Wait (неявное ожидание)

Глобальная настройка: если элемент не найден сразу, драйвер будет "поллить" DOM в течение заданного времени перед тем, как выбросить исключение.

```java
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
```

⚠️ **Не рекомендуется смешивать** implicit и explicit wait в одном проекте — это приводит к непредсказуемым суммарным таймаутам и флакующим тестам.

### 6.2 Explicit Wait (явное ожидание) — рекомендуемый подход

Ждёт конкретное условие для конкретного элемента.

```java
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("submit")));
wait.until(ExpectedConditions.elementToBeClickable(By.id("submit"))).click();
wait.until(ExpectedConditions.textToBePresentInElement(el, "Success"));
wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("spinner")));
wait.until(ExpectedConditions.alertIsPresent());
wait.until(ExpectedConditions.urlContains("/dashboard"));

// Кастомное условие через лямбду (Function<WebDriver, Boolean>)
wait.until(driver1 -> driver1.findElements(By.className("item")).size() > 5);
```

### 6.3 Fluent Wait

Расширенная версия explicit wait: позволяет задать интервал опроса (`pollingEvery`) и игнорировать определённые исключения (например, `NoSuchElementException` во время ожидания).

```java
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.NoSuchElementException;
import java.util.function.Function;

Wait<WebDriver> fluentWait = new FluentWait<>(driver)
        .withTimeout(Duration.ofSeconds(30))
        .pollingEvery(Duration.ofMillis(500))
        .ignoring(NoSuchElementException.class);

WebElement el = fluentWait.until(d -> d.findElement(By.id("dynamic")));
```

### 6.4 `Thread.sleep()` — почему это плохо

`Thread.sleep(5000)` — жёсткая, "слепая" пауза. Она либо тратит время впустую (если элемент появился раньше), либо всё равно падает (если элемент появился позже). В продакшн-коде и на собеседованиях считается анти-паттерном; допустима только для точечной отладки.

---

## 7. Действия с элементами и Actions API

### 7.1 Базовые взаимодействия

```java
WebElement input = driver.findElement(By.id("search"));
input.sendKeys("Selenium Java");
input.clear();
input.click();
boolean displayed = input.isDisplayed();
boolean enabled = input.isEnabled();
boolean selected = input.isSelected();
String text = input.getText();
String attr = input.getAttribute("value");
String cssValue = input.getCssValue("color");
```

### 7.2 Работа с выпадающими списками (`Select`)

```java
import org.openqa.selenium.support.ui.Select;

Select dropdown = new Select(driver.findElement(By.id("country")));
dropdown.selectByVisibleText("Poland");
dropdown.selectByValue("PL");
dropdown.selectByIndex(2);
List<WebElement> selectedOptions = dropdown.getAllSelectedOptions();
```

### 7.3 Actions API — сложные жесты (drag&drop, hover, right click)

```java
import org.openqa.selenium.interactions.Actions;

Actions actions = new Actions(driver);

actions.moveToElement(menuElement).perform();          // наведение (hover)
actions.dragAndDrop(source, target).perform();          // drag & drop
actions.clickAndHold(el).moveByOffset(50, 0).release().perform();
actions.contextClick(el).perform();                     // клик правой кнопкой
actions.keyDown(Keys.CONTROL).click(el1).click(el2).keyUp(Keys.CONTROL).perform(); // multi-select
actions.sendKeys(Keys.ESCAPE).perform();
```

### 7.4 JavaScript Executor

Используется, когда стандартные методы Selenium не справляются (скролл, скрытые элементы, изменение значений напрямую).

```java
import org.openqa.selenium.JavascriptExecutor;

JavascriptExecutor js = (JavascriptExecutor) driver;
js.executeScript("arguments[0].scrollIntoView(true);", element);
js.executeScript("arguments[0].click();", element); // "силовой" клик в обход перехвата другим элементом
Object result = js.executeScript("return document.title;");
```

### 7.5 Фреймы, окна и алерты

```java
// Frames
driver.switchTo().frame("frameName");
driver.switchTo().frame(0);
driver.switchTo().defaultContent();

// Окна/вкладки
String mainWindow = driver.getWindowHandle();
for (String handle : driver.getWindowHandles()) {
    driver.switchTo().window(handle);
}
driver.switchTo().window(mainWindow);

// Alert
Alert alert = driver.switchTo().alert();
alert.accept();
alert.dismiss();
alert.sendKeys("text");
String alertText = alert.getText();
```

### 7.6 Скриншоты

```java
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;
import java.nio.file.Files;

File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
Files.copy(screenshot.toPath(), new File("screenshots/fail.png").toPath());
```

---

## 8. Работа с Chrome/Chromium: опции, headless, профили

### 8.1 ChromeOptions

```java
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;

ChromeOptions options = new ChromeOptions();
options.addArguments("--headless=new");     // современный headless-режим Chrome
options.addArguments("--disable-gpu");
options.addArguments("--window-size=1920,1080");
options.addArguments("--no-sandbox");        // часто нужно в Docker/CI
options.addArguments("--disable-dev-shm-usage"); // борьба с ограничением /dev/shm в контейнерах
options.addArguments("--incognito");
options.setBinary("/usr/bin/chromium-browser"); // если используется именно Chromium, а не Chrome

// Отключение уведомлений/попапов
Map<String, Object> prefs = new HashMap<>();
prefs.put("profile.default_content_setting_values.notifications", 2);
options.setExperimentalOption("prefs", prefs);

WebDriver driver = new ChromeDriver(options);
```

### 8.2 Selenium Manager и явное указание пути к драйверу

Начиная с Selenium 4.6, драйвер обычно не нужно скачивать вручную — Selenium Manager сделает это сам. Но если нужен фиксированный путь (например, в закрытом CI без интернета):

```java
System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
WebDriver driver = new ChromeDriver();
```

### 8.3 Запуск в Docker / CI

Типичная связка для CI: официальный образ `selenium/standalone-chromium` (Selenium Grid в контейнере) + подключение через `RemoteWebDriver`:

```java
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;

ChromeOptions options = new ChromeOptions();
options.addArguments("--headless=new");

WebDriver driver = new RemoteWebDriver(
        new URL("http://localhost:4444/wd/hub"), options);
```

### 8.4 Chrome vs Chromium — что важно знать

Chromium — открытый форк, на котором построен Chrome (в Chrome добавлены проприетарные вещи: кодеки, Google-сервисы, автообновление). Для автотестов разница почти не ощущается: и `ChromeDriver`, и `ChromeOptions` работают с обоими, достаточно указать `setBinary()` на нужный исполняемый файл, если он не в стандартном расположении.

---

## 9. Page Object Model и архитектура фреймворка

### 9.1 Page Object Model (POM)

Паттерн, при котором каждая страница/компонент UI описывается отдельным классом: локаторы и методы взаимодействия со страницей инкапсулированы, а сами тесты остаются "чистыми" и читаемыми.

```java
public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton = By.cssSelector("button[type='submit']");
    private final By errorMessage = By.className("error-message");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public LoginPage open() {
        driver.get("https://example.com/login");
        return this;
    }

    public DashboardPage loginAs(String username, String password) {
        driver.findElement(usernameField).sendKeys(username);
        driver.findElement(passwordField).sendKeys(password);
        driver.findElement(loginButton).click();
        return new DashboardPage(driver);   // Fluent-стиль: возврат следующей страницы
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage)).getText();
    }
}
```

Тест становится читаемым как сценарий:

```java
@Test
void shouldShowErrorOnInvalidLogin() {
    String error = new LoginPage(driver)
            .open()
            .loginAs("wrong@user.com", "wrongpass")
            .getErrorMessage();  // тут loginAs вернул бы LoginPage при ошибке

    assertEquals("Invalid credentials", error);
}
```

### 9.2 PageFactory и `@FindBy` (альтернативный стиль)

```java
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {
    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    public LoginPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }
}
```
Сейчас `@FindBy`/PageFactory используется реже, чем "чистый" POM с явным `driver.findElement()` внутри методов — из-за проблем со `StaleElementReferenceException` при ленивой инициализации элементов.

### 9.3 Типичная структура проекта

```
src
 ├── main/java
 │    ├── pages/            (Page Object классы)
 │    ├── api/               (клиенты REST API)
 │    └── utils/             (DriverFactory, ConfigReader, Waits)
 └── test/java
      ├── ui/                (Selenium-тесты)
      ├── api/                (REST Assured тесты)
      └── integration/        (тесты с БД/очередями)
resources/
      ├── config.properties
      └── testng.xml
```

### 9.4 DriverFactory (управление жизненным циклом драйвера)

```java
public class DriverFactory {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            driverThreadLocal.set(new ChromeDriver(options));
        }
        return driverThreadLocal.get();
    }

    public static void quitDriver() {
        if (driverThreadLocal.get() != null) {
            driverThreadLocal.get().quit();
            driverThreadLocal.remove();
        }
    }
}
```
`ThreadLocal` здесь важен для потокобезопасного параллельного запуска тестов — у каждого потока свой экземпляр драйвера.

---

## 10. Тестирование REST API (REST Assured)

REST Assured — стандартный де-факто инструмент для API-тестирования на Java, DSL в стиле **Given / When / Then** (аналог BDD).

### 10.1 Базовый GET-запрос

```java
import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Test
void shouldReturnUser() {
    given()
        .baseUri("https://api.example.com")
        .header("Authorization", "Bearer " + token)
    .when()
        .get("/users/1")
    .then()
        .statusCode(200)
        .body("name", equalTo("Ivan"))
        .body("email", containsString("@"))
        .contentType(ContentType.JSON)
        .time(lessThan(2000L));
}
```

### 10.2 POST/PUT/DELETE с телом запроса

```java
String requestBody = """
    {
      "name": "Ivan",
      "email": "ivan@example.com"
    }
    """;

Response response = given()
        .contentType(ContentType.JSON)
        .body(requestBody)
    .when()
        .post("/users")
    .then()
        .statusCode(201)
        .extract().response();

int newUserId = response.jsonPath().getInt("id");
```

Тело запроса удобнее собирать через POJO + сериализацию (Jackson/Gson), а не строкой:

```java
public class UserRequest {
    public String name;
    public String email;
}

UserRequest user = new UserRequest();
user.name = "Ivan";
user.email = "ivan@example.com";

given()
    .contentType(ContentType.JSON)
    .body(user)         // REST Assured сам сериализует объект в JSON
.when()
    .post("/users");
```

### 10.3 JSON Path и извлечение данных

```java
Response response = get("/users");
List<String> names = response.jsonPath().getList("name");
String firstEmail = response.jsonPath().getString("[0].email");
```

### 10.4 Схема ответа (Schema Validation)

```java
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

given().when().get("/users/1")
    .then().body(matchesJsonSchemaInClasspath("user-schema.json"));
```

### 10.5 Request/Response спецификации (переиспользуемая конфигурация)

```java
RequestSpecification spec = new RequestSpecBuilder()
        .setBaseUri("https://api.example.com")
        .addHeader("Authorization", "Bearer " + token)
        .setContentType(ContentType.JSON)
        .build();

given().spec(spec).when().get("/users").then().statusCode(200);
```

### 10.6 Комбинация Selenium + REST API в одном тесте

Частая практика: подготовка данных через API (быстрее UI), а проверка — через UI.

```java
@Test
void shouldDisplayCreatedUserInUI() {
    // Arrange: создаём пользователя напрямую через API — быстрее, чем через форму
    given().contentType(ContentType.JSON).body(newUser)
        .post("/api/users").then().statusCode(201);

    // Act: открываем UI и ищем этого пользователя
    driver.get("https://example.com/users");

    // Assert
    assertTrue(driver.getPageSource().contains(newUser.getEmail()));
}
```

---

## 11. Интеграционное тестирование (БД, Testcontainers)

### 11.1 Что такое интеграционные тесты в контексте Java QA

Проверка взаимодействия между несколькими компонентами системы: приложение + база данных, приложение + очередь сообщений, сервис A + сервис B. В отличие от юнит-тестов, здесь не мокают зависимости, а поднимают реальные (или максимально приближенные к реальным) окружения.

### 11.2 Прямые запросы к БД (JDBC)

```java
import java.sql.*;

try (Connection conn = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/testdb", "user", "pass")) {

    PreparedStatement stmt = conn.prepareStatement(
            "SELECT email FROM users WHERE id = ?");
    stmt.setInt(1, userId);

    ResultSet rs = stmt.executeQuery();
    if (rs.next()) {
        String email = rs.getString("email");
        assertEquals("ivan@example.com", email);
    }
}
```
Такая проверка часто используется, чтобы убедиться, что действие в UI/API реально записалось в БД, а не просто вернуло `200 OK`.

### 11.3 Testcontainers — поднятие реальных зависимостей в Docker для теста

Testcontainers — библиотека, которая поднимает Docker-контейнеры (БД, брокеры сообщений, сам браузер) прямо во время выполнения теста и гасит их после — тесты становятся воспроизводимыми и не зависят от внешнего окружения.

```java
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class UserRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @Test
    void shouldInsertUser() throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            // выполняем запросы к реальному Postgres в контейнере
        }
    }
}
```

Testcontainers также умеет поднимать **браузер** (`BrowserWebDriverContainer`) — полезно, когда UI-тесты должны быть полностью изолированы и воспроизводимы в CI без установленного локально Chrome.

### 11.4 WireMock — мокирование внешних HTTP-зависимостей

Когда сервис, с которым интегрируется тестируемое приложение, недоступен или нестабилен (сторонний API), его "подделывают" через WireMock.

```java
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@RegisterExtension
static WireMockExtension wireMock = WireMockExtension.newInstance()
        .options(wireMockConfig().port(8089))
        .build();

@Test
void shouldHandleExternalApiResponse() {
    wireMock.stubFor(get(urlEqualTo("/external/status"))
            .willReturn(aResponse().withStatus(200).withBody("{\"status\":\"ok\"}")));

    // тестируемый код обращается к http://localhost:8089/external/status
}
```

---

## 12. Тестирование очередей (Kafka, RabbitMQ)

### 12.1 Зачем тестировать очереди

В микросервисной архитектуре сервисы часто общаются асинхронно через брокеры сообщений (Kafka, RabbitMQ, ActiveMQ, SQS). Задача QA — убедиться, что сообщение действительно публикуется в нужный топик/очередь, имеет правильную структуру, и что consumer корректно его обрабатывает.

### 12.2 Тестирование Kafka (Java-клиент)

```java
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.clients.consumer.*;
import java.util.Properties;

// Producer — отправка сообщения в тестовых целях
Properties producerProps = new Properties();
producerProps.put("bootstrap.servers", "localhost:9092");
producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
    producer.send(new ProducerRecord<>("orders-topic", "orderId-1", "{\"status\":\"created\"}"));
}

// Consumer — проверка, что сообщение реально пришло
Properties consumerProps = new Properties();
consumerProps.put("bootstrap.servers", "localhost:9092");
consumerProps.put("group.id", "test-consumer-group");
consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
consumerProps.put("auto.offset.reset", "earliest");

try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
    consumer.subscribe(List.of("orders-topic"));
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));

    assertTrue(records.count() > 0);
    for (ConsumerRecord<String, String> record : records) {
        assertTrue(record.value().contains("created"));
    }
}
```

### 12.3 Kafka через Testcontainers (изолированный тестовый брокер)

```java
import org.testcontainers.kafka.KafkaContainer;

@Container
static KafkaContainer kafka = new KafkaContainer("apache/kafka:3.7.0");

// kafka.getBootstrapServers() — адрес для подключения producer/consumer в тесте
```

### 12.4 Тестирование RabbitMQ (Java-клиент)

```java
import com.rabbitmq.client.*;

ConnectionFactory factory = new ConnectionFactory();
factory.setHost("localhost");

try (Connection connection = factory.newConnection();
     Channel channel = connection.createChannel()) {

    channel.queueDeclare("test-queue", true, false, false, null);
    channel.basicPublish("", "test-queue", null, "hello".getBytes());

    GetResponse response = channel.basicGet("test-queue", true);
    assertNotNull(response);
    assertEquals("hello", new String(response.getBody()));
}
```

### 12.5 Типичный сценарий асинхронного end-to-end теста

1. Через REST API инициируем действие (например, "создать заказ").
2. Ожидаем (`Awaitility`, а не `Thread.sleep`!), что в Kafka-топике появится событие `order.created`.
3. Через API/БД проверяем, что consumer обработал событие и изменил статус заказа.

```java
import org.awaitility.Awaitility;
import static java.util.concurrent.TimeUnit.SECONDS;

Awaitility.await()
        .atMost(10, SECONDS)
        .pollInterval(500, java.util.concurrent.TimeUnit.MILLISECONDS)
        .untilAsserted(() -> {
            String status = getOrderStatusFromDb(orderId);
            assertEquals("PROCESSED", status);
        });
```
`Awaitility` — библиотека для явного ожидания асинхронных условий, идеологически то же самое, что `WebDriverWait` в Selenium, но для любых асинхронных процессов (очереди, БД, коллбэки).

---

## 13. Параллельный запуск, отчёты, CI/CD

### 13.1 Параллельный запуск (TestNG)

```xml
<!-- testng.xml -->
<suite name="Suite" parallel="methods" thread-count="4">
    <test name="UITests">
        <classes>
            <class name="tests.LoginTest"/>
        </classes>
    </test>
</suite>
```
Важно: при параллельном запуске UI-тестов каждый поток должен иметь **свой** экземпляр `WebDriver` (см. `ThreadLocal` в разделе 9.4) — иначе тесты будут "мешать" друг другу в одном браузере.

### 13.2 Отчёты

- **Allure Report** — самый популярный инструмент визуализации: шаги, скриншоты, вложения, история запусков. Интегрируется и с JUnit 5, и с TestNG.
- **ExtentReports** — альтернатива, тоже HTML-отчёты с графиками.

```java
@Test
@io.qameta.allure.Step("Логин пользователя {0}")
void login(String username) { ... }

Allure.addAttachment("Screenshot", new ByteArrayInputStream(screenshotBytes));
```

### 13.3 CI/CD (кратко)

Типичный pipeline (GitHub Actions / GitLab CI / Jenkins):
1. Checkout кода.
2. `mvn clean test` (или `gradle test`) в Docker-образе с предустановленным Chromium (например, `selenium/standalone-chromium`) или через Selenium Grid.
3. Публикация Allure/JUnit XML отчётов.
4. Уведомление в Slack/Teams при падении.

```yaml
# Пример шага GitHub Actions
- name: Run tests
  run: mvn clean test -Dheadless=true
```

---

## 14. Частые теоретические вопросы на собеседовании

Короткая шпаргалка-самопроверка — если уверенно отвечаете на всё, теорию можно считать закрытой.

1. Чем отличается `driver.close()` от `driver.quit()`?
2. Чем отличается implicit wait от explicit wait? Почему их не стоит смешивать?
3. Что такое `StaleElementReferenceException` и как его избежать?
4. В чём разница между `findElement` и `findElements` (что произойдёт, если элемент не найден)?
5. Как устроен протокол WebDriver (роль browser driver, JSON/W3C протокол)?
6. Что такое Page Object Model и зачем он нужен?
7. Чем XPath отличается от CSS Selector? Когда что применять?
8. Как обрабатывать всплывающие окна (alert), новые вкладки, iframe?
9. Что такое Selenium Grid и зачем нужен удалённый запуск (`RemoteWebDriver`)?
10. Как обеспечить потокобезопасность WebDriver при параллельном запуске?
11. Разница между `@BeforeEach`/`@BeforeAll` (JUnit) и `@BeforeMethod`/`@BeforeClass` (TestNG)?
12. Checked vs unchecked exceptions в Java — в чём разница и зачем это нужно?
13. Что проверяет REST Assured: код статуса, заголовки, тело, схему — какие способы валидации вы знаете?
14. Как тестировать асинхронные события (очереди) без `Thread.sleep`?
15. Зачем нужен Testcontainers и чем он лучше "локально установленной" тестовой БД?
16. Что такое flaky-тест и какие есть стратегии борьбы с нестабильностью (waits, изоляция данных, retry-логика)?
17. Чем `ArrayList` отличается от `LinkedList`, `HashMap` от `TreeMap`?
18. Что такое `Optional` и зачем он нужен?
19. Как работает Garbage Collector в общих чертах (когда объект становится "мусором")?
20. В чём разница между модульным, интеграционным и E2E тестом?

---

## 15. Сводка ресурсов для углублённого изучения

### Java — язык и основы

- **Официальная документация Oracle Java Tutorials** (англ.) — https://docs.oracle.com/javase/tutorial/
- **Baeldung** (англ.) — крупнейший блог с практическими статьями по Java, Stream API, Spring, тестированию: https://www.baeldung.com/
- **JavaRush** (рус.) — интерактивный курс по Java с большим количеством практики, популярен среди русскоязычных новичков в Java (даже с опытом в других языках): https://javarush.com/
- **"Java. Полное руководство" (Java: The Complete Reference), Herbert Schildt** — классический полный справочник, есть перевод на русский.
- **"Effective Java", Joshua Bloch** — must-read по идиоматичному Java-коду (英/рус издания есть).

### Selenium WebDriver

- **Официальная документация Selenium** (англ., самый актуальный источник) — https://www.selenium.dev/documentation/
- **Selenium Java API Docs (Javadoc)** — https://www.selenium.dev/selenium/docs/api/java/index.html
- **Курс "Selenium WebDriver с нуля" от Software-Testing.ru** (рус.) — статьи и разборы по автоматизации: https://software-testing.ru/
- **Boris Nadion / SDET-QA каналы на YouTube** (рус.) — множество практических разборов Selenium+Java на русском.
- **"Selenium WebDriver Practical Guide" (Udemy, англ.)** — практический курс с упором на Page Object и фреймворк с нуля.
- **habr.com** (рус.) — поиск по тегам "Selenium", "QA Automation", много разборов практических кейсов и архитектуры фреймворков.

### JUnit 5 / TestNG

- **JUnit 5 User Guide** (англ., официальная и очень подробная) — https://junit.org/junit5/docs/current/user-guide/
- **TestNG официальная документация** (англ.) — https://testng.org/doc/documentation-main.html
- **Baeldung: JUnit 5 / TestNG разделы** (англ.) — практические примеры по обоим фреймворкам.

### REST API тестирование (REST Assured)

- **Официальный сайт REST Assured** (англ.) — https://rest-assured.io/
- **REST Assured GitHub Wiki с примерами** (англ.) — https://github.com/rest-assured/rest-assured/wiki/Usage
- **Baeldung: Guide to REST-assured** (англ.) — https://www.baeldung.com/rest-assured-tutorial
- **Postman Learning Center** (англ., полезно для понимания концепций REST API до автоматизации) — https://learning.postman.com/

### Testcontainers / интеграционные тесты

- **Официальная документация Testcontainers** (англ.) — https://testcontainers.com/
- **Testcontainers for Java Quickstart** (англ.) — https://java.testcontainers.org/

### Kafka / RabbitMQ

- **Apache Kafka Documentation** (англ., официальная) — https://kafka.apache.org/documentation/
- **Confluent Developer** (англ., отличные практические туториалы по Kafka, есть примеры на Java) — https://developer.confluent.io/
- **RabbitMQ Java Client Tutorials** (англ., официальные, очень доходчивые пошаговые примеры) — https://www.rabbitmq.com/tutorials
- **habr.com: серия статей "Kafka для начинающих"** (рус.) — поиск по тегу Kafka на Хабре даёт актуальные разборы на русском.

### Общее / архитектура тестирования и практики QA

- **"Тестирование Дьявола" / Ministry of Testing** (англ., сообщество и материалы по QA-практикам) — https://www.ministryoftesting.com/
- **Habr, хаб "Тестирование IT-систем"** (рус.) — https://habr.com/ru/hubs/testing/
- **Awaitility GitHub Wiki** (англ., для асинхронных ожиданий вне Selenium) — https://github.com/awaitility/awaitility/wiki

---

### Как использовать этот конспект

Рекомендуемый порядок изучения:
1. Разделы 1–3 — Java-синтаксис и юнит-тестирование (можно пройтись быстро, у вас уже есть база).
2. Разделы 4–9 — ядро Selenium, самая объёмная и практическая часть, стоит параллельно писать код руками.
3. Раздел 10 — REST Assured, если есть опыт тестирования API на других языках/инструментах (Postman, pytest+requests), пойдёт быстро.
4. Разделы 11–12 — интеграционные тесты и очереди, более продвинутая тема, требует понимания Docker.
5. Раздел 13 — инфраструктурная часть, полезна для понимания "как это работает в реальной команде".
6. Раздел 14 — самопроверка теории перед собеседованием.
