package com.tanya.health_care.code;

import java.util.HashMap;
import java.util.Map;

public class UserData {
    public String email, name, gender, role, birthday, image, deviceToken;

    public UserData() {
    }

    public UserData(String email, String name, String gender, String role, String birthday, String image, String deviceToken) {
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.role = role;
        this.birthday = birthday;
        this.image = image;
        this.deviceToken = deviceToken;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("name", name);
        result.put("gender", gender);
        result.put("role", role);
        result.put("birthday", birthday);
        result.put("image", image);
        result.put("token", deviceToken);
        return result;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getToken() {
        return deviceToken;
    }
    public void setToken(String image) {
        this.deviceToken = deviceToken;
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

