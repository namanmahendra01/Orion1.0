package com.orion.orion.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ParticipantList  implements Parcelable {

    String ui, tim, ji, ml, il, ci;
    int ts;

    public ParticipantList(String ui, String tim, String ji, String ml, String il, String ci, int ts) {
        this.ui = ui;
        this.tim = tim;
        this.ji = ji;
        this.ml = ml;
        this.il = il;
        this.ci = ci;
        this.ts = ts;
    }

    public ParticipantList(){}

    protected ParticipantList(Parcel in) {
        ui = in.readString();
        tim = in.readString();
        ji = in.readString();
        ml = in.readString();
        il = in.readString();
        ci = in.readString();
        ts = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ui);
        dest.writeString(tim);
        dest.writeString(ji);
        dest.writeString(ml);
        dest.writeString(il);
        dest.writeString(ci);
        dest.writeInt(ts);
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

    public String getUi() {
        return ui;
    }

    public void setUi(String userid) {
        this.ui = userid;
    }

    public String getTim() {
        return tim;
    }

    public void setTim(String timestamp) {
        this.tim = timestamp;
    }

    public String getJi() {
        return ji;
    }

    public void setJi(String joiningKey) {
        this.ji = joiningKey;
    }

    public String getMl() {
        return ml;
    }

    public void setMediaLink(String mediaLink) {
        this.ml = mediaLink;
    }

    public String getIl() {
        return il;
    }

    public void setIdLink(String idLink) {
        this.il = idLink;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String contestkey) {
        this.ci = contestkey;
    }

    public int getTs() {
        return ts;
    }

    public void setTotalScore(int totalScore) {
        this.ts = totalScore;
    }

    @Override
    public String toString() {
        return "ParticipantList{" +
                "ui='" + ui + '\'' +
                ", tim='" + tim + '\'' +
                ", ji='" + ji + '\'' +
                ", ml='" + ml + '\'' +
                ", il='" + il + '\'' +
                ", ci='" + ci + '\'' +
                ", ts=" + ts +
                '}';
    }
}
