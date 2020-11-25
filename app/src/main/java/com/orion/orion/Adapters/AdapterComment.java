package com.orion.orion.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterComment extends RecyclerView.Adapter<AdapterComment.ViewHolder> {

    private final Context mContext;
    private final List<Comment> comments;
    private final List<String> commentId;
    private final String mPhotoId;
    private final String mUserId;

    public AdapterComment(Context mContext, List<Comment> comments, List<String> commentId, String mPhotoId, String mUserId) {
        this.mContext = mContext;
        this.comments = comments;
        this.commentId = commentId;
        this.mPhotoId = mPhotoId;
        this.mUserId = mUserId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_commets, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        Comment comment = comments.get(i);
        holder.comment.setText(comment.getC());
        holder.timestamp.setText(comment.getDc().substring(0, 10));
        getUserdetail(comment.getUi(), holder.username, holder.profileimage);
        PopupMenu popup = new PopupMenu(mContext, holder.editButton);
        popup.getMenuInflater().inflate(R.menu.post_comment, popup.getMenu());
        if (!comment.getUi().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            holder.editButton.setVisibility(View.GONE);
        else {
            holder.editButton.setOnClickListener(v -> popup.show());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Delete Comment")) {
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                    myRef.child(mContext.getString(R.string.dbname_user_photos))
                            .child(mUserId)
                            .child(mPhotoId)
                            .child(mContext.getString(R.string.field_comment))
                            .child(commentId.get(i))
                            .removeValue()
                            .addOnCompleteListener(task -> {
                                if (i == comments.size() || i == commentId.size()) {
                                    Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                                } else {
                                    comments.remove(i);
                                    commentId.remove(i);
                                    Query query = myRef.child(mContext.getString(R.string.dbname_users))
                                            .child(mContext.getString(R.string.field_Notifications));
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                                                if (singleSnapshot.child(mContext.getString(R.string.field_notification_message)).equals(mContext.getString(R.string.chat_message))
                                                        && singleSnapshot.child("pId").equals(mPhotoId)) {
                                                    myRef.child(mContext.getString(R.string.dbname_users))
                                                            .child(mContext.getString(R.string.field_Notifications))
                                                            .child(singleSnapshot.getKey())
                                                            .removeValue()
                                                            .addOnSuccessListener(aVoid -> Toast.makeText(mContext, "Deleted Successfully ", Toast.LENGTH_SHORT).show())
                                                            .addOnFailureListener(e -> Toast.makeText(mContext, "Unsuccessful ", Toast.LENGTH_SHORT).show());
                                                    break;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(mContext, "Unsuccessful", Toast.LENGTH_SHORT).show());

                }
                return true;
            });
        }
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
                Glide.with(mContext.getApplicationContext())
                        .load((Objects.requireNonNull(dataSnapshot.getValue(users.class)).getPp()))
                        .placeholder(R.drawable.load)
                        .error(R.drawable.default_image2)
                        .placeholder(R.drawable.load)
                        .into(profileimage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("adkjasdada", "Query Cancelled");
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
        TextView comment, username, timestamp;
        CircleImageView profileimage;
        ImageButton editButton;

        public ViewHolder(@NonNull View convertView) {
            super(convertView);
            comment = convertView.findViewById(R.id.addcomment);
            username = convertView.findViewById(R.id.comment_username);
            timestamp = convertView.findViewById(R.id.comment_time_posted);
            profileimage = convertView.findViewById((R.id.commet_profile_image));
            editButton = convertView.findViewById(R.id.edit);
        }
    }
}
