package com.example.adamoslogistic.views.ui.orders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.adamoslogistic.R;
import com.example.adamoslogistic.generic.RecyclerViewAdapter;
import com.example.adamoslogistic.generic.RecyclerViewAdapterParams;
import com.example.adamoslogistic.generic.Registry;
import com.example.adamoslogistic.views.ChatActivity;

public class DetailedOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_order);

        RecyclerView recyclerView = findViewById(R.id.attributes);
        try {
            Registry.DB_Connections.acquire();
            Cursor result = Registry.db.rawQuery("SELECT * FROM orders WHERE id IN " +
                    "(SELECT order_id FROM settings)", null);
            Registry.DB_Connections.release();

            if (result.moveToFirst()){
                String name = result.getString(result.getColumnIndex("name"));
                String status = result.getString(result.getColumnIndex("status"));

                TextView nameTextView = findViewById(R.id.textView_order_name);
                TextView statusTextView = findViewById(R.id.textView_order_status);

                nameTextView.setText(name);
                statusTextView.setText(status);

                RecyclerViewAdapterParams rvap = new RecyclerViewAdapterParams(
                        new Pair<>("attribute_description", R.id.item_attributeName),
                        new Pair<>("value", R.id.item_attributeValue)
                );
                rvap.context = this;
                rvap.layoutID = R.layout.attribute_item;
                rvap.query = "SELECT attribute_description, CASE WHEN type = 20 THEN description ELSE value END AS value" +
                        " FROM attribute_orders WHERE order_id IN (SELECT order_id FROM settings)";

                RecyclerViewAdapter rva = new RecyclerViewAdapter(rvap, new RecyclerViewAdapter.OnItemListener() {
                    @Override
                    public void onItemClick(int position) throws NoSuchFieldException, IllegalAccessException, InterruptedException {

                    }
                });
                recyclerView.setAdapter(rva);
                recyclerView.setLayoutManager(
                        new LinearLayoutManager(
                                this
                        ));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Button button = findViewById(R.id.button_open_chat);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailedOrderActivity.this, ChatActivity.class));
            }
        });
    }
}
