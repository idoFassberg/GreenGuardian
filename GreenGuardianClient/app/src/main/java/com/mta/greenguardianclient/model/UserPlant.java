package com.mta.greenguardianclient.model;

import java.sql.Date;

public class UserPlant {
    private int plantId;
    private int userId;
    private String plantName;
    private String nickName;
    private int currentHumidityLevel;
    private int recommendedHumidityLevel;
    private String pictureURL;

    private java.sql.Date creationDate;

    public UserPlant() {
        this.creationDate = new java.sql.Date(new java.util.Date().getTime());
    }

    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String name) {
        this.plantName = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public int getCurrentHumidityLevel() {
        return currentHumidityLevel;
    }

    public void setCurrentHumidityLevel(int currentHumidityLevel) {
        this.currentHumidityLevel = currentHumidityLevel;
    }

    public int getRecommendedHumidityLevel() {
        return recommendedHumidityLevel;
    }

    public void setRecommendedHumidityLevel(int recommendedHumidityLevel) {
        this.recommendedHumidityLevel = recommendedHumidityLevel;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getCreationDateStr() {
        return creationDate.toString();
    }

    @Override
    public String toString() {
        return "UserPlant{" +
                "plantId=" + plantId +
                ", userId=" + userId +
                ", plantName='" + plantName + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}

