package com.orion.orion;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyLog;
import com.bumptech.glide.Glide;
import com.danikula.videocache.HttpProxyCacheServer;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.Adapters.AdapterComment;
import com.orion.orion.login.login;
import com.orion.orion.models.Comment;
import com.orion.orion.models.Photo;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.SquareImageView;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.View.GONE;
import static com.orion.orion.util.MyApplication.getProxy;

public class ViewPostActivity extends AppCompatActivity {
    private static final String TAG = "ViewPostFragment";

    private Context mContext;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    FirebaseMethods firebaseMethods;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    String currentUsername = "";

    private Photo mphoto;
    PlayerView playerView;
    ProgressBar progressBar;
    boolean play = true;
    long currentPosition = 0;
    SimpleExoPlayer simpleExoPlayer;

    private final String mLikesString = "";
    private String numberoflike = "0";
    ArrayList<Comment> comments = new ArrayList<>();


    private SquareImageView mPostImage, thumbnail, progress2;
    private BottomNavigationViewEx bottomNavigationView;
    private TextView mBackLabel;
    private TextView duration;
    private TextView mCaption;
    private TextView mUsername;
    private TextView mTimestamp;
    private TextView mLikes;
    private TextView mCommentnumber;
    private TextView mcredit;
    private TextView domain;
    private TextView promoteNum;
    private ImageView mBackArrow;
    private ImageView mEllipses;
    private ImageView mStarYellow;
    private ImageView mStarWhite;
    private ImageView mProfileImage;
    private ImageView mComment;
    private ImageView promote;
    private ImageView promoted;
    private ImageView play2;
    private ImageView mute;
    private ImageView unmute;
    //    SP
    Gson gson;
    SharedPreferences sp;
    LinearLayout progress;
    private users mCurrentUser;
    private boolean likeByCurrentsUser2;
    RecyclerView commentRv;
    private ArrayList<Comment> comment2;
    private ArrayList<String> commentID;
    private AdapterComment adapterComment;

    private boolean notifyLike;
    private boolean notifyPromote;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        mContext = this;
        mPostImage = findViewById(R.id.post_image);
        bottomNavigationView = findViewById(R.id.BottomNavViewBar);
        mBackArrow = findViewById(R.id.backarrow);
        mEllipses = findViewById(R.id.ivEllipses);
        mStarWhite = findViewById(R.id.image_star);
        mStarYellow = findViewById(R.id.image_star_yellow);
        mProfileImage = findViewById(R.id.profile_photo1);
        mBackLabel = findViewById(R.id.tvbacklabel1);
        mCaption = findViewById(R.id.image_caption);
        mUsername = findViewById(R.id.username1);
        mTimestamp = findViewById(R.id.images_time);
        mLikes = findViewById(R.id.likes_number);
        mComment = findViewById(R.id.image_shoutout);
        mCommentnumber = findViewById(R.id.comments_number);
        mcredit = findViewById(R.id.credit);
        promote = findViewById(R.id.promote);
        promoted = findViewById(R.id.promoted);
//        domain = findViewById(R.id.domain12);
        promoteNum = findViewById(R.id.promote_number);

        progress = findViewById(R.id.pro);


        play2 = findViewById(R.id.play);
        mute = findViewById(R.id.mute);
        unmute = findViewById(R.id.unmute);
        playerView = findViewById(R.id.player_view);
        progressBar = findViewById(R.id.progress_bar);
        duration = findViewById(R.id.duration);
        thumbnail = findViewById(R.id.thumbnail);

//          Initialize SharedPreference variables
        sp = getApplicationContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        gson = new Gson();


        Intent i = getIntent();
        mphoto = i.getParcelableExtra("photo");
        comments = i.getParcelableArrayListExtra("comments");

        duration.setVisibility(View.GONE);


        commentRv = findViewById(R.id.recyclerComment);
        commentRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        commentRv.setItemViewCacheSize(9);
        commentRv.setDrawingCacheEnabled(true);
        commentRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(20);
        commentRv.setLayoutManager(linearLayoutManager);

        comments = new ArrayList<>();
        commentID = new ArrayList<>();
        adapterComment = new AdapterComment(this, comments, commentID, mphoto.getPi(), mphoto.getUi());
        adapterComment.setHasStableIds(true);
        commentRv.setAdapter(adapterComment);

        getComments( mphoto.getPi(), mphoto.getUi());

        notifyLike = false;
        notifyPromote = false;

