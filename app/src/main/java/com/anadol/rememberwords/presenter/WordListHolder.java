package com.anadol.rememberwords.presenter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.activities.GroupDetailActivity;
import com.anadol.rememberwords.model.Group;
import com.anadol.rememberwords.model.SimpleParent;

import static com.anadol.rememberwords.view.Fragments.GroupListFragment.REQUIRED_CHANGE;

public class WordListHolder extends MySimpleHolder implements View.OnClickListener, View.OnLongClickListener {
    private static MyListAdapter<? extends SimpleParent> sAdapter;
    private Group mGroup;
    private TextView mTextView;
    private ImageView mImageView;
    private boolean isSelected;
    // TODO Group replace Word

    public WordListHolder(@NonNull View itemView, MyListAdapter<? extends SimpleParent> mAdapter) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.text_group);
        mImageView = itemView.findViewById(R.id.image_group);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        sAdapter = mAdapter;
    }

    @Override
    public <T extends SimpleParent> void onBind(T item, boolean isSelected) {
        mGroup = (Group) item;
        mTextView.setText(mGroup.getName());
        mImageView.setImageDrawable(mGroup.getGroupDrawable());
        this.isSelected = isSelected;
    }

    @Override
    public void onClick(View v) {
        int position = getAdapterPosition();
        if (position == -1) return;
        if (sAdapter.isSelectableMode()){
            isSelected = !isSelected;
            sAdapter.putSelectedItem(mGroup.getIdString(),isSelected);

        }else {
            startActivity();
        }
    }

    private void startActivity() {
        Fragment mFragment = sAdapter.getFragment();

        Activity activity = mFragment.getActivity();
        Intent intent = GroupDetailActivity.newIntent(activity, mGroup);
        String nameTranslation = activity.getString(R.string.color_image_translation);
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.
                makeSceneTransitionAnimation(
                        activity,
                        new Pair<View, String>(mImageView, nameTranslation));
        mFragment.startActivityForResult(intent, REQUIRED_CHANGE, activityOptions.toBundle());
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
