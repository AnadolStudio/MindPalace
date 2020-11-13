package com.anadol.rememberwords.view.Fragments;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.model.CreatorValues;
import com.anadol.rememberwords.model.DataBaseSchema.Groups;
import com.anadol.rememberwords.model.DataBaseSchema.Words;
import com.anadol.rememberwords.model.Group;
import com.anadol.rememberwords.model.MyCursorWrapper;
import com.anadol.rememberwords.model.SettingsPreference;
import com.anadol.rememberwords.model.Word;
import com.anadol.rememberwords.presenter.ComparatorMaker;
import com.anadol.rememberwords.presenter.MyListAdapter;
import com.anadol.rememberwords.presenter.SlowLinearLayoutManager;
import com.anadol.rememberwords.presenter.UpdateExamWordsBackground;
import com.anadol.rememberwords.presenter.WordItemHelperCallBack;
import com.anadol.rememberwords.view.Dialogs.LearnStartBottomSheet;
import com.anadol.rememberwords.view.Dialogs.SettingsBottomSheet;
import com.anadol.rememberwords.view.Dialogs.SortDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static com.anadol.rememberwords.view.Dialogs.LearnStartBottomSheet.MIN_COUNT_WORDS;
import static com.anadol.rememberwords.view.Dialogs.SortDialog.ORDER_SORT;
import static com.anadol.rememberwords.view.Dialogs.SortDialog.TYPE_SORT;
import static com.anadol.rememberwords.view.Fragments.GroupDetailFragment.WordBackground.DELETE_WORDS;
import static com.anadol.rememberwords.view.Fragments.GroupDetailFragment.WordBackground.GET_WORDS;
import static com.anadol.rememberwords.view.Fragments.GroupDetailFragment.WordBackground.INSERT_WORD;
import static com.anadol.rememberwords.view.Fragments.GroupDetailFragment.WordBackground.UPDATE_GROUP;
import static com.anadol.rememberwords.view.Fragments.GroupListFragment.CHANGED_ITEM;
import static com.anadol.rememberwords.view.Fragments.GroupListFragment.KEY_SELECT_MODE;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupDetailFragment extends MyFragment implements IOnBackPressed {
    public static final String GROUP = "group";
    public static final String WORD_SAVE = "word_save";
    private static final String TAG = GroupDetailFragment.class.getName();
    private static final String KEY_SEARCH_QUERY = "search_query";
    private static final int REQUEST_UPDATE_GROUP = 1;
    private static final int REQUEST_UPDATE_WORDS = 2;
    private static long backPressed;

    private RecyclerView mRecyclerView;
    private ImageView imageView;
    private TextView typeGroup;
    private TextView countWordsTextView;
    private Toolbar mToolbar;
    private FloatingActionButton fabAdd;
    private MaterialButton mButtonLearnStart;
    private TextView titleToolbar;
    private SearchView searchView;

    private MyListAdapter<Word> mAdapter;
    private Group mGroup;
    private ArrayList<Word> mWords;
    private ArrayList<String> selectStringArray;
    private boolean selectable;
    private String searchQuery;

    private WordBackground background;
    private LearnStartBottomSheet learnDialog;

    public static GroupDetailFragment newInstance(Group group) {

        Bundle args = new Bundle();
        args.putParcelable(GROUP, group);
        GroupDetailFragment fragment = new GroupDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveData(outState);
    }

    private void saveData(@NonNull Bundle outState) {
        outState.putParcelable(GROUP, mGroup);
        outState.putParcelableArrayList(WORD_SAVE, mWords);
        outState.putBoolean(KEY_SELECT_MODE, mAdapter.isSelectableMode());
        selectStringArray = mAdapter.getSelectedStringArray();
        outState.putStringArrayList(KEY_SELECT_LIST, selectStringArray);
        outState.putString(KEY_SEARCH_QUERY, searchQuery);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups_detail, container, false);
        bind(view);
        getData(savedInstanceState);
        setListeners();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);


        if (savedInstanceState != null) {
            setupAdapter();
        }// в противном случае адаптер инициализируется в background.post()
        bindDataWithView();

        return view;
    }

    private void setupAdapter() {
        mAdapter = new MyListAdapter<>(getActivity(), this, mWords, MyListAdapter.WORD_HOLDER, selectStringArray, selectable, mGroup.getType());
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), RecyclerView.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));

        mRecyclerView.addItemDecoration(dividerItemDecoration);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new WordItemHelperCallBack(mAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void bind(View view) {
        imageView = view.findViewById(R.id.group_color);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        typeGroup = view.findViewById(R.id.type_group);
        countWordsTextView = view.findViewById(R.id.count_text);
        mToolbar = view.findViewById(R.id.toolbar);
        fabAdd = view.findViewById(R.id.fab_add);
        titleToolbar = view.findViewById(R.id.name_group);
        mButtonLearnStart = view.findViewById(R.id.button_startLearn);
    }

    private void getData(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mWords = savedInstanceState.getParcelableArrayList(WORD_SAVE);
            selectStringArray = savedInstanceState.getStringArrayList(KEY_SELECT_LIST);
            selectable = savedInstanceState.getBoolean(KEY_SELECT_MODE);
            searchQuery = savedInstanceState.getString(KEY_SEARCH_QUERY);
            mGroup = savedInstanceState.getParcelable(GROUP);
        } else {
            mWords = new ArrayList<>();
            mGroup = getArguments().getParcelable(GROUP);
            doInBackground(GET_WORDS);
            selectable = false;
            searchQuery = "";
        }
        updateWordCount();
    }

    private void bindDataWithView() {
        updateGroup();

        SlowLinearLayoutManager manager = new SlowLinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        titleToolbar.setSelected(true);// Чтобы пошла анимация бегущей строки
    }

    private void setListeners() {
        fabAdd.setOnClickListener(v -> {
            createAssociation();
        });
        mRecyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            // Движение вниз
            if (oldScrollY < scrollY) {
                fabAdd.hide();
                // Движение вверх
            } else if (oldScrollY > scrollY) {
                fabAdd.show();
            }
        });
        mButtonLearnStart.setOnClickListener(v -> {
            createBottomSheetLearnDialog();
        });

    }

    private void setVisibleFab(boolean show) {
        if (fabAdd == null) return;

        if (show) {
            fabAdd.show();
        } else {
            fabAdd.hide();
        }
    }

    public void editTextOnClick(boolean visible) {
        // TODO переделать на слушателя поднятия клавиатуры
        setVisibleFab(visible);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWords != null && !mWords.isEmpty()) {
            updateStatusWord();
        }
        // TODO_начало ошибки removeEmptyWords (updateWordCount();)
    }

    private void updateStatusWord() {
        UpdateExamWordsBackground examWordsBackground =
                new UpdateExamWordsBackground(getContext(), mWords, () -> mAdapter.notifyDataSetChanged());
        examWordsBackground.execute();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStop() {
        if (background != null && !background.isCancelled()) {
            background.cancel(false);
            Log.i(TAG, "onStop: background was canceled");
        }
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu = mToolbar.getMenu();
        super.onCreateOptionsMenu(menu, inflater);

        switch (mode) {
            case MODE_SELECT:
                inflater.inflate(R.menu.menu_group_detail_selected, menu);
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

            default: // MODE_NORMAL
                inflater.inflate(R.menu.fragment_group_detail, menu);
                MenuItem menuSearch = menu.findItem(R.id.menu_search_detail);

                searchView = (SearchView) menuSearch.getActionView();
                searchView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI); // тоже что и textNoSuggestions
                searchView.setQueryHint(getResources().getString(R.string.search));
                setMenuItemsListeners();

                if (!searchQuery.equals("")) {
                    searchView.setIconified(false); // если поставть false то при повороте НЕ закроет SearchView
                    searchView.setQuery(searchQuery, true);
                }
                break;

        }
    }

    private void setMenuItemsListeners() {
        Configuration configuration = getResources().getConfiguration();
        int orientation = configuration.orientation;

        searchView.setOnSearchClickListener(v -> {
            mode = MODE_SEARCH;
            if (orientation != ORIENTATION_LANDSCAPE) titleToolbar.setVisibility(View.GONE);
        });

        searchView.setOnCloseListener(() -> {
            mode = MODE_NORMAL;
            if (orientation != ORIENTATION_LANDSCAPE) titleToolbar.setVisibility(View.VISIBLE);
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.i(TAG, "onQueryTextChange");
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
                doInBackground(DELETE_WORDS);
                return true;
            case R.id.menu_add_item:
                createAssociation();
                return true;
            case R.id.menu_learn:
                createBottomSheetLearnDialog();
                return true;
            case R.id.menu_settings:
                createBottomSheetSettingDialog();
                return true;
            case R.id.menu_sort:
                createDialogSort(this, SortDialog.Types.WORD);
                return true;
            case R.id.menu_select_all:
                boolean selectAllItems = !mAdapter.isAllItemSelected();
                selectAll(selectAllItems);
                return true;
            case R.id.menu_migrate:
                //TODO migrate через диалог
                Toast.makeText(getContext(), "Migrate", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSearchView() {
        CharSequence chars = searchView.getQuery();
        searchView.setQuery("", false);
        searchView.setQuery(chars, false);
    }

    private void createAssociation() {
        doInBackground(INSERT_WORD);
    }

    private void doInBackground(String insertWord) {
        if (insertWord.equals(GET_WORDS) | insertWord.equals(UPDATE_GROUP)) showLoadingDialog();

        background = new WordBackground();
        background.execute(insertWord);
    }

    void selectAll(boolean select) {
        mAdapter.setAllItemSelected(select);
        updateActionBarTitle();
    }

    @Override
    public boolean onBackPressed() {
        switch (mode) {
            case MODE_SEARCH:
                mode = MODE_NORMAL;
                getActivity().invalidateOptionsMenu();
                titleToolbar.setVisibility(View.VISIBLE);
                searchView.onActionViewCollapsed();
                return true;
            case MODE_SELECT:
                changeSelectableMode(false);
                return true;
            default:
                if ((backPressed + 2000) > System.currentTimeMillis()) {
                    Log.i(TAG, "backPressed + 2000 = " + (backPressed + 2000));
                    Log.i(TAG, "System.currentTimeMillis() = " + System.currentTimeMillis());
                    saveGroup();
                } else {
                    Toast.makeText(getContext(), getString(R.string.double_click_for_exit), Toast.LENGTH_SHORT).show();
                    backPressed = System.currentTimeMillis();
                }
                return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_UPDATE_GROUP:

                Group newGroup = data.getParcelableExtra(GROUP);
                mGroup = new Group(newGroup);
                updateGroup();
                mAdapter.setTypeGroup(mGroup.getType());
                mAdapter.notifyDataSetChanged();
                break;

            case REQUEST_UPDATE_WORDS:
                updateStatusWord();
                break;

            case REQUEST_SORT:
                int type = data.getIntExtra(TYPE_SORT, 0);
                int order = data.getIntExtra(ORDER_SORT, 0);
                Collections.sort(mWords, ComparatorMaker.getComparator(type, order));
                updateSearchView();
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void updateGroup() {
        mGroup.getImage(imageView);
        titleToolbar.setText(mGroup.getName());
        typeGroup.setText(getString(mGroup.getType()));
    }

    public void changeSelectableMode(boolean selectable) {
        if (selectable) {
            mode = MODE_SELECT;
            if (searchView != null && !searchView.isIconified()) {
                searchView.onActionViewCollapsed();
            }
            fabAdd.hide();
        } else {
            mode = MODE_NORMAL;
            mAdapter.setSelectableMode(false);
            fabAdd.show();
        }
        Log.i(TAG, "changeSelectableMode");
        updateActionBarTitle();
    }

    public void dataIsChanged() {
        int tableItem = mGroup.getTableId();
        Intent intent = new Intent().putExtra(CHANGED_ITEM, tableItem);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setResult(RESULT_OK, intent);
        activity.finish();
    }

    private void createBottomSheetLearnDialog() {
        ArrayList<Word> words;
        if (mode == MODE_SELECT) {
            words = removeEmptyWords(mAdapter.getSelectedItems());
        } else {
            words = removeEmptyWords(mWords);
        }
        if (words.size() < MIN_COUNT_WORDS) {
            String s = getString(R.string.min_word_list_size, MIN_COUNT_WORDS);
            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
            return;
        }
        if (learnDialog == null || !learnDialog.isVisible()) {
            showLoadingDialog();
            learnDialog = LearnStartBottomSheet.newInstance(mGroup.getType(), words);
            learnDialog.setTargetFragment(this, REQUEST_UPDATE_WORDS);
            learnDialog.show(getFragmentManager(), LearnStartBottomSheet.class.getName());
            hideLoadingDialog();
        }
    }

    private void createBottomSheetSettingDialog() {
        SettingsBottomSheet settingsDialog = SettingsBottomSheet.newInstance(mGroup);
        settingsDialog.setTargetFragment(this, REQUEST_UPDATE_GROUP);
        settingsDialog.show(getFragmentManager(), SettingsBottomSheet.class.getName());
    }

    private void saveGroup() {
        doInBackground(UPDATE_GROUP);
    }

    public void updateWordCount() {
        int realCount = removeEmptyWords(mWords).size();
        String stringCount = getResources().getString(R.string.associations_count, realCount, mWords.size());
        countWordsTextView.setText(stringCount);
    }

    private void updateActionBarTitle() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.invalidateOptionsMenu();
        switch (mode) {

            case MODE_SELECT:
                updateCountSelectedItems();
                break;
            default:
                titleToolbar.setText(mGroup.getName());
                break;

        }
    }

    private void updateCountSelectedItems() {
        int selectCount = mAdapter.getCountSelectedItems();
        titleToolbar.setText(String.valueOf(selectCount));
    }

    @Override
    public void updateUI() {
        updateWordCount();
        updateActionBarTitle();
    }

    private ArrayList<Word> removeEmptyWords(ArrayList<Word> words) {
        ArrayList<Word> tempList = new ArrayList<>(words);
        Log.i(TAG, "removeEmptyWords: " + tempList);
        // TODO_тут возникает ошибка
        for (Word w : words) {
            // Если имеет пустые поля
            switch (mGroup.getType()) {
                case Group.TYPE_NUMBERS:
                case Group.TYPE_TEXTS:
                    if (w.getOriginal().equals("") || w.getAssociation().equals("")) {
                        tempList.remove(w);
                    }
                    break;

                default:
                case Group.TYPE_DATES:
                case Group.TYPE_LINK:
                    if (w.getOriginal().equals("") || w.getTranslate().equals("")
                            || w.getAssociation().equals("")) {
                        tempList.remove(w);
                    }
                    break;
            }
        }
        return tempList;
    }

    public class WordBackground extends AsyncTask<String, Void, Boolean> {
        static final String GET_WORDS = "words";
        static final String UPDATE_GROUP = "update_group";
        static final String UPDATE_WORDS = "update_words";
        static final String INSERT_WORD = "add_words";
        static final String DELETE_WORDS = "delete_words";

        private String cmd;
        private ArrayList<Word> wordsListToRemove;
        private Word mWordTemp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            MyCursorWrapper cursor = null;
            cmd = strings[0];

            String original;
            String translate;
            String association;
            String comment;
            int countLearn;
            long time;

            ContentResolver contentResolver = getActivity().getContentResolver();

            try {
                switch (cmd) {
                    case UPDATE_GROUP:
                        ContentValues values = CreatorValues.createGroupValues(
                                mGroup.getUUID(),
                                mGroup.getName(),
                                mGroup.getStringDrawable(),
                                mGroup.getType());

                        contentResolver.update(
                                ContentUris.withAppendedId(Groups.CONTENT_URI, mGroup.getTableId()),
                                values,
                                null, null);
                        // Сразу после SAVE_GROUP идёт SAVE_WORDS
                    case UPDATE_WORDS:
                        for (Word word : mWords) {
                            contentResolver.update(Words.CONTENT_URI,
                                    CreatorValues.createWordsValues(word),
                                    Words.UUID + " = ?",
                                    new String[]{word.getUUIDString()});
                        }
                        return true;

                    case INSERT_WORD:
                        UUID uuid = UUID.randomUUID();
                        original = "";
                        association = "";
                        translate = "";
                        comment = "";
                        countLearn = 0;
                        time = 0;


                        Uri uri = contentResolver.insert(
                                Words.CONTENT_URI,
                                CreatorValues.createWordsValues(
                                        uuid,
                                        mGroup.getUUIDString(),
                                        original,
                                        translate,
                                        association,
                                        comment,
                                        countLearn,
                                        time,
                                        false));

                        Long l = (ContentUris.parseId(uri));
                        int idNewWord = Integer.valueOf(l.intValue());
                        Log.i(TAG, "_ID new word : " + idNewWord);

                        mWordTemp = new Word(
                                idNewWord,
                                uuid,
                                mGroup.getUUID(),
                                original,
                                association,
                                translate,
                                comment,
                                countLearn,
                                time,
                                false);

                        return true;

                    case GET_WORDS:
                        cursor = new MyCursorWrapper(contentResolver.query(
                                Words.CONTENT_URI,
                                null,
                                Words.UUID_GROUP + " = ?",
                                new String[]{mGroup.getUUIDString()}, null));

                        Log.i(TAG, "doInBackground: cursor.getCount() " + cursor.getCount());
                        if (cursor.getCount() != 0) {
                            cursor.moveToFirst();
                            //TODO: реализовать порционную (ленивую) загрузку
                            ArrayList<Word> words = new ArrayList<>();
                            while (!cursor.isAfterLast()) {
                                words.add(cursor.getWord());
                                cursor.moveToNext();
                            }
                            mWords.addAll(words);
                            Collections.sort(mWords,
                                    ComparatorMaker.getComparator(
                                            SettingsPreference.getWordTypeSort(getContext()),
                                            SettingsPreference.getWordOrderSort(getContext())));
                        }
                        return true;

                    case DELETE_WORDS:
                        wordsListToRemove = mAdapter.getSelectedItems();
                        String uuidString;
                        for (Word w : wordsListToRemove) {
                            uuidString = w.getUUIDString();

                            contentResolver.delete(Words.CONTENT_URI,
                                    Groups.UUID + " = ?",
                                    new String[]{uuidString});
                        }
                        return true;
                }
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (!b) {
                Log.i(TAG, "onPost: что-то пошло не так");
                hideLoadingDialog();
                return;
            }
            switch (cmd) {
                case UPDATE_GROUP:
                    dataIsChanged();
                    hideLoadingDialog();
                    break;
                case INSERT_WORD:
                    mRecyclerView.smoothScrollToPosition(0);
                    mAdapter.add(0, mWordTemp);
                    mAdapter.notifyItemInserted(0);//Добавит ввод в начало листа
                    updateUI();
                    break;

                case DELETE_WORDS:
                    boolean changeSM = mAdapter.getItemCount() == mAdapter.getItemCount();
                    mAdapter.remove(wordsListToRemove);
                    Toast.makeText(getContext(), getString(R.string.deleting_was_successful), Toast.LENGTH_SHORT).show();
                    if (changeSM) {
                        changeSelectableMode(false);
                    }
                    updateUI();
                    break;

                case GET_WORDS:
                    setupAdapter();
                    updateUI();
                    hideLoadingDialog();
                    break;

            }
        }
    }
}