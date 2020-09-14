package com.orion.orion.contest.joined;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orion.orion.R;
import com.orion.orion.Adapters.SectionPagerAdapter;

public class joined_contest_overview_activity extends AppCompatActivity {
    private static final String TAG ="contest";

    private static final int CREATE_CONTEST = 1;



    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ViewPager mViewPager;
    private FrameLayout mFramelayoutl;
    private RelativeLayout mRelativeLayout;
    String mUid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_contest_overview_activity);
        Log.d(TAG,"onCreate: started.");
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFramelayoutl = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);
        setupFirebaseAuth();
        ;

        setupViewPager();

        Intent i =getIntent();
        String contestkey=i.getStringExtra("contestId");
        String userid=i.getStringExtra("userId");







    }


    //    for adding 3 tabs -media,home,message
    private void setupViewPager() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new fragment_joinedContest_details());
        adapter.addFragment(new fragment_overview());
        adapter.addFragment(new fragment_marks_and_votes());

        mViewPager.setAdapter(adapter);

        TabLayout tablayout = (TabLayout) findViewById(R.id.tabs);
        tablayout.setupWithViewPager(mViewPager);
//        for giving icon to them
        tablayout.getTabAt(0).setText("Contest");
        tablayout.getTabAt(1).setText("Overview");
        tablayout.getTabAt(2).setText("Vote/Marks");





    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");

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
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mViewPager.setCurrentItem(CREATE_CONTEST);



    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
