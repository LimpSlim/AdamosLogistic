package com.example.adamoslogistic.models;

import java.util.List;

public class AllAttributesFromUser {

    private String api_key;
    private List<AttributesFromUser> ATTRIBUTES;

    public AllAttributesFromUser(String api_key, List<AttributesFromUser> attributesFromUsers) {
        this.api_key = api_key;
        this.ATTRIBUTES = attributesFromUsers;
    }

    public String getApi_key() {
        return api_key;
    }

    public List<AttributesFromUser> getATTRIBUTES() {
        return ATTRIBUTES;
    }
}
