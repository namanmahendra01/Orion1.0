package com.orion.orion.explore;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.Adapters.AdapterGridImageExplore;
import com.orion.orion.Adapters.UserListAdapter;
import com.orion.orion.R;
import com.orion.orion.ViewPostActivity;
import com.orion.orion.login.login;
import com.orion.orion.models.Comment;
import com.orion.orion.models.Photo;
import com.orion.orion.models.TopUsers;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.util.BottomNaavigationViewHelper;
import com.orion.orion.util.SNTPClient;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class Explore extends AppCompatActivity implements AdapterGridImageExplore.OnPostItemClickListner {
    private static final String TAG = "Explore";
    private static final int ACTIVITY_NUM = 1;
    //    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final int SET_SIZE_DOMAIN = 300;
    private static final int TOTAL_USER_SIZE = 500;
    private static final int PAGINATION_SIZE = 6;
    //    private static final int RETRY_DURATION = 30000;
    int x = 0;
    int prevHeight;
    int height, dummyHeight;
    private Context mContext;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private DatabaseReference reference;
    private CircleImageView star1, star2, star3, star4, star5, star6, star7, star8;
    private String user1;
    private String user2;
    private String user3;
    private String user4;
    private String user5;
    private String user6;
    private String user7;
    private String user8;
    private final int c = 0;
    private RelativeLayout collapse;
    private TextView overall;
    private TextView domain;
    private EditText mSearchParam;
    private ListView mListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView exploreRv;
    private AdapterGridImageExplore adapterGridImage;
    private ArrayList<String> topUser8;
    private List<users> mUserList;
    private UserListAdapter mAdapter;
    private ArrayList<Photo> starPhotos;
    private ArrayList<Photo> fieldPhotos;
    private ArrayList<Photo> paginatedPhotos;
    private boolean shuffled = false;
    private ImageView cross, up, down;
    //    private ProgressBar loading;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    //domain
    private String USER_DOMAIN;
    private String SELECTED_FILTER;
    private boolean requestedFromCheckPostFetched;
    private boolean requestedFromSelectListener;
    private boolean photosReady;
    protected boolean isAdapterReady;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Permission Granted", Toast.LENGTH_LONG).show();
            }
    }

    @SuppressLint({"CommitPrefEdits", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started.");
        setContentView(R.layout.activity_explore);
        setupFirebaseAuth();
        getUserDomain();
        initWidgets();
        initOnClickListeners();
        setupBottomNavigationView();
        hideSoftKeyboard();
        initTextListener();
        getTop8();
        checkTopDatabase();
        requestedFromCheckPostFetched = false;
        requestedFromSelectListener = false;
        photosReady = false;
        isAdapterReady = false;
        SELECTED_FILTER = getString(R.string.field_overall);
    }

    private void getUserDomain() {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.dbname_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.field_domain));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    USER_DOMAIN = (String) snapshot.getValue();
                    domain.setText(USER_DOMAIN);
                    if (requestedFromCheckPostFetched && requestedFromSelectListener) {
                        SELECTED_FILTER = USER_DOMAIN;
                        checkLastFetched();
                    } else if (requestedFromCheckPostFetched) checkLastFetched();
                    else if (requestedFromSelectListener) {
                        SELECTED_FILTER = USER_DOMAIN;
                        displayPhotos(SELECTED_FILTER);
                    }
                } else
                    Toast.makeText(mContext, "Unable to find Your domain", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                FirebaseCrashlytics.getInstance().log("Error finding USER_DOMAIN Leaderboard" + error.getMessage());
                Log.e(TAG, "Error finding USER_DOMAIN Leaderboard" + error.getMessage());
                Log.d(TAG, "Error: fetching again");
                getUserDomain();
            }
        });
    }

    @SuppressLint("CommitPrefEdits")
    private void initWidgets() {
        Log.d(TAG, "initWidgets: started");
        mContext = Explore.this;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        reference = FirebaseDatabase.getInstance().getReference();

        topUser8 = new ArrayList<>();
        starPhotos = new ArrayList<>();
        fieldPhotos = new ArrayList<>();
        paginatedPhotos = new ArrayList<>();
        mSearchParam = findViewById(R.id.search);
        mListView = findViewById(R.id.listview);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setRefreshing(true);
        exploreRv = findViewById(R.id.exploreRv);
        star1 = findViewById(R.id.circleImageView2);
        star2 = findViewById(R.id.circleImageView3);
        star3 = findViewById(R.id.circleImageView4);
        star4 = findViewById(R.id.circleImageView6);
        star5 = findViewById(R.id.circleImageView7);
        star6 = findViewById(R.id.circleImageView5);
        star7 = findViewById(R.id.circleImageView);
        star8 = findViewById(R.id.circleImageView8);
        overall = findViewById(R.id.overall);
        domain = findViewById(R.id.domain);
        collapse = findViewById(R.id.collapse);
        cross = findViewById(R.id.cross);
        up = findViewById(R.id.up);
        down = findViewById(R.id.down);
//        loading = findViewById(R.id.progressBar);

        View v = collapse;
        prevHeight = v.getHeight();
        Log.d(TAG, "initWidgets: " + prevHeight + "  " + dummyHeight);
        height = 0;
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 2);
        linearLayoutManager.setItemPrefetchEnabled(true);
        linearLayoutManager.setInitialPrefetchItemCount(20);
        exploreRv.setItemViewCacheSize(15);
        exploreRv.setDrawingCacheEnabled(true);
        exploreRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        exploreRv.setLayoutManager(linearLayoutManager);
    }

    private void initOnClickListeners() {
        Log.d(TAG, "initOnClickListeners: started");

        cross.setOnClickListener(view -> {
            mSearchParam.setText("");
            mUserList.clear();
            if (mAdapter != null) mAdapter.notifyDataSetChanged();
        });
        up.setOnClickListener(view -> {
            up.setVisibility(View.GONE);
            down.setVisibility(View.VISIBLE);
            collapse(collapse, 1000);
        });
        down.setOnClickListener(view -> {
            up.setVisibility(View.VISIBLE);
            down.setVisibility(View.GONE);
            collapse(collapse, 1000);
        });

        overall.setOnClickListener(v -> {
            if (photosReady) {
                overall.setTextColor(getResources().getColor(R.color.colorPrimary));
                domain.setTextColor(getResources().getColor(R.color.black));
                swipeRefreshLayout.setRefreshing(true);
                SELECTED_FILTER = getString(R.string.field_overall);
                displayPhotos(SELECTED_FILTER);
                overall.setClickable(false);
                domain.setClickable(true);
            }
        });
        domain.setOnClickListener(v -> {
            if (photosReady) {
                domain.setTextColor(getResources().getColor(R.color.colorPrimary));
                overall.setTextColor(getResources().getColor(R.color.black));
                requestedFromSelectListener = true;
                if (USER_DOMAIN == null) getUserDomain();
                else {
                    swipeRefreshLayout.setRefreshing(true);
                    requestedFromSelectListener = false;
                    SELECTED_FILTER = USER_DOMAIN;
                    displayPhotos(SELECTED_FILTER);
                    overall.setClickable(true);
                    domain.setClickable(false);
                }
            }
        });
        star1.setOnClickListener(v -> jumpToUser(user1));
        star2.setOnClickListener(v -> jumpToUser(user2));
        star3.setOnClickListener(v -> jumpToUser(user3));
        star4.setOnClickListener(v -> jumpToUser(user4));
        star5.setOnClickListener(v -> jumpToUser(user5));
        star6.setOnClickListener(v -> jumpToUser(user6));
        star7.setOnClickListener(v -> jumpToUser(user7));
        star8.setOnClickListener(v -> jumpToUser(user8));
        exploreRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (paginatedPhotos.size() < fieldPhotos.size() && photosReady) {
                        displayMorePhotos();
                    }
                    if (paginatedPhotos.size() < starPhotos.size() && !photosReady) {
//                        loading.setVisibility(View.VISIBLE);
                        displayMorePhotosTop8();
                    }
                }
            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
            }
        });
        swipeRefreshLayout.setColorSchemeResources(
                R.color.black,
                R.color.scheme2,
                R.color.purple,
                R.color.dark_orange,
                R.color.scheme5,
                R.color.scheme6,
                R.color.scheme7,
                R.color.colorPrimary,
                R.color.scheme9,
                R.color.brown,
                R.color.yellow,
                R.color.red
        );
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (photosReady) {
                Log.d(TAG, "initOnClickListeners: starting");
                swipeRefreshLayout.setRefreshing(true);
                displayPhotos(SELECTED_FILTER);
            } else
                swipeRefreshLayout.setRefreshing(false);
        });
        Log.d(TAG, "initOnClickListeners: completed");
    }


    private void jumpToUser(String toUser) {
        if (toUser != null && !toUser.equals("")) {
            Intent i = new Intent(Explore.this, profile.class);
            i.putExtra(getString(R.string.calling_activity), getString(R.string.home));
            i.putExtra(getString(R.string.intent_user), toUser);
            startActivity(i);
        }
    }

    public void collapse(final View v, int duration) {
        final boolean expand = v.getVisibility() != View.VISIBLE;
        prevHeight = v.getHeight();
        if (x == 0) {
            x++;
            dummyHeight = v.getHeight();
        }
        if (prevHeight == 0) {
            int measureSpecParams = View.MeasureSpec.getSize(View.MeasureSpec.UNSPECIFIED);
            v.measure(measureSpecParams, measureSpecParams);
            height = dummyHeight;
        } else {
            height = 0;
        }
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, height);
        valueAnimator.addUpdateListener(animation -> {
            v.getLayoutParams().height = (int) animation.getAnimatedValue();
            v.requestLayout();
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (expand) v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!expand) v.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    private void checkOrGetLocation() {
        Log.d(TAG, "checkOrGetLocation: started");
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        else {
            Log.d(TAG, "checkOrGetLocation: permission checked");
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.d(TAG, "checkOrGetLocation: gps provider unavailable");
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());
                final AlertDialog alert = builder.create();
                alert.show();
            } else {
                Log.d(TAG, "checkOrGetLocation: gps provider available");
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, location -> {
                    try {
                        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        Log.d(TAG, "checkOrGetLocation: addresses" + addresses);
                        String country = addresses.get(0).getCountryName();
                        String city = addresses.get(0).getSubAdminArea();
                        String area = addresses.get(0).getLocality();
                        Log.d(TAG, "checkOrGetLocation: addresse" + addresses.get(0));
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        assert user != null;
                        reference.child(getString(R.string.dbname_leaderboard)).child(mAuth.getCurrentUser().getUid()).child(getString(R.string.field_last_known_location)).child(getString(R.string.field_city)).setValue(city);
                        reference.child(getString(R.string.dbname_leaderboard)).child(mAuth.getCurrentUser().getUid()).child(getString(R.string.field_last_known_location)).child(getString(R.string.field_country)).setValue(country);
                        reference.child(getString(R.string.dbname_leaderboard)).child(mAuth.getCurrentUser().getUid()).child(getString(R.string.field_last_known_location)).child(getString(R.string.field_area)).setValue(area);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void checkTopDatabase() {
        Log.d(TAG, "checkTopDatabase: started");
        String[] fields = getResources().getStringArray(R.array.domain2);
        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
            @Override
            public void onTimeReceived(String currentTimeStamp) {
                Query query = reference.child(getString(R.string.db_topUsersParams));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(getString(R.string.field_last_updated_topUsers)).getValue() == null) {
                            for (String field : fields) {
                                reference.child(getString(R.string.db_topUsersParams)).child(field).child(getString(R.string.field_completed)).setValue("0/" + SET_SIZE_DOMAIN);
                                createTopDatabase(field, 0);
                            }
                        } else {
                            String previousTimeStamp = (String) snapshot.child(getString(R.string.field_last_updated_topUsers)).getValue();
                            //initializing formatting for current date
                            int currentYear = Integer.parseInt(currentTimeStamp.substring(0, 4));
                            int currentMonth = Integer.parseInt(currentTimeStamp.substring(5, 7));
                            int currentDate = Integer.parseInt(currentTimeStamp.substring(8, 10));
//                            String currentTime = currentTimeStamp.substring(12, currentTimeStamp.length() - 1);
                            String currentDateFormat = currentDate + "/" + currentMonth + "/" + currentYear;
                            Date date = new Date(currentDateFormat);
                            int currentDay = date.getDay();

                            int postedYear = Integer.parseInt(previousTimeStamp.substring(0, 4));
                            int postedMonth = Integer.parseInt(previousTimeStamp.substring(5, 7));
                            int postedDate = Integer.parseInt(previousTimeStamp.substring(8, 10));
//                            String postedTime = previousTimeStamp.substring(12, previousTimeStamp.length() - 1);
                            String postedDateFormat = postedDate + "/" + postedMonth + "/" + postedYear;

                            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");
                            long elapsedDays = 0;
                            try {
                                Date date1 = simpleDateFormat.parse(postedDateFormat);
                                Date date2 = simpleDateFormat.parse(currentDateFormat);
                                Log.d(TAG, "checkTopDatabase: date1 " + date1);
                                Log.d(TAG, "checkTopDatabase: date1 " + date2);
                                assert date1 != null;
                                assert date2 != null;
                                elapsedDays = (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
                                Log.d(TAG, "checkTopDatabase: elapsedDays" + elapsedDays);
                                Log.d(TAG, "checkTopDatabase: currentDay" + currentDay);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            //the week has changed
                            if (elapsedDays > currentDay) {
                                for (String field : fields) {
                                    reference.child(getString(R.string.db_topUsersParams)).child(field).child(getString(R.string.field_completed)).setValue("0/300");
                                    createTopDatabase(field, 0);
                                }
                            } else for (String field : fields)
                                if (snapshot.child(field).getValue() == null)
                                    createTopDatabase(field, 0);
                                else {
                                    String str = String.valueOf(snapshot.child(field).child(getString(R.string.field_completed)).getValue());
                                    if (str.equals("")) createTopDatabase(field, 0);
                                    else {
                                        if (!str.equals("300/300")) {
                                            int index = str.indexOf("/");
                                            if (index != -1) createTopDatabase(field, 0);
                                            else {
                                                int completed = Integer.parseInt(str.substring(0, index));
                                                createTopDatabase(field, completed);
                                            }
                                        }
                                    }
                                }

                        }
                        reference.child(getString(R.string.db_topUsersParams)).child(getString(R.string.field_last_updated_topUsers)).setValue(currentTimeStamp);
                        checkLastFetched();
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
        Log.d(TAG, "checkTopDatabase: completed");
    }

    private void checkLastFetched() {
        Log.d(TAG, "checkLastFetched: started");
        requestedFromCheckPostFetched = true;
        if (USER_DOMAIN == null) getUserDomain();
        else {
            requestedFromCheckPostFetched = false;
            String[] fields = {getString(R.string.field_overall), USER_DOMAIN};
            for (String field : fields) {
                String previousTimeStamp = mPreferences.getString(field + "_fieldLastFetched", null);
                if (previousTimeStamp == null || previousTimeStamp.equals("")) {
                    Log.d(TAG, "checkLastFetched: starting fetching users as previousTimeStamp is null or not found - " + field);
                    fetchTopUsers(field);
                } else {
                    SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
                        @Override
                        public void onTimeReceived(String currentTimeStamp) {
                            int currentYear = Integer.parseInt(currentTimeStamp.substring(0, 4));
                            int currentMonth = Integer.parseInt(currentTimeStamp.substring(5, 7));
                            int currentDate = Integer.parseInt(currentTimeStamp.substring(8, 10));
//                        String currentTime = currentTimeStamp.substring(12, currentTimeStamp.length() - 1);
                            String currentDateFormat = currentDate + "/" + currentMonth + "/" + currentYear;
                            Date date = new Date(currentDateFormat);
                            int currentDay = date.getDay();
                            int postedYear = Integer.parseInt(previousTimeStamp.substring(0, 4));
                            int postedMonth = Integer.parseInt(previousTimeStamp.substring(5, 7));
                            int postedDate = Integer.parseInt(previousTimeStamp.substring(8, 10));
//                        String postedTime = previousTimeStamp.substring(12, previousTimeStamp.length() - 1);
                            String postedDateFormat = postedDate + "/" + postedMonth + "/" + postedYear;
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");
                            long elapsedDays = 0;
                            try {
                                Date date1 = simpleDateFormat.parse(postedDateFormat);
                                Date date2 = simpleDateFormat.parse(currentDateFormat);
//                            Log.d(TAG, "checkLastFetched: date1 " + date1);
//                            Log.d(TAG, "checkLastFetched: date2 " + date2);
                                assert date1 != null;
                                assert date2 != null;
                                elapsedDays = (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
//                            Log.d(TAG, "checkLastFetched:elapsedDays" + elapsedDays);
//                            Log.d(TAG, "checkLastFetched:currentDay" + currentDay);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Log.d(TAG, "checkLastFetched: starting fetching users as we ran into error finding timestamp - " + field);
                                fetchTopUsers(field);
                                if (field.equals(getString(R.string.field_overall)))
                                    fetchTopUsers(field);
                            }
                            if (elapsedDays > currentDay) {
                                Log.d(TAG, "checkLastFetched: starting fetching users as database is of last week - " + field);
                                fetchTopUsers(field);
                            } else if (field.equals(USER_DOMAIN)) checkPostsFetched();
                        }

                        @Override
                        public void onError(Exception ex) {
                            Log.d(TAG, "onError: SNTPClient fetching TopUsers from shared Preferences" + ex.getMessage());
                            Log.d(TAG, "fetching again");
                            fetchTopUsers(field);
                        }
                    });
                }
            }
        }
    }

    private void checkPostsFetched() {
        Log.d(TAG, "checkPostsFetched: started");
        String[] fields = {getString(R.string.field_overall), USER_DOMAIN};
        if (USER_DOMAIN == null) getUserDomain();
        for (String field : fields) {
            String previousTimeStamp = mPreferences.getString(field + "_PostsLastUpdated", null);
            String json = mPreferences.getString(field + "_TopPosts", null);
            Set<String> set = mPreferences.getStringSet(field + "_TopUsers", null);
            int completed = mPreferences.getInt(field + "_completed", 0);
            if (set != null) {
                if (json == null || previousTimeStamp == null || previousTimeStamp.equals("")) {
                    Log.d(TAG, "checkPostsFetched: fetching photos as previous timestamp is null - " + field);
                    getPosts(field, completed);
                } else {
                    if (completed < set.size()) getPosts(field, completed);
                    else {
                        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
                            @Override
                            public void onTimeReceived(String currentTimeStamp) {
                                int currentYear = Integer.parseInt(currentTimeStamp.substring(0, 4));
                                int currentMonth = Integer.parseInt(currentTimeStamp.substring(5, 7));
                                int currentDate = Integer.parseInt(currentTimeStamp.substring(8, 10));
//                                String currentTime = currentTimeStamp.substring(12, currentTimeStamp.length() - 1);
                                String currentDateFormat = currentDate + "/" + currentMonth + "/" + currentYear;
                                Date date = new Date(currentDateFormat);
                                int currentDay = date.getDay();
                                int postedYear = Integer.parseInt(previousTimeStamp.substring(0, 4));
                                int postedMonth = Integer.parseInt(previousTimeStamp.substring(5, 7));
                                int postedDate = Integer.parseInt(previousTimeStamp.substring(8, 10));
//                                String postedTime = previousTimeStamp.substring(12, previousTimeStamp.length() - 1);
                                String postedDateFormat = postedDate + "/" + postedMonth + "/" + postedYear;
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");
                                long elapsedDays = 0;
                                try {
                                    Date date1 = simpleDateFormat.parse(postedDateFormat);
                                    Date date2 = simpleDateFormat.parse(currentDateFormat);
                                    assert date1 != null;
                                    assert date2 != null;
                                    elapsedDays = (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    Log.d(TAG, "checkPostsFetched: fetching photos as we ran into error - " + field);
                                    getPosts(field, completed);
                                }
                                if (elapsedDays > currentDay) {
                                    Log.d(TAG, "checkPostsFetched: fetching photos local database is outdated - " + field);
                                    getPosts(field, completed);
                                } else {
                                    if (field.equals(USER_DOMAIN)) {
                                        displayPhotos(SELECTED_FILTER);
                                    }
                                }
                            }

                            @Override
                            public void onError(Exception ex) {
                                Log.d(TAG, "onError: SNTPClient fetching TopUsers from shared Preferences" + ex.getMessage());
                                Log.d(TAG, "checkPostsFetched:fetching photos as we ran into error - " + field);
                                getPosts(field, completed);
                            }
                        });
                    }
                }
            }
        }
//        displayPhotos(SELECTED_FILTER);
    }

    private void createTopDatabase(String field, int completed) {
        Log.d(TAG, "createTopDatabase: started");
        Log.d(TAG, "createTopDatabase: field " + field);
        ArrayList<TopUsers> mList = new ArrayList<>();
        mList.clear();
        reference.child(getString(R.string.explore_update)).removeValue();
        Query query = reference.child(getString(R.string.dbname_leaderboard));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                    //getting domain, user ids and rating
                    String domain = (String) singleSnapshot.child(getString(R.string.field_domain)).getValue();
                    int rating = (int) (long) singleSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_post)).getValue()
                            + (int) (long) singleSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_followers)).getValue()
                            + (int) (long) singleSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_contest)).getValue();
                    String user_id = singleSnapshot.getKey();
                    if (field.equals(getString(R.string.field_overall))) {
                        //creating top SET_SIZE_DOMAIN list using insertion sort algorithm
                        if (mList.size() == 0) mList.add(new TopUsers(user_id, rating));
                        else {
                            int l = mList.size();
                            //loop to push in between and next one further away for arraylist
                            for (int i = 0; i < l; i++) {
                                int r = mList.get(i).getRat();
                                if (rating >= r) {
                                    mList.add(new TopUsers());
                                    for (int j = mList.size() - 1; j > i; j--)
                                        mList.set(j, mList.get(j - 1));
                                    mList.set(i, new TopUsers(user_id, rating));
                                    break;
                                }
                                //pushing at the end
                                else if (i == l - 1)
                                    mList.add(new TopUsers(user_id, rating));
                            }
                            if (mList.size() == SET_SIZE_DOMAIN + 1) mList.remove(SET_SIZE_DOMAIN);
                        }
                    } else if (domain != null && domain.equals(field)) {
                        //creating top SET_SIZE_DOMAIN list using insertion sort algorithm
                        if (mList.size() == 0) mList.add(new TopUsers(user_id, rating));
                        else {
                            int l = mList.size();
                            //loop to push in between and next one further away for arraylist
                            for (int i = 0; i < l; i++) {
                                int r = mList.get(i).getRat();
                                if (rating >= r) {
                                    mList.add(new TopUsers());
                                    for (int j = mList.size() - 1; j > i; j--)
                                        mList.set(j, mList.get(j - 1));
                                    mList.set(i, new TopUsers(user_id, rating));
                                    break;
                                }
                                //pushing at the end
                                else if (i == l - 1)
                                    mList.add(new TopUsers(user_id, rating));
                            }
                            if (mList.size() == SET_SIZE_DOMAIN + 1) mList.remove(SET_SIZE_DOMAIN);
                        }
                    }
                }
                //debugging logs
                Log.d(TAG, "createTopDatabase: mList.size()" + field + mList.size());
                Log.d(TAG, "createTopDatabase: starting upload for Domain field");

                //adding the list fetched to database
                for (int i = completed; i < SET_SIZE_DOMAIN; i++) {
                    if (mList.size() == 0) {
                        reference.child(getString(R.string.db_topUsersParams)).child(field).child(getString(R.string.field_completed)).setValue("300/300");
                    }
                    String value;
                    if (i < mList.size()) {
                        value = mList.get(i).getUi();
                        Log.d(TAG, "createTopDatabase: key, value" + (i + 1) + ", " + value);
                        reference.child(getString(R.string.db_topUsersParams)).child(field).child(String.valueOf(i + 1)).setValue(value);
                        reference.child(getString(R.string.db_topUsersParams)).child(field).child(getString(R.string.field_completed)).setValue((i + 1) + "/300");
                        if (i == mList.size() - 1) {
                            reference.child(getString(R.string.db_topUsersParams)).child(field).child(getString(R.string.field_completed)).setValue("300/300");
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchTopUsers(String firstField) {
        Log.d(TAG, firstField + ": fetchTopUsers: started");
        ArrayList<String> mTopUsersList = new ArrayList<>();
        Query query = reference.child(getString(R.string.db_topUsersParams)).child(firstField);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                        String key = singleSnapshot.getKey();
                        String userID = String.valueOf(singleSnapshot.getValue());
                        assert key != null;
                        if (!key.equals(getString(R.string.field_completed))) {
                            if (!userID.equals("") && !mTopUsersList.contains(userID) && !FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userID) && !userID.equals(getString(R.string.orion_team_user_id)))
                                mTopUsersList.add(userID);
                        } else {
                            Log.d(TAG, firstField + ": fetchTopUsers: size after first field - " + mTopUsersList.size());
                            Query query1 = reference.child(getString(R.string.dbname_leaderboard)).child(mUser.getUid());
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String currentUserCity = String.valueOf(snapshot.child(getString(R.string.field_last_known_location)).child(getString(R.string.field_city)).getValue());
                                        if (currentUserCity.equals("")) checkOrGetLocation();
                                        int currentUserRating = (int) (long) snapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_post)).getValue()
                                                + (int) (long) snapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_followers)).getValue()
                                                + (int) (long) snapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_contest)).getValue();
                                        Log.d(TAG, firstField + ": fetchTopUsers: location - " + currentUserCity + " : " + currentUserRating);
                                        Query query11 = reference.child(getString(R.string.dbname_leaderboard));
                                        query11.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                                                        String userID = singleSnapshot.getKey();
                                                        String userCity = String.valueOf(singleSnapshot.child(getString(R.string.field_last_known_location)).child(getString(R.string.field_city)).getValue());
                                                        assert userID != null;
                                                        if (!userID.equals(mUser.getUid()) && userCity.equals(currentUserCity) && !mTopUsersList.contains(userID)) {
                                                            if (!userID.equals("") && !mTopUsersList.contains(userID) && !FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userID) && !userID.equals(getString(R.string.orion_team_user_id)))
                                                                mTopUsersList.add(userID);
                                                        }
                                                    }
                                                    Log.d(TAG, firstField + ": fetchTopUsers: size after location - " + mTopUsersList.size());

