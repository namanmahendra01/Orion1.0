package com.orion.orion.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
Chat_Activity extends AppCompatActivity {
    private static final String TAG = "Chat_Activity";


    RecyclerView recyclerView;
    ImageView mprofileImage;
    TextView mUsername, accept, decline, sendReqBtn, cancelReqBtn;
    EditText mMessages, reqMessage;
    int x = 0;
    private int mResults;
    private LinearLayout reqLayout, chatLayout;
    ImageButton mSendBtn;

    private FirebaseAuth mAuth;
    Boolean activity = true;
    private FirebaseMethods mFirebaseMethods;
    private List<Chat> paginatedchatlist;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private DatabaseReference myRefUser;
    private FirebaseDatabase mFirebaseDatabase;

    String hisUID;
    String myUID;
    String request;
    public String newMessageKey = "";
    Context context = Chat_Activity.this;
    public String timeStamp, timestamp2;

    ValueEventListener seenListener;
    ValueEventListener recievelistener;
    DatabaseReference userRefForSeen;
    DatabaseReference DbRef;
    private Query queryr1, queryr2;
    LinearLayout sendRequestLayout;

    List<Chat> chatlist;
    List<Chat> chatList2;
    AdapterChat adapterchat1;
    AdapterChat adapterchat2;

    private RequestQueue requestQueue;


    private boolean notifyChat;

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
        reqLayout = findViewById(R.id.requestLayout);
        chatLayout = findViewById(R.id.chatLayout);


        sendRequestLayout = findViewById(R.id.pro);
        reqMessage = findViewById(R.id.msgReq);
        sendReqBtn = findViewById(R.id.sendReq);
        cancelReqBtn = findViewById(R.id.cancelReq);


        mSendBtn = findViewById(R.id.sendBtn);
        mMessages = findViewById(R.id.messageEt);
        mprofileImage = findViewById(R.id.profile_image);
        recyclerView = findViewById(R.id.chat_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        mFirebaseMethods = new FirebaseMethods(Chat_Activity.this);


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

        notifyChat = false;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {

                    displayMoreChat();

                }
            }
        });

        Intent intent = getIntent();
        hisUID = intent.getStringExtra(getString(R.string.his_uid));
        request = intent.getStringExtra("request");

        DatabaseReference refer1 = FirebaseDatabase.getInstance().getReference();

        refer1.child(getString(R.string.dbname_Chats))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(hisUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            if (!request.equals("yes")) {
                                sendRequestLayout.setVisibility(View.VISIBLE);

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });

        cancelReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequestMessage(reqMessage.getText().toString());
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
        if (request.equals("yes")) {
            reqLayout.setVisibility(View.VISIBLE);
            chatLayout.setVisibility(View.GONE);
            readRequestMessage();
        } else {
            reqLayout.setVisibility(View.GONE);
            chatLayout.setVisibility(View.VISIBLE);
            readMessage();

        }

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Decline");
                builder.setMessage("Are you sure, you want to Decline this message?");

//                set buttons
                builder.setPositiveButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Declining: ");

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child(context.getString(R.string.dbname_request))
                                .child(context.getString(R.string.dbname_Chats))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(hisUID)
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                            }
                        });


                    }
                });


                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child(context.getString(R.string.dbname_request))
                        .child(context.getString(R.string.dbname_Chats))
                        .child(hisUID)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .removeValue();


                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();


            }
        });
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Accept");
                builder.setMessage("Are you sure, you want to Accept this message?");

