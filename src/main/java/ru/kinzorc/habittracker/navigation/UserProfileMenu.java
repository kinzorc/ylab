package ru.kinzorc.habittracker.navigation;

import ru.kinzorc.habittracker.common.config.HandlerConstants;
import ru.kinzorc.habittracker.common.util.InputUtils;
import ru.kinzorc.habittracker.core.handler.UserHandler;

public class UserProfileMenu implements Menu {

    @Override
    public void showMenu() {

        while (true) {
            System.out.println("\nПрофиль пользователя:\n "
                    + "\n Имя: " + HandlerConstants.CURRENT_USER.getName()
                    + "\n Email: " + HandlerConstants.CURRENT_USER.getEmail()
                    + "\n Пароль: " + HandlerConstants.CURRENT_USER.getPassword() + "\n");

            System.out.println("Меню профиля: 1) Изменить имя 2) Изменить email 3) Изменить пароль 4) Выход");

            int option = InputUtils.promptMenuValidInput();

            switch (option) {
                case 1 -> UserHandler.setNameUser(HandlerConstants.CURRENT_USER);
                case 2 -> UserHandler.setEmailUser(HandlerConstants.CURRENT_USER);
                case 3 -> UserHandler.setPasswordUser(HandlerConstants.CURRENT_USER);
                case 4 -> {
                    System.out.println("Возврат в личный кабинет");
                    return;
                }
                default -> System.out.println("Пожалуйста, выберите один из предложенных вариантов.");
            }
        }
    }
}
