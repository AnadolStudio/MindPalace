package com.anadol.rememberwords.presenter;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.model.Group;
import com.anadol.rememberwords.model.SimpleParent;
import com.anadol.rememberwords.model.Word;
import com.anadol.rememberwords.view.Fragments.GroupDetailFragment;

public class WordListHolder extends MySimpleHolder implements View.OnClickListener, View.OnLongClickListener, View.OnFocusChangeListener {
    private static MyListAdapter<? extends SimpleParent> sAdapter;

    private Word mWord;
    private EditText original;
    private EditText association;
    private EditText translate;
    private TextView countReps;
    private EditText comment; // TODO: в финальной версии его не будет, либо он будет спрятан
    private boolean isSelected;

    public WordListHolder(@NonNull View itemView, MyListAdapter<? extends SimpleParent> mAdapter) {
        super(itemView);
        original = itemView.findViewById(R.id.original_editText);
        association = itemView.findViewById(R.id.association_editText);
        translate = itemView.findViewById(R.id.translate_editText);
        countReps = itemView.findViewById(R.id.count_reps);
        //        comment = itemView.findViewById(R.id.comment_editText);
        addListeners();
        sAdapter = mAdapter;

    }

    private void addListeners() {
        original.addTextChangedListener(new MyTextWatch(MyTextWatch.ORIGINAL));
        association.addTextChangedListener(new MyTextWatch(MyTextWatch.ASSOCIATION));
        translate.addTextChangedListener(new MyTextWatch(MyTextWatch.TRANSLATE));

        association.setFilters(new InputFilter[]{new MultipleFilter()});
        translate.setFilters(new InputFilter[]{new MultipleFilter()});

        original.setOnFocusChangeListener(this);
        association.setOnFocusChangeListener(this);
        translate.setOnFocusChangeListener(this);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    private void typeGroupSettings(int typeGroup) {
        switch (typeGroup) {
            case Group.TYPE_NUMBERS:
                original.setHint(R.string.number);
                translate.setVisibility(View.GONE);
                break;
            case Group.TYPE_TEXTS:
                original.setHint(R.string.text);
                translate.setVisibility(View.GONE);
                break;
            case Group.TYPE_DATES:
                original.setHint(R.string.date);
                translate.setHint(R.string.event);
                translate.setVisibility(View.VISIBLE);
                break;

            default:
            case Group.TYPE_BOND:
                translate.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBind(SimpleParent item, boolean isSelected) {
        typeGroupSettings(sAdapter.getTypeGroup());

        mWord = (Word) item;
        original.setText(mWord.getOriginal());
        association.setText(mWord.getMultiAssociationFormat());
        translate.setText(mWord.getMultiTranslateFormat());
        original.setHint(getHintOriginal(sAdapter.getTypeGroup()));
        association.setHint(R.string.association);
        translate.setHint(getHintTranslate(sAdapter.getTypeGroup()));

        countReps.setText(sAdapter.getResources()
                .getQuantityString(
                        R.plurals.reps,
                        mWord.getCountLearn(), mWord.getCountLearn(), getDate(mWord.getTime())));

//        comment.setText(mWord.getComment());
        this.isSelected = isSelected;

        setEnabledEditTexts(!sAdapter.isSelectableMode());
        setDrawable(isSelected);
    }

    @StringRes
    private int getHintTranslate(int typeGroup) {
        int res;
        switch (typeGroup) {
            default:
            case Group.TYPE_NUMBERS:
            case Group.TYPE_TEXTS:
            case Group.TYPE_BOND:
                res = R.string.translate;
                break;

            case Group.TYPE_DATES:
                res = R.string.event;
                break;
        }
        return res;

    }

    @StringRes
    private int getHintOriginal(int typeGroup) {
        int res;
        switch (typeGroup) {
            default:
            case Group.TYPE_NUMBERS:
                res = R.string.number;
                break;
            case Group.TYPE_TEXTS:
                res = R.string.text;
                break;
            case Group.TYPE_DATES:
                res = R.string.date;
                break;
            case Group.TYPE_BOND:
                res = R.string.original;
                break;
        }
        return res;
    }

    private String getDate(long time) {
        TimeZone timeZone = TimeZone.getDefault();
        SimpleDateFormat format = new SimpleDateFormat("d MMM");
        format.setTimeZone(timeZone);

        if (time != 0) {
            return format.format(time);
        } else {
            return sAdapter.getResources().getString(R.string.never);
        }
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
    public boolean onLongClick(View v) {
        if (!sAdapter.isSelectableMode()) {
            isSelected = true;
            sAdapter.putSelectedItem(mWord.getUUIDString(), isSelected);
            Log.i(TAG, "onLongClick");
//            setEnabledEditTexts(false);
            sAdapter.setSelectableMode(true, getAdapterPosition());
            return false;
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            GroupDetailFragment fragment = (GroupDetailFragment) sAdapter.getFragment();
            fragment.editTextOnClick();
        }
    }

    private void setDrawable(boolean selected) {
        Resources resources = sAdapter.getFragment().myResources();
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
