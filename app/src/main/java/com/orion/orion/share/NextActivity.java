package com.orion.orion.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.UniversalImageLoader;

public class NextActivity extends AppCompatActivity {


    private static final String TAG ="NextActivity" ;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;
    private  FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    private EditText mCaption;
    ProgressBar progressBar;

    private String mAppend = "file:/";
    private int imageCount=0;
    private String imgURL;
    private Intent intent;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mFirebaseMethods = new FirebaseMethods(NextActivity.this);
        mCaption = findViewById(R.id.caption);




        setupFirebaseAuth();
        ImageView backArrow = findViewById(R.id.ivbackarrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView share = findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(NextActivity.this, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();

                String caption = mCaption.getText().toString();
                if(intent.hasExtra(getString(R.string.selected_image))){

                    imgURL = intent.getStringExtra(getString(R.string.selected_image));

                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo),caption,imageCount,imgURL,null);


                }else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    bitmap = intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, null, bitmap);

                }

            }
        });
        setImage();
    }


        private void setImage(){
             intent = getIntent();
            ImageView image = findViewById(R.id.imageshare);
            if(intent.hasExtra(getString(R.string.selected_image))){
                imgURL = intent.getStringExtra(getString(R.string.selected_image));

                Log.d(TAG,"selectedimage next "+ imgURL);
                UniversalImageLoader.setImage(imgURL,image,null,mAppend);

            }else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                bitmap = intent.getParcelableExtra(getString(R.string.selected_bitmap));
                image.setImageBitmap(bitmap);
            }


    }
    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        myRef=mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

           imageCount =mFirebaseMethods.getImageCount(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);



    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
