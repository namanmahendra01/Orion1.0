package com.orion.orion.models;

public class Promote {
    public Promote(){}

    String tis, tie, pi, ui, ip, stid, pid;



    public String getTis() {
        return tis;
    }

    public void setTis(String timeStart) {
        this.tis = timeStart;
    }

    public String getTie() {
        return tie;
    }

    public void setTie(String timeEnd) {
        this.tie = timeEnd;
    }

    public String getPi() {
        return pi;
    }

    public void setPi(String photoid) {
        this.pi = photoid;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String userid) {
        this.ui = userid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String photoLink) {
        this.ip = photoLink;
    }

    public String getStid() {
        return stid;
    }

    public void setStid(String storyid) {
        this.stid = storyid;
    }

    public String getPID() {
        return pid;
    }

    public void setPID(String promoterId) {
        this.pid = promoterId;
    }

    @Override
    public String toString() {
        return "Promote{" +
                "tiS='" + tis + '\'' +
                ", tiE='" + tie + '\'' +
                ", pi='" + pi + '\'' +
                ", ui='" + ui + '\'' +
                ", ip='" + ip + '\'' +
                ", stID='" + stid + '\'' +
                ", pID='" + pid + '\'' +
                '}';
    }
}
