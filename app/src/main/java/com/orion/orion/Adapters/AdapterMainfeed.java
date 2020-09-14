package com.orion.orion.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orion.orion.CommentActivity;
import com.orion.orion.Notifications.Data;
import com.orion.orion.Notifications.Sender;
import com.orion.orion.Notifications.Token;
import com.orion.orion.R;
import com.orion.orion.ViewPostActivity;
import com.orion.orion.contest.Contest_Evaluation.activity_view_media;
import com.orion.orion.home.MainActivity;
import com.orion.orion.models.Comment;
import com.orion.orion.models.Photo;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.share.NextActivity;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.SquareImageView;
import com.orion.orion.util.UniversalImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AdapterMainfeed extends RecyclerView.Adapter<AdapterMainfeed.ViewHolder> {

    private static final String TAG = "AdapterMainfeed";
    private LayoutInflater mInflater;
    private int mlayoutResource;
    private Context mContext;
    private DatabaseReference mReference;
    private String currentUsername = "";
    private String numberoflike="0";

    private FirebaseMethods mFirebaseMethods;
    private boolean notify = false;

    private List<Photo> photos;

    public AdapterMainfeed(Context mContext, List<Photo> photos) {
        this.mContext = mContext;
        this.photos = photos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.fragment_mainfeed_listitem,parent,false);
        return new AdapterMainfeed.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        mFirebaseMethods = new FirebaseMethods(mContext);





        Photo photo=photos.get(i);
        getCurrentUsername(holder.domain,photo);
        ifCurrentUserLiked(holder,photo);
        ifCurrentUserPromoted(holder,photo);

        holder.eclipse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.eclipse);
                if (photo.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    popupMenu.getMenuInflater().inflate(R.menu.post_menu,popupMenu.getMenu());
                    Log.d(TAG, "onClick: "+ "yespop");
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(mContext, "clicked", Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Delete");
                            builder.setMessage("Are you sure, you want to delete this Post?");

//                set buttons
                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "DeleteMessage: deleteing message");
                                    DeletePost(photo);

                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                            return true;
                        }
                    });

                    popupMenu.show();


                }else {
                    popupMenu.getMenuInflater().inflate(R.menu.post_menu_all,popupMenu.getMenu());
                    Log.d(TAG, "onClick: "+ "yespop");
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(mContext, "clicked", Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Report");
                            builder.setMessage("Are you sure, you want to Report this Post?");

//                set buttons
                            builder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "DeleteMessage: deleteing message");
                                    ReportPost(photo);

                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                            return true;
                        }
                    });

                    popupMenu.show();


                }
            }
        });

        numberofPromote(holder.promoteNum,photo.getPhoto_id(),photo.getUser_id());
//        getLikesString(holder);
//            set the comment
        List<Comment> comments = photo.getComments();
        holder.commentnumber.setText(String.valueOf(comments.size()));

//        get time
        holder.timeDate.setText(photo.getDate_created().substring(0,10));
        holder.caption.setText(photo.getCaption());

//        get post image
        final ImageLoader imageloader = ImageLoader.getInstance();
        imageloader.displayImage(photo.getImage_path(), holder.image);

