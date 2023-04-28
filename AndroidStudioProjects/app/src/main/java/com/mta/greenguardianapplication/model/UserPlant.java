package com.mta.greenguardianapplication.model;

public class UserPlant {
    private String plantId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;
    private String plantName;
    private String nickName;


    public UserPlant() {
    }

    public UserPlant(String plantId, String userId, String plantName, String nickName) {
        this.plantId = plantId;
        this.userId = userId;
        this.plantName = plantName;
        this.nickName = nickName;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
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