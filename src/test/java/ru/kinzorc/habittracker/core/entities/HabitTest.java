package ru.kinzorc.habittracker.core.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.kinzorc.habittracker.core.enums.Habit.HabitExecutionPeriod;
import ru.kinzorc.habittracker.core.enums.Habit.HabitFrequency;
import ru.kinzorc.habittracker.core.enums.Habit.HabitStatus;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты для сущности Habit.
 */
@DisplayName("Тесты для сущности привычки Habit")
class HabitTest {

    private Habit habit;

    @BeforeEach
    @DisplayName("Подготовка: создание новой привычки")
    void setUp() {
        habit = new Habit("Morning Run", "Run every day", HabitFrequency.DAILY, LocalDate.now(), HabitExecutionPeriod.MONTH);
    }

    @Test
    @DisplayName("Тест: Проверка установки и получения имени привычки")
    void testGetAndSetName() {
        assertEquals("Morning Run", habit.getName(), "Имя привычки должно быть 'Morning Run'");
        habit.setName("Evening Walk");
        assertEquals("Evening Walk", habit.getName(), "Имя привычки должно измениться на 'Evening Walk'");
    }

    @Test
    @DisplayName("Тест: Проверка установки и получения описания привычки")
    void testGetAndSetDescription() {
        assertEquals("Run every day", habit.getDescription(), "Описание привычки должно быть 'Run every day'");
        habit.setDescription("Walk every evening");
        assertEquals("Walk every evening", habit.getDescription(), "Описание привычки должно измениться на 'Walk every evening'");
    }

    @Test
    @DisplayName("Тест: Проверка установки и получения частоты выполнения привычки")
    void testGetAndSetFrequency() {
        assertEquals(HabitFrequency.DAILY, habit.getFrequency(), "Частота должна быть 'DAILY'");
        habit.setFrequency(HabitFrequency.WEEKLY);
        assertEquals(HabitFrequency.WEEKLY, habit.getFrequency(), "Частота должна измениться на 'WEEKLY'");
    }

    @Test
    @DisplayName("Тест: Проверка установки и получения периода выполнения привычки")
    void testGetAndSetExecutionPeriod() {
        assertEquals(HabitExecutionPeriod.MONTH, habit.getExecutionPeriod(), "Период должен быть 'MONTH'");
        habit.setExecutionPeriod(HabitExecutionPeriod.YEAR);
        assertEquals(HabitExecutionPeriod.YEAR, habit.getExecutionPeriod(), "Период должен измениться на 'YEAR'");
    }

    @Test
    @DisplayName("Тест: Проверка установки и получения даты начала выполнения")
    void testGetAndSetStartDate() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        habit.setStartDate(startDate);
        assertEquals(startDate, habit.getStartDate(), "Дата начала должна быть '2024-01-01'");
    }

    @Test
    @DisplayName("Тест: Проверка вычисления и установки даты окончания выполнения")
    void testCalculateAndSetEndDate() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        habit.setStartDate(startDate);

        habit.setExecutionPeriod(HabitExecutionPeriod.MONTH);
        assertEquals(startDate.plusMonths(1), habit.getEndDate(), "Дата окончания должна быть через месяц от начала");

        habit.setExecutionPeriod(HabitExecutionPeriod.YEAR);
        assertEquals(startDate.plusYears(1), habit.getEndDate(), "Дата окончания должна быть через год от начала");
    }

    @Test
    @DisplayName("Тест: Проверка установки и получения статуса привычки")
    void testGetAndSetStatus() {
        assertEquals(HabitStatus.ACTIVE, habit.getStatus(), "Статус должен быть 'ACTIVE'");
        habit.setStatus(HabitStatus.FINISHED);
        assertEquals(HabitStatus.FINISHED, habit.getStatus(), "Статус должен измениться на 'FINISHED'");
    }

    @Test
    @DisplayName("Тест: Проверка добавления и получения списка выполнений привычки")
    void testGetAndSetExecutions() {
        habit.setExecutions(List.of(LocalDate.now(), LocalDate.now().minusDays(1)));
        assertEquals(2, habit.getExecutions().size(), "Размер списка выполнений должен быть 2");
    }

    @Test
    @DisplayName("Тест: Проверка метода equals для привычек с одинаковым id")
    void testEquals() {
        Habit anotherHabit = new Habit("Evening Walk", "Walk every evening", HabitFrequency.WEEKLY, LocalDate.now(), HabitExecutionPeriod.YEAR);
        anotherHabit.setId(habit.getId());

        assertEquals(habit, anotherHabit, "Привычки с одинаковым id должны быть равны");
    }

    @Test
    @DisplayName("Тест: Проверка метода hashCode")
    void testHashCode() {
        Habit anotherHabit = new Habit("Evening Walk", "Walk every evening", HabitFrequency.WEEKLY, LocalDate.now(), HabitExecutionPeriod.YEAR);
        anotherHabit.setId(habit.getId());

        assertEquals(habit.hashCode(), anotherHabit.hashCode(), "Хэш-коды привычек с одинаковым id должны быть равны");
    }

    @Test
    @DisplayName("Тест: Проверка строкового представления объекта привычки")
    void testToString() {
        String expected = String.format("Привычка: %s, Описание: %s, Частота: %s, Дата создания: %s, Дата начала: %s, Период: %s, Дата окончания: %s, Статус: %s",
                habit.getName(), habit.getDescription(), habit.getFrequency(), habit.getCreatedDate(), habit.getStartDate(), habit.getExecutionPeriod(), habit.getEndDate(), habit.getStatus());
        assertEquals(expected, habit.toString(), "Метод toString должен возвращать корректное строковое представление привычки");
    }
}
