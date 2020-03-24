package com.example.chatapplication.Data;

public class ModelGroup {
    String message;
    String timestamp;
    String type;
    String sender;

    public ModelGroup(String message, String timestamp, String type, String sender) {
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.sender = sender;
    }

    public ModelGroup() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
