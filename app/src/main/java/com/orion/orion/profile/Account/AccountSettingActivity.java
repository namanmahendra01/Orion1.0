package com.orion.orion.profile.Account;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.R;
import com.orion.orion.login.LoginActivity;
import com.orion.orion.profile.Account.FanFolllowing.FanFollowList;
import com.orion.orion.util.BottomNaavigationViewHelper;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class AccountSettingActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingActivity";
    private static final int ACTIVITY_NUM = 4;
    private Context mContext;
    private ImageView backarrow;
    private TextView mTitleTopBar;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private ListView listview;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsetting);

        mContext = AccountSettingActivity.this;
        listview = findViewById(R.id.lvAccountSettings);
        backarrow = findViewById(R.id.backarrow);
        mTitleTopBar = findViewById(R.id.titleTopBar);
        backarrow.setOnClickListener(v -> finish());
        mTitleTopBar.setText("Options");

//        setupBottomNavigationView();
        setupFirebaseAuth();
        setupSettingList();
        Log.d(TAG, "onCreate: started");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupSettingList() {
        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile));
        options.add("Fans/Following List");
        options.add("Password Reset");
        options.add("Contest");
        options.add("Contact us");
        options.add("Report a Bug/feedback CC_FillFormActivity");
        options.add("Privacy Policy");
        options.add("Terms and Conditions");
        options.add("About");
        options.add(getString(R.string.sign_out));//fragment 1
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, options);
        listview.setAdapter((adapter));
        listview.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(mContext, EditProfile.class));
                    break;
                case 1:
                    startActivity(new Intent(mContext, FanFollowList.class));
                    break;
                case 2:
                    startActivity(new Intent(mContext, Password_Reset.class));
                    break;
                case 3:
                    startActivity(new Intent(mContext, Contest.class));
                    break;
                case 4:
                    String url = "https://api.whatsapp.com/send?phone=" + "919997719032";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    break;
                case 5:
                    String url2 = "https://forms.gle/QikpZcJHz1h64Zvn6";
                    Intent i2 = new Intent(Intent.ACTION_VIEW);
                    i2.setData(Uri.parse(url2));
                    startActivity(i2);
                    break;
                case 6:
                    String url3 = getString(R.string.Privacy_Policy);
                    Intent i3 = new Intent(Intent.ACTION_VIEW);
                    i3.setData(Uri.parse(url3));
                    startActivity(i3);
                    break;
                case 7:
                    String url4 = getString(R.string.Terms_and_Condition);
                    Intent i4 = new Intent(Intent.ACTION_VIEW);
                    i4.setData(Uri.parse(url4));
                    startActivity(i4);
                    break;
                case 8:
                    startActivity(new Intent(mContext, About.class));
                    break;
                case 9:
                    new AlertDialog.Builder(mContext)
                            .setTitle("Log Out")
                            .setMessage("Are you sure you want to log out?")
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                                settings.edit().clear().apply();
                                PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().clear().apply();
                                Intent intent = new Intent(mContext, LoginActivity.class);
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
                        .setMessage("We will be logging you out. \n Please try to log in again")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            settings.edit().clear().apply();
                            if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
                            dialog.dismiss();
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