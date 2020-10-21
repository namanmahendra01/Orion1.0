package com.orion.orion.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.renderscript.Sampler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orion.orion.CommentActivity;
import com.orion.orion.R;
import com.orion.orion.contest.Contest_Evaluation.activity_view_media;
import com.orion.orion.models.JoinForm;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.UniversalImageLoader;

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
    String name1 = "", profilelink = "", username1 = "", college1 = "";

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

        String timeStamp = mparticipantLists.getTimestamp();
        Log.d(TAG, "onBindViewHolder: " + mparticipantLists.getTimestamp());

//        convert time stamp to date and time
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        holder.time.setText(dateTime);


        getparticipantDetails(mparticipantLists.getUserid(), holder.username, holder.displayname, holder.profile, null, null, null);

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


                getparticipantDetails(mparticipantLists.getUserid(), holder.username, holder.displayname, holder.profile, name, username, profileview);
                name.setText(name1);
                username.setText(username1);
                UniversalImageLoader.setImage(profilelink, profileview, null, mAppend);
                getparticipantform(mparticipantLists.getUserid(), mparticipantLists.getJoiningKey(), mparticipantLists.getContestkey(), college, layout);


                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(mContext, profile.class);
                        i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                        i.putExtra(mContext.getString(R.string.intent_user), mparticipantLists.getUserid());
                        mContext.startActivity(i);
                    }
                });
                username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(mContext, profile.class);
                        i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                        i.putExtra(mContext.getString(R.string.intent_user),mparticipantLists.getUserid());
                        mContext.startActivity(i);
                    }
                });
                profileview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(mContext, profile.class);
                        i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                        i.putExtra(mContext.getString(R.string.intent_user),mparticipantLists.getUserid());
                        mContext.startActivity(i);
                    }
                });
                submission.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mparticipantLists.getMediaLink() != null && !mparticipantLists.getMediaLink().equals("")) {

                            if (mparticipantLists.getMediaLink().substring(8, 23).equals("firebasestorage")) {
                                Intent i = new Intent(mContext.getApplicationContext(), activity_view_media.class);
                                i.putExtra("imageLink", mparticipantLists.getMediaLink());
                                i.putExtra("view", "No");

                                mContext.startActivity(i);
                            } else {
                                Uri uri = Uri.parse(mparticipantLists.getMediaLink());
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                mContext.startActivity(intent);
                            }

                        }
                    }
                });
                idcard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext.getApplicationContext(), activity_view_media.class);
                        i.putExtra("imageLink", mparticipantLists.getIdLink());
                        i.putExtra("view", "No");

                        mContext.startActivity(i);

                    }
                });
                reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Reject Participant");
                        builder.setMessage("Are you sure, you want to Reject this Participant?");

