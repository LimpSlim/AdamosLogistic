package com.example.adamoslogistic.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adamoslogistic.JsonPlaceHolderAPI;
import com.example.adamoslogistic.R;
import com.example.adamoslogistic.generic.DB;
import com.example.adamoslogistic.generic.Registry;
import com.example.adamoslogistic.models.User;
import com.example.adamoslogistic.requests.Params;
import com.example.adamoslogistic.requests.LoginRequest;
import com.example.adamoslogistic.requests.LoginResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText editTextEmail, editTextPassword;
    Button buttonEnter, buttonRegistration, buttonEnterWithoutLogin;
    TextView textViewCheck;
    ProgressBar progressBar;

    private Handler eventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Registry.InitDB(getBaseContext());
        Registry.baseContext = getBaseContext();

        buttonEnter = findViewById(R.id.button_enter);
        buttonRegistration = findViewById(R.id.button_register);
        buttonEnterWithoutLogin = findViewById(R.id.button_enter_without_login);
        editTextEmail = findViewById(R.id.editText_email);
        editTextPassword = findViewById(R.id.editText_password);
        textViewCheck = findViewById(R.id.button_check);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(ProgressBar.INVISIBLE);
        //Todo: Страница регистрации

        final Context context = this;
        eventHandler = new Handler(msg -> {
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            switch (msg.what) {
                case 0:
                    startActivity(new Intent(context, MainActivity.class));
                    break;
                case 1:
                    Toast.makeText(context, "Неправильный логин или пароль", Toast.LENGTH_LONG)
                            .show();
                    break;
                case 2:
                    Toast.makeText(context, "Ошибка соединения", Toast.LENGTH_LONG)
                            .show();
                    break;
                case 3:
                    Toast.makeText(context, "Внутренняя ошибка", Toast.LENGTH_LONG)
                            .show();
                    break;
            }
            return false;
        });

        try {
            if (DB.GetCurrentUser().ID != -1)
                eventHandler.sendEmptyMessage(0);
            else {
                buttonEnter.setOnClickListener(v -> {
                    progressBar.setVisibility(ProgressBar.VISIBLE);

                    Params params = new Params(editTextEmail.getText().toString(),
                            editTextPassword.getText().toString());
                    LoginRequest lr = new LoginRequest("login", params);

                    new LoginAsync().execute(lr);
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    class LoginAsync extends AsyncTask<LoginRequest, Void, Void> {

        @Override
        protected Void doInBackground(LoginRequest... params) {

            JsonPlaceHolderAPI jsonPlaceHolderAPI = Registry.retrofit.create(JsonPlaceHolderAPI.class);

            try {
                Response<LoginResponse> response = jsonPlaceHolderAPI
                        .Login(params[0])
                        .execute();

                if (response.isSuccessful()) {
                    LoginResponse lr = response.body();
                    Log.d("Test", lr.length);

                    if (lr.ERROR_ID != null)
                        LoginActivity.this.eventHandler.sendEmptyMessage(1);
                    else {
                        DB.SetCurrentUser(new User(lr.result.api_key, "", 1));
                        DB.SetAvatar(encodeTobase64(getBitmapFromURL(lr.result.avatar)));
                        Log.d("MyLog", encodeTobase64(getBitmapFromURL(lr.result.avatar)));
                        LoginActivity.this.eventHandler.sendEmptyMessage(0);
                    }
                } else {
                    LoginActivity.this.eventHandler.sendEmptyMessage(2);
                    Log.d("MyLog", response.message() + "\n" + response.errorBody());
                }

            } catch (IOException | InterruptedException e) {
                LoginActivity.this.eventHandler.sendEmptyMessage(2);
                Log.d("MyLog", e.toString());
            }

            return null;
        }
    }
}