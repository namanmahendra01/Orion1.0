package com.orion.orion.leaderboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.Adapters.AdapterItemLeaderboard;
import com.orion.orion.R;
import com.orion.orion.dialogs.BottomSheetFilter;
import com.orion.orion.login.login;
import com.orion.orion.models.ItemLeaderboard;
import com.orion.orion.util.BottomNaavigationViewHelper;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.SNTPClient;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;


public class LeaderboardActivity extends AppCompatActivity implements BottomSheetFilter.BottomSheetListener {
    private static final String TAG = "LeaderboardActivity";
    private static final int ACTIVITY_NUM = 3;
    private static final int ANIMATION_DURATION = 500;
    private static final int LEADERBOAD_SIZE = 20;
    private Context mContext;
    FirebaseMethods firebaseMethods;
    private TextView userItemUsername;
    private TextView userItemRank;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView sortedByTime;
    private TextView sortedByLocation;
    private TextView sortedByType;
    private TextView sortedByDomain;
    private RecyclerView mRecyclerView;
    private AdapterItemLeaderboard mAdapter;
    private DatabaseReference reference;
    //TextView usernameProfile;
    private String time;
    private String locationParameter;
    private String typeParameter;
    private String domainParameter;
    //variables
    private ArrayList<ItemLeaderboard> mList;
    private String currentUser;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(LeaderboardActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
            }
    }


    private void updateLeaderboard() {
        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
            @Override
            public void onTimeReceived(String currentTimeStamp) {
                //initializing formatting for current date
                int currentYear = Integer.parseInt(currentTimeStamp.substring(0, 4));
                int currentMonth = Integer.parseInt(currentTimeStamp.substring(5, 7));
                int currentDate = Integer.parseInt(currentTimeStamp.substring(8, 10));
//                String currentTime = currentTimeStamp.substring(12, currentTimeStamp.length() - 1);
                String currentDateFormat = currentDate + "/" + currentMonth + "/" + currentYear;
                Date date = new Date(currentDateFormat);
                int currentDay = date.getDay();

                Query query = reference.child(getString(R.string.dbname_users));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//                            Log.d(TAG, "updateLeaderboard: " + singleSnapshot);
//                            users currentUser = new users();
//                            currentUser=singleSnapshot.getValue(users.class);
//                            //initializing variables for the updation
//                            String user_id = singleSnapshot.getKey();
//                            assert user_id != null;
//                            assert currentUser != null;
//                            String username = currentUser.getUsername();
//                            String domain = currentUser.getDomain();
//                            String profilePhoto = currentUser.getProfile_photo();
//                            boolean changedFollowers=false;
//                            boolean changedJoinedContest = false;
//                            boolean changedCreateContest = false;
//                            if(currentUser.getChangedFollowers()!=null && currentUser.getChangedFollowers().equals("false")) changedFollowers = currentUser.getChangedFollowers().equals("true");
//                            if(currentUser.getChangedJoinedContest()!=null && currentUser.getChangedJoinedContest().equals("false")) changedJoinedContest = currentUser.getChangedJoinedContest().equals("true");
//                            if(currentUser.getChangedCreateContest()!=null && currentUser.getChangedCreateContest().equals("false")) changedCreateContest = currentUser.getChangedCreateContest().equals("true");
                            String user_id = singleSnapshot.getKey();
                            assert user_id != null;
                            String username = (String) singleSnapshot.child(getString(R.string.field_username)).getValue();
                            String domain = (String) singleSnapshot.child(getString(R.string.field_domain)).getValue();
                            String profilePhoto = (String) singleSnapshot.child(getString(R.string.profile_photo)).getValue();
                            boolean changedFollowers = false;
                            boolean changedJoinedContest = false;
                            boolean changedCreateContest = false;
                            if (singleSnapshot.child(getString(R.string.changedFollowers)).getValue() != null && Objects.equals(singleSnapshot.child(getString(R.string.changedFollowers)).getValue(), "true"))
                                changedFollowers = true;
                            if (singleSnapshot.child(getString(R.string.changedJoinedContest)).getValue() != null && Objects.equals(singleSnapshot.child(getString(R.string.changedJoinedContest)).getValue(), "true"))
                                changedJoinedContest = true;
                            if (singleSnapshot.child(getString(R.string.changedCreatedContest)).getValue() != null && Objects.equals(singleSnapshot.child(getString(R.string.changedCreatedContest)).getValue(), "true"))
                                changedCreateContest = true;

                            //location update
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            assert user != null;

//                            if (user.getUid().equals(user_id)) checkOrGetLocation();

                            Log.d(TAG, "updateLeaderboard: USERID: "+user_id);
                            Log.d(TAG, "updateLeaderboard: posts update");
                            //for posts parameters of leaders according the photos
                            Query query1 = reference.child(getString(R.string.dbname_user_photos)).child(user_id);
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    //initializing temp variables for posts
                                    float all_time = 0;
                                    float yearly = 0;
                                    float last_month = 0;
                                    float this_month = 0;
                                    float last_week = 0;
                                    float this_week = 0;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        long likes = snapshot.child(getString(R.string.field_likes)).getChildrenCount();
                                        long comments = snapshot.child(getString(R.string.field_comment)).getChildrenCount();
                                        float rating = (int) (0.5 + likes + 0.2 * comments);

                                        //calculating date related parameters
                                        String postedTimestamp = (String) snapshot.child(getString(R.string.field_date_createdr)).getValue();
                                        assert postedTimestamp != null;
                                        int postedYear = Integer.parseInt(postedTimestamp.substring(0, 4));
                                        int postedMonth = Integer.parseInt(postedTimestamp.substring(5, 7));
                                        int postedDate = Integer.parseInt(postedTimestamp.substring(8, 10));
//                                        String postedTime = postedTimestamp.substring(12, postedTimestamp.length() - 1);
                                        String postedDateFormat = postedDate + "/" + postedMonth + "/" + postedYear;

                                        //calculating difference of dates in post and current one
                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");
                                        long elapsedDays = 0;
                                        try {
                                            Date date1 = simpleDateFormat.parse(postedDateFormat);
                                            Date date2 = simpleDateFormat.parse(currentDateFormat);
//                                            Log.d(TAG, "onTimeReceived: " + date1);
//                                            Log.d(TAG, "onTimeReceived: " + date2);
                                            assert date1 != null;
                                            assert date2 != null;
                                            elapsedDays = (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
//                                            Log.d(TAG, "onDataChange: " + elapsedDays);
//                                            Log.d(TAG, "onDataChange: " + currentDay);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        //all time one
                                        all_time += rating;
                                        //yearly one
                                        if (currentYear == postedYear) {
                                            yearly += rating;
                                            //same monthly in same years
                                            if (currentMonth == postedMonth)
                                                this_month += rating;
                                                //previous month in same year
                                            else if (currentDate == postedMonth + 1)
                                                last_month += rating;
                                        }
                                        //previous month in different year
                                        else if ((currentYear - 1) == postedYear && currentMonth == 1 && postedMonth == 12) {
                                            last_month += rating;
                                        }
                                        //calculating weekly ones
                                        if (elapsedDays > currentDay && elapsedDays <= (currentDay + 7))
                                            last_week += rating;
                                            //same week in same year and same month
                                        else if (elapsedDays <= currentDay)
                                            this_week += rating;
                                    }
                                    Log.d(TAG, "updateLeaderboard: posts " + all_time + "," + yearly + "," + last_month + "," + this_month + "," + last_week + "," + this_week);
                                    reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_all_time)).child(getString(R.string.field_post)).setValue((int) all_time);
                                    reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_yearly)).child(getString(R.string.field_post)).setValue((int) yearly);
                                    reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_last_month)).child(getString(R.string.field_post)).setValue((int) last_month);
                                    reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_this_month)).child(getString(R.string.field_post)).setValue((int) this_month);
                                    reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_last_week)).child(getString(R.string.field_post)).setValue((int) last_week);
                                    reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_this_week)).child(getString(R.string.field_post)).setValue((int) this_week);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }

                            });

                            Log.d(TAG, "updateLeaderboard: followers update");
                            if (changedFollowers
                                    || !dataSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_followers)).exists()
                                    || !dataSnapshot.child(getString(R.string.field_yearly)).child(getString(R.string.field_followers)).exists()
                                    || !dataSnapshot.child(getString(R.string.field_last_month)).child(getString(R.string.field_followers)).exists()
                                    || !dataSnapshot.child(getString(R.string.field_this_month)).child(getString(R.string.field_followers)).exists()
                                    || !dataSnapshot.child(getString(R.string.field_last_week)).child(getString(R.string.field_followers)).exists()
                                    || !dataSnapshot.child(getString(R.string.field_this_week)).child(getString(R.string.field_followers)).exists()) {
                                //for updating follow parameter of database
                                Query query2 = reference.child(getString(R.string.dbname_follower)).child(user_id);
                                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        //getting updates for contests
                                        int currentNoOfFollowers = (int) dataSnapshot.getChildrenCount();
                                        Query query21 = reference.child(getString(R.string.dbname_leaderboard)).child(user_id);
                                        query21.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                //details entries
                                                int previousNoOfFollowers = 0;
                                                if (dataSnapshot.child(getString(R.string.field_followers)).getValue() != null)
                                                    previousNoOfFollowers = (int) (long) dataSnapshot.child(getString(R.string.field_followers)).getValue();

                                                int all_time;
                                                int yearly = 0;
                                                if (dataSnapshot.child(getString(R.string.field_yearly)).child(getString(R.string.field_contest)).getValue() != null)
                                                    yearly = (int) (long) dataSnapshot.child(getString(R.string.field_yearly)).child(getString(R.string.field_contest)).getValue();
                                                int last_month = 0;
                                                if (dataSnapshot.child(getString(R.string.field_last_month)).child(getString(R.string.field_contest)).getValue() != null)
                                                    last_month = (int) (long) dataSnapshot.child(getString(R.string.field_last_month)).child(getString(R.string.field_contest)).getValue();
                                                int this_month = 0;
                                                if (dataSnapshot.child(getString(R.string.field_this_month)).child(getString(R.string.field_contest)).getValue() != null)
                                                    this_month = (int) (long) dataSnapshot.child(getString(R.string.field_this_month)).child(getString(R.string.field_contest)).getValue();
                                                int last_week = 0;
                                                if (dataSnapshot.child(getString(R.string.field_last_week)).child(getString(R.string.field_contest)).getValue() != null)
                                                    last_week = (int) (long) dataSnapshot.child(getString(R.string.field_last_week)).child(getString(R.string.field_contest)).getValue();
                                                int this_week = 0;
                                                if (dataSnapshot.child(getString(R.string.field_this_week)).child(getString(R.string.field_contest)).getValue() != null)
                                                    this_week = (int) (long) dataSnapshot.child(getString(R.string.field_this_week)).child(getString(R.string.field_contest)).getValue();

                                                //getting last updated entries
                                                String lastUpdatedTimestamp = (String) dataSnapshot.child(getString(R.string.field_last_updated)).getValue();
                                                int lastUpdatedYear = 0;
                                                int lastUpdatedMonth = 0;
                                                int lastUpdatedDate = 0;
//                                          String lastUpdatedTime = "";
                                                assert lastUpdatedTimestamp != null;
                                                if (lastUpdatedTimestamp.length() > 12) {
                                                    lastUpdatedYear = Integer.parseInt(lastUpdatedTimestamp.substring(0, 4));
                                                    lastUpdatedMonth = Integer.parseInt(lastUpdatedTimestamp.substring(5, 7));
                                                    lastUpdatedDate = Integer.parseInt(lastUpdatedTimestamp.substring(8, 10));
//                                              lastUpdatedTime = lastUpdatedTimestamp.substring(12, lastUpdatedTimestamp.length() - 1);
                                                }
                                                String lastUpdatedDateFormat = lastUpdatedDate + "/" + lastUpdatedMonth + "/" + lastUpdatedYear;
                                                int finalLastUpdatedYear = lastUpdatedYear;
                                                int finalLastUpdatedMonth = lastUpdatedMonth;

                                                //calculating difference of dates in post and current one
                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");
                                                long elapsedDays = 0;
                                                try {
                                                    Date date1 = simpleDateFormat.parse(lastUpdatedDateFormat);
                                                    Date date2 = simpleDateFormat.parse(currentDateFormat);
//                                                    Log.d(TAG, "onTimeReceived: " + date1);
//                                                    Log.d(TAG, "onTimeReceived: " + date2);
                                                    assert date1 != null;
                                                    assert date2 != null;
                                                    elapsedDays = (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
//                                                    Log.d(TAG, "onDataChange: " + elapsedDays);
//                                                    Log.d(TAG, "onDataChange: " + currentDay);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }


                                                //updating for transition days of different year
                                                if (finalLastUpdatedYear < currentYear) {
                                                    yearly = 0;
                                                    this_month = 0;
                                                    //updating for transition days of different month in different year
                                                    if (finalLastUpdatedMonth == 12 && currentMonth == 1) {
                                                        last_month = this_month;
                                                        //updating for transition days of different month different year of time span of more than 2 weeks
                                                        if (elapsedDays > currentDay + 7) {
                                                            last_week = 0;
                                                            this_week = 0;
                                                        }
                                                        //updating for transition days of different month different year of previous week
                                                        else if (elapsedDays > currentDay && elapsedDays <= currentDay + 7) {
                                                            last_week = this_week;
                                                            this_week = 0;
                                                        }
                                                    } else last_month = 0;
                                                }
                                                //updating for transition days of same year
                                                else {
                                                    //updating for transition days of different month in different year
                                                    if (finalLastUpdatedMonth < currentMonth) {
                                                        this_month = 0;
                                                        //updating for transition days of just previous month
                                                        if ((finalLastUpdatedMonth - currentMonth) == 1) {
                                                            last_month = this_month;
                                                            //updating for transition days of different month different year of time span of more than 2 weeks
                                                            if (elapsedDays > currentDay + 7) {
                                                                last_week = 0;
                                                                this_week = 0;
                                                            }
                                                            //updating for transition days of different month different year of previous week
                                                            else if (elapsedDays > currentDay && elapsedDays <= currentDay + 7) {
                                                                last_week = this_week;
                                                                this_week = 0;
                                                            }
                                                        } else {
                                                            last_month = 0;
                                                            last_week = 0;
                                                            this_week = 0;
                                                        }
                                                    } else {
                                                        //updating for transition days of same month same year of time span of more than 2 weeks
                                                        if (elapsedDays < currentDay + 7) {
                                                            last_week = 0;
                                                            this_week = 0;
                                                        }
                                                        //updating for transition days of same month same year of time span of less than 2 weeks
                                                        else if (elapsedDays > currentDay && elapsedDays <= currentDay + 7) {
                                                            last_week = this_week;
                                                            this_week = 0;
                                                        }
                                                    }
                                                }


                                                //calculating rating for joined
                                                int rating = currentNoOfFollowers - previousNoOfFollowers;
                                                //updating current instance of increasing followers list
                                                all_time = currentNoOfFollowers;
                                                if (finalLastUpdatedYear == currentYear) {
                                                    yearly += rating;
                                                    if (finalLastUpdatedMonth == currentMonth)
                                                        this_month += rating;
                                                }
                                                if (elapsedDays <= currentDay) this_week += rating;

                                                Log.d(TAG, "updateLeaderboard: followers " + all_time + "," + yearly + "," + last_month + "," + this_month + "," + last_week + "," + this_week);
                                                reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_all_time)).child(getString(R.string.field_followers)).setValue(all_time);
                                                reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_yearly)).child(getString(R.string.field_followers)).setValue(yearly);
                                                reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_last_month)).child(getString(R.string.field_followers)).setValue(last_month);
                                                reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_this_month)).child(getString(R.string.field_followers)).setValue(this_month);
                                                reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_last_week)).child(getString(R.string.field_followers)).setValue(last_week);
                                                reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_this_week)).child(getString(R.string.field_followers)).setValue(this_week);
                                                reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_followers)).setValue(currentNoOfFollowers);

                                                reference.child(getString(R.string.dbname_users)).child(user_id).child(getString(R.string.changedFollowers)).setValue("false");
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }

                            Log.d(TAG, "updateLeaderboard: contests update");
                            if (changedJoinedContest || changedCreateContest
                                    || !dataSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_contest)).exists()
                                    || !dataSnapshot.child(getString(R.string.field_yearly)).child(getString(R.string.field_contest)).exists()
                                    || !dataSnapshot.child(getString(R.string.field_last_month)).child(getString(R.string.field_contest)).exists()
                                    || !dataSnapshot.child(getString(R.string.field_this_month)).child(getString(R.string.field_contest)).exists()
                                    || !dataSnapshot.child(getString(R.string.field_last_week)).child(getString(R.string.field_contest)).exists()
                                    || !dataSnapshot.child(getString(R.string.field_this_week)).child(getString(R.string.field_contest)).exists()) {
                                //for competition parameters of leaders
                                Query query3 = reference.child(getString(R.string.dbname_contests)).child(user_id);
                                query3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        //getting updates for contests
                                        int joinedContest = (int) dataSnapshot.child(getString(R.string.joined_contest)).getChildrenCount();
                                        int createdContest = (int) dataSnapshot.child(getString(R.string.created_contest)).getChildrenCount();

                                        Query query31 = reference.child(getString(R.string.dbname_leaderboard)).child(user_id);
                                        query31.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                //details entries
                                                int previousJoinedContests = 0;
                                                if(dataSnapshot.child(getString(R.string.field_joined_contest)).getValue()!=null)
                                                    previousJoinedContests = (int)(long) dataSnapshot.child(getString(R.string.joined_contest)).getValue();
                                                int previousCreatedContest = 0;
                                                if(dataSnapshot.child(getString(R.string.field_created_contest)).getValue()!=null)
                                                    previousCreatedContest = (int) (long)dataSnapshot.child(getString(R.string.created_contest)).getValue();

                                                int all_time = 0;
                                                int yearly = 0;
                                                if (dataSnapshot.child(getString(R.string.field_yearly)).child(getString(R.string.field_contest)).getValue() != null)
                                                    yearly = (int) (long) dataSnapshot.child(getString(R.string.field_yearly)).child(getString(R.string.field_contest)).getValue();
                                                int last_month = 0;
                                                if (dataSnapshot.child(getString(R.string.field_last_month)).child(getString(R.string.field_contest)).getValue() != null)
                                                    last_month = (int) (long) dataSnapshot.child(getString(R.string.field_last_month)).child(getString(R.string.field_contest)).getValue();
                                                int this_month = 0;
                                                if (dataSnapshot.child(getString(R.string.field_this_month)).child(getString(R.string.field_contest)).getValue() != null)
                                                    this_month = (int) (long) dataSnapshot.child(getString(R.string.field_this_month)).child(getString(R.string.field_contest)).getValue();
                                                int last_week = 0;
                                                if (dataSnapshot.child(getString(R.string.field_last_week)).child(getString(R.string.field_contest)).getValue() != null)
                                                    last_week = (int) (long) dataSnapshot.child(getString(R.string.field_last_week)).child(getString(R.string.field_contest)).getValue();
                                                int this_week = 0;
                                                if (dataSnapshot.child(getString(R.string.field_this_week)).child(getString(R.string.field_contest)).getValue() != null)
                                                    this_week = (int) (long) dataSnapshot.child(getString(R.string.field_this_week)).child(getString(R.string.field_contest)).getValue();

                                                //getting last updated entries
                                                String lastUpdatedTimestamp = (String) dataSnapshot.child(getString(R.string.field_last_updated)).getValue();
                                                int lastUpdatedYear = 0;
                                                int lastUpdatedMonth = 0;
                                                int lastUpdatedDate = 0;
                                                //                                          String lastUpdatedTime = "";
                                                assert lastUpdatedTimestamp != null;
                                                if (lastUpdatedTimestamp.length() > 12) {
                                                    lastUpdatedYear = Integer.parseInt(lastUpdatedTimestamp.substring(0, 4));
                                                    lastUpdatedMonth = Integer.parseInt(lastUpdatedTimestamp.substring(5, 7));
                                                    lastUpdatedDate = Integer.parseInt(lastUpdatedTimestamp.substring(8, 10));
                                                    //                                              lastUpdatedTime = lastUpdatedTimestamp.substring(12, lastUpdatedTimestamp.length() - 1);
                                                }
                                                String lastUpdatedDateFormat = lastUpdatedDate + "/" + lastUpdatedMonth + "/" + lastUpdatedYear;
                                                int finalLastUpdatedYear = lastUpdatedYear;
                                                int finalLastUpdatedMonth = lastUpdatedMonth;

                                                //calculating difference of dates in post and current one
                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");
                                                long elapsedDays = 0;
                                                try {
                                                    Date date1 = simpleDateFormat.parse(lastUpdatedDateFormat);
                                                    Date date2 = simpleDateFormat.parse(currentDateFormat);
//                                                    Log.d(TAG, "onTimeReceived: " + date1);
//                                                    Log.d(TAG, "onTimeReceived: " + date2);
                                                    assert date1 != null;
                                                    assert date2 != null;
                                                    elapsedDays = (date2.getTime() - date1.getTime()) / (ANIMATION_DURATION * 60 * 60 * 24);
//                                                    Log.d(TAG, "onDataChange: " + elapsedDays);
//                                                    Log.d(TAG, "onDataChange: " + currentDay);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }


                                                //updating for transition days of different year
                                                if (finalLastUpdatedYear < currentYear) {
                                                    yearly = 0;
                                                    this_month = 0;
                                                    //updating for transition days of different month in different year
                                                    if (finalLastUpdatedMonth == 12 && currentMonth == 1) {
                                                        last_month = this_month;
                                                        //updating for transition days of different month different year of time span of more than 2 weeks
                                                        if (elapsedDays > currentDay + 7) {
                                                            last_week = 0;
                                                            this_week = 0;
                                                        }
                                                        //updating for transition days of different month different year of previous week
                                                        else if (elapsedDays > currentDay && elapsedDays <= currentDay + 7) {
                                                            last_week = this_week;
                                                            this_week = 0;
                                                        }
                                                    } else last_month = 0;
                                                }
                                                //updating for transition days of same year
                                                else {
                                                    //updating for transition days of different month in different year
                                                    if (finalLastUpdatedMonth < currentMonth) {
                                                        this_month = 0;
                                                        //updating for transition days of just previous month
                                                        if ((finalLastUpdatedMonth - currentMonth) == 1) {
                                                            last_month = this_month;
                                                            //updating for transition days of different month different year of time span of more than 2 weeks
                                                            if (elapsedDays > currentDay + 7) {
                                                                last_week = 0;
                                                                this_week = 0;
                                                            }
                                                            //updating for transition days of different month different year of previous week
                                                            else if (elapsedDays > currentDay && elapsedDays <= currentDay + 7) {
                                                                last_week = this_week;
                                                                this_week = 0;
                                                            }
                                                        } else {
                                                            last_month = 0;
                                                            last_week = 0;
                                                            this_week = 0;
                                                        }
                                                    } else {
                                                        //updating for transition days of same month same year of time span of more than 2 weeks
                                                        if (elapsedDays > currentDay + 7) {
                                                            last_week = 0;
                                                            this_week = 0;
                                                        }
                                                        //updating for transition days of same month same year of time span of less than 2 weeks
                                                        else if (elapsedDays > currentDay && elapsedDays <= currentDay + 7) {
                                                            last_week = this_week;
                                                            this_week = 0;
                                                        }
                                                    }
                                                }


                                                //calculating rating for joined
                                                int rating = (int) (2.5 * (joinedContest - previousJoinedContests));
                                                //updating current instance of increasing followers list
                                                all_time += (2.5 * joinedContest);
                                                if (finalLastUpdatedYear == currentYear) {
                                                    yearly += rating;
                                                    if (finalLastUpdatedMonth == currentMonth)
                                                        this_month += rating;
                                                }
                                                if (elapsedDays <= currentDay)
                                                    this_week += rating;

                                                //calculating rating for created
                                                rating = 5 * (createdContest - previousCreatedContest);
                                                //updating current instance of increasing followers list
                                                all_time += (5 * createdContest);
                                                if (finalLastUpdatedYear == currentYear) {
                                                    yearly += rating;
                                                    if (finalLastUpdatedMonth == currentMonth)
                                                        this_month += rating;
                                                }
                                                if (elapsedDays <= currentDay) this_week += rating;

                                                Log.d(TAG, "updateLeaderboard: contests " + all_time + "," + yearly + "," + last_month + "," + this_month + "," + last_week + "," + this_week);
                                                reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_all_time)).child(getString(R.string.field_contest)).setValue(all_time);
                                                reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_yearly)).child(getString(R.string.field_contest)).setValue(yearly);
                                                reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_last_month)).child(getString(R.string.field_contest)).setValue(last_month);
                                                reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_this_month)).child(getString(R.string.field_contest)).setValue(this_month);
                                                reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_last_week)).child(getString(R.string.field_contest)).setValue(last_week);
                                                reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_this_week)).child(getString(R.string.field_contest)).setValue(this_week);
                                                reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_joined_contest)).setValue(joinedContest);
                                                reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_created_contest)).setValue(createdContest);

                                                reference.child(getString(R.string.dbname_users)).child(user_id).child(getString(R.string.changedCreatedContest)).setValue("false");
                                                reference.child(getString(R.string.dbname_users)).child(user_id).child(getString(R.string.changedJoinedContest)).setValue("false");
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }

                            //updating username and domain
                            reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_username)).setValue(username);
                            reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_domain)).setValue(domain);
                            reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.profile_photo)).setValue(profilePhoto);
                            reference.child(getString(R.string.dbname_leaderboard)).child(user_id).child(getString(R.string.field_last_updated)).setValue(currentTimeStamp);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onError(Exception ex) {
                Log.e(SNTPClient.TAG, Objects.requireNonNull(ex.getMessage()));
            }
        });
    }

    private void checkOrGetLocation() {
        Log.d(TAG, "checkOrGetLocation: started");
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        else {
            Log.d(TAG, "checkOrGetLocation: permission checked");
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.d(TAG, "checkOrGetLocation: gps provider unavailable");
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());
                final AlertDialog alert = builder.create();
                alert.show();
            } else {
                Log.d(TAG, "checkOrGetLocation: gps provider available");
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, location -> {
                    try {
                        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        Log.d(TAG, "checkOrGetLocation: addresses" + addresses);
                        String country = addresses.get(0).getCountryName();
                        String city = addresses.get(0).getSubAdminArea();
                        String area = addresses.get(0).getLocality();
                        Log.d(TAG, "checkOrGetLocation: addresse" + addresses.get(0));
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        assert user != null;
                        reference.child(getString(R.string.dbname_leaderboard)).child(user.getUid()).child(getString(R.string.field_last_known_location)).child("city").setValue(city);
                        reference.child(getString(R.string.dbname_leaderboard)).child(user.getUid()).child(getString(R.string.field_last_known_location)).child("country").setValue(country);
                        reference.child(getString(R.string.dbname_leaderboard)).child(user.getUid()).child(getString(R.string.field_last_known_location)).child("area").setValue(area);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_leaderboard);
        Log.d(TAG, "onCreate: started.");
        setupBottomNavigationView();
        setupFirebaseAuth();
        initializeWidgets();
        checkOrGetLocation();
        updateLeaderboard();
        filter();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        sortedByTime.setOnClickListener(v -> {
            String[] timeList = {"All Time", "This Year", "Last Month", "This Month", "Last Week", "This Week"};
            BottomSheetFilter bottomSheet = new BottomSheetFilter(timeList);
            bottomSheet.show(getSupportFragmentManager(), "Time Filter");
        });
        sortedByLocation.setOnClickListener(v -> {
            String[] locationList = {"World", "Country", "City", "Area"};
            BottomSheetFilter bottomSheet = new BottomSheetFilter(locationList);
            bottomSheet.show(getSupportFragmentManager(), "location Filter");
        });
        sortedByType.setOnClickListener(v -> {
            String[] typeList = {"Overall", "Posts", "Followers", "Contests"};
            BottomSheetFilter bottomSheet = new BottomSheetFilter(typeList);
            bottomSheet.show(getSupportFragmentManager(), "Type Filter");
        });
        sortedByDomain.setOnClickListener(v -> {
            String[] domainList = getResources().getStringArray(R.array.domain2);
            BottomSheetFilter bottomSheet = new BottomSheetFilter(domainList);
            bottomSheet.show(getSupportFragmentManager(), "Type Filter");
        });
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mRecyclerView.setVisibility(View.GONE);
            filter();
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onButtonClicked(String text) {
        Log.d(TAG, "onButtonClicked: filter received" + text);
        if (!swipeRefreshLayout.isRefreshing()) switch (text) {
            case "All Time":
            case "Last Week":
            case "This Year":
            case "Last Month":
            case "This Month":
            case "This Week":
                sortedByTime.setText(text);
                sortedByTime.setBackgroundResource(R.drawable.circular_gradient_background);
                YoYo.with(Techniques.ZoomIn).duration(ANIMATION_DURATION).playOn(sortedByTime);
                break;
            case "World":
            case "Country":
            case "City":
            case "Area":
                sortedByLocation.setText(text);
                sortedByLocation.setBackgroundResource(R.drawable.circular_gradient_background);
                sortedByLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                YoYo.with(Techniques.ZoomIn).duration(ANIMATION_DURATION).playOn(sortedByLocation);
//                checkOrGetLocation();
                break;
            case "Overall":
            case "Posts":
            case "Followers":
            case "Contests":
                sortedByType.setText(text);
                sortedByType.setBackgroundResource(R.drawable.circular_gradient_background);
                YoYo.with(Techniques.ZoomIn).duration(ANIMATION_DURATION).playOn(sortedByType);
                break;
//            case "All":
//            case "Photography":
//            case "Film Maker":
//            case "Musician":
//            case "Sketch Artist":
//            case "Writer":
//            case "Others":
            default:
                sortedByDomain.setText(text);
                sortedByDomain.setBackgroundResource(R.drawable.circular_gradient_background);
                YoYo.with(Techniques.ZoomIn).duration(ANIMATION_DURATION).playOn(sortedByDomain);
                break;
        }
        filter();
    }

    private void filter() {
        swipeRefreshLayout.setRefreshing(true);
        mRecyclerView.setVisibility(View.GONE);
        //deciding parameter for time and assigning field to search in
        switch ((String) sortedByTime.getText()) {
            case "All Time":
            default:
                time = getString(R.string.field_all_time);
                break;
            case "This Year":
                time = getString(R.string.field_yearly);
                break;
            case "Last Month":
                time = getString(R.string.field_last_month);
                break;
            case "This Month":
                time = getString(R.string.field_this_month);
                break;
            case "Last Week":
                time = getString(R.string.field_last_week);
                break;
            case "This Week":
                time = getString(R.string.field_this_week);
                break;
        }

        //deciding parameter for location and assigning field to search in
        switch ((String) sortedByLocation.getText()) {
            case "Country":
                locationParameter = getString(R.string.field_country);
                break;
            case "City":
                locationParameter = getString(R.string.field_city);
                break;
            case "Area":
                locationParameter = getString(R.string.field_area);
                break;
            case "World":
                locationParameter = "";
            default:

        }

        //deciding parameter for type
        switch ((String) sortedByType.getText()) {
            case "Posts":
                typeParameter = getString(R.string.field_post);
                break;
            case "Followers":
                typeParameter = getString(R.string.field_followers);
                break;
            case "Contests":
                typeParameter = getString(R.string.field_contest);
                break;
            case "Overall":
            default:
                typeParameter = "";
        }

        //deciding domain parmaeter
        switch ((String) sortedByDomain.getText()) {
            case "All":
            default:
                domainParameter = "";
                break;
            case "Photography":
            case "Film Maker":
            case "Painter":
                domainParameter = (String) sortedByDomain.getText();
                break;
        }

        mList.clear();
        mAdapter.notifyDataSetChanged();
        String finalTime = time;
        String finalLocationParameter = locationParameter;
        String finalTypeParameter = typeParameter;


//        Log.d(TAG, "filter: "+finalTime);
//        Log.d(TAG, "filter: "+finalLocationParameter);
//        Log.d(TAG, "filter: "+finalTypeParameter);


        Query query = reference.child(getString(R.string.dbname_leaderboard)).child(currentUser);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //user details
                int userRating;
                if (finalTypeParameter.equals(""))
                    userRating = (int) (long) dataSnapshot.child(finalTime).child(getString(R.string.field_post)).getValue() + (int) (long) dataSnapshot.child(finalTime).child(getString(R.string.field_followers)).getValue() + (int) (long) dataSnapshot.child(finalTime).child(getString(R.string.field_contest)).getValue();
                else
                    userRating = (int) (long) dataSnapshot.child(finalTime).child(finalTypeParameter).getValue();
                int finalUserRating = userRating;
//                String userDomain = (String) dataSnapshot.child(getString(R.string.field_domain)).getValue();
                String userUsername = (String) dataSnapshot.child(getString(R.string.field_username)).getValue();


                //query for leaderboard database
                Query query1 = reference.child(getString(R.string.dbname_leaderboard));
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    int rank = 1;

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                            //getting user ids, username and profile photos
                            String user_id = singleSnapshot.getKey();
                            String username = (String) singleSnapshot.child(getString(R.string.field_username)).getValue();
                            String profileUrl = (String) singleSnapshot.child(getString(R.string.profile_photo)).getValue();
                            String domain = (String) singleSnapshot.child(getString(R.string.field_domain)).getValue();

                            assert domain != null;
                            Log.d(TAG, "onDataChange: domain" + domain);
                            if (domainParameter.equals("") || domain.equals(domainParameter)) {
                                //calculating total rating and type rating for filter 1 and filter 3
                                int rating;
                                assert user_id != null;
                                if (finalTypeParameter.equals("")) {
                                    rating = (int) (long) dataSnapshot.child(user_id).child(finalTime).child(getString(R.string.field_post)).getValue() + (int) (long) dataSnapshot.child(user_id).child(finalTime).child(getString(R.string.field_followers)).getValue() + (int) (long) dataSnapshot.child(user_id).child(finalTime).child(getString(R.string.field_contest)).getValue();
//                                Log.d(TAG, "onDataChange: user_id"+user_id);
//                                Log.d(TAG, "onDataChange: "+dataSnapshot.child(user_id));
                                } else
                                    rating = (int) (long) dataSnapshot.child(user_id).child(finalTime).child(finalTypeParameter).getValue();
                                int finalRating = rating;


                                //calculating location wise rating for filter 2 by getting last known location
                                if (!finalLocationParameter.equals("")) {
                                    Query query = reference.child(getString(R.string.dbname_leaderboard)).child(currentUser).child(getString(R.string.field_last_known_location)).child(finalLocationParameter);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            String userLocation = (String) dataSnapshot.getValue();
                                            String testLocation = (String) singleSnapshot.child(getString(R.string.field_last_known_location)).child(finalLocationParameter).getValue();
                                            assert testLocation != null;
                                            if (testLocation.equals(userLocation))
                                                rank = addToLeaderboard(rank, finalUserRating, username, finalRating, profileUrl, user_id);

                                            mRecyclerView.setVisibility(View.VISIBLE);
                                            mAdapter.notifyDataSetChanged();
                                            YoYo.with(Techniques.Landing).duration(ANIMATION_DURATION).playOn(mRecyclerView);
                                            String rankText = "#" + rank;
//                                            String ratingText = finalUserRating + "pts";
                                            userItemUsername.setText(userUsername);
                                            userItemRank.setText(rankText);
                                            assert userLocation != null;
                                            if (!userLocation.equals(""))
                                                sortedByLocation.setText(userLocation);
                                            sortedByLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                                            YoYo.with(Techniques.Tada).duration(ANIMATION_DURATION).playOn(sortedByLocation);
                                            swipeRefreshLayout.setRefreshing(false);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }
                                //for rest cases where location will not be a parameter
                                else
                                    rank = addToLeaderboard(rank, finalUserRating, username, finalRating, profileUrl, user_id);

                                mRecyclerView.setVisibility(View.VISIBLE);
                                mAdapter.notifyDataSetChanged();
                                YoYo.with(Techniques.Landing).duration(ANIMATION_DURATION).playOn(mRecyclerView);
                                String rankText = "#" + rank;
//                                String ratingText = finalUserRating + "pts";
                                userItemUsername.setText(userUsername);
                                userItemRank.setText(rankText);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private int addToLeaderboard(int rank, int finalUserRating, String username, int finalRating, String profileUrl, String user_id) {
        Log.d(TAG, "addToLeaderboard: adding" + user_id);
        if (finalUserRating < finalRating)
            rank++;
        if (mList.size() == 0)
            mList.add(new ItemLeaderboard(username, finalRating, profileUrl, user_id));
        else {
            int l = mList.size();
            //loop to push in between and next one further away
            for (int i = 0; i < l; i++) {
                int r = Integer.parseInt(mList.get(i).getPostionParameter());
                if (finalRating >= r) {
                    mList.add(new ItemLeaderboard("", 0, "", ""));
                    for (int j = mList.size() - 1; j > i; j--)
                        mList.set(j, mList.get(j - 1));
                    mList.set(i, new ItemLeaderboard(username, finalRating, profileUrl, user_id));
                    break;
                }
                //pushing at the end
                else if (i == l - 1)
                    mList.add(new ItemLeaderboard(username, finalRating, profileUrl, user_id));
            }
        }
        //removing extra nodes
        if (mList.size() == LEADERBOAD_SIZE + 1) mList.remove(LEADERBOAD_SIZE);
        return rank;
    }


    private void initializeWidgets() {
        Log.e(TAG, "initializeWidgets: ");

        mContext = LeaderboardActivity.this;
        firebaseMethods = new FirebaseMethods(mContext);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(LeaderboardActivity.this));
        //initializing widgets
        userItemUsername = findViewById(R.id.userItemUsername);
        userItemRank = findViewById(R.id.userItemRank);
        sortedByTime = findViewById(R.id.sortedByTime);
        sortedByLocation = findViewById(R.id.sortedByLocation);
        sortedByType = findViewById(R.id.sortedByType);
        sortedByDomain = findViewById(R.id.sortedByDomain);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        YoYo.with(Techniques.BounceIn).duration(ANIMATION_DURATION).playOn(sortedByTime);
        YoYo.with(Techniques.BounceIn).duration(ANIMATION_DURATION).playOn(sortedByLocation);
        YoYo.with(Techniques.BounceIn).duration(ANIMATION_DURATION).playOn(sortedByType);
        YoYo.with(Techniques.BounceIn).duration(ANIMATION_DURATION).playOn(sortedByDomain);
//        usernameProfile=findViewById(R.id.username);

        mList = new ArrayList<>();
        mAdapter = new AdapterItemLeaderboard(mList, mContext);
        mAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mAdapter);
        reference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        currentUser = user.getUid();

        time = "";
        locationParameter = "";
        typeParameter = "";
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
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
                            Intent intent = new Intent(mContext, login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            settings.edit().clear().apply();
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

    private void setupBottomNavigationView() {
        Log.d(TAG, " setupBottomNavigationView:setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.BottomNavViewBar);
        BottomNaavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNaavigationViewHelper.enableNavigation(LeaderboardActivity.this, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}