package com.orion.orion.models;

public class Chat implements  Comparable<Chat>{
    String message,receiver,sender,timestamp,messageid;
    boolean ifseen;

    public Chat(){

    }

    public Chat(String message, String receiver, String sender, String timestamp, String messageid, boolean ifseen) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timestamp = timestamp;
        this.messageid = messageid;
        this.ifseen = ifseen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public boolean isIfseen() {
        return ifseen;
    }

    public void setIfseen(boolean ifseen) {
        this.ifseen = ifseen;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "message='" + message + '\'' +
                ", receiver='" + receiver + '\'' +
                ", sender='" + sender + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", messageid='" + messageid + '\'' +
                ", ifseen=" + ifseen +
                '}';
    }

    @Override
    public int compareTo(Chat o) {
        return this.timestamp.compareTo(o.timestamp);
    }
}
