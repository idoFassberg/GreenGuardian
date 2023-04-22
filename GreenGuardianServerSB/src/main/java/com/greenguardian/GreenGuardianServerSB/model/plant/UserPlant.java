package com.greenguardian.GreenGuardianServerSB.model.plant;

public class UserPlant {
    private int plantId;
    private int userId;
    private String plantName;
    private String nickName;
    /*private int currentHumidityLevel;
    private int recommendedHumidityLevel;
    private String pictureURL;

    private java.sql.Date creationDate;*/

    public UserPlant() {
        super();
    }

    public UserPlant(int plantId, int userId, String plantName, String nickName) {
        this.plantId = plantId;
        this.userId = userId;
        this.plantName = plantName;
        this.nickName = nickName;
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
   /* public int getCurrentHumidityLevel() {
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
    }*/



   /* @Override
    public String toString() {
        return "UserPlant{" +
                "plantId=" + plantId +
                ", userId=" + userId +
                ", plantName='" + plantName + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }*/
}
