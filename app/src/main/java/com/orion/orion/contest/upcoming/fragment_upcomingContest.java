package com.orion.orion.contest.upcoming;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.orion.orion.Adapters.AdapterContestJoined;
import com.orion.orion.Adapters.AdapterContestSearch;
import com.orion.orion.Adapters.AdapterContestUpcoming;
import com.orion.orion.Adapters.AdapterContestUpcomingGrid;
import com.orion.orion.R;
import com.orion.orion.models.ContestDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class fragment_upcomingContest extends Fragment {

    private static final String TAG = "UPCOMING FRAGMENT";
    RecyclerView upcomingContestRv, contestSearchRv, upcomingFilterRv;
    private ArrayList<ContestDetail> contestlist;
    private FirebaseAuth fAuth;
    private EditText searchBox;
    int prevHeight;
    int height, dummyHeight;
    TextView noPost;
    LinearLayout blurBg;
    int x=0;
    private int mResults;
    ProgressBar bottomProgress;

    private int mResults2;
    RelativeLayout relativeLayout;
    ImageView gridB,gridY,colY,colB,filterB,filterY,cross;
    private ArrayList<ContestDetail> contestlist2;
    private ArrayList<ContestDetail> contestlist3;
    private ArrayList<ContestDetail> paginatedcontestlist;
    private ArrayList<ContestDetail> paginatedcontestSearch;
    SwipeRefreshLayout contesRefresh;
    boolean flag1 = false;
    private static int RETRY_DURATION = 1000;
    private static final Handler handler = new Handler(Looper.getMainLooper());

    private Spinner domainspinner, entryfeeSpinner;
    String domain = "Overall", entryfee = "All";


    private AdapterContestUpcoming contestUpcoming;
    private AdapterContestUpcomingGrid adapterContestUpcomingGrid;
    private AdapterContestSearch adapterContestSearch;

    public fragment_upcomingContest() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.activity_fragment_upcoming_contest, container, false);


        upcomingContestRv = view.findViewById(R.id.recycler_view1);
        contestSearchRv = view.findViewById(R.id.recyclerKey);
        searchBox = view.findViewById(R.id.search);
        domainspinner = view.findViewById(R.id.domainspinner);
        entryfeeSpinner = view.findViewById(R.id.entryfeeSpinner);
        gridB=view.findViewById(R.id.gridB);
        gridY=view.findViewById(R.id.gridY);
        colB=view.findViewById(R.id.columnB);
        colY=view.findViewById(R.id.columnY);
        filterB=view.findViewById(R.id.filter);
        filterY=view.findViewById(R.id.filteryellow);
        relativeLayout=view.findViewById(R.id.relparent);
        contesRefresh=view.findViewById(R.id.contest_refresh);
        blurBg=view.findViewById(R.id.pro);
        cross=view.findViewById(R.id.cross);
        bottomProgress=view.findViewById(R.id.pro2);

        noPost=view.findViewById(R.id.noPost);


//**************************************************************************************
        contestSearchRv.setHasFixedSize(true);
        final GridLayoutManager[] linearLayoutManager1 = {new GridLayoutManager(getContext(), 1)};

        contestSearchRv.setLayoutManager(linearLayoutManager1[0]);

        contestlist2 = new ArrayList<>();
        adapterContestSearch = new AdapterContestSearch(getContext(), contestlist2);
        contestSearchRv.setAdapter(adapterContestSearch);

