package com.polyrides.polyridesv2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private static final String EMAIL = "email";
    private Activity here;
    private DatabaseReference usersReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        here = this;

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .build(),
                RC_SIGN_IN);

        usersReference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {

                usersReference = usersReference.child("users");

                Map<String, Object> userData = new HashMap<>();
                userData.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                userData.put("name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                userData.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                userData.put("photo", FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
                userData.put("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

                Map<String, Object> endpoints = new HashMap<>();
                endpoints.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), userData);
                usersReference.updateChildren(endpoints);

                // Successfully signed in
                startActivity(new Intent(this, AppMain.class));
                // ...
            } else {
                Toast toast = new Toast(this);
                toast.setText(R.string.login_failed_text);
                toast.show();
            }
        }
    }
}
