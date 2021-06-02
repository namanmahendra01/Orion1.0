
package com.orion.orion.contest;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
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
import com.orion.orion.profile.profile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class jury_voting_media extends AppCompatActivity {
    private static final String TAG = "jury_voting_media";

    private TableLayout juryTable;
    String username2 = "";
    private ImageView backArrrow;
    private TextView mTopBarTitle;

    List<EditText> etList1 = new ArrayList<EditText>();
    List<EditText> etList2 = new ArrayList<EditText>();
    String joiningKey, text = "", tex2t = "";
    ArrayList<String> participantlist;
    ArrayList<String> markList;
    Button btn;
    LinearLayout progress;
    //    SP
    Gson gson;
    SharedPreferences sp;
    String contestkey, jury, comment;
    int x = 1,y=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jury_voting_media);


        backArrrow = findViewById(R.id.backarrow);
        mTopBarTitle = findViewById(R.id.titleTopBar);
        backArrrow.setOnClickListener(view -> finish());
        mTopBarTitle.setText("Judge");

        juryTable = findViewById(R.id.jurytablevote);
        juryTable.setStretchAllColumns(true);
        btn = findViewById(R.id.submitMarksBtn);
        progress = findViewById(R.id.pro);


        Intent i = getIntent();
        contestkey = i.getStringExtra("contestId");
        jury = i.getStringExtra("jury");
        comment = i.getStringExtra("comment");

        if (jury.equals("jury1")){
            jury="j1";
        }else if(jury.equals("jury2")){
            jury="j2";

        }else{
            jury="j3";

        }

        if (comment.equals("comment1")){
            comment="c1";
        }else if(comment.equals("comment2")){
            comment="c2";

        }else{
            comment="c3";

        }




        //          Initialize SharedPreference variables
        sp = getSharedPreferences(contestkey, Context.MODE_PRIVATE);
        gson = new Gson();

        fetchParticipantList();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progress.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                final int[] i = {0};
                for (String joiningKey : participantlist) {

                    TableRow row = (TableRow) juryTable.getChildAt(x);
                    EditText et = (EditText) row.getChildAt(2);
                    EditText et2 = (EditText) row.getChildAt(3);
                    String text = et.getText().toString();
                   String tex2t = et2.getText().toString();

                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();

                    if (text.equals("") || tex2t.equals("") || (Integer.parseInt(text) > 10 || Integer.parseInt(text) <= 0)) {
                        progress.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        if (text.equals("")) {
                            et.setError("please fill");

                        }else if ((Integer.parseInt(text) > 10 || Integer.parseInt(text) <= 0)) {
                            et.setError("marks must be between 1-10 range");

                        }
                        if (tex2t.equals("")) {
                            et2.setError("please fill");

                        }


                    } else {
                        addUsernameAndMediaLinktoSP(joiningKey, text, tex2t);

                        Log.d(TAG, "onClick: ");
                        ref2.child(getString(R.string.dbname_participantList))
                                .child(contestkey)
                                .child(joiningKey)
                                .child(getString(R.string.juryMarks))
                                .child(jury)
                                .setValue(text)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ref2.child(getString(R.string.dbname_participantList))
                                        .child(contestkey)
                                        .child(joiningKey)
                                        .child(getString(R.string.juryMarks))
                                        .child(comment)
                                        .setValue(tex2t)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        i[0]++;
                                        if (i[0] == participantlist.size()) {
                                            progress.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Toast.makeText(jury_voting_media.this, "Marks Submitted!", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }
                        });


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
        ArrayList<String> list = new ArrayList<>(Collections.nCopies(4, "0"));
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
        ref2.child(getString(R.string.dbname_participantList))
                .child(contestkey)
                .child(joiningKey)
                .child(getString(R.string.field_user_id)).
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
                                        list.set(0, username2);

                                        ref2.child(getString(R.string.dbname_participantList))
                                                .child(contestkey)
                                                .child(joiningKey)
                                                .child(getString(R.string.field_media_link)).
                                                addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {


                                                        String link = dataSnapshot1.getValue().toString();
                                                        list.set(1, link);
                                                        list.set(2, text);
                                                        list.set(3, tex2t);

                                                        //    Add newly Created ArrayList to Shared Preferences
                                                        Log.d(TAG, "onDataChange: final "+ list);
                                                        SharedPreferences.Editor editor = sp.edit();
                                                        String json = gson.toJson(list);
                                                        editor.putString(joiningKey, json);
                                                        editor.apply();

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
            if (markList == null || markList.size() < 4) {    //        if no arrayList is present
                //                fetching table
//                setting row
                TableRow tbrow = new TableRow(jury_voting_media.this);
                tbrow.setLayoutParams(new TableLayout.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                tbrow.setWeightSum(4);

//                setting 1st item of row i.e textview
                TextView t1v = new TextView(jury_voting_media.this);
                getU(joiningKey, t1v);
                t1v.setTextColor(getResources().getColor(R.color.red));
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

                getMarksandComments(joiningKey,Et3v,Et4v);
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
                                .child(getString(R.string.field_user_id));
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
                                .child(getString(R.string.field_media_link)).
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        link[0] = dataSnapshot.getValue().toString();
                                        boolean ok = link[0].length() > 23;
                                        if (ok) {
                                            if (link[0].substring(8, 23).equals("firebasestorage")) {
                                                Intent i = new Intent(jury_voting_media.this, activity_view_media.class);
                                                i.putExtra("imageLink", link[0]);
                                                i.putExtra("contestkey", contestkey);
                                                i.putExtra("joiningkey", joiningKey);
                                                i.putExtra("view", "No");

                                                startActivity(i);
                                            } else {
                                                try {
                                                    Uri uri = Uri.parse(link[0]);
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                    startActivity(intent);

                                                } catch (ActivityNotFoundException e) {
                                                    Toast.makeText(jury_voting_media.this, "Invalid Link", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        } else {
                                            try {
                                                Uri uri = Uri.parse(link[0]);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);

                                            } catch (ActivityNotFoundException e) {
                                                Toast.makeText(jury_voting_media.this, "Invalid Link", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                    }
                });

            } else {

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
                                .child(getString(R.string.field_user_id));
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
                        String json = sp.getString(joiningKey, null);

                        Type type = new TypeToken<ArrayList<String>>() {
                        }.getType();
                        markList = gson.fromJson(json, type);
                        boolean ok = markList.get(1).length() > 23;
                        if (ok) {
                            if (markList.get(1).substring(8, 23).equals("firebasestorage")) {
                                Intent i = new Intent(jury_voting_media.this, activity_view_media.class);
                                i.putExtra("imageLink", markList.get(1));
                                i.putExtra("contestkey", contestkey);
                                i.putExtra("joiningkey", joiningKey);
                                i.putExtra("view", "No");

                                startActivity(i);
                            } else {
                                try {
                                    Uri uri = Uri.parse(markList.get(1));
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);

                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(jury_voting_media.this, "Invalid Link", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "onClick: " + e.getMessage());
                                }
                            }
                        } else {
                            try {
                                Uri uri = Uri.parse(markList.get(1));
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);

                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(jury_voting_media.this, "Invalid Link", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onClick: " + e.getMessage());
                            }
                        }


                    }
                });

            }

        }
    }

    private void getMarksandComments(String joiningKey, EditText et3v, EditText et4v) {
        DatabaseReference ref5 = FirebaseDatabase.getInstance().getReference();
        ref5.child(getString(R.string.dbname_participantList))
                .child(contestkey)
                .child(joiningKey)
                .child(getString(R.string.juryMarks)).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: "+dataSnapshot.getValue());
                        if (dataSnapshot.exists()) {
                            String mark = dataSnapshot.child(jury).getValue().toString();
                            String comnt = dataSnapshot.child(comment).getValue().toString();
                            if (!mark.equals("-")) {
                                et3v.setText(dataSnapshot.child(jury).getValue().toString());
                            }

                            if (!comnt.equals("-")) {
                                et4v.setText(dataSnapshot.child(comment).getValue().toString());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void getU(String joiningKey, TextView textView) {
        DatabaseReference ref5 = FirebaseDatabase.getInstance().getReference();
        ref5.child(getString(R.string.dbname_participantList))
                .child(contestkey)
                .child(joiningKey)
                .child(getString(R.string.field_user_id)).
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



    protected void onPause() {

        super.onPause();

        saveMarks();


    }

    private void saveMarks() {
        for (String joiningKey : participantlist) {

            String json = sp.getString(joiningKey, null);

            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            markList = gson.fromJson(json, type);

            TableRow row = (TableRow) juryTable.getChildAt(y);
            EditText et = (EditText) row.getChildAt(2);
            EditText et2 = (EditText) row.getChildAt(3);
            text = et.getText().toString();
            tex2t = et2.getText().toString();

            if (text.equals("") || tex2t.equals("")) {
                y++;
                continue;
            } else {
                markList = new ArrayList<>(Collections.nCopies(4, "0"));
                addUsernameAndMediaLinktoSP(joiningKey, text, tex2t);
                y++;

            }


        }
        y = 1;


    }

}