//                set buttons
                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Accepting: ");


                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        String key = ref.child(getString(R.string.dbname_users)).push().getKey();
                        ref.child(context.getString(R.string.dbname_request))
                                .child(context.getString(R.string.dbname_Chats))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(hisUID)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        ArrayList<Chat> chat1 = new ArrayList<>((int) dataSnapshot.getChildrenCount());

                                        int x = 0;
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                            x++;
                                            Chat chat = ds.getValue(Chat.class);
                                            chat1.add(chat);
                                            if (x == dataSnapshot.getChildrenCount()) {
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                                                int i = 0;
                                                for (Chat c : chat1) {
                                                    i++;
                                                    int finalI = i;
                                                    ref.child(context.getString(R.string.dbname_ChatList))
                                                            .child(key)
                                                            .child(c.getMid())
                                                            .setValue(c)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    if (finalI == chat1.size()) {

                                                                        ref.child(getString(R.string.dbname_Chats))
                                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                                .child(hisUID)
                                                                                .setValue(key);

                                                                        ref.child(getString(R.string.dbname_Chats))
                                                                                .child(hisUID)
                                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                                .setValue(key)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {

                                                                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                                                                            ref.child(context.getString(R.string.dbname_request))
                                                                                                    .child(context.getString(R.string.dbname_Chats))
                                                                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                                                    .child(hisUID)
                                                                                                    .removeValue()
                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                            finish();
                                                                                                        }
                                                                                                    });

                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });

                                                }
                                            }


                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }

                                });


                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();


            }
        });
        seenMessage();
    }

    private void sendRequestMessage(String message) {
        if (message == null || message.equals("")) {
            Toast.makeText(context, "Type something Please!", Toast.LENGTH_SHORT).show();
        } else {


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
                    newMessageKey = refer1.child(getString(R.string.dbname_Chats)).push().getKey();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(getString(R.string.field_sender_ID), FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put(getString(R.string.field_receiver_ID), hisUID);
                    hashMap.put(getString(R.string.field_message), message);
                    hashMap.put(getString(R.string.field_timestamp), timeStamp);
                    hashMap.put(getString(R.string.field_if_seen), false);
                    hashMap.put(getString(R.string.field_message_ID), newMessageKey);

                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    db.child(getString(R.string.dbname_request))
                            .child(getString(R.string.dbname_Chats))
                            .child(hisUID)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(newMessageKey)
                            .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            reqMessage.setText("");
                            Toast.makeText(context, "Message Request Sent!", Toast.LENGTH_SHORT).show();
                            finish();

                        }
                    });


                    Log.e(SNTPClient.TAG, rawDate);

                }

                @Override
                public void onError(Exception ex) {
                    Log.e(SNTPClient.TAG, ex.getMessage());
                }
            });
        }

    }

    private void readRequestMessage() {


        chatlist = new ArrayList<>();


        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        queryr1 = DbRef.child(getString(R.string.dbname_request))
                .child(getString(R.string.dbname_Chats))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(hisUID);

        recievelistener = queryr1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatlist.clear();
                int x = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    x++;
                    Chat chat = ds.getValue(Chat.class);


                    assert chat != null;
                    chatlist.add(chat);
                    if (x == dataSnapshot.getChildrenCount()) {
                        Collections.sort(chatlist);
                        Collections.reverse(chatlist);
                        displayChat();
                    }


                }


            }
