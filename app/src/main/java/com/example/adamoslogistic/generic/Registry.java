package com.example.adamoslogistic.generic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.adamoslogistic.JsonPlaceHolderAPI;

import java.util.concurrent.Semaphore;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public final class Registry {

    static SQLiteDatabase db;
    static final Semaphore DB_Connections = new Semaphore(1, true);

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(JsonPlaceHolderAPI.HOST)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static void InitDB(Context BaseContext) {
        Registry.db = BaseContext.openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        Registry.db.execSQL("CREATE TABLE IF NOT EXISTS cur_user (name TEXT, api_key TEXT, id INTEGER)");
        Registry.db.execSQL("CREATE TABLE IF NOT EXISTS orders (name TEXT, time_created DATETIME, id INTEGER, status TEXT)");
    }
}
