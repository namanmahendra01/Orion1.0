package com.orion.orion.models;

public class TopUsers {

    private String user_id;
    private int rating;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public TopUsers(String user_id, int rating) {
        this.user_id = user_id;
        this.rating = rating;
    }

    public TopUsers() {
    }


    public int getRating() {
        return rating;
    }

}
