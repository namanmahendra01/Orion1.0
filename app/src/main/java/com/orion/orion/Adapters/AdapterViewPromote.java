package com.orion.orion.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.VolleyLog;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.ViewPostActivity;

import com.orion.orion.models.Comment;
import com.orion.orion.models.Photo;
import com.orion.orion.models.Promote;

import java.util.ArrayList;
import java.util.List;

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


        if (promote.getPID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            myHolder.delete.setVisibility(View.VISIBLE);
        }else{
            myHolder.delete.setVisibility(View.GONE);

        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(context.getString(R.string.dbname_user_photos))
                .child(promote.getUi())
                .child(promote.getPi())
                .child(context.getString(R.string.thumbnail))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (!snapshot.getValue().toString().equals("")) {
                                myHolder.image = snapshot.getValue().toString();
                            } else {
                                myHolder.image = promote.getIp();

                            }
                            setWidgets(promote.getUi(), myHolder.image, myHolder.username, myHolder.post, myHolder.progress);

                        }else{


                    }

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
                builder.setMessage(context.getString(R.string.remove_promotion_prompt));

//                set buttons
                builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(VolleyLog.TAG, "Rejecting: rejected ");
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child(context.getString(R.string.dbname_promote))
                                .child(promote.getPID())
                                .child(promote.getStid())
                                .removeValue();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        reference.child(context.getString(R.string.dbname_user_photos))
                                .child(promote.getUi())
                                .child(promote.getPi())
                                .child(context.getString(R.string.field_promotes))
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
                .child(promote.getPID())
                .child(promote.getStid())
                .child(context.getString(R.string.field_view))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue("true");







        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();
                db1.child(context.getString(R.string.dbname_user_photos))
                        .child(promote.getUi())
                        .child(promote.getPi())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                myHolder.photo = snapshot.getValue(Photo.class);
                                ArrayList<Comment> comments = new ArrayList<>();

                                for (DataSnapshot dSnapshot:snapshot.child(context.getString(R.string.field_comment)).getChildren()){
                                    Comment comment = new Comment();
                                    comment.setUi(dSnapshot.getValue(Comment.class).getUi());
                                    comment.setC(dSnapshot.getValue(Comment.class).getC());
                                    comment.setDc(dSnapshot.getValue(Comment.class).getDc());
                                    comments.add(comment);


                                }

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
                .child(context.getString(R.string.field_username))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        username.setText("@"+dataSnapshot.getValue().toString());
                        Glide.with(context.getApplicationContext())
                                .load(photoLink)
                                .placeholder(R.drawable.load)
                                .error(R.drawable.default_image2)
                                .placeholder(R.drawable.load)
                                .thumbnail(0.5f)
                                .into(post);
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
        return form.getStid().hashCode();
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
