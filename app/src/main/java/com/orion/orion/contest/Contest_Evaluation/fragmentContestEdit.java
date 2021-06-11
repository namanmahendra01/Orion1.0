package com.orion.orion.contest.Contest_Evaluation;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;

import static com.android.volley.VolleyLog.TAG;

public class fragmentContestEdit extends Fragment {
    public fragmentContestEdit() {
    }

    private TextView contestType;
    private TextView quizDateTime;
    private TableLayout publicVotingContainer;
    private TextView entryfee;
    private TextView title;
    private TextView totalprize;
    private TextView maxPart;
    private TextView voteType;
    private TextView regBegin;
    private TextView regEnd;
    private TextView voteBegin;
    private TextView voteEnd;
    private TextView domain, userTv;
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
    private CardView cardView;
    private EditText descrip;
    private EditText rules;
    private LinearLayout prizeLinear;

    private String mAppend = "";
    private String posterlink = "";
    private String descrip2 = "";
    private String rule2 = "";
    private String username = "";

    private TextView jcTv, jcTv2;
    private String judgingCriterias = "";
    private CardView jcCard;

    private String userid;
    private String Contestkey;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_contest_details, container, false);
        setupFirebaseAuth();

        contestType = view.findViewById(R.id.contestTypeTv);
        quizDateTime = view.findViewById(R.id.quizDateTime);
        publicVotingContainer = view.findViewById(R.id.publicVotingContainer);
        entryfee = view.findViewById(R.id.entryfeeTv);
        title = view.findViewById(R.id.titleTv);
        descrip = view.findViewById(R.id.descripEv);
        rules = view.findViewById(R.id.ruleEv);
        TextView descripTv = view.findViewById(R.id.descripTv);
        TextView rulesTv = view.findViewById(R.id.ruleTv);
        totalprize = view.findViewById(R.id.totalprizeTv);
        maxPart = view.findViewById(R.id.maxPartTv);
        voteType = view.findViewById(R.id.voteTypeTv);
        regBegin = view.findViewById(R.id.regB);
        regEnd = view.findViewById(R.id.regE);
        voteBegin = view.findViewById(R.id.votB);
        voteEnd = view.findViewById(R.id.votE);
        domain = view.findViewById(R.id.domainTV);
        openfor = view.findViewById(R.id.openForTv);
        juryname1 = view.findViewById(R.id.jname1Tv);
        juryname2 = view.findViewById(R.id.jname2Tv);
        juryname3 = view.findViewById(R.id.jname3Tv);
        jurypl1 = view.findViewById(R.id.jpl1Tv);
        jurypl2 = view.findViewById(R.id.jpl2Tv);
        jurypl3 = view.findViewById(R.id.jpl3Tv);
        jurypic1 = view.findViewById(R.id.jpic1);
        jurypic2 = view.findViewById(R.id.jpic2);
        jurypic3 = view.findViewById(R.id.jpic3);
        hostedby = view.findViewById(R.id.hostedbyTv);
        filetype = view.findViewById(R.id.fileTv);
        windate = view.findViewById(R.id.winDate);
        poster = view.findViewById(R.id.posterIv);
        jury = view.findViewById(R.id.jury);
        cardView = view.findViewById(R.id.jurydetail);
        p1Tv = view.findViewById(R.id.p1Tv);
        p2Tv = view.findViewById(R.id.p2Tv);
        p3Tv = view.findViewById(R.id.p3Tv);
        userTv = view.findViewById(R.id.usernameCreator);
        jcTv = view.findViewById(R.id.jc);
        jcTv2 = view.findViewById(R.id.jcTv2);
        jcCard = view.findViewById(R.id.jccard);
        prizeLinear = view.findViewById(R.id.prizell);
        Button saveBtn = view.findViewById(R.id.save);
        TextView gp = view.findViewById(R.id.gp);
        TextView notice = view.findViewById(R.id.text);
        RelativeLayout topLayout = view.findViewById(R.id.reLayout1);
        topLayout.setVisibility(View.GONE);
        saveBtn.setVisibility(View.VISIBLE);
        notice.setVisibility(View.VISIBLE);
        descrip.setVisibility(View.VISIBLE);
        rules.setVisibility(View.VISIBLE);
        descripTv.setVisibility(View.GONE);
        rulesTv.setVisibility(View.GONE);

        Bundle b1 = getActivity().getIntent().getExtras();
        Contestkey = b1.getString("contestId");
        userid = b1.getString("userid");
        setgp(userid, gp);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_contests)).child(userid).child(getString(R.string.created_contest)).child(Contestkey);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CreateForm mCreateForm = dataSnapshot.getValue(CreateForm.class);

                if (mCreateForm.getCty().equals("")) {

                } else {

                }
                title.setText(mCreateForm.getCt());
                hostedby.setText(mCreateForm.getHst());
                descrip.setText(mCreateForm.getDes());
                rules.setText(mCreateForm.getRul());
                if (mCreateForm.getEf().equals(""))
                    entryfee.setText("Free");
                else
                    entryfee.setText(mCreateForm.getEf());
                if (mCreateForm.getTp().equals("")) {
                    prizeLinear.setVisibility(View.GONE);
                    totalprize.setText("-");
                } else {
                    totalprize.setText(mCreateForm.getTp());
                    prizeLinear.setVisibility(View.VISIBLE);
                }
