package com.orion.orion.Adapters;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.orion.orion.contest.joined.joined_contest_overview_activity;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.JoinForm;

import java.util.List;

public class AdapterContestJoined extends RecyclerView.Adapter<AdapterContestJoined.ViewHolder> {
    private String mAppend = "";

    private Context mContext;
    private List<JoinForm> joiningForms;

    public AdapterContestJoined(Context mContext, List<JoinForm> joiningForms) {
        this.mContext = mContext;
        this.joiningForms = joiningForms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.layout_contest_item,parent,false);
        return new AdapterContestJoined.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        JoinForm joiningForm= joiningForms.get(i);
        holder.relStatus.setVisibility(View.VISIBLE);
        String key = joiningForm.getCi();
        String userid=joiningForm.getHst();

        setgp(userid, holder.gp);

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

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests));
        ref2.child(joiningForm.getUi())
                .child(mContext.getString(R.string.joined_contest))
                .child(joiningForm.getJi())
                .child(mContext.getString(R.string.rejection_reason))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.rejectBtn.setVisibility(View.VISIBLE);
                            holder.reason=dataSnapshot.getValue().toString();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reasonDialoge(holder.reason);
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
                            builder.setMessage("Are you sure, you want to Report this Contest?");

//                set buttons
                            builder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ReportPost(joiningForm.getCi(),joiningForm.getHst(), holder.p);

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

//
        String status = joiningForm.getSt();
        holder.status.setText(status);
        if (joiningForm.getSt().equals("Rejected")) {
            holder.status.setTextColor(Color.RED);
        } else if (joiningForm.getSt().equals("Accepted")) {
            holder.status.setTextColor(Color.GREEN);
        }


        getcontestDetails(joiningForm.getHst(),joiningForm.getCi(),holder.poster
                ,holder.title,holder.host,holder.regEnd,holder.totalP,holder.entryFee,holder.domain,holder.progress);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals("waiting") || status.equals("Rejected")) {
                    Intent i = new Intent(mContext.getApplicationContext(), ViewContestDetails.class);
                    i.putExtra("userId", joiningForm.getHst());
                    i.putExtra("contestId", joiningForm.getCi());
                    i.putExtra("Vote","No");
                    i.putExtra("reg", "No");
                    mContext.startActivity(i);
                }else{

                    Intent i = new Intent(mContext.getApplicationContext(), joined_contest_overview_activity.class);
                    i.putExtra("userId", joiningForm.getHst());
                    i.putExtra("contestId", joiningForm.getCi());
                    i.putExtra("joiningKey", joiningForm.getJi());
                    i.putExtra("hostId", joiningForm.getHst());


                    mContext.startActivity(i);
                }
            }
        });


    }
    @Override
    public long getItemId(int position) {
        JoinForm form = joiningForms.get(position);
        return form.getJi().hashCode();
    }
    @Override
    public int getItemCount() {
        return joiningForms.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView domain, title, regEnd, entryFee, host, totalP,status,statusTv,gp;
        private ImageView poster,option,progress;
        RelativeLayout relStatus;
        private Button rejectBtn;
        String reason;
        Boolean ok = false;

        int p = 0;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            domain = itemView.findViewById(R.id.domainD);
            title = itemView.findViewById(R.id.titleD);
            regEnd = itemView.findViewById(R.id.regendD);
            entryFee = itemView.findViewById(R.id.entryfeeD);
            host = itemView.findViewById(R.id.hostD);
            totalP = itemView.findViewById(R.id.totalprizeD);
            poster = itemView.findViewById(R.id.posterD);
            status = itemView.findViewById(R.id.status);
            statusTv = itemView.findViewById(R.id.statusTv);
            status.setVisibility(View.VISIBLE);
            statusTv.setVisibility(View.VISIBLE);
            option = itemView.findViewById(R.id.optionC);
            gp = itemView.findViewById(R.id.gp);
            relStatus = itemView.findViewById(R.id.relStatus);
            progress = itemView.findViewById(R.id.progress);
            rejectBtn = itemView.findViewById(R.id.rejectionBtn);




        }
    }
    private void setgp(String userid, TextView gp) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(mContext.getString(R.string.dbname_contests))
                .child(userid)
                .child(mContext.getString(R.string.field_contest_completed))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            long y = (long) snapshot.getValue();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.child(mContext.getString(R.string.dbname_contests))
                                    .child(userid)
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

    private void ReportPost(String contestId,String userId, int p) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contestlist))
                .child(contestId)
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
                                                        .child(userId)
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

    private void reasonDialoge(String s) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle("Rejection Reason");
        alertDialog.setMessage(s);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    private  void getcontestDetails(String userid, String contestid, ImageView poster, TextView title,
                                    TextView host, TextView regend, TextView totalp, TextView entryfee, TextView domain, ImageView progress){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))

                .child(userid)
                .child(mContext.getString(R.string.created_contest))
                .child(contestid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CreateForm createForm=dataSnapshot.getValue(CreateForm.class);
                title.setText(createForm.getCt());
                host.setText(createForm.getHst());
                regend.setText(createForm.getRe());
                totalp.setText(createForm.getTp());
                entryfee.setText(createForm.getEf());
                domain.setText(createForm.getD());

                Glide.with(mContext.getApplicationContext())
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

}
