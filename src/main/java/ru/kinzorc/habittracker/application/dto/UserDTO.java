package ru.kinzorc.habittracker.application.dto;

import ru.kinzorc.habittracker.core.entities.User;
import ru.kinzorc.habittracker.core.enums.User.UserRole;
import ru.kinzorc.habittracker.core.enums.User.UserStatusAccount;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс Data Transfer Object (DTO) для передачи данных пользователя между слоями приложения.
 * <p>
 * Этот класс используется для представления пользователя в виде простого объекта данных,
 * который может быть легко передан между слоями приложения (например, между сервисом и контроллером)
 * или при работе с базой данных.
 * </p>
 */
public class UserDTO {

    /**
     * Уникальный идентификатор пользователя.
     */
    private Long id;

    /**
     * Имя пользователя.
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
     * Определяется значениями перечисления {@link UserRole}.
     * </p>
     */
    private UserRole userRole;

    /**
     * Статус учетной записи пользователя.
     * <p>
     * Определяется значениями перечисления {@link UserStatusAccount}.
     * </p>
     */
    private UserStatusAccount userStatusAccount;

    /**
     * Конструктор для создания объекта DTO на основе сущности {@link User}.
     *
     * @param user объект {@link User}, из которого будут извлечены данные
     */
    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUserName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.userRole = user.getUserRole();
        this.userStatusAccount = user.getUserStatusAccount();
    }

    /**
     * Конструктор для создания объекта DTO на основе данных из {@link ResultSet}.
     *
     * @param resultSet объект {@link ResultSet}, содержащий данные из базы данных
     * @throws SQLException если возникает ошибка при извлечении данных из {@link ResultSet}
     */
    public UserDTO(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getLong("id");
        this.username = resultSet.getString("username");
        this.email = resultSet.getString("email");
        this.password = resultSet.getString("password");
        this.userRole = UserRole.valueOf(resultSet.getString("role").toUpperCase());
        this.userStatusAccount = UserStatusAccount.valueOf(resultSet.getString("status").toUpperCase());
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
     * Преобразование объекта DTO обратно в сущность {@link User}.
     *
     * @return объект {@link User}, созданный на основе данных DTO
     */
    public User toUser() {
        User user = new User(username, password, email, userRole);
        user.setId(id);
        user.setUserStatusAccount(userStatusAccount);
        return user;
    }

    /**
     * Преобразует данные DTO в массив для передачи в SQL-запрос.
     *
     * @return массив объектов для использования в SQL-запросах
     */
    public Object[] toSqlParams() {
        return new Object[]{username, password, email, userRole.toString().toLowerCase(), userStatusAccount.toString().toLowerCase(), id};
    }
}