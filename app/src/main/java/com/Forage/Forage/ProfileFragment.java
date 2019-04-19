package com.Forage.Forage;

import android.content.Context;
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

    private ImageView myProfileView, doneTick;
    private EditText instagramUsername, linkEdit, twitterEdit, bioEdit, facebookEdit, snapchatEdit;
    private AdView adView;
    private TextView myNameText, charCount,viewCount, totalviewCount;



    private HashMap<String,String>fireStorePutHash;

    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private CollectionReference collection = fb.collection("users");
    private DocumentReference userDetailsRef;
    private Handler handler;
    private InputMethodManager inputMethodManager;
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            charCount.setText(String.valueOf(bioEdit.getText().length()));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        profileView = inflater.inflate(R.layout.profile_fragment, container,false);
        myProfileView = profileView.findViewById(R.id.myProfileView);
        charCount = profileView.findViewById(R.id.charLengthView);
        instagramUsername = profileView.findViewById(R.id.instagramUserName);
        twitterEdit = profileView.findViewById(R.id.twitterEdit);
        linkEdit = profileView.findViewById(R.id.linkEditText);
        facebookEdit = profileView.findViewById(R.id.facebookEdit);
        snapchatEdit =  profileView.findViewById(R.id.snapchatUsername);
        bioEdit = profileView.findViewById(R.id.bioEdit);
        doneTick = profileView.findViewById(R.id.doneTick);
        myNameText = profileView.findViewById(R.id.myName);
        viewCount = profileView.findViewById(R.id.viewsCount);
        totalviewCount = profileView.findViewById(R.id.totalviewcount);
        progressCircle = profileView.findViewById(R.id.progressCircle);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);


        adView = profileView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String UUID = sharedPreferences.getString("UserUUID", null);
        fireStorePutHash = new HashMap<>();

        userDetailsRef = collection.document(UUID);
        checkInfo();

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


        instagramUsername.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        twitterEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        facebookEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        snapchatEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        linkEdit.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        bioEdit.addTextChangedListener(textWatcher);
        Glide.with(profileView).load(Profile.getCurrentProfile().getProfilePictureUri(600,600)).circleCrop().into(myProfileView);

        doneTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Tick CLicked");
                progressCircle.setVisibility(View.VISIBLE);

                sendAccountInfo(1);
                sendAccountInfo(2);
                sendAccountInfo(3);
                sendAccountInfo(4);
                sendAccountInfo(5);
                sendAccountInfo(6);


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
    private void sendAccountInfo(int accountNo){
        switch (accountNo){
            case 1:
                if(instagramUsername.getText() != null){
                    String instaUser = instagramUsername.getText().toString();
                    fireStorePutHash.put(INSTA_ID, instaUser);
                    userDetailsRef.set(fireStorePutHash, SetOptions.merge());
                    finishWriteText(instagramUsername);

                }
                break;

            case 2:
                if(twitterEdit.getText() != null){
                    String twitterUsername = twitterEdit.getText().toString();
                    fireStorePutHash.put(TWITTER_ID, twitterUsername);
                    userDetailsRef.set(fireStorePutHash, SetOptions.merge());
                    finishWriteText(twitterEdit);

                }
                break;

            case 3:
                if(bioEdit.getText() != null){
                    String bioString  = bioEdit.getText().toString();
                    fireStorePutHash.put(BIO_ID, bioString);
                    userDetailsRef.set(fireStorePutHash, SetOptions.merge());
                    finishWriteText(bioEdit);
                }
                break;
            case 4:
                if(facebookEdit.getText() != null){
                    String fbString  = facebookEdit.getText().toString();
                    fireStorePutHash.put(FB_URI, fbString);
                    userDetailsRef.set(fireStorePutHash, SetOptions.merge());
                    finishWriteText(facebookEdit);
                }
                break;
            case 5:
                if(linkEdit.getText() != null){
                    String link  = linkEdit.getText().toString();
                    fireStorePutHash.put(SEND_LINK, link);
                    userDetailsRef.set(fireStorePutHash, SetOptions.merge());
                    finishWriteText(linkEdit);
                }
                break;
            case 6:
                if(snapchatEdit.getText() != null){
                    String snapchat  = snapchatEdit.getText().toString();
                    fireStorePutHash.put(SNAP_ID, snapchat);
                    userDetailsRef.set(fireStorePutHash, SetOptions.merge());
                    finishWriteText(snapchatEdit);

                }
                break;
        }



    }

    private void checkInfo(){
        userDetailsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot profileInfo = task.getResult();
                if(task.isSuccessful()){
                    if(profileInfo.contains(INSTA_ID)){
                        Log.d(TAG, "INSTA ID FOUND");
                        if(profileInfo.get(INSTA_ID) != null) {
                            String instaID = profileInfo.get(INSTA_ID).toString();
                            instagramUsername.setText(instaID);
                        }
                    }
                    if(profileInfo.contains(TWITTER_ID)){
                        if(profileInfo.get(TWITTER_ID) != null) {
                            String twitterID = profileInfo.get(TWITTER_ID).toString();
                            twitterEdit.setText(twitterID);

                        }
                    }
                    if(profileInfo.contains(BIO_ID)){
                        if(profileInfo.get(BIO_ID) != null) {
                            String bioId = profileInfo.get(BIO_ID).toString();
                            bioEdit.setText(bioId);
                            charCount.setText(String.valueOf(bioId.length()));

                        }
                    }
                    if(profileInfo.contains(FB_URI)){
                        if(profileInfo.get(FB_URI) != null) {
                            String facebookStr = profileInfo.get(FB_URI).toString();
                            facebookEdit.setText(facebookStr);

                        }
                    }
                    if(profileInfo.contains(SEND_LINK)){
                        if(profileInfo.get(SEND_LINK) != null) {
                            String linkStr = profileInfo.get(SEND_LINK).toString();
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
                            snapchatEdit.setText(snapchat);

                        }
                    }
                    progressCircle.setVisibility(View.INVISIBLE);

                }
            }
        });
    }

    public void finishWriteText(EditText editText){
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkInfo();
            }
        },2000);

    }

}
