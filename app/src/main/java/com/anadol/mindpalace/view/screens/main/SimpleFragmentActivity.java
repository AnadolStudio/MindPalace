package com.anadol.mindpalace.view.screens.main;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import com.anadol.mindpalace.R;

public abstract class SimpleFragmentActivity extends AppCompatActivity {
    private Fragment mFragment;

    public Fragment getFragment() {
        return mFragment;
    }

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        mFragment = fm.findFragmentById(R.id.fragment_container);

        addFragment(fm);
    }

    private void addFragment(FragmentManager fm) {
        if (mFragment == null) {
            mFragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, mFragment)
                    .commit();
        }
    }

    protected void replaceFragment(Fragment fragment) {
        if (fragment == null) fragment = createFragment();

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public interface CallBack {
        void callBack();
    }

}
