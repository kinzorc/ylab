package ru.kinzorc.habittracker.application.service;

import ru.kinzorc.habittracker.application.dto.HabitDTO;
import ru.kinzorc.habittracker.application.dto.UserDTO;
import ru.kinzorc.habittracker.core.entities.Habit;
import ru.kinzorc.habittracker.core.entities.User;
import ru.kinzorc.habittracker.core.enums.Habit.HabitExecutionPeriod;
import ru.kinzorc.habittracker.core.enums.Habit.HabitFrequency;
import ru.kinzorc.habittracker.core.enums.User.UserData;
import ru.kinzorc.habittracker.core.enums.User.UserRole;
import ru.kinzorc.habittracker.core.exceptions.HabitAlreadyExistsException;
import ru.kinzorc.habittracker.core.exceptions.HabitNotFoundException;
import ru.kinzorc.habittracker.core.exceptions.UserAlreadyExistsException;
import ru.kinzorc.habittracker.core.exceptions.UserNotFoundException;
import ru.kinzorc.habittracker.core.repository.HabitRepository;
import ru.kinzorc.habittracker.core.repository.UserRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ApplicationService {

    private final UserRepository userRepository;
    private final HabitRepository habitRepository;

    private User currentUser;

    public ApplicationService(UserRepository userRepository, HabitRepository habitRepository) {
        this.userRepository = userRepository;
        this.habitRepository = habitRepository;
    }

    public void createUser(String name, String password, String email) {
        if (name != null && password != null && email != null) {
            try {
                User user = new User(name, password, email, UserRole.USER);
                userRepository.createUser(new UserDTO(user));
                System.out.println("Вы успешно зарегистрировались!");
            } catch (UserAlreadyExistsException e) {
                System.out.println("Пользователь с таким email уже зарегистрирован!");
            } catch (SQLException e) {
                System.err.println("Ошибка добавления пользователя в базу: " + e.getMessage());
            }
        } else {
            System.err.println("Некорректные или пустые данные: имя, пароль, email.");
        }
    }

    public boolean loginUser(String email, String password) {
        try {
            Optional<UserDTO> userDTO = userRepository.findUserByEmail(email);
            if (userDTO.isPresent()) {
                User user = userDTO.get().toUser();

                if (user.getPassword().equals(password)) {
                    currentUser = user;
                    userRepository.addSession(userDTO.get().getId());

                    System.out.println("Вы успешно авторизовались!");

                    return true;
                }
            }

            System.err.println("Неудачная попытка входа! Попробуйте еще раз.");
            return false;
        } catch (SQLException e) {
            System.err.println("Ошибка в запросе к базе данных" + e.getMessage());
            return false;
        } catch (UserNotFoundException e) {
            System.err.println("Пользователь не найден!");
            return false;
        }
    }

    public boolean logoutUser(User user) {
        try {
            currentUser = null;
            userRepository.removeSession(user.getId());
            System.out.println("Выход из аккаунта: " + user.getUserName());
            return true;
        } catch (SQLException e) {
            System.err.println("Произошла ошибка! Попробуйте еще раз! " + e.getMessage());
            return false;
        }
    }

    // Изменение данных пользователя (имя, email, пароль)
    public void editUser(User user) {
        if (currentUser == null) {
            System.err.println("Вы не авторизованы!");
            return;
        }

        try {
            userRepository.updateUser(new UserDTO(user));
        } catch (SQLException e) {
            System.err.println("Ошибка обновления данных пользователя.");
        } catch (UserNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    // Изменение роли пользователя
    public void changeUserRole(User user) {
        if (currentUser == null || !currentUser.getUserRole().equals(UserRole.ADMIN)) {
            System.err.println("У вас недостаточно прав для изменения роли.");
            return;
        }

        try {
            userRepository.updateUser(new UserDTO(user));
            System.out.println("Роль пользователя успешно обновлена.");
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении роли.");
        } catch (UserNotFoundException e) {
            System.err.println("Пользователь не найден!");
        }
    }

    // Удаление пользователя с удалением сессии
    public void deleteUser(User user) {
        if (currentUser == null || !currentUser.getUserRole().equals(UserRole.ADMIN)) {
            System.err.println("У вас недостаточно прав для удаления пользователя.");
            return;
        }

        try {
            userRepository.removeSession(user.getId());

            try {
                habitRepository.deleteAllHabitsForUser(new UserDTO(user));
                habitRepository.resetAllExecutionsForUser(new UserDTO(user));
            } catch (HabitNotFoundException e) {
                System.err.println("Привычки и их выполнения не найдены для пользователя, удаление продолжается...");
            }

            userRepository.deleteUser(user.getId());
            currentUser = null;
            System.out.println("Пользователь успешно удален.");
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении пользователя: " + e.getMessage());
        } catch (UserNotFoundException e) {
            System.err.println("Пользователь не найден!");
        }
    }

    // Блокировка пользователя с удалением сессии
    public void blockUser(User user) {
        if (currentUser == null || !currentUser.getUserRole().equals(UserRole.ADMIN)) {
            System.err.println("У вас недостаточно прав для блокировки пользователя.");
            return;
        }

        try {
            userRepository.removeSession(user.getId());
            userRepository.updateUser(new UserDTO(user));
            System.out.println("Пользователь успешно заблокирован.");
        } catch (SQLException e) {
            System.err.println("Ошибка при блокировке пользователя: " + e.getMessage());
        } catch (UserNotFoundException e) {
            System.err.println("Пользователь не найден!");
        }
    }

    public Optional<User> getUser(UserData userData, String value) {
        try {
            Optional<UserDTO> userDTO = switch (userData) {
                case ID -> userRepository.findUserById(Long.parseLong(value));
                case USERNAME -> userRepository.findUserByUserName(value);
                case EMAIL -> userRepository.findUserByEmail(value);
                default -> {
                    System.err.println("Указан неправильный параметр пользователя!");
                    yield Optional.empty();
                }
            };

            return userDTO.map(UserDTO::toUser);
        } catch (UserNotFoundException e) {
            System.err.println("Пользователь не найден или введены некорректные данные");
        } catch (SQLException e) {
            System.err.println("Ошибка при работе с базой данных: " + e.getMessage());
        }

        return Optional.empty();
    }

    public List<User> getAllUsers() {
        List<User> users;

        try {
            users = userRepository.findAllUsers().stream().map(UserDTO::toUser).toList();
        } catch (SQLException e) {
            System.err.println("Ошибка! Попробуйте еще раз" + e.getMessage());
            return null;
        }

        return users;
    }

    public User getCurrentUser() {
        if (currentUser == null) {
            System.err.println("Вы не авторизованы в данной сессии!");
            return null;
        }

        return currentUser;
    }

    /**
     * Добавляет новую привычку для пользователя.
     *
     * @param user            пользователь, которому добавляется привычка
     * @param habitName       имя привычки
     * @param description     описание привычки
     * @param frequency       частота выполнения привычки
     * @param executionPeriod период выполнения привычки
     * @param startDate       дата начала выполнения
     */
    public void addHabit(User user, String habitName, String description, HabitFrequency frequency,
                         LocalDate startDate, HabitExecutionPeriod executionPeriod) {

        if (habitName == null || description == null || frequency == null || startDate == null || executionPeriod == null) {
            System.err.println("Указаны некорректные реквизиты для добавления привычки!");
            return;
        }

        try {
            Habit habit = new Habit(habitName, description, frequency, startDate, executionPeriod);

            habitRepository.addHabit(new UserDTO(user), new HabitDTO(habit));
            System.out.println("Привычка добавлена успешно!");
        } catch (HabitAlreadyExistsException e) {
            System.err.println("Привычка с таким именем уже существует.");
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении привычки в базу данных.");
        }
    }

    /**
     * Удаляет привычку по её ID.
     *
     * @param habitId ID привычки
     */
    public void deleteHabit(long habitId) {
        try {
            habitRepository.resetExecutions(habitId);
            habitRepository.deleteHabit(habitId);

            System.out.println("Привычка удалена успешно.");
        } catch (HabitNotFoundException e) {
            System.err.println("Привычка не найдена.");
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении привычки.");
        }
    }

    /**
     * Редактирует существующую привычку.
     *
     * @param habit обновленный объект привычки
     */
    public void editHabit(Habit habit) {
        try {
            habitRepository.updateHabit(new HabitDTO(habit));
            System.out.println("Привычка успешно обновлена.");
        } catch (HabitNotFoundException e) {
            System.err.println("Привычка не найдена!");
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении привычки.");
        }
    }

    /**
     * Добавляет отметку о выполнении привычки на указанную дату.
     *
     * @param habitId       ID привычки
     * @param executionDate дата выполнения
     */
    public void markExecution(long habitId, LocalDateTime executionDate) {
        try {

            if (habitRepository.findHabitByID(habitId).isEmpty()) {
                System.err.println("Привычка с ID " + habitId + " не найдена!");
                return;
            }

            HabitDTO habit = habitRepository.findHabitByID(habitId).get();

            habitRepository.markExecution(habit, executionDate);
            System.out.println("Отметка о выполнении привычки добавлена.");
        } catch (HabitNotFoundException e) {
            System.err.println("Привычка не найдена.");
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении отметки о выполнении.");
        }
    }

    /**
     * Сбрасывает статистику выполнения привычки.
     *
     * @param habitId         ID привычки
     * @param resetExecutions сброс выполненных действий
     * @param resetStreaks    сброс стриков
     */
    public void resetStatistics(long habitId, boolean resetExecutions, boolean resetStreaks) {
        try {
            habitRepository.resetStatistics(habitId, resetExecutions, resetStreaks);
            System.out.println("Статистика привычки успешно сброшена.");
        } catch (HabitNotFoundException e) {
            System.err.println("Привычка не найдена!");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Возвращает список всех привычек.
     *
     * @return список всех привычек
     */
    public List<HabitDTO> getAllHabits() {
        try {
            return habitRepository.findAllHabits();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return List.of();
        }
    }

    /**
     * Возвращает привычку по её имени.
     *
     * @param habitName имя привычки
     * @return объект привычки
     */
    public Optional<HabitDTO> findHabitByName(String habitName) {
        try {
            return habitRepository.findHabitByName(habitName);
        } catch (HabitNotFoundException e) {
            System.err.println(e.getMessage());
            return Optional.empty();
        } catch (SQLException e) {
            System.err.println("Ошибка при поиске привычки.");
            return Optional.empty();
        }
    }

    /**
     * Возвращает статистику выполнения привычки за указанный период.
     *
     * @param habitName        имя привычки
     * @param startPeriodDate начало периода
     * @param endPeriodDate   конец периода
     * @return объект {@link HashMap} с данными за указанный период дата выполнения, процент успешного выполнения на эту дату
     */
    public Map<LocalDate, Integer> getHabitStatistic(String habitName, LocalDateTime startPeriodDate, LocalDateTime endPeriodDate) {
        try {
            Optional<HabitDTO> habit = habitRepository.findHabitByName(habitName);

            if (habit.isEmpty())
                throw new HabitNotFoundException("Привычка не найдена!");

            return habitRepository.getStatisticByPeriod(habit.get(), startPeriodDate, endPeriodDate);
        } catch (HabitNotFoundException e) {
            System.err.println(e.getMessage());
            return null;
        } catch (SQLException e) {
            System.err.println("Ошибка при получении статистики.");
            return null;
        }
    }

    /**
     * Рассчитывает процент выполнения привычки на текущую дату
     *
     * @param habitId ID привычки
     * @return процент выполнения
     */
    public int calculateExecutionPercentage(long habitId) {
        try {
            Optional<HabitDTO> habit = habitRepository.findHabitByID(habitId);

            HabitDTO habitDTO = null;

            if (habit.isPresent()) {
                habitDTO = habit.get();
            } else {
                System.err.println("Не удалось получить процент выполнения привычки!");
                return 0;
            }

            return habitRepository.calculateExecutionPercentage(habitDTO,
                    habitDTO.getStartDate().atStartOfDay(), LocalDateTime.of(habitDTO.getEndDate(), LocalTime.MAX));
        } catch (SQLException | HabitNotFoundException e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }


    /**
     * Удаляет все сессии пользователей
     *
     */
    public void removeAllSessions() {
        try {
            userRepository.removeAllSessions();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
