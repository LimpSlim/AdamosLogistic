package com.example.adamoslogistic.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    public String name;
    public Integer order_id;
    public String time_created;
    public String timeshort;
    public String status;

    public List<OrderAttribute> ATTRIBUTES = new ArrayList<>();
}