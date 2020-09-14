package com.orion.orion.contest.Contest_Evaluation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.Adapters.AdapterContestCreated;
import com.orion.orion.Adapters.AdapterContestUpcoming;
import com.orion.orion.Adapters.AdapterParticipantList;
import com.orion.orion.R;
import com.orion.orion.ViewPostActivity;
import com.orion.orion.contest.create.form;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.profile.profile;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.UniversalImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class fragment_contest_participants extends Fragment {

    private static final String TAG = "Participant FRAGMENT";
    RecyclerView participantRv;
    private ArrayList<ParticipantList> participantLists;
    private ArrayList<ParticipantList> paginatedparticipantList;
    private FirebaseAuth fAuth;
    private TextView request;
    private int mResults;
    String Conteskey;
    boolean notify=false;
    private FirebaseMethods mFirebaseMethods;

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

        Bundle b = getActivity().getIntent().getExtras();
        Conteskey = b.getString("contestId");

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
        adapterParticipantList = new AdapterParticipantList(getContext(), participantLists);
        participantRv.setAdapter(adapterParticipantList);

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

        getParticipant(Conteskey);


        return view;
    }

    private void bottomsheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);

        View bottomSheetView = ((FragmentActivity) getActivity()).getLayoutInflater()
                .inflate(R.layout.layout_bottom_sheet_sendupdate, (LinearLayout) bottomSheetDialog.findViewById(R.id.layout_bottom_sheet_container));
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
                            notify=true;

                            String noti = "Contest Host send you Message.Click here to see.///" + msg1;
                            for (int x = 0; x < participantLists.size(); x++) {

                                if (notify) {
                                    mFirebaseMethods.sendNotification(participantLists.get(x).getUserid(), "", "Contest Host send you a Message.Its important.","Contest");
                                }

                                addToHisNotification(participantLists.get(x).getUserid(), noti);
                                if (x == participantLists.size() - 1) {
                                    bottomSheetDialog.dismiss();
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

    private void sendMessage(String msg1) {


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

    private void getParticipant(String contestkey) {

        FirebaseUser user = fAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_participantList))
                .child(contestkey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        participantLists.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ParticipantList participantList = snapshot.getValue(ParticipantList.class);

                            Log.d(TAG, "onDataChange: " + participantList.toString());

                            participantLists.add(participantList);

                            Collections.reverse(participantLists);
                            displayParticipant();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void displayParticipant() {
        Log.d(TAG, "display first 10 participant");

        paginatedparticipantList = new ArrayList<>();
        if (participantLists != null) {

            try {


                int iteration = participantLists.size();
                if (iteration > 10) {
                    iteration = 10;
                }
                mResults = 10;
                for (int i = 0; i < iteration; i++) {
                    paginatedparticipantList.add(participantLists.get(i));
                }
                Log.d(TAG, "participant: sss" + paginatedparticipantList.size());
                adapterParticipantList = new AdapterParticipantList(getContext(), paginatedparticipantList);
                participantRv.setAdapter(adapterParticipantList);

            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());

            }

        }
    }

    public void displayMoreParticipnat() {
        Log.d(TAG, "display next 15 participant");

        try {
            if (participantLists.size() > mResults && participantLists.size() > 0) {

                int iterations;
                if (participantLists.size() > (mResults + 15)) {
                    Log.d(TAG, "display next 15 participant");
                    iterations = 15;
                } else {
                    Log.d(TAG, "display less tha 15 participant");
                    iterations = participantLists.size() - mResults;
                }
                for (int i = mResults; i < mResults + iterations; i++) {
                    paginatedparticipantList.add(participantLists.get(i));

                }
                mResults = mResults + iterations;
                participantRv.post(new Runnable() {
                    @Override
                    public void run() {
                        adapterParticipantList.notifyDataSetChanged();
                    }
                });

            }

        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());

        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());

        }

    }

}
