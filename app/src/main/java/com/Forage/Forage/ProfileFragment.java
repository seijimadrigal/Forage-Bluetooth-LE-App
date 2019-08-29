package com.Forage.Forage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

import static com.Forage.Forage.Constants.*;

public class ProfileFragment extends Fragment {

    private static final String TAG = "SCANNER FRAGMENT";
    private ProgressBar progressCircle;

    private View profileView;

    private ImageView myProfileView, doneTick, settingBtn;
    private TextView instagramUsername, linkEdit, twitterEdit, bioEdit, facebookEdit, snapchatEdit;
    private Button instagramBtn, linkBtn, twitterBtn, bioBtn, facebookBtn, snapchatBtn;
    private TextView myNameText,viewCount, totalviewCount, myHandleText;

    private HashMap<String,String>fireStorePutHash;

    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private CollectionReference collection = fb.collection("users");
    private DocumentReference userDetailsRef;
    private Handler handler;
    private InputMethodManager inputMethodManager;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        profileView = inflater.inflate(R.layout.profile_fragment, container,false);
        myProfileView = profileView.findViewById(R.id.myProfileView);
        settingBtn = profileView.findViewById(R.id.settings_btn);

        instagramUsername = profileView.findViewById(R.id.instagramUserName);
        twitterEdit = profileView.findViewById(R.id.twitterEdit);
        linkEdit = profileView.findViewById(R.id.linkEditText);
        facebookEdit = profileView.findViewById(R.id.facebookEdit);
        snapchatEdit =  profileView.findViewById(R.id.snapchatUsername);
        bioEdit = profileView.findViewById(R.id.bioEdit);
        myNameText = profileView.findViewById(R.id.myName);
        myHandleText = profileView.findViewById(R.id.myHandle);

        bioBtn = profileView.findViewById(R.id.bioBtn);
        facebookBtn = profileView.findViewById(R.id.fbBtn);
        snapchatBtn = profileView.findViewById(R.id.snapchatBtn);
        twitterBtn = profileView.findViewById(R.id.twitterBtn);
        instagramBtn = profileView.findViewById(R.id.instaBtn);
        linkBtn = profileView.findViewById(R.id.linkBtn);

        final Intent startEditActivity = new Intent(getActivity(),EditProfileActivity.class);

        bioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditActivity.putExtra("edit", BIO_NO);
                startActivity(startEditActivity);
            }
        });
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditActivity.putExtra("edit",FB_NO);
                startActivity(startEditActivity);
            }
        });
        snapchatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditActivity.putExtra("edit",SNAPCHAT_NO);
                startActivity(startEditActivity);
            }
        });
        twitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditActivity.putExtra("edit",TWITTER_NO);
                startActivity(startEditActivity);
            }
        });
        instagramBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditActivity.putExtra("edit",INSTA_NO);
                startActivity(startEditActivity);
            }
        });
        linkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditActivity.putExtra("edit",LINK_NO);
                startActivity(startEditActivity);
            }
        });
        viewCount = profileView.findViewById(R.id.viewsCount);
        totalviewCount = profileView.findViewById(R.id.totalviewcount);
        progressCircle = profileView.findViewById(R.id.progressCircle);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);




        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String UUID = sharedPreferences.getString("UserUUID", null);
        final String handle = sharedPreferences.getString("handle",null);
        fireStorePutHash = new HashMap<>();

        userDetailsRef = collection.document(UUID);
        checkInfo();

        if(handle != null){
            myHandleText.setText(handle);
        }
        progressCircle.setVisibility(View.VISIBLE);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressCircle.setVisibility(View.INVISIBLE);
            }
        },1000);


        final String ProfileName = Profile.getCurrentProfile().getName();
        myNameText.setText(ProfileName);

        Glide.with(profileView).load(Profile.getCurrentProfile().getProfilePictureUri(600,600)).circleCrop().into(myProfileView);


        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startSettings = new Intent(getActivity(), Settings.class);
                startActivity(startSettings);
            }
        });
        return profileView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    private void checkInfo(){
        userDetailsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot profileInfo = task.getResult();
                if(task.isSuccessful() && profileInfo != null){
                    if(profileInfo.contains(INSTA_ID)){
                        Log.d(TAG, "INSTA ID FOUND");
                        if(profileInfo.get(INSTA_ID) != null) {

                            String instaID = profileInfo.get(INSTA_ID).toString();
                            Log.d(TAG, "onComplete: " + instaID );
                            instagramUsername.setText(instaID);
                        }
                    }
                    if(profileInfo.contains(TWITTER_ID)){
                        if(profileInfo.get(TWITTER_ID) != null) {
                            String twitterID = profileInfo.get(TWITTER_ID).toString();
                            Log.d(TAG, "onComplete: " + twitterID );
                            twitterEdit.setText(twitterID);

                        }
                    }
                    if(profileInfo.contains(BIO_ID)){
                        if(profileInfo.get(BIO_ID) != null) {
                            String bioId = profileInfo.get(BIO_ID).toString();
                            Log.d(TAG, "onComplete: " + bioId );
                            bioEdit.setText(bioId);

                        }
                    }
                    if(profileInfo.contains(FB_ID)){
                        if(profileInfo.get(FB_ID) != null) {
                            String facebookStr = profileInfo.get(FB_ID).toString();
                            Log.d(TAG, "onComplete: " + facebookStr );
                            facebookEdit.setText(facebookStr);

                        }
                    }
                    if(profileInfo.contains(SEND_LINK)){
                        if(profileInfo.get(SEND_LINK) != null) {
                            String linkStr = profileInfo.get(SEND_LINK).toString();
                            Log.d(TAG, "onComplete: " + linkStr );
                            linkEdit.setText(linkStr);

                        }
                    }
                    if(profileInfo.contains(VIEWS)){
                        if(profileInfo.get(VIEWS) != null) {
                            String views = profileInfo.get(VIEWS).toString();
                            viewCount.setText(views);

                        }
                    }
                    if(profileInfo.contains(TOTAL_VIEWS)){
                        if(profileInfo.get(TOTAL_VIEWS) != null) {
                            String total_views = profileInfo.get(TOTAL_VIEWS).toString();
                            totalviewCount.setText(total_views);

                        }
                    }
                    if(profileInfo.contains(SNAP_ID)){
                        if(profileInfo.get(SNAP_ID) != null) {
                            String snapchat = profileInfo.get(SNAP_ID).toString();
                            Log.d(TAG, "onComplete: " + snapchat );
                            snapchatEdit.setText(snapchat);

                        }
                    }
                    progressCircle.setVisibility(View.INVISIBLE);

                }
            }
        });
    }


}
