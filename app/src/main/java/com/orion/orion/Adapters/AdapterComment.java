package com.orion.orion.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.models.Comment;

import com.orion.orion.models.users;
import com.orion.orion.profile.profile;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.android.volley.VolleyLog.TAG;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.ViewHolder> {

    private Context mContext;
    private List<Comment> comments;

    public AdapterComment(Context mContext, List<Comment> comments) {
        this.mContext = mContext;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.layout_commets,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        Comment comment= comments.get(i);

        holder.comment.setText(comment.getC());
        holder.timestamp.setText(comment.getDc().substring(0,10));

        getUserdetail(comment.getUi(),holder.username,holder.profileimage);

        holder.username.setOnClickListener(v -> {
            Intent i1 = new Intent(mContext, profile.class);
            i1.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

            i1.putExtra(mContext.getString(R.string.intent_user), comment.getUi());
            mContext.startActivity(i1);
        });

        holder.profileimage.setOnClickListener(v -> {
            Intent i12 = new Intent(mContext, profile.class);
            i12.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

            i12.putExtra(mContext.getString(R.string.intent_user), comment.getUi());
            mContext.startActivity(i12);
        });

       }

    private void getUserdetail(String user_id, TextView username, CircleImageView profileimage) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_users))
                .child(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    username.setText((Objects.requireNonNull(dataSnapshot.getValue(users.class)).getU()));

                Glide.with(mContext)
                        .load((Objects.requireNonNull(dataSnapshot.getValue(users.class)).getPp()))
                        .placeholder(R.drawable.load)
                        .error(R.drawable.default_image2)
                        .placeholder(R.drawable.load)
                        .into(profileimage);



            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Query Cancelled");
            }
        });


    }
    @Override
    public long getItemId(int position) {
        Comment photo = comments.get(position);
        return photo.getDc().hashCode();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView comment,username,timestamp;
        CircleImageView profileimage;

        public ViewHolder(@NonNull View convertView) {
            super(convertView);

            comment= convertView.findViewById(R.id.addcomment);
            username= convertView.findViewById(R.id.comment_username);
         timestamp= convertView.findViewById(R.id.comment_time_posted);
           profileimage= convertView.findViewById((R.id.commet_profile_image));




        }
    }




}
