package com.example.teams_clone.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teams_clone.Listeners.UsersListeners;
import com.example.teams_clone.R;
import com.example.teams_clone.models.Users;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<Users> users;
    private UsersListeners usersListeners;
    private List<Users> selectedUsers;

    public UsersAdapter(List<Users> users, UsersListeners usersListeners) {
        this.users = users;
        this.usersListeners = usersListeners;
        selectedUsers = new ArrayList<>();
    }

    public List<Users> getSelectedUsers() {
        return selectedUsers;
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
        ImageView imageAudioMeeting, imageVideoMeeting, imageChatMessage;
        ConstraintLayout userContainer;
        ImageView imageSelected;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textFirstChar = itemView.findViewById(R.id.textFirstChar);
            textUsername = itemView.findViewById(R.id.textUserName);
            textEmail = itemView.findViewById(R.id.textEmail);
            imageAudioMeeting = itemView.findViewById(R.id.imageAudioMeeting);
            imageVideoMeeting = itemView.findViewById(R.id.imageVideoMeeting);
            imageChatMessage = itemView.findViewById(R.id.imageChatMessage);
            userContainer = itemView.findViewById(R.id.userContainer);
            imageSelected = itemView.findViewById(R.id.imageSelected);
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
            imageChatMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usersListeners.initiateChatMessage(user);
                }
            });

            userContainer.setOnLongClickListener(v -> {

                if(imageSelected.getVisibility() != View.VISIBLE) {
                    selectedUsers.add(user);
                    imageSelected.setVisibility(View.VISIBLE);
                    imageVideoMeeting.setVisibility(View.GONE);
                    imageAudioMeeting.setVisibility(View.GONE);
                    usersListeners.onMultipleUsersAction(true);
                }
                return true;
            });

            userContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(imageSelected.getVisibility() ==  View.VISIBLE) {
                        selectedUsers.remove(user);
                        imageSelected.setVisibility(View.GONE);
                        imageVideoMeeting.setVisibility(View.VISIBLE);
                        imageAudioMeeting.setVisibility(View.VISIBLE);
                        if (selectedUsers.size() == 0) {
                            usersListeners.onMultipleUsersAction(false);
                        }
                    } else {
                        if(selectedUsers.size() > 0) {
                                selectedUsers.add(user);
                                imageSelected.setVisibility(View.VISIBLE);
                                imageVideoMeeting.setVisibility(View.GONE);
                                imageAudioMeeting.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }
}