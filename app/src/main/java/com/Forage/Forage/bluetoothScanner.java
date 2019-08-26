package com.Forage.Forage;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.facebook.Profile;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.UUID;

import static com.Forage.Forage.Constants.*;


public class bluetoothScanner extends Service {


    private BluetoothManager manager;
    private BluetoothAdapter adapter;
    private BluetoothLeScanner leScanner;
    private BluetoothLeAdvertiser advertiser;
    private AdvertiseSettings settings;
    private  FirebaseFirestore fb = FirebaseFirestore.getInstance();

    private String TAG = "Service Activity";
    private String UserUUID;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        advertiser.stopAdvertising(advertiseCallback);
        super.onDestroy();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
        }
        SharedPreferences load = PreferenceManager.getDefaultSharedPreferences(this);
        UserUUID = load.getString("UserUUID",null);
        Intent persistenNotifIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,persistenNotifIntent,0);

        Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.d(TAG, "Notification Builder Success");
            notification = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                    .setContentTitle("Forage")
                    .setContentText("Discovery Mode On")
                    .setSmallIcon(R.drawable.servicelogo)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentIntent(pendingIntent)
                    .setChannelId("FORAGE")
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
        }
        else {
            notification = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                    .setContentTitle("Forage")
                    .setContentText("Discovery Mode On")
                    .setSmallIcon(R.drawable.servicelogo)
                    .setContentIntent(pendingIntent)
                    .build();
        }

        startForeground(45353, notification);






        manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        adapter = manager.getAdapter();
        leScanner = adapter.getBluetoothLeScanner();
        advertiser = adapter.getBluetoothLeAdvertiser();
        ParcelUuid parcelUuid = new ParcelUuid(UUID.fromString(UserUUID));

        //ADVERTISEMENT//
        settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                .setConnectable(false)
                .build();

        AdvertiseData btDataAd = new AdvertiseData.Builder()
                .addServiceUuid(parcelUuid)
                .build();
        advertiser.startAdvertising(settings, btDataAd,advertiseCallback);

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
     AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.d(TAG, "Advertisement Success");
            super.onStartSuccess(settingsInEffect);

        }
         @Override
         public void onStartFailure(int errorCode) {
             Log.d(TAG, "Advertisement Failure");
             super.onStartFailure(errorCode);
         }
     };




}
