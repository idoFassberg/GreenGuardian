package com.mta.greenguardianapplication.model;

import java.util.Date;

public class ChatMessage {
    public String senderId, receiverId, message, dateTime;
    public Date dateObjct;

    public ChatMessage() {
        // Empty constructor required for Firebase
    }

    public ChatMessage(String senderId, String receiverId, String message, String dateTime) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.dateTime = dateTime;
    }

    public String getTimestamp() {
        return dateTime;
    }
}
