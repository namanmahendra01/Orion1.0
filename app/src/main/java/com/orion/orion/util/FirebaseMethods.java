package com.orion.orion.util;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.orion.orion.Notifications.Data;
import com.orion.orion.Notifications.Sender;
import com.orion.orion.Notifications.Token;
import com.orion.orion.R;
import com.orion.orion.contest.contestMainActivity;
import com.orion.orion.contest.create.CheckContest;
import com.orion.orion.contest.joined.JoiningForm;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.Photo;
import com.orion.orion.profile.PostPhotoActivity;
import com.orion.orion.profile.ProfileActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context mContext;
    private FirebaseDatabase mFirebaseDatabase;
    private StorageReference mStorageReference;
    private DatabaseReference myRef;
    private String userID;
    private double mPhotoUploadProgress = 0;
    private RequestQueue requestQueue;
    boolean flag1=true,flag2=true,flag3=true,flag4=true,flag5=true,flag6=true;


    public FirebaseMethods(Context context) {
        mContext = context;
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        requestQueue = Volley.newRequestQueue(mContext);

        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
            userID = mAuth.getCurrentUser().getUid();
        }

    }

    @SuppressLint("DefaultLocale")
    public void uploadNewPhoto(String photoType, final String caption, final int count, final String imgURL, Bitmap bm) {
        FilePaths filepaths = new FilePaths();
        if (photoType.equals(mContext.getString(R.string.new_photo))) {
            String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            final StorageReference storageReference = mStorageReference.child(filepaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/post" + (count + 1));
            if (bm == null) bm = ImageManager.getBitmap(imgURL);
            File file=new File(imgURL);
            long length=file.length()/1024;

            byte[] bytes;
            if(length<200){
                bytes = ImageManager.getBytesFromBitmap(bm, 100);

            }else if(length<500){
                bytes = ImageManager.getBytesFromBitmap(bm, 65);

            }else if(length<800){
                bytes = ImageManager.getBytesFromBitmap(bm, 45);

            }else{
                bytes = ImageManager.getBytesFromBitmap(bm, 25);

            }            UploadTask uploadTask;
            uploadTask = storageReference.putBytes(bytes);
            ProgressDialog dialog=ProgressDialog.show(mContext,"", "Uploading... - ",true);

//            Log.d(TAG, "uploadNewPhoto: photoType"+photoType);
//            Log.d(TAG, "uploadNewPhoto: caption"+caption);
//            Log.d(TAG, "uploadNewPhoto: count"+count);
//            Log.d(TAG, "uploadNewPhoto: imgURL"+imgURL);
//            Log.d(TAG, "uploadNewPhoto: filepaths"+filepaths);
//            Log.d(TAG, "uploadNewPhoto: user_id"+user_id);
//            Log.d(TAG, "uploadNewPhoto: StorageReference"+storageReference);
//            Log.d(TAG, "uploadNewPhoto: bm"+bm);
//            Log.d(TAG, "uploadNewPhoto: bytes"+ Arrays.toString(bytes));
            Log.d(TAG, "uploadNewPhoto: uploadTask"+uploadTask);

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
                ProgressDialog.show(mContext,"", "Uploading... - "+String.format("%.0f", progress)+"%",true);
                if (progress - 15 > mPhotoUploadProgress) {
                    Toast.makeText(mContext, "Photo Upload Progress" + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "onProgress: upload progress" + progress + "% done");
            });
        } else if (photoType.equals(mContext.getString(R.string.profile_photo))) {
            Log.d(TAG, "uploadNewPhoto: uploading new PROFILE photo" + (mContext.getString(R.string.profile_photo)));
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference storageReference = mStorageReference.child(filepaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");
            if (bm == null) bm = ImageManager.getBitmap(imgURL);
            File file=new File(imgURL);
            long length=file.length()/1024;

            byte[] bytes;
            if(length<200){
                bytes = ImageManager.getBytesFromBitmap(bm, 100);

            }else if(length<500){
                bytes = ImageManager.getBytesFromBitmap(bm, 65);

            }else if(length<800){
                bytes = ImageManager.getBytesFromBitmap(bm, 45);

            }else{
                bytes = ImageManager.getBytesFromBitmap(bm, 25);

            }            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);
            uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    setProfilePhoto(uri.toString());
                    Toast.makeText(mContext, "Photo Upload success", Toast.LENGTH_SHORT).show();
                }
            })).addOnFailureListener(e -> {
                Log.d(TAG, "onFailure: Photo Upload Failed");
                Toast.makeText(mContext, "Photo Upload failed", Toast.LENGTH_SHORT).show();
            }).addOnProgressListener(taskSnapshot -> {
                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                if (progress - 15 > mPhotoUploadProgress) {
                    Toast.makeText(mContext, "Photo Upload Progress" + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "onProgress: upload progress" + progress + "% done");
            });
        }
    }

    public void uploadContest(final int count, final String imgURL, Bitmap bm, String contestKey, String p, String joiningkey) {
        FilePaths filepaths = new FilePaths();

        if (!imgURL.equals("")) {
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            String newKey = FirebaseDatabase.getInstance().getReference().child(mContext.getString(R.string.dbname_contests)).push().getKey();

            final StorageReference storageReference = mStorageReference
                    .child(filepaths.FIREBASE_CONTEST_STORAGE + "/" + user_id + "/" + contestKey + "/" + newKey);

            if (bm == null) {
                bm = ImageManager.getBitmap(imgURL);
            }
            File file=new File(imgURL);
            long length=file.length()/1024;

            byte[] bytes;
            if(length<200){
                bytes = ImageManager.getBytesFromBitmap(bm, 100);

            }else if(length<500){
                bytes = ImageManager.getBytesFromBitmap(bm, 65);

            }else if(length<800){
                bytes = ImageManager.getBytesFromBitmap(bm, 45);

            }else{
                bytes = ImageManager.getBytesFromBitmap(bm, 25);

            }
            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri firebaseurl = uri;
                            CreateForm createForm = new CreateForm();
//                           Toast.makeText(mContext, "Photo Upload success" , Toast.LENGTH_SHORT).show();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            if (p.equals("p1")) {
                                flag1=false;
                                ref.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.created_contest))
                                        .child(contestKey)
                                        .child("jpic1").setValue(firebaseurl.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        flag1=true;
                                        if (flag1&&flag2&&flag3&&flag4){
                                            ((CheckContest)mContext).progress.setVisibility(View.GONE);
                                            ((CheckContest)mContext).  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Intent i = new Intent(mContext, contestMainActivity.class);
                                            mContext. startActivity(i);
                                            Toast.makeText(mContext ,"Your Contest request has been submitted.", Toast.LENGTH_SHORT).show();




                                        }
                                    }
                                });
                            }
                            if (p.equals("p2")) {

                                flag2=false;
                                ref.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.created_contest))
                                        .child(contestKey)
                                        .child("jpic2").setValue(firebaseurl.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        flag2=true;
                                        if (flag1&&flag2&&flag3&&flag4){
                                            ((CheckContest)mContext).progress.setVisibility(View.GONE);
                                            ((CheckContest)mContext).  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Intent i = new Intent(mContext, contestMainActivity.class);
                                            mContext. startActivity(i);
                                            Toast.makeText(mContext ,"Your Contest request has been submitted.", Toast.LENGTH_SHORT).show();


                                        }
                                    }
                                });
                            }
                            if (p.equals("p3")) {
                                flag3=false;

                                ref.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.created_contest))
                                        .child(contestKey)
                                        .child("jpic3").setValue(firebaseurl.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        flag3=true;
                                        if (flag1&&flag2&&flag3&&flag4){
                                            ((CheckContest)mContext).progress.setVisibility(View.GONE);
                                            ((CheckContest)mContext).  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Intent i = new Intent(mContext, contestMainActivity.class);
                                            mContext. startActivity(i);
                                            Toast.makeText(mContext ,"Your Contest request has been submitted.", Toast.LENGTH_SHORT).show();


                                        }
                                    }
                                });
                            }
                            if (p.equals("p4")) {
                                flag4=false;

                                ref.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.created_contest))
                                        .child(contestKey)
                                        .child("poster").setValue(firebaseurl.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        flag4=true;
                                        if (flag1&&flag2&&flag3&&flag4){
                                            ((CheckContest)mContext).progress.setVisibility(View.GONE);
                                            ((CheckContest)mContext).  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Intent i = new Intent(mContext, contestMainActivity.class);
                                            mContext. startActivity(i);
                                            Toast.makeText(mContext ,"Your Contest request has been submitted.", Toast.LENGTH_SHORT).show();



                                        }
                                    }
                                });

                            }
                            if (p.equals("p5")) {

                                flag5=false;
                                ref.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.joined_contest))
                                        .child(joiningkey)
                                        .child("idLink").setValue(firebaseurl.toString());
                                ref.child(mContext.getString(R.string.dbname_request))
                                        .child(mContext.getString(R.string.dbname_participantList))
                                        .child(contestKey)
                                        .child(joiningkey)
                                        .child("idLink").setValue(firebaseurl.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        flag5=true;
                                        if (flag5&&flag6) {
                                            ((JoiningForm) mContext).linearLayout.setVisibility(View.GONE);
                                            ((JoiningForm) mContext).  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Intent i = new Intent(mContext, contestMainActivity.class);
                                            mContext. startActivity(i);
                                            Toast.makeText(mContext ,"Your participation request has been submitted.", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            }
                            if (p.equals("p6")) {

                                flag6=false;
                                ref.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.joined_contest))
                                        .child(joiningkey)
                                        .child("mediaLink").setValue(firebaseurl.toString());
                                ref.child(mContext.getString(R.string.dbname_request))
                                        .child(mContext.getString(R.string.dbname_participantList))
                                        .child(contestKey)
                                        .child(joiningkey)
                                        .child("mediaLink").setValue(firebaseurl.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        flag6=true;
                                        if (flag5&&flag6) {
                                            ((JoiningForm) mContext).linearLayout.setVisibility(View.GONE);
                                            ((JoiningForm) mContext).  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Intent i = new Intent(mContext, contestMainActivity.class);
                                            mContext. startActivity(i);
                                            Toast.makeText(mContext ,"Your participation request has been submitted.", Toast.LENGTH_SHORT).show();


                                        }
                                    }
                                });

                            }
                        }

                    });

