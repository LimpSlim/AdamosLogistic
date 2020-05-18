package com.example.adamoslogistic.requests;

public class LoginRequest extends Request {
    public String email;
    public String password;

    public LoginRequest(String _email, String _pass){
        email = _email;
        password = _pass;
    }
}
