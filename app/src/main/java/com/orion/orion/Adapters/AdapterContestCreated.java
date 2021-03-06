package com.orion.orion.Adapters;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.contest.Contest_Evaluation.ContestEvaluationActivity;
import com.orion.orion.models.CreateForm;

import java.util.List;
import java.util.Objects;


public class AdapterContestCreated extends RecyclerView.Adapter<AdapterContestCreated.ViewHolder> {
    private Context mContext;
    private List<CreateForm> createForms;

    public AdapterContestCreated(Context mContext, List<CreateForm> createForms) {
        this.mContext = mContext;
        this.createForms = createForms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_contest_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        CreateForm mcreateForm = createForms.get(i);
        String key = mcreateForm.getCi();
        String userid = mcreateForm.getUi();
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
        ref2.child(mcreateForm.getUi())
                .child(mContext.getString(R.string.created_contest))
                .child(mcreateForm.getCi())
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
        ref.child(key).child(mContext.getString(R.string.field_Participant_List))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
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
        holder.gp.setOnClickListener(view -> displayGpAlert());
        holder.info.setOnClickListener(view -> displayGpAlert());
        holder.option.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(mContext, holder.option);
            popupMenu.getMenuInflater().inflate(R.menu.post_menu_contest, popupMenu.getMenu());
            if (!holder.ok) {
                popupMenu.getMenu().getItem(2).setVisible(false);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.ic_house) {
                    int sdk = android.os.Build.VERSION.SDK_INT;
                    ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        clipboard.setText(key);
                    } else {
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
                    builder.setPositiveButton("Report", (dialog, which) -> ReportPost(mcreateForm.getCi(), mcreateForm.getUi(), holder.p));
                    builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                    builder.create().show();
                }
                return true;
            });
            popupMenu.show();
        });

        holder.relStatus.setVisibility(View.VISIBLE);
        holder.status.setText(mcreateForm.getSt());
        if (mcreateForm.getSt().equals("Rejected")) {
            holder.status.setTextColor(Color.RED);
        } else if (mcreateForm.getSt().equals("Accepted")) {
            holder.status.setTextColor(Color.GREEN);
        }

        holder.entryFee.setText(mcreateForm.getEf());
        holder.domain.setText(mcreateForm.getD());

        Glide.with(holder.itemView.getContext().getApplicationContext())
                .load(mcreateForm.getPo())
                .placeholder(R.drawable.load)
                .error(R.drawable.default_image2)
                .placeholder(R.drawable.load)
                .thumbnail(0.5f)
                .into(holder.poster);
        holder.title.setText(mcreateForm.getCt());
        holder.host.setText(mcreateForm.getHst());
        holder.regEnd.setText(mcreateForm.getRe());
        holder.totalP.setText(mcreateForm.getTp());

        holder.itemView.setOnClickListener(v -> {
            Intent i1 = new Intent(mContext.getApplicationContext(), ContestEvaluationActivity.class);
            i1.putExtra("contestId", mcreateForm.getCi());
            i1.putExtra("userid", mcreateForm.getUi());
            i1.putExtra("title", mcreateForm.getCt());
            mContext.startActivity(i1);
        });
    }

    private void displayGpAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle(mContext.getString(R.string.gp_displayer_tittle));
        alertDialog.setMessage("This is the percentage showing how much genuine this host is.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cool",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    @Override
    public long getItemId(int position) {
        CreateForm form = createForms.get(position);
        return form.getCi().hashCode();
    }
    @Override
    public int getItemCount() {
        return createForms.size();
    }

    private void setgp(String userid, TextView gp) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(mContext.getString(R.string.dbname_contests)).child(userid).child(mContext.getString(R.string.field_contest_completed)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long y = (long) snapshot.getValue();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.child(mContext.getString(R.string.dbname_contests)).child(userid).child(mContext.getString(R.string.field_contest_reports)).addListenerForSingleValueEvent(new ValueEventListener() {
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
    private void ReportPost(String contestId, String userid, int p) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(mContext.getString(R.string.dbname_contestlist))
                .child(contestId)
                .child(mContext.getString(R.string.field_contest_report_list)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                    Toast.makeText(mContext, "You already reported this contest.", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.child(mContext.getString(R.string.dbname_contestlist)).child(contestId).child(mContext.getString(R.string.field_contest_report_list))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(true).addOnCompleteListener(task -> {
                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
                        reference2.child(mContext.getString(R.string.dbname_contestlist))
                                .child(contestId)
                                .child(mContext.getString(R.string.field_contest_report_list))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                long i = snapshot1.getChildrenCount();
                                if ((((i + 1) / p) * 100) > 60) {
                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                                    reference1.child(mContext.getString(R.string.dbname_contests))
                                            .child(userid)
                                            .child(mContext.getString(R.string.field_contest_reports))
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                            if (snapshot1.exists()) {
                                                long x = (long) snapshot1.getValue();
                                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                                                reference1.child(mContext.getString(R.string.dbname_contests))
                                                        .child(userid)
                                                        .child(mContext.getString(R.string.field_contest_reports))
                                                        .setValue(x + 1);
                                            } else {
                                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                                                reference1.child(mContext.getString(R.string.dbname_contests))
                                                        .child(userid)
                                                        .child(mContext.getString(R.string.field_contest_reports))
                                                        .setValue(0);
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
        return position;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        Boolean ok = false;
        int p = 0;
        private TextView domain, title, regEnd, entryFee, host, gp, totalP, status;
        private ImageView poster;
        private ImageView option;
        private ImageView info;
        private RelativeLayout relStatus;
        private Button rejectBtn;
        String reason;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            domain = itemView.findViewById(R.id.domainD);
            title = itemView.findViewById(R.id.titleD);
            regEnd = itemView.findViewById(R.id.regendD);
            entryFee = itemView.findViewById(R.id.entryfeeD);
            host = itemView.findViewById(R.id.hostD);
            totalP = itemView.findViewById(R.id.totalprizeD);
            poster = itemView.findViewById(R.id.posterD);
            relStatus = itemView.findViewById(R.id.relStatus);
            status = itemView.findViewById(R.id.status);
            option = itemView.findViewById(R.id.optionC);
            gp = itemView.findViewById(R.id.gp);
            info = itemView.findViewById(R.id.info);
            rejectBtn = itemView.findViewById(R.id.rejectionBtn);


        }
    }
}