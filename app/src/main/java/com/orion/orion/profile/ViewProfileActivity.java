package com.orion.orion.profile;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
import com.orion.orion.Adapters.AdapterGridImage;
import com.orion.orion.R;
import com.orion.orion.home.Chat_Activity;
import com.orion.orion.models.Comment;
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
    boolean isFollowing=false;
    int rank = 1;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;


    //View Profile Widgets
    private ImageView backButton;
    private TextView mUsername;
    private TextView mDomain;
    private FirebaseDatabase mFirebaseDatabase;
    private CircleImageView mProfilePhoto;
    private ImageView mGmailLink;
    private ImageView mInstagramLink;
    private ImageView mFacebookLink;
    private ImageView mTwitterLink;
    private ImageView mWhatsappLink;
    private String gmail;
    private String instagramProfile;
    private String facebookProfile;
    private String twitterProfile;

    private Button mFollow;
    private Button mMessage;
    private String whatsappNo;
    private TextView mPosts;
    private TextView mFans;
    private TextView mWins;
    private TextView mCreations;
    private TextView mParticipation;

    private TextView mDisplayName;
    private TextView mDescription;
    private TextView mWebsite;
    private TextView mRank;

    private AdapterGridImage adapterGridImage;
    private RecyclerView gridRv;
    private String mUser;


    private boolean notify = false;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_viewprofile);
        dialog = ProgressDialog.show(this, "", "Loading Profile...", true);
        mContext = this;
        mFirebaseMethods = new FirebaseMethods(mContext);
        backButton = findViewById(R.id.back);

        mUsername = findViewById(R.id.username);
        mDomain = findViewById(R.id.domain);
        mProfilePhoto = findViewById(R.id.profile_photo);

        mGmailLink = findViewById(R.id.gmail_link);
        mInstagramLink = findViewById(R.id.instagram_link);
        mFacebookLink = findViewById(R.id.facebook_link);
        mTwitterLink = findViewById(R.id.twitter_link);
        mWhatsappLink = findViewById(R.id.whatsapp_link);



        mFollow = findViewById(R.id.followButton);
        mMessage = findViewById(R.id.messageButton);

        mPosts = findViewById(R.id.posts);
        mFans = findViewById(R.id.fans);
        mWins = findViewById(R.id.win);
        mCreations = findViewById(R.id.creations);
        mParticipation = findViewById(R.id.participations);
        mRank = findViewById(R.id.rank);

        mDisplayName = findViewById(R.id.display_name);
        mDescription = findViewById(R.id.description);
        mWebsite = findViewById(R.id.website);

        gridRv = findViewById(R.id.gridRv);

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

        backButton.setOnClickListener(v -> {
            getSupportFragmentManager().popBackStack();
            finish();
        });

        mFollow.setOnClickListener(v -> {
            YoYo.with(Techniques.FadeIn).duration(500).playOn(mMessage);
            if (isFollowing) {
                isFollowing=false;
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
                    if (!ulist.contains(mUser)){
                        ulist.add(mUser);

                    }
                }
//                save update list
                editor=sp.edit();
                json =gson.toJson(ulist);
                editor.putString("removefollowing",json);
                editor.apply();


//              update following list
                json =sp.getString("addfollowing",null);
                type= new TypeToken<ArrayList<String>>() {}.getType();
                ArrayList<String> ulist2= new ArrayList<String>();
                ulist2=gson.fromJson(json,type);
                if (ulist2==null){

                }else{
                    if (ulist2.contains(mUser)){
                        ulist2.remove(mUser);
//                save update list
                        editor=sp.edit();
                        json =gson.toJson(ulist2);
                        editor.putString("addfollowing",json);
                        editor.apply();
                    }
                }



                FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_following)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(mUser).removeValue();
                FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_follower)).child(mUser).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_users)).child(mUser).child(getString(R.string.changedFollowers)).setValue("true");
                mFollow.setText("Follow");

            } else {


//               addfollowing list
                isFollowing=true;
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
                    if (!list.contains(mUser)) {
                        list.add(mUser);
                    }
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
                    if (!ulist.contains(mUser)){
                        ulist.add(mUser);

                    }


                }
//                save update list
                editor=sp.edit();
                json =gson.toJson(ulist);
                editor.putString("addfollowing",json);
                editor.apply();


