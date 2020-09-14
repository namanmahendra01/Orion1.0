package com.orion.orion.contest.Contest_Evaluation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.Adapters.AdapterGridImageContest;
import com.orion.orion.Adapters.AdapterGridImageSub;
import com.orion.orion.R;
import com.orion.orion.models.ParticipantList;

import java.util.ArrayList;

import static com.android.volley.VolleyLog.TAG;

public class fragment_contest_media extends Fragment {
    private static final int NUM_GRID_COLUMNS = 3;

    RecyclerView gridRv;
    ArrayList<ParticipantList> imgURLsList;
    private AdapterGridImageSub adapterGridImage;

    public fragment_contest_media(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contest_media, container, false);
        Bundle b=getActivity().getIntent().getExtras();
        String Conteskey=b.getString("contestId");


        gridRv=(RecyclerView) view.findViewById(R.id.gridRv);


        gridRv.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager=new GridLayoutManager(getContext(),3);
        gridRv.setLayoutManager(linearLayoutManager);

        imgURLsList=new ArrayList<>();
        adapterGridImage = new AdapterGridImageSub(getContext(),imgURLsList);
        gridRv.setAdapter(adapterGridImage);

        setUpgridview(Conteskey);




        return view;

    }

    private void setUpgridview(String contestkey) {
        ArrayList<ParticipantList> participantLists = new ArrayList<>();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


        reference.child(getString(R.string.dbname_participantList))
                .child(contestkey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                            ParticipantList participantList= snapshot.getValue(ParticipantList.class);
                            Log.d(TAG, "onDataChange: join"+participantList.toString());

                            participantLists.add(participantList);
                        }
                        imgURLsList.addAll(participantLists);
                        gridRv.setAdapter(adapterGridImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
