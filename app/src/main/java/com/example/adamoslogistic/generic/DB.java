package com.example.adamoslogistic.generic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.adamoslogistic.models.Message;
import com.example.adamoslogistic.models.Order;
import com.example.adamoslogistic.models.OrderAddInfo;
import com.example.adamoslogistic.models.OrderAttribute;
import com.example.adamoslogistic.models.Settings;
import com.example.adamoslogistic.models.User;
import com.example.adamoslogistic.views.LoginActivity;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class DB {

    public static User GetCurrentUser() throws InterruptedException {
        User user = new User();

        Registry.DB_Connections.acquire();
        Cursor result = Registry.db.rawQuery("SELECT * FROM cur_user", null);
        Registry.DB_Connections.release();

        if (result.moveToFirst()) {
            user.Api_Key = decryptString(result.getString(result.getColumnIndex("api_key")).getBytes());
            user.Name = decryptString(result.getString(result.getColumnIndex("name")).getBytes());
            user.ID = decryptInt(String.valueOf(result.getInt(result.getColumnIndex("id"))).getBytes());
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
            settings.Order_ID = decryptInt(String.valueOf(result.getInt(result.getColumnIndex("order_id"))).getBytes());
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
        Registry.db.execSQL("DELETE FROM orders");
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm");

        for (Order order : orders) {
            List<OrderAttribute> othersAttribute = new ArrayList<>();

            for (OrderAttribute attribute : order.ATTRIBUTES) {

                if (attribute.name.equals("order_status"))
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
                message.time = decryptString(result.getString(result.getColumnIndex("time")).getBytes());
                message.value = decryptString(result.getString(result.getColumnIndex("value")).getBytes());
                message.user_id = decryptInt(String.valueOf(result.getInt(result.getColumnIndex("user_id"))).getBytes());
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale")
    public static void setOrderInfo(List<OrderAddInfo> info) throws InterruptedException {

        List<String> inserts = new ArrayList<>();
        Registry.DB_Connections.acquire();
        Registry.db.execSQL("DELETE FROM order_info");
        for (OrderAddInfo INFO : info) {
            inserts.add(
                    String.format(
                            "('%s', '%d')",
                            INFO.getName() == null ? "" : INFO.getName(),
                            INFO.getNumber()
                    )
            );
            /*Registry.db.execSQL(
                    String.format(
                            "DELETE FROM order_info WHERE name = %s",
                            INFO.getName()
                            ));
             */
        }

        if (inserts.size() != 0) {
            Registry.db.execSQL(
                    String.format(
                            "INSERT INTO order_info (name, number) VALUES %s",
                            String.join(",", inserts)
                    ));
        }

        Registry.DB_Connections.release();
    }

    public static List<OrderAddInfo> getOrderInfo() throws InterruptedException {

        Registry.DB_Connections.acquire();
        @SuppressLint("DefaultLocale") Cursor result = Registry.db.rawQuery(
                String.format(
                        "SELECT * FROM messages WHERE order_id = '%d'") ,null);
        Registry.DB_Connections.release();

        List<OrderAddInfo> response = new ArrayList<>();
        if (result.moveToFirst()){
            do{
                OrderAddInfo orderAddInfo = new OrderAddInfo("", 0);
                orderAddInfo.setName(decryptString(result.getString(result.getColumnIndex("name")).getBytes()));
                orderAddInfo.setNumber(decryptInt(String.valueOf(result.getInt(result.getColumnIndex("number"))).getBytes()));
                response.add(orderAddInfo);
            }while(result.moveToNext());
        }

        return response;
    }

    public static byte[] encryptString(String data) {
        byte[] encrypted = new byte[0];
        String key = "";
        SharedPreferences pref = Registry.baseContext.getSharedPreferences("SecretKey", Context.MODE_PRIVATE);
        if(pref.contains("SecretKey"))
            key = pref.getString("SecretKey", null);
        try {
            assert key != null;
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            encrypted = cipher.doFinal(data.getBytes());
        } catch (Exception e) {
            Log.d("MyTag", e.toString());
        }
        return encrypted;
    }

    public static String decryptString(byte[] data) {
        String decrypted = "";
        String key = "";
        SharedPreferences pref = Registry.baseContext.getSharedPreferences("SecretKey", Context.MODE_PRIVATE);
        if(pref.contains("SecretKey"))
            key = pref.getString("SecretKey", null);
        try {
            assert key != null;
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // decrypt the text
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            decrypted = new String(cipher.doFinal(data));
        } catch (Exception e) {
            Log.d("MyTag", e.toString());
        }
        return decrypted;
    }

    public static byte[] encryptInt(int data) {
        byte[] encrypted = new byte[0];
        String key = "";
        String _data = String.valueOf(data);
        SharedPreferences pref = Registry.baseContext.getSharedPreferences("SecretKey", Context.MODE_PRIVATE);
        if(pref.contains("SecretKey"))
            key = pref.getString("SecretKey", null);
        try {
            assert key != null;
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            encrypted = cipher.doFinal(_data.getBytes());
        } catch (Exception e) {
            Log.d("MyTag", e.toString());
        }
        return encrypted;
    }

    public static int decryptInt(byte[] data) {
        int decrypted = 0;
        String str = "";
        String key = "";
        SharedPreferences pref = Registry.baseContext.getSharedPreferences("SecretKey", Context.MODE_PRIVATE);
        if(pref.contains("SecretKey"))
            key = pref.getString("SecretKey", null);
        try {
            assert key != null;
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // decrypt the text
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            str = new String(cipher.doFinal(data));
            decrypted = Integer.parseInt(str);
        } catch (Exception e) {
            Log.d("MyTag", e.toString());
        }
        return decrypted;
    }

    public static byte[] encryptDate(Date data) {
        byte[] encrypted = new byte[0];
        String key = "";
        String _data = String.valueOf(data);
        SharedPreferences pref = Registry.baseContext.getSharedPreferences("SecretKey", Context.MODE_PRIVATE);
        if(pref.contains("SecretKey"))
            key = pref.getString("SecretKey", null);
        try {
            assert key != null;
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            encrypted = cipher.doFinal(_data.getBytes());
        } catch (Exception e) {
            Log.d("MyTag", e.toString());
        }
        return encrypted;
    }

    public static Date decryptDate(byte[] data) {
        Date decrypted = null;
        String str = "";
        String key = "";
        SharedPreferences pref = Registry.baseContext.getSharedPreferences("SecretKey", Context.MODE_PRIVATE);
        if(pref.contains("SecretKey"))
            key = pref.getString("SecretKey", null);
        try {
            assert key != null;
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // decrypt the text
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            str = new String(cipher.doFinal(data));
            decrypted = new SimpleDateFormat("dd/MM/yyyy").parse(str);
        } catch (Exception e) {
            Log.d("MyTag", e.toString());
        }
        return decrypted;
    }
}
