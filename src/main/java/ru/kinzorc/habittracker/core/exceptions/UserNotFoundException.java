package ru.kinzorc.habittracker.core.exceptions;

/**
 * Исключение, выбрасываемое при попытке найти пользователя, который отсутствует в системе.
 * <p>
 * Это проверяемое исключение (наследник {@link Exception}), которое выбрасывается, когда пользователь не может быть найден
 * в системе по указанному идентификатору, имени или другому уникальному атрибуту.
 * </p>
 */
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}
