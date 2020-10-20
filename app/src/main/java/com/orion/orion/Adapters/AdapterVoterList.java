package com.orion.orion.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import com.orion.orion.models.JoinForm;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.util.UniversalImageLoader;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.android.volley.VolleyLog.TAG;

public class AdapterVoterList extends RecyclerView.Adapter<AdapterVoterList.ViewHolder> {
    private String mAppend = "";

    private Context mContext;
    private List<String> votingLists;
    String name1="",profilelink="",username1="",idLink="",mediaLink="",comment="",college1="";

    public AdapterVoterList(Context mContext, List<String> votingLists) {
        this.mContext = mContext;
        this.votingLists = votingLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.contest_participant_item,parent,false);
        return new AdapterVoterList.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        String votingList= votingLists.get(i);
        holder.time.setVisibility(View.GONE);

        getparticipantDetails(votingList,holder.username,holder.displayname,holder.profile);


        DatabaseReference ref =FirebaseDatabase.getInstance().getReference();

        Query userquery = ref
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(votingList);
        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    holder.user = singleSnapshot.getValue(users.class);

                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Query Cancelled");
            }
        });



    }

    public long getItemId(int position) {

        return votingLists.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return votingLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username, displayname, time;
        users user = new users();
        private DatabaseReference mReference;

        private CircleImageView profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mReference = FirebaseDatabase.getInstance().getReference();


            profile=(CircleImageView)itemView.findViewById(R.id.profilePartCv);
            username=itemView.findViewById(R.id.username);
            displayname=itemView.findViewById(R.id.displayname);
            time=itemView.findViewById(R.id.timeStamp);

        }
    }


    private void getparticipantDetails(String userid, TextView username, TextView displayname, CircleImageView profile) {
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference();
        ref.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        users user =dataSnapshot.getValue(users.class);
                        Log.d(TAG, "onDataChange: "+ user.getDisplay_name());
                        name1=user.getDisplay_name();
                        username1=user.getUsername();
                        profilelink=user.getProfile_photo();

                        username.setText(user.getUsername());
                        displayname.setText(user.getDisplay_name());
                        UniversalImageLoader.setImage(profilelink,profile,null,mAppend);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}


