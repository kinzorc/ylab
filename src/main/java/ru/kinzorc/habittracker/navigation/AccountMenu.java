package ru.kinzorc.habittracker.navigation;

import ru.kinzorc.habittracker.common.config.HandlerConstants;
import ru.kinzorc.habittracker.common.util.InputUtils;
import ru.kinzorc.habittracker.core.handler.UserHandler;


public class AccountMenu implements Menu {

    @Override
    public void showMenu() {

        if (HandlerConstants.CURRENT_USER == null) {
            System.out.println("\nВы не авторизованы. Переход в главное меню.");
            return;
        }

        while (true) {
            if (HandlerConstants.CURRENT_USER.isAdmin())
                System.out.println("\nЛичный кабинет:\n1) Профиль 2) Мои привычки 3) Удалить аккаунт 4) Администрирование 5) Выход в главное меню");
            else
                System.out.println("\nЛичный кабинет:\n1) Профиль 2) Мои привычки 3) Удалить аккаунт 4) Выход в главное меню");

            int option = InputUtils.promptMenuValidInput();

            switch (option) {
                case 1 -> MenuNavigator.USER_PROFILE_MENU.showMenu();
                case 2 -> MenuNavigator.HABIT_MENU.showMenu();
                case 3 -> {
                    if (UserHandler.handleDeleteUser(HandlerConstants.CURRENT_USER)) {
                        return;
                    }
                }
                case 4 -> {
                    if (HandlerConstants.CURRENT_USER.isAdmin()) {
                        MenuNavigator.ADMIN_MENU.showMenu();
                    } else {
                        System.out.println("Выход в главное меню.");
                        return;
                    }
                }
                case 5 -> {
                    HandlerConstants.CURRENT_USER.setLogin(false);
                    System.out.println("Выход в главное меню.");
                    return;
                }
                default -> System.out.println("Пожалуйста, выберите один из предложенных вариантов.");
            }
        }
    }
}
