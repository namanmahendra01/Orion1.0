package com.orion.orion.contest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.Adapters.AdapterGridImage;
import com.orion.orion.Adapters.AdapterGridImageContest;
import com.orion.orion.R;
import com.orion.orion.contest.Contest_Evaluation.activity_view_media;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.Photo;

import java.util.ArrayList;

import static com.android.volley.VolleyLog.TAG;

public class public_voting_media extends AppCompatActivity {
    private static final int NUM_GRID_COLUMNS = 3;
    private static final int ACTIVITY_NUM = 4;
    RecyclerView gridRv;
    ImageView backArrrow;
    ArrayList<ParticipantList> imgURLsList;
    private AdapterGridImageContest adapterGridImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_voting_media);


        gridRv=(RecyclerView) findViewById(R.id.gridRv);
        backArrrow= findViewById(R.id.backarrow);

        backArrrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        gridRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        gridRv.setDrawingCacheEnabled(true);
        gridRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(20);
        gridRv.setLayoutManager(linearLayoutManager);

        imgURLsList=new ArrayList<>();


        Intent i = getIntent();
        String Conteskey = i.getStringExtra("contestId");
        setUpgridview(Conteskey);




    }

    private void setUpgridview(String contestkey) {
        ArrayList<ParticipantList> participantLists = new ArrayList<>();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child(getString(R.string.dbname_participantList))
                .child(contestkey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ParticipantList participantList = snapshot.getValue(ParticipantList.class);
                            Log.d(TAG, "onDataChange: join" + participantList.toString());

                            participantLists.add(participantList);
                        }
                       imgURLsList.addAll(participantLists);
                        boolean isImage=false;
                        if (imgURLsList.size()!=0&&imgURLsList.get(0).getMediaLink().length()>23){
                            isImage= imgURLsList.get(0).getMediaLink().substring(8,23).equals("firebasestorage");

                        }
                        adapterGridImage = new AdapterGridImageContest(public_voting_media.this,imgURLsList,isImage);
                        adapterGridImage.setHasStableIds(true);
                        gridRv.setAdapter(adapterGridImage);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
