package com.anadol.mindpalace.presenter;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.anadol.mindpalace.model.Group;
import com.anadol.mindpalace.model.SimpleParent;
import com.anadol.mindpalace.R;
import com.anadol.mindpalace.view.Fragments.FragmentListAdapter;
import com.anadol.mindpalace.view.Fragments.IStartGroupDetail;

public class ViewHolderGroup extends MySimpleViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private MyListAdapter<? extends SimpleParent> mAdapter;
    private Group mGroup;
    private TextView mTextView;
    private ImageView mImageView;
    private boolean isSelected;

    public ViewHolderGroup(@NonNull View itemView, MyListAdapter<? extends SimpleParent> mAdapter) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.text_group);
        mImageView = itemView.findViewById(R.id.image_group);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        this.mAdapter = mAdapter;
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

        if (mAdapter.isSelectableMode()) {
            isSelected = !isSelected;
            mAdapter.putSelectedItem(mGroup.getUUIDString(), isSelected);
            setDrawable(isSelected);
        } else {
            startDetailActivity();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!mAdapter.isSelectableMode()) {
            isSelected = true;
            mAdapter.putSelectedItem(mGroup.getUUIDString(), true);
            mAdapter.setSelectableMode(true);
            setDrawable(isSelected);
            return true;
        }
        return false;
    }

    private void setDrawable(boolean selected) {
        if (selected) {
            Resources resources = itemView.getResources();
            mImageView.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.check_group, null));
        } else {
            Group.CreatorDrawable.getImage(mImageView, mGroup.getStringDrawable());
        }
    }

    private void startDetailActivity() {
        FragmentListAdapter mFragment = mAdapter.getFragment();

        if (mFragment instanceof IStartGroupDetail){
            ((IStartGroupDetail)mFragment).startGroupDetail(mGroup);
        }else {
            Log.i(TAG, "startDetailActivity: fragment is not implement IStartGroupDetail");
        }
    }
}