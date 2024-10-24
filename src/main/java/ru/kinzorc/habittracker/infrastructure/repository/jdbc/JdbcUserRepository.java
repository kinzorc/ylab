package ru.kinzorc.habittracker.infrastructure.repository.jdbc;

import ru.kinzorc.habittracker.application.dto.UserDTO;
import ru.kinzorc.habittracker.core.exceptions.UserAlreadyExistsException;
import ru.kinzorc.habittracker.core.exceptions.UserNotFoundException;
import ru.kinzorc.habittracker.core.repository.UserRepository;
import ru.kinzorc.habittracker.infrastructure.repository.utils.JdbcConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса {@link UserRepository} для работы с привычками с использованием JDBC.
 * <p>
 * Этот класс взаимодействует с базой данных PostgreSQL через JDBC и выполняет операции CRUD (создание, чтение, обновление, удаление)
 * для пользователей
 * </p>
 */
public class JdbcUserRepository implements UserRepository {

    private final JdbcConnector jdbcConnector;

    /**
     * Конструктор для создания экземпляра репозитория с JDBC.
     *
     * @param jdbcConnector экземпляр класса {@link JdbcConnector} для управления соединениями с базой данных
     */
    public JdbcUserRepository(JdbcConnector jdbcConnector) {
        this.jdbcConnector = jdbcConnector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createUser(UserDTO user) throws UserAlreadyExistsException, SQLException {

        String query = "INSERT INTO app_schema.users (username, password, email, role, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            try {
                Optional<UserDTO> userDTO = findUserByEmail(user.getEmail());
                if (userDTO.isPresent())
                    throw new UserAlreadyExistsException("Пользователь существует!");
            } catch (UserAlreadyExistsException e) {
                throw new UserAlreadyExistsException(e.getMessage());
            } catch (UserNotFoundException e) {
                String exception = e.getMessage();
            }

            statement.setString(1, user.getUserName());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getUserRole().toString().toLowerCase());
            statement.setString(5, user.getUserStatusAccount().toString().toLowerCase());

            statement.executeUpdate();
            System.out.println("Пользователь успешно создан.");
        } catch (SQLException e) {
            throw new SQLException("Ошибка при создании пользователя: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUser(UserDTO user) throws UserNotFoundException, SQLException {
        String query = "UPDATE app_schema.users SET username = ?, password = ?, email = ?, role = ?, status = ? WHERE id = ?";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            Optional<UserDTO> userDTO = findUserByEmail(user.getEmail());

            if (userDTO.isEmpty())
                throw new UserNotFoundException("Пользователь с ID \" + user.getId() + \" не найден.");

            statement.setString(1, user.getUserName());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getUserRole().toString().toLowerCase());
            statement.setString(5, user.getUserStatusAccount().toString().toLowerCase());

            statement.setLong(6, userDTO.get().getId());

            // Выполняем запрос
            int rowsUpdated = statement.executeUpdate();

            // Если строка не была обновлена, бросаем исключение
            if (rowsUpdated == 0) {
                throw new UserNotFoundException("Пользователь с ID " + user.getId() + " не найден.");
            }

            System.out.println("Пользователь с ID " + user.getId() + " успешно обновлен.");
        } catch (SQLException e) {
            throw new SQLException("Ошибка при обновлении пользователя с ID " + user.getId() + ": " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(long userId) throws UserNotFoundException, SQLException {
        String sql = "DELETE FROM app_schema.users WHERE id = ?";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted == 0) {
                throw new UserNotFoundException("Пользователь по такому ID не найден: " + userId);
            }

            System.out.println("Пользователь с ID " + userId + " успешно удален.");

        } catch (SQLException e) {
            throw new SQLException("Ошибка при удалении пользователя с ID " + userId + ": " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserDTO> findAllUsers() throws SQLException {
        String query = "SELECT * FROM app_schema.users";
        List<UserDTO> users = new ArrayList<>();

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                UserDTO userDTO = new UserDTO(resultSet);
                users.add(userDTO);
            }

        } catch (SQLException e) {
            throw new SQLException("Ошибка при получении списка пользователей: " + e.getMessage());
        }

        return users;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserDTO> findUserById(long userId) throws UserNotFoundException, SQLException {
        String query = "SELECT * FROM app_schema.users WHERE id = ?";
        UserDTO userDTO;

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next())
                    throw new UserNotFoundException("Пользователь по такому ID не найден: " + userId);

                userDTO = new UserDTO(resultSet);
            }
        } catch (SQLException e) {
            throw new SQLException("Ошибка при запросе к базе данных: " + e.getMessage());
        }

        return Optional.of(userDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserDTO> findUserByUserName(String userName) throws UserNotFoundException, SQLException {
        String query = "SELECT * FROM app_schema.users WHERE username = ?";
        UserDTO userDTO;

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, userName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next())
                    throw new UserNotFoundException("Пользователь с именем " + userName + " не найден");

                userDTO = new UserDTO(resultSet);
            }
        } catch (SQLException e) {
            throw new SQLException("Ошибка при запросе к базе данных: " + e.getMessage());
        }

        return Optional.of(userDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserDTO> findUserByEmail(String userEmail) throws UserNotFoundException, SQLException {
        String query = "SELECT * FROM app_schema.users WHERE email = ?";
        UserDTO userDTO;

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, userEmail);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next())
                    throw new UserNotFoundException("Пользователь с таким email не найден: " + userEmail);

                userDTO = new UserDTO(resultSet);
            }
        } catch (SQLException e) {
            throw new SQLException("Ошибка при запросе к базе данных: " + e.getMessage());
        }

        return Optional.of(userDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSession(long userId) throws SQLException {
        String query = "INSERT INTO service_schema.users_sessions (user_id, login_time) VALUES (?, ?)";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userId);
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Ошибка при добавлении сессии пользователя: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSession(long userId) throws SQLException {
        String query = "DELETE FROM service_schema.users_sessions WHERE user_id = ?";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Ошибка при удалении сессии пользователя: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllSessions() throws SQLException {
        String query = "DELETE FROM service_schema.users_sessions";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Ошибка при удалении всех сессий пользователей: " + e.getMessage());
        }
    }
}