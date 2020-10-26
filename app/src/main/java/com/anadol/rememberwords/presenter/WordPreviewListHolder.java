package com.anadol.rememberwords.presenter;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.model.SimpleParent;
import com.anadol.rememberwords.model.Word;

public class WordPreviewListHolder extends MySimpleHolder implements View.OnClickListener, View.OnLongClickListener {
    private static MyListAdapter<? extends SimpleParent> sAdapter;

    private Word mWord;
    private TextView original;
    private TextView association;
    private TextView translate;
    private TextView countReps;
    private boolean isSelected;

    public WordPreviewListHolder(@NonNull View itemView, MyListAdapter<? extends SimpleParent> mAdapter) {
        super(itemView);
        original = itemView.findViewById(R.id.original_textView);
        association = itemView.findViewById(R.id.association_textView);
        translate = itemView.findViewById(R.id.translate_textView);
        countReps = itemView.findViewById(R.id.count_reps);
//        comment = itemView.findViewById(R.id.comment_editText);
        addListeners();

        sAdapter = mAdapter;
    }

    private void addListeners() {
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onBind(SimpleParent item, boolean isSelected) {
        mWord = (Word) item;
        original.setText(mWord.getOriginal());
        association.setText(mWord.getMultiAssociationFormat());
        translate.setText(mWord.getMultiTranslateFormat());

        String countRepsString = sAdapter.getResources()
                .getQuantityString(
                        R.plurals.reps,
                        mWord.getCountLearn(),
                        getDate(mWord.getTime()));

        countReps.setText(countRepsString);
//        comment.setText(mWord.getComment());
        this.isSelected = isSelected;

        setDrawable(isSelected);
    }

    private String getDate(long time) {
        TimeZone timeZone = TimeZone.getDefault();
        SimpleDateFormat format = new SimpleDateFormat("DD:MM");
        format.setTimeZone(timeZone);

        if (time != 0) {
            return format.format(time);
        }else {
            return "";
        }
    }

    @Override
    public void itemTouch(int flag) {
        switch (flag) {
            case ItemTouchHelper.START:
            case ItemTouchHelper.END:
                if (!sAdapter.isSelectableMode()) {
                    onLongClick(itemView);
                } else {
                    onClick(itemView);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: ");
        if (sAdapter.isSelectableMode()) {
            isSelected = !isSelected;
            sAdapter.putSelectedItem(mWord.getUUIDString(), isSelected);
            setDrawable(isSelected);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!sAdapter.isSelectableMode()) {
            isSelected = true;
            setDrawable(isSelected);
            sAdapter.putSelectedItem(mWord.getUUIDString(), true);
            sAdapter.setSelectableMode(true, getAdapterPosition());
            return true;
        }
        return false;
    }

    private void setDrawable(boolean selected) {
        Resources resources = sAdapter.getFragment().myResources();
        if (selected) {
            itemView.setBackground(new ColorDrawable(resources.getColor(R.color.colorAccent)));
        } else {
            itemView.setBackground(new ColorDrawable(resources.getColor(R.color.colorWhite)));
        }
    }

}
