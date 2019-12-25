package com.anadol.rememberwords.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anadol.rememberwords.fragments.GroupDetailFragment;
import com.anadol.rememberwords.myList.Group;

import java.util.UUID;


public class GroupDetailActivity extends SimpleFragmentActivity {//будет Pager
    //TODO: Используется для Unify, проверить на надобность
    public static final String GROUPS = "groups";

    public static final String CURRENT_GROUP = "current_group";

    public static final String MY_UUID = "uuid";
    private static final String TAG = "GroupDetailActivity";
    private static final String POSITION = "position";
    private Group mGroup;
    private UUID id;
    private ViewPager viewPager;
    private Toolbar bottomBar;


    public static Intent newIntent(Context context,Group mGroup, int position){
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra(CURRENT_GROUP, mGroup);
        intent.putExtra(POSITION, position);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mGroup = getIntent().getParcelableExtra(CURRENT_GROUP);

        return GroupDetailFragment.newInstance(mGroup);
    }


    @Override
    public void onBackPressed() {
        GroupDetailFragment fragment = (GroupDetailFragment) getFragment();
        Intent intent = fragment.dataIsChanged();
        int i = getIntent().getIntExtra(POSITION,0);
        intent.putExtra(POSITION,i);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CURRENT_GROUP,mGroup);
    }

}
