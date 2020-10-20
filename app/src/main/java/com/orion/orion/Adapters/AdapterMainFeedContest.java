package com.orion.orion.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.orion.orion.R;
import com.orion.orion.contest.ViewContestDetails;
import com.orion.orion.contest.joined.JoiningForm;
import com.orion.orion.contest.joined.joined_contest_overview_activity;
import com.orion.orion.models.ContestDetail;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.JoinForm;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.StringManipilation;
import com.orion.orion.util.UniversalImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.android.volley.VolleyLog.TAG;

public class AdapterMainFeedContest extends RecyclerView.Adapter<AdapterMainFeedContest.ViewHolder> {
    private String mAppend = "";
    //    SP
    Gson gson;
    SharedPreferences sp;

    String timestamp = "";


    private Context mContext;
    private List<ContestDetail> contestDetails;

    public AdapterMainFeedContest(Context mContext, List<ContestDetail> contestDetails) {
        this.mContext = mContext;
        this.contestDetails = contestDetails;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_contest_item_small, parent, false);
        return new AdapterMainFeedContest.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        ContestDetail contestDetail = contestDetails.get(i);


//          Initialize SharedPreference variables
        sp = mContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        gson = new Gson();

        Log.d(TAG, "dddd: 1"+contestDetails.size()+contestDetail.getResult());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contestlist));
                ref.child(contestDetail.getContestId())
                .child("result")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                if (snapshot.getValue().toString().equals("true")) {
                                    contestDetails.remove(contestDetail);

//                Add newly Created ArrayList to Shared Preferences
                                    SharedPreferences.Editor editor = sp.edit();
                                    String json = gson.toJson(contestDetails);
                                    editor.putString("cl", json);
                                    editor.apply();
                                    Log.d(TAG, "dddd: 2" + contestDetails.size());

                                    AdapterMainFeedContest.this.notifyDataSetChanged();

                                }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        Log.d(TAG, "dddd: kkl"+contestDetails.size());


        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Colombo"), new SNTPClient.Listener() {
            @Override
            public void onTimeReceived(String rawDate) {
                // rawDate -> 2019-11-05T17:51:01+0530

                //*************************************************************************
                String currentTime = StringManipilation.getTime(rawDate);
                java.text.DateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy");
                Date date1 = null;
                try {
                    date1 = (Date) formatter1.parse(currentTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                timestamp = String.valueOf(date1.getTime());

                Log.d(TAG, "onTimeReceived: 1  " + timestamp);


                String regStart = contestDetail.getRegBegin();
                java.text.DateFormat formatter2 = new SimpleDateFormat("dd-MM-yyyy");
                Date date2 = null;
                try {
                    date2 = (Date) formatter2.parse(regStart);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String regS = String.valueOf(date2.getTime());
                //*************************************************************************

                    String voteStart = contestDetail.getVoteBegin();
                    java.text.DateFormat formatter3 = new SimpleDateFormat("dd-MM-yyyy");
                    Date date3 = null;
                    if (!voteStart.equals("-")) {

                        try {
                            date3 = (Date) formatter3.parse(voteStart);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        holder. voteS = String.valueOf(date3.getTime());
                    }





                //*************************************************************************

                String voteEnd = contestDetail.getVoteEnd();
                java.text.DateFormat formatter4 = new SimpleDateFormat("dd-MM-yyyy");
                Date date4 = null;
                if (!voteEnd.equals("-")) {
                    try {
                        date4 = (Date) formatter4.parse(voteEnd);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    holder. voteE = String.valueOf(date4.getTime());
                }


                //*************************************************************************

                String regEnd = contestDetail.getRegEnd();
                java.text.DateFormat formatter5 = new SimpleDateFormat("dd-MM-yyyy");
                Date date5 = null;
                try {
                    date5 = (Date) formatter5.parse(regEnd);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String regE = String.valueOf(date5.getTime());

                //*************************************************************************

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                wait(1000);

                                ((FragmentActivity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


//                compare all dates

                                        Log.d(TAG, "run: timeeeee" + timestamp);
                                        {

                                            if(Long.parseLong(regS) <= Long.parseLong(timestamp)&&Long.parseLong(regE) >= Long.parseLong(timestamp)) {
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_participantList));
                                                ref.child(contestDetail.getContestId())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                long i = dataSnapshot.getChildrenCount();
                                                                if (!contestDetail.getMaxLimit().equals("Unlimited")) {
                                                                    if (!String.valueOf(i).equals(contestDetail.getMaxLimit())) {

                                                                        holder.reg = "yes";
                                                                    } else {

                                                                        holder.reg = "No";
                                                                    }

                                                                } else {
                                                                    holder.reg = "yes";
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                            }
                                            if (!voteStart.equals("-")) {
                                                if (Long.parseLong(holder.voteS) <= Long.parseLong(timestamp) && Long.parseLong(holder.voteE) > Long.parseLong(timestamp)) {
                                                    holder.vote = "yes";
                                                }
                                            }


                                        }

                                    }
                                });

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                    ;
                };
                thread.start();
                //*************************************************************************


                Log.e(SNTPClient.TAG, rawDate);

            }


            @Override
            public void onError(Exception ex) {
                Log.e(SNTPClient.TAG, ex.getMessage());
            }
        });

//


        getcontestDetails(contestDetail.getUserId(), contestDetail.getContestId(), holder.poster
                , holder.title, holder.domain);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext.getApplicationContext(), ViewContestDetails.class);
                i.putExtra("userId", contestDetail.getUserId());
                i.putExtra("contestId", contestDetail.getContestId());
                i.putExtra("Vote", holder.vote);
                i.putExtra("reg", holder.reg);
                mContext.startActivity(i);
            }
        });


    }
    public long getItemId(int position) {
        ContestDetail form = contestDetails.get(position);
        return form.getContestId().hashCode();
    }
    @Override
    public int getItemCount() {
        return contestDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView domain, title;
        private ImageView poster;
        String vote = "No";
        String reg = "No";
        String voteS="";
        String voteE="";

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            domain = itemView.findViewById(R.id.domain);
            title = itemView.findViewById(R.id.title);

            poster = itemView.findViewById(R.id.poster);

        }
    }

    private void getcontestDetails(String userId, String contestId, ImageView poster, TextView title, TextView domain) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(mContext.getString(R.string.dbname_contests))
                .child(userId)
                .child(mContext.getString(R.string.created_contest))
                .child(contestId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    CreateForm createForm = dataSnapshot.getValue(CreateForm.class);
                    title.setText(createForm.getTitle());
                    domain.setText(createForm.getDomain().toString());

                    UniversalImageLoader.setImage(createForm.getPoster(), poster, null, mAppend);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
