package test.sauceDemoSelenide.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("saucedemo.properties")) {
            if (input == null) {
                throw new RuntimeException("Не удалось найти файл saucedemo.properties в папке ресурсов!");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла saucedemo.properties", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}



