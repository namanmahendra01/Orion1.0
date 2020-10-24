package com.orion.orion.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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
import com.orion.orion.Adapters.AdapterMessageRequest;
import com.orion.orion.Adapters.AdaterChatList;
import com.orion.orion.R;
import com.orion.orion.models.Chat;
import com.orion.orion.models.users;
import com.orion.orion.util.FirebaseMethods;

import java.util.ArrayList;
import java.util.List;

import static com.orion.orion.util.SNTPClient.TAG;

public class Message_Request extends AppCompatActivity {

    private static final String TAG = "Message_Request";
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private DatabaseReference myRef2;
    private FirebaseDatabase mFirebaseDatabase;
    FirebaseUser currentUser;

    ImageView backArrow;
    private Context mcontext;

    RecyclerView recyclerView;
    List<Chat> chatList;
    List<String> userlist;
    List<users> accountlist;
    AdapterMessageRequest adapterMessageRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message__request);


        recyclerView = findViewById(R.id.recyclerview);
        backArrow = findViewById(R.id.backarrow);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        chatList = new ArrayList<>();

        loadChats();
    }
    private void loadChats() {
        userlist = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_request))
                .child(getString(R.string.dbname_Chats))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userlist.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String user = ds.getKey();
                    userlist.add(user);
                    Log.d(TAG, "getItemId: lol1"+userlist);

                }

                adapterMessageRequest = new AdapterMessageRequest( Message_Request.this,userlist);
                adapterMessageRequest.setHasStableIds(true);
                recyclerView.setAdapter(adapterMessageRequest);
                for (int i = 0; i < userlist.size(); i++) {
                    lastMessage(userlist.get(i));
                    Log.d(TAG, "getItemId: lol2");

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lastMessage(String uid) {

        DatabaseReference refer = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_request)).
                child(getString(R.string.dbname_Chats)).
                child(currentUser.getUid()).
                child(uid);
        refer .orderByKey()
                .limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lmsg = "default";
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.exists()) {
                        Chat chat = ds.getValue(Chat.class);
                        lmsg = chat.getMessage();

                    }
                }
                adapterMessageRequest.setLastMessage(uid, lmsg);
                Log.d(TAG, "getItemId: lol3");

                adapterMessageRequest.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
