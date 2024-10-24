package ru.kinzorc.habittracker.core.exceptions;

/**
 * Исключение, выбрасываемое в случае, если привычка не найдена.
 * <p>
 * Это проверяемое исключение (наследник {@link Exception}), которое может быть выброшено,
 * когда запрашиваемая привычка отсутствует в системе или не может быть найдена по указанным параметрам.
 * </p>
 */
public class HabitNotFoundException extends Exception {
    public HabitNotFoundException(String message) {
        super(message);
    }
}
