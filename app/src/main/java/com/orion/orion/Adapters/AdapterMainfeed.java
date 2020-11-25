package com.orion.orion.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

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
import com.orion.orion.CommentActivity;
import com.orion.orion.R;
import com.orion.orion.ViewPostActivity;
import com.orion.orion.home.Chat_Activity;
import com.orion.orion.home.Homefragment;
import com.orion.orion.models.Comment;
import com.orion.orion.models.Photo;
import com.orion.orion.models.users;
import com.orion.orion.profile.ProfileActivity;
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
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static com.orion.orion.util.MyApplication.getProxy;

public class AdapterMainfeed extends RecyclerView.Adapter<AdapterMainfeed.ViewHolder> {

    RecyclerView recyclerView;
    static SimpleExoPlayer simpleExoPlayer;


    public interface ReleasePlayer {
        default void releasePlayer() {
            if (simpleExoPlayer != null) {
                simpleExoPlayer.release();
            }
        }


    }


    private static final String TAG = "AdapterMainfeed";

    private Context mContext;
    private String currentUsername = "";
    private String numberoflike = "0";
    //    SP
    Gson gson;
    SharedPreferences sp;
    Homefragment homefragment;

    private FirebaseMethods mFirebaseMethods;
    private boolean notify = false;

    private List<Photo> photos;

    public AdapterMainfeed(Context mContext, List<Photo> photos, RecyclerView recyclerView, Homefragment homefragment) {
        this.mContext = mContext;
        this.photos = photos;
        this.recyclerView = recyclerView;
        this.homefragment = homefragment;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_mainfeed_listitem, parent, false);
        return new AdapterMainfeed.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        mFirebaseMethods = new FirebaseMethods(mContext);
        Photo photo = photos.get(i);
        getCurrentUserDomain(holder.domain, photo);
        ifCurrentUserLiked(holder, photo);
        ifCurrentUserPromoted(holder, photo);
        holder.duration.setVisibility(View.GONE);
        homefragment.bottomProgress.setVisibility(GONE);

//          Initialize SharedPreference variables
        sp = mContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        gson = new Gson();


        holder.eclipse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.eclipse);
                if (photo.getUi().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    popupMenu.getMenuInflater().inflate(R.menu.post_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Delete");
                            builder.setMessage(mContext.getString(R.string.delete_post_prompt));

//                set buttons
                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DeletePost(photo, i);

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
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Report");
                            builder.setMessage(mContext.getString(R.string.report_post_prompt));

//                set buttons
                            builder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ReportPost(photo);

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


        numberofPromote(holder.promoteNum, photo.getPi(), photo.getUi(), holder);
//            set the comment
        List<Comment> comments = photo.getComments();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


        Query query = reference
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(photo.getUi())
                .child(photo.getPi())
                .child(mContext.getString(R.string.field_comment));
        query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            holder.commentnumber.setText(String.valueOf(snapshot.getChildrenCount()));

                        }else{
                            holder.commentnumber.setText("0");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


//        get time
        holder.timeDate.setText(photo.getDc().substring(0, 10));
        holder.caption.setText(photo.getCap());


//        get post


//       ******************* get Image***************


        holder.type = photo.getTy();
        if (holder.type!= null &&  holder.type.equals("photo")) {
            holder.image.setVisibility(View.VISIBLE);
            holder.play2.setVisibility(View.GONE);
            holder.playerView.setVisibility(GONE);
            Glide.with(holder.itemView.getContext().getApplicationContext())
                    .load(photo.getIp())
                    .placeholder(R.drawable.load)
                    .error(R.drawable.default_image2)
                    .placeholder(R.drawable.load)
                    .thumbnail(0.2f)
                    .into(holder.image);
        } else {
            holder.thumbnail.setVisibility(View.VISIBLE);
            holder.unmute.setVisibility(View.VISIBLE);
            holder.play2.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            holder.playerView.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext().getApplicationContext())
                    .load(photo.getT())
                    .placeholder(R.drawable.load)
                    .error(R.drawable.default_image2)
                    .placeholder(R.drawable.load)
                    .thumbnail(0.25f)
                    .into(holder.thumbnail);
        }


//                   ***********get Video***********


//        check if playerView if visible on scrolling

        final Rect scrollBounds = new Rect();
        recyclerView.getHitRect(scrollBounds);
        recyclerView.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        Rect mReact = new Rect();
                        recyclerView.getHitRect(mReact);
                        if (holder.playerView.getLocalVisibleRect(scrollBounds)) {
                            if (!holder.playerView.getLocalVisibleRect(scrollBounds)
                                    || scrollBounds.height() < holder.playerView.getHeight()) {
//                                partially visible

                                if (holder.playerView.getVisibility() == GONE) {
                                    releasePlayer();
                                    holder.playerView.setVisibility(View.VISIBLE);

                                }


                            } else {
//                                fully visible

                            }
                        } else {
//                            invisible
                            holder.playerView.setVisibility(GONE);

                        }

                    }

                    private void releasePlayer() {
                        if (simpleExoPlayer != null) {
                            holder.play2.setVisibility(View.VISIBLE);
                            holder.play = true;
                            holder.thumbnail.setVisibility(View.VISIBLE);
                            simpleExoPlayer.seekTo(0);
                            simpleExoPlayer.setPlayWhenReady(false);
                            simpleExoPlayer.release();
                            holder.currentPosition = 0;
                        }

                    }
                });


