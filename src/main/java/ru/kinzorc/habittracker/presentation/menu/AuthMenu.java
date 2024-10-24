package ru.kinzorc.habittracker.presentation.menu;

import ru.kinzorc.habittracker.application.service.ApplicationService;
import ru.kinzorc.habittracker.core.entities.User;
import ru.kinzorc.habittracker.core.enums.User.UserData;
import ru.kinzorc.habittracker.infrastructure.repository.email.MailSender;
import ru.kinzorc.habittracker.presentation.utils.MenuUtils;

import javax.mail.MessagingException;
import java.util.Scanner;

public class AuthMenu implements Menu {

    @Override
    public void showMenu(ApplicationService applicationService, MenuUtils menuUtils) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nМеню авторизации:\n1) Вход 2) Сбросить пароль 3) Выход в главное меню");
        int option = menuUtils.promptMenuValidInput(scanner);

        switch (option) {
            case 1 -> {
                String email = menuUtils.promptInput(scanner, "  Email: ");
                String password = menuUtils.promptInput(scanner, "  Пароль: ");

                if (applicationService.loginUser(email, password)) {
                    MenuNavigator.ACCOUNT_MENU.showMenu(applicationService, menuUtils);
                }
            }
            case 2 -> {
                String email = menuUtils.promptInput(scanner, "Для сброса пароля введите email пользователя: ");

                if (applicationService.getUser(UserData.EMAIL, email).isPresent()) {
                    MailSender mailSender = new MailSender();
                    String resetCode = menuUtils.generateResetCode();

                    try {
                        mailSender.sendEmail(email, "Код для сброса пароля", "Ваш код для сброса пароля: " + resetCode);
                    } catch (MessagingException e) {
                        throw new RuntimeException("Неудачная попытка отправить сообщение на почту");
                    }

                    String input = menuUtils.promptInput(scanner, "Введите код для сброса пароля: ");

                    if (menuUtils.isValidResetCode(input, resetCode)) {
                        User user = applicationService.getAllUsers().stream().filter(userDTO -> userDTO.getEmail().equals(email)).findFirst().orElseThrow();

                        String newPassword = menuUtils.promptValidInputUserData(scanner, UserData.PASSWORD,
                                "Введите новый пароль: ",
                                """
                                        Пароль должен содержать:
                                        - минимум 8 символов
                                        - хотя бы одну цифру
                                        - хотя бы одну строчную букву
                                        - хотя бы одну заглавную букву
                                        - хотя бы один специальный символ""");

                        user.setPassword(newPassword);
                        applicationService.editUser(user);
                    } else {
                        System.err.println("Неверный проверочный код, выход в главное меню.");
                        MenuNavigator.MAIN_MENU.showMenu(applicationService, menuUtils);
                    }
                }
            }
            case 3 -> System.out.println("Возврат в главное меню.");
            default -> System.out.println("Пожалуйста, выберите один из предложенных вариантов.");

        }
    }
}
