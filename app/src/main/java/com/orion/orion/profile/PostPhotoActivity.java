package com.orion.orion.profile;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.orion.orion.models.Photo;
import com.orion.orion.util.FilePaths;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.ImageManager;
import com.orion.orion.util.StringManipilation;
import com.orion.orion.util.UniversalImageLoader;

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

public class PostPhotoActivity extends AppCompatActivity {


    private static final String TAG = "PostPhotoActivity";
    private final Context mContext = PostPhotoActivity.this;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    private EditText mCaption;

    private String mAppend = "file:/";
    private ImageView backArrow;
    private TextView post;
    private ImageView image;
    private int imageCount = 0;
    private String imgURL;
    private ExtendedFloatingActionButton fab;

    public PostPhotoActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_photo);

        setupFirebaseAuth();
        mFirebaseMethods = new FirebaseMethods(PostPhotoActivity.this);

        fab = findViewById(R.id.fab);
        backArrow = findViewById(R.id.backarrow);
        post = findViewById(R.id.post);
        mCaption = findViewById(R.id.inputCaption);
        image = findViewById(R.id.imageshare);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
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
            Toast.makeText(PostPhotoActivity.this, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();
            String caption = mCaption.getText().toString();
            Log.d(TAG, "onCreate: imgURL" + imgURL);
            Log.d(TAG, "onCreate: imageCount" + imageCount);
            uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imgURL);
            mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imgURL, null);
        });
    }

    private void uploadNewPhoto(String string, String caption, int imageCount, String imgURL) {
        FilePaths filepaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filepaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/post" + (imageCount + 1));
        Bitmap bm = ImageManager.getBitmap(imgURL);
        File file = new File(imgURL);
        long length = file.length() / 1024;

        byte[] bytes;
        if (length < 200) bytes = ImageManager.getBytesFromBitmap(bm, 100);
        else if (length < 500) bytes = ImageManager.getBytesFromBitmap(bm, 65);
        else if (length < 800) bytes = ImageManager.getBytesFromBitmap(bm, 45);
        else bytes = ImageManager.getBytesFromBitmap(bm, 25);
        UploadTask uploadTask;
        uploadTask = storageReference.putBytes(bytes);
        ProgressDialog dialog = ProgressDialog.show(mContext, "", "Uploading... - ", true);

//            Log.d(TAG, "uploadNewPhoto: photoType"+photoType);
//            Log.d(TAG, "uploadNewPhoto: caption"+caption);
//            Log.d(TAG, "uploadNewPhoto: count"+count);
//            Log.d(TAG, "uploadNewPhoto: imgURL"+imgURL);
//            Log.d(TAG, "uploadNewPhoto: filepaths"+filepaths);
//            Log.d(TAG, "uploadNewPhoto: user_id"+user_id);
//            Log.d(TAG, "uploadNewPhoto: StorageReference"+storageReference);
//            Log.d(TAG, "uploadNewPhoto: bm"+bm);
//            Log.d(TAG, "uploadNewPhoto: bytes"+ Arrays.toString(bytes));
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
            Toast.makeText(mContext, "Photo Upload Progress" + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
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
        photo.setCaption(caption);
        photo.setDate_created(sdf.format(new Date()));
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        photo.setPhoto_id(newPhotoKey);
        photo.setThumbnail("");
        photo.setType("photo");
        assert newPhotoKey != null;
        myRef.child(mContext.getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_follower)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                    myRef.child(mContext.getString(R.string.dbname_users)).child(Objects.requireNonNull(snapshot1.getKey())).child(mContext.getString(R.string.post_updates)).child(newPhotoKey).setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @TargetApi(19)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String imgPath = "";
        if (data != null && data.getData() != null && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (DocumentsContract.isDocumentUri(this, uri)) {
                if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        imgPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                        setImage(imgPath);
                    } else {
                        Pattern DIR_SEPORATOR = Pattern.compile("/");
                        Set<String> rv = new HashSet<>();
                        String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
                        String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
                        String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
                        if (TextUtils.isEmpty(rawEmulatedStorageTarget))
                            if (TextUtils.isEmpty(rawExternalStorage)) rv.add("/storage/sdcard0");
                            else rv.add(rawExternalStorage);
                        else {
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
                            if (TextUtils.isEmpty(rawUserId)) rv.add(rawEmulatedStorageTarget);
                            else rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
                        }
                        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                            String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                            Collections.addAll(rv, rawSecondaryStorages);
                        }
                        String[] temp = rv.toArray(new String[0]);
                        for (String s : temp) {
                            File tempf = new File(s + "/" + split[1]);
                            if (tempf.exists()) {
                                imgPath = s + "/" + split[1];
                                setImage(imgPath);
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
                        cursor = this.getContentResolver().query(contentUri, projection, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(column);
                            imgPath = cursor.getString(column_index);
                            setImage(imgPath);
                        }
                    } finally {
                        if (cursor != null) cursor.close();
                    }
                } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type))
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    else if ("video".equals(type))
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    else if ("audio".equals(type))
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
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
                            setImage(imgPath);
                        }
                    } finally {
                        if (cursor != null) cursor.close();
                    }
                } else if ("com.google.android.apps.docs.storage".equals(uri.getAuthority()))
                    setImage(imgPath);
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                Cursor cursor = null;
                String column = "_data";
                String[] projection = {column};
                try {
                    cursor = this.getContentResolver().query(uri, projection, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) setImage(imgPath);
                } finally {
                    if (cursor != null) cursor.close();
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                imgPath = uri.getPath();
                setImage(imgPath);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImage(String imgPath) {
        Log.d(TAG, "setImage next " + imgPath);
        imgURL = imgPath;
        UniversalImageLoader.setImage(imgURL, image, null, mAppend);
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
