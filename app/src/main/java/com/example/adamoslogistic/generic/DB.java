package com.example.adamoslogistic.generic;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.adamoslogistic.models.Message;
import com.example.adamoslogistic.models.Order;
import com.example.adamoslogistic.models.OrderAttribute;
import com.example.adamoslogistic.models.Settings;
import com.example.adamoslogistic.models.User;

import java.util.ArrayList;
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

    public static Settings GetCurrentSettings() throws InterruptedException {
        Settings settings = new Settings();
        Registry.DB_Connections.acquire();

        Cursor result = Registry.db.rawQuery("SELECT * FROM settings", null);
        if (result.moveToFirst()) {
            settings.Order_ID = result.getInt(result.getColumnIndex("order_id"));
        }

        Registry.DB_Connections.release();

        return settings;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale")
    public static void SetCurrentSettings(Settings settings) throws InterruptedException {
        String query;
        if (GetCurrentSettings().Order_ID == 0) {
            query = String.format(
                    "INSERT INTO settings (order_id) VALUES ('%d')",
                    settings.Order_ID
            );
        } else {
            List<String> updates = new ArrayList<>();
            if (settings.Order_ID != -1) {
                updates.add("order_id=" + settings.Order_ID.toString());
            }

            if (updates.size() == 0) {
                return;
            }

            query = String.format(
                    "UPDATE settings SET %s",
                    String.join(",", updates)
            );
        }

        Registry.DB_Connections.acquire();
        Registry.db.execSQL(query);
        Registry.DB_Connections.release();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale")
    public static void SetOrdersList(List<Order> orders) throws ParseException, InterruptedException {
        Registry.DB_Connections.acquire();
        Registry.db.execSQL("DELETE FROM orders;");
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm");

        for (Order order : orders) {
            List<OrderAttribute> othersAttribute = new ArrayList<>();

            for (OrderAttribute attribute : order.ATTRIBUTES) {
                if (attribute.name.equals("time_created")) {
                    order.time_created = format.parse(attribute.value);
                    order.timeshort = attribute.value;
                } else if (attribute.name.equals("order_status"))
                    order.status = attribute.description;
                else {
                    othersAttribute.add(attribute);
                }
            }

            Registry.db.execSQL(
                    String.format(
                            "INSERT INTO orders (id, name, time_created, status, timeshort) VALUES ('%d', '%s', '%s', '%s', '%s')",
                            order.order_id,
                            order.name == null ? "" : order.name,
                            order.time_created == null ? (format.format(new Date())) : order.time_created.toString(),
                            order.status == null ? "" : order.status,
                            order.timeshort == null ? "" : order.timeshort
                    )
            );

            List<String> inserts = new ArrayList<>();
            for (OrderAttribute attribute : othersAttribute) {
                inserts.add(
                        String.format(
                                "('%d', '%s', '%s', '%s', '%s', '%d')",
                                order.order_id,
                                attribute.name == null ? "" : attribute.name,
                                attribute.value == null ? "" : attribute.value,
                                attribute.attribute_description == null ? "" : attribute.attribute_description,
                                attribute.description == null ? "" : attribute.description,
                                attribute.type
                        )
                );
            }

            Registry.db.execSQL(
                    String.format(
                            "DELETE FROM attribute_orders WHERE order_id=%d",
                            order.order_id
                    )
            );
            if (inserts.size() != 0) {
                Registry.db.execSQL(
                        String.format(
                                "INSERT INTO attribute_orders (order_id, name, value, " +
                                        "attribute_description, description, type) VALUES %s ",
                                String.join(",", inserts)
                        )
                );
            }
        }

        Registry.DB_Connections.release();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale")
    public static void SetMessagesList(List<Message> messages) throws ParseException, InterruptedException {

        Integer order_id = GetCurrentSettings().Order_ID;

        List<String> inserts = new ArrayList<>();
        for (Message message : messages) {
            inserts.add(
                    String.format(
                            "('%d', '%s', '%s', '%d')",
                            order_id,
                            message.value == null ? "" : message.value,
                            message.time == null ? "" : message.time,
                            message.user_id
                    )
            );
        }

        Registry.DB_Connections.acquire();
        Registry.db.execSQL(
                String.format(
                        "DELETE FROM messages WHERE order_id = %d",
                        order_id
                ));

        if (inserts.size() != 0) {
            Registry.db.execSQL(
                    String.format(
                            "INSERT INTO messages (order_id, value, time, user_id) VALUES %s",
                            String.join(",", inserts)
                    ));
        }

        Registry.DB_Connections.release();
    }

    public static List<Message> GetMessagesList() throws InterruptedException {
        Integer order_id = GetCurrentSettings().Order_ID;

        Registry.DB_Connections.acquire();
        @SuppressLint("DefaultLocale") Cursor result = Registry.db.rawQuery(
                String.format(
                        "SELECT * FROM messages WHERE order_id = '%d'", order_id) ,null);
        Registry.DB_Connections.release();

        List<Message> response = new ArrayList<>();
        if (result.moveToFirst()){
            do{
                Message message = new Message();
                message.time = result.getString(result.getColumnIndex("time"));
                message.value = result.getString(result.getColumnIndex("value"));
                message.user_id = result.getInt(result.getColumnIndex("user_id"));
                response.add(message);
            }while(result.moveToNext());
        }


        return response;
    }

    @SuppressLint("DefaultLocale")
    public static void AddMessage(Message data) throws InterruptedException {

        Registry.DB_Connections.acquire();
        Registry.db.execSQL(
                String.format(
                        "INSERT INTO messages (order_id, value, time, user_id) VALUES ('%d', '%s', '%s', '%d')",
                        GetCurrentSettings().Order_ID,
                        data.value == null ? "" : data.value,
                        data.time  == null ? "" : data.time,
                        data.user_id
                ));
        Registry.DB_Connections.release();
    }
}