//                contestType
                contestType.setText(mCreateForm.getCty());
                if (mCreateForm.getCty().equals("Quiz"))
                    publicVotingContainer.setVisibility(View.GONE);
                else
                    publicVotingContainer.setVisibility(View.VISIBLE);
                //voting type
                if (mCreateForm.getVt().equals(""))
                    voteType.setText("-");
                else
                    voteType.setText(mCreateForm.getVt());
                domain.setText(mCreateForm.getD());
                openfor.setText(mCreateForm.getOf());
                //participant
                if (mCreateForm.getMlt().equals(""))
                    maxPart.setText("Unlimited");
                else
                    maxPart.setText(mCreateForm.getMlt());
                filetype.setText(mCreateForm.getFt());
                regBegin.setText(mCreateForm.getRb());
                regEnd.setText(mCreateForm.getRe());
                //vote dates
                if (mCreateForm.getVb().equals("") || mCreateForm.getVe().equals("")) {
                    voteBegin.setText("-");
                    voteEnd.setText("-");
                } else {
                    voteBegin.setText(mCreateForm.getVb());
                    voteEnd.setText(mCreateForm.getVe());
                }
                windate.setText(mCreateForm.getWd());
                quizDateTime.setText(mCreateForm.getQdt());

                p1Tv.setText(mCreateForm.getP1());
                p2Tv.setText(mCreateForm.getP2());
                p3Tv.setText(mCreateForm.getP3());


                if (mCreateForm.getJn1().equals("")) {
                    jury.setVisibility(View.GONE);
                    cardView.setVisibility(View.GONE);
                }
                if (!mCreateForm.getJn1().equals("")
                        && mCreateForm.getJn2().equals("")) {
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
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    db.child(getString(R.string.dbname_username))
                            .child(mCreateForm.getJn1())
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

                                                        Glide.with(fragmentContestEdit.this)
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


                }
                if (!mCreateForm.getJn1().equals("")
                        && !mCreateForm.getJn2().equals("")
                        && mCreateForm.getJn3().equals("")) {
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
                            .child(mCreateForm.getJn1())
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

                                                        Glide.with(getActivity().getApplicationContext())
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

                    db.child(getString(R.string.dbname_username))
                            .child(mCreateForm.getJn2())
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


                                                        juryname2.setText(user.getDn());
                                                        jurypl2.setText(user.getU());
                                                        Log.d(TAG, "onDataChange: " + user.getDn());

                                                        Glide.with(getActivity().getApplicationContext())
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

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
                if (!mCreateForm.getJn1().equals("")
                        && !mCreateForm.getJn2().equals("")
                        && !mCreateForm.getJn3().equals("")) {
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
                            .child(mCreateForm.getJn1())
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

                                                        Glide.with(getActivity().getApplicationContext())
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
                    db.child(getString(R.string.dbname_username))
                            .child(mCreateForm.getJn2())
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


                                                        juryname2.setText(user.getDn());
                                                        jurypl2.setText(user.getU());

                                                        Glide.with(getActivity().getApplicationContext())
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

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                    db.child(getString(R.string.dbname_username))
                            .child(mCreateForm.getJn3())
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


                                                        juryname3.setText(user.getDn());
                                                        jurypl3.setText(user.getU());
                                                        Log.d(TAG, "onDataChange: " + user.getDn());

                                                        Glide.with(getActivity().getApplicationContext())
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

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
                if (mCreateForm.getVt().equals("Jury") || mCreateForm.getVt().equals("Jury and Public")) {
                    String f_string = "";
                    jcCard.setVisibility(View.VISIBLE);
                    jcTv.setVisibility(View.VISIBLE);
                    judgingCriterias = mCreateForm.getCr();
                    String[] array = judgingCriterias.split("///");
                    for (String a :
                            array) {
                        f_string = f_string + "\n" + a;

                    }
                    Log.d(TAG, "onCreate: " + f_string);
                    jcTv2.setText(f_string);


                }
                posterlink = mCreateForm.getPo();
                Glide.with(getActivity().getApplicationContext())
                        .load(posterlink)
                        .placeholder(R.drawable.load)
                        .error(R.drawable.default_image2)
                        .placeholder(R.drawable.load)
                        .into(poster);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        jurypic1.setOnClickListener(v -> juryProfile(jurypl1.getText().toString()));
        juryname1.setOnClickListener(v -> juryProfile(jurypl1.getText().toString()));
        jurypl1.setOnClickListener(v -> juryProfile(jurypl1.getText().toString()));
        jurypic2.setOnClickListener(v -> juryProfile(jurypl2.getText().toString()));
        juryname2.setOnClickListener(v -> juryProfile(jurypl2.getText().toString()));
        jurypl2.setOnClickListener(v -> juryProfile(jurypl2.getText().toString()));
        jurypic3.setOnClickListener(v -> juryProfile(jurypl3.getText().toString()));
        juryname3.setOnClickListener(v -> juryProfile(jurypl3.getText().toString()));
        jurypl3.setOnClickListener(v -> juryProfile(jurypl3.getText().toString()));
        descrip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                descrip2 = descrip.getText().toString();
                Log.d(TAG, "afterTextChanged: " + descrip2);
            }
        });
        rules.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                rule2 = rules.getText().toString();
                Log.d(TAG, "afterTextChanged: " + descrip2);
            }
        });
        DatabaseReference ref8 = FirebaseDatabase.getInstance().getReference();
        ref8.child(getString(R.string.dbname_users))
                .child(userid)
                .child(getString(R.string.field_username))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.getValue().toString();
                        userTv.setText(username);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        userTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                juryProfile(userTv.getText().toString());

            }
        });

        saveBtn.setOnClickListener(v -> {
            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_contests));
            ref1.child(userid)
                    .child(getString(R.string.created_contest))
                    .child(Contestkey)
                    .child(getString(R.string.field_description))
                    .setValue(descrip2).addOnSuccessListener(aVoid -> {
                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_contests));
                ref2.child(userid)
                        .child(getString(R.string.created_contest))
                        .child(Contestkey)
                        .child(getString(R.string.field_rule))
                        .setValue(rule2).addOnSuccessListener(aVoid1 -> Toast.makeText(getContext(), "Your changes are saved!", Toast.LENGTH_SHORT).show());
            });
        });
        return view;
    }

    private void juryProfile(String toString) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query userquery = ref.child(getString(R.string.dbname_username)).child(toString);
        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.getValue().toString();
                    Intent i = new Intent(getContext(), profile.class);
                    i.putExtra(getString(R.string.calling_activity), getString(R.string.home));
                    i.putExtra(getString(R.string.intent_user), username);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Query Cancelled");
            }
        });
    }

    private void setupFirebaseAuth() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) Log.d(TAG, "onAuthStateChanged:signed_out");
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
        if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
    }

}
