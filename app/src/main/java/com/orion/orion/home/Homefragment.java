package com.orion.orion.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orion.orion.Adapters.AdapterMainFeedContest;
import com.orion.orion.Adapters.AdapterMainfeed;
import com.orion.orion.Adapters.AdapterPromote;
import com.orion.orion.R;
import com.orion.orion.models.Comment;
import com.orion.orion.models.ContestDetail;
import com.orion.orion.models.Photo;
import com.orion.orion.models.Promote;
import com.orion.orion.models.users;
import com.orion.orion.util.UniversalImageLoader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/*
Shared preference keys:
fl=Following list
ffl = filtered following list
cl = contest list
pl= post list
addfollowing = list of users we followed
removefollowing = list of users we unfollowed
domain= user domain

 */

public class Homefragment extends Fragment implements AdapterMainfeed.ReleasePlayer {
    private static final String TAG = "HomeFragment";
    private ArrayList<Photo> mPhotos;
    private ArrayList<String> mFollowing;
    private ArrayList<String> mFollowing1;
    private ArrayList<Photo> mPaginatedPhotos;
    private RecyclerView contestRv;
    private ArrayList<ContestDetail> contestlist;
    private AdapterMainFeedContest contestUpcoming;
    private RecyclerView promoteRv;
    private ArrayList<String> promotelist;
    private AdapterPromote promote;
    private int c = 0;
    String domain;
    boolean flag1 = false, flag2 = false, flag3 = false, flag4 = false;
    private static int RETRY_DURATION = 1000;
    private static final Handler handler = new Handler(Looper.getMainLooper());

    LinearLayout promo;
    private ImageView star, starFill;
    TextView domaintv, footer;
    SwipeRefreshLayout postReferesh;
    ScrollView scrollView;
    private RecyclerView ListViewRv;
    private AdapterMainfeed mAadapter;
    private int mResults;
    public LinearLayout progress;
    TextView username;
    CircleImageView storySeen, story;

    //    SP
    Gson gson;
    SharedPreferences sp;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ListViewRv = view.findViewById(R.id.listview);
        star = view.findViewById(R.id.domainBtn);
        starFill = view.findViewById(R.id.domainBtnSel);
        domaintv = view.findViewById(R.id.domainTv);
        username = view.findViewById(R.id.story_username);
        storySeen = view.findViewById(R.id.story_photo_seen);
        story = view.findViewById(R.id.story_photo);
        promo = view.findViewById(R.id.promo2);
        footer = view.findViewById(R.id.footer);
        scrollView = view.findViewById(R.id.parent_scroll);
        postReferesh = view.findViewById(R.id.post_refresh);
        progress = view.findViewById(R.id.pro);
//          Initialize SharedPreference variables
        sp = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        gson = new Gson();

        postReferesh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                flag1 = false;
                flag2 = false;
                flag3 = false;
                flag4 = false;

                getFollowerListFromSP();
                Log.d(TAG, "onRefresh: 11");

                checkRefresh();


            }

            private void checkRefresh() {
                Log.d(TAG, "onRefresh: " + flag1 + flag2 + flag3 + flag4);
                if (postReferesh.isRefreshing() && flag1 && flag2 && (flag3 || flag4)) {
                    Log.d(TAG, "onRefresh: 22");
                    postReferesh.setRefreshing(false);
                    handler.removeCallbacks(this::checkRefresh);

                    flag1 = false;
                    flag2 = false;
                    flag3 = false;
                    flag4 = false;
                } else {
                    Log.d(TAG, "onRefresh: 33");
                    handler.postDelayed(this::checkRefresh, RETRY_DURATION);

                }
            }
        });

