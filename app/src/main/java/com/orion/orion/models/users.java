package com.orion.orion.models;

import android.os.Parcel;
import android.os.Parcelable;

public class users implements Parcelable {

    private String ui;
    private String e, d;
    private String u;
    private String pp;
    private String des;
    private String dn;
    private String cf;
    private String ccc;
    private String cjc;
    private String in;
    private String fb;
    private String tw;
    private String wa;
    private String l1;
    private String l2;
    private String l3;

    public String getl1() {
        return l1;
    }

    public String getl2() {
        return l2;
    }

    public String getl3() {
        return l3;
    }

    public users(String ui, String e, String d, String u,
                 String pp, String des, String dn, String cf, String ccc,
                 String cjc, String in, String fb, String tw, String wa,
                 String l1, String l2, String l3,String l4) {
        this.ui = ui;
        this.e = e;
        this.d = d;
        this.u = u;
        this.pp = pp;
        this.des = des;
        this.dn = dn;
        this.cf = cf;
        this.ccc = ccc;
        this.cjc = cjc;
        this.in = in;
        this.fb = fb;
        this.tw = tw;
        this.wa = wa;
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
    }





    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getFb() {
        return fb;
    }

    public void setFb(String facebook) {
        this.fb = facebook;
    }

    public String getTw() {
        return tw;
    }

    public void setTw(String twitter) {
        this.tw = twitter;
    }

    public String getWa() {
        return wa;
    }

    public void setWa(String whatsapp) {
        this.wa = whatsapp;
    }

    public String getCf() {
        return cf;
    }

    public void setChangedFollowers(String changedFollowers) {
        this.cf = changedFollowers;
    }

    public String getCcc() {
        return ccc;
    }

    public void setChangedCreateContest(String changedCreateContest) {
        this.ccc = changedCreateContest;
    }

    public String getCjc() {
        return cjc;
    }

    public void setChangedJoinedContest(String changedJoinedContest) {
        this.cjc = changedJoinedContest;
    }

    public users() {
        this.cjc = "false";
        this.ccc = "false";
        this.cf = "false";
    }

    protected users(Parcel in) {
        ui = in.readString();
        e = in.readString();
        d = in.readString();
        u = in.readString();
        pp = in.readString();
        des = in.readString();
        dn = in.readString();
        cf = in.readString();
        ccc = in.readString();
        cjc = in.readString();
    }

    public static final Creator<users> CREATOR = new Creator<users>() {
        @Override
        public users createFromParcel(Parcel in) {
            return new users(in);
        }

        @Override
        public users[] newArray(int size) {
            return new users[size];
        }
    };

    public String getUi() {
        return ui;
    }

    public void setUi(String user_id) {
        this.ui = user_id;
    }


    public String getE() {
        return e;
    }

    public void setE(String email) {
        this.e = email;
    }

    public String getD() {
        return d;
    }

    public void setD(String domain) {
        this.d = domain;
    }

    public String getU() {
        return u;
    }

    public void setU(String username) {
        this.u = username;
    }


    public String getPp() {
        return pp;
    }

    public void setPp(String profile_photo) {
        this.pp = profile_photo;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String description) {
        this.des = description;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String display_name) {
        this.dn = display_name;
    }


    @Override
    public String toString() {
        return "users{" +
                "ui='" + ui + '\'' +
                ", e='" + e + '\'' +
                ", d='" + d + '\'' +
                ", u='" + u + '\'' +
                ", pp='" + pp + '\'' +
                ", des='" + des + '\'' +
                ", dn='" + dn + '\'' +
                ", cf='" + cf + '\'' +
                ", ccc='" + ccc + '\'' +
                ", cjc='" + cjc + '\'' +
                ", in='" + in + '\'' +
                ", fb='" + fb + '\'' +
                ", tw='" + tw + '\'' +
                ", wa='" + wa + '\'' +
                ", l1='" + l1 + '\'' +
                ", l2='" + l2 + '\'' +
                ", l3='" + l3 + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ui);
        dest.writeString(e);
        dest.writeString(d);
        dest.writeString(u);
        dest.writeString(pp);
        dest.writeString(des);
        dest.writeString(dn);

    }
}
