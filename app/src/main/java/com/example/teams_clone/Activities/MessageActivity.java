package com.example.teams_clone.Activities;

import android.os.Bundle;

import android.util.Log;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teams_clone.R;
import com.example.teams_clone.adapters.MessageAdapter;
import com.example.teams_clone.models.Chat;
import com.example.teams_clone.models.Users;
import com.example.teams_clone.network.ApiClient;
import com.example.teams_clone.network.ApiService;
import com.example.teams_clone.utilities.Constants;
import com.example.teams_clone.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    private String fUser;

    private DatabaseReference reference;

    private String userid;
    private ImageButton btn_send;
    private EditText text_send;

    ApiService apiService;

    boolean notify = false;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        apiService = ApiClient.getClient().create(ApiService.class);

        preferenceManager = new PreferenceManager(getApplicationContext());

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Users user = (Users) getIntent().getSerializableExtra("user");
        if(user != null) {
            TextView textUserName = findViewById(R.id.username);
            textUserName.setText(String.format(
                    "%s %s",
                    user.firstName,
                    user.lastName
            ));
        }

        findViewById(R.id.imageBack).setOnClickListener(v -> onBackPressed());

        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        userid = getIntent().getStringExtra("userid");
        fUser = preferenceManager.getString(Constants.KEY_USER_ID);

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");

        btn_send.setOnClickListener(view -> {
            notify = true;
            String msg = text_send.getText().toString();
//            Date date = new Date();
            String currentTime = simpleDateFormat.format(calendar.getTime());
            if (!msg.equals("")){
                sendMessage(fUser, userid, msg, currentTime);
            } else {
                Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
            }
            text_send.setText("");
        });

        reference = FirebaseDatabase.getInstance().getReference();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                readMessages(fUser, userid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        seenMessage(userid);
    }

    private void seenMessage(final String rId){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null && chat.getReceiver().equals(fUser) && chat.getSender().equals(rId)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    } else {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", false);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message, String time) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("time", time);

        databaseReference.child("Chats").push().setValue(hashMap);
    }

    private void readMessages(String myId, String userId){
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null && (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(myId))){
                        mChat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, preferenceManager);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}