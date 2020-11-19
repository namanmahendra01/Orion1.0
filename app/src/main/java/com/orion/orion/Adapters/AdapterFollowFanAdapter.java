package com.orion.orion.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.models.ItemFollow;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.SNTPClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFollowFanAdapter extends RecyclerView.Adapter<AdapterFollowFanAdapter.ViewHolder> {

    private final Context mContext;
    private final ArrayList<ItemFollow> mLists;
    private final FirebaseMethods mFirebaseMethods;
    private final DatabaseReference myRef;
    private List<String> commentId;
    private String mPhotoId;
    private String mUserId;


    public AdapterFollowFanAdapter(Context mcontext, ArrayList<ItemFollow> mLists) {
        this.mContext = mcontext;
        this.mLists = mLists;
        mFirebaseMethods = new FirebaseMethods(mcontext);
        myRef = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_follow_fan, parent, false);
        return new AdapterFollowFanAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterFollowFanAdapter.ViewHolder holder, int position) {
        ItemFollow itemFollow = mLists.get(position);
        final boolean[] notify = {false};
        Glide.with(holder.itemView.getContext())
                .load(itemFollow.getProfileUrl())
                .placeholder(R.drawable.load)
                .error(R.drawable.default_image2)
                .placeholder(R.drawable.load)
                .thumbnail(0.2f)
                .into(holder.profileimage);
        holder.username.setText(itemFollow.getUsername());
        holder.displayName.setText(itemFollow.getDisplay_name());
        if (itemFollow.isFollowing()) holder.followButton.setText("Unfollow");
        else holder.followButton.setText("Follow");
        if (itemFollow.isFan()) {
            holder.removeButton.setVisibility(View.VISIBLE);
            holder.removeButton.setOnClickListener(v -> {
                itemFollow.setFan(false);
                myRef.child(mContext.getString(R.string.dbname_follower)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(itemFollow.getUserId()).removeValue();
                myRef.child(mContext.getString(R.string.dbname_following)).child(itemFollow.getUserId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                myRef.child(mContext.getString(R.string.dbname_users)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(mContext.getString(R.string.changedFollowers)).setValue("true");
                myRef.child(mContext.getString(R.string.dbname_users)).child(itemFollow.getUserId()).child(mContext.getString(R.string.field_unfollowed_Me))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                holder.removeButton.setVisibility(View.GONE);
            });
        } else holder.removeButton.setVisibility(View.GONE);
        holder.username.setText(itemFollow.getUsername());
        holder.followButton.setOnClickListener(v -> {
            //unfollowing
            if (itemFollow.isFollowing()) {
                myRef.child(mContext.getString(R.string.dbname_following)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(itemFollow.getUserId()).removeValue();
                myRef.child(mContext.getString(R.string.dbname_follower)).child(itemFollow.getUserId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                myRef.child(mContext.getString(R.string.dbname_users)).child(itemFollow.getUserId()).child(mContext.getString(R.string.changedFollowers)).setValue("true");
                holder.followButton.setText("Follow");
                itemFollow.setFollowing(false);
            }
            //follow
            else {
                notify[0] = true;
                myRef.child(mContext.getString(R.string.dbname_following)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(itemFollow.getUserId()).setValue(true);
                myRef.child(mContext.getString(R.string.dbname_follower)).child(itemFollow.getUserId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                myRef.child(mContext.getString(R.string.dbname_users)).child(itemFollow.getUserId()).child(mContext.getString(R.string.changedFollowers)).setValue("true");
                holder.followButton.setText("Unfollow");
                itemFollow.setFollowing(true);
                final DatabaseReference data = myRef.child(mContext.getString(R.string.dbname_users)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                data.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        users user = dataSnapshot.getValue(users.class);
                        if (notify[0])
                            mFirebaseMethods.sendNotification(itemFollow.getUserId(), user.getU(), "becomes your FAN!", "Fan");
                        notify[0] = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                addToHisNotification(itemFollow.getUserId());
            }
        });
        holder.profileimage.setOnClickListener(v -> {
            Intent i = new Intent(mContext, profile.class);
            i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));
            i.putExtra(mContext.getString(R.string.intent_user), itemFollow.getUserId());
            mContext.startActivity(i);
        });
    }

    private void addToHisNotification(String hisUid) {
        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Colombo"), new SNTPClient.Listener() {
            @Override
            public void onTimeReceived(String rawDate) {
                // rawDate -> 2019-11-05T17:51:01+0530
                @SuppressLint("SimpleDateFormat") java.text.DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date date = null;
                try {
                    date = formatter.parse(rawDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String timestamp = String.valueOf(date.getTime());
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("pId", "false");
                hashMap.put(mContext.getString(R.string.field_timestamp), timestamp);
                hashMap.put("pUid", hisUid);
                hashMap.put(mContext.getString(R.string.field_notification_message), "becomes your FAN!");
                hashMap.put(mContext.getString(R.string.field_if_seen), "false");
                hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_users));
                ref.child(hisUid).child(mContext.getString(R.string.field_Notifications)).child(timestamp).setValue(hashMap)
                        .addOnSuccessListener(aVoid -> {
                        })
                        .addOnFailureListener(e -> {
                        });
                Log.e(SNTPClient.TAG, rawDate);
            }

            @Override
            public void onError(Exception ex) {
                Log.e(SNTPClient.TAG, ex.getMessage());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileimage;
        TextView username;
        TextView displayName;
        Button followButton;
        ImageView removeButton;

        public ViewHolder(@NonNull View convertView) {
            super(convertView);
            profileimage = convertView.findViewById((R.id.profile_image));
            username = convertView.findViewById((R.id.username));
            displayName = convertView.findViewById((R.id.display_name));
            followButton = convertView.findViewById((R.id.followButton));
            removeButton = convertView.findViewById((R.id.removeButton));
        }
    }
}
