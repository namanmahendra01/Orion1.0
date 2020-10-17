package com.orion.orion.contest.joined;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.TargetApi;
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
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orion.orion.R;
import com.orion.orion.models.CreateForm;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.Permissions;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.UniversalImageLoader;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.orion.orion.profile.ProfileActivity.VERIFY_PERMISSION_REQUEST;

public class JoiningForm extends AppCompatActivity {
    private static final String TAG = "JoiningForm";

    private EditText nameEt, collegeEt, urlEt;
    private ImageView idIv, submissionIv;
    TextView warn;
    private Button submitBtn, idBtn, mediaBtn;
    boolean isKitKat;
    private String imgurl = "";
    private String mAppend = "file:/";
    private int selectedImage;
    String mediaLink = "", idLink = "", userId, contestId;
    private FirebaseMethods mFirebaseMethods;
    private LinearLayout a1, a2, a3;
    int imageCount = 0;
    String openfor = "";
    LinearLayout mediaLinear, imageLinear;
    String type = "";
    String p5 = "p5", p6 = "p6";

    //firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joining_form);

        setupFirebaseAuth();

        mFirebaseMethods = new FirebaseMethods(JoiningForm.this);


        Intent i = getIntent();
        userId = i.getStringExtra("userId");
        contestId = i.getStringExtra("contestId");

        nameEt = findViewById(R.id.nameEt);
        collegeEt = findViewById(R.id.collegeEt);
        urlEt = findViewById(R.id.url_submission);

        submitBtn = findViewById(R.id.submitBtn);
        idBtn = findViewById(R.id.selectid);
        mediaBtn = findViewById(R.id.selectSubmission);

