package com.example.adamoslogistic.requests;

public class OrderGetRequest {

    public String method;
    public Request params;

    public OrderGetRequest(String method, Request params) {
        this.method = method;
        this.params = params;
    }
}
