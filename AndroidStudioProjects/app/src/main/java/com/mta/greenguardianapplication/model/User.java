package com.mta.greenguardianapplication.model;

import java.io.Serializable;

public class User implements Serializable {
    public String name, image, email, fcmToken, plants;

    public User(String name, String email) {
        setName(name);
        setEmail(email);
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


