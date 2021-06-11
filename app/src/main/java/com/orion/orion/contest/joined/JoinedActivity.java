package com.orion.orion.contest.joined;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orion.orion.Adapters.AdapterContestCreated;
import com.orion.orion.Adapters.AdapterContestJoined;
import com.orion.orion.R;
import com.orion.orion.contest.create.CreatedActivity;
import com.orion.orion.contest.upcoming.UpcomingContestActivity;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.JoinForm;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;


public class JoinedActivity extends AppCompatActivity {

    private static final String TAG = "JOINED FRAGMENT";
    private static int RETRY_DURATION = 1000;
    private static final Handler handler = new Handler(Looper.getMainLooper());

    private ImageView backArrrow;
    private TextView topBarTitle;
    private TextView noPost;
    private RecyclerView joinedContestRv;
    private SwipeRefreshLayout contestRefresh;
    private ProgressBar bottomProgress;

    //    SP
    Gson gson;
    SharedPreferences sp;
    private FirebaseAuth fAuth;
    private int mResults;
    private ArrayList<JoinForm> contestlist;
    private ArrayList<JoinForm> paginatedContestlist;
    private AdapterContestJoined contestJoined;
    boolean flag1 = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined);


        backArrrow= findViewById(R.id.backarrow);
        topBarTitle = findViewById(R.id.titleTopBar);
        contestRefresh = findViewById(R.id.contest_refresh);
        noPost = findViewById(R.id.noPost);
        bottomProgress = findViewById(R.id.pro2);
        joinedContestRv = findViewById(R.id.recycler_view2);

        topBarTitle.setText("Joined Contest");

        backArrrow.setOnClickListener(view -> onBackPressed());
        fAuth = FirebaseAuth.getInstance();
        contestlist = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        joinedContestRv.setHasFixedSize(true);
        joinedContestRv.setItemViewCacheSize(10);
        joinedContestRv.setDrawingCacheEnabled(true);
        joinedContestRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(20);
        joinedContestRv.setLayoutManager(linearLayoutManager);

        contestlist=new ArrayList<>();

        fAuth=FirebaseAuth.getInstance();
        joinedContestRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (contestlist.size() != paginatedContestlist.size())
                        bottomProgress.setVisibility(View.VISIBLE);
                    displayMoreContest();
                } else bottomProgress.setVisibility(View.GONE);
            }
        });
        contestRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                flag1 = false;
                getJoinListFromSP();
                checkRefresh();
            }

            private void checkRefresh() {
                if (contestRefresh.isRefreshing() && flag1) {
                    contestRefresh.setRefreshing(false);
                    handler.removeCallbacks(this::checkRefresh);
                    flag1 = false;
                } else handler.postDelayed(this::checkRefresh, RETRY_DURATION);
            }
        });
        sp = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        gson = new Gson();
        getJoinListFromSP();
    }
    private void getContest() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_contests))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.joined_contest))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        contestlist.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            JoinForm joiningForm = snapshot.getValue(JoinForm.class);
                            contestlist.add(joiningForm);
                        }
                        Collections.reverse(contestlist);
