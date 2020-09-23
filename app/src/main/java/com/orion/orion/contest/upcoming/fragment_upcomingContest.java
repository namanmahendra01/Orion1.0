package com.orion.orion.contest.upcoming;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private int mResults;
    private int mResults2;
    ImageView gridB,gridY,colY,colB;
    private ArrayList<ContestDetail> contestlist2;
    private ArrayList<ContestDetail> contestlist3;
    private ArrayList<ContestDetail> paginatedcontestlist;
    private ArrayList<ContestDetail> paginatedcontestSearch;


    private Spinner domainspinner, entryfeeSpinner;
    String domain = "All", entryfee = "All";


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
                    upcomingContestRv.setAdapter(adapterContestUpcomingGrid);

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
                upcomingContestRv.setAdapter(contestUpcoming);




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

                    displayMoreContest();

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


        return view;
    }

    private void getContestFiltered(String domain, String entryfee) {
        if (!domain.equals("All")) {


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.child(getString(R.string.dbname_contestlist))
                    .orderByChild("doman").equalTo(domain)
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
                                    Log.d(TAG, "onDataChange: asd" + contestDetail);
                                    if (entryfee.equals("Free")) {
                                        if (contestDetail.getEntryfee().equals(entryfee)) {
                                            Log.d(TAG, "onDataChange: qwe" + contestDetail.getEntryfee());

                                            contestlist3.add(contestDetail);
                                        }
                                    }
                                    if (!entryfee.equals("Free"))
                                        if (!contestDetail.getEntryfee().equals("Free")) {
                                            Log.d(TAG, "onDataChange: qwe" + contestDetail.getEntryfee());
                                            contestlist3.add(contestDetail);
                                        }
                                }
                                contestlist.clear();
                                contestlist.addAll(contestlist3);
                                Log.d(TAG, "onDataChange: size "+contestlist.size());
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
                                        if (contestDetail.getEntryfee().equals(entryfee)) {

                                            contestlist3.add(contestDetail);
                                        }
                                    }
                                    if (!entryfee.equals("Free"))
                                        if (!contestDetail.getEntryfee().equals("Free")) {
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
        Log.d(TAG, "display first 10 contest");

        paginatedcontestlist = new ArrayList<>();
        if (contestlist != null) {

            try {




                int iteration = contestlist.size();
                if (iteration > 5) {
                    iteration = 5;
                }
                mResults = 5;
                for (int i = 0; i < iteration; i++) {
                    paginatedcontestlist.add(contestlist.get(i));
                }
                Log.d(TAG, "contest: sss" + paginatedcontestlist.size());
                if ( upcomingContestRv.getAdapter().getClass().equals( contestUpcoming.getClass())) {
                    contestUpcoming = new AdapterContestUpcoming(getContext(), paginatedcontestlist);
                    upcomingContestRv.setAdapter(contestUpcoming);

                }else{
                    adapterContestUpcomingGrid = new AdapterContestUpcomingGrid(getContext(), paginatedcontestlist);
                    upcomingContestRv.setAdapter(adapterContestUpcomingGrid);                }


            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());

            }

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
                    paginatedcontestlist.add(contestlist.get(i));

                }
                mResults = mResults + iterations;
                upcomingContestRv.post(new Runnable() {
                    @Override
                    public void run() {
                        if ( upcomingContestRv.getAdapter().getClass().equals( contestUpcoming.getClass())) {
                            contestUpcoming.notifyDataSetChanged();


                        }else{
                            adapterContestUpcomingGrid.notifyDataSetChanged();
                        }
                    }
                });

            }

        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());

        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());

        }

    }
    private void displaysearch() {
        Log.d(TAG, "display first 10 contest");

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
                Log.d(TAG, "contest: sss" + paginatedcontestSearch.size());
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
        Log.d(TAG, "display next 10 contest");

        try {
            if (contestlist2.size() > mResults2 && contestlist2.size() > 0) {

                int iterations;
                if (contestlist2.size() > (mResults2 + 10)) {
                    Log.d(TAG, "display next 20 contest");
                    iterations = 10;
                } else {
                    Log.d(TAG, "display less tha 20 contest");
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
