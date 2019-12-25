package com.anadol.rememberwords.fragments;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperAdapter {
    void onItemDismiss(RecyclerView.ViewHolder viewHolder, int flag);
}
