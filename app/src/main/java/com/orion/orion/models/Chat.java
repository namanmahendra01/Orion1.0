package com.orion.orion.models;

public class Chat implements  Comparable<Chat>{
    String msg, rID, sID, tim, mID;
    boolean ifs;

    public Chat(){

    }



    public String getMsg() {
        return msg;
    }

    public void setMsg(String message) {
        this.msg = message;
    }

    public String getRID() {
        return rID;
    }

    public void setReceiver(String receiver) {
        this.rID = receiver;
    }

    public String getSID() {
        return sID;
    }

    public void setSender(String sender) {
        this.sID = sender;
    }

    public String getTim() {
        return tim;
    }

    public void setTim(String timestamp) {
        this.tim = timestamp;
    }

    public String getMID() {
        return mID;
    }

    public void setMessageid(String messageid) {
        this.mID = messageid;
    }

    public boolean getIfs() {
        return ifs;
    }

    public void setIfseen(boolean ifseen) {
        this.ifs = ifseen;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "msg='" + msg + '\'' +
                ", rID='" + rID + '\'' +
                ", sID='" + sID + '\'' +
                ", tim='" + tim + '\'' +
                ", mID='" + mID + '\'' +
                ", ifs=" + ifs +
                '}';
    }

    @Override
    public int compareTo(Chat o) {
        return this.tim.compareTo(o.tim);
    }
}
