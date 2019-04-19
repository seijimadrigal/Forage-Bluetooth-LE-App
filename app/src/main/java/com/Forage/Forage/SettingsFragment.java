package com.Forage.Forage;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Share;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class SettingsFragment extends Fragment {


    private final String TAG = "SETTINGS FRAGMENT";
    private View settingFragmentView;
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private EditText scannerTimeText;
    private ImageView doneImageView;
    private ProgressBar progBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        settingFragmentView =inflater.inflate(R.layout.fragment_settings, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String UUID = sharedPreferences.getString("UserUUID", null);
        Button loginButton = settingFragmentView.findViewById(R.id.deleteaccountBtn);
        scannerTimeText = settingFragmentView.findViewById(R.id.scannerTime);
        doneImageView = settingFragmentView.findViewById(R.id.doneSettings);
        progBar = settingFragmentView.findViewById(R.id.progressCircleSettings);
        progBar.setVisibility(View.INVISIBLE);

        SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(getContext());
        int newScantime = sharedpref.getInt("SCANTIME",0);
        if(newScantime > 0 ){
            scannerTimeText.setText(String.valueOf(newScantime));
        }
        else {
            scannerTimeText.setText(String.valueOf(10000));
        }



        doneImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progBar.setVisibility(View.VISIBLE);
                if(scannerTimeText != null) {
                    Log.d(TAG, "SCANNER TIME: " + Integer.parseInt(scannerTimeText.getText().toString()));
                    SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sharedpref.edit();
                    editor.putInt("SCANTIME", Integer.parseInt(scannerTimeText.getText().toString()));
                    editor.apply();

                }
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progBar.setVisibility(View.INVISIBLE);
                    }
                },2000);


            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Intent goToLoginActivity = new Intent(getContext(), loginAuth.class);
                startActivity(goToLoginActivity);
                getActivity().finish();
                fb.collection("users").document(UUID).delete();

            }
        });

        return settingFragmentView;
    }

}
