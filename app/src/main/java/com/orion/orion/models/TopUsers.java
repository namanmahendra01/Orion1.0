package com.orion.orion.models;

public class TopUsers {

    private String ui;
    private int rat;

    public String getUi() {
        return ui;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    public TopUsers(String ui, int rat) {
        this.ui = ui;
        this.rat = rat;
    }

    public TopUsers() {
    }


    public int getRat() {
        return rat;
    }

}
