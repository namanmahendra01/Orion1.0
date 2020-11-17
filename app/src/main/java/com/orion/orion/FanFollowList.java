package com.orion.orion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.Adapters.SectionPagerAdapter;
import com.orion.orion.login.login;
import com.orion.orion.profile.Account.AccountSettingActivity;
import com.orion.orion.util.BottomNaavigationViewHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class FanFollowList extends AppCompatActivity {
    private static final String TAG = "FanFollowListActivity";
    private static final int ACTIVITY_NUM = 4;
    private static final int FOLLOW_FRAGMENT = 0;
    public TabLayout tablayout;
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private ViewPager mViewPager;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext, AccountSettingActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_follow_list);
        Log.d(TAG, "onCreate: started");
        mContext = FanFollowList.this;
        ImageView backarrow = findViewById(R.id.backarrow);
        mViewPager = findViewById(R.id.viewpager_container);
        backarrow.setOnClickListener(v -> startActivity(new Intent(mContext, AccountSettingActivity.class)));
        setupFirebaseAuth();
        setupBottomNavigationView();
        setupViewPager();

    }

    private void setupViewPager() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FansFragment());
        adapter.addFragment(new FollowingFragment());
        mViewPager.setAdapter(adapter);
        tablayout = findViewById(R.id.tabs);
        tablayout.setupWithViewPager(mViewPager);
        tablayout.getTabAt(0).setText("FANS");
        tablayout.getTabAt(1).setText("FOLLOWING");
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tablayout.getSelectedTabPosition()) {
                    case 0:
                        break;
                    case 1:
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + tablayout.getSelectedTabPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                tablayout.removeOnTabSelectedListener(this);
            }
        });
    }

    private void setupBottomNavigationView() {
        Log.d(TAG, " setupBottomNavigationView:setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.BottomNavViewBar);
        BottomNaavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNaavigationViewHelper.enableNavigation(FanFollowList.this, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started");
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuthListener = firebaseAuth -> {
            mUser = firebaseAuth.getCurrentUser();
            if (mUser != null) Log.d(TAG, "onAuthStateChanged: signed_in:" + mUser.getUid());
            else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                Log.d(TAG, "onAuthStateChanged: navigating to login");
                SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                new AlertDialog.Builder(mContext)
                        .setTitle("No user logon found")
                        .setMessage("We will be logging u out. \n Please try to log in again")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            Intent intent = new Intent(mContext, login.class);
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