package com.example.android_chat.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_chat.R;
import com.example.android_chat.activities.ChatGroupActivity;
import com.example.android_chat.entities.User;

import java.io.Serializable;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {
    Context context;
    User currentUser;

    public GroupAdapter(Context context, User currentUser) {
        this.context = context;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_group, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        String participantsName = "";
        holder.groupAvatar.setImageResource(currentUser.getGroupList().get(position).getGroupAvatar());
        holder.txtGroupName.setText(currentUser.getGroupList().get(position).getName());

        for(User user : currentUser.getGroupList().get(position).getParticipants()) {
            if(!currentUser.getName().equals(user.getName())) {
                participantsName += user.getName() + ", ";
            }
        }
        holder.txtUser.setText(participantsName + "você.");

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatGroupActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", (Serializable) currentUser);
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return currentUser.getGroupList().size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView groupAvatar;
        TextView txtGroupName;
        TextView txtUser;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            groupAvatar = (CircleImageView) itemView.findViewById(R.id.groupAvatar);
            txtGroupName = (TextView) itemView.findViewById(R.id.txtGroupName);
            txtUser = (TextView) itemView.findViewById(R.id.txtUser);
        }
    }

}
