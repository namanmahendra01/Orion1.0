package com.orion.orion.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRankListFull extends RecyclerView.Adapter<AdapterRankListFull.ViewHolder> {
    private String mAppend = "";

    private Context mContext;
    private List<ParticipantList> participantLists;

    public AdapterRankListFull(Context mContext, List<ParticipantList> participantLists) {
        this.mContext = mContext;
        this.participantLists = participantLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.rank_list_item, parent, false);
        return new AdapterRankListFull.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        ParticipantList mparticipantList = participantLists.get(i);

        holder.totalScore.setText(String.valueOf(mparticipantList.getTotalScore()));
        if (holder.rankNum.getText().equals(""))
        holder.rankNum.setText(String.valueOf(i + 1));

        getParticipantDetails(mparticipantList.getUserid(), holder.username, holder.profile);


        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, profile.class);
                i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                i.putExtra(mContext.getString(R.string.intent_user),mparticipantList.getUserid());
                mContext.startActivity(i);
            }
        });



    }

    private void getParticipantDetails(String userid, TextView username, CircleImageView profile) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(mContext.getString(R.string.dbname_users))
                .child(userid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        users user = dataSnapshot.getValue(users.class);
                        username.setText(user.getUsername());
                        Glide.with(mContext)
                                .load(user.getProfile_photo())
                                .placeholder(R.drawable.load)
                                .error(R.drawable.default_image2)
                                .placeholder(R.drawable.load)
                                .thumbnail(0.5f)
                                .into(profile);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public long getItemId(int position) {
        ParticipantList form = participantLists.get(position);
        return form.getTimestamp().hashCode();
    }
    @Override
    public int getItemCount() {
        return participantLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username, totalScore, rankNum;
        private CircleImageView profile;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.usernameTv);
            totalScore = itemView.findViewById(R.id.total);
            profile = itemView.findViewById(R.id.profileRcv);
            rankNum = itemView.findViewById(R.id.rankNum);


        }
    }


}
