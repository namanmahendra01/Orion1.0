package com.orion.orion.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orion.orion.R;
import com.orion.orion.contest.Contest_Evaluation.activity_view_media;
import com.orion.orion.models.JoinForm;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.SNTPClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.android.volley.VolleyLog.TAG;

public class AdapterParticipantRequest extends RecyclerView.Adapter<AdapterParticipantRequest.ViewHolder> {
    private String mAppend = "";
    private boolean notify = false;
    private FirebaseMethods mFirebaseMethods;

    private Context mContext;
    private List<ParticipantList> participantLists;

    public AdapterParticipantRequest(Context mContext, List<ParticipantList> participantLists) {
        this.mContext = mContext;
        this.participantLists = participantLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.contest_participant_item, parent, false);
        return new AdapterParticipantRequest.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        mFirebaseMethods = new FirebaseMethods(mContext);

        ParticipantList mparticipantLists = participantLists.get(i);

        String timeStamp = mparticipantLists.getTim();

//        convert time stamp to date and time
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        holder.time.setText(dateTime);


        getparticipantDetails(mparticipantLists.getUi(), holder.username, holder.profile,holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext, R.style.BottomSheetDialogTheme);

                View bottomSheetView = ((FragmentActivity) mContext).getLayoutInflater()
                        .inflate(R.layout.layout_request_bottom_sheet, (LinearLayout) bottomSheetDialog.findViewById(R.id.layout_bottom_sheet_container));
                TextView name = bottomSheetView.findViewById(R.id.displaynameBs);
                TextView username = bottomSheetView.findViewById(R.id.usernameBs);
                TextView college = bottomSheetView.findViewById(R.id.collegeBs);
                TextView idcard = bottomSheetView.findViewById(R.id.idBs);
                TextView allow = bottomSheetView.findViewById(R.id.allow);
                TextView reject = bottomSheetView.findViewById(R.id.reject);
                TextView submission = bottomSheetView.findViewById(R.id.submissionBs);
                LinearLayout layout = bottomSheetView.findViewById(R.id.collegeLinear);
                CircleImageView profileview = bottomSheetView.findViewById(R.id.profileBs);


                name.setText(holder.name1);
                username.setText(holder.username1);
                Glide.with(mContext.getApplicationContext())
                        .load(holder.profilelink)
                        .placeholder(R.drawable.load)
                        .error(R.drawable.default_image2)
                        .placeholder(R.drawable.load)
                        .thumbnail(0.5f)
                        .into(profileview);
                getparticipantform(mparticipantLists.getUi(), mparticipantLists.getJi(), mparticipantLists.getCi(), college, layout,holder);


                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(mContext, profile.class);
                        i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                        i.putExtra(mContext.getString(R.string.intent_user), mparticipantLists.getUi());
                        mContext.startActivity(i);
                    }
                });
                username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(mContext, profile.class);
                        i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                        i.putExtra(mContext.getString(R.string.intent_user), mparticipantLists.getUi());
                        mContext.startActivity(i);
                    }
                });
                profileview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(mContext, profile.class);
                        i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                        i.putExtra(mContext.getString(R.string.intent_user), mparticipantLists.getUi());
                        mContext.startActivity(i);
                    }
                });
                submission.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        boolean ifNull = mparticipantLists.getMl() == null || mparticipantLists.getMl().equals("");
                        if (ifNull) {
                            Toast.makeText(mContext, "Invalid Link", Toast.LENGTH_SHORT).show();

                        } else if (mparticipantLists.getMl().length() > 23) {
                            if (mparticipantLists.getMl().substring(8, 23).equals("firebasestorage")) {
                                Intent i = new Intent(mContext.getApplicationContext(), activity_view_media.class);
                                i.putExtra("imageLink", mparticipantLists.getMl());
                                i.putExtra("view", "No");

                                mContext.startActivity(i);
                            } else {

                                try {
                                    Uri uri = Uri.parse(mparticipantLists.getMl());
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    mContext.startActivity(intent);

                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "Invalid Link", Toast.LENGTH_SHORT).show();
                                    Log.e(SNTPClient.TAG, "onClick: " + e.getMessage());
                                }
                            }
                        } else {

                            try {
                                Uri uri = Uri.parse(mparticipantLists.getMl());
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                mContext.startActivity(intent);

                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(mContext, "Invalid Link", Toast.LENGTH_SHORT).show();
                                Log.e(SNTPClient.TAG, "onClick: " + e.getMessage());
                            }
                        }

                    }
                });
                idcard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext.getApplicationContext(), activity_view_media.class);
                        i.putExtra("imageLink", mparticipantLists.getIl());
                        i.putExtra("view", "No");

                        mContext.startActivity(i);

                    }
                });
                reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                       bottomsheet(mparticipantLists,i);

                    }
                });
                allow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Allow");
                        builder.setMessage(R.string.allow_participanr_prompt);

