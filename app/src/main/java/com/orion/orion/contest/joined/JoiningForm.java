package com.orion.orion.contest.joined;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.Adapters.AdapterParticipantRequest;
import com.orion.orion.R;
import com.orion.orion.contest.contestMainActivity;
import com.orion.orion.login.login;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.users;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.Permissions;
import com.orion.orion.util.SNTPClient;

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
    private final Context mContext = JoiningForm.this;
    private EditText collegeEt, urlEt;
    private ImageView idIv, submissionIv, backArrow;
    TextView warn, decline;
    private Button submitBtn, idBtn, mediaBtn;
    boolean isKitKat;
    private String imgurl = "";
    private int selectedImage;
    String mediaLink = "", idLink = "", userId, contestId;
    private FirebaseMethods mFirebaseMethods;
    private LinearLayout a1, a2, a3;
    int imageCount = 0;
    String openfor = "";
    LinearLayout mediaLinear, imageLinear;
    String type = "";
    String p5 = "p5", p6 = "p6";
    public LinearLayout linearLayout;
    String isJuryOrHost = "false";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
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
        isJuryOrHost = i.getStringExtra("isJuryOrHost");

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
        linearLayout = findViewById(R.id.pro);
        backArrow = findViewById(R.id.backarrow);
        decline = findViewById(R.id.decline);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_contests))
                .child(userId)
                .child(getString(R.string.created_contest))
                .child(contestId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        CreateForm createForm = dataSnapshot.getValue(CreateForm.class);
                        openfor = createForm.getOf();
                        type = createForm.getFt();

                        Log.d(TAG, "onDataChange: " + type + "  " + createForm);
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
                .child(getString(R.string.field_Participant_List))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        submitBtn.setEnabled(false);

                        if (dataSnapshot.exists()) {
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
                                            if (snapshot.exists()) {
                                                submitBtn.setEnabled(false);
                                                warn.setVisibility(View.VISIBLE);
                                            }else{
                                                submitBtn.setEnabled(true);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Submit Joining Form");
                builder.setMessage("Are you sure you want to submit this form?");

//                set buttons
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        boolean ok = checkValidity();
                        if (ok) {
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            linearLayout.setVisibility(View.VISIBLE);

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
                                    String timeStamp = String.valueOf(date.getTime());

                                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                                    String JoiningKey = db.child(getString(R.string.dbname_contests))
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(getString(R.string.joined_contest))
                                            .push().getKey();


                                    if (!type.equals("Image")) {
                                        mediaLink = urlEt.getText().toString();
                                    }
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put(getString(R.string.field_college), collegeEt.getText().toString());
                                    hashMap.put(getString(R.string.field_status), "waiting");
                                    hashMap.put(getString(R.string.field_host), userId);
                                    hashMap.put(getString(R.string.field_contest_ID), contestId);
                                    hashMap.put(getString(R.string.field_timestamp), timeStamp);
                                    hashMap.put(getString(R.string.field_joining_ID), JoiningKey);
                                    hashMap.put(getString(R.string.field_id_link), idLink);
                                    hashMap.put(getString(R.string.field_media_link), mediaLink);
                                    hashMap.put(getString(R.string.field_user_id), FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    db.child(getString(R.string.dbname_contests))
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(getString(R.string.joined_contest))
                                            .child(JoiningKey)
                                            .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            DatabaseReference db2 = FirebaseDatabase.getInstance().getReference();

                                            HashMap<String, Object> hashMap2 = new HashMap<>();
                                            hashMap2.put(getString(R.string.field_timestamp), timeStamp);
                                            hashMap2.put(getString(R.string.field_joining_ID), JoiningKey);
                                            hashMap2.put(getString(R.string.field_total_score), 0);
                                            hashMap2.put(getString(R.string.field_contest_ID), contestId);
                                            hashMap2.put(getString(R.string.field_media_link), mediaLink);
                                            hashMap2.put(getString(R.string.field_user_id), FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            db2.child(getString(R.string.dbname_request))
                                                    .child(getString(R.string.dbname_participantList))
                                                    .child(contestId)
                                                    .child(JoiningKey)
                                                    .setValue(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    int c = 0;
                                                    if (!idLink.equals("")) {
                                                        mFirebaseMethods.uploadContest(imageCount, idLink, null, contestId, p5, JoiningKey);

                                                    } else {
                                                        c++;
                                                    }
                                                    if (type.equals("Image")) {
                                                        mFirebaseMethods.uploadContest(imageCount, mediaLink, null, contestId, p6, JoiningKey);
                                                    } else {
                                                        c++;
                                                    }

                                                    if (c == 2) {
                                                        linearLayout.setVisibility(View.GONE);
                                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                        Intent i = new Intent(JoiningForm.this, contestMainActivity.class);
                                                        startActivity(i);
                                                        Toast.makeText(JoiningForm.this, "Your submission has been submitted.", Toast.LENGTH_SHORT).show();


                                                    }
                                                }
                                            });
                                        }
                                    });


                                    Log.e(SNTPClient.TAG, rawDate);

                                }

                                @Override
                                public void onError(Exception ex) {
                                    Log.e(SNTPClient.TAG, ex.getMessage());
                                }
                            });


                        } else {
                            Toast.makeText(JoiningForm.this, "please fill all entries Correctly!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();


            }
        });







if (isJuryOrHost.equals("true")){
    decline.setVisibility(View.VISIBLE);
}
    }

    public boolean checkValidity() {

        if (openfor.equals("Students")) {

            if (collegeEt.getText().equals("") || idIv.getDrawable() == null||collegeEt.getText()==null) {

                return false;
            }else{

                if (type.equals("Image")) {

                    if (submissionIv.getDrawable() == null) {

                        return false;

                    }

                }else {

                    return isValidUrl(urlEt.getText().toString());
                }
            }

        }else{

            if (type.equals("Image")) {

                if (submissionIv.getDrawable() == null) {

                    return false;

                }

            }else {

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
        return permissionRequest == PackageManager.PERMISSION_GRANTED;
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
            Glide.with(JoiningForm.this)
                    .load(idLink)
                    .placeholder(R.drawable.load)
                    .error(R.drawable.default_image2)
                    .placeholder(R.drawable.load)
                    .thumbnail(0.25f)
                    .into(idIv);
        }
        if (selectedImage == 2) {

            mediaLink = imgurl;

            Glide.with(JoiningForm.this)
                    .load(mediaLink)
                    .placeholder(R.drawable.load)
                    .error(R.drawable.default_image2)
                    .placeholder(R.drawable.load)
                    .into(submissionIv);            }

    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
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
                            Intent intent = new Intent(mContext, login.class);
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
