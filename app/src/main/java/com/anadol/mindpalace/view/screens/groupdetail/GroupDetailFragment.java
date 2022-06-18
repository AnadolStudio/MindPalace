package com.anadol.mindpalace.view.screens.groupdetail;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anadol.mindpalace.R;
import com.anadol.mindpalace.domain.sortusecase.ComparatorMaker;
import com.anadol.mindpalace.data.group.BackgroundSingleton;
import com.anadol.mindpalace.data.group.Group;
import com.anadol.mindpalace.data.group.Word;
import com.anadol.mindpalace.view.adapters.MyListAdapter;
import com.anadol.mindpalace.domain.utils.recyclerview.SlowLinearLayoutManager;
import com.anadol.mindpalace.view.adapters.SwipeCallBack;
import com.anadol.mindpalace.view.screens.SortDialog;
import com.anadol.mindpalace.domain.utils.IOnBackPressed;
import com.anadol.mindpalace.view.screens.SimpleFragment;
import com.anadol.mindpalace.view.screens.main.grouplist.GroupListFragment;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

import static android.app.Activity.RESULT_OK;
import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static com.anadol.mindpalace.data.group.BackgroundSingleton.DatabaseApiKeys.DELETE_WORDS;
import static com.anadol.mindpalace.data.group.BackgroundSingleton.DatabaseApiKeys.GET_WORDS;
import static com.anadol.mindpalace.data.group.BackgroundSingleton.DatabaseApiKeys.INSERT_WORD;
import static com.anadol.mindpalace.data.group.BackgroundSingleton.DatabaseApiKeys.SAVE_GROUP_AND_WORDS;
import static com.anadol.mindpalace.data.group.BackgroundSingleton.DatabaseApiKeys.UPDATE_WORD_EXAM;
import static com.anadol.mindpalace.view.screens.SortDialog.ORDER_SORT;
import static com.anadol.mindpalace.view.screens.SortDialog.TYPE_SORT;

public class GroupDetailFragment extends SimpleFragment implements IOnBackPressed {
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
    private AppBarLayout mAppBarLayout;
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

    private LearnStartBottomSheet learnDialog;
    private SettingsBottomSheet settingsDialog;
    private CompositeDisposable mCompositeDisposable;

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
        outState.putBoolean(GroupListFragment.KEY_SELECT_MODE, mAdapter != null && mAdapter.isSelectableMode());
        selectStringArray = mAdapter == null ? null : mAdapter.getSelectedStringArray();
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

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        setListeners();


        if (savedInstanceState != null) {
            setupAdapter();
        }// в противном случае адаптер инициализируется в background.post()
        bindDataWithView();

        return view;
    }

    private void setupAdapter() {
        mAdapter = new MyListAdapter<>(getActivity(), this, mWords, MyListAdapter.WORD_HOLDER, selectStringArray, selectable, mGroup.getType());
        mRecyclerView.setAdapter(mAdapter);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), RecyclerView.VERTICAL);
//        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));