//          fetch   domain from SP

        domain = sp.getString("domain", null);
        if (domain == null) {           //   if not present
            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();
            reference2
                    .child(getString(R.string.dbname_users))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("domain")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            domain = dataSnapshot.getValue().toString();

//                 save to SP

                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("domain", domain);
                            editor.apply();


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }

        getUserInfo(FirebaseAuth.getInstance().getCurrentUser().getUid(), story, storySeen, username);
        seenStory(FirebaseAuth.getInstance().getCurrentUser().getUid());

        promo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ViewPromoted.class);
                i.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                startActivity(i);

            }
        });


        contestRv = view.findViewById(R.id.recyclerContest);

        promoteRv = view.findViewById(R.id.recyclerPromote);

        mFollowing1 = new ArrayList<>();

        contestRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        contestRv.setLayoutManager(linearLayoutManager);

        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(20);
        contestRv.setItemViewCacheSize(9);
        contestRv.setDrawingCacheEnabled(true);
        contestRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        contestlist = new ArrayList<>();

//****************************************************************************

        promoteRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        promoteRv.setLayoutManager(linearLayoutManager3);

        linearLayoutManager3.setItemPrefetchEnabled(true);
        linearLayoutManager3.setInitialPrefetchItemCount(20);
        promoteRv.setItemViewCacheSize(9);
        promoteRv.setDrawingCacheEnabled(true);
        promoteRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        promotelist = new ArrayList<>();
        promote = new AdapterPromote(getContext(), promotelist);
        promote.setHasStableIds(true);

        promoteRv.setAdapter(promote);


//****************************************************************************
        ListViewRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
        ListViewRv.setLayoutManager(linearLayoutManager1);
        linearLayoutManager1.setItemPrefetchEnabled(true);
        linearLayoutManager1.setInitialPrefetchItemCount(20);
        ListViewRv.setItemViewCacheSize(9);
        ListViewRv.setDrawingCacheEnabled(true);
        ListViewRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {

                        if (scrollView.getChildAt(0).getBottom()
                                == (scrollView.getHeight() + scrollView.getScrollY()) && c != 0) {

                            //scroll view is at bottom
                            displayMorePhotos();
                        } else {

                            //scroll view is not at bottom
                        }
                        c++;
                    }
                });

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star.setVisibility(View.GONE);
                starFill.setVisibility(View.VISIBLE);
                domaintv.setText(domain);

                getFilteredFollowerListFromSP(domain);
            }
        });
        starFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                starFill.setVisibility(View.GONE);
                star.setVisibility(View.VISIBLE);
                domaintv.setText("All");
                getPostListFromSP();
            }
        });

        getFollowerListFromSP();


        return view;


    }

    public void ToggleProgressBar() {
        if (progress.getVisibility() == View.GONE) {
            progress.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        } else {
            progress.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        }
        progress.setVisibility(View.VISIBLE);
    }

    //  fetching FollowerList  from SharedPreferences
    private void getFollowerListFromSP() {
        String json = sp.getString("fl", null);
        Log.d(TAG, "getFollowerListFromSP: 1" + json);

        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        mFollowing = gson.fromJson(json, type);
        if (mFollowing == null) {    //        if no arrayList is present
            Log.d(TAG, "getFollowerListFromSP: 3");
            mFollowing = new ArrayList<>();

            getFollowing();   //            make new Arraylist

        } else {
            Log.d(TAG, "getFollowerListFromSP: 2" + mFollowing);
            checkFollowingUpdate();  //         Check if we followed or unfollowed anyone

        }

    }

    private void checkFollowingUpdate() {

        int c = 0;

        String json = sp.getString("addfollowing", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> list = new ArrayList<>();
        list = gson.fromJson(json, type);
        if (list == null) {    //        not followed anyone
            c++;
        } else {              //    we followed someone....update everylist
            addToPhotosList(list);
            addToContestList(list);
            addToFilteredFollowingList(list);
            getStory();

        }

        json = sp.getString("removefollowing", null);
        type = new TypeToken<ArrayList<String>>() {
        }.getType();
        ArrayList<String> ulist = new ArrayList<>();
        ulist = gson.fromJson(json, type);
        if (ulist == null) {    //         not unfollowed anyone
            c++;
        } else {                  //    we unfollowed someone....update everylist

            removeFromPhotosList(ulist);
            removeFromContestList(ulist);
            removeFromFilteredFollowingList(ulist);

            getStory();
        }

        if (c == 2) {    //  if ther is no update
            getPostListFromSP();
            getContestListFromSP();
            getStory();
        }
    }


    //  fetching Postlist  from SharedPreferences
    private void getPostListFromSP() {
        String json = sp.getString("pl", null);
        Type type = new TypeToken<ArrayList<Photo>>() {
        }.getType();
        mPhotos = gson.fromJson(json, type);
        if (mPhotos == null || mPhotos.size() == 0) {                 //    if no arrayList is present

            getPhotos();                    //  make new Arraylist

        } else {
            checkPostUpdate();              //  Check whether any new Post is there or not


        }

    }


    //  fetching ContestList  from SharedPreferences
    private void getContestListFromSP() {
        String json = sp.getString("cl", null);
        Type type = new TypeToken<ArrayList<ContestDetail>>() {
        }.getType();
        contestlist = gson.fromJson(json, type);
        if (contestlist == null || contestlist.size() == 0) {    //        if no arrayList is present

            contestlist = new ArrayList<>();
            Log.d(TAG, "checkContestUpdate: 00");
            getcontest();   //            make new Arraylist

        } else {

//contestlist.clear();
//            SharedPreferences.Editor editor = sp.edit();
//             json = gson.toJson(contestlist);
//            editor.putString("cl", json);
//            editor.apply();


            checkContestUpdate();


        }

    }

    private void checkContestUpdate() {
        Log.d(TAG, "checkContestUpdate: 1");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.contest_update))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
