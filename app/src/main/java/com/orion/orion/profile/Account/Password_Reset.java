package com.orion.orion.profile.Account;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orion.orion.R;
import com.orion.orion.login.LoginActivity;

public class Password_Reset extends AppCompatActivity {
    private static final String TAG = "Password_Reset";
    private FirebaseAuth mAuth;
    private ImageView backArrow;
    private TextView mTopBarTitle;
    ProgressBar progressBar;
    private  FirebaseAuth.AuthStateListener mAuthListener;
    private String oldP = "", newP = "", confirmP = "", email = "";
    private TextInputEditText oldE, newE, confirmE;
    private FirebaseUser user;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password__reset);

        setupFirebaseAuth();

        mContext=Password_Reset.this;

        oldE = findViewById(R.id.oldpswrd2);
        newE = findViewById(R.id.newpswrd2);
        confirmE = findViewById(R.id.confirm_password2);
        backArrow = findViewById(R.id.backarrow);
        mTopBarTitle = findViewById(R.id.titleTopBar);
        progressBar = findViewById(R.id.pro);

        backArrow.setOnClickListener(view -> finish());
        mTopBarTitle.setText("Password Reset");
        Button confirm = findViewById(R.id.confirm);
        Button cancel = findViewById(R.id.cancel);
        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        email = user.getEmail();
        confirm.setOnClickListener(v -> {
            oldP = oldE.getText().toString();
            newP = newE.getText().toString();
            confirmP = confirmE.getText().toString();
            if(checkEntries()) updatePassword();
        });
        cancel.setOnClickListener(v -> finish());
    }
    private void updatePassword() {
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldP);
        Log.d(TAG, "updatePassword: " + oldP);
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(newP).addOnCompleteListener(task1 -> {
                    if (!task1.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(Password_Reset.this, "failed", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(mContext, "Password Reset Succesful", Toast.LENGTH_SHORT).show();
                    }
                });
            } else{
                progressBar.setVisibility(View.GONE);

                Toast.makeText(mContext, "Wrong Credential", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Boolean checkEntries() {
        if (newP.equals("") || oldP.equals("") || confirmP.equals("")) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!newP.equals(confirmP)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return false;
        } else if (newP.equals(oldP)) {
            Toast.makeText(this, "Please enter different password from old password", Toast.LENGTH_SHORT).show();
            return false;
        }else if (newP.length() < 6){
            Toast.makeText(this, "Password is too short!", Toast.LENGTH_SHORT).show();

        }
        return true;
    }
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                Log.d(TAG, "onAuthStateChanged: navigating to login");
                SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                new AlertDialog.Builder(mContext)
                        .setTitle("No user logon found")
                        .setMessage("We will be logging u out. \n Please try to log in again")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            settings.edit().clear().apply();
                            if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
                            startActivity(intent);
                        })
                        .show();
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