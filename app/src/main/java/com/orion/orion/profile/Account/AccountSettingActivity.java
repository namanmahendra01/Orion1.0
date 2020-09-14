package com.orion.orion.profile.Account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.Adapters.SectionStatePagerAdapter;
import com.orion.orion.R;
import com.orion.orion.activity_view_video;
import com.orion.orion.util.BottomNaavigationViewHelper;
import com.orion.orion.util.FirebaseMethods;

import java.util.ArrayList;

public class AccountSettingActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingActivity";
    private static final int ACTIVITY_NUM = 4;
    public SectionStatePagerAdapter pagerAdapter;
    private  Context mcontext;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsetting);

        mcontext = AccountSettingActivity.this;
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutAccountSetting);
        setupBottomNavigationView();
        setupSettingList();
        getIncomingIntent();

//     ******************   setup backarrow for navigation***********
        ImageView backarrow = (ImageView) findViewById(R.id.backarrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onCreate : navigation back to'profileactivity' ");
                finish();
            }
        });
    }

     private void getIncomingIntent() {
         Intent intent = getIntent();
         if (intent.hasExtra(getString(R.string.selected_image)) || intent.hasExtra(getString(R.string.selected_bitmap))){

             if (intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile))) {

                 if (intent.hasExtra(getString(R.string.selected_image))) {
                     Log.d(TAG,"profile pic" + intent.hasExtra(getString(R.string.selected_image)));
                    String imgURL = intent.getStringExtra(getString(R.string.selected_image));

                     FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingActivity.this);
                     firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                             intent.getStringExtra(getString(R.string.selected_image)), null);
                 } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {


                     FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingActivity.this);
                     firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                             null, (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap)));
                 }

             }
     }


        if (intent.hasExtra(getString(R.string.calling_activity))) {
            Intent i = new Intent(AccountSettingActivity.this,EditProfile.class);
            startActivity(i);

        }
    }



    private void setupSettingList() {
        ListView listview = (ListView) findViewById(R.id.lvAccountSettings);
        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile));
        options.add("Password Reset");
        options.add("Contest");
        options.add("Help");
        options.add("About");
        options.add(getString(R.string.sign_out));//fragment 1
        ArrayAdapter adapter = new ArrayAdapter(mcontext, android.R.layout.simple_list_item_1, options);

        listview.setAdapter((adapter));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    Intent i = new Intent(AccountSettingActivity.this,EditProfile.class);
                    startActivity(i);
                } if (position==1){
                    Intent i = new Intent(AccountSettingActivity.this, Password_Reset.class);
                    startActivity(i);
                } if (position==2){
                    Intent i = new Intent(AccountSettingActivity.this, activity_view_video.class);
                    startActivity(i);
                } if (position==3){
                    Intent i = new Intent(AccountSettingActivity.this, Help.class);
                    startActivity(i);
                } if (position==4){
                    Intent i = new Intent(AccountSettingActivity.this,About.class);
                    startActivity(i);
                } if (position==5){
                    Intent i = new Intent(AccountSettingActivity.this,SignOut.class);
                    startActivity(i);
                }

            }
        });
    }

    private void setupBottomNavigationView() {
        Log.d(TAG, " setupBottomNavigationView:setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.BottomNavViewBar);
        BottomNaavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNaavigationViewHelper.enableNavigation(mcontext,this, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);


    }


}