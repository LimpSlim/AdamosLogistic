package com.example.adamoslogistic.requests;

public class LoginRequest extends Request {

    public String method;
    public Params params;

    public LoginRequest(String method, Params params) {
        this.method = method;
        this.params = params;
    }
}
