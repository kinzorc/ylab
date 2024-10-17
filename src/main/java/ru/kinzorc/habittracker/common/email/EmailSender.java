package ru.kinzorc.habittracker.common.email;

import ru.kinzorc.habittracker.common.config.AppConfig;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {
    private static final Properties MAIL_PROPERTIES = AppConfig.getMailProperties();

    // Метод для отправки почты
    public static void sendEmail(String recipient, String subject, String content) throws Exception {
        final String username = MAIL_PROPERTIES.getProperty("email.username");
        final String password = MAIL_PROPERTIES.getProperty("email.password");

        // Проверяем наличие критических параметров
        if (username == null || password == null) {
            throw new IllegalArgumentException("Отсутствуют параметры для email аутентификации.");
        }

        Session session = Session.getInstance(MAIL_PROPERTIES, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
        } catch (MessagingException e) {
            // Логируем ошибку и выбрасываем исключение дальше
            System.err.println("Ошибка отправки email: " + e.getMessage());
            throw e;
        }
    }

    public static boolean paramIsValid() {
        return MAIL_PROPERTIES.getProperty("param.valid").equalsIgnoreCase("true");
    }
}