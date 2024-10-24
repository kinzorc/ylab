package ru.kinzorc.habittracker.infrastructure.repository.jdbc;

import ru.kinzorc.habittracker.application.dto.HabitDTO;
import ru.kinzorc.habittracker.application.dto.UserDTO;
import ru.kinzorc.habittracker.core.exceptions.HabitAlreadyExistsException;
import ru.kinzorc.habittracker.core.exceptions.HabitNotFoundException;
import ru.kinzorc.habittracker.core.exceptions.UserNotFoundException;
import ru.kinzorc.habittracker.core.repository.HabitRepository;
import ru.kinzorc.habittracker.infrastructure.repository.utils.JdbcConnector;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Реализация интерфейса {@link HabitRepository} для работы с привычками с использованием JDBC.
 * <p>
 * Этот класс взаимодействует с базой данных PostgreSQL через JDBC и выполняет операции CRUD (создание, чтение, обновление, удаление)
 * для привычек и их выполнения.
 * </p>
 */
public class JdbcHabitRepository implements HabitRepository {

    private final JdbcConnector jdbcConnector;

    /**
     * Конструктор для создания экземпляра {@code JdbcHabitRepository} с заданным объектом {@link JdbcConnector}.
     *
     * @param jdbcConnector объект {@link JdbcConnector}, предоставляющий соединение с базой данных
     */
    public JdbcHabitRepository(JdbcConnector jdbcConnector) {
        this.jdbcConnector = jdbcConnector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addHabit(UserDTO user, HabitDTO habit) throws HabitAlreadyExistsException, SQLException {
        String query = "INSERT INTO app_schema.habits (user_id, habit_name, description, frequency, created_date, start_date, end_date, " +
                "execution_period, status, streak, execution_percentage) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Проверка на существование привычки у пользователя
        if (isHabitExistForUser(user.getId(), habit.getName())) {
            throw new HabitAlreadyExistsException("Привычка с таким именем уже существует.");
        }

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, user.getId());
            statement.setString(2, habit.getName());
            statement.setString(3, habit.getDescription());
            statement.setString(4, habit.getFrequency().toString().toLowerCase());
            statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.of(habit.getStartDate(), LocalTime.MIDNIGHT)));
            statement.setTimestamp(7, Timestamp.valueOf(LocalDateTime.of(habit.getEndDate(), LocalTime.MAX)));
            statement.setString(8, habit.getExecutionPeriod().toString().toLowerCase());
            statement.setString(9, habit.getStatus().toString().toLowerCase());
            statement.setInt(10, 0);
            statement.setInt(11, 0);

            statement.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteHabit(long habitId) throws HabitNotFoundException, SQLException {
        String query = "DELETE FROM app_schema.habits WHERE id = ?";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, habitId);

            resetExecutions(habitId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted == 0) {
                throw new HabitNotFoundException("Привычка с данным ID не найдена.");
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllHabitsForUser(UserDTO user) throws HabitNotFoundException, SQLException {
        String query = "DELETE FROM app_schema.habits WHERE user_id = ?";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, user.getId());

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted == 0) {
                throw new HabitNotFoundException("Привычка с данным ID не найдена.");
            }

            resetAllExecutionsForUser(user);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllHabit(UserDTO user) throws HabitNotFoundException, SQLException {
        String query = "DELETE FROM app_schema.habits WHERE user_id = ?";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, user.getId());

            int rowsAffected = statement.executeUpdate();
            resetExecutionsAllHabits();
            if (rowsAffected == 0) {
                throw new HabitNotFoundException("Привычки для данного пользователя не найдены.");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateHabit(HabitDTO habit) throws HabitNotFoundException, SQLException {
        String query = "UPDATE app_schema.habits SET habit_name = ?, description = ?, frequency = ?, start_date = ?, end_date = ?, execution_period = ?, status = ? WHERE id = ?";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, habit.getName());
            statement.setString(2, habit.getDescription());
            statement.setString(3, habit.getFrequency().toString().toLowerCase());
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.of(habit.getStartDate(), LocalTime.MIDNIGHT)));
            statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.of(habit.getEndDate(), LocalTime.MAX)));
            statement.setString(6, habit.getExecutionPeriod().toString().toLowerCase());
            statement.setString(7, habit.getStatus().toString().toLowerCase());
            statement.setLong(8, habit.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new HabitNotFoundException("Привычка с данным ID не найдена.");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void markExecution(HabitDTO habit, LocalDateTime executionDate) throws SQLException {
        String query = "INSERT INTO app_schema.habit_executions (habit_id, date) VALUES (?, ?)";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, habit.getId());
            statement.setTimestamp(2, Timestamp.valueOf(executionDate));

            statement.executeUpdate();

            calculateStreak(habit, executionDate);
            calculateExecutionPercentage(habit,
                    LocalDateTime.of(habit.getStartDate(), LocalTime.MIDNIGHT),
                    LocalDateTime.of(habit.getEndDate(), LocalTime.MAX));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LocalDate> getExecutions(long habitId) throws HabitNotFoundException, SQLException {
        String query = "SELECT date FROM app_schema.habit_executions WHERE habit_id = ?";
        List<LocalDate> executions = new ArrayList<>();

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, habitId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    executions.add(resultSet.getTimestamp("date").toLocalDateTime().toLocalDate());
                }
            }
        }

        if (executions.isEmpty()) {
            throw new HabitNotFoundException("Привычка с данным ID не имеет выполнений.");
        }

        return executions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetExecutions(long habitId) throws HabitNotFoundException, SQLException {
        String query = "DELETE FROM app_schema.habit_executions WHERE habit_id = ?";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, habitId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new HabitNotFoundException("Привычка с данным ID не найдена.");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetAllExecutionsForUser(UserDTO user) throws HabitNotFoundException, SQLException {
        String query = "DELETE FROM app_schema.habit_executions WHERE user_id = ?";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, user.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new HabitNotFoundException("Выполнения для привычки или привычек не найдены");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetExecutionsAllHabits() throws SQLException {
        String query = "DELETE FROM app_schema.habit_executions";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<LocalDate, Integer> getStatisticByPeriod(HabitDTO habit, LocalDateTime startPeriodDate, LocalDateTime endPeriodDate) throws HabitNotFoundException, SQLException {
        String query = "SELECT DATE(date) as execution_date FROM app_schema.habit_executions WHERE habit_id = ?";
        Map<LocalDate, Integer> statistics = new HashMap<>();

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, habit.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    LocalDate executionDate = resultSet.getDate("execution_date").toLocalDate();
                    statistics.put(executionDate, calculateExecutionPercentage(habit,
                            LocalDateTime.of(habit.getStartDate(), LocalTime.MIN), LocalDateTime.of(executionDate, LocalTime.MAX)));
                }
            }
        }

        if (statistics.isEmpty()) {
            throw new HabitNotFoundException("Привычка с данным ID не имеет статистики за указанный период.");
        }

        return statistics;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetStatistics(long habitId, boolean resetExecutions, boolean resetStreaks) throws HabitNotFoundException, SQLException {
        if (resetExecutions) {
            resetExecutions(habitId);
        }

        if (resetStreaks) {
            resetStreaks(habitId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int calculateExecutionPercentage(HabitDTO habit, LocalDateTime startPeriodDate, LocalDateTime endPeriodDate) throws SQLException {
        String query = "SELECT h.frequency, h.execution_period, COUNT(he.habit_id) AS execution_count "
                + "FROM app_schema.habits h LEFT JOIN app_schema.habit_executions he ON h.id = he.habit_id "
                + "WHERE h.id = ? "
                + "GROUP BY h.frequency, h.execution_period";

        if (startPeriodDate.toLocalDate().isBefore(habit.getStartDate()))
            startPeriodDate = habit.getStartDate().atStartOfDay();

        if (endPeriodDate.toLocalDate().isAfter(habit.getEndDate()))
            endPeriodDate = habit.getEndDate().atStartOfDay();

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, habit.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {

                    long period = 0L;

                    String executionPeriod = resultSet.getString("execution_period");
                    String frequency = resultSet.getString("frequency");

                    if (executionPeriod.equals("month") || executionPeriod.equals("year")) {
                        if (frequency.equals("daily")) {
                            period = ChronoUnit.DAYS.between(startPeriodDate, endPeriodDate);
                        } else if (frequency.equals("weekly")) {
                            period = ChronoUnit.WEEKS.between(startPeriodDate, endPeriodDate);
                        }
                    }

                    if (period > 0) {
                        int executions = resultSet.getInt("execution_count");

                        return (int) ((executions / (double) period) * 100);
                    }
                }
            }
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int calculateStreak(HabitDTO habit, LocalDateTime newExecutionDate) throws SQLException {

        int newStreak = 0;

        // Получаем последнюю дату выполнения привычки
        String queryLastExecution = "SELECT MAX(date) as last_execution_date FROM app_schema.habit_executions WHERE habit_id = ?";
        String updateStreakQuery = "UPDATE app_schema.habits SET streak = ? WHERE id = ?";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statementLastExecution = connection.prepareStatement(queryLastExecution);
             PreparedStatement statementCalculateStreak = connection.prepareStatement(updateStreakQuery)) {

            statementLastExecution.setLong(1, habit.getId());

            try (ResultSet resultSet = statementLastExecution.executeQuery()) {
                if (resultSet.next()) {
                    LocalDateTime lastExecutionDate = resultSet.getTimestamp("last_execution_date").toLocalDateTime();

                    // Рассчитываем разницу между последней датой выполнения и новой датой выполнения
                    long difference;
                    if (habit.getFrequency().toString().equalsIgnoreCase("daily")) {
                        difference = ChronoUnit.DAYS.between(lastExecutionDate, newExecutionDate);
                    } else if (habit.getFrequency().toString().equalsIgnoreCase("weekly")) {
                        difference = ChronoUnit.WEEKS.between(lastExecutionDate, newExecutionDate);
                    } else {
                        throw new IllegalArgumentException("Неверная частота выполнения: " + habit.getFrequency());
                    }

                    // Если разница в днях или неделях равна 1, увеличиваем стрик, иначе сбрасываем
                    if (difference == 1) {
                        // Увеличиваем текущий стрик
                        int currentStreak = getCurrentStreak(habit.getId());
                        newStreak = currentStreak + 1;

                        // Обновляем стрик в таблице habits
                        statementCalculateStreak.setInt(1, newStreak);
                    } else {
                        // Сбрасываем стрик до 1
                        statementCalculateStreak.setInt(1, 1);
                    }

                    statementCalculateStreak.setLong(2, habit.getId());
                    statementCalculateStreak.executeUpdate();
                }
            }
        }

        return newStreak;
    }

    // Метод для получения текущего стрика из таблицы habits
    private int getCurrentStreak(long habitId) throws SQLException {
        String query = "SELECT streak FROM app_schema.habits WHERE id = ?";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, habitId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("streak");
                }
            }
        }
        // Если стрик не найден, возвращаем 0 по умолчанию
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitDTO> findAllHabits() throws SQLException {
        String query = "SELECT * FROM app_schema.habits";
        List<HabitDTO> habits = new ArrayList<>();

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                habits.add(new HabitDTO(resultSet));
            }
        } catch (SQLException e) {
            throw new SQLException("Ошибка получения информации по привычкам: " + e.getMessage());
        }

        return habits;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<HabitDTO> findHabitByID(long habitId) throws HabitNotFoundException, SQLException {
        String query = "SELECT * FROM app_schema.habits WHERE id = ?";
        HabitDTO habit = null;

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, habitId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    habit = new HabitDTO(resultSet);
                    habit.setId(resultSet.getLong("id"));
                }
            } catch (SQLException e) {
                throw new HabitNotFoundException("Ошибка при поиске привычки" + e.getMessage());
            }
        } catch (HabitNotFoundException e) {
            throw new HabitNotFoundException("Привычка не найдена!" + e.getMessage());
        }

        return Optional.ofNullable(habit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<HabitDTO> findHabitByName(String habitName) throws SQLException {
        String query = "SELECT * FROM app_schema.habits WHERE habit_name = ?";
        HabitDTO habit = null;

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, habitName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    habit = new HabitDTO(resultSet);
                    habit.setId(resultSet.getLong("id"));
                }
            }
        }

        return Optional.ofNullable(habit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HabitDTO> findHabitByUser(UserDTO user) throws UserNotFoundException, SQLException {
        String query = "SELECT * FROM app_schema.habits WHERE user_id = ?";

        List<HabitDTO> habits = new ArrayList<>();

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Устанавливаем идентификатор пользователя
            statement.setLong(1, user.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    HabitDTO habitDTO = new HabitDTO(resultSet);
                    habits.add(habitDTO);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Ошибка при запросе привычек пользователя: " + e.getMessage());
        }

        if (habits.isEmpty()) {
            throw new UserNotFoundException("У пользователя нет добавленных привычек.");
        }

        return habits;
    }

    // Проверка существования привычки у пользователя
    private boolean isHabitExistForUser(long userId, String habitName) throws SQLException {
        String query = "SELECT COUNT(*) FROM app_schema.habits WHERE user_id = ? AND habit_name = ?";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userId);
            statement.setString(2, habitName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    // Сброс стриков для привычки
    private void resetStreaks(long id) throws SQLException {
        String query = "UPDATE app_schema.habits SET streak = 0 WHERE id = ?";

        try (Connection connection = jdbcConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }
}