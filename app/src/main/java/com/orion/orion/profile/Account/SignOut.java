package com.orion.orion.profile.Account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orion.orion.R;
import com.orion.orion.login.login;

public class SignOut extends AppCompatActivity {
    private static final String TAG = "SignOut";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressBar mProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_signout);

        Button btnConfirmSignOut = findViewById(R.id.btnConfirmsSignOut);
        mProgressbar = findViewById(R.id.progressBar);
        mProgressbar.setVisibility(View.GONE);

        setupFirebaseAuth();

        btnConfirmSignOut.setOnClickListener(v -> {
            SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
            settings.edit().clear().apply();
            mAuth.signOut();
            mProgressbar.setVisibility(View.VISIBLE);
            finish();
        });
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                Log.d(TAG, "onAuthStateChanged: navigating to login");
                Intent intent = new Intent(SignOut.this, login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
    }
}