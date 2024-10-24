package ru.kinzorc.habittracker.application.dto;

import ru.kinzorc.habittracker.core.entities.Habit;
import ru.kinzorc.habittracker.core.enums.Habit.HabitExecutionPeriod;
import ru.kinzorc.habittracker.core.enums.Habit.HabitFrequency;
import ru.kinzorc.habittracker.core.enums.Habit.HabitStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Класс Data Transfer Object (DTO) для передачи данных привычки между слоями приложения.
 * <p>
 * Этот класс используется для представления привычки в виде простого объекта данных,
 * который может быть легко передан между слоями приложения (например, между сервисом и контроллером)
 * или при работе с базой данных.
 * </p>
 */
public class HabitDTO {

    /**
     * Уникальный идентификатор привычки.
     */
    private long id;

    /**
     * Идентификатор пользователя, к которому принадлежит привычка.
     */
    private long userId;

    /**
     * Имя привычки.
     */
    private String name;

    /**
     * Описание привычки.
     */
    private String description;

    /**
     * Частота выполнения привычки.
     * <p>
     * Определяется значениями перечисления {@link HabitFrequency}.
     * </p>
     */
    private HabitFrequency frequency;

    /**
     * Дата создания привычки.
     */
    private LocalDate createdDate;

    /**
     * Дата начала выполнения привычки.
     */
    private LocalDate startDate;

    /**
     * Дата окончания выполнения привычки.
     */
    private LocalDate endDate;

    /**
     * Период выполнения привычки.
     * <p>
     * Определяется значениями перечисления {@link HabitExecutionPeriod}.
     * </p>
     */
    private HabitExecutionPeriod executionPeriod;

    /**
     * Статус привычки.
     * <p>
     * Определяется значениями перечисления {@link HabitStatus}.
     * </p>
     */
    private HabitStatus status;

    /**
     * Текущий стрик (серия выполнений) привычки.
     */
    private int streak;

    /**
     * Процент выполнения привычки.
     */
    private int executionPercentage;

    /**
     * Конструктор для создания объекта DTO на основе сущности {@link Habit}.
     *
     * @param habit объект {@link Habit}, из которого будут извлечены данные
     */
    public HabitDTO(Habit habit) {
        this.id = habit.getId();
        this.name = habit.getName();
        this.description = habit.getDescription();
        this.frequency = habit.getFrequency();
        this.createdDate = habit.getCreatedDate();
        this.startDate = habit.getStartDate();
        this.endDate = habit.getEndDate();
        this.executionPeriod = habit.getExecutionPeriod();
        this.status = habit.getStatus();
        this.streak = 0;
        this.executionPercentage = 0;
    }

    /**
     * Конструктор для создания объекта DTO на основе данных из {@link ResultSet}.
     *
     * @param resultSet объект {@link ResultSet}, содержащий данные из базы данных
     * @throws SQLException если возникает ошибка при извлечении данных из {@link ResultSet}
     */
    public HabitDTO(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getLong("id");
        this.userId = resultSet.getLong("user_id");
        this.name = resultSet.getString("habit_name");
        this.description = resultSet.getString("description");
        this.frequency = HabitFrequency.valueOf(resultSet.getString("frequency").toUpperCase());
        this.createdDate = resultSet.getTimestamp("created_date").toLocalDateTime().toLocalDate();
        this.startDate = resultSet.getTimestamp("start_date").toLocalDateTime().toLocalDate();
        this.endDate = resultSet.getTimestamp("end_date").toLocalDateTime().toLocalDate();
        this.executionPeriod = HabitExecutionPeriod.valueOf(resultSet.getString("execution_period").toUpperCase());
        this.status = HabitStatus.valueOf(resultSet.getString("status").toUpperCase());
        this.streak = resultSet.getInt("streak");
        this.executionPercentage = resultSet.getInt("execution_percentage");
    }

    // for tests
    public HabitDTO() {

    }

    // Геттеры и сеттеры

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HabitFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(HabitFrequency frequency) {
        this.frequency = frequency;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public HabitExecutionPeriod getExecutionPeriod() {
        return executionPeriod;
    }

    public void setExecutionPeriod(HabitExecutionPeriod executionPeriod) {
        this.executionPeriod = executionPeriod;
    }

    public HabitStatus getStatus() {
        return status;
    }

    public void setStatus(HabitStatus status) {
        this.status = status;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public int getExecutionPercentage() {
        return executionPercentage;
    }

    public void setExecutionPercentage(int executionPercentage) {
        this.executionPercentage = executionPercentage;
    }

    /**
     * Преобразование объекта DTO обратно в сущность {@link Habit}.
     *
     * @return объект {@link Habit}, созданный на основе данных DTO
     */
    public Habit toHabit() {
        Habit habit = new Habit(name, description, frequency, startDate, executionPeriod);
        habit.setId(id);
        habit.setStatus(status);
        return habit;
    }

    /**
     * Преобразует данные DTO в массив для передачи в SQL-запрос.
     *
     * @return массив объектов для использования в SQL-запросах
     */
    public Object[] toSqlParams() {
        return new Object[]{userId, name, description, frequency.toString().toLowerCase(), LocalDateTime.of(createdDate, LocalTime.MIDNIGHT),
                LocalDateTime.of(startDate, LocalTime.MIDNIGHT), LocalDateTime.of(endDate, LocalTime.MAX),
                executionPeriod.toString().toLowerCase(), status.toString().toLowerCase(), streak, executionPercentage};
    }
}
