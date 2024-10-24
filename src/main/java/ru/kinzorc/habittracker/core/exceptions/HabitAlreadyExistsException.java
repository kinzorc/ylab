package ru.kinzorc.habittracker.core.exceptions;

/**
 * Исключение, выбрасываемое при попытке добавить привычку, которая уже существует.
 * <p>
 * Это проверяемое исключение (наследник {@link Exception}), которое может быть выброшено в случае,
 * если пользователь пытается создать новую привычку с уже существующим уникальным идентификатором или другой уникальной характеристикой.
 * </p>
 */
public class HabitAlreadyExistsException extends Exception {
    public HabitAlreadyExistsException(String message) {
        super(message);
    }
}