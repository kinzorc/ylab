package ru.kinzorc.habittracker.navigation;

import ru.kinzorc.habittracker.common.config.HandlerConstants;
import ru.kinzorc.habittracker.common.util.InputUtils;
import ru.kinzorc.habittracker.core.handler.HabitHandler;
import ru.kinzorc.habittracker.core.model.Habit;

public class HabitMetricsMenu implements Menu {

    @Override
    public void showMenu() {
        while (true) {
            if (HandlerConstants.CURRENT_USER.getHabits().isEmpty()) {
                System.out.println("У Вас отсутствуют добавленные привычки");
                return;
            }

            System.out.print("\nДля отображения статистики ");
            Habit selectedHabit = HabitHandler.printHabitInfo(HandlerConstants.CURRENT_USER);

            if (selectedHabit == null) {
                System.out.println("Привычка не найдена");
                return;
            }

            System.out.println("\nУправление:\n1) История выполнения 2) Прогресс выполнения 3) Выход в меню привычек");
            int option = InputUtils.promptMenuValidInput();

            switch (option) {
                case 1 -> HabitHandler.printHabitCompletionHistory(selectedHabit);
                case 2 -> HabitHandler.printHabitMetrics(selectedHabit);
                case 3 -> {
                    System.out.println("Возврат в личный кабинет");
                    return;
                }
                default -> System.out.println("Пожалуйста, выберите один из предложенных вариантов.");
            }
        }
    }
}
