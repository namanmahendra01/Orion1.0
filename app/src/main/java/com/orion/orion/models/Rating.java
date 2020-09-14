package com.orion.orion.models;

public class Rating {

    private int post;
    private int followers;
    private int contest;

    public Rating() {
        this.post = 0;
        this.followers = 0;
        this.contest = 0;
    }

    public Rating(int post, int followers, int contest) {
        this.post = post;
        this.followers = followers;
        this.contest = contest;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getContest() {
        return contest;
    }

    public void setContest(int contest) {
        this.contest = contest;
    }
}
