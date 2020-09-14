package com.orion.orion.models;

public class Notification {
    String pId,timeStamp,pUid,notificaton,sUid,sName,sEmail,sImage,seen;

    public Notification(String pId, String timeStamp, String pUid, String notificaton, String sUid, String sName, String sEmail, String sImage,String seen) {
        this.pId = pId;
        this.timeStamp = timeStamp;
        this.pUid = pUid;
        this.notificaton = notificaton;
        this.sUid = sUid;
        this.sName = sName;
        this.sEmail = sEmail;
        this.sImage = sImage;
        this.seen=seen;
    }
    public Notification(){

    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getpUid() {
        return pUid;
    }

    public void setpUid(String pUid) {
        this.pUid = pUid;
    }

    public String getNotificaton() {
        return notificaton;
    }

    public void setNotificaton(String notificaton) {
        this.notificaton = notificaton;
    }

    public String getsUid() {
        return sUid;
    }

    public void setsUid(String sUid) {
        this.sUid = sUid;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getsEmail() {
        return sEmail;
    }

    public void setsEmail(String sEmail) {
        this.sEmail = sEmail;
    }

    public String getsImage() {
        return sImage;
    }

    public void setsImage(String sImage) {
        this.sImage = sImage;
    }
    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "pId='" + pId + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", pUid='" + pUid + '\'' +
                ", notificaton='" + notificaton + '\'' +
                ", sUid='" + sUid + '\'' +
                ", sName='" + sName + '\'' +
                ", sEmail='" + sEmail + '\'' +
                ", sImage='" + sImage + '\'' +
                ", seen='" + seen + '\'' +

                '}';
    }
}
