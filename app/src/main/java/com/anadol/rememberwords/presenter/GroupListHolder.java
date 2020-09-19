package com.anadol.rememberwords.presenter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.activities.GroupDetailActivity;
import com.anadol.rememberwords.view.Fragments.GroupListFragment;
import com.anadol.rememberwords.model.Group;
import com.anadol.rememberwords.model.SimpleParent;

import static com.anadol.rememberwords.view.Fragments.GroupListFragment.REQUIRED_CHANGE;

public class GroupListHolder extends MySimpleHolder implements View.OnClickListener, View.OnLongClickListener {
    private static MyListAdapter<? extends SimpleParent> sAdapter;
    private Group mGroup;
    private TextView mTextView;
    private ImageView mImageView;
    private boolean isSelected;

    public GroupListHolder(@NonNull View itemView, MyListAdapter<? extends SimpleParent> mAdapter) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.text_group);
        mImageView = itemView.findViewById(R.id.image_group);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        sAdapter = mAdapter;
    }

    @Override
    public void onBind(SimpleParent group, boolean isSelected) {
        mGroup = (Group) group;
        mTextView.setText(mGroup.getName());
        this.isSelected = isSelected;
        setDrawable();
    }

    @Override
    public void onClick(View v) {
        int position = getAdapterPosition();
        if (position == -1) return;
        if (sAdapter.isSelectableMode()){
            isSelected = !isSelected;
            sAdapter.putSelectedItem(mGroup.getIdString(), isSelected);
            setDrawable();
        }else {
            startActivity();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!sAdapter.isSelectableMode()) {
            sAdapter.setSelectableMode(true);
//                Тут описана логика смены значка в ActionBar, если выделенны все группы
            isSelected = true;
            sAdapter.putSelectedItem(mGroup.getIdString(), true);
            setDrawable();
            return true;
        }
        return false;
    }

    private void setDrawable() {
        if (isSelected){
            Resources resources = sAdapter.getFragment().getResources();
//                mImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_check));
            // TODO temp
            mImageView.setImageDrawable(new ColorDrawable(resources.getColor(R.color.colorAccent)));
        }else {
            mImageView.setImageDrawable(mGroup.getGroupDrawable());
        }
        if (sAdapter.isSelectableMode()) sAdapter.getFragment().changeSelectableMode(true);
    }

    private void startActivity() {
        GroupListFragment mFragment = sAdapter.getFragment();

        Activity activity = mFragment.getActivity();
        Intent intent = GroupDetailActivity.newIntent(activity, mGroup);
        String nameTranslation = activity.getString(R.string.color_image_translation);
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.
                makeSceneTransitionAnimation(
                        activity,
                        new Pair<View, String>(mImageView, nameTranslation));
        mFragment.startActivityForResult(intent, REQUIRED_CHANGE, activityOptions.toBundle());
    }
}
