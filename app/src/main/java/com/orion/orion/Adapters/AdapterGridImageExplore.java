package com.orion.orion.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.ViewPostActivity;
import com.orion.orion.models.Comment;
import com.orion.orion.models.Photo;
import com.orion.orion.util.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

public class AdapterGridImageExplore extends RecyclerView.Adapter<AdapterGridImageExplore.ViewHolder> {


    private Context mContext;
    private List<Photo> photos;

    public AdapterGridImageExplore(Context mContext, List<Photo> photos) {
        this.mContext = mContext;
        this.photos = photos;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.explore_item, parent, false);
        return new AdapterGridImageExplore.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        Photo photo = photos.get(i);
        YoYo.with(Techniques.Landing).duration(1000).playOn(holder.image);
        UniversalImageLoader.setImage(photo.getImage_path(), holder.image, null, "");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(mContext.getString(R.string.dbname_users)).child(photo.getUser_id()).child(mContext.getString(R.string.field_username)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.username.setText("@" + snapshot.getValue().toString());
                YoYo.with(Techniques.Landing).duration(1000).playOn(holder.username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();
                db1.child(mContext.getString(R.string.dbname_user_photos))
                        .child(photo.getUser_id())
                        .child(photo.getPhoto_id())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<Comment> comments = new ArrayList<>();

                                for (DataSnapshot dSnapshot : snapshot.child("comment").getChildren()) {
                                    Comment comment = new Comment();
                                    comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                    comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                    comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                    comments.add(comment);


                                }

                                Log.d(Constraints.TAG, "onDataChange: klj" + comments);

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
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            image = itemView.findViewById(R.id.image);
            username = itemView.findViewById(R.id.username);


        }
    }


}
