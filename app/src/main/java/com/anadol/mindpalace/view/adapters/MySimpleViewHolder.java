package com.anadol.mindpalace.view.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anadol.mindpalace.data.group.SimpleParent;

abstract class MySimpleViewHolder extends RecyclerView.ViewHolder {
    public static final String TAG = "MySimpleHolder";

    public MySimpleViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract <T extends SimpleParent> void onBind(T item, boolean isSelected);

    protected void itemTouch(int flag) {
    }
}