//              update following list
                json =sp.getString("removefollowing",null);
                type= new TypeToken<ArrayList<String>>() {}.getType();
                ArrayList<String> ulist2= new ArrayList<String>();
                ulist2=gson.fromJson(json,type);
                if (ulist2==null){

                }else{
                    if (ulist2.contains(mUser)){
                        ulist2.remove(mUser);
//                save update list
                        editor=sp.edit();
                        json =gson.toJson(ulist2);
                        editor.putString("removefollowing",json);
                        editor.apply();
                    }
                }


                notify = true;
                FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_following)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(mUser).child(getString(R.string.field_user_id)).setValue(mUser);
                FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_follower)).child(mUser).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getString(R.string.field_user_id)).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbname_users)).child(mUser).child(getString(R.string.changedFollowers)).setValue("true");
                mFollow.setText("Unfollow");
                final DatabaseReference data = myRef.child(getString(R.string.dbname_users)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                    assert setting != null;
                    setProfileWidgets(setting);
                    setUpInfoBox();
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

        mGmailLink.setOnClickListener(v -> {
            if (gmail != null && !gmail.equals("")) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setPackage("com.google.android.gm");
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{gmail});
                i.putExtra(Intent.EXTRA_SUBJECT, "Orion");
                i.putExtra(Intent.EXTRA_TEXT, "We from Orion would like to help and connect u with others out there");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mInstagramLink.setOnClickListener(v -> {
            Uri uri = Uri.parse("http://instagram.com/_u/" + instagramProfile);
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
            likeIng.setPackage("com.instagram.android");
            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/xxx")));
            }
        });
        mFacebookLink.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/426253597411506"));
                startActivity(intent);
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/appetizerandroid")));
            }
        });
        mTwitterLink.setOnClickListener(v -> {
            Intent intent = null;
            try {
                this.getPackageManager().getPackageInfo("com.twitter.android", 0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + twitterProfile));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } catch (Exception e) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/USERID_OR_PROFILENAME"));
            }
            this.startActivity(intent);
        });
        mWhatsappLink.setOnClickListener(v -> {
            if (whatsappNo != null && !whatsappNo.equals("")) {
                String url = "https://api.whatsapp.com/send?phone=" + whatsappNo;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private void setUpInfoBox() {
        Log.d(TAG, "setUpInfoBox: started");
        getFans();
        getPosts();
        getWins();
        getCreations();
        getParticipation();
        mRank.setText(String.valueOf(rank));
        getRank();
        dialog.dismiss();
    }

    private void getFans() {
        Query query = myRef.child(getString(R.string.dbname_follower)).child(mUser);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size = (int) snapshot.getChildrenCount();
                Log.d(TAG, "setUpInfoBox: fansCount" + size);
                if (size == 0) mFans.setText("0");
                else mFans.setText(String.valueOf(size));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mFans.setText("?");
            }
        });
    }

    private void getPosts() {
        Query query = myRef.child(getString(R.string.dbname_user_photos)).child(mUser);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size = (int) snapshot.getChildrenCount();
                Log.d(TAG, "setUpInfoBox: postsCount" + size);
                if (size == 0) mPosts.setText("0");
                else mPosts.setText(String.valueOf(size));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mPosts.setText("?");
            }
        });
    }

    private void getWins() {
    }

    private void getCreations() {
        Query query = myRef.child(getString(R.string.dbname_contests)).child(mUser).child(getString(R.string.created_contest));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size = (int) snapshot.getChildrenCount();
                Log.d(TAG, "setUpInfoBox: creations" + size);
//                mCreations.setText((int) size);
                if (size == 0) mCreations.setText("0");
                else mCreations.setText(String.valueOf(size));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mCreations.setText("?");
            }
        });
    }

    private void getParticipation() {
        Query query = myRef.child(getString(R.string.dbname_contests)).child(mUser).child(getString(R.string.joined_contest));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int size = (int) snapshot.getChildrenCount();
                Log.d(TAG, "setUpInfoBox: participationsCount" + size);
//                mParticipation.setText((int) size);
                if (size == 0) mParticipation.setText("0");
                else mParticipation.setText(String.valueOf(size));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mParticipation.setText("?");
            }
        });
    }

    private void getRank() {
        Query query = myRef.child(getString(R.string.dbname_leaderboard)).child(mUser);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int userRating = (int) (long) snapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_post)).getValue()
                        + (int) (long) snapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_followers)).getValue()
                        + (int) (long) snapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_contest)).getValue();
                Query query1 = myRef.child(getString(R.string.dbname_leaderboard));
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                            int rating = (int) (long) singleSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_post)).getValue()
                                    + (int) (long) singleSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_followers)).getValue()
                                    + (int) (long) singleSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_contest)).getValue();
                            if (rating > userRating && !mUser.equals(singleSnapshot.getKey()))
                                updateRank();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        mRank.setText("?");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mRank.setText("?");
            }
        });
    }

    private void updateRank() {
        rank += 1;
        mRank.setText(String.valueOf(rank));
    }

    private void init() {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        Query query1 = reference1.child(getString(R.string.dbname_user_account_settings)).child(mUser);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users setting = dataSnapshot.getValue(users.class);
                assert setting != null;
                setProfileWidgets(setting);
                setUpInfoBox();
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
                    Log.d(TAG, "onBindViewHolder: " + dataSnapshot.getChildrenCount());
                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (Map<String, Object>) singleSnapshot.getValue();
                    Log.d(TAG, "onDataChange: objectMap" + objectMap);
                    try {
                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_createdr)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                        if (objectMap.get(getString(R.string.thumbnail)) != null)
                            photo.setThumbnail(objectMap.get(getString(R.string.thumbnail)).toString());
                        photo.setType(objectMap.get(getString(R.string.type)).toString());
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
                    } catch (NullPointerException e) {
                        Log.e(TAG, "null pointer exception" + e.getMessage());
                    }
                }
                imgURLsList.addAll(photos);
                Log.d(TAG, "onDataChange: size sdf" + imgURLsList.size());
                Collections.reverse(imgURLsList);
