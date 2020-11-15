package com.orion.orion.models;

public class Notification {
    String pId, tim,pUid, not,sUid, ifs;


    public Notification(){

    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getTim() {
        return tim;
    }

    public void setTim(String timeStamp) {
        this.tim = timeStamp;
    }

    public String getpUid() {
        return pUid;
    }

    public void setpUid(String pUid) {
        this.pUid = pUid;
    }

    public String getNot() {
        return not;
    }

    public void setNotificaton(String notificaton) {
        this.not = notificaton;
    }

    public String getsUid() {
        return sUid;
    }


    @Override
    public String toString() {
        return "Notification{" +
                "pId='" + pId + '\'' +
                ", tim='" + tim + '\'' +
                ", pUid='" + pUid + '\'' +
                ", not='" + not + '\'' +
                ", sUid='" + sUid + '\'' +
                ", ifs='" + ifs + '\'' +
                '}';
    }
}
