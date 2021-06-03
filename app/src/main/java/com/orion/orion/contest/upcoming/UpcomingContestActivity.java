package com.orion.orion.contest.upcoming;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.Adapters.AdapterContestSearch;
import com.orion.orion.Adapters.AdapterContestUpcoming;
import com.orion.orion.Adapters.AdapterContestUpcomingGrid;
import com.orion.orion.R;
import com.orion.orion.contest.create.CreatedActivity;
import com.orion.orion.contest.joined.JoinedActivity;
import com.orion.orion.login.LoginActivity;
import com.orion.orion.models.ContestDetail;
import com.orion.orion.util.BottomNaavigationViewHelper;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.StringManipilation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

public class UpcomingContestActivity extends AppCompatActivity {
    private static final String TAG = "UPCOMING FRAGMENT";
    RecyclerView upcomingContestRv, contestSearchRv, upcomingFilterRv;
    private ArrayList<ContestDetail> contestlist;
    private FirebaseAuth fAuth;
    private EditText searchBox;
    int prevHeight;
    int height, dummyHeight;
    TextView noPost,joined,created;
    LinearLayout blurBg;
    int x = 0;
    private int mResults;
    ProgressBar bottomProgress;
    private static final int ACTIVITY_NUM = 0;
    private static final int CREATE_CONTEST = 1;
    private int mResults2;
    RelativeLayout relativeLayout;
    ImageView gridB, gridY, colY, colB, filterB, filterY, cross;
    private ArrayList<ContestDetail> contestlist2;
    private ArrayList<ContestDetail> contestlist3;
    private ArrayList<ContestDetail> paginatedcontestlist;
    private ArrayList<ContestDetail> paginatedcontestSearch;
    SwipeRefreshLayout contesRefresh;
    boolean flag1 = false;
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private static int RETRY_DURATION = 1000;
    private static final Handler handler = new Handler(Looper.getMainLooper());

    private Spinner domainspinner, entryfeeSpinner;
    String domain = "Overall", entryfee = "Overall";


