package com.orion.orion.models;

import android.os.Parcel;
import android.os.Parcelable;

public class users implements Parcelable {

    private String user_id;
    private String email, domain;
    private String username;
    private String profile_photo;
    private String description;
    private String display_name;
    private String changedFollowers;
    private String changedCreateContest;
    private String changedJoinedContest;
    private String instagram;
    private String facebook;
    private String twitter;
    private String whatsapp;

    public users(String user_id, String email, String domain, String username, String profile_photo, String description, String display_name, String changedFollowers, String changedCreateContest, String changedJoinedContest) {
        this.user_id = user_id;
        this.email = email;
        this.domain = domain;
        this.username = username;
        this.profile_photo = profile_photo;
        this.description = description;
        this.display_name = display_name;
        this.changedFollowers = changedFollowers;
        this.changedCreateContest = changedCreateContest;
        this.changedJoinedContest = changedJoinedContest;
        this.instagram = "";
        this.facebook = "";
        this.twitter = "";
        this.whatsapp = "";
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getChangedFollowers() {
        return changedFollowers;
    }

    public void setChangedFollowers(String changedFollowers) {
        this.changedFollowers = changedFollowers;
    }

    public String getChangedCreateContest() {
        return changedCreateContest;
    }

    public void setChangedCreateContest(String changedCreateContest) {
        this.changedCreateContest = changedCreateContest;
    }

    public String getChangedJoinedContest() {
        return changedJoinedContest;
    }

    public void setChangedJoinedContest(String changedJoinedContest) {
        this.changedJoinedContest = changedJoinedContest;
    }

    public users() {
        this.changedJoinedContest="false";
        this.changedCreateContest = "false";
        this.changedFollowers = "false";
    }

    protected users(Parcel in) {
        user_id = in.readString();
        email = in.readString();
        domain = in.readString();
        username = in.readString();
        profile_photo = in.readString();
        description = in.readString();
        display_name = in.readString();
        changedFollowers =in.readString();
        changedCreateContest =in.readString();
        changedJoinedContest=in.readString();
    }

    public static final Creator<users> CREATOR = new Creator<users>() {
        @Override
        public users createFromParcel(Parcel in) {
            return new users(in);
        }

        @Override
        public users[] newArray(int size) {
            return new users[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }


    @Override
    public String toString() {
        return "users{" +
                "user_id='" + user_id + '\'' +
                ", email='" + email + '\'' +
                ", domain='" + domain + '\'' +
                ", username='" + username + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", description='" + description + '\'' +
                ", display_name='" + display_name + '\'' +

                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(email);
        dest.writeString(domain);
        dest.writeString(username);
        dest.writeString(profile_photo);
        dest.writeString(description);
        dest.writeString(display_name);

    }
}
