package com.example.adamoslogistic.generic;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    protected OnItemListener mOnItemListener;

    protected Integer layoutID;

    public List<HashMap<String, String>> extracted_data = new ArrayList<>();
    protected List<Pair<String, Integer>> columns;

    public RecyclerViewAdapter(@NonNull RecyclerViewAdapterParams params, OnItemListener on_item_listener) throws InterruptedException {
        this.mOnItemListener = on_item_listener;
        this.layoutID = params.layoutID;
        this.columns = params.params;
        dbRequest(params);
    }

    private void dbRequest(@NonNull RecyclerViewAdapterParams params) throws InterruptedException {
        Registry.DB_Connections.acquire();
        @SuppressLint("Recycle") Cursor result = Registry.db.rawQuery(params.query, null);
        Registry.DB_Connections.release();

        final String[] ColumnNames = result.getColumnNames();

        if (result.moveToFirst()) {
            int index = 0;
            do {
                extracted_data.add(new HashMap<>());
                for (String column : ColumnNames) {
                    extracted_data.get(index).put(
                            column,
                            result.getString(
                                    result.getColumnIndex(column)
                            )
                    );
                }
                index++;
            } while (result.moveToNext());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutID, parent, false);
        return new ViewHolder(view, mOnItemListener, columns);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        HashMap<String, String> row = extracted_data.get(position);

        for (Map.Entry<String, String> pair : row.entrySet()) {
            holder.Fields.put(pair.getKey(), pair.getValue());
            if (holder.Controls.containsKey(pair.getKey())){
                if (pair.getKey().equals("kostyl")) continue;
                ((TextView) holder.Controls.get(pair.getKey())).setText(pair.getValue());
            }
        }
    }

    @Override
    public int getItemCount() {
        return extracted_data.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnItemListener onItemListener;
        public Map<String, View> Controls = new HashMap<>();
        protected Map<String, String> Fields = new HashMap<>();

        public ViewHolder(View view, OnItemListener onItemListener, @NonNull List<Pair<String, Integer>> params) {
            super(view);
            this.onItemListener = onItemListener;
            view.setOnClickListener(this);

            for (int i = 0; i < params.size(); i++) {
                Controls.put(
                        params.get(i).first,
                        view.findViewById(params.get(i).second)
                );
            }
        }

        @Override
        public void onClick(View v) {
            try {
                onItemListener.onItemClick(getAdapterPosition());
            } catch (NoSuchFieldException | IllegalAccessException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnItemListener {
        void onItemClick(int position) throws NoSuchFieldException, IllegalAccessException, InterruptedException;
    }
}