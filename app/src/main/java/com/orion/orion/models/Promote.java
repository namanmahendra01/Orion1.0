package com.orion.orion.models;

public class Promote {
    public Promote(){}

    String tiS, tiE, pi, ui, ip, stID, pID;



    public String getTiS() {
        return tiS;
    }

    public void setTimeStart(String timeStart) {
        this.tiS = timeStart;
    }

    public String getTiE() {
        return tiE;
    }

    public void setTimeEnd(String timeEnd) {
        this.tiE = timeEnd;
    }

    public String getPi() {
        return pi;
    }

    public void setPhotoid(String photoid) {
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

    public void setPhotoLink(String photoLink) {
        this.ip = photoLink;
    }

    public String getStID() {
        return stID;
    }

    public void setStoryid(String storyid) {
        this.stID = storyid;
    }

    public String getPID() {
        return pID;
    }

    public void setPromoterId(String promoterId) {
        this.pID = promoterId;
    }

    @Override
    public String toString() {
        return "Promote{" +
                "tiS='" + tiS + '\'' +
                ", tiE='" + tiE + '\'' +
                ", pi='" + pi + '\'' +
                ", ui='" + ui + '\'' +
                ", ip='" + ip + '\'' +
                ", stID='" + stID + '\'' +
                ", pID='" + pID + '\'' +
                '}';
    }
}
