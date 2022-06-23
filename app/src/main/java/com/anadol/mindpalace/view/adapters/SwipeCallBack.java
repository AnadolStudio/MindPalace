package com.anadol.mindpalace.view.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeCallBack extends ItemTouchHelper.Callback {

    private final RecyclerViewItemTouch callback;

    public SwipeCallBack(RecyclerViewItemTouch helperAdapter) {
        callback = helperAdapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView mRecyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipeFlag = ItemTouchHelper.START; // TODO можно сделать более гибким через конструктор
        return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, swipeFlag);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView mRecyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder viewHolder1) {

        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        callback.onItemDismiss(viewHolder, i);
    }

}
