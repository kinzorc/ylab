package ru.kinzorc.habittracker.navigation;

import ru.kinzorc.habittracker.common.config.AppConfig;
import ru.kinzorc.habittracker.common.util.InputUtils;
import ru.kinzorc.habittracker.core.handler.UserHandler;


public class MainMenu implements Menu {

    // Главное меню
    @Override
    public void showMenu() {

        while (true) {
            System.out.println("\nГлавное меню:\n1) Регистрация пользователя 2) Авторизация 3) Настройки приложения 4) Выход из приложения");
            int option = InputUtils.promptMenuValidInput();

            switch (option) {
                case 1 -> UserHandler.handleRegisterUser();
                case 2 -> MenuNavigator.AUTH_MENU.showMenu();
                case 3 -> AppConfig.setParamApp();
                case 4 -> {
                    System.out.println("Выход из приложения");
                    System.exit(0);
                }
                default -> System.out.println("Пожалуйста, выберите один из предложенных вариантов.");
            }
        }
    }





}