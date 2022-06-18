package com.anadol.mindpalace.view.screens.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.anadol.mindpalace.R;
import com.anadol.mindpalace.view.screens.main.grouplist.GroupListFragment;
import com.anadol.mindpalace.domain.utils.IOnBackPressed;
import com.anadol.mindpalace.view.screens.main.lessons.SettingsFragment;
import com.anadol.mindpalace.view.screens.main.statistic.StatisticFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String CURRENT_ID = "id";
    private BottomNavigationView bottomNavigationView;
    private int currentId;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_ID, currentId);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind();
        setListeners();

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        } else {
            currentId = savedInstanceState.getInt(CURRENT_ID);
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
            //
            if (currentId != id) {

                currentId = id;
                switch (id) {
                    case R.id.navigation_statistic:
                        return addFragment(StatisticFragment.newInstance());
                    case R.id.navigation_home:
                        return addFragment(GroupListFragment.newInstance());
                    case R.id.navigation_settings:
                        return addFragment(SettingsFragment.newInstance());
                }
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
