package com.anadol.rememberwords.activities;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.fragments.GroupDetailFragment;
import com.anadol.rememberwords.fragments.IOnBackPressed;
import com.anadol.rememberwords.myList.Group;

import java.util.ArrayList;

import static com.anadol.rememberwords.fragments.GroupDetailFragment.NAMES_ALL_GROUPS;

public class CreateGroupActivity extends SimpleFragmentActivity {

    public static Intent newIntent(Context context){
        return new Intent(context,CreateGroupActivity.class);
    }

    public static Intent newIntent(Context context, ArrayList<Group> groupForMerge){
        Intent intent = new Intent(context,CreateGroupActivity.class);
        intent.putExtra(NAMES_ALL_GROUPS, groupForMerge);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Fragment fragment;
        ArrayList<Group> groupsForMerge = getIntent().getParcelableArrayListExtra(NAMES_ALL_GROUPS);
        if (groupsForMerge == null) {
            Group group = null;
            fragment = GroupDetailFragment.newInstance(group);// null значит то, что это создание новой группы
        }else {
            fragment = GroupDetailFragment.newInstance(groupsForMerge);// Merge
        }
        return fragment;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed)fragment).onBackPressed()) {

            super.onBackPressed();
        }
    }
}
