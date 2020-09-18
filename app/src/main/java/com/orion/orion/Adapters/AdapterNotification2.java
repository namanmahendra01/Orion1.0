package com.orion.orion.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyLog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orion.orion.R;
import com.orion.orion.ViewPostActivity;
import com.orion.orion.models.Comment;
import com.orion.orion.models.Notification;
import com.orion.orion.models.Photo;
import com.orion.orion.models.users;
import com.orion.orion.util.UniversalImageLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AdapterNotification2 extends RecyclerView.Adapter<AdapterNotification2.ViewHolder> {

    private Context context;
    private List<Notification> mNotification;

    public AdapterNotification2(Context context, List<Notification> mNotification) {
        this.context = context;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new AdapterNotification2.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {


        Notification notification = mNotification.get(i);

        if (notification.getNotificaton().contains("///")) {
            String split[] = notification.getNotificaton().split("///");
            holder.msg2 = split[1];
            holder.msg1 = split[0];
        }else{
            holder.msg1=notification.getNotificaton();
        }



        DatabaseReference db2 = FirebaseDatabase.getInstance().getReference();
        db2.child(context.getString(R.string.dbname_users))
                .child(notification.getsUid())
                .child(context.getString(R.string.field_username))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.host =snapshot.getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




        final String timestamp = notification.getTimeStamp();



        holder.text1.setText(holder.msg1);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        getUserInfo(holder.post_image, holder.username1, notification.getsUid());

        if (notification.getpId().equals("false")) {
            holder.post_image.setVisibility(View.GONE);
        } else {
            getpostImage(holder.post_image, notification.getpId(), notification.getpUid());
            holder.post_image.setVisibility(View.VISIBLE);
        }

        holder.timeStamp1.setText(datetime);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.msg2.equals("") && !notification.getpId().equals("false")) {

                    DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();
                    db1.child(context.getString(R.string.dbname_user_photos))
                            .child(notification.getpUid())
                            .child(notification.getpId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    holder.photo = snapshot.getValue(Photo.class);
                                    ArrayList<Comment> comments = new ArrayList<>();

                                    for (DataSnapshot dSnapshot : snapshot.child("comment").getChildren()) {
                                        Comment comment = new Comment();
                                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                        comments.add(comment);


                                    }


                                    Intent i = new Intent(context, ViewPostActivity.class);
                                    i.putExtra("photo", holder.photo);
                                    i.putParcelableArrayListExtra("comments", comments);

                                    context.startActivity(i);


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                }else if(!holder.msg2.equals("") ) {
                    bottomSheet(holder.msg2,holder.host);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Notification");
                builder.setMessage("Are you sure, you want to delete this Notification?");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "DeleteMessage: deleteing message");

                        SharedPreferences sp =context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                        Gson gson=new Gson();

                       mNotification.remove( mNotification.get(i));
                        SharedPreferences.Editor editor = sp.edit();
                        String json = gson.toJson(mNotification);
                        editor.putString("nl", json);
                        editor.apply();


                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("users");
                        ref1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notifications").child(timestamp)
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Notification Deleted", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "cannot Delete", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return false;
            }
        });

//        DatabaseReference ref =FirebaseDatabase.getInstance().getReference();
//
//        Query userquery = ref
//                .child(context.getString(R.string.dbname_users))
//                .orderByChild(context.getString(R.string.field_user_id))
//                .equalTo(mparticipantLists.getUserid());
//        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//
//                    holder.user = singleSnapshot.getValue(users.class);
//
//                }
//
//
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(VolleyLog.TAG, "Query Cancelled");
//            }
//        });
//
//
//
//    }
//
//    }
    }

    private void bottomSheet(String msg2, String host) {
            BottomSheetDialog bottomSheetDialog =new BottomSheetDialog(context,R.style.BottomSheetDialogTheme);

            View bottomSheetView =((FragmentActivity)context).getLayoutInflater()
                    .inflate(R.layout.layout_bottom_sheet_recieveupdate,(LinearLayout)bottomSheetDialog.findViewById(R.id.layout_bottom_sheet_container));
            TextView msg =bottomSheetView.findViewById(R.id.msg);
            TextView username =bottomSheetView.findViewById(R.id.username);

            msg.setText(msg2);
            username.setText(host);



            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();


    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        Photo photo;

        public ImageView post_image;
        public TextView username1, text1, timeStamp1;
        private LinearLayout lin;
        String host="",msg1="",msg2="";

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);
            username1 = itemView.findViewById(R.id.username);
            text1 = itemView.findViewById(R.id.comment);
            timeStamp1 = itemView.findViewById(R.id.timeStamp);
            lin = itemView.findViewById(R.id.lin);


        }
    }

    private void getUserInfo(ImageView imageView, TextView username, String publisherId) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(publisherId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users user = dataSnapshot.getValue(users.class);
                username.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getpostImage(ImageView imageView, String postId, String postUid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(context.getString(R.string.dbname_user_photos)).child(postUid).child(postId).child("image_path");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String img = dataSnapshot.getValue().toString();
                    UniversalImageLoader.setImage(img, imageView, null, "");

                } catch (NullPointerException e) {
                    Log.d(TAG, "onDataChange: " + e.getMessage());

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
