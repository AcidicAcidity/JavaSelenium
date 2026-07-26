package config;

import org.aeonbits.owner.Config;

/**
 * Интерфейс конфигурации на основе библиотеки Owner.
 *
 * ЧТО ТАКОЕ OWNER?
 * Owner позволяет превращать .properties файлы в типобезопасные Java-интерфейсы.
 * Вместо ручного чтения файла через FileInputStream и парсинга строк,
 * ты просто объявляешь методы в интерфейсе, а Owner сам находит значения
 * в config.properties и конвертирует их в нужный тип (String, int, boolean).
 *
 * ПРИМЕР:
 *   TestConfig config = ConfigManager.getConfig();
 *   String url = config.baseUrl();  // вернёт значение из config.properties
 *
 * @Config.Sources — указывает, откуда загружать файл конфигурации.
 * "file:..." — путь к файлу на диске.
 * "classpath:..." — файл внутри jar/проекта (src/test/resources).
 */
@Config.Sources({
    "file:./src/test/resources/config.properties",
    "classpath:config.properties"
})
public interface TestConfig extends Config {

    @Key("base.url")
    @DefaultValue("https://the-internet.herokuapp.com")
    String baseUrl();

    @Key("api.base.uri")
    @DefaultValue("https://reqres.in")
    String apiBaseUri();

    @Key("api.base.path")
    @DefaultValue("/api")
    String apiBasePath();

    @Key("implicit.wait")
    @DefaultValue("10")
    int implicitWait();

    @Key("explicit.wait")
    @DefaultValue("15")
    int explicitWait();

    @Key("browser")
    @DefaultValue("chrome")
    String browser();

    @Key("headless")
    @DefaultValue("false")
    boolean headless();

    @Key("window.maximize")
    @DefaultValue("true")
    boolean windowMaximize();

    @Key("test.username")
    String testUsername();

    @Key("test.password")
    String testPassword();
}
