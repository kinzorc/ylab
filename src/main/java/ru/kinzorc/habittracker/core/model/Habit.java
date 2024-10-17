package ru.kinzorc.habittracker.core.model;


import ru.kinzorc.habittracker.common.data.FrequencyHabit;
import ru.kinzorc.habittracker.common.data.HabitStatus;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

public class Habit {
    private String name;
    private String description;
    private FrequencyHabit frequency;
    private final LocalDate createdDate;
    private LocalDate startDate;
    private HabitStatus status;
    private HashSet<LocalDate> completionHistory;

    public Habit(String name, String description, FrequencyHabit frequency, LocalDate startDate) {
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.createdDate = LocalDate.now();
        this.startDate = startDate;
        this.status = HabitStatus.ACTIVE;
        this.completionHistory = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FrequencyHabit getFrequency() {
        return frequency;
    }

    public void setFrequency(FrequencyHabit frequency) {
        this.completionHistory.clear();
        this.frequency = frequency;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.completionHistory.clear();
        this.startDate = startDate;
    }

    public HabitStatus getStatus() {
        return status;
    }

    public void setStatus(HabitStatus status) {
        this.status = status;
    }

    public HashSet<LocalDate> getCompletionHistory() {
        return completionHistory;
    }

    public void setCompletionHistory(HashSet<LocalDate> completionHistory) {
        this.completionHistory = completionHistory;
    }

    // Метод для отметки успешного выполнения привычки
    public void markCompletion(LocalDate date) {
        // Проверяем статус привычки
        if (status == HabitStatus.FINISHED) {
            System.out.println("Вы завершили выполнение этой привычки, создайте новую!");
            return;
        }

        // Проверяем, что дата выполнения не доходит до даты начала
        if (date.isBefore(startDate)) {
            System.out.println("Дата начала " + startDate + " выполнения привычки " + name + " еще не наступила");
            return;
        }

        // Проверка на выполнение привычки в зависимости от частоты
        if (frequency == FrequencyHabit.DAY) {
            if (completionHistory.contains(date)) {
                System.out.println("Привычка '" + name + "' уже была выполнена сегодня: " + date);
                return;
            }
        } else if (frequency == FrequencyHabit.WEEK) {
            if (isCompletedThisWeek(date)) {
                System.out.println("Привычка '" + name + "' уже была выполнена на этой неделе.");
                return;
            }
        }

        // Добавляем выполнение привычки в историю
        completionHistory.add(date);
        System.out.println("Привычка '" + name + "' успешно выполнена на дату: " + date);
    }

    // Метод для проверки, выполнена ли привычка на этой неделе
    boolean isCompletedThisWeek(LocalDate date) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        // Определяем неделю и год для переданной даты
        int targetWeek = date.get(weekFields.weekOfWeekBasedYear());
        int targetYear = date.get(weekFields.weekBasedYear());

        // Проходим по всем датам в истории выполнения привычек
        for (LocalDate completionDate : completionHistory) {
            int completionWeek = completionDate.get(weekFields.weekOfWeekBasedYear());
            int completionYear = completionDate.get(weekFields.weekBasedYear());

            if (completionWeek == targetWeek && completionYear == targetYear) {
                return true;
            }
        }

        return false;
    }


    // Подсчет стрика в зависимости от периодичности
    public int calculateStreak() {
        if (frequency == FrequencyHabit.DAY) {
            return calculateDailyStreak();
        } else if (frequency == FrequencyHabit.WEEK) {
            return calculateWeeklyStreak();
        }
        return 0;
    }

    // Подсчет стрика для ежедневной привычки
    private int calculateDailyStreak() {
        int streak = 0;
        LocalDate currentDate = LocalDate.now();

        // Проверяем выполнение подряд, начиная с текущего дня
        while (completionHistory.contains(currentDate)) {
            streak++;
            currentDate = currentDate.minusDays(1);  // Переходим к предыдущему дню
        }

        return streak;
    }

    // Подсчет стрика для еженедельной привычки
    private int calculateWeeklyStreak() {
        int streak = 0;
        LocalDate currentDate = LocalDate.now();

        // Проверяем выполнение на текущей неделе
        if (!isCompletedThisWeek(currentDate)) {
            // Если текущая неделя еще не завершена (текущий день недели не воскресенье), продолжаем подсчет стрика
            if (currentDate.getDayOfWeek().getValue() <= 7) {
                // Неделя еще не завершена, продолжаем с предыдущих недель
                currentDate = currentDate.minusWeeks(1);
            } else {
                // Неделя завершена и привычка не выполнена, сбрасываем стрик
                return streak;  // Вернем 0, так как стрик сбрасывается
            }
        } else {
            // Если привычка выполнена на текущей неделе, начинаем подсчет стрика
            streak++;
            currentDate = currentDate.minusWeeks(1);  // Переходим к предыдущей неделе
        }

        // Продолжаем проверку выполнения на предыдущих неделях
        while (isCompletedThisWeek(currentDate)) {
            streak++;
            currentDate = currentDate.minusWeeks(1);  // Переходим к предыдущей неделе
        }

        return streak;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, frequency, createdDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;

        Habit habit = (Habit) obj;

        return Objects.equals(name, habit.name)
                && Objects.equals(description, habit.description)
                && Objects.equals(frequency, habit.frequency)
                && Objects.equals(createdDate, habit.createdDate)
                && Objects.equals(startDate, habit.startDate)
                && Objects.equals(status, habit.status);
    }

    @Override
    public String toString() {
        return "Привычка: " + name + ", Описание привычки: " + description + ", Частота выполнения: " + frequency;
    }
}
