package com.example.adamoslogistic.models;

import java.util.List;

public class AddResponseBodyOrders {
    private String attribute_name;
    private String attribute_description;
    private int attribute_type;
    private List<Values> VALUES;
    private String ERROR_ID;

    public AddResponseBodyOrders(String attribute_name, String attribute_description,
                                 int attribute_type, List<Values> VALUES, String ERROR_ID) {
        this.attribute_name = attribute_name;
        this.attribute_description = attribute_description;
        this.attribute_type = attribute_type;
        this.VALUES = VALUES;
        this.ERROR_ID = ERROR_ID;
    }

    public String getAttribute_name() {
        return attribute_name;
    }

    public String getAttribute_description() {
        return attribute_description;
    }

    public int getAttribute_type() {
        return attribute_type;
    }

    public String getERROR_ID() {
        return ERROR_ID;
    }

    public List<Values> getVALUES() {
        return VALUES;
    }
}
