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
import com.orion.orion.R;
import com.orion.orion.login.login;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.users;
import com.orion.orion.util.FirebaseMethods;

import java.util.HashMap;
import java.util.Objects;

public class CheckContest extends AppCompatActivity {
    private static final String TAG = "ViewContestFragment";
    private static final int ACTIVITY_NUM = 3;


    private TextView entryfee, title, descrip, rules, totalprize, maxPart, voteType,gp,
            regBegin, regEnd, voteBegin, voteEnd, domain, openfor, juryname1, juryname2, juryname3, jury, jurypl1, jurypl2, jurypl3, hostedby, filetype, windate, p1Tv, p2Tv, p3Tv;
    private ImageView poster, jurypic1, jurypic2, jurypic3;
    private String mAppend = "file:/";
    private String jpic1 = "", jpic2 = "", jpic3 = "", posterlink = "";
    private CardView cardView;
    private Button postContest;
   public LinearLayout progress;
   RelativeLayout topLayout1;

    private String newContestKey;
    private CreateForm mCreateForm;
    private LinearLayout prizeLinear;
    private int imageCount = 0;
    private String p1 = "p1", p2 = "p2", p3 = "p3", p4 = "p4";


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

        gp =findViewById(R.id.gp);

        topLayout1 =findViewById(R.id.reLayout1);

        topLayout1.setVisibility(View.GONE);
        topLayout1.setVisibility(View.VISIBLE);



        postContest.setVisibility(View.VISIBLE);

        setgp(FirebaseAuth.getInstance().getCurrentUser().getUid(), gp);


