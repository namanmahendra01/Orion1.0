package com.orion.orion;

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
import com.orion.orion.contest.create.CreatedActivity;
import com.orion.orion.login.LoginActivity;
import com.orion.orion.models.QuizQuestion;
import com.orion.orion.profile.Account.About;

import java.util.ArrayList;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {
    private static final int STREAK_INITIAL_POINTS = 100;
    private static final int STREAK_GROWTH_POINTS = 50;
    private Context mContext;
    private static final String TAG = "QuizActivity";
    private static final int ANIMATION_DURATION = 100;
    private static final int DURATION_INTERVAL = 100;
    private static final int PROGRESS_LENGTH = 1000;
    private static int QUESTION_DURATION;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;


    private TextView topBarTitle;
    private ImageView backArrow;

    private RelativeLayout startLayout;
    private Button startButton;

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
    int i = 0;

    private ArrayList<QuizQuestion> quizQuestionArrayList;
    private int currentQuestionIdx = 0;
    private int points = 0;
    private int streakPoints;
    private boolean lastQuestionCorrect = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_quiz);
        mContext = QuizActivity.this;

        startLayout = findViewById(R.id.startLayout);
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

        setupFirebaseAuth();
        quizQuestionArrayList = fetchQuizQuestions();
        quizQuestionArrayList = getRandomizedList(quizQuestionArrayList);


        backArrow.setOnClickListener(v -> showStopQuizDialog());
        startButton.setOnClickListener(v -> {
            startLayout.setClickable(false);
            startLayout.setVisibility(View.GONE);
            quizLayout.setVisibility(View.VISIBLE);
            fetchNextQuestion(true);
        });
        option1Container.setOnClickListener(v -> {
            QuizQuestion quizQuestion = quizQuestionArrayList.get(currentQuestionIdx-1);
            quizQuestion.setSelected(quizQuestion.getOption1());
            quizQuestionArrayList.set(currentQuestionIdx-1, quizQuestion);
            fetchNextQuestion(false);

        });
        option2Container.setOnClickListener(v -> {
            QuizQuestion quizQuestion = quizQuestionArrayList.get(currentQuestionIdx-1);
            quizQuestion.setSelected(quizQuestion.getOption2());
            quizQuestionArrayList.set(currentQuestionIdx-1, quizQuestion);
            fetchNextQuestion(false);
        });
        option3Container.setOnClickListener(v -> {
            QuizQuestion quizQuestion = quizQuestionArrayList.get(currentQuestionIdx-1);
            quizQuestion.setSelected(quizQuestion.getOption2());
            quizQuestionArrayList.set(currentQuestionIdx-1, quizQuestion);
            fetchNextQuestion(false);
        });
        option4Container.setOnClickListener(v -> {
            QuizQuestion quizQuestion = quizQuestionArrayList.get(currentQuestionIdx-1);
            quizQuestion.setSelected(quizQuestion.getOption2());
            quizQuestionArrayList.set(currentQuestionIdx-1, quizQuestion);
            fetchNextQuestion(false);
        });
        doneButton.setOnClickListener(v -> startActivity(new Intent(mContext, CreatedActivity.class)));
    }

    private ArrayList<QuizQuestion> fetchQuizQuestions() {
        ArrayList<QuizQuestion> quizQuestionArrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            QuizQuestion question = new QuizQuestion();
            question.setQuestion("Question " + i);
            question.setOption1("Option 1" + i);
            question.setOption2("Option 2" + i);
            question.setOption3("Option 3" + i);
            question.setOption4("Option 4" + i);
            question.setAnswer("Option 4" + i);
            quizQuestionArrayList.add(question);
        }
        return quizQuestionArrayList;
    }

    public ArrayList<QuizQuestion> getRandomizedList(ArrayList<QuizQuestion> list) {
        Random rand = new Random();
        ArrayList<QuizQuestion> newList = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            int randomIndex = rand.nextInt(list.size());
            newList.add(list.get(randomIndex));
            list.remove(randomIndex);
        }
        list.clear();
        return newList;
    }

    @SuppressLint("SetTextI18n")
    private void fetchNextQuestion(boolean timedOut) {
        mCountDownTimer.cancel();
        if (currentQuestionIdx < quizQuestionArrayList.size()) {
            if (timedOut)
                lastQuestionCorrect = false;
            else
                points +=getPoints();
            questionTag.setText("Question " + (currentQuestionIdx+1) + " of "+quizQuestionArrayList.size());
            question.setText(quizQuestionArrayList.get(currentQuestionIdx).getQuestion() + i);
            option1Id.setText("A");
            option2Id.setText("B");
            option3Id.setText("C");
            option4Id.setText("D");
            option1Value.setText(quizQuestionArrayList.get(currentQuestionIdx).getOption1());
            option2Value.setText(quizQuestionArrayList.get(currentQuestionIdx).getOption2());
            option3Value.setText(quizQuestionArrayList.get(currentQuestionIdx).getOption3());
            option4Value.setText(quizQuestionArrayList.get(currentQuestionIdx).getOption4());
            YoYo.with(Techniques.Bounce).duration(ANIMATION_DURATION).playOn(questionBox);
            YoYo.with(Techniques.Bounce).duration(ANIMATION_DURATION).playOn(option1Container);
            YoYo.with(Techniques.Bounce).duration(ANIMATION_DURATION).playOn(option2Container);
            YoYo.with(Techniques.Bounce).duration(ANIMATION_DURATION).playOn(option3Container);
            YoYo.with(Techniques.Bounce).duration(ANIMATION_DURATION).playOn(option4Container);
            i = 0;
            mProgressBar.setProgress(i);
            mCountDownTimer.start();
            currentQuestionIdx++;
        } else {

            showEndQuizLayout();
        }
    }

    private int getPoints() {
        int marks;
        if (quizQuestionArrayList.get(currentQuestionIdx).getSelected().equals(quizQuestionArrayList.get(currentQuestionIdx).getAnswer())) {
            //algo to calculate points
            marks = 1000;
            int timePercentageLeft = 100 - i * 100 / (10000 / DURATION_INTERVAL);
            marks += timePercentageLeft * 1000;
            if (lastQuestionCorrect) {
                streakPoints += STREAK_GROWTH_POINTS;
                marks += streakPoints;
            }
            lastQuestionCorrect = true;
        } else {
            lastQuestionCorrect = false;
            streakPoints = STREAK_INITIAL_POINTS;
            return 0;
        }
        return marks;
    }

    private void showEndQuizLayout() {
        quizLayout.setVisibility(View.GONE);
        endLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showStopQuizDialog();
    }


    private void showStopQuizDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle("Are you sure you want to exit the quiz?")
                .setMessage("You won't be able to join it again. \n All answers will be saved as it is and will be evaluated accordingly.")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    ProgressDialog progressdialog = new ProgressDialog(getApplicationContext());
                    progressdialog.setMessage("Saving answers....");
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {

                })
                .show();
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
}