        mEllipses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ViewPostActivity.this, mEllipses);
                if (mphoto.getUi().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    popupMenu.getMenuInflater().inflate(R.menu.post_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ViewPostActivity.this);
                            builder.setTitle("Delete");
                            builder.setMessage("Are you sure, you want to delete this Post?");

//                set buttons
                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "DeleteMessage: deleteing message");
                                    DeletePost();

                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                            return true;
                        }
                    });

                    popupMenu.show();


                } else {
                    popupMenu.getMenuInflater().inflate(R.menu.post_menu_all, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ViewPostActivity.this);
                            builder.setTitle("Report");
                            builder.setMessage("Are you sure, you want to Report this Post?");

//                set buttons
                            builder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "DeleteMessage: deleteing message");
                                    ReportPost();

                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();

                            return true;
                        }
                    });

                    popupMenu.show();


                }
            }
        });

        if (mphoto.getTy().equals("photo")) {

            mPostImage.setVisibility(View.VISIBLE);
            play2.setVisibility(View.GONE);
            Glide.with(ViewPostActivity.this)
                    .load(mphoto.getIp())
                    .placeholder(R.drawable.load)
                    .error(R.drawable.default_image2)
                    .placeholder(R.drawable.load)
                    .into(mPostImage);
            playerView.setVisibility(GONE);
            thumbnail.setVisibility(GONE);

        } else {
            unmute.setVisibility(View.VISIBLE);
            play2.setVisibility(View.VISIBLE);
            mPostImage.setVisibility(View.GONE);
            thumbnail.setVisibility(View.VISIBLE);
            playerView.setVisibility(View.VISIBLE);
        }

//                   ***********get Video***********


        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();


//                    get thumbnail
        reference2
                .child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUi())
                .child(mphoto.getPi())
                .child(getString(R.string.thumbnail))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Glide.with(ViewPostActivity.this)
                                    .load(mphoto.getT())
                                    .placeholder(R.drawable.load)
                                    .error(R.drawable.default_image2)
                                    .placeholder(R.drawable.load)
                                    .thumbnail(0.25f)
                                    .into(thumbnail);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//                     play/pause video

        final Handler[] mHandler = new Handler[1];
        final Runnable[] updateProgressAction = new Runnable[1];
        mUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewPostActivity.this, profile.class);
                i.putExtra(getString(R.string.calling_activity), getString(R.string.home));

                i.putExtra(getString(R.string.intent_user), mphoto.getUi());
                startActivity(i);
            }
        });
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewPostActivity.this, profile.class);
                i.putExtra(getString(R.string.calling_activity), getString(R.string.home));

                i.putExtra(getString(R.string.intent_user), mphoto.getUi());
                startActivity(i);
            }
        });

        playerView.getVideoSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
//                if paused
                if (play) {

                    play = false;
                    play2.setVisibility(View.INVISIBLE);

                    LoadControl loadControl = new DefaultLoadControl();
                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                    if (simpleExoPlayer != null) {

                        simpleExoPlayer.release();
                    }

                    simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(ViewPostActivity.this, trackSelector, loadControl);

                    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                            ViewPostActivity.this, Util.getUserAgent(ViewPostActivity.this, "RecyclerView VideoPlayer"));
                    String mediaUrl = mphoto.getIp();
                    HttpProxyCacheServer proxy = getProxy(ViewPostActivity.this);
                    String proxyUrl = proxy.getProxyUrl(mediaUrl);


                    playerView.setPlayer(simpleExoPlayer);


                    playerView.setKeepScreenOn(true);
                    playerView.setKeepScreenOn(true);


                    MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(Uri.parse(proxyUrl));
