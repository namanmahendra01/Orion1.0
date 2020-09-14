package com.orion.orion.models;

public class juryMarks {
    String comment1,jury1,comment2,jury2,comment3,jury3,jusername1,jusername2,jusername3;
    public juryMarks(){

    }

    public juryMarks(String comment1, String jury1, String comment2, String jury2, String comment3, String jury3, String jusername1, String jusername2, String jusername3) {
        this.comment1 = comment1;
        this.jury1 = jury1;
        this.comment2 = comment2;
        this.jury2 = jury2;
        this.comment3 = comment3;
        this.jury3 = jury3;
        this.jusername1 = jusername1;
        this.jusername2 = jusername2;
        this.jusername3 = jusername3;
    }

    public String getComment1() {
        return comment1;
    }

    public void setComment1(String comment1) {
        this.comment1 = comment1;
    }

    public String getJury1() {
        return jury1;
    }

    public void setJury1(String jury1) {
        this.jury1 = jury1;
    }

    public String getComment2() {
        return comment2;
    }

    public void setComment2(String comment2) {
        this.comment2 = comment2;
    }

    public String getJury2() {
        return jury2;
    }

    public void setJury2(String jury2) {
        this.jury2 = jury2;
    }

    public String getComment3() {
        return comment3;
    }

    public void setComment3(String comment3) {
        this.comment3 = comment3;
    }

    public String getJury3() {
        return jury3;
    }

    public void setJury3(String jury3) {
        this.jury3 = jury3;
    }

    public String getJusername1() {
        return jusername1;
    }

    public void setJusername1(String jusername1) {
        this.jusername1 = jusername1;
    }

    public String getJusername2() {
        return jusername2;
    }

    public void setJusername2(String jusername2) {
        this.jusername2 = jusername2;
    }

    public String getJusername3() {
        return jusername3;
    }

    public void setJusername3(String jusername3) {
        this.jusername3 = jusername3;
    }

    @Override
    public String toString() {
        return "juryMarks{" +
                "comment1='" + comment1 + '\'' +
                ", jury1='" + jury1 + '\'' +
                ", comment2='" + comment2 + '\'' +
                ", jury2='" + jury2 + '\'' +
                ", comment3='" + comment3 + '\'' +
                ", jury3='" + jury3 + '\'' +
                ", jusername1='" + jusername1 + '\'' +
                ", jusername2='" + jusername2 + '\'' +
                ", jusername3='" + jusername3 + '\'' +
                '}';
    }
}
