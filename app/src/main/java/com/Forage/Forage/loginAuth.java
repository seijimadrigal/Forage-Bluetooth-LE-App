package com.Forage.Forage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String handle = sharedPreferences.getString("handle",null);
        if(Profile.getCurrentProfile() != null){

            Log.d(TAG, "Handl: " + handle);
            if(handle == null){
                startEnterHandleActivity();

            }
            else {
                if(handle.length() < 1){
                    startEnterHandleActivity();
                }
                else {
                    startMainActivity();
                }
            }
        }
        setContentView(R.layout.activity_login_auth);
        AppEventsLogger.activateApp(getApplication());

        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL, PROFILE));


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.Forage.Forage",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


        callbackManager = CallbackManager.Factory.create();
        if (appToken != null && !appToken.isExpired()) {
            startMainActivity();

        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                appToken = AccessToken.getCurrentAccessToken();

                if (appToken != null && !appToken.isExpired()) {
                    Log.d(TAG, "Handl: " + handle);
                    if(handle == null){
                        startEnterHandleActivity();

                    }
                    else {
                        if(handle.length() < 1){
                            startEnterHandleActivity();
                        }
                        else {
                            startMainActivity();
                        }
                    }


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
    public void startEnterHandleActivity() {
        Intent startEnterHandleActivity = new Intent(loginAuth.this, EnterHandles.class);
        startActivity(startEnterHandleActivity);
        finish();
    }

}