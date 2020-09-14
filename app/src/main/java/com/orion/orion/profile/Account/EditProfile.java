package com.orion.orion.profile.Account;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orion.orion.R;
import com.orion.orion.models.users;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.UniversalImageLoader;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class EditProfile extends AppCompatActivity {
    private static final String TAG = "EditProfile";
    String domain;
    Spinner spinner;
    private ImageView mProfilephoto;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private String userID;
    private FirebaseMethods mFirebaseMethods;
    private Context mcontext;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mDisplayname, mUsername, mdescription, mEmail, mphonenumber;
    private TextView mchangeprofilephoto;
    private boolean isKitKat;
    private users setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_editprofile);

        initializeImageLoader();
        mProfilephoto = (ImageView) findViewById(R.id.profile_photo);
        mDisplayname = (EditText) findViewById(R.id.display_name);
        mdescription = (EditText) findViewById(R.id.description);
        mUsername = (EditText) findViewById(R.id.username);
        mEmail = (EditText) findViewById(R.id.email);
        spinner = (Spinner) findViewById(R.id.domain);
        mFirebaseMethods = new FirebaseMethods(this);
        mchangeprofilephoto = (TextView) findViewById(R.id.change_Profile_Photo);
        ImageView backarrow = (ImageView) findViewById(R.id.backarrow);
        ImageView checkmark = (ImageView) findViewById(R.id.saveChanges);

        setupFirebaseAuth();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                domain = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        backarrow.setOnClickListener(v -> finish());

        checkmark.setOnClickListener(v -> saveProfileSetting());
    }

    private void saveProfileSetting() {

        final String dislayname = mDisplayname.getText().toString();
        final String username = mUsername.getText().toString();
        final String description = mdescription.getText().toString();

        //if user made a change to username
        if (!setting.getUsername().equals(username)) 
            checkifuserexist(username);
        if (!setting.getDisplay_name().equals(dislayname))
            mFirebaseMethods.updateUserAccountsettings(dislayname, null, null);
        if (!setting.getDescription().equals(description))
            mFirebaseMethods.updateUserAccountsettings(null, description, null);
        if (!setting.getDomain().equals(domain) && !domain.equals("All"))
            mFirebaseMethods.updateUserAccountsettings(null, null, domain);
    }


    private void checkifuserexist(final String username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_users)).orderByChild(getString(R.string.field_username)).equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
//                    add the username
                    mFirebaseMethods.updateUsername(username);
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
        mEmail.setText(setting.getEmail());
        mchangeprofilephoto.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
        });


    }

    @TargetApi(19)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String imgPath = "";
        if (data != null && data.getData() != null && resultCode == RESULT_OK) {
            boolean isImageFromGoogleDrive = false;
            Uri uri = data.getData();
            if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {
                if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        imgPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                        Intent intent = new Intent(this, EditProfile.class);
                        intent.putExtra(getString(R.string.selected_image), imgPath);
                        intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile));
                        this.finish();
                        startActivity(intent);
                    } else {
                        Pattern DIR_SEPORATOR = Pattern.compile("/");
                        Set<String> rv = new HashSet<>();
                        String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
                        String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
                        String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
                        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
                            if (TextUtils.isEmpty(rawExternalStorage))
                                rv.add("/storage/sdcard0");
                            else
                                rv.add(rawExternalStorage);
                        } else {
                            String rawUserId;
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
                                rawUserId = "";
                            else {
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
                            } else rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
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
                                Intent intent = new Intent(this, EditProfile.class);
                                intent.putExtra(getString(R.string.selected_image), imgPath);
                                intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile));
                                this.finish();
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
                            Intent intent = new Intent(this, EditProfile.class);
                            intent.putExtra(getString(R.string.selected_image), imgPath);
                            intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile));
                            this.finish();
                            startActivity(intent);
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
                        cursor = this.getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(column);
                            imgPath = cursor.getString(column_index);
                            Intent intent = new Intent(this, EditProfile.class);
                            intent.putExtra(getString(R.string.selected_image), imgPath);
                            intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile));
                            this.finish();
                            startActivity(intent);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                } else if ("com.google.android.apps.docs.storage".equals(uri.getAuthority())) {
                    isImageFromGoogleDrive = true;
                    Intent intent = new Intent(this, EditProfile.class);
                    intent.putExtra(getString(R.string.selected_image), imgPath);
                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile));
                    finish();
                    startActivity(intent);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                Cursor cursor = null;
                String column = "_data";
                String[] projection = {column};

                try {
                    cursor = getContentResolver().query(uri, projection, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndexOrThrow(column);
                        imgPath = cursor.getString(column_index);
                        Intent intent = new Intent(this, EditProfile.class);
                        intent.putExtra(getString(R.string.selected_image), imgPath);
                        intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile));
                        finish();
                        startActivity(intent);
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                imgPath = uri.getPath();
                Intent intent = new Intent(this, EditProfile.class);
                intent.putExtra(getString(R.string.selected_image), imgPath);
                intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile));
                finish();
                startActivity(intent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
}