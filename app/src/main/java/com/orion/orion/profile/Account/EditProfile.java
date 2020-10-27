package com.orion.orion.profile.Account;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orion.orion.R;
import com.orion.orion.models.users;
import com.orion.orion.profile.PostPhotoActivity;
import com.orion.orion.profile.ProfileActivity;
import com.orion.orion.util.FilePaths;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.ImageManager;
import com.orion.orion.util.Permissions;
import com.orion.orion.util.UniversalImageLoader;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class EditProfile extends AppCompatActivity {
    private static final String TAG = "EditProfile";
    private static final int VERIFY_PERMISSION_REQUEST = 1;

    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private String userID;
    private FirebaseMethods mFirebaseMethods;
    private Context mContext;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ImageView backarrow;
    private ImageView checkmark;
    private ImageView mProfilephoto;
    private TextView mchangeprofilephoto;

    private String imgURL;
    private String mAppend = "file:/";
    private boolean photoChanged = false;

    private EditText mDisplayname;
    private EditText mUsername;
    private EditText mdescription;

    private LinearLayout mGmailLink;
    private LinearLayout mInstagramLink;
    private LinearLayout mFacebookLink;
    private LinearLayout mTwitterLink;
    private LinearLayout mWhatsappLink;

    private TextView mGmail;
    private TextView mInstagram;
    private TextView mFacebook;
    private TextView mTwitter;
    private TextView mWhatsapp;

    private String gmail;
    private String instagramProfile;
    private String facebookProfile;
    private String twitterProfile;
    private String whatsappNo;

    //dialogBoxStuff
    private AlertDialog dialogBuilder;
    private LayoutInflater inflater;
    private View dialogView;
    private TextView title;
    private EditText editComment;
    private Button buttonSubmit;
    private Button buttonCancel;

    private users setting;


    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_editprofile);
        mContext = EditProfile.this;


        backarrow = findViewById(R.id.backarrow);
        checkmark = findViewById(R.id.saveChanges);

        mProfilephoto = findViewById(R.id.profile_photo);
        mchangeprofilephoto = findViewById(R.id.change_Profile_Photo);

        mDisplayname = findViewById(R.id.display_name);
        mdescription = findViewById(R.id.description);
        mUsername = findViewById(R.id.username);

        mGmailLink = findViewById(R.id.gmail_link);
        mInstagramLink = findViewById(R.id.instagram_link);
        mFacebookLink = findViewById(R.id.facebook_link);
        mTwitterLink = findViewById(R.id.twitter_link);
        mWhatsappLink = findViewById(R.id.whatsapp_link);

        mGmail = findViewById(R.id.gmail);
        mInstagram = findViewById(R.id.instagram);
        mFacebook = findViewById(R.id.facebook);
        mTwitter = findViewById(R.id.twitter);
        mWhatsapp = findViewById(R.id.whatsapp);

        gmail = "";
        instagramProfile = "";
        facebookProfile = "";
        twitterProfile = "";
        whatsappNo = "";

        dialogBuilder = new AlertDialog.Builder(this).create();
        inflater = this.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_profile_link, null);

        title = dialogView.findViewById(R.id.textView);
        editComment = dialogView.findViewById(R.id.edt_comment);
        buttonSubmit = dialogView.findViewById(R.id.buttonSubmit);
        buttonCancel = dialogView.findViewById(R.id.buttonCancel);

        mFirebaseMethods = new FirebaseMethods(this);

        initializeImageLoader();
        setupFirebaseAuth();
        initOnClicks();
    }

    private void initOnClicks() {
        mchangeprofilephoto.setOnClickListener(v -> {
            if (checkPermissionArray(Permissions.PERMISSIONS)) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
//
            } else verifyPermission(Permissions.PERMISSIONS);

        });
        mGmailLink.setOnClickListener(v -> {
            title.setText("Add/Edit your email address");
            editComment.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            if (gmail != null && !gmail.equals("")) {
                buttonSubmit.setText("EDIT");
                editComment.setText(gmail);
            } else editComment.setHint("Enter email address to add");
            buttonSubmit.setOnClickListener(view -> {
                String text = String.valueOf(editComment.getText());
                gmail = text;
                mGmail.setText(text);
                if (text.equals("")) editComment.setError("Cant be empty");
                else {
                    mGmailLink.setAlpha(1.0f);
                    dialogBuilder.dismiss();
                }
            });
            buttonCancel.setOnClickListener(view -> dialogBuilder.dismiss());
            dialogBuilder.setView(dialogView);
            dialogBuilder.show();
        });
        mInstagramLink.setOnClickListener(v -> {
            title.setText("Add/Edit your Instagram username");
            editComment.setInputType(InputType.TYPE_CLASS_TEXT);
            if (instagramProfile != null && !instagramProfile.equals("")) {
                buttonSubmit.setText("EDIT");
                editComment.setText(instagramProfile);
            } else editComment.setHint("Enter profile link to add");
            buttonSubmit.setOnClickListener(view -> {
                String text = String.valueOf(editComment.getText());
                instagramProfile = text;
                mInstagram.setText(text);
                if (text.equals("")) editComment.setError("Cant be empty");
                else {
                    mInstagramLink.setAlpha(1.0f);
                    dialogBuilder.dismiss();
                }
            });
            buttonCancel.setOnClickListener(view -> dialogBuilder.dismiss());
            dialogBuilder.setView(dialogView);
            dialogBuilder.show();
        });
        mFacebookLink.setOnClickListener(v -> {
            title.setText("Add/Edit your Facebook unique id");
            editComment.setInputType(InputType.TYPE_CLASS_TEXT);
            if (facebookProfile != null && !facebookProfile.equals("")) {
                buttonSubmit.setText("EDIT");
                editComment.setText(facebookProfile);
            } else editComment.setHint("Enter profile link to add");
            buttonSubmit.setOnClickListener(view -> {
                String text = String.valueOf(editComment.getText());
                facebookProfile = text;
                mFacebook.setText(text);
                if (text.equals("")) editComment.setError("Cant be empty");
                else {
                    mFacebookLink.setAlpha(1.0f);
                    dialogBuilder.dismiss();
                }
            });
            buttonCancel.setOnClickListener(view -> dialogBuilder.dismiss());
            dialogBuilder.setView(dialogView);
            dialogBuilder.show();
        });
        mTwitterLink.setOnClickListener(v -> {
            title.setText("Add/Edit your Twitter id");
            editComment.setInputType(InputType.TYPE_CLASS_TEXT);
            if (twitterProfile != null && !twitterProfile.equals("")) {
                buttonSubmit.setText("EDIT");
                editComment.setText(twitterProfile);
            }
            buttonSubmit.setOnClickListener(view -> {
                String text = String.valueOf(editComment.getText());
                twitterProfile = text;
                mTwitter.setText(text);
                if (text.equals("")) editComment.setError("Cant be empty");
                else {
                    mTwitterLink.setAlpha(1.0f);
                    dialogBuilder.dismiss();
                }
            });
            buttonCancel.setOnClickListener(view -> dialogBuilder.dismiss());
            dialogBuilder.setView(dialogView);
            dialogBuilder.show();
        });
        mWhatsappLink.setOnClickListener(v -> {
            title.setText("Add/Edit your Whats app no");
            editComment.setInputType(InputType.TYPE_CLASS_PHONE);
            if (whatsappNo != null && !whatsappNo.equals("")) {
                buttonSubmit.setText("EDIT");
                editComment.setText(whatsappNo);
            } else editComment.setHint("Enter contact no. to add");
            buttonSubmit.setOnClickListener(view -> {
                String text = String.valueOf(editComment.getText());
                whatsappNo = text;
                mWhatsapp.setText(text);
                if (text.equals("")) editComment.setError("Cant be empty");
                else {
                    mWhatsappLink.setAlpha(1.0f);
                    dialogBuilder.dismiss();
                }
            });
            buttonCancel.setOnClickListener(view -> dialogBuilder.dismiss());
            dialogBuilder.setView(dialogView);
            dialogBuilder.show();
        });
        backarrow.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("Are you sure")
                    .setMessage("You will discard all the changes you made?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> finish())
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                    .show();
        });
        checkmark.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("Are you sure")
                    .setMessage("Would u like to check all the changes u made once again?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> dialog.cancel())
                    .setNegativeButton("No", (dialog, id) -> saveProfileSetting())
                    .show();
        });
    }

    public void verifyPermission(String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, VERIFY_PERMISSION_REQUEST);
    }

    private void saveProfileSetting() {
        final String displayName = mDisplayname.getText().toString();
        final String username = mUsername.getText().toString();
        final String description = mdescription.getText().toString();
        //if user made a change to username
        if (!setting.getUsername().equals(username))
            checkifuserexist(username);

        if (!displayName.equals("") && !setting.getDisplay_name().equals(displayName))
            myRef.child(getString(R.string.dbname_user_account_settings)).child(userID).child(getString(R.string.field_display)).setValue(displayName);
        if (!setting.getDescription().equals(description))
            myRef.child(getString(R.string.dbname_user_account_settings)).child(userID).child(getString(R.string.field_description)).setValue(description);

        if (gmail != null && !gmail.equals("") && (setting.getEmail() == null || !setting.getEmail().equals(gmail)))
            myRef.child(getString(R.string.dbname_user_account_settings)).child(userID).child(getString(R.string.field_email)).setValue(gmail);
        if (instagramProfile != null && !instagramProfile.equals("") && (setting.getInstagram() == null || !setting.getInstagram().equals(instagramProfile)))
            myRef.child(getString(R.string.dbname_user_account_settings)).child(userID).child(getString(R.string.field_instagram)).setValue(instagramProfile);
        if (facebookProfile != null && !facebookProfile.equals("") && (setting.getFacebook() == null || !setting.getFacebook().equals(facebookProfile)))
            myRef.child(getString(R.string.dbname_user_account_settings)).child(userID).child(getString(R.string.field_facebook)).setValue(facebookProfile);
        if (twitterProfile != null && !twitterProfile.equals("") && (setting.getTwitter() == null || !setting.getTwitter().equals(twitterProfile)))
            myRef.child(getString(R.string.dbname_user_account_settings)).child(userID).child(getString(R.string.field_twitter)).setValue(twitterProfile);
        if (whatsappNo != null && !whatsappNo.equals("") && (setting.getWhatsapp() == null || !setting.getWhatsapp().equals(whatsappNo)))
            myRef.child(getString(R.string.dbname_user_account_settings)).child(userID).child(getString(R.string.field_whatsapp)).setValue(whatsappNo);

        if (photoChanged) {
            Log.d(TAG, "uploadNewPhoto: uploading new PROFILE photo" + (mContext.getString(R.string.profile_photo)));
            ProgressDialog dialog = ProgressDialog.show(mContext, "", "Uploading... - ", true);
            FilePaths filepaths = new FilePaths();
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filepaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");
            Bitmap bm = ImageManager.getBitmap(imgURL);
            File file = new File(imgURL);
            long length = file.length() / 1024;
            byte[] bytes;
            if (length < 200) bytes = ImageManager.getBytesFromBitmap(bm, 100);
            else if (length < 500) bytes = ImageManager.getBytesFromBitmap(bm, 65);
            else if (length < 800) bytes = ImageManager.getBytesFromBitmap(bm, 45);
            else bytes = ImageManager.getBytesFromBitmap(bm, 25);
            UploadTask uploadTask = storageReference.putBytes(bytes);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                dialog.dismiss();
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    myRef.child(mContext.getString(R.string.dbname_users)).child(userID).child(mContext.getString(R.string.profile_photo)).setValue(uri.toString());
                    Toast.makeText(mContext, "Photo Upload success", Toast.LENGTH_SHORT).show();
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
        } else mContext.startActivity(new Intent(mContext, ProfileActivity.class));
    }

    private void checkifuserexist(final String username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_users)).orderByChild(getString(R.string.field_username)).equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
