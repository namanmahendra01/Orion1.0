package com.orion.orion.Adapters;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orion.orion.R;
import com.orion.orion.contest.Contest_Evaluation.activity_view_media;
import com.orion.orion.models.JoinForm;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.UniversalImageLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.android.volley.VolleyLog.TAG;

public class AdapterParticipantList extends RecyclerView.Adapter<AdapterParticipantList.ViewHolder> {
    private String mAppend = "";
    //    SP
    Gson gson;
    SharedPreferences sp;
    private Context mContext;
    private List<ParticipantList> participantLists;
    String name1 = "", profilelink = "", username1 = "", idLink = "", mediaLink = "", comment = "", college1 = "";

    public AdapterParticipantList(Context mContext, List<ParticipantList> participantLists) {
        this.mContext = mContext;
        this.participantLists = participantLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.contest_participant_item, parent, false);
        return new AdapterParticipantList.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        ParticipantList mparticipantLists = participantLists.get(i);
//          Initialize SharedPreference variables
        sp = mContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        gson = new Gson();

        String timeStamp = mparticipantLists.getTimestamp();
        Log.d(TAG, "onBindViewHolder: " + mparticipantLists.getTimestamp());

//        convert time stamp to date and time
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        holder.time.setText(dateTime);


        getparticipantDetails(mparticipantLists.getUserid(), holder.username, holder.profile, null, null, null);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext, R.style.BottomSheetDialogTheme);

                View bottomSheetView = ((FragmentActivity) mContext).getLayoutInflater()
                        .inflate(R.layout.layout_bottom_sheet, (LinearLayout) bottomSheetDialog.findViewById(R.id.layout_bottom_sheet_container));
                TextView name = bottomSheetView.findViewById(R.id.displaynameBs);
                TextView remove = bottomSheetView.findViewById(R.id.remove);

                TextView username = bottomSheetView.findViewById(R.id.usernameBs);
                TextView college = bottomSheetView.findViewById(R.id.collegeBs);
                TextView idcard = bottomSheetView.findViewById(R.id.idBs);
                TextView submission = bottomSheetView.findViewById(R.id.submissionBs);
                LinearLayout layout = bottomSheetView.findViewById(R.id.collegeLinear);
                CircleImageView profileview = bottomSheetView.findViewById(R.id.profileBs);

                getparticipantDetails(mparticipantLists.getUserid(), holder.username, holder.profile, name, username, profileview);
                name.setText(name1);
                username.setText(username1);
                UniversalImageLoader.setImage(profilelink, profileview, null, mAppend);
                getparticipantform(mparticipantLists.getUserid(), mparticipantLists.getJoiningKey(), mparticipantLists.getContestkey(), college, layout);

                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Remove Participant");
                        builder.setMessage("Are you sure, you want to remove this Participant?");