//                                                    if (mTopUsersList.size() < TOTAL_USER_SIZE) {
//                                                        Query query2 = reference.child(getString(R.string.dbname_users)).child(mUser.getUid()).child(getString(R.string.field_domain));
//                                                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                            @Override
//                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                if (snapshot.exists()) {
//                                                                    String domain = String.valueOf(snapshot.getValue());
//                                                                    if (!domain.equals("")) {
//                                                                        if (!secondField.equals(""))
//                                                                            domain = secondField;
//                                                                        Query query22 = reference.child(getString(R.string.db_topUsersParams)).child(domain);
//                                                                        query22.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                            @Override
//                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                                if (snapshot.exists()) {
//                                                                                    for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
//                                                                                        String key = singleSnapshot.getKey();
//                                                                                        String userID = String.valueOf(singleSnapshot.getValue());
//                                                                                        assert key != null;
//                                                                                        if (!key.equals(getString(R.string.field_completed))) {
//                                                                                            if (!userID.equals("") && !mTopUsersList.contains(userID)) {
//                                                                                                Log.d(TAG, "fetchTopUsers: adding left" + firstField + " : " + mTopUsersList);
//                                                                                                if (!userID.equals("") && !mTopUsersList.contains(userID) && !FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userID))
//                                                                                                    mTopUsersList.add(userID);
//                                                                                            }
//                                                                                        }
//                                                                                    }
//                                                                                    Log.d(TAG, "fetchTopUsers: users added via second field - " + mTopUsersList.size());
//                                                                                    SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
//                                                                                        @Override
//                                                                                        public void onTimeReceived(String currentTimeSTamp) {
//                                                                                            Set<String> set = new HashSet<>(mTopUsersList);
//                                                                                            Log.d(TAG, "fetchTopUsers: adding set of " + set.size() + " on field " + firstField);
//                                                                                            mEditor.putString(firstField + "_fieldLastFetched", currentTimeSTamp);
//                                                                                            mEditor.putStringSet(firstField + "_TopUsers", set);
//                                                                                            mEditor.apply();
//                                                                                            if (firstField.equals(USER_DOMAIN))
//                                                                                                checkPostsFetched();
//                                                                                            Log.d(TAG, "fetchTopUsers: total users added - " + mTopUsersList.size());
//                                                                                        }
//
//                                                                                        @Override
//                                                                                        public void onError(Exception ex) {
//                                                                                            Log.d(TAG, "onError: SNTPClient updating shared preferences" + ex.getMessage());
//                                                                                        }
//                                                                                    });
//
//                                                                                }
//                                                                            }
//
//                                                                            @Override
//                                                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                            }
//                                                                        });
//                                                                    }
//                                                                }
//
//                                                            }
//
//                                                            @Override
//                                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                                            }
//                                                        });
//                                                    } else {
//                                                    Log.d(TAG, "fetchTopUsers: users added via second field - " + firstField + " -> " + mTopUsersList.size());
                                                    SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
                                                        @Override
                                                        public void onTimeReceived(String currentTimeSTamp) {
                                                            Set<String> set = new HashSet<>(mTopUsersList);
                                                            mEditor.putString(firstField + "_fieldLastFetched", currentTimeSTamp);
                                                            mEditor.putStringSet(firstField + "_TopUsers", set);
                                                            mEditor.apply();
                                                            if (firstField.equals(USER_DOMAIN))
                                                                checkPostsFetched();
                                                            Log.d(TAG, firstField + ": fetchTopUsers: added set of  - " + set.size());
                                                        }

                                                        @Override
                                                        public void onError(Exception ex) {
                                                            Log.d(TAG, firstField + ": onError: SNTPClient updating shared preferences: " + ex.getMessage());
                                                        }
                                                    });
                                                }
