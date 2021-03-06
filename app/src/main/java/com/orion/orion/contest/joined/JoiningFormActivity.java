package com.orion.orion.contest.joined;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.login.LoginActivity;
import com.orion.orion.models.CreateForm;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.Permissions;
import com.orion.orion.util.SNTPClient;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static com.orion.orion.profile.ProfileActivity.VERIFY_PERMISSION_REQUEST;
import static com.orion.orion.util.FileUtils.generateFileName;
import static com.orion.orion.util.FileUtils.getDocumentCacheDir;
import static com.orion.orion.util.FileUtils.getFileName;
import static com.orion.orion.util.FileUtils.saveFileFromUri;

public class JoiningFormActivity extends AppCompatActivity {
    private static final String TAG = "JoiningFormActivity";
    private final Context mContext = JoiningFormActivity.this;

    private TextView mTopBarTitle;
    private TextView linkText;
    private EditText collegeEt;
    private EditText urlEt;
    private EditText desET;
    private ImageView idIv, submissionIv, backArrow;
    TextView warn, decline;
    private Button submitBtn, idBtn, mediaBtn;
    boolean isKitKat;
    private String imgurl = "";
    private int selectedImage;
    String mediaLink = "", idLink = "", userId, contestId;
    private FirebaseMethods mFirebaseMethods;
    int imageCount = 0;
    String openfor = "";

    String type = "";
    String p5 = "p5", p6 = "p6";
    public String contestType = "";
    public LinearLayout linearLayout;
    String isJuryOrHost = "false";