//                     play/pause video

        final Handler[] mHandler = new Handler[1];
        final Runnable[] updateProgressAction = new Runnable[1];

        holder.playerView.getVideoSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.progressBar.setVisibility(View.VISIBLE);
//                if paused
                if (holder.play) {
                    holder.play = false;
                    holder.play2.setVisibility(View.INVISIBLE);

                    LoadControl loadControl = new DefaultLoadControl();


                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                    if (simpleExoPlayer != null) {
                        simpleExoPlayer.release();
                    }

                    simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
                    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                            mContext, Util.getUserAgent(mContext, "RecyclerView VideoPlayer"));
                    String mediaUrl = photo.getIp();
                    HttpProxyCacheServer proxy = getProxy(mContext);
                    String proxyUrl = proxy.getProxyUrl(mediaUrl);

                    holder.playerView.setPlayer(simpleExoPlayer);


                    holder.playerView.setKeepScreenOn(true);
                    holder.playerView.setKeepScreenOn(true);


                    MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(Uri.parse(proxyUrl));
//                    set Volume
                    if (holder.mute.getVisibility() == View.VISIBLE) {
                        simpleExoPlayer.setVolume(0f);
                    } else if (holder.unmute.getVisibility() == View.VISIBLE) {
                        simpleExoPlayer.setVolume(AudioManager.STREAM_MUSIC);
                    }

                    simpleExoPlayer.prepare(videoSource);
                    simpleExoPlayer.seekTo(holder.currentPosition);
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
                                holder.progressBar.setVisibility(View.VISIBLE);


                            } else if (playbackState == Player.STATE_READY) {
                                holder.thumbnail.setVisibility(GONE);
                                holder.duration.setVisibility(View.VISIBLE);
                                holder.progressBar.setVisibility(View.GONE);

//                                display duration
                                updateProgressAction[0] = new Runnable() {
                                    @Override
                                    public void run() {
                                        updateProgress();
                                    }

                                    private void updateProgress() {

                                        long delayMs = TimeUnit.SECONDS.toMillis(1);
                                        mHandler[0].postDelayed(updateProgressAction[0], delayMs);
                                        int sec = (int) (simpleExoPlayer.getDuration() - simpleExoPlayer.getCurrentPosition()) / 1000;
                                        if (sec >= 0) {
                                            String dur = String.valueOf(sec);
                                            holder.duration.setText(dur);

                                        }

                                    }

                                };
                                mHandler[0] = new Handler();
                                mHandler[0].post(updateProgressAction[0]);


                            } else if (playbackState == Player.STATE_ENDED) {
                                holder.play2.setVisibility(View.VISIBLE);
                                holder.play = true;
                                holder.thumbnail.setVisibility(View.VISIBLE);
                                simpleExoPlayer.seekTo(0);
                                simpleExoPlayer.setPlayWhenReady(false);
                                simpleExoPlayer.release();

                            } else if (playbackState == Player.STATE_IDLE) {
                                holder.play2.setVisibility(View.VISIBLE);

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
                    holder.play = true;
                    holder.play2.setVisibility(View.VISIBLE);
                    holder.currentPosition = simpleExoPlayer.getCurrentPosition();
                    simpleExoPlayer.setPlayWhenReady(false);
                    simpleExoPlayer.getPlaybackState();
                    simpleExoPlayer.release();


                }
            }
        });

