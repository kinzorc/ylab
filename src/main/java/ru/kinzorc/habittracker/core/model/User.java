package ru.kinzorc.habittracker.core.model;

import java.util.HashMap;
import java.util.Objects;

public class User {
    private String name;
    private String password;
    private String email;
    private final boolean isAdmin;
    private boolean isLogin;
    private boolean isBlocked;
    private final HashMap<String, Habit> habits;

    public User(String name, String email, String password, boolean isAdmin, boolean isLogin, boolean isBlocked) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isLogin = isLogin;
        this.isBlocked = isBlocked;
        this.habits = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public HashMap<String, Habit> getHabits() {
        return this.habits;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, password);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;

        User user = (User) obj;

        return Objects.equals(name, user.name)
                && Objects.equals(email, user.email)
                && Objects.equals(password, user.password);
    }
}