//        mRecyclerView.addItemDecoration(dividerItemDecoration);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeCallBack(mAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void bind(View view) {
        imageView = view.findViewById(R.id.group_color);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        typeGroup = view.findViewById(R.id.type_group);
        countWordsTextView = view.findViewById(R.id.count_text);
        mAppBarLayout = view.findViewById(R.id.appbar_layout);
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
            mGroup = getArguments().getParcelable(GROUP);
            mWords = new ArrayList<>();
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
        KeyboardVisibilityEvent.setEventListener(getActivity(), b -> {
            if (b) {
                mAppBarLayout.setExpanded(false);// Сжать AppBar при исп. клавиатуры
            }
            setVisibleFab(!b);
        });
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setVisibleFab(boolean show) {
        if (fabAdd == null) {
            return;
        }

        if (show) {
            fabAdd.show();
        } else {
            fabAdd.hide();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu = mToolbar.getMenu();
        super.onCreateOptionsMenu(menu, inflater);

        switch (fragmentsMode) {
            case MODE_SELECT:
                inflater.inflate(R.menu.menu_group_detail_selected, menu);

                mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);

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

                mToolbar.setNavigationIcon(null);

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
        MenuItem menuLearn = menu.findItem(R.id.menu_learn);

        boolean visible = getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;
        menuLearn.setVisible(visible);
    }

    private void setMenuItemsListeners() {
        Configuration configuration = getResources().getConfiguration();
        int orientation = configuration.orientation;

        searchView.setOnSearchClickListener(v -> {
            fragmentsMode = MODE_SEARCH;
            if (orientation != ORIENTATION_LANDSCAPE) {
                titleToolbar.setVisibility(View.GONE);
            }
        });

        searchView.setOnCloseListener(() -> {
            fragmentsMode = MODE_NORMAL;
            if (orientation != ORIENTATION_LANDSCAPE) {
                titleToolbar.setVisibility(View.VISIBLE);
            }
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
//                Collections.sort(mWords,new NeverExamComparator());
//                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_select_all:
                boolean selectAllItems = !mAdapter.isAllItemSelected();
                selectAll(selectAllItems);
                return true;
            case R.id.menu_migrate: // На текущий момент этот функционал отсутствует
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

    private void doInBackground(BackgroundSingleton.DatabaseApiKeys action) {
        WordBackground mBackground = new WordBackground();
        mBackground.subscribeToObservable(action);
    }

    @Override
    public void onStart() {
        super.onStart();
        ArrayMap<String, Observable> lastAction = BackgroundSingleton.get(getContext()).getStackActions();
        if (lastAction.size() > 0 && mCompositeDisposable == null) {
            WordBackground mBackground = new WordBackground();
            for (int i = lastAction.size() - 1; i >= 0; i--) {
                mBackground.subscribeToObservable(lastAction.keyAt(i));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (mWords != null && !mWords.isEmpty()) {
            doInBackground(UPDATE_WORD_EXAM);
        }*/
        // TODO_начало ошибки removeEmptyWords (updateWordCount();)
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null) {
            Log.i(TAG, "onDestroy: dispose");
            mCompositeDisposable.dispose();
        }
    }

    void selectAll(boolean select) {
        mAdapter.setAllItemSelected(select);
        updateActionBarTitle();
    }

    @Override
    public boolean onBackPressed() {
        switch (fragmentsMode) {
            case MODE_SEARCH:
                fragmentsMode = MODE_NORMAL;
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

                // TODO ??? mGroup = data.getParcelableExtra(GROUP) ?
                Group newGroup = data.getParcelableExtra(GROUP);
                mGroup = new Group(newGroup);
                updateGroup();
                mAdapter.setTypeGroup(mGroup.getType());
                mAdapter.notifyDataSetChanged();
                break;

            case REQUEST_UPDATE_WORDS:
                doInBackground(UPDATE_WORD_EXAM);
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
        Group.CreatorDrawable.getImage(imageView, mGroup.getStringDrawable());
        titleToolbar.setText(mGroup.getName());
        typeGroup.setText(getString(mGroup.getType()));
    }

    public void changeSelectableMode(boolean selectable) {
        if (selectable) {
            fragmentsMode = MODE_SELECT;
            if (searchView != null && !searchView.isIconified()) {
                searchView.onActionViewCollapsed();
            }
        } else {
            fragmentsMode = MODE_NORMAL;
            mAdapter.setSelectableMode(false);
        }
        Log.i(TAG, "changeSelectableMode");
        updateActionBarTitle();
    }

    public void dataIsChanged() {
        int tableItem = mGroup.getTableId();
        Intent intent = new Intent().putExtra(GroupListFragment.CHANGED_ITEM, tableItem);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setResult(RESULT_OK, intent);
        activity.finish();

    }

    private void createBottomSheetLearnDialog() {
        ArrayList<Word> words;
        if (fragmentsMode == MODE_SELECT) {
            words = removeEmptyWords(mAdapter.getSelectedItems());
        } else {
            words = removeEmptyWords(mWords);
        }
        if (words.size() < LearnStartBottomSheet.MIN_COUNT_WORDS) {
            String s = getString(R.string.min_word_list_size, LearnStartBottomSheet.MIN_COUNT_WORDS);
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
        if (settingsDialog == null || !settingsDialog.isVisible()) {
            settingsDialog = SettingsBottomSheet.newInstance(mGroup);
            settingsDialog.setTargetFragment(this, REQUEST_UPDATE_GROUP);
            settingsDialog.show(getFragmentManager(), SettingsBottomSheet.class.getName());
        }
    }

    private void saveGroup() {
        doInBackground(SAVE_GROUP_AND_WORDS);
    }

    public void updateWordCount() {
        int realCount = removeEmptyWords(mWords).size();
        String stringCount = getString(R.string.associations_count, realCount, mWords.size());
        countWordsTextView.setText(stringCount);
    }

    private void updateActionBarTitle() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.invalidateOptionsMenu();
        switch (fragmentsMode) {

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

    class WordBackground {// TODO: 06.07.2021 в GroupListFragment есть очень похожий внутренний класс. Как это оптимизировать?

        private void subscribeToObservable(String action) {
            this.subscribeToObservable(BackgroundSingleton.DatabaseApiKeys.valueOf(action));
        }

        private void subscribeToObservable(BackgroundSingleton.DatabaseApiKeys action) {

            switch (action) {
                case GET_WORDS:
                    getWords();
                    break;
                case SAVE_GROUP_AND_WORDS:
                    saveGroupAndWords();
                    break;
                case DELETE_WORDS:
                    deleteWords();
                    break;
                case INSERT_WORD:
                    insertWord();
                    break;
                case UPDATE_WORD_EXAM:
                    updateStatusWord();
                    break;
            }
        }

        private void initCompositeDisposable() {
            if (mCompositeDisposable == null) {
                mCompositeDisposable = new CompositeDisposable();
            }
        }

        private void getWords() {
            initCompositeDisposable();
            Log.i(TAG, "getWords");
            showLoadingDialog();
            Observable<ArrayList<Word>> observable = BackgroundSingleton.get(getContext()).getWords(mGroup.getUUIDString());
            mCompositeDisposable.add(observable.subscribe(words -> {
                mWords = words;
                setupAdapter();
                updateUI();
                hideLoadingDialog();
                Log.i(TAG, "GetWords is done");
            }));
        }

        private void saveGroupAndWords() {
            initCompositeDisposable();
            showLoadingDialog();
            Observable<Integer> observable = BackgroundSingleton.get(getContext()).saveGroupAndWords(mGroup, mWords);
            mCompositeDisposable.add(observable.subscribe(integer -> {
                dataIsChanged();
                hideLoadingDialog();
                Log.i(TAG, "SaveGroupAndWords is done");
            }));
        }

        private void insertWord() {
            initCompositeDisposable();
            Observable<Word> observable = BackgroundSingleton.get(getContext()).insertWord(mGroup.getUUIDString());
            mCompositeDisposable.add(observable.subscribe(word -> {
                if (word == null) {
                    return;
                }

                mRecyclerView.smoothScrollToPosition(0);
                mAdapter.add(0, word);
                mAdapter.notifyItemInserted(0);//Добавит ввод в начало листа
                updateUI();
                Log.i(TAG, "InsertWord is done");
            }));
        }

        private void deleteWords() {
            initCompositeDisposable();
            Observable<ArrayList<Word>> observable = BackgroundSingleton.get(getContext()).deleteWords(mAdapter.getSelectedItems());
            mCompositeDisposable.add(observable.subscribe(words -> {
                boolean changeSM = mAdapter.getCountSelectedItems() == mAdapter.getItemCount();
                mAdapter.remove(words);
                Toast.makeText(getContext(), getString(R.string.deleting_was_successful), Toast.LENGTH_SHORT).show();
                if (changeSM) {
                    changeSelectableMode(false);
                }
                updateUI();
                Log.i(TAG, "DeleteWords is done");
            }));
        }

        private void updateStatusWord() {
            initCompositeDisposable();
            Observable<ArrayList<Word>> observable = BackgroundSingleton.get(getContext()).updateWordsExam(mWords);
            mCompositeDisposable.add(observable.subscribe(words -> {
                mWords = words;
                mAdapter.notifyDataSetChanged();
                Log.i(TAG, "UpdateStatusWord is done");
            }));
        }

    }
}
