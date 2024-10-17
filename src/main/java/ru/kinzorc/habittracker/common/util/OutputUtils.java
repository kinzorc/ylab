package ru.kinzorc.habittracker.common.util;

import ru.kinzorc.habittracker.core.model.Habit;
import ru.kinzorc.habittracker.core.model.User;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class OutputUtils {

    public static String generateResetCode() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    public static void printListUsers(Map<String, User> map) {
        AtomicInteger number = new AtomicInteger(1);
        System.out.println("\nСписок пользователей:\n");
        System.out.printf("%-3S %-20S %-30S %-5S %-5S%n", "№", "Имя", "Email", "Online", "Blocked");
        System.out.println("------------------------------------------------------------------------");
        map.forEach((email, user) -> System.out.printf("%-3s %-20s %-30s %-5S %-5S%n",
                number.getAndIncrement(), user.getName(), user.getEmail(), user.isLogin(), user.isBlocked()));
    }

    public static void printListHabits(Map<String, Habit> habits) {
        AtomicInteger number = new AtomicInteger(1);
        System.out.println("\nСписок привычек:\n");
        System.out.printf("%-3s %-20s %-50s %-10S %-12s %-12s %-5S%n", "№", "Название", "Описание", "Статус", "Дата создания", "Дата начала", "Переодичность");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        habits.values().forEach(habit -> System.out.printf("%-3s %-20s %-50s %-10S %-12s %-12s %-5S%n",
                number.getAndIncrement(), habit.getName(), habit.getDescription(), habit.getStatus(), habit.getCreatedDate(), habit.getStartDate(), habit.getFrequency().name()));
    }
}
