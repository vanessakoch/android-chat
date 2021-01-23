package com.example.android_chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable {
    String name;
    List<User> participants;

    public Group(String name, List<User> groupList) {
        this.name = name;
        this.participants = groupList;
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
