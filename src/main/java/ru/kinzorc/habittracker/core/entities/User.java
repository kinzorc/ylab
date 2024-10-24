package ru.kinzorc.habittracker.core.entities;

import ru.kinzorc.habittracker.core.enums.User.UserRole;
import ru.kinzorc.habittracker.core.enums.User.UserStatusAccount;

import java.util.Objects;

/**
 * Класс представляет сущность пользователя в системе.
 * <p>
 * Пользователь обладает уникальным идентификатором (id), именем, паролем, email,
 * а также ролями и статусами учетной записи и активности.
 * </p>
 */
public class User {

    /**
     * Идентификатор пользователя (id).
     */
    private long id;

    /**
     * Имя пользователя (username).
     */
    private String username;

    /**
     * Пароль пользователя.
     */
    private String password;

    /**
     * Электронная почта пользователя.
     */
    private String email;

    /**
     * Роль пользователя в системе.
     * <p>
     * Определяется значениями перечисления {@link UserRole}, которые обозначают роль пользователя (например, администратор или обычный пользователь).
     * </p>
     */
    private UserRole userRole;

    /**
     * Статус учетной записи пользователя.
     * <p>
     * Может принимать значения из перечисления {@link UserStatusAccount}, указывая на состояние аккаунта (например, активен или заблокирован).
     * </p>
     */
    private UserStatusAccount userStatusAccount;

    /**
     * Конструктор для создания нового пользователя.
     * <p>
     * При создании пользователя автоматически генерируется уникальный идентификатор, устанавливается статус действия аккаунта {@code ACTIVE},
     * а также инициализируется коллекция для хранения привычек пользователя.
     * </p>
     *
     * @param username имя пользователя
     * @param password пароль пользователя
     * @param email    электронная почта пользователя
     * @param userRole роль пользователя, определяется перечислением {@link UserRole}
     */
    public User(String username, String password, String email, UserRole userRole) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.userRole = userRole;
        this.userStatusAccount = UserStatusAccount.ACTIVE;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public UserStatusAccount getUserStatusAccount() {
        return userStatusAccount;
    }

    public void setUserStatusAccount(UserStatusAccount userStatusAccount) {
        this.userStatusAccount = userStatusAccount;
    }

    /**
     * Переопределяет метод {@code equals()} для сравнения пользователей по уникальному идентификатору.
     * <p>
     * Два пользователя считаются равными, если их уникальные идентификаторы {@code id} совпадают.
     * </p>
     *
     * @param o объект для сравнения с текущим пользователем
     * @return {@code true}, если объекты равны, иначе {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    /**
     * Возвращает хэш-код пользователя на основе уникального идентификатора.
     *
     * @return хэш-код пользователя
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Возвращает строковое представление объекта пользователя.
     * <p>
     * Включает информацию об имени, email, роли и статусе учетной записи пользователя.
     * </p>
     *
     * @return строковое представление пользователя
     */
    @Override
    public String toString() {
        return String.format("Пользователь (id - %s): имя - %s, email - %s, роль - %s, статус аккаунта - %s", id, username, email, userRole, userStatusAccount);
    }
}
