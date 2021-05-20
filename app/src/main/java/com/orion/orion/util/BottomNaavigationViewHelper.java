package com.orion.orion.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.chat.MessagesActivity;
import com.orion.orion.NotificationActivity;
import com.orion.orion.R;
import com.orion.orion.contest.contestMainActivity;
import com.orion.orion.LeaderboardActivity;
import com.orion.orion.models.Chat;
import com.orion.orion.profile.profile;

import java.security.AccessControlContext;

public class BottomNaavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";
    static int curItem, prevItem;


    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx, Context context) {
        Log.d(TAG, "set BottomNavigationView Setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableShiftingMode(true);
        bottomNavigationViewEx.setTextVisibility(true);
        bottomNavigationViewEx.setTextSize(10);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(context.getString(R.string.dbname_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(context.getString(R.string.field_Notifications))
                .orderByKey()
                .limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                if (snapshot1.child(context.getString(R.string.field_if_seen)).getValue().equals("false")) {

                                    bottomNavigationViewEx.getMenu().getItem(3).setIcon(R.drawable.ic_noti_red);
                                    break;
                                }

                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        checkMessageSeen(context,bottomNavigationViewEx);
    }

    public static void enableNavigation(final Context context, final Activity callingActivity, BottomNavigationViewEx view) {
        view.setOnNavigationItemSelectedListener(item -> {


            curItem = item.getItemId();
            if (curItem != prevItem) {
                switch (item.getItemId()) {
                    case R.id.ic_contest:
                        prevItem = curItem;
                        item.setEnabled(false);
                        Intent intent1 = new Intent(context, contestMainActivity.class);
                        context.startActivity(intent1);
                        break;
                    case R.id.ic_chat:
                        prevItem = curItem;
                        checkMessageSeen(context, view);
                        item.setEnabled(false);
                        Intent intent2 = new Intent(context, MessagesActivity.class);
                        context.startActivity(intent2);
                        break;
                    case R.id.ic_leaderboard:
                        prevItem = curItem;
                        item.setEnabled(false);
                        Intent intent3 = new Intent(context, LeaderboardActivity.class);
                        context.startActivity(intent3);
                        break;
                    case R.id.ic_notification:

                        prevItem = curItem;
                        view.postDelayed((Runnable) () -> {
                        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();
                        db1.child(context.getString(R.string.dbname_users))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(context.getString(R.string.field_Notifications))
                                .orderByChild(context.getString(R.string.field_if_seen))
                                .equalTo("false")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                if (view.getMenuItemPosition(item)== 3) {
                                                    db1.child(context.getString(R.string.dbname_users))
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child(context.getString(R.string.field_Notifications))
                                                            .child(snapshot1.getKey())
                                                            .child(context.getString(R.string.field_if_seen))
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
                        item.setEnabled(false);
                        Intent intent4 = new Intent(context, NotificationActivity.class);
                        context.startActivity(intent4);
                        break;
                    case R.id.ic_profile:
                        prevItem = curItem;
                        Intent intent5 = new Intent(context, profile.class);
                        context.startActivity(intent5);
                        break;
                }
            }
            return false;
        });

    }

    public static void checkMessageSeen(Context context, BottomNavigationViewEx view) {

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
                                                view.getMenu().getItem(1).setIcon(R.drawable.ic_msgw_red);
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
                        view.getMenu().getItem(1).setIcon(R.drawable.ic_msgw);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}