//


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //
//
    private void seenMessage() {


        userRefForSeen = FirebaseDatabase.getInstance().getReference();
        seenListener = userRefForSeen.child(getString(R.string.dbname_Chats))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(hisUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            String key = snapshot.getValue().toString();

                            DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();


                            DbRef.child(getString(R.string.dbname_ChatList))
                                    .child(key)
                                    .orderByChild(getString(R.string.field_if_seen)).equalTo(false)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.exists()) {
                                                int p = 0;

                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    p++;
                                                    if (ds.child(getString(R.string.field_receiver_ID))
                                                            .getValue().toString()
                                                            .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                        if (activity) {
                                                            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                                            db.child(getString(R.string.dbname_ChatList))
                                                                    .child(key)
                                                                    .child(ds.getKey())
                                                                    .child(getString(R.string.field_if_seen))
                                                                    .setValue(true)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            userRefForSeen.removeEventListener(seenListener);

                                                                        }
                                                                    });
                                                        }
                                                    }
                                                    if (p == dataSnapshot.getChildrenCount()) {

                                                    }
                                                }
                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void readMessage() {

        chatlist = new ArrayList<>();
        DatabaseReference DbRef1 = FirebaseDatabase.getInstance().getReference();
        DbRef1.child(getString(R.string.dbname_Chats))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(hisUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
                            queryr1 = DbRef.child(getString(R.string.dbname_ChatList))
                                    .child(snapshot.getValue().toString());
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

                                    long current = date.getTime();

                                    long sevenDayEarlier = current - 604800000;
                                    recievelistener = queryr1.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            chatlist.clear();
                                            long x = 0;
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                x++;
                                                Chat chat = ds.getValue(Chat.class);
                                                if (Long.parseLong(chat.getTim()) < sevenDayEarlier) {
                                                    DatabaseReference DbRef2 = FirebaseDatabase.getInstance().getReference();
                                                    DbRef2.child(getString(R.string.dbname_ChatList))
                                                            .child(snapshot.getValue().toString())
                                                            .child(chat.getMid())
                                                            .removeValue();
                                                } else {
                                                    assert chat != null;
                                                    chatlist.add(chat);
                                                    if (x == 10) {
                                                        Collections.sort(chatlist);

                                                        Collections.reverse(chatlist);

                                                        displayChat();
                                                    } else if (x == dataSnapshot.getChildrenCount()) {
                                                        Collections.sort(chatlist);

                                                        Collections.reverse(chatlist);

                                                        displayChat();
                                                    }
                                                }
                                            }


                                        }
//


                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    Log.e(SNTPClient.TAG, rawDate);

                                }

                                @Override
                                public void onError(Exception ex) {
                                    Log.e(SNTPClient.TAG, ex.getMessage());
                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void sendMessage(String message) {
        notifyChat=true;
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
                newMessageKey = refer1.child(getString(R.string.dbname_Chats)).push().getKey();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(getString(R.string.field_sender_ID), FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put(getString(R.string.field_receiver_ID), hisUID);
                hashMap.put(getString(R.string.field_message), message);
                hashMap.put(getString(R.string.field_timestamp), timeStamp);
                hashMap.put(getString(R.string.field_if_seen), false);
                hashMap.put(getString(R.string.field_message_ID), newMessageKey);
                refer1.child(getString(R.string.dbname_Chats))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(hisUID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String key = snapshot.getValue().toString();


                                    DatabaseReference refer = FirebaseDatabase.getInstance().getReference();
                                    refer.child(getString(R.string.dbname_ChatList))
                                            .child(key)
                                            .child(newMessageKey)
                                            .setValue(hashMap);

                                    mMessages.setText("");



                                } else {
                                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                    db.child(getString(R.string.dbname_request))
                                            .child(getString(R.string.dbname_Chats))
                                            .child(hisUID)
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(newMessageKey)
                                            .setValue(hashMap);

                                    mMessages.setText("");
                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


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
                adapterchat1 = new AdapterChat(Chat_Activity.this, paginatedchatlist);
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
        Query query = myRef.child(getString(R.string.dbname_users)).child(hisUID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot singleSnapshot) {

                Glide.with(getApplicationContext())
                        .load(singleSnapshot.child(getString(R.string.profile_photo)).getValue().toString())
                        .placeholder(R.drawable.load)
                        .error(R.drawable.default_image2)
                        .placeholder(R.drawable.load)
                        .thumbnail(0.2f)
                        .into(mprofileImage);

                mUsername.setText(singleSnapshot.child(getString(R.string.field_username)).getValue().toString());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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
        userRefForSeen.removeEventListener(seenListener);
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
        userRefForSeen.removeEventListener(seenListener);
        if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
        if(notifyChat){
            final DatabaseReference data = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(getString(R.string.field_username));
            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String user = dataSnapshot.getValue().toString();
                    mFirebaseMethods.sendNotification(hisUID, user, getString(R.string.chat_message), getString(R.string.message_string));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