//                        If snapshot exist,new contest are there
                        Log.d(TAG, "checkContestUpdate: 2");
                        if (snapshot1.exists()) {
                            Log.d(TAG, "checkContestUpdate: 3");
                            Collections.reverse(contestlist);
                            for (DataSnapshot snapshot : snapshot1.getChildren()) {

                                final int[] flag = {0};
                                DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();
                                db1.child(getString(R.string.dbname_contestlist))
                                        .child(snapshot.getKey())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                flag[0]++;
                                                ContestDetail contestDetail = snapshot.getValue(ContestDetail.class);
                                                if (contestDetail != null && !contestDetail.getResult()) {
                                                    Log.d(TAG, "checkContestUpdate: 4");
                                                    contestlist.add(contestDetail);

                                                }
                                                if (flag[0] == snapshot1.getChildrenCount()) {          //when all update added

                                                    Collections.reverse(contestlist);
                                                    //                Add newly Created ArrayList to Shared Preferences
                                                    SharedPreferences.Editor editor = sp.edit();
                                                    String json = gson.toJson(contestlist);
                                                    editor.putString("cl", json);
                                                    editor.apply();

                                                    contestUpcoming = new AdapterMainFeedContest(getContext(), contestlist);
                                                    contestUpcoming.setHasStableIds(true);

                                                    contestRv.setAdapter(contestUpcoming);

                                                    contestUpcoming.notifyDataSetChanged();
                                                    flag4 = true;
                                                    Log.d(TAG, "checkContestUpdate: 6");


//                                                    delete update
                                                    DatabaseReference db3 = FirebaseDatabase.getInstance().getReference();
                                                    db3.child(getString(R.string.dbname_users))
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child(getString(R.string.contest_update))
                                                            .removeValue();
                                                }
                                            }


                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                            }
                        } else {
                            Log.d(TAG, "checkContestUpdate: 5");

                            contestUpcoming = new AdapterMainFeedContest(getContext(), contestlist);
                            contestUpcoming.setHasStableIds(true);

                            contestRv.setAdapter(contestUpcoming);

                            contestUpcoming.notifyDataSetChanged();
                            flag4 = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //  fetching filtered followerlist  from SharedPreferences
    private void getFilteredFollowerListFromSP(String domain) {
        String json = sp.getString("ffl", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        mFollowing1 = gson.fromJson(json, type);
        if (mFollowing1 == null) {    //        if no arrayList is present

            mFollowing1 = new ArrayList<>();
            getFollowingFilltered(domain);  //            make new Arraylist

        } else {

            getfilterPhotos(mFollowing1);
        }

    }

    private void checkPostUpdate() {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.post_updates))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        If snapshot exist,new Posts are there
                        if (snapshot.exists()) {

//                            create two Arraylist,each containing key and userId respectively and corruspondingly

                            ArrayList<String> a1 = new ArrayList<>();
                            ArrayList<String> a2 = new ArrayList<>();
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                a1.add(snapshot1.getKey());
                                a2.add(snapshot1.getValue().toString());
                            }
//                           get photos using arraylist above arraylist
                            getUpdatedPhotos(a1, a2);

                        } else {
//                            No new Post are there,so display photos
                            displayPhotos();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void removeFromFilteredFollowingList(ArrayList<String> list) {
        String json = sp.getString("ffl", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        mFollowing1 = gson.fromJson(json, type);
        if (mFollowing1 == null) {    //        if no arrayList is present

        } else {

            for (int i = 0; i < list.size(); i++) {
                mFollowing1.remove(list.get(i));

            }
            //                        Add updated ArrayList to Shared Preferences
            SharedPreferences.Editor editor = sp.edit();
            json = gson.toJson(mFollowing1);
            editor.putString("ffl", json);
            editor.apply();
        }


    }

    private void addToFilteredFollowingList(ArrayList<String> list) {
        Log.d(TAG, "addToFilteredFollowingList: qwer" + domain);
        String json = sp.getString("ffl", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        mFollowing1 = gson.fromJson(json, type);
        if (mFollowing1 == null) {    //        if no arrayList is present
            mFollowing1 = new ArrayList<>();

        }

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();

        reference2
                .child(getString(R.string.dbname_users))

                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int x=0;
                        for (int i = 0; i < list.size(); i++) {
x++;

                            if (dataSnapshot.child(list.get(i))
                                    .child("domain").getValue().equals(domain)) {
                                mFollowing1.add(list.get(i));
                            }
                            if (x==dataSnapshot.getChildrenCount()){

//                        Add newly Created ArrayList to Shared Preferences
                                SharedPreferences.Editor editor = sp.edit();
                                String json = gson.toJson(mFollowing1);
                                editor.putString("ffl", json);
                                editor.apply();
                            }


                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void removeFromContestList(ArrayList<String> list) {

        String json = sp.getString("cl", null);
        Type type = new TypeToken<ArrayList<ContestDetail>>() {
        }.getType();
        ArrayList<ContestDetail> list1 = new ArrayList<>();
        list1 = gson.fromJson(json, type);
        if (list1==null){
            list1 = new ArrayList<>();
        }
        Log.d(TAG, "removeFromContestList: 3"+list1.size());

        int z=list1.size();

        if (list1.size() != 0&&list1!=null) {
            for (int i = 0; i < list.size(); i++) {
                Log.d(TAG, "removeFromContestList: 8 "+list1.size());
                for (int x=0;x<list1.size();x++){
                    Log.d(TAG, "removeFromContestList: 8m "+list1.size());

                    Log.d(TAG, "removeFromContestList: 4"+list1.get(x));
                    if (list1.get(x).getUserId().equals(list.get(i))) {
                        Log.d(TAG, "removeFromContestList: 1");
                        list1.remove(list1.get(x));
                        x--;
                    }
                }

            }
        }

        Log.d(TAG, "removeFromContestList: 3"+list1.size());

//                        Add newly Created ArrayList to Shared Preferences
        SharedPreferences.Editor editor = sp.edit();
        json = gson.toJson(list1);
        editor.putString("cl", json);
        editor.apply();

        contestUpcoming = new AdapterMainFeedContest(getContext(), contestlist);
        contestUpcoming.setHasStableIds(true);

        contestRv.setAdapter(contestUpcoming);

        contestUpcoming.notifyDataSetChanged();
        flag3 = true;




    }

    private void addToContestList(ArrayList<String> list) {
        String json = sp.getString("cl", null);
        Type type = new TypeToken<ArrayList<ContestDetail>>() {
        }.getType();
        contestlist = gson.fromJson(json, type);
        if (contestlist == null || contestlist.size() == 0) {    //        if no arrayList is present
            contestlist = new ArrayList<>();

        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        for (int i = 0; i < list.size(); i++) {

            final int count = i;


            Query query = reference
                    .child(getString(R.string.dbname_contestlist))
                    .orderByChild("userId")
                    .equalTo(list.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        Collections.reverse(contestlist);
                        int x = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            x++;
                            ContestDetail contestDetail = snapshot.getValue(ContestDetail.class);
                            if (!contestDetail.getResult()) {
                                contestlist.add(contestDetail);
                            }
                            if (x == dataSnapshot.getChildrenCount()&&count==list.size()-1) {

                                Collections.reverse(contestlist);

//                        Add newly Created ArrayList to Shared Preferences
                                SharedPreferences.Editor editor = sp.edit();
                                String json = gson.toJson(contestlist);
                                editor.putString("cl", json);
                                editor.apply();

                                contestUpcoming = new AdapterMainFeedContest(getContext(), contestlist);
                                contestUpcoming.setHasStableIds(true);

                                contestRv.setAdapter(contestUpcoming);

                                contestUpcoming.notifyDataSetChanged();
                                flag3 = true;

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


    private void removeFromPhotosList(ArrayList<String> uid) {

        String json = sp.getString("pl", null);
        Type type = new TypeToken<ArrayList<Photo>>() {
        }.getType();
        mPhotos = gson.fromJson(json, type);
        if (mPhotos == null || mPhotos.size() == 0) {                 //    if no arrayList is present

            mPhotos = new ArrayList<>();                 //  make new Arraylist

        }

        for (int x = 0; x < uid.size(); x++) {


//                    remove photo to mPhotos list

            ArrayList<Photo> l = new ArrayList<>(mPhotos);
            for (Photo a : l) {
                if (a.getUser_id().equals(uid.get(x))) {
                    mPhotos.remove(a);

                }
            }
        }

//                    sort mPhotos
        Collections.sort(mPhotos, new Comparator<Photo>() {
            @Override
            public int compare(Photo o1, Photo o2) {
                return o2.getDate_created().compareTo(o1.getDate_created());
            }
        });

//                add updated list to Shared Preference
        SharedPreferences.Editor editor = sp.edit();
        json = gson.toJson(mPhotos);
        editor.remove("removefollowing");
        editor.putString("pl", json);
        editor.apply();
        displayPhotos();


    }


    private void addToPhotosList(ArrayList<String> uid) {
        String json = sp.getString("pl", null);
        Type type = new TypeToken<ArrayList<Photo>>() {
        }.getType();
        mPhotos = gson.fromJson(json, type);
        if (mPhotos == null || mPhotos.size() == 0) {                 //    if no arrayList is present

            mPhotos = new ArrayList<>();                 //  make new Arraylist

        }
        int l = 0;

        for (int x = 0; x < uid.size(); x++) {
            l++;

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


            Query query = reference
                    .child(getString(R.string.dbname_user_photos))
                    .child(uid.get(x));

            int finalL = l;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int h = 0;
                    for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                        h++;
                        Photo photo = new Photo();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());

                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());

                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());

                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());

                        photo.setDate_created(objectMap.get(getString(R.string.field_date_createdr)).toString());

                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        if (objectMap.get(getString(R.string.thumbnail)) != null)
                            photo.setThumbnail(objectMap.get(getString(R.string.thumbnail)).toString());
                        if (objectMap.get(getString(R.string.type)) != null)
                            photo.setType(objectMap.get(getString(R.string.type)).toString());
                        ArrayList<Comment> comments = new ArrayList<>();

                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comment)).getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);

                        }
                        photo.setComments(comments);
//                    add photo to mPhotos list
                        mPhotos.add(photo);

                        if (finalL == uid.size() && h == snapshot.getChildrenCount()) {

//                    sort mPhotos
                            Collections.sort(mPhotos, new Comparator<Photo>() {
                                @Override
                                public int compare(Photo o1, Photo o2) {
                                    return o2.getDate_created().compareTo(o1.getDate_created());
                                }
                            });

//                add updated list to Shared Preference
                            SharedPreferences.Editor editor = sp.edit();
                            editor.remove("addfollowing");
                            String json = gson.toJson(mPhotos);
                            editor.putString("pl", json);
                            editor.apply();

                            displayPhotos();

                        }
                    }


                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "Query Cancelled");
                }
            });


        }


    }


    private void getUpdatedPhotos(ArrayList<String> key, ArrayList<String> uid) {
        for (int x = 0; x < key.size(); x++) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


            Query query = reference
                    .child(getString(R.string.dbname_user_photos))
                    .child(uid.get(x))
                    .child(key.get(x));
            int finalX = x;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot singleSnapshot) {
                    if (singleSnapshot.exists()) {
                        Photo photo = new Photo();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());

                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());

                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());

                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());

                        photo.setDate_created(objectMap.get(getString(R.string.field_date_createdr)).toString());

                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());

                        if (objectMap.get(getString(R.string.thumbnail)) != null)
                            photo.setThumbnail(objectMap.get(getString(R.string.thumbnail)).toString());
                        photo.setType(objectMap.get(getString(R.string.type)).toString());

                        ArrayList<Comment> comments = new ArrayList<>();

                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comment)).getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);

                        }
                        photo.setComments(comments);
