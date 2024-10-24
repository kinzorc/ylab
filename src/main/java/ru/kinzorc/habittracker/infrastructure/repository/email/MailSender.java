package ru.kinzorc.habittracker.infrastructure.repository.email;

import ru.kinzorc.habittracker.application.service.EmailService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс предоставляет функционал для отправки электронных писем с использованием протоколов SMTP.
 * <p>
 * Реализует интерфейс {@link EmailService} и использует конфигурации, загружаемые из файла свойств.
 * </p>
 */
public class MailSender implements EmailService {

    /**
     * Свойства для конфигурации почтового сервиса, загружаемые из файла {@code application.properties}.
     */
    private static final Properties MAIL_PROPERTIES = new Properties();

    /**
     * Конструктор по умолчанию, который загружает свойства для работы с почтовым сервером.
     */
    public MailSender() {
        loadProperties();
    }

    /**
     * Метод отправки электронного письма.
     * <p>
     * Использует почтовые настройки для подключения к SMTP-серверу и отправки письма.
     * Если в конфигурации отсутствуют данные для аутентификации (логин или пароль), выбрасывается {@link IllegalArgumentException}.
     * </p>
     *
     * @param recipient адрес электронной почты получателя
     * @param subject   тема письма
     * @param text      текст письма
     * @throws MessagingException если возникает ошибка при отправке сообщения
     */
    @Override
    public void sendEmail(String recipient, String subject, String text) throws MessagingException {
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
            message.setText(text);

            Transport.send(message);
        } catch (MessagingException e) {
            System.err.println("Ошибка отправки email: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Метод для загрузки конфигурационного файла с настройками почтового сервера.
     * <p>
     * Загружает свойства из файла {@code application.properties}, расположенного в ресурсах приложения.
     * Если файл не найден или возникает ошибка при его чтении, выводится сообщение об ошибке.
     * </p>
     */
    private void loadProperties() {
        try (InputStream inputStream = MailSender.class.getResourceAsStream("/application.properties")) {
            MAIL_PROPERTIES.load(inputStream);
        } catch (IOException e) {
            System.err.println("Ошибка чтения конфигурационного файла: " + e.getMessage());
        }
    }

}
