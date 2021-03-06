package com.example.teams_clone.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.teams_clone.Listeners.UsersListeners;
import com.example.teams_clone.R;
import com.example.teams_clone.adapters.UsersAdapter;
import com.example.teams_clone.managers.AuthenticationManager;
import com.example.teams_clone.models.Users;
import com.example.teams_clone.utilities.Constants;
import com.example.teams_clone.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UsersListeners {

    private PreferenceManager preferenceManager;
    private List<Users> users = new ArrayList<>();
    private UsersAdapter usersAdapter;
    private TextView textErrorMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imageConference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(getApplicationContext());

        imageConference = findViewById(R.id.imageConference);

        TextView textTitle = findViewById(R.id.texTitle);
        textTitle.setText(String.format(
                "%s %s",
                preferenceManager.getString(Constants.KEY_FIRST_NAME),
                preferenceManager.getString(Constants.KEY_LAST_NAME)
        ));

        findViewById(R.id.textSignOut).setOnClickListener(v -> signOut());

        EditText search_users = findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Update the fcm token in their database when a user signs-in
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null) {
                sendFCMTokenToDatabase(task.getResult());
            }
        });

        textErrorMessage = findViewById(R.id.textErrorMessage);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::getUsers);

        setUserAdapter();
        getUsers();
    }

    private void setUserAdapter() {
        usersAdapter = new UsersAdapter(users, this);
        RecyclerView usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setAdapter(usersAdapter);
    }

    // Search users with their name or email
    private void searchUsers(String search_string) {
        List<Users> filteredUsers = new ArrayList<>();
        for (Users user : users) {
            String user_content = user.firstName.toLowerCase() + user.lastName.toLowerCase() + user.email.toLowerCase();
            if(user_content.contains(search_string)) {
                filteredUsers.add(user);
            }
        }

        usersAdapter.updateUsers(filteredUsers);
        usersAdapter.notifyDataSetChanged();
    }

    // Sending fcm token to database
    private void sendFCMTokenToDatabase(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> Toast.makeText(
                        MainActivity.this, "Unable to send token: " + e.getMessage(), Toast.LENGTH_SHORT)
                        .show());
        documentReference.update(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
    }

    // Get the details of all the users signed up in the app and update the main activity layout
    private void getUsers() {
        swipeRefreshLayout.setRefreshing(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult() != null)
                    {
                        users.clear();
                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            if(myUserId.equals(documentSnapshot.getId())) {
                                continue;
                            }
                            Users user = new Users();
                            user.firstName = documentSnapshot.getString(Constants.KEY_FIRST_NAME);
                            user.lastName = documentSnapshot.getString(Constants.KEY_LAST_NAME);
                            user.email = documentSnapshot.getString(Constants.KEY_EMAIL);
                            user.token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = documentSnapshot.getString(Constants.KEY_USER_ID);
                            users.add(user);
                        }
                        if(users.size() > 0){
                            usersAdapter.notifyDataSetChanged();
                        } else {
                            textErrorMessage.setText(String.format("%s", "No users available"));
                            textErrorMessage.setVisibility(View.VISIBLE);
                        }
                    } else {
                        textErrorMessage.setText(String.format("%s", "No users available"));
                        textErrorMessage.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void signOut() {
        // Sign out for user signed in with Google
        AuthenticationManager.singOutG(this);
        Toast.makeText(this, "Signing Out...", Toast.LENGTH_SHORT).show();
        // Sign out for users signed in with email and password
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        // deleting the fcm-token from their database when a user signs out
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clearPreferences();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Unable to sign out", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void initiateVideoMeeting(Users user) {
        if(user.token == null || user.token.isEmpty()) {
            Toast.makeText(
                    this,
                    user.firstName + " " + user.lastName + " is not available for meeting",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "video");
            startActivity(intent);
        }
    }

    @Override
    public void initiateAudioMeeting(Users user) {
        if(user.token == null || user.token.isEmpty()) {
            Toast.makeText(
                    this,
                    user.firstName + " " + user.lastName + " is not available for meeting",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "audio");
            startActivity(intent);
        }
    }

    @Override
    public void initiateChatMessage(Users user) {
        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("userid", user.id);
        startActivity(intent);
    }

    // Select multiple users for the call
    @Override
    public void onMultipleUsersAction(Boolean isMultipleUsersSelected) {
        if(isMultipleUsersSelected) {
            imageConference.setVisibility(View.VISIBLE);
            imageConference.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
                intent.putExtra("selectedUsers", new Gson().toJson(usersAdapter.getSelectedUsers()));
                intent.putExtra("type", "video");
                intent.putExtra("isMultiple", true);
                startActivity(intent);
            });
        } else {
            imageConference.setVisibility(View.GONE);
        }
    }
}