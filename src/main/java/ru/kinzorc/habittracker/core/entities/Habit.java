package ru.kinzorc.habittracker.core.entities;

import ru.kinzorc.habittracker.core.enums.Habit.HabitExecutionPeriod;
import ru.kinzorc.habittracker.core.enums.Habit.HabitFrequency;
import ru.kinzorc.habittracker.core.enums.Habit.HabitStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Класс представляет привычку пользователя в системе.
 * <p>
 * Привычка включает уникальный идентификатор, имя, описание, частоту выполнения и статус активности.
 * Привычка может иметь различные периоды выполнения (например, месяц или год) и поддерживает
 * отслеживание даты начала и окончания выполнения.
 * </p>
 */
public class Habit {

    /**
     * Дата создания привычки.
     * <p>
     * Определяется автоматически при создании привычки и не может быть изменена.
     * </p>
     */
    private final LocalDate createdDate;

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
     * Определяется значениями перечисления {@link HabitFrequency}, которое указывает, как часто должна выполняться привычка.
     * </p>
     */
    private HabitFrequency frequency;
    /**
     * Уникальный идентификатор привычки (ID).
     */
    private long id;
    /**
     * Дата начала выполнения привычки.
     */
    private LocalDate startDate;

    /**
     * Дата окончания выполнения привычки.
     */
    private LocalDate endDate;

    /**
     * Период выполнения привычки (например, месяц или год).
     */
    private HabitExecutionPeriod executionPeriod;

    /**
     * Статус привычки.
     * <p>
     * Указывает, активна привычка или нет. Определяется значениями перечисления {@link HabitStatus}.
     * </p>
     */
    private HabitStatus status;

    /**
     * Список дат, когда привычка была выполнена.
     * <p>
     * Хранит все даты выполнения привычки, включая как успешные стрики, так и пропуски.
     * </p>
     */
    private List<LocalDate> executions;

    /**
     * Конструктор для создания новой привычки.
     * <p>
     * При создании новой привычки автоматически задаются идентификатор, статус, дата создания и вычисляется дата окончания выполнения.
     * </p>
     *
     * @param name            имя привычки
     * @param description     описание привычки
     * @param frequency       частота выполнения, определяется перечислением {@link HabitFrequency}
     * @param startDate       дата начала выполнения привычки
     * @param executionPeriod период выполнения привычки, определяется перечислением {@link HabitExecutionPeriod}
     */
    public Habit(String name, String description, HabitFrequency frequency, LocalDate startDate, HabitExecutionPeriod executionPeriod) {
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.createdDate = LocalDate.now();
        this.startDate = startDate;
        this.executionPeriod = executionPeriod;
        this.status = HabitStatus.ACTIVE;
        this.executions = new ArrayList<>();

        calculateEndDate(startDate, executionPeriod);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        calculateEndDate(this.startDate, this.executionPeriod);
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void calculateEndDate(LocalDate date, HabitExecutionPeriod habitExecutionPeriod) {
        switch (habitExecutionPeriod) {
            case MONTH -> this.endDate = date.plusMonths(1);
            case YEAR -> this.endDate = date.plusYears(1);
        }
    }

    public HabitExecutionPeriod getExecutionPeriod() {
        return executionPeriod;
    }

    public void setExecutionPeriod(HabitExecutionPeriod executionPeriod) {
        this.executionPeriod = executionPeriod;
        calculateEndDate(this.startDate, this.executionPeriod);
    }

    public HabitStatus getStatus() {
        return status;
    }

    public void setStatus(HabitStatus status) {
        this.status = status;
    }

    public List<LocalDate> getExecutions() {
        return this.executions;
    }

    public void setExecutions(List<LocalDate> executions) {
        this.executions = executions;
    }

    /**
     * Переопределяет метод {@code hashCode()} для генерации хеш-кода на основе идентификатора привычки.
     *
     * @return хеш-код привычки
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Переопределяет метод {@code equals()} для сравнения объектов по идентификатору.
     * <p>
     * Два объекта {@code Habit} считаются равными, если у них одинаковые идентификаторы.
     * </p>
     *
     * @param obj объект для сравнения
     * @return {@code true}, если объекты равны по идентификатору, иначе {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Habit habit = (Habit) obj;
        return id == habit.id;
    }

    /**
     * Возвращает строковое представление объекта привычки.
     *
     * @return строковое представление привычки
     */
    @Override
    public String toString() {
        return String.format("Привычка: %s, Описание: %s, Частота: %s, Дата создания: %s, Дата начала: %s, Период: %s, Дата окончания: %s, Статус: %s",
                name, description, frequency, createdDate, startDate, executionPeriod, endDate, status);
    }
}