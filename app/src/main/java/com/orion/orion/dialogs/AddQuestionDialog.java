package com.orion.orion.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.orion.orion.R;
import com.orion.orion.models.QuizQuestion;

public class AddQuestionDialog extends Dialog implements android.view.View.OnClickListener {

    public Context mContext;

    private EditText question;
    private RadioGroup optionBox;
    private RadioButton option1;
    private RadioButton option2;
    private RadioButton option3;
    private RadioButton option4;
    private EditText option1value;
    private EditText option2value;
    private EditText option3value;
    private EditText option4value;
    private Button addButon;


    public AddQuestionDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_question);

        question = findViewById(R.id.question);
        optionBox = findViewById(R.id.optionsBox);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        option1value = findViewById(R.id.option1Value);
        option2value = findViewById(R.id.option2Value);
        option3value = findViewById(R.id.option3Value);
        option4value = findViewById(R.id.option4Value);
        addButon = findViewById(R.id.addButton);

        option1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                option2.setChecked(false);
                option3.setChecked(false);
                option4.setChecked(false);
            }
        });
        option2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                option1.setChecked(false);
                option3.setChecked(false);
                option4.setChecked(false);
            }
        });
        option3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                option1.setChecked(false);
                option2.setChecked(false);
                option4.setChecked(false);
            }
        });
        option4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                option1.setChecked(false);
                option2.setChecked(false);
                option3.setChecked(false);
            }
        });
        addButon.setOnClickListener(v -> {

        });
    }

    @Override
    public void onClick(View v) {

    }
}
