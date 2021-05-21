
package com.orion.orion.contest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.Adapters.SectionPagerAdapter;
import com.orion.orion.R;
import com.orion.orion.contest.create.fragment_createContest;
import com.orion.orion.contest.joined.fragment_joinedContest;
import com.orion.orion.contest.upcoming.fragment_upcomingContest;
import com.orion.orion.login.LoginActivity;
import com.orion.orion.util.BottomNaavigationViewHelper;

import static java.security.AccessController.getContext;

public class contestMainActivity extends AppCompatActivity {
    private static final String TAG = "contest";
    private static final int ACTIVITY_NUM = 0;
    private static final int CREATE_CONTEST = 1;
    public static final String TIME_SERVER = "time-a.nist.gov";
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest);
        Log.d(TAG, "onCreate: started.");
        mContext = contestMainActivity.this;
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        setupBottomNavigationView();
        setupFirebaseAuth();
        checkCurrentuser(mAuth.getCurrentUser());
        hideSoftKeyboard();
        setupViewPager();
        Log.d(TAG, " context"+this+"  "+getContext()+"  "+getApplicationContext());

    }

    //    for adding 3 tabs -media,home,message
    private void setupViewPager() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new fragment_createContest());
        adapter.addFragment(new fragment_upcomingContest());
        adapter.addFragment(new fragment_joinedContest());


        mViewPager.postDelayed((Runnable) () -> {
            mViewPager.setAdapter(adapter);
            TabLayout tablayout = (TabLayout) findViewById(R.id.tabs);
            tablayout.setupWithViewPager(mViewPager);
            mViewPager.setCurrentItem(CREATE_CONTEST);
//        for giving icon to them
            tablayout.getTabAt(0).setText("create");
            tablayout.getTabAt(1).setText("upcoming");
            tablayout.getTabAt(2).setText("joined");
        }, 1000);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void setupBottomNavigationView() {
        Log.d(TAG, " setupBottomNavigationView:setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.BottomNavViewBar);
        BottomNaavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx,this);
        BottomNaavigationViewHelper.enableNavigation(contestMainActivity.this, this, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }

    private void checkCurrentuser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentuser:check if current user logged in");
        if (user == null) startActivity(new Intent(contestMainActivity.this, LoginActivity.class));
    }


    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            mUser = firebaseAuth.getCurrentUser();
            if (mUser == null) {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                Log.d(TAG, "onAuthStateChanged: navigating to login");
                SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                new android.app.AlertDialog.Builder(mContext)
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
            } else Log.d(TAG, "onAuthStateChanged: signed_in:" + mUser.getUid());
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mViewPager.setCurrentItem(CREATE_CONTEST);
        checkCurrentuser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
    }


}