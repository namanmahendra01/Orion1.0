package com.orion.orion.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import androidx.annotation.Nullable;

public class Photo implements Parcelable {
    private String cap;
    private String dc;
    private String ip;
    private String pi;
    private String ty;
    private String ui;
    private String tg;
    private String t;
    private List<Comment> comment;


    public Photo(String cap, String dc, String ip, String pi, String ty, String ui, String tg, String t, List<Comment> comment) {
        this.cap = cap;
        this.dc = dc;
        this.ip = ip;
        this.pi = pi;
        this.ty = ty;
        this.ui = ui;
        this.tg = tg;
        this.t = t;
        this.comment = comment;
    }

    public Photo() {
    }

    protected Photo(Parcel in) {
        cap = in.readString();
        dc = in.readString();
        ip = in.readString();
        pi = in.readString();
        ui = in.readString();
        tg = in.readString();
        ty = in.readString();
        t = in.readString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Photo) {
            Photo temp = (Photo) obj;
            return this.pi.equals(temp.pi);
        }
        return false;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getCap() {
        return cap;
    }

    public void setCap(String caption) {
        this.cap = caption;
    }

    public String getDc() {
        return dc;
    }

    public void setDc(String date_created) {
        this.dc = date_created;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String image_path) {
        this.ip = image_path;
    }

    public String getPi() {
        return pi;
    }

    public void setPi(String photo_id) {
        this.pi = photo_id;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String user_id) {
        this.ui = user_id;
    }


    public String getTg() {
        return tg;
    }

    public void setTg(String tags) {
        tg = tags;
    }


    public String getTy() {
        return ty;
    }

    public void setTy(String type) {
        this.ty = type;
    }


    public String getT() {
        return t;
    }

    public void setT(String thumbnail) {
        this.t = thumbnail;
    }

    public List<Comment> getComments() {
        return comment;
    }

    public void setComments(List<Comment> comments) {
        this.comment = comments;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "cap='" + cap + '\'' +
                ", dc='" + dc + '\'' +
                ", ip='" + ip + '\'' +
                ", pi='" + pi + '\'' +
                ", ty='" + ty + '\'' +
                ", ui='" + ui + '\'' +
                ", tg='" + tg + '\'' +
                ", t='" + t + '\'' +
                ", comment=" + comment +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cap);
        dest.writeString(dc);
        dest.writeString(ip);
        dest.writeString(pi);
        dest.writeString(ui);
        dest.writeString(tg);
        dest.writeString(ty);
        dest.writeString(t);
    }
}
