package com.example.adamoslogistic.models;

public class User {
    public Integer ID = -1;
    public String Api_Key;
    public String Name;

    public User(String api_key, String name, Integer id) {
        init(api_key, name, id);
    }

    public User(){
        init("", "", -1);
    }

    public void init(String api_key, String name, Integer id){
        Api_Key = api_key;
        Name = name;
        ID = id;
    }
}
