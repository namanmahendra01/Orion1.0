package com.orion.orion.contest.Contest_Evaluation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orion.orion.Adapters.AdapterRankList;
import com.orion.orion.Adapters.AdapterWinners;
import com.orion.orion.R;
import com.orion.orion.contest.ranking;
import com.orion.orion.models.ContestDetail;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.juryMarks;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.StringManipilation;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import static com.android.volley.VolleyLog.TAG;

public class fragment_contest_overview extends Fragment {

    private TableLayout juryTable, juryTable2;

    String joiningKey = "";
    private int mResults;
    ScrollView scrollView;
int c=0;
    RecyclerView rankRv;
    private boolean notify = false;
    private FirebaseMethods mFirebaseMethods;

    private ArrayList<ParticipantList> participantLists;
    private ArrayList<ParticipantList> paginatedParticipantLists;

    private ArrayList<ParticipantList> participantLists2;

    private AdapterRankList rankList;
    private RelativeLayout relWinner;

    //    SP
    Gson gson;
    SharedPreferences sp;
    users user = new users();

   public LinearLayout progress;
    RecyclerView winnerRv;
    private AdapterWinners winnerList;
    private Button pubBtn, pubBtn2;
    String timestamp = "";
    TextView seeRank;
    boolean isPublicAndJuryVote=false;


    String Conteskey;


    public fragment_contest_overview() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contest_overview, container, false);

        juryTable = view.findViewById(R.id.jurytable);
        juryTable2 = view.findViewById(R.id.jurytable2);
        pubBtn = view.findViewById(R.id.pubBtn);
        pubBtn2 = view.findViewById(R.id.pubBtn2);
        relWinner = view.findViewById(R.id.relWin);
        scrollView = view.findViewById(R.id.scroll);
        seeRank = view.findViewById(R.id.seeRank);
        progress = view.findViewById(R.id.pro);

        mFirebaseMethods = new FirebaseMethods(getActivity());


        juryTable.setStretchAllColumns(true);
        juryTable2.setStretchAllColumns(true);
        Bundle b = getActivity().getIntent().getExtras();
        Conteskey = b.getString("contestId");


        rankRv = view.findViewById(R.id.rankList);
        rankRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rankRv.setLayoutManager(linearLayoutManager);

        seeRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getContext(), ranking.class);
                Bundle args = new Bundle();
                args.putParcelableArrayList("participant", (ArrayList<? extends Parcelable>) participantLists);
                i.putExtra("BUNDLE",args);
                startActivity(i);
            }
        });

//          Initialize SharedPreference variables
        sp = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        gson = new Gson();

        participantLists=new ArrayList<>();
        participantLists2 = new ArrayList<>();


//        **********************************************************

        winnerRv = view.findViewById(R.id.recyclerWinner);
        winnerRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        winnerRv.setLayoutManager(linearLayoutManager1);


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
                                if (dataSnapshot.exists()) {
                                    ContestDetail contestDetail = dataSnapshot.getValue(ContestDetail.class);
                                    String WinDec = contestDetail.getWinDec();
                                    boolean result = contestDetail.getResult();
                                    if (contestDetail.getVoteType().equals("Jury and Public")){
                                        isPublicAndJuryVote=true;
                                    }


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
                                                            if (result) {
                                                                pubBtn2.setVisibility(View.VISIBLE);
                                                                relWinner.setVisibility(View.VISIBLE);


                                                            } else {
                                                                if((Long.parseLong(winD)+172800000) < Long.parseLong(timestamp)){
                                                                    Log.d(TAG, "run: 1");
                                                                    publishResultAutomatically();

                                                                }
                                                               else if (Long.parseLong(winD) <= Long.parseLong(timestamp)) {
                                                                    Log.d(TAG, "run: 2");

                                                                    pubBtn.setVisibility(View.VISIBLE);
                                                                    relWinner.setVisibility(View.VISIBLE);

                                                                } else{
                                                                    Log.d(TAG, "run: 3");

                                                                    relWinner.setVisibility(View.INVISIBLE);

                                                                }
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

        pubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Publish Result");
                builder.setMessage("Are you sure, you want to publish result?");

//                set buttons
                builder.setPositiveButton("Publish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(Constraints.TAG, "Publish result: publishing result");
                        progress.setVisibility(View.VISIBLE);
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        boolean manual=true;
                       mFirebaseMethods.publishResut(manual,Conteskey,participantLists,progress,getActivity());
                        pubBtn2.setVisibility(View.VISIBLE);
                        relWinner.setVisibility(View.VISIBLE);

                    }
                });


                builder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
                builder.create().show();

            }
        });


        getParticipantListFromSP();


        return view;

    }

    private void publishResultAutomatically() {
        Log.d(TAG, "onDataChange: run 10");
        progress.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(getString(R.string.dbname_participantList))
                .child(Conteskey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: run 11");

                        int x=0;
                        ArrayList<ParticipantList> participantLists=new ArrayList<>();
                         for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            x++;
                             Log.d(TAG, "onDataChange: run 8");
                            participantLists.add(snapshot.getValue(ParticipantList.class));
                            if (x==dataSnapshot.getChildrenCount()){
                                Log.d(TAG, "onDataChange: run 9");

                                boolean manual=false;
//                                for (int l=0;l<4;l++){
//                                    participantLists.addAll(participantLists);
//                                }
                                mFirebaseMethods.publishResut(manual,Conteskey,participantLists, progress, getActivity());
                                pubBtn2.setVisibility(View.VISIBLE);
                                relWinner.setVisibility(View.VISIBLE);

                            }


                        }
                    }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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

//

                                                long total = t + o + p;
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
            long c;
            if (isPublicAndJuryVote){
                 c = (a + b) / 2;

            }else{
                 c = (a + b);

            }
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


                            users user = ds.getValue(users.class);
                            textView.setText(user.getUsername());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    private void getRank() {
        participantLists2.clear();
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
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "onDataChange: " + e.getMessage());
        }

        displayParticipantRank();

        winnerList = new AdapterWinners(getContext(), participantLists2);
        winnerList.setHasStableIds(true);

        winnerRv.setAdapter(winnerList);
    }

    //  fetching ParticipantList  from SharedPreferences
    private void getParticipantListFromSP() {
        String json = sp.getString(Conteskey, null);

        Type type = new TypeToken<ArrayList<ParticipantList>>() {
        }.getType();
        participantLists = gson.fromJson(json, type);
        if (participantLists == null) {    //        if no arrayList is present
            participantLists = new ArrayList<>();


            getRank();
            juryMarksTable(Conteskey);
            juryAndPublicMarksTable(Conteskey);

        } else {

            getRank();
            juryMarksTable(Conteskey);
            juryAndPublicMarksTable(Conteskey);

        }

    }

    private void displayParticipantRank() {
        Log.d(TAG, "display first 10 contest");
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
                Log.d(TAG, "contest: sss" + paginatedParticipantLists.size());
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