package com.orion.orion.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orion.orion.Adapters.AdapterChat;

import com.orion.orion.Adapters.AdapterNotification2;
import com.orion.orion.Notifications.Data;
import com.orion.orion.Notifications.Sender;
import com.orion.orion.Notifications.Token;
import com.orion.orion.R;
import com.orion.orion.models.Chat;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.users;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.Adapters.UserListAdapter;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.StringManipilation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static com.android.volley.VolleyLog.TAG;

public class
Chat_Activity extends AppCompatActivity {
    private static final String TAG = "Chat_Activity";


    RecyclerView recyclerView;
    ImageView mprofileImage;
    TextView mUsername, mUserStatus, accept, decline;
    EditText mMessages;
    int x=0;
    private int mResults;
    private LinearLayout reqLayout, chatLayout;
    ImageButton mSendBtn;
    private List<users> mUserList;
    private UserListAdapter mAdapter;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;
    Boolean activity=true;
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

    List<Chat> chatlist;
    List<Chat> chatList2;
    AdapterChat adapterchat1;
    AdapterChat adapterchat2;

    private RequestQueue requestQueue;


    private boolean notify = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_);

        mUsername = (TextView) findViewById(R.id.username);
        accept = (TextView) findViewById(R.id.accept);

        decline = (TextView) findViewById(R.id.decline);
        reqLayout = (LinearLayout) findViewById(R.id.requestLayout);
        chatLayout = (LinearLayout) findViewById(R.id.chatLayout);


        mUserStatus = (TextView) findViewById(R.id.onlineStatus);
        mSendBtn = (ImageButton) findViewById(R.id.sendBtn);
        mMessages = (EditText) findViewById(R.id.messageEt);
        mprofileImage = (ImageView) findViewById(R.id.profile_image);
        recyclerView = findViewById(R.id.chat_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        mFirebaseMethods = new FirebaseMethods(Chat_Activity.this);


//        Linear layout for recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);



//        recycler properties

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        if (x!=0){
            recyclerView.smoothScrollToPosition(0);
        }

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
        hisUID = intent.getStringExtra(getString(R.string.his_UID));
        request = intent.getStringExtra("request");


        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;

                String message = mMessages.getText().toString().trim();

                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(context, "Type something idiot", Toast.LENGTH_SHORT).show();
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
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();


                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child(context.getString(R.string.dbname_request))
                                .child(context.getString(R.string.dbname_Chats))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(hisUID)
                                .removeValue();


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
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();


                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child(context.getString(R.string.dbname_request))
                                .child(context.getString(R.string.dbname_Chats))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(hisUID)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                            Chat chat = ds.getValue(Chat.class);

                                            Log.d(TAG, "onDataChange: dfdf" + chat);

                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                            ref.child(context.getString(R.string.dbname_Chats))
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child(hisUID)
                                                    .child(chat.getMessageid())
                                                    .setValue(chat)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                                                ref.child(context.getString(R.string.dbname_request))
                                                                        .child(context.getString(R.string.dbname_Chats))
                                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                        .child(hisUID)
                                                                        .removeValue();

                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }

                                });


                DatabaseReference ref1= FirebaseDatabase.getInstance().getReference();
                ref1.child(context.getString(R.string.dbname_request))
                        .child(context.getString(R.string.dbname_Chats))
                        .child(hisUID)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                    Chat chat = ds.getValue(Chat.class);
                                    Log.d(TAG, "onDataChangsse: dfdf" + chat);
                                    ref.child(context.getString(R.string.dbname_Chats))
                                            .child(hisUID)
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(chat.getMessageid())
                                            .setValue(chat)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                                        ref.child(context.getString(R.string.dbname_request))
                                                                .child(context.getString(R.string.dbname_Chats))
                                                                .child(hisUID)
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .removeValue();

                                                    }
                                                }
                                            });
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

    private void readRequestMessage() {


        chatlist = new ArrayList<>();

//

        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        queryr1 = DbRef.child(getString(R.string.dbname_request)).child(getString(R.string.dbname_Chats)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(hisUID);

        recievelistener = queryr1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatlist.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: values" + ds.getValue(Chat.class));
                    Chat chat = ds.getValue(Chat.class);


                    assert chat != null;
                    chatlist.add(chat);
                }
                    Collections.sort(chatlist);
                    Collections.reverse(chatlist);
                    displayChat();



            }
//


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                mResults = mResults + iterations;
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapterchat1.notifyDataSetChanged();
                    }
                });

            }

        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());

        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());

        }

    }