//                   Intent intent = new Intent(mContext, MainActivity.class);
//                   mContext.startActivity(intent);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo Upload Failed");
                    Toast.makeText(mContext, "Pics Upload failed", Toast.LENGTH_SHORT).show();


                }
            });
        }

    }
    public void sendNotification(final String hisUID, final String username,final String message,final String tittle) {
        final  DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query= allToken.orderByKey().equalTo(hisUID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), username + " " + message, tittle, hisUID, R.drawable.ic_home);
                    Sender sender = new Sender(data, token.getToken());

                    try {
                        JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("JSON_RESPONSE", "onResponse: " + response.toString());
                                    }

                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onError: " + error.toString());

                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {

                                Map<String,String> headers = new HashMap<>();
                                headers.put("Content-Type","application/json");
                                headers.put("Authorization","key=AAAAPBXWnHs:APA91bFkGt9VULR-c7XBIcsF0SYlKTiWod88zRpKwIBf-74w46zKCUuIqKIsATJ_Lbv56jdPWhJ0QclNp56kN8__I1mJOQcKJeGjg6CfrIkDYL9SmEy_Dz0zDhyF4WJIlMdE0khSk7qI");

                                return headers;
                            }
                        };


                        requestQueue.add(jsonObjectRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void setProfilePhoto(String url) {

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);
    }

    public String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return sdf.format(new Date());
    }

    private void addPhotoToDatabase(String caption, String url) {
        Log.d(TAG, "addPhtotto database: adding photo to database");
        String tags = StringManipilation.getTags(caption);
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_user_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimeStamp());
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




    public void publishResut(boolean manual, String Conteskey, ArrayList<ParticipantList> participantLists,
                             LinearLayout progress, FragmentActivity activity, ArrayList<ParticipantList> winnerList) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        if (manual) {

            ref.child(mContext.getString(R.string.dbname_contestlist))
                    .child(Conteskey)
                    .child("result")
                    .setValue(true);

            ref.child(mContext.getString(R.string.dbname_contests))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("completed")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                long x = (long) snapshot.getValue();
                                ref.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("completed")
                                        .setValue(x + 1);
                            } else {
                                ref.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("completed")
                                        .setValue(1);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }else{
            ref.child(mContext.getString(R.string.dbname_contestlist))
                    .child(Conteskey)
                    .child("result")
                    .setValue(true);
        }

        ref.child(mContext.getString(R.string.dbname_contests))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_created_contest))
                .child(Conteskey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.child("jname_1").getValue().toString().equals("")){
                            ref.child(mContext.getString(R.string.dbname_users))
                                    .orderByChild(mContext.getString(R.string.field_username))
                                    .equalTo(snapshot.child("jname_1").getValue().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                for (DataSnapshot snapshot1:snapshot.getChildren()){
                                                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))
                                                            .child(snapshot1.getKey())
                                                            .child("judged");

                                                          ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    if (snapshot.exists()) {
                                                                        long l = (long) snapshot.getValue();
                                                                        ref2.setValue(l+1);
                                                                    }else{
                                                                        ref2.setValue(1);
                                                                    }

                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });

                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                        if (!snapshot.child("jname_2").getValue().toString().equals("")){
                            ref.child(mContext.getString(R.string.dbname_users))
                                    .orderByChild(mContext.getString(R.string.field_username))
                                    .equalTo(snapshot.child("jname_2").getValue().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                for (DataSnapshot snapshot1:snapshot.getChildren()){
                                                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))
                                                            .child(snapshot1.getKey())
                                                            .child("judged");

                                                    ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                long l = (long) snapshot.getValue();
                                                                ref2.setValue(l+1);
                                                            }else{
                                                                ref2.setValue(1);
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                        if (!snapshot.child("jname_3").getValue().toString().equals("")){
                            ref.child(mContext.getString(R.string.dbname_users))
                                    .orderByChild(mContext.getString(R.string.field_username))
                                    .equalTo(snapshot.child("jname_3").getValue().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                for (DataSnapshot snapshot1:snapshot.getChildren()){
                                                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))
                                                            .child(snapshot1.getKey())
                                                            .child("judged");

                                                    ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                long l = (long) snapshot.getValue();
                                                                ref2.setValue(l+1);
                                                            }else{
                                                                ref2.setValue(1);
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        if (winnerList.size() != 0) {

            for (int x = 0; x < winnerList.size(); x++) {
                DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))
                        .child(winnerList.get(x).getUserid())
                        .child("win");

                int finalX = x;
                ref3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        Log.d(TAG, "onDataChange: mnmn "+winnerList.get(finalX).getUserid());
                        if (snapshot1.exists()){
                            long l= (long)snapshot1.getValue();
                            ref3.setValue(l+1);
                        }else{
                            ref3.setValue(1);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
            }

        if (participantLists.size() != 0) {
            for (int x = 0; x < participantLists.size(); x++) {

                  sendNotification(participantLists.get(x).getUserid(), "", "Result has been declared of a contest.Check your ranking now.", "Result Declared");


                addToHisNotification("" + participantLists.get(x).getUserid(), "Result has been declared of a contest.Check your ranking now.");

                int finalX = x;
                ref.child(mContext.getString(R.string.dbname_contests))
                        .child(participantLists.get(finalX).getUserid())
                        .child("participated")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    long l= (long) snapshot.getValue();
                                    ref.child(mContext.getString(R.string.dbname_contests))
                                            .child(participantLists.get(finalX).getUserid())
                                            .child("participated")
                                            .setValue(l+ 1);
                                    if (finalX==participantLists.size()-1){

                                        progress.setVisibility(View.GONE);
                                    }
                                } else {
                                    ref.child(mContext.getString(R.string.dbname_contests))
                                            .child(participantLists.get(finalX).getUserid())
                                            .child("participated")
                                            .setValue(1);
                                    if (finalX==participantLists.size()-1){
                                        progress.setVisibility(View.GONE);

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


            }
        }else {
            progress.setVisibility(View.GONE);

        }
    }


    private void addToHisNotification(String hisUid, String notification) {

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
                String timestamp = String.valueOf(date.getTime());


                //data to put in notification
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("pId", "false");
                hashMap.put("timeStamp", timestamp);
                hashMap.put("pUid", hisUid);
                hashMap.put("seen", "false");
                hashMap.put("notificaton", notification);
                hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
                        .addOnSuccessListener(aVoid -> {

                        }).addOnFailureListener(e -> {

                });
                Log.e(SNTPClient.TAG, rawDate);

            }

            @Override
            public void onError(Exception ex) {
                Log.e(SNTPClient.TAG, ex.getMessage());
            }
        });


    }
    public int getImageCount(DataSnapshot dataSnapshot) {

        int count = 0;
        for (DataSnapshot ds : dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()) {
            count++;
        }
        return count;

    }

    public void updateUserAccountsettings(String displayname, String description, String domain) {
        if (displayname != null) {

            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_display))
                    .setValue(displayname);
        }
        if (description != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);
        }

        if (domain != null) {


            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child("domain")
                    .setValue(domain);
        }


    }

    public void updateUsername(String username) {

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }

    public void updateemail(String email) {

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);

    }