        Intent intent = getIntent();
        if (intent.getStringExtra("entryfee").equals("")) {
            entryfee.setText("Free");
        } else {
            entryfee.setText(intent.getStringExtra("entryfee"));
        }
        if (intent.getStringExtra("total_prize").equals("")) {
            prizeLinear.setVisibility(View.GONE);
            totalprize.setText("-");
        } else {
            totalprize.setText(intent.getStringExtra("total_prize"));
            prizeLinear.setVisibility(View.VISIBLE);
        }
        if (intent.getStringExtra("maxLimit").equals("")) {
            maxPart.setText("Unlimited");
        } else {
            maxPart.setText(intent.getStringExtra("maxLimit"));
        }
        if (intent.getStringExtra("voteBegin").equals("")) {
            voteBegin.setText("-");
        } else {
            voteBegin.setText(intent.getStringExtra("voteBegin"));
        }
        if (intent.getStringExtra("voteEnd").equals("")) {
            voteEnd.setText("-");
        } else {
            voteEnd.setText(intent.getStringExtra("voteEnd"));
        }
        if (intent.getStringExtra("jname_1").equals("")) {
            jury.setVisibility(View.GONE);
            cardView.setVisibility(View.GONE);
        }
        if (!intent.getStringExtra("jname_1").equals("") && intent.getStringExtra("jname_2").equals("")) {
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
                            if (dataSnapshot.exists()){

                                db.child(getString(R.string.dbname_users))
                                        .child(dataSnapshot.getValue().toString())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                users user = snapshot.getValue(users.class);


                                                juryname1.setText(user.getDn());
                                                jurypl1.setText(user.getU());
                                                Log.d(TAG, "onDataChange: " + user.getDn());

                                                Glide.with(CheckContest.this)
                                                        .load(user.getPp())
                                                        .placeholder(R.drawable.load)
                                                        .error(R.drawable.default_image2)
                                                        .placeholder(R.drawable.load)
                                                        .thumbnail(0.25f)
                                                        .into(jurypic1);                                              }

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

        }
        if (!intent.getStringExtra("jname_1").equals("") && !intent.getStringExtra("jname_2").equals("") && intent.getStringExtra("jname_3").equals("")) {
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
                            if (dataSnapshot.exists()){

                                db.child(getString(R.string.dbname_users))
                                        .child(dataSnapshot.getValue().toString())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                users user = snapshot.getValue(users.class);


                                                juryname1.setText(user.getDn());
                                                jurypl1.setText(user.getU());

                                                Glide.with(CheckContest.this)
                                                        .load(user.getPp())
                                                        .placeholder(R.drawable.load)
                                                        .error(R.drawable.default_image2)
                                                        .placeholder(R.drawable.load)
                                                        .thumbnail(0.25f)
                                                        .into(jurypic1);                                                 }

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
            db.child(getString(R.string.dbname_username))
                    .child(intent.getStringExtra("jname_2"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){

                                db.child(getString(R.string.dbname_users))
                                        .child(dataSnapshot.getValue().toString())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                users user = snapshot.getValue(users.class);


                                                juryname2.setText(user.getDn());
                                                jurypl2.setText(user.getU());

                                                Glide.with(CheckContest.this)
                                                        .load(user.getPp())
                                                        .placeholder(R.drawable.load)
                                                        .error(R.drawable.default_image2)
                                                        .placeholder(R.drawable.load)
                                                        .thumbnail(0.25f)
                                                        .into(jurypic2);                                                 }

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

        }
        if (!intent.getStringExtra("jname_1").equals("") && !intent.getStringExtra("jname_2").equals("")
                && !intent.getStringExtra("jname_3").equals("")) {
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
                            if (dataSnapshot.exists()){

                                db.child(getString(R.string.dbname_users))
                                        .child(dataSnapshot.getValue().toString())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                users user = snapshot.getValue(users.class);


                                                juryname1.setText(user.getDn());
                                                jurypl1.setText(user.getU());
                                                Log.d(TAG, "onDataChange: " + user.getDn());

                                                Glide.with(CheckContest.this)
                                                        .load(user.getPp())
                                                        .placeholder(R.drawable.load)
                                                        .error(R.drawable.default_image2)
                                                        .placeholder(R.drawable.load)
                                                        .thumbnail(0.25f)
                                                        .into(jurypic1);                                                 }

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

            db.child(getString(R.string.dbname_username))
                    .child(intent.getStringExtra("jname_2"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){

                                db.child(getString(R.string.dbname_users))
                                        .child(dataSnapshot.getValue().toString())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                users user = snapshot.getValue(users.class);


                                                juryname2.setText(user.getDn());
                                                jurypl2.setText(user.getU());
                                                Log.d(TAG, "onDataChange: " + user.getDn());

                                                Glide.with(CheckContest.this)
                                                        .load(user.getPp())
                                                        .placeholder(R.drawable.load)
                                                        .error(R.drawable.default_image2)
                                                        .placeholder(R.drawable.load)
                                                        .thumbnail(0.25f)
                                                        .into(jurypic2);                                                 }

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

            db.child(getString(R.string.dbname_username))
                    .child(intent.getStringExtra("jname_3"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){

                                db.child(getString(R.string.dbname_users))
                                        .child(dataSnapshot.getValue().toString())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                users user = snapshot.getValue(users.class);


                                                juryname3.setText(user.getDn());
                                                jurypl3.setText(user.getU());
                                                Log.d(TAG, "onDataChange: " + user.getDn());

                                                Glide.with(CheckContest.this)
                                                        .load(user.getPp())
                                                        .placeholder(R.drawable.load)
                                                        .error(R.drawable.default_image2)
                                                        .placeholder(R.drawable.load)
                                                        .thumbnail(0.25f)
                                                        .into(jurypic3);                                                 }

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

        }

        title.setText(intent.getStringExtra("title"));
        descrip.setText(intent.getStringExtra("descrip"));
        rules.setText(intent.getStringExtra("rule"));
        voteType.setText(intent.getStringExtra("votetype"));
        regBegin.setText(intent.getStringExtra("regBegin"));
        regEnd.setText(intent.getStringExtra("regEnd"));
        domain.setText(intent.getStringExtra("domain"));
        openfor.setText(intent.getStringExtra("openfor"));
        hostedby.setText(intent.getStringExtra("host"));
        filetype.setText(intent.getStringExtra("filetype"));
        windate.setText(intent.getStringExtra("winDeclare"));
        p1Tv.setText(intent.getStringExtra("place_1"));
        p2Tv.setText(intent.getStringExtra("place_2"));
        p3Tv.setText(intent.getStringExtra("place_3"));
        posterlink = intent.getStringExtra("poster");

        Glide.with(CheckContest.this)
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
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                        Intent intent1=new Intent(CheckContest.this, contestMainActivity.class);
//                        startActivity(intent1);
                    })
                    .show();
        });

    }

    private void postcontest() {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        newContestKey = db.child(getString(R.string.dbname_contests)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();
        mCreateForm = new CreateForm();
        HashMap<String, Object> hashMap = new HashMap<>();
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


        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put(getString(R.string.field_entry_fee),entryfee.getText().toString());
        hashMap2.put(getString(R.string.field_vote_type), voteType.getText().toString());
        hashMap2.put(getString(R.string.field_timestamp), timeStamp);
        hashMap2.put(getString(R.string.field_domain), domain.getText().toString());
        hashMap2.put(getString(R.string.field_user_id), FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap2.put(getString(R.string.field_contest_ID), newContestKey);
        hashMap2.put(getString(R.string.field_registration_begin), regBegin.getText().toString());
        hashMap2.put(getString(R.string.field_registration_end), regEnd.getText().toString());
        hashMap2.put(getString(R.string.field_voting_begin), voteBegin.getText().toString());
        hashMap2.put(getString(R.string.field_voting_end), voteEnd.getText().toString());
        hashMap2.put(getString(R.string.field_winners_declare), windate.getText().toString());
        hashMap2.put(getString(R.string.field_result), false);
        hashMap2.put(getString(R.string.field_max_participant_limit), maxPart.getText().toString());

        db.child(getString(R.string.dbname_contests)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getString(R.string.created_contest)).child(newContestKey).setValue(hashMap);
        db.child(getString(R.string.dbname_request)).child(getString(R.string.dbname_contests)).child(newContestKey).setValue(hashMap2);

        Toast.makeText(CheckContest.this, "Form Filled", Toast.LENGTH_SHORT).show();

        mFirebaseMethods.uploadContest(imageCount, jpic1, null, newContestKey, p1, "");
        mFirebaseMethods.uploadContest(imageCount, jpic2, null, newContestKey, p2, "");
        mFirebaseMethods.uploadContest(imageCount, jpic3, null, newContestKey, p3, "");
        mFirebaseMethods.uploadContest(imageCount, posterlink, null, newContestKey, p4, "");
//        fragment_createContest fragment = new fragment_createContest();
//        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.container,fragment);
//        transaction.commit();
//        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }


    private void setupFirebaseAuth() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Log.d(TAG, "onAuthStateChanged: navigating to login");
                    SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                    new android.app.AlertDialog.Builder(getApplicationContext())
                            .setTitle("No user logon found")
                            .setMessage("We will be logging u out. \n Please try to log in again")
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                Intent intent = new Intent(getApplicationContext(), login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                settings.edit().clear().apply();
                                if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
                                startActivity(intent);
                            })
                            .show();
                }
            }
        };
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //retrieve user information from the database
                //retrieve image for the user in question
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                                            } else {
                                                gp.setText("100%");

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        } else {
                            gp.setText("100%");
                        }

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
