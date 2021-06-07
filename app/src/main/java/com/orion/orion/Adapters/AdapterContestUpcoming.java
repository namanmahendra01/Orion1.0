package com.orion.orion.Adapters;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.contest.ViewContestDetails;
import com.orion.orion.contest.joined.JoiningForm;
import com.orion.orion.contest.public_voting_media;
import com.orion.orion.contest.result.ResultDeclaredActivity;
import com.orion.orion.contest.jury_voting_Activity;
import com.orion.orion.models.ContestDetail;
import com.orion.orion.models.CreateForm;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.StringManipilation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.android.volley.VolleyLog.TAG;

public class AdapterContestUpcoming extends RecyclerView.Adapter<AdapterContestUpcoming.ViewHolder> {


    String timestamp = "";


    private Context mContext;
    private List<ContestDetail> mContestDetail;

    public AdapterContestUpcoming(Context mContext, List<ContestDetail> mContestDetail) {
        this.mContext = mContext;
        this.mContestDetail = mContestDetail;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_contest_item, parent, false);
        return new AdapterContestUpcoming.ViewHolder(view);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {



        if (i%3==0||i==0){
            DatabaseReference db = FirebaseDatabase.getInstance()
                    .getReference(mContext.getString(R.string.dbname_Sponsors))
                    .child("sponsorId");

                   db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                holder.medialink=snapshot
                                       .child(mContext.getString(R.string.field_media_link))
                                       .getValue().toString();
                              holder.intentlink =snapshot
                                        .child(mContext.getString(R.string.field_intent_link))
                                        .getValue().toString();

                              if (holder.medialink.equals("")||holder.intentlink.equals("")){
                                  holder.sponsorImage.setVisibility(View.GONE);

                              }else{
                                  holder.sponsorImage.setVisibility(View.VISIBLE);
                                  Glide.with(mContext.getApplicationContext())
                                          .load(holder.medialink)
                                          .placeholder(R.drawable.load)
                                          .error(R.drawable.default_image2)
                                          .placeholder(R.drawable.load)
                                          .thumbnail(0.5f)
                                          .override(1024,1024)
                                          .into(holder.sponsorImage);
                              }


                            }else{
                                holder.sponsorImage.setVisibility(View.GONE);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                   holder.sponsorImage.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           try{
                               Uri uri = Uri.parse(holder.intentlink);
                               Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                               mContext.startActivity(intent);

                           }catch (ActivityNotFoundException e){
                               Log.e(SNTPClient.TAG, "onClick: "+ e.getMessage());
                           }
                       }
                   });

        }
        ContestDetail mcontest = mContestDetail.get(i);
        String key = mcontest.getCi();

        setgp(mcontest, holder.gp);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_participantList));
        db.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.p = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contestlist));
        ref.child(key)
                .child(mContext.getString(R.string.field_Participant_List))

                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.ok = true;

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        holder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.option);
                popupMenu.getMenuInflater().inflate(R.menu.post_menu_contest, popupMenu.getMenu());
                if (!holder.ok) {
                    popupMenu.getMenu().getItem(2).setVisible(false);

                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.ic_house) {
                            int sdk = android.os.Build.VERSION.SDK_INT;
                            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboard.setText(key);
                            } else {
                                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                android.content.ClipData clip = android.content.ClipData.newPlainText("Key", key);
                                clipboard.setPrimaryClip(clip);
                            }
                        } else if (item.getItemId() == R.id.ic_house1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Share");
                            builder.setMessage(R.string.type_of_share);

//                set buttons
                            builder.setPositiveButton("Ask for Vote", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String message = "Vote for me and help me to win this contest." +
                                            "\nTo Vote:" +
                                            "\n1) Download Orion:" +"https://play.google.com/store/apps/details?id=" + mContext.getPackageName()+
                                            "\n3) Enter contest key in contest search: "+ key +
                                            "\n4) Select contest and then Select Vote"+
                                            "\n5) Select submission you want to vote for."+
                                            "\n6) Vote";

                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("text/plain");
                                    share.putExtra(Intent.EXTRA_TEXT, message);
                                    mContext.startActivity(Intent.createChooser(share, "Select"));

                                }
                            });
                            builder.setNegativeButton("Ask for Participation", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String message = "Participate in this exciting contest." +
                                            "\nTo Participate:" +
                                            "\n1) Download Orion:" +"https://play.google.com/store/apps/details?id=" + mContext.getPackageName()+
                                            "\n3) Enter contest key in contest search: "+  key+
                                            "\n4) Select contest and then Select Participate"+
                                            "\n5) Fill Submission Form."+
                                            "\n6) Click Submit"+
                                            "\n Compete with the best!";

                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("text/plain");
                                    share.putExtra(Intent.EXTRA_TEXT, message);
                                    mContext.startActivity(Intent.createChooser(share, "Select"));
                                }
                            });
                            builder.create().show();
                        } else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Report");
                            builder.setMessage(R.string.report_prompt);

