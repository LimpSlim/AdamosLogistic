package com.example.adamoslogistic.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.adamoslogistic.requests.LoginRequest;
import com.example.adamoslogistic.requests.LoginResponse;

import java.io.IOException;

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

                    LoginRequest lr = new LoginRequest(
                            editTextEmail.getText().toString(),
                            editTextPassword.getText().toString());

                    new LoginAsync().execute(lr);
                });
            }
        } catch (InterruptedException e) {
            eventHandler.sendEmptyMessage(3);
        }
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

                    if (lr.ERROR_ID != null)
                        LoginActivity.this.eventHandler.sendEmptyMessage(1);
                    else {
                        DB.SetCurrentUser(new User(lr.api_key, lr.name, lr.id));
                        LoginActivity.this.eventHandler.sendEmptyMessage(0);
                    }
                } else LoginActivity.this.eventHandler.sendEmptyMessage(2);

            } catch (IOException | InterruptedException e) {
                LoginActivity.this.eventHandler.sendEmptyMessage(2);
            }

            return null;
        }
    }
}
