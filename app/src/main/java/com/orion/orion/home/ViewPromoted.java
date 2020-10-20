package com.orion.orion.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.Adapters.AdapterContestJoined;
import com.orion.orion.Adapters.AdapterViewPromote;
import com.orion.orion.R;
import com.orion.orion.models.JoinForm;
import com.orion.orion.models.Promote;

import java.util.ArrayList;
import java.util.Collections;

public class ViewPromoted extends AppCompatActivity {
    RecyclerView promoteRv;
    private ArrayList<Promote> promoteList;
    ImageView back,front,cross;
    private static final String TAG = "ViewPromoted";


    private AdapterViewPromote adapterViewPromote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_promoted);

        back=findViewById(R.id.back);
        front=findViewById(R.id.front);
        cross=findViewById(R.id.cross);


        Intent i = getIntent();
        String userid= i.getStringExtra("userid");

        promoteRv=findViewById(R.id.recyclerViewPromote);
        promoteRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        promoteRv.setLayoutManager(linearLayoutManager);

        promoteList=new ArrayList<>();
        adapterViewPromote = new AdapterViewPromote(this,promoteList);
        adapterViewPromote.setHasStableIds(true);
        promoteRv.setAdapter(adapterViewPromote);

        getPromoted(userid);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayoutManager.findLastVisibleItemPosition() > 0) {
                    linearLayoutManager.scrollToPosition(linearLayoutManager.findLastVisibleItemPosition() - 1);
                }
            }
        });
        front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (linearLayoutManager.findLastVisibleItemPosition() < (adapterViewPromote.getItemCount() - 1)) {
                    linearLayoutManager.scrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + 1);
                }
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finish();

            }
        });

    }
    private void getPromoted(String userid) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child(getString(R.string.dbname_promote))
                .child(userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        promoteList.clear();
                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                            Promote promote= snapshot.getValue(Promote.class);

                            promoteList.add(promote);
                        }
                        adapterViewPromote.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
