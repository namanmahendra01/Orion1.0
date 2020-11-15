package com.orion.orion.models;

public class Like {
    public String ui;

   public Like() {
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String user_id) {
        this.ui = user_id;
    }

    @Override
    public String toString() {
        return "Like{" +
                "ui='" + ui + '\'' +
                '}';
    }
}
