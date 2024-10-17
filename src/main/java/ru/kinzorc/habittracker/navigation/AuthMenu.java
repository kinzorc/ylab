package ru.kinzorc.habittracker.navigation;

import ru.kinzorc.habittracker.common.util.InputUtils;
import ru.kinzorc.habittracker.core.handler.UserHandler;

public class AuthMenu implements Menu {

    @Override
    public void showMenu() {

        System.out.println("\nМеню авторизации:\n1) Вход 2) Сбросить пароль 3) Выход в главное меню");
        int option = InputUtils.promptMenuValidInput();

        switch (option) {
            case 1 -> {
                if (UserHandler.handleLoginUser()) {
                    MenuNavigator.ACCOUNT_MENU.showMenu();
                }
            }
            case 2 -> {
                if (UserHandler.handleResetPassword()) {
                    MenuNavigator.ACCOUNT_MENU.showMenu();
                } else {
                    MenuNavigator.MAIN_MENU.showMenu();
                }
            }
            case 3 -> System.out.println("Возврат в главное меню.");
            default -> System.out.println("Пожалуйста, выберите один из предложенных вариантов.");

        }
    }
}
