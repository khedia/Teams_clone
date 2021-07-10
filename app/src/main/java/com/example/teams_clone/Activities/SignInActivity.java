package com.example.teams_clone.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teams_clone.R;
import com.example.teams_clone.managers.AuthenticationManager;
import com.example.teams_clone.utilities.Constants;
import com.example.teams_clone.utilities.PreferenceManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import static co.apptailor.googlesignin.RNGoogleSigninModule.RC_SIGN_IN;

public class SignInActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private MaterialButton buttonSignIn;
    private ProgressBar signInProgressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        preferenceManager = new PreferenceManager(getApplicationContext());

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        findViewById(R.id.textSignUp).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        signInProgressBar = findViewById(R.id.signInProgressBar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        Button signInButton = findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(v -> AuthenticationManager.signInG(this));

        buttonSignIn.setOnClickListener(v -> {
            if (inputEmail.getText().toString().trim().isEmpty()) {
                Toast.makeText(SignInActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
                Toast.makeText(SignInActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
            } else if (inputPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(SignInActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
            } else {
                signIn();
            }
        });
    }

    private void signIn() {

        buttonSignIn.setVisibility(View.INVISIBLE);
        signInProgressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_FIRST_NAME, documentSnapshot.getString(Constants.KEY_FIRST_NAME));
                        preferenceManager.putString(Constants.KEY_LAST_NAME, documentSnapshot.getString(Constants.KEY_LAST_NAME));
                        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        signInProgressBar.setVisibility(View.INVISIBLE);
                        buttonSignIn.setVisibility(View.VISIBLE);
                        Toast.makeText(SignInActivity.this, "Unable to Sign In", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                if (account != null) {
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    database.collection(Constants.KEY_COLLECTION_USERS)
                            .whereEqualTo(Constants.KEY_EMAIL, account.getEmail())
                            .whereEqualTo(Constants.KEY_FIRST_NAME, account.getGivenName())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                                    Log.w("not", "google");
                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                    preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                                    preferenceManager.putString(Constants.KEY_FIRST_NAME, account.getGivenName());
                                    preferenceManager.putString(Constants.KEY_LAST_NAME, account.getFamilyName());
                                    preferenceManager.putString(Constants.KEY_EMAIL, account.getEmail());
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    Log.w("new", "google");
                                    HashMap<String, Object> user = new HashMap<>();
                                    user.put(Constants.KEY_FIRST_NAME, account.getGivenName());
                                    user.put(Constants.KEY_LAST_NAME, account.getFamilyName());
                                    user.put(Constants.KEY_EMAIL, account.getEmail());

                                    database.collection(Constants.KEY_COLLECTION_USERS)
                                            .add(user)
                                            .addOnSuccessListener(documentReference -> {
                                                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                                preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                                                preferenceManager.putString(Constants.KEY_FIRST_NAME, account.getGivenName());
                                                preferenceManager.putString(Constants.KEY_LAST_NAME, account.getFamilyName());
                                                preferenceManager.putString(Constants.KEY_EMAIL, account.getEmail());
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            })
                                            .addOnFailureListener(e -> {
                                                signInProgressBar.setVisibility(View.INVISIBLE);
                                                buttonSignIn.setVisibility(View.VISIBLE);
                                                Toast.makeText(SignInActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                }
                            });

                } else {
                    signInProgressBar.setVisibility(View.INVISIBLE);
                    buttonSignIn.setVisibility(View.VISIBLE);
                    Toast.makeText(SignInActivity.this, "Unable to Sign In", Toast.LENGTH_SHORT).show();
                }

        } catch(ApiException e) {
                Log.d("Unable to sign in: ", e.getMessage());
                Toast.makeText(SignInActivity.this, "Unable to Sign In " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}