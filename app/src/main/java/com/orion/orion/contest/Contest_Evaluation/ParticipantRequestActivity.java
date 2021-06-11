package com.orion.orion.contest.Contest_Evaluation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.Adapters.AdapterParticipantRequest;
import com.orion.orion.R;
import com.orion.orion.models.ParticipantList;

import java.util.ArrayList;
import java.util.Collections;

public class ParticipantRequestActivity extends AppCompatActivity {
    private static final String TAG = "Participant FRAGMENT";
    private RecyclerView participantRv;
    private ArrayList<ParticipantList> participantLists;
    private ArrayList<ParticipantList> paginatedparticipantList;
    private int mResults;
    private FirebaseAuth fAuth;
    private ImageView backArrow;
    private TextView mTopBarTitle;
    private String Conteskey;

    private AdapterParticipantRequest adapterParticipantRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_requests);


        backArrow = findViewById(R.id.backarrow);
        mTopBarTitle = findViewById(R.id.titleTopBar);
        backArrow.setOnClickListener(view -> finish());
        mTopBarTitle.setText("Participant Request");
        Intent i = getIntent();
        Conteskey = i.getStringExtra("ContestKey");

        participantRv = findViewById(R.id.recycler_view4);
        participantRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        participantRv.setLayoutManager(linearLayoutManager);
        participantLists = new ArrayList<>();
        participantRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE)
                    displayMoreParticipnat();
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
        paginatedparticipantList = new ArrayList<>();
        if (participantLists != null) {
            try {
                int iteration = participantLists.size();
                if (iteration > 20) iteration = 20;
                mResults = 20;
                for (int i = 0; i < iteration; i++)
                    paginatedparticipantList.add(participantLists.get(i));
                adapterParticipantRequest = new AdapterParticipantRequest(ParticipantRequestActivity.this, paginatedparticipantList);
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
        try {
            if (participantLists.size() > mResults && participantLists.size() > 0) {
                int iterations;
                if (participantLists.size() > (mResults + 20)) {
                    iterations = 20;
                } else {
                    iterations = participantLists.size() - mResults;
                }
                for (int i = mResults; i < mResults + iterations; i++) {
                    paginatedparticipantList.add(participantLists.get(i));
                }
                participantRv.post(() -> adapterParticipantRequest.notifyItemRangeInserted(mResults, iterations));
                mResults = mResults + iterations;
            }

        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());
        }
    }
}