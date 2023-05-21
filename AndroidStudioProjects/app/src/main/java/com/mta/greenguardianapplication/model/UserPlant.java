package com.mta.greenguardianapplication.model;

public class UserPlant {
    private String nickName;
    private int optimalHumidity;
    private String pictureUrl;
    private String plantType;
    private String userId;

    private String boardId;
    private int currentHumidity;

    public UserPlant() {
        // Default constructor required for Firebase
    }

    public UserPlant(String nickName, int optimalHumidity, String pictureUrl, String plantType, String userId, String boardId, Integer currentHumidity) {
        this.nickName = nickName;
        this.optimalHumidity = optimalHumidity;
        this.pictureUrl = pictureUrl;
        this.plantType = plantType;
        this.userId = userId;
        this.boardId = boardId;
        this.currentHumidity = currentHumidity;
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

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public int getCurrentHumidity() {
        return currentHumidity;
    }

    public void setCurrentHumidity(int currentHumidity) {
        this.currentHumidity = currentHumidity;
    }
}