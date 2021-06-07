package com.orion.orion.models;

public class juryMarks {
    String c1, j1, c2, j2, c3, j3, jn1, jn2, jn3;
    public juryMarks(){

    }


    public String getC1() {
        return c1;
    }


    public String getJ1() {
        return j1;
    }


    public String getC2() {
        return c2;
    }


    public String getJ2() {
        return j2;
    }


    public String getC3() {
        return c3;
    }


    public String getJ3() {
        return j3;
    }



    @Override
    public String toString() {
        return "juryMarks{" +
                "c1='" + c1 + '\'' +
                ", j1='" + j1 + '\'' +
                ", c2='" + c2 + '\'' +
                ", j2='" + j2 + '\'' +
                ", c3='" + c3 + '\'' +
                ", j3='" + j3 + '\'' +
                ", jn1='" + jn1 + '\'' +
                ", jn2='" + jn2 + '\'' +
                ", jn3='" + jn3 + '\'' +

                '}';
    }
}
