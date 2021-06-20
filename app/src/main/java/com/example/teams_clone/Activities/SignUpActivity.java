package com.example.teams_clone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.teams_clone.R;
import com.example.teams_clone.utilities.Constants;
import com.example.teams_clone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private EditText inputFirstName, inputLastName, inputEmail, inputPassword, inputConfirmPassword;
    private MaterialButton buttonSignUp;
    private ProgressBar signUpProgressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findViewById(R.id.imageBack).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.textSignIn).setOnClickListener(v -> onBackPressed());

        preferenceManager = new PreferenceManager(getApplicationContext());

        inputFirstName = findViewById(R.id.inputFirstName);
        inputLastName = findViewById(R.id.inputLastName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        signUpProgressBar = findViewById(R.id.signUpProgressBar);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputFirstName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Enter first name", Toast.LENGTH_SHORT).show();
                } else if(inputLastName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Enter last name", Toast.LENGTH_SHORT).show();
                } else if(inputEmail.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
                    Toast.makeText(SignUpActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
                } else if(inputPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                } else if(inputConfirmPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Confirm your password", Toast.LENGTH_SHORT).show();
                } else if(!inputPassword.getText().toString().equals(inputConfirmPassword.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "Password and Confirm password must be same", Toast.LENGTH_SHORT).show();
                } else {
                    signUp();
                }
            }
        });
    }

    private void signUp()
    {
        buttonSignUp.setVisibility(View.INVISIBLE);
        signUpProgressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_FIRST_NAME, inputFirstName.getText().toString());
        user.put(Constants.KEY_LAST_NAME, inputLastName.getText().toString());
        user.put(Constants.KEY_EMAIL, inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, inputPassword.getText().toString());

        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                        preferenceManager.putString(Constants.KEY_FIRST_NAME, inputFirstName.getText().toString());
                        preferenceManager.putString(Constants.KEY_LAST_NAME, inputLastName.getText().toString());
                        preferenceManager.putString(Constants.KEY_EMAIL, inputEmail.getText().toString());
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signUpProgressBar.setVisibility(View.INVISIBLE);
                        buttonSignUp.setVisibility(View.VISIBLE);
                        Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}