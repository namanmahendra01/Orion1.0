package com.orion.orion.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.orion.orion.models.Photo;

import java.util.List;

public class AdapterGridImageExplore extends RecyclerView.Adapter<AdapterGridImageExplore.ViewHolder> {

    private Context mContext;
    private List<Photo> photos;
    private OnPostItemClickListner onPostItemClickListner;


    public AdapterGridImageExplore(Context mContext, List<Photo> photos, OnPostItemClickListner onPostItemClickListner) {
        this.mContext = mContext;
        this.photos = photos;
        this.onPostItemClickListner = onPostItemClickListner;
    }


    public interface OnPostItemClickListner {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.explore_item, parent, false);
        return new ViewHolder(view, onPostItemClickListner);
    }

    @Override
    public long getItemId(int position) {
        Photo photo = photos.get(position);
        return photo.getPi().hashCode();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        Photo photo = photos.get(i);
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        int width = display.getWidth(); // ((display.getWidth()*20)/100)
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width / 2, width / 2);
        holder.image.setLayoutParams(parms);

        if (photo.getTy() != null)
            if (photo.getTy().equals("video"))
                Glide.with(holder.itemView.getContext())
                        .load(photo.getT())
                        .placeholder(R.drawable.load)
                        .error(R.drawable.default_image2)
                        .centerCrop()
                        .placeholder(R.drawable.load)
                        .thumbnail(0.5f)
                        .into(holder.image);
            else
                Glide.with(holder.itemView.getContext())
                        .load(photo.getIp())
                        .placeholder(R.drawable.load)
                        .error(R.drawable.default_image2)
                        .centerCrop()
                        .placeholder(R.drawable.load)
                        .thumbnail(0.5f)
                        .into(holder.image);
        else
            Glide.with(holder.itemView.getContext())
                    .load(photo.getIp())
                    .placeholder(R.drawable.load)
                    .error(R.drawable.default_image2)
                    .centerCrop()
                    .placeholder(R.drawable.load)
                    .thumbnail(0.5f)
                    .into(holder.image);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(mContext.getString(R.string.dbname_users))
                .child(photo.getUi())
                .child(mContext.getString(R.string.field_username))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.username.setText("@" + snapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView username;
        private ImageView image, progress;
        OnPostItemClickListner onPostItemClickListner;

        public ViewHolder(@NonNull View itemView, OnPostItemClickListner onPostItemClickListner) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            progress = itemView.findViewById(R.id.progress);
            username = itemView.findViewById(R.id.username);
            this.onPostItemClickListner = onPostItemClickListner;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onPostItemClickListner.onItemClick(getAdapterPosition());
        }
    }
}