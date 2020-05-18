package com.example.adamoslogistic.requests;

import com.example.adamoslogistic.models.OrderAttribute;

import java.util.List;

public class OrderGetResponse extends Response {
    public String name;
    public int order_id;

    public List<OrderAttribute> ATTRIBUTES;
}