//                    set Volume
                    if (mute.getVisibility() == View.VISIBLE) {
                        simpleExoPlayer.setVolume(0f);
                    } else if (unmute.getVisibility() == View.VISIBLE) {
                        simpleExoPlayer.setVolume(AudioManager.STREAM_MUSIC);
                    }

                    simpleExoPlayer.prepare(videoSource);
                    simpleExoPlayer.seekTo(currentPosition);
                    simpleExoPlayer.setPlayWhenReady(true);
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

                                progressBar.setVisibility(View.VISIBLE);

                            } else if (playbackState == Player.STATE_READY) {

                                duration.setVisibility(View.VISIBLE);
                                thumbnail.setVisibility(GONE);
                                progressBar.setVisibility(View.GONE);

//                                display duration
                                updateProgressAction[0] = new Runnable() {
                                    @Override
                                    public void run() {
                                        updateProgress();
                                    }

                                    private void updateProgress() {

                                        long delayMs = TimeUnit.SECONDS.toMillis(1);
                                        mHandler[0].postDelayed(updateProgressAction[0], delayMs);
                                        duration.setText(String.valueOf((int) (simpleExoPlayer.getDuration() - simpleExoPlayer.getCurrentPosition()) / 1000));

                                    }

                                };
                                mHandler[0] = new Handler();
                                mHandler[0].post(updateProgressAction[0]);


                            } else if (playbackState == Player.STATE_ENDED) {

                                play2.setVisibility(View.VISIBLE);
                                play = true;
                                thumbnail.setVisibility(View.VISIBLE);
                                simpleExoPlayer.seekTo(0);
                                simpleExoPlayer.setPlayWhenReady(false);
                                simpleExoPlayer.release();

                            } else if (playbackState == Player.STATE_IDLE) {
                                play2.setVisibility(View.VISIBLE);

                            }
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
                } else {
//                    if playing
                    play = true;
                    play2.setVisibility(View.VISIBLE);
                    currentPosition = simpleExoPlayer.getCurrentPosition();
                    simpleExoPlayer.setPlayWhenReady(false);
                    simpleExoPlayer.getPlaybackState();
                    simpleExoPlayer.release();


                }
            }
        });

//        toggle volume
        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mute.setVisibility(View.GONE);
                unmute.setVisibility(View.VISIBLE);
                if (simpleExoPlayer != null) {
                    simpleExoPlayer.setVolume(AudioManager.STREAM_MUSIC);

                }


            }
        });
        unmute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mute.setVisibility(View.VISIBLE);
                unmute.setVisibility(View.GONE);
                if (simpleExoPlayer != null) {
                    simpleExoPlayer.setVolume(0f);

                }
            }
        });

        setupFirebaseAuth();
        getCurrentUser();
        ifCurrentUserLiked();
        ifCurrentUserPromoted();
        numberofPromote();
        getPhototDetail();
        setupWidgets();

        mStarWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "whitestar clicked");

                mStarWhite.setVisibility(View.GONE);
                mStarYellow.setVisibility(View.VISIBLE);
                notifyLike = true;
                addlike();
                NumberOfLikes();
            }
        });
        mStarYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "yellowstar clicked");
                mStarWhite.setVisibility(View.VISIBLE);
                mStarYellow.setVisibility(View.GONE);
                removeLike();
                NumberOfLikes();
                notifyLike = false;
                if (!mphoto.getUi().equals(mAuth.getCurrentUser().getUid()))
                    Log.d(TAG, "onClick: preparing to remove like");
                myRef.child(getString(R.string.dbname_users))
                        .child(mphoto.getUi())
                        .child(getString(R.string.field_Notifications))
                        .orderByKey()
                        .limitToLast(3)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        if (dataSnapshot.exists()
                                                && dataSnapshot.child(getString(R.string.field_notification_message)).getValue().equals("Liked your post")
                                                && dataSnapshot.child("sUid").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                && dataSnapshot.child("pUid").getValue().equals(mphoto.getUi())
                                                && dataSnapshot.child("pId").getValue().equals(mphoto.getPi())) {
                                            Log.d(TAG, "onDataChange: " + dataSnapshot);
                                            Log.d(TAG, "onDataChange: "+(dataSnapshot.exists()
                                                    && dataSnapshot.child(getString(R.string.field_notification_message)).getValue().equals("Liked your post")
                                                    && dataSnapshot.child("sUid").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    && dataSnapshot.child("pUid").getValue().equals(mphoto.getUi())
                                                    && dataSnapshot.child("pId").getValue().equals(mphoto.getPi())
                                            ));
                                            myRef.child(getString(R.string.dbname_users))
                                                    .child(mphoto.getUi())
                                                    .child(getString(R.string.field_Notifications))
                                                    .child(dataSnapshot.getKey()).removeValue()
                                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "onDataChange: Notification Deleted"))
                                                    .addOnFailureListener(e -> Log.d(TAG, "onDataChange: Notification not Deleted"));
                                            break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
        promote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promotePost();

            }
        });


        promoted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unPromotePost();
            }
        });
    }


    private void ReportPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_reports))
                .child(getString(R.string.field_post))
                .child(mphoto.getPi())
                .child(getString(R.string.field_user_id))
                .setValue(mphoto.getUi());
    }

    private void unPromotePost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Promotion");
        builder.setMessage("Are you sure, you want to remove this Promotion?");
