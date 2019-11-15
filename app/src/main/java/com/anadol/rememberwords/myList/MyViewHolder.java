package com.anadol.rememberwords.myList;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyViewHolder extends RecyclerView.ViewHolder{
    private View[] mViews;


    MyViewHolder(LayoutInflater inflater, ViewGroup viewGroup,@LayoutRes int layout) {
        super(inflater.inflate(layout,viewGroup,false));
    }

    public View[] getViews() {
        return mViews;
    }

    public void setViews(View[] views) {
        mViews = views;
    }
}
