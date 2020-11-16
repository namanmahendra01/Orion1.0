package com.orion.orion.models;

import java.util.List;

public class JoinForm {
    public JoinForm(){}

    String clg, st, ci, tim, ji, il, ml, ui, hst;
    private List<juryMarks> jurymarks;





    public String getClg() {
        return clg;
    }

    public void setClg(String college) {
        this.clg = college;
    }



    public String getSt() {
        return st;
    }

    public void setSt(String status) {
        this.st = status;
    }


    public String getCi() {
        return ci;
    }

    public void setCi(String contestKey) {
        this.ci = contestKey;
    }
    public String getTim() {
        return tim;
    }

    public void setTim(String timestamp) {
        this.tim = timestamp;
    }
    public String getJi() {
        return ji;
    }

    public void setJi(String joiningKey) {
        this.ji = joiningKey;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String userid) {
        this.ui = userid;
    }
    public String getHst() {
        return hst;
    }

    public void setHst(String hostId) {
        this.hst = hostId;
    }

    @Override
    public String toString() {
        return "JoinForm{" +
                "clg='" + clg + '\'' +
                ", st='" + st + '\'' +
                ", ci='" + ci + '\'' +
                ", tim='" + tim + '\'' +
                ", ji='" + ji + '\'' +
                ", il='" + il + '\'' +
                ", ml='" + ml + '\'' +
                ", ui='" + ui + '\'' +
                ", hst='" + hst + '\'' +
                ", jurymarks=" + jurymarks +
                '}';
    }
}
