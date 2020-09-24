package com.orion.orion.contest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.R;
import com.orion.orion.contest.create.fragment_createContest;
import com.orion.orion.contest.joined.fragment_joinedContest;
import com.orion.orion.contest.upcoming.fragment_upcomingContest;
import com.orion.orion.login.login;
import com.orion.orion.util.BottomNaavigationViewHelper;
import com.orion.orion.Adapters.SectionPagerAdapter;

import java.sql.Time;
import java.sql.Timestamp;

public class contestMainActivity extends AppCompatActivity {
    private static final String TAG ="contest";
    private static final int ACTIVITY_NUM =2;
    private static final int CREATE_CONTEST = 1;
    public static final String TIME_SERVER = "time-a.nist.gov";



    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ViewPager mViewPager;
    private FrameLayout mFramelayoutl;
    private RelativeLayout mRelativeLayout;
    String mUid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest);
        Log.d(TAG,"onCreate: started.");
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFramelayoutl = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);
        setupBottomNavigationView();
        setupFirebaseAuth();
        checkCurrentuser(mAuth.getCurrentUser());

        hideSoftKeyboard();
        setupViewPager();


    }


    //    for adding 3 tabs -media,home,message
    private void setupViewPager() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
//
        adapter.addFragment(new fragment_createContest());
        adapter.addFragment(new fragment_upcomingContest());
        adapter.addFragment(new fragment_joinedContest());



//        mViewPager.setAdapter(adapter);

//        TabLayout tablayout = (TabLayout) findViewById(R.id.tabs);
//        tablayout.setupWithViewPager(mViewPager);
//
//
////        for giving icon to them
//        tablayout.getTabAt(0).setText("create");
//        tablayout.getTabAt(1).setText("upcoming");
//        tablayout.getTabAt(2).setText("joined");
        mViewPager.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mViewPager.setAdapter(adapter);

//        mViewPager.setAdapter(adapter);

                                    TabLayout tablayout = (TabLayout) findViewById(R.id.tabs);
                                    tablayout.setupWithViewPager(mViewPager);
                                    mViewPager.setCurrentItem(CREATE_CONTEST);

//        for giving icon to them
                                    tablayout.getTabAt(0).setText("create");
                                    tablayout.getTabAt(1).setText("upcoming");
                                    tablayout.getTabAt(2).setText("joined");
                                }
                            }, 10);



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
    private void setupBottomNavigationView(){
        Log.d(TAG," setupBottomNavigationView:setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx=( BottomNavigationViewEx)findViewById(R.id.BottomNavViewBar);
        BottomNaavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNaavigationViewHelper.enableNavigation(contestMainActivity.this,this,bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem=menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }
    private void checkCurrentuser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentuser:check if current user logged in");
        if (user == null) {
            Intent intent = new Intent(contestMainActivity.this, login.class);
            startActivity(intent);
        }




    }


    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                checkCurrentuser(user);
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
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
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
