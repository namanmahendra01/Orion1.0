package com.orion.orion.models;

public class Leaderboard {

    private Rating all_time;
    private Rating yearly;
    private Rating last_month;
    private Rating this_month;
    private Rating last_week;
    private Rating this_week;
    private location last_known_location;
    private String last_updated;
    private int followers;
    private int joined_contest;
    private int created_contest;
    private String username;
    private String domain;
    private String profile_photo;

    public Rating getAll_time() {
        return all_time;
    }

    public void setAll_time(Rating all_time) {
        this.all_time = all_time;
    }

    public Rating getYearly() {
        return yearly;
    }

    public void setYearly(Rating yearly) {
        this.yearly = yearly;
    }

    public Rating getLast_month() {
        return last_month;
    }

    public void setLast_month(Rating last_month) {
        this.last_month = last_month;
    }

    public Rating getThis_month() {
        return this_month;
    }

    public void setThis_month(Rating this_month) {
        this.this_month = this_month;
    }

    public Rating getLast_week() {
        return last_week;
    }

    public void setLast_week(Rating last_week) {
        this.last_week = last_week;
    }

    public Rating getThis_week() {
        return this_week;
    }

    public void setThis_week(Rating this_week) {
        this.this_week = this_week;
    }

    public location getLast_known_location() {
        return last_known_location;
    }

    public void setLast_known_location(location last_known_location) {
        this.last_known_location = last_known_location;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getJoined_contest() {
        return joined_contest;
    }

    public void setJoined_contest(int joined_contest) {
        this.joined_contest = joined_contest;
    }

    public int getCreated_contest() {
        return created_contest;
    }

    public void setCreated_contest(int created_contest) {
        this.created_contest = created_contest;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Leaderboard(Rating all_time, Rating yearly, Rating last_month, Rating this_month, Rating last_week, Rating this_week, location last_known_location, String last_updated, int followers, int joined_contest, int created_contest, String username, String domain, String profile_photo) {
        this.all_time = all_time;
        this.yearly = yearly;
        this.last_month = last_month;
        this.this_month = this_month;
        this.last_week = last_week;
        this.this_week = this_week;
        this.last_known_location = last_known_location;
        this.last_updated = last_updated;
        this.followers = followers;
        this.joined_contest = joined_contest;
        this.created_contest = created_contest;
        this.username = username;
        this.domain = domain;
        this.profile_photo = profile_photo;
    }

    public Leaderboard() {
        this.last_updated = "";
        this.followers = 0;
        this.joined_contest = 0;
        this.created_contest = 0;
        this.last_known_location = new location();
        this.all_time = new Rating();
        this.yearly = new Rating();
        this.last_month = new Rating();
        this.this_month = new Rating();
        this.last_week = new Rating();
        this.this_week = new Rating();
        this.username = "";
        this.domain = "";
        this.profile_photo="";
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }
}
