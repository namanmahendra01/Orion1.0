package com.orion.orion.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.R;
import com.orion.orion.contest.contestMainActivity;
import com.orion.orion.explore.Explore;
import com.orion.orion.home.MainActivity;
import com.orion.orion.leaderboard.LeaderboardActivity;
import com.orion.orion.profile.profile;

public class BottomNaavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";
    static int  curItem,prevItem;


    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx) {
        Log.d(TAG, "set BottomNavigationView Setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableShiftingMode(true);
        bottomNavigationViewEx.setTextVisibility(true);
        bottomNavigationViewEx.setTextSize(10);

    }

    public static void enableNavigation(final Context context, final Activity callingActivity ,BottomNavigationViewEx view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            public boolean onNavigationItemSelected(MenuItem item) {
                curItem= item.getItemId();

                Log.d(TAG, "onNavigationItemSelected: "+curItem+" "+prevItem);

                if (curItem!=prevItem) {
                    switch (item.getItemId()) {

                        case R.id.ic_house:
                            prevItem = curItem;
                            item.setEnabled(false);
                            Intent intent1 = new Intent(context, MainActivity.class);
                            context.startActivity(intent1);
                            break;

                        case R.id.ic_notification:
                            prevItem = curItem;

                            item.setEnabled(false);

                            Intent intent2 = new Intent(context, Explore.class);
                            context.startActivity(intent2);

                            break;

                        case R.id.ic_contest:
                            prevItem = curItem;

                            item.setEnabled(false);

                            Intent intent3 = new Intent(context, contestMainActivity.class);
                            context.startActivity(intent3);
                            break;

                        case R.id.ic_leaderboard:
                            prevItem = curItem;

                            item.setEnabled(false);

                            Intent intent4 = new Intent(context, LeaderboardActivity.class);
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
            }


        });
    }
}