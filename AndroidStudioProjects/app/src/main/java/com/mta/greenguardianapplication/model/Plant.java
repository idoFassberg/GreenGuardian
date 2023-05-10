package com.mta.greenguardianapplication.model;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Plant {
    private String name;
    private String type;
    private int recommendedHumidity;
    private String pictureUrl;

    public Plant() {
        // Required empty constructor for Firebase
    }

    public Plant(String name, String type, int recommendedHumidity, String pictureUrl) {
        this.name = name;
        this.type = type;
        this.recommendedHumidity = recommendedHumidity;
        this.pictureUrl = pictureUrl;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getRecommendedHumidity() {
        return recommendedHumidity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRecommendedHumidity(int recommendedHumidity) {
        this.recommendedHumidity = recommendedHumidity;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    // Additional setter and getter methods as needed
}
