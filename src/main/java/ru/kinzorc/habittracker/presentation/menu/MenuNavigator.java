package ru.kinzorc.habittracker.presentation.menu;


import ru.kinzorc.habittracker.application.service.ApplicationService;
import ru.kinzorc.habittracker.presentation.utils.MenuUtils;

public enum MenuNavigator {

    MAIN_MENU(new MainMenu()),
    AUTH_MENU(new AuthMenu()),
    ACCOUNT_MENU(new AccountMenu()),
    ADMIN_MENU(new AdminMenu()),
    USER_PROFILE_MENU(new UserProfileMenu()),
    HABIT_MENU(new HabitMenu());

    private final Menu menu;

    // Конструктор, который привязывает объект Menu к элементу перечисления
    MenuNavigator(Menu menu) {
        this.menu = menu;
    }

    public void showMenu(ApplicationService applicationService, MenuUtils menuUtils) {
        menu.showMenu(applicationService, menuUtils);
    }

}
