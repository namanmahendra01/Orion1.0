package com.orion.orion.contest.result;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class fragment_result_overview extends Fragment {

    private TableLayout juryTable, juryTable2;

    String joiningKey = "";
    String timestamp;
    RecyclerView rankRv;

    private ArrayList<ParticipantList> participantLists;
    private ArrayList<ParticipantList> participantLists2;

    private AdapterRankList rankList;


    users user = new users();

    RecyclerView winnerRv;
    private AdapterWinners winnerList;
    private RelativeLayout relWinner;


    String Conteskey;


    public fragment_result_overview() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contest_overview, container, false);

        juryTable = view.findViewById(R.id.jurytable);
        juryTable2 = view.findViewById(R.id.jurytable2);

        juryTable.setStretchAllColumns(true);
        juryTable2.setStretchAllColumns(true);
        Bundle b = getActivity().getIntent().getExtras();
        Conteskey = b.getString("contestId");
        relWinner = view.findViewById(R.id.relWin);

        juryMarksTable(Conteskey);
        juryAndPublicMarksTable(Conteskey);


        rankRv = view.findViewById(R.id.rankList);
        rankRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rankRv.setLayoutManager(linearLayoutManager);


        participantLists = new ArrayList<>();
        rankList = new AdapterRankList(getContext(), participantLists);
        rankList.setHasStableIds(true);
        rankRv.setAdapter(rankList);

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

                timestamp = String.valueOf(date1.getTime());

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child(getString(R.string.dbname_contestlist))
                        .child(Conteskey)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ContestDetail contestDetail = dataSnapshot.getValue(ContestDetail.class);
                                String WinDec = contestDetail.getWinDec();


                                java.text.DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                Date date = null;
                                try {
                                    date = (Date) formatter.parse(WinDec);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String winD = String.valueOf(date.getTime());
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            synchronized (this) {
                                                wait(1000);

                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        if (Long.parseLong(winD) <= Long.parseLong(timestamp)) {
                                                            relWinner.setVisibility(View.VISIBLE);

                                                        }


                                                    }
                                                });

                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    ;
                                };
                                thread.start();
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                Log.e(SNTPClient.TAG, rawDate);

            }


            @Override
            public void onError(Exception ex) {
                Log.e(SNTPClient.TAG, ex.getMessage());
            }
        });


        getRank(Conteskey);


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
                            joiningKey = participantList.getJoiningKey().toString();
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
                                            getUsername(finalParticipantList.getUserid(), t1v);
                                            t1v.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                                                    Query userquery = ref
                                                            .child(getString(R.string.dbname_users))
                                                            .orderByChild(getString(R.string.field_username))
                                                            .equalTo(t1v.getText().toString());
                                                    userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                                                                user = singleSnapshot.getValue(users.class);
                                                                Intent i = new Intent(getActivity(), profile.class);
                                                                i.putExtra(getString(R.string.calling_activity), getString(R.string.home));

                                                                i.putExtra(getString(R.string.intent_user), user);
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
                                            t2v.setText(juryMarks.getJury1());
                                            t2v.setTextColor(Color.BLACK);
                                            t2v.setGravity(Gravity.CENTER);
                                            tbrow.addView(t2v);
                                            TextView t3v = new TextView(getActivity());
                                            t3v.setText(juryMarks.getJury2());
                                            t3v.setTextColor(Color.BLACK);
                                            t3v.setGravity(Gravity.CENTER);
                                            tbrow.addView(t3v);
                                            TextView t4v = new TextView(getActivity());
                                            t4v.setText(juryMarks.getJury3());
                                            t4v.setTextColor(Color.BLACK);
                                            t4v.setGravity(Gravity.CENTER);
                                            tbrow.addView(t4v);
                                            TextView t5v = new TextView(getActivity());
                                            long x = 0, y = 0, z = 0;
                                            if (juryMarks.getJury1().equals("")) {
                                                x = 0;
                                            } else {
                                                x = Long.parseLong(juryMarks.getJury1());

                                            }
                                            if (juryMarks.getJury2().equals("")) {
                                                y = 0;
                                            } else {
                                                y = Long.parseLong(juryMarks.getJury2());

                                            }
                                            if (juryMarks.getJury3().equals("")) {
                                                z = 0;
                                            } else {
                                                z = Long.parseLong(juryMarks.getJury3());

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
                            joiningKey = participantList.getJoiningKey().toString();
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
                                            getUsername(finalParticipantList.getUserid(), t1v);
                                            t1v.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                                                    Query userquery = ref
                                                            .child(getString(R.string.dbname_users))
                                                            .orderByChild(getString(R.string.field_username))
                                                            .equalTo(t1v.getText().toString());
                                                    userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                                                                user = singleSnapshot.getValue(users.class);
                                                                Intent i = new Intent(getActivity(), profile.class);
                                                                i.putExtra(getString(R.string.calling_activity), getString(R.string.home));

                                                                i.putExtra(getString(R.string.intent_user), user);
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
                                            if (juryMarks.getJury1().equals("")) {
                                                t = 0;
                                            } else {
                                                t = Long.parseLong(juryMarks.getJury1());

                                            }
                                            if (juryMarks.getJury2().equals("")) {
                                                o = 0;
                                            } else {
                                                o = Long.parseLong(juryMarks.getJury2());

                                            }
                                            if (juryMarks.getJury3().equals("")) {
                                                p = 0;
                                            } else {
                                                p = Long.parseLong(juryMarks.getJury3());

                                            }

                                            Log.d(TAG, "onDataChange: marks" + t);
                                            Log.d(TAG, "onDataChange: marks" + o);

                                            Log.d(TAG, "onDataChange: marks" + p);

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
                                            getVoteCount(finalParticipantList.getJoiningKey(), t3v, contestkey, t4v, t2v);
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
                    .child("totalScore")
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
                        Log.d(TAG, "onDataChange: child count " + i);
//
                        t3v.setText(String.valueOf(i));
                        getTotal(t2v, t3v, t4v, contestkey, joiningKey);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void getUsername(String userid, TextView textView) {
        DatabaseReference ref5 = FirebaseDatabase.getInstance().getReference();
        ref5.child(getString(R.string.dbname_user_account_settings)).
                orderByChild("user_id").equalTo(userid).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            Log.d(TAG, "onDataChange: ji" + dataSnapshot.getChildren().toString());

                            users user = ds.getValue(users.class);
                            textView.setText(user.getUsername());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void getRank(String contestkey) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                                return Integer.compare(o1.getTotalScore(), o2.getTotalScore());
                            }
                        });
                        Collections.reverse(participantLists);
                        try {
                            for (int x = 0; x < 3; x++) {
                                participantLists2.add(participantLists.get(x));

                            }
                        }catch (IndexOutOfBoundsException e){
                            Log.e(TAG, "onDataChange: "+e.getMessage() );
                        }

                        rankList.notifyDataSetChanged();
                        winnerList.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}