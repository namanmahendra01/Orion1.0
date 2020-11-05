package com.orion.orion.profile.Account;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.R;
import com.orion.orion.login.login;
import com.orion.orion.util.BottomNaavigationViewHelper;

import java.util.ArrayList;

public class AccountSettingActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingActivity";
    private static final int ACTIVITY_NUM = 4;
    private Context mcontext;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ListView listview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsetting);

        mcontext = AccountSettingActivity.this;
//        ViewPager mViewPager = findViewById(R.id.viewpager_container);
//        RelativeLayout mRelativeLayout = findViewById(R.id.relLayoutAccountSetting);
        listview = findViewById(R.id.lvAccountSettings);
        ImageView backarrow = findViewById(R.id.backarrow);

        setupBottomNavigationView();
        setupFirebaseAuth();
        setupSettingList();
        backarrow.setOnClickListener(v -> {
            Log.d(TAG, "onCreate : navigation back to'profileactivity' ");
            finish();
        });
    }

    private void setupSettingList() {
        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile));
        options.add("Password Reset");
        options.add("Contest");
        options.add("Help");
        options.add("About");
        options.add(getString(R.string.sign_out));//fragment 1
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mcontext, android.R.layout.simple_list_item_1, options);
        listview.setAdapter((adapter));
        listview.setOnItemClickListener((parent, view, position, id) -> {
            switch (position){
                case 0: startActivity(new Intent(mcontext, EditProfile.class));
                    break;
                case 1: startActivity(new Intent(mcontext, Password_Reset.class));
                    break;
                case 2: startActivity(new Intent(mcontext, Password_Reset.class));
                    break;
                case 3: startActivity(new Intent(mcontext, Help.class));
                    break;
                case 4: startActivity(new Intent(mcontext, About.class));
                    break;
                case 5: new AlertDialog.Builder(mcontext)
                        .setTitle("Log Out but why?")
                        .setMessage("Are u sure u want to do this?.")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                            settings.edit().clear().apply();
                            PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().clear().apply();
                            Intent intent = new Intent(mcontext, login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mAuth.signOut();
                            startActivity(intent);
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
                    break;
            }
        });
    }

    private void setupBottomNavigationView() {
        Log.d(TAG, " setupBottomNavigationView:setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.BottomNavViewBar);
        BottomNaavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNaavigationViewHelper.enableNavigation(mcontext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());
            else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                Log.d(TAG, "onAuthStateChanged: navigating to login");
                SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                new AlertDialog.Builder(mcontext)
                        .setTitle("No user logon found")
                        .setMessage("We will be logging u out. \n Please try to log in again")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            Intent intent = new Intent(mcontext, login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            settings.edit().clear().apply();
                            startActivity(intent);
                        })
                        .show();
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
        if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
    }
}