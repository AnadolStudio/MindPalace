package com.anadol.rememberwords.fragments;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.activities.LearnDetailActivity;
import com.anadol.rememberwords.model.Group;
import com.anadol.rememberwords.model.Word;
import com.anadol.rememberwords.presenter.ItemTouchHelperAdapter;
import com.anadol.rememberwords.view.Fragments.MyFragment;

import java.util.ArrayList;
import java.util.Random;

import static com.anadol.rememberwords.view.Fragments.GroupListFragment.KEY_SELECT_MODE;


/**
 * A simple {@link Fragment} subclass.
 */
public class LearnStartFragment extends MyFragment implements View.OnClickListener, IOnBackPressed {
    public static final String GROUP = "group";
    public static final String WORDS = "words";
    public static final String TYPE = "type";
    public static final String OBJECT = "object";
    public static final String USE = "use";
    public static final String WORD_SELECTED = "word_selected";
    public static final int QUIZ = 0;
    public static final int TRUE_FALSE = 1;
    public static final int ANSWER_QUESTION = 2;
    public static final int ORIGINAL = 0;
    public static final int TRANSLATE = 1;
    public static final int TRANSCRIPT = 2;
    private static final String TAG = "learn_start";
    private static final int ALL = 0;
    private static final int SELECTED = 1;
    private static final int DIAPASON = 2;
    private static final int RANDOM = 3;

    private Group mGroup;
    private ArrayList<Word> mWords;

    private ViewPager mViewPagerType;
    private ViewPager mViewPagerObject;


    private Button startButton;
    private Spinner mSpinner;

    private int type;
    private int object;
    private RecyclerView mRecyclerView;
    private WordAdapter mAdapter;
    private EditText mCountWords;
    private TextView countWords;
    private boolean selectAll;
    private int selectCount;
    private ArrayList<String> selectArray;
    private boolean selectable = false;

    public static LearnStartFragment newInstance(Group group, ArrayList<Word> mWords) {

        Bundle args = new Bundle();
        // TODO: В данный момент mGroup нужна только для того, чтобы передавать свое имя
        args.putParcelable(GROUP, group);
        args.putParcelableArrayList(WORDS, mWords);
        LearnStartFragment fragment = new LearnStartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TYPE, type);
        outState.putInt(OBJECT, object);
        outState.putBoolean(KEY_SELECT_MODE, mAdapter.isSelectable);

        selectArray.clear();

        for (int i = 0; i < mAdapter.mSelectionsArray.size(); i++) {
            if (mAdapter.mSelectionsArray.valueAt(i)) {
                selectArray.add(mAdapter.mSelectionsArray.keyAt(i));
            }
        }
        Log.i(TAG, "onSaveInstanceState: " + selectArray.size());
        outState.putStringArrayList(KEY_SELECT_LIST, selectArray);

