package com.orion.orion.contest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.orion.orion.Adapters.AdapterRankListFull;
import com.orion.orion.R;
import com.orion.orion.models.ParticipantList;

import java.util.ArrayList;

public class ranking extends AppCompatActivity {
    private static final String TAG = "ranking";

    ArrayList<ParticipantList> participantLists;
    ArrayList<ParticipantList> paginatedParticipantLists;
    int mResults;
    private ImageView backArrrow;
    private TextView mTopBarTitle;
    RecyclerView rankRv;
    private AdapterRankListFull rankList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        rankRv=findViewById(R.id.ranklist);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        rankRv.setLayoutManager(linearLayoutManager1);

        backArrrow= findViewById(R.id.backarrow);
        mTopBarTitle = findViewById(R.id.titleTopBar);

        backArrrow.setOnClickListener(view -> finish());
        mTopBarTitle.setText("Rank List");

        Intent i = getIntent();
        Bundle args = i.getBundleExtra("BUNDLE");
        assert args != null;
         participantLists =( args.getParcelableArrayList("participant"));
        Log.d(TAG, "onCreate: naman "+participantLists);



        rankRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {

                    displayMoreParticipantRank();

                }
            }
        });

        displayParticipantRank();
    }



    private void displayParticipantRank() {
        Log.d(TAG, "display first 10 contest");
        paginatedParticipantLists = new ArrayList<>();
        if (participantLists != null) {

            try {


                int iteration = participantLists.size();
                if (iteration > 20) {
                    iteration = 20;
                }
                mResults = 20;
                for (int i = 0; i < iteration; i++) {
                    paginatedParticipantLists.add(participantLists.get(i));
                }
                Log.d(TAG, "contest: sss" + paginatedParticipantLists.size());
                rankList = new AdapterRankListFull(ranking.this, paginatedParticipantLists);
                rankList.setHasStableIds(true);
                rankRv.setAdapter(rankList);

            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());

            }

        }
    }

    public void displayMoreParticipantRank() {
        Log.d(TAG, "display next 10 contest");

        try {
            if (participantLists.size() > mResults && participantLists.size() > 0) {

                int iterations;
                if (participantLists.size() > (mResults + 20)) {
                    Log.d(TAG, "display next 20 contest");
                    iterations = 20;
                } else {
                    Log.d(TAG, "display less tha 20 contest");
                    iterations = participantLists.size() - mResults;
                }
                for (int i = mResults; i < mResults + iterations; i++) {
                    paginatedParticipantLists.add(participantLists.get(i));

                }
                int positionStart =mResults;
                rankRv.post(new Runnable() {
                    @Override
                    public void run() {
                        rankList.notifyItemRangeInserted(positionStart,iterations);
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