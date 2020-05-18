package com.example.adamoslogistic.views.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.adamoslogistic.R;
import com.example.adamoslogistic.generic.DB;
import com.example.adamoslogistic.models.User;

public class UserFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user, container, false);

        TextView name = root.findViewById(R.id.name);
        TextView id = root.findViewById(R.id.id);

        try {
            User curUser = DB.GetCurrentUser();
            name.setText(curUser.Name);
            id.setText(curUser.ID.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return root;
    }
}