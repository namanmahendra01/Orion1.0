package com.orion.orion.contest.Contest_Evaluation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orion.orion.R;
import com.orion.orion.Adapters.SectionPagerAdapter;
import com.orion.orion.contest.create.CreatedActivity;
import com.orion.orion.login.LoginActivity;

public class ContestEvaluationActivity extends AppCompatActivity {
    private static final String TAG ="contest";
    private final Context mContext = this;

    private TextView mTopToolbarTitle;
    private ImageView mBackArrow;
    private ViewPager mViewPager;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_view);

        mViewPager = findViewById(R.id.viewpager_container);
        mTopToolbarTitle = findViewById(R.id.titleTopBar);
        mBackArrow = findViewById(R.id.backarrow);

        mTopToolbarTitle.setText("Contest Evaluation");
        mBackArrow.setOnClickListener(v -> startActivity(new Intent(mContext, CreatedActivity.class)));

        setupFirebaseAuth();
        setupViewPager();
    }

    private void setupViewPager() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new fragmentContestEdit());
        adapter.addFragment(new fragmentContestParticipants());
        adapter.addFragment(new fragmentContestOverview());
        mViewPager.setAdapter(adapter);
        TabLayout tablayout = findViewById(R.id.tabs);
        tablayout.setupWithViewPager(mViewPager);
        tablayout.getTabAt(0).setText("Edit");
        tablayout.getTabAt(1).setText("Participants");
        tablayout.getTabAt(2).setText("Overview");
        mViewPager.setCurrentItem(1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setupFirebaseAuth() {
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
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
    }

}
