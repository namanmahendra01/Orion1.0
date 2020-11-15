package com.orion.orion.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {
    private String c;
    private String ui;
    private String dc;

    public Comment(String c, String ui, String dc) {
        this.c = c;
        this.ui = ui;
        this.dc = dc;
    }

    public Comment() {

    }

    protected Comment(Parcel in) {
        c = in.readString();
        ui = in.readString();
        dc = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(c);
        dest.writeString(ui);
        dest.writeString(dc);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public String getC() {
        return c;
    }

    public void setC(String comment) {
        this.c = comment;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String user_id) {
        this.ui = user_id;
    }



    public String getDc() {
        return dc;
    }

    public void setDc(String date_created) {
        this.dc = date_created;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "c='" + c + '\'' +
                ", ui='" + ui + '\'' +
                ", dc='" + dc + '\'' +
                '}';
    }
}

