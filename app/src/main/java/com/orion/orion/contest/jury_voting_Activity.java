package com.orion.orion.contest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orion.orion.Adapters.AdapterContestUpcoming;
import com.orion.orion.Adapters.AdapterJudge;
import com.orion.orion.R;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.textFieldList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.orion.orion.util.SNTPClient.TAG;

public class jury_voting_Activity extends AppCompatActivity {
    private static final String TAG = "jury_voting_media";

    String userId = "", xj;
    private ImageView backArrrow;
    private TextView mTopBarTitle;
    ArrayList<ParticipantList> participantlist;
    TextView btn;
    LinearLayout progress;
    String[] criteriaArray;
    ArrayList<textFieldList> textField = new ArrayList<>();
    boolean fetchFromDB = false;
    private String filetype;
    RecyclerView judgeRv;
    String contestkey, jury, comment, criterias;
    int p = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jury_voting);


        backArrrow = findViewById(R.id.backarrow);
        mTopBarTitle = findViewById(R.id.titleTopBar);
        backArrrow.setOnClickListener(view -> finish());
        mTopBarTitle.setText("Judge");


        btn = findViewById(R.id.submitMarksBtn);
        progress = findViewById(R.id.pro);
        judgeRv = findViewById(R.id.judgeRv);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        judgeRv.setHasFixedSize(true);
        judgeRv.setDrawingCacheEnabled(true);
        judgeRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(20);
        judgeRv.setLayoutManager(linearLayoutManager);


        Intent i = getIntent();
        contestkey = i.getStringExtra("contestId");
        jury = i.getStringExtra("jury");
        comment = i.getStringExtra("comment");
        userId = i.getStringExtra("userId");

        SharedPreferences sp = getSharedPreferences("markTF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString(contestkey, null);
        Type type = new TypeToken<ArrayList<textFieldList>>() {
        }.getType();
        textField = gson.fromJson(json, type);
        if (textField == null || textField.size() == 0) {
            fetchFromDB = true;
        }


        if (jury.equals("jury1")) {
            jury = "j1";
            xj = "xj1";
            comment = "c1";
        } else if (jury.equals("jury2")) {
            jury = "j2";
            xj = "xj2";
            comment = "c2";


        } else {
            jury = "j3";
            xj = "xj3";
            comment = "c3";

        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(getString(R.string.dbname_contests))
                .child(userId)
                .child(getString(R.string.created_contest))
                .child(contestkey)

                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            criterias = snapshot.child(getString(R.string.field_judge_criteria)).getValue().toString();
                            if (criterias.contains("///")) {
                                criteriaArray = criterias.split("///");

                            } else {
                                criteriaArray[0] = criterias;
                            }
                            filetype = snapshot.child(getString(R.string.field_file_type)).getValue().toString();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("markTF", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = sp.getString(contestkey, null);
                Type type = new TypeToken<ArrayList<textFieldList>>() {
                }.getType();
                ArrayList<textFieldList> textFieldList1 = gson.fromJson(json, type);
                boolean ok = checkIfEntryEmptyOrWrong(textFieldList1);
                if (ok) {
                    progress.setVisibility(View.VISIBLE);
                    createMarksString(textFieldList1);
                }

            }
        });


        fetchParticipantList();

    }

    private void createMarksString(ArrayList<textFieldList> textFieldList1) {
        for (int x = 0; x < participantlist.size(); x++) {
            String f_string = "";
            for (int y = 0; y < criteriaArray.length; y++) {

                if (y == 0) {
                    f_string = f_string + textFieldList1.get(x).getEt1() + "///";
                    if (y == criteriaArray.length - 1) {
                        addToDatabase(f_string, x, textFieldList1);
                    }
                } else if (y == 1) {
                    f_string = f_string + textFieldList1.get(x).getEt2() + "///";
                    if (y == criteriaArray.length - 1) {
                        addToDatabase(f_string, x, textFieldList1);
                    }

                } else if (y == 2) {
                    f_string = f_string + textFieldList1.get(x).getEt3() + "///";
                    if (y == criteriaArray.length - 1) {
                        addToDatabase(f_string, x, textFieldList1);
                    }

                } else if (y == 3) {
                    Log.d(TAG, "createMarksString: 1");

                    f_string = f_string + textFieldList1.get(x).getEt4() + "///";
                    if (y == criteriaArray.length - 1) {
                        Log.d(TAG, "createMarksString: 1");

                        addToDatabase(f_string, x, textFieldList1);
                    }

                } else if (y == 4) {
                    f_string = f_string + textFieldList1.get(x).getEt5() + "///";
                    if (y == criteriaArray.length - 1) {
                        addToDatabase(f_string, x, textFieldList1);
                    }

                } else if (y == 5) {
                    f_string = f_string + textFieldList1.get(x).getEt6() + "///";
                    if (y == criteriaArray.length - 1) {
                        addToDatabase(f_string, x, textFieldList1);
                    }

                } else if (y == 6) {
                    f_string = f_string + textFieldList1.get(x).getEt7() + "///";
                    if (y == criteriaArray.length - 1) {
                        addToDatabase(f_string, x, textFieldList1);
                    }

                } else if (y == 7) {
                    f_string = f_string + textFieldList1.get(x).getEt8() + "///";
                    if (y == criteriaArray.length - 1) {
                        addToDatabase(f_string, x, textFieldList1);
                    }

                } else if (y == 8) {
                    f_string = f_string + textFieldList1.get(x).getEt9() + "///";
                    if (y == criteriaArray.length - 1) {
                        addToDatabase(f_string, x, textFieldList1);
                    }

                } else if (y == 9) {
                    f_string = f_string + textFieldList1.get(x).getEt10() + "///";
                    if (y == criteriaArray.length - 1) {
                        addToDatabase(f_string, x, textFieldList1);
                    }

                }
            }

        }


    }

    private void addToDatabase(String f_string, int x, ArrayList<textFieldList> textFieldList1) {
        p++;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(
                getString(R.string.dbname_participantList))
                .child(contestkey)
                .child(participantlist.get(x).getJi())
                .child(getString(R.string.juryMarks));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ref.child(jury).setValue(textFieldList1.get(x).getTotal());
                    ref.child(xj).setValue(f_string);
                    if (textFieldList1.get(x).getFeedback() != null) {
                        ref.child(comment).setValue(textFieldList1.get(x).getFeedback());
                    }
                    if (p == participantlist.size()) {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(jury_voting_Activity.this, "Marks Submitted Successfully!", Toast.LENGTH_SHORT).show();
                        p = 0;

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private boolean checkIfEntryEmptyOrWrong(ArrayList<textFieldList> textFieldList1) {
        boolean ok = false;
        if (textFieldList1.size() == participantlist.size()) {
            ok = true;
        }
        if (ok) {

            outerLoop:
            for (int x = 0; x < participantlist.size(); x++) {
                for (int y = 0; y < criteriaArray.length; y++) {

                    if (y == 0) {
                        if (textFieldList1.get(x).getEt1() == null || textFieldList1.get(x).getEt1().equals("")) {
                            ok = false;
                            showToast("Some fields are empty!Please fill them first.");
                            break outerLoop;
                        } else if (Integer.parseInt(textFieldList1.get(x).getEt1()) <= 0 || Integer.parseInt(textFieldList1.get(x).getEt1()) > 10) {
                            ok = false;
                            showToast("Marks must be given in 1-10 range");
                            break outerLoop;

                        } else {
                            ok = true;
                        }

                    } else if (y == 1) {
                        if (textFieldList1.get(x).getEt2() == null || textFieldList1.get(x).getEt2().equals("")) {
                            ok = false;
                            showToast("Some fields are empty!Please fill them first.");

                            break outerLoop;
                        } else if (Integer.parseInt(textFieldList1.get(x).getEt2()) <= 0 || Integer.parseInt(textFieldList1.get(x).getEt2()) > 10) {
                            ok = false;
                            showToast("Marks must be given in 1-10 range");
                            break outerLoop;

                        } else {
                            ok = true;
                        }
                    } else if (y == 2) {
                        if (textFieldList1.get(x).getEt3() == null || textFieldList1.get(x).getEt3().equals("")) {
                            ok = false;
                            showToast("Some fields are empty!Please fill them first.");

                            break outerLoop;
                        } else if (Integer.parseInt(textFieldList1.get(x).getEt3()) <= 0 || Integer.parseInt(textFieldList1.get(x).getEt3()) > 10) {
                            ok = false;
                            showToast("Marks must be given in 1-10 range");
                            break outerLoop;

                        } else {
                            ok = true;
                        }
                    } else if (y == 3) {
                        if (textFieldList1.get(x).getEt4() == null || textFieldList1.get(x).getEt4().equals("")) {
                            ok = false;
                            showToast("Some fields are empty!Please fill them first.");

                            break outerLoop;
                        } else if (Integer.parseInt(textFieldList1.get(x).getEt4()) <= 0 || Integer.parseInt(textFieldList1.get(x).getEt4()) > 10) {
                            ok = false;
                            showToast("Marks must be given in 1-10 range");
                            break outerLoop;

                        } else {
                            ok = true;
                        }
                    } else if (y == 4) {
                        if (textFieldList1.get(x).getEt5() == null || textFieldList1.get(x).getEt5().equals("")) {
                            ok = false;
                            showToast("Some fields are empty!Please fill them first.");

                            break outerLoop;
                        } else if (Integer.parseInt(textFieldList1.get(x).getEt5()) <= 0 || Integer.parseInt(textFieldList1.get(x).getEt5()) > 10) {
                            ok = false;
                            showToast("Marks must be given in 1-10 range");
                            break outerLoop;

                        } else {
                            ok = true;
                        }
                    } else if (y == 5) {
                        if (textFieldList1.get(x).getEt6() == null || textFieldList1.get(x).getEt6().equals("")) {
                            ok = false;
                            showToast("Some fields are empty!Please fill them first.");

                            break outerLoop;
                        } else if (Integer.parseInt(textFieldList1.get(x).getEt6()) <= 0 || Integer.parseInt(textFieldList1.get(x).getEt6()) > 10) {
                            ok = false;
                            showToast("Marks must be given in 1-10 range");
                            break outerLoop;

                        } else {
                            ok = true;
                        }
                    } else if (y == 6) {
                        if (textFieldList1.get(x).getEt7() == null || textFieldList1.get(x).getEt7().equals("")) {
                            ok = false;
                            showToast("Some fields are empty!Please fill them first.");

                            break outerLoop;
                        } else if (Integer.parseInt(textFieldList1.get(x).getEt7()) <= 0 || Integer.parseInt(textFieldList1.get(x).getEt7()) > 10) {
                            ok = false;
                            showToast("Marks must be given in 1-10 range");
                            break outerLoop;

                        } else {
                            ok = true;
                        }
                    } else if (y == 7) {
                        if (textFieldList1.get(x).getEt8() == null || textFieldList1.get(x).getEt8().equals("")) {
                            ok = false;
                            showToast("Some fields are empty!Please fill them first.");

                            break outerLoop;
                        } else if (Integer.parseInt(textFieldList1.get(x).getEt8()) <= 0 || Integer.parseInt(textFieldList1.get(x).getEt8()) > 10) {
                            ok = false;
                            showToast("Marks must be given in 1-10 range");
                            break outerLoop;

                        } else {
                            ok = true;
                        }
                    } else if (y == 8) {
                        if (textFieldList1.get(x).getEt9() == null || textFieldList1.get(x).getEt9().equals("")) {
                            ok = false;
                            showToast("Some fields are empty!Please fill them first.");

                            break outerLoop;
                        } else if (Integer.parseInt(textFieldList1.get(x).getEt9()) <= 0 || Integer.parseInt(textFieldList1.get(x).getEt9()) > 10) {
                            ok = false;
                            showToast("Marks must be given in 1-10 range");
                            break outerLoop;

                        } else {
                            ok = true;
                        }
                    } else if (y == 9) {
                        if (textFieldList1.get(x).getEt10() == null || textFieldList1.get(x).getEt10().equals("")) {
                            ok = false;
                            showToast("Some fields are empty!Please fill them first.");

                            break outerLoop;
                        } else if (Integer.parseInt(textFieldList1.get(x).getEt10()) <= 0 || Integer.parseInt(textFieldList1.get(x).getEt10()) > 10) {
                            ok = false;
                            showToast("Marks must be given in 1-10 range");
                            break outerLoop;

                        } else {
                            ok = true;
                        }
                    }
                }
            }
        }
        return ok;

    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

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
                            participantlist.add(snapshot1.getValue(ParticipantList.class));
                        }
                        putDetails();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void putDetails() {
        judgeRv.setItemViewCacheSize(participantlist.size());

        AdapterJudge adapterJudge = new AdapterJudge(this, participantlist, criteriaArray, jury, comment, filetype, fetchFromDB);
        adapterJudge.setHasStableIds(true);

        judgeRv.setAdapter(adapterJudge);


    }

}