package com.orion.orion.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

public class QuizQuestion implements Comparable<QuizQuestion>, Parcelable {
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String answer;
    private String selected;

    protected QuizQuestion(Parcel in) {
        question = in.readString();
        option1 = in.readString();
        option2 = in.readString();
        option3 = in.readString();
        option4 = in.readString();
        answer = in.readString();
        selected = in.readString();
    }

    public boolean isEmpty(){
        return this.question.isEmpty() && this.option1.isEmpty() && this.option2.isEmpty() && this.option3.isEmpty() && this.option4.isEmpty() && this.answer.isEmpty();
    }
    public static final Creator<QuizQuestion> CREATOR = new Creator<QuizQuestion>() {
        @Override
        public QuizQuestion createFromParcel(Parcel in) {
            return new QuizQuestion(in);
        }

        @Override
        public QuizQuestion[] newArray(int size) {
            return new QuizQuestion[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    public QuizQuestion() {
        question = "";
        option1 = "";
        option2 = "";
        option3 = "";
        option4 = "";
        answer = "";
        selected = "";
    }


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean option1Correct() {
        return option1.equals(answer);
    }

    public boolean option2Correct() {
        return option2.equals(answer);
    }

    public boolean option3Correct() {
        return option3.equals(answer);
    }

    public boolean option4Correct() {
        return option4.equals(answer);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @Override
    public int compareTo(QuizQuestion o) {
        return question.compareTo(o.question);
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeString(option1);
        dest.writeString(option2);
        dest.writeString(option3);
        dest.writeString(option4);
        dest.writeString(answer);
        dest.writeString(selected);
    }
}