//                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.d(TAG, firstField + ": onCancelled: Error: " + error.getMessage());
                                            }
                                        });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d(TAG, firstField + ": onCancelled: Error: " + error.getMessage());
                                }

                            });

                        }
                    }
                } else
                    SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
                        @Override
                        public void onTimeReceived(String currentTimeSTamp) {
                            Set<String> set = new HashSet<>(mTopUsersList);
                            mEditor.putString(firstField + "_fieldLastFetched", currentTimeSTamp);
                            mEditor.putStringSet(firstField + "_TopUsers", set);
                            mEditor.apply();
                            if (firstField.equals(USER_DOMAIN))
                                checkPostsFetched();
                            Log.d(TAG, firstField + ": fetchTopUsers: added set of  - " + set.size());
                        }

                        @Override
                        public void onError(Exception ex) {
                            Log.d(TAG, firstField + ": onError: SNTPClient updating shared preferences: " + ex.getMessage());
                        }
                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, firstField + ": onCancelled: Error: " + error.getMessage());
            }
        });
    }

    private void getPosts(String field, int startingIndex) {
        Log.d(TAG, field + ": getPosts: started");
        Set<String> set = mPreferences.getStringSet(field + "_TopUsers", null);
        if (set != null) {
            ArrayList<String> mTopUsersList = new ArrayList<>(set);
            Gson gson = new Gson();
            String json = mPreferences.getString(field + "_TopPosts", null);
            ArrayList<Photo> fieldPhotos;
            if (json == null) fieldPhotos = new ArrayList<>();
            else {
                Type type = new TypeToken<List<Photo>>() {
                }.getType();
                fieldPhotos = gson.fromJson(json, type);
            }
            Log.d(TAG, field + ": getPosts: found photos of size  - " + fieldPhotos.size() + " for users - " + set.size());
            SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
                @Override
                public void onTimeReceived(String currentTimeSTamp) {
                    if (mTopUsersList.size() == 0) {
                        Gson gson = new Gson();
                        String json = gson.toJson(fieldPhotos);
                        mEditor.putString(field + "_PostsLastUpdated", currentTimeSTamp);
                        mEditor.putString(field + "_TopPosts", json);
                        mEditor.putInt(field + "_completed", 0);
                        mEditor.apply();
                        displayPhotos(SELECTED_FILTER);
                        Log.d(TAG, field + ": getPosts: uploading photos of - " + fieldPhotos.size());
                    } else for (int i = startingIndex; i < mTopUsersList.size(); i++) {
                        String userId = mTopUsersList.get(i);
                        Log.d(TAG, field + ": getPosts: fetching userId - " + userId);
                        Query query = reference.child(getString(R.string.dbname_user_photos)).child(userId);
                        int finalI = i;
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot singleSnapshot : snapshot.getChildren())
                                        if (singleSnapshot.exists()) {
                                            Photo photo = singleSnapshot.getValue(Photo.class);
                                            boolean exists = false;
                                            for (Photo existingPhoto : fieldPhotos) {
                                                if (photo.equals(existingPhoto)) {
                                                    exists = true;
                                                    break;
                                                }
                                            }
                                            if (!exists) fieldPhotos.add(photo);
                                        }
                                    Gson gson = new Gson();
                                    String json = gson.toJson(fieldPhotos);
                                    mEditor.putString(field + "_PostsLastUpdated", currentTimeSTamp);
                                    mEditor.putString(field + "_TopPosts", json);
                                    if (finalI == mTopUsersList.size() - 1)
                                        mEditor.putInt(field + "_completed", mTopUsersList.size());
                                    else mEditor.putInt(field + "_completed", finalI);
                                    mEditor.apply();
                                    displayPhotos(SELECTED_FILTER);
                                    Log.d(TAG, field + ": getPosts: uploading photos of size - " + fieldPhotos.size());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d(TAG, field + ": getPosts: Error - " + error.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception ex) {
                    Log.d(TAG, field + ": getPosts: Error - " + ex.getMessage());
                }
            });
        }
    }

    private void getTop8() {
        Query query = reference.child(getString(R.string.db_topUsersParams)).child(getString(R.string.field_overall)).limitToFirst(8);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot singleSnapshot : snapshot.getChildren())
                        if (!singleSnapshot.getValue().toString().equals("") && !singleSnapshot.getKey().equals(getString(R.string.field_completed)))
                            topUser8.add(singleSnapshot.getValue().toString());
                    Log.d(TAG, "getTop8: topUser" + topUser8);
                    getStarImage(topUser8);
                    String json = mPreferences.getString(getString(R.string.field_overall) + "_TopPosts", null);
                    if (json == null || json.equals(""))
                        getStarPhotos(topUser8);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getStarPhotos(ArrayList<String> user_ids) {
        Log.d(TAG, "getStarPhotos: started");
        Log.d(TAG, "getStarImage: " + user_ids);
        for (String user_id : user_ids) {
            if (!user_id.equals(mUser.getUid()))
                reference.child(getString(R.string.dbname_user_photos))
                        .child(user_id)
                        .limitToLast(5)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists())
                                    for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                                        if (singleSnapshot.exists()) {
                                            Photo photo = singleSnapshot.getValue(Photo.class);
                                            starPhotos.add(photo);
                                            if (isAdapterReady && paginatedPhotos.size() < PAGINATION_SIZE) {
                                                paginatedPhotos.add(photo);
                                                adapterGridImage.notifyItemInserted(paginatedPhotos.size() - 1);
                                            } else if (!isAdapterReady) displayPhotosTop8();
                                        }
                                    }
                                Log.d(TAG, "getStarPhotos: " + starPhotos.size());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
        }
    }

    private void displayPhotosTop8() {
        isAdapterReady = true;
        Log.d(TAG, "displayPhotosTop8: fetching photos of - " + starPhotos.size());
        try {
            if (starPhotos.size() != 0) {
                runOnUiThread(() -> findViewById(R.id.noPost).setVisibility(View.GONE));
                paginatedPhotos = new ArrayList<>();
                for (int i = 0; i < starPhotos.size(); i++) {
                    paginatedPhotos.add(starPhotos.get(i));
                    if (i == starPhotos.size() - 1 || i == PAGINATION_SIZE - 1) {
                        Log.d(TAG, "displayPhotosTop8: paginatedPhotos - " + paginatedPhotos.size());
                        adapterGridImage = new AdapterGridImageExplore(mContext, paginatedPhotos, this);
                        ((SimpleItemAnimator) exploreRv.getItemAnimator()).setSupportsChangeAnimations(false);
                        swipeRefreshLayout.setRefreshing(false);
                        adapterGridImage.setHasStableIds(true);
                        exploreRv.post(() -> exploreRv.setAdapter(adapterGridImage));
                        break;
                    }
                }
            } else runOnUiThread(() -> {
                swipeRefreshLayout.setRefreshing(false);
                findViewById(R.id.noPost).setVisibility(View.VISIBLE);
            });

        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());
            swipeRefreshLayout.setRefreshing(false);
            findViewById(R.id.noPost).setVisibility(View.VISIBLE);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());
            swipeRefreshLayout.setRefreshing(false);
            findViewById(R.id.noPost).setVisibility(View.VISIBLE);
        }
    }

    private void displayMorePhotosTop8() {
        Log.d(TAG, "displayMorePhotosTop8: started");
        Log.d(TAG, "displayMorePhotosTop8: paginatedPhotos - " + paginatedPhotos.size());
        Log.d(TAG, "displayMorePhotosTop8: starPhotos - " + starPhotos.size());
        int l = paginatedPhotos.size();
        int addCount = PAGINATION_SIZE;
        if (l % 2 == 1) addCount++;
        try {
            for (int i = l; i < starPhotos.size(); i++) {
                if (i == starPhotos.size() || i == l + addCount) {
                    int itemCount = (i == starPhotos.size()) ? (starPhotos.size() - l) : addCount;
                    Log.d(TAG, "displayMorePhotosTop8: itemcount" + itemCount);
                    exploreRv.post(() -> {
                        adapterGridImage.notifyItemRangeInserted(l, itemCount);
//                        loading.setVisibility(View.GONE);
                    });
                    break;
                } else paginatedPhotos.add(starPhotos.get(i));
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());

        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());
        }
    }

    private void getStarImage(ArrayList<String> user_id) {
        Log.d(TAG, "getStarImage: " + user_id);
        reference.child(getString(R.string.dbname_users)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int x = 0; x < user_id.size(); x++) {
                    if (x == 0) {
                        user1 = user_id.get(x);
                        setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star1);
                    }
                    if (x == 1) {
                        user2 = user_id.get(x);
                        setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star2);
                    }
                    if (x == 2) {
                        user3 = user_id.get(x);
                        setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star3);
                    }
                    if (x == 3) {
                        user4 = user_id.get(x);
                        setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star4);
                    }
                    if (x == 4) {
                        user5 = user_id.get(x);
                        setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star5);
                    }
                    if (x == 5) {
                        user6 = user_id.get(x);
                        setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star6);
                    }
                    if (x == 6) {
                        user7 = user_id.get(x);
                        setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star7);
                    }
                    if (x == 7) {
                        user8 = user_id.get(x);
                        setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setImage(String link, CircleImageView imageView) {
        Glide.with(getApplicationContext())
                .load(link)
                .placeholder(R.drawable.load)
                .error(R.drawable.default_image2)
                .placeholder(R.drawable.load)
                .thumbnail(0.5f)
                .into(imageView);
    }

    private void displayPhotos(String field) {
        Log.d(TAG, "displayPhotos: started");
//        if (!photosReady) {
//            AlertDialog alertDialog = new AlertDialog.Builder(this)
//                    .setTitle("Get Ready")
//                    .setMessage("We have fetched some data according to your personalized liking")
//                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss())
//                    .show();
//            alertDialog.show();
//        }
        requestedFromSelectListener = false;
        photosReady = true;
        findViewById(R.id.noPost).setVisibility(View.GONE);
        if (swipeRefreshLayout.isRefreshing()) {
            Log.d(TAG, field + ": displayPhotos: started");
            paginatedPhotos.clear();
            fieldPhotos.clear();
            if (adapterGridImage != null) adapterGridImage.notifyDataSetChanged();
            Gson gson = new Gson();
            String json = mPreferences.getString(field + "_TopPosts", null);
            Log.d(TAG, field + ": displayPhotos: json     " + json);
            if (json != null && !json.equals("")) {
                Type type = new TypeToken<List<Photo>>() {
                }.getType();
                fieldPhotos = gson.fromJson(json, type);
                Log.d(TAG, field + ": photos retrieved " + fieldPhotos.size());
                Query query = reference.child(getString(R.string.explore_update));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(getString(R.string.field_last_updated)).exists()) {
                            Log.d(TAG, field + ": displayPhotos: checking if posts need to be deleted");
                            String previousTimeStamp = String.valueOf(snapshot.child(getString(R.string.field_last_updated)).getValue());
                            SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
                                @Override
                                public void onTimeReceived(String currentTimeStamp) {
                                    int currentYear = Integer.parseInt(currentTimeStamp.substring(0, 4));
                                    int currentMonth = Integer.parseInt(currentTimeStamp.substring(5, 7));
                                    int currentDate = Integer.parseInt(currentTimeStamp.substring(8, 10));
//                                    String currentTime = currentTimeStamp.substring(12, currentTimeStamp.length() - 1);
                                    String currentDateFormat = currentDate + "/" + currentMonth + "/" + currentYear;
                                    Date date = new Date(currentDateFormat);
                                    int currentDay = date.getDay();

                                    int postedYear = Integer.parseInt(previousTimeStamp.substring(0, 4));
                                    int postedMonth = Integer.parseInt(previousTimeStamp.substring(5, 7));
                                    int postedDate = Integer.parseInt(previousTimeStamp.substring(8, 10));
//                                    String postedTime = previousTimeStamp.substring(12, previousTimeStamp.length() - 1);
                                    String postedDateFormat = postedDate + "/" + postedMonth + "/" + postedYear;

                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");
                                    long elapsedDays = 0;
                                    try {
                                        Date date1 = simpleDateFormat.parse(postedDateFormat);
                                        Date date2 = simpleDateFormat.parse(currentDateFormat);
                                        assert date1 != null;
                                        assert date2 != null;
                                        elapsedDays = (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        fetchPhotos();
                                    }

                                    if (elapsedDays <= currentDay) {
                                        Log.d(TAG, field + ": displayPhotos: deleting posts");
                                        ArrayList<Photo> fieldPhotos2 = new ArrayList<>(fieldPhotos);
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            for (Photo photo : fieldPhotos)
                                                if (photo.getPi().equals(dataSnapshot.getKey())) {
                                                    fieldPhotos2.remove(photo);
                                                    break;
                                                }
                                        }
                                        Log.d(TAG, field + ": displayPhotos: " + fieldPhotos2.size());
                                        Gson gson = new Gson();
                                        String json = gson.toJson(fieldPhotos2);
                                        mEditor.putString(field + "_PostsLastUpdated", currentTimeStamp);
                                        mEditor.putString(field + "_TopPosts", json);
                                        mEditor.putInt(field + "_completed", fieldPhotos2.size());
                                        mEditor.apply();
                                        Log.d(TAG, field + ": displayPhotos: uploading after deletion" + fieldPhotos2.size() + " photos for " + field);
                                    }
                                    fetchPhotos();
                                }

                                @Override
                                public void onError(Exception ex) {
                                    Log.d(TAG, field + ": onError: SNTPClient fetching TopUsers from shared Preferences" + ex.getMessage());
                                    Log.d(TAG, field + ": fetching again");
                                    fetchPhotos();
                                }
                            });
                        } else fetchPhotos();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                swipeRefreshLayout.setRefreshing(false);
                findViewById(R.id.noPost).setVisibility(View.VISIBLE);
            }
        }
    }

    private void fetchPhotos() {
        Log.d(TAG, "fetchPhotos: fetching photos of - " + fieldPhotos.size());
        try {
            paginatedPhotos.clear();
            if (fieldPhotos.size() != 0) {
                runOnUiThread(() -> findViewById(R.id.noPost).setVisibility(View.GONE));
                if (!shuffled) {
                    Collections.shuffle(fieldPhotos);
                    shuffled = true;
                }
                paginatedPhotos = new ArrayList<>();
                for (int i = 0; i < fieldPhotos.size(); i++) {
                    paginatedPhotos.add(fieldPhotos.get(i));
                    if (i == fieldPhotos.size() - 1 || i == 8) {
                        Log.d(TAG, "fetchPhotos: paginatedPhotos - " + paginatedPhotos.size());
                        adapterGridImage = new AdapterGridImageExplore(mContext, paginatedPhotos, this);
                        ((SimpleItemAnimator) exploreRv.getItemAnimator()).setSupportsChangeAnimations(false);
                        swipeRefreshLayout.setRefreshing(false);
                        adapterGridImage.setHasStableIds(true);
                        exploreRv.post(() -> exploreRv.setAdapter(adapterGridImage));
                        break;
                    }
                }
            } else runOnUiThread(() -> {
                swipeRefreshLayout.setRefreshing(false);
                findViewById(R.id.noPost).setVisibility(View.VISIBLE);
            });

        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());
            swipeRefreshLayout.setRefreshing(false);
            findViewById(R.id.noPost).setVisibility(View.VISIBLE);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());
            swipeRefreshLayout.setRefreshing(false);
            findViewById(R.id.noPost).setVisibility(View.VISIBLE);
        }

    }

    public void displayMorePhotos() {
        Log.d(TAG, "displayMorePhotos: started");
        int l = paginatedPhotos.size();
        Log.d(TAG, "displayMorePhotos: photos retrieved " + fieldPhotos.size());
        try {
            if (paginatedPhotos.size() <= fieldPhotos.size()) {
                for (int i = l - 1; i < fieldPhotos.size(); i++) {
                    if (i == fieldPhotos.size() - 1 || i == l + 11) {
                        Log.d(TAG, "displayMorePhotos: paginatedPhotos" + paginatedPhotos.size());
                        int itemCount = (i == fieldPhotos.size() - 1) ? (fieldPhotos.size() - l) : 12;
                        Log.d(TAG, "displayMorePhotos: itemcount" + itemCount);
                        exploreRv.post(() -> {
                            // Notify adapter with appropriate notify methods
                            adapterGridImage.notifyItemRangeInserted(l - 1, itemCount);

                        });
                        break;
                    } else paginatedPhotos.add(fieldPhotos.get(i));
                }
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "Null pointer exception" + e.getMessage());

        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "index out of bound" + e.getMessage());
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
            cross.setVisibility(View.GONE);

        } else {
            cross.setVisibility(View.VISIBLE);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getString(R.string.dbname_username)).orderByKey()
                    .startAt(keyword).endAt(keyword + "\uf8ff");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                        reference.child(getString(R.string.dbname_users))
                                .child(singleSnapshot.getValue().toString())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        mUserList.add(snapshot.getValue(users.class));
                                        updateUserList();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

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
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            Log.d(TAG, "selected user" + mUserList.get(position).toString());
            Intent intent = new Intent(Explore.this, profile.class);
            intent.putExtra(getString(R.string.calling_activity), getString(R.string.search_activity));
            intent.putExtra(getString(R.string.intent_user), mUserList.get(position).getUi());
            startActivity(intent);
        });

    }

    private void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: started");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.BottomNavViewBar);
        BottomNaavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNaavigationViewHelper.enableNavigation(Explore.this, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
        Log.d(TAG, "setupBottomNavigationView: completed");
    }

    @Override
    public void onItemClick(int position) {
        Photo photo = paginatedPhotos.get(position);
        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference();
        db1.child(mContext.getString(R.string.dbname_user_photos)).child(photo.getUi()).child(photo.getPi()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Comment> comments = new ArrayList<>();
                for (DataSnapshot dSnapshot : snapshot.child("comment").getChildren()) {
                    Comment comment = new Comment();
                    comment.setUi(dSnapshot.getValue(Comment.class).getUi());
                    comment.setC(dSnapshot.getValue(Comment.class).getC());
                    comment.setDc(dSnapshot.getValue(Comment.class).getDc());
                    comments.add(comment);
                }
                Log.d(Constraints.TAG, "onDataChange: klj" + comments);
                Intent i1 = new Intent(mContext, ViewPostActivity.class);
                i1.putExtra("photo", photo);
                i1.putParcelableArrayListExtra("comments", comments);
                mContext.startActivity(i1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                            Intent intent = new Intent(mContext, login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            settings.edit().clear().apply();
                            if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
                            startActivity(intent);
                        })
                        .show();
            } else Log.d(TAG, "onAuthStateChanged: signed_in:" + mUser.getUid());
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
        if (mAuthListener != null) mAuth.removeAuthStateListener(mAuthListener);
    }
}