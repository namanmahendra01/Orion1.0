package com.orion.orion;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.util.UniversalImageLoader;

public class activity_view_video extends AppCompatActivity {
    private static final String TAG = "activity_view_media";

    private ImageView mediaIv, voteNo, voteYes;
    private TextView votingNumber;
    private String mVotingnumber = "";
    private RelativeLayout relativeLayout;
    VideoView videoViewLandscape;
    String joiningKey = "", contestKey = "", view = "";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);


        voteNo = findViewById(R.id.noVote);
        voteYes = findViewById(R.id.yesVote);
        relativeLayout = findViewById(R.id.relLayout2);
        votingNumber = findViewById(R.id.votingNumber);
        videoViewLandscape =(VideoView) findViewById(R.id.mediaIvj);
        String str = "https://firebasestorage.googleapis.com/v0/b/orion-6f75c.appspot.com/o/VID_20190608_193508.mp4?alt=media&token=95cd72af-a7d6-4d55-96d6-119b11c16217";
        Uri uri = Uri.parse(str);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoViewLandscape);
        videoViewLandscape.setMediaController(mediaController);
        videoViewLandscape.setVideoPath("https://firebasestorage.googleapis.com/v0/b/orion-6f75c.appspot.com/o/VID_20190608_193508.mp4?alt=media&token=95cd72af-a7d6-4d55-96d6-119b11c16217");
        videoViewLandscape.start();

        videoViewLandscape.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        videoViewLandscape.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    return true;
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    return true;
                }
                return false;
            }
        });

    }


}
