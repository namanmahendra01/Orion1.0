package com.orion.orion.contest.Contest_Evaluation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.util.UniversalImageLoader;

public class activity_view_media extends AppCompatActivity {
    private static final String TAG = "activity_view_media";

    private ImageView mediaIv, voteNo, voteYes,progress;
    private TextView votingNumber;
    private String mVotingnumber = "";
    private RelativeLayout relativeLayout;
    boolean voted = false;
    String joiningKey = "", contestKey = "", view = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_media);


        mediaIv = findViewById(R.id.mediaIv);
        voteNo = findViewById(R.id.noVote);
        voteYes = findViewById(R.id.yesVote);
        relativeLayout = findViewById(R.id.relLayout2);
        votingNumber = findViewById(R.id.votingNumber);
        progress = findViewById(R.id.progress);


        Intent i = getIntent();
        String imagelink = i.getStringExtra("imageLink");
        contestKey = i.getStringExtra("contestkey");
        joiningKey = i.getStringExtra("joiningkey");
        view = i.getStringExtra("view");
        UniversalImageLoader.setImage(imagelink, mediaIv, progress, "");


        if (view.equals("No")) {
            relativeLayout.setVisibility(View.GONE);
            votingNumber.setVisibility(View.GONE);

        } else {
            ifCurrentUserVote();

        }


        voteNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "whitestar clicked");
                if (voted) {
                    Toast.makeText(activity_view_media.this, "You have already voted for this contest.", Toast.LENGTH_SHORT).show();
                } else {
                    voteNo.setVisibility(View.GONE);
                    voteYes.setVisibility(View.VISIBLE);
                    addVote();
                    NumberofVotes();
                }


            }
        });
        voteYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "yellowstar clicked");

                voteNo.setVisibility(View.VISIBLE);
                voteYes.setVisibility(View.GONE);
                removeVote();
                NumberofVotes();


            }
        });


    }

    private void removeVote() {
        Log.d(TAG, " like removed");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_participantList))
                .child(contestKey)
                .child(joiningKey)
                .child(getString(R.string.voting_list))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeValue();

        reference.child(getString(R.string.dbname_contestlist))
                .child(contestKey)
                .child("voterlist")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeValue();
        NumberofVotes();
        voted=false;


    }

    private void addVote() {
        Log.d(TAG, " like add");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_participantList))
                .child(contestKey)
                .child(joiningKey)
                .child(getString(R.string.voting_list))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(true);

        reference.child(getString(R.string.dbname_contestlist))
                .child(contestKey)
                .child("voterlist")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(true);
        NumberofVotes();
    }

    private void ifCurrentUserVote() {
        Log.d(TAG, " checking current user liked or not");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.dbname_participantList))
                .child(contestKey)
                .child(joiningKey)
                .child(getString(R.string.voting_list))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                        if (dataSnapshot2.exists()) {
                            Log.d(TAG, " checking current user liked or not: Already liked");
                            voteNo.setVisibility(View.GONE);
                            voteYes.setVisibility(View.VISIBLE);
                            NumberofVotes();

                        } else {
                            Log.d(TAG, " checking current user liked or not: not liked");
                            voteNo.setVisibility(View.VISIBLE);
                            voteYes.setVisibility(View.GONE);
                            NumberofVotes();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        reference.child(getString(R.string.dbname_contestlist))
                .child(contestKey)
                .child("voterlist")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            voted = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void NumberofVotes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_participantList))
                .child(contestKey)
                .child(joiningKey)
                .child(getString(R.string.voting_list));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mVotingnumber = String.valueOf(dataSnapshot.getChildrenCount());
                votingNumber.setText(mVotingnumber);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
