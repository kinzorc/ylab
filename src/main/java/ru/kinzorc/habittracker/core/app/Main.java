package ru.kinzorc.habittracker.core.app;

import ru.kinzorc.habittracker.navigation.MenuNavigator;


public class Main {
    public static void main(String[] args) {

        System.out.println("Не забудьте настроить параметры для отрпавки почтовых уведомлений, через меню \"Настройки\"");
        MenuNavigator.MAIN_MENU.showMenu();
    }
}