//                set buttons
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(VolleyLog.TAG, "Rejecting: rejected ");

                promote.setVisibility(View.VISIBLE);
                promoted.setVisibility(View.GONE);



                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                db.child(getString(R.string.dbname_promote))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(mphoto.getPi())
                        .removeValue();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child(getString(R.string.dbname_user_photos))
                        .child(mphoto.getUi())
                        .child(mphoto.getPi())
                        .child(getString(R.string.field_promotes))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue();

                notifyPromote=false;
                myRef.child(getString(R.string.dbname_users))
                        .child(mphoto.getUi())
                        .child(getString(R.string.field_Notifications))
                        .orderByKey()
                        .limitToLast(3)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        if (dataSnapshot.exists()
                                                && dataSnapshot.child(getString(R.string.field_notification_message)).getValue().equals(getString(R.string.promote_message))
                                                && dataSnapshot.child("sUid").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                && dataSnapshot.child("pUid").getValue().equals(mphoto.getUi())
                                                && dataSnapshot.child("pId").getValue().equals(mphoto.getPi())) {

                                            myRef.child(getString(R.string.dbname_users))
                                                    .child(mphoto.getUi())
                                                    .child(getString(R.string.field_Notifications))
                                                    .child(dataSnapshot.getKey()).removeValue()
                                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "onDataChange: Notification Deleted"))
                                                    .addOnFailureListener(e -> Log.d(TAG, "onDataChange: Notification not Deleted"));
                                            break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

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

    private void getComments(String photoId, String userId) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_user_photos))
                .child(userId)
                .child(photoId)
                .child(getString(R.string.field_comment))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        comments.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Comment comment = snapshot1.getValue(Comment.class);
                            comments.add(comment);
                            commentID.add(snapshot1.getKey());
                        }
                        adapterComment.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void numberofPromote() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUi())
                .child(mphoto.getPi())
                .child(getString(R.string.field_promotes));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String num = String.valueOf(dataSnapshot.getChildrenCount());
                promoteNum.setText(num);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void promotePost() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);

        View bottomSheetView = this.getLayoutInflater()
                .inflate(R.layout.layout_bottom_sheet_promote, bottomSheetDialog.findViewById(R.id.layout_bottom_sheet_container));
        TextView username = bottomSheetView.findViewById(R.id.usernameBs);
        TextView cancel = bottomSheetView.findViewById(R.id.cancel);
        TextView promote1 = bottomSheetView.findViewById(R.id.promote);
        ImageView post = bottomSheetView.findViewById(R.id.postBs);
        Glide.with(ViewPostActivity.this)
                .load(mphoto.getIp())
                .placeholder(R.drawable.load)
                .error(R.drawable.default_image2)
                .placeholder(R.drawable.load)
                .thumbnail(0.5f)
                .into(post);
        username.setText(currentUsername);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        promote1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                promote.setVisibility(View.GONE);
                promoted.setVisibility(View.VISIBLE);


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
                        Long timeStart = date.getTime();
                        Long timeEnd = date.getTime() + 84600000;


                        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(getString(R.string.field_photo_id), mphoto.getPi());
                        hashMap.put(getString(R.string.field_user_id), mphoto.getUi());
                        hashMap.put(getString(R.string.field_image_path), mphoto.getIp());
                        hashMap.put(getString(R.string.field_story_ID), mphoto.getPi());
                        hashMap.put(getString(R.string.field_promotion_time_end), String.valueOf(timeEnd));
                        hashMap.put(getString(R.string.field_promotion_time_start), String.valueOf(timeStart));
                        hashMap.put(getString(R.string.field_promoter_ID), FirebaseAuth.getInstance().getCurrentUser().getUid());


                        db1.child(getString(R.string.dbname_promote))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(mphoto.getPi())
                                .setValue(hashMap);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        reference.child(getString(R.string.dbname_user_photos))
                                .child(mphoto.getUi())
                                .child(mphoto.getPi())
                                .child(getString(R.string.field_promotes))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue("true");
                        notifyPromote = true;
                        addToHisNotification("" + mphoto.getUi(), mphoto.getPi(), getString(R.string.promote_message));


                        bottomSheetDialog.dismiss();

                        Log.e(SNTPClient.TAG, rawDate);

                    }

                    @Override
                    public void onError(Exception ex) {
                        Log.e(SNTPClient.TAG, ex.getMessage());
                    }
                });


            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();


    }

    private void DeletePost() {

        progress.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        final int[] x = {0};
        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference();
        reference3.child(getString(R.string.dbname_follower))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                x[0]++;
                                DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference();
                                reference3.child(getString(R.string.dbname_users))
                                        .child(snapshot1.getKey())
                                        .child(getString(R.string.post_updates))
                                        .child(mphoto.getPi())
                                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                if (x[0] == snapshot.getChildrenCount()) {
                                    deleteFurther();
                                }
                            }
                        } else {
                            deleteFurther();

                        }
                    }

                    private void deleteFurther() {

                        if (!mphoto.getTy().equals("photo")) {
                            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(mphoto.getT());
                            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                        }
                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
                        reference2.child(getString(R.string.dbname_user_photos))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(mphoto.getPi())
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(mphoto.getIp());
                                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // File deleted successfully

                                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
                                        reference2.child(getString(R.string.explore_update))
                                                .child(mphoto.getPi())
                                                .setValue(true)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
                                                            @Override
                                                            public void onTimeReceived(String currentTimeStamp) {
                                                                reference2.child(getString(R.string.explore_update)).child(getString(R.string.field_last_updated)).setValue(currentTimeStamp);
                                                            }

                                                            @Override
                                                            public void onError(Exception ex) {

                                                            }
                                                        });
                                                        String json = sp.getString("pl", null);
                                                        String json2 = sp.getString("myMedia", null);

                                                        Type type = new TypeToken<ArrayList<Photo>>() {
                                                        }.getType();
                                                        ArrayList<Photo> photoList = new ArrayList<>();
                                                        ArrayList<Photo> mymediaList = new ArrayList<>();

                                                        photoList = gson.fromJson(json, type);
                                                        mymediaList = gson.fromJson(json2, type);
                                                        ArrayList<Photo> photoList2 = new ArrayList<>();
                                                        ArrayList<Photo> mymediaList2 = new ArrayList<>();
                                                        if (photoList != null) {
                                                            photoList2 = new ArrayList<>(photoList);
                                                        }
                                                        if (mymediaList != null) {
                                                            mymediaList2 = new ArrayList<>(mymediaList);
                                                        }


                                                        if (photoList2.size() == 0) {                 //    if no arrayList is present


                                                        } else {

                                                            for (Photo a : photoList) {
                                                                if (a.getPi().equals(mphoto.getPi()))
                                                                    photoList2.remove(a);

                                                            }
                                                        }


                                                        if (mymediaList2.size() == 0) {                 //    if no arrayList is present


                                                        } else {

                                                            for (Photo a : mymediaList) {
                                                                if (a.getPi().equals(mphoto.getPi()))
                                                                    mymediaList2.remove(a);

                                                            }
                                                        }


                                                        //  delete from post list and save updated list
                                                        SharedPreferences.Editor editor = sp.edit();
                                                        json = gson.toJson(photoList2);
                                                        json2 = gson.toJson(mymediaList2);

                                                        editor.putString("pl", json);
                                                        editor.putString("myMedia", json2);

                                                        editor.apply();


                                                        progress.setVisibility(GONE);
                                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                        finish();
                                                    }
                                                });


                                    }

                                }).

                                        addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Uh-oh, an error occurred!
                                                Log.d(VolleyLog.TAG, "onFailure: did not delete file");
                                            }
                                        });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    @SuppressLint("ClickableViewAccessibility")


    private void getCurrentUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCurrentUser = dataSnapshot.getValue(users.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Query Cancelled");
            }
        });
    }

    private void NumberOfLikes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUi())
                .child(mphoto.getPi())
                .child(getString(R.string.field_likes));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numberoflike = String.valueOf(dataSnapshot.getChildrenCount());
                mLikes.setText(numberoflike);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void ifCurrentUserLiked() {
        Log.d(TAG, " checking current user liked or not");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUi())
                .child(mphoto.getPi())
                .child(getString(R.string.field_likes))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, " checking current user liked or not: Already liked");
                    mStarWhite.setVisibility(View.GONE);
                    mStarYellow.setVisibility(View.VISIBLE);
                    NumberOfLikes();
                    likeByCurrentsUser2 = true;

                } else {
                    Log.d(TAG, " checking current user liked or not: not liked");
                    mStarWhite.setVisibility(View.VISIBLE);
                    mStarYellow.setVisibility(View.GONE);
                    NumberOfLikes();
                    likeByCurrentsUser2 = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void addToHisNotification(String hisUid, String pId, String notification) {

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
                Log.d(TAG, "onCreateView: timestampyesss" + date.getTime());
                String timestamp = String.valueOf(date.getTime());
//        data to put in notification
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("pId", pId);
                hashMap.put(getString(R.string.field_timestamp), timestamp);
                hashMap.put("pUid", hisUid);
                hashMap.put(getString(R.string.field_notification_message), notification);
                hashMap.put(getString(R.string.field_if_seen), "false");
                hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users));
                ref.child(hisUid).child(getString(R.string.field_Notifications)).child(timestamp).setValue(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
                Log.e(SNTPClient.TAG, rawDate);
            }

            @Override
            public void onError(Exception ex) {
                Log.e(SNTPClient.TAG, ex.getMessage());
            }
        });
    }

    private void addlike() {
        Log.d(TAG, " like add");
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        reference1.child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUi())
                .child(mphoto.getPi())
                .child(getString(R.string.field_likes))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_user_id))
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        NumberOfLikes();
        if (!mphoto.getUi().equals(mAuth.getCurrentUser().getUid()))
            addToHisNotification(mphoto.getUi(), mphoto.getPi(), getString(R.string.liked_message));
    }

    private void removeLike() {
        Log.d(TAG, " like removed");
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        reference1.child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUi())
                .child(mphoto.getPi())
                .child(getString(R.string.field_likes))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeValue();
        NumberOfLikes();
    }

    private void ifCurrentUserPromoted() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_photos))
                .child(mphoto.getUi())
                .child(mphoto.getPi())
                .child(getString(R.string.field_promotes))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, " checking current user liked or not: Already liked");
                    promote.setVisibility(View.GONE);
                    promoted.setVisibility(View.VISIBLE);

                } else {
                    Log.d(TAG, " checking current user liked or not: not liked");
                    promote.setVisibility(View.VISIBLE);
                    promoted.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getPhototDetail() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .child(mphoto.getUi());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users user = dataSnapshot.getValue(users.class);
                mUsername.setText(user.getU());
                currentUsername = user.getU();
                Glide.with(ViewPostActivity.this)
                        .load(user.getPp())
                        .placeholder(R.drawable.load)
                        .error(R.drawable.default_image2)
                        .placeholder(R.drawable.load)
                        .into(mProfileImage);
                mcredit.setText("© " + user.getU());
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Query Cancelled");
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setupWidgets() {
        mTimestamp.setText(mphoto.getDc().substring(0, 10));

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                Intent i = new Intent(ViewPostActivity.this, CommentActivity.class);
                i.putExtra("photoId", mphoto.getPi());
                i.putExtra("userId", mphoto.getUi());
                startActivity(i);

            }
        });
        mCommentnumber.setText(String.valueOf(comments.size()));
        mCaption.setText(mphoto.getCap());
        mLikes.setText(mLikesString);


    }


    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started");
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(mContext);
        mAuthListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) Log.d(TAG, "onAuthStateChanged: signed_in:" + firebaseAuth.getCurrentUser().getUid());
            else {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                Log.d(TAG, "onAuthStateChanged: navigating to login");
                SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                new AlertDialog.Builder(mContext)
                        .setTitle("No user logon found")
                        .setMessage("We will be logging you out. \n Please try to log in again")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            Intent intent = new Intent(mContext, login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            settings.edit().clear().apply();
                            startActivity(intent);
                        })
                        .show();
            }
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
        if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
        if (simpleExoPlayer != null) simpleExoPlayer.release();
        if(notifyLike || notifyPromote){
            final DatabaseReference data = myRef.child(getString(R.string.dbname_users)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    users user = dataSnapshot.getValue(users.class);
                    Log.d(TAG, "onDataChange: user.getU()"+user.getU());
                    if(notifyLike) firebaseMethods.sendNotification(mphoto.getUi(), user.getU(), getString(R.string.liked_message), getString(R.string.like_string));
                    if(notifyPromote) firebaseMethods.sendNotification(mphoto.getUi(), user.getU(), getString(R.string.promote_message), getString(R.string.promote_string));

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (simpleExoPlayer != null) simpleExoPlayer.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null) simpleExoPlayer.release();
    }
}