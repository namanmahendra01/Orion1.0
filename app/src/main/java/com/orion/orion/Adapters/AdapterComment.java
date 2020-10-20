package com.orion.orion.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orion.orion.R;
import com.orion.orion.contest.ViewContestDetails;
import com.orion.orion.contest.joined.JoiningForm;
import com.orion.orion.contest.joined.joined_contest_overview_activity;
import com.orion.orion.models.Comment;
import com.orion.orion.models.ContestDetail;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.JoinForm;

import com.orion.orion.models.Photo;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.util.UniversalImageLoader;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.android.volley.VolleyLog.TAG;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.ViewHolder> {
    private String mAppend = "";

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
        return new AdapterComment.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        Comment comment= comments.get(i);

        holder.comment.setText(comment.getComment());
        holder.timestamp.setText(comment.getDate_created().substring(0,10));

        getUserdetail(comment.getUser_id(),holder.username,holder.profileimage);

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, profile.class);
                i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                i.putExtra(mContext.getString(R.string.intent_user), holder.user);
                mContext.startActivity(i);
            }
        });

        holder.profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, profile.class);
                i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                i.putExtra(mContext.getString(R.string.intent_user), holder.user);
                mContext.startActivity(i);
            }
        });

        DatabaseReference ref =FirebaseDatabase.getInstance().getReference();

        Query userquery = ref
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(comment.getUser_id());
        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    holder.user = singleSnapshot.getValue(users.class);

                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Query Cancelled");
            }
        });



    }

    private void getUserdetail(String user_id, TextView username, CircleImageView profileimage) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    username.setText((singleSnapshot.getValue(users.class).getUsername()));

              UniversalImageLoader.setImage(singleSnapshot.getValue(users.class).getProfile_photo(),profileimage,null,"");

                }


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
        return photo.getDate_created().hashCode();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView comment,username,timestamp;
        CircleImageView profileimage;
        users user = new users();

        public ViewHolder(@NonNull View convertView) {
            super(convertView);

            comment=(TextView)convertView.findViewById(R.id.addcomment);
            username=(TextView)convertView.findViewById(R.id.comment_username);
         timestamp=(TextView)convertView.findViewById(R.id.comment_time_posted);
           profileimage=(CircleImageView)convertView.findViewById((R.id.commet_profile_image));




        }
    }




}