//                set buttons
                        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "onClick: mnb 1");

                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                                db.child(mContext.getString(R.string.dbname_contests))
                                        .child(mparticipantLists.getUserid())
                                        .child(mContext.getString(R.string.joined_contest))
                                        .child(mparticipantLists.getJoiningKey())
                                        .child("status")
                                        .setValue("Rejected");

                                db.child(mContext.getString(R.string.dbname_contests))
                                        .child(mparticipantLists.getUserid())
                                        .child("Joinedupdates")
                                        .child(mparticipantLists.getJoiningKey())
                                        .setValue("Rejected");

                                db.child(mContext.getString(R.string.dbname_contestlist))
                                        .child(mparticipantLists.getContestkey())
                                        .child("participantlist")
                                        .child(mparticipantLists.getUserid())
                                        .removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            if (mparticipantLists.getMediaLink().length()<23)
                                            {
                                                deleteUrl(dialog);
                                            }
                                           else if (mparticipantLists.getMediaLink() == null || mparticipantLists.getMediaLink().equals("")
                                                    || !mparticipantLists.getMediaLink().substring(8, 23).equals("firebasestorage")) {

                                               deleteUrl(dialog);


                                            } else {

                                               deleteImage(dialog);

                                            }

                                        });


                            }

                            private void deleteImage(DialogInterface dialog) {
                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(mparticipantLists.getMediaLink());
                                photoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference();
                                        ref2.child(mContext.getString(R.string.dbname_participantList))
                                                .child(mparticipantLists.getContestkey())
                                                .child(mparticipantLists.getJoiningKey())
                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //    Add newly Created ArrayList to Shared Preferences
                                                String json = sp.getString(mparticipantLists.getContestkey(), null);

                                                Type type = new TypeToken<ArrayList<ParticipantList>>() {
                                                }.getType();
                                                ArrayList<ParticipantList> participantList=new ArrayList<>();

                                                participantList = gson.fromJson(json, type);

                                                if (participantList==null){

                                                }else{
                                                    ArrayList<ParticipantList> participantList2=new ArrayList<>(participantList);


                                                    Log.d(TAG, "onSuccess: lklk"+participantList2.size());
                                                    for (ParticipantList a:participantList){
                                                        if (a.getJoiningKey().equals(mparticipantLists.getJoiningKey())){
                                                            participantList2.remove(a);
                                                        }
                                                    }
                                                    Log.d(TAG, "onSuccess: lklk 2"+participantList2.size());

                                                    participantList.remove(mparticipantLists);

                                                    SharedPreferences.Editor editor = sp.edit();
                                                    json = gson.toJson(participantList2);
                                                    editor.putString(mparticipantLists.getContestkey(), json);
                                                    editor.apply();

                                                }


                                                participantLists.remove(participantLists.get(i));

                                                AdapterParticipantList.this.notifyItemRemoved(i);


                                                db.child(mContext.getString(R.string.dbname_users))
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child(mContext.getString(R.string.changedJoinedContest))
                                                        .setValue("true");


                                                dialog.dismiss();
                                            }
                                        });

                                    }
                                });
                            }

                            private void deleteUrl(DialogInterface dialog) {
                                DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                                //    Add newly Created ArrayList to Shared Preferences
                                String json = sp.getString(mparticipantLists.getContestkey(), null);

                                Type type = new TypeToken<ArrayList<ParticipantList>>() {
                                }.getType();
                                ArrayList<ParticipantList> participantList=new ArrayList<>();

                                participantList = gson.fromJson(json, type);

                                if (participantList==null){

                                }else{
                                    ArrayList<ParticipantList> participantList2=new ArrayList<>(participantList);

                                    Log.d(TAG, "onSuccess: lklk 8"+participantList2.size());

                                    for (ParticipantList a:participantList){
                                        Log.d(TAG, "onSuccess: lklk ioi");

                                        if (a.getJoiningKey().equals(participantLists.get(i).getJoiningKey())){
                                            Log.d(TAG, "onSuccess: lklk yes");
                                            participantList2.remove(a);
                                        }
                                    }
                                    Log.d(TAG, "onSuccess: lklk 9"+participantList2.size());

                                    participantList.remove(participantLists.get(i));

                                    SharedPreferences.Editor editor = sp.edit();
                                    json = gson.toJson(participantList2);
                                    editor.putString(mparticipantLists.getContestkey(), json);
                                    editor.apply();

                                }


                                participantLists.remove(participantLists.get(i));

                                AdapterParticipantList.this.notifyItemRemoved(i);

                                db.child(mContext.getString(R.string.dbname_users))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(mContext.getString(R.string.changedJoinedContest))
                                        .setValue("true");
                                dialog.dismiss();
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


                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(mContext, profile.class);
                        i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                        i.putExtra(mContext.getString(R.string.intent_user),mparticipantLists.getUserid());
                        mContext.startActivity(i);
                    }
                });
                username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(mContext, profile.class);
                        i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                        i.putExtra(mContext.getString(R.string.intent_user), mparticipantLists.getUserid());
                        mContext.startActivity(i);
                    }
                });
                profileview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(mContext, profile.class);
                        i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                        i.putExtra(mContext.getString(R.string.intent_user), mparticipantLists.getUserid());
                        mContext.startActivity(i);
                    }
                });
                submission.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean ok=mparticipantLists.getMediaLink().length()>23;
                        boolean ifNull=mparticipantLists.getMediaLink() != null && !mparticipantLists.getMediaLink().equals("");
                        if (ifNull){
                            Toast.makeText(mContext, "Invalid Link", Toast.LENGTH_SHORT).show();

                        }else if (ok) {
                            if (mparticipantLists.getMediaLink().substring(8, 23).equals("firebasestorage")) {
                                Intent i = new Intent(mContext.getApplicationContext(), activity_view_media.class);
                                i.putExtra("imageLink", mparticipantLists.getMediaLink());
                                i.putExtra("view", "No");

                                mContext.startActivity(i);
                            } else {
                                try{
                                    Uri uri = Uri.parse(mparticipantLists.getMediaLink());
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    mContext.startActivity(intent);

                                }catch (ActivityNotFoundException e){
                                    Toast.makeText(mContext, "Invalid Link", Toast.LENGTH_SHORT).show();
                                    Log.e(SNTPClient.TAG, "onClick: "+ e.getMessage());
                                }
                            }
                        }else {
                            try{
                                Uri uri = Uri.parse(mparticipantLists.getMediaLink());
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                mContext.startActivity(intent);

                            }catch (ActivityNotFoundException e){
                                Toast.makeText(mContext, "Invalid Link", Toast.LENGTH_SHORT).show();
                                Log.e(SNTPClient.TAG, "onClick: "+ e.getMessage());
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

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

    }

    private void getparticipantform(String userid, String joiningkey, String contestkey, TextView college, LinearLayout layout) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(mContext.getString(R.string.dbname_contests))
                .child(userid)
                .child(mContext.getString(R.string.joined_contest))
                .child(joiningkey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        JoinForm joinForm = dataSnapshot.getValue(JoinForm.class);
                        college1 = joinForm.getCollege();
                        String hostid = joinForm.getHostId();
                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
                        ref1.child(mContext.getString(R.string.dbname_contests))
                                .child(hostid)
                                .child(mContext.getString(R.string.created_contest))
                                .child(contestkey)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String openfor = dataSnapshot.child("openFor").getValue().toString();
                                        if (openfor.equals("All")) {
                                            layout.setVisibility(View.GONE);
                                        } else {
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

        private TextView username, time;
        private DatabaseReference mReference;

        private CircleImageView profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mReference = FirebaseDatabase.getInstance().getReference();


            profile = (CircleImageView) itemView.findViewById(R.id.profilePartCv);
            username = itemView.findViewById(R.id.username);
            time = itemView.findViewById(R.id.timeStamp);

        }
    }


    private void getparticipantDetails(String userid, TextView username, CircleImageView profile, TextView name, TextView username2, CircleImageView profileview) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        users user = dataSnapshot.getValue(users.class);
                        Log.d(TAG, "onDataChange: " + user.getDisplay_name());
                        name1 = user.getDisplay_name();
                        username1 = user.getUsername();
                        profilelink = user.getProfile_photo();
                        try {
                            name.setText(name1);
                            username2.setText(username1);
                            UniversalImageLoader.setImage(profilelink, profileview, null, mAppend);

                        } catch (NullPointerException e) {
                            Log.e(TAG, "onDataChange: " + e.getMessage());

                        }

                        username.setText(user.getUsername());
                        UniversalImageLoader.setImage(profilelink, profile, null, mAppend);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}
