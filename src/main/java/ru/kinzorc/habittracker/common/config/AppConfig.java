package ru.kinzorc.habittracker.common.config;


import ru.kinzorc.habittracker.common.data.DataOfUser;
import ru.kinzorc.habittracker.common.util.InputUtils;

import java.io.FileOutputStream;
import java.util.Properties;


public class AppConfig {
    private static final Properties MAIL_PROPERTIES = new Properties();
    private static final String CONFIG_FILE = "HabitTracker/src/main/resources/config.properties";

    public static void setParamApp() {
        System.out.println("""
                Настройки приложения для отправки почтовых уведомлений для сброса пароля.
                Отправка уведомлений реализована через SSL протокол и тестировалась на сервисе gmail.com
                Выберите параметр:
                1) Ввести данные 2) Выход в главное меню
                """);

        int option = InputUtils.promptMenuValidInput();

        if (option == 1) {
            String smtpServer = InputUtils.promptInput("Введите адрес smtp сервера: ");
            String smtpPort = InputUtils.promptInput("Введите ssl порт: ");
            String email = InputUtils.promptValidInputUserData(DataOfUser.EMAIL, "Введите email: ", "Некорректный email");
            String password = InputUtils.promptInput("Введите пароль: ");

            setProperties(smtpServer, smtpPort, smtpPort, email, password);
        } else if (option == 2)
            System.out.println("Выход в главное меню");
    }

    // Метод для установки параметров
    private static void setProperties(String smtpServer, String smtpPort, String sFactoryPort,
                                      String username, String password) {
        MAIL_PROPERTIES.put("mail.smtp.host", smtpServer);
        MAIL_PROPERTIES.put("mail.smtp.port", smtpPort);
        MAIL_PROPERTIES.put("mail.smtp.auth", "true");
        MAIL_PROPERTIES.put("mail.smtp.socketFactory.port", sFactoryPort);
        MAIL_PROPERTIES.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        MAIL_PROPERTIES.put("mail.smtp.socketFactory.fallback", "false");
        MAIL_PROPERTIES.put("email.username", username);
        MAIL_PROPERTIES.put("email.password", password);
        MAIL_PROPERTIES.put("param.valid", "true");
        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
            MAIL_PROPERTIES.store(output, null);
        } catch (Exception e) {
            System.out.println("Настройки установлены, но при сохранении в файл возника ошибка - " + e.getMessage());
        }
    }

    public static Properties getMailProperties() {
        return MAIL_PROPERTIES;
    }

}