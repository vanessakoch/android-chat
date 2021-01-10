package com.example.android_chat;

public class Message {
    private String name;
    private String message;
    private boolean isSelf;

    public Message(String name, String message, boolean isSelf) {
        this.name = name;
        this.message = message;
        this.isSelf = isSelf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }
}
