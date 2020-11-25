package com.orion.orion.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.ViewPostActivity;
import com.orion.orion.models.Comment;
import com.orion.orion.models.Photo;

import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class AdapterGridImage extends RecyclerView.Adapter<AdapterGridImage.ViewHolder> {


    private Context mContext;
    private List<Photo> photos;


    public AdapterGridImage(Context mContext, List<Photo> photos) {
        this.mContext = mContext;
        this.photos = photos;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_image_item, parent, false);
        return new AdapterGridImage.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        int width = display.getWidth(); // ((display.getWidth()*20)/100)
        CardView.LayoutParams parms = new CardView.LayoutParams(width / 3, width / 3);
        holder.image.setLayoutParams(parms);

        Photo photo = photos.get(i);
        if (photo.getTy().equals("photo")) {
            Glide.with(holder.itemView.getContext().getApplicationContext())
                    .load(photo.getIp())
                    .placeholder(R.drawable.load)
                    .error(R.drawable.default_image2)
                    .centerCrop()
                    .placeholder(R.drawable.load)
                    .thumbnail(0.5f)
                    .into(holder.image);
        } else {
            Glide.with(holder.itemView.getContext().getApplicationContext())
                    .load(photo.getT())
                    .placeholder(R.drawable.load)
                    .error(R.drawable.default_image2)
                    .centerCrop()
                    .placeholder(R.drawable.load)
                    .thumbnail(0.5f)
                    .into(holder.image);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();
                db1.child(mContext.getString(R.string.dbname_user_photos))
                        .child(photo.getUi())
                        .child(photo.getPi())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<Comment> comments = new ArrayList<>();

                                for (DataSnapshot dSnapshot : snapshot.child(mContext.getString(R.string.field_comment)).getChildren()) {
                                    Comment comment = new Comment();
                                    comment.setUi(dSnapshot.getValue(Comment.class).getUi());
                                    comment.setC(dSnapshot.getValue(Comment.class).getC());
                                    comment.setDc(dSnapshot.getValue(Comment.class).getDc());
                                    comments.add(comment);

                                }

                                Intent i = new Intent(mContext, ViewPostActivity.class);
                                i.putExtra("photo", photo);
                                i.putParcelableArrayListExtra("comments", comments);

                                mContext.startActivity(i);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public long getItemId(int position) {
        Photo photo = photos.get(position);
        return photo.getPi().hashCode();
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image, progress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            image = itemView.findViewById(R.id.image);
            progress = itemView.findViewById(R.id.progress);


        }
    }


}
