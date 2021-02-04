package com.example.android_chat.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
    private int userAvatar;
    private String name;
    private String phone;
    private List<Group> groupList;
    private List<User> contactsList = new ArrayList<User>();
    private List<Message> messageList;
    public static Map<String, List<Message>> chatMessages = new HashMap<String,List<Message>>();

    public User(int userAvatar, String name, String phone) {
        this.userAvatar = userAvatar;
        this.name = name;
        this.phone = phone;
    }

    public User(){}

    public int getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(int userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<User> getContactsList() {
        return contactsList;
    }

    public List<Group> getGroupList() {
        if (this.groupList == null) {
            this.groupList = new ArrayList<Group>();
        }
        return groupList;
    }

    public List<Message> getMessageList() {
        if (this.messageList == null) {
            this.messageList = new ArrayList<Message>();
        }
        return messageList;
    }



    @Override
    public String toString() {
        return "User= " + name + ", phone='" + phone;
    }
}