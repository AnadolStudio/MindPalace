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

import java.util.ArrayList;

public class LabelEmptyList {
    private TextView textTouch;
    private ArrayList mArrayList;

    public LabelEmptyList(Context context, ViewGroup parent, ArrayList list, @NonNull View.OnClickListener clickListener) {
        mArrayList = list;

        View view = LayoutInflater.from(context).inflate(R.layout.list_is_empty,parent,false);

        textTouch = view.findViewById(R.id.text_empty);
//        buttonTouch = view.findViewById(R.id.button_empty);
        parent.addView(view);
    }

    public LabelEmptyList(Context context, ViewGroup parent, ArrayList list/*, @NonNull View.OnClickListener clickListener*/) {
        mArrayList = list;

        View view = LayoutInflater.from(context).inflate(R.layout.list_is_empty,parent,false);

        textTouch = view.findViewById(R.id.text_empty);
        textTouch.setVisibility(View.INVISIBLE);

        parent.addView(view);
    }

    public void update(){
        if (mArrayList != null && !mArrayList.isEmpty()){
            textTouch.setVisibility(View.INVISIBLE);

        }
        if (mArrayList != null && mArrayList.isEmpty()) {
            textTouch.setVisibility(View.VISIBLE);
        }

    }

}
