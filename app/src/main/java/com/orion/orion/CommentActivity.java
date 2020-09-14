package com.orion.orion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.Adapters.AdapterComment;
import com.orion.orion.Adapters.AdapterContestJoined;
import com.orion.orion.home.MainActivity;
import com.orion.orion.models.Comment;
import com.orion.orion.models.JoinForm;
import com.orion.orion.models.users;
import com.orion.orion.util.FirebaseMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class CommentActivity extends AppCompatActivity {
    RecyclerView commentRv;
    private ArrayList<Comment> comments;
    private static final String TAG = "CommentActivity";

    private AdapterComment adapterComment;


    private ImageView mBackArrow,mCheckMark;
    private EditText mComment;
    private boolean notify = false;
    private  FirebaseMethods mFirebaseMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent i = getIntent();
        String phhotoId=i.getStringExtra("photoId");
        String userId=i.getStringExtra("userId");
        Log.d(TAG, "onCreate: kol"+phhotoId+userId);

        mBackArrow=(ImageView)findViewById(R.id.backarrow);
        mCheckMark=(ImageView)findViewById(R.id.checkMark);
        mComment=(EditText)findViewById(R.id.comment);
        mFirebaseMethods = new FirebaseMethods(CommentActivity.this);



        commentRv=findViewById(R.id.recyclerComment);
        commentRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        commentRv.setLayoutManager(linearLayoutManager);

        comments=new ArrayList<>();
        adapterComment = new AdapterComment(this,comments);
        commentRv.setAdapter(adapterComment);

        getComments(phhotoId,userId);


        mCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mComment.getText().toString().equals("")){
                    Log.d(TAG,"attempting to submit new comment");
                    notify = true;

                    addNewComment(mComment.getText().toString(),userId,phhotoId);

                    mComment.setText("");

                    closeKeyboard();
                }else{
                    Toast.makeText(CommentActivity.this, "C'mon..Give a Shoutout", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: dfg");
                finish();
            }
        });
    }
    private String getTimeStamp(){
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return  sdf.format(new Date());
    }
    private void closeKeyboard(){
        View view=this.getCurrentFocus();
        if (view!=null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }

    }

    private void addNewComment(String newComment, String userId, String phhotoId){
        Log.d(TAG,"adding new comment");

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();


        String commentID = myRef.push().getKey();

        Comment comment = new Comment();
        comment.setComment(newComment);
        comment.setDate_created(getTimeStamp());
        comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());


        myRef.child(getString(R.string.dbname_user_photos))
                .child(userId)
                .child(phhotoId)
                .child(getString(R.string.field_comment))
                .child(commentID)
                .setValue(comment);

        final DatabaseReference data = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users user = dataSnapshot.getValue(users.class);

                if (notify) {
                    mFirebaseMethods.sendNotification(userId, user.getUsername(), "commented on your post","Comment");
                }
                notify = false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addToHisNotification(""+userId,phhotoId,"commented on your post");





    }
    private void addToHisNotification(String hisUid,String pId,String notification){

        String timestamp=""+System.currentTimeMillis();


//        data to put in notification
        HashMap<Object,String> hashMap = new HashMap<>();
        hashMap.put("pId",pId);

        hashMap.put("timeStamp",timestamp);

        hashMap.put("pUid",hisUid);

        hashMap.put("notificaton",notification);
        hashMap.put("seen","false");

        hashMap.put("sUid",FirebaseAuth.getInstance().getCurrentUser().getUid());



        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
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
    private void getComments(String phhotoId, String userId) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_user_photos))
                .child(userId)
                .child(phhotoId)
                .child(getString(R.string.field_comment))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        comments.clear();
                        for (DataSnapshot snapshot1:snapshot.getChildren()){

                            Comment comment=snapshot1.getValue(Comment.class);
                            Log.d(TAG, "onDataChange: kil"+comment.toString());
                            comments.add(comment);

                        }

                        adapterComment.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}
