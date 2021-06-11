package com.orion.orion.contest.joined;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.Adapters.AdapterJudge;
import com.orion.orion.Adapters.AdapterVoterList;
import com.orion.orion.R;
import com.orion.orion.models.juryMarks;
import com.orion.orion.models.users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.android.volley.VolleyLog.TAG;

public class fragmentMarksAndVotes extends Fragment {

    private int participants = 0;
    String joiningKey = "";
    RecyclerView votesRv;
    private ArrayList<String> votingLists;
    private ArrayList<String> paginatedVotingList;
    int mResults;
    RelativeLayout jmark3,jmark2,jmark1;
    private TextView  c1, c2, c3, c4, c5, c6, c7, c8, c9, c10,ts;
    private TextView ec1, ec2, ec3, ec4, ec5, ec6, ec7, ec8, ec9, ec10, feedBack;
    private LinearLayout lec1, lec2, lec3, lec4, lec5, lec6, lec7, lec8, lec9, lec10;

    private TextView  c12, c22, c32, c42, c52, c62, c72, c82, c92, c102, ts2;
    private TextView ec12, ec22, ec32, ec42, ec52, ec62, ec72, ec82, ec92, ec102, feedBack2;
    private LinearLayout lec12, lec22, lec32, lec42, lec52, lec62, lec72, lec82, lec92, lec102;

    private TextView  c13, c23, c33, c43, c53, c63, c73, c83, c93, c103, ts3;
    private TextView ec13, ec23, ec33, ec43, ec53, ec63, ec73, ec83, ec93, ec103, feedBack3;
    private LinearLayout lec13, lec23, lec33, lec43, lec53, lec63, lec73, lec83, lec93, lec103;
    private AdapterVoterList voterList;

    String Conteskey,hostId,criterias;
    String[] criteriaArray;


