package com.orion.orion;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orion.orion.login.LoginActivity;
import com.orion.orion.profile.Account.About;

public class QuizActivity extends AppCompatActivity {
    private Context mContext;
    private static final String TAG = "QuizActivity";
    private static final int DURATION_INTERVAL = 100;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;


    private TextView topBarTitle;
    private ImageView backArrow;

    private ProgressBar mProgressBar;

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

    private CountDownTimer mCountDownTimer;
    int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_quiz);
        mContext = QuizActivity.this;

        topBarTitle = findViewById(R.id.titleTopBar);
        backArrow = findViewById(R.id.backarrow);

        mProgressBar = findViewById(R.id.progressbar);
        topBarTitle.setText("Quiz");

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


        question.setText("Question 1");
        option1Id.setText("A");
        option2Id.setText("B");
        option3Id.setText("C");
        option4Id.setText("D");
        option1Value.setText("Option 1");
        option2Value.setText("Option 2");
        option3Value.setText("Option 3");
        option4Value.setText("Option 4");


        option1Container.setOnClickListener(v -> fetchNextQuestion());
        option2Container.setOnClickListener(v -> fetchNextQuestion());
        option3Container.setOnClickListener(v -> fetchNextQuestion());
        option4Container.setOnClickListener(v -> fetchNextQuestion());

        mProgressBar.setProgress(0);
        mCountDownTimer = new CountDownTimer(10000, DURATION_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                i++;
                mProgressBar.setProgress(i * 100 / (10000 / DURATION_INTERVAL));
            }

            @Override
            public void onFinish() {
                i++;
                mProgressBar.setProgress(100);
                fetchNextQuestion();
            }
        };
        mCountDownTimer.start();

        backArrow.setOnClickListener(v -> showStopQuizDialog());


        setupFirebaseAuth();

    }

    private void fetchNextQuestion() {
        if (nextQuestionAvailable()) {
            mCountDownTimer.cancel();
            question.setText("Question 2");
            option1Id.setText("A");
            option2Id.setText("B");
            option3Id.setText("C");
            option4Id.setText("D");
            option1Value.setText("Option 21");
            option2Value.setText("Option 12");
            option3Value.setText("Option 13");
            option4Value.setText("Option 14");
            mProgressBar.setProgress(0);
            mCountDownTimer.start();
        } else {
            mCountDownTimer.cancel();
        }
    }

    private boolean nextQuestionAvailable() {
        return true;

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