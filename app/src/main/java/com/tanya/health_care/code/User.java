package com.tanya.health_care.code;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User {
    public String email, name, gender, role, birthday;

    public User() {
    }

    public User(String email, String name, String gender, String role, String birthday) {
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.role = role;
        this.birthday = birthday;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("name", name);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

