package com.orion.orion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.contest.create.CreatedActivity;
import com.orion.orion.contest.upcoming.UpcomingContestActivity;
import com.orion.orion.login.LoginActivity;
import com.orion.orion.models.QuizQuestion;
import com.orion.orion.models.QuizQuestionEncoded;
import com.orion.orion.util.SNTPClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class QuizActivity extends AppCompatActivity {

    private static final int STREAK_INITIAL_POINTS = 100;
    private static final int STREAK_GROWTH_POINTS = 50;
    private static final int PERCENTAGE_TIME_MULTIPLIER = 5;
    private Context mContext;
    private static final String TAG = "QuizActivity";
    private static final int ANIMATION_DURATION = 500;
    private static final int DURATION_INTERVAL = 100;
    private static final int PROGRESS_LENGTH = 1000;
    private static int QUESTION_DURATION;
    private final static String DATE_FORMAT_PATTERN_SNTP = "yyyy-MM-dd'T'HH:mm:ssZ";
    private final static String DATE_FORMAT_PATTERN_DATETIME = "dd-M-yyyy hh:mm:ss";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private DatabaseReference myRef;

    private TextView topBarTitle;
    private ImageView backArrow;

    private RelativeLayout startLayout;
    private TextView timerCountDown;
    private Button startButton;
    private ImageView close;

    private RelativeLayout quizLayout;
    private RelativeLayout questionBox;
    private ProgressBar mProgressBar;
    private TextView questionTag;
    private TextView question;
    private RelativeLayout option1Container;
    private RelativeLayout option2Container;
    private RelativeLayout option3Container;
    private RelativeLayout option4Container;
    private TextView option1Id;
    private TextView option2Id;
    private TextView option3Id;
    private TextView option4Id;
    private TextView option1Value;
    private TextView option2Value;
    private TextView option3Value;
    private TextView option4Value;

    private RelativeLayout endLayout;
    private Button doneButton;

    private CountDownTimer mCountDownTimer;
    CountDownTimer startQuizCountDownTimer;
    int i = 0;

    private ProgressDialog dialog;
    public ArrayList<QuizQuestion> quizQuestionArrayList;
    private int currentQuestionIdx = 0;
    private int pointsAccuracy = 0;
    private int pointsSpeed = 0;
    private int pointsConsistency = 0;

    private int streakPoints;
    private boolean allQuestionsFetched = false;
    private boolean lastQuestionCorrect = false;
    private long difference;

    private String contestType;
    private String contestId;
    private String userId;

    private Boolean isQuizTaken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_quiz);

        startLayout = findViewById(R.id.startLayout);
        timerCountDown = findViewById(R.id.timerCountdown);
        close = findViewById(R.id.closeButton);
        startButton = findViewById(R.id.startButton);
        quizLayout = findViewById(R.id.quizLayout);
        topBarTitle = findViewById(R.id.titleTopBar);
        backArrow = findViewById(R.id.backarrow);
        mProgressBar = findViewById(R.id.progressbar);
        topBarTitle.setText("Quiz");
        questionBox = findViewById(R.id.questionBox);
        questionTag = findViewById(R.id.questionTag);
        question = findViewById(R.id.question);
        option1Container = findViewById(R.id.option1);
        option2Container = findViewById(R.id.option2);
        option3Container = findViewById(R.id.option3);
        option4Container = findViewById(R.id.option4);
        option1Id = option1Container.findViewById(R.id.optionId);
        option2Id = option2Container.findViewById(R.id.optionId);
        option3Id = option3Container.findViewById(R.id.optionId);
        option4Id = option4Container.findViewById(R.id.optionId);
        option1Value = option1Container.findViewById(R.id.optionValue);
        option2Value = option2Container.findViewById(R.id.optionValue);
        option3Value = option3Container.findViewById(R.id.optionValue);
        option4Value = option4Container.findViewById(R.id.optionValue);
        endLayout = findViewById(R.id.endLayout);
        doneButton = findViewById(R.id.doneButton);

        quizQuestionArrayList = new ArrayList<>();
        close.setOnClickListener(v -> showStopQuizDialog());
        backArrow.setOnClickListener(v-> showStopQuizDialog());
        startButton.setOnClickListener(v -> {
            dialog = ProgressDialog.show(this, "", "Loading Questions ...", true);

            quizQuestionArrayList = randomizeList(quizQuestionArrayList);
            Log.d(TAG, "quizQuestionArrayList " + quizQuestionArrayList.size());

            startLayout.setClickable(false);
            startLayout.setVisibility(View.GONE);
            quizLayout.setVisibility(View.VISIBLE);
            fetchNextQuestion(true);
        });
        option1Container.setOnClickListener(v -> {
            QuizQuestion quizQuestion = quizQuestionArrayList.get(currentQuestionIdx - 1);
            quizQuestion.setSelected(quizQuestion.getOption1());
            quizQuestionArrayList.set(currentQuestionIdx - 1, quizQuestion);
            fetchNextQuestion(false);
        });
        option2Container.setOnClickListener(v -> {
            QuizQuestion quizQuestion = quizQuestionArrayList.get(currentQuestionIdx - 1);
            quizQuestion.setSelected(quizQuestion.getOption2());
            quizQuestionArrayList.set(currentQuestionIdx - 1, quizQuestion);
            fetchNextQuestion(false);
        });
        option3Container.setOnClickListener(v -> {
            QuizQuestion quizQuestion = quizQuestionArrayList.get(currentQuestionIdx - 1);
            quizQuestion.setSelected(quizQuestion.getOption3());
            quizQuestionArrayList.set(currentQuestionIdx - 1, quizQuestion);
            fetchNextQuestion(false);
        });
        option4Container.setOnClickListener(v -> {
            QuizQuestion quizQuestion = quizQuestionArrayList.get(currentQuestionIdx - 1);
            quizQuestion.setSelected(quizQuestion.getOption4());
            quizQuestionArrayList.set(currentQuestionIdx - 1, quizQuestion);
            fetchNextQuestion(false);
        });
        doneButton.setOnClickListener(v -> finish());

        mContext = QuizActivity.this;
        myRef = FirebaseDatabase.getInstance().getReference();
        QUESTION_DURATION = 10000;
        streakPoints = STREAK_INITIAL_POINTS;
        mProgressBar.setProgress(0);
        mCountDownTimer = new CountDownTimer(QUESTION_DURATION, DURATION_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                i++;
                mProgressBar.setProgress(i * 100 / (QUESTION_DURATION / DURATION_INTERVAL));
            }

            @Override
            public void onFinish() {
                mProgressBar.setProgress(PROGRESS_LENGTH);
                fetchNextQuestion(true);
            }
        };

        Intent intent = getIntent();
        contestType = intent.getStringExtra("contestType");
        contestId = intent.getStringExtra("contestId");
        userId = intent.getStringExtra("userId");

        setupFirebaseAuth();
        checkQuizTaken();
        if (!isQuizTaken)
            initalizeStartQuizTimer();

    }

    private void initalizeStartQuizTimer() {

        SNTPClient.getDate(TimeZone.getTimeZone("Asia/Colombo"), new SNTPClient.Listener() {
            @Override
            public void onTimeReceived(String rawDate) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN_SNTP);
                Date currentDateTime = parseDate(rawDate, dateFormat);
//                currentDateTime.setDate(16);
//                currentDateTime.setMonth(5);
//                currentDateTime.setHours(0);
//                currentDateTime.setMinutes(0);
//                currentDateTime.setSeconds(23);
                myRef.child(getString(R.string.dbname_contests))
                        .child(userId)
                        .child(getString(R.string.created_contest))
                        .child(contestId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {

                                    String datetime = snapshot.child(getString(R.string.field_quizStartDateTime)).getValue().toString();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN_DATETIME);
                                    Date quizDateTime = parseDate(datetime, dateFormat);
                                    String duration = snapshot.child(getString(R.string.field_quesDuration)).getValue().toString();
                                    int noOfQuestion = (int) snapshot.child(getString(R.string.field_questions)).getChildrenCount();

                                    QUESTION_DURATION = Integer.parseInt(duration.substring(0, 2)) * 1000;
                                    int activeTime = (int) (QUESTION_DURATION * noOfQuestion * 1.5);
                                    difference = quizDateTime.getTime() - currentDateTime.getTime();
//

//                                    Log.d(TAG, "onDataChange: SNTP currentDateT " + currentDateTime);
//                                    Log.d(TAG, "onDataChange: SNTP quizDateTime " + quizDateTime);
//                                    Log.d(TAG, "onDataChange: SNTP difference " + difference);
//                                    Log.d(TAG, "onDataChange: SNTP difference " + differenceToString(difference));
//                                    Log.d(TAG, "onDataChange: SNTP duration " + duration);
//                                    Log.d(TAG, "onDataChange: SNTP noOfQuestion " + noOfQuestion);
//                                    Log.d(TAG, "onDataChange: SNTP activeTime " + activeTime);

                                    fetchQuizQuestions(contestId, userId, quizQuestionArrayList);
                                    updateUI(difference, activeTime);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

            @Override
            public void onError(Exception ex) {
                Log.e(SNTPClient.TAG, ex.getMessage());
            }
        });

    }

    private void updateUI(long difference, int activeTime) {
        if (difference > 0) {
            startButton.setClickable(false);
            startButton.setAlpha(0.3f);
            timerCountDown.setText(differenceToString(difference));
            startQuizCountDownTimer = new CountDownTimer(difference, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    timerCountDown.setText(differenceToString(millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    startButton.setClickable(true);
                    startButton.setAlpha(1f);
                    timerCountDown.setText("The Quiz is live");
                }
            };
            startQuizCountDownTimer.start();
        } else if (difference < 0 && Math.abs(difference) < activeTime) {
            startButton.setClickable(true);
            startButton.setAlpha(1f);
            timerCountDown.setText("The Quiz is live");
        } else {
            startButton.setClickable(false);
            startButton.setAlpha(0.3f);
            timerCountDown.setText("The Quiz is no longer available!");
        }
    }

    private void fetchQuizQuestions(String contestId, String userId, ArrayList<QuizQuestion> quizQuestionArrayList) {
        myRef.child(getString(R.string.dbname_contests))
                .child(userId)
                .child(getString(R.string.created_contest))
                .child(contestId)
                .child(getString(R.string.field_questions))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<QuizQuestion> quizQuestions = new ArrayList<>();
                        if (snapshot.exists())
                            for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                if (dataSnapshot.exists()) {
                                    QuizQuestionEncoded quizQuestionEncoded = dataSnapshot.getValue(QuizQuestionEncoded.class);
                                    String ques = quizQuestionEncoded.getQu();
                                    String[] options = quizQuestionEncoded.getOpt().split(getString(R.string.option_seperator_delimintor));
                                    String ans = quizQuestionEncoded.getAns();
                                    QuizQuestion question = new QuizQuestion();
                                    question.setQuestion(ques);
                                    question.setOption1(options[0]);
                                    question.setOption2(options[1]);
                                    question.setOption3(options[2]);
                                    question.setOption4(options[3]);
                                    question.setAnswer(ans);
                                    quizQuestions.add(question);
                                }
                        quizQuestionArrayList.addAll(quizQuestions);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public ArrayList<QuizQuestion> randomizeList(ArrayList<QuizQuestion> quizQuestions) {
        Random rand = new Random();
        ArrayList<QuizQuestion> newList = new ArrayList<>();
        int size = quizQuestions.size();
        for (int i = 0; i < size; i++) {
            int randomIndex = rand.nextInt(quizQuestions.size());
            newList.add(quizQuestions.get(randomIndex));
            quizQuestions.remove(randomIndex);
        }
        quizQuestions.clear();
        dialog.dismiss();
        return newList;
    }

    @SuppressLint("SetTextI18n")
    private void fetchNextQuestion(boolean timedOut) {
        mCountDownTimer.cancel();
        if (currentQuestionIdx < quizQuestionArrayList.size()) {
            if (timedOut)
                lastQuestionCorrect = false;
            else
                updatePoints();
            questionTag.setText("Question " + (currentQuestionIdx + 1) + " of " + quizQuestionArrayList.size());
            question.setText(quizQuestionArrayList.get(currentQuestionIdx).getQuestion() + i);
            option1Id.setText("A");
            option2Id.setText("B");
            option3Id.setText("C");
            option4Id.setText("D");
            option1Value.setText(quizQuestionArrayList.get(currentQuestionIdx).getOption1());
            option2Value.setText(quizQuestionArrayList.get(currentQuestionIdx).getOption2());
            option3Value.setText(quizQuestionArrayList.get(currentQuestionIdx).getOption3());
            option4Value.setText(quizQuestionArrayList.get(currentQuestionIdx).getOption4());
            YoYo.with(Techniques.FadeInRight).duration(ANIMATION_DURATION).playOn(questionBox);
            YoYo.with(Techniques.FadeInRight).duration(ANIMATION_DURATION).playOn(option1Container);
            YoYo.with(Techniques.FadeInRight).duration(ANIMATION_DURATION).playOn(option2Container);
            YoYo.with(Techniques.FadeInRight).duration(ANIMATION_DURATION).playOn(option3Container);
            YoYo.with(Techniques.FadeInRight).duration(ANIMATION_DURATION).playOn(option4Container);
            i = 0;
            mProgressBar.setProgress(i);
            mCountDownTimer.start();
            currentQuestionIdx++;
        } else {
            if (currentQuestionIdx == quizQuestionArrayList.size()) {
                if (timedOut)
                    lastQuestionCorrect = false;
                else
                    updatePoints();
            }
            uploadResults();
            quizLayout.setVisibility(View.GONE);
            endLayout.setVisibility(View.VISIBLE);
        }
    }

    private void updatePoints() {
        String answer = quizQuestionArrayList.get(currentQuestionIdx - 1).getAnswer();
        String attempted = quizQuestionArrayList.get(currentQuestionIdx - 1).getSelected();
        Log.d(TAG, "updatePoints: currentQuestionIdx" + (currentQuestionIdx - 1));
        Log.d(TAG, "updatePoints: answer" + answer);
        Log.d(TAG, "updatePoints: attempted" + attempted);
        if (answer.equals(attempted)) {
            Log.d(TAG, "updatePoints: true");
            //algo to calculate points
            pointsAccuracy += 1000;
            int timePercentageLeft = 100 - i * 100 / (QUESTION_DURATION / DURATION_INTERVAL);
            pointsSpeed += timePercentageLeft * PERCENTAGE_TIME_MULTIPLIER;
            if (lastQuestionCorrect) {
                streakPoints += STREAK_GROWTH_POINTS;
                pointsConsistency += streakPoints;
            }
            lastQuestionCorrect = true;
        } else {
            Log.d(TAG, "updatePoints: false");
            lastQuestionCorrect = false;
            streakPoints = STREAK_INITIAL_POINTS;
        }
    }

    private void uploadResults() {
        Log.d(TAG, "uploadResults: pointsAccuracy" + pointsAccuracy);
        Log.d(TAG, "uploadResults: pointsSpeed" + pointsSpeed);
        Log.d(TAG, "uploadResults: pointsConsistency" + pointsConsistency);

        dialog = ProgressDialog.show(this, "", "Finishing Up...", true);
        myRef.child(getString(R.string.dbname_participantList))
                .child(contestId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                            for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                if (dataSnapshot.exists()) {
                                    String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    if (userid.equals(dataSnapshot.child(getString(R.string.field_user_id)).getValue().toString())) {
                                        myRef.child(getString(R.string.dbname_participantList))
                                                .child(contestId)
                                                .child(dataSnapshot.getKey())
                                                .child(getString(R.string.juryMarks))
                                                .child(getString(R.string.field_jury_1))
                                                .setValue(String.valueOf(pointsAccuracy));
                                        myRef.child(getString(R.string.dbname_participantList))
                                                .child(contestId)
                                                .child(dataSnapshot.getKey())
                                                .child(getString(R.string.juryMarks))
                                                .child(getString(R.string.field_jury_2))
                                                .setValue(String.valueOf(pointsSpeed));
                                        myRef.child(getString(R.string.dbname_participantList))
                                                .child(contestId)
                                                .child(dataSnapshot.getKey())
                                                .child(getString(R.string.juryMarks))
                                                .child(getString(R.string.field_jury_3))
                                                .setValue(String.valueOf(pointsConsistency));
                                        myRef.child(getString(R.string.dbname_participantList))
                                                .child(contestId)
                                                .child(dataSnapshot.getKey())
                                                .child(getString(R.string.juryMarks))
                                                .child(getString(R.string.field_quiz_taken))
                                                .setValue(true);
                                    }
                                }
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public static Date parseDate(String stringToParse, SimpleDateFormat sdf) {
        Date date;
        try {
            date = sdf.parse(stringToParse);
            return date;
        } catch (ParseException e) {
            Log.d(TAG, "parseDate: " + e);
        }
        return null;
    }

    public String differenceToString(long difference) {
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = difference / daysInMilli;
        difference = difference % daysInMilli;
        long elapsedHours = difference / hoursInMilli;
        difference = difference % hoursInMilli;
        long elapsedMinutes = difference / minutesInMilli;
        difference = difference % minutesInMilli;
        long elapsedSeconds = difference / secondsInMilli;

        return String.format("%d days, %d hours, %d minutes, %d seconds%n", elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
    }

    private void showStopQuizDialog() {
        if(quizLayout.getVisibility()==View.VISIBLE)
            new AlertDialog.Builder(mContext)
                    .setTitle("Are you sure you want to exit the quiz?")
                    .setMessage("You won't be able to join it again. \nAll answers will be saved as it is and will be evaluated accordingly.")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        ProgressDialog progressdialog = new ProgressDialog(getApplicationContext());
                        progressdialog.setMessage("Saving answers....");
                        uploadResults();
                        finish();
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {

                    })
                    .show();
        else
            startActivity(new Intent(mContext, UpcomingContestActivity.class));
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
                new AlertDialog.Builder(mContext)
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

    @Override
    protected void onStop() {
        super.onStop();
        uploadResults();
    }

    @Override
    protected void onPause() {
        super.onPause();
        uploadResults();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkQuizTaken();
    }

    private void checkQuizTaken() {
        dialog = ProgressDialog.show(this, "", "Loading Up...", true);
        myRef.child(getString(R.string.dbname_participantList))
                .child(contestId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                            for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                if (dataSnapshot.exists()) {
                                    String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                                    Log.d(TAG, "checkQuizTaken: found user "+userid.equals(dataSnapshot.child(getString(R.string.field_user_id)).getValue().toString()));
                                    if (userid.equals(dataSnapshot.child(getString(R.string.field_user_id)).getValue().toString())) {
//                                        Log.d(TAG, "checkQuizTaken: quiz attempted "+(dataSnapshot.child(getString(R.string.juryMarks)).child(getString(R.string.field_quiz_taken)).getValue()!=null));
                                        if (dataSnapshot.child(getString(R.string.juryMarks)).child(getString(R.string.field_quiz_taken)).getValue() != null) {
                                            isQuizTaken = (Boolean) dataSnapshot.child(getString(R.string.juryMarks)).child(getString(R.string.field_quiz_taken)).getValue();
//                                            Log.d(TAG, "checkQuizTaken: isQuizTaken "+isQuizTaken);
                                            if (isQuizTaken) {
                                                startButton.setClickable(false);
                                                startButton.setAlpha(0.3f);
                                                timerCountDown.setText("You have already attempted the quiz!");
                                            }
                                        }
                                    }
                                }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        dialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkQuizTaken();
    }

    @Override
    public void onBackPressed() {
        showStopQuizDialog();
//        super.onBackPressed();
    }
}