package com.orion.orion.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.contest.Contest_Evaluation.contest_evaluation_activity;
import com.orion.orion.contest.ViewContestDetails;
import com.orion.orion.models.ContestDetail;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.util.UniversalImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.android.volley.VolleyLog.TAG;

public class AdapterRankList extends RecyclerView.Adapter<AdapterRankList.ViewHolder> {
    private String mAppend = "";

    private Context mContext;
    private List<ParticipantList> participantLists;

    public AdapterRankList(Context mContext, List<ParticipantList> participantLists) {
        this.mContext = mContext;
        this.participantLists = participantLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.rank_list_item, parent, false);
        return new AdapterRankList.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        ParticipantList mparticipantList = participantLists.get(i);

        holder.totalScore.setText(String.valueOf(mparticipantList.getTotalScore()));
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
                        UniversalImageLoader.setImage(user.getProfile_photo(), profile, null, mAppend);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    public long getItemId(int position) {
        ParticipantList form = participantLists.get(position);
        return form.getJoiningKey().hashCode();
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
