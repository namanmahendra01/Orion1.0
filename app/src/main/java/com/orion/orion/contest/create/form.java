package com.orion.orion.contest.create;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.dialogs.BottomSheetDomain;
import com.orion.orion.login.LoginActivity;
import com.orion.orion.util.Permissions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.orion.orion.profile.ProfileActivity.VERIFY_PERMISSION_REQUEST;


public class form extends AppCompatActivity implements BottomSheetDomain.BottomSheetListener {
    private static final String TAG = "form";
    private static final int ANIMATION_DURATION = 100;
    boolean isKitKat;
    int layoutActive = 1;

    private TextView mTopBarTitle;

    //Widgets
    private ScrollView layout1;
    private ScrollView layout2;
    private ScrollView layout3;
    private View active1;
    private View active2;
    private View active3;
    private Button previousButton;
    private Button nextButton;

    private ImageView backArrow;

    //layout1 widgets
    private ImageView poster;
    private LinearLayout selectPoster;
    private EditText eventTitle;
    private EditText hostedBy;
    private EditText description;
    private RadioGroup AllStudents;
    private RadioButton all;
    private RadioButton students;
    private RadioGroup PictureVideoDocument;
    private RadioButton picture;
    private RadioButton mediaLink;

    private TextView selectDomain;


    //layout 2
    private RadioGroup VotingType;
    private RadioButton Public;
    private RadioButton Jury;
    private RadioButton PublicAndJury;
    private LinearLayout jurySelectionContainer;
    private LinearLayout jury1Container;
    private LinearLayout jury2Container;
    private LinearLayout jury3Container;
    private LinearLayout publicVotingContainer;
    private LinearLayout publicVotingContainerDates;
    private RadioGroup JuryNumber;
    private RadioButton one;
    private RadioButton two;
    private RadioButton three;
    private EditText mDisplayDateRB;
    private EditText mDisplayDateRE;
    private EditText mDisplayDateVB;
    private EditText mDisplayDateVE;
    private EditText mDisplayDateWin;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListener2;
    private DatePickerDialog.OnDateSetListener mDateSetListener3;
    private DatePickerDialog.OnDateSetListener mDateSetListener4;
    private DatePickerDialog.OnDateSetListener mDateSetListener5;
    private EditText juryName1;
    private EditText juryName2;
    private EditText juryName3;
    private ImageView j1Checked;
    private ImageView j2Checked;
    private ImageView j3Checked;

    //layout3 widgets
    private SwitchCompat toggleLimitedNoOfParticipants;
    private EditText mLimitedNoOfParticipants;
    private SwitchCompat toggleEntryFees;
    private EditText mEntryFees;
    private SwitchCompat togglePrize;
    private LinearLayout PH;
    private LinearLayout P1;
    private LinearLayout P2;
    private LinearLayout P3;
    private EditText firstPrize;
    private EditText secondPrize;
    private EditText thirdPrize;
    private TextView totalPrize;
    private EditText extraRules;
    private EditText exraQuery;


    //Values
    private int selectedImage;
    private String imgurl = "";
    private String posterLink = "";
    private String title;
    private String hosted;
    private String des;
    private String openFor = "";
    private String fileType = "";
    private String domain = "";
    private String votingType = "";
    private String noOfJury = "";
    private String JuryName1 = "";
    private String JuryName2 = "";
    private String JuryName3 = "";
    private String date1 = "";
    private String date2 = "";
    private String date3 = "";
    private String date4 = "";
    private String date5 = "";
    private String participantType = "Unlimited";
    private String noOfParticipants = "";
    private String fees = "";
    private String prizeMoney = "No";
    private String prizeFirst = "";
    private String prizeSecond = "";
    private String prizeThird = "";
    private String prizeTotal = "";
    private String extraRule = "";


    //firebase
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;


