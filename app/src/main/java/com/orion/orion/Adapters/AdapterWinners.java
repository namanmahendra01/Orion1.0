package com.orion.orion.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.contest.Contest_Evaluation.activity_view_media;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.util.SNTPClient;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.android.volley.VolleyLog.TAG;

public class AdapterWinners extends RecyclerView.Adapter<AdapterWinners.ViewHolder> {
    private String mAppend = "";

    private Context mContext;
    private List<ParticipantList> participantLists;

    public AdapterWinners(Context mContext, List<ParticipantList> participantLists) {
        this.mContext = mContext;
        this.participantLists = participantLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_winner_item, parent, false);
        return new AdapterWinners.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        ParticipantList mparticipantList = participantLists.get(i);

                 holder.rankNum.setText(String.valueOf(i+1));
                 getParticipantDetails(mparticipantList.getUi(), holder.username, holder.profile,holder.displayname);

                 holder.media.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         boolean ok=mparticipantList.getMl().length()>23;
                         boolean ifNull=mparticipantList.getMl() == null || mparticipantList.getMl().equals("");
                         if (ifNull){
                             Toast.makeText(mContext, "Invalid Link", Toast.LENGTH_SHORT).show();

                         }else if (ok) {
                             if (mparticipantList.getMl().substring(8, 23).equals("firebasestorage")) {
                                 Intent i = new Intent(mContext.getApplicationContext(), activity_view_media.class);
                                 i.putExtra("imageLink", mparticipantList.getMl());
                                 i.putExtra("view", "No");

                                 mContext.startActivity(i);
                             } else {

                                 try{
                                     Uri uri = Uri.parse(mparticipantList.getMl());
                                     Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                     mContext.startActivity(intent);

                                 }catch (ActivityNotFoundException e){
                                     Toast.makeText(mContext, "Invalid Link", Toast.LENGTH_SHORT).show();
                                     Log.e(SNTPClient.TAG, "onClick: "+ e.getMessage());
                                 }
                             }
                         }else {

                             try{
                                 Uri uri = Uri.parse(mparticipantList.getMl());
                                 Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                 mContext.startActivity(intent);

                             }catch (ActivityNotFoundException e){
                                 Toast.makeText(mContext, "Invalid Link", Toast.LENGTH_SHORT).show();
                                 Log.e(SNTPClient.TAG, "onClick: "+ e.getMessage());
                             }
                         }

                     }
                 });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, profile.class);
                i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                i.putExtra(mContext.getString(R.string.intent_user),mparticipantList.getUi());
                mContext.startActivity(i);
            }
        });

        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, profile.class);
                i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                i.putExtra(mContext.getString(R.string.intent_user),mparticipantList.getUi());
                mContext.startActivity(i);
            }
        });

        holder.displayname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, profile.class);
                i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                i.putExtra(mContext.getString(R.string.intent_user), mparticipantList.getUi());
                mContext.startActivity(i);
            }
        });




    }


    private void getParticipantDetails(String userid, TextView username, CircleImageView profile, TextView displayname) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(mContext.getString(R.string.dbname_users))
                .child(userid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        users user = dataSnapshot.getValue(users.class);
                        username.setText(user.getU());
                        displayname.setText(user.getDn());
                        Glide.with(mContext)
                                .load(user.getPp())
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
    public long getItemId(int position) {
        ParticipantList form = participantLists.get(position);
        return form.getJi().hashCode();
    }
    @Override
    public int getItemCount() {
        return participantLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username,displayname,rankNum;
        private CircleImageView profile;
        private TextView media;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.usernameW1);
            profile = itemView.findViewById(R.id.profileW1);
            rankNum = itemView.findViewById(R.id.ranknum);

            media = itemView.findViewById(R.id.mediaW1);
            displayname = itemView.findViewById(R.id.displayW1);


        }
    }


}
