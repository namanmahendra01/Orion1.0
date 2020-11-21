package com.orion.orion.models;

public class CreateForm {
    public CreateForm(){

    }

    String ef, ct, des, po, ft, d, vt, rul, rb, re,
            vb, ve, wd, mlt, p1, p2, p3, tp,
            jn1, jn2, jn3, jp1, jp2, jp3, tim, ci,
            hst, of, ui, st;

    public CreateForm(String ef, String ct, String des, String po,
                      String ft, String d, String vt, String rul, String rb,

                      String re, String vb, String ve, String wd, String mlt, String p1,
                      String p2, String p3, String tp, String jn1, String jn2, String jn3,

                      String jp1, String jp2, String jp3, String tim, String ci, String hst, String of, String ui, String st) {
        this.ef = ef;
        this.ct = ct;
        this.des = des;
        this.po = po;
        this.ft = ft;
        this.d = d;
        this.vt = vt;
        this.rul = rul;
        this.rb = rb;
        this.re = re;
        this.vb = vb;
        this.ve = ve;
        this.wd = wd;
        this.mlt = mlt;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.tp = tp;
        this.jn1 = jn1;
        this.jn2 = jn2;
        this.jn3 = jn3;
        this.jp1 = jp1;
        this.jp2 = jp2;
        this.jp3 = jp3;
        this.tim = tim;
        this.ci = ci;
        this.hst = hst;
        this.of = of;
        this.ui = ui;
        this.st = st;
    }

    public String getEf() {
        return ef;
    }

    public void setEf(String entryfee) {
        this.ef = entryfee;
    }

    public String getCt() {
        return ct;
    }

    public void setCt(String title) {
        this.ct = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String descrip) {
        this.des = descrip;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String poster) {
        this.po = poster;
    }

    public String getFt() {
        return ft;
    }

    public void setFt(String filetype) {
        this.ft = filetype;
    }

    public String getD() {
        return d;
    }

    public void setD(String domain) {
        this.d = domain;
    }

    public String getVt() {
        return vt;
    }

    public void setVt(String vt) {
        this.vt = vt;
    }

    public String getRul() {
        return rul;
    }

    public void setRul(String rule) {
        this.rul = rule;
    }

    public String getRb() {
        return rb;
    }

    public void setRb(String rb) {
        this.rb = rb;
    }

    public String getRe() {
        return re;
    }

    public void setRe(String re) {
        this.re = re;
    }

    public String getVb() {
        return vb;
    }

    public void setVb(String voteBegin) {
        this.vb = voteBegin;
    }

    public String getVe() {
        return ve;
    }

    public void setVe(String voteEnd) {
        this.ve = voteEnd;
    }

    public String getWd() {
        return wd;
    }

    public void setWd(String winDeclare) {
        this.wd = winDeclare;
    }



    public String getMlt() {
        return mlt;
    }

    public void setMlt(String maxLimit) {
        this.mlt = maxLimit;
    }


    public String getP1() {
        return p1;
    }

    public void setP1(String place_1) {
        this.p1 = place_1;
    }

    public String getP2() {
        return p2;
    }

    public void setP2(String place_2) {
        this.p2 = place_2;
    }

    public String getP3() {
        return p3;
    }

    public void setP3(String place_3) {
        this.p3 = place_3;
    }

    public String getTp() {
        return tp;
    }

    public void setTp(String total_prize) {
        this.tp = total_prize;
    }


    public String getJn1() {
        return jn1;
    }

    public void setJn1(String jname_1) {
        this.jn1 = jname_1;
    }

    public String getJn2() {
        return jn2;
    }

    public void setJn2(String jname_2) {
        this.jn2 = jname_2;
    }

    public String getJn3() {
        return jn3;
    }

    public void setJn3(String jname_3) {
        this.jn3 = jname_3;
    }




    public String getTim() {
        return tim;
    }

    public void setTim(String timestamp) {
        this.tim = timestamp;
    }


    public String getCi() {
        return ci;
    }

    public void setCi(String contestkey) {
        this.ci = contestkey;
    }


    public String getHst() {
        return hst;
    }

    public void setHst(String host) {
        this.hst = host;
    }

    public String getOf() {
        return of;
    }

    public void setOf(String openFor) {
        this.of = openFor;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String userid) {
        this.ui = userid;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String status) {
        this.st = status;
    }

    @Override
    public String toString() {
        return "CreateForm{" +
                "ef='" + ef + '\'' +
                ", ct='" + ct + '\'' +
                ", des='" + des + '\'' +
                ", po='" + po + '\'' +
                ", ft='" + ft + '\'' +
                ", d='" + d + '\'' +
                ", vt='" + vt + '\'' +
                ", rul='" + rul + '\'' +
                ", rb='" + rb + '\'' +
                ", re='" + re + '\'' +
                ", vb='" + vb + '\'' +
                ", ve='" + ve + '\'' +
                ", wd='" + wd + '\'' +
                ", mlt='" + mlt + '\'' +
                ", p1='" + p1 + '\'' +
                ", p2='" + p2 + '\'' +
                ", p3='" + p3 + '\'' +
                ", tp='" + tp + '\'' +
                ", jn1='" + jn1 + '\'' +
                ", jn2='" + jn2 + '\'' +
                ", jn3='" + jn3 + '\'' +
                ", jp1='" + jp1 + '\'' +
                ", jp2='" + jp2 + '\'' +
                ", jp3='" + jp3 + '\'' +
                ", tim='" + tim + '\'' +
                ", ci='" + ci + '\'' +
                ", hst='" + hst + '\'' +
                ", of='" + of + '\'' +
                ", ui='" + ui + '\'' +
                ", st='" + st + '\'' +
                '}';
    }
}
