package com.example.teams_clone.managers;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teams_clone.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import static co.apptailor.googlesignin.RNGoogleSigninModule.RC_SIGN_IN;

public class AuthenticationManager {
    private static GoogleSignInClient getClient(AppCompatActivity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        return GoogleSignIn.getClient(activity, gso);
    }

    public static void signInG(AppCompatActivity activity) {
        Intent signInIntent = getClient(activity).getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public static void singOutG(AppCompatActivity activity) {
        getClient(activity).signOut().addOnCompleteListener(activity, task -> {
        });
    }

}
