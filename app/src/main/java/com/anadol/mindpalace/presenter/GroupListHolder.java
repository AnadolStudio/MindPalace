package com.anadol.mindpalace.presenter;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.anadol.mindpalace.model.Group;
import com.anadol.mindpalace.model.SimpleParent;
import com.anadol.mindpalace.R;
import com.anadol.mindpalace.view.Fragments.GroupListFragment;

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
            isSelected = true;
            sAdapter.putSelectedItem(mGroup.getUUIDString(), true);
            sAdapter.setSelectableMode(true);
            setDrawable(isSelected);
            return true;
        }
        return false;
    }

    private void setDrawable(boolean selected) {
        if (selected) {
            Resources resources = sAdapter.getResources();
            mImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.check_group, null));
        } else {
            mGroup.getImage(mImageView);
        }
    }

    @Override
    public void itemTouch(int flag) {
        switch (flag) {
            case ItemTouchHelper.START:

                break;

            case ItemTouchHelper.END:

                break;
        }
    }

    private void startActivity() {
        GroupListFragment mFragment = (GroupListFragment) sAdapter.getFragment();
        mFragment.startDetailActivity(mGroup);
    }
}