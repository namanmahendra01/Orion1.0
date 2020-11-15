package com.orion.orion.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orion.orion.R;
import com.orion.orion.ViewPostActivity;
import com.orion.orion.models.Comment;
import com.orion.orion.models.Notification;
import com.orion.orion.models.Photo;

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

        if (notification.getNot().contains("///")) {
            String split[] = notification.getNot().split("///");
            holder.msg2 = split[1];
            holder.msg1 = split[0];
        }else{
            holder.msg1=notification.getNot();
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




        final String timestamp = notification.getTim();



        holder.text1.setText(holder.msg1);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String datetime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        getUserInfo(holder.post_image, holder.username1, notification.getsUid());

        if (notification.getpId().equals("false")) {
            holder.post_image.setVisibility(View.GONE);
        } else {
            getpostImage(holder.post_image, notification.getpId(), notification.getpUid(),holder.progress);
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
                                    if (snapshot.exists()) {
                                        holder.photo = snapshot.getValue(Photo.class);
                                        ArrayList<Comment> comments = new ArrayList<>();

                                        for (DataSnapshot dSnapshot : snapshot.child(context.getString(R.string.field_comment)).getChildren()) {
                                            Comment comment = new Comment();
                                            comment.setUi(dSnapshot.getValue(Comment.class).getUi());
                                            comment.setC(dSnapshot.getValue(Comment.class).getC());
                                            comment.setDc(dSnapshot.getValue(Comment.class).getDc());
                                            comments.add(comment);


                                        }


                                        Intent i = new Intent(context, ViewPostActivity.class);
                                        i.putExtra("photo", holder.photo);
                                        i.putParcelableArrayListExtra("comments", comments);

                                        context.startActivity(i);


                                    }
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
                builder.setMessage(R.string.delete_notification_prompt);

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteNotification(notification,i);


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
    public int getItemViewType(int position) {
        return position;
    }
    public long getItemId(int position) {
        if (mNotification!=null&&mNotification.size()!=0) {
            Notification form = mNotification.get(position);
            return form.getTim().hashCode();
        }else{
            return position;
        }
    }
    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        Photo photo;

        public ImageView post_image,progress;
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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                .child(publisherId)
                .child(context.getString(R.string.field_username));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String user = dataSnapshot.getValue().toString();
                username.setText(user);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getpostImage(ImageView imageView, String postId, String postUid, ImageView progress) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(context.getString(R.string.dbname_user_photos)).child(postUid).child(postId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                        if(dataSnapshot.child(context.getString(R.string.type)).getValue().toString().equals("photo")){
                            String img = dataSnapshot.child(context.getString(R.string.field_image_path)).getValue().toString();
                            Glide.with(context)
                                    .load(img)
                                    .placeholder(R.drawable.load)
                                    .error(R.drawable.default_image2)
                                    .placeholder(R.drawable.load)
                                    .thumbnail(0.6f)
                                    .into(imageView);
                        }else{
                            String img = dataSnapshot.child(context.getString(R.string.thumbnail)).getValue().toString();
                            Glide.with(context)
                                    .load(img)
                                    .placeholder(R.drawable.load)
                                    .error(R.drawable.default_image2)
                                    .placeholder(R.drawable.load)
                                    .thumbnail(0.6f)
                                    .into(imageView);
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void deleteNotification(Notification notification, int i) {
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference(context.getString(R.string.dbname_users));
        ref1.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(context.getString(R.string.field_Notifications))
                .child(notification.getTim())
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                SharedPreferences sp =context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                Gson gson=new Gson();

                String json = sp.getString("nl", null);
                Type type = new TypeToken<ArrayList<Notification>>() {
                }.getType();
                ArrayList<Notification> notifyList=new ArrayList<>();
                notifyList = gson.fromJson(json, type);
                if (notifyList==null){

                }else {
                    notifyList.remove(notification);

                    mNotification.remove(notification);
                    ArrayList<Notification> notifyList2=new ArrayList<>(notifyList);

                    for(Notification a:notifyList){
                        if (a.getTim().equals(notification.getTim())){
                            notifyList2.remove(a);
                        }
                    }
                    SharedPreferences.Editor editor = sp.edit();
                    String json1 = gson.toJson(notifyList2);
                    editor.putString("nl", json1);
                    editor.apply();

                    AdapterNotification2.this.notifyItemRemoved(i);


                }

                Toast.makeText(context, "Notification Deleted", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {



                Toast.makeText(context, "cannot Delete", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
