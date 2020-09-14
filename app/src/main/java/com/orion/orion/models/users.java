package com.orion.orion.models;

import android.os.Parcel;
import android.os.Parcelable;

public class users implements Parcelable {

        private String user_id;


        private String email,domain;
        private String username;
    private String profile_photo;
    private String description;
    private String display_name;



    public users(String user_id, String email,
                 String username, String profile_photo,
                 String description, String display_name,
                 String domain) {
        this.user_id = user_id;
        this.email = email;
        this.username = username;
        this.profile_photo = profile_photo;
        this.domain=domain;
        this.description = description;
        this.display_name = display_name;

    }

    public users(){

}

    protected users(Parcel in) {
        user_id = in.readString();
        email = in.readString();
        domain = in.readString();
        username = in.readString();
        profile_photo = in.readString();
        description = in.readString();
        display_name = in.readString();

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
