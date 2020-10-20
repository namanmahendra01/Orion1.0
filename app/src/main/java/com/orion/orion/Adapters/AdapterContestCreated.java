package com.orion.orion.Adapters;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.contest.Contest_Evaluation.contest_evaluation_activity;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.Photo;
import com.orion.orion.profile.Account.Contest;
import com.orion.orion.util.UniversalImageLoader;

import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class AdapterContestCreated extends RecyclerView.Adapter<AdapterContestCreated.ViewHolder> {
    private String mAppend = "";
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
        return new AdapterContestCreated.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        CreateForm mcreateForm = createForms.get(i);
        String key = mcreateForm.getContestkey();
        String userid = mcreateForm.getUserid();
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contestlist));
        ref.child(key).child("participantlist").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.ok = true;
                    Log.d(TAG, "onClick: lkj1" + holder.ok);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        holder.option.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(mContext, holder.option);
            popupMenu.getMenuInflater().inflate(R.menu.post_menu_contest, popupMenu.getMenu());
            Log.d(TAG, "onClick: lkj" + holder.ok);
            if (!holder.ok) {
                popupMenu.getMenu().getItem(2).setVisible(false);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.ic_house) {
                    int sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(key);
                    } else {
                        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
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
                    builder.setPositiveButton("Report", (dialog, which) -> {
                        Log.d(TAG, "DeleteMessage: deleteing message");
                        ReportPost(mcreateForm.getContestkey(), mcreateForm.getUserid(), holder.p);
                    });
                    builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                    builder.create().show();
                }
                return true;
            });
            popupMenu.show();
        });

        holder.relStatus.setVisibility(View.VISIBLE);
        holder.status.setText(mcreateForm.getStatus());
        if (mcreateForm.getStatus().equals("Rejected")) {
            holder.status.setTextColor(Color.RED);
        } else if (mcreateForm.getStatus().equals("Accepted")) {
            holder.status.setTextColor(Color.GREEN);
        }

        holder.entryFee.setText(mcreateForm.getEntryfee());
        holder.domain.setText(mcreateForm.getDomain());
        UniversalImageLoader.setImage(mcreateForm.getPoster(), holder.poster, null, mAppend);
        holder.title.setText(mcreateForm.getTitle());
        holder.host.setText(mcreateForm.getHost());
        holder.regEnd.setText(mcreateForm.getRegEnd());
        holder.totalP.setText(mcreateForm.getTotal_prize());
        holder.itemView.setOnClickListener(v -> {
            Intent i1 = new Intent(mContext.getApplicationContext(), contest_evaluation_activity.class);
            i1.putExtra("contestId", mcreateForm.getContestkey());
            i1.putExtra("userid", mcreateForm.getUserid());
            mContext.startActivity(i1);
        });
    }
    @Override
    public long getItemId(int position) {
        CreateForm form = createForms.get(position);
        return form.getContestkey().hashCode();
    }
    @Override
    public int getItemCount() {
        return createForms.size();
    }

    private void setgp(String userid, TextView gp) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(mContext.getString(R.string.dbname_contests)).child(userid).child("completed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long y = (long) snapshot.getValue();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.child(mContext.getString(R.string.dbname_contests)).child(userid).child("reports").addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void ReportPost(String contestId, String userid, int p) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(mContext.getString(R.string.dbname_contestlist)).child(contestId).child("tr").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Toast.makeText(mContext, "You already reported this contest.", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.child(mContext.getString(R.string.dbname_contestlist)).child(contestId).child("tr").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true).addOnCompleteListener(task -> {
                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
                        reference2.child(mContext.getString(R.string.dbname_contestlist)).child(contestId).child("tr").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                long i = snapshot1.getChildrenCount();
                                if ((((i + 1) / p) * 100) > 60) {
                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                                    reference1.child(mContext.getString(R.string.dbname_contests)).child(userid).child("reports").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                            if (snapshot1.exists()) {
                                                long x = (long) snapshot1.getValue();
                                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                                                reference1.child(mContext.getString(R.string.dbname_contests)).child(userid).child("reports").setValue(x + 1);
                                            } else {
                                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
                                                reference1.child(mContext.getString(R.string.dbname_contests)).child(userid).child("reports").setValue(0);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        Boolean ok = false;
        int p = 0;
        private TextView domain, title, regEnd, entryFee, host, gp, totalP, status;
        private ImageView poster, option;
        private RelativeLayout relStatus;

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
        }
    }
}