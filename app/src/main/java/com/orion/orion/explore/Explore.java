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
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.orion.orion.Adapters.AdapterGridImageExplore;
import com.orion.orion.Adapters.UserListAdapter;
import com.orion.orion.R;
import com.orion.orion.ViewPostActivity;
import com.orion.orion.dialogs.BottomSheetDomain;
import com.orion.orion.models.Comment;
import com.orion.orion.models.Photo;
import com.orion.orion.models.TopUsers;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;
import com.orion.orion.util.BottomNaavigationViewHelper;
import com.orion.orion.util.SNTPClient;
import com.orion.orion.util.UniversalImageLoader;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class Explore extends AppCompatActivity implements BottomSheetDomain.BottomSheetListener, AdapterGridImageExplore.OnPostItemClickListner {
    private static final String TAG = "Explore";
    private static final int ACTIVITY_NUM = 1;
    private static int RETRY_DURATION = 1000;
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private Context mContext;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private DatabaseReference reference;
    int x = 0;
    private CircleImageView star1, star2, star3, star4, star5, star6, star7, star8;
    private String user1;
    private String user2;
    private String user3;
    private String user4;
    private String user5;
    private String user6;
    private String user7;
    private String user8;
    private int c = 0;
    private RelativeLayout topBox;
    private RelativeLayout collapse;
    private TextView spinner;
    private EditText mSearchParam;
    private ListView mListView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView exploreRv;
    private AdapterGridImageExplore adapterGridImage;

    private ArrayList<String> topUser8;
    private List<users> mUserList;
    private UserListAdapter mAdapter;
    private ArrayList<Photo> fieldPhotos;
    private ArrayList<Photo> paginatedPhotos;
    private boolean shuffled = false;
    private ImageView cross,up,down;

    int prevHeight;
    int height, dummyHeight;

    private FusedLocationProviderClient fusedLocationClient;

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
initWidgets();
        initOnClickListeners();


//        topBox.setOnClickListener(v -> {
//            if (v.getId() != spinner.getId()) exploreRv.post(() -> expand(collapse, 500));
//        });
//        topBox.setOnTouchListener((v, event) -> {
//            if (v.getId() != collapse.getId())
//                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_UP) {
//                    exploreRv.post(() -> expand(collapse, 500));
//                    return true;
//                }
//            return false;
//
//        });

        exploreRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (paginatedPhotos.size() < fieldPhotos.size()) displayMorePhotos();
                } else if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d(TAG, "onScrollStateChanged: top");
//                    if(collapse.getVisibility()!=View.VISIBLE) exploreRv.post(() -> expand(collapse, 500));
                }
            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    // Recycle view scrolling down...
                    Log.d(TAG, "onScrolled: down");
                    if (collapse.getVisibility() == View.VISIBLE) ;
//                        exploreRv.post(() -> expand(collapse, 500));
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this::displayPhotos);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);


        setupBottomNavigationView();
        hideSoftKeyboard();
        initTextListener();
        getTop8();
        checkTopDatabase();
