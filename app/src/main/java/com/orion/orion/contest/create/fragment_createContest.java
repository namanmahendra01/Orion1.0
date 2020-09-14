package com.orion.orion.contest.create;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.Adapters.AdapterContestCreated;
import com.orion.orion.R;
import com.orion.orion.models.CreateForm;

import java.util.ArrayList;
import java.util.Collections;


public class fragment_createContest extends Fragment {

    private static final String TAG = "JOINED FRAGMENT";
    RecyclerView createdContestRv;
    FloatingActionButton floatbtn;
    private ArrayList<CreateForm> contestlist;
    private ArrayList<CreateForm> paginatedContestlist;
    private int mResults;
    private FirebaseAuth fAuth;
    private AdapterContestCreated contestCreated;

    public fragment_createContest() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_create_contest, container, false);
        floatbtn = view.findViewById(R.id.float_btn);
        floatbtn.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), form.class);
            startActivity(i);
        });


        createdContestRv = view.findViewById(R.id.recycler_view3);
        createdContestRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        createdContestRv.setLayoutManager(linearLayoutManager);

        contestlist = new ArrayList<>();
        contestCreated = new AdapterContestCreated(getContext(), contestlist);
        createdContestRv.setAdapter(contestCreated);


        createdContestRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {

                    displayMoreContest();

                }
            }
        });


        fAuth = FirebaseAuth.getInstance();
        getContest();


        return view;
    }

    private void getContest() {

        FirebaseUser user = fAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_contests))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.created_contest))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        contestlist.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CreateForm createForm = snapshot.getValue(CreateForm.class);

                            contestlist.add(createForm);
                        }
                        Collections.reverse(contestlist);
                        displaycontest();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void displaycontest() {
        Log.d(TAG, "display first 10 contest");

        paginatedContestlist = new ArrayList<>();
        if (contestlist != null) {

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
                contestCreated = new AdapterContestCreated(getContext(), paginatedContestlist);
                createdContestRv.setAdapter(contestCreated);

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
                    paginatedContestlist.add(contestlist.get(i));

                }
                mResults = mResults + iterations;
                createdContestRv.post(new Runnable() {
                    @Override
                    public void run() {
                        contestCreated.notifyDataSetChanged();
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
