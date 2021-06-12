package com.orion.orion.contest.joined;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.orion.orion.Adapters.AdapterWinners;
import com.orion.orion.R;
import com.orion.orion.contest.Contest_Evaluation.ChatRoomActivity;
import com.orion.orion.contest.ranking;
import com.orion.orion.models.ContestDetail;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.juryMarks;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.StringManipilation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.android.volley.VolleyLog.TAG;

public class fragmentJC_Overview extends Fragment {

    private TableLayout juryTable, juryTable2;

    String joiningKey = "";
    String timestamp;
    RecyclerView rankRv;

    private ArrayList<ParticipantList> participantLists;
    private ArrayList<ParticipantList> participantLists2;

    private ArrayList<ParticipantList> paginatedParticipantLists;
    private AdapterRankList rankList;
    int mResults;

    users user = new users();

    RecyclerView winnerRv;
    private AdapterWinners winnerList;
    private RelativeLayout relWinner;
    RelativeLayout juryRl;

    Button chatRoom;
    String Conteskey;
    TextView seeRank;
    private SwipeRefreshLayout participantRefresh;
    boolean flag1 = false;
    private static int RETRY_DURATION = 1000;
    private static final Handler handler = new Handler(Looper.getMainLooper());

    private TextView paramType;
    private TextView param1;
    private TextView param2;
    private TextView param3;
    private TextView totalParam1;
    private TextView totalParam2;

