package ru.kinzorc.habittracker.presentation.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.kinzorc.habittracker.core.enums.User.UserData;

import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MenuUtilsTest {

    private MenuUtils menuUtils;
    private Scanner mockScanner;

    @BeforeEach
    void setUp() {
        menuUtils = new MenuUtils();
        mockScanner = mock(Scanner.class);
    }

    @Test
    @DisplayName("Ввод пункта меню - корректный ввод")
    void promptMenuValidInput_valid() {
        when(mockScanner.hasNextInt()).thenReturn(true);
        when(mockScanner.nextInt()).thenReturn(3);

        int result = menuUtils.promptMenuValidInput(mockScanner);

        assertEquals(3, result, "Значение должно быть 3");
        verify(mockScanner, times(1)).nextInt();
    }

    @Test
    @DisplayName("Ввод пункта меню - некорректный ввод, повторный запрос")
    void promptMenuValidInput_invalid_thenValid() {
        when(mockScanner.hasNextInt()).thenReturn(false).thenReturn(true);
        when(mockScanner.nextInt()).thenReturn(2);

        int result = menuUtils.promptMenuValidInput(mockScanner);

        assertEquals(2, result, "Значение должно быть 2 после некорректного ввода");
        verify(mockScanner, times(2)).hasNextInt();
    }

    @Test
    @DisplayName("Проверка корректности имени пользователя")
    void isValidUsername() {
        assertTrue(menuUtils.isValidUsername("validUsername"), "Имя пользователя должно быть корректным");
        assertFalse(menuUtils.isValidUsername(""), "Пустое имя пользователя недопустимо");
        assertFalse(menuUtils.isValidUsername("ab"), "Имя пользователя должно содержать минимум 3 символа");
    }

    @Test
    @DisplayName("Проверка корректности email")
    void isValidEmail() {
        assertTrue(menuUtils.isValidEmail("test@example.com"), "Корректный email должен пройти проверку");
        assertFalse(menuUtils.isValidEmail("invalid-email"), "Некорректный email не должен пройти проверку");
    }

    @Test
    @DisplayName("Проверка корректности пароля")
    void isValidPassword() {
        assertTrue(menuUtils.isValidPassword("Valid11!"), "Пароль с валидными символами должен пройти проверку");
        assertFalse(menuUtils.isValidPassword("aa"), "Короткий пароль не должен пройти проверку");
        assertFalse(menuUtils.isValidPassword("nouppercase1!"), "Пароль без заглавной буквы не должен пройти проверку");
    }

    @Test
    @DisplayName("Проверка ввода имени пользователя с валидацией")
    void promptValidInputUserData_username() {
        when(mockScanner.nextLine()).thenReturn("validUsername");

        String result = menuUtils.promptValidInputUserData(mockScanner, UserData.USERNAME, "Введите имя пользователя: ", "Ошибка!");

        assertEquals("validUsername", result, "Имя пользователя должно быть валидным");
    }

    @Test
    @DisplayName("Проверка ввода email с валидацией")
    void promptValidInputUserData_email() {
        when(mockScanner.nextLine()).thenReturn("invalid-email").thenReturn("test@example.com");

        String result = menuUtils.promptValidInputUserData(mockScanner, UserData.EMAIL, "Введите email: ", "Ошибка!");

        assertEquals("test@example.com", result, "Email должен быть валидным после второго ввода");
    }

    @Test
    @DisplayName("Проверка ввода частоты выполнения привычки")
    void promptHabitFrequencyValid() {
        when(mockScanner.nextLine()).thenReturn("daily");

        String result = menuUtils.promptHabitFrequencyValid(mockScanner, "Введите частоту выполнения привычки: ", "Ошибка!");

        assertEquals("DAILY", result, "Частота выполнения должна быть DAILY");
    }

    @Test
    @DisplayName("Проверка ввода периода выполнения привычки")
    void promptHabitExecutionPeriodValid() {
        when(mockScanner.nextLine()).thenReturn("month");

        String result = menuUtils.promptHabitExecutionPeriodValid(mockScanner, "Введите период выполнения: ", "Ошибка!");

        assertEquals("MONTH", result, "Период выполнения должен быть MONTH");
    }

    @Test
    @DisplayName("Проверка корректного ввода даты")
    void promptDateValid_valid() {
        when(mockScanner.nextLine()).thenReturn("12.12.2024");

        LocalDate result = menuUtils.promptDateValid(mockScanner, "Введите дату: ", "Некорректная дата!");

        assertEquals(LocalDate.of(2024, 12, 12), result, "Дата должна быть корректно преобразована");
    }

    @Test
    @DisplayName("Проверка некорректного ввода даты")
    void promptDateValid_invalid_thenValid() {
        when(mockScanner.nextLine()).thenReturn("invalid-date").thenReturn("12.12.2024");

        LocalDate result = menuUtils.promptDateValid(mockScanner, "Введите дату: ", "Некорректная дата!");

        assertEquals(LocalDate.of(2024, 12, 12), result, "После некорректного ввода дата должна быть преобразована корректно");
    }

    @Test
    @DisplayName("Проверка генерации кода сброса")
    void generateResetCode() {
        String code = menuUtils.generateResetCode();

        assertEquals(6, code.length(), "Сгенерированный код должен быть шестизначным");
    }

    @Test
    @DisplayName("Проверка корректности кода сброса")
    void isValidResetCode() {
        String code = "123456";
        assertTrue(menuUtils.isValidResetCode("123456", code), "Код должен совпадать");
        assertFalse(menuUtils.isValidResetCode("654321", code), "Код не должен совпадать");
    }
}
