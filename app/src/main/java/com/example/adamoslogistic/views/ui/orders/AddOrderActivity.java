package com.example.adamoslogistic.views.ui.orders;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adamoslogistic.JsonPlaceHolderAPI;
import com.example.adamoslogistic.R;
import com.example.adamoslogistic.adapters.ForNewOrder;
import com.example.adamoslogistic.generic.DB;
import com.example.adamoslogistic.generic.Registry;
import com.example.adamoslogistic.models.AddResponseBodyOrders;
import com.example.adamoslogistic.models.AllAttributesFromUser;
import com.example.adamoslogistic.models.ApiKey;
import com.example.adamoslogistic.models.AttributesFromUser;
import com.example.adamoslogistic.models.OrderAddInfo;
import com.example.adamoslogistic.models.Order_id;
import com.example.adamoslogistic.models.Step;
import com.example.adamoslogistic.models.Values;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddOrderActivity extends AppCompatActivity {

    private JsonPlaceHolderAPI JsonPlaceHolderAPI;
    private Order_id order_id;
    private Handler eventHandler;
    private String api_key = DB.GetCurrentUser().Api_Key;
    private int i = 0;

    public AddOrderActivity() throws InterruptedException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        eventHandler = new Handler(msg -> {
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:

                    break;
                case 4:
                    Toast.makeText(AddOrderActivity.this, "Ошибка создания заказа", Toast.LENGTH_LONG)
                            .show();
                    break;
            }
            return false;
        });
    }

    class addOrderAsync extends AsyncTask <ApiKey, Void, Void> {
        @Override
        protected Void doInBackground(ApiKey... params) {
            JsonPlaceHolderAPI = Registry.retrofit.create(JsonPlaceHolderAPI.class);
            Call<Order_id> call = JsonPlaceHolderAPI.addOrder(params[0]);

            try {
                Response<Order_id> response = call.execute();
                if (response.isSuccessful()) {
                    order_id = response.body();
                    AddOrderActivity.this.eventHandler.sendEmptyMessage(0);
                } else AddOrderActivity.this.eventHandler.sendEmptyMessage(4);
            } catch (Exception e) {
                AddOrderActivity.this.eventHandler.sendEmptyMessage(4);
            }
            return null;
        }
    }

    class addOrderInfoAsync extends AsyncTask <ApiKey, Void, Void> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(ApiKey... params) {
            JsonPlaceHolderAPI = Registry.retrofit.create(JsonPlaceHolderAPI.class);
            Call<List<OrderAddInfo>> call = JsonPlaceHolderAPI.addOrderInfo(params[0]);

            try {
                Response<List<OrderAddInfo>> response = call.execute();
                if (response.isSuccessful()) {
                    List<OrderAddInfo> info = response.body();
                    DB.setOrderInfo(info);
                    AddOrderActivity.this.eventHandler.sendEmptyMessage(1);
                }
                else AddOrderActivity.this.eventHandler.sendEmptyMessage(4);
            } catch (Exception e) {
                AddOrderActivity.this.eventHandler.sendEmptyMessage(4);
            }
            return null;
        }
    }

    class addOrderStepAsync extends AsyncTask <Step, Void, Void> {
        @Override
        protected Void doInBackground(Step... params) {
            JsonPlaceHolderAPI = Registry.retrofit.create(JsonPlaceHolderAPI.class);
            Call<List<AddResponseBodyOrders>> call = JsonPlaceHolderAPI.addStep(params[0]);

            try {
                Response<List<AddResponseBodyOrders>> response = call.execute();
                if (response.isSuccessful()) {
                    List<AddResponseBodyOrders> addResponseBodyOrders = response.body();
                    AddOrderActivity.this.eventHandler.sendEmptyMessage(2);
                } else AddOrderActivity.this.eventHandler.sendEmptyMessage(4);
            } catch (Exception e) {
                AddOrderActivity.this.eventHandler.sendEmptyMessage(4);
            }
            return null;
        }
    }

    class fromUserAsync extends AsyncTask <AllAttributesFromUser, Void, Void> {
        @Override
        protected Void doInBackground(AllAttributesFromUser... params) {
            JsonPlaceHolderAPI = Registry.retrofit.create(JsonPlaceHolderAPI.class);
            Call<Integer> call = JsonPlaceHolderAPI.attributeAdd(params[0]);

            try {
                Response<Integer> response = call.execute();
                if (response.isSuccessful()) {
                    AddOrderActivity.this.eventHandler.sendEmptyMessage(3);
                } else AddOrderActivity.this.eventHandler.sendEmptyMessage(4);
            } catch (Exception e) {
                AddOrderActivity.this.eventHandler.sendEmptyMessage(4);
            }
            return null;
        }
    }
    /*private String api_key = "";
    private String value = "";
    private Order_id orderId;
    private List<OrderAddInfo> info;
    private int order_id;
    private List<AddResponseBodyOrders> addResponseBodyOrders;
    private List<AttributesFromUser> ATTRIBUTES = new ArrayList<>();
    private int i = 0;
    private int pos = 0;
    TextView step, category_name;
    Button next, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        step = findViewById(R.id.step);
        category_name = findViewById(R.id.category_name);
        next = findViewById(R.id.next);
        back = findViewById(R.id.back);


        try {
            api_key = DB.GetCurrentUser().Api_Key;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        addNewOrder();
        addOrderInfo();
    }

    private void addNewOrder() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(JsonPlaceHolderAPI.HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            JsonPlaceHolderAPI jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderAPI.class);
            ApiKey api = new ApiKey(api_key);
            Call<Order_id> call = jsonPlaceHolderApi.addOrder(api);
            call.enqueue(new Callback<Order_id>() {
                @Override
                public void onResponse(Call<Order_id> call, Response<Order_id> response) {
                    orderId = response.body();
                    order_id = orderId.getOrder_id();
                }

                @Override
                public void onFailure(Call<Order_id> call, Throwable t) {
                    Log.d("MyLog", t.toString());
                    Log.d("MyLog", "ОШИБКА: выход в onFailure");
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
            Log.d("MyLog", e.toString());
            Log.d("MyLog", "ОШИБКА: вывалилось в catch");
        }
    }

    private void addOrderInfo() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(JsonPlaceHolderAPI.HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            JsonPlaceHolderAPI jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderAPI.class);
            ApiKey api = new ApiKey(api_key);
            Call<List<OrderAddInfo>> call = jsonPlaceHolderApi.addOrderInfo(api);
            call.enqueue(new Callback<List<OrderAddInfo>>() {

                @Override
                public void onResponse(Call<List<OrderAddInfo>> call, Response<List<OrderAddInfo>> response) {
                    info = response.body();
                    category_name.setText(info.get(i).getName());
                    try {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(JsonPlaceHolderAPI.HOST)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        JsonPlaceHolderAPI jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderAPI.class);
                        Step step = new Step(api_key, info.get(i).getNumber());
                        Call<List<AddResponseBodyOrders>> call2 = jsonPlaceHolderApi.addStep(step);
                        call2.enqueue(new Callback<List<AddResponseBodyOrders>>() {
                            @Override
                            public void onResponse(Call<List<AddResponseBodyOrders>> call,
                                    Response<List<AddResponseBodyOrders>> response) {
                                addResponseBodyOrders = response.body();
                                ForNewOrder adapter = new ForNewOrder(AddOrderActivity.this, addResponseBodyOrders);
                                RecyclerView recyclerView = findViewById(R.id.attributes);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(AddOrderActivity.this));
                                next.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        for (int j = 0; j < addResponseBodyOrders.size(); j++) {
                                            View view = recyclerView.getChildAt(j);
                                            if (view != null) {
                                                if (addResponseBodyOrders.get(j).getAttribute_type() == 20) {
                                                    Spinner spinner_for_user = view.findViewById(R.id.spinner_for_user);
                                                    spinner_for_user.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                            pos = position;
                                                        }

                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                        }
                                                    });
                                                    List<Values> values = addResponseBodyOrders.get(j).getVALUES();
                                                    value = values.get(pos).getValue();
                                                } else if (addResponseBodyOrders.get(j).getAttribute_type() == 10) {
                                                    CalendarView calendarView = view.findViewById(R.id.choose_date);
                                                    calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
                                                        int mYear = year;
                                                        int mMonth = month;
                                                        int mDay = dayOfMonth;
                                                        value = new StringBuilder().append(mYear)
                                                                .append("-").append(mMonth)
                                                                .append("-").append(mDay).toString();
                                                    });
                                                } else if (addResponseBodyOrders.get(j).getAttribute_type() == 0) {
                                                    EditText attribute_from_user = view.findViewById(R.id.attribute_from_user);
                                                    value = attribute_from_user.getText().toString();
                                                }
                                                String attribute_name = addResponseBodyOrders.get(j).getAttribute_name();
                                                AttributesFromUser attribute = new AttributesFromUser(order_id, attribute_name, value);
                                                ATTRIBUTES.add(attribute);
                                            }
                                        }
                                        try {
                                            Retrofit retrofit = new Retrofit.Builder()
                                                    .baseUrl(JsonPlaceHolderAPI.HOST)
                                                    .addConverterFactory(GsonConverterFactory.create())
                                                    .build();

                                            JsonPlaceHolderAPI jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderAPI.class);
                                            for (int k = 0; k < ATTRIBUTES.size(); k++) {
                                                Log.d("MyLog", ATTRIBUTES.get(k).getAttribute_name());
                                            }
                                            AllAttributesFromUser allAttributesFromUser = new AllAttributesFromUser(api_key, ATTRIBUTES);
                                            Call<Integer> call2 = jsonPlaceHolderApi.attributeAdd(allAttributesFromUser);
                                            call2.enqueue(new Callback<Integer>() {
                                                @Override
                                                public void onResponse(Call<Integer> call, Response<Integer> response) {

                                                }

                                                @Override
                                                public void onFailure(Call<Integer> call, Throwable t) {

                                                }
                                            });
                                        }catch (Exception e) {
                                            e.printStackTrace();
                                            Log.d("MyLog", e.toString());
                                            Log.d("MyLog", "ОШИБКА: вывалилось в catch");
                                        }
                                        ATTRIBUTES.clear();
                                        if (i < info.size()-1) {
                                            i++;
                                            category_name.setText(info.get(i).getName());
                                            try {
                                                Retrofit retrofit = new Retrofit.Builder()
                                                        .baseUrl(JsonPlaceHolderAPI.HOST)
                                                        .addConverterFactory(GsonConverterFactory.create())
                                                        .build();

                                                JsonPlaceHolderAPI jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderAPI.class);
                                                Step step = new Step(api_key, info.get(i).getNumber());
                                                Call<List<AddResponseBodyOrders>> call = jsonPlaceHolderApi.addStep(step);
                                                call.enqueue(new Callback<List<AddResponseBodyOrders>>() {
                                                    @Override

                                                    public void onResponse(Call<List<AddResponseBodyOrders>> call,
                                                            Response<List<AddResponseBodyOrders>> response) {
                                                        addResponseBodyOrders.clear();
                                                        addResponseBodyOrders = response.body();
                                                        ForNewOrder adapter2 = new ForNewOrder(AddOrderActivity.this, addResponseBodyOrders);
                                                        recyclerView.setAdapter(adapter2);
                                                        recyclerView.setLayoutManager(new LinearLayoutManager(AddOrderActivity.this));
                                                    }

                                                    @Override
                                                    public void onFailure(Call<List<AddResponseBodyOrders>> call, Throwable t) {
                                                        Log.d("MyLog", t.toString());
                                                        Log.d("MyLog", "ОШИБКА: выход в onFailure");
                                                    }
                                                });
                                            }catch (Exception e) {
                                                e.printStackTrace();
                                                Log.d("MyLog", e.toString());
                                                Log.d("MyLog", "ОШИБКА: вывалилось в catch");
                                            }
                                        }
                                        else {
                                            Toast.makeText(AddOrderActivity.this, "Заказ успешно сформирован", Toast.LENGTH_LONG).show();
                                            returnToOrdersFragment();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<List<AddResponseBodyOrders>> call, Throwable t) {
                                Log.d("MyLog", t.toString());
                                Log.d("MyLog", "ОШИБКА: выход в onFailure");
                            }
                        });
                    }catch (Exception e) {
                        e.printStackTrace();
                        Log.d("MyLog", e.toString());
                        Log.d("MyLog", "ОШИБКА: вывалилось в catch");
                    }
                }

                @Override
                public void onFailure(Call<List<OrderAddInfo>> call, Throwable t) {
                    Log.d("MyLog", t.toString());
                    Log.d("MyLog", "ОШИБКА: выход в onFailure");
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
            Log.d("MyLog", e.toString());
            Log.d("MyLog", "ОШИБКА: вывалилось в catch");
        }
    }

    private void addOrderStep(int number) {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(JsonPlaceHolderAPI.HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            JsonPlaceHolderAPI jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderAPI.class);
            Step step = new Step(api_key, number);
            Call<List<AddResponseBodyOrders>> call = jsonPlaceHolderApi.addStep(step);
            call.enqueue(new Callback<List<AddResponseBodyOrders>>() {
                @Override
                public void onResponse(Call<List<AddResponseBodyOrders>> call,
                        Response<List<AddResponseBodyOrders>> response) {
                    addResponseBodyOrders = response.body();
                }

                @Override
                public void onFailure(Call<List<AddResponseBodyOrders>> call, Throwable t) {
                    Log.d("MyLog", t.toString());
                    Log.d("MyLog", "ОШИБКА: выход в onFailure");
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
            Log.d("MyLog", e.toString());
            Log.d("MyLog", "ОШИБКА: вывалилось в catch");
        }
    }

     */

    private void returnToOrdersFragment() {
        finish();
    }
}