        idIv = findViewById(R.id.idIv);
        submissionIv = findViewById(R.id.submisionIv);
        a1 = findViewById(R.id.college);
        a2 = findViewById(R.id.collegeid);
        warn = findViewById(R.id.warn);
        imageLinear = findViewById(R.id.ImageLinearLayout);
        mediaLinear = findViewById(R.id.mediaLinearLayout);


        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG, "onCreate: llll"+userId+"  "+contestId);
        db.child(getString(R.string.dbname_contests))
                .child(userId)
                .child(getString(R.string.created_contest))
                .child(contestId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        CreateForm createForm = dataSnapshot.getValue(CreateForm.class);
                        openfor = createForm.getOpenFor();
                        type = createForm.getFiletype();

                        Log.d(TAG, "onDataChange: "+type+"  "+createForm);
                        if (type.equals("Image")) {
                            imageLinear.setVisibility(View.VISIBLE);
                            submissionIv.setVisibility(View.VISIBLE);
                            mediaLinear.setVisibility(View.GONE);

                        } else {
                            mediaLinear.setVisibility(View.VISIBLE);
                            imageLinear.setVisibility(View.GONE);
                            submissionIv.setVisibility(View.GONE);

                        }
                        if (openfor.equals("Students")) {
                            a1.setVisibility(View.VISIBLE);
                            a2.setVisibility(View.VISIBLE);
                            idIv.setVisibility(View.VISIBLE);
                        } else if (openfor.equals("All")) {
                            a1.setVisibility(View.GONE);
                            a2.setVisibility(View.GONE);
                            idIv.setVisibility(View.GONE);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_contestlist));
        ref.child(contestId)
                .child("participantlist")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            submitBtn.setEnabled(false);
                            warn.setVisibility(View.VISIBLE);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        idBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImage = 1;
                if (checkPermissionArray(Permissions.PERMISSIONS)) {

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        isKitKat = true;
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    } else {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    }
                } else {
                    verifyPermission(Permissions.PERMISSIONS);

                }


            }
        });
        mediaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImage = 2;
                if (checkPermissionArray(Permissions.PERMISSIONS)) {

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        isKitKat = true;
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    } else {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                    }
                } else {
                    verifyPermission(Permissions.PERMISSIONS);

                }

            }


        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ok = checkValidity();
                if (ok) {

                    SNTPClient.getDate(TimeZone.getTimeZone("Asia/Colombo"), new SNTPClient.Listener() {
                        @Override
                        public void onTimeReceived(String rawDate) {
                            // rawDate -> 2019-11-05T17:51:01+0530


                            String str_date = rawDate;
                            java.text.DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                            Date date = null;
                            try {
                                date = (Date) formatter.parse(str_date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "onCreateView: timestampyesss" + date.getTime());
                            String timeStamp = String.valueOf(date.getTime());

                            DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                            String JoiningKey = db.child(getString(R.string.dbname_contests))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(getString(R.string.joined_contest))
                                    .push().getKey();


                            if (!type.equals("Image")) {
                                mediaLink=urlEt.getText().toString();
                            }
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("name", nameEt.getText().toString());
                            hashMap.put("college", collegeEt.getText().toString());
                            hashMap.put("status", "waiting");
                            hashMap.put("payment", "true");
                            hashMap.put("hostId", userId);
                            hashMap.put("contestKey", contestId);
                            hashMap.put("timestamp", timeStamp);
                            hashMap.put("joiningKey", JoiningKey);
                            hashMap.put("idLink", idLink);
                            hashMap.put("mediaLink", mediaLink);
                            hashMap.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            db.child(getString(R.string.dbname_contests))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(getString(R.string.joined_contest))
                                    .child(JoiningKey)
                                    .setValue(hashMap);

                            DatabaseReference db2 = FirebaseDatabase.getInstance().getReference();

                            HashMap<String, Object> hashMap2 = new HashMap<>();
                            hashMap2.put("timestamp", timeStamp);
                            hashMap2.put("joiningKey", JoiningKey);
                            hashMap2.put("totalScore", 0);
                            hashMap2.put("contestkey", contestId);
                            hashMap2.put("mediaLink", mediaLink);
                            hashMap2.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            db2.child(getString(R.string.dbname_request))
                                    .child(getString(R.string.dbname_participantList))
                                    .child(contestId)
                                    .child(JoiningKey)
                                    .setValue(hashMap2);


                            mFirebaseMethods.uploadContest(imageCount, idLink, null, contestId, p5, JoiningKey);
                            if (!type.equals("Image")) {
                                mFirebaseMethods.uploadContest(imageCount, mediaLink, null, contestId, p6, JoiningKey);
                            }

                            Log.e(SNTPClient.TAG, rawDate);

                        }

                        @Override
                        public void onError(Exception ex) {
                            Log.e(SNTPClient.TAG, ex.getMessage());
                        }
                    });


                } else {
                    Toast.makeText(JoiningForm.this, "please fill all entries!", Toast.LENGTH_SHORT).show();
                }
            }


        });


    }

    public boolean checkValidity() {


        if (openfor.equals("Students")) {
            if (collegeEt.getText().equals("") || idIv.getDrawable() == null) {
                return false;
            }

        }
        if (type.equals("Image")) {
            if (submissionIv.getDrawable() == null) {
                return false;

            } else {
                return isValidUrl(urlEt.getText().toString());
            }

        }

        return true;

    }

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }


    public void verifyPermission(String[] permissions) {
        ActivityCompat.requestPermissions(
                JoiningForm.this,
                permissions,
                VERIFY_PERMISSION_REQUEST
        );
    }

    public boolean checkPermissionArray(String[] permissions) {

        for (int i = 0; i < permissions.length; i++) {
            String check = permissions[i];
            if (!checkPermissions(check)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkPermissions(String permission) {

        int permissionRequest = ActivityCompat.checkSelfPermission(JoiningForm.this, permission);
        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            return false;

        } else {
            return true;
        }
    }

    @TargetApi(19)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String imgPath = "";
        if (data != null && data.getData() != null && resultCode == RESULT_OK) {
            boolean isImageFromGoogleDrive = false;
            Uri uri = data.getData();

            if (isKitKat && DocumentsContract.isDocumentUri(JoiningForm.this, uri)) {
                if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        imgPath = Environment.getExternalStorageDirectory() + "/" + split[1];

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
                        for (int i = 0; i < temp.length; i++) {
                            File tempf = new File(temp[i] + "/" + split[1]);
                            if (tempf.exists()) {
                                imgPath = temp[i] + "/" + split[1];

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
                        cursor = JoiningForm.this.getContentResolver().query(contentUri, projection, null, null,
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
                        cursor = JoiningForm.this.getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(column);
                            imgPath = cursor.getString(column_index);

                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                } else if ("com.google.android.apps.docs.storage".equals(uri.getAuthority())) {
                    isImageFromGoogleDrive = true;

                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                Cursor cursor = null;
                String column = "_data";
                String[] projection = {column};

                try {
                    cursor = JoiningForm.this.getContentResolver().query(uri, projection, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndexOrThrow(column);
                        imgPath = cursor.getString(column_index);

                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                imgPath = uri.getPath();

            }


        }
        Log.d(TAG, "onActivityResult: " + imgPath);
        imgurl = imgPath;
        setImage();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImage() {
        if (selectedImage == 1) {

            idLink = imgurl;
            UniversalImageLoader.setImage(idLink, idIv, null, mAppend);
        }
        if (selectedImage == 2) {

            mediaLink = imgurl;

            UniversalImageLoader.setImage(mediaLink, submissionIv, null, mAppend);
        }

    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve user information from the database
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
