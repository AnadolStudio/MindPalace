package com.anadol.mindpalace.view.screens.groupdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.anadol.mindpalace.R;
import com.anadol.mindpalace.data.group.Group;
import com.anadol.mindpalace.view.screens.main.SimpleFragmentActivity;
import com.anadol.mindpalace.domain.utils.IOnBackPressed;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class GroupDetailActivity extends SimpleFragmentActivity {
    public static final String CURRENT_GROUP = "current_group";

    private static final String TAG = "GroupDetailActivity";
    private static CallBack mCallBack;

    // группы в List, а затем возвращает ее для обновления
    private Group mGroup;

    public static Intent newIntent(Context context, Group mGroup) {
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra(CURRENT_GROUP, mGroup);
        return intent;
    }

    public static Intent newIntent(Context context, Group mGroup, CallBack callBack) {
        mCallBack = callBack;
        return newIntent(context, mGroup);
    }

    @Override
    protected Fragment createFragment() {
        mGroup = getIntent().getParcelableExtra(CURRENT_GROUP);
        return GroupDetailFragment.newInstance(mGroup);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCallBack != null) {
            mCallBack.callBack();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
            Log.i(TAG, "onBackPressed: ");
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CURRENT_GROUP, mGroup);
    }
}
