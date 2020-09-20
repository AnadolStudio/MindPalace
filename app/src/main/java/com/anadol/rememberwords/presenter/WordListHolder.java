package com.anadol.rememberwords.presenter;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.model.SimpleParent;
import com.anadol.rememberwords.model.Word;

public class WordListHolder extends MySimpleHolder implements View.OnClickListener, View.OnLongClickListener {
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

        setEnabledEditTexts(!isSelected);
        setDrawable(isSelected);
    }

    private void setEnabledEditTexts(boolean b) {
        original.setEnabled(b);
        association.setEnabled(b);
        translate.setEnabled(b);
        comment.setEnabled(b);
    }

    @Override
    public void onClick(View v) {
        int position = getAdapterPosition();
        if (position == -1) return;

        if (sAdapter.isSelectableMode()) {
            isSelected = !isSelected;
            sAdapter.putSelectedItem(mWord.getUUIDString(), isSelected);
            setDrawable(isSelected);
        }
    }

    @Override
    public void itemTouch(int flag) {
        switch (flag){
            case ItemTouchHelper.START:

                break;

            case ItemTouchHelper.END:

                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!sAdapter.isSelectableMode()) {
            sAdapter.setSelectableMode(true);
            isSelected = true;
            sAdapter.putSelectedItem(mWord.getUUIDString(), true);
            setDrawable(isSelected);
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

        private RecyclerView.ViewHolder holder;
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
            switch (type) {
                case ORIGINAL:
                    if (!mWord.getOriginal().equals(string)) {
                        mWord.setOriginal(string);
                    }
                    break;
                case TRANSLATE:
                    if (!mWord.getTranslate().equals(string)) {
                        mWord.setTranslate(string);
                    }
                    break;
                case ASSOCIATION:
                    if (!mWord.getAssociation().equals(string)) {
                        mWord.setAssociation(string);
                    }
                    break;
                case COMMENT:
                    if (!mWord.getComment().equals(string)) {
                        mWord.setComment(string);
                    }
                    break;
            }
        }
    }

}
