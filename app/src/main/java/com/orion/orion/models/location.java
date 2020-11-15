package com.orion.orion.models;

public class location {

    private String cntry;
    private String cty;
    private String area;

    public location() {
        this.cntry = "";
        this.cty = "";
        this.area = "";
    }


    public String getCntry() {
        return cntry;
    }

    public void setCntry(String country) {
        this.cntry = country;
    }

    public String getCty() {
        return cty;
    }

    public void setCty(String city) {
        this.cty = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
