package com.orion.orion.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.Adapters.AdapterGridImage;
import com.orion.orion.Adapters.AdapterMainFeedContest;
import com.orion.orion.R;
import com.orion.orion.home.Chat_Activity;
import com.orion.orion.models.Comment;
import com.orion.orion.models.ContestDetail;
import com.orion.orion.models.Like;
import com.orion.orion.models.Photo;
import com.orion.orion.models.users;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.UniversalImageLoader;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity {

    //standards
    private static final String TAG = "ProfileViewFragment";
    private static final int ACTIVITY_NUM = 4;
    ArrayList<Photo> imgURLsList;
    ProgressDialog dialog;
    private Context mContext;
    //View Profile Widgets
    private ProgressBar mProgressBar;
    private ImageView backButton;
    private CircleImageView mProfilePhoto;
    private TextView mUsername;
    private TextView mFollowers;
    private TextView mDomain;
    private Button mFollow;
    private Button mMessage;
    private TextView mCreated;
    private TextView mJoined;
    private TextView mWon;
    private TextView mDescription;
    private TextView mWebsite;
    private String mUser;
    private AdapterGridImage adapterGridImage;
    private RecyclerView gridRv;
    private boolean notify = false;
    private BottomNavigationViewEx bottomNavigationView;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_viewprofile);
        mProgressBar = findViewById(R.id.profileprogressbar);
        dialog = ProgressDialog.show(this, "", "Loading Profile...", true);
        backButton = findViewById(R.id.back);
        mProfilePhoto = findViewById(R.id.profile_photo);
        mUsername =findViewById(R.id.display_name);
        mFollowers =findViewById(R.id.follower);
        mDomain = findViewById(R.id.domain);
        mFollow = findViewById(R.id.followButton);
        mMessage = findViewById(R.id.messageButton);
        mCreated =findViewById(R.id.created_contests);
        mJoined = findViewById(R.id.joined_contests);
        mWon = findViewById(R.id.contests_won);
        mDescription = findViewById(R.id.description);
        mWebsite = findViewById(R.id.website);
        gridRv = findViewById(R.id.gridRv);
        bottomNavigationView =findViewById(R.id.BottomNavViewBar);
        mFirebaseMethods = new FirebaseMethods(this);
        gridRv.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 3);
        gridRv.setLayoutManager(linearLayoutManager);
        imgURLsList = new ArrayList<>();
        adapterGridImage = new AdapterGridImage(this, imgURLsList);
        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(20);
        gridRv.setItemViewCacheSize(9);
        gridRv.setDrawingCacheEnabled(true);
        gridRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        adapterGridImage.setHasStableIds(true);
        gridRv.setAdapter(adapterGridImage);
        mContext =this;
        try {
            Intent i=getIntent();

            mUser = i.getStringExtra(getString(R.string.intent_user));
            init();
        } catch (NullPointerException e) {
            Log.d(TAG, "null pointer Exception" + e.getMessage());
            Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().popBackStack();
        }
        setupFirebaseAuth();
        isFolllowing();
        getFollowerCount();
        getCompDetails();

        backButton.setOnClickListener(v -> {
           getSupportFragmentManager().popBackStack();
           finish();
        });
        mFollow.setOnClickListener(v -> {
            YoYo.with(Techniques.FadeIn).duration(500).playOn(mMessage);
            boolean isFollowing = mFollow.getText().equals("Unfollow");
            if (isFollowing) {

//               remove from following list
                SharedPreferences sp =getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                Gson gson=new Gson();
                String json =sp.getString("fl",null);
                Type type= new TypeToken<ArrayList<String>>() {}.getType();
                ArrayList<String> list= new ArrayList<String>();
                list=gson.fromJson(json,type);
                if (list==null){

                }else{
                    list.remove(mUser);

                }
//                 save following list
                SharedPreferences.Editor editor=sp.edit();
                json =gson.toJson(list);
                editor.putString("fl",json);
                editor.apply();


//              update following list

                 json =sp.getString("removefollowing",null);
                 type= new TypeToken<ArrayList<String>>() {}.getType();

                ArrayList<String> ulist= new ArrayList<String>();
                ulist=gson.fromJson(json,type);
                if (ulist==null){
                    ulist= new ArrayList<String>();
                    ulist.add(mUser);
                }else{
                    ulist.add(mUser);

                }
//                save update list
                editor=sp.edit();
                json =gson.toJson(ulist);
                editor.putString("removefollowing",json);
                editor.apply();



                FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_following)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(mUser).removeValue();
                FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_follower)).child(mUser).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                mFollow.setText("Follow");

            } else {

//               addfollowing list
                SharedPreferences sp =getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                Gson gson=new Gson();
                String json =sp.getString("fl",null);
                Type type= new TypeToken<ArrayList<String>>() {}.getType();
                ArrayList<String> list= new ArrayList<String>();
                list=gson.fromJson(json,type);
                if (list==null){
                    list= new ArrayList<String>();
                    list.add(mUser);
                }else{
                    list.add(mUser);

                }
//                 save following list
                SharedPreferences.Editor editor=sp.edit();
                json =gson.toJson(list);
                editor.putString("fl",json);
                editor.apply();


//              update following list

                json =sp.getString("addfollowing",null);
                type= new TypeToken<ArrayList<String>>() {}.getType();

                ArrayList<String> ulist= new ArrayList<String>();
                ulist=gson.fromJson(json,type);
                if (ulist==null){
                    ulist= new ArrayList<String>();
                    ulist.add(mUser);
                }else{
                    ulist.add(mUser);

                }
//                save update list
                editor=sp.edit();
                json =gson.toJson(ulist);
                editor.putString("addfollowing",json);
                editor.apply();




                notify = true;
                FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_following)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(mUser).child(getString(R.string.field_user_id)).setValue(mUser);
                FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_follower)).child(mUser).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getString(R.string.field_user_id)).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mFollow.setText("Unfollow");
                final DatabaseReference data = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                data.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        users user = dataSnapshot.getValue(users.class);
                        if (notify) {
                            mFirebaseMethods.sendNotification(mUser, user.getUsername(), "becomes your FAN!", "Fan");
                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                addToHisNotification(mUser, "becomes your FAN!");
            }
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
            Query query1 = reference1.child(getString(R.string.dbname_user_account_settings)).child(mUser);
            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    users setting = dataSnapshot.getValue(users.class);
                    setProfileWidgets(setting);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        });
        mMessage.setOnClickListener(v -> {
            YoYo.with(Techniques.FadeIn).duration(500).playOn(mMessage);
            Intent intent = new Intent(ViewProfileActivity.this, Chat_Activity.class);
            intent.putExtra(getString(R.string.his_UID), mUser);
            intent.putExtra("request", "no");
            startActivity(intent);
        });

    }


    private void init() {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        Query query1 = reference1.child(getString(R.string.dbname_user_account_settings)).child(mUser);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users setting = dataSnapshot.getValue(users.class);
                setProfileWidgets(setting);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos)).child(mUser);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Photo> photos = new ArrayList<>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                    photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                    photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                    photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                    photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                    photo.setDate_created(objectMap.get(getString(R.string.field_date_createdr)).toString());
                    photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                    ArrayList<Comment> comments = new ArrayList<>();
                    for (DataSnapshot dSnapshot : singleSnapshot.child(getString(R.string.field_comment)).getChildren()) {
                        Comment comment = new Comment();
                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                        comments.add(comment);
                    }
                    photo.setComments(comments);
                    List<Like> likeList = new ArrayList<Like>();
                    for (DataSnapshot dSnapshot : singleSnapshot.child(getString(R.string.field_likes)).getChildren()) {
                        Like like = new Like();
                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
                        likeList.add(like);
                    }
                    photos.add(photo);
                }
                setupImageGrid(photos);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Query Cancelled");
            }
        });
    }

    private void addToHisNotification(String hisUid, String notification) {
        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Colombo"), new SNTPClient.Listener() {
            @Override
            public void onTimeReceived(String rawDate) {
                // rawDate -> 2019-11-05T17:51:01+0530
                String str_date = rawDate;
                java.text.DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date date = null;
                try {
                    date = formatter.parse(str_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onCreateView: timestampyesss" + date.getTime());
                String timestamp = String.valueOf(date.getTime());
                //data to put in notification
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("pId", "false");
                hashMap.put("timeStamp", timestamp);
                hashMap.put("pUid", hisUid);
                hashMap.put("seen", "false");
                hashMap.put("notificaton", notification);
                hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
                        .addOnSuccessListener(aVoid -> { })
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

    private void isFolllowing() {
        mFollow.setText("Follow");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_following)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild(getString(R.string.field_user_id)).equalTo(mUser);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    mFollow.setText("Unfollow");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getFollowerCount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_follower)).child(mUser);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFollowers.setText(dataSnapshot.getChildrenCount() + " FANS");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void setupImageGrid(final ArrayList<Photo> photos) {
        imgURLsList.addAll(photos);
        Log.d(TAG, "onDataChange: size sdf"+imgURLsList.size());
        Collections.reverse(imgURLsList);
        gridRv.setAdapter(adapterGridImage);
    }

    private void getCompDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_contests)).child(mUser);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String joinedContests = String.valueOf(snapshot.child(getString(R.string.field_joined_contest)).getChildrenCount());
                String createdContests = String.valueOf(snapshot.child(getString(R.string.field_created_contest)).getChildrenCount());
                mJoined.setText(joinedContests);
                mCreated.setText(createdContests);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void setProfileWidgets(users userSetting) {
        Log.d(TAG, "onDataChange: 1234" + userSetting.toString());
        UniversalImageLoader.setImage(userSetting.getProfile_photo(), mProfilePhoto, null, "");
        mUsername.setText(userSetting.getUsername());
        mDescription.setText(userSetting.getDescription());
        mDomain.setText(userSetting.getDomain());
        mWebsite.setText(userSetting.getEmail());
        mProgressBar.setVisibility(View.GONE);
        dialog.dismiss();
    }


    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
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
