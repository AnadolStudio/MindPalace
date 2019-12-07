package com.anadol.rememberwords.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.fragments.GroupDetailFragment;
import com.anadol.rememberwords.fragments.GroupListFragment;
import com.anadol.rememberwords.myList.Group;

import java.util.ArrayList;
import java.util.UUID;

import static com.anadol.rememberwords.fragments.GroupListFragment.CHANGED_ITEM;
import static com.anadol.rememberwords.fragments.GroupListFragment.namesEqual;


public class GroupDetailActivity extends SimpleFragmentActivity {//будет Pager
    //TODO: Используется для Unify, проверить на надобность
    public static final String GROUPS = "groups";

    public static final String CURRENT_GROUP = "current_group";

    public static final String MY_UUID = "uuid";
    private static final String TAG = "GroupDetailActivity";
    private Group mGroup;
    private UUID id;
    private ViewPager viewPager;
    private Toolbar bottomBar;


    public static Intent newIntent(Context context,Group mGroup){
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra(CURRENT_GROUP, mGroup);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mGroup = getIntent().getParcelableExtra(CURRENT_GROUP);

        return GroupDetailFragment.newInstance(mGroup);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        GroupDetailFragment fragment = (GroupDetailFragment) getFragment();
        intent.putExtra(CHANGED_ITEM,fragment.dataIsChanged());
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CURRENT_GROUP,mGroup);
    }

}