//                set buttons
                            builder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ReportPost(mcontest, holder.p);

                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                        }
                        return true;
                    }

                });

                popupMenu.show();

            }

        });
        DatabaseReference ref8 = FirebaseDatabase.getInstance().getReference();
        ref8.child(mContext.getString(R.string.dbname_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_username))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.username = dataSnapshot.getValue().toString();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        ref8.child(mContext.getString(R.string.dbname_users))
                .child(mcontest.getUi())
                .child(mContext.getString(R.string.field_username))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        holder.hostUsername = dataSnapshot.getValue().toString();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests));
        ref1.child(mcontest.getUi())
                .child(mContext.getString(R.string.created_contest))
                .child(mcontest.getCi())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        CreateForm mCreateForm = dataSnapshot.getValue(CreateForm.class);

                        holder.juryusername1 = mCreateForm.getJn1();
                        holder.juryusername2 = mCreateForm.getJn2();
                        holder.juryusername3 = mCreateForm.getJn3();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        holder.resultBtn.setVisibility(View.GONE);
        holder.participateBtn.setVisibility(View.GONE);
        holder.regSoonBtn.setVisibility(View.GONE);
        holder.voteBtn.setVisibility(View.GONE);
        holder.contestBtn.setVisibility(View.GONE);
        holder.limitBtn.setVisibility(View.GONE);

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


                String regStart = mcontest.getRb();
                java.text.DateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy");
                Date date2 = null;
                try {
                    date2 = (Date) formatter2.parse(regStart);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String regS = String.valueOf(date2.getTime());

                //*************************************************************************

                String voteStart = mcontest.getVb();
                java.text.DateFormat formatter3 = new SimpleDateFormat("dd-MM-yyyy");
                Date date3 = null;
                if (!voteStart.equals("-")) {

                    try {
                        date3 = (Date) formatter3.parse(voteStart);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    holder.voteS = String.valueOf(date3.getTime());
                }

                //*************************************************************************

                String voteEnd = mcontest.getVe();
                java.text.DateFormat formatter4 = new SimpleDateFormat("dd-MM-yyyy");
                Date date4 = null;
                if (!voteEnd.equals("-")) {
                    try {
                        date4 = (Date) formatter4.parse(voteEnd);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    holder.voteE = String.valueOf(date4.getTime());
                }


                //*************************************************************************

                String regEnd = mcontest.getRe();
                java.text.DateFormat formatter5 = new SimpleDateFormat("dd-MM-yyyy");
                Date date5 = null;
                try {
                    date5 = (Date) formatter5.parse(regEnd);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String regE = String.valueOf(date5.getTime());

                Log.d(TAG, "onTimeReceived: rege  " + regE);

                //*************************************************************************

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                wait(1000);

                                ((FragmentActivity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


//                compare all dates

                                        if (mcontest.getR()) {
                                            holder.resultBtn.setVisibility(View.VISIBLE);

                                        } else {

                                            if (Long.parseLong(regS) > Long.parseLong(timestamp)) {
                                                holder.regSoonBtn.setVisibility(View.VISIBLE);
                                            }
                                            if (Long.parseLong(regS) <= Long.parseLong(timestamp) && Long.parseLong(regE) >= Long.parseLong(timestamp)) {
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_participantList));
                                                ref.child(mcontest.getCi())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                long i = dataSnapshot.getChildrenCount();
                                                                if (!mcontest.getMlt().equals("Unlimited")) {
                                                                    if (!String.valueOf(i).equals(mcontest.getMlt())) {

                                                                        holder.participateBtn.setVisibility(View.VISIBLE);
                                                                        holder.reg = "yes";
                                                                    } else {

                                                                        holder.limitBtn.setVisibility(View.VISIBLE);
                                                                        holder.reg = "No";
                                                                    }

                                                                } else {
                                                                    holder.participateBtn.setVisibility(View.VISIBLE);
                                                                    holder.reg = "yes";
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                            }


                                            if (!voteStart.equals("-")) {
                                                if (Long.parseLong(holder.voteS) <= Long.parseLong(timestamp) && Long.parseLong(holder.voteE) >= Long.parseLong(timestamp)) {
                                                    holder.voteBtn.setVisibility(View.VISIBLE);
                                                    holder.vote = "yes";
                                                }
                                                if (Long.parseLong(holder.voteE) < Long.parseLong(timestamp) && Long.parseLong(regE) < Long.parseLong(timestamp)) {
//
                                                    holder.contestBtn.setVisibility(View.VISIBLE);
                                                }
                                            } else {
                                                if (Long.parseLong(regE) < Long.parseLong(timestamp)) {
//
                                                    holder.contestBtn.setVisibility(View.VISIBLE);
                                                }
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
                //*************************************************************************


                Log.e(SNTPClient.TAG, rawDate);

            }


            @Override
            public void onError(Exception ex) {
                Log.e(SNTPClient.TAG, ex.getMessage());
            }
        });

        holder.gp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpAlertDialog();

            }
        });
        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpAlertDialog();
            }
        });

        holder.participateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (holder.username.equals(holder.juryusername1) || holder.username.equals(holder.juryusername2)
                        || holder.username.equals(holder.juryusername3) || holder.username.equals(holder.hostUsername)) {
                    Intent i = new Intent(mContext.getApplicationContext(), JoiningForm.class);
                    i.putExtra("userId", mcontest.getUi());
                    i.putExtra("contestId", mcontest.getCi());
                    i.putExtra("isJuryOrHost", "true");
                    mContext.startActivity(i);
                } else {
                    Intent i = new Intent(mContext.getApplicationContext(), JoiningForm.class);
                    i.putExtra("userId", mcontest.getUi());
                    i.putExtra("contestId", mcontest.getCi());
                    i.putExtra("isJuryOrHost", "false");
                    mContext.startActivity(i);
                }

            }
        });

        holder.voteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mcontest.getVt().equals("Public")) {
                    Intent i = new Intent(mContext.getApplicationContext(), public_voting_media.class);
                    i.putExtra("userId", mcontest.getUi());
                    i.putExtra("contestId", mcontest.getCi());
                    mContext.startActivity(i);

                } else {

                    if (holder.username.equals(holder.juryusername1)) {
                        Intent i = new Intent(mContext.getApplicationContext(), jury_voting_Activity.class);
                        i.putExtra("userId", mcontest.getUi());
                        i.putExtra("contestId", mcontest.getCi());
                        i.putExtra("jury", "jury1");
                        i.putExtra("comment", "comment1");
                        i.putExtra("mediaType", mcontest.getMlt());

                        mContext.startActivity(i);

                    } else if (holder.username.equals(holder.juryusername2)) {
                        Intent i = new Intent(mContext.getApplicationContext(), jury_voting_Activity.class);
                        i.putExtra("userId", mcontest.getUi());
                        i.putExtra("contestId", mcontest.getCi());
                        i.putExtra("jury", "jury2");
                        i.putExtra("comment", "comment2");
                        i.putExtra("mediaType", mcontest.getMlt());


                        mContext.startActivity(i);

                    } else if (holder.username.equals(holder.juryusername3)) {
                        Intent i = new Intent(mContext.getApplicationContext(), jury_voting_Activity.class);
                        i.putExtra("userId", mcontest.getUi());
                        i.putExtra("contestId", mcontest.getCi());
                        i.putExtra("jury", "jury3");
                        i.putExtra("comment", "comment3");
                        i.putExtra("mediaType", mcontest.getMlt());

                        mContext.startActivity(i);

                    } else {
                        Intent i = new Intent(mContext.getApplicationContext(), public_voting_media.class);
                        i.putExtra("userId", mcontest.getUi());
                        i.putExtra("contestId", mcontest.getCi());
                        mContext.startActivity(i);
                    }
                }


            }
        });


        holder.entryFee.setText(mcontest.getEf());
        holder.domain.setText(mcontest.getD());

        getcontestDetails(mcontest.getUi(), mcontest.getCi(), holder.poster
                , holder.title, holder.host, holder.regEnd, holder.totalP, holder.progress);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mcontest.getR()) {
                    Intent i = new Intent(mContext.getApplicationContext(), ResultDeclaredActivity.class);
                    i.putExtra("userId", mcontest.getUi());
                    i.putExtra("contestId", mcontest.getCi());

                    mContext.startActivity(i);
                } else {
                    Intent i = new Intent(mContext.getApplicationContext(), ViewContestDetails.class);
                    i.putExtra("userId", mcontest.getUi());
                    i.putExtra("contestId", mcontest.getCi());
                    i.putExtra("Vote", holder.vote);
                    i.putExtra("reg", holder.reg);
                    mContext.startActivity(i);
                }


            }
        });


    }

    private void gpAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle(mContext.getString(R.string.gp_displayer_tittle));
        alertDialog.setMessage(mContext.getString(R.string.gp_diplayer_description));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cool",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    private void setgp(ContestDetail mcontest, TextView gp) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(mContext.getString(R.string.dbname_contests))
                .child(mcontest.getUi())
                .child(mContext.getString(R.string.field_contest_completed))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            long y = (long) snapshot.getValue();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.child(mContext.getString(R.string.dbname_contests))
                                    .child(mcontest.getUi())
                                    .child(mContext.getString(R.string.field_contest_reports))
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

    private void ReportPost(ContestDetail mcontest, int p) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contestlist))
                .child(mcontest.getCi())
                .child(mContext.getString(R.string.field_contest_report_list));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Toast.makeText(mContext, "You already reported this contest.", Toast.LENGTH_SHORT).show();

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
                                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))
                                                        .child(mcontest.getUi())
                                                        .child(mContext.getString(R.string.field_contest_reports));
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

    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType: "+position);
        return position;
    }

    @Override
    public long getItemId(int position) {
        ContestDetail form = mContestDetail.get(position);
        return form.getCi().hashCode();
    }

    @Override
    public int getItemCount() {
        return mContestDetail.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private String juryusername1 = "", juryusername2 = "", juryusername3 = "";

        private TextView domain, title, regEnd, entryFee, host, totalP, ended, gp;
        private ImageView poster, option, info, progress,sponsorImage;
        String vote = "No";
        String reg = "No";
        String voteS = "";
        String voteE = "";
        Boolean ok = false;
        String intentlink,medialink;
        int p = 0;
        String username = "";
        String hostUsername = "";
        private Button voteBtn, participateBtn, regSoonBtn, contestBtn, resultBtn, limitBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            domain = itemView.findViewById(R.id.domainD);
            title = itemView.findViewById(R.id.titleD);
            regEnd = itemView.findViewById(R.id.regendD);
            entryFee = itemView.findViewById(R.id.entryfeeD);
            host = itemView.findViewById(R.id.hostD);
            totalP = itemView.findViewById(R.id.totalprizeD);
            poster = itemView.findViewById(R.id.posterD);
            voteBtn = itemView.findViewById(R.id.voteBtn);
            participateBtn = itemView.findViewById(R.id.participateBtn);
            regSoonBtn = itemView.findViewById(R.id.regSoonBtn);
            contestBtn = itemView.findViewById(R.id.contestBtn);
            resultBtn = itemView.findViewById(R.id.resultBtn);
            limitBtn = itemView.findViewById(R.id.limitBtn);
            option = itemView.findViewById(R.id.optionC);
            gp = itemView.findViewById(R.id.gp);
            info = itemView.findViewById(R.id.info);
            progress = itemView.findViewById(R.id.progress);
            sponsorImage = itemView.findViewById(R.id.sponsor);




        }
    }

    private void getcontestDetails(String userid, String contestid, ImageView poster, TextView title,
                                   TextView host, TextView regend, TextView totalp, ImageView progress) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))
                .child(userid)
                .child(mContext.getString(R.string.created_contest))
                .child(contestid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CreateForm createForm = dataSnapshot.getValue(CreateForm.class);
                title.setText(createForm.getCt());
                host.setText(createForm.getHst());
                regend.setText(createForm.getRe());
                totalp.setText(createForm.getTp());
                Glide.with(mContext.getApplicationContext())
                        .load(createForm.getPo())
                        .placeholder(R.drawable.load)
                        .error(R.drawable.default_image2)
                        .placeholder(R.drawable.load)
                        .thumbnail(0.5f)
                        .override(1024,1024)
                        .into(poster);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
