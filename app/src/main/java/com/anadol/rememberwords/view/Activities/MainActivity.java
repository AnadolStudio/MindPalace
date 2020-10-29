package com.anadol.rememberwords.view.Activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.view.Fragments.IOnBackPressed;
import com.anadol.rememberwords.view.Fragments.GroupListFragment;
import com.anadol.rememberwords.view.Fragments.SettingsFragment;
import com.anadol.rememberwords.view.Fragments.StatisticFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind();
        setListeners();

        if (savedInstanceState == null){
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }

    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }

    private void bind() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setListeners() {
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();

            switch (id) {
                case R.id.navigation_statistic:
                    return addFragment(StatisticFragment.newInstance());
                case R.id.navigation_home:
                    return addFragment(GroupListFragment.newInstance());
                case R.id.navigation_settings:
                    return addFragment(SettingsFragment.newInstance());
            }

            return false;
        });
    }

    private boolean addFragment(Fragment f) {
        if (f == null) return false;
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(R.id.fragment_container) == null) {
            fm.beginTransaction()
                    .add(R.id.fragment_container, f)
                    .commit();
        } else {
            fm.beginTransaction()
                    .replace(R.id.fragment_container, f)
                    .commit();
        }
        return true;
    }


}