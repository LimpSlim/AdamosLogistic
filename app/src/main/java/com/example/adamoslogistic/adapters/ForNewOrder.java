package com.example.adamoslogistic.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.example.adamoslogistic.R;
import com.example.adamoslogistic.models.AddResponseBodyOrders;
import com.example.adamoslogistic.models.Values;

import java.util.ArrayList;
import java.util.List;

public class ForNewOrder extends RecyclerView.Adapter<ForNewOrder.ViewHolder> {
    private LayoutInflater inflater;
    private List<AddResponseBodyOrders> attributes;
    private Context context;

    public ForNewOrder(Context context, List<AddResponseBodyOrders> attributes) {
        this.attributes = attributes;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.field_for_new_order, parent, false);
        return new ForNewOrder.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForNewOrder.ViewHolder holder, int position) {
        if (position == attributes.size() - 1) {

        }
        holder.attribute_name.setText(attributes.get(position).getAttribute_description());
        if (attributes.get(position).getAttribute_type() == 20) {
            holder.attribute_from_user.setVisibility(View.GONE);
            holder.choose_date.setVisibility(View.GONE);
            holder.spinner_for_user.setVisibility(View.VISIBLE);
            List<Values> values = attributes.get(position).getVALUES();
            List<String> descriptions = new ArrayList<>();
            for (Values value: values) {
                descriptions.add(value.getDescription());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, descriptions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner_for_user.setAdapter(adapter);
        }
        else if (attributes.get(position).getAttribute_type() == 10) {
            holder.attribute_from_user.setVisibility(View.GONE);
            holder.spinner_for_user.setVisibility(View.GONE);
            holder.choose_date.setVisibility(View.VISIBLE);
        }
        else {
            holder.spinner_for_user.setVisibility(View.GONE);
            holder.choose_date.setVisibility(View.GONE);
            holder.attribute_from_user.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return attributes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView attribute_name;
        final EditText attribute_from_user;
        final Spinner spinner_for_user;
        final CalendarView choose_date;

        ViewHolder(View view){
            super(view);
            attribute_name = view.findViewById(R.id.attribute_name);
            attribute_from_user = view.findViewById(R.id.attribute_from_user);
            spinner_for_user = view.findViewById(R.id.spinner_for_user);
            choose_date = view.findViewById(R.id.choose_date);
        }
    }
}