//    public void updateTopUsers() {
//        ArrayList<TopUsers> mListOverall = new ArrayList<>();
//        ArrayList<TopUsers> mListFollower = new ArrayList<>();
//        mListOverall.clear();
//        mListFollower.clear();
//
//        Log.d(TAG, "updateTopUsers" + mContext);
//        Query query = myRef.child(mContext.getString(R.string.dbname_leaderboard));
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
//
//                    //getting user ids, username and profile photos
//                    String user_id = singleSnapshot.getKey();
//                    String domain = (String) singleSnapshot.child(mContext.getString(R.string.field_domain)).getValue();
//                    int followers = (int) (long) singleSnapshot.child(mContext.getString(R.string.field_followers)).getValue();
//                    int rating = (int) (long) singleSnapshot.child(mContext.getString(R.string.field_all_time)).child(mContext.getString(R.string.field_post)).getValue()
//                            + (int) (long) singleSnapshot.child(mContext.getString(R.string.field_all_time)).child(mContext.getString(R.string.field_followers)).getValue()
//                            + (int) (long) singleSnapshot.child(mContext.getString(R.string.field_all_time)).child(mContext.getString(R.string.field_contest)).getValue();
//
//                    TopUsers emptyItem = new TopUsers("", 0, "");
//                    TopUsers dataItemOverall = new TopUsers(user_id, rating, domain);
//                    TopUsers dataItemFollower = new TopUsers(user_id, followers, domain);
//
//
//                    if (mListOverall.size() == 0 || mListFollower.size() == 0) {
//                        mListOverall.add(dataItemOverall);
//                        mListFollower.add(dataItemFollower);
//                    } else {
//                        int l = mListOverall.size();
//
//                        //loop to push in between and next one further away for overall
//                        for (int i = 0; i < l; i++) {
//                            int r = mListOverall.get(i).getRating();
//                            if (rating >= r) {
//                                mListOverall.add(emptyItem);
//                                for (int j = mListOverall.size() - 1; j > i; j--)
//                                    mListOverall.set(j, mListOverall.get(j - 1));
//                                mListOverall.set(i, dataItemOverall);
//                                break;
//                            }
//                            //pushing at the end
//                            else if (i == l - 1)
//                                mListOverall.add(dataItemOverall);
//                        }
//
//                        //loop to push in between and next one further away for follower
//                        for (int i = 0; i < l; i++) {
//                            int r = mListFollower.get(i).getRating();
//                            if (rating >= r) {
//                                mListFollower.add(emptyItem);
//                                for (int j = mListFollower.size() - 1; j > i; j--)
//                                    mListFollower.set(j, mListFollower.get(j - 1));
//                                mListFollower.set(i, dataItemFollower);
//                                break;
//                            }
//                            //pushing at the end
//                            else if (i == l - 1)
//                                mListFollower.add(dataItemFollower);
//                        }
//                    }
//
//
//                    //removing extra nodes
//                    if (mListOverall.size() == 101 || mListFollower.size() == 101) {
//                        mListOverall.remove(100);
//                        mListFollower.remove(100);
//
//                    }
//                }
//
//                for (int i = 0; i < mListOverall.size(); i++) {
////                    myRef.child("top_users").child("overall").child(String.valueOf(i + 1)).setValue(mListOverall.get(i));
////                    myRef.child("top_users").child("follower").child(String.valueOf(i + 1)).setValue(mListFollower.get(i));
//                    myRef.child("top_users").child("overall").child(String.valueOf(i + 1)).child(mContext.getString(R.string.field_user_id)).setValue(mListOverall.get(i).getUser_id());
//                    myRef.child("top_users").child("overall").child(String.valueOf(i + 1)).child(mContext.getString(R.string.field_domain)).setValue(mListOverall.get(i).getDomain());
//                    myRef.child("top_users").child("follower").child(String.valueOf(i + 1)).child(mContext.getString(R.string.field_user_id)).setValue(mListFollower.get(i).getUser_id());
//                    myRef.child("top_users").child("follower").child(String.valueOf(i + 1)).child(mContext.getString(R.string.field_domain)).setValue(mListFollower.get(i).getDomain());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

}




