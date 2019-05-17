package com.Forage.Forage;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.facebook.Profile;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

import static com.Forage.Forage.Constants.FB_ID;
import static com.Forage.Forage.Constants.FB_NAME;
import static com.Forage.Forage.Constants.FB_PHOTO;
import static com.Forage.Forage.Constants.VIEWS;

public class MainActivity extends AppCompatActivity{
    private static String UserUUID = null;
    private String TAG = "Main Activity";

    private BottomNavigationView bottomNavigationView;
    private  FirebaseFirestore fb = FirebaseFirestore.getInstance();
    public static final String CHANNEL_ID = "FORAGE";



    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragmentselect  = null;
            Log.d(TAG, "onNavigationItemSelected: ");

            switch(menuItem.getItemId()){
                case R.id.action_scanner:

                    fragmentselect = new scanner_Fragment();
                    Log.d(TAG, "onNavigationItemSelected: Scanner" );
                    break;
                case R.id.action_profile:
                    Log.d(TAG, "onNavigationItemSelected: Profile" );
                    fragmentselect = new ProfileFragment();
                    break;
                case R.id.search_profile:
                    fragmentselect = new SearchFragment();
                    Log.d(TAG, "onNavigationItemSelected: Search" );

                    break;
            }
            if(fragmentselect != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentselect).commit();
                return true;
            }
            return false;

        }

    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.navView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);


        SharedPreferences load = PreferenceManager.getDefaultSharedPreferences(this);
        boolean setupBool = load.getBoolean("setup",true);
        if(setupBool){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new ProfileFragment()).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container , new SearchFragment()).commit();
        }
        SharedPreferences.Editor edit = load.edit();
        edit.putBoolean("setup",false);
        edit.apply();
        UserUUID = load.getString("UserUUID",null);
        HashMap<String, Integer> num = new HashMap<>();
        num.put(VIEWS, 0);
        fb.collection("users").document(UserUUID).set(num, SetOptions.merge());
        MobileAds.initialize(this, "ca-app-pub-7186375146103063~4181879592");
        createChannel();
        UserUUID = load.getString("UserUUID",null);
        String handle = load.getString("handle",null);
        Log.d(TAG, "SERVICE STARTED");
        HashMap<String, Object> data = new HashMap<>();
        Profile fbProfile = Profile.getCurrentProfile();
        String FBURI = fbProfile.getLinkUri().toString();
        String FBNAME = fbProfile.getName();
        String FBID = fbProfile.getId();
        String profilePhotoURI = fbProfile.getProfilePictureUri(400,400).toString();

        //data.put("FB URI", FBURI);
        data.put(FB_NAME, FBNAME);
        data.put(FB_ID, FBID);
        data.put(FB_PHOTO, profilePhotoURI);
        Log.d(TAG, "onCreate:" + FBID);
        fb.collection("users").document(UserUUID).set(data, SetOptions.merge());
        HashMap<String,String> identifier = new HashMap<>();
        identifier.put("UUID",UserUUID);
        if(handle != null){
            fb.collection("handles").document(handle).set(identifier);
            HashMap<String,String> handleHash = new HashMap<>();
            handleHash.put("handle",handle);
            fb.collection("users").document(UserUUID).set(handleHash,SetOptions.merge());
        }

    }

    private void createChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH
            );
            serviceChannel.setDescription("Forage Service");
            serviceChannel.enableLights(true);
            serviceChannel.setLightColor(Color.GREEN);
            serviceChannel.enableVibration(false);
            manager.createNotificationChannel(serviceChannel);

        }
    }

}

