package com.Forage.Forage;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.UUID;

public class forage extends Application {
    String TAG = "FORAGE PERSISITENT";


    private static final String UserUUID = UUID.randomUUID().toString();
    @Override
    public void onCreate() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String UUID = sharedPreferences.getString("UserUUID", null);

        if(UUID == null) {
            SharedPreferences saveUUID = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = saveUUID.edit();
            editor.putString("UserUUID", UserUUID);
            editor.apply();
        }
        Log.d(TAG, "NOTIFICATION CHANNEL STARTED ");
        super.onCreate();
    }


}
