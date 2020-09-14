package com.orion.orion.models;

public class Promote {
    public Promote(){}

    String timeStart,timeEnd,photoid,userid,photoLink,storyid,promoterId;

    public Promote(String timeStart, String timeEnd, String photoid, String userid, String photoLink, String storyid, String promoterId) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.photoid = photoid;
        this.userid = userid;
        this.photoLink = photoLink;
        this.storyid = storyid;
        this.promoterId = promoterId;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getPhotoid() {
        return photoid;
    }

    public void setPhotoid(String photoid) {
        this.photoid = photoid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }

    public String getPromoterId() {
        return promoterId;
    }

    public void setPromoterId(String promoterId) {
        this.promoterId = promoterId;
    }

    @Override
    public String toString() {
        return "Promote{" +
                "timeStart='" + timeStart + '\'' +
                ", timeEnd='" + timeEnd + '\'' +
                ", photoid='" + photoid + '\'' +
                ", userid='" + userid + '\'' +
                ", photoLink='" + photoLink + '\'' +
                ", storyid='" + storyid + '\'' +
                ", promoterId='" + promoterId + '\'' +
                '}';
    }
}
