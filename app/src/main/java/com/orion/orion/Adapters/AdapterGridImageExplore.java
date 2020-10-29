package com.orion.orion.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.models.Photo;
import com.orion.orion.util.UniversalImageLoader;

import java.util.List;

public class AdapterGridImageExplore extends RecyclerView.Adapter<AdapterGridImageExplore.ViewHolder> {

    private Context mContext;
    private List<Photo> photos;
    private OnPostItemClickListner onPostItemClickListner;

    public AdapterGridImageExplore(Context mContext, List<Photo> photos, OnPostItemClickListner onPostItemClickListner) {
        this.mContext = mContext;
        this.photos = photos;
        this.onPostItemClickListner=onPostItemClickListner;

    }


    public interface OnPostItemClickListner{
        void onItemClick(int position);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.explore_item, parent, false);
        return new ViewHolder(view,onPostItemClickListner);
    }

    @Override
    public long getItemId(int position) {
        Photo photo = photos.get(position);
        return photo.getPhoto_id().hashCode();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        Photo photo = photos.get(i);

        if (photo.getType() != null)
            if (photo.getType().equals("video"))
                UniversalImageLoader.setImage(photo.getThumbnail(), holder.image, holder.progress, "");
            else
                UniversalImageLoader.setImage(photo.getImage_path(), holder.image, holder.progress, "");
        else
            UniversalImageLoader.setImage(photo.getImage_path(), holder.image, holder.progress, "");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(mContext.getString(R.string.dbname_users)).child(photo.getUser_id()).child(mContext.getString(R.string.field_username)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.username.setText("@" + snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        holder.itemView.setOnClickListener(v -> {
//            DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();
//            db1.child(mContext.getString(R.string.dbname_user_photos)).child(photo.getUser_id()).child(photo.getPhoto_id()).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    ArrayList<Comment> comments = new ArrayList<>();
//                    for (DataSnapshot dSnapshot : snapshot.child("comment").getChildren()) {
//                        Comment comment = new Comment();
//                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
//                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
//                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
//                        comments.add(comment);
//                    }
//                    Log.d(Constraints.TAG, "onDataChange: klj" + comments);
//                    Intent i1 = new Intent(mContext, ViewPostActivity.class);
//                    i1.putExtra("photo", photo);
//                    i1.putParcelableArrayListExtra("comments", comments);
//                    mContext.startActivity(i1);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        });
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
        private ImageView image,progress;
        OnPostItemClickListner onPostItemClickListner;

        public ViewHolder(@NonNull View itemView, OnPostItemClickListner onPostItemClickListner) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            progress = itemView.findViewById(R.id.progress);
            username = itemView.findViewById(R.id.username);
            this.onPostItemClickListner=onPostItemClickListner;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onPostItemClickListner.onItemClick(getAdapterPosition());
        }
    }
}