package ru.kinzorc.habittracker.core.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import ru.kinzorc.habittracker.common.config.HandlerConstants;
import ru.kinzorc.habittracker.common.data.DataOfUser;
import ru.kinzorc.habittracker.common.util.InputUtils;
import ru.kinzorc.habittracker.core.model.User;
import ru.kinzorc.habittracker.core.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserHandlerTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        HandlerConstants.USER_SERVICE = userService;
    }

    @Test
    void testHandleRegisterUserSuccess() {
        try (MockedStatic<InputUtils> mockedInputUtils = Mockito.mockStatic(InputUtils.class)) {
            // Мокаем ввод данных пользователя
            mockedInputUtils.when(() -> InputUtils.promptValidInputUserData(eq(DataOfUser.NAME), anyString(), anyString()))
                    .thenReturn("Test User");
            mockedInputUtils.when(() -> InputUtils.promptValidInputUserData(eq(DataOfUser.EMAIL), anyString(), anyString()))
                    .thenReturn("test@example.com");
            mockedInputUtils.when(() -> InputUtils.promptValidInputUserData(eq(DataOfUser.PASSWORD), anyString(), anyString()))
                    .thenReturn("password123");

            // Мокаем успешную регистрацию пользователя
            when(userService.registerUser(anyString(), anyString(), anyString())).thenReturn(true);

            // Вызов метода
            UserHandler.handleRegisterUser();

            // Проверяем, что метод регистрации был вызван
            verify(userService).registerUser("Test User", "test@example.com", "password123");
        }
    }

    @Test
    void testHandleLoginUserSuccess() {
        try (MockedStatic<InputUtils> mockedInputUtils = Mockito.mockStatic(InputUtils.class)) {
            // Мокаем ввод email и пароля
            mockedInputUtils.when(() -> InputUtils.promptInput("  Email: ")).thenReturn("test@example.com");
            mockedInputUtils.when(() -> InputUtils.promptInput("  Пароль: ")).thenReturn("password123");

            // Мокаем пользователя, который успешно залогинился
            User user = new User("Test User", "test@example.com", "password123", false, false, false);
            user.setLogin(true);  // Успешный вход
            when(userService.loginUser(anyString(), anyString())).thenReturn(user);

            // Вызов метода
            boolean result = UserHandler.handleLoginUser();

            // Проверяем успешный вход
            assertTrue(result, "Пользователь должен успешно войти");
            assertTrue(user.isLogin(), "Пользователь должен быть залогинен");
            assertEquals("Test User", HandlerConstants.CURRENT_USER.getName());
        }
    }

    @Test
    void testHandleLoginUserFailed() {
        try (MockedStatic<InputUtils> mockedInputUtils = Mockito.mockStatic(InputUtils.class)) {
            // Мокаем ввод email и пароля
            mockedInputUtils.when(() -> InputUtils.promptInput("  Email: ")).thenReturn("test@example.com");
            mockedInputUtils.when(() -> InputUtils.promptInput("  Пароль: ")).thenReturn("wrongpassword");

            // Мокаем неудачный вход (пользователь не найден или неправильный пароль)
            when(userService.loginUser(anyString(), anyString())).thenReturn(null);

            // Вызов метода
            boolean result = UserHandler.handleLoginUser();

            // Проверяем, что вход не удался
            assertFalse(result);
            assertNull(HandlerConstants.CURRENT_USER);
        }
    }

    @Test
    void testHandleDeleteUserSuccess() {
        try (MockedStatic<InputUtils> mockedInputUtils = Mockito.mockStatic(InputUtils.class)) {
            // Мокаем ввод пароля
            mockedInputUtils.when(() -> InputUtils.promptInput("Введите пароль для удаления аккаунта: ")).thenReturn("password123");

            // Добавляем пользователя в мапу
            User user = new User("Test User", "test@example.com", "password123", false, false, false);

            // Мокаем успешное удаление
            when(userService.deleteUser(any(User.class))).thenReturn(true);

            // Вызов метода
            boolean result = UserHandler.handleDeleteUser(user);

            // Проверяем успешное удаление
            assertTrue(result);
            verify(userService).deleteUser(user);
        }
    }

    @Test
    void testHandleDeleteUserFailed() {
        try (MockedStatic<InputUtils> mockedInputUtils = Mockito.mockStatic(InputUtils.class)) {
            // Мокаем ввод пароля
            mockedInputUtils.when(() -> InputUtils.promptInput("Введите пароль для удаления аккаунта: ")).thenReturn("wrongpassword");

            // Добавляем пользователя в мапу
            User user = new User("Test User", "test@example.com", "password123", false, false, false);

            // Вызов метода
            boolean result = UserHandler.handleDeleteUser(user);

            // Проверяем, что удаление не удалось
            assertFalse(result);
            verify(userService, never()).deleteUser(user);
        }
    }
}