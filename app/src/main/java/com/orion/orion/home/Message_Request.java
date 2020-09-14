package com.orion.orion.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

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
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


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
                    for (DataSnapshot ds1 : ds.getChildren()) {
                        Log.d(TAG, "onDataChange: ds" + ds1.getChildren().toString());

                        Chat chat = ds1.getValue(Chat.class);
                        Log.d(TAG, "onDataChange: chat" + chat);

                        if (chat.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            String user = ds.getKey();
                            userlist.add(user);
                            break;
                        }
                    }



                }
                adapterMessageRequest = new AdapterMessageRequest( Message_Request.this,userlist);

                recyclerView.setAdapter(adapterMessageRequest);
                for (int i = 0; i < userlist.size(); i++) {
                    lastMessage(userlist.get(i));
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lastMessage(String uid) {
        DatabaseReference refer = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_request)).child(getString(R.string.dbname_Chats)).child(currentUser.getUid()).child(uid);
        refer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lmsg = "default";
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Chat chat = ds.getValue(Chat.class);
                    if (chat == null) {
                        continue;
                    }
                    String sender = chat.getSender();
                    String reciever = chat.getReceiver();
                    if (sender == null || reciever == null) {
                        continue;
                    }
                    if (chat.getReceiver().equals(currentUser.getUid()) && chat.getSender().equals(uid)
                            || chat.getReceiver().equals(uid) && chat.getSender().equals(currentUser.getUid())) {
                        lmsg = chat.getMessage();
                    }

                }
                adapterMessageRequest.setLastMessage(uid, lmsg);
                adapterMessageRequest.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