//
//
    private void seenMessage() {

        userRefForSeen = FirebaseDatabase.getInstance().getReference();
        seenListener=  userRefForSeen.child(getString(R.string.dbname_Chats)).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(hisUID)
                .orderByChild("ifseen").equalTo(false)
     .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (activity) {
                            Log.d(TAG, "onDataChange: itne" + activity);

                            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                            db.child(getString(R.string.dbname_Chats)).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(hisUID)
                                    .child(ds.getKey())
                                    .child("ifseen")
                                    .setValue(true)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            userRefForSeen.removeEventListener(seenListener);

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

    private void readMessage() {

        chatlist = new ArrayList<>();


//

        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        queryr1 = DbRef.child(getString(R.string.dbname_Chats)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(hisUID);

        recievelistener = queryr1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatlist.clear();
                long x=0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    x++;
                    Log.d(TAG, "onDataChange: values" + ds.getValue(Chat.class));
                    Chat chat = ds.getValue(Chat.class);


                    assert chat != null;
                    if (chat.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && chat.getSender().equals(hisUID) || chat.getReceiver()
                            .equals(hisUID) && chat.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        chatlist.add(chat);
                        if (x==10){
                            Log.d(TAG, "onDataChange: display forst10");
                            displayChat();

                        }
                    }
                    Collections.sort(chatlist);
                    Collections.reverse(chatlist);
                    displayChat();


                }
            }
//


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                    date = (Date) formatter.parse(str_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onCreateView: timestampyesss" + date.getTime());
                timeStamp = String.valueOf(date.getTime());


                DatabaseReference refer = FirebaseDatabase.getInstance().getReference();
                refer.child(getString(R.string.dbname_Chats))
                        .child(hisUID).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                    newMessageKey = db.child(getString(R.string.dbname_Chats)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(hisUID).push().getKey();
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap.put("receiver", hisUID);
                                    hashMap.put("message", message);
                                    hashMap.put("timestamp", timeStamp);
                                    hashMap.put("ifseen", false);
                                    hashMap.put("messageid", newMessageKey);
                                    HashMap<String, Object> hashMap1 = new HashMap<>();
                                    hashMap1.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap1.put("receiver", hisUID);
                                    hashMap1.put("message", message);
                                    hashMap1.put("timestamp", timeStamp);
                                    hashMap1.put("ifseen", true);
                                    hashMap1.put("messageid", newMessageKey);
                                    db.child(getString(R.string.dbname_Chats)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(hisUID).child(newMessageKey).setValue(hashMap1);
                                    db.child(getString(R.string.dbname_Chats)).child(hisUID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(newMessageKey).setValue(hashMap);

                                    mMessages.setText("");


                                    final DatabaseReference data = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_users))
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    data.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            users user = dataSnapshot.getValue(users.class);

                                            if (notify) {
                                                mFirebaseMethods.sendNotification(hisUID, user.getUsername(), "sent you a message.","Message");
                                            }
                                            notify = false;

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                } else {
                                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                    newMessageKey = db.child(getString(R.string.dbname_Chats)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(hisUID).push().getKey();
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap.put("receiver", hisUID);
                                    hashMap.put("message", message);
                                    hashMap.put("timestamp", timeStamp);
                                    hashMap.put("ifseen", false);
                                    hashMap.put("messageid", newMessageKey);

                                    HashMap<String, Object> hashMap1 = new HashMap<>();
                                    hashMap1.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap1.put("receiver", hisUID);
                                    hashMap1.put("message", message);
                                    hashMap1.put("timestamp", timeStamp);
                                    hashMap1.put("ifseen", true);
                                    hashMap1.put("messageid", newMessageKey);

                                    db.child(getString(R.string.dbname_request)).child(getString(R.string.dbname_Chats)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(hisUID).child(newMessageKey).setValue(hashMap1);
                                    db.child(getString(R.string.dbname_request)).child(getString(R.string.dbname_Chats)).child(hisUID).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(newMessageKey).setValue(hashMap);

                                    mMessages.setText("");
                                }
                            }

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
        Query query = myRef.child(getString(R.string.dbname_user_account_settings)).orderByChild(getString(R.string.field_user_id)).equalTo(hisUID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {


                    ImageLoader imageLoader = ImageLoader.getInstance();

                    imageLoader.displayImage(singleSnapshot.getValue(users.class).getProfile_photo(), mprofileImage);

                    mUsername.setText(singleSnapshot.getValue(users.class).getDisplay_name());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        if (x!=0){
            recyclerView.smoothScrollToPosition(0);
        }

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
        if (x!=0){
            recyclerView.smoothScrollToPosition(0);
        }
        activity=true;
    }

    @Override
    public void onStop() {
        super.onStop();
        activity=false;
        userRefForSeen.removeEventListener(seenListener);
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
