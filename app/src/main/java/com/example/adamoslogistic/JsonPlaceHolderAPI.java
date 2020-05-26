package com.example.adamoslogistic;

import com.example.adamoslogistic.models.Order;
import com.example.adamoslogistic.requests.LoginRequest;
import com.example.adamoslogistic.requests.LoginResponse;
import com.example.adamoslogistic.requests.MessageAddRequest;
import com.example.adamoslogistic.requests.MessageAddResponse;
import com.example.adamoslogistic.requests.MessageGetResponse;
import com.example.adamoslogistic.requests.MessageGetRequest;
import com.example.adamoslogistic.requests.Request;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface JsonPlaceHolderAPI {

    String HOST = "http://25.99.235.231/";

    @POST("process.php?route=user/login")
    Call<LoginResponse> Login(
            @Body LoginRequest request
    );

    @POST("process.php?route=order/get")
    Call<List<Order>> OrderGet(
            @Body Request request
    );

    @POST("process.php?route=message/get")
    Call<MessageGetResponse> MessageGet(
            @Body MessageGetRequest request
    );

    @POST("process.php?route=message/add")
    Call<MessageAddResponse> MessageAdd(
            @Body MessageAddRequest request
    );

}