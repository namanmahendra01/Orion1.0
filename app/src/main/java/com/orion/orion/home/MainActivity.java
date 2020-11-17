package com.orion.orion.home;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.Adapters.SectionPagerAdapter;
import com.orion.orion.Notifications.Token;
import com.orion.orion.R;
import com.orion.orion.login.login;
import com.orion.orion.models.Chat;
import com.orion.orion.util.BottomNaavigationViewHelper;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_NUM = 0;
    private static final int HOME_FRAGMENT = 1;
    private static final int REQ_CODE_VERSION_UPDATE = 530;
    public TabLayout tablayout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ViewPager mViewPager;
    private FrameLayout mFramelayoutl;
    private RelativeLayout mRelativeLayout;
    private Context mContext;
    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        Log.d(TAG, "onCreate:starting.");
        mAuth = FirebaseAuth.getInstance();
        mViewPager = findViewById(R.id.viewpager_container);
        mFramelayoutl = findViewById(R.id.container);
        mRelativeLayout = findViewById(R.id.relLayoutParent);

        checkForAppUpdate();
        setupBottomNavigationView();
        checkCurrentuser(mAuth.getCurrentUser());
        setupFirebaseAuth();
        setupViewPager();
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void checkCurrentuser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentuser:check if current user logged in");
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
        } catch (NullPointerException e) {
            Log.e(TAG, "updateToken: " + e.getMessage());
        }
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            checkCurrentuser(user);
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                settings.edit().clear().apply();
                PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().clear().apply();
                Intent intent = new Intent(mContext, login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mAuth.signOut();
                startActivity(intent);
            }
        };
    }

    private void checkForAppUpdate() {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(mContext);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Create a listener to track request state updates.
        installStateUpdatedListener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(InstallState installState) {
                // Show module progress, log state, or install the update.
                if (installState.installStatus() == InstallStatus.DOWNLOADED)
                    // After the update is downloaded, show a notification
                    // and request user confirmation to restart the app.
                    popupSnackbarForCompleteUpdateAndUnregister();
            }
        };

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // Request the update.
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                    // Before starting an update, register a listener for updates.
                    appUpdateManager.registerListener(installStateUpdatedListener);
                    // Start an update.
                    startAppUpdateFlexible(appUpdateInfo);
                } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Start an update.
                    startAppUpdateImmediate(appUpdateInfo);
                }
            }
        });
    }

    private void startAppUpdateImmediate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    MainActivity.REQ_CODE_VERSION_UPDATE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    private void startAppUpdateFlexible(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    MainActivity.REQ_CODE_VERSION_UPDATE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            unregisterInstallStateUpdListener();
        }
    }

    /**
     * Displays the snackbar notification and call to action.
     * Needed only for Flexible app update
     */
    private void popupSnackbarForCompleteUpdateAndUnregister() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_main_layout), "An update has just been downloaded.", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(getResources().getColor(R.color.scheme8));
        snackbar.show();

        unregisterInstallStateUpdListener();
    }

    /**
     * Checks that the update is not stalled during 'onResume()'.
     * However, you should execute this check at all app entry points.
     */
    private void checkNewAppVersionState() {
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            //FLEXIBLE:
                            // If the update is downloaded but not installed,
                            // notify the user to complete the update.
                            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                                popupSnackbarForCompleteUpdateAndUnregister();
                            }

                            //IMMEDIATE:
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                startAppUpdateImmediate(appUpdateInfo);
                            }
                        });

    }

    /**
     * Needed only for FLEXIBLE update
     */
    private void unregisterInstallStateUpdListener() {
        if (appUpdateManager != null && installStateUpdatedListener != null)
            appUpdateManager.unregisterListener(installStateUpdatedListener);
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQ_CODE_VERSION_UPDATE && resultCode != RESULT_OK) {
            //RESULT_OK / RESULT_CANCELED / RESULT_IN_APP_UPDATE_FAILED
            Log.d(TAG, "Update flow failed! Result code: " + resultCode);
            // If the update is cancelled or fails,
            // you can request to start the update again.
            unregisterInstallStateUpdListener();
        }
    }

    @Override
    protected void onResume() {
        checkCurrentuser(mAuth.getCurrentUser());
        checkNewAppVersionState();
        super.onResume();
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
        unregisterInstallStateUpdListener();
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
        tablayout = findViewById(R.id.tabs);
        tablayout.setupWithViewPager(mViewPager);
        tablayout.getTabAt(0).setIcon(R.drawable.ic_bell_black);
        tablayout.getTabAt(1).setText("ORION");
        tablayout.getTabAt(2).setIcon(R.drawable.ic_chat_black);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_users)).child(mAuth.getCurrentUser().getUid())
                .child("Notifications")
                .orderByKey()
                .limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                if (snapshot1.child("seen").getValue().equals("true"))
                                    tablayout.getTabAt(0).setIcon(R.drawable.ic_bell_black);
                                if (snapshot1.child("seen").getValue().equals("false")) {
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

        checkMessageSeen(mContext);
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tablayout.getSelectedTabPosition() == 0) {
                    tablayout.getTabAt(0).setIcon(R.drawable.ic_bell_black);
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    db.child(getString(R.string.dbname_users))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Notifications")
                            .orderByChild("seen")
                            .equalTo("false")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                            if (tablayout.getSelectedTabPosition() == 0) {
                                                db.child(getString(R.string.dbname_users))
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child("Notifications")
                                                        .child(snapshot1.getKey())
                                                        .child("seen")
                                                        .setValue("true");
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
                if (tablayout.getSelectedTabPosition() == 2) checkMessageSeen(mContext);
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
                .child(mAuth.getCurrentUser().getUid());
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
                                            Log.d(TAG, "onDataChange: " + ds);
                                            if (!chat.getIfs() && chat.getrid().equals(mAuth.getCurrentUser().getUid())) {
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

                    if (x[0] > 0) break;
                    else tablayout.getTabAt(2).setIcon(R.drawable.ic_chat_black);
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
        if (mFramelayoutl.getVisibility() == View.VISIBLE) showLayout();
    }

    private void setupBottomNavigationView() {
        Log.d(TAG, " setupBottomNavigationView:setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.BottomNavViewBar);
        BottomNaavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNaavigationViewHelper.enableNavigation(MainActivity.this, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}