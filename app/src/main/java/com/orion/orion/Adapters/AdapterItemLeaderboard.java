package com.orion.orion.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.LogDescriptor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.explore.Explore;
import com.orion.orion.models.ItemLeaderboard;
import com.orion.orion.models.Leaderboard;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.util.UniversalImageLoader;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class AdapterItemLeaderboard extends RecyclerView.Adapter<AdapterItemLeaderboard.ViewHolder>{

    private ArrayList<ItemLeaderboard> mList;
    Context mContext;
    public AdapterItemLeaderboard(ArrayList<ItemLeaderboard> mList, Context mContext) {
        this.mList = mList;
        this.mContext=mContext;

    }

    @NonNull
    @Override
    public AdapterItemLeaderboard.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterItemLeaderboard.ViewHolder holder, int position) {
        ItemLeaderboard currentItem = mList.get(position);

        holder.Position.setText(Integer.toString(position+1));
        holder.PositionName.setText(currentItem.getPostionName());
        holder.PositionParameter.setText(currentItem.getPostionParameter());
        Log.i("sakdada", "onBindViewHolder: "+currentItem.getPostionProfile());
        UniversalImageLoader.setImage(currentItem.getPostionProfile(),holder.PositionProfile,null,"");
        if(position==0 || position==1 || position==2)
            holder.PositionCard.setBackgroundColor(Color.parseColor("#E5E6EB"));
        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: "+currentItem.getUserID());

            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            Query query=db.child(mContext.getString(R.string.dbname_users)).child(currentItem.getUserID());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    users user=snapshot.getValue(users.class);
                    Intent i = new Intent(mContext, profile.class);
                    i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));
                    i.putExtra(mContext.getString(R.string.intent_user), user);
                    mContext.startActivity(i);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView Position;
        public TextView PositionName;
        public TextView PositionParameter;
        public ImageView PositionProfile;
        public LinearLayout PositionCard;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            Position=itemView.findViewById(R.id.position);
            PositionName=itemView.findViewById(R.id.positionName);
            PositionParameter=itemView.findViewById(R.id.positionPararmeter);
            PositionProfile=itemView.findViewById(R.id.positionProfile);
            PositionCard=itemView.findViewById(R.id.leaderboardCard);



        }
    }
}
