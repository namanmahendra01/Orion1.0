package com.orion.orion.models;

public class TopUsers {

//    private String username;
//    private String profile_photo;
    private String user_id;
    private int rating;
    private String domain;


    public TopUsers() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public TopUsers(String user_id, int rating, String domain) {
        this.user_id = user_id;
        this.rating = rating;
        this.domain = domain;
    }
}