//        toggle volume
        holder.mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.mute.setVisibility(View.GONE);
                holder.unmute.setVisibility(View.VISIBLE);
                if (simpleExoPlayer != null) {
                    simpleExoPlayer.setVolume(AudioManager.STREAM_MUSIC);
                }
            }
        });
        holder.unmute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.mute.setVisibility(View.VISIBLE);
                holder.unmute.setVisibility(View.GONE);
                if (simpleExoPlayer != null) {
                    simpleExoPlayer.setVolume(0f);

                }
            }
        });


//        get username
        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference();
        Query query3 = reference3
                .child(mContext.getString(R.string.dbname_users))
                .child(photo.getUi());
        query3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot singleSnapshot) {
                currentUsername = singleSnapshot.getValue(users.class).getU();

                holder.username.setText(currentUsername);
                holder.credit.setText("© " + currentUsername);


                holder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, profile.class);
                        i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                        i.putExtra(mContext.getString(R.string.intent_user), photo.getUi());
                        mContext.startActivity(i);
                    }
                });
                Glide.with(mContext.getApplicationContext())
                        .load(singleSnapshot.getValue(users.class).getPp())
                        .placeholder(R.drawable.load)
                        .error(R.drawable.default_image2)
                        .placeholder(R.drawable.load)
                        .thumbnail(0.2f)
                        .into(holder.mProfileImage);
                holder.mProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, profile.class);
                        i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                        i.putExtra(mContext.getString(R.string.intent_user), photo.getUi());
                        mContext.startActivity(i);
                    }
                });


                holder.setting = singleSnapshot.getValue(users.class);
                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, CommentActivity.class);
                        i.putExtra("photoId", photo.getPi());
                        i.putExtra("userId", photo.getUi());
                        mContext.startActivity(i);
                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();
                db1.child(mContext.getString(R.string.dbname_user_photos))
                        .child(photo.getUi())
                        .child(photo.getPi())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.photo = snapshot.getValue(Photo.class);
                                ArrayList<Comment> comments = new ArrayList<>();

                                for (DataSnapshot dSnapshot : snapshot.child(mContext.getString(R.string.field_comment)).getChildren()) {
                                    Comment comment = new Comment();
                                    comment.setUi(dSnapshot.getValue(Comment.class).getUi());
                                    comment.setC(dSnapshot.getValue(Comment.class).getC());
                                    comment.setDc(dSnapshot.getValue(Comment.class).getDc());
                                    comments.add(comment);


                                }


                                Intent i = new Intent(mContext, ViewPostActivity.class);
                                i.putExtra("photo", holder.photo);
                                i.putParcelableArrayListExtra("comments", comments);

                                mContext.startActivity(i);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

        holder.headerLatout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();
                db1.child(mContext.getString(R.string.dbname_user_photos))
                        .child(photo.getUi())
                        .child(photo.getPi())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.photo = snapshot.getValue(Photo.class);
                                ArrayList<Comment> comments = new ArrayList<>();

                                for (DataSnapshot dSnapshot : snapshot.child(mContext.getString(R.string.field_comment)).getChildren()) {
                                    Comment comment = new Comment();
                                    comment.setUi(dSnapshot.getValue(Comment.class).getUi());
                                    comment.setC(dSnapshot.getValue(Comment.class).getC());
                                    comment.setDc(dSnapshot.getValue(Comment.class).getDc());
                                    comments.add(comment);


                                }


                                Intent i = new Intent(mContext, ViewPostActivity.class);
                                i.putExtra("photo", holder.photo);
                                i.putParcelableArrayListExtra("comments", comments);

                                mContext.startActivity(i);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

//        get the object


