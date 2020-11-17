package com.orion.orion.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.Adapters.AdaterChatList;
import com.orion.orion.R;
import com.orion.orion.models.Chat;
import com.orion.orion.models.users;
import com.orion.orion.util.FirebaseMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class messagesfragment extends Fragment {
    private static final String TAG = "messagesfragment";

    public messagesfragment() {
    }

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private DatabaseReference myRef2;
    private int mResults;
    private int x = 0;

    private FirebaseDatabase mFirebaseDatabase;
    FirebaseUser currentUser;
    TextView request;


    RecyclerView recyclerView;
    List<Chat> chatList;
    List<String> userlist;
    List<String> userlist2;
    List<String> paginateduserList;


    List<users> accountlist;
    AdaterChatList adaterChatList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        request = view.findViewById(R.id.msgRequest);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {

                    displayMoreChatList();

                }
            }
        });


        myRef = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_request))
                .child(getString(R.string.dbname_Chats))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        request.setText("Requests(" + (int) dataSnapshot.getChildrenCount() + ")");

                    } else {
                        request.setText("Requests(" + 0 + ")");
                    }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Message_Request.class);
                startActivity(i);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        setupFirebaseAuth();

//
        getUserList();


        return view;
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

                   db .child(userlist.get(i))
                    .orderByKey()
                    .limitToLast(1)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: chat" + dataSnapshot1.getValue());
                                Chat chat = dataSnapshot1.getValue(Chat.class);
                                chatList.add(chat);

                            }
                            if (chatList.size() == userlist.size()) {

                                sortChatList(chatList,userlist);

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

        Collections.sort(chatList, new Comparator<Chat>() {
            @Override
            public int compare(Chat o1, Chat o2) {
                return o2.getTim().compareTo(o1.getTim());
            }
        });

        for (int i = 0; i < chatList.size(); i++) {


            if (chatList.get(i).getsid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                messagesfragment.this.userlist2.add(chatList.get(i).getrid());
            } else {

                messagesfragment.this.userlist2.add(chatList.get(i).getsid());

            }


        }
        adaterChatList = new AdaterChatList(getContext(), messagesfragment.this.userlist2);
        for (int i = 0; i < userlist2.size(); i++) {
            lastMessage(userlist2.get(i));
        }

        displayChatList();

    }


    private void lastMessage(String uid) {

        DatabaseReference refer1 = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_Chats));
        refer1.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        DatabaseReference refer = FirebaseDatabase.getInstance().getReference(getString(R.string.dbname_ChatList));
                        refer .child(snapshot.getValue().toString())
                                .orderByKey()
                                .limitToLast(1)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String lmsg = "default";
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            if (ds.exists()) {
                                                Chat chat = ds.getValue(Chat.class);
                                                lmsg = chat.getMsg();

                                            }

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
                if (iteration > 10) {
                    iteration = 10;
                }
                mResults = 10;
                for (int i = 0; i < iteration; i++) {
                    paginateduserList.add(userlist2.get(i));
                }
                Log.d(TAG, "chatslist: sss" + paginateduserList.size());
                adaterChatList = new AdaterChatList(getContext(), paginateduserList);
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
                for (int i = mResults; i < mResults + iterations; i++) {
                    paginateduserList.add(userlist2.get(i));

                }
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        adaterChatList.notifyItemRangeInserted(mResults,iterations);
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
        mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}