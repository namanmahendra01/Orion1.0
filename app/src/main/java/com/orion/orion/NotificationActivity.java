package com.orion.orion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.Adapters.AdapterNotification2;
import com.orion.orion.models.Notification;
import com.orion.orion.util.BottomNaavigationViewHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import static java.security.AccessController.getContext;

public class NotificationActivity extends AppCompatActivity {
    private static final String TAG = "mediafragment";
    private static final int ACTIVITY_NUM = 3;

    private Context mContext;

    RecyclerView notificationRv;
    private TextView clearNotification;
    private int mResults;

    private FirebaseAuth fAuth;
    private ArrayList<Notification> notifyList;
    private AdapterNotification2 adapterNotification2;
    int x = 0;
    private ArrayList<Notification> paginatedNotifications;
    ImageView emptyNotification;
    //    SP
    Gson gson;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification2);
        mContext = NotificationActivity.this;
        notificationRv = findViewById(R.id.recycler_view);
        clearNotification = findViewById(R.id.clearNotification);
        emptyNotification = findViewById(R.id.emptyNotification);
        setupBottomNavigationView();
        Log.d(TAG, " context"+this+"  "+getContext()+"  "+getApplicationContext());

        //          Initialize SharedPreference variables
        sp = mContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        gson = new Gson();
        notificationRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        notificationRv.setLayoutManager(linearLayoutManager);
        notifyList = new ArrayList<>();
        notificationRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE)
                    displayMoreNotification();
            }
        });
        fAuth = FirebaseAuth.getInstance();
        getNotifcationFromSP();
        clearNotification.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("You want to delete all the notification")
                    .setTitle("Are you sure?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        notifyList.clear();
                        if (paginatedNotifications != null) paginatedNotifications.clear();
                        SharedPreferences.Editor editor = sp.edit().remove("nl");
                        editor.apply();
                        if (adapterNotification2 != null)
                            adapterNotification2.notifyDataSetChanged();
                        FirebaseUser user = fAuth.getCurrentUser();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users))
                                .child(user.getUid())
                                .child(getString(R.string.field_Notifications));
                        reference.removeValue();
                        //delete from database here then refresh for u naman
                        displayMoreNotification();
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                    .show();
        });
    }

    //  fetching filtered notificationList  from SharedPreferences
    private void getNotifcationFromSP() {
        String json = sp.getString("nl", null);
        Type type = new TypeToken<ArrayList<Notification>>() {
        }.getType();
        notifyList = gson.fromJson(json, type);
        if (notifyList == null || notifyList.size() == 0) {    //        if no arrayList is present
            notifyList = new ArrayList<>();
            readNotification();  //            make new Arraylist
        } else checkUpdate();

    }

    private void checkUpdate() {
        DatabaseReference refer = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users));
        refer.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_Notifications))
                .orderByKey()
                .limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            if (snapshot1.exists() && notifyList.size() != 0) {
                                if (notifyList == null || notifyList.get(0).getTim().equals(snapshot1.getKey()))
                                    displayNotification();
                                else updateNotificationList();
                            }
                            displayNotification();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateNotificationList() {
        Collections.reverse(notifyList);
        DatabaseReference refer = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users));
        refer.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_Notifications))
                .orderByKey()
                .startAt(notifyList.get(notifyList.size() - 1).getTim())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            int x = 0;
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                x++;
                                if (x == 1) continue;
                                Notification notification = snapshot1.getValue(Notification.class);
                                notifyList.add(notification);
                            }
                            Collections.reverse(notifyList);
                            //                        Add newly Created ArrayList to Shared Preferences
                            SharedPreferences.Editor editor = sp.edit();
                            String json = gson.toJson(notifyList);
                            editor.putString("nl", json);
                            editor.apply();
                            displayNotification();
                        } else readNotification();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void readNotification() {
        FirebaseUser user = fAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users))
                .child(user.getUid()).child(getString(R.string.field_Notifications));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifyList.clear();
                int x = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    x++;
                    Notification notification = snapshot.getValue(Notification.class);
                    notifyList.add(notification);
                    if (x == dataSnapshot.getChildrenCount()) {
                        Collections.reverse(notifyList);
//                        Add newly Created ArrayList to Shared Preferences
                        SharedPreferences.Editor editor = sp.edit();
                        String json = gson.toJson(notifyList);
                        editor.putString("nl", json);
                        editor.apply();
                        displayNotification();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayNotification() {
        paginatedNotifications = new ArrayList<>();
        if (notifyList != null) {

            try {
                int iteration = notifyList.size();
                if (iteration == 0) emptyNotification.setVisibility(View.VISIBLE);
                else {
                    emptyNotification.setVisibility(View.GONE);
                    if (iteration > 10) iteration = 10;
                    mResults = 10;
                    for (int i = 0; i < iteration; i++)
                        paginatedNotifications.add(notifyList.get(i));
                    adapterNotification2 = new AdapterNotification2(mContext, paginatedNotifications);
                    adapterNotification2.setHasStableIds(true);
                    notificationRv.setAdapter(adapterNotification2);
                    x++;
                }
            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());
            }
        }
    }

    public void displayMoreNotification() {
        try {
            if (notifyList.size() > mResults && notifyList.size() > 0) {
                int iterations;
                if (notifyList.size() > (mResults + 20)) iterations = 20;
                else iterations = notifyList.size() - mResults;
                for (int i = mResults; i < mResults + iterations; i++)
                    paginatedNotifications.add(notifyList.get(i));
                notificationRv.post(() -> adapterNotification2.notifyItemRangeInserted(mResults, iterations));
                mResults = mResults + iterations;
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());
        }
    }
    private void setupBottomNavigationView() {
        Log.d(TAG, " setupBottomNavigationView:setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.BottomNavViewBar);
        BottomNaavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNaavigationViewHelper.enableNavigation(NotificationActivity.this, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}