package com.example.android_chat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ChatAdapter extends BaseAdapter {

    private Context context;
    private List<Message> messageList;

    public ChatAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = messageList.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (messageList.get(position).isSelf()) {
            convertView = mInflater.inflate(R.layout.list_item_message_right, null);
        } else {
            convertView = mInflater.inflate(R.layout.list_item_message_left, null);
        }

        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
        TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);

        lblFrom.setText(message.getName());
        txtMsg.setText(message.getMessage());

        return convertView;
    }
}
