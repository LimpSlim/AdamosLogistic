package com.example.adamoslogistic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adamoslogistic.R;
import com.example.adamoslogistic.generic.DB;
import com.example.adamoslogistic.models.Message;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    public List<Message> messages;
    private int user_id;

    private static int USER_MESSAGE = 1;
    private static int SERVER_MESSAGE = 2;

    public ChatAdapter(Context context, List<Message> messages) throws InterruptedException {
        this.messages = messages;
        this.inflater = LayoutInflater.from(context);
        this.user_id = DB.GetCurrentUser().ID;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == USER_MESSAGE) {
            view = inflater.inflate(R.layout.user_message_item, parent, false);
            return new UserViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.manager_message_item, parent, false);
            return new ServerViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (user_id == messages.get(position).user_id)
            return USER_MESSAGE;
        else
            return SERVER_MESSAGE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == USER_MESSAGE) {
            ((UserViewHolder)holder).setUserMessage(messages.get(position).value, messages.get(position).time);
        } else {
            ((ServerViewHolder)holder).setServerMessage(messages.get(position).value, messages.get(position).time);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView userMessageBox;
        private TextView userTimeBox;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);

            userMessageBox = itemView.findViewById(R.id.user_message_box);
            userTimeBox = itemView.findViewById(R.id.user_time_box);
        }

        private void setUserMessage(String message, String time) {
            userMessageBox.setText(message);
            userTimeBox.setText(time);
        }
    }

    class ServerViewHolder extends RecyclerView.ViewHolder {

        private TextView serverMessageBox;
        private TextView serverTimeBox;

        ServerViewHolder(@NonNull View itemView) {
            super(itemView);

            serverMessageBox = itemView.findViewById(R.id.server_message_box);
            serverTimeBox = itemView.findViewById(R.id.server_time_box);
        }

        private void setServerMessage(String message, String time) {
            serverMessageBox.setText(message);
            serverTimeBox.setText(time);
        }
    }
}
