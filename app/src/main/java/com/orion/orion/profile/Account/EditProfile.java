package com.orion.orion.profile.Account;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
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

import com.bumptech.glide.Glide;
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
import com.orion.orion.R;
import com.orion.orion.login.LoginActivity;
import com.orion.orion.models.users;
import com.orion.orion.profile.ProfileActivity;
import com.orion.orion.util.FilePaths;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.ImageManager;
import com.orion.orion.util.Permissions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

import static com.orion.orion.util.FileUtils.generateFileName;
import static com.orion.orion.util.FileUtils.getDocumentCacheDir;
import static com.orion.orion.util.FileUtils.getFileName;
import static com.orion.orion.util.FileUtils.saveFileFromUri;

public class EditProfile extends AppCompatActivity {
    private static final String TAG = "EditProfile";
    private static final int VERIFY_PERMISSION_REQUEST = 1;

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private String userID;
    private Context mContext;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods mFirebaseMethods;

    private ImageView backarrow;
    private ImageView checkmark;
    private ImageView mProfilephoto;
    private TextView mchangeprofilephoto;

    private String imgURL;
    private boolean photoChanged = false;

    private EditText mDisplayname;
    private EditText mUsername;
    private EditText mdescription;
    private EditText mExternalLinks;
    private ImageView mAddLink;
    private LinearLayout link1Container;
    private LinearLayout link2Container;
    private LinearLayout link3Container;
    private TextView mLink1;
    private ImageView mLink1delete;
    private TextView mLink2;
    private ImageView mLink2delete;
    private TextView mLink3;
    private ImageView mLink3delete;

    private LinearLayout mGmailLink;
    private LinearLayout mInstagramLink;
    private LinearLayout mFacebookLink;
    private LinearLayout mTwitterLink;
    private LinearLayout mWhatsappLink;

    private String gmail;
    private String instagramProfile;
    private String facebookProfile;
    private String twitterProfile;
    private String whatsappNo;
    private String externalLink1;
    private String externalLink2;
    private String externalLink3;

    //dialogBoxStuff
    private AlertDialog dialogBuilder;
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

        mFirebaseMethods = new FirebaseMethods(mContext);

        backarrow = findViewById(R.id.backarrow);
        checkmark = findViewById(R.id.saveChanges);

        mProfilephoto = findViewById(R.id.profile_photo);
        mchangeprofilephoto = findViewById(R.id.change_Profile_Photo);

        mDisplayname = findViewById(R.id.display_name);
        mdescription = findViewById(R.id.description);
        mUsername = findViewById(R.id.username);
        mExternalLinks = findViewById(R.id.externalLinks);
        mAddLink = findViewById(R.id.addLink);
        link1Container = findViewById(R.id.relLayout5);
        link2Container = findViewById(R.id.relLayout6);
        link3Container = findViewById(R.id.relLayout7);
        mLink1 = findViewById(R.id.link1);
        mLink1delete = findViewById(R.id.link1delete);
        mLink2 = findViewById(R.id.link2);
        mLink2delete = findViewById(R.id.link2delete);
        mLink3 = findViewById(R.id.link3);
        mLink3delete = findViewById(R.id.link3delete);

        mGmailLink = findViewById(R.id.gmail_link);
        mInstagramLink = findViewById(R.id.instagram_link);
        mFacebookLink = findViewById(R.id.facebook_link);
        mTwitterLink = findViewById(R.id.twitter_link);
        mWhatsappLink = findViewById(R.id.whatsapp_link);


        gmail = "";
        instagramProfile = "";
        facebookProfile = "";
        twitterProfile = "";
        whatsappNo = "";
        externalLink1 = "";
        externalLink2 = "";
        externalLink3 = "";

        dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_profile_link, null);

        title = dialogView.findViewById(R.id.textView);
        editComment = dialogView.findViewById(R.id.edt_comment);
        buttonSubmit = dialogView.findViewById(R.id.buttonSubmit);
        buttonCancel = dialogView.findViewById(R.id.buttonCancel);

