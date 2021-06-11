package com.orion.orion.models;

public class ContestDetail {


    private String cty, ef, d, ui, ci, vt, tim, rb, re, vb, ve, wd, mlt;
    Boolean r;

 public ContestDetail(){}


    public String getEf() {
        return ef;
    }

    public void setEf(String entryfee) {
        this.ef = entryfee;
    }

    public String getD() {
        return d;
    }

    public void setD(String domain) {
        this.d = domain;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String userId) {
        this.ui = userId;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String contestId) {
        this.ci = contestId;
    }

    public String getVt() {
        return vt;
    }

    public void setVt(String voteType) {
        this.vt = voteType;
    }

    public String getTim() {
        return tim;
    }

    public void setTim(String timestamp) {
        this.tim = timestamp;
    }

    public String getRb() {
        return rb;
    }

    public void setRb(String regBegin) {
        this.rb = regBegin;
    }

    public String getRe() {
        return re;
    }

    public void setRe(String regEnd) {
        this.re = regEnd;
    }

    public String getVb() {
        return vb;
    }

    public void setVb(String vb) {
        this.vb = vb;
    }

    public String getVe() {
        return ve;
    }

    public void setVe(String ve) {
        this.ve = ve;
    }

    public String getWd() {
        return wd;
    }

    public void setWd(String winDec) {
        this.wd = winDec;
    }
    public Boolean getR() {
        return r;
    }

    public void setR(Boolean result) {
        this.r = result;
    }

    public String getMlt() {
        return mlt;
    }

    public void setMlt(String maxLimit) {
        this.mlt = maxLimit;
    }

    @Override
    public String toString() {
        return "ContestDetail{" +
                "ef='" + ef + '\'' +
                ", d='" + d + '\'' +
                ", ui='" + ui + '\'' +
                ", ci='" + ci + '\'' +
                ", vt='" + vt + '\'' +
                ", tim='" + tim + '\'' +
                ", rb='" + rb + '\'' +
                ", re='" + re + '\'' +
                ", vb='" + vb + '\'' +
                ", ve='" + ve + '\'' +
                ", wd='" + wd + '\'' +
                ", mlt='" + mlt + '\'' +
                ", r=" + r +
                '}';
    }

    public String getCty() {
        return cty;
    }

    public void setCty(String cty) {
        this.cty = cty;
    }
}
