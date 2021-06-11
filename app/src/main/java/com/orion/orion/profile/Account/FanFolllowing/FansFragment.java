package com.orion.orion.profile.Account.FanFolllowing;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.Adapters.AdapterFollowFanAdapter;
import com.orion.orion.R;
import com.orion.orion.models.ItemFollow;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FansFragment extends Fragment {
    private static final String TAG = "FANS_FRAGMANT";
    AdapterFollowFanAdapter adapterFollowFanAdapter;
    private DatabaseReference myRef;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noList;
    private ArrayList<ItemFollow> mLists;

    public FansFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);

        myRef = FirebaseDatabase.getInstance().getReference();
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        noList = view.findViewById(R.id.noList);
        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mLists = new ArrayList<>();
        adapterFollowFanAdapter = new AdapterFollowFanAdapter(getContext(), mLists);
        adapterFollowFanAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapterFollowFanAdapter);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.scheme2,
                R.color.purple,
                R.color.dark_orange,
                R.color.scheme5,
                R.color.scheme6,
                R.color.scheme7,
                R.color.option_background_pressed,
                R.color.scheme9,
                R.color.brown,
                R.color.yellow,
                R.color.red
        );
        fetchList();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            mLists.clear();
            noList.setVisibility(View.GONE);
            fetchList();
        });
        return view;
    }

    private void fetchList() {
        Query query = myRef.child(getString(R.string.dbname_follower)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                        ItemFollow itemFollow = new ItemFollow();
                        itemFollow.setUserId(singleSnapshot.getKey());
                        itemFollow.setFan(true);
                        Query query1 = myRef.child(getString(R.string.dbname_following)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(itemFollow.getUserId());
                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) itemFollow.setFollowing(true);
                                addToList(itemFollow);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                swipeRefreshLayout.setRefreshing(false);
                                noList.setText("LOOKS LIKE WE RAN INTO ISSUE ¯\\_(ツ)_/¯");
                                noList.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    noList.setText("LOOKS LIKE YOU HAVE NO FANS ¯\\_(ツ)_/¯");
                    noList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
                noList.setText("LOOKS LIKE WE RAN INTO ISSUE ¯\\_(ツ)_/¯");
                noList.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addToList(ItemFollow itemFollow) {
        Log.d(TAG, "addToList: started");
        Query query111 = myRef.child(getString(R.string.dbname_users)).child(itemFollow.getUserId());
        query111.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    itemFollow.setUsername((String) snapshot.child(getString(R.string.field_username)).getValue());
                    itemFollow.setDisplay_name((String) snapshot.child(getString(R.string.field_display_name)).getValue());
                    itemFollow.setProfileUrl((String) snapshot.child(getString(R.string.profile_photo)).getValue());
                    swipeRefreshLayout.setRefreshing(false);
                    mLists.add(itemFollow);
                    noList.setVisibility(View.GONE);
                    adapterFollowFanAdapter.notifyDataSetChanged();
                } else
                    FirebaseCrashlytics.getInstance().log("Failed to find data followers database");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
                FirebaseCrashlytics.getInstance().log("Failed to fetch user data from followers database" + error.getMessage());
            }
        });
    }
}