    public fragmentMarksAndVotes() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_marks_and_votes, container, false);

        jmark1=itemView.findViewById(R.id.jmark1);
        jmark2=itemView.findViewById(R.id.jmark2);
        jmark3=itemView.findViewById(R.id.jmark3);

        Bundle b = getActivity().getIntent().getExtras();
        Conteskey = b.getString("contestId");
        joiningKey = b.getString("joiningKey");
        hostId = b.getString("hostId");


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(getString(R.string.dbname_contests))
                .child(hostId)
                .child(getString(R.string.created_contest))
                .child(Conteskey)

                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            criterias = snapshot.child(getString(R.string.field_judge_criteria)).getValue().toString();
                            if (criterias.contains("///")) {
                                criteriaArray = criterias.split("///");

                            } else {
                                criteriaArray[0] = criterias;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_contests))
                .child(hostId)
                .child(getString(R.string.created_contest))
                .child(Conteskey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            if(!dataSnapshot.child(getString(R.string.field_jury_name_1)).getValue().toString().equals("")){

                                initializeWidget1(itemView);
                                jmark1.setVisibility(android.view.View.VISIBLE);
                            }
                            if(!dataSnapshot.child(getString(R.string.field_jury_name_2)).getValue().toString().equals("")){
                                initializeWidget2(itemView);
                                jmark2.setVisibility(android.view.View.VISIBLE);

                            }
                            if(!dataSnapshot.child(getString(R.string.field_jury_name_3)).getValue().toString().equals("")){
                                initializeWidget3(itemView);
                                jmark3.setVisibility(android.view.View.VISIBLE);

                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



        votesRv = itemView.findViewById(R.id.votelistRv);
        votesRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
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

        votingLists = new ArrayList<>();

        getRank(Conteskey, joiningKey);


        return itemView;

    }

    private void initializeWidget3(android.view.View itemView) {


        lec13 = itemView.findViewById(R.id.markLL13);
        lec23 = itemView.findViewById(R.id.markLL23);
        lec33 = itemView.findViewById(R.id.markLL33);
        lec43 = itemView.findViewById(R.id.markLL43);
        lec53 = itemView.findViewById(R.id.markLL53);
        lec63 = itemView.findViewById(R.id.markLL63);
        lec73 = itemView.findViewById(R.id.markLL73);
        lec83 =itemView.findViewById(R.id.markLL83);
        lec93 = itemView.findViewById(R.id.markLL93);
        lec103 = itemView.findViewById(R.id.markLL103);

        ec13 = itemView.findViewById(R.id.markEt13);
        ec23 = itemView.findViewById(R.id.markEt23);
        ec33 = itemView.findViewById(R.id.markEt33);
        ec43 = itemView.findViewById(R.id.markEt43);
        ec53 = itemView.findViewById(R.id.markEt53);
        ec63 = itemView.findViewById(R.id.markEt63);
        ec73 = itemView.findViewById(R.id.markEt73);
        ec83 = itemView.findViewById(R.id.markEt83);
        ec93 = itemView.findViewById(R.id.markEt93);
        ec103 = itemView.findViewById(R.id.markEt103);

        c13 = itemView.findViewById(R.id.cr13);
        c23 = itemView.findViewById(R.id.cr23);
        c33 = itemView.findViewById(R.id.cr33);
        c43 = itemView.findViewById(R.id.cr43);
        c53 = itemView.findViewById(R.id.cr53);
        c63 = itemView.findViewById(R.id.cr63);
        c73 = itemView.findViewById(R.id.cr73);
        c83 = itemView.findViewById(R.id.cr83);
        c93 = itemView.findViewById(R.id.cr93);
        c103 = itemView.findViewById(R.id.cr103);

        feedBack3=itemView.findViewById(R.id.feedback3);
        ts3=itemView.findViewById(R.id.tsEt3);


        setCriteriaViews(c13, c23, c33, c43, c53, c63, c73, c83, c93, c103,lec13, lec23, lec33, lec43, lec53, lec63, lec73, lec83, lec93, lec103);

        setMarks(ec13, ec23, ec33, ec43, ec53, ec63, ec73, ec83, ec93, ec103, feedBack3,ts3,"j3","c3","xj3");

    }



    private void initializeWidget2(android.view.View itemView) {
        lec12 = itemView.findViewById(R.id.markLL12);
        lec22 = itemView.findViewById(R.id.markLL22);
        lec32 = itemView.findViewById(R.id.markLL32);
        lec42 = itemView.findViewById(R.id.markLL42);
        lec52= itemView.findViewById(R.id.markLL52);
        lec62 = itemView.findViewById(R.id.markLL62);
        lec72 = itemView.findViewById(R.id.markLL72);
        lec82 = itemView.findViewById(R.id.markLL82);
        lec92 = itemView.findViewById(R.id.markLL92);
        lec102 = itemView.findViewById(R.id.markLL102);

        ec12 = itemView.findViewById(R.id.markEt12);
        ec22 = itemView.findViewById(R.id.markEt22);
        ec32 = itemView.findViewById(R.id.markEt32);
        ec42 = itemView.findViewById(R.id.markEt42);
        ec52 = itemView.findViewById(R.id.markEt52);
        ec62 = itemView.findViewById(R.id.markEt62);
        ec72 = itemView.findViewById(R.id.markEt72);
        ec82 = itemView.findViewById(R.id.markEt82);
        ec92= itemView.findViewById(R.id.markEt92);
        ec102 = itemView.findViewById(R.id.markEt102);

        c12 = itemView.findViewById(R.id.cr12);
        c22 = itemView.findViewById(R.id.cr22);
        c32 = itemView.findViewById(R.id.cr32);
        c42 = itemView.findViewById(R.id.cr42);
        c52 = itemView.findViewById(R.id.cr52);
        c62 = itemView.findViewById(R.id.cr62);
        c72 = itemView.findViewById(R.id.cr72);
        c82 = itemView.findViewById(R.id.cr82);
        c92 = itemView.findViewById(R.id.cr92);
        c102 = itemView.findViewById(R.id.cr102);

        ts2=itemView.findViewById(R.id.tsEt2);

        feedBack2=itemView.findViewById(R.id.feedback2);

        setCriteriaViews(c12, c22, c32, c42, c52, c62, c72, c82, c92, c102,lec12, lec22, lec32, lec42, lec52, lec62, lec72, lec82, lec92, lec102);
        setMarks(ec12, ec22, ec32, ec42, ec52, ec62, ec72, ec82, ec92, ec102, feedBack2,ts2,"j2","c2","xj2");

    }

    private void initializeWidget1(android.view.View itemView) {

        lec1 = itemView.findViewById(R.id.markLL1);
        lec2 = itemView.findViewById(R.id.markLL2);
        lec3 = itemView.findViewById(R.id.markLL3);
        lec4 = itemView.findViewById(R.id.markLL4);
        lec5 = itemView.findViewById(R.id.markLL5);
        lec6 = itemView.findViewById(R.id.markLL6);
        lec7 = itemView.findViewById(R.id.markLL7);
        lec8 = itemView.findViewById(R.id.markLL8);
        lec9 = itemView.findViewById(R.id.markLL9);
        lec10 = itemView.findViewById(R.id.markLL10);

        ec1 = itemView.findViewById(R.id.markEt1);
        ec2 = itemView.findViewById(R.id.markEt2);
        ec3 = itemView.findViewById(R.id.markEt3);
        ec4 = itemView.findViewById(R.id.markEt4);
        ec5 = itemView.findViewById(R.id.markEt5);
        ec6 = itemView.findViewById(R.id.markEt6);
        ec7 = itemView.findViewById(R.id.markEt7);
        ec8 = itemView.findViewById(R.id.markEt8);
        ec9 = itemView.findViewById(R.id.markEt9);
        ec10 = itemView.findViewById(R.id.markEt10);

        c1 = itemView.findViewById(R.id.cr1);
        c2 = itemView.findViewById(R.id.cr2);
        c3 = itemView.findViewById(R.id.cr3);
        c4 = itemView.findViewById(R.id.cr4);
        c5 = itemView.findViewById(R.id.cr5);
        c6 = itemView.findViewById(R.id.cr6);
        c7 = itemView.findViewById(R.id.cr7);
        c8 = itemView.findViewById(R.id.cr8);
        c9 = itemView.findViewById(R.id.cr9);
        c10 = itemView.findViewById(R.id.cr10);

        ts=itemView.findViewById(R.id.tsEt);
        feedBack=itemView.findViewById(R.id.feedback);

        setCriteriaViews(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10,lec1, lec2, lec3, lec4, lec5, lec6, lec7, lec8, lec9, lec10);
        setMarks(ec1, ec2, ec3, ec4, ec5, ec6, ec7, ec8, ec9, ec10, feedBack,ts,"j1","c1","xj1");

    }

    private void setMarks(TextView ec1, TextView ec2, TextView ec3, TextView ec4,
                          TextView ec5, TextView ec6, TextView ec7, TextView ec8, TextView ec9,
                          TextView ec10, TextView feedBack, TextView ts, String j, String c, String xj) {



        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(getString(R.string.dbname_participantList))
                .child(Conteskey)
                .child(joiningKey)
                .child(getString(R.string.juryMarks))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            try {
                                String[] marksArray = new String[10];
                                marksArray = snapshot.child(xj).getValue().toString().split("///");

                                ts.setText(snapshot.child(j).getValue().toString());

                                feedBack.setText(snapshot.child(c).getValue().toString());

                                putMarks(marksArray, ec1, ec2, ec3, ec4, ec5, ec6, ec7, ec8, ec9, ec10);

                            }catch (NullPointerException e){
                            }



                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void putMarks(String[] marksArray, TextView ec1, TextView ec2, TextView ec3, TextView ec4, TextView ec5, TextView ec6, TextView ec7, TextView ec8, TextView ec9, TextView ec10) {
        if (marksArray.length > 0) {

            for (int x = 1; x <= marksArray.length; x++) {

                if (x == 1) {

                    ec1.setText(marksArray[x - 1]);
                } else if (x == 2) {
                    ec2.setText(marksArray[x - 1]);

                } else if (x == 3) {
                    ec3.setText(marksArray[x - 1]);

                } else if (x == 4) {
                   ec4.setText(marksArray[x - 1]);

                } else if (x == 5) {
                   ec5.setText(marksArray[x - 1]);

                } else if (x == 6) {
                    ec6.setText(marksArray[x - 1]);

                } else if (x == 7) {
                    ec7.setText(marksArray[x - 1]);

                } else if (x == 8) {
                   ec8.setText(marksArray[x - 1]);

                } else if (x == 9) {
                    ec9.setText(marksArray[x - 1]);

                } else if (x == 10) {
                    ec10.setText(marksArray[x - 1]);

                }
            }
        }
    }

    private void setCriteriaViews(TextView c1, TextView c2, TextView c3, TextView c4,
                                  TextView c5, TextView c6, TextView c7, TextView c8,
                                  TextView c9, TextView c10, LinearLayout lec1, LinearLayout lec2,
                                  LinearLayout lec3, LinearLayout lec4, LinearLayout lec5, LinearLayout lec6,
                                  LinearLayout lec7, LinearLayout lec8, LinearLayout lec9, LinearLayout lec10) {
        for (int x = 1; x <= criteriaArray.length; x++) {
            if (x == 1) {
                lec1.setVisibility(View.VISIBLE);
                c1.setText(criteriaArray[x - 1]);
            } else if (x == 2) {
                lec2.setVisibility(View.VISIBLE);
                c2.setText(criteriaArray[x - 1]);
            } else if (x == 3) {
                lec3.setVisibility(View.VISIBLE);
                c3.setText(criteriaArray[x - 1]);
            } else if (x == 4) {
                lec4.setVisibility(View.VISIBLE);
                c4.setText(criteriaArray[x - 1]);
            } else if (x == 5) {
                lec5.setVisibility(View.VISIBLE);
                c5.setText(criteriaArray[x - 1]);
            } else if (x == 6) {
                lec6.setVisibility(View.VISIBLE);
                c6.setText(criteriaArray[x - 1]);
            } else if (x == 7) {
                lec7.setVisibility(View.VISIBLE);
                c7.setText(criteriaArray[x - 1]);
            } else if (x == 8) {
                lec8.setVisibility(View.VISIBLE);
                c8.setText(criteriaArray[x - 1]);
            } else if (x == 9) {
                lec9.setVisibility(View.VISIBLE);
                c9.setText(criteriaArray[x - 1]);
            } else if (x == 10) {
                lec10.setVisibility(View.VISIBLE);
                c10.setText(criteriaArray[x - 1]);
            }

        }

    }



    private void getRank(String contestkey, String joiningKey) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_participantList))
                .child(contestkey)
                .child(joiningKey)
                .child(getString(R.string.voting_list))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        votingLists.clear();
                        int x = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            x++;
                            String votingList = snapshot.getKey();


                            votingLists.add(votingList);
                            if (x == dataSnapshot.getChildrenCount()) {
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
                voterList = new AdapterVoterList(getContext(), votingLists);
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
                int positionStart = mResults;
                votesRv.post(new Runnable() {
                    @Override
                    public void run() {
                        voterList.notifyItemRangeInserted(positionStart, iterations);
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