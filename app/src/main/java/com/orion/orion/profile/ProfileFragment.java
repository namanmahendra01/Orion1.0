package com.orion.orion.profile;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
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
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.Adapters.AdapterGridImage;
import com.orion.orion.R;
import com.orion.orion.models.Comment;
import com.orion.orion.models.Like;
import com.orion.orion.models.Photo;
import com.orion.orion.models.users;
import com.orion.orion.profile.Account.AccountSettingActivity;
import com.orion.orion.share.NextActivity;
import com.orion.orion.util.BottomNaavigationViewHelper;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.Permissions;
import com.orion.orion.util.UniversalImageLoader;

import java.io.File;
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

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

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

    private BottomNavigationViewEx bottomNavigationView;
    private LinearLayout share_btn;
    private AdapterGridImage adapterGridImage;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    ProgressDialog dialog;
    public ProfileFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mProgressBar = view.findViewById(R.id.profileprogressbar);
        dialog=ProgressDialog.show(getActivity(),"","Loading Profile...",true);

        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mUsername = (TextView) view.findViewById(R.id.display_name);
        mFollowers = (TextView) view.findViewById(R.id.follower);
        mDomain = (TextView) view.findViewById(R.id.domain);

        mCreated=(TextView) view.findViewById(R.id.created_contests);
        mJoined=(TextView) view.findViewById(R.id.joined_contests);
        mWon=(TextView) view.findViewById(R.id.contests_won);

        editProfile=(Button) view.findViewById(R.id.texteditprofile);

        mDescription = (TextView) view.findViewById(R.id.description);
        mWebsite = (TextView) view.findViewById(R.id.website);
        menu=(ImageView) view.findViewById(R.id.menu);

        share_btn = (LinearLayout) view.findViewById(R.id.share_skill_btn);
        gridRv = (RecyclerView) view.findViewById(R.id.gridRv);
        gridRv.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        gridRv.setLayoutManager(linearLayoutManager);
        imgURLsList = new ArrayList<>();
        adapterGridImage = new AdapterGridImage(getContext(), imgURLsList);
        gridRv.setAdapter(adapterGridImage);

        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.BottomNavViewBar);
        mFirebaseMethods = new FirebaseMethods(getActivity());


        Log.d(TAG, "onCreateView:started");
        setupBottomNavigationView();
        setupFirebaseAuth();
        SetupGridView();
        getFollowerCount();
        getCompDetails();


        share_btn.setOnClickListener(v -> {
            YoYo.with(Techniques.FadeIn).duration(500).playOn(share_btn);
            if (checkPermissionArray(Permissions.PERMISSIONS)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    isKitKat = true;
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                } else {
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});

                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                }
            } else {
                verifyPermission(Permissions.PERMISSIONS);

            }

        });

        menu.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AccountSettingActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });


        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AccountSettingActivity.class);
            intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        return view;
    }



    public void verifyPermission(String[] permissions) {
        ActivityCompat.requestPermissions(getActivity(), permissions, VERIFY_PERMISSION_REQUEST);
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

        int permissionRequest = ActivityCompat.checkSelfPermission(getActivity(), permission);
        return permissionRequest == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(19)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String imgPath = "";
        if (data != null && data.getData() != null && resultCode == RESULT_OK) {
            boolean isImageFromGoogleDrive = false;
            Uri uri = data.getData();

            if (isKitKat && DocumentsContract.isDocumentUri(getActivity(), uri)) {
                if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        imgPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                        Intent intent = new Intent(getActivity(), NextActivity.class);
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
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                rawUserId = "";
                            } else {
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
                            }
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
                                Intent intent = new Intent(getActivity(), NextActivity.class);
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
                        cursor = getActivity().getContentResolver().query(contentUri, projection, null, null,
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
                        cursor = getActivity().getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(column);
                            imgPath = cursor.getString(column_index);
                            Intent intent = new Intent(getActivity(), NextActivity.class);
                            intent.putExtra(getString(R.string.selected_image), imgPath);
                            startActivity(intent);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                } else if ("com.google.android.apps.docs.storage".equals(uri.getAuthority())) {
                    isImageFromGoogleDrive = true;
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    intent.putExtra(getString(R.string.selected_image), imgPath);
                    startActivity(intent);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                Cursor cursor = null;
                String column = "_data";
                String[] projection = {column};

                try {
                    cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndexOrThrow(column);
                        imgPath = cursor.getString(column_index);
                        Intent intent = new Intent(getActivity(), NextActivity.class);
                        intent.putExtra(getString(R.string.selected_image), imgPath);
                        startActivity(intent);
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                imgPath = uri.getPath();
                Intent intent = new Intent(getActivity(), NextActivity.class);
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
        Query query = reference
                .child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                    Photo photo = new Photo();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                    try {


                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());

                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());

                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());

                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());

                        photo.setDate_created(objectMap.get(getString(R.string.field_date_createdr)).toString());

                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        ArrayList<Comment> comments = new ArrayList<>();

                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comment)).getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);

                        }
                        photo.setComments(comments);
                        List<Like> likeList = new ArrayList<Like>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_likes)).getChildren()) {
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
                Log.d(TAG, "onDataChange: size sdf"+imgURLsList.size());
                gridRv.setAdapter(adapterGridImage);
//


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
        BottomNaavigationViewHelper.enableNavigation(getActivity(), getActivity(), bottomNavigationView);
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

            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
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