//        toggle star
        holder.whitestar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                holder.whitestar.setVisibility(View.GONE);
                holder.yellowstar.setVisibility(View.VISIBLE);
                addlike(holder, photo);
                NumberOfLikes(holder, photo);
            }
        });
        holder.yellowstar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.whitestar.setVisibility(View.VISIBLE);
                holder.yellowstar.setVisibility(View.GONE);
                removeLike(holder, photo);
                NumberOfLikes(holder, photo);
                reference.child(mContext.getString(R.string.dbname_users))
                        .child(photo.getUi())
                        .child(mContext.getString(R.string.field_Notifications))
                        .orderByKey()
                        .limitToLast(3)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        if (dataSnapshot.exists()
                                                && dataSnapshot.child(mContext.getString(R.string.field_notification_message)).getValue().equals("Liked your post")
                                                && dataSnapshot.child("sUid").getValue().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                && dataSnapshot.child("pUid").getValue().equals(photo.getUi())
                                                && dataSnapshot.child("pId").getValue().equals(photo.getPi())) {
                                            reference.child(mContext.getString(R.string.dbname_users))
                                                    .child(photo.getUi())
                                                    .child(mContext.getString(R.string.field_Notifications))
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

//        toggle promote
        holder.promote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                promotePost(photo, holder);
            }
        });


        holder.promoted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unPromotePost(photo, holder);
            }
        });

    }


    private void unPromotePost(Photo photo, ViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Remove Promotion");
        builder.setMessage(mContext.getString(R.string.remove_promotion_prompt));

//                set buttons
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                togglePromoteBtn(holder);

                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                db.child(mContext.getString(R.string.dbname_promote))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(photo.getPi())
                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        reference.child(mContext.getString(R.string.dbname_user_photos))
                                .child(photo.getUi())
                                .child(photo.getPi())
                                .child(mContext.getString(R.string.field_promotes))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                Toast.makeText(mContext, "Post promotion removed!", Toast.LENGTH_SHORT).show();
                            }
                        });
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

    private void promotePost(Photo photo, ViewHolder holder) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext, R.style.BottomSheetDialogTheme);

        View bottomSheetView = ((FragmentActivity) mContext).getLayoutInflater()
                .inflate(R.layout.layout_bottom_sheet_promote, bottomSheetDialog.findViewById(R.id.layout_bottom_sheet_container));
        TextView username = bottomSheetView.findViewById(R.id.usernameBs);
        TextView cancel = bottomSheetView.findViewById(R.id.cancel);
        TextView promote = bottomSheetView.findViewById(R.id.promote);
        ImageView post = bottomSheetView.findViewById(R.id.postBs);
        ImageView progress = bottomSheetView.findViewById(R.id.progress);

        if (photo.getTy().equals("photo")) {
            Glide.with(holder.itemView.getContext().getApplicationContext())
                    .load(photo.getIp())
                    .placeholder(R.drawable.load)
                    .error(R.drawable.default_image2)
                    .placeholder(R.drawable.load)
                    .thumbnail(0.2f)
                    .into(post);
        } else {
            Glide.with(holder.itemView.getContext().getApplicationContext())
                    .load(photo.getT())
                    .placeholder(R.drawable.load)
                    .error(R.drawable.default_image2)
                    .placeholder(R.drawable.load)
                    .thumbnail(0.2f)
                    .into(post);
        }

        username.setText(currentUsername);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        promote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                togglePromoteBtn(holder);

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
                        hashMap.put(mContext.getString(R.string.field_photo_id), photo.getPi());
                        hashMap.put(mContext.getString(R.string.field_user_id), photo.getUi());
                        hashMap.put(mContext.getString(R.string.field_image_path), photo.getIp());
                        hashMap.put(mContext.getString(R.string.field_story_ID), photo.getPi());
                        hashMap.put(mContext.getString(R.string.field_promotion_time_end), String.valueOf(timeEnd));
                        hashMap.put(mContext.getString(R.string.field_promotion_time_start), String.valueOf(timeStart));
                        hashMap.put(mContext.getString(R.string.field_promoter_ID), FirebaseAuth.getInstance().getCurrentUser().getUid());


                        db1.child(mContext.getString(R.string.dbname_promote))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(photo.getPi())
                                .setValue(hashMap);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        reference.child(mContext.getString(R.string.dbname_user_photos))
                                .child(photo.getUi())
                                .child(photo.getPi())
                                .child(mContext.getString(R.string.field_promotes))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue("true");

                        addToHisNotification("" + photo.getUi(), photo.getPi(), "promoted your post.");
