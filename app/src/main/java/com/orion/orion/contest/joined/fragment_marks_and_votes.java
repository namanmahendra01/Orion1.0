package com.orion.orion.contest.joined;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.Adapters.AdapterContestCreated;
import com.orion.orion.Adapters.AdapterRankList;
import com.orion.orion.Adapters.AdapterRankListFull;
import com.orion.orion.Adapters.AdapterVoterList;
import com.orion.orion.R;
import com.orion.orion.contest.ranking;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.juryMarks;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class fragment_marks_and_votes extends Fragment {

    private TableLayout juryTable,juryTable2;
    private int participants=0;
    String joiningKey ="";
    RecyclerView votesRv;
    private ArrayList<String> votingLists;
    private ArrayList<String> paginatedVotingList;
    int mResults;
    users user = new users();




    private AdapterVoterList voterList;

    String Conteskey;


    public fragment_marks_and_votes() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marks_and_votes, container, false);

        juryTable=view.findViewById(R.id.jurytable);

        juryTable.setStretchAllColumns(true);
        Bundle b=getActivity().getIntent().getExtras();
        Conteskey=b.getString("contestId");
        joiningKey=b.getString("joiningKey");

        juryMarksTable(Conteskey,joiningKey);


        votesRv=view.findViewById(R.id.votelistRv);
        votesRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        votesRv.setLayoutManager(linearLayoutManager);



        votesRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {

                    displayMoreVoterList();

                }
            }
        });

        votingLists=new ArrayList<>();

        getRank(Conteskey,joiningKey);



        return view;

    }

    private void juryMarksTable(String conteskey, String joiningKey) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(getString(R.string.dbname_participantList))
                .child(conteskey)
                .child(joiningKey)
                .child(getString(R.string.juryMarks))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     juryMarks   juryMarks=dataSnapshot.getValue(juryMarks.class);
                        Log.d(TAG, "onDataChange: kkk"+juryMarks.toString());
                        TableRow row1= (TableRow)juryTable.getChildAt(1);
                        TableRow row2= (TableRow)juryTable.getChildAt(2);
                        TableRow row3= (TableRow)juryTable.getChildAt(3);
                        TextView t1=(TextView ) row1.getChildAt(1);
                        TextView t2=(TextView ) row1.getChildAt(2);
                        TextView t3=(TextView ) row2.getChildAt(1);
                        TextView t4=(TextView ) row2.getChildAt(2);
                        TextView t5=(TextView ) row3.getChildAt(1);
                        TextView t6=(TextView ) row3.getChildAt(2);
                        t1.setText(juryMarks.getJury1());
                        t3.setText(juryMarks.getJury2());
                        t5.setText(juryMarks.getJury3());

                        t2.setText(juryMarks.getComment1());
                        t4.setText(juryMarks.getComment2());
                        t6.setText(juryMarks.getComment3());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }




    private void getRank(String contestkey, String joiningKey) {

        DatabaseReference reference =FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_participantList))
                .child(contestkey)
                .child(joiningKey)
                .child(getString(R.string.voting_list))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        votingLists.clear();
                        int x=0;
                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                            x++;
                            String votingList= snapshot.getKey();


                            votingLists.add(votingList);
                            if (x==dataSnapshot.getChildrenCount()){
                                Collections.reverse(votingLists);

                                displayVoterList();
                            }

                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void displayVoterList() {
        paginatedVotingList = new ArrayList<>();
        if (votingLists != null) {

            try {


                int iteration = votingLists.size();
                if (iteration > 20) {
                    iteration = 20;
                }
                mResults = 20;
                for (int i = 0; i < iteration; i++) {
                    paginatedVotingList.add(votingLists.get(i));
                }
                voterList = new AdapterVoterList(getContext(),votingLists);
                voterList.setHasStableIds(true);
                votesRv.setAdapter(voterList);


            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());

            }

        }
    }

    public void displayMoreVoterList() {

        try {
            if (votingLists.size() > mResults && votingLists.size() > 0) {

                int iterations;
                if (votingLists.size() > (mResults + 20)) {
                    iterations = 20;
                } else {
                    iterations = votingLists.size() - mResults;
                }
                for (int i = mResults; i < mResults + iterations; i++) {
                    paginatedVotingList.add(votingLists.get(i));

                }
                int positionStart =mResults;
                votesRv.post(new Runnable() {
                    @Override
                    public void run() {
                        voterList.notifyItemRangeInserted(positionStart,iterations);
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