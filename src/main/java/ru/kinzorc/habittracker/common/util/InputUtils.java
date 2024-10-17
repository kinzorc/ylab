package ru.kinzorc.habittracker.common.util;

import ru.kinzorc.habittracker.common.data.DataOfUser;
import ru.kinzorc.habittracker.common.data.FrequencyHabit;
import ru.kinzorc.habittracker.common.data.HabitStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class InputUtils {
    private static final Scanner scanner = new Scanner(System.in);

    private InputUtils() {
    }

    // Проверка имени
    static boolean isValidUsername(String name) {
        // Имя должно содержать от 3 до 20 символов и начинаться с буквы
        String usernameRegex = "^[a-zA-Zа-яА-Я0-9][a-zA-Zа-яА-Я0-9-]{2,19}$";
        return name != null && name.matches(usernameRegex);
    }

    // Проверка адреса почты
    static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    // Проверка пароля
    // Пароль должен содержать:
    // - минимум 8 символов
    // - хотя бы одну цифру
    // - хотя бы одну строчную букву
    // - хотя бы одну заглавную букву
    // - хотя бы один специальный символ
    static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$";
        return password != null && password.matches(passwordRegex);
    }

    // Проверка на корректность кода из почты
    public static boolean isValidResetCode(String codeUser, String codeFromMail) {
        return codeUser != null && codeUser.equals(codeFromMail);
    }

    public static String promptInput(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    // Метод на проверку ввода данных пользователем (name, email, password)
    public static String promptValidInputUserData(DataOfUser param, String message, String errorMessage) {
        while (true) {
            String data = promptInput(message);
            switch (param) {
                case NAME -> {
                    if (isValidUsername(data))
                        return data;
                }
                case EMAIL -> {
                    if (isValidEmail(data))
                        return data;
                }
                case PASSWORD -> {
                    if (isValidPassword(data))
                        return data;
                }
            }
            System.out.println(errorMessage);
        }
    }

    // Метод для проверки пользователем пунктов меню, проверяеся, что вводится число, а не любое другое значение
    public static int promptMenuValidInput() {
        int option;

        while (true) {
            System.out.print("Введите пункт меню: ");
            if (scanner.hasNextInt()) {
                option = scanner.nextInt();
                scanner.nextLine();
                return option;
            } else {
                System.out.println("Ошибка! Введите целое число.");
                scanner.nextLine();
            }
        }
    }

    public static String promptFrequencyValid(String message, String errorMessage) {
        while (true) {
            System.out.print(message);
            String data = scanner.nextLine();
            data = data.toLowerCase();
            switch (data) {
                case "day" -> {
                    if ("day".equalsIgnoreCase(FrequencyHabit.DAY.toString()))
                        return "DAY";
                }
                case "week" -> {
                    if ("week".equalsIgnoreCase(FrequencyHabit.WEEK.toString()))
                        return "WEEK";
                }
                case "0" -> {
                    return "0";
                }
                default -> {
                    System.out.println(errorMessage);
                    return null;
                }
            }
        }
    }

    public static String promptStatusValid(String message, String errorMessage) {
        while (true) {
            System.out.print(message);
            String data = scanner.nextLine();
            data = data.toUpperCase();
            switch (data) {
                case "ACTIVE" -> {
                    if ("ACTIVE".equalsIgnoreCase(HabitStatus.ACTIVE.toString()))
                        return data;
                }
                case "FINISHED" -> {
                    if ("week".equalsIgnoreCase(HabitStatus.FINISHED.toString()))
                        return data;
                }
                case "0" -> {
                    return "0";
                }
                default -> {
                    System.out.println(errorMessage);
                    return null;
                }
            }
        }
    }

    public static LocalDate promptDateValid(String message, String errorMessage) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date;

        while (true) {
            System.out.print(message);
            String data = scanner.nextLine();

            if (data.equalsIgnoreCase("0"))
                return null;

            // Парсим строку в дату
            try {
                date = LocalDate.parse(data, dateFormatter);
                break;
            } catch (Exception e) {
                System.out.println(errorMessage);
            }
        }

        return date;
    }
}