    public fragmentJC_Overview() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contest_overview, container, false);

        juryTable = view.findViewById(R.id.jurytable);
        juryTable2 = view.findViewById(R.id.jurytable2);
        chatRoom = view.findViewById(R.id.chatRoom);

        juryTable.setStretchAllColumns(true);
        juryTable2.setStretchAllColumns(true);

        Bundle b = getActivity().getIntent().getExtras();
        Conteskey = b.getString("contestId");

        relWinner = view.findViewById(R.id.relWin);
        juryRl = view.findViewById(R.id.jutyRl);

        seeRank = view.findViewById(R.id.seeRank);
        participantRefresh = view.findViewById(R.id.participant_refresh);

        paramType = view.findViewById(R.id.paramType);
        param1 = view.findViewById(R.id.param1);
        param2 = view.findViewById(R.id.param2);
        param3 = view.findViewById(R.id.param3);
        totalParam1 = view.findViewById(R.id.totalParam1);
        totalParam2 = view.findViewById(R.id.totalParam2);

        chatRoom.setVisibility(View.VISIBLE);

        chatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ChatRoomActivity.class);
                i.putExtra("ContestId", Conteskey);
                startActivity(i);
            }
        });
        seeRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), ranking.class);
                Bundle args = new Bundle();
                args.putParcelableArrayList("participant", (ArrayList<? extends Parcelable>) participantLists);
                i.putExtra("BUNDLE", args);
                startActivity(i);
            }
        });

        rankRv = view.findViewById(R.id.rankList);
        rankRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rankRv.setLayoutManager(linearLayoutManager);
        participantLists = new ArrayList<>();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(getString(R.string.dbname_contestlist))
                .child(Conteskey)
                .child(getString(R.string.field_result))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.getValue().toString().equals("true")) {
                                relWinner.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        getRank(Conteskey);
//        **********************************************************
        participantLists2 = new ArrayList<>();
        winnerRv = view.findViewById(R.id.recyclerWinner);
        winnerRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        winnerRv.setLayoutManager(linearLayoutManager1);

        winnerList = new AdapterWinners(getContext(), participantLists2);
        winnerList.setHasStableIds(true);
        winnerRv.setAdapter(winnerList);


        ref.child(getString(R.string.dbname_contestlist))
                .child(Conteskey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            ContestDetail contestDetail = snapshot.getValue(ContestDetail.class);
                            assert contestDetail != null;
                            String WinDec = contestDetail.getWd();
                            boolean result = contestDetail.getR();
                            if (contestDetail.getCty() != null && contestDetail.getCty().equals("Quiz")) {
                                paramType.setText("Points Table");
                                param1.setText("Accuracy");
                                param2.setText("Speed");
                                param3.setText("Consistency");
                                totalParam1.setText("Points");
                                totalParam2.setText("-");
                            }
                            if(contestDetail.getVt().equals("Public"))
                                juryRl.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        participantRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                flag1 = false;

                getRank(Conteskey);
                checkRefresh();
            }

            private void checkRefresh() {
                if (participantRefresh.isRefreshing() && flag1) {
                    participantRefresh.setRefreshing(false);
                    handler.removeCallbacks(this::checkRefresh);
                    flag1 = false;
                } else handler.postDelayed(this::checkRefresh, RETRY_DURATION);
            }
        });

        getRank(Conteskey);

        if (juryRl.getVisibility() != View.GONE) {
            juryMarksTable(Conteskey);

        }
        juryAndPublicMarksTable(Conteskey);


        return view;

    }

    private void juryMarksTable(String conteskey) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(getString(R.string.dbname_participantList))
                .child(conteskey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ParticipantList participantList = new ParticipantList();
                            participantList = snapshot.getValue(ParticipantList.class);
                            Log.d(TAG, "onDataChange: " + participantList.toString());
                            joiningKey = participantList.getJi().toString();
                            Log.d(TAG, "onDataChange: " + joiningKey);
                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
                            ParticipantList finalParticipantList = participantList;
                            ref2.child(getString(R.string.dbname_participantList))
                                    .child(conteskey)
                                    .child(joiningKey)
                                    .child(getString(R.string.juryMarks))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            juryMarks juryMarks = new juryMarks();
                                            juryMarks = dataSnapshot.getValue(juryMarks.class);
                                            TableRow tbrow = new TableRow(getActivity());
                                            tbrow.setLayoutParams(new TableLayout.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                                            tbrow.setWeightSum(5);
                                            TextView t1v = new TextView(getActivity());
                                            getU(finalParticipantList.getUi(), t1v);
                                            t1v.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                                                    Query userquery = ref
                                                            .child(getString(R.string.dbname_username))
                                                            .child(t1v.getText().toString());
                                                    userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()) {
                                                                String username2 = dataSnapshot.getValue().toString();

                                                                Intent i = new Intent(getContext(), profile.class);
                                                                i.putExtra(getString(R.string.calling_activity), getString(R.string.home));

                                                                i.putExtra(getString(R.string.intent_user), username2);
                                                                startActivity(i);

                                                            }


                                                        }


                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            Log.d(TAG, "Query Cancelled");
                                                        }
                                                    });


                                                }
                                            });
                                            t1v.setTextColor(Color.RED);
                                            t1v.setGravity(Gravity.CENTER);
                                            tbrow.addView(t1v);
                                            TextView t2v = new TextView(getActivity());
                                            t2v.setText(juryMarks.getJ1());
                                            t2v.setTextColor(Color.BLACK);
                                            t2v.setGravity(Gravity.CENTER);
                                            tbrow.addView(t2v);
                                            TextView t3v = new TextView(getActivity());
                                            t3v.setText(juryMarks.getJ2());
                                            t3v.setTextColor(Color.BLACK);
                                            t3v.setGravity(Gravity.CENTER);
                                            tbrow.addView(t3v);
                                            TextView t4v = new TextView(getActivity());
                                            t4v.setText(juryMarks.getJ3());
                                            t4v.setTextColor(Color.BLACK);
                                            t4v.setGravity(Gravity.CENTER);
                                            tbrow.addView(t4v);
                                            TextView t5v = new TextView(getActivity());
                                            long x = 0, y = 0, z = 0;
                                            if (juryMarks.getJ1().equals("")) {
                                                x = 0;
                                            } else {
                                                x = Long.parseLong(juryMarks.getJ1());

                                            }
                                            if (juryMarks.getJ2().equals("")) {
                                                y = 0;
                                            } else {
                                                y = Long.parseLong(juryMarks.getJ2());

                                            }
                                            if (juryMarks.getJ3().equals("")) {
                                                z = 0;
                                            } else {
                                                z = Long.parseLong(juryMarks.getJ3());

                                            }
                                            long total = x + y + z;
                                            t5v.setText(String.valueOf(total));
                                            t5v.setTextColor(Color.BLACK);
                                            t5v.setMaxLines(1);
                                            t5v.setGravity(Gravity.CENTER);
                                            tbrow.addView(t5v);
                                            juryTable.addView(tbrow);
                                            Log.d(TAG, "onDataChange: " + juryMarks.toString());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void juryAndPublicMarksTable(String contestkey) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(getString(R.string.dbname_participantList))
                .child(contestkey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ParticipantList participantList = new ParticipantList();
                            participantList = snapshot.getValue(ParticipantList.class);
                            Log.d(TAG, "onDataChange: " + participantList.toString());
                            joiningKey = participantList.getJi().toString();
                            Log.d(TAG, "onDataChange: " + joiningKey);
                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
                            ParticipantList finalParticipantList = participantList;
                            ref2.child(getString(R.string.dbname_participantList))
                                    .child(contestkey)
                                    .child(joiningKey)
                                    .child(getString(R.string.juryMarks))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            juryMarks juryMarks = new juryMarks();
                                            juryMarks = dataSnapshot.getValue(juryMarks.class);
                                            TableRow tbrow = new TableRow(getActivity());
                                            tbrow.setLayoutParams(new TableLayout.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                                            tbrow.setWeightSum(5);
                                            TextView t1v = new TextView(getActivity());
                                            getU(finalParticipantList.getUi(), t1v);
                                            t1v.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                                                    Query userquery = ref
                                                            .child(getString(R.string.dbname_username))
                                                            .child(t1v.getText().toString());
                                                    userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()) {
                                                                String username2 = dataSnapshot.getValue().toString();

                                                                Intent i = new Intent(getContext(), profile.class);
                                                                i.putExtra(getString(R.string.calling_activity), getString(R.string.home));

                                                                i.putExtra(getString(R.string.intent_user), username2);
                                                                startActivity(i);

                                                            }


                                                        }


                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            Log.d(TAG, "Query Cancelled");
                                                        }
                                                    });

                                                }
                                            });
                                            t1v.setTextColor(Color.RED);
                                            t1v.setGravity(Gravity.CENTER);
                                            tbrow.addView(t1v);
                                            TextView t2v = new TextView(getActivity());
                                            long t = 0, o = 0, p = 0;
                                            if (juryMarks.getJ1().equals("")) {
                                                t = 0;
                                            } else {
                                                t = Long.parseLong(juryMarks.getJ1());

                                            }
                                            if (juryMarks.getJ2().equals("")) {
                                                o = 0;
                                            } else {
                                                o = Long.parseLong(juryMarks.getJ2());

                                            }
                                            if (juryMarks.getJ3().equals("")) {
                                                p = 0;
                                            } else {
                                                p = Long.parseLong(juryMarks.getJ3());

                                            }