//        checkLastFetched();
//        checkPostsFetched();
//        displayPhotos();
//        handler.postDelayed(this::checkLastFetched, RETRY_DURATION);
//        handler.postDelayed(this::checkPostsFetched, RETRY_DURATION);
//        handler.postDelayed(this::displayPhotos, RETRY_DURATION);
//
    }

    private void initWidgets() {
        Log.d(TAG, "initWidgets: started");
        mContext = Explore.this;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        reference = FirebaseDatabase.getInstance().getReference();

        topUser8 = new ArrayList<>();
        fieldPhotos = new ArrayList<>();
        paginatedPhotos = new ArrayList<>();
        mSearchParam = findViewById(R.id.search);
        mListView = findViewById(R.id.listview);
        topBox = findViewById(R.id.topBox);
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
        spinner = findViewById(R.id.spinnerDo);
        collapse = findViewById(R.id.collapse);
        cross = findViewById(R.id.cross);
        up = findViewById(R.id.up);
        down = findViewById(R.id.down);


        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchParam.setText("");
                mUserList.clear();
                mAdapter.notifyDataSetChanged();
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                up.setVisibility(View.GONE);
                down.setVisibility(View.VISIBLE);

                collapse(collapse,1000,0);
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                up.setVisibility(View.VISIBLE);
                down.setVisibility(View.GONE);
                collapse(collapse,1000,dummyHeight);

            }
        });
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
        Log.d(TAG, "initOnClickListeners: completed");
    }

    public void expand( View v, int duration,int targetHeight) {
        final boolean expand = v.getVisibility() == View.VISIBLE;
        prevHeight = v.getHeight();
        if (x == 0) {
            x++;
            dummyHeight = v.getHeight();
        }
        Log.d(TAG, "expand:" + expand);
        int prevHeight  = v.getHeight();

        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public  void collapse(final View v, int duration, int targetHeight) {
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
        int finalHeight = height;
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();

            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (expand) {
                    v.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!expand) {
                    v.setVisibility(View.INVISIBLE);
                }
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
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        else {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                        .setNegativeButton("No", (dialog, id) -> dialog.cancel());
                final AlertDialog alert = builder.create();
                alert.show();
            }

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                Location location = task.getResult();

                if (location != null) try {
                    Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Log.d(TAG, "onDataChange: " + addresses);
                    String country = addresses.get(0).getCountryName();
                    String city = addresses.get(0).getSubAdminArea();
                    String area = addresses.get(0).getLocality();
                    Log.d(TAG, "onDataChange: " + addresses);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    reference.child(getString(R.string.dbname_leaderboard)).child(user.getUid()).child(getString(R.string.field_last_known_location)).child("city").setValue(city);
                    reference.child(getString(R.string.dbname_leaderboard)).child(user.getUid()).child(getString(R.string.field_last_known_location)).child("country").setValue(country);
                    reference.child(getString(R.string.dbname_leaderboard)).child(user.getUid()).child(getString(R.string.field_last_known_location)).child("area").setValue(area);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void checkTopDatabase() {
        Log.d(TAG, "checkTopDatabase: started");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("Photography");
        fields.add("Film Maker");
        fields.add("Musician");
        fields.add("Sketch Artist");
        fields.add("Writer");
        fields.add("Others");
        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
            @Override
            public void onTimeReceived(String currentTimeStamp) {
                Query query = reference.child(getString(R.string.db_topUsersParams));
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(getString(R.string.field_last_updated_topUsers)).getValue() == null) {
                            reference.child(getString(R.string.db_topUsersParams)).child(getString(R.string.field_overall)).child(getString(R.string.field_completed)).setValue("0/300");
                            for (String field : fields)
                                reference.child(getString(R.string.db_topUsersParams)).child(field).child(getString(R.string.field_completed)).setValue("0/300");
                            createTopDatabase(getString(R.string.field_overall), 0);
                            for (String field : fields) createTopDatabase(field, 0);
                        } else {
                            String previousTimeStamp = (String) snapshot.child(getString(R.string.field_last_updated_topUsers)).getValue();
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
                                reference.child(getString(R.string.db_topUsersParams)).child(getString(R.string.field_overall)).child(getString(R.string.field_completed)).setValue("0/300");
                                for (String field : fields)
                                    reference.child(getString(R.string.db_topUsersParams)).child(field).child(getString(R.string.field_completed)).setValue("0/300");
                                createTopDatabase(getString(R.string.field_overall), 0);
                                for (String field : fields) createTopDatabase(field, 0);
                            } else {
                                for (String field : fields) {
                                    if (snapshot.child(field).getValue() == null)
                                        createTopDatabase(field, 0);
                                    else {
                                        String str = String.valueOf(snapshot.child(field).child(getString(R.string.field_completed)).getValue());
                                        if (str.equals("")) createTopDatabase(field, 0);
                                        else {
                                            if (!str.equals("300/300")) {
                                                Log.d(TAG, "checkTopDatabase: flag" + field);
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
                                if (snapshot.child(getString(R.string.field_overall)).getValue() == null)
                                    createTopDatabase(getString(R.string.field_overall), 0);
                                else {
                                    String str = String.valueOf(snapshot.child(getString(R.string.field_overall)).child(getString(R.string.field_completed)).getValue());
                                    if (str.equals(""))
                                        createTopDatabase(getString(R.string.field_overall), 0);
                                    else {
                                        if (!str.equals("300/300")) {
                                            int index = str.indexOf("/");
                                            if (index != -1)
                                                createTopDatabase(getString(R.string.field_overall), 0);
                                            else {
                                                int completed = Integer.parseInt(str.substring(0, index));
                                                createTopDatabase(getString(R.string.field_overall), completed);
                                            }

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
        ArrayList<String> fields = new ArrayList<>();
        fields.add("Photography");
        fields.add("Film Maker");
        fields.add("Musician");
        fields.add("Sketch Artist");
        fields.add("Writer");
        fields.add("Others");
        fields.add("Overall");

        for (String field : fields) {
            String previousTimeStamp = mPreferences.getString(field + "_fieldLastFetched", null);
            Log.d(TAG, "checkLastFetched: field " + field);
            if (previousTimeStamp == null || previousTimeStamp.equals("")) {
                Log.d(TAG, "checkLastFetched: starting fetching users as previousTimeStamp is null or not found" + field);
                String firstField = getString(R.string.field_overall);
                if (field.equals("Overall")) fetchTopUsers(field, "");
                else fetchTopUsers(field, firstField);
            } else {
                SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
                    @Override
                    public void onTimeReceived(String currentTimeStamp) {
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
//                            Log.d(TAG, "checkLastFetched: date1 " + date1);
//                            Log.d(TAG, "checkLastFetched: date2 " + date2);
                            assert date1 != null;
                            assert date2 != null;
                            elapsedDays = (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
//                            Log.d(TAG, "checkLastFetched:elapsedDays" + elapsedDays);
//                            Log.d(TAG, "checkLastFetched:currentDay" + currentDay);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            String firstField = getString(R.string.field_overall);
                            fetchTopUsers(field, firstField);
                            if (field.equals("Overall"))
                                fetchTopUsers(field, "");
                        }

                        if (elapsedDays > currentDay) {
                            String firstField = getString(R.string.field_overall);
                            fetchTopUsers(field, firstField);
                            if (field.equals("Overall"))
                                fetchTopUsers(field, "");
                        }
                    }

                    @Override
                    public void onError(Exception ex) {
                        Log.d(TAG, "onError: SNTPClient fetching TopUsers from shared Preferences" + ex.getMessage());
                        Log.d(TAG, "fetching again");
                        String firstField = getString(R.string.field_overall);
                        fetchTopUsers(field, firstField);
                        if (field.equals("Overall"))
                            fetchTopUsers(field, "");
                    }
                });
            }
        }
        checkPostsFetched();
        Log.d(TAG, "checkLastFetched: completed");
    }

    private void createTopDatabase(String field, int completed) {
        Log.d(TAG, "createTopDatabase: started");
        Log.d(TAG, "createTopDatabase: field " + field);
        ArrayList<TopUsers> mList = new ArrayList<>();
        mList.clear();
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
                    assert domain != null;
                    if (field.equals(getString(R.string.field_overall))) {
                        //creating top 300 list using insertion sort algorithm
                        if (mList.size() == 0) mList.add(new TopUsers(user_id, rating));
                        else {
                            int l = mList.size();
                            //loop to push in between and next one further away for arraylist
                            for (int i = 0; i < l; i++) {
                                int r = mList.get(i).getRating();
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
                            if (mList.size() == 301) mList.remove(300);
                        }
                    } else if (domain.equals(field)) {
                        //creating top 300 list using insertion sort algorithm
                        if (mList.size() == 0) mList.add(new TopUsers(user_id, rating));
                        else {
                            int l = mList.size();
                            //loop to push in between and next one further away for arraylist
                            for (int i = 0; i < l; i++) {
                                int r = mList.get(i).getRating();
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
                            if (mList.size() == 301) mList.remove(300);
                        }
                    }
                }
                //debugging logs
                Log.d(TAG, "createTopDatabase: mList.size()" + field + mList.size());
                Log.d(TAG, "createTopDatabase: starting upload for Domain field");

                //adding the list fetched to database
                for (int i = completed; i < 300; i++) {
                    String value = "";
                    if (i < mList.size()) value = mList.get(i).getUser_id();
                    Log.d(TAG, "createTopDatabase: key, value" + (i + 1) + ", " + value);
                    reference.child(getString(R.string.db_topUsersParams)).child(field).child(String.valueOf(i + 1)).setValue(value);
                    reference.child(getString(R.string.db_topUsersParams)).child(field).child(getString(R.string.field_completed)).setValue((i + 1) + "/300");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchTopUsers(String firstField, String secondField) {
        Log.d(TAG, "fetchTopUsers: started");
        Log.d(TAG, "fetchTopUsers: field " + firstField + "," + secondField);
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
                            if (!userID.equals("") && !mTopUsersList.contains(userID)) {
                                mTopUsersList.add(userID);
                                Log.d(TAG, "onDataChange: adding first" + firstField + " : " + mTopUsersList.size());
                            }
                        } else {
                            Log.d(TAG, "fetchTopUsers: no.of users added from primary db mTopUsersList.size() - " + mTopUsersList.size());
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            assert currentUser != null;
                            String currentUserID = currentUser.getUid();
//                            Log.d(TAG, "fetchTopUsers: currentUserID - " + currentUserID);
                            Query query1 = reference.child(getString(R.string.dbname_leaderboard)).child(currentUserID);
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String currentUserCity = String.valueOf(snapshot.child(getString(R.string.field_last_known_location)).child(getString(R.string.field_city)).getValue());
                                        if (currentUserCity.equals("")) checkOrGetLocation();
                                        int currentUserRating = (int) (long) snapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_post)).getValue()
                                                + (int) (long) snapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_followers)).getValue()
                                                + (int) (long) snapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_contest)).getValue();
                                        Log.d(TAG, "fetchTopUsers: currentUserCity : currentUserRating - " + currentUserCity + " : " + currentUserRating);
                                        Query query11 = reference.child(getString(R.string.dbname_leaderboard));
                                        query11.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                                                        String userID = singleSnapshot.getKey();
                                                        String userCity = String.valueOf(singleSnapshot.child(getString(R.string.field_last_known_location)).child(getString(R.string.field_city)).getValue());
//                                                        Log.d(TAG, "fetchTopUsers: userID : userCity - " + userID + " : " + userCity);
                                                        assert userID != null;
                                                        if (!userID.equals(currentUserID) && userCity.equals(currentUserCity) && !mTopUsersList.contains(userID)) {
                                                            int userRating = (int) (long) singleSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_post)).getValue()
                                                                    + (int) (long) singleSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_followers)).getValue()
                                                                    + (int) (long) singleSnapshot.child(getString(R.string.field_all_time)).child(getString(R.string.field_contest)).getValue();
//                                                            Log.d(TAG, "fetchTopUsers: userID : userCity : - " + userID + " : " + userCity + " : " + userRating);
                                                            mTopUsersList.add(userID);
                                                            Log.d(TAG, "onDataChange: adding location" + firstField + " : " + mTopUsersList.size());
                                                        }
                                                    }
                                                    Log.d(TAG, "fetchTopUsers: no.of users added from location db mTopUsersList.size() - " + mTopUsersList.size());
                                                    if (mTopUsersList.size() < 500) {
                                                        Query query2 = reference.child(getString(R.string.dbname_users)).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child(getString(R.string.field_domain));
                                                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.exists()) {
                                                                    String domain = String.valueOf(snapshot.getValue());
                                                                    Log.d(TAG, "fetchTopUsers: domain" + domain);
                                                                    if (!domain.equals("")) {
                                                                        if (!secondField.equals("")) {
                                                                            domain = secondField;
                                                                        }
                                                                        Query query22 = reference.child(getString(R.string.db_topUsersParams)).child(domain);
                                                                        query22.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if (snapshot.exists()) {
                                                                                    for (DataSnapshot singleSnapshot : snapshot.getChildren()) {

                                                                                        String key = singleSnapshot.getKey();
                                                                                        String userID = String.valueOf(singleSnapshot.getValue());
                                                                                        assert key != null;
                                                                                        Log.d(TAG, "onDataChange: adding left" + firstField + " : " + mTopUsersList);

                                                                                        if (!key.equals(getString(R.string.field_completed))) {
                                                                                            if (!userID.equals("") && !mTopUsersList.contains(userID)) {
                                                                                                Log.d(TAG, "onDataChange: adding left" + firstField + " : " + mTopUsersList);
                                                                                                mTopUsersList.add(userID);
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    Log.d(TAG, "fetchTopUsers: no.of users added from secondary db mTopUsersList.size() - " + mTopUsersList.size());
                                                                                    SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
                                                                                        @Override
                                                                                        public void onTimeReceived(String currentTimeSTamp) {
                                                                                            Set<String> set = new HashSet<>(mTopUsersList);
                                                                                            Log.d(TAG, "fetchTopUsers: adding set of " + set.size() + " on field " + firstField);
//                                                                                            Log.d(TAG, "fetchTopUsers: adding finalDomain" + firstField);
//                                                                                            Log.d(TAG, "fetchTopUsers: adding currentTimeSTamp" + currentTimeSTamp);
//                                                                                            Log.d(TAG, "fetchTopUsers: set size" + set.size());
                                                                                            mEditor.putString(firstField + "_fieldLastFetched", currentTimeSTamp);
                                                                                            mEditor.putStringSet(firstField + "_TopUsers", set);
                                                                                            mEditor.apply();
                                                                                            Log.d(TAG, "fetchTopUsers: fetch complete for field " + firstField + " staring posts fetch");
                                                                                            getPosts(firstField, 0);
                                                                                        }

                                                                                        @Override
                                                                                        public void onError(Exception ex) {
                                                                                            Log.d(TAG, "onError: SNTPClient updating shared preferences" + ex.getMessage());
                                                                                        }
                                                                                    });

                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    } else {
                                                        Log.d(TAG, "fetchTopUsers: mTopUsersList.size() - " + mTopUsersList.size());
                                                        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
                                                            @Override
                                                            public void onTimeReceived(String currentTimeSTamp) {
                                                                Set<String> set = new HashSet<>(mTopUsersList);
                                                                Log.d(TAG, "fetchTopUsers: adding set of " + set.size() + " on field " + firstField);
//                                                                                            Log.d(TAG, "fetchTopUsers: adding finalDomain" + firstField);
//                                                                                            Log.d(TAG, "fetchTopUsers: adding currentTimeSTamp" + currentTimeSTamp);
//                                                                                            Log.d(TAG, "fetchTopUsers: set size" + set.size());
                                                                mEditor.putString(firstField + "_fieldLastFetched", currentTimeSTamp);
                                                                mEditor.putStringSet(firstField + "_TopUsers", set);
                                                                mEditor.apply();
                                                                Log.d(TAG, "fetchTopUsers: fetch complete for field " + firstField + " staring posts fetch");
                                                            }

                                                            @Override
                                                            public void onError(Exception ex) {
                                                                Log.d(TAG, "onError: SNTPClient updating shared preferences" + ex.getMessage());
                                                            }
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

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }

                            });

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.d(TAG, "fetchTopUsers: completed");
    }

    private void checkPostsFetched() {
        Log.d(TAG, "checkPostsFetched: started");
        ArrayList<String> fields = new ArrayList<>();
        fields.add("Photography");
        fields.add("Film Maker");
        fields.add("Musician");
        fields.add("Sketch Artist");
        fields.add("Writer");
        fields.add("Others");
        fields.add("Overall");
        for (String field : fields) {
            Log.d(TAG, "checkPostsFetched: field " + field);
            Gson gson = new Gson();
            String previousTimeStamp = mPreferences.getString(field + "_PostsLastUpdated", null);
            String json = mPreferences.getString(field + "_TopPosts", null);
            Set<String> set = mPreferences.getStringSet(field + "_TopUsers", null);
            int completed = mPreferences.getInt(field + "_completed", 0);
//            Log.d(TAG, "checkPostsFetched: set" + set);
//            Log.d(TAG, "checkPostsFetched: json" + json);
//            Log.d(TAG, "checkPostsFetched: previousTimeStamp" + previousTimeStamp);
//            Log.d(TAG, "checkPostsFetched: completed - " + completed+" of "+set.size());
            if (set == null) {
//                if (field.equals("Overall")) fetchTopUsers(field, "");
//                else fetchTopUsers(field, "Overall");
                handler.postDelayed(this::checkPostsFetched, RETRY_DURATION);
                RETRY_DURATION *= 2;
            } else {
                if (json == null || previousTimeStamp == null || previousTimeStamp.equals("")) {
                    getPosts(field, completed);
                } else {
//                Type type = new TypeToken<List<Photo>>() {}.getType();
//                ArrayList<Photo> fieldPhotos = gson.fromJson(json, type);
                    if (completed < set.size()) {
                        getPosts(field, completed);
                    } else {
                        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
                            @Override
                            public void onTimeReceived(String currentTimeStamp) {
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
//                                    Log.d(TAG, "checkPostsFetched: date1 " + date1);
//                                    Log.d(TAG, "checkPostsFetched: date1 " + date2);
                                    assert date1 != null;
                                    assert date2 != null;
                                    elapsedDays = (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
//                                    Log.d(TAG, "checkPostsFetched: elapsedDays " + elapsedDays);
//                                    Log.d(TAG, "checkPostsFetched: currentDay " + currentDay);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    getPosts(field, completed);
                                }

                                if (elapsedDays > currentDay) {
                                    getPosts(field, completed);
                                }
                            }

                            @Override
                            public void onError(Exception ex) {
                                Log.d(TAG, "onError: SNTPClient fetching TopUsers from shared Preferences" + ex.getMessage());
                                Log.d(TAG, "fetching again");
                                getPosts(field, completed);
                            }
                        });
                    }
                }
            }
        }
        displayPhotos();
        Log.d(TAG, "checkPostsFetched: completed");
    }

    private void getPosts(String field, int startingIndex) {
        Log.d(TAG, "getPosts: started");
        Log.d(TAG, "getPosts: field " + field);
        Set<String> set = mPreferences.getStringSet(field + "_TopUsers", null);
        if (set == null) {
//            if (field.equals("Overall")) fetchTopUsers(field, "");
//            else fetchTopUsers(field, "Overall");
            handler.postDelayed(() -> getPosts(field, startingIndex), RETRY_DURATION);
            RETRY_DURATION *= 2;
        } else {
            ArrayList<String> mTopUsersList = new ArrayList<>(set);
            Gson gson = new Gson();
            String json = mPreferences.getString(field + "_TopPosts", null);
            ArrayList<Photo> fieldPhotos;
            if (json == null) {
                fieldPhotos = new ArrayList<>();
            } else {
                Type type = new TypeToken<List<Photo>>() {
                }.getType();
                fieldPhotos = gson.fromJson(json, type);
            }
            Log.d(TAG, "getPosts: photos of size " + fieldPhotos.size() + " for users " + set.size());
            SNTPClient.getDate(TimeZone.getTimeZone("Asia/Kolkata"), new SNTPClient.Listener() {
                @Override
                public void onTimeReceived(String currentTimeSTamp) {
                    for (int i = startingIndex; i < mTopUsersList.size(); i++) {
                        String userId = mTopUsersList.get(i);
//                        Log.d(TAG, "checkPosts: fetching for index " + i + " and userId " + userId);
                        Query query = reference.child(getString(R.string.dbname_user_photos)).child(userId);
                        int finalI = i;
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists())
                                    for (DataSnapshot singleSnapshot : snapshot.getChildren())
                                        if (singleSnapshot.exists()) {
                                            Photo photo = singleSnapshot.getValue(Photo.class);
                                            if (!fieldPhotos.contains(photo))
                                                fieldPhotos.add(photo);
                                        }
//                                Log.d(TAG, "getPosts: currentTimeSTamp" + currentTimeSTamp);
                                Gson gson = new Gson();
                                String json = gson.toJson(fieldPhotos);
//                                Log.d(TAG, "onDataChange: " + json);
                                mEditor.putString(field + "_PostsLastUpdated", currentTimeSTamp);
                                mEditor.putString(field + "_TopPosts", json);
                                if (finalI == mTopUsersList.size() - 1)
                                    mEditor.putInt(field + "_completed", mTopUsersList.size());
                                else mEditor.putInt(field + "_completed", finalI);
                                mEditor.apply();
                                Log.d(TAG, "getPosts: uploading " + fieldPhotos.size() + " photos for " + field);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onError(Exception ex) {
                }
            });
        }
    }

    @Override
    public void onButtonClicked(String text) {
        spinner.setText(text);
        swipeRefreshLayout.setRefreshing(true);
        Log.d(TAG, "onItemSelected: qwer" + text);
        displayPhotos();
    }

    private void getTop8() {
        Query query = reference.child(getString(R.string.db_topUsersParams)).child(getString(R.string.field_overall)).limitToFirst(8);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                    topUser8.add(singleSnapshot.getValue().toString());
                    Log.d(TAG, "getTop8: topUser" + topUser8);
                }
                getStarImage(topUser8);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getStarImage(ArrayList<String> user_id) {
        Log.d(TAG, "getStarImage: " + user_id);
        reference.child(getString(R.string.dbname_users)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (int x = 0; x < user_id.size(); x++) {
                    if (x == 0) {
                        user1 = user_id.get(x);
                        UniversalImageLoader.setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star1, null, "");
                    }
                    if (x == 1) {
                        user2 = user_id.get(x);
                        UniversalImageLoader.setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star2, null, "");
                    }
                    if (x == 2) {
                        user3 = user_id.get(x);
                        UniversalImageLoader.setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star3, null, "");
                    }
                    if (x == 3) {
                        user4 = user_id.get(x);
                        UniversalImageLoader.setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star4, null, "");
                    }
                    if (x == 4) {
                        user5 = user_id.get(x);
                        UniversalImageLoader.setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star5, null, "");
                    }
                    if (x == 5) {
                        user6 = user_id.get(x);
                        UniversalImageLoader.setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star6, null, "");
                    }
                    if (x == 6) {
                        user7 = user_id.get(x);
                        UniversalImageLoader.setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star7, null, "");
                    }
                    if (x == 7) {
                        user8 = user_id.get(x);
                        UniversalImageLoader.setImage(snapshot.child(user_id.get(x)).child(getString(R.string.profile_photo)).getValue().toString(), star8, null, "");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void displayPhotos() {
        if (swipeRefreshLayout.isRefreshing()) {
            Log.d(TAG, "displayPhotos: started");
            String field = spinner.getText().toString();
            if (field.equals("All")) field = "Overall";
            Log.d(TAG, "displayPhotos: started" + field);
            paginatedPhotos.clear();
            fieldPhotos.clear();
            if (adapterGridImage != null) adapterGridImage.notifyDataSetChanged();
            Gson gson = new Gson();
            String json = mPreferences.getString(field + "_TopPosts", null);
            if (json == null || json.equals("")) {
                Log.d(TAG, "displayPhotos: handler1");
                handler.postDelayed(this::displayPhotos, RETRY_DURATION);
            } else {
                Log.d(TAG, "displayPhotos: handler2");
                handler.removeCallbacks(this::displayPhotos);
                Type type = new TypeToken<List<Photo>>() {
                }.getType();
                fieldPhotos = gson.fromJson(json, type);
                Log.d(TAG, "displayPhotos: photos retrieved " + fieldPhotos.size());
                try {
                    if (!shuffled) {
                        Collections.shuffle(fieldPhotos);
                        shuffled = true;
                    }
                    paginatedPhotos = new ArrayList<>();
                    for (int i = 0; i < fieldPhotos.size(); i++) {
                        if (i == fieldPhotos.size() - 1 || i == 8) {
                            Log.d(TAG, "displayPhotos: paginatedPhotos" + paginatedPhotos.size());
                            adapterGridImage = new AdapterGridImageExplore(mContext, paginatedPhotos, this);
                            ((SimpleItemAnimator) exploreRv.getItemAnimator()).setSupportsChangeAnimations(false);
                            swipeRefreshLayout.setRefreshing(false);
                            adapterGridImage.setHasStableIds(true);
                            exploreRv.setAdapter(adapterGridImage);
                            break;
                        } else paginatedPhotos.add(fieldPhotos.get(i));
                    }

                } catch (NullPointerException e) {
                    Log.e(TAG, "Null pointer exception" + e.getMessage());

                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, "index out of bound" + e.getMessage());
                }
            }
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
                        exploreRv.post(new Runnable() {
                            @Override
                            public void run() {
                                // Notify adapter with appropriate notify methods
                                adapterGridImage.notifyItemRangeInserted(l - 1, itemCount);

                            }
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
                intent.putExtra(getString(R.string.intent_user), mUserList.get(position).getUser_id());
                startActivity(intent);
            }
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
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.BottomNavViewBar);
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
        db1.child(mContext.getString(R.string.dbname_user_photos)).child(photo.getUser_id()).child(photo.getPhoto_id()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Comment> comments = new ArrayList<>();
                for (DataSnapshot dSnapshot : snapshot.child("comment").getChildren()) {
                    Comment comment = new Comment();
                    comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                    comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                    comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
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
}