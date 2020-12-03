package com.anadol.mindpalace.view.Fragments;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anadol.mindpalace.view.Dialogs.LoadingDialog;
import com.anadol.mindpalace.view.Dialogs.LoadingView;
import com.anadol.mindpalace.view.Dialogs.SortDialog;

public abstract class SimpleFragment extends Fragment implements FragmentAdapter {
    public static final String MODE = "mode";
    public static final int MODE_NORMAL = 0;
    public static final int MODE_SEARCH = 1;
    public static final int MODE_SELECT = 2;
    public static final String SCROLL_POSITION = "scroll_position";
    protected static final int REQUEST_SORT = 103;
    protected static final String KEY_SELECT_ALL = "select_all";
    protected static final String KEY_SELECT_COUNT = "select_count";
    protected static final String KEY_SELECT_LIST = "select_list";
    protected int mode = MODE_NORMAL;
    protected LoadingView mLoadingView;

    @Override
    public void updateUI() {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MODE, mode);
    }

    public int getMode() {
        return mode;
    }

    @Override
    public void changeSelectableMode(boolean selected) {
    }

    @Override
    public Resources myResources() {
        return getResources();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mode = savedInstanceState.getInt(MODE);
        }
    }

    public void showLoadingDialog() {
        mLoadingView = LoadingDialog.view(getFragmentManager());
        mLoadingView.showLoadingIndicator();
    }

    void selectAll(boolean select) {
    }

    public void hideLoadingDialog() {
        if (mLoadingView != null) mLoadingView.hideLoadingIndicator();
    }

    protected void createDialogSort(SimpleFragment fragment, SortDialog.Types type) {
        SortDialog sortDialog = SortDialog.newInstance(type);
        sortDialog.setTargetFragment(fragment, REQUEST_SORT);
        sortDialog.show(getFragmentManager(), SortDialog.class.getName());
    }

}
