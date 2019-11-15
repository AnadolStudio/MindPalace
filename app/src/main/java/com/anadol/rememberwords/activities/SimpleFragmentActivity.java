package com.anadol.rememberwords.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.fragments.GroupListFragment;
import com.anadol.rememberwords.fragments.MyFragment;

public abstract class SimpleFragmentActivity extends AppCompatActivity {
    private Fragment fragment;

    public Fragment getFragment() {
        return fragment;
    }

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment==null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container,fragment)
                    .commit();
        }
    }


}
