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
import androidx.recyclerview.widget.ItemTouchHelper;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.view.Activities.GroupDetailActivity;
import com.anadol.rememberwords.model.Group;
import com.anadol.rememberwords.model.SimpleParent;
import com.anadol.rememberwords.view.Fragments.GroupListFragment;

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
    public void onBind(SimpleParent item, boolean isSelected) {
        mGroup = (Group) item;
        mTextView.setText(mGroup.getName());
        this.isSelected = isSelected;
        setDrawable(isSelected);
    }

    @Override
    public void onClick(View v) {
        int position = getAdapterPosition();
        if (position == -1) return;
        if (sAdapter.isSelectableMode()) {
            isSelected = !isSelected;
            sAdapter.putSelectedItem(mGroup.getUUIDString(), isSelected);
            setDrawable(isSelected);
        } else {
            startActivity();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!sAdapter.isSelectableMode()) {
            sAdapter.setSelectableMode(true);
            isSelected = true;
            sAdapter.putSelectedItem(mGroup.getUUIDString(), true);
            setDrawable(isSelected);
            return true;
        }
        return false;
    }

    private void setDrawable(boolean selected) {
        if (selected) {
            Resources resources = sAdapter.getFragment().getResources();
//                mImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_check));
            // TODO temp
            mImageView.setImageDrawable(new ColorDrawable(resources.getColor(R.color.colorAccent)));
        } else {
            mImageView.setImageDrawable(mGroup.getGroupDrawable());
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

    private void startActivity() {
        GroupListFragment mFragment = (GroupListFragment) sAdapter.getFragment();

        Activity activity = mFragment.getActivity();
        Intent intent = GroupDetailActivity.newIntent(activity, mGroup);
        // TODO хочу другую анимацию
        /*String nameTranslation = activity.getString(R.string.color_image_translation);
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.
                makeSceneTransitionAnimation(
                        activity,
                        new Pair<View, String>(mImageView, nameTranslation));
        mFragment.startActivityForResult(intent, REQUIRED_CHANGE, activityOptions.toBundle());*/
        mFragment.startActivityForResult(intent, REQUIRED_CHANGE);
    }
}
