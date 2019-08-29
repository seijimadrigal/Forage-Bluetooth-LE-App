package com.Forage.Forage;

import static com.Forage.Forage.Constants.*;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


public class scanner_Fragment extends Fragment {
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private BluetoothLeScanner btLe;
    private LocationManager locationManager;
    private static final String TAG = "SCANNER FRAGMENT";

    private boolean bleScanning = false;
    private int REQUEST_ENABLE_BT = 1;

    private int accounts;

    private Switch switch1;
    private Button ScannerBtn;
    private ImageView foundProfileView;
    private ImageView foundProfileView2;
    private ImageView foundProfileView3;
    private ImageView foundProfileView4;
    private ImageView foundProfileView5;
    private ImageView fbPhotoView;
    private ProgressBar scannerProg;
    private static final int SCAN_TIME_DEFAULT = 10000;
    private int scantime = 0;
    private boolean enableBt;

    private ArrayList<String> newProfileName = new ArrayList<>();
    private ArrayList<Uri> newFBPhoto  = new ArrayList<>();
    private ArrayList<String>newProfileLink  =  new ArrayList<>();
    private ArrayList<String>newProfileID  = new ArrayList<>();
    private ArrayList<String>newSnapID  = new ArrayList<>();
    private ArrayList<String> newProfileInstaID = new ArrayList<>();
    private ArrayList<String>newProfilePhotoURI  = new ArrayList<>();
    private ArrayList<String>newBioID  = new ArrayList<>();
    private ArrayList<String>newTwitterID = new ArrayList<>();
    private ArrayList<String> newCustomLINK = new ArrayList<>();
    private ArrayList<String> UUIDs = new ArrayList<>();
    private ArrayList<Integer> ViewCount = new ArrayList<>();


