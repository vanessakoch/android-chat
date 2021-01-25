package com.example.android_chat.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable {
    private int groupAvatar;
    private String name;
    private List<User> participants;

    public Group(int groupAvatar, String name, List<User> groupList) {
        this.groupAvatar = groupAvatar;
        this.name = name;
        this.participants = groupList;
    }

    public int getGroupAvatar() {
        return groupAvatar;
    }

    public void setGroupAvatar(int groupAvatar) {
        this.groupAvatar = groupAvatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getParticipants() {
        if (this.participants == null) {
            this.participants = new ArrayList<User>();
        }
        return participants;
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", participants=" + participants +
                '}';
    }
}
