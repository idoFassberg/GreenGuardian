package com.mta.greenguardianapplication.model;

import java.util.Date;

public class ChatMessage {
    public String senderId, receiverId, message;
    public long dateTime;
    /*public Date dateObject;*/

    public ChatMessage() {
        // Empty constructor required for Firebase
    }

    public ChatMessage(String senderId, String receiverId, String message, Long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.dateTime = timestamp;
    }
}
