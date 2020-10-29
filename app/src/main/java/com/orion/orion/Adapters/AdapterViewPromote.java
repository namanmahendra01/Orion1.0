package com.orion.orion.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.VolleyLog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orion.orion.R;
import com.orion.orion.ViewPostActivity;

import com.orion.orion.home.ViewPromoted;
import com.orion.orion.models.Chat;
import com.orion.orion.models.Comment;
import com.orion.orion.models.Like;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.Photo;
import com.orion.orion.models.Promote;
import com.orion.orion.models.users;
import com.orion.orion.util.UniversalImageLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AdapterViewPromote extends RecyclerView.Adapter<AdapterViewPromote.MyHolder> {

    Context context;
    List<Promote> promoteList;
    FirebaseUser fUser;

    public AdapterViewPromote(Context context, List<Promote> promoteList) {
        this.context = context;
        this.promoteList = promoteList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.promote_view_item, viewGroup, false);
        return new MyHolder(view);



    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, final int i) {
//        get data

        Promote promote= promoteList.get(i);

        Log.d(TAG, "onBindViewHolder: pos"+i);

        if (promote.getPromoterId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            myHolder.delete.setVisibility(View.VISIBLE);
        }else{
            myHolder.delete.setVisibility(View.GONE);

        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(context.getString(R.string.dbname_user_photos))
                .child(promote.getUserid())
                .child(promote.getPhotoid())
                .child("thumbnail")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.getValue().toString().equals("")){
                            myHolder.image=snapshot.getValue().toString();
                        }else{
                            myHolder.image=promote.getPhotoLink();

                        }
                        setWidgets(promote.getUserid(),myHolder.image,myHolder.username,myHolder.post,myHolder.progress);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        myHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Remove Selected Promotion");
                builder.setMessage("Are you sure, you want to remove this Promotion?");

//                set buttons
                builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(VolleyLog.TAG, "Rejecting: rejected ");
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child(context.getString(R.string.dbname_promote))
                                .child(promote.getPromoterId())
                                .child(promote.getStoryid())
                                .removeValue();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        reference.child(context.getString(R.string.dbname_user_photos))
                                .child(promote.getUserid())
                                .child(promote.getPhotoid())
                                .child("Promote")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .removeValue();


                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

            }
        });

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(context.getString(R.string.dbname_promote))
                .child(promote.getPromoterId())
                .child(promote.getStoryid())
                .child("views")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue("true");







        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();
                db1.child(context.getString(R.string.dbname_user_photos))
                        .child(promote.getUserid())
                        .child(promote.getPhotoid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                myHolder.photo = snapshot.getValue(Photo.class);
                                Log.d(TAG, "onDataChange: klj"+myHolder.photo.getPhoto_id());
                                ArrayList<Comment> comments = new ArrayList<>();

                                for (DataSnapshot dSnapshot:snapshot.child("comment").getChildren()){
                                    Comment comment = new Comment();
                                    comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                    comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                    comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                    comments.add(comment);


                                }

                                Log.d(TAG, "onDataChange: klj"+comments);

                                Intent i = new Intent(context, ViewPostActivity.class);
                                i.putExtra("photo",myHolder.photo);
                                i.putParcelableArrayListExtra("comments",comments);

                                context.startActivity(i);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



            }
        });



    }

    private void setWidgets(String promoterId, String photoLink, TextView username, ImageView post, ImageView progress) {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(context.getString(R.string.dbname_users))
                .child(promoterId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        users user=dataSnapshot.getValue(users.class);
                        username.setText("@"+user.getUsername());
                        UniversalImageLoader.setImage(photoLink,post,progress,"");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public long getItemId(int position) {
        Promote form = promoteList.get(position);
        return form.getStoryid().hashCode();
    }
    @Override
    public int getItemCount() {
        return promoteList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {

        TextView username;
        ImageView post,delete,progress;
        Photo photo;
        String image="";

        public MyHolder(@NonNull View itemView) {


            super(itemView);

            username=itemView.findViewById(R.id.username);
            post=itemView.findViewById(R.id.post);
            delete=itemView.findViewById(R.id.delete);
            progress=itemView.findViewById(R.id.progress);





        }
    }


}
