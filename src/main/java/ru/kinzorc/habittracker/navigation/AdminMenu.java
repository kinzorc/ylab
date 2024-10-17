package ru.kinzorc.habittracker.navigation;

import ru.kinzorc.habittracker.common.config.HandlerConstants;
import ru.kinzorc.habittracker.common.util.InputUtils;
import ru.kinzorc.habittracker.common.util.OutputUtils;
import ru.kinzorc.habittracker.core.handler.HabitHandler;
import ru.kinzorc.habittracker.core.handler.UserHandler;
import ru.kinzorc.habittracker.core.model.User;


public class AdminMenu implements Menu {

    @Override
    public void showMenu() {

        while (true) {
            System.out.println("""

                    Администрирование:
                    1) Список пользователей 2) Список привычек пользователя 3) Заблокировать пользователя 4) Удалить пользователя 5) Выход в личный кабинет""");

            int option = InputUtils.promptMenuValidInput();

            switch (option) {
                case 1 -> {
                    OutputUtils.printListUsers(HandlerConstants.USERS);
                    InputUtils.promptInput("Введите enter для выхода...");
                }
                case 2 -> {
                    User user = UserHandler.getFindUser();
                    if (user != null) {
                        HabitHandler.printUserListHabits(user);
                        InputUtils.promptInput("Введите enter для выхода...");
                    } else {
                        System.out.println("Пользователь не найден!");
                    }
                }
                case 3 -> UserHandler.handleBlockUser(UserHandler.getFindUser());
                case 4 -> UserHandler.handleDeleteUser(UserHandler.getFindUser());
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
