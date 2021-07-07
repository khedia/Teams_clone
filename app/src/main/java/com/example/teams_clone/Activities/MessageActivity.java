package com.example.teams_clone.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    private String fUser;
    DatabaseReference reference;

    private String userid;
    private TextView textUserName;
    private ImageButton btn_send;
    private EditText text_send;

    ApiService apiService;

    boolean notify = false;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

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

        findViewById(R.id.imageBack).setOnClickListener(v -> onBackPressed());

        textUserName = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        userid = getIntent().getStringExtra("userid");
        fUser = preferenceManager.getString(Constants.KEY_USER_ID);

        Users user = (Users) getIntent().getSerializableExtra("user");
        if(user != null) {
            textUserName.setText(String.format("%s %s", user.firstName, user.lastName));
        }

        btn_send.setOnClickListener(view -> {
            notify = true;
            String msg = text_send.getText().toString();
            if (!msg.equals("")){
                sendMessage(fUser, userid, msg); // todo - you are setting hi somewhere here
            } else {
                Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
            }
            text_send.setText("");
        });

        reference = FirebaseDatabase.getInstance().getReference();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                if (user != null) {
                    textUserName.setText(user.firstName);
                } else{
                    Log.e("TAG", "onDataChange: Null userid" );
                }

                readMessages(fUser, userid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        databaseReference.child("Chats").push().setValue(hashMap);

        // add user to chat fragment
//        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
//                .child(fuser)
//                .child(userid);
//
//        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists()){
//                    chatRef.child("id").setValue(userid);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
//                .child(userid)
//                .child(fuser);
//        chatRefReceiver.child("id").setValue(fuser);

//        final String msg = message;

//        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Users user = dataSnapshot.getValue(Users.class);
//                if (notify) {
//                    sendNotifiaction(receiver, user.firstName, msg);
//                }
//                notify = false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void readMessages(String myid, String userid){
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat != null && (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid))){
                        mChat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}