    private View layout;




    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        switch1.setChecked(false);
                        if (btAdapter == null || !btAdapter.isEnabled()) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        }

                        break;
                    case BluetoothAdapter.STATE_ON:
                        switch1.setChecked(true);

                }
            }
        }
    };


    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private CollectionReference UUIDREF  = fb.collection("users");
    private HashMap<String, Map<String, Object>> accountsHash = new HashMap<String, Map<String, Object>>();


    public scanner_Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountsHash = new HashMap<>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
         scantime = sharedPreferences.getInt("SCANTIME", 0);

        // BLUETOOTH STATE LISTENER
         IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(bluetoothReceiver,filter);

        clearArrayLists();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        //PERMISSIONS//

        int COARSE_LOCATION_PERMISSION = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if(COARSE_LOCATION_PERMISSION == PackageManager.PERMISSION_DENIED ) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        //  if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
        //    askForGPS();
        //}
        btManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        if (btAdapter == null || !btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        btLe = btAdapter.getBluetoothLeScanner();


    }

    private void leScan(final boolean enabled) {

        if (enabled && enableBt) {
            Log.d(TAG, "Starting Scanning");
            accounts = 0;
            clearArrayLists();
            ScannerBtn.setVisibility(View.INVISIBLE);
            scannerProg.setVisibility(View.VISIBLE);
            Handler mHandler = new Handler();

            if(scantime > 0 ) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btLe.stopScan(leScanCallback);
                        scannerProg.setVisibility(View.INVISIBLE);
                        ScannerBtn.setVisibility(View.VISIBLE);
                        bleScanning = false;
                        getProfiles();
                    }
                }, scantime);

            }
            else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btLe.stopScan(leScanCallback);
                        scannerProg.setVisibility(View.INVISIBLE);
                        ScannerBtn.setVisibility(View.VISIBLE);
                        bleScanning = false;
                        getProfiles();
                    }
                }, SCAN_TIME_DEFAULT);
            }

            btLe.startScan(leScanCallback);
            bleScanning = true;
        }
        else if(!enableBt && enabled){
            btLe.stopScan(leScanCallback);
            bleScanning = false;

            Toast.makeText(getActivity(), "Enable Discovery", Toast.LENGTH_LONG).show();
        } else {
            btLe.stopScan(leScanCallback);
            scannerProg.setVisibility(View.INVISIBLE);
            bleScanning = false;
        }

    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            BluetoothDevice device = result.getDevice();
            ScanRecord records = result.getScanRecord();
            int RSSI = result.getRssi();
            bluetoothDeviceList(device, RSSI, records);

        }

    };

    private void bluetoothDeviceList(BluetoothDevice device , final int RSSI, ScanRecord record){
        final List btList  = record.getServiceUuids();

        if(btList!= null) {
            for (int i = 0; i < btList.size(); i++) {
                Log.d(TAG, "bluetoothDeviceList: " + btList.get(i).toString());
                DocumentReference docRef = UUIDREF.document(btList.get(i).toString());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documents = task.getResult();
                        if (task.isSuccessful()) {
                            if (documents.exists()) {
                                if (!accountsHash.containsKey(documents.getId())) {

                                    ViewCount.add(documents.getLong(VIEWS).intValue());
                                    accountsHash.put(documents.getId(), documents.getData());
                                    //accountFound(documents.getId());

                                }
                            } else {
                                Log.d(TAG, "DOCUMENT IS NOT FOUND");
                            }
                        }
                    }
                });
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onRequestPermissionsResult: Permission Granted");
        }
    }


    private void startProfileViewActivity(int AccountNum){
        Log.d(TAG, "startProfileViewActivity: " + AccountNum + " ACCOUNT NAME: "+ newProfileName.get(AccountNum) + " INSTAID: " + newProfileInstaID.get(AccountNum) );
        Intent startProfileView = new Intent(getContext(), NewProfileView.class);
        startProfileView

                .putExtra(FB_PHOTO, newProfilePhotoURI.get(AccountNum))
                .putExtra(FB_NAME, newProfileName.get(AccountNum));
        try {
            startProfileView.putExtra(BIO_ID, newBioID.get(AccountNum))
            .putExtra(SNAP_ID, newSnapID.get(AccountNum))
                    .putExtra(UUID_FS, UUIDs.get(AccountNum))
            .putExtra(TWITTER_ID, newTwitterID.get(AccountNum))
            .putExtra(INSTA_ID, newProfileInstaID.get(AccountNum))
            .putExtra(SEND_LINK, newCustomLINK.get(AccountNum));

            startProfileView .putExtra(FB_LINK, newProfileLink.get(AccountNum));
        }catch (IndexOutOfBoundsException io){
            Log.d(TAG, "Accoutns NUll");
        }

        startActivity(startProfileView);
    }

    private void getprofilePicture() {
        Uri uri = Profile.getCurrentProfile().getProfilePictureUri(400, 400);
        Glide.with(layout).load(uri).apply(RequestOptions.circleCropTransform()).into(fbPhotoView);

    }

    private void getProfiles(){
        Iterator it = accountsHash.entrySet().iterator();

        while(it.hasNext()){
            HashMap.Entry pair = (HashMap.Entry) it.next();
            Log.d(TAG, "getProfiles: " + pair.getKey() + " = "+ pair.getValue());
            accountFound(pair.getKey().toString());
            it.remove();

        }



    }

    private void accountFound(String key) {
        Log.d(TAG, "Account Number: \t" + accounts);
        UUIDs.add(accounts,key);
        Map Map = accountsHash.get(key);
        Object fbName = Map.get(FB_NAME);
        Object fbURL = Map.get(FB_URI);
        Object fbPhoto = Map.get(FB_PHOTO);
        Object fbID = Map.get(FB_ID);
        Object instaID = Map.get(INSTA_ID);
        Object bioIDOBJ = Map.get(BIO_ID);
        Object twitterID = Map.get(TWITTER_ID);
        Object customLink = Map.get(SEND_LINK);
        Object snapID = Map.get(SNAP_ID);

        Uri fbPhotoURI = Uri.parse(fbPhoto.toString());


        try {

            newProfilePhotoURI.add(accounts,fbPhotoURI.toString());
            newProfileName.add(accounts,fbName.toString());
            newProfileID.add(accounts,fbID.toString());

            if(fbURL == null) {
                newProfileLink.add(accounts, "");
            } else {
                newProfileLink.add(accounts, fbURL.toString());
            }
            if(instaID == null) {
                newProfileInstaID.add(accounts, "");
            } else{
                newProfileInstaID.add(accounts, instaID.toString());
            }
            if (bioIDOBJ == null){
                newBioID.add(accounts,"");
            }else{
                newBioID.add(accounts,bioIDOBJ.toString());
            }
            if(twitterID == null){
                newTwitterID.add(accounts,"");
            } else{
                newTwitterID.add(accounts,twitterID.toString());
            }
            if(customLink == null){
                newCustomLINK.add(accounts,"");
            } else{
                newCustomLINK.add(accounts,customLink.toString());
            }
            if(snapID == null){
                newSnapID.add(accounts,"");
            }
            else {
                newSnapID.add(accounts,snapID.toString());
            }


        } catch (NullPointerException nfe) {
            Log.d(TAG, "Some information could not be retrieved");

        }

        setImageView(accounts);
        accounts++;

        Log.d(TAG, "accountFound: " + fbName.toString());
    }

    private void setImageView(int accountNum){
        Log.d(TAG, "setImageView: " + accountNum);

            switch (accountNum) {
                case 0:
                    Log.d(TAG, "Image: 1 " + newProfilePhotoURI.get(accountNum));

                    try{
                        foundProfileView.setClickable(true);
                        Glide.with(layout).load(Uri.parse(newProfilePhotoURI.get(accountNum))).apply(RequestOptions.circleCropTransform()).into(foundProfileView);}
                    catch (Exception e){
                        break;
                    }
                    break;
                case 1:
                    Log.d(TAG, "Image: 2 " + newProfilePhotoURI.get(accountNum));
                    try {
                        foundProfileView2.setClickable(true);
                        Glide.with(layout).load(Uri.parse(newProfilePhotoURI.get(accountNum))).apply(RequestOptions.circleCropTransform()).into(foundProfileView2);
                    } catch (Exception e){
                        break;
                    }
                    break;
                case 2:
                    Log.d(TAG, "Image: 3 " + newProfilePhotoURI.get(accountNum));
                    try {
                        foundProfileView2.setClickable(true);
                        Glide.with(layout).load(Uri.parse(newProfilePhotoURI.get(accountNum))).apply(RequestOptions.circleCropTransform()).into(foundProfileView3);
                    break;}
                    catch (Exception e){
                        break;
                    }
                case 3:
                    Log.d(TAG, "Image: 4 " + newProfilePhotoURI.get(accountNum));
                    try {
                        foundProfileView4.setClickable(true);
                        Glide.with(layout).load(Uri.parse(newProfilePhotoURI.get(accountNum))).apply(RequestOptions.circleCropTransform()).into(foundProfileView4);
                        break;}
                    catch (Exception e){
                        break;
                    }
                case 4:
                    Log.d(TAG, "Image: 5 " + newProfilePhotoURI.get(accountNum));
                    try {
                        foundProfileView5.setClickable(true);
                        Glide.with(layout).load(Uri.parse(newProfilePhotoURI.get(accountNum))).apply(RequestOptions.circleCropTransform()).into(foundProfileView5);
                        break;}
                    catch (Exception e){
                        break;
                    }
                    default:
                        break;
            }
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_scanner_,container,false);
        switch1 = layout.findViewById(R.id.discoverability);
        foundProfileView = layout.findViewById(R.id.foundProfile);
        foundProfileView2 = layout.findViewById(R.id.foundProfile2);
        foundProfileView3 = layout.findViewById(R.id.foundProfile3);
        foundProfileView4 = layout.findViewById(R.id.foundProfile4);
        foundProfileView5 = layout.findViewById(R.id.foundProfile5);


        setnotClickable();

        fbPhotoView = layout.findViewById(R.id.fbProfile);
        scannerProg = layout.findViewById(R.id.scannerProg);
        ScannerBtn = layout.findViewById(R.id.scannerBtn);
        handleAnimation(fbPhotoView);
        scannerProg.setVisibility(View.INVISIBLE);

        // Gets Profile Photo of User

        getprofilePicture();

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && btAdapter.isEnabled()) {
                    enableBt = true;
                    Intent startBleService = new Intent(getActivity(), bluetoothScanner.class);
                    startBleService.putExtra("switch", enableBt);
                    handleAnimation(fbPhotoView);

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getActivity().startForegroundService(startBleService);
                    }
                    else{
                        getActivity().startService(startBleService);
                    }
                    Log.d(TAG, "DISCOVERABILITY ENABLED");
                }
                else
                {
                    enableBt = false;
                    Log.d(TAG, "DISCOVERABILITY DISABLED");
                    Intent stopBleService = new Intent(getActivity(), bluetoothScanner.class);
                    Toast.makeText(getContext(), "DISCOVERABILITY DISABLED",Toast.LENGTH_SHORT).show();
                    stopBleService.putExtra("switch", enableBt);
                    btLe.stopScan(leScanCallback);
                    switch1.setChecked(false);
                    fbPhotoView.clearAnimation();
                    getActivity().stopService(stopBleService);
                }
            }
        });

        switch1.setChecked(true);


        ScannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setnotClickable();
                if (btAdapter == null || !btAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }

                if(enableBt) {

                    bleScanning ^= true;
                    leScan(bleScanning);
                    foundProfileView.setImageDrawable(null);
                    foundProfileView2.setImageDrawable(null);
                    foundProfileView3.setImageDrawable(null);
                }
                else{
                    Toast.makeText(getContext(),"Please Turn On Discoverability",Toast.LENGTH_SHORT).show();
                }
            }
        });


        foundProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(foundProfileView.getDrawable() != null){
                    startProfileViewActivity(0);
                }

            }
        });
        foundProfileView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(foundProfileView2.getDrawable() != null){
                    startProfileViewActivity(1);
                }
            }
        });
        foundProfileView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(foundProfileView3.getDrawable() != null){
                    startProfileViewActivity(2);
                }
            }
        });
        foundProfileView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(foundProfileView4.getDrawable() != null){
                    startProfileViewActivity(3);
                }
            }
        });
        foundProfileView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(foundProfileView4.getDrawable() != null){
                    startProfileViewActivity(4);
                }
            }
        });
        return layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
            getActivity().unregisterReceiver(bluetoothReceiver);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");

        btLe.stopScan(leScanCallback);

    }

    private void clearArrayLists(){
        newProfileID.clear();
        newProfilePhotoURI.clear();
        newProfileName.clear();
        newFBPhoto.clear();
        newCustomLINK.clear();
        newTwitterID.clear();
        newProfileLink.clear();
        newBioID.clear();
        UUIDs.clear();
        newSnapID.clear();
        ViewCount.clear();
    }

    private void setnotClickable(){
        foundProfileView.setClickable(false);
        foundProfileView2.setClickable(false);
        foundProfileView3.setClickable(false);
        foundProfileView4.setClickable(false);
        foundProfileView5.setClickable(false);
    }

    private void handleAnimation(View view){

         Animation scaleup = AnimationUtils.loadAnimation(getContext(),R.anim.scaleup);
         Animation scaledown = AnimationUtils.loadAnimation(getContext(),R.anim.scaledown);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(scaleup);
        set.addAnimation(scaledown);

        view.startAnimation(set);

    }
}
