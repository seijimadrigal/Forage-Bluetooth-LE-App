package com.Forage.Forage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

import static com.Forage.Forage.Constants.*;

public class NewProfileView extends AppCompatActivity {

    private String TAG = "PROFILEVIEW";
    private ImageView fbPhoto;
    private TextView fbName, bioText, linkTEXT;
    private ImageView fbBtn;
    private ImageView instaBtn, snapchatBtn;
    private ImageView twitterBtn;
    private android.support.v7.widget.Toolbar toolbar;
    private RelativeLayout customLinkcontainer;
    private RelativeLayout bioContainer;

    private String profileLink;
    private String profileID;
    private String profilePhotoUri;
    private String instaProfileId;
    private String fbProfilePhoto;
    private String bioID;
    private String twitterID;
    private String customLINK;
    private String snapID;
    private String UUID;
    private int viewCount;

    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private CollectionReference collection = fb.collection("users");
    private DocumentReference documentReference;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile_view);
        toolbar = findViewById(R.id.profileviewToolbar);
        setSupportActionBar(toolbar);
        fbPhoto = findViewById(R.id.profilePhoto);
        fbName = findViewById(R.id.profileName);
        fbBtn = findViewById(R.id.fbBtn);
        instaBtn = findViewById(R.id.instaButton);
        snapchatBtn = findViewById(R.id.snapchatBtn);
        bioContainer = findViewById(R.id.bioContainer);
        customLinkcontainer = findViewById(R.id.customcontainer);

        bioText = findViewById(R.id.bioText);
        twitterBtn = findViewById(R.id.twitterBtn);
        linkTEXT = findViewById(R.id.linkTEXT);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_white_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "FINISH ACTIVITY");
                HashMap<String,Object> viewHash = new HashMap<>();
                viewHash.put(VIEWS, viewCount - 1);
                documentReference.set(viewHash, SetOptions.merge());
                finish();

            }
        });



        final Intent intent = getIntent();

        String profileName = intent.getStringExtra(FB_NAME);
        //profileLink = intent.getStringExtra("FBLINK");
        profileID = intent.getStringExtra(FB_ID);
        twitterID = intent.getStringExtra(TWITTER_ID);
        snapID = intent.getStringExtra(SNAP_ID);
        profileLink = intent.getStringExtra(FB_LINK);
        instaProfileId = intent.getStringExtra(INSTA_ID);
        fbProfilePhoto = intent.getStringExtra(FB_PHOTO);
        bioID = intent.getStringExtra(BIO_ID);
        customLINK = intent.getStringExtra(SEND_LINK);
        UUID = intent.getStringExtra(UUID_FS);
        documentReference = collection.document(UUID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot profileInfo  = task.getResult();
                if(task.isSuccessful()){
                    if(profileInfo.contains(VIEWS)){
                        Log.d(TAG, "Contains Views");
                        try {
                            HashMap<String, Object> viewsHash =  new HashMap<>();
                            viewCount = profileInfo.getLong(VIEWS).intValue();
                            Log.d(TAG, "View Count " + viewCount);
                            viewCount++;
                            viewsHash.put(VIEWS, viewCount);
                            documentReference.set(viewsHash,SetOptions.merge());

                        } catch (NullPointerException npe){
                            Log.d(TAG, "Unable to get Viewcount ");
                        }
                    }
                    if(profileInfo.contains(TOTAL_VIEWS)){
                        Log.d(TAG, "Contains View Total");
                        try {

                            int totalViewCount = profileInfo.getLong(TOTAL_VIEWS).intValue();
                            Log.d(TAG, "Total Views "+ totalViewCount);
                            HashMap<String,Object> totalviewHash1 = new HashMap<>();
                            totalViewCount++;
                            totalviewHash1.put(TOTAL_VIEWS, totalViewCount);
                            documentReference.set(totalviewHash1, SetOptions.merge());

                        }
                        catch (NullPointerException npe){
                            Log.d(TAG, "Unable to get Total Viewcount ");
                        }

                    }
                    else{
                        HashMap<String,Integer> totalviewHash2 = new HashMap<>();
                        totalviewHash2.put(TOTAL_VIEWS, 1);
                        documentReference.set(totalviewHash2, SetOptions.merge());
                    }

                }
            }
        });
        Log.d(TAG, "Profile Link: " + profileLink );
        Log.d(TAG, "Profile ID: " + profileID );
        Log.d(TAG, "Profile Photo URI: " + fbProfilePhoto );
        Log.d(TAG, "Profile Name: " + profileName );
        Log.d(TAG, "Twitter ID: "+ twitterID);
        Log.d(TAG, "INSTA URI " + instaProfileId);

        try {
            Glide.with(this).load(Uri.parse(fbProfilePhoto)).circleCrop().into(fbPhoto);
            if(instaProfileId.length() < 2){
                instaBtn.setVisibility(View.INVISIBLE);
            }
            if(profileLink.length() < 2){
                fbBtn.setVisibility(View.INVISIBLE);
            }
            if(twitterID.length() < 2){
                twitterBtn.setVisibility(View.INVISIBLE);
            }
            if(snapID.length() < 2){
                snapchatBtn.setVisibility(View.INVISIBLE);
            }
            if(bioID.length() < 2){
                bioContainer.setVisibility(View.INVISIBLE);
            }
            if(customLINK.length() <2 ){
                customLinkcontainer.setVisibility(View.INVISIBLE);
            }
        }
        catch (NullPointerException nfe){
            if(instaProfileId == null){
                instaBtn.setVisibility(View.INVISIBLE);
            }
            if(profileLink == null){
                fbBtn.setVisibility(View.INVISIBLE);
            }
            if(twitterID == null){
                twitterBtn.setVisibility(View.INVISIBLE);
            }
            if(snapID == null){
                snapchatBtn.setVisibility(View.INVISIBLE);
            }
        }

        bioText.setText(bioID);
        fbName.setText(profileName);
        linkTEXT.setText(customLINK);

        linkTEXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "LINK TEXT: " + customLINK);
                Intent goTOLINK = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+customLINK));
                startActivity(goTOLINK);
            }
        });

        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goTOprofile = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"+profileLink));
                startActivity(goTOprofile);

                // Intent goTOprofile = new Intent(Intent.ACTION_VIEW, Uri.parse("facebook://profile/"+profileLink));
                //startActivity(goTOprofile);

            }
        });

        instaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri instaUri = Uri.parse("https://www.instagram.com/" + instaProfileId);
                Log.d(TAG, "INSTA URI: " + instaUri.toString());
                Intent goToinsta = new Intent(Intent.ACTION_VIEW,instaUri );
                startActivity(goToinsta);
            }
        });

        twitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri twitterURI = Uri.parse("https://www.twitter.com/"+twitterID);
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + twitterID)));
                }catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + twitterID)));
                }
            }
        });
        snapchatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("snapchat://add/" + snapID)));
                }catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://snapchat.com/add/" + snapID)));
                }
            }
        });



    }


}
