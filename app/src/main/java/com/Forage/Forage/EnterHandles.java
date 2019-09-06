package com.Forage.Forage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class EnterHandles extends AppCompatActivity {

    private EditText handle;
    private TextView errorText;
    private FirebaseFirestore fb = FirebaseFirestore.getInstance();
    private CollectionReference handles = fb.collection("handles");
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_handles);
        Button nextBtn = findViewById(R.id.next_btn);
        handle  = findViewById(R.id.enter_handles);
        handle.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        errorText = findViewById(R.id.error_username_text);
        final ProgressBar usernameProg = findViewById(R.id.usernamefind_prog);

        usernameProg.setVisibility(View.INVISIBLE);
        nextBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                usernameProg.setVisibility(View.VISIBLE);
                final int handleLength = handle.getText().toString().length();
                if(handleLength >= 6 && handleLength <= 20){
                    handles.document(handle.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();
                            if(doc!=null){
                                if(doc.exists()){
                                    errorText.setText(getString(R.string.username_exists));
                                    runPostDelay();
                                    usernameProg.setVisibility(View.INVISIBLE);
                                }
                                else{

                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor edit = sharedPreferences.edit();
                                    edit.putString("handle",handle.getText().toString());
                                    edit.apply();
                                    Intent startMainActivity = new Intent(EnterHandles.this, MainActivity.class);
                                    startActivity(startMainActivity);
                                    finish();
                                    usernameProg.setVisibility(View.INVISIBLE);

                                }
                            }
                        }
                    });

                }
                else if(handleLength < 6){
                    errorText.setText(getString(R.string.username_short));
                    runPostDelay();
                }
                else {
                    errorText.setText(getString(R.string.usernam_long));
                    runPostDelay();
                }
            }
        });


    }

    private void runPostDelay(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                errorText.setText(null);
            }
        },5000);
    }
}
