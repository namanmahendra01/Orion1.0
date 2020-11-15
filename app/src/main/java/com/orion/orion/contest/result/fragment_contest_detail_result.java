package com.orion.orion.contest.result;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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

public class fragment_contest_detail_result extends Fragment {
    public fragment_contest_detail_result(){}


    private TextView entryfee,title,totalprize,maxPart,voteType,gp,
            regBegin,regEnd,voteBegin,voteEnd,domain,openfor,juryname1,juryname2,juryname3,jury
            ,jurypl1,jurypl2,jurypl3,hostedby,filetype,windate,p1Tv,p2Tv,p3Tv,descrip,rules;;
    private ImageView poster,jurypic1,jurypic2,jurypic3;
    private String mAppend = "",username="";
    private String posterlink="";
    private CardView cardView;
    RelativeLayout topLayout;

    private LinearLayout prizeLinear;
    String userid,Contestkey;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_contest_details, container, false);


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
        gp = view.findViewById(R.id.gp);

        topLayout = view.findViewById(R.id.reLayout1);

        topLayout.setVisibility(View.GONE);




        Bundle b1=getActivity().getIntent().getExtras();
        Contestkey=b1.getString("contestId");
        userid=b1.getString("userId");

        setgp(userid, gp);

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_contests))
                .child(userid)
                .child(getString(R.string.created_contest))
                .child(Contestkey);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CreateForm mCreateForm=dataSnapshot.getValue(CreateForm.class);
                if (mCreateForm.getEf().equals("")){
                    entryfee.setText("Free");
                }else{
                    entryfee.setText(mCreateForm.getEf());
                }
                if (mCreateForm.getTp().equals("")){
                    prizeLinear.setVisibility(View.GONE);
                    totalprize.setText("-");

                }else{
                    totalprize.setText(mCreateForm.getTp());
                    prizeLinear.setVisibility(View.VISIBLE);

                }
                if (mCreateForm.getMLt().equals("")){
                    maxPart.setText("Unlimited");

                }else{
                    maxPart.setText(mCreateForm.getMLt());

                }
                if (mCreateForm.getVb().equals("")){
                    voteBegin.setText("-");

                }else{
                    voteBegin.setText(mCreateForm.getVb());

                }
                if (mCreateForm.getVe().equals("")){
                    voteEnd.setText("-");

                }else{
                    voteEnd.setText(mCreateForm.getVe());

                }
                if (mCreateForm.getJn1().equals("") ){
                    jury.setVisibility(View.GONE);
                    cardView.setVisibility(View.GONE);
                }
                if (!mCreateForm.getJn1().equals("") &&  mCreateForm.getJn2().equals("")){
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

                                                        Glide.with(fragment_contest_detail_result.this)
                                                                .load(user.getPp())
                                                                .placeholder(R.drawable.load)
                                                                .error(R.drawable.default_image2)
                                                                .placeholder(R.drawable.load)
                                                                .thumbnail(0.25f)
                                                                .into(jurypic1);                                                     }

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
                if(!mCreateForm.getJn1().equals("") &&  !mCreateForm.getJn2().equals("")
                        && mCreateForm.getJn3().equals("")){
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

                                                        Glide.with(fragment_contest_detail_result.this)
                                                                .load(user.getPp())
                                                                .placeholder(R.drawable.load)
                                                                .error(R.drawable.default_image2)
                                                                .placeholder(R.drawable.load)
                                                                .thumbnail(0.25f)
                                                                .into(jurypic1);                                                    }

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

                                                        Glide.with(fragment_contest_detail_result.this)
                                                                .load(user.getPp())
                                                                .placeholder(R.drawable.load)
                                                                .error(R.drawable.default_image2)
                                                                .placeholder(R.drawable.load)
                                                                .thumbnail(0.25f)
                                                                .into(jurypic2);                                                    }

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
                if(!mCreateForm.getJn1().equals("") &&  !mCreateForm.getJn2().equals("")
                        && !mCreateForm.getJn3().equals("")){
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

                                                        Glide.with(fragment_contest_detail_result.this)
                                                                .load(user.getPp())
                                                                .placeholder(R.drawable.load)
                                                                .error(R.drawable.default_image2)
                                                                .placeholder(R.drawable.load)
                                                                .thumbnail(0.25f)
                                                                .into(jurypic1);                                                    }

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

                                                        Glide.with(fragment_contest_detail_result.this)
                                                                .load(user.getPp())
                                                                .placeholder(R.drawable.load)
                                                                .error(R.drawable.default_image2)
                                                                .placeholder(R.drawable.load)
                                                                .thumbnail(0.25f)
                                                                .into(jurypic2);                                                    }

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

                                                        Glide.with(fragment_contest_detail_result.this)
                                                                .load(user.getPp())
                                                                .placeholder(R.drawable.load)
                                                                .error(R.drawable.default_image2)
                                                                .placeholder(R.drawable.load)
                                                                .thumbnail(0.25f)
                                                                .into(jurypic3);                                                    }

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

                posterlink=mCreateForm.getPo();

                Glide.with(fragment_contest_detail_result.this)
                        .load(posterlink)
                        .placeholder(R.drawable.load)
                        .error(R.drawable.default_image2)
                        .placeholder(R.drawable.load)
                        .into(poster);
                title.setText(mCreateForm.getCt());
                descrip.setText(mCreateForm.getDes());
                rules.setText(mCreateForm.getRul());
                voteType.setText(mCreateForm.getVt());
                regBegin.setText(mCreateForm.getRb());
                regEnd.setText(mCreateForm.getRe());
                domain.setText(mCreateForm.getD());
                openfor.setText(mCreateForm.getOf());


                hostedby.setText(mCreateForm.getHst());
                filetype.setText(mCreateForm.getFt());
                windate.setText(mCreateForm.getWd());
                p1Tv.setText(mCreateForm.getP1());

                p2Tv.setText(mCreateForm.getP2());

                p3Tv.setText(mCreateForm.getP3());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        jurypic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                juryProfile(jurypl1.getText().toString());


            }
        });
        juryname1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                juryProfile(jurypl1.getText().toString());


            }
        });
        jurypl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                juryProfile(jurypl1.getText().toString());



            }
        });

        jurypic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                juryProfile(jurypl2.getText().toString());



            }
        });
        juryname2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                juryProfile(jurypl2.getText().toString());



            }
        });
        jurypl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                juryProfile(jurypl2.getText().toString());



            }
        });

        jurypic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                juryProfile(jurypl3.getText().toString());



            }
        });
        juryname3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                juryProfile(jurypl3.getText().toString());


            }
        });
        jurypl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                juryProfile(jurypl3.getText().toString());



            }
        });



        return view;
    }

    private void juryProfile(String toString) {
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference();

        Query userquery = ref
                .child(getString(R.string.dbname_username))
                .child(toString);
        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
if (dataSnapshot.exists()){
                    username = dataSnapshot.getValue().toString();

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


}
