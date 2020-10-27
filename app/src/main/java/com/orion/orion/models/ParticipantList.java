package com.orion.orion.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ParticipantList  implements Parcelable {

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

    protected ParticipantList(Parcel in) {
        userid = in.readString();
        timestamp = in.readString();
        joiningKey = in.readString();
        mediaLink = in.readString();
        idLink = in.readString();
        contestkey = in.readString();
        totalScore = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userid);
        dest.writeString(timestamp);
        dest.writeString(joiningKey);
        dest.writeString(mediaLink);
        dest.writeString(idLink);
        dest.writeString(contestkey);
        dest.writeInt(totalScore);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParticipantList> CREATOR = new Creator<ParticipantList>() {
        @Override
        public ParticipantList createFromParcel(Parcel in) {
            return new ParticipantList(in);
        }

        @Override
        public ParticipantList[] newArray(int size) {
            return new ParticipantList[size];
        }
    };

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
