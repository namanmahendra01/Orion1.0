package com.orion.orion.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.home.ViewPromoted;
import com.orion.orion.models.Chat;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.Promote;
import com.orion.orion.models.users;
import com.orion.orion.util.UniversalImageLoader;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AdapterPromote extends RecyclerView.Adapter<AdapterPromote.MyHolder> {

    Context context;
    List<String> promoteList;
    FirebaseUser fUser;

    public AdapterPromote(Context context, List<String> promoteList) {
        this.context = context;
        this.promoteList = promoteList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(context).inflate(R.layout.promote_item, viewGroup, false);
            return new MyHolder(view);



    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, final int i) {
//        get data
        String userid= promoteList.get(i);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(context.getString(R.string.dbname_promote))
                .child(userid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                        for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                            myHolder.promote = dataSnapshot1.getValue(Promote.class);

                            Long timeEnd = Long.parseLong(myHolder.promote.getTimeEnd());
                            seenStory(myHolder, myHolder.promote.getPromoterId(), myHolder.promote.getStoryid(), timeEnd);
                            getUserInfo(myHolder.promote.getPromoterId(), myHolder.story, myHolder.storySeen, myHolder.username);

                        }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ViewPromoted.class);
                i.putExtra("userid",userid);
                context.startActivity(i);

            }
        });



    }

    private void getUserInfo(String userid, CircleImageView storyseen, CircleImageView story, TextView username) {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(context.getString(R.string.dbname_users))
                .child(userid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        users user=dataSnapshot.getValue(users.class);
                        UniversalImageLoader.setImage(user.getProfile_photo(),story,null,"");
                        UniversalImageLoader.setImage(user.getProfile_photo(),storyseen,null,"");
                        username.setText(user.getUsername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void seenStory(MyHolder holder, String userid, String storykey, Long timeEnd){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
db.child(context.getString(R.string.dbname_promote))
        .child(userid)
        .addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        long l = dataSnapshot.getChildrenCount();
        int t=0;
        for (DataSnapshot snapshot:dataSnapshot.getChildren()){

            if ((snapshot.child("views").child(FirebaseAuth.getInstance()
            .getCurrentUser().getUid()).exists())){
                t++;
                Log.d(TAG, "onDataChange: ss"+t);
            }
        }if (t==l){
            Log.d(TAG, "onDataChange: ss2"+t);
            holder.storySeen.setVisibility(View.VISIBLE);
            holder.story.setVisibility(View.GONE);

        }else {
            Log.d(TAG, "onDataChange: ss4"+t);
            holder.storySeen.setVisibility(View.GONE);
            holder.story.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});


    }
    public long getItemId(int position) {

        return promoteList.get(position).hashCode();
    }
    @Override
    public int getItemCount() {
        return promoteList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView username;
        CircleImageView storySeen,story;
        Promote promote;

        public MyHolder(@NonNull View itemView) {


            super(itemView);

            username=itemView.findViewById(R.id.story_username);
            storySeen=itemView.findViewById(R.id.story_photo_seen);
            story=itemView.findViewById(R.id.story_photo);




        }
    }


}