//                set buttons
                        builder.setPositiveButton("Reject", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "Rejecting: rejected ");

                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                db.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("Joinedupdates")
                                        .child(mparticipantLists.getJoiningKey())
                                        .setValue("Rejected");
                                db.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.joined_contest))
                                        .child(mparticipantLists.getJoiningKey())
                                        .child("status")
                                        .setValue("Rejected");

                                deleteRequest(mparticipantLists.getContestkey(), mparticipantLists.getJoiningKey(), mparticipantLists.getUserid());
                                participantLists.remove(i);

                                AdapterParticipantRequest.this.notifyItemRemoved(i);

                                if (mparticipantLists.getMediaLink() == null || mparticipantLists.getMediaLink().equals("")||!mparticipantLists.getMediaLink().substring(8,23).equals("firebasestorage")) {

                                }else{
                                    StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(mparticipantLists.getMediaLink());
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
                allow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Allow");
                        builder.setMessage("Are you sure, you want to Allow this Participant?");

//                set buttons
                        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "DeleteMessage: deleteing message");
                                notify = true;

                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                                db.child(mContext.getString(R.string.dbname_contests))
                                        .child(mparticipantLists.getUserid())
                                        .child(mContext.getString(R.string.joined_contest))
                                        .child(mparticipantLists.getJoiningKey())
                                        .child("status")
                                        .setValue("Accepted");

                                db.child(mContext.getString(R.string.dbname_contests))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("Joinedupdates")
                                        .child(mparticipantLists.getJoiningKey())
                                        .setValue("Accepted");

                                db.child(mContext.getString(R.string.dbname_contestlist))
                                        .child(mparticipantLists.getContestkey())
                                        .child("participantlist")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(true);

                                db.child(mContext.getString(R.string.dbname_users))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.changedJoinedContest))
                                        .setValue("true");

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.child(mContext.getString(R.string.dbname_request))
                                        .child(mContext.getString(R.string.dbname_participantList))
                                        .child(mparticipantLists.getContestkey())
                                        .child(mparticipantLists.getJoiningKey())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                ParticipantList participantList = dataSnapshot.getValue(ParticipantList.class);

                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                                ref.child(mContext.getString(R.string.dbname_participantList))
                                                        .child(mparticipantLists.getContestkey())
                                                        .child(mparticipantLists.getJoiningKey())
                                                        .setValue(participantList)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    HashMap<String, Object> hashMap3 = new HashMap<>();
                                                                    hashMap3.put("jury1", "");
                                                                    hashMap3.put("jury2", "");
                                                                    hashMap3.put("jury3", "");
                                                                    hashMap3.put("jusername1", "-");
                                                                    hashMap3.put("jusername2", "-");
                                                                    hashMap3.put("jusername3", "-");
                                                                    hashMap3.put("comment1", "-");
                                                                    hashMap3.put("comment2", "-");
                                                                    hashMap3.put("comment3", "-");

                                                                    DatabaseReference db3 = FirebaseDatabase.getInstance().getReference();
                                                                    db3.child(mContext.getString(R.string.dbname_participantList))
                                                                            .child(mparticipantLists.getContestkey())
                                                                            .child(mparticipantLists.getJoiningKey())
                                                                            .child(mContext.getString(R.string.juryMarks))
                                                                            .setValue(hashMap3);
                                                                    deleteRequest(mparticipantLists.getContestkey(), mparticipantLists.getJoiningKey(), mparticipantLists.getUserid());
                                                                    bottomSheetDialog.dismiss();

                                                                    participantLists.remove(i);
                                                                    AdapterParticipantRequest.this.notifyItemRemoved(i);


                                                                }
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }

                                        });

                                final DatabaseReference data = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_users))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                data.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        users user = dataSnapshot.getValue(users.class);

                                        if (notify) {
                                            mFirebaseMethods.sendNotification(mparticipantLists.getUserid(),"", "You are now a participant.Check your ranking now.","Submission Accepted");
                                        }
                                        notify = false;

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                addToHisNotification(""+mparticipantLists.getUserid(),"Submission Accepted for a contest.Check on Contests.");

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
                Log.d(TAG, "onCreateView: timestampyesss" + date.getTime());
                String timestamp = String.valueOf(date.getTime());


                //data to put in notification
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("pId", "false");
                hashMap.put("timeStamp", timestamp);
                hashMap.put("pUid", hisUid);
                hashMap.put("seen","false");
                hashMap.put("notificaton", notification);
                hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                ref.child(hisUid).child("Notifications").child(timestamp).setValue(hashMap)
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

    private void getparticipantform(String userid, String joiningkey, String contestkey, TextView college, LinearLayout layout) {
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference();
        ref.child(mContext.getString(R.string.dbname_contests))
                .child(userid)
                .child(mContext.getString(R.string.joined_contest))
                .child(joiningkey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        JoinForm joinForm = dataSnapshot.getValue(JoinForm.class);
                        college1=joinForm.getCollege();
                        String hostid=joinForm.getHostId();
                        DatabaseReference ref1 =FirebaseDatabase.getInstance().getReference();
                        ref1.child(mContext.getString(R.string.dbname_contests))
                                .child(hostid)
                                .child(mContext.getString(R.string.created_contest))
                                .child(contestkey)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String openfor = dataSnapshot.child("openFor").getValue().toString();
                                        if (openfor.equals("All")){
                                            layout.setVisibility(View.GONE);
                                        }else{
                                            layout.setVisibility(View.VISIBLE);
                                            college.setText(college1);
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
        return form.getJoiningKey().hashCode();
    }
    @Override
    public int getItemCount() {
        return participantLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username, displayname, time;
        private DatabaseReference mReference;

        private CircleImageView profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mReference = FirebaseDatabase.getInstance().getReference();


            profile=(CircleImageView)itemView.findViewById(R.id.profilePartCv);
            username=itemView.findViewById(R.id.username);
            displayname=itemView.findViewById(R.id.displayname);
            time=itemView.findViewById(R.id.timeStamp);

        }
    }


    private void getparticipantDetails(String userid, TextView username, TextView displayname, CircleImageView profile,TextView name,TextView username2,CircleImageView profileview) {
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference();
        ref.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        users user =dataSnapshot.getValue(users.class);
                        Log.d(TAG, "onDataChange: "+ user.getDisplay_name());
                        name1=user.getDisplay_name();
                        username1=user.getUsername();
                        profilelink=user.getProfile_photo();
                        try {
                            name.setText(name1);
                            username2.setText(username1);
                            UniversalImageLoader.setImage(profilelink,profileview,null,mAppend);

                        }catch (NullPointerException e){
                            Log.e(TAG, "onDataChange: "+e.getMessage() );

                        }

                        username.setText(user.getUsername());
                        displayname.setText(user.getDisplay_name());
                        UniversalImageLoader.setImage(profilelink,profile,null,mAppend);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    }


