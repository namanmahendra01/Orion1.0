package com.orion.orion.explore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.Adapters.AdapterGridImageExplore;
import com.orion.orion.Adapters.UserListAdapter;
import com.orion.orion.R;
import com.orion.orion.dialogs.BottomSheetDomain;
import com.orion.orion.models.CreateForm;
import com.orion.orion.models.Photo;
import com.orion.orion.models.TopUsers;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.util.BottomNaavigationViewHelper;
import com.orion.orion.util.FirebaseMethods;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.UniversalImageLoader;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class Explore extends AppCompatActivity implements BottomSheetDomain.BottomSheetListener {
    private static final String TAG = "notification";
    private static final int ACTIVITY_NUM = 1;
    private Context mContext;
    String spin;
    Boolean nearby = true, overall = true, follower = true, load = true, overallR = true, followerR = true;
    users user1 = new users();
    users user2 = new users();
    users user3 = new users();
    users user4 = new users();
    users user5 = new users();
    users user6 = new users();
    users user7 = new users();
    users user8 = new users();
    private TextView spinner;
    private EditText mSearchParam;
    private ListView mListView;
    private RecyclerView exploreRv;
    private String currentLoc;
    private ProgressBar progressBar;
    private int mResults;
    private int count = 0, count1 = 0, count2 = 0, count3 = 0;
    private AdapterGridImageExplore adapterGridImage;
    private ArrayList<String> usersList;
    private ArrayList<String> usersList2;
    private ArrayList<String> usersList3;
    private ArrayList<String> usersList4;
    private ArrayList<String> usersList5;
    private ArrayList<String> usersList6;
    private ArrayList<String> usersList7;
    private ArrayList<String> usersList8;
    private ArrayList<String> usersList9;
    private ArrayList<String> usersList10;
    private ArrayList<Photo> photos;
    private ArrayList<Photo> paginatedphotos;
    //    SP
    Gson gson;
    SharedPreferences sp,sp1;
    private List<users> mUserList;
    private UserListAdapter mAdapter;

    private CircleImageView star1, star2, star3, star4, star5, star6, star7, star8;

    private DatabaseReference reference;
    FirebaseMethods firebaseMethods;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSearchParam = findViewById(R.id.search);
        mListView = findViewById(R.id.listview);
        exploreRv = findViewById(R.id.exploreRv);

        star1 = findViewById(R.id.circleImageView2);
        star2 = findViewById(R.id.circleImageView3);
        star3 = findViewById(R.id.circleImageView4);
        star4 = findViewById(R.id.circleImageView6);
        star5 = findViewById(R.id.circleImageView7);
        star6 = findViewById(R.id.circleImageView5);
        star7 = findViewById(R.id.circleImageView);
        star8 = findViewById(R.id.circleImageView8);
        progressBar = findViewById(R.id.progress_circular);

//          Initialize SharedPreference variables
        sp = getSharedPreferences("naman", Context.MODE_PRIVATE);
        sp1 = getSharedPreferences("naman2", Context.MODE_PRIVATE);

        gson = new Gson();
        spinner = findViewById(R.id.spinnerDo);
        usersList10 = new ArrayList<>();

        spin = spinner.getText().toString();

        spinner.setOnClickListener(v -> {
            BottomSheetDomain bottomSheetDomain = new BottomSheetDomain();
            bottomSheetDomain.show(getSupportFragmentManager(), "Domain Selection");
        });
        star1.setOnClickListener(v -> {
            Intent i = new Intent(Explore.this, profile.class);
            i.putExtra(getString(R.string.calling_activity), getString(R.string.home));
            i.putExtra(getString(R.string.intent_user), user1);
            startActivity(i);
        });
        star2.setOnClickListener(v -> {
            Intent i = new Intent(Explore.this, profile.class);
            i.putExtra(getString(R.string.calling_activity), getString(R.string.home));
            i.putExtra(getString(R.string.intent_user), user2);
            startActivity(i);
        });
        star3.setOnClickListener(v -> {
            Intent i = new Intent(Explore.this, profile.class);
            i.putExtra(getString(R.string.calling_activity), getString(R.string.home));
            i.putExtra(getString(R.string.intent_user), user3);
            startActivity(i);
        });
        star4.setOnClickListener(v -> {
            Intent i = new Intent(Explore.this, profile.class);
            i.putExtra(getString(R.string.calling_activity), getString(R.string.home));
            i.putExtra(getString(R.string.intent_user), user4);
            startActivity(i);
        });
        star5.setOnClickListener(v -> {
            Intent i = new Intent(Explore.this, profile.class);
            i.putExtra(getString(R.string.calling_activity), getString(R.string.home));
            i.putExtra(getString(R.string.intent_user), user5);
            startActivity(i);
        });
        star6.setOnClickListener(v -> {
            Intent i = new Intent(Explore.this, profile.class);
            i.putExtra(getString(R.string.calling_activity), getString(R.string.home));
            i.putExtra(getString(R.string.intent_user), user6);
            startActivity(i);
        });
        star7.setOnClickListener(v -> {
            Intent i = new Intent(Explore.this, profile.class);
            i.putExtra(getString(R.string.calling_activity), getString(R.string.home));
            i.putExtra(getString(R.string.intent_user), user7);
            startActivity(i);
        });
        star8.setOnClickListener(v -> {
            Intent i = new Intent(Explore.this, profile.class);
            i.putExtra(getString(R.string.calling_activity), getString(R.string.home));
            i.putExtra(getString(R.string.intent_user), user8);
            startActivity(i);
        });

        exploreRv.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 3);
        exploreRv.setLayoutManager(linearLayoutManager);

        exploreRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    displayMorePhotos();
                }
            }
        });
        check();


        Log.d(TAG, "onCreate: started.");
        setupBottomNavigationView();
