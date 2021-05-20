package com.orion.orion.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.Adapters.AdaterChatList;
import com.orion.orion.R;
import com.orion.orion.login.LoginActivity;
import com.orion.orion.models.Chat;
import com.orion.orion.util.BottomNaavigationViewHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {


    private static final String TAG = "ChatActivity";
    private static final int ACTIVITY_NUM = 1;
    private Context mContext;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseUser mUser;
    private int mResults;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser currentUser;

    private  int x=0;
    private TextView request;
    private RecyclerView recyclerView;
    private List<Chat> chatList;
    private List<String> userlist;
    private List<String> userlist2;
    private List<String> paginateduserList;
    private AdaterChatList adaterChatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        mContext = MessagesActivity.this;
        recyclerView = findViewById(R.id.recyclerview);
        request = findViewById(R.id.msgRequest);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        setupBottomNavigationView();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE)
                    displayMoreChatList();
            }
        });
        myRef = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_request)).child(getString(R.string.dbname_Chats)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    request.setText("Requests(" + String.valueOf((int) dataSnapshot.getChildrenCount()) + ")");
                else request.setText("Requests(" + String.valueOf(0) + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        request.setOnClickListener(v -> {
            Intent i = new Intent(mContext, Message_Request.class);
            startActivity(i);
        });
        setupFirebaseAuth();
        getUserList();
    }

    private void getUserList() {
        userlist = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_Chats)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userlist.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String user = snapshot1.getValue().toString();
                    userlist.add(user);
                }
                loadChats(userlist);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadChats(List<String> userlist) {
        chatList = new ArrayList<>();
        for (int i = 0; i < userlist.size(); i++) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_ChatList));
            db.child(userlist.get(i)).orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: chat" + dataSnapshot1.getValue());
                        Chat chat = dataSnapshot1.getValue(Chat.class);
                        chatList.add(chat);
                    }
                    if (chatList.size() == userlist.size()) {
                        sortChatList(chatList, userlist);
                        Log.d(TAG, "onDataChange: chat size1" + chatList.size());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void sortChatList(List<Chat> chatList, List<String> userlist) {
        userlist2 = new ArrayList<>();
        Collections.sort(chatList, (o1, o2) -> o2.getTim().compareTo(o1.getTim()));
        for (int i = 0; i < chatList.size(); i++)
            if (chatList.get(i).getSid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                this.userlist2.add(chatList.get(i).getRid());
            else this.userlist2.add(chatList.get(i).getSid());
        adaterChatList = new AdaterChatList(mContext, this.userlist2);
        for (int i = 0; i < userlist2.size(); i++) lastMessage(userlist2.get(i));
        displayChatList();
    }


    private void lastMessage(String uid) {
        DatabaseReference refer1 = FirebaseDatabase.getInstance()
                .getReference(getString(R.string.dbname_Chats));
        refer1.child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid())
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DatabaseReference refer = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_ChatList));
                        refer.child(snapshot.getValue().toString())
                                .orderByKey()
                                .limitToLast(1)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String lmsg = "default";
                                        for (DataSnapshot ds : dataSnapshot.getChildren())
                                            if (ds.exists()) {
                                                Chat chat = ds.getValue(Chat.class);
                                                lmsg = chat.getMsg();
                                            }
                                        adaterChatList.setLastMessage(uid, lmsg);
                                        adaterChatList.notifyDataSetChanged();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void displayChatList() {
        Log.d(TAG, "display first 10 chatslist");
        paginateduserList = new ArrayList<>();
        if (userlist2 != null) {
            try {
                int iteration = userlist2.size();
                if (iteration > 10) iteration = 10;
                mResults = 10;
                for (int i = 0; i < iteration; i++) paginateduserList.add(userlist2.get(i));
                Log.d(TAG, "chatslist: sss" + paginateduserList.size());
                adaterChatList = new AdaterChatList(mContext, paginateduserList);
                adaterChatList.setHasStableIds(true);
                recyclerView.setAdapter(adaterChatList);
                x++;
            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());
            }
        }
    }

    public void displayMoreChatList() {
        Log.d(TAG, "display next 20 chatslist");
        try {
            if (userlist2.size() > mResults && userlist2.size() > 0) {
                int iterations;
                if (userlist2.size() > (mResults + 20)) {
                    Log.d(TAG, "display next 20 chatslist");
                    iterations = 20;
                } else {
                    Log.d(TAG, "display less tha 20 chatslist");
                    iterations = userlist2.size() - mResults;
                }
                for (int i = mResults; i < mResults + iterations; i++)
                    paginateduserList.add(userlist2.get(i));
                recyclerView.post(() -> adaterChatList.notifyItemRangeInserted(mResults, iterations));
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
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            mUser = firebaseAuth.getCurrentUser();
            if (mUser == null) {
                Log.d(TAG, "onAuthStateChanged:signed_out");
                Log.d(TAG, "onAuthStateChanged: navigating to login");
                SharedPreferences settings = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                new android.app.AlertDialog.Builder(mContext)
                        .setTitle("No user logon found")
                        .setMessage("We will be logging u out. \n Please try to log in again")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            settings.edit().clear().apply();
                            if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
                            startActivity(intent);
                        })
                        .show();
            } else Log.d(TAG, "onAuthStateChanged: signed_in:" + mUser.getUid());
        };
    }

    private void setupBottomNavigationView() {
        Log.d(TAG, " setupBottomNavigationView:setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.BottomNavViewBar);
        BottomNaavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx, this);
        BottomNaavigationViewHelper.enableNavigation(MessagesActivity.this, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
    }
}