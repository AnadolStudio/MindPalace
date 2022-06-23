package com.anadol.mindpalace.view.screens.main.grouplist;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.anadol.mindpalace.R;
import com.anadol.mindpalace.data.group.BackgroundSingleton;
import com.anadol.mindpalace.data.group.Group;
import com.anadol.mindpalace.domain.sortusecase.ComparatorFactory;
import com.anadol.mindpalace.domain.utils.recyclerview.RecyclerViewAnimation;
import com.anadol.mindpalace.view.adapters.MyListAdapter;
import com.anadol.mindpalace.domain.utils.recyclerview.SlowGridLayoutManager;
import com.anadol.mindpalace.view.screens.SortDialog;
import com.anadol.mindpalace.domain.utils.IOnBackPressed;
import com.anadol.mindpalace.view.adapters.IStartGroupDetail;
import com.anadol.mindpalace.view.screens.SimpleFragment;
import com.anadol.mindpalace.view.screens.groupdetail.GroupDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

import static android.app.Activity.RESULT_OK;
import static com.anadol.mindpalace.data.group.BackgroundSingleton.DatabaseApiKeys.DELETE_GROUPS;
import static com.anadol.mindpalace.data.group.BackgroundSingleton.DatabaseApiKeys.GET_GROUPS;
import static com.anadol.mindpalace.data.group.BackgroundSingleton.DatabaseApiKeys.INSERT_GROUP;
import static com.anadol.mindpalace.view.screens.SortDialog.ORDER_SORT;
import static com.anadol.mindpalace.view.screens.SortDialog.TYPE_SORT;

public class GroupListFragment extends SimpleFragment implements IOnBackPressed, IStartGroupDetail {
    public static final String CHANGED_ITEM = "changed_item";
    public static final String KEY_GROUP_SAVE = "group_save";
    public static final int REQUIRED_CHANGE = 1;
    private static final String TAG = GroupListFragment.class.getName();
    private static final String KEY_SEARCH_QUERY = "search_query";
    private RecyclerView mRecyclerView;
    private SearchView searchView;
    private FloatingActionButton fab;
    private Toolbar mToolbar;

    private ArrayList<Group> mGroupsList;
    private MyListAdapter<Group> mAdapter;
    private ArrayList<String> selectedStringArray;

    private String searchQuery;
    private boolean selectable;

    private CompositeDisposable mCompositeDisposable;

    public static GroupListFragment newInstance() {
        Bundle args = new Bundle();
        GroupListFragment fragment = new GroupListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveData(outState);
    }

