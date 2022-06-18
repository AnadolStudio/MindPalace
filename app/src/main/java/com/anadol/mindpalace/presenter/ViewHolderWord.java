package com.anadol.mindpalace.presenter;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.anadol.mindpalace.model.Group;
import com.anadol.mindpalace.model.SimpleParent;
import com.anadol.mindpalace.R;
import com.anadol.mindpalace.model.Word;
import com.anadol.mindpalace.view.screens.grouplist.GroupListFragment;

public class ViewHolderWord extends MySimpleViewHolder implements View.OnClickListener, View.OnLongClickListener{
    private MyListAdapter<? extends SimpleParent> mAdapter;

    private Word mWord;
    private EditText original;
    private EditText association;
    private EditText translate;
    private TextView countReps;
    private EditText comment; // TODO: в финальной версии его не будет, либо он будет спрятан
    private boolean isSelected;

    public ViewHolderWord(@NonNull View itemView, MyListAdapter<? extends SimpleParent> mAdapter) {
        super(itemView);
        original = itemView.findViewById(R.id.original_editText);
        association = itemView.findViewById(R.id.association_editText);
        translate = itemView.findViewById(R.id.translate_editText);
        countReps = itemView.findViewById(R.id.count_reps);
        //        comment = itemView.findViewById(R.id.comment_editText);
        addListeners();
        this.mAdapter = mAdapter;

    }

    private void addListeners() {
        original.addTextChangedListener(new MyTextWatch(MyTextWatch.ORIGINAL));
        association.addTextChangedListener(new MyTextWatch(MyTextWatch.ASSOCIATION));
        translate.addTextChangedListener(new MyTextWatch(MyTextWatch.TRANSLATE));

        association.setFilters(new InputFilter[]{new MultipleFilter()});
        translate.setFilters(new InputFilter[]{new MultipleFilter()});

        original.setOnClickListener(this);
        association.setOnClickListener(this);
        translate.setOnClickListener(this);

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
            case Group.TYPE_LINK:
                translate.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBind(SimpleParent item, boolean isSelected) {
        typeGroupSettings(mAdapter.getTypeGroup());

        mWord = (Word) item;
        original.setText(mWord.getOriginal());
        association.setText(mWord.getMultiAssociationFormat());
        translate.setText(mWord.getMultiTranslateFormat());
        original.setHint(getHintOriginal(mAdapter.getTypeGroup()));
        association.setHint(R.string.association);
        translate.setHint(getHintTranslate(mAdapter.getTypeGroup()));

        SpannableString info = getStatusAssociation();
        countReps.setText(info);

//        comment.setText(mWord.getComment());
        this.isSelected = isSelected;

        setFocusableEditTexts(!mAdapter.isSelectableMode());
        setDrawable(isSelected);
    }

    private SpannableString getStatusAssociation() {
        Resources resources = mAdapter.getResources();
        String isLearned = isLearned(resources);
        String date = getDate(mWord, resources);

        String s = resources.getString(R.string.reps,
                isLearned, mWord.getCountLearn(), date);

        SpannableString info = new SpannableString(s);

        int colorTimeRepeat = getColorTimeRepeat(resources);
        int colorStatus = getColorStatus(resources);

        int indexDate = s.indexOf(date);
        int indexStatus = s.indexOf(isLearned);
        info.setSpan(new ForegroundColorSpan(colorTimeRepeat), indexDate, indexDate + date.length(), 0);
        info.setSpan(new ForegroundColorSpan(colorStatus), indexStatus, indexStatus + isLearned.length(), 0);
        return info;
    }

    private int getColorStatus(Resources resources) {
        int color;
        if (mWord.getTime() == 0) {
            color = resources.getColor(R.color.colorSecondaryText);
        } else if (mWord.isExam()) {
            color = resources.getColor(R.color.colorLearned);
        } else {
            color = resources.getColor(R.color.colorLearning);
        }

        return color;
    }

    private int getColorTimeRepeat(Resources resources) {
        int colorSpan;
        if (mWord.getTime() != 0 && mWord.isRepeatable()) {
            colorSpan = resources.getColor(R.color.colorReadyRepeat);
        } else {
            colorSpan = resources.getColor(R.color.colorNotReadyRepeat);
        }
        return colorSpan;
    }

    private String isLearned(Resources resources) {
        String isLearned;

        if (mWord.getTime() == 0) {
            isLearned = resources.getString(R.string.not_learned);
        } else if (mWord.isExam()) {
            isLearned = resources.getString(R.string.learned);
        } else {
            isLearned = resources.getString(R.string.learning);
        }
        return isLearned;
    }

    @StringRes
    private int getHintTranslate(int typeGroup) {
        int res;
        switch (typeGroup) {
            default:
            case Group.TYPE_NUMBERS:
            case Group.TYPE_TEXTS:
            case Group.TYPE_LINK:
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
            case Group.TYPE_LINK:
                res = R.string.original;
                break;
        }
        return res;
    }

    private String getDate(Word word, Resources resources) {
        long time = word.getNextRepeatTime();
        Log.i(TAG, "getDate: nextTime " + time);

        TimeZone timeZone = TimeZone.getDefault();
        SimpleDateFormat format = new SimpleDateFormat("d MMM H:mm");
        format.setTimeZone(timeZone);
        if (time != 0) {
            return format.format(time);
        } else {
            return resources.getString(R.string.unknown);
        }
    }


    private void setFocusableEditTexts(boolean b) {

        original.setFocusable(b);
        original.setLongClickable(b);
        original.setFocusableInTouchMode(b);
        original.setCursorVisible(b);

        association.setFocusable(b);
        association.setLongClickable(b);
        association.setFocusableInTouchMode(b);
        association.setCursorVisible(b);

        translate.setFocusable(b);
        translate.setLongClickable(b);
        translate.setFocusableInTouchMode(b);
        translate.setCursorVisible(b);

//        comment.setEnabled(b);
    }

    @Override
    public void itemTouch(int flag) {
        switch (flag) {
            case ItemTouchHelper.START:

                if (!mAdapter.isSelectableMode()) {
                    onLongClick(itemView);
                } else {
                    onClick(itemView);
                }
                break;
            case ItemTouchHelper.END:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (mAdapter.isSelectableMode()) {
            Log.i(TAG, "onClick: ");
            isSelected = !isSelected;
            mAdapter.putSelectedItem(mWord.getUUIDString(), isSelected);
            mAdapter.notifyItemChanged(getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!mAdapter.isSelectableMode()) {
            isSelected = true;
            mAdapter.putSelectedItem(mWord.getUUIDString(), isSelected);
            Log.i(TAG, "onLongClick");
            setFocusableEditTexts(false);
            mAdapter.setSelectableMode(true, getAdapterPosition());
            return true;
        }
        return false;
    }


    private void setDrawable(boolean selected) {
        Resources resources = itemView.getResources();

        if (selected) {
            itemView.setForeground(new ColorDrawable(resources.getColor(R.color.colorSelect)));
        } else {
            itemView.setForeground(null);
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
            GroupListFragment.GroupDetailFragment fragment = (GroupListFragment.GroupDetailFragment) mAdapter.getFragment();
            fragment.updateWordCount();
        }
    }
}
