package com.orion.orion.profile;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.Permissions;
import com.orion.orion.util.UniversalImageLoader;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity{

    public static final int VERIFY_PERMISSION_REQUEST = 1;
    private static final String TAG = "ProfileFragment";
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;
    private static final int PICK_IMAGE = 100;

    RecyclerView gridRv;
    ArrayList<Photo> imgURLsList;
    Uri imageUri;
    boolean isKitKat;
    //    Profile Widgets
    private ProgressBar mProgressBar;
    private ImageView menu;
    private CircleImageView mProfilePhoto;
    private TextView mUsername;
    private TextView mFollowers;
    private TextView mDomain;
    private Button editProfile;
    private TextView mCreated;
    private TextView mJoined;
    private TextView mWon;
    private TextView mDescription;
    private TextView mWebsite;

    //    SP
    Gson gson;
    SharedPreferences sp;
    private BottomNavigationViewEx bottomNavigationView;
    private LinearLayout share_btn;
    private AdapterGridImage adapterGridImage;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private ProgressDialog dialog;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        mProgressBar = findViewById(R.id.profileprogressbar);
        dialog=ProgressDialog.show(this,"","Loading Profile...",true);

        mProfilePhoto = (CircleImageView) findViewById(R.id.profile_photo);
        mUsername = (TextView) findViewById(R.id.display_name);
        mFollowers = (TextView) findViewById(R.id.follower);
        mDomain = (TextView)findViewById(R.id.domain);

        mCreated=(TextView) findViewById(R.id.created_contests);
        mJoined=(TextView) findViewById(R.id.joined_contests);
        mWon=(TextView) findViewById(R.id.contests_won);

        editProfile=(Button) findViewById(R.id.texteditprofile);

        mDescription = (TextView) findViewById(R.id.description);
        mWebsite = (TextView) findViewById(R.id.website);
        menu=(ImageView) findViewById(R.id.menu);

//          Initialize SharedPreference variables
        sp =getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        gson = new Gson();
        share_btn = (LinearLayout) findViewById(R.id.share_skill_btn);
        gridRv = (RecyclerView) findViewById(R.id.gridRv);
        gridRv.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 3);
        gridRv.setDrawingCacheEnabled(true);
        gridRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(20);
        gridRv.setLayoutManager(linearLayoutManager);
        imgURLsList = new ArrayList<>();

        bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.BottomNavViewBar);
        mFirebaseMethods = new FirebaseMethods(this);

        Log.d(TAG, "onCreateView:started");
        setupBottomNavigationView();
        setupFirebaseAuth();
        fetchPhotosFromSp();
//        SetupGridView();
        getFollowerCount();
        getCompDetails();

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
        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, AccountSettingActivity.class);
            intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }
    private void fetchPhotosFromSp() {
        String json = sp.getString("myMedia", null);
        Type type = new TypeToken<ArrayList<Photo>>() {
        }.getType();
        imgURLsList = gson.fromJson(json, type);
        if (true) {    //        if no arrayList is present
            imgURLsList = new ArrayList<>();
            SetupGridView();             //            make new Arraylist

        } else {
            checkUpdate();       //         Check if new post is there

        }
    }
    private void checkUpdate() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    if (imgURLsList.get(0).getPhoto_id().equals(dataSnapshot.getKey())){

                        adapterGridImage = new AdapterGridImage(ProfileActivity.this, imgURLsList);
                        adapterGridImage.setHasStableIds(true);
                        gridRv.setAdapter(adapterGridImage);
                    }else{
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

        for (String check : permissions) {
            if (!checkPermissions(check)) {
                return false;
            }
        }
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
                            String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                            Collections.addAll(rv, rawSecondaryStorages);
                        }
                        String[] temp = rv.toArray(new String[rv.size()]);
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
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
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
                        cursor =this.getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
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
    private void getFollowerCount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_follower)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mFollowers.setText(dataSnapshot.getChildrenCount() +" FANS");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getCompDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_contests)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String joinedContests= String.valueOf(snapshot.child(getString(R.string.field_joined_contest)).getChildrenCount());
                String createdContests= String.valueOf(snapshot.child(getString(R.string.field_created_contest)).getChildrenCount());
                mJoined.setText(joinedContests);
                mCreated.setText(createdContests);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void SetupGridView() {
        final ArrayList<Photo> photos = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onBindViewHolder: "+dataSnapshot.getChildrenCount());
                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                    try {
                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_createdr)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                        if (objectMap.get(getString(R.string.thumbnail))!=null) photo.setThumbnail(objectMap.get(getString(R.string.thumbnail)).toString());
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
        Log.d(TAG, "onDataChange: 1234" + userSetting.toString());
        UniversalImageLoader.setImage(userSetting.getProfile_photo(), mProfilePhoto, null, "");
        mUsername.setText(userSetting.getUsername());
        mDescription.setText(userSetting.getDescription());
        mDomain.setText(userSetting.getDomain());
        mWebsite.setText(userSetting.getEmail());
        mProgressBar.setVisibility(View.GONE);
        dialog.dismiss();
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
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
            else Log.d(TAG, "onAuthStateChanged:signed_out");
        };
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //retrieve user information from the database
                setProfileWidgets(Objects.requireNonNull(dataSnapshot.child(getString(R.string.dbname_users)).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).getValue(users.class)));
                //retrieve image for the user in question
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