    private void saveData(@NonNull Bundle outState) {
        outState.putBoolean(KEY_SELECT_MODE, mAdapter != null && mAdapter.isSelectableMode());
        outState.putParcelableArrayList(KEY_GROUP_SAVE, mGroupsList);
        selectedStringArray = mAdapter == null ? null : mAdapter.getSelectedStringArray();
        outState.putStringArrayList(KEY_SELECT_LIST, selectedStringArray);
        outState.putString(KEY_SEARCH_QUERY, searchQuery);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        bind(view);
        getData(savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        setListeners();

        if (mGroupsList != null) {
            setupAdapter();
        }

        mRecyclerView.setLayoutManager(createGridLayoutManager());

        return view;
    }

    private void bind(View view) {
        fab = view.findViewById(R.id.fab);
        mRecyclerView = view.findViewById(R.id.recycler);
        mToolbar = view.findViewById(R.id.toolbar);

    }

    private void getData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mGroupsList = savedInstanceState.getParcelableArrayList(KEY_GROUP_SAVE);
            searchQuery = savedInstanceState.getString(KEY_SEARCH_QUERY);
            selectedStringArray = savedInstanceState.getStringArrayList(KEY_SELECT_LIST);
            selectable = savedInstanceState.getBoolean(KEY_SELECT_MODE);
        } else {
            doInBackground(GET_GROUPS);
            searchQuery = "";
            selectable = false;
        }
    }

    private void doInBackground(BackgroundSingleton.DatabaseApiKeys action) {
        GroupBackground mBackground = new GroupBackground();
        mBackground.subscribeToObservable(action);
    }

    private void setListeners() {
        fab.setOnClickListener(v -> createGroup());
        mRecyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            // Движение вниз
            if (oldScrollY < scrollY) {
                fab.hide();
                // Движение вверх
            } else if (oldScrollY > scrollY) {
                fab.show();
            }
        });
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onStart() {
        super.onStart();
        ArrayMap<String, Observable> lastAction = BackgroundSingleton.get(getContext()).getStackActions();
        if (lastAction.size() > 0 && mCompositeDisposable == null) {
            GroupBackground background = new GroupBackground();
            for (int i = lastAction.size() - 1; i >= 0; i--) {
                background.subscribeToObservable(lastAction.keyAt(i));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    private SlowGridLayoutManager createGridLayoutManager() {

        Configuration configuration = getResources().getConfiguration();
        int width = configuration.screenWidthDp;
        Log.i(TAG, "Configuration.screenWidthDp: " + width);

        int countColumn;

        if (width < 599) {
            countColumn = 2;
        } else if (width < 839) {
            countColumn = 4;
        } else {
            countColumn = 6;
        }

        return new SlowGridLayoutManager(getActivity(), countColumn);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu = mToolbar.getMenu();
        super.onCreateOptionsMenu(menu, inflater);
        Log.i(TAG, "onCreateOptionsMenu: " + fragmentsMode);

        switch (fragmentsMode) {
            case MODE_NORMAL:
            case MODE_SEARCH:
                inflater.inflate(R.menu.fragment_group_list, menu);
                MenuItem menuSearch = menu.findItem(R.id.menu_search_list);

                mToolbar.setNavigationIcon(null);

                searchView = (SearchView) menuSearch.getActionView();
                searchView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI); // тоже что и textNoSuggestions
                searchView.setQueryHint(getResources().getString(R.string.search));
                setMenuItemsListeners();

                if (!searchQuery.equals("")) {
                    searchView.setIconified(false); // если поставть false то при повороте НЕ закроет SearchView

                    searchView.setQuery(searchQuery, true);
                }
                break;

            case MODE_SELECT:

                inflater.inflate(R.menu.menu_group_list_selected, menu);
                MenuItem select = menu.findItem(R.id.menu_select_all);

                mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);

                if (mAdapter != null && mAdapter.isAllItemSelected()) {
                    select.setIcon(R.drawable.ic_menu_select_all_on);
                } else if (mAdapter == null) {
                    Log.i(TAG, "onCreateOptionsMenu: mAdapter == null");
                } else {
                    select.setIcon(R.drawable.ic_menu_select_all_off);
                }
                updateCountSelectedItems();
                break;
        }
    }

    private void setMenuItemsListeners() {
        Log.i(TAG, "setMenuItemsListeners");
        searchView.setOnSearchClickListener(v -> {
            searchMode();
        });

        searchView.setOnCloseListener(() -> {
            normalMode();
            setActionBarTitle(getString(R.string.app_name));
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchQuery = s;
                mAdapter.getFilter().filter(searchQuery);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_remove:
                doInBackground(DELETE_GROUPS);
                return true;
            case R.id.menu_select_all:
                boolean selectAllItems = !mAdapter.isAllItemSelected();
                selectAll(selectAllItems);
                return true;
            case R.id.menu_sort:
                createDialogSort(this, SortDialog.Types.GROUP);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSearchView() {
        Log.i(TAG, "updateSearchView");
        CharSequence chars = searchView.getQuery();
        searchView.setQuery("", false);
        searchView.setQuery(chars, false);
    }

    void selectAll(boolean select) {
        mAdapter.setAllItemSelected(select);
        updateActionBarTitle();
    }

    @Override
    public void startGroupDetail(Group group) {
        Log.i(TAG, "startDetailActivity: group UUID " + group.getUUIDString());
        showLoadingDialog();

        Intent intent = GroupDetailActivity.newIntent(getActivity(), group, this::hideLoadingDialog);
        startActivityForResult(intent, REQUIRED_CHANGE);
        searchQuery = "";
        searchView.setQuery(searchQuery, false);
        searchView.onActionViewCollapsed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "Result code: " + resultCode + " RequestCode: " + requestCode);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUIRED_CHANGE:
                int id = data.getIntExtra(CHANGED_ITEM, 0);
                Log.i(TAG, "CHANGED_ITEM equal " + id);
                if (id == 0) {
                    return;
                }

                GroupBackground mBackground = new GroupBackground();
                mBackground.getGroupItem(id);

                break;
            case REQUEST_SORT:
                int type = data.getIntExtra(TYPE_SORT, 0);
                int order = data.getIntExtra(ORDER_SORT, 0);
                Collections.sort(mGroupsList, ComparatorFactory.getComparator(type, order));
                updateSearchView();
                mAdapter.notifyDataSetChanged();
                break;

        }
    }

    @Override
    public boolean onBackPressed() {
        switch (fragmentsMode) {
            case MODE_SEARCH:
                normalMode();
                getActivity().invalidateOptionsMenu();
                setActionBarTitle(getString(R.string.app_name));
                searchView.onActionViewCollapsed();
                return true;
            case MODE_SELECT:
                changeSelectableMode(false);
                return true;
            default:
                return false;
        }
    }

    private void setActionBarTitle(String string) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(string);
    }

    public void changeSelectableMode(boolean selectable) {
        if (selectable) {
            selectMode();
            if (searchView != null && !searchView.isIconified()) {
                searchView.onActionViewCollapsed();
            }
            fab.hide();
        } else {
            mAdapter.setSelectableMode(false);
            normalMode();
            fab.show();
        }
        updateActionBarTitle();
    }
    // Необходим для показа количества выбранных объектов (тут групп)

    private void updateActionBarTitle() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.invalidateOptionsMenu();
        if (fragmentsMode == MODE_SELECT) {
            updateCountSelectedItems();
        } else {
            activity.getSupportActionBar().setTitle(getString(R.string.app_name));
        }
    }

    public void updateCountSelectedItems() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        int selectCount = mAdapter.getCountSelectedItems();
        activity.getSupportActionBar().setTitle(String.valueOf(selectCount));
    }

    private void createGroup() {
        doInBackground(INSERT_GROUP);
    }

    @Override
    public void updateUI() {
        updateActionBarTitle();
    }


    private void setupAdapter() {
        mAdapter = new MyListAdapter<>(getActivity(), GroupListFragment.this, mGroupsList, MyListAdapter.GROUP_HOLDER, selectedStringArray, selectable);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerViewAnimation.addTranslationAnim(mRecyclerView);
    }

    class GroupBackground { // TODO: 06.07.2021 в GroupDetailFragment есть очень похожий внутренний класс. Как это оптимизировать?

        private void subscribeToObservable(String action) {
            this.subscribeToObservable(BackgroundSingleton.DatabaseApiKeys.valueOf(action));
        }

        private void subscribeToObservable(BackgroundSingleton.DatabaseApiKeys action) {

            switch (action) {
                case GET_GROUPS:
                    getGroups();
                    break;
                case GET_GROUP_ITEM:
                    // TODO: 06.07.2021 обязательно ли сохранять lastItem в BackgroundSingleton. Нельзя ли вернуть это значение из IntentExtra ?
                    getGroupItem(BackgroundSingleton.get(getContext()).getLastItem());
                    break;
                case DELETE_GROUPS:
                    deleteGroups();
                    break;
                case INSERT_GROUP:
                    insertGroup();
                    break;
            }
        }

        private void initCompositeDisposable() {
            if (mCompositeDisposable == null) {
                mCompositeDisposable = new CompositeDisposable();
            }
        }

        private void getGroups() {
            initCompositeDisposable();
            Observable<ArrayList<Group>> observable = BackgroundSingleton.get(getContext()).getGroups();
            mCompositeDisposable.add(observable.subscribe(groups -> {
                mGroupsList = groups;
                setupAdapter();
                Log.i(TAG, "GetGroups is done");
            }));
        }

        private void getGroupItem(int itemId) {
            initCompositeDisposable();
            Observable<Group> observable = BackgroundSingleton.get(getContext()).getGroupItem(itemId);
            mCompositeDisposable.add(observable.subscribe(group -> {
                if (group == null) return;

                int position = mAdapter.getAdapterPosition(group.getTableId());
                if (position != -1) {
                    mAdapter.updateItem(position, group);
                    mRecyclerView.smoothScrollToPosition(position);
                }
                if (searchView == null) Log.i(TAG, "onPost: searchVIew == null");
                Log.i(TAG, "GetGroupItem is done");
            }));
        }

        private void insertGroup() {
            initCompositeDisposable();
            Observable<Group> observable = BackgroundSingleton.get(getContext()).insertGroup();
            mCompositeDisposable.add(observable.subscribe(group -> {

                mAdapter.add(0, group);
                mAdapter.notifyItemInserted(0);
                mRecyclerView.smoothScrollToPosition(0);
                Log.i(TAG, "InsertGroup is done");
            }));
        }

        private void deleteGroups() {
            initCompositeDisposable();
            Observable<ArrayList<Group>> observable = BackgroundSingleton.get(getContext()).deleteGroups(mAdapter.getSelectedItems());
            mCompositeDisposable.add(observable.subscribe(groups -> {

                mAdapter.remove(groups);
                Toast.makeText(getContext(), getString(R.string.deleting_was_successful), Toast.LENGTH_SHORT).show();
                updateActionBarTitle();
                Log.i(TAG, "DeleteGroup is done");
            }));
        }
    }

}