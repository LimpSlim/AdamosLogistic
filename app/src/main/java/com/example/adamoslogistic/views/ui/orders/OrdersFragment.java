package com.example.adamoslogistic.views.ui.orders;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adamoslogistic.JsonPlaceHolderAPI;
import com.example.adamoslogistic.R;
import com.example.adamoslogistic.generic.DB;
import com.example.adamoslogistic.generic.RecyclerViewAdapter;
import com.example.adamoslogistic.generic.RecyclerViewAdapterParams;
import com.example.adamoslogistic.generic.Registry;
import com.example.adamoslogistic.models.Order;
import com.example.adamoslogistic.models.Settings;
import com.example.adamoslogistic.requests.Request;
import com.example.adamoslogistic.views.DetailedOrderActivity;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class OrdersFragment extends Fragment {

    private RecyclerView ordersRecyclerView;
    RecyclerViewAdapter rva;
    private Handler eventHandler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_orders, container, false);

        ordersRecyclerView = root.findViewById(R.id.orders);

        DrawOrders();

        try {
            Request r = new Request();
            r.api_key = DB.GetCurrentUser().Api_Key;
            new OrderGetAsync().execute(r);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        eventHandler = new Handler(msg -> {
            switch (msg.what) {
                case 0:
                    DrawOrders();
                    break;
                case 1:
                    Toast.makeText(getContext(), "Ошибка соединения", Toast.LENGTH_LONG)
                            .show();
                    break;
            }
            return false;
        });

        return root;
    }

    private void DrawOrders() {
        RecyclerViewAdapterParams rvap = new RecyclerViewAdapterParams(
                new Pair<>("name", R.id.textView_order_name),
                new Pair<>("timeshort", R.id.textView_order_time_created),
                new Pair<>("status", R.id.textView_order_status)
        );

        rvap.layoutID = R.layout.order_item;
        rvap.context = getContext();
        rvap.query = "SELECT name, timeshort, status, id FROM orders ORDER BY time_created DESC";

        try {
            rva = new RecyclerViewAdapter(rvap, new RecyclerViewAdapter.OnItemListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onItemClick(int position) throws NoSuchFieldException, IllegalAccessException, InterruptedException {
                    Integer id = Integer.parseInt(rva.extracted_data.get(position).get("id"));
                    DB.SetCurrentSettings(new Settings(id));
                    startActivity(new Intent(getActivity().getApplicationContext(), DetailedOrderActivity.class));
                }
            });

            ordersRecyclerView.setAdapter(rva);
            ordersRecyclerView.setLayoutManager(
                    new LinearLayoutManager(getContext())
            );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class OrderGetAsync extends AsyncTask<Request, Void, Void> {

        private JsonPlaceHolderAPI JsonPlaceHolderAPI;

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Request... params) {
            JsonPlaceHolderAPI = Registry.retrofit.create(JsonPlaceHolderAPI.class);
            Call<List<Order>> call = JsonPlaceHolderAPI.OrderGet(params[0]);

            try {
                Response<List<Order>> response = call.execute();
                if (response.isSuccessful()) {
                    List<Order> orders = response.body();
                    DB.SetOrdersList(orders);
                    OrdersFragment.this.eventHandler.sendEmptyMessage(0);
                } else OrdersFragment.this.eventHandler.sendEmptyMessage(1);
            } catch (ParseException | InterruptedException | IOException e) {
                OrdersFragment.this.eventHandler.sendEmptyMessage(1);
            }

            return null;
        }
    }
}