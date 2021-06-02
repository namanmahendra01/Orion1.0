package com.orion.orion.contest.create;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orion.orion.Adapters.AdapterContestCreated;
import com.orion.orion.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class CreatedActivity extends AppCompatActivity {
    private static final String TAG = "JOINED FRAGMENT";
    RecyclerView createdContestRv;
    FloatingActionButton floatbtn;
    private ArrayList<com.orion.orion.models.CreateForm> contestlist;
    private ArrayList<com.orion.orion.models.CreateForm> paginatedContestlist;
    private int mResults;
    private AdapterContestCreated contestCreated;
    ProgressBar bottomProgress;


    TextView noPost;
    SwipeRefreshLayout contestRefresh;
    boolean flag1 = false;
    private static int RETRY_DURATION = 1000;
    private static final Handler handler = new Handler(Looper.getMainLooper());

    //    SP
    private Gson gson;
    private SharedPreferences sp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created);


        floatbtn = findViewById(R.id.float_btn);
        contestRefresh=findViewById(R.id.contest_refresh);
        noPost=findViewById(R.id.noPost);
        bottomProgress=findViewById(R.id.pro2);
        createdContestRv = findViewById(R.id.recycler_view3);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        contestlist = new ArrayList<>();

        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(20);
        createdContestRv.setHasFixedSize(true);
        createdContestRv.setItemViewCacheSize(10);
        createdContestRv.setDrawingCacheEnabled(true);
        createdContestRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        createdContestRv.setLayoutManager(linearLayoutManager);

        floatbtn.setOnClickListener(v -> {
            Intent i = new Intent(CreatedActivity.this, CreateForm.class);
            startActivity(i);
        });
        createdContestRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (contestlist.size()!=paginatedContestlist.size())
                        bottomProgress.setVisibility(View.VISIBLE);
                    displayMoreContest();
                }else bottomProgress.setVisibility(View.GONE);
            }
        });
        contestRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                flag1 = false;
                getCreateListFromSP();
                checkRefresh();
            }
            private void checkRefresh() {
                if (contestRefresh.isRefreshing() && flag1 ) {
                    contestRefresh.setRefreshing(false);
                    handler.removeCallbacks(this::checkRefresh);
                    flag1 = false;
                } else handler.postDelayed(this::checkRefresh, RETRY_DURATION);
            }
        });
        sp = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        gson = new Gson();
        getCreateListFromSP();
    }
    private void getContest() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_contests))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.created_contest))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            contestlist.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                com.orion.orion.models.CreateForm createForm = snapshot.getValue(com.orion.orion.models.CreateForm.class);

                                contestlist.add(createForm);
                            }
                            Collections.reverse(contestlist);

//                        Add newly Created ArrayList to Shared Preferences
                            SharedPreferences.Editor editor = sp.edit();
                            String json = gson.toJson(contestlist);
                            editor.putString("createlist", json);
                            editor.apply();

                            displaycontest();

                        }else{
                            displaycontest();

                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    //  fetching FollowerList  from SharedPreferences
    private void getCreateListFromSP() {
        String json = sp.getString("createlist", null);

        Type type = new TypeToken<ArrayList<com.orion.orion.models.CreateForm>>() {
        }.getType();
        contestlist = gson.fromJson(json, type);
        if (contestlist == null) {    //        if no arrayList is present
            contestlist = new ArrayList<>();
            Log.d(TAG, "getCreateListFromSP: 1");
            getContest();             //            make new Arraylist

        } else {
            Log.d(TAG, "getCreateListFromSP: 2");

            checkCreateUpdate();       //         Check if new contest is there

        }

    }

    private void checkCreateUpdate() {
        DatabaseReference refer = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_contests));

        refer.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_contest_created_updates))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            int x=0;

                            for (DataSnapshot snapshot1:snapshot.getChildren()){
                                x++;
                                for (com.orion.orion.models.CreateForm a:contestlist){


                                    if (a.getCi().equals(snapshot1.getKey())){

                                        a.setSt(snapshot1.getValue().toString());
                                    }

                                    if (x==snapshot.getChildrenCount()){
                                        //    Add newly Created ArrayList to Shared Preferences
                                        SharedPreferences.Editor editor = sp.edit();
                                        String json = gson.toJson(contestlist);
                                        editor.putString("createlist", json);
                                        editor.apply();

                                        refer.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child(getString(R.string.field_contest_created_updates))
                                                .removeValue();

                                        checkNewContesUpdate();
                                    }

                                }




                            }
                        }else {
                            checkNewContesUpdate();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkNewContesUpdate() {
        DatabaseReference refer = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_contests));
        refer.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.created_contest))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.getChildrenCount() != contestlist.size()) {
                                updateCreateList();

                            } else {
                                refer.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(getString(R.string.created_contest))
                                        .orderByKey()
                                        .limitToLast(1)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                        if (contestlist.get(0).getCi().equals(snapshot1.getKey())) {
                                                            displaycontest();
                                                        } else {
                                                            updateCreateList();
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }else{
                            displaycontest();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void updateCreateList() {

        Collections.reverse(contestlist);
        contestlist.clear();
        DatabaseReference refer = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_contests));
        refer.child(FirebaseAuth.getInstance().getCurrentUser().getUid())

                .child(getString(R.string.created_contest))

                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            com.orion.orion.models.CreateForm createForm = snapshot1.getValue(com.orion.orion.models.CreateForm.class);

                            contestlist.add(createForm);
                        }
                        Collections.reverse(contestlist);

                        //    Add newly Created ArrayList to Shared Preferences
                        SharedPreferences.Editor editor = sp.edit();
                        String json = gson.toJson(contestlist);
                        editor.putString("createlist", json);
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
        bottomProgress.setVisibility(View.GONE);


        flag1=true;
        paginatedContestlist = new ArrayList<>();
        if (contestlist != null&&contestlist.size()!=0) {

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
                contestCreated = new AdapterContestCreated(this, paginatedContestlist);
                contestCreated.setHasStableIds(true);
                createdContestRv.setAdapter(contestCreated);

            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());

            }

        }else {
            noPost.setVisibility(View.VISIBLE);
            bottomProgress.setVisibility(View.GONE);

        }
    }

    public void displayMoreContest() {
        Log.d(TAG, "display next 10 contest");

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
                createdContestRv.post(new Runnable() {
                    @Override
                    public void run() {
                        contestCreated.notifyItemRangeInserted(mResults,iterations);
                    }
                });
                mResults = mResults + iterations;


            }else{
                bottomProgress.setVisibility(View.GONE);

            }

        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());

        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());

        }

    }

}