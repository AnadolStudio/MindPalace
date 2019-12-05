package com.anadol.rememberwords.activities;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;

import com.anadol.rememberwords.fragments.GroupDetailFragment;
import com.anadol.rememberwords.myList.Group;

import java.util.ArrayList;

import static com.anadol.rememberwords.activities.GroupDetailActivity.GROUPS;
import static com.anadol.rememberwords.fragments.GroupDetailFragment.NAMES_ALL_GROUPS;

public class CreateGroupActivity extends SimpleFragmentActivity {

    public static Intent newIntent(Context context, String[] name){
        Intent intent = new Intent(context,CreateGroupActivity.class);
        intent.putExtra(NAMES_ALL_GROUPS,name);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        ArrayList<Group> groups = getIntent().getParcelableArrayListExtra(GROUPS);
        Fragment fragment;
        String[] names = getIntent().getStringArrayExtra(NAMES_ALL_GROUPS);
        if (groups == null) {
            Group group = null;
             fragment = GroupDetailFragment.newInstance(group);// null значит то, что это создание новой группы
        }else {
            fragment = GroupDetailFragment.newInstance(groups);// UNIFY
        }
        return fragment;
    }
}
