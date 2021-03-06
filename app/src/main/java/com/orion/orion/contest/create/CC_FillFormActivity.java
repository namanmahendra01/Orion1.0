package com.orion.orion.contest.create;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.orion.orion.Adapters.AdapterQuestionList;
import com.orion.orion.R;
import com.orion.orion.dialogs.AddQuestionDialog;
import com.orion.orion.dialogs.BottomSheetDomain;
import com.orion.orion.login.LoginActivity;
import com.orion.orion.models.QuizQuestion;
import com.orion.orion.util.CustomDateTimePicker;
import com.orion.orion.util.Permissions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.orion.orion.profile.ProfileActivity.VERIFY_PERMISSION_REQUEST;
import static com.orion.orion.util.FileUtils.generateFileName;
import static com.orion.orion.util.FileUtils.getDocumentCacheDir;
import static com.orion.orion.util.FileUtils.getFileName;
import static com.orion.orion.util.FileUtils.saveFileFromUri;


public class CC_FillFormActivity extends AppCompatActivity implements BottomSheetDomain.BottomSheetListener, AddQuestionDialog.OnAddButtonClickListener {
    private static final String TAG = "CC_FillFormActivity";
    private static final int ANIMATION_DURATION = 100;
    private static final int MAX_QUIZ_QUESTION = 5;
    boolean isKitKat;
    int layoutActive = 1;


    private CustomDateTimePicker customDateTimePicker;

    //Widgets
    private TextView mTopBarTitle;
    private ScrollView layout1;
    private ScrollView layout2;
    private ScrollView layout3;
    private View active1;
    private View active2;
    private View active3;
    private Button previousButton;
    private Button nextButton;
    int l = 0;
    private ArrayList<Integer> criteriaFields = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

    private int count;

    private ImageView cross1, cross2, cross3, cross4, cross5, cross6, cross7, cross8, cross9, cross10;
    private TextView criteriaTv1, criteriaTv2, criteriaTv3, criteriaTv4, criteriaTv5, criteriaTv6, criteriaTv7, criteriaTv8, criteriaTv9, criteriaTv10;
    private LinearLayout criteriaLL1, criteriaLL2, criteriaLL3, criteriaLL4, criteriaLL5, criteriaLL6, criteriaLL7, criteriaLL8, criteriaLL9, criteriaLL10;
    private ImageView backArrow;

    //layout1 widgets
    private ImageView poster;
    private LinearLayout selectPoster;
    private EditText eventTitle;
    private EditText hostedBy;
    private EditText criteriaEt;
    private TextView addCriteria, charCount;
    private EditText description;
    private RadioGroup AllStudents;
    private RadioButton all;
    private RadioButton students;
    private LinearLayout submissionTypeContainer;
    private RadioGroup QuizSubmission;
    private RadioButton quiz;
    private RadioButton submission;
    private RadioButton offline;
    private RadioGroup PictureVideoDocument;
    private RadioButton picture;
    private RadioButton mediaLink;

    private TextView selectDomain;


    //layout 2

    //question

    //    private EditText question;
//    private ImageView option1;
//    private ImageView option2;
//    private ImageView option3;
//    private ImageView option4;
//    private EditText option1value;
//    private EditText option2value;
//    private EditText option3value;
//    private EditText option4value;
    private LinearLayout durationContainer;
    private Spinner durationSelector;
    private LinearLayout quizStartDateTimeContainer;
    private LinearLayout quizStartDateTimePickerContainer;
    private LinearLayout judgingCriteriaBox;
    private RecyclerView recyclerView;
    private RelativeLayout questionAdditionBox;
    private TextView addQuestionButton;
    private RadioGroup VotingType;
    private RadioButton Public;
    private RadioButton Jury;
    private RadioButton PublicAndJury;
    private LinearLayout jurySelectionContainer;
    private LinearLayout jury1Container;
    private LinearLayout jury2Container;
    private LinearLayout jury3Container;
    private LinearLayout publicVotingContainer;
    private LinearLayout criteriaContainer;
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
    private EditText mDisplayDateTimeQS;
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
    private String contestType = "";
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
    private String datetime = "";
    private String duration = "";
    private String participantType = "Unlimited";
    private String noOfParticipants = "";
    private String fees = "";
    private String prizeMoney = "No";
    private String prizeFirst = "";
    private String prizeSecond = "";
    private String prizeThird = "";
    private String prizeTotal = "";
    private String extraRule = "";

    private int option_selected_num;
    private ArrayList<QuizQuestion> quizQuestionArrayList;
    private QuizQuestion quizQuestion;
    private AdapterQuestionList adapterQuestionList;


