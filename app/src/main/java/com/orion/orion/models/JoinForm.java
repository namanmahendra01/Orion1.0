package com.orion.orion.models;

import java.util.List;

public class JoinForm {
    public JoinForm(){}

    String name,username,college,comment,status,payment,contestKey,timestamp,joiningKey,idLink,mediaLink,userid,hostId;
    private List<juryMarks> jurymarks;

    public JoinForm(String name, String username, String college, String comment, String status,
                    String payment,String contestKey,String timestamp,String joiningKey,String mediaLink,String userid,String idLink,String hostId) {
        this.name = name;
        this.username = username;
        this.college = college;
        this.comment = comment;
        this.status = status;
        this.payment = payment;
        this.contestKey=contestKey;
        this.timestamp=timestamp;
        this.joiningKey=joiningKey;
        this.mediaLink=mediaLink;
        this.userid=userid;
        this.idLink=idLink;
        this.hostId=hostId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }
    public String getContestKey() {
        return contestKey;
    }

    public void setContestKey(String contestKey) {
        this.contestKey = contestKey;
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
    public String getIdLink() {
        return idLink;
    }

    public void setIdLink(String idLink) {
        this.idLink = idLink;
    }
    public String getMediaLink() {
        return mediaLink;
    }

    public void setMediaLink(String mediaLink) {
        this.mediaLink = mediaLink;
    }
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }
    @Override
    public String toString() {
        return "JoinForm{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", college='" + college + '\'' +
                ", comment='" + comment + '\'' +
                ", status='" + status + '\'' +
                ", payment='" + payment + '\'' +
                ", contestKey='" + contestKey + '\'' +
                ", joiningKey='" + joiningKey + '\'' +
                ", idLink='" + idLink + '\'' +

                ", mediaLink='" + mediaLink + '\'' +

                ", userid='" + userid + '\'' +
                ", hostId='" + hostId + '\'' +




                '}';
    }
}
