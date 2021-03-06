package com.orion.orion.util;

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

import com.android.volley.RequestQueue;
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
import com.orion.orion.contest.create.CC_CheckActivity;
import com.orion.orion.contest.create.CreatedActivity;
import com.orion.orion.contest.joined.JoinedActivity;
import com.orion.orion.contest.joined.JoiningFormActivity;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.ParticipantList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

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
    boolean flag1 = true, flag2 = true, flag3 = true, flag4 = true, flag5 = true, flag6 = true;
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


            String imgUrl2 = compressImage(imgURL);

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
                                flag1 = false;
                                ref.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.created_contest))
                                        .child(contestKey)
                                        .child(mContext.getString(R.string.field_jury_pic_1)).setValue(firebaseurl.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        flag1 = true;
                                        if (flag1 && flag2 && flag3 && flag4) {
                                            ((CC_CheckActivity) mContext).progress.setVisibility(View.GONE);
                                            ((CC_CheckActivity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Intent i = new Intent(mContext, CreatedActivity.class);
                                            mContext.startActivity(i);
                                            Toast.makeText(mContext, "Your Contest request has been submitted.", Toast.LENGTH_SHORT).show();


                                        }
                                    }
                                });
                            }
                            if (p.equals("p2")) {

                                flag2 = false;
                                ref.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.created_contest))
                                        .child(contestKey)
                                        .child(mContext.getString(R.string.field_jury_pic_2)).setValue(firebaseurl.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        flag2 = true;
                                        if (flag1 && flag2 && flag3 && flag4) {
                                            ((CC_CheckActivity) mContext).progress.setVisibility(View.GONE);
                                            ((CC_CheckActivity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Intent i = new Intent(mContext, CreatedActivity.class);
                                            mContext.startActivity(i);
                                            Toast.makeText(mContext, "Your Contest request has been submitted.", Toast.LENGTH_SHORT).show();


                                        }
                                    }
                                });
                            }
                            if (p.equals("p3")) {
                                flag3 = false;

                                ref.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.created_contest))
                                        .child(contestKey)
                                        .child(mContext.getString(R.string.field_jury_pic_3)).setValue(firebaseurl.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        flag3 = true;
                                        if (flag1 && flag2 && flag3 && flag4) {
                                            ((CC_CheckActivity) mContext).progress.setVisibility(View.GONE);
                                            ((CC_CheckActivity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Intent i = new Intent(mContext, CreatedActivity.class);
                                            mContext.startActivity(i);
                                            Toast.makeText(mContext, "Your Contest request has been submitted.", Toast.LENGTH_SHORT).show();


                                        }
                                    }
                                });
                            }
                            if (p.equals("p4")) {
                                flag4 = false;

                                ref.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.created_contest))
                                        .child(contestKey)
                                        .child(mContext.getString(R.string.field_poster)).setValue(firebaseurl.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        flag4 = true;
                                        if (flag1 && flag2 && flag3 && flag4) {
                                            ((CC_CheckActivity) mContext).progress.setVisibility(View.GONE);
                                            ((CC_CheckActivity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Intent i = new Intent(mContext, CreatedActivity.class);
                                            mContext.startActivity(i);
                                            Toast.makeText(mContext, "Your Contest request has been submitted.", Toast.LENGTH_SHORT).show();


                                        }
                                    }
                                });

                            }
                            if (p.equals("p5")) {

                                flag5 = false;
                                ref.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.joined_contest))
                                        .child(joiningkey)
                                        .child(mContext.getString(R.string.field_id_link)).setValue(firebaseurl.toString());
                                ref.child(mContext.getString(R.string.dbname_request))
                                        .child(mContext.getString(R.string.dbname_participantList))
                                        .child(contestKey)
                                        .child(joiningkey)
                                        .child(mContext.getString(R.string.field_id_link)).setValue(firebaseurl.toString()).addOnSuccessListener(aVoid -> {
                                            flag5 = true;
                                            if (flag5 && flag6) {
                                                ((JoiningFormActivity) mContext).linearLayout.setVisibility(View.GONE);
                                                ((JoiningFormActivity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                Intent i = new Intent(mContext, JoinedActivity.class);
                                                mContext.startActivity(i);
                                                Toast.makeText(mContext, "Your participation request has been submitted.", Toast.LENGTH_SHORT).show();
                                            }

                                        });
                            }
                            if (p.equals("p6")) {

                                flag6 = false;
                                ref.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.joined_contest))
                                        .child(joiningkey)
                                        .child(mContext.getString(R.string.field_media_link)).setValue(firebaseurl.toString());
                                ref.child(mContext.getString(R.string.dbname_request))
                                        .child(mContext.getString(R.string.dbname_participantList))
                                        .child(contestKey)
                                        .child(joiningkey)
                                        .child(mContext.getString(R.string.field_media_link)).setValue(firebaseurl.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        flag6 = true;
                                        if (flag5 && flag6) {
                                            ((JoiningFormActivity) mContext).linearLayout.setVisibility(View.GONE);
                                            ((JoiningFormActivity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Intent i = new Intent(mContext, JoinedActivity.class);
                                            mContext.startActivity(i);
                                            Toast.makeText(mContext, "Your participation request has been submitted.", Toast.LENGTH_SHORT).show();


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

    public void sendNotification(final String hisUID, final String username, final String message, final String tittle) {
        final DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisUID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), username + " " + message, tittle, hisUID, R.drawable.orion_logo_png);
                    Sender sender = new Sender(data, token.getToken());
                    try {
                        JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                "https://fcm.googleapis.com/fcm/send", senderJsonObj,
                                response -> Log.d("JSON_RESPONSE", "onResponse: " + response.toString()),
                                error -> Log.d("JSON_RESPONSE", "onError: " + error.toString())
                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAAPBXWnHs:APA91bFkGt9VULR-c7XBIcsF0SYlKTiWod88zRpKwIBf-74w46zKCUuIqKIsATJ_Lbv56jdPWhJ0QclNp56kN8__I1mJOQcKJeGjg6CfrIkDYL9SmEy_Dz0zDhyF4WJIlMdE0khSk7qI");
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


    public String getTim() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return sdf.format(new Date());
    }


    public String compressImage(String imagePath) {

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
                    .child(mContext.getString(R.string.field_result))
                    .setValue(true)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            ref.child(mContext.getString(R.string.dbname_contests))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(mContext.getString(R.string.field_contest_completed))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                long x = (long) snapshot.getValue();
                                                ref.child(mContext.getString(R.string.dbname_contests))
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child(mContext.getString(R.string.field_contest_completed))
                                                        .setValue(x + 1)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                publishResutFurther(manual, Conteskey, participantLists,
                                                                        progress, activity, winnerList);
                                                            }
                                                        });
                                            } else {
                                                ref.child(mContext.getString(R.string.dbname_contests))
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child(mContext.getString(R.string.field_contest_completed))
                                                        .setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        publishResutFurther(manual, Conteskey, participantLists, progress, activity, winnerList);
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    });


        } else {
            ref.child(mContext.getString(R.string.dbname_contestlist))
                    .child(Conteskey)
                    .child(mContext.getString(R.string.field_result))
                    .setValue(true)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            publishResutFurther(manual, Conteskey, participantLists, progress, activity, winnerList);
                        }
                    });
        }


    }

    public void publishResutFurther(boolean manual, String Conteskey, ArrayList<ParticipantList> participantLists, LinearLayout progress, FragmentActivity activity, ArrayList<ParticipantList> winnerList) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child(mContext.getString(R.string.dbname_contests))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.created_contest))
                .child(Conteskey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.child(mContext.getString(R.string.field_jury_name_1)).getValue().toString().equals("")) {
                            ref.child(mContext.getString(R.string.dbname_username))
                                    .child(snapshot.child(mContext.getString(R.string.field_jury_name_1)).getValue().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))
                                                        .child(snapshot.getValue().toString())
                                                        .child(mContext.getString(R.string.field_contest_judged));

                                                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            long l = (long) snapshot.getValue();
                                                            ref2.setValue(l + 1);
                                                            Log.d(TAG, "onDataChange: cgy 1");
                                                        } else {
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
                        if (!snapshot.child(mContext.getString(R.string.field_jury_name_2)).getValue().toString().equals("")) {
                            ref.child(mContext.getString(R.string.dbname_username))
                                    .child(snapshot.child(mContext.getString(R.string.field_jury_name_2)).getValue().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))
                                                        .child(snapshot.getValue().toString())
                                                        .child(mContext.getString(R.string.field_contest_judged));

                                                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            long l = (long) snapshot.getValue();
                                                            ref2.setValue(l + 1);
                                                        } else {
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
                        if (!snapshot.child(mContext.getString(R.string.field_jury_name_3)).getValue().toString().equals("")) {
                            ref.child(mContext.getString(R.string.dbname_username))
                                    .child(snapshot.child(mContext.getString(R.string.field_jury_name_3)).getValue().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))
                                                        .child(snapshot.getValue().toString())
                                                        .child(mContext.getString(R.string.field_contest_judged));

                                                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            long l = (long) snapshot.getValue();
                                                            ref2.setValue(l + 1);
                                                        } else {
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
        Log.d(TAG, "onDataChange: cgy 3" + winnerList + " " + participantLists);

        if (winnerList.size() != 0) {
            Log.d(TAG, "onDataChange: cgy 2");


            for (int x = 0; x < winnerList.size(); x++) {
                DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))
                        .child(winnerList.get(x).getUi())
                        .child(mContext.getString(R.string.field_contest_wins));

                int finalX = x;
                int finalX1 = x;
                ref3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        if (snapshot1.exists()) {
                            long l = (long) snapshot1.getValue();
                            ref3.setValue(l + 1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (finalX1 == winnerList.size() - 1) {
                                                sendNotyToParticipants(participantLists, progress);

                                            }
                                        }
                                    });
                        } else {
                            ref3.setValue(1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (finalX1 == winnerList.size() - 1) {
                                                sendNotyToParticipants(participantLists, progress);

                                            }
                                        }

                                        ;
                                    });

                        }

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
            if (winnerList.size() == 0) {
                sendNotyToParticipants(participantLists, progress);
            }


        }

    }

    private void sendNotyToParticipants(ArrayList<ParticipantList> participantLists, LinearLayout progress) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG, "onDataChange: cgy 5");

        if (participantLists.size() != 0) {
            Log.d(TAG, "onDataChange: cgy 4");

            for (int x = 0; x < participantLists.size(); x++) {

                sendNotification(participantLists.get(x).getUi(), "", "Result has been declared of a contest.Check your ranking now.", "Result Declared");


                addToHisNotification("" + participantLists.get(x).getUi(), "Result has been declared of a contest.Check your ranking now.");

                int finalX = x;
                ref.child(mContext.getString(R.string.dbname_contests))
                        .child(participantLists.get(finalX).getUi())
                        .child(mContext.getString(R.string.field_contest_participated))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    long l = (long) snapshot.getValue();
                                    ref.child(mContext.getString(R.string.dbname_contests))
                                            .child(participantLists.get(finalX).getUi())
                                            .child(mContext.getString(R.string.field_contest_participated))
                                            .setValue(l + 1);
                                    if (finalX == participantLists.size() - 1) {
                                        Log.d(TAG, "onDataChange: cgy 7");

                                        progress.setVisibility(View.GONE);
                                    }
                                } else {
                                    ref.child(mContext.getString(R.string.dbname_contests))
                                            .child(participantLists.get(finalX).getUi())
                                            .child(mContext.getString(R.string.field_contest_participated))
                                            .setValue(1);
                                    if (finalX == participantLists.size() - 1) {
                                        Log.d(TAG, "onDataChange: cgy 8");

                                        progress.setVisibility(View.GONE);

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


            }
        } else {
            Log.d(TAG, "onDataChange: cgy 6");

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
                String timestamp = String.valueOf(date.getTime());


//        data to put in notification
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("pId", "false");

                hashMap.put(mContext.getString(R.string.field_timestamp), timestamp);

                hashMap.put("pUid", hisUid);

                hashMap.put(mContext.getString(R.string.field_notification_message), notification);
                hashMap.put(mContext.getString(R.string.field_if_seen), "false");

                hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_users));
                ref.child(hisUid).child(mContext.getString(R.string.field_Notifications)).child(timestamp).setValue(hashMap)
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




