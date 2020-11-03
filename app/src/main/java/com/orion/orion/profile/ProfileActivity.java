package com.orion.orion.profile;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import com.orion.orion.R;
import com.orion.orion.dialogs.DialogPostSelection;
import com.orion.orion.models.Comment;
import com.orion.orion.models.Like;
import com.orion.orion.models.Photo;
import com.orion.orion.models.users;
import com.orion.orion.profile.Account.AccountSettingActivity;
import com.orion.orion.util.BottomNaavigationViewHelper;
import com.orion.orion.util.Permissions;
import com.orion.orion.util.UniversalImageLoader;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {

    public static final int VERIFY_PERMISSION_REQUEST = 1;
    private static final String TAG = "ProfileFragment";
    private static final int ACTIVITY_NUM = 4;
    FirebaseUser user;
    int rank = 1;
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    private ArrayList<Photo> imgURLsList;
    Uri imageUri;
    boolean isKitKat;
    //    Profile Widgets
    private ImageView menu;
    private TextView mUsername;
    private TextView mDomain;
    private CircleImageView mProfilePhoto;
    private ProgressDialog dialog;

    private ImageView mGmailLink;
    private ImageView mInstagramLink;
    private ImageView mFacebookLink;
    private ImageView mTwitterLink;
    private ImageView mWhatsappLink;

    private String gmail;
    private String instagramProfile;
    private String facebookProfile;
    private String twitterProfile;

    private TextView mPosts;
    private TextView mFans;
    private TextView mWins;
    private TextView mCreations;
    private TextView mParticipation;
    private TextView mRank;

    private TextView mDescription;
    private TextView mWebsite;
    private String whatsappNo;
    private LinearLayout share_btn;
    private RecyclerView gridRv;
    private BottomNavigationViewEx bottomNavigationView;
    private AdapterGridImage adapterGridImage;

    //    SP
    Gson gson;
    SharedPreferences sp;

    //firebase

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        dialog = ProgressDialog.show(this, "", "Loading Profile...", true);
        mContext = ProfileActivity.this;

        menu = findViewById(R.id.menu);
        mUsername = findViewById(R.id.display_name);
        mDomain = findViewById(R.id.domain);
        mProfilePhoto = findViewById(R.id.profile_photo);

        mGmailLink = findViewById(R.id.gmail_link);
        mInstagramLink = findViewById(R.id.instagram_link);
        mFacebookLink = findViewById(R.id.facebook_link);
        mTwitterLink = findViewById(R.id.twitter_link);
        mWhatsappLink = findViewById(R.id.whatsapp_link);


        mPosts = findViewById(R.id.posts);
        mFans = findViewById(R.id.fans);
        mWins = findViewById(R.id.win);
        mCreations = findViewById(R.id.creations);
        mParticipation = findViewById(R.id.participations);
        mRank = findViewById(R.id.rank);

        mDescription = findViewById(R.id.description);
        mWebsite = findViewById(R.id.website);

        share_btn = findViewById(R.id.share_skill_btn);

        gridRv = findViewById(R.id.gridRv);

        bottomNavigationView = findViewById(R.id.BottomNavViewBar);


        gridRv.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 3);
        gridRv.setLayoutManager(linearLayoutManager);
        gridRv.setDrawingCacheEnabled(true);
        gridRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(20);
        gridRv.setItemViewCacheSize(9);
        gridRv.setDrawingCacheEnabled(true);
        gridRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        gridRv.setLayoutManager(linearLayoutManager);

        imgURLsList = new ArrayList<>();


//          Initialize SharedPreference variables
        sp = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        gson = new Gson();
        setupBottomNavigationView();
        setupFirebaseAuth();
        fetchPhotosFromSp();
