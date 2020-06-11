package com.example.adamoslogistic.generic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.adamoslogistic.JsonPlaceHolderAPI;

import java.util.concurrent.Semaphore;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public final class Registry {

    public static SQLiteDatabase db;
    public static final Semaphore DB_Connections = new Semaphore(1, true);
    public static Context baseContext;

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(JsonPlaceHolderAPI.HOST)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static void InitDB(Context BaseContext) {
        Registry.db = BaseContext.openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        Registry.db.execSQL("CREATE TABLE IF NOT EXISTS cur_user (name TEXT, api_key TEXT, id INTEGER)");

        Registry.db.execSQL("CREATE TABLE IF NOT EXISTS settings (order_id INTEGER)");

        Registry.db.execSQL("CREATE TABLE IF NOT EXISTS orders (" +
                "name TEXT, time_created DATETIME, id INTEGER, status TEXT, timeshort TEXT)");

        Registry.db.execSQL("CREATE TABLE IF NOT EXISTS attribute_orders (" +
                "order_id INTEGER, name TEXT, value TEXT, description TEXT, attribute_description TEXT, type INTEGER)");

        Registry.db.execSQL("CREATE TABLE IF NOT EXISTS messages(" +
                "order_id INTEGER, value TEXT, time TEXT, user_id INTEGER)");

        Registry.db.execSQL("CREATE TABLE IF NOT EXISTS order_info (name TEXT, number INTEGER)");
    }
}
