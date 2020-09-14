package com.orion.orion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyLog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.Adapters.AdapterMainfeed;
import com.orion.orion.models.Comment;
import com.orion.orion.models.Photo;
import com.orion.orion.models.users;
import com.orion.orion.util.BottomNaavigationViewHelper;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.SquareImageView;
import com.orion.orion.util.UniversalImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class ViewPostActivity extends AppCompatActivity {
    private static final String TAG = "ViewPostFragment";




    //firebase
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    String currentUsername = "";

    private Photo mphoto;
    private int activitynumber = 0;
    private String photoUsername;
    private String PhotoUrl;

    private boolean mLikebyCurrentUser;
    private StringBuilder mUsers;
    private String mLikesString = "";
    private String numberoflike = "0";
    ArrayList<Comment> comments = new ArrayList<>();


    private SquareImageView mPostImage;
    private BottomNavigationViewEx bottomNavigationView;
    private TextView mBackLabel, mCaption, mUsername, mTimestamp, mLikes, mCommentnumber, mcredit, domain, promoteNum;
    private ImageView mBackArrow, mEllipses, mStarYellow, mStarWhite, mProfileImage, mComment, promote, promoted;

    private GestureDetector mgesture;
    private users mCurrentUser;
    private boolean likeByCurrentsUser2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        mPostImage = (SquareImageView) findViewById(R.id.post_image);
        bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.BottomNavViewBar);
        mBackArrow = (ImageView) findViewById(R.id.backarrow);
        mEllipses = (ImageView) findViewById(R.id.ivEllipses);
        mStarWhite = (ImageView) findViewById(R.id.image_star);
        mStarYellow = (ImageView) findViewById(R.id.image_star_yellow);
        mProfileImage = (ImageView) findViewById(R.id.profile_photo1);
        mBackLabel = (TextView) findViewById(R.id.tvbacklabel1);
        mCaption = (TextView) findViewById(R.id.image_caption);
        mUsername = (TextView) findViewById(R.id.username1);
        mTimestamp = (TextView) findViewById(R.id.images_time);
        mLikes = (TextView) findViewById(R.id.likes_number);
        mComment = (ImageView) findViewById(R.id.image_shoutout);
        mCommentnumber = (TextView) findViewById(R.id.comments_number);
        mcredit = (TextView) findViewById(R.id.credit);
        promote = (ImageView) findViewById(R.id.promote);
        promoted = (ImageView) findViewById(R.id.promoted);
        domain = (TextView) findViewById(R.id.domain12);
        promoteNum = (TextView) findViewById(R.id.promote_number);






        Intent i = getIntent();
        mphoto = i.getParcelableExtra("photo");
        comments = i.getParcelableArrayListExtra("comments");


        mEllipses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ViewPostActivity.this, mEllipses);
                if (mphoto.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    popupMenu.getMenuInflater().inflate(R.menu.post_menu,popupMenu.getMenu());
                    Log.d(TAG, "onClick: "+ "yespop");
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(ViewPostActivity.this, "clicked", Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder builder = new AlertDialog.Builder(ViewPostActivity.this);
                            builder.setTitle("Delete");
                            builder.setMessage("Are you sure, you want to delete this Post?");

//                set buttons
                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "DeleteMessage: deleteing message");
                                    DeletePost();

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
                            Toast.makeText(ViewPostActivity.this, "clicked", Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder builder = new AlertDialog.Builder(ViewPostActivity.this);
                            builder.setTitle("Report");
                            builder.setMessage("Are you sure, you want to Report this Post?");

//                set buttons
                            builder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "DeleteMessage: deleteing message");
                                    ReportPost();

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


        Log.d(TAG, "onCreate:kjknjk " + comments.size());


        UniversalImageLoader.setImage(mphoto.getImage_path(), mPostImage, null, "");
        activitynumber = 4;
        getCurrentUser();


        setupFirebaseAuth();
        ifCurrentUserLiked();
        ifCurrentUserPromoted();
        numberofPromote();

        getPhototDetail();
        setupWidgets();


        mStarWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "whitestar clicked");

                mStarWhite.setVisibility(View.GONE);
                mStarYellow.setVisibility(View.VISIBLE);
                addlike();
                NumberOfLikes();


            }
        });
        mStarYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "yellowstar clicked");

                mStarWhite.setVisibility(View.VISIBLE);
                mStarYellow.setVisibility(View.GONE);
                removeLike();
                NumberOfLikes();


            }
        });
        promote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promotePost();
            }
        });


        promoted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unPromotePost();
            }
        });
    }

    private void ReportPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_reports))
                .child("posts")
                .child(mphoto.getPhoto_id())
                .child("user_id")
                .setValue(mphoto.getUser_id());
    }

    private void unPromotePost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Promotion");
        builder.setMessage("Are you sure, you want to remove this Promotion?");

