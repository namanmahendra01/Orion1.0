package com.orion.orion.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orion.orion.Adapters.AdapterMainfeed;
import com.orion.orion.Adapters.AdapterNotification2;
import com.orion.orion.R;
import com.orion.orion.contest.create.CheckContest;
import com.orion.orion.contest.create.form;
import com.orion.orion.models.Notification;
import com.orion.orion.models.Photo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class notificationFragment extends Fragment {
    private  static final String TAG ="mediafragment";
    RecyclerView notificationRv;
    private TextView clearNotification;
    private int mResults;

    private FirebaseAuth fAuth;
    private ArrayList<Notification> notifyList;
    private AdapterNotification2 adapterNotification2;
    int x=0;
    private ArrayList<Notification> paginatedNotifications;

    //    SP
    Gson gson;
    SharedPreferences sp;

    public notificationFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification1,container,false);
        notificationRv=view.findViewById(R.id.recycler_view);
        clearNotification=view.findViewById(R.id.clearNotification);

        //          Initialize SharedPreference variables
        sp = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        gson = new Gson();

        notificationRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        notificationRv.setLayoutManager(linearLayoutManager);
        notifyList=new ArrayList<>();
        notificationRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    displayMoreNotification();
                }
            }
        });

        fAuth=FirebaseAuth.getInstance();
        getNotifcationFromSP();
        clearNotification.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            builder.setMessage("Uou want to delete all the notification")
                    .setTitle("Are u sure?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        notifyList.clear();
                        paginatedNotifications.clear();
                        SharedPreferences.Editor editor = sp.edit().remove("nl");
                        editor.apply();


                        adapterNotification2.notifyDataSetChanged();
                        FirebaseUser user = fAuth.getCurrentUser();
                        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("Notifications");
                        reference.setValue(null);
                        //delete from database here then refresh for u naman
                        displayMoreNotification();
                    })
                    .setNegativeButton("No", (dialog, id) -> {
                        dialog.cancel();
                    })
                    .show();
        });


        return view;
    }
    //  fetching filtered notificationList  from SharedPreferences
    private void getNotifcationFromSP() {
        String json = sp.getString("nl", null);
        Type type = new TypeToken<ArrayList<Notification>>() {
        }.getType();
        notifyList = gson.fromJson(json, type);
        if (notifyList == null||notifyList.size()==0) {    //        if no arrayList is present

            notifyList = new ArrayList<>();
            readNotification();  //            make new Arraylist

        } else {

            checkUpdate();
        }

    }

    private void checkUpdate() {
        DatabaseReference refer = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users));
  refer.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Notifications")
                .orderByKey()
                .limitToLast(1)
          .addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  for (DataSnapshot snapshot1:snapshot.getChildren()){
                      if (notifyList.get(0).getTimeStamp().equals(snapshot1.getKey())){

                          displayNotification();
                      }else{

                          updateNotificationList();
                      }
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
    }

    private void updateNotificationList() {


        Collections.reverse(notifyList);
        DatabaseReference refer = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users));
        refer.child(FirebaseAuth.getInstance().getCurrentUser().getUid())

                .child("Notifications")
                .orderByKey()
                .startAt(notifyList.get(notifyList.size()-1).getTimeStamp())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            Notification notification=snapshot1.getValue(Notification.class);
                            notifyList.add(notification);
                        }
                        Collections.reverse(notifyList);
                        //                        Add newly Created ArrayList to Shared Preferences
                        SharedPreferences.Editor editor = sp.edit();
                        String json = gson.toJson(notifyList);
                        editor.putString("nl", json);
                        editor.apply();

                        displayNotification();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void readNotification(){
        FirebaseUser user = fAuth.getCurrentUser();
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("Notifications");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifyList.clear();
                long x=0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    x++;
                    Notification notification=snapshot.getValue(Notification.class);
                    notifyList.add(notification);

                }
                Collections.reverse(notifyList);

//                        Add newly Created ArrayList to Shared Preferences
                SharedPreferences.Editor editor = sp.edit();
                String json = gson.toJson(notifyList);
                editor.putString("nl", json);
                editor.apply();

                displayNotification();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void displayNotification() {
        Log.d(TAG, "display first 10 Notification");
        paginatedNotifications = new ArrayList<>();
        if (notifyList != null) {

            try {
                int iteration = notifyList.size();
                if (iteration > 10) {
                    iteration = 10;
                }
                mResults = 10;
                for (int i = 0; i < iteration; i++) {
                    paginatedNotifications.add(notifyList.get(i));
                }
                adapterNotification2 = new AdapterNotification2(getContext(), paginatedNotifications);
                notificationRv.setAdapter(adapterNotification2);
                x++;
            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());
            }
        }
    }

    public void displayMoreNotification() {
        Log.d(TAG, "display next 20 photo");
        try {
            if (notifyList.size() > mResults && notifyList.size() > 0) {
                int iterations;
                if (notifyList.size() > (mResults + 20)) {
                    Log.d(TAG, "display next 20 photo");
                    iterations = 20;
                } else {
                    Log.d(TAG, "display less tha 20 photo");
                    iterations = notifyList.size() - mResults;
                }
                for (int i = mResults; i < mResults + iterations; i++) {
                    paginatedNotifications.add(notifyList.get(i));
                }
                mResults = mResults + iterations;
                notificationRv.post(() -> adapterNotification2.notifyDataSetChanged());
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());
        }
    }
}
