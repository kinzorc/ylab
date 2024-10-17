package ru.kinzorc.habittracker.common.config;

import ru.kinzorc.habittracker.core.model.User;
import ru.kinzorc.habittracker.core.service.HabitService;
import ru.kinzorc.habittracker.core.service.UserService;

import java.util.HashMap;


public class HandlerConstants {
    public static UserService USER_SERVICE;
    public static HabitService HABIT_SERVICE;
    public static final HashMap<String, User> USERS;
    public static User CURRENT_USER = null;

    static {
        USER_SERVICE = new UserService();
        HABIT_SERVICE = new HabitService();
        USERS = new HashMap<>();
        USERS.put("1@1.test", new User("admin", "1@1.test", "1", true, false, false));
    }

}
