package ru.kinzorc.habittracker.infrastructure.repository.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DisplayName("Контейнерные тесты для JdbcConnector")
public class JdbcConnectorTest {

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    private JdbcConnector jdbcConnector;

    @BeforeEach
    @DisplayName("Инициализация JdbcConnector перед каждым тестом")
    void setUp() {
        System.setProperty("postgres_db.url", postgresContainer.getJdbcUrl());
        System.setProperty("postgres_db.username", postgresContainer.getUsername());
        System.setProperty("postgres_db.password", postgresContainer.getPassword());

        jdbcConnector = new JdbcConnector();
    }

    @Test
    @DisplayName("Тест: Успешное подключение к PostgreSQL через контейнер")
    void testSuccessfulConnection() throws SQLException {
        try (Connection connection = jdbcConnector.getConnection()) {
            assertNotNull(connection, "Подключение не должно быть null");
            assertTrue(connection.isValid(2), "Соединение должно быть валидным");
        }
    }

    @Test
    @DisplayName("Тест: Проверка выполнения SQL-запроса")
    void testExecuteSQL() throws SQLException {
        try (Connection connection = jdbcConnector.getConnection()) {
            Statement statement = connection.createStatement();
            boolean result = statement.execute("CREATE TABLE test_table (id SERIAL PRIMARY KEY, name VARCHAR(50))");
            assertFalse(result, "Создание таблицы должно завершиться без ошибок");
        }
    }
}
