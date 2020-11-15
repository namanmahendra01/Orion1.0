package com.orion.orion.models;

public class Leaderboard {

    private Rating at;
    private Rating y;
    private Rating lm;
    private Rating tm;
    private Rating lw;
    private Rating tw;
    private location lkl;
    private String lu;
    private int f;
    private int jc;
    private int cc;
    private String u;
    private String d;
    private String pp;

    public Rating getAt() {
        return at;
    }

    public void setAt(Rating all_time) {
        this.at = all_time;
    }

    public Rating getY() {
        return y;
    }

    public void setY(Rating yearly) {
        this.y = yearly;
    }

    public Rating getLm() {
        return lm;
    }

    public void setLm(Rating last_month) {
        this.lm = last_month;
    }

    public Rating getTm() {
        return tm;
    }

    public void setTm(Rating this_month) {
        this.tm = this_month;
    }

    public Rating getLw() {
        return lw;
    }

    public void setLw(Rating last_week) {
        this.lw = last_week;
    }

    public Rating getTw() {
        return tw;
    }

    public void setTw(Rating this_week) {
        this.tw = this_week;
    }

    public location getLkl() {
        return lkl;
    }

    public void setLkl(location last_known_location) {
        this.lkl = last_known_location;
    }

    public String getLu() {
        return lu;
    }

    public void setLu(String last_updated) {
        this.lu = last_updated;
    }

    public int getF() {
        return f;
    }

    public void setF(int followers) {
        this.f = followers;
    }

    public int getJc() {
        return jc;
    }

    public void setJc(int joined_contest) {
        this.jc = joined_contest;
    }

    public int getCc() {
        return cc;
    }

    public void setCc(int created_contest) {
        this.cc = created_contest;
    }

    public String getU() {
        return u;
    }

    public void setU(String username) {
        this.u = username;
    }

    public String getD() {
        return d;
    }

    public void setD(String domain) {
        this.d = domain;
    }

    public Leaderboard(Rating at, Rating y, Rating lm, Rating tm, Rating lw, Rating tw, location lkl, String lu, int f, int jc, int cc, String u, String d, String pp) {
        this.at = at;
        this.y = y;
        this.lm = lm;
        this.tm = tm;
        this.lw = lw;
        this.tw = tw;
        this.lkl = lkl;
        this.lu = lu;
        this.f = f;
        this.jc = jc;
        this.cc = cc;
        this.u = u;
        this.d = d;
        this.pp = pp;
    }

    public Leaderboard() {
        this.lu = "";
        this.f = 0;
        this.jc = 0;
        this.cc = 0;
        this.lkl = new location();
        this.at = new Rating();
        this.y = new Rating();
        this.lm = new Rating();
        this.tm = new Rating();
        this.lw = new Rating();
        this.tw = new Rating();
        this.u = "";
        this.d = "";
        this.pp ="";
    }

    public String getPp() {
        return pp;
    }

    public void setPp(String profile_photo) {
        this.pp = profile_photo;
    }
}
