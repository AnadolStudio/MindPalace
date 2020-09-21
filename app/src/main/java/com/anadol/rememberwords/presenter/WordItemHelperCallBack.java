package com.anadol.rememberwords.presenter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class WordItemHelperCallBack extends ItemTouchHelper.Callback {

    private ItemTouchHelperAdapter mHelperAdapter;

    public WordItemHelperCallBack(ItemTouchHelperAdapter helperAdapter) {
        mHelperAdapter = helperAdapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView mRecyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipeFlag = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, swipeFlag);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView mRecyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        mHelperAdapter.onItemDismiss(viewHolder, i);
    }
}
