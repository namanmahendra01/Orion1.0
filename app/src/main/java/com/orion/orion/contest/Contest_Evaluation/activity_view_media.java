package com.orion.orion.contest.Contest_Evaluation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.login.login;

public class activity_view_media extends AppCompatActivity {
    private static final String TAG = "activity_view_media";
    private final Context mContext = this;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private ImageView voteNo;
    private ImageView voteYes;
    private TextView votingNumber;
    private String mVotingnumber = "";
    private boolean voted = false;
    private String joiningKey = "";
    private String contestKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_media);

        ImageView mediaIv = findViewById(R.id.mediaIv);
        voteNo = findViewById(R.id.noVote);
        voteYes = findViewById(R.id.yesVote);
        RelativeLayout relativeLayout = findViewById(R.id.relLayout2);
        votingNumber = findViewById(R.id.votingNumber);
        ImageView progress = findViewById(R.id.progress);

        Intent i = getIntent();
        String imagelink = i.getStringExtra("imageLink");
        contestKey = i.getStringExtra("contestkey");
        joiningKey = i.getStringExtra("joiningkey");
        String view = i.getStringExtra("view");

        Glide.with(mContext)
                .load(imagelink)
                .placeholder(R.drawable.load)
                .error(R.drawable.default_image2)
                .placeholder(R.drawable.load)
                .into(mediaIv);

        setupFirebaseAuth();

        if (view!=null && view.equals("No")) {
            relativeLayout.setVisibility(View.GONE);
            votingNumber.setVisibility(View.GONE);
        } else ifCurrentUserVote();

        voteNo.setOnClickListener(v -> {
            Log.d(TAG, "Vote no clicked");
            if (voted) Toast.makeText(activity_view_media.this, "You have already voted for this contest.", Toast.LENGTH_SHORT).show();
            else {
                voteNo.setVisibility(View.GONE);
                voteYes.setVisibility(View.VISIBLE);
                addVote();
                NumberofVotes();
            }
        });
        voteYes.setOnClickListener(v -> {
            Log.d(TAG, "Vote yes clicked");
            voteNo.setVisibility(View.VISIBLE);
            voteYes.setVisibility(View.GONE);
            removeVote();
            NumberofVotes();
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
                        } else {
                            Log.d(TAG, " checking current user liked or not: Not liked");
                            voteNo.setVisibility(View.VISIBLE);
                            voteYes.setVisibility(View.GONE);
                        }
                        NumberofVotes();
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
                        if (snapshot.exists()) voted = true;
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
    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
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
        if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
    }
}