        outState.putInt(KEY_SELECT_COUNT, selectCount);
        outState.putBoolean(KEY_SELECT_ALL, selectAll);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_learn_start, container, false);

        mGroup = getArguments().getParcelable(GROUP);// возможно нигде не понадобиться
        mWords = getArguments().getParcelableArrayList(WORDS);

        if (savedInstanceState != null) {
            /*type = savedInstanceState.getInt(TYPE);*/
            object = savedInstanceState.getInt(OBJECT);
            selectable = savedInstanceState.getBoolean(KEY_SELECT_MODE);
            selectArray = savedInstanceState.getStringArrayList(KEY_SELECT_LIST);
            selectCount = savedInstanceState.getInt(KEY_SELECT_COUNT);
            selectAll = savedInstanceState.getBoolean(KEY_SELECT_ALL);

        } else {
            type = TRUE_FALSE;
            object = ORIGINAL;
            selectArray = new ArrayList<>();
            selectCount = 0;

            selectAll = false;
        }

        inflateMyView(view);

        return view;
    }

    private void inflateMyView(View view) {
//      System.out.println(mWords.size());

        mSpinner = view.findViewById(R.id.spinner);

        TextView textView = view.findViewById(R.id.type_test);
        textView.requestFocus();

        mViewPagerType = view.findViewById(R.id.view_pager_type);
        mViewPagerType.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return CardTypeTestFragment.newInstance(i, mWords.size());
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        mViewPagerType.setOffscreenPageLimit(5);

        mViewPagerObject = view.findViewById(R.id.view_pager_object);
        mViewPagerObject.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return CardObjectTestFragment.newInstance(i, mWords);
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        mViewPagerObject.setOffscreenPageLimit(5);

        startButton = view.findViewById(R.id.start_button);
        mCountWords = view.findViewById(R.id.count_word_edit_text);
        setListeners();

        /*if (!isSelectMode()) {
            updateOptions(mWords);
        }*/

        FrameLayout frameLayout = view.findViewById(R.id.recycler_container);
        mRecyclerView = frameLayout.findViewById(R.id.recycler_detail);

        mAdapter = new WordAdapter(mWords);
        mAdapter.setSelectable(selectable);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        registerForContextMenu(mRecyclerView);
        mRecyclerView.setLongClickable(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new WordItemHelperCallBack(mAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        if (mSpinner.getSelectedItemPosition() == 0 || mSpinner.getSelectedItemPosition() == 1) {
            mCountWords.setEnabled(false);
        }
        countWords = view.findViewById(R.id.count_text);
        String stringCount = getResources().getQuantityString(R.plurals.association_items, mWords.size(), mWords.size());
        countWords.setText(getResources().getString(R.string.word_count, stringCount));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateActionBarTitle(mAdapter.isSelectable);
    }

    @Override
    public boolean onBackPressed() {
        switch (mode) {

            case MODE_SELECT:
                mode = MODE_NORMAL;
                modeSelectedTurnOff(-1);
                mAdapter.mSelectionsArray.clear();
                for (int i = 0; i < mAdapter.getList().size(); i++) {
                    Word word = mAdapter.getList().get(i);
                    mAdapter.mSelectionsArray.put(word.getUUIDString(), false);
                }
                return true;
            default:
                return false;
        }
    }

    private void modeSelectedTurnOff(int position) {
        mAdapter.setSelectable(false);
        mAdapter.notifyDataSetChanged();
        updateActionBarTitle(false);
        selectCount = 0;
        if (position == -1) position = ALL;
        mSpinner.setSelection(position);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (mode == MODE_SELECT) {
            inflater.inflate(R.menu.menu_group_list_selected, menu);
            MenuItem item = menu.findItem(R.id.menu_select_all);

            MenuItem remove = menu.findItem(R.id.menu_remove);
            remove.setVisible(false);

            if (selectAll) {
                item.setIcon(R.drawable.ic_menu_select_all_on);
            } else {
                item.setIcon(R.drawable.ic_menu_select_all_off);
            }
            updateActionBarTitle(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_select_all:
                selectAll = !selectAll;
                mAdapter.mSelectionsArray.clear();

                if (selectAll) {
                    for (int i = 0; i < mAdapter.getList().size(); i++) {
                        Word word = mAdapter.getList().get(i);
                        mAdapter.mSelectionsArray.put(word.getUUIDString(), true);
                    }
                    selectCount = mAdapter.getList().size();
                } else {
                    for (int i = 0; i < mAdapter.getList().size(); i++) {
                        Word word = mAdapter.getList().get(i);
                        mAdapter.mSelectionsArray.put(word.getUUIDString(), false);
                    }
                    selectCount = 0;
                }
                getActivity().invalidateOptionsMenu();
                updateActionBarTitle(true);
                mAdapter.notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setListeners() {

        mViewPagerType.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                updateUI();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        mViewPagerObject.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                updateUI();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemSelected: " + position);
                switch (position) {
                    case ALL:// ALL
                        mCountWords.setText("");
                        mCountWords.setEnabled(false);
                        if (mAdapter.isSelectable) modeSelectedTurnOff(position);
                        break;
                    case SELECTED://SELECTED
                        mCountWords.setText("");
                        mCountWords.setEnabled(false);
                        if (!mAdapter.isSelectable()) {
                            mAdapter.setSelectable(true);
                        }
                        break;
                    case DIAPASON://DIAPASON
                    case RANDOM://RANDOM
                        mCountWords.setEnabled(true);
                        if (mAdapter.isSelectable) modeSelectedTurnOff(position);
                        break;
                }
                if (mCountWords.isEnabled()) {
                    mCountWords.requestFocus();
                }
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCountWords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateUI();
            }
        });
        mCountWords.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String newString = source.toString();
                String old = dest.toString();

                if (newString.contains("-") && old.contains("-")) {
                    newString = newString.replaceAll("-", "");
                }

                return newString;
            }
        }, new InputFilter.LengthFilter(7)});

        CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateUI();
            }
        };


        startButton.setOnClickListener(this);
    }

    @Override
    public void updateUI() {
        boolean isAllReady = true;
        View currentType = mViewPagerType.getChildAt(mViewPagerType.getCurrentItem());

        View currentObject = mViewPagerObject.getChildAt(mViewPagerObject.getCurrentItem());

        try { // при повороте возвращает null из-за mCountWord
            if (!currentType.isEnabled()) {
                isAllReady = false;
            }
            if (!currentObject.isEnabled()) {
                isAllReady = false;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        if (currentObject == null){
//            Toast.makeText(getContext(),"On a null object reference (for developers)",Toast.LENGTH_SHORT).show(); // Temp
//        }else


        if (mode == MODE_SELECT) {
            int min = 2;
            if (mViewPagerType.getCurrentItem() == QUIZ) min = 4;
            isAllReady = selectCount >= min;
            Log.i(TAG, "selectCount: " + selectCount);
        }

        String countWords = mCountWords.getText().toString();
        if (mCountWords.isEnabled() && countWords.equals("")) {
            isAllReady = false;
        } else if (mSpinner.getSelectedItemPosition() == RANDOM) {
            isAllReady = !countWords.contains("-");
        }

        startButton.setEnabled(isAllReady);
    }

    private void removeWordsWithEmptyCells(int objectTests, ArrayList<Word> words) {
        ArrayList<Word> tempList = new ArrayList<>(words);
        for (Word w : tempList) {
            switch (objectTests) {
                case ORIGINAL:
                    if (w.getOriginal().equals("")) {
                        words.remove(w);
                    }
                    break;
                case TRANSLATE:
                    if (w.getTranslate().equals("")) {
                        words.remove(w);
                    }
                    break;
            }
        }
    }


    private void updateActionBarTitle(boolean selectMode) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (!selectMode) {
            activity.getSupportActionBar().setTitle(mGroup.getName());
        } else {
            activity.getSupportActionBar().setTitle(String.valueOf(selectCount));
        }
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
                            v.setX(-parent);
                            v.animate().translationX(1.0f)
                                    .setDuration(200)
                                    .setStartDelay(i * 50)
                                    .start();
                            v.animate().setStartDelay(0);//возвращаю дефолтное значение
                        }

                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        Log.i(TAG, "Remove OnPreDrawListener");
                        return true;
                    }
                });
    }

    @Override
    public void onClick(View v) {
        {
            boolean isAllReady = true;
            ArrayList<Word> learnList = new ArrayList<>();
            String s;
            int minCount = 2;
            if (mViewPagerType.getCurrentItem() == QUIZ) minCount = 4;

            /* На краях ViewPager getChildCount == 2
             * Пофиксил увеличением лимита загружаемых страниц setOffscreenPage(5)
             */

            Log.d(TAG, "Current " + mViewPagerType.getCurrentItem() +
                    " Count " + mViewPagerType.getChildCount());

            Intent intent = LearnDetailActivity.newIntent( // Тут предаются выбратнные атрибуты для начала теста
                    getContext(),
                    learnList,
                    mViewPagerType.getCurrentItem(),
                    mViewPagerObject.getCurrentItem());

            switch (mSpinner.getSelectedItemPosition()) {
                case ALL://ALL
                    learnList.addAll(mWords);
                    break;
                case SELECTED://SELECTED
                    // Заполняю
                    for (int i = 0; i < mAdapter.mSelectionsArray.size(); i++) {
                        if (mAdapter.mSelectionsArray.valueAt(i)) {
                            for (Word w : mWords) {
                                if (w.getUUIDString().equals(mAdapter.mSelectionsArray.keyAt(i))) {
                                    learnList.add(w);
                                }
                            }
                        }
                    }
                    break;
                case DIAPASON://DIAPASON
                    s = mCountWords.getText().toString();
                    String[] diapason;
                    if (s.contains("-")) {
                        diapason = s.split("-", 2);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.diapason_error), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int[] countInt = new int[diapason.length];

                    for (int i = 0; i < diapason.length; i++) {
                        if (diapason[i].equals("")) {
                            Toast.makeText(getContext(), getString(R.string.diapason_error), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        countInt[i] = Integer.valueOf(diapason[i]);
                    }

                    int max = Math.max(countInt[0], countInt[1]);
                    int min = Math.min(countInt[0], countInt[1]);

                    if (min == 0) {
                        Toast.makeText(getContext(), getString(R.string.diapason_error), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (max - min < minCount) {
                        Toast.makeText(getContext(), getString(R.string.min_word_list_size, minCount), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (max > mWords.size()) {
                        Toast.makeText(getContext(), getString(R.string.override_number_words), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (int i = min - 1; i < max; i++) {
                        learnList.add(mWords.get(i));
                    }

                    break;
                case RANDOM://RANDOM
                    s = mCountWords.getText().toString();
                    int count = Integer.valueOf(s);
                    if (count == 0 || count < minCount) {
                        Toast.makeText(getContext(), getString(R.string.min_word_list_size, minCount), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (count > mWords.size()) {
                        Toast.makeText(getContext(), getString(R.string.override_number_words), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<Integer> integerArrayList = new ArrayList<>();
                    Random random = new Random();
                    boolean b = false;
                    while (!b) {
                        int r = random.nextInt(mWords.size());
                        if (integerArrayList.indexOf(r) == -1) {
                            integerArrayList.add(r);
                        }
//                        Log.i(TAG, "Random: " + r);
                        if (integerArrayList.size() == count) {
                            b = true;
                        }
                    }
                    for (int i : integerArrayList) {
                        learnList.add(mWords.get(i));
                    }

                    break;

            }


            // Если выбран один, но имеет пустые ячейки
            /*int i = 0;
            boolean[] booleans = {switchOriginal.isChecked(), switchTranslate.isChecked()};
            for (boolean b : booleans){
                if (b){
                    i++;
                }
            }
            if (i == 1){
                int j = 0;
                for (Word w : mWords){
                    if (switchOriginal.isChecked()) {
                        if (w.getOriginal().equals("")) j++;
                    }
                    if (switchTranslate.isChecked()) {
                        if (w.getTranslate().equals("")) j++;
                    }
                }
                if (j != 0){
                    isAllReady = false;
                    Toast.makeText(getContext(),getString(R.string.your_used_object_have_empty_cells) , Toast.LENGTH_LONG).show();
                }
            }*/

            /*if (mViewPagerObject.getCurrentItem() == TRANSCRIPT){
                int learnSize = learnList.size();
                removeWordsWithEmptyCells(TRANSCRIPT,learnList);


                if (learnList.size() < minCount){
                    isAllReady = false;
                    s = getString(R.string.min_transcript_list_size,minCount);
                    Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
                }else if (learnList.size() != learnSize){
                    s = getString(R.string.word_without_transcription);
                    Toast toast = Toast.makeText(getContext(),s , Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP,toast.getXOffset(),toast.getYOffset());
                    toast.show();
//                    Snackbar.make(getView(),s,Snackbar.LENGTH_LONG).show();
                }
            }*/

            if (isAllReady) { // Выполнять, если все требования соблюдены
                startActivity(intent);
            }
        }
    }

    public class WordHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        TextView original;
        TextView association;
        TextView translate;

        private boolean isSelectableMode = false; //default
        private boolean isSelectableItem = false; //default
        private WordAdapter myParentAdapter;

        public WordHolder(@NonNull View itemView, WordAdapter parentAdapter) {
            super(itemView);

            original = itemView.findViewById(R.id.original_textView);
            association = itemView.findViewById(R.id.association_textView);
            translate = itemView.findViewById(R.id.translate_textView);

            myParentAdapter = parentAdapter;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        public void bind(Word word) {
            int position = getAdapterPosition();

            String origStr = word.getOriginal();
            String transcriptStr = word.getAssociation();
            String tranStr = word.getTranslate();

            isSelectableMode = myParentAdapter.isSelectable;
            isSelectableItem = myParentAdapter.isItemSelectable(mAdapter.getList().get(position).getUUIDString());

            original.setText(origStr);
            association.setText(transcriptStr);
            translate.setText(tranStr);

            if (isSelectableMode && isSelectableItem) {
                Resources resources = getResources();
                itemView.setBackground(new ColorDrawable(resources.getColor(R.color.colorAccent)));

            } else {
                itemView.setBackground(null);
            }
        }


        @Override
        public void onClick(View view) {
            int i = getAdapterPosition();
            if (i == RecyclerView.NO_POSITION) return;
            isSelectableMode = myParentAdapter.isSelectable;
            if (isSelectableMode) {
                isSelectableItem = !isSelectableItem;
                myParentAdapter.setItemChecked((mAdapter.getList().get(i).getUUIDString()), isSelectableItem);
                updateActionBarTitle(true);

                Resources resources = getResources();
                if (isSelectableItem) {
                    // Here will be some Drawable
                    itemView.setBackground(new ColorDrawable(resources.getColor(R.color.colorAccent)));
                } else {
                    itemView.setBackground(null);
                }
            }

        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if (!isSelectableMode) {
                mSpinner.setSelection(SELECTED);
                myParentAdapter.setSelectable(true);
//                myParentAdapter.notifyDataSetChanged();

                myParentAdapter.setItemChecked((mAdapter.getList().get(position).getUUIDString()), true);

                updateActionBarTitle(true);
//                    wordHolder.setEnabledAll(false);
            } else {
                isSelectableItem = !isSelectableItem;

                myParentAdapter.setItemChecked((mAdapter.getList().get(position).getUUIDString()), isSelectableItem);
                if (isSelectableItem) {
                    Resources resources = getResources();
                    itemView.setBackground(new ColorDrawable(resources.getColor(R.color.colorAccent)));
                } else {
                    itemView.setBackground(null);
                }
            }

            myParentAdapter.notifyItemChanged(position);
//            Log.i(TAG, "onLongClick: true");
//                    notifyItemChanged(position);
            return true;
        }
    }

    public class WordAdapter extends RecyclerView.Adapter<WordHolder>
            implements ItemTouchHelperAdapter {

        private ArrayList<Word> mList;
        private ArrayMap<String, Boolean> mSelectionsArray = new ArrayMap<>();
        private boolean isSelectable = false;

        public WordAdapter(ArrayList<Word> list) {
            setList(list);
        }

        public ArrayList<Word> getList() {
            return mList;
        }

        public void setList(ArrayList<Word> list) {
//            Collections.sort(list);
            mList = list;
            if (mList.isEmpty() && Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                addAnimation();
            }
            setSelectionsArray(selectArray);
        }

        @NonNull
        @Override
        public WordHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_words_list_preview, viewGroup, false);
            return new WordHolder(view, this);
        }

        @Override
        public void onBindViewHolder(@NonNull WordHolder wordHolder, int i) {
            wordHolder.bind(mList.get(i));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder viewHolder, int flag) {
//            int position = viewHolder.getAdapterPosition();
            WordHolder wordHolder = (WordHolder) viewHolder;
            switch (flag) {
                case ItemTouchHelper.START://Выделить
                case ItemTouchHelper.END:
                    wordHolder.onLongClick(wordHolder.itemView);
                    break;
            }
        }

        private void setItemChecked(String id, boolean isChecked) {
            if (isChecked) {
                selectCount++;
            } else {
                selectCount--;
            }
            mSelectionsArray.put(id, isChecked);
            int j = 0;
            for (int i = 0; i < mSelectionsArray.size(); i++) {
                if (mSelectionsArray.valueAt(i)) j++;
            }
            updateUI();

            selectAll = (j == mSelectionsArray.size());
            getActivity().invalidateOptionsMenu();
        }

        private boolean isItemSelectable(String id) {
            return mSelectionsArray.get(id) == null ? false : mSelectionsArray.get(id);
        }

        public boolean isSelectable() {
            return isSelectable;
        }

        public void setSelectable(boolean selectable) {
            isSelectable = selectable;
            if (isSelectable) {
                mode = MODE_SELECT;
            } else {
                mode = MODE_NORMAL;
            }

            getActivity().invalidateOptionsMenu();
//            addAlphaAnim();
        }

        public void setSelectionsArray(ArrayList<String> selectionsArray) {
            if (selectionsArray == null) return;

            if (!selectionsArray.isEmpty()) {
                for (int i = 0; i < selectionsArray.size(); i++) {
                    mSelectionsArray.put(selectionsArray.get(i), true);
                }
                Log.i(TAG, "StringArray.size(): " + selectionsArray.size());
            }
            for (int i = 0; i < mList.size(); i++) {
                Word word = mList.get(i);
                if (mSelectionsArray.get(word.getUUIDString()) == null) {
                    mSelectionsArray.put(word.getUUIDString(), false);
                }

            }
            Log.i(TAG, "mSelectionsArray.size(): " + mSelectionsArray.size());

        }
    }

    public class WordItemHelperCallBack extends ItemTouchHelper.Callback {

        private ItemTouchHelperAdapter mHelperAdapter;

        public WordItemHelperCallBack(ItemTouchHelperAdapter helperAdapter) {
            mHelperAdapter = helperAdapter;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView mRecyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int swipeFlag = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, swipeFlag);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView mRecyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            mHelperAdapter.onItemDismiss(viewHolder, i);
        }

    }

}