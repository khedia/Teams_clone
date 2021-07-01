package com.example.teams_clone.managers;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teams_clone.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.concurrent.Executor;

import static co.apptailor.googlesignin.RNGoogleSigninModule.RC_SIGN_IN;

public class AuthenticationManager {
    private static GoogleSignInClient mGoogleSignInClient;

    public AuthenticationManager(AppCompatActivity activity){
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        //        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //        if(account != null) {
        //            goMainScreen();
        //        }

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public static Intent getSignIntent(){
        return mGoogleSignInClient.getSignInIntent();
    }

    public static void signInG(AppCompatActivity activity) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public static void singOutG(AppCompatActivity activity) {
        mGoogleSignInClient.signOut().addOnCompleteListener(activity, task -> {
            // something here - TODO
        });
    }

}
