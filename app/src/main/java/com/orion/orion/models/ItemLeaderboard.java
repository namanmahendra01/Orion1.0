package com.orion.orion.models;

public class ItemLeaderboard {
    public String postionName;
    public int postionParameter;
    public String postionProfile;
    public String ui;

    public String getUserID() {
        return ui;
    }

    public String getPostionProfile() {
        return postionProfile;
    }

    public ItemLeaderboard(String postionName, int postionParameter, String postionProfile, String userID) {
        this.postionName = postionName;
        this.postionParameter = postionParameter;
        this.postionProfile = postionProfile;
        this.ui =userID;
    }

    public String getPostionName() {
        return postionName;
    }

    public String getPostionParameter() {
        return String.valueOf(postionParameter);
    }
}
