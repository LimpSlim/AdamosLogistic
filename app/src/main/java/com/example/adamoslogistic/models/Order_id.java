package com.example.adamoslogistic.models;

public class Order_id {
    private String api_key;
    private int order_id;

    public Order_id(String api_key, int order_id) {
        this.api_key = api_key;
        this.order_id = order_id;
    }

    public String getApi_key() {
        return api_key;
    }

    public int getOrder_id() {
        return order_id;
    }
}
