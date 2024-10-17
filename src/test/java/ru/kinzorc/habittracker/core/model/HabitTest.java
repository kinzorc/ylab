package ru.kinzorc.habittracker.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kinzorc.habittracker.common.data.FrequencyHabit;
import ru.kinzorc.habittracker.common.data.HabitStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class HabitTest {

    private Habit habit;

    @BeforeEach
    void setUp() {
        habit = new Habit("Morning Run", "Run every morning", FrequencyHabit.DAY, LocalDate.now().minusDays(5));
    }

    @Test
    void testGetName() {
        assertEquals("Morning Run", habit.getName());
    }

    @Test
    void testSetName() {
        habit.setName("Evening Run");
        assertEquals("Evening Run", habit.getName());
    }

    @Test
    void testGetDescription() {
        assertEquals("Run every morning", habit.getDescription());
    }

    @Test
    void testSetDescription() {
        habit.setDescription("Run every evening");
        assertEquals("Run every evening", habit.getDescription());
    }

    @Test
    void testGetFrequency() {
        assertEquals(FrequencyHabit.DAY, habit.getFrequency());  // Проверяем начальное значение
    }

    @Test
    void testSetFrequency() {
        habit.setFrequency(FrequencyHabit.WEEK);  // Устанавливаем WEEK и проверяем
        assertEquals(FrequencyHabit.WEEK, habit.getFrequency());
        assertTrue(habit.getCompletionHistory().isEmpty());  // История выполнения должна быть очищена
    }

    @Test
    void testGetCreatedDate() {
        assertEquals(LocalDate.now(), habit.getCreatedDate());  // Проверяем, что привычка была создана сегодня
    }

    @Test
    void testGetStartDate() {
        assertEquals(LocalDate.now().minusDays(5), habit.getStartDate());  // Проверяем дату начала
    }

    @Test
    void testSetStartDate() {
        LocalDate newStartDate = LocalDate.now().minusDays(2);  // Устанавливаем новую дату
        habit.setStartDate(newStartDate);
        assertEquals(newStartDate, habit.getStartDate());  // Проверяем корректность
        assertTrue(habit.getCompletionHistory().isEmpty());  // История выполнения должна быть очищена
    }

    @Test
    void testGetStatus() {
        assertEquals(HabitStatus.ACTIVE, habit.getStatus());  // Проверяем статус
    }

    @Test
    void testSetStatus() {
        habit.setStatus(HabitStatus.FINISHED);  // Устанавливаем FINISHED и проверяем
        assertEquals(HabitStatus.FINISHED, habit.getStatus());
    }

    @Test
    void testMarkCompletion() {
        LocalDate today = LocalDate.now();  // Используем реальную дату
        habit.markCompletion(today);
        assertTrue(habit.getCompletionHistory().contains(today));

        // Попытка повторного выполнения привычки в тот же день
        habit.markCompletion(today);
        assertEquals(1, habit.getCompletionHistory().size());  // Должен быть только один вход
    }

    @Test
    void testIsCompletedThisWeek() {
        LocalDate today = LocalDate.now();
        habit.markCompletion(today);
        assertTrue(habit.isCompletedThisWeek(today));  // Проверяем, выполнена ли привычка на этой неделе
    }

    @Test
    void testCalculateDailyStreak() {
        habit.markCompletion(LocalDate.now());
        habit.markCompletion(LocalDate.now().minusDays(1));
        habit.markCompletion(LocalDate.now().minusDays(2));

        int streak = habit.calculateStreak();
        assertEquals(3, streak);  // Стрик должен быть равен 3
    }

    @Test
    void testCalculateWeeklyStreak() {
        Habit habitTest = new Habit("Run", "Run", FrequencyHabit.WEEK, LocalDate.now().minusWeeks(5));


        // Выполнение привычки на разных неделях
        habitTest.markCompletion(LocalDate.now());  // Выполнение на текущей неделе
        habitTest.markCompletion(LocalDate.now().minusWeeks(1));  // Выполнение на прошлой неделе
        habitTest.markCompletion(LocalDate.now().minusWeeks(2));  // Выполнение две недели назад

        // Проверяем, что стрик правильно считается за три недели
        int streak = habitTest.calculateStreak();
        assertEquals(3, streak);  // Ожидаем стрик длиной 3
    }

    @Test
    void testEqualsAndHashCode() {
        Habit sameHabit = new Habit("Morning Run", "Run every morning", FrequencyHabit.DAY, LocalDate.now().minusDays(5));
        Habit differentHabit = new Habit("Evening Run", "Run every evening", FrequencyHabit.WEEK, LocalDate.now().minusDays(5));

        assertEquals(habit, sameHabit);  // Объекты равны
        assertNotEquals(habit, differentHabit);  // Объекты не равны

        assertEquals(habit.hashCode(), sameHabit.hashCode());  // Одинаковый hashCode для равных объектов
        assertNotEquals(habit.hashCode(), differentHabit.hashCode());  // Разные hashCode для разных объектов
    }
}