package com.orion.orion.Adapters;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.contest.Contest_Evaluation.activity_view_media;
import com.orion.orion.models.ParticipantList;

import java.util.List;

import static com.orion.orion.util.SNTPClient.TAG;

public class AdapterGridImageContest extends RecyclerView.Adapter<AdapterGridImageContest.ViewHolder> {


    private static Context mContext;
    boolean isImage;
    static ViewHolder previousHolder=null;
    private List<ParticipantList> participantLists;
    public interface changeBackground {
        default void changeColor() {

            if(previousHolder!=null) {
                SharedPreferences sp =mContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                String color =sp.getString("backColor",null);

                if(color.equals("White")){
                    previousHolder.card.setCardBackgroundColor(Color.WHITE);

                }else if(color.equals("Yellow")){
                    previousHolder.card.setCardBackgroundColor(Color.YELLOW);
                }


            }
        }


    }


    public AdapterGridImageContest(Context mContext, List<ParticipantList> participantLists, boolean isImage) {
        this.mContext = mContext;
        this.participantLists = participantLists;
        this.isImage = isImage;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (isImage) {
            view = LayoutInflater.from(mContext).inflate(R.layout.grid_image_item, parent, false);

        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.public_vote_item, parent, false);

        }
        return new AdapterGridImageContest.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        ParticipantList participantList = participantLists.get(i);

        if (isImage) {
            Display display = ((Activity)mContext).getWindowManager().getDefaultDisplay();
            int width = display.getWidth(); // ((display.getWidth()*20)/100)
            CardView.LayoutParams parms = new CardView.LayoutParams(width/3,width/3);
            holder.image.setLayoutParams(parms);
            ifCurrentUserVote(holder, participantList.getJi(), participantList.getCi());

            Glide.with(mContext.getApplicationContext())
                    .load(participantList.getMl())
                    .placeholder(R.drawable.load)
                    .error(R.drawable.default_image2)
                    .placeholder(R.drawable.load)
                    .thumbnail(0.5f)
                    .centerCrop()
                    .into(holder.image);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    previousHolder = holder;


                    Intent i = new Intent(mContext, activity_view_media.class);
                    i.putExtra("imageLink", participantList.getMl());
                    i.putExtra("contestkey", participantList.getCi());
                    i.putExtra("joiningkey", participantList.getJi());
                    i.putExtra("view", "yes");


                    mContext.startActivity(i);


                }
            });
        } else {
            ifCurrentUserVote(holder, participantList.getJi(), participantList.getCi());

            holder.viewSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        Uri uri = Uri.parse(participantList.getMl());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mContext.startActivity(intent);

                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(mContext, "Invalid Link", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onClick: " + e.getMessage());
                    }


                }
            });

            holder.num.setText(String.valueOf(i + 1));


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query userquery = ref
                    .child(mContext.getString(R.string.dbname_users))
                    .child(participantList.getUi())
                    .child(mContext.getString(R.string.field_username));
            userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    holder.name.setText(snapshot.getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            holder.voteNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    reference.child(mContext.getString(R.string.dbname_contestlist))
                            .child(participantList.getCi())
                            .child(mContext.getString(R.string.field_total_voters_list))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Toast.makeText(mContext, "You have already voted for this contest.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        holder.voteNo.setVisibility(View.GONE);
                                        holder.voteYes.setVisibility(View.VISIBLE);
                                        addVote(holder, participantList.getJi(), participantList.getCi());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                }
            });
            holder.voteYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.voteNo.setVisibility(View.VISIBLE);
                    holder.voteYes.setVisibility(View.GONE);
                    removeVote(holder, participantList.getJi(), participantList.getCi());


                }
            });



        }
//


    }


    private void removeVote(ViewHolder holder, String joiningKey, String contestkey) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(mContext.getString(R.string.dbname_participantList))
                .child(contestkey)
                .child(joiningKey)
                .child(mContext.getString(R.string.voting_list))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeValue();

        reference.child(mContext.getString(R.string.dbname_contestlist))
                .child(contestkey)
                .child(mContext.getString(R.string.field_total_voters_list))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeValue();


    }

    private void addVote(ViewHolder holder, String joiningKey, String contestkey) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(mContext.getString(R.string.dbname_participantList))
                .child(contestkey)
                .child(joiningKey)
                .child(mContext.getString(R.string.voting_list))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(true);

        reference.child(mContext.getString(R.string.dbname_contestlist))
                .child(contestkey)
                .child(mContext.getString(R.string.field_total_voters_list))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(true);
    }


    public long getItemId(int position) {
        ParticipantList form = participantLists.get(position);
        return form.getJi().hashCode();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return participantLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView card;
        private ImageView image;
        TextView viewSub, num, name;
        private ImageView voteNo, voteYes;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            voteNo = itemView.findViewById(R.id.noVote);
            voteYes = itemView.findViewById(R.id.yesVote);
            image = itemView.findViewById(R.id.image);
            viewSub = itemView.findViewById(R.id.view);
            num = itemView.findViewById(R.id.num);
            name = itemView.findViewById(R.id.name);
            card = itemView.findViewById(R.id.card);



        }
    }

    private void ifCurrentUserVote(ViewHolder holder, String joiningKey, String contestKey) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(mContext.getString(R.string.dbname_participantList))
                .child(contestKey)
                .child(joiningKey)
                .child(mContext.getString(R.string.voting_list))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                        if (dataSnapshot2.exists()) {
                            if (isImage) {
                                holder.card.setCardBackgroundColor(Color.YELLOW);
                                previousHolder = holder;
                            } else {
                                holder.voteNo.setVisibility(View.GONE);
                                holder.voteYes.setVisibility(View.VISIBLE);
                            }


                        } else {
                            if (!isImage) {
                                holder.voteNo.setVisibility(View.VISIBLE);
                                holder.voteYes.setVisibility(View.GONE);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