//                    add photo to mPhotos list
                        mPhotos.add(photo);
//                    sort mPhotos
                        Collections.sort(mPhotos, new Comparator<Photo>() {
                            @Override
                            public int compare(Photo o1, Photo o2) {
                                return o2.getDate_created().compareTo(o1.getDate_created());
                            }
                        });


                    } else {

//                        if Photo id doesnt exist it means it has been deleted.So remove the Photo from mPhotos List

                        ArrayList<Photo> l = new ArrayList<>(mPhotos);
                        for (Photo a : l) {
                            if (a.getPhoto_id().equals(key.get(finalX))) {
                                mPhotos.remove(a);

                            }
                        }


                    }
                    //                add updated list to Shared Preference
                    SharedPreferences.Editor editor = sp.edit();
                    String json = gson.toJson(mPhotos);
                    editor.putString("pl", json);
                    editor.apply();
//                remove update_post node from database
                    removeUpdate();
//                  display post
                    displayPhotos();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "Query Cancelled");
                }
            });


        }


    }

    private void removeUpdate() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.post_updates))
                .removeValue();
    }

    private void getUserInfo(String userid, CircleImageView storyseen, CircleImageView story, TextView username) {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_users))
                .child(userid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        users user = dataSnapshot.getValue(users.class);
                        UniversalImageLoader.setImage(user.getProfile_photo(), story, null, "");
                        UniversalImageLoader.setImage(user.getProfile_photo(), storyseen, null, "");
                        username.setText(user.getUsername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void seenStory(String userid) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_promote))
                .child(userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            long l = dataSnapshot.getChildrenCount();
                            int t = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                if ((snapshot.child("views").child(FirebaseAuth.getInstance()
                                        .getCurrentUser().getUid()).exists())) {
                                    t++;
                                    Log.d(TAG, "onDataChange: ss" + t);

                                    break;
                                }
                            }
                            if (t == l) {
                                Log.d(TAG, "onDataChange: ss2" + t);
                                storySeen.setVisibility(View.VISIBLE);
                                story.setVisibility(View.GONE);
                                footer.setVisibility(View.GONE);

                            } else if (t == 0) {
                                storySeen.setVisibility(View.GONE);
                                story.setVisibility(View.VISIBLE);
                                footer.setVisibility(View.GONE);

                            }
                        } else {
                            storySeen.setVisibility(View.GONE);
                            story.setVisibility(View.GONE);
                            footer.setVisibility(View.VISIBLE);

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void getFollowingFilltered(String domain) {

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference();

        reference2
                .child(getString(R.string.dbname_users))

                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (int i = 0; i < mFollowing.size(); i++) {


                            if (dataSnapshot.child(mFollowing.get(i))
                                    .child("domain").getValue().equals(domain)) {
                                mFollowing1.add(mFollowing.get(i));
                            }
                            if (i == mFollowing.size() - 1) {
                                getfilterPhotos(mFollowing1);
                            }

                        }

//                        Add newly Created ArrayList to Shared Preferences
                        SharedPreferences.Editor editor = sp.edit();
                        String json = gson.toJson(mFollowing1);
                        editor.putString("ffl", json);
                        editor.apply();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }


    private void getfilterPhotos(ArrayList<String> mFollowing1) {
        ArrayList<Photo> list = new ArrayList<>();
        for (int x = 0; x < mFollowing1.size(); x++) {

            for (Photo a : mPhotos) {
                if (a.getUser_id().equals(mFollowing1.get(x))) {
                    list.add(a);
                }
            }
        }
        mPhotos.clear();
        mPhotos.addAll(list);
        list.clear();
        Collections.sort(mPhotos, new Comparator<Photo>() {
            @Override
            public int compare(Photo o1, Photo o2) {
                return o2.getDate_created().compareTo(o1.getDate_created());
            }
        });
        displayPhotos();
    }

    private void getcontest() {
        if (contestlist == null||contestlist.size()==0) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            for (int i = 0; i < mFollowing.size(); i++) {

                final int count = i;


                Query query = reference
                        .child(getString(R.string.dbname_contestlist))
                        .orderByChild("userId")
                        .equalTo(mFollowing.get(i));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ContestDetail contestDetail = snapshot.getValue(ContestDetail.class);
                            if (!contestDetail.getResult()) {
                                contestlist.add(contestDetail);
                            }
                        }

                        Collections.sort(contestlist, new Comparator<ContestDetail>() {
                            @Override
                            public int compare(ContestDetail o1, ContestDetail o2) {
                                return o2.getTimestamp().compareTo(o1.getTimestamp());
                            }
                        });
//                Add newly Created ArrayList to Shared Preferences
                        SharedPreferences.Editor editor = sp.edit();
                        String json = gson.toJson(contestlist);
                        editor.putString("cl", json);
                        editor.apply();

                        contestUpcoming = new AdapterMainFeedContest(getContext(), contestlist);
                        contestUpcoming.setHasStableIds(true);

                        contestRv.setAdapter(contestUpcoming);

                        contestUpcoming.notifyDataSetChanged();
                        flag3 = true;

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            if (mFollowing.size()==0){
                flag3=true;
            }
        } else {
            checkContestUpdate();
        }

    }


    private void getFollowing() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    mFollowing.add(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());
                }


