package com.example.android_chat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_chat.adapters.GroupAdapter;
import com.example.android_chat.R;
import com.example.android_chat.entities.User;

public class GroupFragment extends Fragment {
    User currentUser;
    RecyclerView recyclerView;
    GroupAdapter adapter;

    public GroupFragment(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        adapter = new GroupAdapter(getContext(), currentUser);

        recyclerView = (RecyclerView) view.findViewById(R.id.contact_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

}
