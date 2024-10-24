package ru.kinzorc.habittracker.presentation.utils;

import ru.kinzorc.habittracker.application.dto.HabitDTO;
import ru.kinzorc.habittracker.application.dto.UserDTO;
import ru.kinzorc.habittracker.application.service.ApplicationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Утилитный класс для вывода информации на консоль.
 * <p>
 * Предоставляет методы для форматированного вывода данных о пользователях и привычках.
 * </p>
 */
public class PrintUtils {

    /**
     * Выводит список всех пользователей в форматированной таблице.
     * <p>
     * Таблица включает номер, идентификатор пользователя, имя, email, роль и статус учетной записи.
     * </p>
     *
     * @param users список объектов {@link UserDTO}, представляющих пользователей
     */
    public static void pintAllUsers(List<UserDTO> users) {
        AtomicInteger increment = new AtomicInteger(1);
        System.out.println("\nСписок пользователей:\n");
        System.out.printf("%-3S %-6S %-20S %-30S %-12S %-12S%n", "№", "ID", "Имя", "Email", "Роль", "Статус аккаунта");
        System.out.println("------------------------------------------------------------------------");

        users.forEach(user -> System.out.printf("%-3s %-6S %-20s %-30s %-12S %-12S%n",
                increment.getAndIncrement(), user.getId(), user.getUserName(), user.getEmail(), user.getUserRole(), user.getUserStatusAccount()));
    }

    /**
     * Выводит список всех привычек в форматированной таблице.
     * <p>
     * Таблица включает номер, идентификатор привычки, название, описание, статус, дату создания, дату начала и частоту выполнения.
     * </p>
     *
     * @param habits список объектов {@link HabitDTO}, представляющих привычки
     */
    public static void printListHabits(List<HabitDTO> habits) {
        AtomicInteger number = new AtomicInteger(1);
        System.out.println("\nСписок привычек:\n");
        System.out.printf("%-3s %-3s %-3s %-20s %-30s %-10s %-12s %-12s %-12s %-5s %-5s %-3s %-3s%n",
                "№", "ID", "USER_ID", "Название", "Описание", "Периодичность", "Дата создания", "Дата начала", "Дата окончания", "Период выполнения", "Статус", "Стрик", "Процент выполнения");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        habits.forEach(habit -> System.out.printf("%-3s %-3s %-3s %-20s %-30s %-10s %-12s %-12s %-12s %-5s %-5s %-3s %-3s%n",
                number.getAndIncrement(), habit.getId(), habit.getUserId(), habit.getName(),
                habit.getDescription(), habit.getFrequency().name(), habit.getCreatedDate(),
                habit.getStartDate(), habit.getEndDate(), habit.getExecutionPeriod(), habit.getStatus(), habit.getStreak(), habit.getExecutionPercentage()));

    }

    /**
     * Выводит детальную информацию о привычке и её статистике выполнения.
     * <p>
     * Включает название привычки, описание, дату создания, период выполнения, дату начала и окончания выполнения.
     * Также выводит список дат, когда привычка была выполнена.
     * </p>
     *
     * @param habit объект {@link HabitDTO}, представляющий привычку
     */
    public static void printInfoForHabit(HabitDTO habit, ApplicationService applicationService) {
        System.out.println("Название привычки: " + habit.getName());
        System.out.println("Дата создания: " + habit.getCreatedDate());
        System.out.println("Описание привычки: " + habit.getDescription());
        System.out.println("Период выполнения: " + habit.getExecutionPeriod());
        System.out.println("Дата начала выполнения: " + habit.getStartDate());
        System.out.println("Дата окончания выполнения: " + habit.getEndDate());

        System.out.println("\nТекущий стрик: " + habit.getStreak());
        System.out.println("Текущий процент выполнения: " + habit.getExecutionPercentage());

        System.out.println("\nДаты выполнения:\n");
        AtomicInteger number = new AtomicInteger(1);
        System.out.printf("%-20s %-3s%n", "Дата выполнения", "Процент выполнения");
        System.out.println("---------------------------------------------------------------");

        Map<LocalDate, Integer> executions = applicationService.getHabitStatistic(habit.getName(),
                habit.getStartDate().atStartOfDay(), LocalDateTime.of(habit.getEndDate(), LocalTime.MAX));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        executions.forEach((key, value) -> System.out.printf("%-20s %-3s%n", key.format(dateTimeFormatter), value));
    }
}