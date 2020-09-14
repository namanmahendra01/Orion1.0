package com.orion.orion.Notifications;

public class Sender {
    private Data data;
    private String to;

    public  Sender(){

    }

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "Sender{" +
                "data=" + data +
                ", to='" + to + '\'' +
                '}';
    }
}
