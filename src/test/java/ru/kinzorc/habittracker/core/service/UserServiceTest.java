package ru.kinzorc.habittracker.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.kinzorc.habittracker.common.config.HandlerConstants;
import ru.kinzorc.habittracker.core.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Инициализация mock объектов и сброс данных перед каждым тестом
        MockitoAnnotations.openMocks(this);
        HandlerConstants.USERS.clear(); // Очищаем мапу перед каждым тестом
    }

    @Test
    void testRegisterUserSuccess() {
        // Регистрация пользователя с уникальным email
        boolean result = userService.registerUser("Test User", "test@example.com", "password");
        assertTrue(result, "Пользователь должен быть успешно зарегистрирован");

        // Проверяем, что пользователь добавлен в HandlerConstants.USERS
        assertNotNull(HandlerConstants.USERS.get("test@example.com"));
    }

    @Test
    void testRegisterUserFailureWhenEmailExists() {
        // Добавляем пользователя в мапу для эмуляции уже существующего email
        HandlerConstants.USERS.put("test@example.com", new User("Existing User", "test@example.com", "password", false, false, false));

        // Попытка зарегистрировать пользователя с тем же email
        boolean result = userService.registerUser("New User", "test@example.com", "password");
        assertFalse(result, "Регистрация не должна быть успешной, если email уже существует");
    }

    @Test
    void testLoginUserSuccess() {
        // Добавляем пользователя в мапу для авторизации
        User user = new User("Test User", "test@example.com", "password", false, false, false);
        HandlerConstants.USERS.put("test@example.com", user);

        // Авторизация пользователя
        User loggedInUser = userService.loginUser("test@example.com", "password");
        assertNotNull(loggedInUser, "Пользователь должен успешно войти");
        assertTrue(loggedInUser.isLogin(), "Статус логина пользователя должен быть true");
    }

    @Test
    void testLoginUserFailureWhenWrongPassword() {
        // Добавляем пользователя в мапу
        HandlerConstants.USERS.put("test@example.com", new User("Test User", "test@example.com", "password", false, false, false));

        // Попытка авторизации с неправильным паролем
        User loggedInUser = userService.loginUser("test@example.com", "wrongpassword");
        assertNull(loggedInUser, "Авторизация должна провалиться с неправильным паролем");
    }

    @Test
    void testDeleteUserSuccess() {
        // Добавляем пользователя в мапу для удаления
        User user = new User("Test User", "test@example.com", "password", false, false, false);
        HandlerConstants.USERS.put("test@example.com", user);

        // Удаление пользователя
        boolean result = userService.deleteUser(user);
        assertTrue(result, "Пользователь должен быть успешно удален");

        // Проверяем, что пользователь удален из мапы
        assertNull(HandlerConstants.USERS.get("test@example.com"));
    }

    @Test
    void testDeleteUserFailureWhenUserIsNull() {
        // Попытка удалить null пользователя
        boolean result = userService.deleteUser(null);
        assertFalse(result, "Удаление должно провалиться, если пользователь равен null");
    }
}