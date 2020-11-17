package com.orion.orion.models;

public class ItemFollow {
    private String userId;
    private String profileUrl;
    private String username;
    private String display_name;
    private boolean isFollowing;
    private boolean isFan;

    public ItemFollow(String userId, String profileUrl, String username, String display_name, boolean isFollowing, boolean isFan) {
        this.userId = userId;
        this.profileUrl = profileUrl;
        this.username = username;
        this.display_name = display_name;
        this.isFollowing = isFollowing;
        this.isFan = isFan;
    }

    public ItemFollow() {
        this.userId = "";
        this.profileUrl = "";
        this.username = "";
        this.display_name = "";
        this.isFollowing = false;
        this.isFan = false;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public boolean isFan() {
        return isFan;
    }

    public void setFan(boolean fan) {
        isFan = fan;
    }

}
