package com.example.adamoslogistic.views;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.adamoslogistic.JsonPlaceHolderAPI;
import com.example.adamoslogistic.R;
import com.example.adamoslogistic.adapters.ChatAdapter;
import com.example.adamoslogistic.generic.DB;

import com.example.adamoslogistic.generic.Registry;
import com.example.adamoslogistic.models.Message;
import com.example.adamoslogistic.models.User;
import com.example.adamoslogistic.requests.MessageAddRequest;
import com.example.adamoslogistic.requests.MessageAddResponse;
import com.example.adamoslogistic.requests.MessageGetRequest;
import com.example.adamoslogistic.requests.MessageGetResponse;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private User user;
    private Timer mTimer;
    private RecyclerView recyclerView;
    private EditText chatSendingWindow;

    private Handler eventHandler;
    private ChatAdapter ca;
    private String lastMessage = "";

    private ArrayList<Message> message = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        try {
            user = DB.GetCurrentUser();
            ImageButton send = findViewById(R.id.Send);
            chatSendingWindow = findViewById(R.id.your_message);
            recyclerView = findViewById(R.id.message_view);

            eventHandler = new Handler(msg -> {
                switch (msg.what) {
                    case 0:
                        Message message = new Message();
                        message.time = new SimpleDateFormat("YYYY-MM-dd HH:mm").format(new Date());
                        message.user_id = ChatActivity.this.user.ID;
                        message.value = lastMessage;
                        ca.messages.add(message);
                        ca.notifyDataSetChanged();
                        break;
                    case 1:
                        Toast.makeText(this, "Ошибка сервера", Toast.LENGTH_LONG)
                                .show();
                        break;
                    case 2:
                        Toast.makeText(this, "Ошибка соединения", Toast.LENGTH_LONG)
                                .show();
                        break;
                }
                return false;
            });

            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ca.notifyDataSetChanged();
                    new MessageGetAsync().execute();
                }
            }, 0, 5000);

            ca = new ChatAdapter(this, DB.GetMessagesList());

            recyclerView.setAdapter(ca);
            recyclerView.setLayoutManager(
                    new LinearLayoutManager(
                            this
                    ));

            send.setOnClickListener(v -> {
                MessageAddRequest mar = new MessageAddRequest();
                mar.value = lastMessage = chatSendingWindow.getText().toString();
                new MessageAddAsync().execute(mar);
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class MessageGetAsync extends AsyncTask<Void, Void, Void> {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Void... params) {

            JsonPlaceHolderAPI jsonPlaceHolderAPI = Registry.retrofit.create(JsonPlaceHolderAPI.class);

            try {
                MessageGetRequest mgreq = new MessageGetRequest();
                mgreq.api_key = DB.GetCurrentUser().Api_Key;
                mgreq.order_id = DB.GetCurrentSettings().Order_ID;

                Response<MessageGetResponse> response = jsonPlaceHolderAPI
                        .MessageGet(mgreq)
                        .execute();

                if (response.isSuccessful()) {
                    MessageGetResponse mgr = response.body();

                    if (mgr.ERROR_ID != null)
                        ChatActivity.this.eventHandler.sendEmptyMessage(1);
                    else {
                        DB.SetMessagesList(mgr.data);
                        ChatActivity.this.eventHandler.sendEmptyMessage(0);
                    }
                } else ChatActivity.this.eventHandler.sendEmptyMessage(2);

            } catch (IOException | InterruptedException | ParseException e) {
                ChatActivity.this.eventHandler.sendEmptyMessage(2);
            }

            return null;
        }
    }

    class MessageAddAsync extends AsyncTask<MessageAddRequest, Void, Void> {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(MessageAddRequest... params) {

            JsonPlaceHolderAPI jsonPlaceHolderAPI = Registry.retrofit.create(JsonPlaceHolderAPI.class);

            try {
                params[0].order_id = DB.GetCurrentSettings().Order_ID;
                params[0].api_key = DB.GetCurrentUser().Api_Key;

                Response<MessageAddResponse> response = jsonPlaceHolderAPI
                        .MessageAdd(params[0])
                        .execute();

                if (response.isSuccessful()) {
                    MessageAddResponse mgr = response.body();

                    if (mgr.ERROR_ID != null)
                        ChatActivity.this.eventHandler.sendEmptyMessage(1);
                    else {
                        DB.AddMessage(mgr.data);
                        ChatActivity.this.eventHandler.sendEmptyMessage(0);
                    }
                } else ChatActivity.this.eventHandler.sendEmptyMessage(2);

            } catch (IOException | InterruptedException  e) {
                ChatActivity.this.eventHandler.sendEmptyMessage(2);
            }

            return null;
        }
    }
}
