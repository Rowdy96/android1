package com.example.chatapplication.Models;

import com.google.gson.annotations.SerializedName;

public class messages {

    private int id;
    @SerializedName("message")
    private String message;
    private int toUserId;
    private int fromUserId;
    private String timeStamp;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }


    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


    public int getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(int fromUserId) {
        this.fromUserId = fromUserId;
    }


    public int getToUserId() {
        return toUserId;
    }

    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