//                        Add newly Created ArrayList to Shared Preferences
                SharedPreferences.Editor editor = sp.edit();
                String json = gson.toJson(mFollowing);
                editor.putString("fl", json);
                editor.apply();

                getPostListFromSP();
                getcontest();
                getStory();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Query Cancelled");
            }
        });
    }


    private void getStory() {
        promotelist.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        for (int i = 0; i < mFollowing.size(); i++) {

            final int count = i;
            final long delete = 0;

            Query query = reference
                    .child(getString(R.string.dbname_promote))
                    .child(mFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Long timestamp = System.currentTimeMillis();
                    if (dataSnapshot.exists()) {
                        promotelist.add(mFollowing.get(count));
                        long child1 = dataSnapshot.getChildrenCount();
                        long delete = 0;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Promote promote = snapshot.getValue(Promote.class);
                            if (timestamp >= Long.parseLong(promote.getTimeStart()) && timestamp <= Long.parseLong(promote.getTimeEnd())) {
                            } else {
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();

                                reference1
                                        .child(getString(R.string.dbname_promote))
                                        .child(mFollowing.get(count))
                                        .child(snapshot.getKey())
                                        .removeValue();
                                delete++;
                            }
                        }
                        if (delete == child1) {
                            promotelist.remove(mFollowing.get(count));
                        }


                        promote.notifyDataSetChanged();
                        flag2 = true;
                    } else {
                        promote.notifyDataSetChanged();
                        flag2 = true;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        if (mFollowing.size()==0){
            flag2=true;
        }
    }


    private void getPhotos() {
        if (mPhotos == null) {
            mPhotos = new ArrayList<>();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            for (int i = 0; i < mFollowing.size(); i++) {

                final int count = i;

                Query query = reference
                        .child(getString(R.string.dbname_user_photos))
                        .child(mFollowing.get(i))
                        .orderByChild(getString(R.string.field_user_id))
                        .equalTo(mFollowing.get(i));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long x = 0;
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            x++;

                            Photo photo = new Photo();
                            Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                            photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());

                            photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());

                            photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());

                            photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());

                            photo.setDate_created(objectMap.get(getString(R.string.field_date_createdr)).toString());

                            photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());


                            if (objectMap.get(getString(R.string.thumbnail)) != null) {
                                photo.setThumbnail(objectMap.get(getString(R.string.thumbnail)).toString());

                            }
                            photo.setType(objectMap.get(getString(R.string.type)).toString());

                            ArrayList<Comment> comments = new ArrayList<>();

                            for (DataSnapshot dSnapshot : singleSnapshot
                                    .child(getString(R.string.field_comment)).getChildren()) {
                                Comment comment = new Comment();
                                comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                comments.add(comment);

                            }
                            photo.setComments(comments);
                            mPhotos.add(photo);
