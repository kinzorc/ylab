package ru.kinzorc.habittracker.core.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.kinzorc.habittracker.core.enums.User.UserRole;
import ru.kinzorc.habittracker.core.enums.User.UserStatusAccount;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты для класса User.
 */
@DisplayName("Тесты для сущности пользователя User")
class UserTest {

    private User user;

    @BeforeEach
    @DisplayName("Подготовка: создание нового пользователя")
    void setUp() {
        user = new User("testUser", "testPass123", "test@example.com", UserRole.USER);
    }

    @Test
    @DisplayName("Тест: Проверка установки и получения имени пользователя")
    void testGetAndSetUserName() {
        assertEquals("testUser", user.getUserName(), "Имя пользователя должно быть 'testUser'");
        user.setUserName("newUserName");
        assertEquals("newUserName", user.getUserName(), "Имя пользователя должно измениться на 'newUserName'");
    }

    @Test
    @DisplayName("Тест: Проверка установки и получения пароля")
    void testGetAndSetPassword() {
        assertEquals("testPass123", user.getPassword(), "Пароль должен быть 'testPass123'");
        user.setPassword("newPass123");
        assertEquals("newPass123", user.getPassword(), "Пароль должен измениться на 'newPass123'");
    }

    @Test
    @DisplayName("Тест: Проверка установки и получения email")
    void testGetAndSetEmail() {
        assertEquals("test@example.com", user.getEmail(), "Email должен быть 'test@example.com'");
        user.setEmail("newemail@example.com");
        assertEquals("newemail@example.com", user.getEmail(), "Email должен измениться на 'newemail@example.com'");
    }

    @Test
    @DisplayName("Тест: Проверка установки и получения роли пользователя")
    void testGetAndSetUserRole() {
        assertEquals(UserRole.USER, user.getUserRole(), "Роль пользователя должна быть 'USER'");
        user.setUserRole(UserRole.ADMIN);
        assertEquals(UserRole.ADMIN, user.getUserRole(), "Роль пользователя должна измениться на 'ADMIN'");
    }

    @Test
    @DisplayName("Тест: Проверка установки и получения статуса аккаунта")
    void testGetAndSetUserStatusAccount() {
        assertEquals(UserStatusAccount.ACTIVE, user.getUserStatusAccount(), "Статус должен быть 'ACTIVE'");
        user.setUserStatusAccount(UserStatusAccount.BLOCKED);
        assertEquals(UserStatusAccount.BLOCKED, user.getUserStatusAccount(), "Статус должен измениться на 'BLOCKED'");
    }

    @Test
    @DisplayName("Тест: Проверка метода equals для пользователей с одинаковым id")
    void testEquals() {
        User anotherUser = new User("otherUser", "otherPass", "other@example.com", UserRole.USER);
        anotherUser.setId(user.getId());

        assertEquals(user, anotherUser, "Пользователи с одинаковым id должны быть равны");
    }

    @Test
    @DisplayName("Тест: Проверка метода hashCode")
    void testHashCode() {
        User anotherUser = new User("otherUser", "otherPass", "other@example.com", UserRole.USER);
        anotherUser.setId(user.getId());

        assertEquals(user.hashCode(), anotherUser.hashCode(), "Хэш-коды пользователей с одинаковым id должны быть равны");
    }

    @Test
    @DisplayName("Тест: Проверка строкового представления объекта пользователя")
    void testToString() {
        String expected = String.format("Пользователь (id - %s): имя - testUser, email - test@example.com, роль - USER, статус аккаунта - ACTIVE", user.getId());
        assertEquals(expected, user.toString(), "Метод toString должен возвращать корректное строковое представление пользователя");
    }
}
