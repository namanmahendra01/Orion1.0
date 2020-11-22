package com.orion.orion.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.abedelazizshe.lightcompressorlibrary.CompressionListener;
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor;
import com.abedelazizshe.lightcompressorlibrary.VideoQuality;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
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
import com.orion.orion.R;
import com.orion.orion.login.login;
import com.orion.orion.models.Photo;
import com.orion.orion.util.FilePaths;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.ImageManager;
import com.orion.orion.util.SquareImageView;
import com.orion.orion.util.StringManipilation;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class PostVideoActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int SIZE_IN_BYTES = 100000000;
    private static final int DURATION_IN_MS = 60000;
    private final int REQUEST_SELECT_VIDEO = 0;
    private final int REQUEST_SELECT_THUMBNAIL = 1;
    final Handler[] mHandler = new Handler[1];
    final Runnable[] updateProgressAction = new Runnable[1];

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods mFirebaseMethods;
    private DatabaseReference myRef;

    private final Context mContext = PostVideoActivity.this;
    private RelativeLayout rootView;
    private ImageView back;
    private ExtendedFloatingActionButton fab;
    private TextView progress;
    private CardView videoBox;
    private PlayerView playerView;
    private ImageView play2;
    private ImageView close;
    private ImageView mute;
    private ImageView unmute;
    private ImageView thumbnailImage;

    private ImageView thumbnailButton;
    private SquareImageView thumbnail;
    private StorageReference mStorageReference;

    private TextView duration;
    private TextView post;
    private TextView inputCaption;

    SimpleExoPlayer simpleExoPlayer;
    private String caption;
    private int postCount = 0;
    private Uri videoUri;
    private Uri imageUri;

    public PostVideoActivity() {
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (simpleExoPlayer!=null) {
            simpleExoPlayer.release();
        }
        videoBox.setVisibility(View.INVISIBLE);
        VideoCompressor.cancel();
        progress.setVisibility(View.INVISIBLE);
        if(videoUri!=null){
            File file = new File(Objects.requireNonNull(videoUri.getPath()));
            if (file.exists()) file.delete();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_video);

        setupFirebaseAuth();
        setReadStoragePermission();
        initWidgets();
        back.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("Back without saving")
                    .setMessage("You will discard all the changes you made")
                    .setCancelable(false)
                    .setPositiveButton("Go", (dialog, id) -> finish())
                    .setNegativeButton("Stay", (dialog, id) -> dialog.cancel())
                    .show();
        });
        rootView = findViewById(R.id.relLayout);
        rootView.setOnClickListener(v -> {
            if(v.getId()!=inputCaption.getId()){
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
            }
        });
        fab.setOnClickListener(v -> {
            fab.setEnabled(false);
            VideoCompressor.cancel();
            if (simpleExoPlayer != null) simpleExoPlayer.release();
            progress.setVisibility(View.INVISIBLE);
            if (videoUri != null) {
                File file = new File(Objects.requireNonNull(videoUri.getPath()));
                if (file.exists()) file.delete();
            }
            imageUri = null;
            if (thumbnail.getVisibility() == View.VISIBLE) thumbnail.setImageURI(null);
            pickVideo();
        });
        post.setOnClickListener(v -> postVideo());
        mute.setOnClickListener(view -> {
            if (simpleExoPlayer != null) {
                mute.setVisibility(View.GONE);
                unmute.setVisibility(View.VISIBLE);
                if (simpleExoPlayer != null)
                    simpleExoPlayer.setVolume(AudioManager.STREAM_MUSIC);
            }
        });
        unmute.setOnClickListener(view -> {
            if (simpleExoPlayer != null) {
                mute.setVisibility(View.VISIBLE);
                unmute.setVisibility(View.GONE);
                if (simpleExoPlayer != null) simpleExoPlayer.setVolume(0f);
            }
        });
        close.setOnClickListener(v -> {
            simpleExoPlayer.release();
            videoBox.setVisibility(View.INVISIBLE);
            VideoCompressor.cancel();
            progress.setVisibility(View.INVISIBLE);
            if (videoUri != null) {
                File file = new File(Objects.requireNonNull(videoUri.getPath()));
                if (file.exists()) file.delete();
            }
        });
        play2.setOnClickListener(v -> {
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(true);
                play2.setVisibility(View.INVISIBLE);
                thumbnail.setVisibility(View.GONE);
            }
        });
        videoBox.setOnClickListener(v -> {
            if (simpleExoPlayer != null && v != mute && v != unmute && v != close && play2.getVisibility() == View.INVISIBLE && simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(false);
                play2.setVisibility(View.VISIBLE);
            }
        });
        thumbnailButton.setOnClickListener(v -> pickImage());
        post.setOnClickListener(v -> postVideo());
    }

    private void initWidgets() {
        Log.d(TAG, "initWidgets: started");
        back = findViewById(R.id.backarrow);
        fab = findViewById(R.id.fab);
        progress = findViewById(R.id.progress);
        videoBox = findViewById(R.id.videoBox);
        thumbnail = findViewById(R.id.thumbnail);
        thumbnailImage = findViewById(R.id.thumbnailImage);

        playerView = findViewById(R.id.player_view);
        play2 = findViewById(R.id.play);
        mute = findViewById(R.id.mute);
        unmute = findViewById(R.id.unmute);
        close = findViewById(R.id.close);
        duration = findViewById(R.id.duration);
        post = findViewById(R.id.post);
        inputCaption = findViewById(R.id.inputCaption);
        thumbnailButton = findViewById(R.id.thumbnailButton);
        Log.d(TAG, "initWidgets: completed");
    }

    private void postVideo() {
        caption = inputCaption.getText().toString();
        boolean flag= progress.getText().equals("100% completed")||progress.getVisibility()!=View.VISIBLE;
        if (videoUri != null && flag && imageUri != null && caption.length()<150) {
            Log.d(TAG, "postVideo: preparing to upload");
            File f = new File(Objects.requireNonNull(videoUri.getPath()));
            long size = f.length();
            Log.d(TAG, "postVideo: videoUri" + videoUri.getPath());
            Log.d(TAG, "postVideo: size" + size / 1024 / 1024 + "MB");
            uploadVideo();
        } else {
            if (videoUri == null)
                Toast.makeText(mContext, "No video to upload :(", Toast.LENGTH_SHORT).show();
            else if(caption.length()>=150)
                Toast.makeText(mContext, "Caption size must be less then 150 letters //0‑0\\\\", Toast.LENGTH_SHORT).show();
            else if(imageUri==null)
                Toast.makeText(mContext, "Please add a thumbnail also ༼ つ ◕_◕ ༽つdt", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mContext, "Please wait for some more time", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadVideo() {
        FilePaths filepaths = new FilePaths();
        String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final StorageReference storageReferencePhoto = mStorageReference.child(filepaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/post" + (postCount + 1));

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Thumbnail...");
        progressDialog.show();



        String imgUrl2= mFirebaseMethods.compressImage(getPathFromUri(mContext,imageUri));

        Bitmap bm = ImageManager.getBitmap(imgUrl2);


        byte[] bytes;

        bytes = ImageManager.getBytesFromBitmap(bm, 100);

        storageReferencePhoto.putBytes(bytes)
                .addOnSuccessListener(taskSnapshot -> {
                    progressDialog.dismiss();
                    Toast.makeText(PostVideoActivity.this, "Thumbnail Uploaded", Toast.LENGTH_SHORT).show();
                    final StorageReference storageReferenceVideo = mStorageReference.child(filepaths.FIREBASE_VIDEO_STORAGE + "/" + user_id + "/post" + (postCount + 1));
                    progressDialog.setTitle("Uploading Video...");
                    progressDialog.show();
                    Log.d(TAG, "uploadVideo: " + new File(Objects.requireNonNull(videoUri.getPath())));
                    storageReferenceVideo.putFile(videoUri)
                            .addOnSuccessListener(taskSnapshot1 -> {
                                progressDialog.dismiss();
                                Toast.makeText(PostVideoActivity.this, "Video Uploaded", Toast.LENGTH_SHORT).show();
                                storageReferencePhoto.getDownloadUrl()
                                        .addOnSuccessListener(uriThumbnail -> storageReferenceVideo.getDownloadUrl()
                                                .addOnSuccessListener(uriVideo -> {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(PostVideoActivity.this, "Video Uploaded", Toast.LENGTH_SHORT).show();
                                                    addVideoToDatabase(caption, uriThumbnail.toString(), uriVideo.toString());
                                                    startActivity(new Intent(mContext, ProfileActivity.class));
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(PostVideoActivity.this, "Failed to retrieve thumbnail url " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(mContext, ProfileActivity.class));
                                                }))
                                        .addOnFailureListener(e -> Toast.makeText(PostVideoActivity.this, "Failed to retrieve thumbnail url " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(PostVideoActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(mContext, ProfileActivity.class));
                            })
                            .addOnProgressListener(snapshot -> {
                                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(PostVideoActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(mContext, ProfileActivity.class));
                })
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                });
    }

    private void addVideoToDatabase(String caption, String uriThumbnail, String uriVideo) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        String tags = StringManipilation.getTags(caption);
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_user_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCap(caption);
        photo.setDc(sdf.format(new Date()));
        photo.setIp(uriVideo);
        photo.setT(uriThumbnail);
        photo.setTg(tags);
        photo.setUi(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        photo.setPi(newPhotoKey);
        photo.setTy("video");
        assert newPhotoKey != null;
        myRef.child(mContext.getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_follower)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                    myRef.child(mContext.getString(R.string.dbname_users))
                            .child(Objects.requireNonNull(snapshot1.getKey()))
                            .child(mContext.getString(R.string.post_updates)).child(newPhotoKey)
                            .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initializePlayer() {
        LoadControl loadControl = new DefaultLoadControl();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        if (simpleExoPlayer != null) simpleExoPlayer.release();
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setupVideoPlayer(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "RecyclerView VideoPlayer"));

        if (uri != null) {
            final String path = getPathFromUri(mContext, uri);
            assert path != null;
            File f = new File(path);
            float size = f.length();

            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            if (mute.getVisibility() == View.VISIBLE) simpleExoPlayer.setVolume(0f);
            else simpleExoPlayer.setVolume(AudioManager.STREAM_MUSIC);
            simpleExoPlayer.prepare(videoSource);
            simpleExoPlayer.setPlayWhenReady(false);
            simpleExoPlayer.getPlaybackState();
            simpleExoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                }

                @Override
                public void onLoadingChanged(boolean isLoading) {
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == Player.STATE_BUFFERING) {
                        thumbnail.setVisibility(View.INVISIBLE);
                    } else if (playbackState == Player.STATE_READY) {
                        duration.setVisibility(View.VISIBLE);
                        //display duration
                        updateProgressAction[0] = new Runnable() {
                            @Override
                            public void run() {
                                updateProgress();
                            }

                            @SuppressLint("SetTextI18n")
                            private void updateProgress() {
                                long delayMs = TimeUnit.SECONDS.toMillis(1);
                                mHandler[0].postDelayed(updateProgressAction[0], delayMs);
                                duration.setText((int) (simpleExoPlayer.getDuration() - simpleExoPlayer.getCurrentPosition()) / 1000 + "s");
                            }
                        };
                        mHandler[0] = new Handler();
                        mHandler[0].post(updateProgressAction[0]);
                    } else if (playbackState == Player.STATE_ENDED) {
                        play2.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onPlayerStateChanged: imageUri" + imageUri);
                        thumbnail.setVisibility(View.VISIBLE);
                        thumbnail.setImageURI(null);
                        thumbnail.setImageURI(imageUri);
                        simpleExoPlayer.seekTo(0);
                        simpleExoPlayer.setPlayWhenReady(false);
//                        simpleExoPlayer.release();
                    } else if (playbackState == Player.STATE_IDLE)
                        play2.setVisibility(View.VISIBLE);
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                }

                @Override
                public void onPositionDiscontinuity(int reason) {

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }

                @Override
                public void onSeekProcessed() {

                }
            });
        }
        thumbnail.setVisibility(View.VISIBLE);
        thumbnail.setImageURI(null);
        thumbnail.setImageURI(imageUri);
    }

    private void setReadStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select thumbnail"), REQUEST_SELECT_THUMBNAIL);
    }

    private void pickVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select video"), REQUEST_SELECT_VIDEO);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                return getDataColumn(context, contentUri, null, null);
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
        else if ("file".equalsIgnoreCase(uri.getScheme())) return uri.getPath();
        return null;
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

    public File savevideoUri(String filePath) {
        if (filePath != null) {
            File videoUri = new File(filePath);
            String videoUriName = videoUri.getName();
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "Orion");
            boolean success = true;
            if (!folder.exists()) {
                Toast.makeText(mContext, "Directory Does Not Exist, Create It", Toast.LENGTH_SHORT).show();
                success = folder.mkdirs();
            }
            if (success) {
                Toast.makeText(mContext, "Directory Created", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "savevideoUri: failed to create direnctory");
                Toast.makeText(mContext, "Could not access external storage. Plz check ur permissions or Try again", Toast.LENGTH_SHORT).show();
            }

            if (Build.VERSION.SDK_INT >= 29) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, videoUriName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "video/mp4");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, folder.getPath());
                values.put(MediaStore.Images.Media.IS_PENDING, 1);
                Uri collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                Uri fileUri = getApplicationContext().getContentResolver().insert(collection, values);

                if (fileUri != null) {
                    try {
                        final ParcelFileDescriptor descriptor = getApplicationContext().getContentResolver().openFileDescriptor(fileUri, "w");
                        if (descriptor != null) {
                            FileOutputStream out = new FileOutputStream(descriptor.getFileDescriptor());
                            FileInputStream inputStream = new FileInputStream(videoUri);
                            byte[] buf = new byte[4096];
                            while (true) {
                                int sz = inputStream.read(buf);
                                if (sz <= 0) break;
                                out.write(buf, 0, sz);
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                values.clear();
                values.put(MediaStore.Video.Media.IS_PENDING, 0);
                assert fileUri != null;
                getApplicationContext().getContentResolver().update(fileUri, values, null, null);
                return new File(Objects.requireNonNull(fileUri.getPath()));
            } else {
                File downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File desFile = new File(downloadsPath, videoUriName);
                if (desFile.exists()) desFile.delete();
                try {
                    desFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return desFile;
            }

        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        fab.setEnabled(true);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_SELECT_VIDEO) {
                if (data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        //get path of file
                        final String path = getPathFromUri(mContext, uri);
                        assert path != null;
                        //get size of file
                        float size = (new File(path)).length();
                        //get duration of file
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(mContext, uri);
                        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        assert time != null;
                        final long timeInMillisec = Long.parseLong(time);

                        //checking met conditions
                        if (size > SIZE_IN_BYTES || timeInMillisec > DURATION_IN_MS) {
//                        if(false){
                            Toast.makeText(mContext, String.format("%.2f", size / 1024 / 1024) + " MB, " + timeInMillisec / 1000 + "s o_O", Toast.LENGTH_LONG).show();
                            new AlertDialog.Builder(mContext)
                                    .setTitle("File too large!")
                                    .setMessage("You are required to select a video file of " + System.getProperty("line.separator") + "1. Less then 100MB" + System.getProperty("line.separator") + "2. Less than 1 minute of duration." + System.getProperty("line.separator") + "Either one of those 2 conditions are not met")
                                    .setPositiveButton("Ok", (dialog, which) -> {
                                        dialog.dismiss();
                                        // Continue with delete operation
                                    })
                                    .show();
                        } else {
                            Toast.makeText(mContext, String.format("%.2f", size / 1024 / 1024) + " MB, " + timeInMillisec / 1000 + "s ^_^", Toast.LENGTH_LONG).show();
                            videoBox.setVisibility(View.VISIBLE);
                            initializePlayer();
                            setupVideoPlayer(uri);
                            if (size < 8000000) videoUri = uri;
                            else {
                                VideoQuality quality = VideoQuality.VERY_LOW;
                                if (size >= 8000000 && size < 16000000)
                                    quality = VideoQuality.VERY_HIGH;
                                else if (size >= 16000000 && size <= 26000000)
                                    quality = VideoQuality.HIGH;
                                else if (size >= 26000000 && size <= 40000000)
                                    quality = VideoQuality.MEDIUM;
                                else if (size >= 40000000 && size <= 80000000)
                                    quality = VideoQuality.LOW;

//                                MediaExtractor mex = new MediaExtractor();
//                                try {
//                                    mex.setDataSource(path);
//                                    MediaFormat mf = mex.getTrackFormat(0);
//                                    int bitRate = mf.getInteger(MediaFormat.KEY_BIT_RATE);
//                                    Log.d(TAG, "onActivityResult: bitRate"+bitRate);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }

                                String standard = " bytes";
                                if (size > 1024 * 1024) {
                                    size /= 1024 * 1024;
                                    standard = " MB";
                                } else if (size > 1024) {
                                    size /= 1024;
                                    standard = " KB";
                                }
                                final File desFile = savevideoUri(path);
                                assert desFile != null;
                                Log.d(TAG, "onActivityResult: desFile " + desFile.getPath());
                                final float finalSize = size;
                                final String finalStandard = standard;
                                Log.d(TAG, "onActivityResult: size of file selected: " + finalSize + " " + finalStandard);
                                VideoCompressor.start(path, desFile.getPath(), new CompressionListener() {
                                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                                    @Override
                                    public void onStart() {
                                        progress.setVisibility(View.VISIBLE);
                                        progress.setText("0.0%");
                                        Log.d(TAG, "onStart: ");
                                    }

                                    @SuppressLint({"DefaultLocale", "SetTextI18n"})
                                    @Override
                                    public void onSuccess() {
                                        float size = desFile.length();
                                        String standard = " bytes";
                                        if (size > 1024 * 1024) {
                                            size /= 1024 * 1024;
                                            standard = " MB";
                                        } else if (size > 1024) {
                                            size /= 1024;
                                            standard = " KB";
                                        }
                                        progress.setText("100% completed");
                                        Log.d(TAG, "onSuccess: file_size" + size);
                                        Log.d(TAG, "onSuccess: videoUri" + videoUri);
                                        Toast.makeText(mContext, "We trimmed it to - " + String.format("%.2f", size) + standard, Toast.LENGTH_LONG).show();
                                        videoUri = Uri.fromFile(desFile);
                                        setupVideoPlayer(videoUri);
                                    }

                                    @Override
                                    public void onFailure(@NotNull String failureMessage) {
                                        progress.setText(failureMessage);
                                        Log.d(TAG, "onFailure: " + failureMessage);
                                        if (desFile.exists())
                                            desFile.delete();
                                    }

                                    @SuppressLint({"DefaultLocale", "SetTextI18n"})
                                    @Override
                                    public void onProgress(float percent) {
                                        Log.d(TAG, "onProgress: " + percent);
                                        progress.setText(String.format("%.2f", percent) + "%");
                                    }

                                    @Override
                                    public void onCancelled() {
                                        Log.d(TAG, "onCancelled: ");
                                    }
                                }, quality, false, true);
                            }
                        }
                    }
                }
            } else {
                if (requestCode == REQUEST_SELECT_THUMBNAIL) {
                    if (data != null && data.getData() != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            final String path = getPathFromUri(mContext, uri);
                            Log.d(TAG, "onActivityResult: path: " + path);
                            Log.d(TAG, "onActivityResult: uri: " + uri);
                            imageUri = uri;
                            thumbnailImage.setImageURI(imageUri);
                            if (thumbnail.getVisibility() == View.VISIBLE)
                                thumbnail.setImageURI(imageUri);
                        }
                    }

                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        mFirebaseMethods = new FirebaseMethods(mContext);
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
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
                            Intent intent = new Intent(mContext, login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            settings.edit().clear().apply();
                            startActivity(intent);
                        })
                        .show();
            } else Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());
        };
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postCount = mFirebaseMethods.getImageCount(dataSnapshot);
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