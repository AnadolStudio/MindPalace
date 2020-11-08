package com.anadol.rememberwords.presenter;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.anadol.rememberwords.R;

public class WordItemHelperCallBack extends ItemTouchHelper.Callback {

    private static final String TAG = WordItemHelperCallBack.class.getName();
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

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View item = viewHolder.itemView;

        /*int  color = recyclerView.getResources().getColor(R.color.colorAccent);
        Drawable background = new ColorDrawable(color);

        background.setBounds(item.getLeft(),item.getTop(),item.getRight(),item.getBottom());
        background.draw(c);*/

/*
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            float width = (float) item.getWidth();

//            Log.i(TAG, "onChildDraw: dX "+ dX);
//            Log.i(TAG, "onChildDraw: Math.abs(dX) "+ Math.abs(dX));
            if (Math.abs(dX) < width * 0.5) {
                viewHolder.itemView.setTranslationX(dX);
            }
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
*/
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

    }
}
