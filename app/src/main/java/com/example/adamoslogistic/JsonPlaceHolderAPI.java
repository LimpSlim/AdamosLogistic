package com.example.adamoslogistic;

import com.example.adamoslogistic.models.AddResponseBodyOrders;
import com.example.adamoslogistic.models.AllAttributesFromUser;
import com.example.adamoslogistic.models.ApiKey;
import com.example.adamoslogistic.models.Order;
import com.example.adamoslogistic.models.OrderAddInfo;
import com.example.adamoslogistic.models.Order_id;
import com.example.adamoslogistic.models.Step;
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
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface JsonPlaceHolderAPI {

    String HOST = "https://kittco.ru/adamos/";

    @POST("auth/process.php")
    Call<LoginResponse> Login(
            @Body LoginRequest request
    );

    @POST("adamos/process.php?route=order/get")
    Call<List<Order>> OrderGet(
            @Body Request request
    );

    @POST("adamos/process.php?route=message/get")
    Call<MessageGetResponse> MessageGet(
            @Body MessageGetRequest request
    );

    @POST("adamos/process.php?route=message/add")
    Call<MessageAddResponse> MessageAdd(
            @Body MessageAddRequest request
    );

    @POST("adamos/process.php?route=order/add/info")
    Call<List<OrderAddInfo>> addOrderInfo (
            @Body ApiKey api_key
    );

    @POST("adamos/process.php?route=order/add/step")
    Call<List<AddResponseBodyOrders>> addStep (
            @Body Step step
    );

    @POST("adamos/process.php?route=order/attribute/add")
    Call<Integer> attributeAdd (
            @Body AllAttributesFromUser allAttributesFromUser
    );

    @POST("adamos/process.php?route=order/add")
    Call<Order_id> addOrder(
            //@Body PostAddOrderData addOrderData
            @Body ApiKey apiKey
    );
}