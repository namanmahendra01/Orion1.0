package com.orion.orion.contest.Contest_Evaluation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orion.orion.Adapters.AdapterParticipantList;
import com.orion.orion.R;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.util.FirebaseMethods;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class fragment_contest_participants extends Fragment {

    private static final String TAG = "Participant FRAGMENT";
    RecyclerView participantRv;
    private ArrayList<ParticipantList> participantLists;
    private ArrayList<ParticipantList> paginatedparticipantList;
    private FirebaseAuth fAuth;
    private TextView request,partNum;
    private int mResults;
    String Conteskey;
    boolean notify = false;
    private FirebaseMethods mFirebaseMethods;

    SwipeRefreshLayout participantRefresh;
    boolean flag1 = false;
    private static int RETRY_DURATION = 1000;
    private static final Handler handler = new Handler(Looper.getMainLooper());

    //    SP
    Gson gson;
    SharedPreferences sp;
    FloatingActionButton floatbtn;


    private AdapterParticipantList adapterParticipantList;

    public fragment_contest_participants() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_participant, container, false);

        mFirebaseMethods = new FirebaseMethods(getActivity());
        participantRefresh = view.findViewById(R.id.participant_refresh);
        partNum = view.findViewById(R.id.partNum);

        Bundle b = getActivity().getIntent().getExtras();
        Conteskey = b.getString("contestId");
