package com.orion.orion.contest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.contest.joined.JoiningForm;
import com.orion.orion.login.LoginActivity;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;


public class ViewContestDetails extends AppCompatActivity {

    private static final String TAG = "ViewContestDetails";

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
    private TextView userTv;
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
    private ImageView options;
    private String mAppend = "";
    private String posterlink = "";
    private CardView cardView;
    private Button participateBtn, VoteBtn;
    String username = "", currentUser = "", hostUsername = "";
    Boolean ok = false;
    ImageView backArrrow;
    private TextView jcTv, jcTv2;
    private String judgingCriterias = "";
    private CardView jcCard;
    CreateForm mCreateForm;
    int p = 0;


    private String juryusername1 = "", juryusername2 = "", juryusername3 = "";
    private LinearLayout prizeLinear;
    String userId, contestId, vot, reg;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_details);

        setupFirebaseAuth();
        contestType = findViewById(R.id.contestTypeTv);
        quizDateTime = findViewById(R.id.quizDateTime);
        publicVotingContainer = findViewById(R.id.publicVotingContainer);
        jcTv = findViewById(R.id.jc);
        jcTv2 = findViewById(R.id.jcTv2);
        jcCard = findViewById(R.id.jccard);
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
        prizeLinear = findViewById(R.id.prizell);
        participateBtn = findViewById(R.id.participateBtn);
        VoteBtn = findViewById(R.id.voteBtn);
        options = findViewById(R.id.optionC);
        gp = findViewById(R.id.gp);
        userTv = findViewById(R.id.usernameCreator);


        Intent i = getIntent();
        userId = i.getStringExtra("userId");
        contestId = i.getStringExtra("contestId");
        vot = i.getStringExtra("Vote");
        reg = i.getStringExtra("reg");

        setgp(userId, gp);
        backArrrow = findViewById(R.id.backarrow);

        backArrrow.setOnClickListener(view -> finish());
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_participantList));
        db.child(contestId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                p = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference ref8 = FirebaseDatabase.getInstance().getReference();
        ref8.child(getString(R.string.dbname_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_username))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currentUser = dataSnapshot.getValue().toString();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        ref8.child(getString(R.string.dbname_users))
                .child(userId)
                .child(getString(R.string.field_username))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        hostUsername = dataSnapshot.getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_contestlist));
        ref.child(contestId)
                .child(getString(R.string.field_Participant_List))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ok = true;

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        options.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(ViewContestDetails.this, options);
            popupMenu.getMenuInflater().inflate(R.menu.post_menu_contest, popupMenu.getMenu());
            if (!ok) {
                popupMenu.getMenu().getItem(2).setVisible(false);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.ic_house) {
                    int sdk = android.os.Build.VERSION.SDK_INT;
                    ClipboardManager clipboard = (ClipboardManager) ViewContestDetails.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        clipboard.setText(contestId);
                    } else {
                        android.content.ClipData clip = android.content.ClipData.newPlainText("Key", contestId);
                        clipboard.setPrimaryClip(clip);
                    }
                } else if (item.getItemId() == R.id.ic_house1) {
                    String message =
                            "https://play.google.com/store/apps/details?id=" + ViewContestDetails.this.getPackageName() +
                                    "Download ORION and share,participate in your domains contests."
                                    + "Enter Contest key " + contestId + " in Contest"
                                    + "Vote or Participate";
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, message);

                    ViewContestDetails.this.startActivity(Intent.createChooser(share, "Select"));
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewContestDetails.this);
                    builder.setTitle("Report");
                    builder.setMessage("Are you sure, you want to Report this Contest?");

