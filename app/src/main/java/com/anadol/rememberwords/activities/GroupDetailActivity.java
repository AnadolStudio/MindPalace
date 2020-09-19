package com.anadol.rememberwords.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.fragments.GroupDetailFragment;
import com.anadol.rememberwords.fragments.IOnBackPressed;
import com.anadol.rememberwords.model.Group;


public class GroupDetailActivity extends SimpleFragmentActivity {//будет Pager
    public static final String CURRENT_GROUP = "current_group";

    private static final String TAG = "GroupDetailActivity";
    private static final String POSITION = "position"; // При создание Activity запоминавет позицию
    // группы в List, а затем возвращает ее для обновления
    private Group mGroup;


    public static Intent newIntent(Context context, Group mGroup){
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

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed)fragment).onBackPressed()) {
            Intent intent = ((GroupDetailFragment) fragment).dataIsChanged();
            setResult(RESULT_OK, intent);
            super.onBackPressed();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CURRENT_GROUP,mGroup);
    }

}
