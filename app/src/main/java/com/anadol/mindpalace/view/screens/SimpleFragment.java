package com.anadol.mindpalace.view.screens;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anadol.mindpalace.view.adapters.FragmentListAdapter;
import com.anadolstudio.core.dialogs.LoadingView;

public abstract class SimpleFragment extends Fragment implements FragmentListAdapter {
    public static final String MODE = "mode";
    public static final int MODE_NORMAL = 0;
    public static final int MODE_SEARCH = 1;
    public static final int MODE_SELECT = 2;
    public static final String SCROLL_POSITION = "scroll_position";
    protected static final int REQUEST_SORT = 103;
    protected static final String KEY_SELECT_LIST = "select_list";
    protected static final String KEY_SELECT_MODE = "select_mode";

    protected int fragmentsMode = MODE_NORMAL;
    protected LoadingView mLoadingView;

    @Override
    public void updateUI() {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MODE, fragmentsMode);
    }

    @Override
    public void changeSelectableMode(boolean selected) {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            fragmentsMode = savedInstanceState.getInt(MODE);
        }
    }

    public void showLoadingDialog() {
        mLoadingView = LoadingView.Base.Companion.view(getParentFragmentManager());
        mLoadingView.showLoadingIndicator();
    }

    public void hideLoadingDialog() {
        if (mLoadingView != null) mLoadingView.hideLoadingIndicator();
    }

    protected void createDialogSort(SimpleFragment fragment, SortDialog.Types type) {
        SortDialog sortDialog = SortDialog.newInstance(type);
        sortDialog.setTargetFragment(fragment, REQUEST_SORT);
        sortDialog.show(getFragmentManager(), SortDialog.class.getName());
    }

    protected void selectMode(){
        fragmentsMode = MODE_SELECT;
    }
    protected void normalMode(){
        fragmentsMode = MODE_NORMAL;
    }
    protected void searchMode(){
        fragmentsMode = MODE_SEARCH;
    }
}
