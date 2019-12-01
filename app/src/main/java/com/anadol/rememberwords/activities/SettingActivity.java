package com.anadol.rememberwords.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.anadol.rememberwords.activities.SimpleFragmentActivity;
import com.anadol.rememberwords.database.LayoutPreference;
import com.anadol.rememberwords.fragments.SettingFragment;

public class SettingActivity extends SimpleFragmentActivity {

    public static Intent newIntent(Context context){
        return new Intent(context,SettingActivity.class);
    }


    @Override
    protected Fragment createFragment() {
        return SettingFragment.newInstance();
    }


    @Override
    public void onBackPressed() {
        onNavigateUp();
    }

    @Override
    public boolean onNavigateUp() {
        setResult(Activity.RESULT_OK);
        return super.onNavigateUp();
    }
}
