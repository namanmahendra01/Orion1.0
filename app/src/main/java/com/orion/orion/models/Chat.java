package com.orion.orion.models;

public class Chat implements  Comparable<Chat>{
    String msg, rid, sid, tim, mid;
    boolean ifs;

    public Chat(){

    }



    public String getMsg() {
        return msg;
    }

    public void setMsg(String message) {
        this.msg = message;
    }

    public String getRid() {
        return rid;
    }

    public void setReceiver(String receiver) {
        this.rid = receiver;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sender) {
        this.sid = sender;
    }

    public String getTim() {
        return tim;
    }

    public void setTim(String timestamp) {
        this.tim = timestamp;
    }

    public String getMid() {
        return mid;
    }

    public void setMessageid(String messageid) {
        this.mid = messageid;
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
                ", rID='" + rid + '\'' +
                ", sID='" + sid + '\'' +
                ", tim='" + tim + '\'' +
                ", mID='" + mid + '\'' +
                ", ifs=" + ifs +
                '}';
    }

    @Override
    public int compareTo(Chat o) {
        return this.tim.compareTo(o.tim);
    }
}
