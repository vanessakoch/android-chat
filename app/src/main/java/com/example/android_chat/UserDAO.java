package com.example.android_chat;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public static List<User> usersList = new ArrayList<User>();

    public static void insertUsers() {
        User vanessa = new User("Vanessa", "47999999999");
        User joao = new User("Joao", "47888888888");
        User maria = new User("Maria", "4784554444");

        // Usuários
        usersList.add(vanessa);
        usersList.add(joao);
        usersList.add(maria);

        // Contatos
        vanessa.getContactsList().add(joao);
        vanessa.getContactsList().add(maria);

        joao.getContactsList().add(vanessa);
        joao.getContactsList().add(maria);

        maria.getContactsList().add(vanessa);
        maria.getContactsList().add(joao);

        // Grupos
        List<User> participants = new ArrayList<User>();
        participants.add(joao);
        participants.add(maria);
        participants.add(vanessa);

        Group group = new Group("IFSC", participants);

        vanessa.getGroupList().add(group);
        maria.getGroupList().add(group);
        joao.getGroupList().add(group);

    }

    public static User getUser(String name) {
        for (User user : usersList) {
            if (name.equalsIgnoreCase(user.getName()))
                return user;
        }
        return null;
    }

    public static List<User> getUsersList() {
        return usersList;
    }

}