package com.orion.orion.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.orion.orion.R;
import com.orion.orion.contest.upcoming.UpcomingContestActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "activity_login";
    private static final int ANIMATION_DURATION = 1000;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context mContext;
    private RelativeLayout rootView;
    private ProgressBar mProgressBar;
    private ImageView appIcon;
    private TextView appTitle;
    private RelativeLayout afterAnimationView;
    private EditText mEmail;
    private EditText mPassword;
    private Button btnLogin;
    private TextView linkSignup,flg;
    private TextView forgotPassword;
    String justRegistered;

    //    SP
    Gson gson;
    SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate: started.");

//          Initialize SharedPreference variables
        sp = getSharedPreferences("Login", Context.MODE_PRIVATE);
        gson = new Gson();
        justRegistered = sp.getString("yes", "");

        initializeWidgets();
        setupFirebaseAuth();
        if (!justRegistered.equals("yes")) {
            if (mAuth.getCurrentUser() != null) {
                Intent intent = new Intent(LoginActivity.this, UpcomingContestActivity.class);
                startActivity(intent);
                finish();
            }
        }else{
            flg.setVisibility(View.VISIBLE);
        }
        init();

    }


    private void initializeWidgets() {
        Log.d(TAG, "initializeWidgets: ");
        appIcon = findViewById(R.id.appIcon);
        rootView = findViewById(R.id.rootView);
        flg = findViewById(R.id.flg);

        afterAnimationView = findViewById(R.id.afterAnimationView);
        mProgressBar = findViewById(R.id.loginrequestloadingprogressbar);
        mProgressBar.setVisibility(View.GONE);
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        btnLogin = findViewById(R.id.btn_login);
        linkSignup = findViewById(R.id.link_signup);
        forgotPassword = findViewById(R.id.forgotPassword);
        mContext = LoginActivity.this;
    }


    private void init() {
        rootView.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        });



        btnLogin.setOnClickListener(v -> {
            mProgressBar.setVisibility(View.VISIBLE);
            String email = mEmail.getText().toString();
            String password = mPassword.getText().toString();
            if (email.equals("")) {
                mProgressBar.setVisibility(View.GONE);
                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mEmail);
                mEmail.setText("");
                mEmail.setError("Please enter a email-id");
                mEmail.requestFocus();
            } else if (password.equals("")) {
                mProgressBar.setVisibility(View.GONE);
                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mPassword);
                mPassword.setText("");
                mPassword.setError("Please enter a password");
                mPassword.requestFocus();
            } else {
                mProgressBar.setVisibility(View.VISIBLE);
                mEmail.setFocusable(false);
                mPassword.setFocusable(false);
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        ProgressDialog progressDialog = new ProgressDialog(mContext);
                        progressDialog.setMessage("Authenticating");
                        progressDialog.show();
                        mProgressBar.setVisibility(View.GONE);
                        try {
                            assert user != null;
                            Log.d(TAG, "init: " + user);
                            if (user.isEmailVerified()) {
                                Log.d(TAG, "onComplete:email is verified.");
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("yes", "no");
                                editor.apply();
                                progressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, UpcomingContestActivity.class);
                                startActivity(intent);
                            } else {
                                new AlertDialog.Builder(mContext)
                                        .setTitle("Sorry")
                                        .setMessage("Email is not verified \n Check your email inbox")
                                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                            dialog.dismiss();
                                            mEmail.setFocusableInTouchMode(true);
                                            mPassword.setFocusableInTouchMode(true);
                                        })
                                        .show();
                                mProgressBar.setVisibility(View.GONE);
                                mAuth.signOut();
                            }

                        } catch (NullPointerException e) {
                            Log.e(TAG, "NullPointerException" + e.getMessage());
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        new AlertDialog.Builder(mContext)
                                .setTitle("Wrong credentials")
                                .setMessage(task.getException().getLocalizedMessage())
                                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                    dialog.dismiss();
                                    mEmail.setFocusableInTouchMode(true);
                                    mPassword.setFocusableInTouchMode(true);
                                })
                                .show();
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mEmail);
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mPassword);
                        mEmail.setText("");
                        mPassword.setText("");
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        forgotPassword.setOnClickListener(v -> {
            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(mContext);
            View promptsView = li.inflate(R.layout.dialog_find_your_account, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView.findViewById(R.id.input_email);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            (dialog, id) -> {
                                // get user input and set it to result
                                // edit text
                                if (userInput.getText() != null && userInput.getText().length() != 0) {
                                    FirebaseAuth.getInstance().setLanguageCode("en"); // Set to English
                                    FirebaseAuth.getInstance().sendPasswordResetEmail(String.valueOf(userInput.getText()))
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            })
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(mContext, "We have sent and email to " + userInput.getText() + " please confirm to reset password", Toast.LENGTH_LONG).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(mContext, "Reset password request is failed", Toast.LENGTH_LONG).show();


                                                }
                                            });
                                } else {
                                    Toast.makeText(mContext, "Please enter Something!", Toast.LENGTH_LONG).show();

                                }


                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        });

        linkSignup.setOnClickListener(v -> {
            Log.d(TAG, "on Click:navigating to register screen");
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        Log.d(TAG, "init: mAuth.getCurrentUser()" + mAuth.getCurrentUser());

    }

    //    *********************FIREBASE***************************
    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
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
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    //   ************************FIREBASE****************************
}