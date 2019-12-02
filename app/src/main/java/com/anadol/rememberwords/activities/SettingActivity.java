package com.anadol.rememberwords.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.anadol.rememberwords.database.LayoutPreference;
import com.anadol.rememberwords.fragments.SettingListFragment;

public class SettingActivity extends SimpleFragmentActivity {

    public static Intent newIntent(Context context){
        return new Intent(context,SettingActivity.class);
    }


    @Override
    protected Fragment createFragment() {
        int i = LayoutPreference.getLayoutPreference(this);
        return SettingListFragment.newInstance(i);
    }


    @Override
    public void onBackPressed() {
        onNavigateUp();
    }

    @Override
    public boolean onNavigateUp() {
        return super.onNavigateUp();
    }
}
