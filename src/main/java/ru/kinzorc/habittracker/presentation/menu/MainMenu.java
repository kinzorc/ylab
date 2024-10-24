package ru.kinzorc.habittracker.presentation.menu;

import ru.kinzorc.habittracker.application.service.ApplicationService;
import ru.kinzorc.habittracker.core.enums.User.UserData;
import ru.kinzorc.habittracker.presentation.utils.MenuUtils;

import java.util.Scanner;


public class MainMenu implements Menu {

    // Главное меню
    @Override
    public void showMenu(ApplicationService applicationService, MenuUtils menuUtils) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nГлавное меню:\n1) Регистрация пользователя 2) Авторизация 3) Выход из приложения");
            int option = menuUtils.promptMenuValidInput(scanner);

            switch (option) {
                case 1 -> {
                    String name = menuUtils.promptValidInputUserData(scanner, UserData.USERNAME, "Имя пользователя: ",
                            "Имя пользователя должно содержать от 3 до 20 символов и начинаться с буквы"); // Ввод имени
                    String email = menuUtils.promptValidInputUserData(scanner, UserData.EMAIL, "Введите email: ", "Некорректный email"); // Ввод email
                    String password = menuUtils.promptValidInputUserData(scanner, UserData.PASSWORD, "Введите пароль: ", """
                            Пароль должен содержать:
                            - минимум 8 символов
                            - хотя бы одну цифру
                            - хотя бы одну строчную букву
                            - хотя бы одну заглавную букву
                            - хотя бы один специальный символ"""); // ввод пароля

                    applicationService.createUser(name, email, password);
                }
                case 2 -> MenuNavigator.AUTH_MENU.showMenu(applicationService, menuUtils);
                case 3 -> {
                    System.out.println("Выход из приложения");
                    System.exit(0);
                }
                default -> System.out.println("Выбран неправильный пункт меню.");
            }
        }
    }


}