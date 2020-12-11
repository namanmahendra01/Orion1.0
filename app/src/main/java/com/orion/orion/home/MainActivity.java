package com.orion.orion.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.Notifications.Token;
import com.orion.orion.R;
import com.orion.orion.login.login;
import com.orion.orion.models.Chat;
import com.orion.orion.util.BottomNaavigationViewHelper;
import com.orion.orion.Adapters.SectionPagerAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        {


    private static final String TAG = "MainActivity";
    private static final int HOME_FRAGMENT = 1;
            public TabLayout tablayout;
    private static final int ACTIVITY_NUM = 0;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ViewPager mViewPager;
    private FrameLayout mFramelayoutl;
    private RelativeLayout mRelativeLayout;
    LinearLayout prom;
        Context context=MainActivity.this;
    String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate:starting.");
        mAuth = FirebaseAuth.getInstance();
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFramelayoutl = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);


        setupBottomNavigationView();
        checkCurrentuser(mAuth.getCurrentUser());
        setupFirebaseAuth();

        setupViewPager();


//        update Token
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    protected void onResume() {
        checkCurrentuser(mAuth.getCurrentUser());
        super.onResume();
    }





    //    *********************FIREBASE***************************
    private void checkCurrentuser(FirebaseUser user) {
        if (user == null) {
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
        }


    }

            public void updateToken(String token) {

                try {


                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
                    Token mToken = new Token(token);
                    ref.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(mToken);
                }catch (NullPointerException e){
                    Log.e(TAG, "updateToken: "+e.getMessage() );
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
                    Log.d(TAG, "onAuthStateChanged: navigating to login");
                    SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                    new android.app.AlertDialog.Builder(context)
                            .setTitle("No user logon found")
                            .setMessage("We will be logging u out. \n Please try to log in again")
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                Intent intent = new Intent(context, login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                settings.edit().clear().apply();
                                if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
                                startActivity(intent);
                            })
                            .show();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        mViewPager.setCurrentItem(HOME_FRAGMENT);
        checkCurrentuser(mAuth.getCurrentUser());


    }

            @Override
            public void onDestroy() {
                super.onDestroy();
            }

            @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //   ************************FIREBASE****************************


    //    for adding 3 tabs -media,home,message
    private void setupViewPager() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new notificationFragment());
        adapter.addFragment(new Homefragment());
        adapter.addFragment(new messagesfragment());

        mViewPager.setAdapter(adapter);

        tablayout = (TabLayout) findViewById(R.id.tabs);
        tablayout.setupWithViewPager(mViewPager);
//        for giving icon to them
        tablayout.getTabAt(0).setIcon(R.drawable.ic_bell_black);
        tablayout.getTabAt(1).setText("ORION");
        tablayout.getTabAt(2).setIcon(R.drawable.ic_chat_black);


        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users))

                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_unfollowed_Me));
               reference3 .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            int x=0;
                            for (DataSnapshot snapshot1:snapshot.getChildren()){
                                x++;
                                String mUser=snapshot1.getKey();

                                //               remove from following list
                                SharedPreferences sp =getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                                Gson gson=new Gson();
                                String json =sp.getString("fl",null);
                                Type type= new TypeToken<ArrayList<String>>() {}.getType();
                                ArrayList<String> list= new ArrayList<String>();
                                list=gson.fromJson(json,type);
                                if (list==null){

                                }else{
                                    list.remove(mUser);

                                }
//                 save following list
                                SharedPreferences.Editor editor=sp.edit();
                                json =gson.toJson(list);
                                editor.putString("fl",json);
                                editor.apply();


//              update following list
                                json =sp.getString("removefollowing",null);
                                type= new TypeToken<ArrayList<String>>() {}.getType();
                                ArrayList<String> ulist= new ArrayList<String>();
                                ulist=gson.fromJson(json,type);
                                if (ulist==null){
                                    ulist= new ArrayList<String>();
                                    ulist.add(mUser);
                                }else{
                                    if (!ulist.contains(mUser)){
                                        ulist.add(mUser);

                                    }
                                }
//                save update list
                                editor=sp.edit();
                                json =gson.toJson(ulist);
                                editor.putString("removefollowing",json);
                                editor.apply();


//              update following list
                                json =sp.getString("addfollowing",null);
                                type= new TypeToken<ArrayList<String>>() {}.getType();
                                ArrayList<String> ulist2= new ArrayList<String>();
                                ulist2=gson.fromJson(json,type);
                                if (ulist2==null){

                                }else{
                                    if (ulist2.contains(mUser)){
                                        ulist2.remove(mUser);
//                save update list
                                        editor=sp.edit();
                                        json =gson.toJson(ulist2);
                                        editor.putString("addfollowing",json);
                                        editor.apply();
                                    }
                                }


                                if (x==snapshot.getChildrenCount()){
                                    reference3.removeValue();
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_Notifications)).orderByKey().limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                                if (snapshot1.child(getString(R.string.field_if_seen)).getValue().equals("true")) {

                                    tablayout.getTabAt(0).setIcon(R.drawable.ic_bell_black);

                                }
                                if (snapshot1.child(getString(R.string.field_if_seen)).getValue().equals("false")) {
                                    tablayout.getTabAt(0).setIcon(R.drawable.ic_bell_red);
                                    break;
                                }

                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        checkMessageSeen(context);


        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tablayout.getSelectedTabPosition() == 0) {


                    tablayout.getTabAt(0).setIcon(R.drawable.ic_bell_black);

                    tablayout.postDelayed((Runnable) () -> {
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child(getString(R.string.dbname_users))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(getString(R.string.field_Notifications))
                                .orderByChild(getString(R.string.field_if_seen))
                                .equalTo("false")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                if (tablayout.getSelectedTabPosition() == 0) {
                                                    db.child(getString(R.string.dbname_users))
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child(getString(R.string.field_Notifications))
                                                            .child(snapshot1.getKey())
                                                            .child(getString(R.string.field_if_seen))
                                                            .setValue("true");
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }, 10);


                }
                if (tablayout.getSelectedTabPosition()==2){
                    checkMessageSeen(context);
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

            public void checkMessageSeen(Context context) {

                DatabaseReference refer = FirebaseDatabase.getInstance().getReference();
                Query query = refer.child(context.getString(R.string.dbname_Chats))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        final long[] x = {0};
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {


                            refer.child(context.getString(R.string.dbname_ChatList))
                                    .child(dataSnapshot.getValue().toString())
                                    .orderByKey()
                                    .limitToLast(1)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {


                                            for (DataSnapshot ds : snapshot1.getChildren()) {

                                                if (ds.exists()) {

                                                    Chat chat = ds.getValue(Chat.class);
                                                    if (!chat.getIfs()&&chat.getRid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                        tablayout.getTabAt(2).setIcon(R.drawable.ic_chat_red);
                                                        x[0]++;
                                                    }

                                                }


                                            }

                                        }


                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                            if (x[0]>0){

                                break;
                            }else{
                                tablayout.getTabAt(2).setIcon(R.drawable.ic_chat_black);

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            public void hideLayout() {
        mRelativeLayout.setVisibility(View.GONE);
        mFramelayoutl.setVisibility(View.VISIBLE);

    }

    public void showLayout() {
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFramelayoutl.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mFramelayoutl.getVisibility() == View.VISIBLE) {
            showLayout();
        }
    }

    private void setupBottomNavigationView() {
        Log.d(TAG, " setupBottomNavigationView:setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.BottomNavViewBar);
        BottomNaavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNaavigationViewHelper.enableNavigation(MainActivity.this, this, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


}
