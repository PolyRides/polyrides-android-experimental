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
import com.google.firebase.iid.FirebaseInstanceId;

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



        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            Intent intent = new Intent(this, AppMain.class);
            if (getIntent().hasExtra("requestUid")) {
                intent.putExtra("requestUid", getIntent().getExtras().getString("requestUid"));
            }
            else if (getIntent().hasExtra("offerUid")) {
                intent.putExtra("offerUid", getIntent().getExtras().getString("offerUid"));
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else {

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


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {

                usersReference = usersReference.child("Profile");

                String firstName;
                String lastName = "";
                if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName().split(" ").length != 2) {
                    firstName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                }
                else {
                    firstName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().split(" ")[0];
                    lastName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().split(" ")[1];
                }

                Map<String, Object> userData = new HashMap<>();
                userData.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                userData.put("firstName", firstName);
                userData.put("lastName", lastName);
                userData.put("emailAddress", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                userData.put("photo", FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
                userData.put("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                userData.put("profileDescription", "");
                userData.put("deviceToken", FirebaseInstanceId.getInstance().getToken());
                userData.put("rating", 0);

                Map<String, Object> endpoints = new HashMap<>();
                endpoints.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), userData);
                usersReference.updateChildren(endpoints);

                // Successfully signed in
                Intent intent = new Intent(this, AppMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } else {
                Toast toast = new Toast(this);
                toast.setText(R.string.login_failed_text);
                toast.show();
            }
        }
    }
}