//        ********************************************************************************
        upcomingContestRv.setHasFixedSize(true);
        final GridLayoutManager[] linearLayoutManager = {new GridLayoutManager(getContext(), 1)};



        upcomingContestRv.setLayoutManager(linearLayoutManager[0]);

        contestlist = new ArrayList<>();
        contestUpcoming = new AdapterContestUpcoming(getContext(), contestlist);
        contestUpcoming.setHasStableIds(true);

        upcomingContestRv.setAdapter(contestUpcoming);

        gridB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridB.setVisibility(View.GONE);
                gridY.setVisibility(View.VISIBLE);
                colY.setVisibility(View.GONE);
                colB.setVisibility(View.VISIBLE);

                    linearLayoutManager[0] = new GridLayoutManager(getContext(), 2);

                    upcomingContestRv.setLayoutManager(linearLayoutManager[0]);
                    adapterContestUpcomingGrid = new AdapterContestUpcomingGrid(getContext(), paginatedcontestlist);
                    adapterContestUpcomingGrid.setHasStableIds(true);
                    upcomingContestRv.setAdapter(adapterContestUpcomingGrid);

            }
        });
        filterB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterY.setVisibility(View.VISIBLE);
                filterB.setVisibility(View.GONE);

                expand(relativeLayout,1000);

            }
        });
        filterY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterY.setVisibility(View.GONE);
                filterB.setVisibility(View.VISIBLE);
                expand(relativeLayout,1000);


            }
        });
        colB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridB.setVisibility(View.VISIBLE);
                gridY.setVisibility(View.GONE);
                colY.setVisibility(View.VISIBLE);
                colB.setVisibility(View.GONE);
                linearLayoutManager[0] = new GridLayoutManager(getContext(),1);
                upcomingContestRv.setLayoutManager(linearLayoutManager[0]);
                contestUpcoming = new AdapterContestUpcoming(getContext(), paginatedcontestlist);
                contestUpcoming.setHasStableIds(true);
                upcomingContestRv.setAdapter(contestUpcoming);




            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        paginatedcontestSearch.clear();
                        adapterContestSearch.notifyDataSetChanged();
                        searchBox.setText("");
                blurBg.setVisibility(View.GONE);
            }
        });

        contestSearchRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {

                    displayMoreSearch();

                }
            }
        });

        upcomingContestRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (contestlist.size()!=paginatedcontestlist.size()){
                        bottomProgress.setVisibility(View.VISIBLE);

                    }
                    displayMoreContest();

                }else{
                    bottomProgress.setVisibility(View.GONE);

                }
            }
        });



        contestlist3 = new ArrayList<>();


        fAuth = FirebaseAuth.getInstance();
        getContestFiltered(domain, entryfee);

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

                String key="";
                if (searchBox.getText().toString().contains(".")||
                        searchBox.getText().toString().contains(",")||searchBox.getText().toString().contains("#")||
                        searchBox.getText().toString().contains("$")||searchBox.getText().toString().contains("[")||
                        searchBox.getText().toString().contains("]")){

                }else {
                     key = searchBox.getText().toString();
                }

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
                if (contesRefresh.isRefreshing() && flag1 ) {
                    contesRefresh.setRefreshing(false);
                    handler.removeCallbacks(this::checkRefresh);

                    flag1 = false;

                } else {
                    handler.postDelayed(this::checkRefresh, RETRY_DURATION);

                }
            }
        });


        return view;
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
        if (!domain.equals("Overall")) {


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child(getString(R.string.dbname_contestlist))
                    .orderByChild(getString(R.string.field_domain)).equalTo(domain)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            contestlist.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ContestDetail contestDetail = snapshot.getValue(ContestDetail.class);

                                contestlist.add(contestDetail);
                            }
                            if (!entryfee.equals("All")) {

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

                                displaycontest();                            }


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
                                ContestDetail contestDetail = snapshot.getValue(ContestDetail.class);

                                contestlist.add(contestDetail);
                            }

                            if (!entryfee.equals("All")) {
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

        flag1=true;
        paginatedcontestlist = new ArrayList<>();

        if (contestlist != null&&contestlist.size()!=0) {

            try {




                int iteration = contestlist.size();
                if (iteration > 5) {
                    iteration = 5;
                }
                mResults = 5;
                for (int i = 0; i < iteration; i++) {
                    paginatedcontestlist.add(contestlist.get(i));
                }
                if ( upcomingContestRv.getAdapter().getClass().equals( contestUpcoming.getClass())) {

                    contestUpcoming = new AdapterContestUpcoming(getContext(), paginatedcontestlist);
                    contestUpcoming.setHasStableIds(true);

                    upcomingContestRv.setAdapter(contestUpcoming);

                }else{

                    adapterContestUpcomingGrid = new AdapterContestUpcomingGrid(getContext(), paginatedcontestlist);
                    adapterContestUpcomingGrid.setHasStableIds(true);

                    upcomingContestRv.setAdapter(adapterContestUpcomingGrid);                }


            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());

            }

        }else{
            bottomProgress.setVisibility(View.GONE);
            noPost.setVisibility(View.VISIBLE);
            if ( upcomingContestRv.getAdapter().getClass().equals( contestUpcoming.getClass())) {

                contestUpcoming = new AdapterContestUpcoming(getContext(), contestlist);
                contestUpcoming.setHasStableIds(true);

                upcomingContestRv.setAdapter(contestUpcoming);

            }else{

                adapterContestUpcomingGrid = new AdapterContestUpcomingGrid(getContext(), contestlist);
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
                        if ( upcomingContestRv.getAdapter().getClass().equals( contestUpcoming.getClass())) {
                            contestUpcoming.notifyItemRangeInserted(mResults,iterations);


                        }else{
                            adapterContestUpcomingGrid.notifyItemRangeInserted(mResults,iterations);
                        }
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
                adapterContestSearch = new AdapterContestSearch(getContext(), paginatedcontestSearch);
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


}
