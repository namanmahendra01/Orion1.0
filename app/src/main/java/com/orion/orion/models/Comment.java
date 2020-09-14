package com.orion.orion.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable {
    private String comment;
    private String user_id;
    private String date_created;

    public Comment(String comment, String user_id, String date_created) {
        this.comment = comment;
        this.user_id = user_id;
        this.date_created = date_created;
    }
    public Comment() {

    }

    protected Comment(Parcel in) {
        comment = in.readString();
        user_id = in.readString();
        date_created = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(comment);
        dest.writeString(user_id);
        dest.writeString(date_created);
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }



    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment='" + comment + '\'' +
                ", user_id='" + user_id + '\'' +
                ", date_created='" + date_created + '\'' +
                '}';
    }
}

