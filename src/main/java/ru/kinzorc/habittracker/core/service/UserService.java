package ru.kinzorc.habittracker.core.service;

import ru.kinzorc.habittracker.common.config.HandlerConstants;
import ru.kinzorc.habittracker.common.data.DataOfUser;
import ru.kinzorc.habittracker.core.model.User;


public class UserService {

    public UserService() {
    }

    // Метод для регистрации нового пользователя  с добавлению в мапу users
    public boolean registerUser(String name, String email, String password) {
        if (HandlerConstants.USERS.containsKey(email)) {
            return false;
        }

        HandlerConstants.USERS.put(email, new User(name, email, password, false, false, false));
        return true;
    }

    // Метод для авторизации пользователя
    public User loginUser(String email, String password) {
        User user = HandlerConstants.USERS.get(email);

        if (user == null || !user.getPassword().equals(password)) {
            System.out.println("Пользователь не найден или неверный пароль!");
            return null;
        }

        user.setLogin(true);
        return user;
    }

    // Метод для удаления пользователя
    public boolean deleteUser(User user) {
        if (user == null) {
            return false;
        }

        return HandlerConstants.USERS.remove(user.getEmail()) != null;
    }

    public void blockUser(User user) {
        user.setBlocked(true);
    }

    // Метод для изменения данных пользователя
    public void setUserData(User user, DataOfUser param, String value) {
        switch (param) {
            case NAME -> user.setName(value);
            case EMAIL -> user.setEmail(value);
            case PASSWORD -> user.setPassword(value);
        }
    }

    // Метод для возврата пользователя по имени
    public User getUserFromName(String name) {
        return HandlerConstants.USERS.values().stream().filter(user -> user.getName().equals(name)).findFirst().orElse(null);
    }

}
