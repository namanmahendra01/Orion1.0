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
import com.orion.orion.contest.result.ResultDeclaredActivity;
import com.orion.orion.models.ContestDetail;
import com.orion.orion.models.CreateForm;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.StringManipilation;

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
        getNumberofParticipants(mcontest.getCi(),mcontest.getMLt());
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
                            String message =
                                    "https://play.google.com/store/apps/details?id=" + mContext.getPackageName() +
                                            "\nDownload ORION to Participate and Vote in contests or share you skill."
                                            + "\nEnter Contest key( *" + key + "* )in Contest Search to vote for this contest";
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("text/plain");
                            share.putExtra(Intent.EXTRA_TEXT, message);

                            mContext.startActivity(Intent.createChooser(share, "Select"));
                        } else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Report");
                            builder.setMessage(mContext.getString(R.string.report_prompt));

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

        DatabaseReference ref2= FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests));
                ref2.child(mcontest.getUi())
                .child(mContext.getString(R.string.created_contest))
                .child(mcontest.getCi())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        CreateForm mCreateForm=dataSnapshot.getValue(CreateForm.class);
                        juryusername1=mCreateForm.getJn1();
                        juryusername2=mCreateForm.getJn2();
                        juryusername3=mCreateForm.getJn3();

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
                DateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy");
                Date date1 = null;
                try {
                    date1 = (Date) formatter1.parse(currentTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                  timestamp = String.valueOf(date1.getTime());



                String regStart = mcontest.getRb();
                DateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy");
                Date date2 = null;
                try {
                    date2 = (Date) formatter2.parse(regStart);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String regS = String.valueOf(date2.getTime());

                //*************************************************************************

                String voteStart = mcontest.getVb();
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

                String voteEnd = mcontest.getVe();
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

               String regEnd = mcontest.getRe();
                DateFormat formatter5 = new SimpleDateFormat("dd-MM-yyyy");
                Date date5 = null;
                try {
                    date5= (Date) formatter5.parse(regEnd);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String regE = String.valueOf(date5.getTime());


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


                                        if (mcontest.getR()){

                                        }else {

                                            if(Long.parseLong(regS) <= Long.parseLong(timestamp)&&Long.parseLong(regE) >= Long.parseLong(timestamp))
                                            {
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_participantList));
                                            ref.child(mcontest.getCi())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            long i = dataSnapshot.getChildrenCount();
                                                            if (!mcontest.getMLt().equals("Unlimited")) {
                                                                if (!String.valueOf(i).equals(mcontest.getMLt())) {

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






        holder.entryFee.setText(mcontest.getEf());
        holder.domain.setText(mcontest.getD());

        getcontestDetails(mcontest.getUi(),mcontest.getCi(),holder.poster
        ,holder.title,holder.progress);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mcontest.getR()){
                    Intent i = new Intent(mContext.getApplicationContext(), ResultDeclaredActivity.class);
                    i.putExtra("userId",mcontest.getUi());
                    i.putExtra("contestId",mcontest.getCi());

                    mContext.startActivity(i);
                }else {
                    Intent i = new Intent(mContext.getApplicationContext(), ViewContestDetails.class);
                    i.putExtra("userId", mcontest.getUi());
                    i.putExtra("contestId", mcontest.getCi());
                    i.putExtra("Vote",holder.vote);
                    i.putExtra("reg", holder.reg);
                    mContext.startActivity(i);
                }


            }
        });


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
        return form.getCi().hashCode();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return mContestDetail.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView domain, title, entryFee,gp;
        private ImageView poster,option,progress;
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
            progress = itemView.findViewById(R.id.progress);



            Log.d(TAG, "hello2kk2: "+ timestamp2);


        }
    }
        private  void getcontestDetails(String userid, String contestid, ImageView poster, TextView title, ImageView progress){
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))
                    .child(userid)
                    .child(mContext.getString(R.string.created_contest))
                    .child(contestid);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    CreateForm createForm=dataSnapshot.getValue(CreateForm.class);
                    title.setText(createForm.getCt());
                    Log.d(TAG, "onDataChange: image"+createForm.getPo() );
                    Glide.with(mContext)
                            .load(createForm.getPo())
                            .placeholder(R.drawable.load)
                            .error(R.drawable.default_image2)
                            .placeholder(R.drawable.load)
                            .thumbnail(0.5f)
                            .into(poster);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

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
                                                        .child(mContext.getString(R.string.dbname_contests))
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

    private void setgp(ContestDetail mcontest, TextView gp) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(mContext.getString(R.string.dbname_contests))
                .child(mcontest.getUi())
                .child("completed")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            long y = (long) snapshot.getValue();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.child(mContext.getString(R.string.dbname_contests))
                                    .child(mcontest.getUi())
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
