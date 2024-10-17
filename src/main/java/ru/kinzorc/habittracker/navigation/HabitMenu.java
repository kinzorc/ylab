package ru.kinzorc.habittracker.navigation;

import ru.kinzorc.habittracker.common.config.HandlerConstants;
import ru.kinzorc.habittracker.common.util.InputUtils;
import ru.kinzorc.habittracker.core.handler.HabitHandler;

public class HabitMenu implements Menu {

    @Override
    public void showMenu() {

        while (true) {
            System.out.println("\nМои привычки:\n");
            HabitHandler.printUserListHabits(HandlerConstants.CURRENT_USER);
            System.out.println("""

                    Управление: 1) Статистика по привычке 2) Отметить выполнение 3) Добавить привычку
                                4) Изменить привычку 5) Завершить привычку 6) Удалить привычку 7) Выход в личный кабинет""");
            int option = InputUtils.promptMenuValidInput();

            switch (option) {
                case 1 -> MenuNavigator.HABIT_METRICS_MENU.showMenu();
                case 2 -> HabitHandler.userMarkDoneHabit(HandlerConstants.CURRENT_USER);
                case 3 -> HabitHandler.addHabitForUser(HandlerConstants.CURRENT_USER);
                case 4 -> HabitHandler.editHabitForUser(HandlerConstants.CURRENT_USER);
                case 5 -> HabitHandler.removeHabitForUser(HandlerConstants.CURRENT_USER);
                case 6 -> HabitHandler.removeHabitForUser(HandlerConstants.CURRENT_USER);
                case 7 -> {
                    System.out.println("Возврат в личный кабинет");
                    return;
                }
                default -> System.out.println("Пожалуйста, выберите один из предложенных вариантов.");
            }
        }
    }
}
