package com.orion.orion.contest.Contest_Evaluation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.orion.orion.Adapters.AdapterParticipantList;
import com.orion.orion.Adapters.AdapterParticipantRequest;
import com.orion.orion.R;
import com.orion.orion.models.ParticipantList;

import java.util.ArrayList;
import java.util.Collections;

public class Participant_Request extends AppCompatActivity {
    private static final String TAG = "Participant FRAGMENT";
    RecyclerView participantRv;
    private ArrayList<ParticipantList> participantLists;
    private ArrayList<ParticipantList> paginatedparticipantList;

    private int mResults;
    private FirebaseAuth fAuth;
    ImageView backArrow;

    String Conteskey;

    private AdapterParticipantRequest adapterParticipantRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_requests);

        Intent i = getIntent();
        Conteskey = i.getStringExtra("ContestKey");
        backArrow = findViewById(R.id.backarrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        participantRv = findViewById(R.id.recycler_view4);
        participantRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        participantRv.setLayoutManager(linearLayoutManager);

        participantLists = new ArrayList<>();

        participantRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {

                    displayMoreParticipnat();

                }
            }
        });

        fAuth = FirebaseAuth.getInstance();

        getParticipant(Conteskey);


    }

    private void getParticipant(String contestkey) {

        FirebaseUser user = fAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_request))
                .child(getString(R.string.dbname_participantList))
                .child(contestkey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        participantLists.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ParticipantList participantList = snapshot.getValue(ParticipantList.class);

                            Log.d(TAG, "onDataChange: " + participantList.toString());

                            participantLists.add(participantList);

                            Collections.reverse(participantLists);
                            displayParticipant();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void displayParticipant() {
        Log.d(TAG, "display first 10 participant");

        paginatedparticipantList = new ArrayList<>();
        if (participantLists != null) {

            try {

                int iteration = participantLists.size();
                if (iteration > 20) {
                    iteration = 20;
                }
                mResults = 20;
                for (int i = 0; i < iteration; i++) {
                    paginatedparticipantList.add(participantLists.get(i));
                }
                Log.d(TAG, "participant: sss" + paginatedparticipantList.size());
                adapterParticipantRequest = new AdapterParticipantRequest(Participant_Request.this, paginatedparticipantList);
                adapterParticipantRequest.setHasStableIds(true);
                participantRv.setAdapter(adapterParticipantRequest);

            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());

            }

        }
    }

    public void displayMoreParticipnat() {
        Log.d(TAG, "display next 15 participant");

        try {
            if (participantLists.size() > mResults && participantLists.size() > 0) {

                int iterations;
                if (participantLists.size() > (mResults + 20)) {
                    Log.d(TAG, "display next 15 participant");
                    iterations = 20;
                } else {
                    Log.d(TAG, "display less tha 15 participant");
                    iterations = participantLists.size() - mResults;
                }
                for (int i = mResults; i < mResults + iterations; i++) {
                    paginatedparticipantList.add(participantLists.get(i));

                }
                participantRv.post(new Runnable() {
                    @Override
                    public void run() {
                        adapterParticipantRequest.notifyItemRangeInserted(mResults,iterations);
                    }
                });
                mResults = mResults + iterations;


            }

        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());

        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());

        }

    }

}