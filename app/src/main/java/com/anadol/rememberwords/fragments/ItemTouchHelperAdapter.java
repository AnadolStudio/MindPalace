package com.anadol.rememberwords.fragments;

import android.support.v7.widget.RecyclerView;

public interface ItemTouchHelperAdapter {
    void onItemDismiss(RecyclerView.ViewHolder viewHolder, int flag);
}
