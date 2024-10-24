package ru.kinzorc.habittracker.application.service;

import javax.mail.MessagingException;

/**
 * Интерфейс для отправки электронных писем.
 * <p>
 * Определяет метод для отправки писем с использованием сервиса электронной почты.
 * Реализация данного интерфейса должна обеспечить функционал отправки писем
 * на указанный адрес с заданной темой и текстом.
 * </p>
 */
public interface EmailService {

    /**
     * Отправка электронного письма.
     * <p>
     * Отправляет электронное письмо на указанный email-адрес с заданными темой и содержимым.
     * Если происходит ошибка во время отправки сообщения, выбрасывается исключение {@link MessagingException}.
     * </p>
     *
     * @param recipient адрес электронной почты получателя
     * @param subject   тема письма
     * @param text      текст письма
     * @throws MessagingException если возникает ошибка при отправке сообщения
     */
    void sendEmail(String recipient, String subject, String text) throws MessagingException;
}
