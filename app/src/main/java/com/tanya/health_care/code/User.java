package com.tanya.health_care.code;

import java.util.Date;

public class User {
    private String email, password, gender, role;
    private Date birthday;

    public User() {
    }

    public User(String email, String password, String gender, String role, Date birthday) {
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.role = role;
        this.birthday = birthday;
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}

