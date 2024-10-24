package ru.kinzorc.habittracker.core.exceptions;

/**
 * Исключение, выбрасываемое при попытке создания пользователя, который уже существует в системе.
 * <p>
 * Это проверяемое исключение (наследник {@link Exception}), которое может быть выброшено,
 * когда пользователь с таким же уникальным идентификатором, именем или электронной почтой уже существует в системе.
 * </p>
 */
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
