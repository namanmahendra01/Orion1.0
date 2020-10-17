package com.orion.orion.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    boolean isImage;
    private List<ParticipantList> participantLists;

    public AdapterGridImageContest(Context mContext, List<ParticipantList> participantLists, boolean isImage) {
        this.mContext = mContext;
        this.participantLists = participantLists;
        this.isImage = isImage;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (isImage){
           view = LayoutInflater.from(mContext).inflate(R.layout.grid_image_item,parent,false);

        }else {
            view = LayoutInflater.from(mContext).inflate(R.layout.public_vote_item,parent,false);

        }
        return new AdapterGridImageContest.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        ParticipantList participantList= participantLists.get(i);
        if (isImage) {
            UniversalImageLoader.setImage(participantList.getMediaLink(), holder.image, null, "");
            holder.image.setOnClickListener(new View.OnClickListener() {
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
        }else{
            holder.viewSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Uri uri = Uri.parse(participantList.getMediaLink());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(intent);



                }
            });

            holder.num.setText(String.valueOf(i+1));


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query userquery = ref
                    .child(mContext.getString(R.string.dbname_users))
                    .child(participantList.getUserid())
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

        }
//




    }

    @Override
    public int getItemCount() {
        return participantLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        TextView viewSub,num,name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            image = itemView.findViewById(R.id.image);
            viewSub = itemView.findViewById(R.id.view);
            num = itemView.findViewById(R.id.num);
            name = itemView.findViewById(R.id.name);



        }
    }


}
