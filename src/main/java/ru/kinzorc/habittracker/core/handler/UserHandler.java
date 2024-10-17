package ru.kinzorc.habittracker.core.handler;

import ru.kinzorc.habittracker.common.config.AppConfig;
import ru.kinzorc.habittracker.common.config.HandlerConstants;
import ru.kinzorc.habittracker.common.data.DataOfUser;
import ru.kinzorc.habittracker.common.email.EmailSender;
import ru.kinzorc.habittracker.common.util.InputUtils;
import ru.kinzorc.habittracker.common.util.OutputUtils;
import ru.kinzorc.habittracker.core.model.User;
import ru.kinzorc.habittracker.core.service.UserService;


public class UserHandler {

    public UserHandler() {
    }

    // Метод для регистарции пользователя
    public static void handleRegisterUser() {
        String name = InputUtils.promptValidInputUserData(DataOfUser.NAME, "Имя пользователя: ",
                "Имя пользователя должно содержать от 3 до 20 символов и начинаться с буквы"); // Ввод имени
        String email = InputUtils.promptValidInputUserData(DataOfUser.EMAIL, "Введите email: ", "Некорректный email"); // Ввод email
        String password = InputUtils.promptValidInputUserData(DataOfUser.PASSWORD, "Введите пароль: ", """
                        Пароль должен содержать:
                        - минимум 8 символов
                        - хотя бы одну цифру
                        - хотя бы одну строчную букву
                        - хотя бы одну заглавную букву
                        - хотя бы один специальный символ"""); // ввод пароля

        boolean isRegistered = HandlerConstants.USER_SERVICE.registerUser(name, email, password);

        System.out.println(isRegistered ? "Вы успешно зарегистрировались!" : "Пользователь с таким email уже существует");
    }

    // Метод для авторизации пользователя
    public static boolean handleLoginUser() {
        System.out.println("Вход в личный кабинет:");

        if (HandlerConstants.USERS == null) {
            System.out.println("В базе отсуствуют пользователи, необходимо зарегистрироваться");
            return false;
        }

        String email = InputUtils.promptInput("  Email: ");
        String password = InputUtils.promptInput("  Пароль: ");

        HandlerConstants.CURRENT_USER = HandlerConstants.USER_SERVICE.loginUser(email, password);

        // Добавляем проверку на null
        if (HandlerConstants.CURRENT_USER == null) {
            System.out.println("Неудачная попытка входа!");
            return false;
        }

        if (!HandlerConstants.CURRENT_USER.isLogin()) {
            System.out.println("Неудачная попытка входа!");
            return false;
        }

        if (HandlerConstants.CURRENT_USER.isBlocked()) {
            System.out.println("Пользователь заблокирован! Вход невозможен, обратитесь к администратору!");
            return false;
        }

        System.out.println("Успешный вход! Привет " + HandlerConstants.CURRENT_USER.getName() + "!");
        return true;
    }

    // Метод для удаления пользователя с проверкой пароля
    public static boolean handleDeleteUser(User currentUser) {
        String password = InputUtils.promptInput("Введите пароль для удаления аккаунта: ");

        if (!password.equals(currentUser.getPassword())) {
            System.out.println("Пароль неверный. Удаление не удалось. Возврат в меню профиля.");
            return false;
        }

        boolean isDeleted = HandlerConstants.USER_SERVICE.deleteUser(currentUser);

        System.out.println(isDeleted ? "Аккаунт был успешно удален. Возврат в главное меню." : "Аккаунт не был удален! Возврат в меню профиля.");

        return isDeleted;
    }

    // Метод для сброса пароля
    public static boolean handleResetPassword() {
        if (AppConfig.getMailProperties() == null) {
            System.out.println("Не настроена учетная запись для отправки почтовых уведомлений!");
            return false;
        }

        User user = HandlerConstants.USERS.get(InputUtils.promptInput("Введите email для сброса пароля: "));

        if (user == null) {
            System.out.println("Пользователь с таким email не зарегистрирован!");
            return false;
        }

        if (EmailSender.paramIsValid()) {
            String resetCode = OutputUtils.generateResetCode();
            try {
                EmailSender.sendEmail(user.getEmail(), "Код для сброса пароля", "Ваш код: " + resetCode);
                System.out.println("Код для сброса отправлен на email!");
            } catch (Exception e) {
                System.out.println("Ошибка отправки письма: " + e.getMessage());
                return false;
            }

            String userCode = InputUtils.promptInput("Введите код: ");
            if (!InputUtils.isValidResetCode(userCode, resetCode)) {
                System.out.println("Неверный код!");
                return false;
            }

            String newPassword = InputUtils.promptValidInputUserData(DataOfUser.PASSWORD, "Введите новый пароль: ",
                    """
                    Пароль должен содержать:
                    - минимум 8 символов
                    - хотя бы одну цифру
                    - хотя бы одну строчную букву
                    - хотя бы одну заглавную букву
                    - хотя бы один специальный символ
                    """);
            user.setPassword(newPassword);
            System.out.println("Пароль успешно изменен!");
            return true;
        }

        return false;
    }

    // Метод для блокировки пользователя
    public static void handleBlockUser(User user) {
        if (user.isAdmin()) {
            System.out.println("Невозможно заблокировать администратора!");
        } else {
            HandlerConstants.USER_SERVICE.blockUser(user);
            System.out.println("Пользователь " + user.getName() + " заблокирован!");
        }
    }

    // Метод для смены имени пользователя
    public static void setNameUser(User user) {
        HandlerConstants.USER_SERVICE.setUserData(user, DataOfUser.NAME,
                InputUtils.promptValidInputUserData(DataOfUser.NAME, "Введите имя: ",
                        "Имя должно содержать от 3 до 20 символов и начинаться с буквы")); // Ввод имени
    }

    // Метод для смены email пользователя
    public static void setEmailUser(User user) {
        HandlerConstants.USERS.remove(user.getEmail());
        String email = InputUtils.promptValidInputUserData(DataOfUser.EMAIL, "Введите email: ", "Некорректный email");
        user.setEmail(email);
        user.setLogin(true);
        HandlerConstants.USERS.put(user.getEmail(), user);
    }

    // Метод для смены пароля пользователя
    public static void setPasswordUser(User user) {
        HandlerConstants.USER_SERVICE.setUserData(user, DataOfUser.PASSWORD, InputUtils.promptValidInputUserData(DataOfUser.PASSWORD, "Введите пароль: ",
                """
                        Пароль должен содержать:
                        - минимум 8 символов
                        - хотя бы одну цифру
                        - хотя бы одну строчную букву
                        - хотя бы одну заглавную букву
                        - хотя бы один специальный символ""")); // Ввод пароля
    }

    // Метод для поиска пользователяпо имени
    public static User getFindUser() {
        String name = InputUtils.promptInput("Имя пользователя: ");
        return HandlerConstants.USER_SERVICE.getUserFromName(name);
    }
}