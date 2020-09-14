package com.orion.orion.contest.result;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.orion.orion.R;
import com.orion.orion.Adapters.SectionPagerAdapter;

public class ResultDeclaredActivity extends AppCompatActivity {
    private static final String TAG ="contest";
    private static final int ACTIVITY_NUM =2;
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
        setContentView(R.layout.activity_result_declared);
        Log.d(TAG,"onCreate: started.");
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFramelayoutl = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);


        setupViewPager();






    }




    //   ************************FIREBASE****************************

    //    for adding 3 tabs -media,home,message
    private void setupViewPager() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new fragment_result_overview());
        adapter.addFragment(new fragment_contest_detail_result());

        mViewPager.setAdapter(adapter);

        TabLayout tablayout = (TabLayout) findViewById(R.id.tabs);
        tablayout.setupWithViewPager(mViewPager);
//        for giving icon to them
        tablayout.getTabAt(0).setText("Result");
        tablayout.getTabAt(1).setText("Contest");


    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


}
