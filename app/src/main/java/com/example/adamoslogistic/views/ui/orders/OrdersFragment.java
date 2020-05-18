package com.example.adamoslogistic.views.ui.orders;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.example.adamoslogistic.requests.Request;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class OrdersFragment extends Fragment {

    private RecyclerView ordersRecyclerView;
    private Handler h;

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

        h = new Handler(msg -> {
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

    public void DrawOrders() {
        RecyclerViewAdapterParams rvap = new RecyclerViewAdapterParams(
                new Pair<>("name", R.id.textView_order_name),
                new Pair<>("time_created", R.id.textView_order_time_created),
                new Pair<>("status", R.id.textView_order_status)
        );

        rvap.layoutID = R.layout.order_item;
        rvap.context = getContext();
        rvap.db_name = "app.db";
        rvap.query = "SELECT name, time_created, status FROM orders ORDER BY time_created DESC";

        try {
            RecyclerViewAdapter rva = new RecyclerViewAdapter(rvap, new RecyclerViewAdapter.OnItemListener() {
                @Override
                public void onItemClick(int position) throws NoSuchFieldException, IllegalAccessException, InterruptedException {

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

        @Override
        protected Void doInBackground(Request... params) {
            JsonPlaceHolderAPI = Registry.retrofit.create(JsonPlaceHolderAPI.class);
            Call<List<Order>> call = JsonPlaceHolderAPI.OrderGet(params[0]);

            try {
                Response<List<Order>> response = call.execute();
                if (response.isSuccessful()) {
                    List<Order> orders = response.body();
                    DB.SetOrdersList(orders);
                    OrdersFragment.this.h.sendEmptyMessage(0);
                } else OrdersFragment.this.h.sendEmptyMessage(1);
            } catch (ParseException | InterruptedException | IOException e) {
                OrdersFragment.this.h.sendEmptyMessage(1);
            }

            return null;
        }
    }
}