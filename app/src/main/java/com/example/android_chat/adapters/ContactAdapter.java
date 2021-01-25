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
import com.example.android_chat.activities.ChatActivity;
import com.example.android_chat.activities.MainActivity;
import com.example.android_chat.entities.User;

import java.io.Serializable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {
    Context context;
    User currentUser;

    public ContactAdapter(Context context, User currentUser) {
        this.context = context;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.contactAvatar.setImageResource(currentUser.getContactsList().get(position).getUserAvatar());
        holder.txtName.setText(currentUser.getContactsList().get(position).getName());
        holder.txtPhone.setText(currentUser.getContactsList().get(position).getPhone());

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", (Serializable) currentUser);
                bundle.putSerializable("contact", currentUser.getContactsList().get(holder.getAdapterPosition()));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return currentUser.getContactsList().size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView contactAvatar;
        TextView txtName;
        TextView txtPhone;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            contactAvatar = (CircleImageView) itemView.findViewById(R.id.contactAvatar);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtPhone = (TextView) itemView.findViewById(R.id.txtPhone);
        }
    }

}
