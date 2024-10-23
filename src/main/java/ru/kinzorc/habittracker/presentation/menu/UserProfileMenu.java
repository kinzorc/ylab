package ru.kinzorc.habittracker.presentation.menu;

import ru.kinzorc.habittracker.application.service.ApplicationService;
import ru.kinzorc.habittracker.core.enums.User.UserData;
import ru.kinzorc.habittracker.presentation.utils.MenuUtils;

import java.util.Scanner;

public class UserProfileMenu implements Menu {

    @Override
    public void showMenu(ApplicationService applicationService, MenuUtils menuUtils) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nПрофиль пользователя:\n "
                    + "\n Имя: " + applicationService.getCurrentUser().getUserName()
                    + "\n Email: " + applicationService.getCurrentUser().getEmail()
                    + "\n Роль в системе: " + applicationService.getCurrentUser().getUserRole() + "\n");

            System.out.println("Меню профиля: 1) Изменить имя 2) Изменить email 3) Изменить пароль 4) Выход");

            int option = menuUtils.promptMenuValidInput(scanner);

            switch (option) {
                case 1 -> applicationService.editUser(UserData.USERNAME,
                        menuUtils.promptValidInputUserData(scanner, UserData.USERNAME,
                                "Введите новое имя: ",
                                "Имя должно содержать от 3 до 20 символов и начинаться с буквы"));
                case 2 -> applicationService.editUser(UserData.EMAIL,
                        menuUtils.promptValidInputUserData(scanner, UserData.EMAIL,
                                "Введите новый email: ",
                                "Некорректный email"));
                case 3 -> applicationService.editUser(UserData.PASSWORD,
                        menuUtils.promptValidInputUserData(scanner, UserData.PASSWORD, "Введите пароль: ", """
                                Пароль должен содержать:
                                - минимум 8 символов
                                - хотя бы одну цифру
                                - хотя бы одну строчную букву
                                - хотя бы одну заглавную букву
                                - хотя бы один специальный символ"""));
                case 4 -> {
                    System.out.println("Возврат в личный кабинет");
                    return;
                }
                default -> System.out.println("Пожалуйста, выберите один из предложенных вариантов.");
            }
        }
    }
}
