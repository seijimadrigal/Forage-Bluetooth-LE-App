package com.Forage.Forage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Settings extends AppCompatActivity {
    private final String TAG = "SETTINGS";
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private EditText scannerTimeText;
    private ImageView doneImageView;
    private ProgressBar progBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String UUID = sharedPreferences.getString("UserUUID", null);
        Button loginButton = findViewById(R.id.deleteaccountBtn);
        scannerTimeText = findViewById(R.id.scannerTime);
        doneImageView = findViewById(R.id.doneSettings);
        progBar = findViewById(R.id.progressCircleSettings);
        progBar.setVisibility(View.INVISIBLE);

        SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(this);
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
                    SharedPreferences sharedpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
                Intent goToLoginActivity = new Intent(getApplicationContext(), loginAuth.class);
                startActivity(goToLoginActivity);
                finish();


                fb.collection("users").document(UUID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isComplete()){
                            String handle = task.getResult().get("handle").toString();
                                fb.collection("handles").document(handle).delete();
                               fb.collection("users").document(UUID).delete();
                        }
                    }
                });


            }
        });
    }
}