    public static boolean isDateAfter(String startDate, String endDate) {
        try {
            String myFormatString = "dd-M-yyyy"; // for example
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat(myFormatString);
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);
            assert date1 != null;
            return date1.after(startingDate);
        } catch (Exception e) {
            return false;
        }
    }

    private void disableEmoji() {
        InputFilter emojiFilter = (source, start, end, dest, dstart, dend) -> {
            for (int index = start; index < end - 1; index++) {
                int type = Character.getType(source.charAt(index));
                if (type == Character.SURROGATE) return "";
            }
            return null;
        };
        eventTitle.setFilters(new InputFilter[]{emojiFilter});
        hostedBy.setFilters(new InputFilter[]{emojiFilter});
        description.setFilters(new InputFilter[]{emojiFilter});
        juryName1.setFilters(new InputFilter[]{emojiFilter});
        juryName2.setFilters(new InputFilter[]{emojiFilter});
        juryName3.setFilters(new InputFilter[]{emojiFilter});
        firstPrize.setFilters(new InputFilter[]{emojiFilter});
        secondPrize.setFilters(new InputFilter[]{emojiFilter});
        thirdPrize.setFilters(new InputFilter[]{emojiFilter});
        totalPrize.setFilters(new InputFilter[]{emojiFilter});
        extraRules.setFilters(new InputFilter[]{emojiFilter});
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onButtonClicked(String text) {
        domain = text;
        selectDomain.setText(text);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure")
                .setMessage("You will discard all the changes you made?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> finish())
                .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contest);

        mContext = form.this;
        mTopBarTitle = findViewById(R.id.titleTopBar);
        mTopBarTitle.setText("Create Contest");
        setupFirebaseAuth();
        initializeWidgets();
        disableEmoji();
        layout1.setOnClickListener(v -> hideKeyboardFrom(mContext, layout1));
        layout2.setOnClickListener(v -> hideKeyboardFrom(mContext, layout2));
        layout3.setOnClickListener(v -> hideKeyboardFrom(mContext, layout3));
        backArrow.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Are you sure")
                    .setMessage("You will discard all the changes you made?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> finish())
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                    .show();
        });
        selectPoster.setOnClickListener(v -> {
            selectPoster.setEnabled(false);
            selectedImage = 1;
            if (checkPermissionArray(Permissions.PERMISSIONS)) {
                isKitKat = true;
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            } else verifyPermission(Permissions.PERMISSIONS);
        });
        AllStudents.setOnCheckedChangeListener((group, checkedId) -> {
            hideKeyboardFrom(mContext, AllStudents);
            for (int i = 0; i < group.getChildCount(); i++) {
                if (all.getId() == checkedId) openFor = all.getText().toString();
                if (students.getId() == checkedId) openFor = students.getText().toString();
            }
        });
        PictureVideoDocument.setOnCheckedChangeListener((group, checkedId) -> {
            hideKeyboardFrom(mContext, PictureVideoDocument);
            for (int i = 0; i < group.getChildCount(); i++) {
                if (picture.getId() == checkedId) {
                    fileType = picture.getText().toString();
                }
                if (mediaLink.getId() == checkedId) {
                    fileType = mediaLink.getText().toString();
                }

            }
        });
        selectDomain.setOnClickListener(v -> {
            hideKeyboardFrom(mContext, selectDomain);
            BottomSheetDomain bottomSheetDomain = new BottomSheetDomain();
            bottomSheetDomain.show(getSupportFragmentManager(), "Domain Selection");
        });
        VotingType.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (Public.getId() == checkedId) {
                    votingType = Public.getText().toString();

                    Public.setClickable(false);
                    Jury.setClickable(true);
                    PublicAndJury.setClickable(true);
                    Jury.setChecked(false);
                    PublicAndJury.setChecked(false);
                    one.setChecked(false);
                    two.setChecked(false);
                    three.setChecked(false);

                    noOfJury ="";
                    juryName1.setText("");
                    JuryName1="";
                    juryName2.setText("");
                    JuryName2="";
                    juryName3.setText("");
                    JuryName3="";

                    if (jurySelectionContainer.getVisibility() == View.VISIBLE) {
                        YoYo.with(Techniques.FadeOutUp).duration(ANIMATION_DURATION).playOn(jurySelectionContainer);
                        jurySelectionContainer.setVisibility(View.GONE);
                        YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(jury1Container);
                        YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(jury2Container);
                        YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(jury3Container);
                        new Handler()
                                .postDelayed(() -> {
                                    jury1Container.setVisibility(View.GONE);
                                    jury2Container.setVisibility(View.GONE);
                                    jury3Container.setVisibility(View.GONE);
                                }, ANIMATION_DURATION);
                    }
                    if (publicVotingContainer.getVisibility() == View.GONE) {
                        publicVotingContainer.setVisibility(View.VISIBLE);
                        publicVotingContainerDates.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInDown).duration(ANIMATION_DURATION).playOn(publicVotingContainer);
                        YoYo.with(Techniques.FadeInDown).duration(ANIMATION_DURATION).playOn(publicVotingContainerDates);
                    }
                }
                else if (Jury.getId() == checkedId) {
                    votingType = Jury.getText().toString();

                    Public.setClickable(true);
                    Jury.setClickable(false);
                    PublicAndJury.setClickable(true);
                    Public.setChecked(false);
                    PublicAndJury.setChecked(false);
                    one.setChecked(false);
                    two.setChecked(false);
                    three.setChecked(false);

                    noOfJury ="";
                    juryName1.setText("");
                    JuryName1="";
                    juryName2.setText("");
                    JuryName2="";
                    juryName3.setText("");
                    JuryName3="";
                    mDisplayDateVB.setText("");
                    mDisplayDateVE.setText("");
                    date3="";
                    date4="";

                    jury1Container.setVisibility(View.GONE);
                    jury2Container.setVisibility(View.GONE);
                    jury3Container.setVisibility(View.GONE);

                    if (jurySelectionContainer.getVisibility() == View.GONE) {
                        jurySelectionContainer.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInDown).duration(ANIMATION_DURATION).playOn(jurySelectionContainer);
                    }
                    if (publicVotingContainer.getVisibility() == View.VISIBLE) {
                        YoYo.with(Techniques.FadeOutUp).duration(ANIMATION_DURATION).playOn(publicVotingContainer);
                        YoYo.with(Techniques.FadeOutUp).duration(ANIMATION_DURATION).playOn(publicVotingContainerDates);
                        new Handler().postDelayed(() -> {
                            publicVotingContainer.setVisibility(View.GONE);
                            publicVotingContainerDates.setVisibility(View.GONE);
                        }, ANIMATION_DURATION);
                    }
                    if (noOfJury.equals("1")) {
                        noOfJury = one.getText().toString();
                        JuryName2="";
                        JuryName3="";
                        if (jury1Container.getVisibility() == View.GONE) {
                            jury1Container.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury1Container);
                        }
                        if (jury2Container.getVisibility() == View.VISIBLE) {
                            YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(jury2Container);
                            new Handler().postDelayed(() -> jury2Container.setVisibility(View.GONE), ANIMATION_DURATION);
                        }
                        if (jury3Container.getVisibility() == View.VISIBLE) {
                            YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(jury3Container);
                            new Handler().postDelayed(() -> jury3Container.setVisibility(View.GONE), ANIMATION_DURATION);
                        }
                    }
                    if (noOfJury.equals("2")) {
                        noOfJury = two.getText().toString();
                        JuryName3="";
                        if (jury1Container.getVisibility() == View.GONE) {
                            jury1Container.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury1Container);
                        }
                        if (jury2Container.getVisibility() == View.GONE) {
                            jury2Container.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury2Container);
                        }
                        if (jury3Container.getVisibility() == View.VISIBLE) {
                            YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(jury3Container);
                            new Handler().postDelayed(() -> jury3Container.setVisibility(View.GONE), ANIMATION_DURATION);
                        }
                    }
                    if (noOfJury.equals("3")) {
                        noOfJury = three.getText().toString();
                        if (jury1Container.getVisibility() == View.GONE) {
                            jury1Container.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury1Container);
                        }
                        if (jury2Container.getVisibility() == View.GONE) {
                            jury2Container.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury2Container);
                        }
                        if (jury3Container.getVisibility() == View.GONE) {
                            jury3Container.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury3Container);
                        }
                    }
                }
                else {
                    votingType = PublicAndJury.getText().toString();

                    Public.setClickable(true);
                    Jury.setClickable(true);
                    PublicAndJury.setClickable(false);
                    Jury.setChecked(false);
                    Public.setChecked(false);
                    one.setChecked(false);
                    two.setChecked(false);
                    three.setChecked(false);

                    noOfJury ="";
                    juryName1.setText("");
                    JuryName1="";
                    juryName2.setText("");
                    JuryName2="";
                    juryName3.setText("");
                    JuryName3="";

                    jury1Container.setVisibility(View.GONE);
                    jury2Container.setVisibility(View.GONE);
                    jury3Container.setVisibility(View.GONE);

                    if (jurySelectionContainer.getVisibility() == View.GONE) {
                        jurySelectionContainer.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInDown).duration(ANIMATION_DURATION).playOn(jurySelectionContainer);
                    }
                    if (publicVotingContainer.getVisibility() == View.GONE) {
                        publicVotingContainer.setVisibility(View.VISIBLE);
                        publicVotingContainerDates.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInDown).duration(ANIMATION_DURATION).playOn(publicVotingContainer);
                        YoYo.with(Techniques.FadeInDown).duration(ANIMATION_DURATION).playOn(publicVotingContainerDates);
                    }
                    if (noOfJury.equals("1")) {
                        noOfJury = one.getText().toString();
                        juryName2.setText("");
                        juryName3.setText("");
                        JuryName2="";
                        JuryName3="";
                        if (jury1Container.getVisibility() == View.GONE) {
                            jury1Container.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury1Container);
                        }
                        if (jury2Container.getVisibility() == View.VISIBLE) {
                            YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(jury2Container);
                            new Handler().postDelayed(() -> jury2Container.setVisibility(View.GONE), ANIMATION_DURATION);
                        }
                        if (jury2Container.getVisibility() == View.VISIBLE) {
                            YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(jury2Container);
                            new Handler().postDelayed(() -> jury3Container.setVisibility(View.GONE), ANIMATION_DURATION);
                        }
                    }
                    if (noOfJury.equals("2")) {
                        noOfJury = two.getText().toString();
                        juryName3.setText("");
                        JuryName3="";
                        if (jury1Container.getVisibility() == View.GONE) {
                            jury1Container.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury1Container);
                        }
                        if (jury2Container.getVisibility() == View.GONE) {
                            jury2Container.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury2Container);
                        }
                        if (jury3Container.getVisibility() == View.VISIBLE) {
                            YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(jury3Container);
                            new Handler().postDelayed(() -> jury3Container.setVisibility(View.GONE), ANIMATION_DURATION);
                        }
                    }
                    if (noOfJury.equals("3")) {
                        noOfJury = three.getText().toString();
                        if (jury1Container.getVisibility() == View.GONE) {
                            jury1Container.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury1Container);
                        }
                        if (jury2Container.getVisibility() == View.GONE) {
                            jury2Container.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury2Container);
                        }
                        if (jury3Container.getVisibility() == View.GONE) {
                            jury3Container.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury3Container);
                        }
                    }
                }
            }
        });
        JuryNumber.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (one.getId() == checkedId) {
                    noOfJury = one.getText().toString();
                    juryName2.setText("");
                    juryName3.setText("");
                    JuryName2="";
                    JuryName3="";
                    if (jury1Container.getVisibility() == View.GONE) {
                        jury1Container.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury1Container);
                    }
                    if (jury2Container.getVisibility() == View.VISIBLE) {
                        YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(jury2Container);
                        new Handler().postDelayed(() -> jury2Container.setVisibility(View.GONE), ANIMATION_DURATION);
                    }
                    if (jury3Container.getVisibility() == View.VISIBLE) {
                        YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(jury3Container);
                        new Handler().postDelayed(() -> jury3Container.setVisibility(View.GONE), ANIMATION_DURATION);
                    }
                } else if (two.getId() == checkedId) {
                    noOfJury = two.getText().toString();
                    juryName3.setText("");
                    JuryName3="";
                    if (jury1Container.getVisibility() == View.GONE) {
                        jury1Container.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury1Container);
                    }
                    if (jury2Container.getVisibility() == View.GONE) {
                        jury2Container.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury2Container);
                    }
                    if (jury3Container.getVisibility() == View.VISIBLE) {
                        YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(jury3Container);
                        new Handler().postDelayed(() -> jury3Container.setVisibility(View.GONE), ANIMATION_DURATION);
                    }
                } else {
                    noOfJury = three.getText().toString();
                    if (jury1Container.getVisibility() == View.GONE) {
                        jury1Container.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury1Container);
                    }
                    if (jury2Container.getVisibility() == View.GONE) {
                        jury2Container.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury2Container);
                    }
                    if (jury3Container.getVisibility() == View.GONE) {
                        jury3Container.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(jury3Container);
                    }
                }
            }
        });
        juryName1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                juryName1.setTextColor(Color.BLACK);
                String username = juryName1.getText().toString();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                Query query = db.child(getString(R.string.dbname_username)).child(username);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            JuryName1 = juryName1.getText().toString();
                            usernameExist(juryName1);
                            j1Checked.setVisibility(View.VISIBLE);

                        } else {
                            juryName1.setError("Username not found");
                            j1Checked.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        juryName2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                juryName2.setTextColor(Color.BLACK);
                String username = juryName2.getText().toString();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                Query query = db.child(getString(R.string.dbname_username)).child(username);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            JuryName2 = juryName2.getText().toString();
                            usernameExist(juryName2);
                            j2Checked.setVisibility(View.VISIBLE);
                        } else {
                            juryName2.setError("Username not found");
                            j2Checked.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
        });
        juryName3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                juryName3.setTextColor(Color.BLACK);
                String username = juryName3.getText().toString();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                Query query = db.child(getString(R.string.dbname_username)).child(username);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            JuryName3 = juryName3.getText().toString();
                            usernameExist(juryName3);
                            j3Checked.setVisibility(View.VISIBLE);
                        } else {
                            juryName3.setError("Username not found");
                            j3Checked.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        mDisplayDateRB.setOnClickListener(v -> {
            Calendar cal1 = Calendar.getInstance();
            int year = cal1.get(Calendar.YEAR);
            int month = cal1.get(Calendar.MONTH);
            int day = cal1.get(Calendar.DAY_OF_MONTH);
            mDateSetListener = (view, year1, month1, dayOfMonth) -> {
                month1 = month1 + 1;
                date1 = dayOfMonth + "-" + month1 + "-" + year1;
                if ((isDateAfter(date1, date2) || date2.equals("")) && (isDateAfter(date1, date3) || date3.equals("")) && (isDateAfter(date1, date4) || date4.equals("")) && (isDateAfter(date1, date5) || date5.equals(""))) {
                    mDisplayDateRB.setText(date1);
                } else {
                    mDisplayDateRB.setText("");
                    mDisplayDateRB.requestFocus();
                    date1 = "";
                }
            };
            DatePickerDialog dialog = new DatePickerDialog(form.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        mDisplayDateRE.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            mDateSetListener2 = (view, year14, month14, dayOfMonth) -> {
                month14 = month14 + 1;
                date2 = dayOfMonth + "-" + month14 + "-" + year14;
                if ((isDateAfter(date1, date2) || date1.equals("")) && (isDateAfter(date2, date3) || date3.equals("")) && (isDateAfter(date2, date4) || date4.equals("")) && (isDateAfter(date2, date5) || date5.equals(""))) {
                    mDisplayDateRE.setText(date2);
                } else {
                    mDisplayDateRE.setText("");
                    mDisplayDateRE.requestFocus();
                    date2 = "";
                }
            };

            DatePickerDialog dialog = new DatePickerDialog(form.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener2, year, month, day);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        mDisplayDateVB.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            mDateSetListener3 = (view, year12, month12, dayOfMonth) -> {
                month12 = month12 + 1;
                date3 = dayOfMonth + "-" + month12 + "-" + year12;
                if ((isDateAfter(date3, date4) || date4.equals("")) && (isDateAfter(date2, date3) || date2.equals("")) && (isDateAfter(date1, date3) || date1.equals("")) && (isDateAfter(date3, date5) || date5.equals(""))) {
                    mDisplayDateVB.setText(date3);
                } else {
                    mDisplayDateVB.setText("");
                    mDisplayDateVB.requestFocus();
                    date3 = "";
                }
            };

            DatePickerDialog dialog = new DatePickerDialog(form.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener3, year, month, day);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        mDisplayDateVE.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            mDateSetListener4 = (view, year13, month13, dayOfMonth) -> {
                month13 = month13 + 1;
                date4 = dayOfMonth + "-" + month13 + "-" + year13;
                if ((isDateAfter(date3, date4) || date3.equals("")) && (isDateAfter(date2, date4) || date2.equals("")) && (isDateAfter(date1, date4) || date1.equals("")) && (isDateAfter(date4, date5) || date5.equals(""))) {
                    mDisplayDateVE.setText(date4);
                } else {
                    mDisplayDateVE.setText("");
                    mDisplayDateVE.requestFocus();
                    date4 = "";
                }
            };

            DatePickerDialog dialog = new DatePickerDialog(form.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener4, year, month, day);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        mDisplayDateWin.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            mDateSetListener5 = (view, year15, month15, dayOfMonth) -> {
                month15 = month15 + 1;
                date5 = dayOfMonth + "-" + month15 + "-" + year15;
                if ((isDateAfter(date1, date5) || date1.equals("")) && (isDateAfter(date2, date5) || date2.equals("")) && (isDateAfter(date3, date5) || date3.equals("")) && (isDateAfter(date4, date5) || date4.equals(""))) {
                    mDisplayDateWin.setText(date5);
                } else {
                    mDisplayDateWin.setText("");
                    mDisplayDateWin.requestFocus();
                    date5 = "";
                }
            };
            DatePickerDialog dialog = new DatePickerDialog(form.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener5, year, month, day);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        toggleLimitedNoOfParticipants.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                participantType = "Limited";
                mLimitedNoOfParticipants.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(mLimitedNoOfParticipants);
            } else {
                participantType = "Unlimited";
                mLimitedNoOfParticipants.setText("");
                noOfParticipants="";
                YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(mLimitedNoOfParticipants);
                new Handler().postDelayed(() -> mLimitedNoOfParticipants.setVisibility(View.GONE), ANIMATION_DURATION);
            }
        });
        toggleEntryFees.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mEntryFees.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(mEntryFees);
                togglePrize.setChecked(true);
                if (P1.getVisibility() == View.GONE) {
                    P1.setVisibility(View.VISIBLE);
                    P2.setVisibility(View.VISIBLE);
                    P3.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(P1);
                    YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(P2);
                    YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(P3);
                }
                togglePrize.setClickable(false);
                prizeMoney = "Yes";
            } else {
                YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(mEntryFees);
                mLimitedNoOfParticipants.setVisibility(View.GONE);
                new Handler().postDelayed(() -> mEntryFees.setVisibility(View.GONE), ANIMATION_DURATION);
                mEntryFees.setText("");
                fees="";
                togglePrize.setClickable(true);
            }
        });
        togglePrize.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                P1.setVisibility(View.VISIBLE);
                P2.setVisibility(View.VISIBLE);
                P3.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(P1);
                YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(P2);
                YoYo.with(Techniques.FadeInLeft).duration(ANIMATION_DURATION).playOn(P3);
                prizeMoney = "Yes";
            } else {
                YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(P1);
                YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(P2);
                YoYo.with(Techniques.FadeOutLeft).duration(ANIMATION_DURATION).playOn(P3);
                new Handler().postDelayed(() -> {
                    P1.setVisibility(View.GONE);
                    P2.setVisibility(View.GONE);
                    P3.setVisibility(View.GONE);
                }, ANIMATION_DURATION);
                prizeMoney = "No";
                firstPrize.setText("");
                secondPrize.setText("");
                thirdPrize.setText("");
                prizeFirst="";
                prizeSecond="";
                prizeThird="";
            }
        });
        if (firstPrize.getVisibility() == View.VISIBLE || secondPrize.getVisibility() == View.VISIBLE || thirdPrize.getVisibility() == View.VISIBLE) {
            firstPrize.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().trim().length() > 0) {
                        try {
                            int a = 0;
                            int b = 0;
                            int c = 0;
                            if (!firstPrize.getText().toString().equals("")) {
                                a = Integer.parseInt(firstPrize.getText().toString());
                                prizeFirst = s.toString();
                            }
                            if (!secondPrize.getText().toString().equals(""))
                                b = Integer.parseInt(secondPrize.getText().toString());
                            if (!thirdPrize.getText().toString().equals(""))
                                c = Integer.parseInt(thirdPrize.getText().toString());
                            prizeTotal = String.valueOf(a + b + c);
                            Log.d(TAG, "afterTextChanged: a" + a);
                            Log.d(TAG, "afterTextChanged: b" + b);
                            Log.d(TAG, "afterTextChanged: c" + c);
                            Log.d(TAG, "afterTextChanged: d" + prizeTotal);
                            totalPrize.setText(prizeTotal);
                        } catch (NumberFormatException e) {
                            firstPrize.setText("0");
                            Log.e(TAG, "afterTextChanged: " + e.getMessage());
                        }
                    }
                }
            });
            secondPrize.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().trim().length() > 0) {
                        try {
                            int a = 0;
                            int b = 0;
                            int c = 0;
                            if (!firstPrize.getText().toString().equals(""))
                                a = Integer.parseInt(firstPrize.getText().toString());
                            if (!secondPrize.getText().toString().equals("")) {
                                b = Integer.parseInt(secondPrize.getText().toString());
                                prizeSecond = s.toString();
                            }
                            if (!thirdPrize.getText().toString().equals(""))
                                c = Integer.parseInt(thirdPrize.getText().toString());
                            prizeTotal = String.valueOf(a + b + c);
                            Log.d(TAG, "afterTextChanged: a" + a);
                            Log.d(TAG, "afterTextChanged: b" + b);
                            Log.d(TAG, "afterTextChanged: c" + c);
                            Log.d(TAG, "afterTextChanged: d" + prizeTotal);
                            totalPrize.setText(prizeTotal);
                        } catch (NumberFormatException e) {
                            secondPrize.setText("0");
                            Log.e(TAG, "afterTextChanged: " + e.getMessage());
                        }
                    }
                }
            });
            thirdPrize.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().trim().length() > 0) {
                        try {
                            int a = 0;
                            int b = 0;
                            int c = 0;
                            if (!firstPrize.getText().toString().equals(""))
                                a = Integer.parseInt(firstPrize.getText().toString());
                            if (!secondPrize.getText().toString().equals(""))
                                b = Integer.parseInt(secondPrize.getText().toString());
                            if (!thirdPrize.getText().toString().equals("")) {
                                c = Integer.parseInt(thirdPrize.getText().toString());
                                prizeThird = s.toString();
                            }
                            prizeTotal = String.valueOf(a + b + c);
                            Log.d(TAG, "afterTextChanged: a" + a);
                            Log.d(TAG, "afterTextChanged: b" + b);
                            Log.d(TAG, "afterTextChanged: c" + c);
                            Log.d(TAG, "afterTextChanged: d" + prizeTotal);
                            totalPrize.setText(prizeTotal);
                        } catch (NumberFormatException e) {
                            thirdPrize.setText("0");
                            Log.e(TAG, "afterTextChanged: " + e.getMessage());
                        }
                    }

                }
            });
        }
        previousButton.setOnClickListener(v -> {
            if (layoutActive == 2) {
                layout2.setVisibility(View.GONE);
                layout1.setVisibility(View.VISIBLE);
                active2.setVisibility(View.INVISIBLE);
                active1.setVisibility(View.VISIBLE);
                layoutActive = 1;
            }
            if (layoutActive == 3) {
                layout3.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                active3.setVisibility(View.INVISIBLE);
                active2.setVisibility(View.VISIBLE);
                layoutActive = 2;
            }
        });
        nextButton.setOnClickListener(v -> {
            if (layoutActive == 1) {
                title = eventTitle.getText().toString();
                hosted = hostedBy.getText().toString();
                des = description.getText().toString();
                if (posterLink.equals("") || title.equals("") || hosted.equals("") || des.equals("") || openFor.equals("") || fileType.equals("") || domain.equals("")) {
                    if (posterLink.equals("")) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(selectPoster);
                        Toast.makeText(mContext, "Please select a poster", Toast.LENGTH_SHORT).show();
                    }
                    if (title.equals("")) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(eventTitle);
                        eventTitle.setError("Please enter a title");
                        eventTitle.requestFocus();
                    }
                    if (hosted.equals("")) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(hostedBy);
                        hostedBy.setError("Please enter a username of host");
                        hostedBy.requestFocus();
                    }
                    if (des.equals("")) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(description);
                        description.setError("Please enter some description at least");
                        description.requestFocus();
                    }
                    if (openFor.equals("")) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(AllStudents);
                        Toast.makeText(mContext, "Please make a selection", Toast.LENGTH_SHORT).show();
                        AllStudents.requestFocus();
                    }
                    if (fileType.equals("")) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(PictureVideoDocument);
                        Toast.makeText(mContext, "Please make a selection", Toast.LENGTH_SHORT).show();
                        PictureVideoDocument.requestFocus();
                    }
                    if (domain.equals("")) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(selectDomain);
                        Toast.makeText(mContext, "Please make a selection", Toast.LENGTH_SHORT).show();
                        selectDomain.requestFocus();
                    }
                } else {
                    Log.d(TAG, "onCreate: button clicked");
                    Log.d(TAG, "onCreate: " + title + hosted + des + openFor + fileType);
                    layout1.setVisibility(View.GONE);
                    previousButton.setVisibility(View.VISIBLE);
                    layout2.setVisibility(View.VISIBLE);
                    layoutActive = 2;
                    active1.setVisibility(View.INVISIBLE);
                    active2.setVisibility(View.VISIBLE);
                }
            } else if (layoutActive == 2) {
                if (votingType.equals("") || date1.equals("") || date2.equals("") || date5.equals("")) {
                    if (votingType.equals("")) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(VotingType);
                        Toast.makeText(mContext, "Please make a selection", Toast.LENGTH_SHORT).show();
                    }
                    if (date1.equals("")) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mDisplayDateRB);
                    }
                    if (date2.equals("")) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mDisplayDateRE);
                    }
                    if (date5.equals("")) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mDisplayDateWin);
                    }
                } else {
                    if (votingType.equals("Public")) {
                        if (date3.equals("") || date4.equals("")) {
                            if (date3.equals("")) {
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mDisplayDateVB);
                            }
                            if (date4.equals("")) {
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mDisplayDateVE);
                            }
                        } else {
                            layout3.setVisibility(View.VISIBLE);
                            layout2.setVisibility(View.GONE);
                            active2.setVisibility(View.INVISIBLE);
                            active3.setVisibility(View.VISIBLE);
                            layoutActive = 3;
                        }
                    }
                    if (votingType.equals("Jury")) {
                        if (noOfJury.equals("")) {
                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(jurySelectionContainer);
                            Toast.makeText(mContext, "Please make a selection", Toast.LENGTH_SHORT).show();
                            jurySelectionContainer.requestFocus();
                        } else if (noOfJury.equals("1") && juryName1.getCurrentTextColor() != (Color.GREEN)) {
                            Log.d(TAG, "onCreate: outer1");
                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName1);
                            juryName1.setError("Please enter a valid username");
                            juryName1.requestFocus();
                        } else if (noOfJury.equals("2") && (juryName1.getCurrentTextColor() != (Color.GREEN) || juryName2.getCurrentTextColor() != (Color.GREEN))) {
                            Log.d(TAG, "onCreate: outer2");
                            if (JuryName1.equals("")) {
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName1);
                            }
                            if (JuryName2.equals("")) {
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName2);
                            }
                        } else if (noOfJury.equals("3") && (juryName1.getCurrentTextColor() != (Color.GREEN) || juryName2.getCurrentTextColor() != (Color.GREEN) || juryName3.getCurrentTextColor() != (Color.GREEN))) {
                            Log.d(TAG, "onCreate: outer3");
                            if (JuryName1.equals("")) {
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName1);
                            }
                            if (JuryName2.equals("")) {
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName2);
                            }
                            if (JuryName3.equals("")) {
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName3);
                            }
                        }else if(JuryName1.equals(JuryName2)||JuryName1.equals(JuryName3)||JuryName2.equals(JuryName3)) {
                            if (JuryName1.equals(JuryName2)){
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName1);
                                juryName1.setError("Jury members must be different!");
                                juryName1.requestFocus();

                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName2);
                                juryName2.setError("Jury members must be different!");
                                juryName2.requestFocus();
                            }
                            if (JuryName1.equals(JuryName3)){
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName1);
                                juryName1.setError("Jury members must be different!");
                                juryName1.requestFocus();

                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName3);
                                juryName3.setError("Jury members must be different!");
                                juryName3.requestFocus();
                            }
                            if (JuryName3.equals(JuryName2)){
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName3);
                                juryName3.setError("Jury members must be different!");
                                juryName3.requestFocus();

                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName2);
                                juryName2.setError("Jury members must be different!");
                                juryName2.requestFocus();
                            }

                        }else
                         {
                            layout3.setVisibility(View.VISIBLE);
                            layout2.setVisibility(View.GONE);
                            active2.setVisibility(View.INVISIBLE);
                            active3.setVisibility(View.VISIBLE);
                            layoutActive = 3;
                        }
                    }
                    if (votingType.equals("Jury and Public")) {
                        if (noOfJury.equals("")) {
                            Log.d(TAG, "onCreate: outerouter");
                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(jurySelectionContainer);
                            Toast.makeText(mContext, "Please make a selection", Toast.LENGTH_SHORT).show();
                            jurySelectionContainer.requestFocus();
                        } else if (noOfJury.equals("1") && juryName1.getCurrentTextColor() != (Color.GREEN)) {
                            Log.d(TAG, "onCreate: outer1");
                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName1);
                            juryName1.setError("Please enter a valid username");
                            juryName1.requestFocus();
                        } else if (noOfJury.equals("2") && (juryName1.getCurrentTextColor() != (Color.GREEN) || juryName2.getCurrentTextColor() != (Color.GREEN))) {
                            Log.d(TAG, "onCreate: outer2");
                            if (JuryName1.equals("")) {
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName1);
                            }
                            if (JuryName2.equals("")) {
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName2);
                            }
                        } else if (noOfJury.equals("3") && (juryName1.getCurrentTextColor() != (Color.GREEN) || juryName2.getCurrentTextColor() != (Color.GREEN) || juryName3.getCurrentTextColor() != (Color.GREEN))) {
                            Log.d(TAG, "onCreate: outer3");
                            if (JuryName1.equals("")) {
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName1);
                            }
                            if (JuryName2.equals("")) {
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName2);
                            }
                            if (JuryName3.equals("")) {
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName3);
                            }
                        } else {
                            if (date3.equals("") || date4.equals("")) {
                                if (date3.equals("")) {
                                    YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mDisplayDateVB);
                                    mDisplayDateVB.setError("Please enter a date");
                                    mDisplayDateVB.requestFocus();
                                }
                                if (date4.equals("")) {
                                    YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mDisplayDateVE);
                                    mDisplayDateVE.setError("Please enter a date");
                                    mDisplayDateVE.requestFocus();
                                }
                            } else {
                                layout3.setVisibility(View.VISIBLE);
                                layout2.setVisibility(View.GONE);
                                active2.setVisibility(View.INVISIBLE);
                                active3.setVisibility(View.VISIBLE);
                                layoutActive = 3;
                            }
                        }
                    }
                }
            } else if (layoutActive == 3) {

                if (mLimitedNoOfParticipants.getVisibility() == View.VISIBLE)
                    noOfParticipants = mLimitedNoOfParticipants.getText().toString();
                if (mEntryFees.getVisibility() == View.VISIBLE)
                    fees = mEntryFees.getText().toString();
                extraRule = extraRules.getText().toString();

                if (toggleLimitedNoOfParticipants.isChecked() && noOfParticipants.equals("")) {
                    YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mLimitedNoOfParticipants);
                    mLimitedNoOfParticipants.setError("Please enter a number");
                    mLimitedNoOfParticipants.requestFocus();
                } else if (toggleEntryFees.isChecked() && fees.equals("")) {
                    YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mEntryFees);
                    mEntryFees.setError("Please enter a number");
                    mEntryFees.requestFocus();
                } else if (togglePrize.isChecked() && (prizeFirst.equals("") || prizeSecond.equals("")
                        || prizeThird.equals("") || prizeFirst.equals("0") || prizeSecond.equals("0")
                        || prizeThird.equals("0") || Integer.parseInt(prizeSecond) >= Integer.parseInt(prizeFirst) ||
                        Integer.parseInt(prizeThird) >= Integer.parseInt(prizeSecond)
                        ||checkFee(fees,prizeThird))) {
                    if (prizeFirst.equals("") || prizeFirst.equals("0")) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(firstPrize);
                        firstPrize.setError("Please enter some prize");
                        firstPrize.requestFocus();
                    }
                    if (prizeSecond.equals("") || prizeSecond.equals("0")) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(secondPrize);
                        secondPrize.setError("Please enter some prize");
                        secondPrize.requestFocus();
                    }
                    if (prizeThird.equals("") || prizeThird.equals("0")) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(thirdPrize);
                        thirdPrize.setError("Please enter some prize");
                        thirdPrize.requestFocus();
                    }
                    if (Integer.parseInt(prizeSecond) >= Integer.parseInt(prizeFirst)) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(firstPrize);
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(secondPrize);
                        firstPrize.setError("2nd prize cannot be more than 1st");
                        secondPrize.setError("2nd prize cannot be more than 1st");
                        firstPrize.requestFocus();
                        secondPrize.requestFocus();
                    }
                    if (Integer.parseInt(prizeThird) >= Integer.parseInt(prizeSecond)) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(secondPrize);
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(thirdPrize);
                        secondPrize.setError("3nd prize cannot be more than 2nd");
                        thirdPrize.setError("3rd prize cannot be more than 2nd");
                        secondPrize.requestFocus();
                        thirdPrize.requestFocus();
                    }
                    if(!fees.equals("")) {
                        if ((Integer.parseInt(prizeThird) <= Integer.parseInt(fees))) {
                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mEntryFees);
                            mEntryFees.setError("Entry fee must be less than each prize money");
                            mEntryFees.requestFocus();
                        }
                    }
                } else
                    submit();
            }
        });

    }
    private boolean checkFee(String fees, String prizeThird) {
        if (fees.equals("")){
            return false;
        }else{
            return Integer.parseInt(prizeThird) <= Integer.parseInt(fees);
        }
    }


    private void submit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are u sure about all the details and create this contest?")
                .setTitle("Create Contest")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent i = new Intent(form.this, CheckContest.class);
                    i.putExtra("entryfee", fees);
                    i.putExtra("title", title);
                    i.putExtra("descrip", des);
                    i.putExtra("poster", posterLink);
                    i.putExtra("filetype", fileType);
                    i.putExtra("domain", domain);
                    i.putExtra("votetype", votingType);
                    i.putExtra("rule", extraRule);
                    i.putExtra("regBegin", date1.replace("/", "-"));
                    i.putExtra("regEnd", date2.replace("/", "-"));
                    if (date3.equals("")) {
                        i.putExtra("voteBegin", "-");
                    } else {
                        i.putExtra("voteBegin", date3.replace("/", "-"));
                    }
                    if (date4.equals("")) {
                        i.putExtra("voteEnd", "-");
                    } else {
                        i.putExtra("voteEnd", date4.replace("/", "-"));
                    }
                    i.putExtra("winDeclare", date5.replace("/", "-"));
                    i.putExtra("numParticipants", participantType);
                    i.putExtra("maxLimit", noOfParticipants);
                    i.putExtra("pmoney", prizeMoney);
                    i.putExtra("place_1", prizeFirst);
                    i.putExtra("place_2", prizeSecond);
                    i.putExtra("place_3", prizeThird);
                    i.putExtra("total_prize", prizeTotal);
                    i.putExtra("numJury", noOfJury);
                    i.putExtra("jname_1", JuryName1);
                    i.putExtra("jname_2", JuryName2);
                    i.putExtra("jname_3", JuryName3);
                    i.putExtra("openfor", openFor);
                    i.putExtra("host", hosted);
                    startActivity(i);
                })
                .setNegativeButton("No", (dialog, id) -> {
                    dialog.cancel();
                })
                .show();
    }

    private void updateTotal() {
        try {
            int a = 0;
            int b = 0;
            int c = 0;
            if (!firstPrize.getText().toString().equals(""))
                a = Integer.parseInt(firstPrize.getText().toString());
            if (!secondPrize.getText().toString().equals(""))
                b = Integer.parseInt(secondPrize.getText().toString());
            if (!thirdPrize.getText().toString().equals(""))
                c = Integer.parseInt(thirdPrize.getText().toString());
            prizeTotal = String.valueOf(a + b + c);

            totalPrize.setText(prizeTotal);
        } catch (Exception e) {
            Log.e(TAG, "afterTextChanged: " + e.getMessage());
        }
    }

    @SuppressLint("CutPasteId")
    private void initializeWidgets() {
        //back arrow
        backArrow = findViewById(R.id.backarrow);

        //layouts
        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        active1 = findViewById(R.id.active1);
        active2 = findViewById(R.id.active2);
        active3 = findViewById(R.id.active3);


        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);

        layout1.setVisibility(View.VISIBLE);
        layout2.setVisibility(View.GONE);
        layout3.setVisibility(View.GONE);
        previousButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.VISIBLE);

        //layout1 widgets
        poster = findViewById(R.id.poster);
        selectPoster = findViewById(R.id.selectPoster);
        eventTitle = findViewById(R.id.eventTitle);
        hostedBy = findViewById(R.id.hostedBy);
        description = findViewById(R.id.description);
        AllStudents = findViewById(R.id.AllStudents);
        all = findViewById(R.id.All);
        students = findViewById(R.id.student);
        PictureVideoDocument = findViewById(R.id.PictureVideoDocument);
        picture = findViewById(R.id.picture);
        mediaLink = findViewById(R.id.Media_Link);
        selectDomain = findViewById(R.id.selectDomain);

        //layout 2 widgets
        VotingType = findViewById(R.id.votingType);
        Public = findViewById(R.id.publicVote);
        Jury = findViewById(R.id.juryVote);
        PublicAndJury = findViewById(R.id.juryAndPublicVote);
        jurySelectionContainer = findViewById(R.id.jurySelectionContainer);
        jury1Container = findViewById(R.id.jury1Container);
        jury2Container = findViewById(R.id.jury2Container);
        jury3Container = findViewById(R.id.jury3Container);
        publicVotingContainer = findViewById(R.id.publicVotingContainer);
        publicVotingContainerDates = findViewById(R.id.publicVotingContainerDates);
        mDisplayDateRB = findViewById(R.id.datepicker1);
        mDisplayDateRE = findViewById(R.id.datepicker2);
        mDisplayDateVB = findViewById(R.id.datepicker3);
        mDisplayDateVE = findViewById(R.id.datepicker4);
        mDisplayDateWin = findViewById(R.id.datepicker5);
        JuryNumber = findViewById(R.id.jurySelection);
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        juryName1 = findViewById(R.id.juryName1);
        juryName2 = findViewById(R.id.juryName2);
        juryName3 = findViewById(R.id.juryName3);
        j1Checked = findViewById(R.id.j1Checked);
        j2Checked = findViewById(R.id.j2Checked);
        j3Checked = findViewById(R.id.j3Checked);
        jurySelectionContainer.setVisibility(View.GONE);
        jury1Container.setVisibility(View.GONE);
        jury2Container.setVisibility(View.GONE);
        jury3Container.setVisibility(View.GONE);
        j1Checked.setVisibility(View.GONE);
        j2Checked.setVisibility(View.GONE);
        j3Checked.setVisibility(View.GONE);

        publicVotingContainer.setVisibility(View.GONE);
        publicVotingContainerDates.setVisibility(View.GONE);

        //layout3 widgets
        toggleLimitedNoOfParticipants = findViewById(R.id.toggleNoOfPartictipants);
        mLimitedNoOfParticipants = findViewById(R.id.noOfParticipants);
        toggleEntryFees = findViewById(R.id.toggleEntryFees);
        mEntryFees = findViewById(R.id.entryFees);
        togglePrize = findViewById(R.id.togglePrize);
        P1 = findViewById(R.id.P1);
        P2 = findViewById(R.id.P2);
        P3 = findViewById(R.id.P3);
        firstPrize = findViewById(R.id.firstPrize);
        secondPrize = findViewById(R.id.secondPrize);
        thirdPrize = findViewById(R.id.thirdPrize);
        totalPrize = findViewById(R.id.totalPrize);
        extraRules = findViewById(R.id.extraRules);
        mLimitedNoOfParticipants.setVisibility(View.GONE);
        mEntryFees.setVisibility(View.GONE);
        P1.setVisibility(View.GONE);
        P2.setVisibility(View.GONE);
        P3.setVisibility(View.GONE);
    }
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getPathFromUri(final Context context, final Uri uri) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type))
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                else if ("video".equals(type))
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                else if ("audio".equals(type))
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) return uri.getPath();
        return null;
    }

    @TargetApi(19)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        selectPoster.setEnabled(true);
        String imgPath = "";
        if (data != null && data.getData() != null && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                imgPath = getPathFromUri(mContext, uri);
                Log.d(TAG, "onActivityResult: path: " + imgPath);
                Log.d(TAG, "onActivityResult: uri: " + uri);
                imgurl = imgPath;
                setImage();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImage() {
        if (selectedImage == 1) {
            posterLink = imgurl;
            Log.d(TAG, "setImage: posterLink" + posterLink);
            String mAppend = "file:/";
            Log.d(TAG, "setImage: mAppend" + mAppend);
            if (!posterLink.equals("")) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)selectPoster.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_END,RelativeLayout.TRUE);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
                selectPoster.setLayoutParams(params);
                selectPoster.setAlpha(0.5f);
                ((TextView) findViewById(R.id.selectPosterText)).setText("Change Poster");
                selectPoster.setBackgroundTintList(mContext.getResources().getColorStateList(R.color.black));
                poster.setVisibility(View.VISIBLE);
                poster.setBackgroundColor(Color.rgb(0, 0, 0));
                Glide.with(getApplicationContext())
                        .load(posterLink)
                        .placeholder(R.drawable.load)
                        .error(R.drawable.default_image2)
                        .placeholder(R.drawable.load)
                        .into(poster);                 }
        }
    }

    public void verifyPermission(String[] permissions) {
        ActivityCompat.requestPermissions(form.this, permissions, VERIFY_PERMISSION_REQUEST);
    }

    public boolean checkPermissionArray(String[] permissions) {
        for (String check : permissions) if (!checkPermissions(check)) return false;
        return true;
    }

    public boolean checkPermissions(String permission) {
        int permissionRequest = ActivityCompat.checkSelfPermission(form.this, permission);
        return permissionRequest == PackageManager.PERMISSION_GRANTED;
    }

    private void usernameExist(EditText juryname) {
        juryname.setTextColor(Color.GREEN);
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
