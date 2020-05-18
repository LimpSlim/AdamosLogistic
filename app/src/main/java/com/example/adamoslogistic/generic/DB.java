package com.example.adamoslogistic.generic;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.example.adamoslogistic.models.Order;
import com.example.adamoslogistic.models.OrderAttribute;
import com.example.adamoslogistic.models.User;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public final class DB {

    public static User GetCurrentUser() throws InterruptedException {
        User user = new User();

        Registry.DB_Connections.acquire();
        Cursor result = Registry.db.rawQuery("SELECT * FROM cur_user", null);
        Registry.DB_Connections.release();

        if (result.moveToFirst()) {
            user.Api_Key = result.getString(result.getColumnIndex("api_key"));
            user.Name = result.getString(result.getColumnIndex("name"));
            user.ID = result.getInt(result.getColumnIndex("id"));
        }

        return user;
    }

    @SuppressLint("DefaultLocale")
    public static void SetCurrentUser(User user) throws InterruptedException {
        user.Name = user.Name == null ? "" : user.Name;
        user.Api_Key = user.Api_Key == null ? "" : user.Api_Key;

        Registry.DB_Connections.acquire();
        Registry.db.execSQL("DELETE FROM cur_user;");
        Registry.db.execSQL(
                String.format(
                        "INSERT INTO cur_user (api_key, name, id) VALUES ('%s', '%s', '%d')",
                        user.Api_Key,
                        user.Name,
                        user.ID
                )
        );
        Registry.DB_Connections.release();
    }

    @SuppressLint("DefaultLocale")
    public static void SetOrdersList(List<Order> orders) throws ParseException, InterruptedException {
        Registry.DB_Connections.acquire();
        Registry.db.execSQL("DELETE FROM orders;");
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm");

        for (Order order : orders) {
            for (OrderAttribute attribute : order.ATTRIBUTES) {
                if (attribute.name.equals("time_created")) {
                    order.time_created = format.parse(attribute.value);
                } else if (attribute.name.equals("order_status")) {
                    order.status = attribute.description;
                }
            }
            Registry.db.execSQL(
                    String.format(
                            "INSERT INTO orders (id, name, time_created, status) VALUES ('%d', '%s', '%s', '%s')",
                            order.order_id,
                            order.name == null ? "" : order.name,
                            order.time_created == null ? (format.format(new Date())) : order.time_created.toString(),
                            order.status == null ? "" : order.status
                    )
            );
        }
        Registry.DB_Connections.release();
    }
}
