package ru.kinzorc.habittracker.core.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kinzorc.habittracker.common.data.FrequencyHabit;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        // Инициализируем объект User перед каждым тестом
        user = new User("Test User", "test@example.com", "password123", false, false, false);
    }

    @Test
    void testGetName() {
        assertEquals("Test User", user.getName());
    }

    @Test
    void testSetName() {
        user.setName("User Test");
        assertEquals("User Test", user.getName());
    }

    @Test
    void testGetEmail() {
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void testSetEmail() {
        user.setEmail("user@example.com");
        assertEquals("user@example.com", user.getEmail());
    }

    @Test
    void testGetPassword() {
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testSetPassword() {
        user.setPassword("newpassword123");
        assertEquals("newpassword123", user.getPassword());
    }

    @Test
    void testIsAdmin() {
        assertFalse(user.isAdmin());
    }

    @Test
    void testIsLogin() {
        assertFalse(user.isLogin());
        user.setLogin(true);
        assertTrue(user.isLogin());
    }

    @Test
    void testIsBlocked() {
        assertFalse(user.isBlocked());
        user.setBlocked(true);
        assertTrue(user.isBlocked());
    }

    @Test
    void testEqualsAndHashCode() {
        User sameUser = new User("Test User", "test@example.com", "password123", false, false, false);
        User differentUser = new User("User Test", "user@example.com", "password456", false, false, false);

        assertEquals(user, sameUser); // Объекты равны
        assertNotEquals(user, differentUser); // Объекты не равны

        assertEquals(user.hashCode(), sameUser.hashCode()); // Одинаковый hashCode для равных объектов
        assertNotEquals(user.hashCode(), differentUser.hashCode()); // Разные hashCode для разных объектов
    }

    @Test
    void testHabitsCollection() {
        HashMap<String, Habit> habits = user.getHabits();
        assertNotNull(habits); // Коллекция не должна быть null
        assertTrue(habits.isEmpty()); // На момент создания коллекция должна быть пустой

        // Пример добавления привычки
        habits.put("Morning Run", new Habit("Morning Run", "Morning Run", eq(FrequencyHabit.DAY), eq(LocalDate.now())));
        assertEquals(1, habits.size()); // Проверяем, что привычка добавлена
        assertTrue(habits.containsKey("Morning Run"));
    }
}