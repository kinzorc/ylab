package ru.kinzorc.habittracker.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.kinzorc.habittracker.application.dto.HabitDTO;
import ru.kinzorc.habittracker.application.dto.UserDTO;
import ru.kinzorc.habittracker.core.entities.Habit;
import ru.kinzorc.habittracker.core.entities.User;
import ru.kinzorc.habittracker.core.enums.Habit.HabitExecutionPeriod;
import ru.kinzorc.habittracker.core.enums.Habit.HabitFrequency;
import ru.kinzorc.habittracker.core.enums.User.UserRole;
import ru.kinzorc.habittracker.core.exceptions.HabitAlreadyExistsException;
import ru.kinzorc.habittracker.core.exceptions.HabitNotFoundException;
import ru.kinzorc.habittracker.core.exceptions.UserAlreadyExistsException;
import ru.kinzorc.habittracker.core.exceptions.UserNotFoundException;
import ru.kinzorc.habittracker.core.repository.HabitRepository;
import ru.kinzorc.habittracker.core.repository.UserRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApplicationServiceTest {

    private UserRepository userRepository;
    private HabitRepository habitRepository;
    private ApplicationService applicationService;

    private UserDTO testUserDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        habitRepository = Mockito.mock(HabitRepository.class);
        applicationService = new ApplicationService(userRepository, habitRepository);

        testUser = new User("TestUser", "password", "test@test.com", UserRole.USER);
        testUser.setId(1L);
        testUserDTO = new UserDTO(testUser);
    }

    @Test
    @DisplayName("Создание пользователя успешно")
    void createUser_success() throws SQLException, UserAlreadyExistsException {
        doNothing().when(userRepository).createUser(any(UserDTO.class));

        applicationService.createUser("TestUser", "password", "test@test.com");

        verify(userRepository, times(1)).createUser(any(UserDTO.class));
    }

    @Test
    @DisplayName("Создание пользователя — пользователь уже существует")
    void createUser_userAlreadyExists() throws SQLException, UserAlreadyExistsException {
        doThrow(new UserAlreadyExistsException("Пользователь с таким email уже зарегистрирован")).when(userRepository).createUser(any(UserDTO.class));

        applicationService.createUser("TestUser", "password", "test@test.com");

        verify(userRepository, times(1)).createUser(any(UserDTO.class));
    }

    @Test
    @DisplayName("Авторизация пользователя успешно")
    void loginUser_success() throws SQLException, UserNotFoundException {
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(testUserDTO));

        boolean result = applicationService.loginUser("test@test.com", "password");

        assertTrue(result);
        verify(userRepository, times(1)).addSession(testUser.getId());
    }

    @Test
    @DisplayName("Авторизация пользователя — неудачная попытка (неверный пароль)")
    void loginUser_invalidPassword() throws SQLException, UserNotFoundException {
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(testUserDTO));

        boolean result = applicationService.loginUser("test@test.com", "wrongPassword");

        assertFalse(result);
        verify(userRepository, never()).addSession(anyLong());
    }

    @Test
    @DisplayName("Авторизация пользователя — неудачная попытка (пользователь не найден)")
    void loginUser_userNotFound() throws SQLException, UserNotFoundException {
        when(userRepository.findUserByEmail(anyString())).thenThrow(new UserNotFoundException("Пользователь не найден"));

        boolean result = applicationService.loginUser("test@test.com", "password");

        assertFalse(result);
    }

    @Test
    @DisplayName("Выход пользователя успешно")
    void logoutUser_success() throws SQLException {
        applicationService.loginUser("test@test.com", "password");

        doNothing().when(userRepository).removeSession(testUser.getId());

        boolean result = applicationService.logoutUser(testUser);

        assertTrue(result);
        verify(userRepository, times(1)).removeSession(testUser.getId());
    }

    @Test
    @DisplayName("Добавление новой привычки успешно")
    void addHabit_success() throws SQLException, HabitAlreadyExistsException {

        doNothing().when(habitRepository).addHabit(any(UserDTO.class), any(HabitDTO.class));

        applicationService.addHabit(testUser, "test_habit", "test_description", HabitFrequency.DAILY, LocalDate.now(), HabitExecutionPeriod.MONTH);

        verify(habitRepository, times(1)).addHabit(any(UserDTO.class), any(HabitDTO.class));
    }

    @Test
    @DisplayName("Добавление новой привычки — привычка уже существует")
    void addHabit_habitAlreadyExists() throws SQLException, HabitAlreadyExistsException {
        doThrow(new HabitAlreadyExistsException("Привычка с таким именем уже существует")).when(habitRepository).addHabit(any(UserDTO.class), any(HabitDTO.class));

        applicationService.addHabit(testUser, "test_habit", "test_description", HabitFrequency.DAILY, LocalDate.now(), HabitExecutionPeriod.MONTH);

        verify(habitRepository, times(1)).addHabit(any(UserDTO.class), any(HabitDTO.class));
    }

    @Test
    @DisplayName("Удаление привычки успешно")
    void deleteHabit_success() throws SQLException, HabitNotFoundException {
        doNothing().when(habitRepository).resetExecutions(anyLong());
        doNothing().when(habitRepository).deleteHabit(anyLong());

        applicationService.deleteHabit(1L);

        verify(habitRepository, times(1)).resetExecutions(anyLong());
        verify(habitRepository, times(1)).deleteHabit(anyLong());
    }

    @Test
    @DisplayName("Удаление привычки — привычка не найдена")
    void deleteHabit_habitNotFound() throws SQLException, HabitNotFoundException {
        doThrow(new HabitNotFoundException("Привычка не найдена")).when(habitRepository).deleteHabit(anyLong());

        applicationService.deleteHabit(1L);

        verify(habitRepository, times(1)).deleteHabit(anyLong());
    }

    @Test
    @DisplayName("Редактирование привычки успешно")
    void editHabit_success() throws SQLException, HabitNotFoundException {

        Habit testHabit = new Habit("test_habit", "test_description", HabitFrequency.DAILY, LocalDate.now(), HabitExecutionPeriod.MONTH);
        testHabit.setId(1L);
        HabitDTO testHabitDTO = new HabitDTO(testHabit);

        ArgumentCaptor<HabitDTO> habitCaptor = ArgumentCaptor.forClass(HabitDTO.class);

        doNothing().when(habitRepository).updateHabit(any(HabitDTO.class));

        applicationService.editHabit(testHabit);

        verify(habitRepository, times(1)).updateHabit(habitCaptor.capture());

        HabitDTO capturedHabit = habitCaptor.getValue();
        assertEquals(testHabitDTO.getId(), capturedHabit.getId());
        assertEquals(testHabitDTO.getName(), capturedHabit.getName());
        assertEquals(testHabitDTO.getDescription(), capturedHabit.getDescription());
        assertEquals(testHabitDTO.getFrequency(), capturedHabit.getFrequency());
        assertEquals(testHabitDTO.getExecutionPeriod(), capturedHabit.getExecutionPeriod());
    }

    @Test
    @DisplayName("Редактирование привычки — привычка не найдена")
    void editHabit_habitNotFound() throws SQLException, HabitNotFoundException {
        doThrow(new HabitNotFoundException("Привычка не найдена")).when(habitRepository).updateHabit(any(HabitDTO.class));

        Habit testHabit = new Habit("test_habit", "test_description", HabitFrequency.DAILY, LocalDate.now(), HabitExecutionPeriod.MONTH);
        applicationService.editHabit(testHabit);

        verify(habitRepository, times(1)).updateHabit(any(HabitDTO.class));
    }
}