//                    add the username
                    myRef.child(mContext.getString(R.string.dbname_users)).child(userID).child(mContext.getString(R.string.field_username)).setValue(username);
                    Toast.makeText(EditProfile.this, "saved username", Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        Toast.makeText(EditProfile.this, "That Username already exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void initializeImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public boolean checkPermissionArray(String[] permissions) {
        for (String check : permissions) if (!checkPermissions(check)) return false;
        return true;
    }

    public boolean checkPermissions(String permission) {
        int permissionRequest = ActivityCompat.checkSelfPermission(this, permission);
        return permissionRequest == PackageManager.PERMISSION_GRANTED;
    }

    //    private void setProfileimage(){
//        Log.d(TAG,"setProfileimage: setting profile image");
//        String imgURL="https://www.pngmart.com/files/7/Red-Smoke-Transparent-Images-PNG.png";
//        UniversalImageLoader.setImage(imgURL, mProfilephoto,null,"");
//    }
    private void setProfileWidgets(users userSetting) {
        setting = userSetting;

        UniversalImageLoader.setImage(setting.getProfile_photo(), mProfilephoto, null, "");
        mDisplayname.setText(setting.getDisplay_name());
        mUsername.setText(setting.getUsername());
        mdescription.setText(setting.getDescription());

        if (userSetting.getEmail() == null || userSetting.getEmail().equals("")) {
            mGmailLink.setAlpha(0.5f);
        } else {
            mGmail.setText(userSetting.getEmail());
            gmail = userSetting.getEmail();
        }

        if (userSetting.getInstagram() == null || userSetting.getInstagram().equals(""))
            mInstagramLink.setAlpha(0.5f);
        else {
            mInstagram.setText(userSetting.getInstagram());
            instagramProfile = userSetting.getInstagram();
        }

        if (userSetting.getFacebook() == null || userSetting.getFacebook().equals("")) {
            mFacebookLink.setAlpha(0.5f);
        } else {
            mFacebook.setText(userSetting.getFacebook());
            facebookProfile = userSetting.getFacebook();

        }
        if (userSetting.getTwitter() == null || userSetting.getTwitter().equals("")) {
            mTwitterLink.setAlpha(0.5f);
        } else {
            mTwitter.setText(userSetting.getTwitter());
            twitterProfile = userSetting.getTwitter();
        }

        if (userSetting.getWhatsapp() == null || userSetting.getWhatsapp().equals("")) {
            mWhatsappLink.setAlpha(0.5f);
        } else {
            mWhatsapp.setText(userSetting.getWhatsapp());
            whatsappNo = userSetting.getWhatsapp();
        }
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
        UniversalImageLoader.setImage(imgURL, mProfilephoto, null, mAppend);
        photoChanged = true;
    }


    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

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
                setProfileWidgets(dataSnapshot.child(getString(R.string.dbname_users)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(users.class));
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

    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Are you sure")
                .setMessage("You will discard all the changes you made?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> finish())
                .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                .show();
    }
}