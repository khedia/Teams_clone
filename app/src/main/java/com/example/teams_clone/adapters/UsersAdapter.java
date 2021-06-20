package com.example.teams_clone.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teams_clone.Listeners.UsersListeners;
import com.example.teams_clone.Listeners.UsersListeners;
import com.example.teams_clone.R;
import com.example.teams_clone.models.Users;
import com.google.firebase.firestore.auth.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<Users> users;
    private UsersListeners usersListeners;

    public UsersAdapter(List<Users> users, UsersListeners usersListeners) {
        this.users = users;
        this.usersListeners = usersListeners;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_user,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.UserViewHolder holder, int position) {
        holder.sendUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        TextView textFirstChar, textUsername, textEmail;
        ImageView imageAudioMeeting, imageVideoMeeting;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textFirstChar = itemView.findViewById(R.id.textFirstChar);
            textUsername = itemView.findViewById(R.id.textUserName);
            textEmail = itemView.findViewById(R.id.textEmail);
            imageAudioMeeting = itemView.findViewById(R.id.imageAudioMeeting);
            imageVideoMeeting = itemView.findViewById(R.id.imageVideoMeeting);
        }

        void sendUserData(Users user) {
            textFirstChar.setText(user.firstName.substring(0, 1));
            textUsername.setText(String.format("%s %s", user.firstName, user.lastName));
            textEmail.setText(user.email);
            imageAudioMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usersListeners.initiateAudioMeeting(user);
                }
            });
            imageVideoMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usersListeners.initiateVideoMeeting(user);
                }
            });
        }
    }
}