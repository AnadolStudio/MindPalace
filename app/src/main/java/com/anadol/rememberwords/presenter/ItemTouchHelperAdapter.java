package com.anadol.rememberwords.presenter;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperAdapter {
    void onItemDismiss(RecyclerView.ViewHolder viewHolder, int flag);
}
