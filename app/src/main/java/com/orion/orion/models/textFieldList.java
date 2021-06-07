package com.orion.orion.models;

public class textFieldList {
    String et1,et2,et3,et4,et5,et6,et7,et8,et9,et10,total,feedback;
    public textFieldList(){

    }

    public String getEt1() {
        return et1;
    }

    public void setEt1(String et1) {
        this.et1 = et1;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getEt3() {
        return et3;
    }

    public void setEt3(String et3) {
        this.et3 = et3;
    }

    public String getEt4() {
        return et4;
    }

    public void setEt4(String et4) {
        this.et4 = et4;
    }

    public String getEt5() {
        return et5;
    }

    public void setEt5(String et5) {
        this.et5 = et5;
    }

    public String getEt6() {
        return et6;
    }

    public void setEt6(String et6) {
        this.et6 = et6;
    }

    public String getEt7() {
        return et7;
    }

    public void setEt7(String et7) {
        this.et7 = et7;
    }

    public String getEt8() {
        return et8;
    }

    public void setEt8(String et8) {
        this.et8 = et8;
    }

    public String getEt9() {
        return et9;
    }

    public void setEt9(String et9) {
        this.et9 = et9;
    }

    public String getEt10() {
        return et10;
    }

    public void setEt10(String et10) {
        this.et10 = et10;
    }

    public String getEt2() {
        return et2;
    }

    public void setEt2(String et2) {
        this.et2 = et2;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public textFieldList(String et1, String et2, String feedback,String et3, String et4, String et5, String et6, String et7, String et8, String et9, String et10, String total) {
        this.et1 = et1;
        this.et2 = et2;
        this.et3 = et3;
        this.et4 = et4;
        this.et5 = et5;
        this.et6 = et6;
        this.et7 = et7;
        this.et8 = et8;
        this.et9 = et9;
        this.et10 = et10;
        this.total = total;
        this.feedback = feedback;

    }


    @Override
    public String toString() {
        return "judgeMarks{" +
                "et1='" + et1 + '\'' +
                ", et2='" + et2 + '\'' +
                ", et3='" + et3 + '\'' +
                ", et4='" + et4 + '\'' +
                ", et5='" + et5 + '\'' +
                ", et6='" + et6 + '\'' +
                ", et7='" + et7 + '\'' +
                ", et8='" + et8 + '\'' +
                ", et9='" + et9 + '\'' +
                ", et10='" + et10 + '\'' +
                ", total='" + total + '\'' +
                "feedback='" + feedback + '\'' +

                '}';
    }
}
