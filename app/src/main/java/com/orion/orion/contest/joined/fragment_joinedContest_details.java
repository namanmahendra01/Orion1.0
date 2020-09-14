package com.orion.orion.contest.joined;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orion.orion.R;
import com.orion.orion.contest.ViewContestDetails;
import com.orion.orion.contest.joined.JoiningForm;
import com.orion.orion.models.CreateForm;

import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.UniversalImageLoader;

import static com.android.volley.VolleyLog.TAG;

public class fragment_joinedContest_details extends Fragment {
    public fragment_joinedContest_details(){}


    private TextView entryfee,title,totalprize,maxPart,voteType,
            regBegin,regEnd,voteBegin,voteEnd,domain,openfor,juryname1,juryname2,juryname3,jury
            ,jurypl1,jurypl2,jurypl3,hostedby,filetype,windate,p1Tv,p2Tv,p3Tv,description,rules2,descrip,rules;
    private ImageView poster,jurypic1,jurypic2,jurypic3;
    private String mAppend = "";
    private String jpic1="",jpic2="",jpic3="",posterlink="";
    private CardView cardView;
    users user = new users();

    private LinearLayout prizeLinear;
    String userid,Contestkey;

    //firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;
    private  FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contest_detail_result, container, false);



        setupFirebaseAuth();

        entryfee=view.findViewById(R.id.entryfeeTv);
        title=view.findViewById(R.id.titleTv);
        descrip=view.findViewById(R.id.descripTv);
        rules=view.findViewById(R.id.ruleTv);
        totalprize=view.findViewById(R.id.totalprizeTv);
        maxPart=view.findViewById(R.id.maxPartTv);
        voteType=view.findViewById(R.id.voteTypeTv);
        regBegin=view.findViewById(R.id.regB);
        regEnd=view.findViewById(R.id.regE);
        voteBegin=view.findViewById(R.id.votB);
        voteEnd=view.findViewById(R.id.votE);
        domain=view.findViewById(R.id.domainTV);
        openfor=view.findViewById(R.id.openForTv);
        juryname1=view.findViewById(R.id.jname1Tv);
        juryname2=view.findViewById(R.id.jname2Tv);
        juryname3=view.findViewById(R.id.jname3Tv);
        jurypl1=view.findViewById(R.id.jpl1Tv);
        jurypl2=view.findViewById(R.id.jpl2Tv);
        jurypl3=view.findViewById(R.id.jpl3Tv);
        jurypic1=view.findViewById(R.id.jpic1);
        jurypic2=view.findViewById(R.id.jpic2);
        jurypic3=view.findViewById(R.id.jpic3);
        hostedby=view.findViewById(R.id.hostedbyTv);
        filetype=view.findViewById(R.id.fileTv);
        windate=view.findViewById(R.id.winDate);
        poster=view.findViewById(R.id.posterIv);
        jury=view.findViewById(R.id.jury);
        cardView=view.findViewById(R.id.jurydetail);
        p1Tv=view.findViewById(R.id.p1Tv);
        p2Tv=view.findViewById(R.id.p2Tv);
        p3Tv=view.findViewById(R.id.p3Tv);
        prizeLinear=view.findViewById(R.id.prizell);










        Bundle b1=getActivity().getIntent().getExtras();
        Contestkey=b1.getString("contestId");
        userid=b1.getString("userId");
        Log.d(TAG, "onCreateView: asd0"+userid+"AND"+Contestkey);

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_contests))
                .child(userid)
                .child(getString(R.string.created_contest))
                .child(Contestkey);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CreateForm mCreateForm=dataSnapshot.getValue(CreateForm.class);
                if (mCreateForm.getEntryfee().equals("")){
                    entryfee.setText("Free");
                }else{
                    entryfee.setText(mCreateForm.getEntryfee());
                }
                if (mCreateForm.getTotal_prize().equals("")){
                    prizeLinear.setVisibility(View.GONE);
                    totalprize.setText("-");

                }else{
                    totalprize.setText(mCreateForm.getTotal_prize());
                    prizeLinear.setVisibility(View.VISIBLE);

                }
                if (mCreateForm.getMaxLimit().equals("")){
                    maxPart.setText("Unlimited");

                }else{
                    maxPart.setText(mCreateForm.getMaxLimit());

                }
                if (mCreateForm.getVoteBegin().equals("")){
                    voteBegin.setText("-");

                }else{
                    voteBegin.setText(mCreateForm.getVoteBegin());

                }
                if (mCreateForm.getVoteEnd().equals("")){
                    voteEnd.setText("-");

                }else{
                    voteEnd.setText(mCreateForm.getVoteEnd());

                }
                if (mCreateForm.getJname_1().equals("") ){
                    jury.setVisibility(View.GONE);
                    cardView.setVisibility(View.GONE);
                }
                if (!mCreateForm.getJname_1().equals("") &&  mCreateForm.getJname_2().equals("")){
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
                    db.child(getString(R.string.dbname_user_account_settings))
                            .orderByChild(getString(R.string.field_username))
                            .equalTo(mCreateForm.getJname_1())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        users user = snapshot.getValue(users.class);


                                        juryname1.setText(user.getDisplay_name());
                                        jurypl1.setText(user.getUsername());
                                        Log.d(TAG, "onDataChange: " + user.getDisplay_name());

                                        UniversalImageLoader.setImage(user.getProfile_photo(), jurypic1, null, "");

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });



                }
                if(!mCreateForm.getJname_1().equals("") &&  !mCreateForm.getJname_2().equals("")
                        && mCreateForm.getJname_3().equals("")){
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
                    db.child(getString(R.string.dbname_user_account_settings))
                            .orderByChild(getString(R.string.field_username))
                            .equalTo(mCreateForm.getJname_1())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        users user = snapshot.getValue(users.class);


                                        juryname1.setText(user.getDisplay_name());
                                        jurypl1.setText(user.getUsername());
                                        UniversalImageLoader.setImage(user.getProfile_photo(), jurypic1, null, "");


                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                    DatabaseReference db2 = FirebaseDatabase.getInstance().getReference();
                    db2.child(getString(R.string.dbname_user_account_settings))
                            .orderByChild(getString(R.string.field_username))
                            .equalTo(mCreateForm.getJname_2())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        users user = snapshot.getValue(users.class);


                                        juryname2.setText(user.getDisplay_name());
                                        jurypl2.setText(user.getUsername());
                                        UniversalImageLoader.setImage(user.getProfile_photo(), jurypic2, null, "");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
                if(!mCreateForm.getJname_1().equals("") &&  !mCreateForm.getJname_2().equals("")
                        && !mCreateForm.getJname_3().equals("")){
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
                    db.child(getString(R.string.dbname_user_account_settings))
                            .orderByChild(getString(R.string.field_username))
                            .equalTo(mCreateForm.getJname_1())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        users user = snapshot.getValue(users.class);


                                        juryname1.setText(user.getDisplay_name());
                                        jurypl1.setText(user.getUsername());
                                        UniversalImageLoader.setImage(user.getProfile_photo(), jurypic1, null, "");

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                    DatabaseReference db2 = FirebaseDatabase.getInstance().getReference();
                    db2.child(getString(R.string.dbname_user_account_settings))
                            .orderByChild(getString(R.string.field_username))
                            .equalTo(mCreateForm.getJname_2())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        users user = snapshot.getValue(users.class);



                                        juryname2.setText(user.getDisplay_name());
                                        jurypl2.setText(user.getUsername());
                                        UniversalImageLoader.setImage(user.getProfile_photo(),jurypic2,null,"");

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                    DatabaseReference db3 = FirebaseDatabase.getInstance().getReference();
                    db3.child(getString(R.string.dbname_user_account_settings))
                            .orderByChild(getString(R.string.field_username))
                            .equalTo(mCreateForm.getJname_3())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        users user = snapshot.getValue(users.class);


                                        juryname3.setText(user.getDisplay_name());
                                        jurypl3.setText(user.getUsername());
                                        UniversalImageLoader.setImage(user.getProfile_photo(), jurypic3, null, "");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
                posterlink=mCreateForm.getPoster();

                UniversalImageLoader.setImage(posterlink,poster,null,mAppend);

                title.setText(mCreateForm.getTitle());
                descrip.setText(mCreateForm.getDescrip());
                rules.setText(mCreateForm.getRule());
                voteType.setText(mCreateForm.getVotetype());
                regBegin.setText(mCreateForm.getRegBegin());
                regEnd.setText(mCreateForm.getRegEnd());
                domain.setText(mCreateForm.getDomain());
                openfor.setText(mCreateForm.getOpenFor());

                hostedby.setText(mCreateForm.getHost());
                filetype.setText(mCreateForm.getFiletype());
                windate.setText(mCreateForm.getWinDeclare());
                p1Tv.setText(mCreateForm.getPlace_1());

                p2Tv.setText(mCreateForm.getPlace_2());

                p3Tv.setText(mCreateForm.getPlace_3());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        jurypic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref =FirebaseDatabase.getInstance().getReference();

                Query userquery = ref
                        .child(getString(R.string.dbname_users))
                        .orderByChild(getString(R.string.field_username))
                        .equalTo(jurypl1.getText().toString());
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
        juryname1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref =FirebaseDatabase.getInstance().getReference();

                Query userquery = ref
                        .child(getString(R.string.dbname_users))
                        .orderByChild(getString(R.string.field_username))
                        .equalTo(jurypl1.getText().toString());
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
        jurypl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref =FirebaseDatabase.getInstance().getReference();

                Query userquery = ref
                        .child(getString(R.string.dbname_users))
                        .orderByChild(getString(R.string.field_username))
                        .equalTo(jurypl1.getText().toString());
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

        jurypic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref =FirebaseDatabase.getInstance().getReference();

                Query userquery = ref
                        .child(getString(R.string.dbname_users))
                        .orderByChild(getString(R.string.field_username))
                        .equalTo(jurypl2.getText().toString());
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
        juryname2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref =FirebaseDatabase.getInstance().getReference();

                Query userquery = ref
                        .child(getString(R.string.dbname_users))
                        .orderByChild(getString(R.string.field_username))
                        .equalTo(jurypl2.getText().toString());
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
        jurypl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref =FirebaseDatabase.getInstance().getReference();

                Query userquery = ref
                        .child(getString(R.string.dbname_users))
                        .orderByChild(getString(R.string.field_username))
                        .equalTo(jurypl2.getText().toString());
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

        jurypic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref =FirebaseDatabase.getInstance().getReference();

                Query userquery = ref
                        .child(getString(R.string.dbname_users))
                        .orderByChild(getString(R.string.field_username))
                        .equalTo(jurypl3.getText().toString());
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
        juryname3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref =FirebaseDatabase.getInstance().getReference();

                Query userquery = ref
                        .child(getString(R.string.dbname_users))
                        .orderByChild(getString(R.string.field_username))
                        .equalTo(jurypl3.getText().toString());
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
        jurypl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref =FirebaseDatabase.getInstance().getReference();

                Query userquery = ref
                        .child(getString(R.string.dbname_users))
                        .orderByChild(getString(R.string.field_username))
                        .equalTo(jurypl3.getText().toString());
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


        return view;
    }


    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        myRef=mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
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
