package com.orion.orion.profile;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orion.orion.R;
import com.orion.orion.login.login;
import com.orion.orion.models.Photo;
import com.orion.orion.util.FilePaths;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.ImageManager;
import com.orion.orion.util.Permissions;
import com.orion.orion.util.StringManipilation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class PostPhotoActivity extends AppCompatActivity {


    private static final String TAG = "PostPhotoActivity";
    private final Context mContext = PostPhotoActivity.this;
    private static final int VERIFY_PERMISSION_REQUEST = 1;

    //firebase
    private RelativeLayout rootView;
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private ExtendedFloatingActionButton fab;

    private ProgressDialog dialog;
    private EditText mCaption;

    private ImageView image;
    private int imageCount = 0;
    private String imgURL;

    public PostPhotoActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_photo);

        setupFirebaseAuth();
        mFirebaseMethods = new FirebaseMethods(PostPhotoActivity.this);

        fab = findViewById(R.id.fab);
        ImageView backArrow = findViewById(R.id.backarrow);
        TextView post = findViewById(R.id.post);
        mCaption = findViewById(R.id.inputCaption);
        image = findViewById(R.id.imageshare);

        rootView = findViewById(R.id.relLayout);
        rootView.setOnClickListener(v -> {
            if(v.getId()!=mCaption.getId()){
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
            }
        });
        fab.setOnClickListener(v -> {
            fab.setEnabled(false);
            if (checkPermissionArray(Permissions.PERMISSIONS)) {
                fab.setEnabled(false);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            } else verifyPermission(Permissions.PERMISSIONS);
        });
        backArrow.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are u sure")
                    .setMessage("You will discard all the changes u made?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> finish())
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                    .show();
        });
        post.setOnClickListener(v -> {
            String caption = mCaption.getText().toString();
            Log.d(TAG, "onCreate: imgURL" + imgURL);
            Log.d(TAG, "onCreate: imageCount" + imageCount);
            if (imgURL != null && !imgURL.equals("")) {
                dialog = ProgressDialog.show(mContext, "", "Uploading...  ", true);
                uploadNewPhoto(caption, imageCount, imgURL);
            } else {
                Toast.makeText(PostPhotoActivity.this, "Please Select Image First.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void uploadNewPhoto(String caption, int imageCount, String imgURL) {
        FilePaths filepaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filepaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/post" + (imageCount + 1));
        String imgUrl2 = mFirebaseMethods.compressImage(imgURL);
        Bitmap bm = ImageManager.getBitmap(imgUrl2);
        byte[] bytes;
        bytes = ImageManager.getBytesFromBitmap(bm, 100);
        UploadTask uploadTask;
        uploadTask = storageReference.putBytes(bytes);
        Log.d(TAG, "uploadNewPhoto: uploadTask" + uploadTask);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            dialog.dismiss();
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Toast.makeText(mContext, "Photo Upload success", Toast.LENGTH_SHORT).show();
                addPhotoToDatabase(caption, uri.toString());
            });
            mContext.startActivity(new Intent(mContext, ProfileActivity.class));
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Log.d(TAG, "onFailure: Photo Upload Failed");
            Toast.makeText(mContext, "Photo Upload failed", Toast.LENGTH_SHORT).show();
            mContext.startActivity(new Intent(mContext, PostPhotoActivity.class));
        }).addOnProgressListener(taskSnapshot -> {
            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            ProgressDialog.show(mContext, "", "Uploading... - " + String.format("%.0f", progress) + "%", true);
            Log.d(TAG, "onProgress: upload progress" + progress + "% done");
        });
    }


    private void addPhotoToDatabase(String caption, String url) {
        Log.d(TAG, "addPhtotto database: adding photo to database");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        String tags = StringManipilation.getTags(caption);
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_user_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCap(caption);
        photo.setDc(sdf.format(new Date()));
        photo.setIp(url);
        photo.setTg(tags);
        photo.setUi(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        photo.setPi(newPhotoKey);
        photo.setT("");
        photo.setTy("photo");
        assert newPhotoKey != null;
        myRef.child(mContext.getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_follower)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                    myRef.child(mContext.getString(R.string.dbname_users)).child(Objects.requireNonNull(snapshot1.getKey()))
                            .child(mContext.getString(R.string.post_updates))
                            .child(newPhotoKey)
                            .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getPathFromUri(final Context context, final Uri uri) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type))
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                else if ("video".equals(type))
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                else if ("audio".equals(type))
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) return uri.getPath();
        return null;
    }


    @TargetApi(19)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        fab.setEnabled(true);
        String imgPath = "";
        if (data != null && data.getData() != null && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                imgPath = getPathFromUri(mContext, uri);
                Log.d(TAG, "onActivityResult: path: " + imgPath);
                Log.d(TAG, "onActivityResult: uri: " + uri);
                setImage(imgPath);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImage(String imgPath) {
        Log.d(TAG, "setImage next " + imgPath);
        imgURL = imgPath;
        String mAppend = "file:/";
        Glide.with(getApplicationContext())
                .load(imgURL)
                .placeholder(R.drawable.load)
                .error(R.drawable.default_image2)
                .placeholder(R.drawable.load)
                .into(image);
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

    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                Log.d(TAG, "onAuthStateChanged: navigating to login");
                SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                new android.app.AlertDialog.Builder(mContext)
                        .setTitle("No user logon found")
                        .setMessage("We will be logging u out. \n Please try to log in again")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            Intent intent = new Intent(mContext, login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            settings.edit().clear().apply();
                            if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
                            startActivity(intent);
                        })
                        .show();
            } else Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());
        };
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
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
        if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
    }
}
