package com.msc.app.cook;

public class Users {
    private String userName, admin, password, email ;

    public Users(String userName, String password, String email) {
        this.userName = userName;
        this.admin = admin;
        this.password = password;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return userName;
    }

    public void setName(String name) { userName = userName; }

    public String getPassword() {
        return password;
    }

    public String getAdmin() {
        return admin;
    }
}
