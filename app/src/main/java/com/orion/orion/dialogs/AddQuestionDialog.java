package com.orion.orion.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.orion.orion.R;
import com.orion.orion.models.QuizQuestion;

import java.util.ArrayList;

public class AddQuestionDialog extends DialogFragment {

    private static final int ANIMATION_DURATION = 100;
    private static final String TAG = "Add Question Dialog Box";
    public Context mContext;

    private EditText question;
    private ImageView option1;
    private ImageView option2;
    private ImageView option3;
    private ImageView option4;
    private EditText option1value;
    private EditText option2value;
    private EditText option3value;
    private EditText option4value;
    private Button addButon;
    private int option_selected_num;

    private QuizQuestion quizQuestion;
    private int position;

    public void setPosition(int position) {
        this.position = position;
    }

    public interface OnAddButtonClickListener {
        void onClick(QuizQuestion quizQuestion, boolean isEdit, int idx);
    }

    OnAddButtonClickListener onAddButtonClickListener;


    public void setQuizQuestion(QuizQuestion quizQuestion){
        this.quizQuestion = quizQuestion;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_question, container, false);
        this.mContext = getContext();
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.dialog_add_question);

        question = view.findViewById(R.id.question);
        option1 = view.findViewById(R.id.option1);
        option2 = view.findViewById(R.id.option2);
        option3 = view.findViewById(R.id.option3);
        option4 = view.findViewById(R.id.option4);
        option1value = view.findViewById(R.id.option1Value);
        option2value = view.findViewById(R.id.option2Value);
        option3value = view.findViewById(R.id.option3Value);
        option4value = view.findViewById(R.id.option4Value);
        addButon = view.findViewById(R.id.addButton);

        initializeQuestion(quizQuestion);
        position = -1;

        option1.setOnClickListener(v -> {
            Drawable buttonDrawable = option1.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.GREEN);
            buttonDrawable = option2.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.DKGRAY);
            buttonDrawable = option3.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.DKGRAY);
            buttonDrawable = option4.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.DKGRAY);
            option_selected_num = 1;
        });
        option2.setOnClickListener(v -> {
            Drawable buttonDrawable = option2.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.GREEN);
            buttonDrawable = option1.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.DKGRAY);
            buttonDrawable = option3.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.DKGRAY);
            buttonDrawable = option4.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.DKGRAY);
            option_selected_num = 2;

        });
        option3.setOnClickListener(v -> {

            Drawable buttonDrawable = option3.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.GREEN);

            buttonDrawable = option1.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.DKGRAY);
            buttonDrawable = option2.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.DKGRAY);
            buttonDrawable = option4.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.DKGRAY);
            option_selected_num = 3;

        });
        option4.setOnClickListener(v -> {
            Drawable buttonDrawable = option4.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.GREEN);

            buttonDrawable = option1.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.DKGRAY);
            buttonDrawable = option2.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.DKGRAY);
            buttonDrawable = option3.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.DKGRAY);
            option_selected_num = 4;

        });
        addButon.setOnClickListener(v -> {
            if (question.getText().toString().equals("") || option1value.getText().toString().equals("") || option2value.getText().toString().equals("") || option3value.getText().toString().equals("") || option4value.getText().toString().equals("") || option_selected_num < 1 || option_selected_num > 4) {
                if (question.getText().toString().equals("")) {
                    YoYo.with(Techniques.Bounce).duration(ANIMATION_DURATION).playOn(question);
                    question.requestFocus();
                    question.setError("Empty!");
                } else if (option1value.getText().toString().equals("")) {
                    YoYo.with(Techniques.Bounce).duration(ANIMATION_DURATION).playOn(option1value);
                    option1value.requestFocus();
                    option1value.setError("Empty!");
                } else if (option2value.getText().toString().equals("")) {
                    YoYo.with(Techniques.Bounce).duration(ANIMATION_DURATION).playOn(option2value);
                    option2value.requestFocus();
                    option2value.setError("Empty!");
                } else if (option3value.getText().toString().equals("")) {
                    YoYo.with(Techniques.Bounce).duration(ANIMATION_DURATION).playOn(option3value);
                    option3value.requestFocus();
                    option3value.setError("Empty!");
                } else if (option4value.getText().toString().equals("")) {
                    YoYo.with(Techniques.Bounce).duration(ANIMATION_DURATION).playOn(option4value);
                    option4value.requestFocus();
                    option4value.setError("Empty!");
                } else {
                    YoYo.with(Techniques.Bounce).duration(ANIMATION_DURATION).playOn(option1);
                    YoYo.with(Techniques.Bounce).duration(ANIMATION_DURATION).playOn(option2);
                    YoYo.with(Techniques.Bounce).duration(ANIMATION_DURATION).playOn(option3);
                    YoYo.with(Techniques.Bounce).duration(ANIMATION_DURATION).playOn(option4);
                    Toast.makeText(mContext, "Make a selection", Toast.LENGTH_SHORT).show();
                }
            } else {
                //adding questions to arraylist
                quizQuestion = new QuizQuestion();
                quizQuestion.setQuestion(question.getText().toString());
                quizQuestion.setOption1(option1value.getText().toString().trim());
                quizQuestion.setOption2(option2value.getText().toString().trim());
                quizQuestion.setOption3(option3value.getText().toString().trim());
                quizQuestion.setOption4(option4value.getText().toString().trim());
                if (option_selected_num == 1)
                    quizQuestion.setAnswer(quizQuestion.getOption1());
                else if (option_selected_num == 2)
                    quizQuestion.setAnswer(quizQuestion.getOption2());
                else if (option_selected_num == 3)
                    quizQuestion.setAnswer(quizQuestion.getOption3());
                else
                    quizQuestion.setAnswer(quizQuestion.getOption4());

                if(getTag().equals("Add"))
                    onAddButtonClickListener.onClick(quizQuestion, false , position);
                else
                    onAddButtonClickListener.onClick(quizQuestion, true , position);

                getDialog().dismiss();
            }
        });
        return view;
    }

    private void initializeQuestion(QuizQuestion quizQuestion) {
        if (quizQuestion==null) {
            quizQuestion = new QuizQuestion();
            question.setText("");
            option1value.setText("");
            option2value.setText("");
            option3value.setText("");
            option4value.setText("");
            option_selected_num = 0;
        } else {
            question.setText(quizQuestion.getQuestion());
            option1value.setText(quizQuestion.getOption1());
            option2value.setText(quizQuestion.getOption2());
            option3value.setText(quizQuestion.getOption3());
            option4value.setText(quizQuestion.getOption4());

            Drawable buttonDrawable = option1.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.DKGRAY);
            buttonDrawable = option2.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.DKGRAY);
            buttonDrawable = option3.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.DKGRAY);
            buttonDrawable = option4.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.DKGRAY);

            if (quizQuestion.option1Correct()){
                buttonDrawable = option1.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, Color.GREEN);
                option_selected_num = 1;
            }
            else if (quizQuestion.option2Correct()){
                buttonDrawable = option2.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, Color.GREEN);
                option_selected_num = 2;
            }
            else if (quizQuestion.option3Correct()){
                buttonDrawable = option3.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, Color.GREEN);
                option_selected_num = 3;
            }
            if (quizQuestion.option4Correct()){
                buttonDrawable = option3.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                DrawableCompat.setTint(buttonDrawable, Color.GREEN);
                option_selected_num = 4;
            }
        }
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onAddButtonClickListener = (OnAddButtonClickListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach:ClassCarException:" + e.getMessage());
        }
    }
}
