// This interface is used to call the respective feature's function which the user selects
package com.example.teams_clone.Listeners;

import com.example.teams_clone.models.Users;

public interface UsersListeners {

    void initiateVideoMeeting(Users user);

    void initiateAudioMeeting(Users user);

    void initiateChatMessage(Users user);

    void onMultipleUsersAction(Boolean isMultipleUsersSelected);
}
