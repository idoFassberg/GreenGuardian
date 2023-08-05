package com.mta.greenguardianapplication.model;

import java.io.Serializable;

public class User implements Serializable {
    public String name, image, email, fcmToken, plants, id;

    public User() {
        // Default constructor required for Firebase
    }
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
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


