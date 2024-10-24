package ru.kinzorc.habittracker.presentation.menu;

import ru.kinzorc.habittracker.application.service.ApplicationService;
import ru.kinzorc.habittracker.core.entities.User;
import ru.kinzorc.habittracker.core.enums.User.UserRole;
import ru.kinzorc.habittracker.presentation.utils.MenuUtils;

import java.util.Scanner;


public class AccountMenu implements Menu {

    @Override
    public void showMenu(ApplicationService applicationService, MenuUtils menuUtils) {
        Scanner scanner = new Scanner(System.in);

        if (applicationService.getCurrentUser() == null) {
            System.out.println("\nВы не авторизованы. Переход в главное меню.");
            return;
        }

        while (true) {
            if (applicationService.getCurrentUser().getUserRole() == UserRole.ADMIN)
                System.out.println("\nЛичный кабинет:\n1) Профиль 2) Мои привычки 3) Удалить мой аккаунт 4) Администрирование 5) Выход в главное меню");
            else
                System.out.println("\nЛичный кабинет:\n1) Профиль 2) Мои привычки 3) Удалить мой аккаунт 4) Выход в главное меню");

            int option = menuUtils.promptMenuValidInput(scanner);

            switch (option) {
                case 1 -> MenuNavigator.USER_PROFILE_MENU.showMenu(applicationService, menuUtils);
                case 2 -> MenuNavigator.HABIT_MENU.showMenu(applicationService, menuUtils);
                case 3 -> {
                    User currnetUser = applicationService.getCurrentUser();
                    applicationService.deleteUser(currnetUser);

                    if (applicationService.getCurrentUser() == null) {
                        return;
                    }
                }
                case 4 -> {
                    if (applicationService.getCurrentUser().getUserRole() == UserRole.ADMIN) {
                        MenuNavigator.ADMIN_MENU.showMenu(applicationService, menuUtils);
                    } else {
                        System.out.println("Выход в главное меню.");
                        return;
                    }
                }
                case 5 -> {
                    User currnetUser = applicationService.getCurrentUser();
                    boolean isLogout = applicationService.logoutUser(currnetUser);

                    if (isLogout) {
                        System.out.println("Выход в главное меню.");
                        return;
                    }
                }
                default -> System.out.println("Пожалуйста, выберите один из предложенных вариантов.");
            }
        }
    }
}
