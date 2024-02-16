package com.tanya.health_care.code;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String email, password, gender, role, birthday;

    public User() {
    }

    public User(String email, String password, String gender, String role, String birthday) {
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.role = role;
        this.birthday = birthday;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("password", password);
        result.put("gender", gender);
        result.put("role", role);
        result.put("birthday", birthday);
        return result;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}