//                adapterGridImage = new AdapterGridImage(mContext, imgURLsList);
//                adapterGridImage.setHasStableIds(true);
                gridRv.setAdapter(adapterGridImage);
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
                    isFollowing = mFollow.getText().equals("Unfollow");

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void setProfileWidgets(users userSetting) {
        Log.d(TAG, "onDataChange: " + userSetting.toString());
        UniversalImageLoader.setImage(userSetting.getProfile_photo(), mProfilePhoto, null, "");
        mUsername.setText(userSetting.getUsername());
        mDomain.setText(userSetting.getDomain());

        if(userSetting.getDisplay_name() ==null || userSetting.getDisplay_name().equals(""))
            mDisplayName.setVisibility(View.GONE);
        else {
            mDisplayName.setText(userSetting.getDisplay_name());
        }
        if (userSetting.getDescription() == null || userSetting.getDescription().equals(""))
            mDescription.setVisibility(View.GONE);
        else {
            mDescription.setVisibility(View.VISIBLE);
            mDescription.setText(userSetting.getDescription());
        }

        if (userSetting.getEmail() == null || userSetting.getEmail().equals("")) {
//            mWebsite.setVisibility(View.GONE);
            mGmailLink.setClickable(false);
            mGmailLink.setAlpha(0.5f);
        } else {
//            mWebsite.setVisibility(View.VISIBLE);
//            mWebsite.setText(userSetting.getEmail());
            gmail = userSetting.getEmail();
        }

        if (userSetting.getInstagram() == null || userSetting.getInstagram().equals("")) {
            mInstagramLink.setClickable(false);
            mInstagramLink.setAlpha(0.5f);
        } else {
            instagramProfile = userSetting.getInstagram();
        }

        if (userSetting.getFacebook() == null || userSetting.getFacebook().equals("")) {
            mFacebookLink.setClickable(false);
            mFacebookLink.setAlpha(0.5f);
        } else {
            facebookProfile = userSetting.getFacebook();

        }
        if (userSetting.getTwitter() == null || userSetting.getTwitter().equals("")) {
            mTwitterLink.setClickable(false);
            mTwitterLink.setAlpha(0.5f);
        } else {
            twitterProfile = userSetting.getTwitter();
        }

        if (userSetting.getWhatsapp() == null || userSetting.getWhatsapp().equals("")) {
            mWhatsappLink.setClickable(false);
            mWhatsappLink.setAlpha(0.5f);
        } else {
            whatsappNo = userSetting.getWhatsapp();
        }
//        dialog.dismiss();
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
