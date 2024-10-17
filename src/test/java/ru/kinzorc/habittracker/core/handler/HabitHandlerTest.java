package ru.kinzorc.habittracker.core.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.kinzorc.habittracker.common.config.HandlerConstants;
import ru.kinzorc.habittracker.common.data.FrequencyHabit;
import ru.kinzorc.habittracker.common.util.InputUtils;
import ru.kinzorc.habittracker.core.model.Habit;
import ru.kinzorc.habittracker.core.model.User;
import ru.kinzorc.habittracker.core.service.HabitService;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class HabitHandlerTest {

    private User user;

    @BeforeEach
    void setUp() {
        // Инициализация пользователя
        user = new User("testUser", "test@example.com", "password", false, false, false);
        // Мокируем HandlerConstants.HABIT_SERVICE
        HandlerConstants.HABIT_SERVICE = Mockito.mock(HabitService.class);
    }

    @Test
    void testAddHabitForUser() {
        try (var mockedInputUtils = mockStatic(InputUtils.class)) {
            // Мокируем ввод данных
            mockedInputUtils.when(() -> InputUtils.promptInput(anyString()))
                    .thenReturn("Morning Run", "Run every morning");

            mockedInputUtils.when(() -> InputUtils.promptFrequencyValid(anyString(), anyString()))
                    .thenReturn("DAY");  // Возвращаем "DAY" для частоты выполнения

            mockedInputUtils.when(() -> InputUtils.promptDateValid(anyString(), anyString()))
                    .thenReturn(LocalDate.now().plusDays(1));  // Дата начала в будущем

            // Мокирование вызова сервиса
            doNothing().when(HandlerConstants.HABIT_SERVICE).addHabit(any(User.class), any(Habit.class));

            // Запуск тестируемого метода
            HabitHandler.addHabitForUser(user);

            // Проверка, что привычка была добавлена через сервис
            verify(HandlerConstants.HABIT_SERVICE).addHabit(any(User.class), any(Habit.class));
        }
    }

    @Test
    void testRemoveHabitForUser() {
        try (var mockedInputUtils = mockStatic(InputUtils.class)) {
            // Мокируем ввод названия привычки
            mockedInputUtils.when(() -> InputUtils.promptInput(anyString()))
                    .thenReturn("Morning Run");

            // Мокирование вызова удаления привычки
            when(HandlerConstants.HABIT_SERVICE.removeHabit(any(User.class), anyString()))
                    .thenReturn(true);  // Предположим, что привычка была удалена

            // Запуск тестируемого метода
            HabitHandler.removeHabitForUser(user);

            // Проверка, что привычка была удалена через сервис
            verify(HandlerConstants.HABIT_SERVICE).removeHabit(any(User.class), anyString());
        }
    }

    @Test
    void testEditHabitForUser() {
        Habit mockHabit = new Habit("Morning Run", "Run every morning", FrequencyHabit.DAY, LocalDate.now());

        try (var mockedInputUtils = mockStatic(InputUtils.class)) {
            // Мокируем ввод названия привычки
            mockedInputUtils.when(() -> InputUtils.promptInput(anyString()))
                    .thenReturn("Morning Run", "Evening Run");

            // Мокируем получение привычки
            when(HandlerConstants.HABIT_SERVICE.getHabit(any(User.class), anyString()))
                    .thenReturn(mockHabit);

            // Запуск тестируемого метода
            HabitHandler.editHabitForUser(user);

            // Проверка, что привычка была получена и изменена
            verify(HandlerConstants.HABIT_SERVICE).getHabit(any(User.class), anyString());
        }
    }

    @Test
    void testUserMarkDoneHabitHabitFound() {
        try (var mockedInputUtils = mockStatic(InputUtils.class)) {
            Habit mockHabit = new Habit("Morning Run", "Run every morning", FrequencyHabit.DAY, LocalDate.now().minusDays(5));

            // Мокируем ввод названия привычки
            mockedInputUtils.when(() -> InputUtils.promptInput(anyString()))
                    .thenReturn("Morning Run");

            // Мокируем получение привычки
            when(HandlerConstants.HABIT_SERVICE.getHabit(any(User.class), anyString()))
                    .thenReturn(mockHabit);

            // Мокируем вызов метода для отметки привычки
            doNothing().when(HandlerConstants.HABIT_SERVICE).markHabit(mockHabit);

            // Запуск тестируемого метода
            HabitHandler.userMarkDoneHabit(user);

            // Проверка, что привычка была получена и выполнена
            verify(HandlerConstants.HABIT_SERVICE).getHabit(any(User.class), anyString());
            verify(HandlerConstants.HABIT_SERVICE).markHabit(mockHabit);
        }
    }

    @Test
    void testUserMarkDoneHabitHabitNotFound() {
        try (var mockedInputUtils = mockStatic(InputUtils.class)) {
            // Мокируем ввод названия привычки
            mockedInputUtils.when(() -> InputUtils.promptInput(anyString()))
                    .thenReturn("Evening Walk");

            // Мокируем отсутствие привычки
            when(HandlerConstants.HABIT_SERVICE.getHabit(any(User.class), anyString()))
                    .thenReturn(null);

            // Запуск тестируемого метода
            HabitHandler.userMarkDoneHabit(user);

            // Проверка, что привычка была запрошена, но не найдена
            verify(HandlerConstants.HABIT_SERVICE).getHabit(any(User.class), anyString());
        }
    }
}