    //firebase
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

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

        mContext = CC_FillFormActivity.this;


        setupFirebaseAuth();
        initializeWidgets();
        disableEmoji();

        quizQuestionArrayList = new ArrayList<>();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.quiz_duration_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationSelector.setAdapter(adapter);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterQuestionList = new AdapterQuestionList(mContext, quizQuestionArrayList);
        recyclerView.setAdapter(adapterQuestionList);

        customDateTimePicker = new CustomDateTimePicker(this, new CustomDateTimePicker.ICustomDateTimeListener() {
            @Override
            public void onSet(Dialog dialog, Calendar calendarSelected,
                              Date dateSelected, int year, String monthFullName,
                              String monthShortName, int monthNumber, int day,
                              String weekDayFullName, String weekDayShortName,
                              int hour24, int hour12, int min, int sec,
                              String AM_PM) {

                String date = calendarSelected.get(Calendar.DAY_OF_MONTH) + "-" + (monthNumber + 1) + "-" + year;
//                Log.d(TAG, "onSet: date : " + date);
//                Log.d(TAG, "onSet: date1 : " + date1);
//                Log.d(TAG, "onSet: date2 : " + date2);
//                Log.d(TAG, "onSet: date5 : " + date5);
//                Log.d(TAG, "onSet: isDateAfter(date1, date) : " + (date1.equals("") || isDateAfter(date1, date)));
//                Log.d(TAG, "onSet: isDateAfter(date2, date) : " + (date2.equals("") || isDateAfter(date2, date)));
//                Log.d(TAG, "onSet: isDateAfter(date, date5) : " + (date5.equals("") || isDateAfter(date, date5)));
                if ((date1.equals("") || isDateAfter(date1, date))
                        && (date2.equals("") || isDateAfter(date2, date))
                        && (date5.equals("") || isDateAfter(date, date5))) {
                    String format = date + " " + hour24 + ":" + min + ":" + sec;
                    mDisplayDateTimeQS.setText(format);
                    datetime = format;

                } else {
                    Toast.makeText(mContext, "Please selecta valid time!", Toast.LENGTH_LONG).show();
                    YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mDisplayDateRB);
                    mDisplayDateTimeQS.setText("");
                    mDisplayDateTimeQS.requestFocus();
                    datetime = "";
                }
            }

            @Override
            public void onCancel() {
                mDisplayDateTimeQS.setText("");
                mDisplayDateTimeQS.requestFocus();
                datetime = "";
            }
        });

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
        QuizSubmission.setOnCheckedChangeListener((group, checkedId) -> {
            hideKeyboardFrom(mContext, QuizSubmission);
            for (int i = 0; i < group.getChildCount(); i++) {
                if (quiz.getId() == checkedId) {
                    contestType = quiz.getText().toString();
                    submissionTypeContainer.setVisibility(View.GONE);
                    judgingCriteriaBox.setVisibility(View.GONE);
                    questionAdditionBox.setVisibility(View.VISIBLE);
                    quizStartDateTimeContainer.setVisibility(View.VISIBLE);
                    quizStartDateTimePickerContainer.setVisibility(View.VISIBLE);
                    picture.setChecked(false);
                    mediaLink.setChecked(false);
                }
                if (submission.getId() == checkedId) {
                    contestType = submission.getText().toString();
                    submissionTypeContainer.setVisibility(View.VISIBLE);
                    judgingCriteriaBox.setVisibility(View.VISIBLE);
                    questionAdditionBox.setVisibility(View.GONE);
                    quizStartDateTimeContainer.setVisibility(View.GONE);
                    quizStartDateTimePickerContainer.setVisibility(View.GONE);
                }
                if (offline.getId() == checkedId) {
                    contestType = offline.getText().toString();
                    submissionTypeContainer.setVisibility(View.VISIBLE);
                    judgingCriteriaBox.setVisibility(View.VISIBLE);
                }
            }
        });
        PictureVideoDocument.setOnCheckedChangeListener((group, checkedId) -> {
            hideKeyboardFrom(mContext, PictureVideoDocument);
            for (int i = 0; i < group.getChildCount(); i++) {
                if (picture.getId() == checkedId) fileType = picture.getText().toString();
                if (mediaLink.getId() == checkedId) fileType = mediaLink.getText().toString();
            }
        });
        selectDomain.setOnClickListener(v -> {
            hideKeyboardFrom(mContext, selectDomain);
            BottomSheetDomain bottomSheetDomain = new BottomSheetDomain();
            bottomSheetDomain.show(getSupportFragmentManager(), "Domain Selection");
        });

        addQuestionButton.setOnClickListener(v -> {
            AddQuestionDialog dialog = new AddQuestionDialog();
            dialog.show(getSupportFragmentManager(), "Add");
//            Log.d(TAG, "addQuestionButton: "+dialog.getQuizQuestion().getQuestion());
//            Log.d(TAG, "addQuestionButton: "+dialog.getQuizQuestion().getQuestion());
//            Log.d(TAG, "addQuestionButton: "+dialog.getQuizQuestion().getQuestion());
//            Log.d(TAG, "addQuestionButton: "+dialog.getQuizQuestion().getQuestion());


//            if(quizQuestionArrayList.size()<dialog.getQuizQuestionArrayList().size()) {
//                quizQuestionArrayList.clear();
//                quizQuestionArrayList.addAll(dialog.getQuizQuestionArrayList());
//            }
        });
        VotingType.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (Public.getId() == checkedId) {
                    criteriaContainer.setVisibility(View.GONE);
                    votingType = Public.getText().toString();

                    Public.setClickable(false);
                    Jury.setClickable(true);
                    PublicAndJury.setClickable(true);
                    Jury.setChecked(false);
                    PublicAndJury.setChecked(false);
                    one.setChecked(false);
                    two.setChecked(false);
                    three.setChecked(false);

                    noOfJury = "";
                    juryName1.setText("");
                    JuryName1 = "";
                    juryName2.setText("");
                    JuryName2 = "";
                    juryName3.setText("");
                    JuryName3 = "";

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
                } else if (Jury.getId() == checkedId) {
                    criteriaContainer.setVisibility(View.VISIBLE);
                    votingType = Jury.getText().toString();

                    Public.setClickable(true);
                    Jury.setClickable(false);
                    PublicAndJury.setClickable(true);
                    Public.setChecked(false);
                    PublicAndJury.setChecked(false);
                    one.setChecked(false);
                    two.setChecked(false);
                    three.setChecked(false);

                    noOfJury = "";
                    juryName1.setText("");
                    JuryName1 = "";
                    juryName2.setText("");
                    JuryName2 = "";
                    juryName3.setText("");
                    JuryName3 = "";
                    mDisplayDateVB.setText("");
                    mDisplayDateVE.setText("");
                    date3 = "";
                    date4 = "";

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
                        JuryName2 = "";
                        JuryName3 = "";
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
                        JuryName3 = "";
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
                } else {
                    criteriaContainer.setVisibility(View.VISIBLE);
                    votingType = PublicAndJury.getText().toString();

                    Public.setClickable(true);
                    Jury.setClickable(true);
                    PublicAndJury.setClickable(false);
                    Jury.setChecked(false);
                    Public.setChecked(false);
                    one.setChecked(false);
                    two.setChecked(false);
                    three.setChecked(false);

                    noOfJury = "";
                    juryName1.setText("");
                    JuryName1 = "";
                    juryName2.setText("");
                    JuryName2 = "";
                    juryName3.setText("");
                    JuryName3 = "";

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
                        JuryName2 = "";
                        JuryName3 = "";
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
                        JuryName3 = "";
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
                    JuryName2 = "";
                    JuryName3 = "";
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
                    JuryName3 = "";
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
        cross1.setOnClickListener(v -> {
            criteriaFields.add(1);
            criteriaTv1.setText("");
            criteriaLL1.setVisibility(View.GONE);
        });
        cross2.setOnClickListener(v -> {
            criteriaFields.add(2);
            criteriaTv2.setText("");
            criteriaLL2.setVisibility(View.GONE);
        });
        cross3.setOnClickListener(v -> {
            criteriaFields.add(3);
            criteriaTv3.setText("");
            criteriaLL3.setVisibility(View.GONE);
        });
        cross4.setOnClickListener(v -> {
            criteriaFields.add(4);
            criteriaTv4.setText("");
            criteriaLL4.setVisibility(View.GONE);
        });
        cross5.setOnClickListener(v -> {
            criteriaFields.add(5);
            criteriaTv5.setText("");
            criteriaLL5.setVisibility(View.GONE);
        });
        cross6.setOnClickListener(v -> {
            criteriaFields.add(6);
            criteriaTv6.setText("");
            criteriaLL6.setVisibility(View.GONE);
        });
        cross7.setOnClickListener(v -> {
            criteriaFields.add(7);
            criteriaTv7.setText("");
            criteriaLL7.setVisibility(View.GONE);
        });
        cross8.setOnClickListener(v -> {
            criteriaFields.add(8);
            criteriaTv8.setText("");
            criteriaLL8.setVisibility(View.GONE);
        });
        cross9.setOnClickListener(v -> {
            criteriaFields.add(9);

            criteriaTv9.setText("");
            criteriaLL9.setVisibility(View.GONE);
        });
        cross10.setOnClickListener(v -> {
            criteriaFields.add(10);

            criteriaTv10.setText("");
            criteriaLL10.setVisibility(View.GONE);
        });
        criteriaEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                charCount.setText(String.valueOf(count) + "/30");
                if (count > 30) {
                    criteriaEt.setError("Criteria length must be less than 30");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        addCriteria.setOnClickListener(v -> {
            if (criteriaEt.getText() != null && !criteriaEt.getText().toString().equals("")) {
                if (criteriaEt.getText().toString().length() <= 30) {
                    try {
                        if (criteriaFields.get(0).equals(1)) {
                            criteriaFields.remove(0);
                            criteriaLL1.setVisibility(View.VISIBLE);
                            criteriaTv1.setText(criteriaEt.getText().toString());
                            criteriaEt.setText("");
                        } else if (criteriaFields.get(0).equals(2)) {
                            criteriaFields.remove(0);
                            criteriaLL2.setVisibility(View.VISIBLE);
                            criteriaTv2.setText(criteriaEt.getText().toString());
                            criteriaEt.setText("");
                        } else if (criteriaFields.get(0).equals(3)) {
                            criteriaFields.remove(0);
                            criteriaLL3.setVisibility(View.VISIBLE);
                            criteriaTv3.setText(criteriaEt.getText().toString());
                            criteriaEt.setText("");
                        } else if (criteriaFields.get(0).equals(4)) {
                            criteriaFields.remove(0);
                            criteriaLL4.setVisibility(View.VISIBLE);
                            criteriaTv4.setText(criteriaEt.getText().toString());
                            criteriaEt.setText("");
                        } else if (criteriaFields.get(0).equals(5)) {
                            criteriaFields.remove(0);
                            criteriaLL5.setVisibility(View.VISIBLE);
                            criteriaTv5.setText(criteriaEt.getText().toString());
                            criteriaEt.setText("");
                        } else if (criteriaFields.get(0).equals(6)) {
                            criteriaFields.remove(0);
                            criteriaLL6.setVisibility(View.VISIBLE);
                            criteriaTv6.setText(criteriaEt.getText().toString());
                            criteriaEt.setText("");
                        } else if (criteriaFields.get(0).equals(7)) {
                            criteriaFields.remove(0);
                            criteriaLL7.setVisibility(View.VISIBLE);
                            criteriaTv7.setText(criteriaEt.getText().toString());
                            criteriaEt.setText("");
                        } else if (criteriaFields.get(0).equals(8)) {
                            criteriaFields.remove(0);
                            criteriaLL8.setVisibility(View.VISIBLE);
                            criteriaTv8.setText(criteriaEt.getText().toString());
                            criteriaEt.setText("");
                        } else if (criteriaFields.get(0).equals(9)) {
                            criteriaFields.remove(0);
                            criteriaLL9.setVisibility(View.VISIBLE);
                            criteriaTv9.setText(criteriaEt.getText().toString());
                            criteriaEt.setText("");
                        } else if (criteriaFields.get(0).equals(10)) {
                            criteriaFields.remove(0);
                            criteriaLL10.setVisibility(View.VISIBLE);
                            criteriaTv10.setText(criteriaEt.getText().toString());
                            criteriaEt.setText("");
                        }

                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(mContext, "Only 10 criteria can be added", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mContext, "Criteria length must be less than 30", Toast.LENGTH_LONG).show();

                }

            } else {
                criteriaEt.setError("Please enter something");
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
                String dateQuiz = datetime.split(" ")[0];
                if ((isDateAfter(date1, date2) || date2.equals("")) && (dateQuiz.equals("") || isDateAfter(date1, dateQuiz)) && (isDateAfter(date1, date3) || date3.equals("")) && (isDateAfter(date1, date4) || date4.equals("")) && (isDateAfter(date1, date5) || date5.equals(""))) {
                    mDisplayDateRB.setText(date1);
                } else {
                    mDisplayDateRB.setText("");
                    mDisplayDateRB.requestFocus();
                    date1 = "";
                }
            };
            DatePickerDialog dialog = new DatePickerDialog(CC_FillFormActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
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
                String dateQuiz = datetime.split(" ")[0];
                if ((isDateAfter(date1, date2) || date1.equals("")) && (dateQuiz.equals("") || isDateAfter(date2, dateQuiz)) && (isDateAfter(date2, date3) || date3.equals("")) && (isDateAfter(date2, date4) || date4.equals("")) && (isDateAfter(date2, date5) || date5.equals(""))) {
                    mDisplayDateRE.setText(date2);
                } else {
                    mDisplayDateRE.setText("");
                    mDisplayDateRE.requestFocus();
                    date2 = "";
                }
            };

            DatePickerDialog dialog = new DatePickerDialog(CC_FillFormActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener2, year, month, day);
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

            DatePickerDialog dialog = new DatePickerDialog(CC_FillFormActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener3, year, month, day);
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

            DatePickerDialog dialog = new DatePickerDialog(CC_FillFormActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener4, year, month, day);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        mDisplayDateTimeQS.setOnClickListener(v -> customDateTimePicker.showDialog());
        mDisplayDateWin.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            mDateSetListener5 = (view, year15, month15, dayOfMonth) -> {
                month15 = month15 + 1;
                date5 = dayOfMonth + "-" + month15 + "-" + year15;
                String dateQuiz = datetime.split(" ")[0];
                if ((isDateAfter(date1, date5) || date1.equals(""))
                        && (isDateAfter(date2, date5) || date2.equals(""))
                        && (isDateAfter(dateQuiz, date5) || dateQuiz.equals(""))
                        && (isDateAfter(date3, date5) || date3.equals(""))
                        && (isDateAfter(date4, date5) || date4.equals(""))) {
                    mDisplayDateWin.setText(date5);
                } else {
                    mDisplayDateWin.setText("");
                    mDisplayDateWin.requestFocus();
                    date5 = "";
                }
            };
            DatePickerDialog dialog = new DatePickerDialog(CC_FillFormActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener5, year, month, day);
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
                noOfParticipants = "";
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
                fees = "";
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
                prizeFirst = "";
                prizeSecond = "";
                prizeThird = "";
            }
        });
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
                if (posterLink.equals("") || title.equals("") || hosted.equals("") || des.equals("") || openFor.equals("") || contestType.equals("") || domain.equals("")) {
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
                    if (contestType.equals("")) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(QuizSubmission);
                        Toast.makeText(mContext, "Please make a selection", Toast.LENGTH_SHORT).show();
                        QuizSubmission.requestFocus();
                    }
                    if (domain.equals("")) {
                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(selectDomain);
                        Toast.makeText(mContext, "Please make a selection", Toast.LENGTH_SHORT).show();
                        selectDomain.requestFocus();
                    }
                } else if ((contestType.equals("Submission") || contestType.equals("Offline")) && fileType.equals("")) {
                    YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(PictureVideoDocument);
                    Toast.makeText(mContext, "Please make a selection", Toast.LENGTH_SHORT).show();
                    PictureVideoDocument.requestFocus();
                } else {
//                    Log.d(TAG, "onCreate: " + title + hosted + des + openFor + fileType);
                    layout1.setVisibility(View.GONE);
                    previousButton.setVisibility(View.VISIBLE);
                    layout2.setVisibility(View.VISIBLE);
                    layoutActive = 2;
                    active1.setVisibility(View.INVISIBLE);
                    active2.setVisibility(View.VISIBLE);
                }
            } else if (layoutActive == 2) {
                if (contestType.equals("Quiz")) {
                    Log.d(TAG, "list size: "+quizQuestionArrayList.size());
//                    quizQuestionArrayList.clear();
//                    quizQuestionArrayList.addAll(adapterQuestionList.getQuestionList());
                    if (quizQuestionArrayList.size() < MAX_QUIZ_QUESTION || datetime.equals("") || durationSelector.getSelectedItem().toString().equals("") || date1.equals("") || date2.equals("") || date5.equals("")) {
                        if (quizQuestionArrayList.size() < MAX_QUIZ_QUESTION)
                            Toast.makeText(mContext, "You need to enter " + MAX_QUIZ_QUESTION + " questions at least", Toast.LENGTH_LONG).show();
                        if (datetime.equals("")) {
                            YoYo.with(Techniques.Bounce).duration(ANIMATION_DURATION).playOn(mDisplayDateTimeQS);
                            mDisplayDateTimeQS.requestFocus();
                            mDisplayDateTimeQS.setError("Empty!");
                        }
                        if (date1.equals(""))
                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mDisplayDateRB);
                        if (date2.equals(""))
                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mDisplayDateRE);
                        if (date5.equals(""))
                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(mDisplayDateWin);
                        if (durationSelector.getSelectedItem().toString().equals(""))
                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(durationSelector);
                        durationContainer.setVisibility(View.VISIBLE);
                    } else {
                        durationContainer.setVisibility(View.GONE);
                        duration = durationSelector.getSelectedItem().toString();
                        layout3.setVisibility(View.VISIBLE);
                        layout2.setVisibility(View.GONE);
                        active2.setVisibility(View.INVISIBLE);
                        active3.setVisibility(View.VISIBLE);
                        layoutActive = 3;
                    }
                } else {
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
                            if (criteriaFields.size() == 10) {
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(criteriaContainer);
                                Toast.makeText(mContext, "Please enter criteria atleast 1", Toast.LENGTH_SHORT).show();
                                criteriaContainer.requestFocus();
                            } else if (noOfJury.equals("")) {
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
                            } else if (noOfJury.equals("2") || noOfJury.equals("3")) {
                                boolean ok = false;
                                if (noOfJury.equals("2")) {
                                    if (JuryName1.equals(JuryName2)) {
                                        ok = true;
                                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName1);
                                        juryName1.setError("Jury members must be different!");
                                        juryName1.requestFocus();

                                        YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName2);
                                        juryName2.setError("Jury members must be different!");
                                        juryName2.requestFocus();
                                    }

                                }
                                if (noOfJury.equals("3")) {

                                    if (JuryName1.equals(JuryName2) || JuryName1.equals(JuryName3) || JuryName2.equals(JuryName3)) {
                                        ok = true;
                                        if (JuryName1.equals(JuryName2)) {
                                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName1);
                                            juryName1.setError("Jury members must be different!");
                                            juryName1.requestFocus();

                                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName2);
                                            juryName2.setError("Jury members must be different!");
                                            juryName2.requestFocus();
                                        }
                                        if (JuryName1.equals(JuryName3)) {
                                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName1);
                                            juryName1.setError("Jury members must be different!");
                                            juryName1.requestFocus();

                                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName3);
                                            juryName3.setError("Jury members must be different!");
                                            juryName3.requestFocus();
                                        }
                                        if (JuryName3.equals(JuryName2)) {
                                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName3);
                                            juryName3.setError("Jury members must be different!");
                                            juryName3.requestFocus();

                                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName2);
                                            juryName2.setError("Jury members must be different!");
                                            juryName2.requestFocus();
                                        }

                                    }
                                }

                                if (!ok) {
                                    layout3.setVisibility(View.VISIBLE);
                                    layout2.setVisibility(View.GONE);
                                    active2.setVisibility(View.INVISIBLE);
                                    active3.setVisibility(View.VISIBLE);
                                    layoutActive = 3;
                                }

                            } else {
                                layout3.setVisibility(View.VISIBLE);
                                layout2.setVisibility(View.GONE);
                                active2.setVisibility(View.INVISIBLE);
                                active3.setVisibility(View.VISIBLE);
                                layoutActive = 3;
                            }
                        }
                        if (votingType.equals("Jury and Public")) {
                            if (criteriaFields.size() == 10) {
                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(criteriaContainer);
                                Toast.makeText(mContext, "Please enter criteria atleast 1", Toast.LENGTH_SHORT).show();
                                criteriaContainer.requestFocus();
                            } else if (noOfJury.equals("")) {
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
                                } else if (noOfJury.equals("2") || noOfJury.equals("3")) {
                                    boolean ok = false;
                                    if (noOfJury.equals("2")) {
                                        if (JuryName1.equals(JuryName2)) {
                                            ok = true;
                                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName1);
                                            juryName1.setError("Jury members must be different!");
                                            juryName1.requestFocus();
                                            YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName2);
                                            juryName2.setError("Jury members must be different!");
                                            juryName2.requestFocus();
                                        }

                                    }
                                    if (noOfJury.equals("3")) {

                                        if (JuryName1.equals(JuryName2) || JuryName1.equals(JuryName3) || JuryName2.equals(JuryName3)) {
                                            ok = true;

                                            if (JuryName1.equals(JuryName2)) {
                                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName1);
                                                juryName1.setError("Jury members must be different!");
                                                juryName1.requestFocus();

                                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName2);
                                                juryName2.setError("Jury members must be different!");
                                                juryName2.requestFocus();
                                            }
                                            if (JuryName1.equals(JuryName3)) {
                                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName1);
                                                juryName1.setError("Jury members must be different!");
                                                juryName1.requestFocus();

                                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName3);
                                                juryName3.setError("Jury members must be different!");
                                                juryName3.requestFocus();
                                            }
                                            if (JuryName3.equals(JuryName2)) {
                                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName3);
                                                juryName3.setError("Jury members must be different!");
                                                juryName3.requestFocus();

                                                YoYo.with(Techniques.Shake).duration(ANIMATION_DURATION).playOn(juryName2);
                                                juryName2.setError("Jury members must be different!");
                                                juryName2.requestFocus();
                                            }

                                        }
                                    }
                                    if (!ok) {
                                        layout3.setVisibility(View.VISIBLE);
                                        layout2.setVisibility(View.GONE);
                                        active2.setVisibility(View.INVISIBLE);
                                        active3.setVisibility(View.VISIBLE);
                                        layoutActive = 3;
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
                        || checkFee(fees, prizeThird))) {
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
                    if (!fees.equals("")) {
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

    }


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

    private boolean checkFee(String fees, String prizeThird) {
        if (fees.equals("")) {
            return false;
        } else {
            return Integer.parseInt(prizeThird) <= Integer.parseInt(fees);
        }
    }

    private void submit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are u sure about all the details and create this contest?")
                .setTitle("Create Contest")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    Intent i = new Intent(CC_FillFormActivity.this, CC_CheckActivity.class);
                    i.putExtra("entryfee", fees);
                    i.putExtra("title", title);
                    i.putExtra("descrip", des);
                    i.putExtra("poster", posterLink);
                    i.putExtra("contestType", contestType);
                    i.putExtra("filetype", fileType);
                    if (datetime.equals(""))
                        i.putExtra("startTime", "-");
                    else
                        i.putExtra("startTime", datetime);
                    i.putExtra("duration", duration);
                    i.putExtra("duration", duration);
                    if (quizQuestionArrayList.size() == 0)
                        i.putExtra("questionList", "");
                    else {
                        i.putParcelableArrayListExtra("questionList", quizQuestionArrayList);
                    }
                    i.putExtra("domain", domain);
                    i.putExtra("votetype", votingType);
                    i.putExtra("rule", extraRule);
                    i.putExtra("regBegin", date1.replace("/", "-"));
                    i.putExtra("regEnd", date2.replace("/", "-"));
                    if (date3.equals("")) i.putExtra("voteBegin", "-");
                    else i.putExtra("voteBegin", date3.replace("/", "-"));
                    if (date4.equals("")) i.putExtra("voteEnd", "-");
                    else i.putExtra("voteEnd", date4.replace("/", "-"));
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
                    if (votingType.equals("Jury") || votingType.equals("Jury and Public")) {
                        String judging_criterias = getJudgingString();
                        i.putExtra("judgeCriteria", judging_criterias);
                    }
                    startActivity(i);
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                .show();
    }

    private String getJudgingString() {
        String f_string = "";
        String addString = "";
        boolean ok = true;
        for (int x = 1; x <= 10; x++) {
            addString = getStringFromTV(x);
            if (addString == null || addString.equals("")) {
            } else {
                if (ok) {
                    f_string = addString;
                    ok = false;
                } else {
                    f_string = f_string + "///" + addString;

                }
            }

        }
        return f_string;
    }

    private String getStringFromTV(int l) {
        if (l == 1) {
            return (criteriaTv1.getText().toString());
        } else if (l == 2) {

            return (criteriaTv2.getText().toString());
        } else if (l == 3) {

            return (criteriaTv3.getText().toString());
        } else if (l == 4) {

            return (criteriaTv4.getText().toString());
        } else if (l == 5) {

            return (criteriaTv5.getText().toString());
        } else if (l == 6) {

            return (criteriaTv6.getText().toString());
        } else if (l == 7) {

            return (criteriaTv7.getText().toString());
        } else if (l == 8) {

            return (criteriaTv8.getText().toString());
        } else if (l == 9) {

            return (criteriaTv9.getText().toString());
        } else if (l == 10) {

            return (criteriaTv10.getText().toString());
        } else return null;
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
        mTopBarTitle = findViewById(R.id.titleTopBar);
        mTopBarTitle.setText("Create Contest");

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
        submissionTypeContainer = findViewById(R.id.submissionTypeContainer);
        QuizSubmission = findViewById(R.id.QuizSubmission);
        quiz = findViewById(R.id.quiz);
        submission = findViewById(R.id.submission);
        offline = findViewById(R.id.offline);
        PictureVideoDocument = findViewById(R.id.PictureVideoDocument);
        picture = findViewById(R.id.picture);
        mediaLink = findViewById(R.id.Media_Link);
        selectDomain = findViewById(R.id.selectDomain);

        //layout 2 widgets
        durationContainer = findViewById(R.id.durationContainer);
        recyclerView = findViewById(R.id.recyclerView);
        durationSelector = findViewById(R.id.durationSelector);
        quizStartDateTimeContainer = findViewById(R.id.quizStartDateTimeContainer);
        quizStartDateTimePickerContainer = findViewById(R.id.quizStartDateTimePickerContainer);

        judgingCriteriaBox = findViewById(R.id.judgingCriteriaBox);
        questionAdditionBox = findViewById(R.id.questionAdditionBox);
        addQuestionButton = findViewById(R.id.addQuestionButton);
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
        mDisplayDateTimeQS = findViewById(R.id.datetimePicker);
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

        criteriaEt = findViewById(R.id.criteriaEt);
        addCriteria = findViewById(R.id.add);
        charCount = findViewById(R.id.txtCount);
        criteriaContainer = findViewById(R.id.judgingCriteria);

        cross1 = findViewById(R.id.cross1);
        cross2 = findViewById(R.id.cross2);
        cross3 = findViewById(R.id.cross3);
        cross4 = findViewById(R.id.cross4);
        cross5 = findViewById(R.id.cross5);
        cross6 = findViewById(R.id.cross6);
        cross7 = findViewById(R.id.cross7);
        cross8 = findViewById(R.id.cross8);
        cross9 = findViewById(R.id.cross9);
        cross10 = findViewById(R.id.cross10);

        criteriaTv1 = findViewById(R.id.c1);
        criteriaTv2 = findViewById(R.id.c2);
        criteriaTv3 = findViewById(R.id.c3);
        criteriaTv4 = findViewById(R.id.c4);
        criteriaTv5 = findViewById(R.id.c5);
        criteriaTv6 = findViewById(R.id.c6);
        criteriaTv7 = findViewById(R.id.c7);
        criteriaTv8 = findViewById(R.id.c8);
        criteriaTv9 = findViewById(R.id.c9);
        criteriaTv10 = findViewById(R.id.c10);

        criteriaLL1 = findViewById(R.id.criteriaLinear1);
        criteriaLL2 = findViewById(R.id.criteriaLinear2);
        criteriaLL3 = findViewById(R.id.criteriaLinear3);
        criteriaLL4 = findViewById(R.id.criteriaLinear4);
        criteriaLL5 = findViewById(R.id.criteriaLinear5);
        criteriaLL6 = findViewById(R.id.criteriaLinear6);
        criteriaLL7 = findViewById(R.id.criteriaLinear7);
        criteriaLL8 = findViewById(R.id.criteriaLinear8);
        criteriaLL9 = findViewById(R.id.criteriaLinear9);
        criteriaLL10 = findViewById(R.id.criteriaLinear10);


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
                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4);
                }
                String[] contentUriPrefixesToTry = new String[]{
                        "content://downloads/public_downloads",
                        "content://downloads/my_downloads"
                };
                for (String contentUriPrefix : contentUriPrefixesToTry) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));
                    try {
                        String path = getDataColumn(context, contentUri, null, null);
                        if (path != null) {
                            return path;
                        }
                    } catch (Exception e) {
                    }
                }

                // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                String fileName = getFileName(context, uri);
                File cacheDir = getDocumentCacheDir(context);
                File file = generateFileName(fileName, cacheDir);
                String destinationPath = null;
                if (file != null) {
                    destinationPath = file.getAbsolutePath();
                    saveFileFromUri(context, uri, destinationPath);
                }
                return destinationPath;
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
        else if ("file".equalsIgnoreCase(uri.getScheme()))
            return uri.getPath();
        else
            Toast.makeText(context, "Unable to upload image", Toast.LENGTH_LONG).show();
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
                if (imgPath != null) {
                    Log.d(TAG, "onActivityResult: path: " + imgPath);
                    Log.d(TAG, "onActivityResult: uri: " + uri);
                    imgurl = imgPath;
                    setImage();
                }
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
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) selectPoster.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
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
                        .into(poster);
            }
        }
    }

    public void verifyPermission(String[] permissions) {
        ActivityCompat.requestPermissions(CC_FillFormActivity.this, permissions, VERIFY_PERMISSION_REQUEST);
    }

    public boolean checkPermissionArray(String[] permissions) {
        for (String check : permissions) if (!checkPermissions(check)) return false;
        return true;
    }

    public boolean checkPermissions(String permission) {
        int permissionRequest = ActivityCompat.checkSelfPermission(CC_FillFormActivity.this, permission);
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

    @Override
    public void onClick(QuizQuestion quizQuestion, boolean isEdit, int position) {
        if (!quizQuestion.isEmpty()) {
            quizQuestionArrayList.add(quizQuestion);
            adapterQuestionList.notifyItemInserted(quizQuestionArrayList.size() - 1);
        }
        Log.d(TAG, "onClick: " + quizQuestionArrayList.size());
    }
}
