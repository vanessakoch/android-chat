package com.example.android_chat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessagesListAdapter extends RecyclerView.Adapter<MessagesListAdapter.MessagesViewHolder> {

    private Context context;
    private List<Message> messagesList;

    public MessagesListAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messagesList = messageList;
    }


    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (messagesList.get(viewType).isSelf()) {
            view = mInflater.inflate(R.layout.list_item_message_right,
                    null);
        } else {
            view = mInflater.inflate(R.layout.list_item_message_left,
                    null);
        }



        MessagesViewHolder viewHolder = new MessagesViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {
        holder.lblMsg.setText(messagesList.get(position).getName());
        holder.txtMsg.setText(messagesList.get(position).getMessage());
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public void insertItem(Message message) {
        messagesList.add(message);
        notifyItemInserted(getItemCount());
    }

    public static class MessagesViewHolder extends RecyclerView.ViewHolder{
        TextView lblMsg;
        TextView txtMsg;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setTag(this);
            TextView lblMsg = (TextView) itemView.findViewById(R.id.lblMsgFrom);
            TextView txtMsg = (TextView) itemView.findViewById(R.id.txtMsg);
        }
    }

}
