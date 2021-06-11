package com.orion.orion.models;

import androidx.annotation.NonNull;

public class QuizQuestionEncoded implements Comparable<QuizQuestionEncoded> {
    private String qu;
    private String opt;
    private String ans;

    public QuizQuestionEncoded() {
        qu = "";
        opt = "";
        ans = "";
    }
    QuizQuestionEncoded(String question, String options, String answeer){
        this.qu = question;
        this.opt = options;
        this.ans = answeer;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    public String getQu() {
        return qu;
    }

    public void setQu(String qu) {
        this.qu = qu;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    @Override
    public int compareTo(QuizQuestionEncoded o) {
        return qu.compareTo(o.qu);
    }
}
