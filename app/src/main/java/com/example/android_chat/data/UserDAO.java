package com.example.android_chat.data;

import com.example.android_chat.R;
import com.example.android_chat.entities.Group;
import com.example.android_chat.entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public static List<User> usersList;

    public static List<User> getUsersList() {
        if (usersList == null) {
            usersList = new ArrayList<User>();

            User maria = new User(R.drawable.perfil1, "Maria", "4784554444");
            User vanessa = new User(R.drawable.perfil2, "Vanessa", "47999999999");
            User joao = new User(R.drawable.perfil3, "Joao", "47888888888");

            // Usuários
            getUsersList().add(vanessa);
            getUsersList().add(joao);
            getUsersList().add(maria);

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

            Group group = new Group(R.drawable.perfilgroup1, "IFSC", participants);

            vanessa.getGroupList().add(group);
            maria.getGroupList().add(group);
            joao.getGroupList().add(group);

        }
        return usersList;
    }


}