package com.anadol.rememberwords.activities;

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

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.fragments.GroupDetailFragment;
import com.anadol.rememberwords.myList.Group;

import java.util.ArrayList;
import java.util.UUID;

import static com.anadol.rememberwords.fragments.GroupListFragment.namesEqual;

public class GroupDetailActivity extends SimpleFragmentActivity {//будет Pager
    public static final String GROUPS = "groups";
    public static final String MY_UUID = "uuid";
    private ArrayList<Group> mGroups;
    private UUID id;
    private ViewPager viewPager;
    private Toolbar bottomBar;


    public static Intent newIntent(Context context, ArrayList<Group> mGroups, UUID id){
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra(GROUPS, mGroups);
        intent.putExtra(MY_UUID,id);
        return intent;
    }

    public String[] getNames(String[] s) {
        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0;i<mGroups.size();i++){
            if (!namesEqual(mGroups.get(i).getName(),s)) {
                arrayList.add(mGroups.get(i).getName());
            }
        }
        String[] names = new String[arrayList.size()];
        names = arrayList.toArray(names);
        return names;
    }

    @Override
    protected Fragment createFragment() {
        Group group = null;
        mGroups = getIntent().getParcelableArrayListExtra(GROUPS);
        id = (UUID) getIntent().getSerializableExtra(MY_UUID);
        for (int i = 0; i < mGroups.size(); i++) {
            if (mGroups.get(i).getId().equals(id)) {
                group = mGroups.get(i);
            }
        }
        return GroupDetailFragment.newInstance(group,getNames(new String[]{group.getName()}));
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(GROUPS,mGroups);
    }

}
