package com.orion.orion.models;

public class location {

    private String country;
    private String city;
    private String area;

    public location() {
        this.country = "";
        this.city = "";
        this.area = "";
    }

    public location(String country, String city, String area) {
        this.country = country;
        this.city = city;
        this.area = area;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
