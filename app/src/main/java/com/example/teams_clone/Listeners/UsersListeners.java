package com.example.teams_clone.Listeners;

import com.example.teams_clone.models.Users;

public interface UsersListeners {

    void initiateVideoMeeting(Users user);

    void initiateAudioMeeting(Users user);

    void onMultipleUsersAction(Boolean isMultipleUsersSelected);
}
