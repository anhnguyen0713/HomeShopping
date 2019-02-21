package com.anhnguyen.homeshopping.model;

import java.util.Date;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String avatar = null;
    private int gender;
    private Date birthday;
    private int occupation;
    private boolean isLoggedIn = false;

    private static User user = new User();

    private User(){}

    public static User getInstance(){
        return user;
    }

    public boolean isLoggedIn(){
        return isLoggedIn;
    }

    public void setLoggedIn(boolean status){
        isLoggedIn = status;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getAvatar(){
        return avatar;
    }

    public void setAvatar(String avatar){
        this.avatar = avatar;
    }
}
