package com.orion.orion.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orion.orion.R;
import com.orion.orion.models.ItemLeaderboard;
import com.orion.orion.profile.profile;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterLeaderboard extends RecyclerView.Adapter<AdapterLeaderboard.ViewHolder> {

    private final ArrayList<ItemLeaderboard> mList;
    Context mContext;

    public AdapterLeaderboard(ArrayList<ItemLeaderboard> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AdapterLeaderboard.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1)
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_leaderboard, parent, false));
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false));
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 1 || position == 2) return 1;
        else return 2;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLeaderboard.ViewHolder holder, int position) {
        ItemLeaderboard currentItem = mList.get(position);

        String rank = Integer.toString(position + 1);
        holder.Position.setText(rank);
        holder.PositionName.setText(currentItem.getPostionName());
        holder.PositionParameter.setText(currentItem.getPostionParameter());
        Glide.with(holder.itemView.getContext().getApplicationContext())
                .load(currentItem.getPostionProfile())
                .placeholder(R.drawable.load)
                .error(R.drawable.default_image2)
                .placeholder(R.drawable.load)
                .thumbnail(0.2f)
                .into(holder.PositionProfile);
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(mContext, profile.class);
            i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));
            i.putExtra(mContext.getString(R.string.intent_user), currentItem.getUserID());
            mContext.startActivity(i);
        });
    }
        public long getItemId(int position) {
        ItemLeaderboard board = mList.get(position);
        return board.getUserID().hashCode();
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
            Position = itemView.findViewById(R.id.position);
            PositionName = itemView.findViewById(R.id.positionName);
            PositionParameter = itemView.findViewById(R.id.positionPararmeter);
            PositionProfile = itemView.findViewById(R.id.positionProfile);
            PositionCard = itemView.findViewById(R.id.leaderboardCard);
        }
    }
}