//                        Add newly Created ArrayList to Shared Preferences
                        SharedPreferences.Editor editor = sp.edit();
                        String json = gson.toJson(contestlist);
                        editor.putString("joinlist", json);
                        editor.apply();
                        displaycontest();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    //  fetching JoinList  from SharedPreferences
    private void getJoinListFromSP() {
        String json = sp.getString("joinlist", null);
        Log.d(TAG, "getJoinListFromSP: 3");
        Type type = new TypeToken<ArrayList<JoinForm>>() {
        }.getType();
        contestlist = gson.fromJson(json, type);
        if (contestlist == null) {    //        if no arrayList is present
            contestlist = new ArrayList<>();
            Log.d(TAG, "getJoinListFromSP: 1");
            getContest();             //            make new Arraylist

        } else {
            Log.d(TAG, "getJoinListFromSP: 2");

            checkJoinUpdate();       //         Check if new contest is there

        }

    }

    private void checkJoinUpdate() {
        DatabaseReference refer = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_contests));

        refer.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_joined_updates))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            int x = 0;
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                x++;
                                for (JoinForm a : contestlist) {


                                    if (a.getJi().equals(snapshot1.getKey())) {

                                        a.setSt(snapshot1.getValue().toString());
                                    }
                                    if (x == snapshot.getChildrenCount()) {
                                        //    Add newly Created ArrayList to Shared Preferences
                                        SharedPreferences.Editor editor = sp.edit();
                                        String json = gson.toJson(contestlist);
                                        editor.putString("joinlist", json);
                                        editor.apply();

                                        refer.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child(getString(R.string.field_joined_updates))
                                                .removeValue();

                                        checkNewJoinUpdate();
                                    }

                                }


                            }
                        } else {
                            checkNewJoinUpdate();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void checkNewJoinUpdate() {
        DatabaseReference refer = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_contests));

        refer.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.joined_contest))
                .orderByKey()
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                if (contestlist.size() != 0) {
                                    if (contestlist.get(0).getJi().equals(snapshot1.getKey())) {
                                        Log.d(TAG, "getJoinListFromSP :5 ");

                                        displaycontest();
                                    } else {
                                        Log.d(TAG, "getJoinListFromSP :6");

                                        updateCreateList();
                                    }
                                } else {
                                    Log.d(TAG, "getJoinListFromSP : 7 ");

                                    updateCreateList();
                                }
                            }
                        } else {
                            Log.d(TAG, "getJoinListFromSP :4 ");
                            updateCreateList();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateCreateList() {

        contestlist.clear();
        DatabaseReference refer = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_contests));
        refer.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.joined_contest))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            JoinForm joinForm = snapshot1.getValue(JoinForm.class);

                            contestlist.add(joinForm);
                        }
                        Collections.reverse(contestlist);

                        //    Add newly Created ArrayList to Shared Preferences
                        SharedPreferences.Editor editor = sp.edit();
                        String json = gson.toJson(contestlist);
                        editor.putString("joinlist", json);
                        editor.apply();

                        displaycontest();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void displaycontest() {
        Log.d(TAG, "display first 10 contest");
        noPost.setVisibility(View.GONE);


        flag1 = true;
        paginatedContestlist = new ArrayList<>();
        if (contestlist != null && contestlist.size() != 0) {
            try {

                int iteration = contestlist.size();
                if (iteration > 5) {
                    iteration = 5;
                }
                mResults = 5;
                for (int i = 0; i < iteration; i++) {
                    paginatedContestlist.add(contestlist.get(i));
                }
                Log.d(TAG, "contest: sss" + paginatedContestlist.size());
                contestJoined = new AdapterContestJoined(this, paginatedContestlist);
                contestJoined.setHasStableIds(true);
                joinedContestRv.setAdapter(contestJoined);

            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());

            }

        } else {
            noPost.setVisibility(View.VISIBLE);
            bottomProgress.setVisibility(View.GONE);

        }
    }

    public void displayMoreContest() {
        Log.d(TAG, "display next 10 contest");
        bottomProgress.setVisibility(View.GONE);

        try {
            if (contestlist.size() > mResults && contestlist.size() > 0) {

                int iterations;
                if (contestlist.size() > (mResults + 10)) {
                    Log.d(TAG, "display next 20 contest");
                    iterations = 10;
                } else {
                    Log.d(TAG, "display less tha 20 contest");
                    iterations = contestlist.size() - mResults;
                }
                for (int i = mResults; i < mResults + iterations; i++) {
                    paginatedContestlist.add(contestlist.get(i));

                }
                joinedContestRv.post(new Runnable() {
                    @Override
                    public void run() {
                        contestJoined.notifyDataSetChanged();
                    }
                });
                mResults = mResults + iterations;


            } else {
                bottomProgress.setVisibility(View.GONE);

            }

        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());

        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());

        }

    }
    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, UpcomingContestActivity.class));
//        if (exit) moveTaskToBack(true); // finish activity
//        else {
//            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
//            exit = true;
//            new Handler().postDelayed(() -> exit = false, 2 * 1000);
//        }
        Intent i = new Intent(JoinedActivity.this, UpcomingContestActivity.class);
        startActivity(i);
    }
}