package com.mta.greenguardianapplication.model;

import java.io.Serializable;

public class User implements Serializable {
    public String name;
    public String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String email;
    public String fcmToken;
    public String plants;
    public String id;

    public User() {
        // Default constructor required for Firebase
    }
    public User(String id, String name, String email,String image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}