    private AdapterContestUpcoming contestUpcoming;
    private AdapterContestUpcomingGrid adapterContestUpcomingGrid;
    private AdapterContestSearch adapterContestSearch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_contest);
        initWidgets();
        setupBottomNavigationView();
        setupFirebaseAuth();
        checkCurrentuser(mAuth.getCurrentUser());
        hideSoftKeyboard();

        joined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UpcomingContestActivity.this, JoinedActivity.class);
                startActivity(i);
            }
        });
        created.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UpcomingContestActivity.this, CreatedActivity.class);
                startActivity(i);
            }
        });


        final GridLayoutManager[] linearLayoutManager1 = {new GridLayoutManager(this, 1)};
        contestlist2 = new ArrayList<>();
        adapterContestSearch = new AdapterContestSearch(this, contestlist2);

        contestSearchRv.setHasFixedSize(true);
        contestSearchRv.setLayoutManager(linearLayoutManager1[0]);
        contestSearchRv.setAdapter(adapterContestSearch);

        final GridLayoutManager[] linearLayoutManager = {new GridLayoutManager(this, 1)};
        contestlist = new ArrayList<>();
        contestUpcoming = new AdapterContestUpcoming(this, contestlist);

        upcomingContestRv.setHasFixedSize(true);
        upcomingContestRv.setLayoutManager(linearLayoutManager[0]);
        contestUpcoming.setHasStableIds(true);
        upcomingContestRv.setAdapter(contestUpcoming);


        gridB.setOnClickListener(v -> {
            gridB.setVisibility(View.GONE);
            gridY.setVisibility(View.VISIBLE);
            colY.setVisibility(View.GONE);
            colB.setVisibility(View.VISIBLE);
            linearLayoutManager[0] = new GridLayoutManager(this, 2);
            upcomingContestRv.setLayoutManager(linearLayoutManager[0]);
            adapterContestUpcomingGrid = new AdapterContestUpcomingGrid(this, paginatedcontestlist);
            adapterContestUpcomingGrid.setHasStableIds(true);
            upcomingContestRv.setAdapter(adapterContestUpcomingGrid);

        });
        filterB.setOnClickListener(v -> {
            filterY.setVisibility(View.VISIBLE);
            filterB.setVisibility(View.GONE);
            expand(relativeLayout, 1000);
        });
        filterY.setOnClickListener(v -> {
            filterY.setVisibility(View.GONE);
            filterB.setVisibility(View.VISIBLE);
            expand(relativeLayout, 1000);
        });
        colB.setOnClickListener(v -> {
            gridB.setVisibility(View.VISIBLE);
            gridY.setVisibility(View.GONE);
            colY.setVisibility(View.VISIBLE);
            colB.setVisibility(View.GONE);
            linearLayoutManager[0] = new GridLayoutManager(this, 1);
            upcomingContestRv.setLayoutManager(linearLayoutManager[0]);
            contestUpcoming = new AdapterContestUpcoming(this, paginatedcontestlist);
            contestUpcoming.setHasStableIds(true);
            upcomingContestRv.setAdapter(contestUpcoming);
        });
        cross.setOnClickListener(view1 -> {
            paginatedcontestSearch.clear();
            adapterContestSearch.notifyDataSetChanged();
            searchBox.setText("");
            blurBg.setVisibility(View.GONE);
        });
        contestSearchRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE)
                    displayMoreSearch();
            }
        });
        contestlist3 = new ArrayList<>();
        fAuth = FirebaseAuth.getInstance();
        upcomingContestRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (contestlist.size() != paginatedcontestlist.size())
                        bottomProgress.setVisibility(View.VISIBLE);
                    displayMoreContest();
                } else bottomProgress.setVisibility(View.GONE);
            }
        });
        domainspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                domain = domainspinner.getSelectedItem().toString();
                getContestFiltered(domain, entryfee);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        entryfeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                entryfee = entryfeeSpinner.getSelectedItem().toString();
                getContestFiltered(domain, entryfee);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String key = "";
                if (!searchBox.getText().toString().contains(".") &&
                        !searchBox.getText().toString().contains(",") &&
                        !searchBox.getText().toString().contains("#") &&
                        !searchBox.getText().toString().contains("$") &&
                        !searchBox.getText().toString().contains("[") &&
                        !searchBox.getText().toString().contains("]"))
                    key = searchBox.getText().toString();
                searchContest(key);
            }
        });
        contesRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                flag1 = false;
                getContestFiltered(domain, entryfee);
                Log.d(TAG, "onRefresh: 11");
                checkRefresh();
            }

            private void checkRefresh() {
                if (contesRefresh.isRefreshing() && flag1) {
                    contesRefresh.setRefreshing(false);
                    handler.removeCallbacks(this::checkRefresh);
                    flag1 = false;
                } else handler.postDelayed(this::checkRefresh, RETRY_DURATION);
            }
        });

        getContestFiltered(domain, entryfee);

    }

    public void initWidgets(){
        upcomingContestRv = findViewById(R.id.recycler_view1);
        contestSearchRv = findViewById(R.id.recyclerKey);
        searchBox = findViewById(R.id.search);
        domainspinner = findViewById(R.id.domainspinner);
        entryfeeSpinner = findViewById(R.id.entryfeeSpinner);
        gridB = findViewById(R.id.gridB);
        gridY = findViewById(R.id.gridY);
        colB =findViewById(R.id.columnB);
        colY = findViewById(R.id.columnY);
        filterB = findViewById(R.id.filter);
        filterY = findViewById(R.id.filteryellow);
        relativeLayout = findViewById(R.id.relparent);
        contesRefresh = findViewById(R.id.contest_refresh);
        blurBg = findViewById(R.id.pro);
        cross = findViewById(R.id.cross);
        bottomProgress = findViewById(R.id.pro2);
        noPost = findViewById(R.id.noPost);
        joined = findViewById(R.id.joined);
        created = findViewById(R.id.created);


    }

    public void expand(final View v, int duration) {
        final boolean expand = v.getVisibility() != View.VISIBLE;

        prevHeight = v.getHeight();
        if (x == 0) {
            x++;
            dummyHeight = v.getHeight();
        }
        if (prevHeight == 0) {
            int measureSpecParams = View.MeasureSpec.getSize(View.MeasureSpec.UNSPECIFIED);
            v.measure(measureSpecParams, measureSpecParams);
            height = dummyHeight;
        } else {
            height = 0;
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, height);
        int finalHeight = height;
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();

            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (expand) {
                    v.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!expand) {
                    v.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    private void getContestFiltered(String domain, String entryfee) {
        final String[] timestamp = new String[1];
        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Colombo"), new SNTPClient.Listener() {
            @Override
            public void onTimeReceived(String rawDate) {
                // rawDate -> 2019-11-05T17:51:01+0530

                //*************************************************************************
                String currentTime = StringManipilation.getTime(rawDate);
                java.text.DateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy");
                Date date1 = null;
                try {
                    date1 = (Date) formatter1.parse(currentTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                timestamp[0] = String.valueOf(date1.getTime());

                if (!domain.equals("Overall")) {


                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.child(getString(R.string.dbname_contestlist))
                            .orderByChild(getString(R.string.field_domain)).equalTo(domain)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    contestlist.clear();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        String resDate = snapshot.child(getString(R.string.field_winners_declare)).getValue().toString();
                                        java.text.DateFormat formatter5 = new SimpleDateFormat("dd-MM-yyyy");
                                        Date date5 = null;
                                        try {
                                            date5 = (Date) formatter5.parse(resDate);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        String resDate2 = String.valueOf(date5.getTime());
                                        if ((Long.parseLong(resDate2) + 604800000) >= Long.parseLong(timestamp[0])) {
                                            ContestDetail contestDetail = snapshot.getValue(ContestDetail.class);

                                            contestlist.add(contestDetail);
                                        }
                                    }
                                    if (!entryfee.equals("Overall")) {

                                        contestlist3.clear();
                                        for (int x = 0; x < contestlist.size(); x++) {
                                            ContestDetail contestDetail = contestlist.get(x);
                                            if (entryfee.equals("Free")) {
                                                if (contestDetail.getEf().equals(entryfee)) {

                                                    contestlist3.add(contestDetail);
                                                }
                                            }
                                            if (!entryfee.equals("Free"))
                                                if (!contestDetail.getEf().equals("Free")) {
                                                    contestlist3.add(contestDetail);
                                                }
                                        }
                                        contestlist.clear();
                                        contestlist.addAll(contestlist3);
                                        Collections.reverse(contestlist);
                                        displaycontest();
                                    } else {
                                        Collections.reverse(contestlist);

                                        displaycontest();
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                } else {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.child(getString(R.string.dbname_contestlist))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    contestlist.clear();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        String resDate = snapshot.child(getString(R.string.field_winners_declare)).getValue().toString();
                                        java.text.DateFormat formatter5 = new SimpleDateFormat("dd-MM-yyyy");
                                        Date date5 = null;
                                        try {
                                            date5 = (Date) formatter5.parse(resDate);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        String resDate2 = String.valueOf(date5.getTime());
                                        if ((Long.parseLong(resDate2) + 604800000) >= Long.parseLong(timestamp[0])) {
                                            ContestDetail contestDetail = snapshot.getValue(ContestDetail.class);

                                            contestlist.add(contestDetail);
                                        }
                                    }

                                    if (!entryfee.equals("Overall")) {
                                        contestlist3.clear();
                                        for (int x = 0; x < contestlist.size(); x++) {
                                            ContestDetail contestDetail = contestlist.get(x);
                                            if (entryfee.equals("Free")) {
                                                if (contestDetail.getEf().equals(entryfee)) {

                                                    contestlist3.add(contestDetail);
                                                }
                                            }
                                            if (!entryfee.equals("Free"))
                                                if (!contestDetail.getEf().equals("Free")) {
                                                    contestlist3.add(contestDetail);
                                                }
                                        }
                                        contestlist.clear();
                                        contestlist.addAll(contestlist3);
                                        Collections.reverse(contestlist);
                                        displaycontest();
                                    } else {
                                        Collections.reverse(contestlist);
                                        displaycontest();
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }


            @Override
            public void onError(Exception ex) {
                Log.e(SNTPClient.TAG, ex.getMessage());
            }
        });
    }


    private void searchContest(String key) {

        if (key.length() < 20) {

        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child(getString(R.string.dbname_contestlist))
                    .child(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                contestlist2.clear();
                                ContestDetail contestDetail = dataSnapshot.getValue(ContestDetail.class);

                                contestlist2.add(contestDetail);
                                Collections.reverse(contestlist2);
                                displaysearch();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        }

    }

    private void displaycontest() {
        noPost.setVisibility(View.GONE);
        bottomProgress.setVisibility(View.GONE);

        flag1 = true;
        paginatedcontestlist = new ArrayList<>();

        if (contestlist != null && contestlist.size() != 0) {

            try {


                int iteration = contestlist.size();
                if (iteration > 5) {
                    iteration = 5;
                }
                mResults = 5;
                for (int i = 0; i < iteration; i++) {
                    paginatedcontestlist.add(contestlist.get(i));
                }
                if (upcomingContestRv.getAdapter().getClass().equals(contestUpcoming.getClass())) {

                    contestUpcoming = new AdapterContestUpcoming(this, paginatedcontestlist);
                    contestUpcoming.setHasStableIds(true);

                    upcomingContestRv.setAdapter(contestUpcoming);

                } else {

                    adapterContestUpcomingGrid = new AdapterContestUpcomingGrid(this, paginatedcontestlist);
                    adapterContestUpcomingGrid.setHasStableIds(true);

                    upcomingContestRv.setAdapter(adapterContestUpcomingGrid);
                }


            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());

            }

        } else {
            bottomProgress.setVisibility(View.GONE);
            noPost.setVisibility(View.VISIBLE);
            if (upcomingContestRv.getAdapter().getClass().equals(contestUpcoming.getClass())) {

                contestUpcoming = new AdapterContestUpcoming(this, contestlist);
                contestUpcoming.setHasStableIds(true);

                upcomingContestRv.setAdapter(contestUpcoming);

            } else {

                adapterContestUpcomingGrid = new AdapterContestUpcomingGrid(this, contestlist);
                adapterContestUpcomingGrid.setHasStableIds(true);

                upcomingContestRv.setAdapter(adapterContestUpcomingGrid);


            }

        }
    }

    public void displayMoreContest() {

        try {
            if (contestlist.size() > mResults && contestlist.size() > 0) {

                int iterations;
                if (contestlist.size() > (mResults + 10)) {
                    iterations = 10;
                } else {
                    iterations = contestlist.size() - mResults;
                }
                for (int i = mResults; i < mResults + iterations; i++) {
                    paginatedcontestlist.add(contestlist.get(i));

                }
                upcomingContestRv.post(new Runnable() {
                    @Override
                    public void run() {
                        if (upcomingContestRv.getAdapter().getClass().equals(contestUpcoming.getClass())) {
                            contestUpcoming.notifyItemRangeInserted(mResults, iterations);


                        } else {
                            adapterContestUpcomingGrid.notifyItemRangeInserted(mResults, iterations);
                        }
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

    private void displaysearch() {
        blurBg.setVisibility(View.VISIBLE);
        paginatedcontestSearch = new ArrayList<>();
        if (contestlist2 != null) {

            try {


                int iteration = contestlist2.size();
                if (iteration > 5) {
                    iteration = 5;
                }
                mResults2 = 5;
                for (int i = 0; i < iteration; i++) {
                    paginatedcontestSearch.add(contestlist2.get(i));
                }
                adapterContestSearch = new AdapterContestSearch(this, paginatedcontestSearch);
                contestSearchRv.setAdapter(adapterContestSearch);

            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());

            }

        }
    }

    public void displayMoreSearch() {

        try {
            if (contestlist2.size() > mResults2 && contestlist2.size() > 0) {

                int iterations;
                if (contestlist2.size() > (mResults2 + 10)) {
                    iterations = 10;
                } else {
                    iterations = contestlist2.size() - mResults2;
                }
                for (int i = mResults2; i < mResults2 + iterations; i++) {
                    paginatedcontestSearch.add(contestlist2.get(i));

                }
                mResults2 = mResults2 + iterations;
                contestSearchRv.post(new Runnable() {
                    @Override
                    public void run() {
                        adapterContestSearch.notifyDataSetChanged();
                    }
                });

            }

        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());

        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());

        }

    }
    private void setupBottomNavigationView() {
        Log.d(TAG, " setupBottomNavigationView:setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.BottomNavViewBar);
        BottomNaavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx,this);
        BottomNaavigationViewHelper.enableNavigation(UpcomingContestActivity.this, this, bottomNavigationViewEx);

        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }
    private void checkCurrentuser(FirebaseUser user) {
        Log.d(TAG, "checkCurrentuser:check if current user logged in");
        if (user == null) startActivity(new Intent(UpcomingContestActivity.this, LoginActivity.class));
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
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            settings.edit().clear().apply();
                            if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
                            startActivity(intent);
                        })
                        .show();
            } else Log.d(TAG, "onAuthStateChanged: signed_in:" + mUser.getUid());
        };
    }
    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        checkCurrentuser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
    }

}