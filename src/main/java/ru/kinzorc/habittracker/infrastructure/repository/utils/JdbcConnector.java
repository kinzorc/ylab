package ru.kinzorc.habittracker.infrastructure.repository.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Класс предоставляет функционал для установки соединения с базой данных PostgreSQL
 * с использованием JDBC. Он загружает конфигурацию базы данных из файла свойств и
 * предоставляет метод для получения соединения с базой данных.
 */
public class JdbcConnector {

    /**
     * Свойства базы данных, загруженные из конфигурационного файла.
     */
    private final Properties DB_PROPERTIES = new Properties();

    /**
     * Конструктор по умолчанию, который вызывает метод для загрузки свойств базы данных из файла.
     */
    public JdbcConnector() {
        loadProperties();
    }

    /**
     * Метод загружает свойства базы данных из конфигурационного файла.
     * <p>
     * Конфигурационный файл должен находиться по пути `/application.properties`.
     * Если файл не найден или произошла ошибка при его чтении, выводится сообщение об ошибке.
     * </p>
     */
    private void loadProperties() {
        try (InputStream inputStream = getClass().getResourceAsStream("/application.properties")) {
            if (inputStream != null) {
                DB_PROPERTIES.load(inputStream);
            } else {
                System.err.println("Конфигурационный файл не найден.");
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения конфигурационного файла: " + e.getMessage());
        }
    }

    /**
     * Метод для получения соединения с базой данных PostgreSQL.
     * <p>
     * Использует параметры подключения (URL, имя пользователя, пароль),
     * которые загружаются из конфигурационного файла. Если параметры подключения не найдены,
     * выбрасывается исключение {@link IllegalArgumentException}.
     * </p>
     *
     * @return объект {@link Connection} для соединения с базой данных
     * @throws SQLException             если возникает ошибка при установлении соединения с базой данных
     * @throws IllegalArgumentException если параметры подключения некорректны или отсутствуют в файле свойств
     */
    public Connection getConnection() throws SQLException {
        if (DB_PROPERTIES.getProperty("postgres_db.url") == null ||
                DB_PROPERTIES.getProperty("postgres_db.username") == null ||
                DB_PROPERTIES.getProperty("postgres_db.password") == null) {
            throw new IllegalArgumentException("В файле конфигурации указаны некорректные параметры подключения.");
        }

        String url = DB_PROPERTIES.getProperty("postgres_db.url");
        String username = DB_PROPERTIES.getProperty("postgres_db.username");
        String password = DB_PROPERTIES.getProperty("postgres_db.password");

        Connection connection;

        try {
            connection = DriverManager.getConnection(url, username, password);
            if (connection == null) {
                System.err.println("Не удалось подключиться к базе данных, проверьте параметры подключения в конфигурационном файле.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
            throw e;
        }

        return connection;
    }

}