package com.orion.orion.home;

import android.content.Intent;
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
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.Adapters.AdapterMainfeed;
import com.orion.orion.Adapters.AdapterNotification2;
import com.orion.orion.R;
import com.orion.orion.contest.create.CheckContest;
import com.orion.orion.contest.create.form;
import com.orion.orion.models.Notification;
import com.orion.orion.models.Photo;

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
    public notificationFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification1,container,false);
        notificationRv=view.findViewById(R.id.recycler_view);
        clearNotification=view.findViewById(R.id.clearNotification);
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
        readNotification();
        clearNotification.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            builder.setMessage("Uou want to delete all the notification")
                    .setTitle("Are u sure?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        notifyList.clear();
                        paginatedNotifications.clear();
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
        Log.d(TAG, "onCreateView: aja krle");

        return view;
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
                    if(x==10){
                        Log.d(TAG, "onDataChange: display 10 notification");
                        displayNotification();
                    }
                }
                Collections.reverse(notifyList);
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
                Log.d(TAG, "displayNotification: sss" + paginatedNotifications.size());
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