//                set buttons
                    builder.setPositiveButton("Report", (dialog, which) -> {
                        Log.d(TAG, "DeleteMessage: deleteing message");
                        ReportPost(contestId, userId, p);

                    });
                    builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                    builder.create().show();
                }
                return true;
            });

            popupMenu.show();

        });

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_contests))
                .child(userId)
                .child(getString(R.string.created_contest))
                .child(contestId);
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCreateForm = dataSnapshot.getValue(CreateForm.class);
                assert mCreateForm != null;
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users));
                ref.child(mCreateForm.getUi())
                        .child(getString(R.string.field_username))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                userTv.setText(snapshot.getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                title.setText(mCreateForm.getCt());
                hostedby.setText(mCreateForm.getHst());
                descrip.setText(mCreateForm.getDes());
                rules.setText(mCreateForm.getRul());
                domain.setText(mCreateForm.getD());
                openfor.setText(mCreateForm.getOf());
                contestType.setText(mCreateForm.getCty());
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
                if (mCreateForm.getCty()!=null && mCreateForm.getCty().equals("Quiz"))
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


                juryusername1 = mCreateForm.getJn1();
                juryusername2 = mCreateForm.getJn2();
                juryusername3 = mCreateForm.getJn3();

                if (mCreateForm.getJn1().equals("")) {
                    jury.setVisibility(View.GONE);
                    cardView.setVisibility(View.GONE);
                }
                if (!mCreateForm.getJn1().equals("") && mCreateForm.getJn2().equals("")) {
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
                                                        Log.d(TAG, "onDataChange: " + user.getDn());

                                                        Glide.with(getApplicationContext())
                                                                .load(user.getPp())
                                                                .placeholder(R.drawable.load)
                                                                .error(R.drawable.default_image2)
                                                                .placeholder(R.drawable.load)
                                                                .thumbnail(0.5f)
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
                                                        Log.d(TAG, "onDataChange: " + user.getDn());

                                                        Glide.with(getApplicationContext())
                                                                .load(user.getPp())
                                                                .placeholder(R.drawable.load)
                                                                .error(R.drawable.default_image2)
                                                                .placeholder(R.drawable.load)
                                                                .thumbnail(0.5f)
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


                                                        Glide.with(getApplicationContext())
                                                                .load(user.getPp())
                                                                .placeholder(R.drawable.load)
                                                                .error(R.drawable.default_image2)
                                                                .placeholder(R.drawable.load)
                                                                .thumbnail(0.5f)
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
                                                        Log.d(TAG, "onDataChange: " + user.getDn());


                                                        Glide.with(getApplicationContext())
                                                                .load(user.getPp())
                                                                .placeholder(R.drawable.load)
                                                                .error(R.drawable.default_image2)
                                                                .placeholder(R.drawable.load)
                                                                .thumbnail(0.5f)
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


                                                        Glide.with(getApplicationContext())
                                                                .load(user.getPp())
                                                                .placeholder(R.drawable.load)
                                                                .error(R.drawable.default_image2)
                                                                .placeholder(R.drawable.load)
                                                                .thumbnail(0.5f)
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


                                                        Glide.with(getApplicationContext())
                                                                .load(user.getPp())
                                                                .placeholder(R.drawable.load)
                                                                .error(R.drawable.default_image2)
                                                                .placeholder(R.drawable.load)
                                                                .thumbnail(0.5f)
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
                posterlink = mCreateForm.getPo();

                if (mCreateForm.getVt().equals("Jury") || mCreateForm.getVt().equals("Jury and Public")) {
                    String f_string = "";
                    jcCard.setVisibility(View.VISIBLE);
                    jcTv.setVisibility(View.VISIBLE);
                    judgingCriterias = mCreateForm.getCr();
                    String[] array = judgingCriterias.split("///");
                    for (String a : array)
                        f_string = f_string + "\n" + a;
                    Log.d(TAG, "onCreate: " + f_string);
                    jcTv2.setText(f_string);
                }
                Glide.with(getApplicationContext())
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
        userTv.setOnClickListener(v -> juryProfile(userTv.getText().toString()));
        jurypl1.setOnClickListener(v -> juryProfile(jurypl1.getText().toString()));

        jurypic2.setOnClickListener(v -> juryProfile(jurypl2.getText().toString()));
        juryname2.setOnClickListener(v -> juryProfile(jurypl2.getText().toString()));
        jurypl2.setOnClickListener(v -> juryProfile(jurypl2.getText().toString()));

        jurypic3.setOnClickListener(v -> juryProfile(jurypl3.getText().toString()));
        juryname3.setOnClickListener(v -> juryProfile(jurypl3.getText().toString()));
        jurypl3.setOnClickListener(v -> juryProfile(jurypl3.getText().toString()));

        if (reg.equals("yes")) {
            participateBtn.setVisibility(View.VISIBLE);
            participateBtn.setOnClickListener(v -> {
                if (currentUser.equals(juryusername1) || currentUser.equals(juryusername2)
                        || currentUser.equals(juryusername3) || currentUser.equals(hostUsername)) {
                    Intent i1 = new Intent(getApplicationContext(), JoiningForm.class);
                    i1.putExtra("userId", userId);
                    i1.putExtra("contestId", contestId);
                    i1.putExtra("isJuryOrHost", "true");
                    startActivity(i1);
                } else {
                    Intent i1 = new Intent(getApplicationContext(), JoiningForm.class);
                    i1.putExtra("userId", userId);
                    i1.putExtra("contestId", contestId);
                    i1.putExtra("isJuryOrHost", "false");
                    startActivity(i1);
                }
            });
        } else participateBtn.setVisibility(View.GONE);

        if (vot.equals("yes")) {
            VoteBtn.setVisibility(View.VISIBLE);
            VoteBtn.setOnClickListener(v -> {
                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
                ref1.child(getString(R.string.dbname_users))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                users user = new users();
                                user = dataSnapshot.getValue(users.class);
                                String username = user.getU();
                                if (username.equals(juryusername1)) {
                                    Intent i12 = new Intent(ViewContestDetails.this, jury_voting_Activity.class);
                                    i12.putExtra("userId", userId);
                                    i12.putExtra("contestId", contestId);
                                    i12.putExtra("jury", "jury1");
                                    i12.putExtra("comment", "comment1");
                                    i12.putExtra("mediaType", mCreateForm.getMlt());
                                    startActivity(i12);
                                } else if (username.equals(juryusername2)) {
                                    Intent i12 = new Intent(ViewContestDetails.this, jury_voting_Activity.class);
                                    i12.putExtra("userId", userId);
                                    i12.putExtra("contestId", contestId);
                                    i12.putExtra("jury", "jury2");
                                    i12.putExtra("comment", "comment2");
                                    i12.putExtra("mediaType", mCreateForm.getMlt());
                                    startActivity(i12);
                                } else if (username.equals(juryusername3)) {
                                    Intent i12 = new Intent(ViewContestDetails.this, jury_voting_Activity.class);
                                    i12.putExtra("userId", userId);
                                    i12.putExtra("contestId", contestId);
                                    i12.putExtra("jury", "jury3");
                                    i12.putExtra("comment", "comment3");
                                    i12.putExtra("mediaType", mCreateForm.getMlt());
                                    startActivity(i12);
                                } else {
                                    Intent i12 = new Intent(ViewContestDetails.this, public_voting_media.class);
                                    i12.putExtra("userId", userId);
                                    i12.putExtra("contestId", contestId);
                                    startActivity(i12);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            });

        } else VoteBtn.setVisibility(View.GONE);
    }

    private void juryProfile(String toString) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query userquery = ref
                .child(getString(R.string.dbname_username))
                .child(toString);
        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username = dataSnapshot.getValue().toString();
                    Intent i = new Intent(ViewContestDetails.this, profile.class);
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

    private void ReportPost(String contestId, String userId, int p) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_contestlist))
                .child(contestId)
                .child(getString(R.string.field_contest_report_list));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Toast.makeText(ViewContestDetails.this, "You already reported this contest.", Toast.LENGTH_SHORT).show();

                } else {


                    reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(true)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            long i = snapshot.getChildrenCount();
                                            if ((((i + 1) / p) * 100) > 60) {
                                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_contests))
                                                        .child(userId)
                                                        .child(getString(R.string.field_contest_reports));
                                                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            long x = (long) snapshot.getValue();
                                                            reference1
                                                                    .setValue(x + 1);
                                                        } else {
                                                            reference1
                                                                    .setValue(1);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }
                            });

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
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
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                settings.edit().clear().apply();
                                if (mAuthListener != null)
                                    mAuth.removeAuthStateListener(mAuthListener);
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
