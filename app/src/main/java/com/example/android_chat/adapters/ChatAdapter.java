package com.example.android_chat.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android_chat.entities.Message;
import com.example.android_chat.R;
import com.example.android_chat.entities.User;

import java.util.List;

public class ChatAdapter extends BaseAdapter {

    private Context context;
    private User currentUser;
    private String queue;

    public ChatAdapter(Context context, User currentUser, String queue) {
        this.context = context;
        this.currentUser = currentUser;
        this.queue = queue;
    }

    @Override
    public int getCount() {
        if(User.chatMessages != null && User.chatMessages.get(queue) != null)
            return User.chatMessages.get(queue).size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(User.chatMessages.get(queue) != null)
            return User.chatMessages.get(queue).get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = User.chatMessages.get(queue).get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (message.getSender().getName().equalsIgnoreCase(currentUser.getName())) {
            convertView = mInflater.inflate(R.layout.list_item_message_right, null);
        } else {
            convertView = mInflater.inflate(R.layout.list_item_message_left, null);
        }

        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
        TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);

        lblFrom.setText(message.getSender().getName());
        txtMsg.setText(message.getMessage());

        return convertView;
    }
}
