
package com.orion.orion.contest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orion.orion.R;
import com.orion.orion.contest.Contest_Evaluation.activity_view_media;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.juryMarks;

import com.orion.orion.models.users;
import com.orion.orion.profile.profile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class jury_voting_media extends AppCompatActivity {
    private static final String TAG = "jury_voting_media";

    private TableLayout juryTable;
    String username2 = "";
    List<EditText> etList1 = new ArrayList<EditText>();
    List<EditText> etList2 = new ArrayList<EditText>();
    String joiningKey, text = "", tex2t = "";
    ArrayList<String> participantlist;
    ArrayList<String> markList;
    Button btn;
    //    SP
    Gson gson;
    SharedPreferences sp;
    String contestkey, jury, comment;
    int x = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jury_voting_media);
        juryTable = findViewById(R.id.jurytablevote);
        juryTable.setStretchAllColumns(true);
        btn = findViewById(R.id.submitMarksBtn);


        Intent i = getIntent();
        contestkey = i.getStringExtra("contestId");
        String userid = i.getStringExtra("userId");
        jury = i.getStringExtra("jury");
        comment = i.getStringExtra("comment");

        //          Initialize SharedPreference variables
        sp = getSharedPreferences(contestkey, Context.MODE_PRIVATE);
        gson = new Gson();

        fetchParticipantList();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                for (String joiningKey : participantlist) {

                    String json = sp.getString(joiningKey, null);

                    Type type = new TypeToken<ArrayList<String>>() {
                    }.getType();
                    markList = gson.fromJson(json, type);

                    TableRow row = (TableRow) juryTable.getChildAt(x);
                    EditText et = (EditText) row.getChildAt(2);
                    EditText et2 = (EditText) row.getChildAt(3);
                    text = et.getText().toString();
                    tex2t = et2.getText().toString();

                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();

                    if (text.equals("") || tex2t.equals("")) {
                        et.setError("please fill");
                        et2.setError("please fill");
                    } else {
                        markList = new ArrayList<>(Collections.nCopies(4, "0"));
                        addUsernameAndMediaLinktoSP(joiningKey,text,tex2t);

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

                    }


                }
                x = 1;
                int tableChild = juryTable.getChildCount();
                for (int x = 1; x < tableChild; x++) {
                    TableRow row = (TableRow) juryTable.getChildAt(x);
                    EditText et = (EditText) row.getChildAt(2);
                    EditText et2 = (EditText) row.getChildAt(3);
                    String text = et.getText().toString();
                    String tex2t = et2.getText().toString();
                    Log.d(TAG, "onClick: " + text + " " + tex2t);
                }


            }
        });
    }

    private void addUsernameAndMediaLinktoSP(String joiningKey, String text, String tex2t) {
ArrayList<String> list= new ArrayList<>(Collections.nCopies(4,"0"));

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
        ArrayList<String> finalList = list;
        ref2.child(getString(R.string.dbname_participantList))
                .child(contestkey)
                .child(joiningKey)
                .child("userid").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ref2.child(getString(R.string.dbname_users))
                                .child(dataSnapshot.getValue().toString())
                                .child(getString(R.string.field_username))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String username2 = snapshot.getValue().toString();
                                        finalList.set(0, username2);

                                        ref2.child(getString(R.string.dbname_participantList))
                                                .child(contestkey)
                                                .child(joiningKey)
                                                .child("mediaLink").
                                                addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {


                                                        String link = dataSnapshot1.getValue().toString();
                                                        finalList.set(1, link);
                                                        finalList.set(2,text);
                                                        finalList.set(3,tex2t);

                                                        //    Add newly Created ArrayList to Shared Preferences
                                                        SharedPreferences.Editor editor = sp.edit();
                                                        String json = gson.toJson(finalList);
                                                        editor.putString(joiningKey, json);
                                                        editor.apply();
                                                        Log.d(TAG, "onClick: vbn" + finalList);

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        Log.d(TAG, "onDataChange: " + username2);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void fetchParticipantList() {
        participantlist = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(getString(R.string.dbname_participantList))
                .child(contestkey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            participantlist.add(snapshot1.getKey());
                        }
                        fetchdetails();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void fetchdetails() {

        for (String joiningKey : participantlist) {
            String json = sp.getString(joiningKey, null);

            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            markList = gson.fromJson(json, type);
            Log.d(TAG, "fetchdetails: " + markList);
            if (markList == null || markList.size() < 4) {    //        if no arrayList is present
                Log.d(TAG, "fetchdetails: 1");
                //                fetching table
//                setting row
                TableRow tbrow = new TableRow(jury_voting_media.this);
                tbrow.setLayoutParams(new TableLayout.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                tbrow.setWeightSum(4);

//                setting 1st item of row i.e textview
                TextView t1v = new TextView(jury_voting_media.this);
                getUsername(joiningKey, t1v);
                t1v.setTextColor(getResources().getColor(R.color.yellow));
                t1v.setGravity(Gravity.CENTER);
                tbrow.addView(t1v);

                //                setting 2nd item of row i.e textview

                TextView t2v = new TextView(jury_voting_media.this);
                t2v.setText("View");
                t2v.setTextColor(Color.BLUE);
                t2v.setGravity(Gravity.CENTER);
                tbrow.addView(t2v);


                //                setting 3rd item of row i.e editview

                EditText Et3v = new EditText(jury_voting_media.this);
                Et3v.setTextColor(Color.BLACK);
                Et3v.setGravity(Gravity.CENTER);
                Et3v.setInputType(InputType.TYPE_CLASS_NUMBER);
                Et3v.setMaxLines(1);
                Et3v.setMaxWidth(10);
                tbrow.addView(Et3v);
                etList1.add(Et3v);

                //                setting 4th item of row i.e editview

                EditText Et4v = new EditText(jury_voting_media.this);
                Et4v.setTextColor(Color.BLACK);
                Et4v.setGravity(Gravity.CENTER);
                Et4v.setMaxLines(1);
                Et4v.setMaxWidth(10);
                etList1.add(Et4v);
                tbrow.addView(Et4v);

//                add row to jury table
                juryTable.addView(tbrow);

                t1v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                        Query userquery = ref
                                .child(getString(R.string.dbname_participantList))
                                .child(contestkey)
                                .child(joiningKey)
                                .child("userid");
                        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String userid = dataSnapshot.getValue().toString();
                                Intent i = new Intent(jury_voting_media.this, profile.class);
                                i.putExtra(getString(R.string.calling_activity), getString(R.string.home));

                                i.putExtra(getString(R.string.intent_user), userid);
                                startActivity(i);


                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(TAG, "Query Cancelled");
                            }
                        });


                    }
                });
                t2v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String[] link = {""};
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                        ref.child(getString(R.string.dbname_participantList))
                                .child(contestkey)
                                .child(joiningKey)
                                .child("mediaLink").
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        link[0] = dataSnapshot.getValue().toString();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                        Intent i = new Intent(jury_voting_media.this, activity_view_media.class);
                        i.putExtra("imageLink", link[0]);
                        i.putExtra("contestkey", contestkey);
                        i.putExtra("joiningkey", joiningKey);
                        i.putExtra("view", "No");

                        startActivity(i);

                    }
                });

            } else {
                Log.d(TAG, "fetchdetails: 2");

//                fetching table
//                setting row
                TableRow tbrow = new TableRow(jury_voting_media.this);
                tbrow.setLayoutParams(new TableLayout.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                tbrow.setWeightSum(4);

//                setting 1st item of row i.e textview
                TextView t1v = new TextView(jury_voting_media.this);
                t1v.setText(markList.get(0));
                t1v.setTextColor(getResources().getColor(R.color.yellow));
                t1v.setGravity(Gravity.CENTER);
                tbrow.addView(t1v);

                //                setting 2nd item of row i.e textview

                TextView t2v = new TextView(jury_voting_media.this);
                t2v.setText("View");
                t2v.setTextColor(Color.BLUE);
                t2v.setGravity(Gravity.CENTER);
                tbrow.addView(t2v);


                //                setting 3rd item of row i.e editview

                EditText Et3v = new EditText(jury_voting_media.this);
                Et3v.setTextColor(Color.BLACK);
                Et3v.setGravity(Gravity.CENTER);
                Et3v.setInputType(InputType.TYPE_CLASS_NUMBER);
                Et3v.setMaxLines(1);
                Et3v.setMaxWidth(10);
                Et3v.setText(markList.get(2));
                tbrow.addView(Et3v);
                etList1.add(Et3v);

                //                setting 4th item of row i.e editview

                EditText Et4v = new EditText(jury_voting_media.this);
                Et4v.setTextColor(Color.BLACK);
                Et4v.setGravity(Gravity.CENTER);
                Et4v.setMaxLines(1);
                Et4v.setMaxWidth(10);
                Et4v.setText(markList.get(3));
                etList1.add(Et4v);
                tbrow.addView(Et4v);

//                add row to jury table
                juryTable.addView(tbrow);

                t1v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                        Query userquery = ref
                                .child(getString(R.string.dbname_participantList))
                                .child(contestkey)
                                .child(joiningKey)
                                .child("userid");
                        userquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String userid = dataSnapshot.getValue().toString();
                                Intent i = new Intent(jury_voting_media.this, profile.class);
                                i.putExtra(getString(R.string.calling_activity), getString(R.string.home));

                                i.putExtra(getString(R.string.intent_user), userid);
                                startActivity(i);


                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(TAG, "Query Cancelled");
                            }
                        });


                    }
                });
                t2v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(jury_voting_media.this, activity_view_media.class);
                        i.putExtra("imageLink", markList.get(1));
                        i.putExtra("contestkey", contestkey);
                        i.putExtra("joiningkey", joiningKey);
                        i.putExtra("view", "No");

                        startActivity(i);

                    }
                });

            }

        }
    }


    private void getUsername(String joiningKey, TextView textView) {
        DatabaseReference ref5 = FirebaseDatabase.getInstance().getReference();
        ref5.child(getString(R.string.dbname_participantList))
                .child(contestkey)
                .child(joiningKey)
                .child("userid").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ref5.child(getString(R.string.dbname_users))
                                .child(dataSnapshot.getValue().toString())
                                .child(getString(R.string.field_username))
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        username2 = snapshot.getValue().toString();
                                        textView.setText(username2);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

protected   void  onPause() {

    super.onPause();

    Log.d(TAG, "onPause: qwe");
    for (String joiningKey : participantlist) {

        String json = sp.getString(joiningKey, null);

        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        markList = gson.fromJson(json, type);

        TableRow row = (TableRow) juryTable.getChildAt(x);
        EditText et = (EditText) row.getChildAt(2);
        EditText et2 = (EditText) row.getChildAt(3);
        text = et.getText().toString();
        tex2t = et2.getText().toString();

        if (text.equals("") || tex2t.equals("")) {
         continue;
        } else {
            markList = new ArrayList<>(Collections.nCopies(4, "0"));
            addUsernameAndMediaLinktoSP(joiningKey,text,tex2t);
            x++;

        }


    }
    x = 1;
    int tableChild = juryTable.getChildCount();
    for (int x = 1; x < tableChild; x++) {
        TableRow row = (TableRow) juryTable.getChildAt(x);
        EditText et = (EditText) row.getChildAt(2);
        EditText et2 = (EditText) row.getChildAt(3);
        String text = et.getText().toString();
        String tex2t = et2.getText().toString();
        Log.d(TAG, "onClick: " + text + " " + tex2t);
    }


}

}


