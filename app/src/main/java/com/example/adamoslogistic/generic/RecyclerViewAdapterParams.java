package com.example.adamoslogistic.generic;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecyclerViewAdapterParams {
    public Integer layoutID;
    public String query;
    public List<Pair<String, Integer>> params;
    public Context context;

    @SafeVarargs
    public RecyclerViewAdapterParams(Pair<String, Integer> ... args) {
        params = new ArrayList<>();
        params.addAll(Arrays.asList(args));
    }
}