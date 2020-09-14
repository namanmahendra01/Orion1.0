
package com.orion.orion.contest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.contest.Contest_Evaluation.activity_view_media;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.juryMarks;

import com.orion.orion.models.users;
import com.orion.orion.profile.profile;

import java.util.ArrayList;
import java.util.List;



public class jury_voting_media extends AppCompatActivity {
    private static final String TAG = "jury_voting_media";

    private TableLayout juryTable;
    String username2="";
    List<EditText> etList1 = new ArrayList<EditText>();
    List<EditText> etList2 = new ArrayList<EditText>();
    String joiningKey,text="",tex2t="";
    Button btn;
    users user = new users();
    String contestkey,jury,comment;
    int x=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jury_voting_media);
        juryTable=findViewById(R.id.jurytablevote);
        juryTable.setStretchAllColumns(true);
        btn=findViewById(R.id.submitMarksBtn);


        Intent i = getIntent();
         contestkey=i.getStringExtra("contestId");
        String userid=i.getStringExtra("userId");
        jury=i.getStringExtra("jury");
        comment=i.getStringExtra("comment");
        init(contestkey);
    }
    private void init(String contestkey) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(getString(R.string.dbname_participantList))
                .child(contestkey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ParticipantList participantList = new ParticipantList();
                            participantList = snapshot.getValue(ParticipantList.class);
                            Log.d(TAG, "onDataChange: " + participantList.toString());
                            joiningKey = participantList.getJoiningKey().toString();
                            Log.d(TAG, "onDataChange: " + joiningKey);
                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
                            ParticipantList finalParticipantList = participantList;
                            ParticipantList finalParticipantList1 = participantList;
                            ParticipantList finalParticipantList2 = participantList;
                            ref2.child(getString(R.string.dbname_participantList))
                                    .child(contestkey)
                                    .child(joiningKey)
                                    .child(getString(R.string.juryMarks))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                            juryMarks juryMarks = new juryMarks();
                                            juryMarks = dataSnapshot.getValue(juryMarks.class);
                                            TableRow tbrow = new TableRow(jury_voting_media.this);
                                            tbrow.setLayoutParams(new TableLayout.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                                            tbrow.setWeightSum(4);
                                            TextView t1v = new TextView(jury_voting_media.this);
                                            getUsername(finalParticipantList1.getUserid(), t1v);
                                            t1v.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    DatabaseReference ref =FirebaseDatabase.getInstance().getReference();

                                                    Query userquery = ref
                                                            .child(getString(R.string.dbname_users))
                                                            .orderByChild(getString(R.string.field_username))
                                                            .equalTo(t1v.getText().toString());
                                                    userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                                                                user = singleSnapshot.getValue(users.class);
                                                                Intent i = new Intent(jury_voting_media.this, profile.class);
                                                                i.putExtra(getString(R.string.calling_activity),getString(R.string.home));

                                                                i.putExtra(getString(R.string.intent_user), user);
                                                                startActivity(i);
                                                            }


                                                        }


                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            Log.d(TAG, "Query Cancelled");
                                                        }
                                                    });



                                                }
                                            });
                                            t1v.setTextColor(Color.RED);
                                            t1v.setGravity(Gravity.CENTER);
                                            tbrow.addView(t1v);
                                            TextView t2v = new TextView(jury_voting_media.this);
                                            t2v.setText("View");
                                            t2v.setTextColor(Color.BLUE);
                                            t2v.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                    Intent i = new Intent(jury_voting_media.this, activity_view_media.class);
                                                    i.putExtra("imageLink", finalParticipantList2.getMediaLink());
                                                    i.putExtra("contestkey", contestkey);
                                                    i.putExtra("joiningkey", finalParticipantList2.getJoiningKey());
                                                    i.putExtra("view", "No");

                                                    startActivity(i);

                                                }
                                            });
                                            t2v.setGravity(Gravity.CENTER);
                                            tbrow.addView(t2v);
                                            EditText Et3v = new EditText(jury_voting_media.this);
                                            Et3v.setTextColor(Color.BLACK);
                                            Et3v.setGravity(Gravity.CENTER);
                                            Et3v.setInputType(InputType.TYPE_CLASS_NUMBER);
                                            Et3v.setMaxLines(1);
                                            Et3v.setMaxWidth(10);
                                            tbrow.addView(Et3v);
                                            etList1.add(Et3v);
                                            EditText Et4v = new EditText(jury_voting_media.this);
                                            Et4v.setTextColor(Color.BLACK);
                                            Et4v.setGravity(Gravity.CENTER);
                                            Et4v.setMaxLines(1);
                                            Et4v.setMaxWidth(10);

                                            etList1.add(Et4v);
                                            tbrow.addView(Et4v);
                                            juryTable.addView(tbrow);
                                            Log.d(TAG, "onDataChange: " + juryMarks.toString());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });



                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child(getString(R.string.dbname_participantList))
                        .child(contestkey)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    ParticipantList participantList = new ParticipantList();
                                    participantList = snapshot.getValue(ParticipantList.class);
                                    Log.d(TAG, "onDataChange: " + participantList.toString());
                                    joiningKey = participantList.getJoiningKey().toString();
                                    Log.d(TAG, "onDataChange: " + joiningKey);
                                    TableRow row= (TableRow)juryTable.getChildAt(x);
                                    EditText et=(EditText ) row.getChildAt(2);
                                    EditText et2=(EditText ) row.getChildAt(3);
                                     text=et.getText().toString();
                                     tex2t=et2.getText().toString();
                                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
                                    if (text.equals("")||tex2t.equals("")){
                                        et.setError("please fill");
                                        et2.setError("please fill");
                                    }else{
                                        ref2.child(getString(R.string.dbname_participantList))
                                                .child(contestkey)
                                                .child(joiningKey)
                                                .child(getString(R.string.juryMarks))
                                                .child(jury)
                                                .setValue(text);
                                        ref2.child(getString(R.string.dbname_participantList))
                                                .child(contestkey)
                                                .child(joiningKey)
                                                .child(getString(R.string.juryMarks))
                                                .child(comment)
                                                .setValue(tex2t);
                                        x++;
                                        Log.d(TAG, "onDataChange: "+x);
                                    }


                                }
                                x=1;

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                x--;

                            }
                        });
                int tableChild= juryTable.getChildCount();
                for (int x=1;x<tableChild;x++){
                    TableRow row= (TableRow)juryTable.getChildAt(x);
                    EditText et=(EditText ) row.getChildAt(2);
                    EditText et2=(EditText ) row.getChildAt(3);
                    String text=et.getText().toString();
                    String tex2t=et2.getText().toString();
                    Log.d(TAG, "onClick: "+text+" "+tex2t);
                }

            }
        });


    }
    private void getUsername(String userid,TextView textView) {
        DatabaseReference ref5 = FirebaseDatabase.getInstance().getReference();
        ref5.child(getString(R.string.dbname_user_account_settings)).
                orderByChild("user_id").equalTo(userid).
        addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    Log.d(TAG, "onDataChange: ji"+dataSnapshot.getChildren().toString());

                    users user = ds.getValue(users.class);
                 username2=user.getUsername();
               textView.setText(username2);
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d(TAG, "onDataChange: username2jij"+username2);


    }

}
