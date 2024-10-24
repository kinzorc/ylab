package ru.kinzorc.habittracker.presentation.utils;

import ru.kinzorc.habittracker.core.enums.User.UserData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Random;
import java.util.Scanner;

/**
 * Утилитный класс для работы с пользовательским вводом в меню.
 * <p>
 * Класс содержит методы для проверки и обработки ввода данных пользователей и привычек,
 * а также для валидации данных (например, email, пароля) и генерации уникальных кодов.
 * </p>
 */
public class MenuUtils {

    /**
     * Метод для ввода номера пункта меню.
     * <p>
     * Запрашивает у пользователя ввод числа, проверяя, что вводится целое число.
     * Если введено некорректное значение, пользователю выдается ошибка и предлагается повторить ввод.
     * </p>
     *
     * @param scanner объект {@link Scanner} для ввода данных
     * @return выбранный пункт меню в виде целого числа
     */
    public int promptMenuValidInput(Scanner scanner) {
        int option;

        while (true) {
            System.out.print("Выберите пункт меню: ");
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

    /**
     * Запрашивает ввод данных у пользователя.
     *
     * @param scanner объект {@link Scanner} для ввода данных
     * @param message сообщение для пользователя, с пояснением, какие данные нужно ввести
     * @return введенная строка
     */
    public String promptInput(Scanner scanner, String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    /**
     * Проверяет корректность введенного имени пользователя.
     * <p>
     * Имя должно содержать от 3 до 20 символов, начинаться с буквы или цифры и может включать дефис.
     * </p>
     *
     * @param name имя пользователя для проверки
     * @return true, если имя корректное, иначе false
     */
    public boolean isValidUsername(String name) {
        String usernameRegex = "^[a-zA-Zа-яА-Я0-9][a-zA-Zа-яА-Я0-9-]{2,19}$";
        return name != null && name.matches(usernameRegex);
    }

    /**
     * Проверяет корректность введенного email.
     * <p>
     * Email должен быть в формате example@example.com.
     * </p>
     *
     * @param email email для проверки
     * @return true, если email корректен, иначе false
     */
    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    /**
     * Проверяет корректность введенного пароля.
     * <p>
     * Пароль должен содержать минимум 8 символов, включать хотя бы одну цифру, одну строчную и одну заглавную буквы,
     * а также хотя бы один специальный символ.
     * </p>
     *
     * @param password пароль для проверки
     * @return true, если пароль корректен, иначе false
     */
    public boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$";
        return password != null && password.matches(passwordRegex);
    }

    /**
     * Запрашивает у пользователя ввод данных, таких как имя, email или пароль, и проверяет корректность введенных данных.
     *
     * @param scanner      объект {@link Scanner} для ввода данных
     * @param param        тип данных {@link UserData} (имя, email или пароль)
     * @param message      сообщение для ввода данных
     * @param errorMessage сообщение об ошибке в случае некорректного ввода
     * @return корректный ввод данных
     */
    public String promptValidInputUserData(Scanner scanner, UserData param, String message, String errorMessage) {
        while (true) {
            String data = promptInput(scanner, message);
            switch (param) {
                case USERNAME -> {
                    if (isValidUsername(data)) return data;
                }
                case EMAIL -> {
                    if (isValidEmail(data)) return data;
                }
                case PASSWORD -> {
                    if (isValidPassword(data)) return data;
                }
            }
            System.out.println(errorMessage);
        }
    }

    /**
     * Запрашивает у пользователя ввод частоты выполнения привычки (daily, weekly) с проверкой корректности.
     *
     * @param scanner      объект {@link Scanner} для ввода данных
     * @param message      сообщение для пользователя
     * @param errorMessage сообщение об ошибке в случае некорректного ввода
     * @return корректное значение частоты выполнения привычки
     */
    public String promptHabitFrequencyValid(Scanner scanner, String message, String errorMessage) {
        while (true) {
            System.out.print(message);
            String data = scanner.nextLine();
            switch (data) {
                case "daily" -> {
                    return "DAILY";
                }
                case "weekly" -> {
                    return "WEEKLY";
                }
                case "0" -> {
                    return "0";
                }
                default -> System.out.println(errorMessage);
            }
        }
    }

    /**
     * Запрашивает у пользователя ввод статуса привычки (active, finished) с проверкой корректности.
     *
     * @param scanner объект {@link Scanner} для ввода данных
     * @param message сообщение для пользователя
     * @param errorMessage сообщение об ошибке в случае некорректного ввода
     * @return корректное значение статуса привычки
     */
    public String promptHabitStatusValid(Scanner scanner, String message, String errorMessage) {
        while (true) {
            System.out.print(message);
            String data = scanner.nextLine();
            switch (data) {
                case "active" -> {
                    return "ACTIVE";
                }
                case "finished" -> {
                    return "FINISHED";
                }
                case "0" -> {
                    return "0";
                }
                default -> System.out.println(errorMessage);
            }
        }
    }

    /**
     * Запрашивает у пользователя ввод периода выполнения привычки (month, year) с проверкой корректности.
     *
     * @param scanner объект {@link Scanner} для ввода данных
     * @param message сообщение для пользователя
     * @param errorMessage сообщение об ошибке в случае некорректного ввода
     * @return корректное значение периода выполнения привычки
     */
    public String promptHabitExecutionPeriodValid(Scanner scanner, String message, String errorMessage) {
        while (true) {
            System.out.print(message);
            String data = scanner.nextLine();
            switch (data) {
                case "month" -> {
                    return "MONTH";
                }
                case "year" -> {
                    return "YEAR";
                }
                case "0" -> {
                    return "0";
                }
                default -> System.out.println(errorMessage);
            }
        }
    }

    /**
     * Запрашивает у пользователя корректную дату в формате "dd.MM.yyyy" с проверкой.
     *
     * @param scanner      объект {@link Scanner} для ввода данных
     * @param message      сообщение для пользователя
     * @param errorMessage сообщение об ошибке в случае некорректного ввода
     * @return корректная дата выполнения привычки в формате {@link LocalDate}
     */
    public LocalDate promptDateValid(Scanner scanner, String message, String errorMessage) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date = null;

        while (true) {
            System.out.print(message);
            String data = scanner.nextLine();

            if (data.equalsIgnoreCase("0"))
                return null;

            try {
                date = LocalDate.parse(data, dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println(errorMessage);
            }

            if (date == null)
                System.err.println("Ошибка в преобразовании даты, попробуйте еще раз.");
            else
                break;
        }

        return date;
    }

    /**
     * Генерирует шестизначный код для сброса пароля.
     *
     * @return шестизначный код в виде строки
     */
    public String generateResetCode() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    /**
     * Проверяет корректность введенного пользователем кода для сброса пароля.
     *
     * @param codeUser     код, введенный пользователем
     * @param codeFromMail код, отправленный на почту
     * @return true, если коды совпадают, иначе false
     */
    public boolean isValidResetCode(String codeUser, String codeFromMail) {
        return codeUser != null && codeUser.equals(codeFromMail);
    }
}