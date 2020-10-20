package com.orion.orion.Adapters;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.orion.orion.contest.jury_voting_media;
import com.orion.orion.contest.public_voting_media;
import com.orion.orion.contest.result.ResultDeclaredActivity;
import com.orion.orion.models.ContestDetail;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.users;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.StringManipilation;
import com.orion.orion.util.UniversalImageLoader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.android.volley.VolleyLog.TAG;

public class AdapterContestUpcomingGrid extends RecyclerView.Adapter<AdapterContestUpcomingGrid.ViewHolder> {
    private String mAppend = "";



    private  String juryusername1="",juryusername2="",juryusername3="";
     String timestamp="";
     String timestamp2="";

    private Context mContext;
    private List<ContestDetail> mContestDetail;

    public AdapterContestUpcomingGrid(Context mContext, List<ContestDetail> mContestDetail) {
        this.mContext = mContext;
        this.mContestDetail = mContestDetail;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_contest_item2, parent, false);
        return new AdapterContestUpcomingGrid.ViewHolder(view);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {



        ContestDetail mcontest = mContestDetail.get(i);
        getNumberofParticipants(mcontest.getContestId(),mcontest.getMaxLimit());
        String key = mcontest.getContestId();
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
                .child("participantlist")

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
                Log.d(TAG, "onClick: lkj" + holder.ok);
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
                            String message =
                                    "https://play.google.com/store/apps/details?id=" + mContext.getPackageName() +
                                            "Download ORION and share,participate in your domains contests."
                                            + "Enter Contest key " + key + " in Contest"
                                            + "Vote or Participate";
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("text/plain");
                            share.putExtra(Intent.EXTRA_TEXT, message);

                            mContext.startActivity(Intent.createChooser(share, "Select"));
                        } else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Report");
                            builder.setMessage("Are you sure, you want to Report this Contest?");

//                set buttons
                            builder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "DeleteMessage: deleteing message");
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

        DatabaseReference ref2= FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests));
                ref2.child(mcontest.getUserId())
                .child(mContext.getString(R.string.created_contest))
                .child(mcontest.getContestId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        CreateForm mCreateForm=dataSnapshot.getValue(CreateForm.class);
                        juryusername1=mCreateForm.getJname_1();
                        juryusername2=mCreateForm.getJname_2();
                        juryusername3=mCreateForm.getJname_3();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Colombo"), new SNTPClient.Listener()
        {
            @Override
            public void onTimeReceived(String rawDate) {
                // rawDate -> 2019-11-05T17:51:01+0530

         //*************************************************************************
                String currentTime = StringManipilation.getTime(rawDate);
                Log.d(TAG, "onTimeReceived: current time"+currentTime);
                DateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy");
                Date date1 = null;
                try {
                    date1 = (Date) formatter1.parse(currentTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                  timestamp = String.valueOf(date1.getTime());

                Log.d(TAG, "onTimeReceived: 1  "+timestamp);


                String regStart = mcontest.getRegBegin();
                DateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy");
                Date date2 = null;
                try {
                    date2 = (Date) formatter2.parse(regStart);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String regS = String.valueOf(date2.getTime());

                //*************************************************************************

                String voteStart = mcontest.getVoteBegin();
                DateFormat formatter3 = new SimpleDateFormat("dd-MM-yyyy");
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

                String voteEnd = mcontest.getVoteEnd();
                DateFormat formatter4 = new SimpleDateFormat("dd-MM-yyyy");
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

               String regEnd = mcontest.getRegEnd();
                DateFormat formatter5 = new SimpleDateFormat("dd-MM-yyyy");
                Date date5 = null;
                try {
                    date5= (Date) formatter5.parse(regEnd);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String regE = String.valueOf(date5.getTime());

                Log.d(TAG, "onTimeReceived: rege  "+regE);

                //*************************************************************************

              Thread  thread = new Thread(){
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                wait(1000);

                                ((FragmentActivity)mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {




//                compare all dates

                                        Log.d(TAG, "run: timeeeee"+timestamp);

                                        if (mcontest.getResult()){

                                        }else {

                                            if(Long.parseLong(regS) <= Long.parseLong(timestamp)&&Long.parseLong(regE) >= Long.parseLong(timestamp))
                                            {
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_participantList));
                                            ref.child(mcontest.getContestId())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            long i = dataSnapshot.getChildrenCount();
                                                            if (!mcontest.getMaxLimit().equals("Unlimited")) {
                                                                if (!String.valueOf(i).equals(mcontest.getMaxLimit())) {

                                                                    holder.reg = "yes";
                                                                } else {

                                                                    holder.reg = "No";
                                                                }

                                                            }else{
                                                                holder.reg = "yes";
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                        }


                                            if (!voteStart.equals("-")) {
                                                if (Long.parseLong(holder.voteS) <= Long.parseLong(timestamp) && Long.parseLong(holder.voteE) >=Long.parseLong(timestamp)) {
                                                    holder. vote = "yes";
                                                }

                                            }

                                        }

                                    }
                                });

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    };
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






        holder.entryFee.setText(mcontest.getEntryfee());
        holder.domain.setText(mcontest.getDoman());

        getcontestDetails(mcontest.getUserId(),mcontest.getContestId(),holder.poster
        ,holder.title);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mcontest.getResult()){
                    Intent i = new Intent(mContext.getApplicationContext(), ResultDeclaredActivity.class);
                    i.putExtra("userId",mcontest.getUserId());
                    i.putExtra("contestId",mcontest.getContestId());

                    mContext.startActivity(i);
                }else {
                    Intent i = new Intent(mContext.getApplicationContext(), ViewContestDetails.class);
                    i.putExtra("userId", mcontest.getUserId());
                    i.putExtra("contestId", mcontest.getContestId());
                    i.putExtra("Vote",holder.vote);
                    i.putExtra("reg", holder.reg);
                    mContext.startActivity(i);
                }


            }
        });


        Log.d(TAG, "hello22: "+ timestamp2);
    }

    private void getNumberofParticipants(String contestId, String maxLimit) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_participantList));
                ref.child(contestId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long i = dataSnapshot.getChildrenCount();
                        if (String.valueOf(i).equals(maxLimit)){

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    public long getItemId(int position) {
        ContestDetail form = mContestDetail.get(position);
        return form.getContestId().hashCode();
    }
    @Override
    public int getItemCount() {
        return mContestDetail.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView domain, title, entryFee,gp;
        private ImageView poster,option;
        String vote="No";
        String reg="No";
        String voteS="";
        String voteE="";
        Boolean ok = false;
        int p = 0;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            domain = itemView.findViewById(R.id.domainD);
            title = itemView.findViewById(R.id.titleD);
            entryFee = itemView.findViewById(R.id.entryfeeD);

            poster = itemView.findViewById(R.id.posterD);
            option = itemView.findViewById(R.id.optionC);
            gp = itemView.findViewById(R.id.gp);


            Log.d(TAG, "hello2kk2: "+ timestamp2);


        }
    }
        private  void getcontestDetails(String userid, String contestid, ImageView poster, TextView title){
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))
                    .child(userid)
                    .child(mContext.getString(R.string.created_contest))
                    .child(contestid);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    CreateForm createForm=dataSnapshot.getValue(CreateForm.class);
                    title.setText(createForm.getTitle());
                    Log.d(TAG, "onDataChange: image"+createForm.getPoster() );
                    UniversalImageLoader.setImage(createForm.getPoster(),poster,null,mAppend);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




    }
    private void ReportPost(ContestDetail mcontest, int p) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(mContext.getString(R.string.dbname_contestlist))
                .child(mcontest.getContestId())
                .child("tr")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            Toast.makeText(mContext, "You already reported this contest.", Toast.LENGTH_SHORT).show();

                        } else {


                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.child(mContext.getString(R.string.dbname_contestlist))
                                    .child(mcontest.getContestId())
                                    .child("tr")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(true)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
                                            reference2.child(mContext.getString(R.string.dbname_contestlist))
                                                    .child(mcontest.getContestId())
                                                    .child("tr")
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            long i = snapshot.getChildrenCount();
                                                            if ((((i + 1) / p) * 100) > 60) {
                                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                                                reference.child(mContext.getString(R.string.dbname_contests))
                                                                        .child(mcontest.getUserId())
                                                                        .child("reports")
                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if (snapshot.exists()) {
                                                                                    long x = (long) snapshot.getValue();
                                                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                                                                    reference.child(mContext.getString(R.string.dbname_contests))
                                                                                            .child(mcontest.getUserId())
                                                                                            .child("reports")
                                                                                            .setValue(x + 1);
                                                                                } else {
                                                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                                                                    reference.child(mContext.getString(R.string.dbname_contests))
                                                                                            .child(mcontest.getUserId())
                                                                                            .child("reports")
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


    private void setgp(ContestDetail mcontest, TextView gp) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(mContext.getString(R.string.dbname_contests))
                .child(mcontest.getUserId())
                .child("completed")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            long y = (long) snapshot.getValue();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.child(mContext.getString(R.string.dbname_contests))
                                    .child(mcontest.getUserId())
                                    .child("reports")
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
