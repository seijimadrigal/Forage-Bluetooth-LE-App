package com.Forage.Forage;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

import static com.Forage.Forage.Constants.BIO_ID;
import static com.Forage.Forage.Constants.BIO_NO;
import static com.Forage.Forage.Constants.FB_ID;
import static com.Forage.Forage.Constants.FB_NO;
import static com.Forage.Forage.Constants.FB_URI;
import static com.Forage.Forage.Constants.INSTA_ID;
import static com.Forage.Forage.Constants.INSTA_NO;
import static com.Forage.Forage.Constants.LINK_NO;
import static com.Forage.Forage.Constants.SEND_LINK;
import static com.Forage.Forage.Constants.SNAPCHAT_NO;
import static com.Forage.Forage.Constants.SNAP_ID;
import static com.Forage.Forage.Constants.TWITTER_ID;
import static com.Forage.Forage.Constants.TWITTER_NO;

public class EditProfileActivity extends AppCompatActivity {
    private HashMap<String,String> fireStorePutHash;
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private CollectionReference collection = fb.collection("users");
    private DocumentReference userDetailsRef;
    private EditText profileEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.edit_toolbar);
        profileEdit = findViewById(R.id.edit_profile_edit);
        setSupportActionBar(toolbar);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String UUID = sharedPreferences.getString("UserUUID", null);
        final String handle = sharedPreferences.getString("handle",null);
        fireStorePutHash = new HashMap<>();

        userDetailsRef = collection.document(UUID);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final int type = getIntent().getIntExtra("edit",0);

        switch (type){
            case BIO_NO:
                toolbar.setTitle(R.string.edit_bio);
                break;

            case FB_NO:
                toolbar.setTitle(R.string.edit_fb);
                break;
            case SNAPCHAT_NO:
                toolbar.setTitle(R.string.edit_snapchat);
                break;

            case TWITTER_NO:
                toolbar.setTitle(R.string.edit_twitter);
                break;
            case INSTA_NO:
                toolbar.setTitle(R.string.edit_insta);
                break;

            case LINK_NO:
                toolbar.setTitle(R.string.edit_link);
                break;

                default:
                    toolbar.setTitle(R.string.edit);
                    break;
          }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAccountInfo(type);
            }
        });

    }

    private void sendAccountInfo(int type){
        switch (type){
            case BIO_NO:
                updateValue(BIO_ID);
                break;
            case FB_NO:
                updateValue(FB_ID);
                break;
            case SNAPCHAT_NO:
                updateValue(SNAP_ID);
                break;
            case TWITTER_NO:
                updateValue(TWITTER_ID);
                break;
            case INSTA_NO:
                updateValue(INSTA_ID);
                break;
            case LINK_NO:
                updateValue(SEND_LINK);
                break;
        }

    }

    private void updateValue(String ID){
        if(profileEdit.getText() != null){
            String newTxt  = profileEdit.getText().toString();
                fireStorePutHash.put(ID, newTxt);
                  userDetailsRef.set(fireStorePutHash, SetOptions.merge());
                finish();
                }

    }

}
