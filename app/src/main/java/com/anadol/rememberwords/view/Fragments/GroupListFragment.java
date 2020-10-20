package com.anadol.rememberwords.view.Fragments;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.model.CreatorValues;
import com.anadol.rememberwords.model.DataBaseSchema.Groups;
import com.anadol.rememberwords.model.DataBaseSchema.Words;
import com.anadol.rememberwords.model.Group;
import com.anadol.rememberwords.model.MyCursorWrapper;
import com.anadol.rememberwords.presenter.IdComparator;
import com.anadol.rememberwords.presenter.MyListAdapter;
import com.anadol.rememberwords.presenter.SlowGridLayoutManager;
import com.anadol.rememberwords.view.Activities.GroupDetailActivity;
import com.google.android.material.appbar.AppBarLayout.LayoutParams;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.anadol.rememberwords.presenter.MyAnimations.addTranslationAnim;
import static com.anadol.rememberwords.presenter.MyListAdapter.GROUP_HOLDER;
import static com.anadol.rememberwords.view.Fragments.GroupListFragment.GroupBackground.DELETE_GROUP;
import static com.anadol.rememberwords.view.Fragments.GroupListFragment.GroupBackground.GET_GROUPS;
import static com.anadol.rememberwords.view.Fragments.GroupListFragment.GroupBackground.GET_GROUP_ITEM;
import static com.anadol.rememberwords.view.Fragments.GroupListFragment.GroupBackground.INSERT_GROUP;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupListFragment extends MyFragment implements IOnBackPressed {
    public static final String KEY_SELECT_MODE = "select_mode";
    public static final String CHANGED_ITEM = "changed_item";
    public static final int REQUIRED_CHANGE = 1;
    public static final String KEY_GROUP_SAVE = "group_save";
    private static final String TAG = "GroupListFragment";
    private static final String KEY_SEARCH_QUERY = "search_query";
    private RecyclerView mRecyclerView;
    private SearchView searchView;
    private FloatingActionButton fab;
    private Toolbar mToolbar;

    private ArrayList<Group> mGroupsList;
    private MyListAdapter<Group> mAdapter;
    private ArrayList<String> selectedStringArray;
    private GroupBackground background;

    private String searchQuery;
    private boolean selectable;


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
        outState.putBoolean(KEY_SELECT_MODE, mAdapter.isSelectableMode());
        outState.putParcelableArrayList(KEY_GROUP_SAVE, mGroupsList);
        selectedStringArray = mAdapter.getSelectedStringArray();
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
        setListeners();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

        if (savedInstanceState != null) {
            setupAdapter();
        } else {
            addTranslationAnim(mRecyclerView);
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
            mGroupsList = new ArrayList<>();
            doInBackground(GET_GROUPS);
            searchQuery = "";
            selectable = false;
        }
    }

    private void doInBackground(String insertWord) {
        Log.i(TAG, "doInBackground: " + insertWord);
        background = new GroupBackground();
        background.execute(insertWord);
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
    public void onStop() {
        if (background != null && !background.isCancelled()) {
            background.cancel(false);
            Log.i(TAG, "onStop: doInBackground was canceled");
        }
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu = mToolbar.getMenu();
        super.onCreateOptionsMenu(menu, inflater);
        Log.i(TAG, "onCreateOptionsMenu: " + mode);
        switch (mode) {
            case MODE_NORMAL:
            case MODE_SEARCH:
                setScrollFlags(false);
                inflater.inflate(R.menu.fragment_group_list, menu);
                MenuItem menuSearch = menu.findItem(R.id.menu_search_list);

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
                setScrollFlags(true);

                inflater.inflate(R.menu.menu_group_list_selected, menu);
                MenuItem select = menu.findItem(R.id.menu_select_all);

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

    private void setScrollFlags(boolean isSelected) {
        LayoutParams layoutParams = (LayoutParams) mToolbar.getLayoutParams();
        if (!isSelected) {
            layoutParams.setScrollFlags(LayoutParams.SCROLL_FLAG_SCROLL
                    | LayoutParams.SCROLL_FLAG_SNAP
                    | LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        } else {
            layoutParams.setScrollFlags(LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        }
        mToolbar.setLayoutParams(layoutParams);
    }

    private void setMenuItemsListeners() {
        Log.i(TAG, "setMenuItemsListeners");
        searchView.setOnSearchClickListener(v -> {
            mode = MODE_SEARCH;
//            setActionBarTitle("");
        });

        searchView.setOnCloseListener(() -> {
            mode = MODE_NORMAL;
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
                doInBackground(DELETE_GROUP);
                return true;
            case R.id.menu_select_all:
                boolean selectAllItems = !mAdapter.isAllItemSelected();
                selectAll(selectAllItems);
                return true;
            case R.id.menu_sort_alphabetically:
                Collections.sort(mGroupsList);
                updateSearchView();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_sort_date:
                Collections.sort(mGroupsList, new IdComparator());
                updateSearchView();
                mAdapter.notifyDataSetChanged();
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

    public void startDetailActivity(Group group) {
        showLoadingDialog();

        Intent intent = GroupDetailActivity.newIntent(getActivity(), group, this::hideLoadingDialog);
        startActivityForResult(intent, REQUIRED_CHANGE);
        searchQuery = "";
        searchView.setQuery(searchQuery, false);
        searchView.onActionViewCollapsed();
    }

    @Override
    public boolean onBackPressed() {
        switch (mode) {
            case MODE_SEARCH:
                mode = MODE_NORMAL;
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
            mode = MODE_SELECT;
            if (searchView != null && !searchView.isIconified()) {
                searchView.onActionViewCollapsed();
            }
            fab.hide();
        } else {
            mAdapter.setSelectableMode(false);
            mode = MODE_NORMAL;
            fab.show();
        }
        updateActionBarTitle();
    }

    // Необходим для показа количества выбранных объектов (тут групп)
    private void updateActionBarTitle() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.invalidateOptionsMenu();
        if (mode == MODE_SELECT) {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "Result code: " + resultCode + " RequestCode: " + requestCode);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUIRED_CHANGE:
                //  Возможно группа будет обновлятся всегда, даже если изменения не происходили
                int id = data.getIntExtra(CHANGED_ITEM, 0);
                Log.i(TAG, "CHANGED_ITEM equal " + id);
                if (id == 0) {
                    return;
                }

                background = new GroupBackground();
                background.setGroupId(id);
                background.execute(GET_GROUP_ITEM);

                break;
        }
    }

    private void createGroup() {
        doInBackground(INSERT_GROUP);
    }

    @Override
    public void updateUI() {
        updateActionBarTitle();
    }


    private void setupAdapter() {
//        addAlphaAnim();
        mAdapter = new MyListAdapter<>(GroupListFragment.this, mGroupsList, GROUP_HOLDER, selectedStringArray, selectable);
        mRecyclerView.setAdapter(mAdapter);
    }

    public class GroupBackground extends AsyncTask<String, Void, Boolean> {
        static final String GET_GROUPS = "get_groups";
        static final String GET_GROUP_ITEM = "group_item";
        static final String DELETE_GROUP = "remove_group";
        static final String INSERT_GROUP = "add_group";

        private ArrayList<Group> groupsListToRemove;
        private String command;
        private Group mGroupTemp;
        private int itemId;


        public void setGroupId(int id) {
            itemId = id;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            this.command = strings[0];
            MyCursorWrapper cursor = null;
            try {

                ContentResolver contentResolver = getContext().getContentResolver();
                switch (command) {
                    case GET_GROUPS:
                        cursor = new MyCursorWrapper(
                                contentResolver.query(
                                        Groups.CONTENT_URI,
                                        null, null, null, null));

                        if (cursor.getCount() == 0) {
                            Log.i(TAG, "doIn: нет Объектов");
                            return true;
                        }
                        cursor.moveToFirst();

                        if (!mGroupsList.isEmpty()) mGroupsList.clear();

                        while (!cursor.isAfterLast()) {
                            mGroupsList.add(cursor.getGroup());
                            Log.i(TAG, "Id :" + cursor.getGroup().getTableId());
                            cursor.moveToNext();
                        }

                        return true;

                    case GET_GROUP_ITEM:
                        cursor = new MyCursorWrapper(
                                contentResolver.query(
                                        ContentUris.withAppendedId(
                                                Groups.CONTENT_URI,
                                                itemId),
                                        null, null, null, null));
                        if (cursor.getCount() == 0) {
                            Log.i(TAG, "doIn: нет Объектов");
                            return true;
                        }
                        cursor.moveToFirst();
                        mGroupTemp = cursor.getGroup();
                        return true;

                    case INSERT_GROUP:
                        UUID uuid = UUID.randomUUID();
                        String name = getString(R.string.new_group);

                        String colors = Group.getColorsStringFromInts(Group.getDefaultColors());
                        ContentValues values = CreatorValues.createGroupValues(
                                uuid,
                                name,
                                colors,
                                Group.TYPE_NUMBERS);
                        Uri uri = contentResolver.insert(Groups.CONTENT_URI, values);

                        // Это более правильный метод конвертации long в int
                        Long l = (ContentUris.parseId(uri));
                        int idNewGroup = Integer.valueOf(l.intValue());
                        Log.i(TAG, "_ID new group : " + idNewGroup);
                        mGroupTemp = new Group(idNewGroup, uuid, colors, name, Group.TYPE_NUMBERS);
                        return true;

                    case DELETE_GROUP:
                        groupsListToRemove = mAdapter.getSelectedItems();
                        String uuidString;
                        String nameGroup;
                        for (Group g : groupsListToRemove) {
                            uuidString = g.getUUIDString();
                            nameGroup = g.getName();
                            // Удаляю Группу и слова этой группы
                            contentResolver.delete(
                                    Groups.CONTENT_URI,
                                    Groups.UUID + " = ?",
                                    new String[]{uuidString});
                            contentResolver.delete(
                                    Words.CONTENT_URI,
                                    Groups.UUID + " = ?",
                                    new String[]{nameGroup});
                        }
                        return true;
                }

                if (cursor != null) cursor.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean b) {

            int position;
            if (!b) {
                Log.i(TAG, "onPost: что-то пошло не так");
                return;
            }

            switch (command) {
                case DELETE_GROUP:
                    // Обновленный код
                    mAdapter.remove(groupsListToRemove);
                    Toast.makeText(getContext(), getString(R.string.deleting_was_successful), Toast.LENGTH_SHORT).show();
                    changeSelectableMode(false);
                    break;
                case INSERT_GROUP:
                    mAdapter.add(mGroupTemp);
                    // Скролит к последнему
                    position = mAdapter.getItemCount();
                    mAdapter.notifyItemChanged(position);
                    mRecyclerView.smoothScrollToPosition(position);
                    break;
                case GET_GROUPS:
                    setupAdapter();
                    break;
                case GET_GROUP_ITEM:
                    position = mAdapter.getAdapterPosition(mGroupTemp.getTableId());
                    if (position != -1) {
                        mAdapter.updateItem(position, mGroupTemp);
                        mRecyclerView.smoothScrollToPosition(position);
                    }
                    if (searchView == null) Log.i(TAG, "onPost: searchVIew == null");
                    break;
            }

        }
    }
}