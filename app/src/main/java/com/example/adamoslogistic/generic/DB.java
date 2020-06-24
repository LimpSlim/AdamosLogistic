package com.example.adamoslogistic.generic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.adamoslogistic.models.Message;
import com.example.adamoslogistic.models.Order;
import com.example.adamoslogistic.models.OrderAddInfo;
import com.example.adamoslogistic.models.OrderAttribute;
import com.example.adamoslogistic.models.Settings;
import com.example.adamoslogistic.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public final class DB {

    public static User GetCurrentUser() throws InterruptedException {
        User user = new User();

        /*Registry.DB_Connections.acquire();
        Cursor result = Registry.db.rawQuery("SELECT * FROM cur_user", null);
        Registry.DB_Connections.release();

        if (result.moveToFirst()) {
            user.Api_Key = result.getString(result.getColumnIndex("api_key"));
            user.Name = result.getString(result.getColumnIndex("name"));
            user.ID = result.getInt(result.getColumnIndex("id"));
        }

        return user;*/
        try {
            SharedPreferences curUser = Registry.baseContext.getSharedPreferences("user", Context.MODE_PRIVATE);
            if(curUser.contains("id"))
                user.ID = curUser.getInt("id", 0);

            if(curUser.contains("api_key"))
                user.Api_Key = curUser.getString("api_key", null);

            if(curUser.contains("name"))
                user.Name = curUser.getString("name", null);

            return user;
        }catch (Exception e) {
            return new User("", "", -1);
        }
    }

    @SuppressLint("DefaultLocale")
    public static void SetCurrentUser(User user) throws InterruptedException {
       /* user.Name = user.Name == null ? "" : user.Name;
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
        Registry.DB_Connections.release();*/
        SharedPreferences curUser = Registry.baseContext.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = curUser.edit();
        editor.putInt("id", user.ID);
        editor.putString("api_key", user.Api_Key);
        editor.putString("name", user.Name);
        editor.apply();
    }

    public static Settings GetCurrentSettings() throws InterruptedException {
        /*Settings settings = new Settings();
        Registry.DB_Connections.acquire();

        Cursor result = Registry.db.rawQuery("SELECT * FROM settings", null);
        if (result.moveToFirst()) {
            settings.Order_ID = result.getInt(result.getColumnIndex("order_id"));
        }

        Registry.DB_Connections.release();

        return settings;*/

        Settings settings = new Settings();

        try {
            SharedPreferences Settings = Registry.baseContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
            if(Settings.contains("order_id"))
                settings.Order_ID = Settings.getInt("order_id", 0);

            return settings;
        }catch (Exception e) {
            return new Settings(0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale")
    public static void SetCurrentSettings(Settings settings) throws InterruptedException {
        /*String query;
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
        Registry.DB_Connections.release();*/

        SharedPreferences Settings = Registry.baseContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = Settings.edit();
        editor.putInt("order_id", settings.Order_ID);
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale")
    public static void SetOrdersList(List<Order> orders) throws ParseException, InterruptedException {
        Registry.DB_Connections.acquire();
        Registry.db.execSQL("DELETE FROM orders");
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        for (Order order : orders) {
            List<OrderAttribute> othersAttribute = new ArrayList<>();

            Registry.db.execSQL(
                    String.format(
                            "INSERT INTO orders (id, title, time_start) VALUES ('%d', '%s', '%s')",
                            order.id,
                            order.title == null ? "" : order.title,
                            order.time_start == null ? (format.format(new Date())) : order.time_start.toString()
                    )
            );
        }

        Registry.DB_Connections.release();
        /*SharedPreferences ordersList = Registry.baseContext.getSharedPreferences("orders", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ordersList.edit();
        Gson gson = new Gson();
        String json = gson.toJson(orders);
        editor.putString("orders", json);
        editor.apply();*/
    }

    /*public static List<Order> GetOrdersList() throws InterruptedException {
        Settings settings = new Settings();
        Registry.DB_Connections.acquire();

        Cursor result = Registry.db.rawQuery("SELECT * FROM settings", null);
        if (result.moveToFirst()) {
            settings.Order_ID = result.getInt(result.getColumnIndex("order_id"));
        }

        Registry.DB_Connections.release();

        return settings;

        /*List<Order> orders = new ArrayList<>();

        try {
            SharedPreferences ordersList = Registry.baseContext.getSharedPreferences("orders", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = ordersList.getString("orders", null);
            Type type = new TypeToken<ArrayList<Order>>() {}.getType();
            orders = gson.fromJson(json, type);

            if (orders == null) {
                orders = new ArrayList<>();
            }
        }catch (Exception e) {

        }
        return orders;
    }*/

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
                orderAddInfo.setName(result.getString(result.getColumnIndex("name")));
                orderAddInfo.setNumber(result.getInt(result.getColumnIndex("number")));
                response.add(orderAddInfo);
            }while(result.moveToNext());
        }

        return response;
    }

    /*public static byte[] encryptString(String data) {
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
    }*/

    public static String GetAvatar() throws InterruptedException {
        String str = "";
        try {
            SharedPreferences avatar = Registry.baseContext.getSharedPreferences("avatar", Context.MODE_PRIVATE);

            if(avatar.contains("avatar"))
                str = avatar.getString("avatar", null);

        }catch (Exception e) {

        }
        return str;
    }

    public static void SetAvatar(String ava) throws InterruptedException {
        SharedPreferences avatar = Registry.baseContext.getSharedPreferences("avatar", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = avatar.edit();
        editor.putString("avatar", ava);
        editor.apply();
    }
}
