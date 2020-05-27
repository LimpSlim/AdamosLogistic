package com.example.adamoslogistic.models;

public class AttributesFromUser {

    private int order_id;
    private String attribute_name;
    private String value;

    public AttributesFromUser(int order_id, String attribute_name, String value) {
        this.order_id = order_id;
        this.attribute_name = attribute_name;
        this.value = value;
    }

    public int getOrder_id() {
        return order_id;
    }

    public String getAttribute_name() {
        return attribute_name;
    }

    public String getValue() {
        return value;
    }
}
