package com.anadol.rememberwords.view.Fragments;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
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
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.activities.LearnStartActivity;
import com.anadol.rememberwords.fragments.ColorPicker;
import com.anadol.rememberwords.fragments.IOnBackPressed;
import com.anadol.rememberwords.model.CreatorValues;
import com.anadol.rememberwords.model.DataBaseSchema.Groups;
import com.anadol.rememberwords.model.DataBaseSchema.Words;
import com.anadol.rememberwords.model.Group;
import com.anadol.rememberwords.model.MyCursorWrapper;
import com.anadol.rememberwords.model.Word;
import com.anadol.rememberwords.presenter.IdComparator;
import com.anadol.rememberwords.presenter.MyListAdapter;
import com.anadol.rememberwords.presenter.SlowLinearLayoutManager;
import com.anadol.rememberwords.presenter.WordItemHelperCallBack;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.anadol.rememberwords.model.Group.NON_COLOR;
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
    public static final String POSITION = "position";
    private static final String TAG = "GroupDetailFragment";
    private static final String DIALOG_COLOR = "color";
    private static final String KEY_SEARCH_QUERY = "search_query";
    private static final int REQUEST_DRAWABLE = 1;
    private static final int REQUEST_MULTI_TRANSLATE = 2;
    private static long backPressed;
    private RecyclerView mRecyclerView;
    private MyListAdapter<Word> mAdapter;
    private Group mGroup;
    private ArrayList<Word> mWords;
    private ImageView imageView;
    private ArrayList<String> selectStringArray;
    private boolean selectable;
    private TextView countWordsTextView;
    private Toolbar toolbar;
    private Snackbar mSnackbar;
    private FloatingActionButton fabAdd;
    private MaterialButton mButtonLearnStart;
    private TextView titleToolbar;
    private SearchView searchView;
    private String searchQuery;
    private WordBackground background;

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
        activity.setSupportActionBar(toolbar);


        if (savedInstanceState != null) {
            setupAdapter();
        }// в противном случае адаптер инициализируется в background.post()
        bindDataWithView();

        return view;
    }

    private void setupAdapter() {
        mAdapter = new MyListAdapter<>(this, mWords, MyListAdapter.WORD_HOLDER, selectStringArray, selectable);
        mRecyclerView.setAdapter(mAdapter);
//        addAnimation();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new WordItemHelperCallBack(mAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void bind(View view) {
        imageView = view.findViewById(R.id.group_color);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        countWordsTextView = view.findViewById(R.id.count_text);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        fabAdd = view.findViewById(R.id.fab_add);
        titleToolbar = view.findViewById(R.id.name_group);
        mButtonLearnStart = view.findViewById(R.id.button_startLearn);
    }

    private void getData(Bundle savedInstanceState) {
        mGroup = getArguments().getParcelable(GROUP);

        if (savedInstanceState != null) {
            mWords = savedInstanceState.getParcelableArrayList(WORD_SAVE);
            selectStringArray = savedInstanceState.getStringArrayList(KEY_SELECT_LIST);
            selectable = savedInstanceState.getBoolean(KEY_SELECT_MODE);
            searchQuery = savedInstanceState.getString(KEY_SEARCH_QUERY);
        } else {
            mWords = new ArrayList<>();
            doInBackground(GET_WORDS);
            selectable = false;
            searchQuery = "";
        }
    }

    private void setListeners() {
        imageView.setOnClickListener(v -> {
            FragmentManager fm = getFragmentManager();
            DialogFragment dialog = ColorPicker.newInstance(mGroup.getColors());
            dialog.setTargetFragment(GroupDetailFragment.this, REQUEST_DRAWABLE);
            dialog.show(fm, DIALOG_COLOR);
        });
        fabAdd.setOnClickListener(v -> {
            createAssociation();
            /*BottomSheetDialogLearnResult settings = BottomSheetDialogLearnResult.newInstance();
            settings.show(getFragmentManager(), "bottom_sheet_dialog_fragment_learn");*/
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
            createLearnStartActivity();
/*
            BottomSheetDialogFragmentLearn settings = BottomSheetDialogFragmentLearn.newInstance();
            settings.show(getFragmentManager(), "bottom_sheet_dialog_fragment_learn");
*/
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

    public void editTextOnClick() {
        setVisibleFab(false);
    }

    private void bindDataWithView() {
        imageView.setImageDrawable(mGroup.getGroupDrawable());

        SlowLinearLayoutManager manager = new SlowLinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        fabAdd.setColorFilter(Color.rgb(255, 255, 255));// TODO это обязательно?
        titleToolbar.setText(mGroup.getName());
        titleToolbar.setSelected(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateWordCount();
    }

    private void addAnimation() {
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {

                        int parent = mRecyclerView.getRight();

                        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                            View v = mRecyclerView.getChildAt(i);
//                                v.setAlpha(0.0f);
                            //TODO протестировать с alpha
                            v.setX(-parent);
                            v.animate().translationX(1.0f)
                                    .setDuration(200)
                                    .setStartDelay(i * 50)
                                    .start();
                            v.animate().setStartDelay(0);//возвращаю дефолтное значение

                            if (i > 15) {
                                break;
                            }
                        }

                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        Log.i(TAG, "Remove OnPreDrawListener");
                        return true;
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO: Буду использовать другую анимацию
//        addTransitionListener();
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
        menu = toolbar.getMenu();
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
                searchView.setQueryHint(getResources().getString(R.string.search));
                setMenuItemsListeners();

                if (!searchQuery.equals("")) {
                    searchView.setIconified(false); // не помню зачем нужен этот метод
                    searchView.setQuery(searchQuery, true);
                }
                break;

        }
    }

    private void setMenuItemsListeners() {
        searchView.setOnSearchClickListener(v -> {
            mode = MODE_SEARCH;
            titleToolbar.setVisibility(View.GONE);
        });

        searchView.setOnCloseListener(() -> {
            mode = MODE_NORMAL;
            titleToolbar.setVisibility(View.VISIBLE);
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
                doInBackground(DELETE_WORDS);
                return true;
            case R.id.menu_add_item:
                createAssociation();
                return true;
            case R.id.menu_learn:
                createLearnStartActivity();
                return true;
            case R.id.menu_sort_alphabetically:
                // TODO сортировка Integer будет проводиться только для группы Числа
                Collections.sort(mWords);
                updateSearchView();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_sort_date:
                Collections.sort(mWords, new IdComparator());
                updateSearchView();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_select_all:
                boolean selectAllItems = !mAdapter.isAllItemSelected();
                selectAll(selectAllItems);
                return true;
            case R.id.menu_migrate:
                //TODO
                Toast.makeText(getContext(),"Migrate", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSearchView() {
        searchView.setQuery("", false);
        searchView.setQuery(searchQuery, false);
    }

    private void createAssociation() {
        doInBackground(INSERT_WORD);
    }

    private void doInBackground(String insertWord) {
        if (insertWord.equals(GET_WORDS)) showLoadingDialog();

        background = new WordBackground();
        background.execute(insertWord);
    }

    void selectAll(boolean select) {
        mAdapter.setAllItemSelected(select);
        updateActionBarTitle();
        mAdapter.notifyDataSetChanged();
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
            case REQUEST_DRAWABLE:
                int[] newColors = data.getIntArrayExtra(ColorPicker.EXTRA_GRADIENT);
                int[] colors = new int[3];
                int i = 0;
                for (int c : newColors) {
                    colors[i] = c;
                    i++;
                }
                while (i != 3) {
                    colors[i] = NON_COLOR;
                    i++;
                }

                mGroup.setColors(colors);
                imageView.setImageDrawable(mGroup.getGroupDrawable());
                break;
            case REQUEST_MULTI_TRANSLATE:
                int p = data.getIntExtra(POSITION, 0);
                mAdapter.notifyItemChanged(p);
                break;
        }
    }

    public void changeSelectableMode(boolean selectable) {
        if (selectable) {
            mode = MODE_SELECT;
/*            if (searchView != null && searchView.isShown()) {
                searchView.onActionViewCollapsed();
            }*/
            fabAdd.hide();
        } else {
            mAdapter.setSelectableMode(false);
            mode = MODE_NORMAL;
            fabAdd.show();
        }
        updateActionBarTitle();
    }

    public void dataIsChanged() {
        int tableItem = mGroup.getTableId();
        Intent intent = new Intent().putExtra(CHANGED_ITEM, tableItem);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setResult(RESULT_OK, intent);
        activity.finish();
    }

    private void createLearnStartActivity() {
        Intent intent;
        ArrayList<Word> words = removeEmptyWords(mWords);
        if (words.size() < 2) {
            String s = getString(R.string.min_word_list_size, 2);
            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
            return;
        }
        showLoadingDialog();

        intent = LearnStartActivity.newIntent(getContext(), mGroup, words, this::hideLoadingDialog);
        startActivity(intent);
    }

    private void saveGroup() {/*
        String name = nameGroup.getText().toString().trim();
        // Групп с именем "" быть не должно
        if (!name.equals(mGroup.getName())) {
            if (name.equals("")) {
                nameGroup.setError(getString(R.string.is_empty));
                return;
            }

        }
        mGroup.setName(name);*/
        //    TODO эта ф-ия перейдет в BottomSheet
        doInBackground(UPDATE_GROUP);
    }

    public void updateWordCount() {
//        String stringCount = getResources().getQuantityString(R.plurals.association_items, mWords.size(), mWords.size());
        int realCount = removeEmptyWords(mWords).size();
        String stringCount = getResources().getString(R.string.associations_count, realCount, mWords.size());
        countWordsTextView.setText(stringCount);
    }

    private void updateActionBarTitle() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.invalidateOptionsMenu();
        if (mode == MODE_SELECT) {
            updateCountSelectedItems();
        } else {
            activity.getSupportActionBar().setTitle(getString(R.string.app_name));
        }
    }

    private void updateCountSelectedItems() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        int selectCount = mAdapter.getCountSelectedItems();
        Log.i(TAG, "updateCountSelectedItems: " + selectCount);
        activity.getSupportActionBar().setTitle(String.valueOf(selectCount));
    }

    @Override
    public void updateUI() {
        updateWordCount();
        updateActionBarTitle();
    }

    private ArrayList<Word> removeEmptyWords(ArrayList<Word> words) {
        // TODO: можно ли улучшить? (Для других типов групп)
        ArrayList<Word> tempList = new ArrayList<>(words);
        for (Word w : words) {
            // Если имеет пустые поля
            if (w.getOriginal().equals("") || w.getTranslate().equals("")
                /*|| w.getAssociation().equals("")*/) {
                tempList.remove(w);
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

            ContentResolver contentResolver = getActivity().getContentResolver();

            try {
                switch (cmd) {
                    case UPDATE_GROUP:
                        ContentValues values = CreatorValues.createGroupValues(mGroup.getUUID(), mGroup.getName(), mGroup.getColorsString(), mGroup.getColors());

                        contentResolver.update(
                                ContentUris.withAppendedId(Groups.CONTENT_URI, mGroup.getTableId()),
                                values,
                                null, null);
                        // Сразу после SAVE_GROUP идёт SAVE_WORDS
                    case UPDATE_WORDS:
                        for (Word word : mWords) {
                            contentResolver.update(Words.CONTENT_URI,
                                    CreatorValues.createWordsValues(word),
                                    Groups.UUID + " = ?",
                                    new String[]{word.getUUIDString()});
                        }
                        return true;

                    case INSERT_WORD:
                        UUID uuid = UUID.randomUUID();
                        original = "";
                        association = "";
                        translate = "";
                        comment = "";

                        Uri uri = contentResolver.insert(
                                Words.CONTENT_URI,
                                CreatorValues.createWordsValues(uuid, mGroup.getUUIDString(), original, translate, association, comment));

                        Long l = (ContentUris.parseId(uri));
                        int idNewWord = Integer.valueOf(l.intValue());
                        Log.i(TAG, "_ID new word : " + idNewWord);

                        mWordTemp = new Word(idNewWord, uuid, original, translate, association, mGroup.getUUIDString(), comment);

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

                            // TODO сортировка будет доступна из меню
                            Collections.sort(words);
                            mWords.addAll(words);
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
                    break;
                case INSERT_WORD:
                    mRecyclerView.smoothScrollToPosition(0);
                    mAdapter.add(0, mWordTemp);
                    mAdapter.notifyItemInserted(0);//Добавит ввод в начало листа
                    updateUI();
                    break;

                case DELETE_WORDS:
                    mAdapter.remove(wordsListToRemove);
                    Toast.makeText(getContext(), getString(R.string.deleting_was_successful), Toast.LENGTH_SHORT).show();
                    changeSelectableMode(false);
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