package com.orion.orion.contest.Contest_Evaluation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.Adapters.AdapterChat;
import com.orion.orion.R;
import com.orion.orion.models.Chat;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.SNTPClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class
ChatRoomActivity extends AppCompatActivity {
    private static final String TAG = "Chat_Activity";

    RecyclerView recyclerView;
    ImageView mprofileImage;
    TextView mUsername, accept, decline,rel2;
    EditText mMessages;
    int x = 0;
    private int mResults;
    private LinearLayout chatLayout;
    ImageButton mSendBtn;
    ValueEventListener recievelistener;

    RelativeLayout rel1;
    private FirebaseAuth mAuth;
    Boolean activity = true;
    private FirebaseMethods mFirebaseMethods;
    private List<Chat> paginatedchatlist;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    String contestId;
    String myUID;

    public String newMessageKey = "";
    Context context = ChatRoomActivity.this;
    public String timeStamp;


    List<Chat> chatlist;
    AdapterChat adapterchat1;



    private void disableEmojiInTitle() {
        InputFilter emojiFilter = (source, start, end, dest, dstart, dend) -> {
            for (int index = start; index < end - 1; index++) {
                int type = Character.getType(source.charAt(index));
                if (type == Character.SURROGATE) return "";
            }
            return null;
        };
        mMessages.setFilters(new InputFilter[]{emojiFilter});
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mUsername = findViewById(R.id.username);
        accept = findViewById(R.id.accept);

        decline = findViewById(R.id.decline);
        chatLayout = findViewById(R.id.chatLayout);
        rel1 = findViewById(R.id.rel1);
        rel2 = findViewById(R.id.abc);

        rel1.setVisibility(View.GONE);
        rel2.setVisibility(View.VISIBLE);

        mSendBtn = findViewById(R.id.sendBtn);
        mMessages = findViewById(R.id.messageEt);
        mprofileImage = findViewById(R.id.profile_image);
        recyclerView = findViewById(R.id.chat_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mFirebaseMethods = new FirebaseMethods(ChatRoomActivity.this);


//        Linear layout for recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(20);
        recyclerView.setItemViewCacheSize(9);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        disableEmojiInTitle();

//        recycler properties

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        if (x != 0) {
            recyclerView.smoothScrollToPosition(0);
        }
        Intent intent = getIntent();
        contestId = intent.getStringExtra("ContestId");



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {

                    displayMoreChat();

                }
            }
        });



        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = mMessages.getText().toString().trim();

                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(context, "Type something please", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(message);
                }

            }
        });

        chatLayout.setVisibility(View.VISIBLE);
        readMessage();


    }



    private void readMessage() {

        chatlist = new ArrayList<>();
        DatabaseReference DbRef1 = FirebaseDatabase.getInstance().getReference();
      Query query=  DbRef1.child(getString(R.string.dbname_Participant_Chat_Room))
                .child(contestId);
        recievelistener = query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            chatlist.clear();
                            long x = 0;
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                x++;
                                Chat chat = ds.getValue(Chat.class);

                                    assert chat != null;
                                    chatlist.add(chat);
                                    if (x == 10) {
                                        Collections.sort(chatlist);

                                        Collections.reverse(chatlist);

                                        displayChat();
                                    } else if (x == snapshot.getChildrenCount()) {
                                        Collections.sort(chatlist);

                                        Collections.reverse(chatlist);

                                        displayChat();
                                    }

                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void sendMessage(String message) {
        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Colombo"), new SNTPClient.Listener() {
            @Override
            public void onTimeReceived(String rawDate) {
                // rawDate -> 2019-11-05T17:51:01+0530


                String str_date = rawDate;
                java.text.DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date date = null;
                try {
                    date = formatter.parse(str_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                timeStamp = String.valueOf(date.getTime());


                DatabaseReference refer1 = FirebaseDatabase.getInstance().getReference();
                newMessageKey = refer1.child(getString(R.string.dbname_Participant_Chat_Room)).push().getKey();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.field_sender_ID), FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put(getString(R.string.field_contest_ID), contestId);
                hashMap.put(getString(R.string.field_message), message);
                hashMap.put(getString(R.string.field_timestamp), timeStamp);
                hashMap.put(getString(R.string.field_if_seen), false);
                hashMap.put(getString(R.string.field_message_ID), newMessageKey);
                refer1.child(getString(R.string.dbname_Participant_Chat_Room))
                        .child(contestId)
                        .child(newMessageKey)
                        .setValue(hashMap);

                mMessages.setText("");


                Log.e(SNTPClient.TAG, rawDate);

            }

            @Override
            public void onError(Exception ex) {
                Log.e(SNTPClient.TAG, ex.getMessage());
            }
        });


    }

    private void displayChat() {
        Log.d(TAG, "display first 10 chat");
        x++;

        paginatedchatlist = new ArrayList<>();
        if (chatlist != null) {
            try {
                int iteration = chatlist.size();
                if (iteration > 10) {
                    iteration = 10;
                }
                mResults = 10;
                for (int i = 0; i < iteration; i++) {
                    paginatedchatlist.add(chatlist.get(i));
                }
                Log.d(TAG, "displaychat: sss" + paginatedchatlist.size());
                adapterchat1 = new AdapterChat(ChatRoomActivity.this, paginatedchatlist,"ChatRoom");
                adapterchat1.setHasStableIds(true);
//                    set adapter to recycler view
                recyclerView.setAdapter(adapterchat1);
                recyclerView.smoothScrollToPosition(0);
            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());

            }

        }
    }

    public void displayMoreChat() {
        Log.d(TAG, "display next 20 chat");
        try {
            if (chatlist.size() > mResults && chatlist.size() > 0) {

                int iterations;
                if (chatlist.size() > (mResults + 20)) {
                    Log.d(TAG, "display next 20 chat");
                    iterations = 20;
                } else {
                    Log.d(TAG, "display less tha 20 chat");
                    iterations = chatlist.size() - mResults;
                }
                for (int i = mResults; i < mResults + iterations; i++) {
                    paginatedchatlist.add(chatlist.get(i));

                }
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapterchat1.notifyItemRangeInserted(mResults, iterations);
                    }
                });
                mResults = mResults + iterations;


            }

        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());

        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());

        }

    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setup FirebaseAuth: setting up firebase auth.");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                myUID = user.getUid();

                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };



    }

    @Override
    public void onStart() {
        super.onStart();
        if (x != 0) recyclerView.smoothScrollToPosition(0);
        setupFirebaseAuth();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (x != 0) recyclerView.smoothScrollToPosition(0);
        activity = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        activity = false;
        if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);

    }

}