//

                                            long total = t + o + p;
                                            Log.d(TAG, "onDataChange: marks" + total);
                                            t2v.setText(String.valueOf(total));
                                            t2v.setTextColor(Color.BLACK);
                                            t2v.setGravity(Gravity.CENTER);
                                            tbrow.addView(t2v);
                                            TextView t3v = new TextView(getActivity());
//


                                            t3v.setTextColor(Color.BLACK);
                                            t3v.setGravity(Gravity.CENTER);
                                            tbrow.addView(t3v);

                                            TextView t4v = new TextView(getActivity());


                                            t4v.setTextColor(Color.BLACK);
                                            t4v.setGravity(Gravity.CENTER);
                                            getVoteCount(finalParticipantList.getJi(), t3v, contestkey, t4v, t2v);
                                            tbrow.addView(t4v);
                                            juryTable2.addView(tbrow);
                                            Log.d(TAG, "onDataChange: " + juryMarks.toString());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void getTotal(TextView t2v, TextView t3v, TextView t4v, String contestkey, String Joiningkey) {
        try {
            long a = Long.parseLong(t2v.getText().toString());
            long b = Long.parseLong(t3v.getText().toString());
            long c = (a + b) / 2;
            t4v.setText(String.valueOf(c));
            DatabaseReference ref4 = FirebaseDatabase.getInstance().getReference();
            ref4.child(getString(R.string.dbname_participantList))
                    .child(contestkey)
                    .child(Joiningkey)
                    .child(getString(R.string.field_total_score))
                    .setValue((int) c);
        } catch (NumberFormatException e) {

        }

    }

    private void getVoteCount(String joiningKey, TextView t3v, String contestkey, TextView t4v, TextView t2v) {
        DatabaseReference ref4 = FirebaseDatabase.getInstance().getReference();
        ref4.child(getString(R.string.dbname_participantList))
                .child(contestkey)
                .child(joiningKey)
                .child(getString(R.string.voting_list))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long i = dataSnapshot.getChildrenCount();
//
                        t3v.setText(String.valueOf(i));
                        getTotal(t2v, t3v, t4v, contestkey, joiningKey);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void getU(String userid, TextView textView) {
        DatabaseReference ref5 = FirebaseDatabase.getInstance().getReference();
        ref5.child(getString(R.string.dbname_users)).
                child(userid).
                child(getString(R.string.field_username)).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String user = dataSnapshot.getValue().toString();
                        textView.setText(user);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void getRank(String contestkey) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_participantList))
                .child(contestkey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        participantLists.clear();
                        participantLists2.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ParticipantList participantList = snapshot.getValue(ParticipantList.class);

                            participantLists.add(participantList);
                        }
                        Collections.sort(participantLists, new Comparator<ParticipantList>() {
                            @Override
                            public int compare(ParticipantList o1, ParticipantList o2) {
                                return Integer.compare(o1.getTs(), o2.getTs());
                            }
                        });
                        Collections.reverse(participantLists);
                        try {
                            for (int x = 0; x < 3; x++) {
                                participantLists2.add(participantLists.get(x));

                            }
                        } catch (IndexOutOfBoundsException e) {
                            Log.e(TAG, "onDataChange: " + e.getMessage());
                        }

                        displayParticipantRank();
                        winnerList.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void displayParticipantRank() {
        flag1=true;

        paginatedParticipantLists = new ArrayList<>();
        if (participantLists != null) {

            try {


                int iteration = participantLists.size();
                if (iteration > 10) {
                    iteration = 10;
                }
                mResults = 10;
                for (int i = 0; i < iteration; i++) {
                    paginatedParticipantLists.add(participantLists.get(i));
                }
                rankList = new AdapterRankList(getContext(), paginatedParticipantLists);
                rankList.setHasStableIds(true);
                rankRv.setAdapter(rankList);

            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());

            }

        }
    }
}