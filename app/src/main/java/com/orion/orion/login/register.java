package com.orion.orion.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.dialogs.BottomSheetDomain;
import com.orion.orion.util.FirebaseMethods;

public class register extends AppCompatActivity implements BottomSheetDomain.BottomSheetListener {

    private static final String TAG = "register";
    private static final int ANIMATION_DURATION = 1000;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context mContext;
    //widgets
    private RelativeLayout rootView;
    private ProgressBar mProgressBar;
    private EditText mUsername;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private TextView domainSelection;
    private TextView mPleasewait;
    private Button btnregister;
    private TextView linkLogin;
    private ImageView showPasswordToggle;
    private FusedLocationProviderClient fusedLocationClient;
    //variables
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String domain;
    private FirebaseMethods firebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String append = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);
        Log.d(TAG, "onCreate: started.");

        mContext = register.this;
        firebaseMethods = new FirebaseMethods(mContext);
        setupFirebaseAuth();
        initWidgets();
        init();
    }

    private void initWidgets() {
        Log.d(TAG, "initWidgets: initiliazing widgets");
        rootView = findViewById(R.id.rootView);
        mProgressBar = findViewById(R.id.Registerrequestloadingprogressbar);
        mUsername = findViewById(R.id.username);
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mConfirmPassword = findViewById(R.id.confirm_password);
        domainSelection = findViewById(R.id.domain_selection);
        btnregister = findViewById(R.id.btn_register);
        linkLogin = findViewById(R.id.link_login);
        mContext = register.this;
        mProgressBar.setVisibility(View.GONE);
        showPasswordToggle = findViewById(R.id.show_pass_btn);
        domain = "";

    }

    private boolean checkInputs(String email, String username, String password, String confirmPassword, String domain) {
        Log.d(TAG, "checkInputs:checking inputs for null values");

        if (username.equals("")) {
            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mUsername);
            Toast.makeText(mContext, "Empty username field", Toast.LENGTH_SHORT).show();
            mUsername.setError("Please enter a username");
            mUsername.requestFocus();
            return false;
        }
        if (email.equals("")) {
            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mEmail);
            Toast.makeText(mContext, "Empty email field", Toast.LENGTH_SHORT).show();
            mEmail.setError("Please enter a email-id");
            mEmail.requestFocus();
            return false;
        }
        if (password.equals("")) {
            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mPassword);
            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mConfirmPassword);
            mPassword.setText("");
            mConfirmPassword.setText("");
            mPassword.setError("Please enter a password");
            mPassword.requestFocus();
            Toast.makeText(mContext, "Empty password field", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (confirmPassword.equals("")) {
            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mConfirmPassword);
            mConfirmPassword.setText("");
            mConfirmPassword.setError("Please enter a password");
            mConfirmPassword.requestFocus();
            Toast.makeText(mContext, "Empty confirm password field", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (domain.equals("")) {
            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(domainSelection);
            domainSelection.setText("Select Your Domain");
            Toast.makeText(mContext, "No domain selected", Toast.LENGTH_SHORT).show();
            domainSelection.setError("Please enter a password");
            domainSelection.requestFocus();
            return false;
        } else
            return true;
    }

    private boolean checkValidity(String password) {
        if (password.length() < 6) {
            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mPassword);
            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mConfirmPassword);
            mPassword.setText("");
            mConfirmPassword.setText("");
            mPassword.setError("Password too short");
            mPassword.requestFocus();
            mConfirmPassword.setError("Password too short");
            mConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }


    @Override
    public void onButtonClicked(String text) {
        Log.d(TAG, "onButtonClicked: domain selected " + text);
        domain = text;
        Log.d(TAG, "onButtonClicked: " + text);
        domainSelection.setText(text);
        YoYo.with(Techniques.FadeInUp).duration(ANIMATION_DURATION).playOn(domainSelection);
    }


    private void init() {

        rootView.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        });

        //domain selection bottom sheet
        domainSelection.setOnClickListener(v -> {
            BottomSheetDomain bottomSheetDomain = new BottomSheetDomain();
            bottomSheetDomain.show(getSupportFragmentManager(), "Domain Selection");
        });

        showPasswordToggle.setOnClickListener(v -> {
            if (mPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                showPasswordToggle.setImageResource(R.drawable.ic_visibility_on);
                mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                showPasswordToggle.setImageResource(R.drawable.ic_visibility_off);
                mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        //registration button
        btnregister.setOnClickListener(v -> {
            email = mEmail.getText().toString();
            username = mUsername.getText().toString();
            password = mPassword.getText().toString();
            confirmPassword = mConfirmPassword.getText().toString();

            if (checkInputs(email, username, password, confirmPassword, domain)) {
                if (checkValidity(password) || checkValidity(confirmPassword))
                    if (password.equals(confirmPassword)) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        firebaseMethods.RegisterNewEmail(email, password,mProgressBar,mEmail,mUsername,mPassword,mConfirmPassword,domainSelection);
                    }
                    else {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mPassword);
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mConfirmPassword);
                        mPassword.setText("");
                        mConfirmPassword.setText("");
                        mPassword.setError("Password don not match");
                        mPassword.requestFocus();
                        mConfirmPassword.setError("Password don not match");
                        mConfirmPassword.requestFocus();
                    }
            } else {
                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mPassword);
                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mConfirmPassword);
                mPassword.setText("");
                mConfirmPassword.setText("");
            }
        });

        //link login
        linkLogin.setOnClickListener(v -> {
            Log.d(TAG, "on Click:navigating to register screen");
            Intent intent = new Intent(register.this, login.class);
            startActivity(intent);
        });
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        checkifuserexist(username);
                        Log.d(TAG, "onDataChange: " + "redirecting to login activity");
                        Intent intent = new Intent(register.this, login.class);
                        intent.putExtra("CameFromRegister",1);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                finish();
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };
    }

    private void checkifuserexist(final String username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_users)).orderByChild(getString(R.string.field_username)).equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                    if (singleSnapshot.exists())
                        append = myRef.push().getKey().substring(3, 10);

                String mUsername = "";
                mUsername = username + append;
                //setting up leaderboard for user
                firebaseMethods.addNewUser(email, mUsername, domain);

                Log.d(TAG, "onDataChange: " + "userAdded");
                Toast.makeText(mContext, "Signup successful.Sending verification email.", Toast.LENGTH_SHORT).show();
//                mAuth.signOut();
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
