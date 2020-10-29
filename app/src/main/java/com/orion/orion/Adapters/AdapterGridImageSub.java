package com.orion.orion.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.orion.R;
import com.orion.orion.contest.Contest_Evaluation.activity_view_media;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.util.UniversalImageLoader;

import java.util.List;

import static android.content.ContentValues.TAG;

public class AdapterGridImageSub extends RecyclerView.Adapter<AdapterGridImageSub.ViewHolder> {



    private Context mContext;
    private List<ParticipantList> participantLists;

    public AdapterGridImageSub(Context mContext, List<ParticipantList> participantLists) {
        this.mContext = mContext;
        this.participantLists = participantLists;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.grid_image_item,parent,false);
        return new AdapterGridImageSub.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        ParticipantList participantList= participantLists.get(i);
        Log.d(TAG, "onBindViewHolder: sdf"+participantList.getMediaLink());
        UniversalImageLoader.setImage(participantList.getMediaLink(),holder.image,holder.progress,"");
//

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent i = new Intent(mContext, activity_view_media.class);
                i.putExtra("imageLink", participantList.getMediaLink());
                i.putExtra("contestkey", participantList.getContestkey());
                i.putExtra("joiningkey", participantList.getJoiningKey());
                i.putExtra("view", "No");


                mContext.startActivity(i);



            }
        });

    }
    @Override
    public int getItemViewType(int position) {
        return position;
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

        private ImageView image,progress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            image = itemView.findViewById(R.id.image);
            progress = itemView.findViewById(R.id.progress);




        }
    }


}