//                set buttons
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(VolleyLog.TAG, "Rejecting: rejected ");

                promote.setVisibility(View.VISIBLE);
                promoted.setVisibility(View.GONE);

                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                db.child(getString(R.string.dbname_promote))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mphoto.getPhoto_id())
                        .removeValue();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child(getString(R.string.dbname_user_photos))
                        .child(mphoto.getUser_id())
                        .child(mphoto.getPhoto_id())
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

    private void numberofPromote() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUser_id())
                .child(mphoto.getPhoto_id())
                .child("Promote");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String num = String.valueOf(dataSnapshot.getChildrenCount());
                promoteNum.setText(num);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void promotePost() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);

        View bottomSheetView = this.getLayoutInflater()
                .inflate(R.layout.layout_bottom_sheet_promote, (LinearLayout) bottomSheetDialog.findViewById(R.id.layout_bottom_sheet_container));
        TextView username = bottomSheetView.findViewById(R.id.usernameBs);
        TextView cancel = bottomSheetView.findViewById(R.id.cancel);
        TextView promote1 = bottomSheetView.findViewById(R.id.promote);
        ImageView post = bottomSheetView.findViewById(R.id.postBs);
        UniversalImageLoader.setImage(mphoto.getImage_path(), post, null, "");
        username.setText(currentUsername);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        promote1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                promote.setVisibility(View.GONE);
                promoted.setVisibility(View.VISIBLE);


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
                        Long timeStart = date.getTime();
                        Long timeEnd = date.getTime() + 84600000;


                        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("photoid", mphoto.getPhoto_id());
                        hashMap.put("userid", mphoto.getUser_id());
                        hashMap.put("photoLink", mphoto.getImage_path());
                        hashMap.put("storyid", mphoto.getPhoto_id());
                        hashMap.put("timeEnd", String.valueOf(timeEnd));
                        hashMap.put("timeStart", String.valueOf(timeStart));
                        hashMap.put("promoterId", FirebaseAuth.getInstance().getCurrentUser().getUid());


                        db1.child(getString(R.string.dbname_promote))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(mphoto.getPhoto_id())
                                .setValue(hashMap);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        reference.child(getString(R.string.dbname_user_photos))
                                .child(mphoto.getUser_id())
                                .child(mphoto.getPhoto_id())
                                .child("Promote")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue("true");

                        addToHisNotification("" + mphoto.getUser_id(), mphoto.getPhoto_id(), "Promoted your post.");


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

    private void DeletePost() {
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(mphoto.getImage_path());
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
        reference2.child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mphoto.getPhoto_id())
                .removeValue();


    }

    @SuppressLint("ClickableViewAccessibility")


    private void getCurrentUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    mCurrentUser = singleSnapshot.getValue(users.class);
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Query Cancelled");
            }
        });
    }

    private void NumberOfLikes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUser_id())
                .child(mphoto.getPhoto_id())
                .child("likes");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numberoflike = String.valueOf(dataSnapshot.getChildrenCount());
                mLikes.setText(numberoflike);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void ifCurrentUserLiked() {
        Log.d(TAG, " checking current user liked or not");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUser_id())
                .child(mphoto.getPhoto_id())
                .child("likes")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, " checking current user liked or not: Already liked");
                    mStarWhite.setVisibility(View.GONE);
                    mStarYellow.setVisibility(View.VISIBLE);
                    NumberOfLikes();
                    likeByCurrentsUser2 = true;

                } else {
                    Log.d(TAG, " checking current user liked or not: not liked");
                    mStarWhite.setVisibility(View.VISIBLE);
                    mStarYellow.setVisibility(View.GONE);
                    NumberOfLikes();
                    likeByCurrentsUser2 = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void addToHisNotification(String hisUid, String pId, String notification) {

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
                Log.d(TAG, "onCreateView: timestampyesss" + date.getTime());
                String timestamp = String.valueOf(date.getTime());

                //        data to put in notification
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("pId", pId);

                hashMap.put("timeStamp", timestamp);

                hashMap.put("pUid", hisUid);

                hashMap.put("notificaton", notification);
                hashMap.put("seen", "false");


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


                Log.e(SNTPClient.TAG, rawDate);

            }

            @Override
            public void onError(Exception ex) {
                Log.e(SNTPClient.TAG, ex.getMessage());
            }
        });


    }

    private void addlike() {
        Log.d(TAG, " like add");

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        reference1.child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUser_id())
                .child(mphoto.getPhoto_id())
                .child("likes")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_user_id))
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        NumberOfLikes();
        addToHisNotification("" + mphoto.getUser_id(), mphoto.getPhoto_id(), "Liked your post");


    }

    private void removeLike() {
        Log.d(TAG, " like removed");

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        reference1.child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUser_id())
                .child(mphoto.getPhoto_id())
                .child("likes")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeValue();
        NumberOfLikes();


    }

    private void ifCurrentUserPromoted() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUser_id())
                .child(mphoto.getPhoto_id())
                .child("Promote")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, " checking current user liked or not: Already liked");
                    promote.setVisibility(View.GONE);
                    promoted.setVisibility(View.VISIBLE);

                } else {
                    Log.d(TAG, " checking current user liked or not: not liked");
                    promote.setVisibility(View.VISIBLE);
                    promoted.setVisibility(View.GONE);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getPhototDetail() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .child(mphoto.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users user = dataSnapshot.getValue(users.class);
                mUsername.setText(user.getUsername());
                currentUsername = user.getUsername();
                UniversalImageLoader.setImage(user.getProfile_photo(), mProfileImage, null, "");
                mcredit.setText("© " + user.getUsername());

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Query Cancelled");
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setupWidgets() {
        mTimestamp.setText(mphoto.getDate_created().substring(0, 10));

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                Intent i = new Intent(ViewPostActivity.this, CommentActivity.class);
                i.putExtra("photoId", mphoto.getPhoto_id());
                i.putExtra("userId", mphoto.getUser_id());
                startActivity(i);

            }
        });
        mCommentnumber.setText(String.valueOf(comments.size()));
        mCaption.setText(mphoto.getCaption());
        mLikes.setText(mLikesString);


    }

    private String getTimestampDifference() {

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(("yyyy-MM-dd'T'HH:mm:ss'Z'"), Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = mphoto.getDate_created();
        try {
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24)));
        } catch (ParseException e) {
            Log.e(TAG, "Parse Exception" + e.getMessage());
            difference = "0";
        }
        return difference;
    }



    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