    private RelativeLayout submissionContiner;
    private LinearLayout mediaLinear;
    private LinearLayout imageLinear;
    private LinearLayout collegeidLinearLayout;
    private TextView quizRules;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference ref;

    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joining_form);
        setupFirebaseAuth();
        mTopBarTitle = findViewById(R.id.titleTopBar);
        mTopBarTitle.setText("Joining Contest");
        mFirebaseMethods = new FirebaseMethods(JoiningFormActivity.this);

        Intent i = getIntent();
        contestType = i.getStringExtra("contestType");
        userId = i.getStringExtra("userId");
        contestId = i.getStringExtra("contestId");
        isJuryOrHost = i.getStringExtra("isJuryOrHost");

        linkText = findViewById(R.id.linkText);
        collegeEt = findViewById(R.id.collegeEt);
        urlEt = findViewById(R.id.url_submission);
        submitBtn = findViewById(R.id.submitBtn);
        idBtn = findViewById(R.id.selectid);
        mediaBtn = findViewById(R.id.selectSubmission);
        desET = findViewById(R.id.des);
        idIv = findViewById(R.id.idIv);
        submissionIv = findViewById(R.id.submisionIv);
        warn = findViewById(R.id.warn);

        submissionContiner = findViewById(R.id.relLayout);
        collegeidLinearLayout = findViewById(R.id.collegeidLinearLayout);
        imageLinear = findViewById(R.id.ImageLinearLayout);
        mediaLinear = findViewById(R.id.mediaLinearLayout);
        quizRules = findViewById(R.id.quizRules);
        linearLayout = findViewById(R.id.pro);
        backArrow = findViewById(R.id.backarrow);
        decline = findViewById(R.id.decline);

        dialog = ProgressDialog.show(this, "", "Loading...", true);
        ref = FirebaseDatabase.getInstance().getReference();

        backArrow.setOnClickListener(view -> finish());
        idBtn.setOnClickListener(v -> {
            selectedImage = 1;
            idBtn.setEnabled(true);
            if (checkPermissionArray(Permissions.PERMISSIONS)) {
                isKitKat = true;
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            } else verifyPermission(Permissions.PERMISSIONS);
        });
        mediaBtn.setOnClickListener(v -> {
            mediaBtn.setEnabled(true);
            selectedImage = 2;
            if (checkPermissionArray(Permissions.PERMISSIONS)) {
                isKitKat = true;
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            } else {
                verifyPermission(Permissions.PERMISSIONS);
            }
        });
        submitBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Submit Joining Form");
            builder.setMessage("Are you sure you want to submit this CC_FillFormActivity?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                if (mediaLink != null)
                    if (checkValidity()) {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        linearLayout.setVisibility(View.VISIBLE);
                        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Colombo"), new SNTPClient.Listener() {
                            @Override
                            public void onTimeReceived(String rawDate) {
                                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                                Date date = null;
                                try {
                                    date = formatter.parse(rawDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String des = desET.getText().toString();
                                if (des == null || des.equals("")) des = "";
                                String timeStamp = String.valueOf(date.getTime());
                                DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();
                                String JoiningKey = db1.child(getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(getString(R.string.joined_contest))
                                        .push().getKey();
                                if (!type.equals("Image"))
                                    mediaLink = urlEt.getText().toString();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put(getString(R.string.field_contestType), contestType);
                                hashMap.put(getString(R.string.field_college), collegeEt.getText().toString());
                                hashMap.put(getString(R.string.field_status), "waiting");
                                hashMap.put(getString(R.string.field_host), userId);
                                hashMap.put(getString(R.string.field_contest_ID), contestId);
                                hashMap.put(getString(R.string.field_timestamp), timeStamp);
                                hashMap.put(getString(R.string.field_joining_ID), JoiningKey);
                                hashMap.put(getString(R.string.field_id_link), idLink);
                                hashMap.put(getString(R.string.field_media_link), mediaLink);
                                hashMap.put(getString(R.string.field_user_id), FirebaseAuth.getInstance().getCurrentUser().getUid());
                                String finalDes = des;
                                db1.child(getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(getString(R.string.joined_contest))
                                        .child(JoiningKey)
                                        .setValue(hashMap)
                                        .addOnSuccessListener(aVoid -> {
                                            DatabaseReference db2 = FirebaseDatabase.getInstance().getReference();
                                            HashMap<String, Object> hashMap2 = new HashMap<>();
                                            hashMap2.put(getString(R.string.field_timestamp), timeStamp);
                                            hashMap2.put(getString(R.string.field_joining_ID), JoiningKey);
                                            hashMap2.put(getString(R.string.field_total_score), 0);
                                            hashMap2.put(getString(R.string.field_contest_ID), contestId);
                                            hashMap2.put(getString(R.string.field_media_link), mediaLink);
                                            hashMap2.put(getString(R.string.submission_description), finalDes);
                                            hashMap2.put(getString(R.string.field_user_id), FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            db2.child(getString(R.string.dbname_request))
                                                    .child(getString(R.string.dbname_participantList))
                                                    .child(contestId)
                                                    .child(JoiningKey)
                                                    .setValue(hashMap2)
                                                    .addOnSuccessListener(aVoid1 -> { int c = 0;
                                                        if (!idLink.equals(""))
                                                            mFirebaseMethods.uploadContest(imageCount, idLink, null, contestId, p5, JoiningKey);
                                                        else
                                                            c++;
                                                        if (type != null && type.equals("Image"))
                                                            mFirebaseMethods.uploadContest(imageCount, mediaLink, null, contestId, p6, JoiningKey);
                                                        else
                                                            c++;
                                                        if (c == 2) {
                                                            Log.d(TAG, "onTimeReceived: redirecting to Joinedactivity.class");
                                                            linearLayout.setVisibility(View.GONE);
                                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                            startActivity(new Intent(mContext, JoinedActivity.class));
                                                            Toast.makeText(JoiningFormActivity.this, "Your submission has been submitted.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        });

                                Log.e(SNTPClient.TAG, rawDate);

                            }

                            @Override
                            public void onError(Exception ex) {
                                Log.e(SNTPClient.TAG, ex.getMessage());
                            }
                        });
                    } else {
                        Toast.makeText(JoiningFormActivity.this, "Please fill all the entries correctly!", Toast.LENGTH_SHORT).show();
                    }
            }).setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.create().show();

        });

        if (isJuryOrHost.equals("true")) {
            decline.setVisibility(View.VISIBLE);
            submitBtn.setEnabled(false);
        }

        ref.child(getString(R.string.dbname_contestlist))
                .child(contestId)
                .child(getString(R.string.field_Participant_List))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            dialog.dismiss();
                            submissionContiner.setVisibility(View.GONE);
                            submitBtn.setEnabled(false);
                            warn.setVisibility(View.VISIBLE);
                        } else {
                            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
                            ref1.child(mContext.getString(R.string.dbname_request))
                                    .child(mContext.getString(R.string.dbname_participantList))
                                    .child(contestId)
                                    .orderByChild(getString(R.string.field_user_id))
                                    .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            dialog.dismiss();
                                            if (snapshot.exists()) {
                                                submissionContiner.setVisibility(View.GONE);
                                                submitBtn.setEnabled(false);
                                                warn.setVisibility(View.VISIBLE);
                                            } else {
                                                submissionContiner.setVisibility(View.VISIBLE);
                                                ref.child(getString(R.string.dbname_contests))
                                                        .child(userId)
                                                        .child(getString(R.string.created_contest))
                                                        .child(contestId)
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                CreateForm createForm = dataSnapshot.getValue(com.orion.orion.models.CreateForm.class);
                                                                openfor = createForm.getOf();
                                                                type = createForm.getFt();
                                                                contestType = createForm.getCty();
                                                                if (contestType.equals("Quiz")) {
                                                                    mediaLinear.setVisibility(View.GONE);
                                                                    imageLinear.setVisibility(View.GONE);
                                                                    idIv.setVisibility(View.GONE);
                                                                    submissionIv.setVisibility(View.GONE);
                                                                    quizRules.setVisibility(View.VISIBLE);
                                                                } else {
                                                                    quizRules.setVisibility(View.GONE);
                                                                    if (type.equals("Image")) {
                                                                        imageLinear.setVisibility(View.VISIBLE);
                                                                        submissionIv.setVisibility(View.VISIBLE);
                                                                        mediaLinear.setVisibility(View.GONE);
                                                                    } else {
                                                                        mediaLinear.setVisibility(View.VISIBLE);
                                                                        imageLinear.setVisibility(View.GONE);
                                                                        submissionIv.setVisibility(View.GONE);
                                                                    }
                                                                }
                                                                if (openfor.equals("Students")) {
                                                                    collegeidLinearLayout.setVisibility(View.VISIBLE);
                                                                    idIv.setVisibility(View.VISIBLE);
                                                                } else if (openfor.equals("All")) {
                                                                    collegeidLinearLayout.setVisibility(View.GONE);
                                                                    idIv.setVisibility(View.GONE);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public boolean checkValidity() {
        if (contestType.equals("Quiz")) {
            if (openfor.equals("Students"))
                return !collegeEt.getText().equals("") && idIv.getDrawable() != null && collegeEt.getText() != null && idLink != null;
            return true;
        }
        if (openfor.equals("Students"))
            if (collegeEt.getText().equals("") || idIv.getDrawable() == null || collegeEt.getText() == null || idLink == null)
                return false;
            else if (type.equals("Image"))
                return submissionIv.getDrawable() != null;
            else
                return isValidUrl(urlEt.getText().toString());
        else if (type.equals("Image"))
            return submissionIv.getDrawable() != null;
        else
            return isValidUrl(urlEt.getText().toString());
    }

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    public void verifyPermission(String[] permissions) {
        ActivityCompat.requestPermissions(JoiningFormActivity.this, permissions, VERIFY_PERMISSION_REQUEST);
    }

    public boolean checkPermissionArray(String[] permissions) {
        for (String check : permissions)
            if (!checkPermissions(check))
                return false;
        return true;
    }

    public boolean checkPermissions(String permission) {
        int permissionRequest = ActivityCompat.checkSelfPermission(JoiningFormActivity.this, permission);
        return permissionRequest == PackageManager.PERMISSION_GRANTED;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
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
        mediaBtn.setEnabled(true);
        String imgPath = "";
        if (data != null && data.getData() != null && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                imgPath = getPathFromUri(mContext, uri);
                if (imgPath != null) {
                    Log.d(TAG, "onActivityResult: path: " + imgPath);
                    Log.d(TAG, "onActivityResult: uri: " + uri);
                    imgurl = imgPath;
                    setImage();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImage() {
        if (selectedImage == 1) {
            idLink = imgurl;
            Glide.with(getApplicationContext())
                    .load(idLink)
                    .placeholder(R.drawable.load)
                    .error(R.drawable.default_image2)
                    .placeholder(R.drawable.load)
                    .thumbnail(0.25f)
                    .into(idIv);
        }
        if (selectedImage == 2) {
            mediaLink = imgurl;
            Glide.with(getApplicationContext())
                    .load(mediaLink)
                    .placeholder(R.drawable.load)
                    .error(R.drawable.default_image2)
                    .placeholder(R.drawable.load)
                    .into(submissionIv);
        }

    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuthListener = firebaseAuth -> {
            mUser = firebaseAuth.getCurrentUser();
            if (mUser == null) {
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
                            startActivity(intent);
                        })
                        .show();
            } else Log.d(TAG, "onAuthStateChanged: signed_in:" + mUser.getUid());
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
