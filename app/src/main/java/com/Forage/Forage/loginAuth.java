package com.Forage.Forage;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

public class loginAuth extends AppCompatActivity {



    private static final String EMAIL = "email";
    private static final String BIRTHDAY = "user_birthday";
    private static final String PROFILE = "public_profile";
    private static final String LINK = "user_link";
    public AccessToken appToken;

    private CallbackManager callbackManager;

    private static final String TAG = "LOGIN ACTIVITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(Profile.getCurrentProfile() != null){
            startMainActivity();
        }
        setContentView(R.layout.activity_login_auth);

        //FacebookSdk.sdkInitialize(getApplication());
        AppEventsLogger.activateApp(getApplication());

        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL, PROFILE));

        callbackManager = CallbackManager.Factory.create();
        if (appToken != null && !appToken.isExpired()) {
            startMainActivity();

        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                appToken = AccessToken.getCurrentAccessToken();

                if (appToken != null && !appToken.isExpired()) {
                    startMainActivity();

                }
                    GraphRequest request = GraphRequest.newMeRequest(appToken, new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {

                        }
                    });
                    Bundle profiles = new Bundle();
                    profiles.putString("fields", "id,name");
                    request.setParameters(profiles);
                    request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void startMainActivity() {
        Intent startMainActivity = new Intent(loginAuth.this, MainActivity.class);
        startActivity(startMainActivity);
        finish();
    }
}