//        newStuff();
//        hideSoftKeyboard();
//        initTextListener();
//        getNearbyUsers();
//        getTop8();
    }

    private void check() {
//        //    Add newly Created ArrayList to Shared Preferences
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("createlist", "2");
//        editor.apply();
//        //    Add newly Created ArrayList to Shared Preferences
//         editor = sp1.edit();
//        editor.putString("createlist", "3");
//        editor.apply();

        String json = sp.getString("createlist", "null");
        String json1 = sp1.getString("createlist", "null");

        Log.d(TAG, "check: wer"+json);
        Log.d(TAG, "check: wer"+json1);



    }

    private void newStuff() {
        reference = FirebaseDatabase.getInstance().getReference();
        mContext = Explore.this;
        firebaseMethods = new FirebaseMethods(mContext);
        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
            @Override
            public void onTimeReceived(String currentTimeStamp) {

                Query query = reference.child("db_topUsersParams");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("last_updated_topUsers").getValue() == null) createDomainDocument();
                        else {
                            Log.d(TAG, "createDomainDocument: started");
                            String previousTimeStamp = (String) snapshot.child("last_updated_topUsers").getValue();
                            //initializing formatting for current date
                            int currentYear = Integer.parseInt(currentTimeStamp.substring(0, 4));
                            int currentMonth = Integer.parseInt(currentTimeStamp.substring(5, 7));
                            int currentDate = Integer.parseInt(currentTimeStamp.substring(8, 10));
                            String currentTime = currentTimeStamp.substring(12, currentTimeStamp.length() - 1);
                            String currentDateFormat = currentDate + "/" + currentMonth + "/" + currentYear;
                            Date date = new Date(currentDateFormat);
                            int currentDay = date.getDay();

                            int postedYear = Integer.parseInt(previousTimeStamp.substring(0, 4));
                            int postedMonth = Integer.parseInt(previousTimeStamp.substring(5, 7));
                            int postedDate = Integer.parseInt(previousTimeStamp.substring(8, 10));
                            String postedTime = previousTimeStamp.substring(12, previousTimeStamp.length() - 1);
                            String postedDateFormat = postedDate + "/" + postedMonth + "/" + postedYear;

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");
                            long elapsedDays = 0;
                            try {
                                Date date1 = simpleDateFormat.parse(postedDateFormat);
                                Date date2 = simpleDateFormat.parse(currentDateFormat);
                                Log.d(TAG, "onTimeReceived: " + date1);
                                Log.d(TAG, "onTimeReceived: " + date2);
                                assert date1 != null;
                                assert date2 != null;
                                elapsedDays = (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
                                Log.d(TAG, "onDataChange: elapsedDays" + elapsedDays);
                                Log.d(TAG, "onDataChange: currentDay" + currentDay);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            //the week has changed
                            if (elapsedDays > currentDay)
                                createDomainDocument();
                            else {
                                ArrayList<String> fields = new ArrayList<>();
                                fields.add("Photography");
                                fields.add("Film Maker");
                                fields.add("Musician");
                                fields.add("Sketch Artist");
                                fields.add("Writer");
                                fields.add("Others");
                                for (String field : fields) {
                                    String valuesUpdated= (String) snapshot.child(field).getValue();
                                    if(valuesUpdated==null){
                                        completedDomainDocument(field,0);
                                    }
                                    else{
                                        int completedValues= Integer.parseInt(valuesUpdated.substring(0,valuesUpdated.indexOf("/")));
                                        int totalValues= Integer.parseInt(valuesUpdated.substring(valuesUpdated.indexOf("/")+1));
                                        if(completedValues<totalValues){
                                            Log.d(TAG, "onDataChange: completedValues"+completedValues);
                                            Log.d(TAG, "onDataChange: totalValues"+totalValues);
                                            completedDomainDocument(field,completedValues);
                                        }
                                    }
                                }
                            }
                        }
                        reference.child("db_topUsersParams").child("last_updated_topUsers").setValue(currentTimeStamp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onError(Exception ex) {
                Log.e(SNTPClient.TAG, Objects.requireNonNull(ex.getMessage()));
            }
        });
        String currentTimeStamp = firebaseMethods.getTimeStamp();
        Log.d(TAG, "newStuff: " + currentTimeStamp);

    }

    private void createDomainDocument() {
        Log.d(TAG, "createDomainDocument: started");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("Photography");
        fields.add("Film Maker");
        fields.add("Musician");
        fields.add("Sketch Artist");
        fields.add("Writer");
        fields.add("Others");
        for (String field : fields) {

            ArrayList<TopUsers> mListOverall = new ArrayList<>();
            mListOverall.clear();

            Query query = reference.child(getString(R.string.dbname_leaderboard));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot singleSnapshot : snapshot.getChildren()) {

                        String domain = (String) singleSnapshot.child(getString(R.string.field_domain)).getValue();
                        assert domain != null;
                        assert field != null;
                        if (domain.equals(field)) {
                            int rating = (int) (long) singleSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_post)).getValue()
                                    + (int) (long) singleSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_followers)).getValue()
                                    + (int) (long) singleSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_contest)).getValue();
                            //getting user ids, username and profile photos
                            String user_id = singleSnapshot.getKey();
                            TopUsers emptyItem = new TopUsers();
                            TopUsers dataItemOverall = new TopUsers(user_id, rating);
                            if (mListOverall.size() == 0) {
                                mListOverall.add(dataItemOverall);
                            } else {
                                int l = mListOverall.size();

                                //loop to push in between and next one further away for overall
                                for (int i = 0; i < l; i++) {
                                    int r = mListOverall.get(i).getRating();
                                    if (rating >= r) {
                                        mListOverall.add(emptyItem);
                                        for (int j = mListOverall.size() - 1; j > i; j--)
                                            mListOverall.set(j, mListOverall.get(j - 1));
                                        mListOverall.set(i, dataItemOverall);
                                        break;
                                    }
                                    //pushing at the end
                                    else if (i == l - 1)
                                        mListOverall.add(dataItemOverall);
                                }
                                if (mListOverall.size() == 301) {
                                    mListOverall.remove(300);
                                }
                            }
                        }
                    }
                    Log.d(TAG, "onDataChange: stratingupdate");
                    Map<String, Object> user = new HashMap<>();
                    user.put("type", field);
                    db.collection("Domain Collection")
                            .document(field + " Document")
                            .set(user);
                    db.collection("Domain Collection")
                            .document(field + " Document")
                            .collection(field + " Collection")
                            .document(field + " Document 1")
                            .set(user);
                    db.collection("Domain Collection")
                            .document(field + " Document").collection(field + " Collection")
                            .document(field + " Document 2")
                            .set(user);
                    db.collection("Domain Collection")
                            .document(field + " Document")
                            .collection(field + " Collection")
                            .document(field + " Document 3")
                            .set(user);

                    final int[] k = {0};
                    for (int i = 0; i < 300; i++) {
                        Log.d(TAG, "onDataChange: user"+field+user);
                        int finalI = i;
                        db.collection("Domain Collection")
                                .document(field + " Document")
                                .collection(field + " Collection")
                                .document(field + " Document 1")
                                .update("type",i);
                        if (i < 100) {
                            user.clear();
                            user.put(String.valueOf(i + 1), "11");
                            db.collection("Domain Collection")
                                    .document(field + " Document")
                                    .collection(field + " Collection")
                                    .document(field + " Document 1")
                                    .set(user,SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "onDataChange: Success"+(finalI +1)+"/"+"300");
                                        reference.child("db_topUsersParams").child(field).setValue((finalI +1)+"/"+"300");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.d(TAG, "onDataChange: error"+e.getMessage());
                                    });
                        } else if (i < 200) {
                            user.clear();
                            user.put(String.valueOf(i + 1), "22");
                            db.collection("Domain Collection")
                                    .document(field + " Document")
                                    .collection(field + " Collection")
                                    .document(field + " Document 2")
                                    .set(user,SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "onDataChange: Success"+(finalI +1)+"/"+"300");
                                        reference.child("db_topUsersParams").child(field).setValue((finalI +1)+"/"+"300");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.d(TAG, "onDataChange: "+e.getMessage());

                                    });
                        } else {
                            user.clear();
                            user.put(String.valueOf(i + 1), "33");
                            db.collection("Domain Collection")
                                    .document(field + " Document")
                                    .collection(field + " Collection")
                                    .document(field + " Document 3")
                                    .set(user,SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "onDataChange: Success"+(finalI +1)+"/"+"300");
                                        reference.child("db_topUsersParams").child(field).setValue((finalI +1)+"/"+"300");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.d(TAG, "onDataChange: "+e.getMessage());

                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void completedDomainDocument(String field, int completedValues){
        Log.d(TAG, "createDomainDocument: " + field);
        Log.d(TAG, "createDomainDocument: " + completedValues);
        ArrayList<TopUsers> mListOverall = new ArrayList<>();
        mListOverall.clear();

        Query query = reference.child(getString(R.string.dbname_leaderboard));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singleSnapshot : snapshot.getChildren()) {

                    String domain = (String) singleSnapshot.child(getString(R.string.field_domain)).getValue();
                    assert domain != null;
                    assert field != null;
                    if (domain.equals(field)) {
                        int rating = (int) (long) singleSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_post)).getValue()
                                + (int) (long) singleSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_followers)).getValue()
                                + (int) (long) singleSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_contest)).getValue();
                        //getting user ids, username and profile photos
                        String user_id = singleSnapshot.getKey();
                        TopUsers emptyItem = new TopUsers();
                        TopUsers dataItemOverall = new TopUsers(user_id, rating);
                        if (mListOverall.size() == 0) {
                            mListOverall.add(dataItemOverall);
                        } else {
                            int l = mListOverall.size();

                            //loop to push in between and next one further away for overall
                            for (int i = completedValues; i < l; i++) {
                                int r = mListOverall.get(i).getRating();
                                if (rating >= r) {
                                    mListOverall.add(emptyItem);
                                    for (int j = mListOverall.size() - 1; j > i; j--)
                                        mListOverall.set(j, mListOverall.get(j - 1));
                                    mListOverall.set(i, dataItemOverall);
                                    break;
                                }
                                //pushing at the end
                                else if (i == l - 1)
                                    mListOverall.add(dataItemOverall);
                            }
                            if (mListOverall.size() == 301) {
                                mListOverall.remove(300);
                            }
                        }
                    }
                }

                Map<String, Object> user = new HashMap<>();
                user.put("type", field);
                db.collection("Domain Collection").document(field + " Document").set(user);
                db.collection("Domain Collection").document(field + " Document").collection(field + " Collection").document(field + " Document 1").set(user);
                db.collection("Domain Collection").document(field + " Document").collection(field + " Collection").document(field + " Document 2").set(user);
                db.collection("Domain Collection").document(field + " Document").collection(field + " Collection").document(field + " Document 3").set(user);

                for (int i = completedValues; i < 300; i++) {
                    Log.d(TAG, "createDomainDocument: user"+field+user);
                    int finalI = i;
                    if (i < 100) {
                        user.clear();
                        user.put(String.valueOf(i + 1), "1");
                        db.collection("Domain Collection")
                                .document(field + " Document")
                                .collection(field + " Collection")
                                .document(field + " Document 1")
                                .update(user)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "onDataChange: Success"+(finalI +1)+"/"+"300");
                                    reference.child("db_topUsersParams").child(field).setValue((finalI +1)+"/"+"300");
                                })
                                .addOnFailureListener(e -> Log.d(TAG, "onDataChange: "+e.getMessage()));
                    } else if (i < 200) {
                        user.clear();
                        user.put(String.valueOf(i + 1), "2");
                        db.collection("Domain Collection")
                                .document(field + " Document")
                                .collection(field + " Collection")
                                .document(field + " Document 2")
                                .update(user)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "createDomainDocument: Success");
                                    reference.child("db_topUsersParams").child(field).setValue((finalI +1)+"/"+"300");
                                })
                                .addOnFailureListener(e -> {
                                    Log.d(TAG, "createDomainDocument: "+e.getMessage());

                                });
                    } else {
                        user.clear();
                        user.put(String.valueOf(i + 1), "3");
                       DocumentReference ref= db.collection("Domain Collection")
                                .document(field + " Document")
                                .collection(field + " Collection")
                                .document(field + " Document 3");
                               ref .update(user)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "createDomainDocument: Success");
                                    reference.child("db_topUsersParams").child(field).setValue((finalI +1)+"/"+"300");
                                })
                                .addOnFailureListener(e -> {
                                    Log.d(TAG, "createDomainDocument: "+e.getMessage());
                                });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onButtonClicked(String text) {
        spin = spinner.getText().toString();
        spinner.setText(text);
        Log.d(TAG, "onItemSelected: qwer" + spin);
        getNearbyUsers();
    }

    private void getTop8() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("top_users").child("overall").limitToFirst(8).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    usersList10.add(snapshot1.child("user_id").getValue().toString());
                }
                getStarImage(usersList10);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getStarImage(ArrayList<String> user_id) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_users)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int x = 0; x < user_id.size(); x++) {
                    if (x == 0) {
                        user1 = snapshot.child(user_id.get(x)).getValue(users.class);
                        UniversalImageLoader.setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star1, null, "");
                    }
                    if (x == 1) {
                        user2 = snapshot.child(user_id.get(x)).getValue(users.class);
                        UniversalImageLoader.setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star2, null, "");
                    }
                    if (x == 2) {
                        user3 = snapshot.child(user_id.get(x)).getValue(users.class);
                        UniversalImageLoader.setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star3, null, "");
                    }
                    if (x == 3) {
                        user4 = snapshot.child(user_id.get(x)).getValue(users.class);
                        UniversalImageLoader.setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star4, null, "");
                    }
                    if (x == 4) {
                        user5 = snapshot.child(user_id.get(x)).getValue(users.class);
                        UniversalImageLoader.setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star5, null, "");
                    }
                    if (x == 5) {
                        user6 = snapshot.child(user_id.get(x)).getValue(users.class);
                        UniversalImageLoader.setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star6, null, "");
                    }
                    if (x == 6) {
                        user7 = snapshot.child(user_id.get(x)).getValue(users.class);
                        UniversalImageLoader.setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star7, null, "");
                    }
                    if (x == 7) {
                        user8 = snapshot.child(user_id.get(x)).getValue(users.class);
                        UniversalImageLoader.setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star8, null, "");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getNearbyUsers() {
        count = 0;
        count1 = 0;
        count2 = 0;
        count3 = 0;
        nearby = true;
        overall = true;
        follower = true;
        load = true;
        overallR = true;
        followerR = true;
        usersList2 = new ArrayList<>();
        usersList3 = new ArrayList<>();
        usersList4 = new ArrayList<>();
        usersList5 = new ArrayList<>();
        usersList6 = new ArrayList<>();
        usersList7 = new ArrayList<>();
        usersList8 = new ArrayList<>();
        usersList9 = new ArrayList<>();
        photos = new ArrayList<>();
        paginatedphotos = new ArrayList<>();
        usersList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.DropOut).duration(500).playOn(progressBar);
        exploreRv.setVisibility(View.GONE);
        Log.d(TAG, "getNearbyUsers: ert");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(getString(R.string.dbname_users)).orderByChild("domain").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int x = 0, y = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    x++;
                    y++;
