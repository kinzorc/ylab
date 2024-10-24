package ru.kinzorc.habittracker.infrastructure.repository.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.kinzorc.habittracker.application.dto.HabitDTO;
import ru.kinzorc.habittracker.application.dto.UserDTO;
import ru.kinzorc.habittracker.core.entities.Habit;
import ru.kinzorc.habittracker.core.entities.User;
import ru.kinzorc.habittracker.core.enums.Habit.HabitExecutionPeriod;
import ru.kinzorc.habittracker.core.enums.Habit.HabitFrequency;
import ru.kinzorc.habittracker.core.enums.User.UserRole;
import ru.kinzorc.habittracker.core.enums.User.UserStatusAccount;
import ru.kinzorc.habittracker.core.exceptions.HabitAlreadyExistsException;
import ru.kinzorc.habittracker.core.exceptions.HabitNotFoundException;
import ru.kinzorc.habittracker.infrastructure.repository.utils.JdbcConnector;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class JdbcHabitRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private JdbcHabitRepository habitRepository;
    private UserDTO testUser;

    @BeforeEach
    void setUp() {
        // Инициализация источника данных
        JdbcConnector jdbcConnector = new JdbcConnector();
        habitRepository = new JdbcHabitRepository(jdbcConnector);

        testUser = new UserDTO(new User("testUser", "password123", "testUser@example.com", UserRole.USER));
        testUser.setId(1L);
        testUser.setUserStatusAccount(UserStatusAccount.ACTIVE);

    }


    @Test
    @DisplayName("Добавление новой привычки для пользователя")
    void addHabit_success() throws SQLException, HabitAlreadyExistsException {
        HabitDTO testHabit = new HabitDTO(new Habit("test_habit1", "test description", HabitFrequency.DAILY, LocalDate.now(), HabitExecutionPeriod.MONTH));
        testHabit.setUserId(testUser.getId());

        habitRepository.addHabit(testUser, testHabit);

        Optional<HabitDTO> savedHabit = habitRepository.findHabitByName(testHabit.getName());
        assertTrue(savedHabit.isPresent(), "Привычка должна быть добавлена.");
        assertEquals(testHabit.getName(), savedHabit.get().getName(), "Имена привычек должны совпадать.");
    }

    @Test
    @DisplayName("Попытка добавить уже существующую привычку")
    void addHabit_habitAlreadyExists() throws SQLException, HabitAlreadyExistsException {
        HabitDTO testHabit = new HabitDTO(new Habit("test_habit2", "test description", HabitFrequency.DAILY, LocalDate.now(), HabitExecutionPeriod.MONTH));
        testHabit.setUserId(testUser.getId());

        habitRepository.addHabit(testUser, testHabit);

        assertThrows(HabitAlreadyExistsException.class, () -> habitRepository.addHabit(testUser, testHabit), "Привычка уже существует.");
    }

    @Test
    @DisplayName("Удаление привычки несуществующего ID")
    void deleteHabit_notFound() {
        assertThrows(HabitNotFoundException.class, () -> habitRepository.deleteHabit(9999L), "Привычка с данным ID не найдена.");
    }

    @Test
    @DisplayName("Обновление привычки")
    void updateHabit_success() throws SQLException, HabitAlreadyExistsException, HabitNotFoundException {
        HabitDTO testHabit = new HabitDTO(new Habit("test_habit3", "test description", HabitFrequency.DAILY, LocalDate.now(), HabitExecutionPeriod.MONTH));
        testHabit.setUserId(testUser.getId());

        habitRepository.addHabit(testUser, testHabit);

        Optional<HabitDTO> testHabitOptional = habitRepository.findHabitByName(testHabit.getName());

        assertTrue(testHabitOptional.isPresent(), "Привычка должна существовать в базе данных после добавления.");

        HabitDTO savedHabit = testHabitOptional.get();
        savedHabit.setDescription("Updated Description");
        habitRepository.updateHabit(savedHabit);

        Optional<HabitDTO> updatedHabit = habitRepository.findHabitByID(savedHabit.getId());

        assertTrue(updatedHabit.isPresent(), "Обновленная привычка должна быть найдена в базе данных.");
        assertEquals("Updated Description", updatedHabit.get().getDescription(), "Описание должно быть обновлено.");
    }

    @Test
    @DisplayName("Поиск привычки по ID")
    void findHabitByID_success() throws SQLException, HabitAlreadyExistsException, HabitNotFoundException {
        HabitDTO testHabit = new HabitDTO(new Habit("test_habit4", "test description", HabitFrequency.DAILY, LocalDate.now(), HabitExecutionPeriod.MONTH));
        testHabit.setUserId(testUser.getId());

        habitRepository.addHabit(testUser, testHabit);

        Optional<HabitDTO> savedHabit = habitRepository.findHabitByName(testHabit.getName());
        assertTrue(savedHabit.isPresent());

        Optional<HabitDTO> foundHabit = habitRepository.findHabitByID(savedHabit.get().getId());
        assertTrue(foundHabit.isPresent());
        assertEquals(testHabit.getName(), foundHabit.get().getName(), "Привычка должна быть найдена по ID.");
    }

    @Test
    @DisplayName("Поиск привычки по имени")
    void findHabitByName_success() throws SQLException, HabitAlreadyExistsException {
        HabitDTO testHabit = new HabitDTO(new Habit("test_habit5", "test description", HabitFrequency.DAILY, LocalDate.now(), HabitExecutionPeriod.MONTH));
        testHabit.setUserId(testUser.getId());
        habitRepository.addHabit(testUser, testHabit);

        Optional<HabitDTO> foundHabit = habitRepository.findHabitByName(testHabit.getName());
        assertTrue(foundHabit.isPresent());
        assertEquals(testHabit.getName(), foundHabit.get().getName(), "Привычка должна быть найдена по имени.");
    }

}
