package com.example.adamoslogistic.models;

public class Step {

    private String api_key;
    private int number;

    public Step(String api_key, int number) {
        this.api_key = api_key;
        this.number = number;
    }

    public String getApi_key() {
        return api_key;
    }

    public int getNumber() {
        return number;
    }
}
