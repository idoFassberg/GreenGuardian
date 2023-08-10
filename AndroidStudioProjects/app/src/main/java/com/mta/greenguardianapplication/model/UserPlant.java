package com.mta.greenguardianapplication.model;

import java.util.HashMap;
import java.util.Map;

public class UserPlant {
    private String plantId;
    private String nickName;
    private int optimalHumidity;
    private String pictureUrl;
    private String plantType;
    private String userId;

    private int currentHumidity;
    private Map<String, Object> statsHumidity;
    public UserPlant() {
        // Default constructor required for Firebase
    }

    public UserPlant(String plantId, String nickName, int optimalHumidity, String pictureUrl, String plantType, String userId, Integer currentHumidity) {
        this.plantId = plantId;
        this.nickName = nickName;
        this.optimalHumidity = optimalHumidity;
        this.pictureUrl = pictureUrl;
        this.plantType = plantType;
        this.userId = userId;
        this.currentHumidity = currentHumidity;
        this.statsHumidity =  new HashMap<>();
    }
    public Map<String, Object> getStatsHumidity() {
        return statsHumidity;
    }

    public void setStatsHumidity(Map<String, Object> statsHumidity) {
        this.statsHumidity = statsHumidity;
    }
    public String getPlantType() {
        return plantType;
    }

    public void setPlantType(String plantType) {
        this.plantType = plantType;
    }

    public int getOptimalHumidity() {
        return optimalHumidity;
    }

    public void setOptimalHumidity(int optimalHumidity) {
        this.optimalHumidity = optimalHumidity;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public int getCurrentHumidity() {
        return currentHumidity;
    }

    public void setCurrentHumidity(int currentHumidity) {
        this.currentHumidity = currentHumidity;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }
}