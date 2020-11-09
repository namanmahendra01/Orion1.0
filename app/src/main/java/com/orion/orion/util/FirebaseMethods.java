package com.orion.orion.util;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
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
    private static final float maxHeight = 1280.0f;
    private static final float maxWidth = 1280.0f;

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
    public void uploadContest(final int count, final String imgURL, Bitmap bm, String contestKey, String p, String joiningkey){
        FilePaths filepaths = new FilePaths();

        if (!imgURL.equals("")) {
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            String newKey = FirebaseDatabase.getInstance().getReference().child(mContext.getString(R.string.dbname_contests)).push().getKey();

            final StorageReference storageReference = mStorageReference
                    .child(filepaths.FIREBASE_CONTEST_STORAGE + "/" + user_id + "/" + contestKey + "/" + newKey);

            if (bm == null) {
                bm = ImageManager.getBitmap(imgURL);
            }


            String imgUrl2= compressImage(imgURL);

             bm = ImageManager.getBitmap(imgUrl2);


            byte[] bytes;



           bytes = ImageManager.getBytesFromBitmap(bm, 100);


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



    public String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return sdf.format(new Date());
    }


    public String compressImage(String imagePath) {
        Log.d(TAG, "compressImage: "+imagePath);
  
        File file = new File(imagePath);
        if (file.exists() && file.canRead()) {
            Log.d(TAG, "compressImage: yes");
            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

            float imgRatio = (float) actualWidth / (float) actualHeight;
            float maxRatio = maxWidth / maxHeight;

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
                bmp = BitmapFactory.decodeFile(imagePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

            if (bmp != null) {
                bmp.recycle();
            }

            ExifInterface exif;
            try {
                exif = new ExifInterface(imagePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream out = null;
            String filepath = getFilename();
            try {
                out = new FileOutputStream(filepath);

                //write the compressed bitmap at the destination specified by filename.
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return filepath;

        }
        return "";
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String getFilename() {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                    + "/Android/data/"
                    + mContext.getApplicationContext().getPackageName()
                    + "/Files/Compressed");

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs();
            }

            String mImageName = "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
            String uriString = (mediaStorageDir.getAbsolutePath() + "/" + mImageName);
            ;
            return uriString;

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
                            ref.child(mContext.getString(R.string.dbname_username))
                                    .child(snapshot.child("jname_1").getValue().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))
                                                            .child(snapshot.getKey())
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

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                        if (!snapshot.child("jname_2").getValue().toString().equals("")){
                            ref.child(mContext.getString(R.string.dbname_username))
                                    .child(snapshot.child("jname_2").getValue().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))
                                                            .child(snapshot.getKey())
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

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                        if (!snapshot.child("jname_3").getValue().toString().equals("")){
                            ref.child(mContext.getString(R.string.dbname_username))
                                    .child(snapshot.child("jname_3").getValue().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))
                                                            .child(snapshot.getKey())
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


}