//                set buttons
                        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notify = true;

                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                                db.child(mContext.getString(R.string.dbname_contests))
                                        .child(mparticipantLists.getUi())
                                        .child(mContext.getString(R.string.joined_contest))
                                        .child(mparticipantLists.getJi())
                                        .child(mContext.getString(R.string.field_status))
                                        .setValue("Accepted");

                                db.child(mContext.getString(R.string.dbname_contestlist))
                                        .child(mparticipantLists.getCi())
                                        .child(mContext.getString(R.string.field_Participant_List))
                                        .child(mparticipantLists.getUi())
                                        .setValue(true);

                                db.child(mContext.getString(R.string.dbname_contests))
                                        .child(mparticipantLists.getUi())
                                        .child(mContext.getString(R.string.field_joined_updates))
                                        .child(mparticipantLists.getJi())
                                        .setValue("Accepted");


                                db.child(mContext.getString(R.string.dbname_users))
                                        .child(mparticipantLists.getUi())
                                        .child(mContext.getString(R.string.changedJoinedContest))
                                        .setValue("true");

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.child(mContext.getString(R.string.dbname_request))
                                        .child(mContext.getString(R.string.dbname_participantList))
                                        .child(mparticipantLists.getCi())
                                        .child(mparticipantLists.getJi())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                ParticipantList participantList = dataSnapshot.getValue(ParticipantList.class);
                                                Log.d(TAG, "onSuccess: klkl2");

                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                                ref.child(mContext.getString(R.string.dbname_participantList))
                                                        .child(mparticipantLists.getCi())
                                                        .child(mparticipantLists.getJi())
                                                        .setValue(participantList)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "onSuccess: klkl1");
                                                                HashMap<String, Object> hashMap3 = new HashMap<>();
                                                                hashMap3.put(mContext.getString(R.string.field_jury_1), "");
                                                                hashMap3.put(mContext.getString(R.string.field_jury_2), "");
                                                                hashMap3.put(mContext.getString(R.string.field_jury_3), "");
                                                                hashMap3.put(mContext.getString(R.string.field_jury_name_1), "-");
                                                                hashMap3.put(mContext.getString(R.string.field_jury_name_2), "-");
                                                                hashMap3.put(mContext.getString(R.string.field_jury_name_3), "-");
                                                                hashMap3.put(mContext.getString(R.string.field_jury_comment1), "-");
                                                                hashMap3.put(mContext.getString(R.string.field_jury_comment2), "-");
                                                                hashMap3.put(mContext.getString(R.string.field_jury_comment3), "-");

                                                                DatabaseReference db3 = FirebaseDatabase.getInstance().getReference();
                                                                db3.child(mContext.getString(R.string.dbname_participantList))
                                                                        .child(mparticipantLists.getCi())
                                                                        .child(mparticipantLists.getJi())
                                                                        .child(mContext.getString(R.string.juryMarks))
                                                                        .setValue(hashMap3)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d(TAG, "onSuccess: klkl2");

                                                                                deleteRequest(mparticipantLists.getCi(), mparticipantLists.getJi(), mparticipantLists.getUi());
                                                                                bottomSheetDialog.dismiss();

                                                                                participantLists.remove(i);
                                                                                AdapterParticipantRequest.this.notifyItemRemoved(i);
                                                                            }
                                                                        });
                                                            }
                                                        });


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }

                                        });


                                if (notify) {
                                    mFirebaseMethods.sendNotification(mparticipantLists.getUi(), "", "You are now a participant.Check your ranking now.", "Submission Accepted");
                                }
                                notify = false;


                                addToHisNotification("" + mparticipantLists.getUi(), "Submission Accepted for a contest.Check on Contests.");

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
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });


    }

    private void addToHisNotification(String hisUid, String notification) {

        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Colombo"), new SNTPClient.Listener() {
            @Override
            public void onTimeReceived(String rawDate) {
                // rawDate -> 2019-11-05T17:51:01+0530

                String str_date = rawDate;
                java.text.DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date date = null;
                try {
                    date = (Date) formatter.parse(str_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String timestamp = String.valueOf(date.getTime());

//        data to put in notification
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("pId", "false");

                hashMap.put(mContext.getString(R.string.field_timestamp), timestamp);

                hashMap.put("pUid", hisUid);

                hashMap.put(mContext.getString(R.string.field_notification_message), notification);
                hashMap.put(mContext.getString(R.string.field_if_seen), "false");

                hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());


                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_users));
                ref.child(hisUid).child(mContext.getString(R.string.field_Notifications)).child(timestamp).setValue(hashMap)
                        .addOnSuccessListener(aVoid -> {

                        }).addOnFailureListener(e -> {

                });
                Log.e(SNTPClient.TAG, rawDate);

            }

            @Override
            public void onError(Exception ex) {
                Log.e(SNTPClient.TAG, ex.getMessage());
            }
        });


    }

    private void deleteRequest(String contestkey, String joiningKey, String userid) {

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
        ref1.child(mContext.getString(R.string.dbname_request))
                .child(mContext.getString(R.string.dbname_participantList))
                .child(contestkey)
                .child(joiningKey)
                .removeValue();

    }
    private void bottomsheet(ParticipantList mparticipantLists, int i) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext, R.style.BottomSheetDialogTheme);
        View bottomSheetView = ((Activity)mContext).getLayoutInflater().inflate(R.layout.layout_bottom_sheet_rejection, bottomSheetDialog.findViewById(R.id.layout_bottom_sheet_container));
        EditText msg = bottomSheetView.findViewById(R.id.msg);
        TextView send = bottomSheetView.findViewById(R.id.send);
        TextView cancel = bottomSheetView.findViewById(R.id.cancel);
        send.setOnClickListener(v -> {
            String msg1 = msg.getText().toString();
            if (msg1.equals(""))
                Toast.makeText(mContext, "Write Something", Toast.LENGTH_SHORT).show();
            else {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Reject Participant");
                builder.setMessage(R.string.reject_participant_prompt);

//                set buttons
                builder.setPositiveButton("Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Rejecting: rejected ");


                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child(mContext.getString(R.string.dbname_contests))
                                .child(mparticipantLists.getUi())
                                .child(mContext.getString(R.string.joined_contest))
                                .child(mparticipantLists.getJi())
                                .child(mContext.getString(R.string.rejection_reason))
                                .setValue(msg1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                        db.child(mContext.getString(R.string.dbname_contests))
                                .child(mparticipantLists.getUi())
                                .child(mContext.getString(R.string.field_joined_updates))
                                .child(mparticipantLists.getJi())
                                .setValue("Rejected").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                db.child(mContext.getString(R.string.dbname_contests))
                                        .child(mparticipantLists.getUi())
                                        .child(mContext.getString(R.string.joined_contest))
                                        .child(mparticipantLists.getJi())
                                        .child(mContext.getString(R.string.field_status))
                                        .setValue("Rejected")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                deleteRequest(mparticipantLists.getCi(), mparticipantLists.getJi(), mparticipantLists.getUi());
                                                participantLists.remove(i);
                                                Log.d(TAG, "onSuccess: "+ mparticipantLists.getJi());

                                                AdapterParticipantRequest.this.notifyItemRemoved(i);

                                                if (mparticipantLists.getMl() == null || mparticipantLists.getMl().equals("") || checkSubstring()) {

                                                } else {
                                                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(mparticipantLists.getMl());
                                                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // File deleted successfully
                                                            Log.d(TAG, "onSuccess: deleted file");
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            // Uh-oh, an error occurred!
                                                            Log.d(TAG, "onFailure: did not delete file");
                                                        }
                                                    });
                                                }

                                                bottomSheetDialog.dismiss();

                                            }

                                            private boolean checkSubstring() {
                                                if (mparticipantLists.getMl().length()>23){
                                                    return !mparticipantLists.getMl().substring(8, 23).equals("firebasestorage");
                                                }else{
                                                    return true;
                                                }
                                            }
                                        });
                            }
                        });

                                    }
                                });


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
        });

        cancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void getparticipantform(String userid, String joiningkey, String contestkey, TextView college, LinearLayout layout, ViewHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(mContext.getString(R.string.dbname_contests))
                .child(userid)
                .child(mContext.getString(R.string.joined_contest))
                .child(joiningkey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        JoinForm joinForm = dataSnapshot.getValue(JoinForm.class);
                        holder.college1 = joinForm.getClg();
                        String hostid = joinForm.getHst();
                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
                        ref1.child(mContext.getString(R.string.dbname_contests))
                                .child(hostid)
                                .child(mContext.getString(R.string.created_contest))
                                .child(contestkey)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String openfor = dataSnapshot.child(mContext.getString(R.string.field_open_for)).getValue().toString();
                                        if (openfor.equals("All")) {
                                            layout.setVisibility(View.GONE);
                                        } else {
                                            layout.setVisibility(View.VISIBLE);
                                            college.setText(holder.college1);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public long getItemId(int position) {
        ParticipantList form = participantLists.get(position);
        return form.getJi().hashCode();
    }

    @Override
    public int getItemCount() {
        return participantLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username, time;
        private DatabaseReference mReference;
        String name1 = "", profilelink = "", username1 = "", college1 = "";

        private CircleImageView profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mReference = FirebaseDatabase.getInstance().getReference();


            profile = (CircleImageView) itemView.findViewById(R.id.profilePartCv);
            username = itemView.findViewById(R.id.username);
            time = itemView.findViewById(R.id.timeStamp);

        }
    }


    private void getparticipantDetails(String userid, TextView username, CircleImageView profile, ViewHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(mContext.getString(R.string.dbname_users))
                .child(userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        users user = dataSnapshot.getValue(users.class);
                        holder.name1 = user.getDn();
                        holder.username1 = user.getU();
                        holder. profilelink = user.getPp();
                        try {
                            Glide.with(mContext.getApplicationContext().getApplicationContext())
                                    .load(holder.profilelink)
                                    .placeholder(R.drawable.load)
                                    .error(R.drawable.default_image2)
                                    .placeholder(R.drawable.load)
                                    .thumbnail(0.5f)
                                    .into(profile);
                        } catch (NullPointerException e) {
                            Log.e(TAG, "onDataChange: " + e.getMessage());

                        }

                        username.setText(user.getU());


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}


