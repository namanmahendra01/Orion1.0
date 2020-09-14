package com.orion.orion.models;

import java.util.List;

public class ParticipantList {

    String userid,timestamp,joiningKey,mediaLink,idLink,contestkey;
    int totalScore;

    public ParticipantList(String userid, String timestamp, String joiningKey, String mediaLink, String idLink, int totalScore,String contestkey) {
        this.userid = userid;
        this.timestamp = timestamp;
        this.joiningKey = joiningKey;
        this.mediaLink = mediaLink;
        this.idLink = idLink;
        this.totalScore=totalScore;
        this.contestkey=contestkey;

    }

    public ParticipantList(){}

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getJoiningKey() {
        return joiningKey;
    }

    public void setJoiningKey(String joiningKey) {
        this.joiningKey = joiningKey;
    }

    public String getMediaLink() {
        return mediaLink;
    }

    public void setMediaLink(String mediaLink) {
        this.mediaLink = mediaLink;
    }

    public String getIdLink() {
        return idLink;
    }

    public void setIdLink(String idLink) {
        this.idLink = idLink;
    }

    public String getContestkey() {
        return contestkey;
    }

    public void setContestkey(String contestkey) {
        this.contestkey = contestkey;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    @Override
    public String toString() {
        return "ParticipantList{" +
                "userid='" + userid + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", joiningKey='" + joiningKey + '\'' +
                ", mediaLink='" + mediaLink + '\'' +
                ", idLink='" + idLink + '\'' +
                ", contestkey='" + contestkey + '\'' +
                ", totalScore=" + totalScore +
                '}';
    }
}
