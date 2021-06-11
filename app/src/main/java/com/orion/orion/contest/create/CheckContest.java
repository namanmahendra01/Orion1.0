package com.orion.orion.contest.create;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.QuizActivity;
import com.orion.orion.R;
import com.orion.orion.login.LoginActivity;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.QuizQuestion;
import com.orion.orion.models.QuizQuestionEncoded;
import com.orion.orion.models.users;
import com.orion.orion.util.FirebaseMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CheckContest extends AppCompatActivity {
    private static final String TAG = "ViewContestFragment";

    private CardView cardView;
    private CardView jcCard;
    public LinearLayout progress;
    private RelativeLayout topLayout1;

    private TextView contestType;
    private TextView quizDateTime;
    private TableLayout publicVotingContainer;
    private TextView entryfee;
    private TextView title;
    private TextView descrip;
    private TextView rules;
    private TextView totalprize;
    private TextView maxPart;
    private TextView voteType;
    private TextView gp;
    private TextView jcTv;
    private TextView jcTv2;
    private TextView regBegin;
    private TextView regEnd;
    private TextView voteBegin;
    private TextView voteEnd;
    private TextView domain;
    private TextView openfor;
    private TextView juryname1;
    private TextView juryname2;
    private TextView juryname3;
    private TextView jury;
    private TextView jurypl1;
    private TextView jurypl2;
    private TextView jurypl3;
    private TextView hostedby;
    private TextView filetype;
    private TextView windate;
    private TextView p1Tv;
    private TextView p2Tv;
    private TextView p3Tv;
    private ImageView poster;
    private ImageView jurypic1;
    private ImageView jurypic2;
    private ImageView jurypic3;

    private Button postContest;

    private String newContestKey;
    private CreateForm mCreateForm;
    private LinearLayout prizeLinear;
    private int imageCount = 0;

    private String duration = "";
    ArrayList<QuizQuestion> quizQuestionArrayList;
    private String mAppend = "file:/";
    private String jpic1 = "";
    private String jpic2 = "";
    private String jpic3 = "";
    private String posterlink = "";
    private String judgingCriterias = "";

    private String p1 = "p1";
    private String p2 = "p2";
    private String p3 = "p3";
    private String p4 = "p4";


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_details);
        Log.d(TAG, "onCreate: started.");
        mFirebaseMethods = new FirebaseMethods(CheckContest.this);
        setupFirebaseAuth();

        progress = findViewById(R.id.pro);

        contestType = findViewById(R.id.contestTypeTv);
        quizDateTime = findViewById(R.id.quizDateTime);
        publicVotingContainer = findViewById(R.id.publicVotingContainer);
        entryfee = findViewById(R.id.entryfeeTv);
        title = findViewById(R.id.titleTv);
        descrip = findViewById(R.id.descripTv);
        rules = findViewById(R.id.ruleTv);
        totalprize = findViewById(R.id.totalprizeTv);
        maxPart = findViewById(R.id.maxPartTv);
        voteType = findViewById(R.id.voteTypeTv);
        regBegin = findViewById(R.id.regB);
        regEnd = findViewById(R.id.regE);
        voteBegin = findViewById(R.id.votB);
        voteEnd = findViewById(R.id.votE);
        domain = findViewById(R.id.domainTV);
        openfor = findViewById(R.id.openForTv);
        juryname1 = findViewById(R.id.jname1Tv);
        juryname2 = findViewById(R.id.jname2Tv);
        juryname3 = findViewById(R.id.jname3Tv);
        jurypl1 = findViewById(R.id.jpl1Tv);
        jurypl2 = findViewById(R.id.jpl2Tv);
        jurypl3 = findViewById(R.id.jpl3Tv);
        jurypic1 = findViewById(R.id.jpic1);
        jurypic2 = findViewById(R.id.jpic2);
        jurypic3 = findViewById(R.id.jpic3);
        hostedby = findViewById(R.id.hostedbyTv);
        filetype = findViewById(R.id.fileTv);
        windate = findViewById(R.id.winDate);
        poster = findViewById(R.id.posterIv);
        jury = findViewById(R.id.jury);
        cardView = findViewById(R.id.jurydetail);
        p1Tv = findViewById(R.id.p1Tv);
        p2Tv = findViewById(R.id.p2Tv);
        p3Tv = findViewById(R.id.p3Tv);
        postContest = findViewById(R.id.postContest);
        prizeLinear = findViewById(R.id.prizell);
        jcTv = findViewById(R.id.jc);
        jcTv2 = findViewById(R.id.jcTv2);
        jcCard = findViewById(R.id.jccard);

        gp = findViewById(R.id.gp);

        topLayout1 = findViewById(R.id.reLayout1);

        topLayout1.setVisibility(View.GONE);
        topLayout1.setVisibility(View.VISIBLE);


        postContest.setVisibility(View.VISIBLE);

        setgp(FirebaseAuth.getInstance().getCurrentUser().getUid(), gp);


        Intent intent = getIntent();

        duration = intent.getStringExtra("duration");
        quizQuestionArrayList = intent.getParcelableArrayListExtra("questionList");

        posterlink = intent.getStringExtra("poster");
        title.setText(intent.getStringExtra("title"));
        descrip.setText(intent.getStringExtra("descrip"));
        rules.setText(intent.getStringExtra("rule"));

        //fees + prize
        if (intent.getStringExtra("entryfee").equals("")) entryfee.setText("Free");
        else entryfee.setText(intent.getStringExtra("entryfee"));
        if (intent.getStringExtra("total_prize").equals("")) {
            prizeLinear.setVisibility(View.GONE);
            totalprize.setText("-");
        } else {
            totalprize.setText(intent.getStringExtra("total_prize"));
            prizeLinear.setVisibility(View.VISIBLE);
        }

        if(intent.getStringExtra("contestType").equals(""))
            contestType.setText("-");
        else {
            if(intent.getStringExtra("contestType").equals("Quiz"))
                publicVotingContainer.setVisibility(View.GONE);
            else
                publicVotingContainer.setVisibility(View.VISIBLE);
            contestType.setText(intent.getStringExtra("contestType"));
        }
        if(intent.getStringExtra("votetype").equals(""))
            voteType.setText("-");
        else
            voteType.setText(intent.getStringExtra("votetype"));
        if (intent.getStringExtra("votetype").equals("Jury") || intent.getStringExtra("votetype").equals("Jury and Public")) {
            String f_string = "";
            jcCard.setVisibility(View.VISIBLE);
            jcTv.setVisibility(View.VISIBLE);
            judgingCriterias = intent.getStringExtra("judgeCriteria");
            String[] array = judgingCriterias.split("///");
            for (String a : array)
                f_string = f_string + "\n" + a;
            Log.d(TAG, "onCreate: " + f_string);
            jcTv2.setText(f_string);
        }

        //domain + open for
        domain.setText(intent.getStringExtra("domain"));
        openfor.setText(intent.getStringExtra("openfor"));

        //participation + submission
        if (intent.getStringExtra("maxLimit").equals("")) maxPart.setText("Unlimited");
        else maxPart.setText(intent.getStringExtra("maxLimit"));
        if(intent.getStringExtra("filetype").equals(""))
            filetype.setText("-");
        else
            filetype.setText(intent.getStringExtra("filetype"));

        //registration dates
        regBegin.setText(intent.getStringExtra("regBegin"));
        regEnd.setText(intent.getStringExtra("regEnd"));

        //voting dates
        if (intent.getStringExtra("voteBegin").equals("") || intent.getStringExtra("voteEnd").equals("")) {
            voteBegin.setText("-");
            voteEnd.setText("-");
            publicVotingContainer.setVisibility(View.GONE);
        } else {
            voteBegin.setText(intent.getStringExtra("voteBegin"));
            voteEnd.setText(intent.getStringExtra("voteEnd"));
        }

        //winner announcement date + quiz date and time
        windate.setText(intent.getStringExtra("winDeclare"));
        quizDateTime.setText(intent.getStringExtra("startTime"));

        hostedby.setText(intent.getStringExtra("host"));
        p1Tv.setText(intent.getStringExtra("place_1"));
        p2Tv.setText(intent.getStringExtra("place_2"));
        p3Tv.setText(intent.getStringExtra("place_3"));


        //jury
        if (intent.getStringExtra("jname_1").equals("")) {
            jury.setVisibility(View.GONE);
            cardView.setVisibility(View.GONE);
        } else if (!intent.getStringExtra("jname_1").equals("")
                && intent.getStringExtra("jname_2").equals("")) {
            jury.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.VISIBLE);
            jurypic1.setVisibility(View.VISIBLE);
            juryname1.setVisibility(View.VISIBLE);
            jurypl1.setVisibility(View.VISIBLE);
            jurypic2.setVisibility(View.GONE);
            juryname2.setVisibility(View.GONE);
            jurypl2.setVisibility(View.GONE);
            jurypic3.setVisibility(View.GONE);
            juryname3.setVisibility(View.GONE);
            jurypl3.setVisibility(View.GONE);
            Log.d(TAG, "onCreate: " + intent.getStringExtra("jname_1"));

            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            db.child(getString(R.string.dbname_username))
                    .child(intent.getStringExtra("jname_1"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                db.child(getString(R.string.dbname_users))
                                        .child(dataSnapshot.getValue().toString())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                users user = snapshot.getValue(users.class);
                                                juryname1.setText(user.getDn());
                                                jurypl1.setText(user.getU());
                                                Log.d(TAG, "onDataChange: " + user.getDn());
                                                Glide.with(getApplicationContext())
                                                        .load(user.getPp())
                                                        .placeholder(R.drawable.load)
                                                        .error(R.drawable.default_image2)
                                                        .placeholder(R.drawable.load)
                                                        .thumbnail(0.25f)
                                                        .into(jurypic1);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        } else if (!intent.getStringExtra("jname_1").equals("")
                && !intent.getStringExtra("jname_2").equals("")
                && intent.getStringExtra("jname_3").equals("")) {
            jury.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.VISIBLE);
            jurypic1.setVisibility(View.VISIBLE);
            juryname1.setVisibility(View.VISIBLE);
            jurypl1.setVisibility(View.VISIBLE);
            jurypic2.setVisibility(View.VISIBLE);
            juryname2.setVisibility(View.VISIBLE);
            jurypl2.setVisibility(View.VISIBLE);
            jurypic3.setVisibility(View.GONE);
            juryname3.setVisibility(View.GONE);
            jurypl3.setVisibility(View.GONE);
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            db.child(getString(R.string.dbname_username))
                    .child(Objects.requireNonNull(intent.getStringExtra("jname_1")))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists())
                                db.child(getString(R.string.dbname_users))
                                        .child(dataSnapshot.getValue().toString())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                users user = snapshot.getValue(users.class);
                                                juryname1.setText(user.getDn());
                                                jurypl1.setText(user.getU());
                                                Glide.with(getApplicationContext())
                                                        .load(user.getPp())
                                                        .placeholder(R.drawable.load)
                                                        .error(R.drawable.default_image2)
                                                        .placeholder(R.drawable.load)
                                                        .thumbnail(0.25f)
                                                        .into(jurypic1);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            db.child(getString(R.string.dbname_username))
                    .child(intent.getStringExtra("jname_2"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists())
                                db.child(getString(R.string.dbname_users))
                                        .child(dataSnapshot.getValue().toString())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                users user = snapshot.getValue(users.class);
                                                juryname2.setText(user.getDn());
                                                jurypl2.setText(user.getU());
                                                Glide.with(getApplicationContext())
                                                        .load(user.getPp())
                                                        .placeholder(R.drawable.load)
                                                        .error(R.drawable.default_image2)
                                                        .placeholder(R.drawable.load)
                                                        .thumbnail(0.25f)
                                                        .into(jurypic2);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            jury.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.VISIBLE);
            jurypic1.setVisibility(View.VISIBLE);
            juryname1.setVisibility(View.VISIBLE);
            jurypl1.setVisibility(View.VISIBLE);
            jurypic2.setVisibility(View.VISIBLE);
            juryname2.setVisibility(View.VISIBLE);
            jurypl2.setVisibility(View.VISIBLE);
            jurypic3.setVisibility(View.VISIBLE);
            juryname3.setVisibility(View.VISIBLE);
            jurypl3.setVisibility(View.VISIBLE);
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            db.child(getString(R.string.dbname_username))
                    .child(intent.getStringExtra("jname_1"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists())
                                db.child(getString(R.string.dbname_users))
                                        .child(dataSnapshot.getValue().toString())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                users user = snapshot.getValue(users.class);
                                                juryname1.setText(user.getDn());
                                                jurypl1.setText(user.getU());
                                                Log.d(TAG, "onDataChange: " + user.getDn());
                                                Glide.with(getApplicationContext())
                                                        .load(user.getPp())
                                                        .placeholder(R.drawable.load)
                                                        .error(R.drawable.default_image2)
                                                        .placeholder(R.drawable.load)
                                                        .thumbnail(0.25f)
                                                        .into(jurypic1);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            db.child(getString(R.string.dbname_username))
                    .child(intent.getStringExtra("jname_2"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists())
                                db.child(getString(R.string.dbname_users))
                                        .child(dataSnapshot.getValue().toString())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                users user = snapshot.getValue(users.class);


                                                juryname2.setText(user.getDn());
                                                jurypl2.setText(user.getU());
                                                Log.d(TAG, "onDataChange: " + user.getDn());

                                                Glide.with(getApplicationContext())
                                                        .load(user.getPp())
                                                        .placeholder(R.drawable.load)
                                                        .error(R.drawable.default_image2)
                                                        .placeholder(R.drawable.load)
                                                        .thumbnail(0.25f)
                                                        .into(jurypic2);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            db.child(getString(R.string.dbname_username))
                    .child(intent.getStringExtra("jname_3"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) db.child(getString(R.string.dbname_users))
                                    .child(dataSnapshot.getValue().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            users user = snapshot.getValue(users.class);


                                            juryname3.setText(user.getDn());
                                            jurypl3.setText(user.getU());
                                            Log.d(TAG, "onDataChange: " + user.getDn());

                                            Glide.with(getApplicationContext())
                                                    .load(user.getPp())
                                                    .placeholder(R.drawable.load)
                                                    .error(R.drawable.default_image2)
                                                    .placeholder(R.drawable.load)
                                                    .thumbnail(0.25f)
                                                    .into(jurypic3);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }


        Glide.with(getApplicationContext())
                .load(posterlink)
                .placeholder(R.drawable.load)
                .error(R.drawable.default_image2)
                .placeholder(R.drawable.load)
                .into(poster);

        postContest.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to create this contest? ")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        progress.setVisibility(View.VISIBLE);
                        postcontest();
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    })
                    .show();
        });

    }

    private void postcontest() {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        newContestKey = db.child(getString(R.string.dbname_contests)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
        mCreateForm = new CreateForm();


        ArrayList<QuizQuestionEncoded> quizQuestionEncodedArrayList = new ArrayList<>();
        for(QuizQuestion quizQuestion : quizQuestionArrayList){
            QuizQuestionEncoded temp = new QuizQuestionEncoded();
            temp.setQu(quizQuestion.getQuestion());
            temp.setOpt(quizQuestion.getOption1() + getString(R.string.option_seperator_delimintor) + quizQuestion.getOption2()+ getString(R.string.option_seperator_delimintor) +quizQuestion.getOption3()+ getString(R.string.option_seperator_delimintor) +quizQuestion.getOption4());
            temp.setAns(quizQuestion.getAnswer());
            quizQuestionEncodedArrayList.add(temp);
        }
        quizQuestionArrayList.clear();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(getString(R.string.field_contestType), contestType.getText().toString());
        hashMap.put(getString(R.string.field_quesDuration), duration);
        hashMap.put(getString(R.string.field_questions), quizQuestionEncodedArrayList);
        hashMap.put(getString(R.string.field_quizStartDateTime), quizDateTime.getText().toString());
        hashMap.put(getString(R.string.field_entry_fee), entryfee.getText().toString());
        hashMap.put(getString(R.string.field_contest_title), title.getText().toString());
        hashMap.put(getString(R.string.field_description), descrip.getText().toString());
        hashMap.put(getString(R.string.field_file_type), filetype.getText().toString());
        hashMap.put(getString(R.string.field_domain), domain.getText().toString());
        hashMap.put(getString(R.string.field_vote_type), voteType.getText().toString());
        hashMap.put(getString(R.string.field_rule), rules.getText().toString());
        hashMap.put(getString(R.string.field_registration_begin), regBegin.getText().toString());
        hashMap.put(getString(R.string.field_registration_end), regEnd.getText().toString());
        hashMap.put(getString(R.string.field_voting_begin), voteBegin.getText().toString());
        hashMap.put(getString(R.string.field_voting_end), voteEnd.getText().toString());
        hashMap.put(getString(R.string.field_winners_declare), windate.getText().toString());
        hashMap.put(getString(R.string.field_max_participant_limit), maxPart.getText().toString());
        hashMap.put(getString(R.string.field_place1_prize), p1Tv.getText().toString());
        hashMap.put(getString(R.string.field_place2_prize), p2Tv.getText().toString());
        hashMap.put(getString(R.string.field_place3_prize), p3Tv.getText().toString());
        hashMap.put(getString(R.string.field_total_prize), totalprize.getText().toString());
        hashMap.put(getString(R.string.field_jury_name_1), jurypl1.getText().toString());
        hashMap.put(getString(R.string.field_jury_name_2), jurypl2.getText().toString());
        hashMap.put(getString(R.string.field_jury_name_3), jurypl3.getText().toString());
        hashMap.put(getString(R.string.field_host), hostedby.getText().toString());
        hashMap.put(getString(R.string.field_open_for), openfor.getText().toString());
        hashMap.put(getString(R.string.field_timestamp), timeStamp);
        hashMap.put(getString(R.string.field_contest_ID), newContestKey);
        hashMap.put(getString(R.string.field_user_id), FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put(getString(R.string.field_status), "waiting");
        hashMap.put(getString(R.string.field_judge_criteria), judgingCriterias);

        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put(getString(R.string.field_contestType), contestType.getText().toString());
        hashMap2.put(getString(R.string.field_entry_fee), entryfee.getText().toString());
        hashMap2.put(getString(R.string.field_vote_type), voteType.getText().toString());
        hashMap2.put(getString(R.string.field_timestamp), timeStamp);
        hashMap2.put(getString(R.string.field_domain), domain.getText().toString());
        hashMap2.put(getString(R.string.field_user_id), FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap2.put(getString(R.string.field_contest_ID), newContestKey);
        hashMap.put(getString(R.string.field_quesDuration), duration);
        hashMap2.put(getString(R.string.field_registration_begin), regBegin.getText().toString());
        hashMap2.put(getString(R.string.field_registration_end), regEnd.getText().toString());
        hashMap2.put(getString(R.string.field_voting_begin), voteBegin.getText().toString());
        hashMap2.put(getString(R.string.field_voting_end), voteEnd.getText().toString());
        hashMap.put(getString(R.string.field_quizStartDateTime), quizDateTime.getText().toString());
        hashMap2.put(getString(R.string.field_winners_declare), windate.getText().toString());
        hashMap2.put(getString(R.string.field_result), false);
        hashMap2.put(getString(R.string.field_max_participant_limit), maxPart.getText().toString());

        db.child(getString(R.string.dbname_contests))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.created_contest))
                .child(newContestKey)
                .setValue(hashMap);
        Log.d(TAG, "postcontest: "+quizQuestionEncodedArrayList.size());
        db.child(getString(R.string.dbname_request)).child(getString(R.string.dbname_contests)).child(newContestKey).setValue(hashMap2);
        Toast.makeText(CheckContest.this, "Form Submitted", Toast.LENGTH_SHORT).show();
        mFirebaseMethods.uploadContest(imageCount, jpic1, null, newContestKey, p1, "");
        mFirebaseMethods.uploadContest(imageCount, jpic2, null, newContestKey, p2, "");
        mFirebaseMethods.uploadContest(imageCount, jpic3, null, newContestKey, p3, "");
        mFirebaseMethods.uploadContest(imageCount, posterlink, null, newContestKey, p4, "");
    }


    private void setupFirebaseAuth() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null)
                Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
            else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                Log.d(TAG, "onAuthStateChanged: navigating to login");
                SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                new android.app.AlertDialog.Builder(getApplicationContext())
                        .setTitle("No user logon found")
                        .setMessage("We will be logging u out. \n Please try to log in again")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            settings.edit().clear().apply();
                            if (mAuthListener != null)
                                mAuth.removeAuthStateListener(mAuthListener);
                            startActivity(intent);
                        })
                        .show();
            }
        };
    }

    private void setgp(String userid, TextView gp) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_contests))
                .child(userid)
                .child(getString(R.string.field_contest_completed))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            long y = (long) snapshot.getValue();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.child(getString(R.string.dbname_contests))
                                    .child(userid)
                                    .child(getString(R.string.field_contest_reports))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                long x = (long) snapshot.getValue();
                                                gp.setText(String.valueOf(100 - (((x * 100) / y))) + "%");
                                            } else gp.setText("100%");
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        } else gp.setText("100%");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