//        get username
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(photo.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    currentUsername = singleSnapshot.getValue(users.class).getUsername();

                    holder.username.setText(singleSnapshot.getValue(users.class).getUsername());
                    holder.credit.setText("© " + singleSnapshot.getValue(users.class).getUsername());
                    holder.username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(mContext, profile.class);
                            i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                            i.putExtra(mContext.getString(R.string.intent_user), holder.user);
                            mContext.startActivity(i);
                        }
                    });

                    imageloader.displayImage(singleSnapshot.getValue(users.class).getProfile_photo(), holder.mProfileImage);
                    holder.mProfileImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(mContext, profile.class);
                            i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                            i.putExtra(mContext.getString(R.string.intent_user), holder.user);
                            mContext.startActivity(i);
                        }
                    });


                    holder.setting = singleSnapshot.getValue(users.class);
                    holder.comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(mContext, CommentActivity.class);
                            i.putExtra("photoId", photo.getPhoto_id());
                            i.putExtra("userId", photo.getUser_id());
                            mContext.startActivity(i);
                        }
                    });
                }


            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Query Cancelled");
            }
        });
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();
                db1.child(mContext.getString(R.string.dbname_user_photos))
                        .child(photo.getUser_id())
                        .child(photo.getPhoto_id())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.photo = snapshot.getValue(Photo.class);
                                Log.d(TAG, "onDataChange: klj"+holder.photo.getPhoto_id());
                                ArrayList<Comment> comments = new ArrayList<>();

                                for (DataSnapshot dSnapshot:snapshot.child("comment").getChildren()){
                                    Comment comment = new Comment();
                                    comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                    comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                    comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                    comments.add(comment);


                                }

                                Log.d(TAG, "onDataChange: klj"+comments);

                                Intent i = new Intent(mContext, ViewPostActivity.class);
                                i.putExtra("photo",holder.photo);
                                i.putParcelableArrayListExtra("comments",comments);

                                mContext.startActivity(i);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });






//        get the object

        Query userquery = FirebaseDatabase.getInstance().getReference()
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(photo.getUser_id());
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
//
//        }
        holder.whitestar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "whitestar clicked");
                notify = true;
                holder.whitestar.setVisibility(View.GONE);
                holder.yellowstar.setVisibility(View.VISIBLE);
                addlike(holder,photo);
                NumberOfLikes(holder,photo);





            }
        });
        holder.yellowstar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "yellowstar clicked");

                holder.whitestar.setVisibility(View.VISIBLE);
                holder.yellowstar.setVisibility(View.GONE);
                removeLike(holder,photo);
                NumberOfLikes(holder, photo);



            }
        });
        holder.promote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;

                promotePost(photo,holder);
            }
        });


     holder.promoted.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            unPromotePost(photo,holder);
        }
    });

}

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof LinearLayoutManager && getItemCount() > 0) {
            LinearLayoutManager llm = (LinearLayoutManager) manager;
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (!recyclerView.canScrollVertically(1) &&newState == RecyclerView.SCROLL_STATE_IDLE) {

                        int visiblePosition = llm.findLastVisibleItemPosition();
                        Log.d(TAG, "om: awe"+visiblePosition+ " "+newState);
                    }

                }


            });
        }
    }
    private void unPromotePost(Photo photo, ViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Remove Promotion");
        builder.setMessage("Are you sure, you want to remove this Promotion?");

//                set buttons
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(VolleyLog.TAG, "Rejecting: rejected ");

                holder.promote.setVisibility(View.VISIBLE);
                holder.promoted.setVisibility(View.GONE);

                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
              db.child(mContext.getString(R.string.dbname_promote))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(photo.getPhoto_id())
                      .removeValue();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child(mContext.getString(R.string.dbname_user_photos))
                        .child(photo.getUser_id())
                        .child(photo.getPhoto_id())
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


    private void promotePost(Photo photo, ViewHolder holder) {
        BottomSheetDialog bottomSheetDialog =new BottomSheetDialog(mContext,R.style.BottomSheetDialogTheme);

        View bottomSheetView =((FragmentActivity)mContext).getLayoutInflater()
                .inflate(R.layout.layout_bottom_sheet_promote   ,(LinearLayout)bottomSheetDialog.findViewById(R.id.layout_bottom_sheet_container));
        TextView username =bottomSheetView.findViewById(R.id.usernameBs);
        TextView cancel =bottomSheetView.findViewById(R.id.cancel);
        TextView promote =bottomSheetView.findViewById(R.id.promote);
        ImageView post =bottomSheetView.findViewById(R.id.postBs);
        UniversalImageLoader.setImage(photo.getImage_path(),post,null,"");
        username.setText(currentUsername);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        promote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.promote.setVisibility(View.GONE);
                holder.promoted.setVisibility(View.VISIBLE);


                SNTPClient.getDate(TimeZone.getTimeZone("Asia/Colombo"), new SNTPClient.Listener() {
                    @Override
                    public void onTimeReceived(String rawDate) {
                        // rawDate -> 2019-11-05T17:51:01+0530


                        String str_date = rawDate;
                        java.text.DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                        Date date = null;
                        try {
                            date = (Date) formatter.parse(str_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Long timeStart= date.getTime();
                        Long timeEnd = date.getTime()+84600000;


                        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("photoid",photo.getPhoto_id());
                        hashMap.put("userid",photo.getUser_id());
                        hashMap.put("photoLink",photo.getImage_path());
                        hashMap.put("storyid",photo.getPhoto_id());
                        hashMap.put("timeEnd",String.valueOf(timeEnd));
                        hashMap.put("timeStart",String.valueOf(timeStart));
                        hashMap.put("promoterId",FirebaseAuth.getInstance().getCurrentUser().getUid());


                        db1.child(mContext.getString(R.string.dbname_promote))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(photo.getPhoto_id())
                                .setValue(hashMap);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        reference.child(mContext.getString(R.string.dbname_user_photos))
                                .child(photo.getUser_id())
                                .child(photo.getPhoto_id())
                                .child("Promote")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue("true");

                        addToHisNotification(""+photo.getUser_id(),photo.getPhoto_id(),"promoted your post.");
                        final DatabaseReference data = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_users))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        data.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                users user = dataSnapshot.getValue(users.class);

                                if (notify) {
                                    mFirebaseMethods.sendNotification(photo.getUser_id(), user.getUsername(), "promoted your post.","Promote");
                                }
                                notify = false;

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        bottomSheetDialog.dismiss();

                        Log.e(SNTPClient.TAG, rawDate);

                    }

                    @Override
                    public void onError(Exception ex) {
                        Log.e(SNTPClient.TAG, ex.getMessage());
                    }
                });




            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();


    }

    private void ifCurrentUserPromoted(ViewHolder holder, Photo photo) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.dbname_user_photos))
                .child(photo.getUser_id())
                .child(photo.getPhoto_id())
                .child("Promote")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Log.d(TAG, " checking current user liked or not: Already liked");
                    holder.promote.setVisibility(View.GONE);
                    holder.promoted.setVisibility(View.VISIBLE);

                }else {
                    Log.d(TAG, " checking current user liked or not: not liked");
                    holder.promote.setVisibility(View.VISIBLE);
                    holder.promoted.setVisibility(View.GONE);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void numberofPromote(TextView promoteNum, String photo_id, String user_id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.dbname_user_photos))
                .child(user_id)
                .child(photo_id)
                .child("Promote");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String num= String.valueOf(dataSnapshot.getChildrenCount());
               promoteNum.setText(num);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView mProfileImage;
        String likeString;
        TextView username, timeDate,domain, caption,commentnumber,likenumber,credit,promoteNum;
        ImageView yellowstar, whitestar, comment,promote,promoted,eclipse;
        SquareImageView image;

        users setting = new users();
        com.orion.orion.models.users user = new users();
        StringBuilder users;
        String mLikeString;
        boolean likeByCurrentsUser;
        boolean likeByCurrentsUser2;
        Photo photo;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            username = (TextView) itemView.findViewById(R.id.username);
            image = (SquareImageView) itemView.findViewById(R.id.post_image);
          yellowstar = (ImageView) itemView.findViewById(R.id.image_star_yellow);
           whitestar = (ImageView) itemView.findViewById(R.id.image_star);
            comment = (ImageView) itemView.findViewById(R.id.image_shoutout);
        caption = (TextView) itemView.findViewById(R.id.image_caption);
            timeDate = (TextView) itemView.findViewById(R.id.images_time);
           mProfileImage = (CircleImageView) itemView.findViewById(R.id.profile_photo);
            promote = (ImageView) itemView.findViewById(R.id.promote);
            promoted = (ImageView) itemView.findViewById(R.id.promoted);
            domain=(TextView)itemView.findViewById(R.id.domain12) ;
            promoteNum=(TextView)itemView.findViewById(R.id.promote_number) ;
          users = new StringBuilder();
           commentnumber=(TextView)itemView.findViewById(R.id.comments_number) ;
           likenumber=(TextView)itemView.findViewById(R.id.likes_number) ;
           eclipse=itemView.findViewById(R.id.ivEllipses);


            credit=(TextView)itemView.findViewById(R.id.credit) ;



        }
    }





    private void addToHisNotification(String hisUid,String pId,String notification){

        String timestamp=""+System.currentTimeMillis();


//        data to put in notification
        HashMap<Object,String> hashMap = new HashMap<>();
        hashMap.put("pId",pId);

        hashMap.put("timeStamp",timestamp);

        hashMap.put("pUid",hisUid);

        hashMap.put("notificaton",notification);
        hashMap.put("seen","false");


        hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());



        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }



    private void getCurrentUsername(TextView domain, Photo photo) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_users))
                .child(photo.getUser_id())
                .child(mContext.getString(R.string.domain));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                domain.setText(dataSnapshot.getValue().toString());


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Query Cancelled");
            }
        });
    }
    private void DeletePost( Photo photo) {
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(photo.getImage_path());
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d(VolleyLog.TAG, "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d(VolleyLog.TAG, "onFailure: did not delete file");
            }
        });

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
        reference2.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(photo.getPhoto_id())
                .removeValue();


    }
    private void ReportPost( Photo photo) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(mContext.getString(R.string.dbname_reports))
                .child("posts")
                .child(photo.getPhoto_id())
                .child("user_id")
                .setValue(photo.getUser_id());
    }


    private  void NumberOfLikes(final ViewHolder holder, Photo photo){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.dbname_user_photos))
                .child(photo.getUser_id())
                .child(photo.getPhoto_id())
                .child("likes");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numberoflike= String.valueOf(dataSnapshot.getChildrenCount());
                holder.likenumber.setText(numberoflike);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    private String getTimestampDifference(Photo photo) {

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(("yyyy-MM-dd'T'HH:mm:ss'Z'"), Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = photo.getDate_created();
        try {
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24)));
        } catch (ParseException e) {
            Log.e(TAG, "Parse Exception" + e.getMessage());
            difference = "0";
        }
        return difference;
    }

    private void ifCurrentUserLiked(final ViewHolder holder, Photo photo){
        Log.d(TAG, " checking current user liked or not");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.dbname_user_photos))
                .child(photo.getUser_id())
                .child(photo.getPhoto_id())
                .child("likes")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Log.d(TAG, " checking current user liked or not: Already liked");
                    holder.whitestar.setVisibility(View.GONE);
                    holder.yellowstar.setVisibility(View.VISIBLE);
                    NumberOfLikes(holder, photo);
                    holder.likeByCurrentsUser2=true;

                }else {
                    Log.d(TAG, " checking current user liked or not: not liked");
                    holder.whitestar.setVisibility(View.VISIBLE);
                    holder.yellowstar.setVisibility(View.GONE);
                    NumberOfLikes(holder, photo);
                    holder.likeByCurrentsUser2=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    private void addlike(final ViewHolder holder, Photo photo){
        Log.d(TAG, " like add");

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        reference1.child(mContext.getString(R.string.dbname_user_photos))
                .child(photo.getUser_id())
                .child(photo.getPhoto_id())
                .child("likes")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("user_id")
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        NumberOfLikes(holder, photo);

        final DatabaseReference data = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users user = dataSnapshot.getValue(users.class);

                if (notify) {
                    mFirebaseMethods.sendNotification(photo.getUser_id(), user.getUsername(), "liked your post","Like");
                }
                notify = false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        addToHisNotification(""+photo.getUser_id(),photo.getPhoto_id(),"Liked your post");


    }
    private void removeLike(final ViewHolder holder, Photo photo){
        Log.d(TAG, " like removed");

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        reference1.child(mContext.getString(R.string.dbname_user_photos))
                .child(photo.getUser_id())
                .child(photo.getPhoto_id())
                .child("likes")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeValue();
        NumberOfLikes(holder, photo);



    }





}
