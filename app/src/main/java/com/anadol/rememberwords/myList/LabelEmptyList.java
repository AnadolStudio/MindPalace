package com.anadol.rememberwords.myList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.TextView;

import com.anadol.rememberwords.R;

public class LabelEmptyList {
    private TextView textTouch;
    private Adapter mAdapter;
    private RecyclerView.Adapter mRecyclerAdapter;

    public LabelEmptyList(Context context, ViewGroup parent, Adapter adapter, @NonNull View.OnClickListener clickListener) {
        mAdapter = adapter;

        View view = LayoutInflater.from(context).inflate(R.layout.list_is_empty,parent,false);

        textTouch = view.findViewById(R.id.text_empty);
//        buttonTouch = view.findViewById(R.id.button_empty);
        parent.addView(view);
    }

    public LabelEmptyList(Context context, ViewGroup parent, RecyclerView.Adapter adapter/*, @NonNull View.OnClickListener clickListener*/) {
        mRecyclerAdapter = adapter;

        View view = LayoutInflater.from(context).inflate(R.layout.list_is_empty,parent,false);

        textTouch = view.findViewById(R.id.text_empty);
        textTouch.setVisibility(View.INVISIBLE);

        parent.addView(view);
    }

    public void update(){
        if (mAdapter != null && !mAdapter.isEmpty()){
            textTouch.setVisibility(View.INVISIBLE);

        }
        if (mAdapter != null && mAdapter.isEmpty()) {
            textTouch.setVisibility(View.VISIBLE);
        }

        if (mRecyclerAdapter != null && mRecyclerAdapter.getItemCount() != 0){
            textTouch.setVisibility(View.INVISIBLE);
        }
        if (mRecyclerAdapter != null && mRecyclerAdapter.getItemCount() == 0){
            textTouch.setVisibility(View.VISIBLE);
        }
    }

    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
    }

    public void setRecyclerAdapter(RecyclerView.Adapter recyclerAdapter) {
        mRecyclerAdapter = recyclerAdapter;
    }

    /* private View CreateView(Context context, @LayoutRes int layout, ViewGroup parent, Adapter adapter,@NonNull View.OnClickListener clickListener) {

        View view = LayoutInflater.from(context).inflate(layout,parent,false);

        textTouch = view.findViewById(R.id.text_empty);
        buttonTouch = view.findViewById(R.id.button_empty);
        buttonTouch.setOnClickListener(clickListener);
    }*/
}
