package config;

import org.aeonbits.owner.ConfigFactory;

/**
 * Фабрика конфигураций — Singleton.
 *
 * ЗАЧЕМ НУЖЕН ЭТОТ КЛАСС?
 * Чтобы не создавать объект TestConfig в каждом тесте заново,
 * мы делаем один центральный метод getConfig(), который:
 * 1. Создаёт конфиг через ConfigFactory (из Owner)
 * 2. Кеширует его (создаётся только один раз)
 * 3. Возвращает готовый к использованию объект
 *
 * ПАТТЕРН: Singleton — гарантирует, что в программе есть только
 * один экземпляр конфигурации.
 */
public class ConfigManager {

    // volatile — гарантирует видимость изменений между потоками
    private static volatile TestConfig config;

    private ConfigManager() {
        // Приватный конструктор — нельзя создать извне
    }

    public static TestConfig getConfig() {
        if (config == null) {
            synchronized (ConfigManager.class) {
                if (config == null) {
                    config = ConfigFactory.create(TestConfig.class);
                }
            }
        }
        return config;
    }
}