//                            if (x<count){
//                                continue;
//                            }
                    if (!usersList.contains(snapshot1.getKey())) {
                        usersList4.add(snapshot1.getKey());
                    } else {
                        x--;
                    }
                    if (x == 100) {
                        Log.d(TAG, "onDataChange: jkllll");
                        getNearbyUsersAgain();
                        break;
                    } else if (y == snapshot.getChildrenCount()) {
                        Log.d(TAG, "onDataChange: kkkkkk");
                        getNearbyUsersAgain();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getNearbyUsersAgain() {
        Log.d(TAG, "getNearbyUsersAgain: a");
        if (load) {
            if (usersList.size() >= 21) {
                displayExpore(usersList);
                load = false;

            }
        }
        if (nearby) {
            Log.d(TAG, "getNearbyUsersAgain:kkl a");
            if (usersList4.size() != 0) {
                Log.d(TAG, "getNearbyUsersAgain: vaa");
                for (int x = count; x < this.usersList4.size(); x++) {
                    usersList5.add(usersList4.get(x));
                    if (usersList4.size() - 1 > x + 9) {
                        Log.d(TAG, "getNearbyUsersAgain: jhvh");
                        if (x == count + 9) {
                            Log.d(TAG, "getNearbyUsersAgain: kb");
                            finalList(usersList5);
                            count = count + 10;
                            if (overallR) {
                                Log.d(TAG, "getNearbyUsersAgain: jvv");
                                getTopOverall();
                            } else {
                                Log.d(TAG, "getNearbyUsersAgain: vvg");
                                getOverallAgain();
                            }
                            break;
                        }
                    } else {
                        Log.d(TAG, "getNearbyUsersAgain: ghvhg");
                        if (x == usersList4.size() - 1) {
                            finalList(usersList5);
                            if (overallR) {
                                getTopOverall();
                            } else {
                                getOverallAgain();
                            }
                            nearby = false;
                            break;
                        }

                    }
                }
            } else {
                Log.d(TAG, "getNearbyUsersAgain: vb b  ");
                if (overallR) {
                    getTopOverall();
                } else {
                    getOverallAgain();
                }
                nearby = false;
            }
        } else {
            Log.d(TAG, "getNearbyUsersAgain: jhbh");
            if (overallR) {
                getTopOverall();
            } else {
                getOverallAgain();
            }
        }
    }

    private void finalList(ArrayList<String> getusersList) {
        usersList.addAll(getusersList);
    }

    private void getTopOverall() {

        if (load) {
            if (usersList.size() >= 21) {
                displayExpore(usersList);
                load = false;

            }
        }
        overallR = false;
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("top_users").child("overall").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int x = 0, y = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    x++;
                    y++;
                    Log.d(TAG, "onDataChange: film  " + snapshot1.child("user_id").getValue().toString());
//                            if (x<count){
//                                continue;
//                            }
                    if (spin.equals("All")) {
                        if (!usersList.contains(snapshot1.child("user_id").getValue().toString())) {
                            usersList6.add(snapshot1.child("user_id").getValue().toString());
                        } else {
                            x--;
                        }
                    } else {
                        if (!usersList.contains(snapshot1.child("user_id").getValue().toString())
                                && (snapshot1.child("domain").getValue().toString().equals(spin))) {
                            usersList6.add(snapshot1.child("user_id").getValue().toString());
                        } else {
                            x--;
                        }
                    }
                    if (x == 100) {
                        Log.d(TAG, "getOverallAgain: nv jhl");
                        getOverallAgain();
                        break;

                    } else if (y == snapshot.getChildrenCount()) {
                        Log.d(TAG, "getOverallAgain: njcfhl");
                        getOverallAgain();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getOverallAgain() {
        Log.d(TAG, "getOverallAgain: njhl" + overall + usersList6.size());
        if (load) {
            if (usersList.size() >= 21) {
                displayExpore(usersList);
                load = false;
            }
        }
        if (overall) {
            if (usersList6.size() != 0) {
                for (int x = count1; x < usersList6.size(); x++) {
                    try {
                        usersList7.add(usersList6.get(x));
                        if (usersList6.size() - 1 > x + 9) {
                            Log.d(TAG, "getOverallAgain: jjhh");
                            if (x == count1 + 9) {
                                Log.d(TAG, "getOverallAgain: bhb");
                                finalList(usersList7);
                                count1 = count1 + 10;
                                if (followerR) {
                                    getTopFollower();
                                } else {
                                    getTopFollowerAgain();
                                }
                                break;
                            }
                        } else {
                            Log.d(TAG, "getOverallAgain: lkn");
                            if (x == usersList6.size() - 1) {
                                Log.d(TAG, "getOverallAgain: jhvjv");
                                finalList(usersList7);
                                if (followerR) {
                                    Log.d(TAG, "getOverallAgain:gg jhvjv");

                                    getTopFollower();
                                } else {
                                    getTopFollowerAgain();
                                }
                                overall = false;
                                break;
                            }

                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Log.e(TAG, "getOverallAgain: " + e.getMessage());
                        if (followerR) {
                            getTopFollower();
                        } else {
                            getTopFollowerAgain();
                        }
                        overall = false;
                        break;
                    } catch (IndexOutOfBoundsException e) {
                        Log.e(TAG, "getOverallAgain: " + e.getMessage());
                        if (followerR) {
                            getTopFollower();
                        } else {
                            getTopFollowerAgain();
                        }
                        overall = false;
                        break;
                    }
                }
            } else {
                if (followerR) {
                    getTopFollower();
                } else {
                    getTopFollowerAgain();
                }
                overall = false;
            }
        } else {
            if (followerR) {
                getTopFollower();
            } else {
                getTopFollowerAgain();
            }
        }
    }

    private void getTopFollower() {
        Log.d(TAG, "getTopOverall: ara");
        if (load) {
            if (usersList.size() >= 21) {
                displayExpore(usersList);
                load = false;
            }
        }
        followerR = false;

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("top_users").child("follower").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int x = 0, y = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    x++;
                    y++;
//                            if (x<count){
//                                continue;
//                            }
                    Log.d(TAG, "onDataChange: film 2  " + snapshot1.child("user_id").getValue().toString());
                    if (spin.equals("All")) {
                        if (!usersList.contains(snapshot1.child("user_id").getValue().toString())) {
                            usersList8.add(snapshot1.child("user_id").getValue().toString());
                        } else {
                            x--;
                        }
                    } else {
                        if (!usersList.contains(snapshot1.child("user_id").getValue().toString()) && (snapshot1.child("domain").getValue().toString().equals(spin))) {
                            usersList6.add(snapshot1.child("user_id").getValue().toString());
                        } else {
                            x--;
                        }
                    }
                    Log.d(TAG, "onDataChange: pl" + y);
                    if (x == 100) {
                        Log.d(TAG, "onDataChange: ask");
                        getTopFollowerAgain();
                        break;

                    } else if (y == snapshot.getChildrenCount()) {
                        Log.d(TAG, "onDataChange: ask2");
                        getTopFollowerAgain();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getTopFollowerAgain() {

        if (load) {
            if (usersList.size() >= 21) {
                displayExpore(usersList);
                load = false;
            }
        }
        if (usersList8.size() != 0) {
            if (follower) {
                for (int x = count2; x < this.usersList8.size(); x++) {

                    usersList9.add(usersList8.get(x));
                    if (usersList8.size() - 1 > x + 9) {
                        if (x == count2 + 9) {
                            finalList(usersList9);
                            count2 = count2 + 10;
                            break;
                        }
                    } else {
                        if (x == usersList8.size() - 1) {
                            finalList(usersList9);
                            follower = false;
                            if (follower || overall || nearby) {
                                getNearbyUsersAgain();
                            } else {
                                Log.d(TAG, "getTopFollowerAgain: 4");
                                displayExpore(usersList);
                            }
                            break;
                        }
                    }
                }
            }
            if (follower || overall || nearby) {
                Log.d(TAG, "getTopFollowerAgain: 3");
                getNearbyUsersAgain();
            } else {
                Log.d(TAG, "getTopFollowerAgain: 4");
                displayExpore(usersList);
            }
        } else {
            follower = false;
            if (follower || overall || nearby) {
                Log.d(TAG, "getTopFollowerAgain: 1");
                getNearbyUsersAgain();
            } else {
                Log.d(TAG, "getTopFollowerAgain: 2");
                displayExpore(usersList);
            }
        }
    }

    private void displayExpore(ArrayList<String> usersListF) {
        for (int x = count3; x < usersListF.size(); x++) {
            Log.d(TAG, "displayExpore: user" + usersListF.get(x));
            Log.d(TAG, "displayExpore: size  " + usersListF.size());
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            db.child(getString(R.string.dbname_user_photos)).child(usersListF.get(x)).orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Photo photo = snapshot1.getValue(Photo.class);
                        photos.add(photo);
                    }
                    displayPhotos();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            if (x == usersListF.size() - 1) {
                count3 = x + 1;
            }
        }
    }

    private void initTextListener() {
        mUserList = new ArrayList<>();
        mSearchParam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = mSearchParam.getText().toString();
                searchForMatch(text);
            }
        });
    }

    private void searchForMatch(String keyword) {
        Log.d(TAG, "searching for a match" + keyword);
        mUserList.clear();
        if (keyword.length() == 0) {
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getString(R.string.dbname_users)).orderByChild(getString(R.string.field_username)).startAt(keyword).endAt(keyword + "\uf8ff");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        mUserList.add(singleSnapshot.getValue(users.class));
                        updateUserList();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    private void updateUserList() {
        mAdapter = new UserListAdapter(Explore.this, R.layout.layout_user_listitem, mUserList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "selected user" + mUserList.get(position).toString());
                Intent intent = new Intent(Explore.this, profile.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.search_activity));
                intent.putExtra(getString(R.string.intent_user), mUserList.get(position));
                startActivity(intent);
            }
        });

    }

    private void displayPhotos() {
        Log.d(TAG, "display first 10 contest");
        paginatedphotos = new ArrayList<>();
        if (photos != null) {
            try {
                int iteration = photos.size();
                if (iteration > 5) {
                    iteration = 5;
                }
                mResults = 5;
                for (int i = 0; i < iteration; i++) {
                    paginatedphotos.add(photos.get(i));
                }
                Log.d(TAG, "contest: sss" + paginatedphotos.size());
                adapterGridImage = new AdapterGridImageExplore(this, paginatedphotos);
                exploreRv.setAdapter(adapterGridImage);
                progressBar.setVisibility(View.GONE);
                exploreRv.setVisibility(View.VISIBLE);
                exploreRv.setAdapter(adapterGridImage);

            } catch (NullPointerException e) {
                Log.e(TAG, "Null pointer exception" + e.getMessage());

            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "index out of bound" + e.getMessage());

            }
        }
    }

    public void displayMorePhotos() {
        Log.d(TAG, "display next 10 contest");
        try {
            if (photos.size() > mResults && photos.size() > 0) {
                int iterations;
                if (photos.size() > (mResults + 10)) {
                    Log.d(TAG, "display next 20 contest");
                    iterations = 10;
                } else {
                    Log.d(TAG, "display less tha 20 contest");
                    iterations = photos.size() - mResults;
                }
                for (int i = mResults; i < mResults + iterations; i++) {
                    paginatedphotos.add(photos.get(i));
                }
                mResults = mResults + iterations;
                exploreRv.post(new Runnable() {
                    @Override
                    public void run() {
                        adapterGridImage.notifyDataSetChanged();
                    }
                });

            }

        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());

        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());

        }
    }

    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void setupBottomNavigationView() {
        Log.d(TAG, " setupBottomNavigationView:setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.BottomNavViewBar);
        BottomNaavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNaavigationViewHelper.enableNavigation(Explore.this, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
