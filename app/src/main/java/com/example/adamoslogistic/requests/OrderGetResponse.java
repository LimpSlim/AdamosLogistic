package com.example.adamoslogistic.requests;

import com.example.adamoslogistic.models.Order;
import com.example.adamoslogistic.models.OrderAttribute;

import java.util.List;

public class OrderGetResponse extends Response {

    public List<Order> result;
    public String length;
}
