package ru.kinzorc.habittracker.navigation;


public enum MenuNavigator {

    MAIN_MENU(new MainMenu()),
    AUTH_MENU(new AuthMenu()),
    ACCOUNT_MENU(new AccountMenu()),
    ADMIN_MENU(new AdminMenu()),
    USER_PROFILE_MENU(new UserProfileMenu()),
    HABIT_MENU(new HabitMenu()),
    HABIT_METRICS_MENU(new HabitMetricsMenu());

    private final Menu menu;

    // Конструктор, который привязывает объект Menu к элементу перечисления
    MenuNavigator(Menu menu) {
        this.menu = menu;
    }

    public void showMenu() {
        menu.showMenu();
    }

}