//                        sort array List
                            Collections.sort(mPhotos, new Comparator<Photo>() {
                                @Override
                                public int compare(Photo o1, Photo o2) {
                                    return o2.getDate_created().compareTo(o1.getDate_created());
                                }
                            });

//                        Add newly Created ArrayList to Shared Preferences
                            SharedPreferences.Editor editor = sp.edit();
                            String json = gson.toJson(mPhotos);
                            editor.putString("pl", json);
                            editor.apply();

                        }
                        if (count >= mFollowing.size() - 1) {
//                        call display photos
                            displayPhotos();

                        }


                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "Query Cancelled");
                    }
                });


            }
        } else {
            checkPostUpdate();
        }

    }

    private void displayPhotos() {
        Log.d(TAG, "display first 10 photo");
        flag1 = true;
        mPaginatedPhotos = new ArrayList<>();
        if (mPhotos != null && mPhotos.size() != 0) {

            try {

                int iteration = mPhotos.size();
                if (iteration > 10) {
                    iteration = 10;
                }
                mResults = 10;
                for (int i = 0; i < iteration; i++) {
                    mPaginatedPhotos.add(mPhotos.get(i));
                }
                Log.d(TAG, "displayPhotos: sss" + mPaginatedPhotos.size());
                mAadapter = new AdapterMainfeed(getContext(), mPaginatedPhotos, ListViewRv);
                mAadapter.setHasStableIds(true);
                ListViewRv.setAdapter(mAadapter);

            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());

            }

        }
    }

    public void displayMorePhotos() {
        Log.d(TAG, "display next 10 photo");

        try {
            if (mPhotos.size() > mResults && mPhotos.size() > 0) {

                int iterations;
                if (mPhotos.size() > (mResults + 10)) {
                    Log.d(TAG, "display next 10 photo");
                    iterations = 10;
                } else {
                    Log.d(TAG, "display less tha 10 photo");
                    iterations = mPhotos.size() - mResults;
                }
                for (int i = mResults; i < mResults + iterations; i++) {
                    mPaginatedPhotos.add(mPhotos.get(i));

                }
                ListViewRv.post(new Runnable() {
                    @Override
                    public void run() {
                        mAadapter.notifyItemRangeInserted(mResults, iterations);
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

    @Override
    public void onPause() {
        super.onPause();

        Homefragment obj = new Homefragment();
        obj.releasePlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Homefragment obj = new Homefragment();
        obj.releasePlayer();
    }


}