package com.example.teams_clone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teams_clone.R;
import com.example.teams_clone.models.Chat;
import com.example.teams_clone.utilities.Constants;
import com.example.teams_clone.utilities.MessageStatusConstant;
import com.example.teams_clone.utilities.PreferenceManager;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private PreferenceManager preferenceManager;

    public static  final int MSG_TYPE_LEFT = 0;
    public static  final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;

    public MessageAdapter(Context mContext, List<Chat> mChat, PreferenceManager preferenceManager){
        this.mChat = mChat;
        this.mContext = mContext;
        this.preferenceManager = preferenceManager;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);
        holder.show_message.setText(chat.getMessage());
        holder.timeOfMessage.setText(chat.getTime());

        if (position == mChat.size()-1){
            if (chat.isIsseen()){
                holder.txt_seen.setText(MessageStatusConstant.SEEN);
            } else {
                holder.txt_seen.setText(MessageStatusConstant.DELIVERED);
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public TextView txt_seen;
        public TextView timeOfMessage;

        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            timeOfMessage = itemView.findViewById(R.id.timeOfMessage);
            txt_seen = itemView.findViewById(R.id.txt_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        String fUser = preferenceManager.getString(Constants.KEY_USER_ID);
        if (mChat.get(position).getSender().equals(fUser)) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}