//        SetupGridView();

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
            Intent intent;
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

        share_btn.setOnClickListener(v -> {
            YoYo.with(Techniques.FadeIn).duration(500).playOn(share_btn);
            if (checkPermissionArray(Permissions.PERMISSIONS)) {
                DialogPostSelection dialogPostSelection = new DialogPostSelection(ProfileActivity.this);
                dialogPostSelection.show();
            } else verifyPermission(Permissions.PERMISSIONS);
        });
        menu.setOnClickListener(v -> {
            Intent intent = new Intent(this, AccountSettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    private void setUpInfoBox() {
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
        Query query = myRef.child(getString(R.string.dbname_follower)).child(user.getUid());
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
        Query query = myRef.child(getString(R.string.dbname_user_photos)).child(user.getUid());
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
        Query query = myRef.child(getString(R.string.dbname_contests)).child(user.getUid()).child(getString(R.string.created_contest));
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
        Query query = myRef.child(getString(R.string.dbname_contests)).child(user.getUid()).child(getString(R.string.joined_contest));
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
        Query query = myRef.child(getString(R.string.dbname_leaderboard)).child(user.getUid());
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
                            if (rating > userRating && !user.getUid().equals(singleSnapshot.getKey()))
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

    private void fetchPhotosFromSp() {
        String json = sp.getString("myMedia", null);
        Type type = new TypeToken<ArrayList<Photo>>() {
        }.getType();
        imgURLsList = gson.fromJson(json, type);
//        Log.d(TAG, "fetchPhotosFromSp: "+imgURLsList.size());
        if (imgURLsList == null || imgURLsList.size() == 0) {
            imgURLsList = new ArrayList<>();
            SetupGridView();
        } else {
            checkUpdate();
        }
    }

    private void checkUpdate() {
        Log.d(TAG, "checkUpdate: started");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_user_photos)).child(user.getUid());

        Log.d(TAG, "checkUpdate: user" + user.getUid());
        Query query = reference.limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (imgURLsList.get(0).getPhoto_id().equals(dataSnapshot.getKey())) {

                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getChildrenCount()==imgURLsList.size()){
                                    Log.d(TAG, "onDataChange: photoList" + imgURLsList);
                                    adapterGridImage = new AdapterGridImage(ProfileActivity.this, imgURLsList);
                                    adapterGridImage.setHasStableIds(true);
                                    gridRv.setAdapter(adapterGridImage);
                                }else{
                                    SetupGridView();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        SetupGridView();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void verifyPermission(String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, VERIFY_PERMISSION_REQUEST);
    }

    public boolean checkPermissionArray(String[] permissions) {
        for (String check : permissions) if (!checkPermissions(check)) return false;
        return true;
    }

    public boolean checkPermissions(String permission) {
        int permissionRequest = ActivityCompat.checkSelfPermission(this, permission);
        return permissionRequest == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(19)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String imgPath = "";
        if (data != null && data.getData() != null && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {
                if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        imgPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                        Intent intent = new Intent(this, PostPhotoActivity.class);
                        intent.putExtra(getString(R.string.selected_image), imgPath);
                        startActivity(intent);
                    } else {
                        Pattern DIR_SEPORATOR = Pattern.compile("/");
                        Set<String> rv = new HashSet<>();
                        String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
                        String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
                        String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
                        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
                            if (TextUtils.isEmpty(rawExternalStorage)) {
                                rv.add("/storage/sdcard0");
                            } else {
                                rv.add(rawExternalStorage);
                            }
                        } else {
                            String rawUserId;
                            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                            String[] folders = DIR_SEPORATOR.split(path);
                            String lastFolder = folders[folders.length - 1];
                            boolean isDigit = false;
                            try {
                                Integer.valueOf(lastFolder);
                                isDigit = true;
                            } catch (NumberFormatException ignored) {
                            }
                            rawUserId = isDigit ? lastFolder : "";
                            if (TextUtils.isEmpty(rawUserId)) {
                                rv.add(rawEmulatedStorageTarget);
                            } else {
                                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
                            }
                        }
                        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                            assert rawSecondaryStoragesStr != null;
                            String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                            Collections.addAll(rv, rawSecondaryStorages);
                        }
                        String[] temp = rv.toArray(new String[0]);
                        for (String s : temp) {
                            File tempf = new File(s + "/" + split[1]);
                            if (tempf.exists()) {
                                imgPath = s + "/" + split[1];
                                Intent intent = new Intent(this, PostPhotoActivity.class);
                                intent.putExtra(getString(R.string.selected_image), imgPath);
                                startActivity(intent);
                            }
                        }
                    }
                } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    String id = DocumentsContract.getDocumentId(uri);
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                    Cursor cursor = null;
                    String column = "_data";
                    String[] projection = {column};
                    try {
                        cursor = this.getContentResolver().query(contentUri, projection, null, null,
                                null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(column);
                            imgPath = cursor.getString(column_index);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{split[1]};

                    Cursor cursor = null;
                    String column = "_data";
                    String[] projection = {column};

                    try {
                        assert contentUri != null;
                        cursor = this.getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(column);
                            imgPath = cursor.getString(column_index);
                            Intent intent = new Intent(this, PostPhotoActivity.class);
                            intent.putExtra(getString(R.string.selected_image), imgPath);
                            startActivity(intent);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                } else if ("com.google.android.apps.docs.storage".equals(uri.getAuthority())) {
                    Intent intent = new Intent(this, PostPhotoActivity.class);
                    intent.putExtra(getString(R.string.selected_image), imgPath);
                    startActivity(intent);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                Cursor cursor = null;
                String column = "_data";
                String[] projection = {column};

                try {
                    cursor = this.getContentResolver().query(uri, projection, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndexOrThrow(column);
                        imgPath = cursor.getString(column_index);
                        Intent intent = new Intent(this, PostPhotoActivity.class);
                        intent.putExtra(getString(R.string.selected_image), imgPath);
                        startActivity(intent);
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                imgPath = uri.getPath();
                Intent intent = new Intent(this, PostPhotoActivity.class);
                intent.putExtra(getString(R.string.selected_image), imgPath);
                startActivity(intent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void SetupGridView() {
        final ArrayList<Photo> photos = new ArrayList<>();
       imgURLsList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                Log.d(TAG, "onDataChange: imgURLsList.size()" + imgURLsList.size());
                imgURLsList.addAll(photos);
                Log.d(TAG, "onDataChange: imgURLsList.size()" + imgURLsList.size());
                Collections.reverse(imgURLsList);
                //    Add newly Created ArrayList to Shared Preferences
                SharedPreferences.Editor editor = sp.edit();
                String json = gson.toJson(imgURLsList);
                editor.putString("myMedia", json);
                editor.apply();
                adapterGridImage = new AdapterGridImage(ProfileActivity.this, imgURLsList);
                adapterGridImage.setHasStableIds(true);
                gridRv.setAdapter(adapterGridImage);//
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Query Cancelled");
            }
        });
    }

    private void setProfileWidgets(users userSetting) {
        Log.d(TAG, "onDataChange: " + userSetting.toString());
        UniversalImageLoader.setImage(userSetting.getProfile_photo(), mProfilePhoto, null, "");
        mUsername.setText(userSetting.getUsername());
        mDomain.setText(userSetting.getDomain());

        if (userSetting.getDescription() == null || userSetting.getDescription().equals(""))
            mDescription.setVisibility(View.GONE);
        else {
            mDescription.setVisibility(View.VISIBLE);
            mDescription.setText(userSetting.getDescription());
        }

        if (userSetting.getEmail() == null || userSetting.getEmail().equals("")) {
            mWebsite.setVisibility(View.GONE);
            mGmailLink.setClickable(false);
            mGmailLink.setAlpha(0.5f);
        } else {
            mWebsite.setVisibility(View.VISIBLE);
            mWebsite.setText(userSetting.getEmail());
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

    private void setupBottomNavigationView() {
        Log.d(TAG, " setupBottomNavigationView:setting up BottomNavigationView");
        BottomNaavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNaavigationViewHelper.enableNavigation(this, this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);

    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //retrieve user information from the database
                        setProfileWidgets(Objects.requireNonNull(dataSnapshot.child(getString(R.string.dbname_users)).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).getValue(users.class)));
                        //retrieve image for the user in question
                        setUpInfoBox();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else Log.d(TAG, "onAuthStateChanged:signed_out");
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