//                        final DatabaseReference data = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_users)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(mContext.getString(R.string.field_username));
//                        data.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                String user = dataSnapshot.getValue().toString();
//
//                                if (notify) {
//                                    mFirebaseMethods.sendNotification(photo.getUi(), user, "promoted your post.", "Promote");
//                                }
//                                notify = false;
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });



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


    private void ifCurrentUserPromoted(ViewHolder holder, Photo photo) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.dbname_user_photos))
                .child(photo.getUi())
                .child(photo.getPi())
                .child(mContext.getString(R.string.field_promotes))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.promote.setVisibility(View.GONE);
                    holder.promoted.setVisibility(View.VISIBLE);

                } else {
                    holder.promote.setVisibility(View.VISIBLE);
                    holder.promoted.setVisibility(View.GONE);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void numberofPromote(TextView promoteNum, String photo_id, String user_id, ViewHolder holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.dbname_user_photos))
                .child(user_id)
                .child(photo_id)
                .child(mContext.getString(R.string.field_promotes));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String num = String.valueOf(dataSnapshot.getChildrenCount());
                    promoteNum.setText(num);
                }else{
                    promoteNum.setText("0");

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void togglePromoteBtn(ViewHolder holder) {
        if (holder.promote.getVisibility() == View.VISIBLE) {
            holder.promote.setVisibility(View.GONE);
            holder.promoted.setVisibility(View.VISIBLE);
        } else {
            holder.promote.setVisibility(View.VISIBLE);
            holder.promoted.setVisibility(GONE);
        }
    }

    @Override
    public long getItemId(int position) {
        Photo photo = photos.get(position);
        if (photo!=null&&photos.size()!=0) {
            return photo.getPi().hashCode();
        }else{
            return position;
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView mProfileImage;
        TextView username, timeDate, domain, caption, commentnumber, likenumber, credit, promoteNum, duration;
        ImageView yellowstar, whitestar, comment, promote, promoted, eclipse, play2, mute, unmute;
        SquareImageView image, thumbnail,progress;
        PlayerView playerView;
        ProgressBar progressBar;


        users setting = new users();
        StringBuilder users;
        String type = "";
        Photo photo;
        boolean likeByCurrentsUser2;
        boolean play = true;
        long currentPosition = 0;
        RelativeLayout postRelLayout, headerLatout, footerLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            username = itemView.findViewById(R.id.username);
            image = itemView.findViewById(R.id.post_image);
            yellowstar = itemView.findViewById(R.id.image_star_yellow);
            whitestar = itemView.findViewById(R.id.image_star);
            comment = itemView.findViewById(R.id.image_shoutout);
            caption = itemView.findViewById(R.id.image_caption);
            timeDate = itemView.findViewById(R.id.images_time);
            mProfileImage = itemView.findViewById(R.id.profile_photo);
            promote = itemView.findViewById(R.id.promote);
            promoted = itemView.findViewById(R.id.promoted);
            domain = itemView.findViewById(R.id.domain12);
            promoteNum = itemView.findViewById(R.id.promote_number);
            users = new StringBuilder();
            commentnumber = itemView.findViewById(R.id.comments_number);
            likenumber = itemView.findViewById(R.id.likes_number);
            eclipse = itemView.findViewById(R.id.ivEllipses);
//         exoplayer
            play2 = itemView.findViewById(R.id.play);
            mute = itemView.findViewById(R.id.mute);
            unmute = itemView.findViewById(R.id.unmute);
            playerView = itemView.findViewById(R.id.player_view);
            progressBar = itemView.findViewById(R.id.progress_bar);
            duration = itemView.findViewById(R.id.duration);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            postRelLayout = itemView.findViewById(R.id.post_imagelayout);
            footerLayout = itemView.findViewById(R.id.promotion);
            headerLatout = itemView.findViewById(R.id.header);
            progress = itemView.findViewById(R.id.progress);



            credit = itemView.findViewById(R.id.credit);


        }

    }

    private void addToHisNotification(String hisUid, String pId, String notification) {

        String timestamp = "" + System.currentTimeMillis();

//        data to put in notification
        HashMap<Object,String> hashMap = new HashMap<>();
        hashMap.put("pId",pId);

        hashMap.put(mContext.getString(R.string.field_timestamp),timestamp);

        hashMap.put("pUid",hisUid);

        hashMap.put(mContext.getString(R.string.field_notification_message),notification);
        hashMap.put(mContext.getString(R.string.field_if_seen),"false");

        hashMap.put("sUid",FirebaseAuth.getInstance().getCurrentUser().getUid());


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_users));
        ref.child(hisUid).child(mContext.getString(R.string.field_Notifications)).child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void getCurrentUserDomain(TextView domain, Photo photo) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_users))
                .child(photo.getUi())
                .child(mContext.getString(R.string.field_domain));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                domain.setText(dataSnapshot.getValue().toString());


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void DeletePost(Photo photo, int i) {

        final int[] x = {0};
        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference();
        reference3.child(mContext.getString(R.string.dbname_follower))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                                x[0]++;
                                DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference();
                                reference3.child(mContext.getString(R.string.dbname_users))
                                        .child(snapshot1.getKey())
                                        .child(mContext.getString(R.string.post_updates))
                                        .child(photo.getPi())
                                        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                if (x[0] == snapshot.getChildrenCount()) {
                                    DeleteFurther();
                                }
                            }
                        } else {
                            DeleteFurther();
                        }
                    }

                    private void DeleteFurther() {

                       if( !photo.getTy().equals("photo")){
                           StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(photo.getT());
                           photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {

                               }
                           });
                       }

                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
                        reference2.child(mContext.getString(R.string.dbname_user_photos))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(photo.getPi())
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(photo.getIp());
                                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // File deleted successfully

                                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
                                        reference2.child(mContext.getString(R.string.explore_update))
                                                .child(photo.getPi())
                                                .setValue(true)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        String json = sp.getString("pl", null);
                                                        String json2 = sp.getString("myMedia", null);

                                                        Type type = new TypeToken<ArrayList<Photo>>() {
                                                        }.getType();
                                                        ArrayList<Photo> photoList = new ArrayList<>();
                                                        ArrayList<Photo> mymediaList = new ArrayList<>();

                                                        photoList = gson.fromJson(json, type);
                                                        mymediaList = gson.fromJson(json2, type);
                                                        ArrayList<Photo> photoList2 =new ArrayList<>();
                                                        ArrayList<Photo> mymediaList2= new ArrayList<>();
                                                        if( photoList != null) {
                                                            photoList2 = new ArrayList<>(photoList);
                                                        }
                                                        if (mymediaList != null){
                                                            mymediaList2 = new ArrayList<>(mymediaList);
                                                        }


                                                        if (photoList2.size() == 0) {                 //    if no arrayList is present


                                                        } else {

                                                            for (Photo a : photoList) {
                                                                if (a.getPi().equals(photo.getPi()))
                                                                    photoList2.remove(a);

                                                            }
                                                        }


                                                        if (mymediaList2.size() == 0) {

                                                        } else {

                                                            for (Photo a : mymediaList) {
                                                                if (a.getPi().equals(photo.getPi()))
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


                                                        photos.remove(photo);
                                                        AdapterMainfeed.this.notifyItemRemoved(i);
                                                    }
                                                });





                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
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

    private void ReportPost(Photo photo) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(mContext.getString(R.string.dbname_reports))
                .child(mContext.getString(R.string.field_post))
                .child(photo.getPi())
                .child(mContext.getString(R.string.field_user_id))
                .setValue(photo.getUi())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "Post Reported!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void NumberOfLikes(final ViewHolder holder, Photo photo) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.dbname_user_photos))
                .child(photo.getUi())
                .child(photo.getPi())
                .child(mContext.getString(R.string.field_likes));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    numberoflike = String.valueOf(dataSnapshot.getChildrenCount());
                    holder.likenumber.setText(numberoflike);

                }else{
                    holder.likenumber.setText("0");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void ifCurrentUserLiked(final ViewHolder holder, Photo photo) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.dbname_user_photos))
                .child(photo.getUi())
                .child(photo.getPi())
                .child(mContext.getString(R.string.field_likes))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.whitestar.setVisibility(View.GONE);
                    holder.yellowstar.setVisibility(View.VISIBLE);
                    NumberOfLikes(holder, photo);
                    holder.likeByCurrentsUser2 = true;

                } else {
                    holder.whitestar.setVisibility(View.VISIBLE);
                    holder.yellowstar.setVisibility(View.GONE);
                    NumberOfLikes(holder, photo);
                    holder.likeByCurrentsUser2 = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void addlike(final ViewHolder holder, Photo photo) {

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        reference1.child(mContext.getString(R.string.dbname_user_photos))
                .child(photo.getUi())
                .child(photo.getPi())
                .child(mContext.getString(R.string.field_likes))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.field_user_id))
                .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
        NumberOfLikes(holder, photo);

//        final DatabaseReference data = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_users))
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        data.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                users user = dataSnapshot.getValue(users.class);
//
//                if (notify) {
//                    mFirebaseMethods.sendNotification(photo.getUi(), user.getU(), "liked your post", "Like");
//                }
//                notify = false;
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


        addToHisNotification("" + photo.getUi(), photo.getPi(), "Liked your post");


    }


    private void removeLike(final ViewHolder holder, Photo photo) {

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();
        reference1.child(mContext.getString(R.string.dbname_user_photos))
                .child(photo.getUi())
                .child(photo.getPi())
                .child(mContext.getString(R.string.field_likes))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                NumberOfLikes(holder, photo);

            }
        });


    }


}