//        FirebaseMethods mFirebaseMethods = new FirebaseMethods(this);


        setupFirebaseAuth();
        initOnClicks();
    }

    private void initOnClicks() {
        mchangeprofilephoto.setOnClickListener(v -> {
            if (checkPermissionArray(Permissions.PERMISSIONS)) {
                mchangeprofilephoto.setEnabled(false);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            } else verifyPermission(Permissions.PERMISSIONS);

        });
        mGmailLink.setOnClickListener(v -> {
            title.setText("Add/Edit your email address");
            editComment.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            if (gmail != null || !gmail.equals("")) {
                buttonSubmit.setText("ADD");
                editComment.setText(gmail);
            } else editComment.setHint("Enter email address to add");
            buttonSubmit.setOnClickListener(view -> {
                String text = String.valueOf(editComment.getText());
                gmail = text;
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
            if (instagramProfile != null || !instagramProfile.equals("")) {
                buttonSubmit.setText("ADD");
                editComment.setText(instagramProfile);
            } else {
                editComment.setText(instagramProfile);
                editComment.setHint("Enter profile link to add");
            }
            buttonSubmit.setOnClickListener(view -> {
                String text = String.valueOf(editComment.getText());
                instagramProfile = text;
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
            if (facebookProfile != null || !facebookProfile.equals("")) {
                buttonSubmit.setText("ADD");
                editComment.setText(facebookProfile);
            } else {
                editComment.setText(facebookProfile);
                editComment.setHint("Enter profile link to add");
            }
            buttonSubmit.setOnClickListener(view -> {
                String text = String.valueOf(editComment.getText());
                facebookProfile = text;
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
            if (twitterProfile != null || !twitterProfile.equals("")) {
                buttonSubmit.setText("ADD");
                editComment.setText(twitterProfile);
            } else {
                editComment.setText(twitterProfile);
                editComment.setHint("Enter profile link to add");
            }
            buttonSubmit.setOnClickListener(view -> {
                String text = String.valueOf(editComment.getText());
                twitterProfile = text;
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
            if (whatsappNo != null || !whatsappNo.equals("")) {
                buttonSubmit.setText("ADD");
                editComment.setText(whatsappNo);
            } else {
                editComment.setText(whatsappNo);
                editComment.setHint("Enter contact no. to add");
            }
            buttonSubmit.setOnClickListener(view -> {
                String text = String.valueOf(editComment.getText());
                whatsappNo = text;
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
        mAddLink.setOnClickListener(v -> {
            String link = String.valueOf(mExternalLinks.getText());
            if (!link.equals("")) {
                if (link1Container.getVisibility() == View.VISIBLE && link2Container.getVisibility() == View.VISIBLE && link3Container.getVisibility() == View.VISIBLE) {
                    Toast.makeText(mContext, "You can add at the most 3 links", Toast.LENGTH_LONG).show();
                } else if (link1Container.getVisibility() == View.VISIBLE && link2Container.getVisibility() == View.VISIBLE && link3Container.getVisibility() != View.VISIBLE) {
                    link3Container.setVisibility(View.VISIBLE);
                    mLink3.setText(link);
                    externalLink3 = link;
                    mExternalLinks.setText("");
                } else if (link1Container.getVisibility() == View.VISIBLE && link2Container.getVisibility() != View.VISIBLE && link3Container.getVisibility() != View.VISIBLE) {
                    link2Container.setVisibility(View.VISIBLE);
                    mLink2.setText(link);
                    externalLink2 = link;
                    mExternalLinks.setText("");
                } else {
                    link1Container.setVisibility(View.VISIBLE);
                    mLink1.setText(link);
                    externalLink1 = link;
                    mExternalLinks.setText("");
                }
            }
        });
        mLink1delete.setOnClickListener(v -> {
            if (link2Container.getVisibility() == View.VISIBLE && link3Container.getVisibility() == View.VISIBLE) {
                mLink1.setText(mLink2.getText());
                mLink2.setText(mLink3.getText());
                externalLink1 = externalLink2;
                externalLink2 = externalLink3;
                externalLink3 = "";
                mLink3.setText("");
                link3Container.setVisibility(View.GONE);
            } else if (link2Container.getVisibility() == View.VISIBLE && link3Container.getVisibility() != View.VISIBLE) {
                mLink1.setText(mLink2.getText());
                externalLink1 = externalLink2;
                externalLink2 = "";
                mLink2.setText("");
                link2Container.setVisibility(View.GONE);
            } else {
                externalLink1 = "";
                mLink1.setText("");
                link1Container.setVisibility(View.GONE);
            }
        });
        mLink2delete.setOnClickListener(v -> {
            if (link3Container.getVisibility() == View.VISIBLE) {
                mLink2.setText(mLink3.getText());
                externalLink2 = externalLink3;
                externalLink3 = "";
                mLink3.setText("");
                link3Container.setVisibility(View.GONE);
            } else {
                externalLink1 = "";
                mLink2.setText("");
                link2Container.setVisibility(View.GONE);
            }
        });
        mLink3delete.setOnClickListener(v -> {
            externalLink3 = "";
            mLink3.setText("");
            link3Container.setVisibility(View.GONE);
        });
        backarrow.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("Are you sure")
                    .setMessage("You will discard all the changes you made?")
                    .setCancelable(false)
                    .setPositiveButton("Go back", (dialog, id) -> finish())
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                    .show();
        });
        checkmark.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("Are you sure")
                    .setMessage("Save changes you made?")
                    .setCancelable(false)
                    .setPositiveButton("Save", (dialog, id) -> saveProfileSetting())
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                    .show();
        });
    }

    public void verifyPermission(String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, VERIFY_PERMISSION_REQUEST);
    }

    @SuppressLint("DefaultLocale")
    private void saveProfileSetting() {
        final String displayName = mDisplayname.getText().toString();
        final String username = mUsername.getText().toString();
        final String description = mdescription.getText().toString();
        //if user made a change to username
        if (!setting.getU().equals(username))
            checkifuserexist(username,setting.getU());

        if (!displayName.equals("") && !setting.getDn().equals(displayName))
            myRef.child(getString(R.string.dbname_users)).child(userID).child(getString(R.string.field_display_name)).setValue(displayName);
        if (!setting.getDes().equals(description))
            myRef.child(getString(R.string.dbname_users)).child(userID).child(getString(R.string.field_description)).setValue(description);

        if (!gmail.equals("") && (setting.getE() == null || !setting.getE().equals(gmail)))
            myRef.child(getString(R.string.dbname_users)).child(userID).child(getString(R.string.field_email)).setValue(gmail);
        if (!instagramProfile.equals("") && (setting.getIn() == null || !setting.getIn().equals(instagramProfile)))
            myRef.child(getString(R.string.dbname_users)).child(userID).child(getString(R.string.field_instagram)).setValue(instagramProfile);
        if (!facebookProfile.equals("") && (setting.getFb() == null || !setting.getFb().equals(facebookProfile)))
            myRef.child(getString(R.string.dbname_users)).child(userID).child(getString(R.string.field_facebook)).setValue(facebookProfile);
        if (!twitterProfile.equals("") && (setting.getTw() == null || !setting.getTw().equals(twitterProfile)))
            myRef.child(getString(R.string.dbname_users)).child(userID).child(getString(R.string.field_twitter)).setValue(twitterProfile);
        if (!whatsappNo.equals("") && (setting.getWa() == null || !setting.getWa().equals(whatsappNo)))
            myRef.child(getString(R.string.dbname_users)).child(userID).child(getString(R.string.field_whatsapp)).setValue(whatsappNo);

//        Log.d(TAG, "externalLink123: " + mLink1.getText() + mLink2.getText() + mLink3.getText());
//        Log.d(TAG, "externalLink123: " + externalLink1 + externalLink2 + externalLink3);

        externalLink1 = mLink1.getText().toString();
        externalLink2 = mLink2.getText().toString();
        externalLink3 = mLink3.getText().toString();

        myRef.child(getString(R.string.dbname_users)).child(userID).child(getString(R.string.field_link1)).setValue(externalLink1);
        myRef.child(getString(R.string.dbname_users)).child(userID).child(getString(R.string.field_link2)).setValue(externalLink2);
        myRef.child(getString(R.string.dbname_users)).child(userID).child(getString(R.string.field_link3)).setValue(externalLink3);

        if (photoChanged) {
            Log.d(TAG, "uploadNewPhoto: uploading new PROFILE photo" + (mContext.getString(R.string.profile_photo)));
            ProgressDialog dialog = ProgressDialog.show(mContext, "", "Uploading... - ", true);

            FilePaths filepaths = new FilePaths();
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filepaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

            String imgUrl2 = mFirebaseMethods.compressImage(imgURL);
            Bitmap bm = ImageManager.getBitmap(imgUrl2);
            byte[] bytes;
            bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = storageReference.putBytes(bytes);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                dialog.dismiss();
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    myRef.child(mContext.getString(R.string.dbname_users)).child(userID).child(mContext.getString(R.string.profile_photo)).setValue(uri.toString());
                    Toast.makeText(mContext, "Photo Upload success", Toast.LENGTH_SHORT).show();
                });
                mContext.startActivity(new Intent(mContext, ProfileActivity.class));
                finish();
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Log.d(TAG, "onFailure: Photo Upload Failed");
                Toast.makeText(mContext, "Photo Upload failed", Toast.LENGTH_SHORT).show();
                mContext.startActivity(new Intent(mContext, EditProfile.class));
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                ProgressDialog.show(mContext, "", "Uploading... - " + String.format("%.0f", progress) + "%", true);
                Toast.makeText(mContext, "Photo Upload Progress" + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onProgress: upload progress" + progress + "% done");
            });
        } else {
            mContext.startActivity(new Intent(mContext, ProfileActivity.class));
            finish();
        }
    }

    private void checkifuserexist(final String username, String oldUsername) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_username))
                .child(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
//                    add the username
                    myRef.child(mContext.getString(R.string.dbname_users)).child(userID).child(mContext.getString(R.string.field_username)).setValue(username);
                    myRef.child(mContext.getString(R.string.dbname_username))
                            .child(username)
                            .setValue(userID);
                    myRef.child(mContext.getString(R.string.dbname_username))
                            .child(oldUsername)
                            .removeValue();

                    Toast.makeText(EditProfile.this, "saved username", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(EditProfile.this, "That Username already exist", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public boolean checkPermissionArray(String[] permissions) {
        for (String check : permissions) if (!checkPermissions(check)) return false;
        return true;
    }

    public boolean checkPermissions(String permission) {
        int permissionRequest = ActivityCompat.checkSelfPermission(this, permission);
        return permissionRequest == PackageManager.PERMISSION_GRANTED;
    }


    private void setProfileWidgets(users userSetting) {
        setting = userSetting;

        Glide.with(getApplicationContext())
                .load(setting.getPp())
                .placeholder(R.drawable.load)
                .error(R.drawable.default_image2)
                .placeholder(R.drawable.load)
                .thumbnail(0.2f)
                .into(mProfilephoto);

        mDisplayname.setText(setting.getDn());
        mUsername.setText(setting.getU());
        mdescription.setText(setting.getDes());

        if (userSetting.getl1() == null || userSetting.getl1().equals(""))
            link1Container.setVisibility(View.GONE);
        else mLink1.setText(userSetting.getl1());
        if (userSetting.getl2() == null || userSetting.getl2().equals(""))
            link2Container.setVisibility(View.GONE);
        else mLink2.setText(userSetting.getl2());
        if (userSetting.getl3() == null || userSetting.getl3().equals(""))
            link3Container.setVisibility(View.GONE);
        else mLink3.setText(userSetting.getl3());

        if (userSetting.getE() == null || userSetting.getE().equals(""))
            mGmailLink.setAlpha(0.5f);
        else gmail = userSetting.getE();

        if (userSetting.getIn() == null || userSetting.getIn().equals(""))
            mInstagramLink.setAlpha(0.5f);
        else instagramProfile = userSetting.getIn();

        if (userSetting.getFb() == null || userSetting.getFb().equals(""))
            mFacebookLink.setAlpha(0.5f);
        else facebookProfile = userSetting.getFb();

        if (userSetting.getTw() == null || userSetting.getTw().equals(""))
            mTwitterLink.setAlpha(0.5f);
        else twitterProfile = userSetting.getTw();

        if (userSetting.getWa() == null || userSetting.getWa().equals(""))
            mWhatsappLink.setAlpha(0.5f);
        else whatsappNo = userSetting.getWa();
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
                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4);
                }
                String[] contentUriPrefixesToTry = new String[]{
                        "content://downloads/public_downloads",
                        "content://downloads/my_downloads"
                };
                for (String contentUriPrefix : contentUriPrefixesToTry) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));
                    try {
                        String path = getDataColumn(context, contentUri, null, null);
                        if (path != null) {
                            return path;
                        }
                    } catch (Exception e) {
                    }
                }

                // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                String fileName = getFileName(context, uri);
                File cacheDir = getDocumentCacheDir(context);
                File file = generateFileName(fileName, cacheDir);
                String destinationPath = null;
                if (file != null) {
                    destinationPath = file.getAbsolutePath();
                    saveFileFromUri(context, uri, destinationPath);
                }
                return destinationPath;
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
        else if ("file".equalsIgnoreCase(uri.getScheme()))
            return uri.getPath();
        else
            Toast.makeText(context, "Unable to upload image", Toast.LENGTH_LONG).show();
        return null;
    }

    @TargetApi(19)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mchangeprofilephoto.setEnabled(true);
        String imgPath = "";
        if (data != null && data.getData() != null && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                imgPath = getPathFromUri(mContext, uri);
                if(imgPath!=null) {
                    Log.d(TAG, "onActivityResult: path: " + imgPath);
                    Log.d(TAG, "onActivityResult: uri: " + uri);
                    setImage(imgPath);
                }
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
                    .thumbnail(0.2f)
                    .into(mProfilephoto);
            photoChanged = true;
    }


    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                Log.d(TAG, "onAuthStateChanged: navigating to login");
                SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                new AlertDialog.Builder(mContext)
                        .setTitle("No user logon found")
                        .setMessage("We will be logging u out. \n Please try to log in again")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            Intent intent = new Intent(mContext, LoginActivity.class);
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