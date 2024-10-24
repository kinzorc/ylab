package ru.kinzorc.habittracker.infrastructure.repository.jdbc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.kinzorc.habittracker.application.dto.UserDTO;
import ru.kinzorc.habittracker.core.entities.User;
import ru.kinzorc.habittracker.core.enums.User.UserRole;
import ru.kinzorc.habittracker.core.exceptions.UserAlreadyExistsException;
import ru.kinzorc.habittracker.core.exceptions.UserNotFoundException;
import ru.kinzorc.habittracker.infrastructure.repository.utils.JdbcConnector;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class JdbcUserRepositoryTest {

    // Инициализация PostgreSQL Testcontainer
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");
    private JdbcUserRepository userRepository;
    private JdbcConnector jdbcConnector;

    @BeforeEach
    void setUp() {
        jdbcConnector = new JdbcConnector();
        userRepository = new JdbcUserRepository(jdbcConnector);

        String createUsersTable = "CREATE TABLE IF NOT EXISTS app_schema.users (" +
                "id SERIAL PRIMARY KEY, " +
                "username VARCHAR(50), " +
                "password VARCHAR(100), " +
                "email VARCHAR(100), " +
                "role VARCHAR(20), " +
                "status VARCHAR(20))";
        try (var connection = jdbcConnector.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(createUsersTable);
        } catch (SQLException e) {
            fail("Ошибка при создании таблицы: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Удаление всех данных из таблиц после каждого теста
        try (var connection = jdbcConnector.getConnection();
             var statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS app_schema.users");
        }
    }

    @Test
    @DisplayName("Создание нового пользователя")
    void createUser_success() throws SQLException, UserAlreadyExistsException, UserNotFoundException {
        UserDTO user = new UserDTO(new User("testUser", "password123", "test@test.com", UserRole.USER));

        userRepository.createUser(user);

        Optional<UserDTO> foundUser = userRepository.findUserByEmail("test@test.com");
        assertTrue(foundUser.isPresent(), "Пользователь должен быть создан");
        assertEquals("testUser", foundUser.get().getUserName(), "Имя пользователя должно совпадать");
    }

    @Test
    @DisplayName("Ошибка при создании существующего пользователя")
    void createUser_alreadyExists() throws SQLException, UserAlreadyExistsException {
        UserDTO user = new UserDTO(new User("testUser", "password123", "test@test.com", UserRole.USER));

        userRepository.createUser(user);
        assertThrows(UserAlreadyExistsException.class, () -> userRepository.createUser(user), "Ожидается ошибка UserAlreadyExistsException");
    }

    @Test
    @DisplayName("Обновление пользователя")
    void updateUser_success() throws SQLException, UserNotFoundException, UserAlreadyExistsException {
        UserDTO user = new UserDTO(new User("testUser", "password123", "test@test.com", UserRole.USER));
        userRepository.createUser(user);

        user.setUserName("updatedUser");
        userRepository.updateUser(user);

        Optional<UserDTO> updatedUser = userRepository.findUserByEmail("test@test.com");
        assertTrue(updatedUser.isPresent(), "Пользователь должен существовать после обновления");
        assertEquals("updatedUser", updatedUser.get().getUserName(), "Имя пользователя должно быть обновлено");
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUser_success() throws SQLException, UserNotFoundException, UserAlreadyExistsException {
        UserDTO user = new UserDTO(new User("testUser", "password123", "test@test.com", UserRole.USER));
        userRepository.createUser(user);

        Optional<UserDTO> foundUser = userRepository.findUserByEmail("test@test.com");
        assertTrue(foundUser.isPresent(), "Пользователь должен существовать перед удалением");

        userRepository.deleteUser(foundUser.get().getId());

        assertThrows(UserNotFoundException.class, () -> userRepository.findUserById(foundUser.get().getId()), "Ожидается ошибка UserNotFoundException");
    }

    @Test
    @DisplayName("Нахождение пользователя по Email")
    void findUserByEmail_success() throws SQLException, UserAlreadyExistsException, UserNotFoundException {
        UserDTO user = new UserDTO(new User("testUser", "password123", "test@test.com", UserRole.USER));
        userRepository.createUser(user);

        Optional<UserDTO> foundUser = userRepository.findUserByEmail("test@test.com");
        assertTrue(foundUser.isPresent(), "Пользователь должен быть найден по email");
    }

    @Test
    @DisplayName("Ошибка при нахождении несуществующего пользователя")
    void findUserByEmail_notFound() throws SQLException {
        assertThrows(UserNotFoundException.class, () -> userRepository.findUserByEmail("nonexistent@test.com"), "Ожидается ошибка UserNotFoundException");
    }
}
