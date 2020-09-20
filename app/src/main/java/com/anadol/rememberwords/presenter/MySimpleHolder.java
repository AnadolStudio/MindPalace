package com.anadol.rememberwords.presenter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anadol.rememberwords.model.SimpleParent;

abstract class MySimpleHolder extends RecyclerView.ViewHolder {

    public MySimpleHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract <T extends SimpleParent> void onBind(T item, boolean isSelected);

    public abstract void itemTouch(int flag);
}