//          Initialize SharedPreference variables
        sp = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        gson = new Gson();

        request = view.findViewById(R.id.request);
        floatbtn = view.findViewById(R.id.float_btn);

        floatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomsheet();
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(getString(R.string.dbname_request))
                .child(getString(R.string.dbname_participantList))
                .child(Conteskey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String i = String.valueOf(dataSnapshot.getChildrenCount());
                        request.setText("Requests(" + i + ")");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), Participant_Request.class);
                i.putExtra("ContestKey", Conteskey);
                startActivity(i);
            }
        });


        participantRv = view.findViewById(R.id.recycler_view4);
        participantRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        participantRv.setLayoutManager(linearLayoutManager);

        participantLists = new ArrayList<>();


        fAuth = FirebaseAuth.getInstance();

        participantRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {

                    displayMoreParticipnat();

                }
            }
        });


        participantRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                flag1 = false;

                getParticipantListFromSP();
                checkRefresh();


            }

            private void checkRefresh() {
                if (participantRefresh.isRefreshing() && flag1) {
                    participantRefresh.setRefreshing(false);
                    handler.removeCallbacks(this::checkRefresh);

                    flag1 = false;

                } else {
                    handler.postDelayed(this::checkRefresh, RETRY_DURATION);

                }
            }
        });
        getParticipantListFromSP();

        return view;
    }

    //  fetching ParticipantList  from SharedPreferences
    private void getParticipantListFromSP() {
        String json = sp.getString(Conteskey, null);

        Type type = new TypeToken<ArrayList<ParticipantList>>() {
        }.getType();
        participantLists = gson.fromJson(json, type);
        if (participantLists == null || participantLists.size() == 0) {    //        if no arrayList is present
            participantLists = new ArrayList<>();
            Log.d(TAG, "ttt 1");
            fetchParticipants();             //            make new Arraylist

        } else {
            Log.d(TAG, "ttt 2" + participantLists);
            checkUpdate();       //         Check if new paricipant is there

        }

    }

    private void checkUpdate() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


        reference.child(getString(R.string.dbname_participantList))
                .child(Conteskey)
                .orderByKey()
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Log.d(TAG, "onDataChange: hjh 1");
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: hjh 2");

                                if (participantLists.get(0).getJoiningKey().equals(snapshot1.getKey())) {
                                    Log.d(TAG, "onDataChange: hjh 3");

                                    displayParticipant();
                                } else {

                                    updateList();
                                }
                            }
                        } else {
                            updateList();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateList() {
        Collections.reverse(participantLists);
        DatabaseReference refer = FirebaseDatabase.getInstance().getReference();
        refer.child(FirebaseAuth.getInstance().getCurrentUser().getUid())

                .child(getString(R.string.dbname_participantList))
                .child(Conteskey)
                .orderByKey()
                .startAt(participantLists.get(participantLists.size() - 1).getJoiningKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            int x=0;
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                x++;
                                ParticipantList participantList = snapshot1.getValue(ParticipantList.class);

                                participantLists.add(participantList);

                                if(x==snapshot.getChildrenCount()){
                                    Collections.reverse(participantLists);

                                    //    Add newly Created ArrayList to Shared Preferences
                                    SharedPreferences.Editor editor = sp.edit();
                                    String json = gson.toJson(participantLists);
                                    editor.putString(Conteskey, json);
                                    editor.apply();


                                    displayParticipant();
                                }

                            }



                        } else {
                            fetchParticipants();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void fetchParticipants() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_participantList))
                .child(Conteskey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            int x=0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                x++;
                                ParticipantList participantList = snapshot.getValue(ParticipantList.class);

                                participantLists.add(participantList);

                                if(x==dataSnapshot.getChildrenCount()){
                                    Collections.reverse(participantLists);

                                    //    Add newly Created ArrayList to Shared Preferences
                                    SharedPreferences.Editor editor = sp.edit();
                                    String json = gson.toJson(participantLists);
                                    editor.putString(Conteskey, json);
                                    editor.apply();

                                    displayParticipant();
                                }
                            }



                        } else {
                            participantLists.clear();
                            SharedPreferences.Editor editor = sp.edit();
                            String json = gson.toJson(participantLists);
                            editor.putString(Conteskey, json);
                            editor.apply();

                            displayParticipant();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void bottomsheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);

        View bottomSheetView = getActivity().getLayoutInflater()
                .inflate(R.layout.layout_bottom_sheet_sendupdate, bottomSheetDialog.findViewById(R.id.layout_bottom_sheet_container));
        EditText msg = bottomSheetView.findViewById(R.id.msg);
        TextView send = bottomSheetView.findViewById(R.id.send);
        TextView cancel = bottomSheetView.findViewById(R.id.cancel);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg1 = msg.getText().toString();
                if (msg1.equals("")) {
                    Toast.makeText(getActivity(), "Write Something", Toast.LENGTH_SHORT).show();
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Message");
                    builder.setMessage("Are you sure, you want to send this Message?");

//                set buttons
                    builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            notify = true;

                            String noti = "Contest Host send you Message.Click here to see.///" + msg1;
                            for (int x = 0; x < participantLists.size(); x++) {

                                if (notify) {
                                    mFirebaseMethods.sendNotification(participantLists.get(x).getUserid(), "", "Contest Host send you a Message.Its important.", "Contest");
                                }

                                addToHisNotification(participantLists.get(x).getUserid(), noti);
                                if (x == participantLists.size() - 1) {
                                    bottomSheetDialog.dismiss();
                                    Toast.makeText(getContext(), "Message sent!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            notify = false;

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
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    private void addToHisNotification(String hisUid, String notification) {

        String timestamp = "" + System.currentTimeMillis();


//        data to put in notification
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", "false");
        hashMap.put("timeStamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notificaton", notification);
        hashMap.put("seen", "false");
        hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());


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


    private void displayParticipant() {
        Log.d(TAG, "display first 20 participant");
        flag1 = true;
        partNum.setText("Participants: " + participantLists.size());
        paginatedparticipantList = new ArrayList<>();
        if (participantLists != null) {

            try {


                int iteration = participantLists.size();
                if (iteration > 20) {
                    iteration = 20;
                }
                mResults = 20;
                for (int i = 0; i < iteration; i++) {
                    paginatedparticipantList.add(participantLists.get(i));
                }
                Log.d(TAG, "participant: sss" + paginatedparticipantList.size());
                adapterParticipantList = new AdapterParticipantList(getContext(), paginatedparticipantList);
                adapterParticipantList.setHasStableIds(true);
                participantRv.setAdapter(adapterParticipantList);

            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());

            }

        }
    }

    public void displayMoreParticipnat() {
        Log.d(TAG, "display next 20 participant");

        try {
            if (participantLists.size() > mResults && participantLists.size() > 0) {

                int iterations;
                if (participantLists.size() > (mResults + 20)) {
                    Log.d(TAG, "display next 15 participant");
                    iterations = 20;
                } else {
                    Log.d(TAG, "display less tha 15 participant");
                    iterations = participantLists.size() - mResults;
                }
                for (int i = mResults; i < mResults + iterations; i++) {
                    paginatedparticipantList.add(participantLists.get(i));

                }
                participantRv.post(new Runnable() {
                    @Override
                    public void run() {
                        adapterParticipantList.notifyItemRangeInserted(mResults,iterations);
                    }
                });
                mResults = mResults + iterations;


            }

        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());

        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());

        }

    }

}
