package com.orion.orion.models;

public class Rating {

    private int p;
    private int f;
    private int c;

    public Rating() {
        this.p = 0;
        this.f = 0;
        this.c = 0;
    }



    public int getP() {
        return p;
    }

    public void setP(int post) {
        this.p = post;
    }

    public int getF() {
        return f;
    }

    public void setF(int followers) {
        this.f = followers;
    }

    public int getC() {
        return c;
    }

    public void setC(int contest) {
        this.c = contest;
    }
}
