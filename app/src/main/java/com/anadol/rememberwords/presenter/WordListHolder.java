package com.anadol.rememberwords.presenter;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.model.SimpleParent;
import com.anadol.rememberwords.model.Word;
import com.anadol.rememberwords.view.Fragments.GroupDetailFragment;

public class WordListHolder extends MySimpleHolder implements View.OnClickListener, View.OnLongClickListener, View.OnFocusChangeListener {
    private static MyListAdapter<? extends SimpleParent> sAdapter;

    private Word mWord;
    private EditText original;
    private EditText association;
    private EditText translate;
    private EditText comment; // TODO: в финальной версии его не будет, либо он будет спрятан
    private boolean isSelected;

    public WordListHolder(@NonNull View itemView, MyListAdapter<? extends SimpleParent> mAdapter) {
        super(itemView);
        original = itemView.findViewById(R.id.original_editText);
        association = itemView.findViewById(R.id.association_editText);
        translate = itemView.findViewById(R.id.translate_editText);
//        comment = itemView.findViewById(R.id.comment_editText);
        addListeners();

        sAdapter = mAdapter;
    }

    private void addListeners() {
        original.addTextChangedListener(new MyTextWatch(MyTextWatch.ORIGINAL));
        association.addTextChangedListener(new MyTextWatch(MyTextWatch.ASSOCIATION));
        translate.addTextChangedListener(new MyTextWatch(MyTextWatch.TRANSLATE));
        translate.setFilters(new InputFilter[]{new TranslateFilter()});

        original.setOnFocusChangeListener(this);
        association.setOnFocusChangeListener(this);
        translate.setOnFocusChangeListener(this);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onBind(SimpleParent item, boolean isSelected) {
        mWord = (Word) item;
        original.setText(mWord.getOriginal());
        association.setText(mWord.getAssociation());
        translate.setText(mWord.getMultiTranslateFormat());
//        comment.setText(mWord.getComment());
        this.isSelected = isSelected;

        setEnabledEditTexts(!sAdapter.isSelectableMode());
        setDrawable(isSelected);
    }

    private void setEnabledEditTexts(boolean b) {
        original.setEnabled(b);
        association.setEnabled(b);
        translate.setEnabled(b);
//        comment.setEnabled(b);
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
        } else {
            GroupDetailFragment fragment = (GroupDetailFragment) sAdapter.getFragment();
            fragment.editTextOnClick();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            GroupDetailFragment fragment = (GroupDetailFragment) sAdapter.getFragment();
            fragment.editTextOnClick();
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
        Resources resources = sAdapter.getFragment().getResources();
        if (selected) {
            itemView.setBackground(new ColorDrawable(resources.getColor(R.color.colorAccent)));
        } else {
            itemView.setBackground(new ColorDrawable(resources.getColor(R.color.colorWhite)));
        }
    }

    public class MyTextWatch implements TextWatcher {
        static final int ORIGINAL = 0;
        static final int TRANSLATE = 1;
        static final int ASSOCIATION = 2;
        static final int COMMENT = 3;

        private int type;

        public MyTextWatch(int type) {
            this.type = type;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String string = s.toString();
            boolean b = false;
            switch (type) {
                case ORIGINAL:
                    if (!mWord.getOriginal().equals(string)) {
                        mWord.setOriginal(string);
                        b = true;
                    }
                    break;
                case TRANSLATE:
                    if (!mWord.getTranslate().equals(string)) {
                        mWord.setTranslate(string);
                        b = true;
                    }
                    break;
                case ASSOCIATION:
                    if (!mWord.getAssociation().equals(string)) {
                        mWord.setAssociation(string);
                        b = true;
                    }
                    break;
                case COMMENT:
                    if (!mWord.getComment().equals(string)) {
                        mWord.setComment(string);
                        b = true;
                    }
                    break;
            }
            if (b) updateWordCount();
        }

        private void updateWordCount() {
            GroupDetailFragment fragment = (GroupDetailFragment) sAdapter.getFragment();
            fragment.updateWordCount();
        }
    }

}
