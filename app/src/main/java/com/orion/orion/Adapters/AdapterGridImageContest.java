package com.orion.orion.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.ViewPostActivity;
import com.orion.orion.contest.Contest_Evaluation.activity_view_media;
import com.orion.orion.contest.public_voting_media;
import com.orion.orion.models.Comment;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.Photo;
import com.orion.orion.util.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

public class AdapterGridImageContest extends RecyclerView.Adapter<AdapterGridImageContest.ViewHolder> {



    private Context mContext;
    private List<ParticipantList> participantLists;

    public AdapterGridImageContest(Context mContext, List<ParticipantList> participantLists) {
        this.mContext = mContext;
        this.participantLists = participantLists;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.grid_image_item,parent,false);
        return new AdapterGridImageContest.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        ParticipantList participantList= participantLists.get(i);
        UniversalImageLoader.setImage(participantList.getMediaLink(),holder.image,null,"");
//

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent i = new Intent(mContext, activity_view_media.class);
                i.putExtra("imageLink", participantList.getMediaLink());
                i.putExtra("contestkey", participantList.getContestkey());
                i.putExtra("joiningkey", participantList.getJoiningKey());
                i.putExtra("view", "yes");


                mContext.startActivity(i);



            }
        });

    }

    @Override
    public int getItemCount() {
        return participantLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            image = itemView.findViewById(R.id.image);



        }
    }


}
