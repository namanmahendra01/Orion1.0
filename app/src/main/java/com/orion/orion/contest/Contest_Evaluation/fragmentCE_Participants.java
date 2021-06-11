package com.orion.orion.contest.Contest_Evaluation;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.Button;
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


public class fragmentCE_Participants extends Fragment {

    private static final String TAG = "Participant FRAGMENT";
    private RecyclerView participantRv;
    private ArrayList<ParticipantList> participantLists;
    private ArrayList<ParticipantList> paginatedparticipantList;
    private TextView request, partNum;
    private int mResults;
    private String Conteskey,contestTitle;
    boolean notify = false;
    private FirebaseMethods mFirebaseMethods;

    private TextView chatRoomBtn;
    private SwipeRefreshLayout participantRefresh;
    boolean flag1 = false;
    private static int RETRY_DURATION = 1000;
    private static final Handler handler = new Handler(Looper.getMainLooper());

    //    SP
    private Gson gson;
    private SharedPreferences sp;

    private AdapterParticipantList adapterParticipantList;

    public fragmentCE_Participants() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_participant, container, false);

        mFirebaseMethods = new FirebaseMethods(getActivity());
        participantRefresh = view.findViewById(R.id.participant_refresh);
        partNum = view.findViewById(R.id.partNum);
        chatRoomBtn = view.findViewById(R.id.chatRoom);

        Bundle b = getActivity().getIntent().getExtras();
        Conteskey = b.getString("contestId");
        contestTitle=b.getString("title");
//          Initialize SharedPreference variables
        sp = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        gson = new Gson();

        request = view.findViewById(R.id.request);
        FloatingActionButton floatbtn = view.findViewById(R.id.float_btn);

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

        request.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), ParticipantRequestActivity.class);
            i.putExtra("ContestKey", Conteskey);
            startActivity(i);
        });

        chatRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ChatRoomActivity.class);
                i.putExtra("ContestId", Conteskey);
                startActivity(i);
            }
        });

        participantRv = view.findViewById(R.id.recycler_view4);
        participantRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        participantRv.setLayoutManager(linearLayoutManager);

        participantLists = new ArrayList<>();

        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        participantRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE)
                    displayMoreParticipnat();
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
                } else handler.postDelayed(this::checkRefresh, RETRY_DURATION);
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
            fetchParticipants();             //            make new Arraylist
        } else {
            checkUpdate();       //         Check if new paricipant is there

        }

    }

    private void checkUpdate() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference((getString(R.string.dbname_participantList))).child(Conteskey);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (participantLists.size()!=snapshot.getChildrenCount()){
                        fetchParticipants();
                    }else{
                        reference.orderByKey()
                                .limitToLast(1)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                if (participantLists.get(0).getJi().equals(snapshot1.getKey())) {
                                                    displayParticipant();
                                                } else                         fetchParticipants();

                                            }
                                        } else                        fetchParticipants();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                    }
                }else{
                    fetchParticipants();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    private void fetchParticipants() {
        participantLists.clear();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_participantList))
                .child(Conteskey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            int x = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                x++;
                                ParticipantList participantList = snapshot.getValue(ParticipantList.class);
                                participantLists.add(participantList);
                                if (x == dataSnapshot.getChildrenCount()) {
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
        View bottomSheetView = getActivity().getLayoutInflater().inflate(R.layout.layout_bottom_sheet_sendupdate, bottomSheetDialog.findViewById(R.id.layout_bottom_sheet_container));
        EditText msg = bottomSheetView.findViewById(R.id.msg);
        TextView send = bottomSheetView.findViewById(R.id.send);
        TextView cancel = bottomSheetView.findViewById(R.id.cancel);
        send.setOnClickListener(v -> {
            String msg1 = msg.getText().toString();
            if (msg1.equals(""))
                Toast.makeText(getActivity(), "Write Something", Toast.LENGTH_SHORT).show();
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Message");
                builder.setMessage("Are you sure, you want to send this Message?");
//                set buttons
                builder.setPositiveButton("Send", (dialog, which) -> {
                    notify = true;
                    String noti = contestTitle+" Host send you Message.Click here to see.///" + msg1;
                    for (int x = 0; x < participantLists.size(); x++) {
                        if (notify)
                            mFirebaseMethods.sendNotification(participantLists.get(x).getUi(), "", "Contest Host send you a Message.Its important.", "Contest");
                        addToHisNotification(participantLists.get(x).getUi(), noti);
                        if (x == participantLists.size() - 1) {
                            bottomSheetDialog.dismiss();
                            Toast.makeText(getContext(), "Message sent!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    notify = false;

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.create().show();

            }
        });

        cancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    private void addToHisNotification(String hisUid, String notification) {
        String timestamp = "" + System.currentTimeMillis();

//        data to put in notification
        HashMap<Object,String> hashMap = new HashMap<>();
        hashMap.put("pId","false");

        hashMap.put(getString(R.string.field_timestamp),timestamp);

        hashMap.put("pUid",hisUid);

        hashMap.put(getString(R.string.field_notification_message),notification);
        hashMap.put(getString(R.string.field_if_seen),"false");

        hashMap.put("sUid",FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users));
        ref.child(hisUid).child(getString(R.string.field_Notifications)).child(timestamp).setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                }).addOnFailureListener(e -> {
        });

    }


    private void displayParticipant() {
        flag1 = true;
        partNum.setText("Participants: " + participantLists.size());
        paginatedparticipantList = new ArrayList<>();
        if (participantLists != null) {
            try {
                int iteration = participantLists.size();
                if (iteration > 20) iteration = 20;
                mResults = 20;
                for (int i = 0; i < iteration; i++)
                    paginatedparticipantList.add(participantLists.get(i));
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
        try {
            if (participantLists.size() > mResults && participantLists.size() > 0) {
                int iterations;
                if (participantLists.size() > (mResults + 20)) {
                    iterations = 20;
                } else {
                    iterations = participantLists.size() - mResults;
                }
                for (int i = mResults; i < mResults + iterations; i++) {
                    paginatedparticipantList.add(participantLists.get(i));
                }
                participantRv.post(() -> adapterParticipantList.notifyItemRangeInserted(mResults, iterations));
                mResults = mResults + iterations;
            }

        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());
        }
    }
}
