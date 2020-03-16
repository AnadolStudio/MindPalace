package com.anadol.rememberwords.fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Transition;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anadol.rememberwords.activities.LearnStartActivity;
import com.anadol.rememberwords.database.DatabaseHelper;
import com.anadol.rememberwords.database.DbSchema;
import com.anadol.rememberwords.database.DbSchema.Tables.Cols;
import com.anadol.rememberwords.database.MyCursorWrapper;
import com.anadol.rememberwords.myList.DoInBackground;
import com.anadol.rememberwords.myList.Group;
import com.anadol.rememberwords.myList.LabelEmptyList;
import com.anadol.rememberwords.R;
import com.anadol.rememberwords.myList.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.anadol.rememberwords.database.DbSchema.Tables.Cols.NAME_GROUP;
import static com.anadol.rememberwords.database.DbSchema.Tables.GROUPS;
import static com.anadol.rememberwords.database.DbSchema.Tables.WORDS;
import static com.anadol.rememberwords.fragments.DialogResult.RESULT;
import static com.anadol.rememberwords.fragments.GroupListFragment.*;
import static com.anadol.rememberwords.myList.Group.NON_COLOR;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupDetailFragment extends MyFragment implements IOnBackPressed{
    private static final String TAG = "GroupDetailFragment";

    public static final String GROUP = "group";
    public static final String WORD_SAVE = "word_save";
    public static final String NAMES_ALL_GROUPS = "names_all_groups";
    public static final String POSITION = "position";
    public static final String IS_CREATED = "is_created";
    public static final String IS_CHANGED = "is_changed";

    private static final String DIALOG_COLOR = "color";
    private static final String GRADIENT = "gradient";
    private static final int REQUEST_DRAWABLE = 1;
    private static final int REQUEST_TRANSLATE_RESULT = 4;
    private static final int REQUEST_MULTI_TRANSLATE = 2;

    private static final String GET_WORDS = "words";
    private static final String SAVE_GROUP = "add_group";
    private static final String SAVE_WORDS = "add_words";
    private static final String REMOVE_WORD = "remove_words";
    private static final String TYPE_SORT = "type_sort";
    private static final String MERGE_GROUPS = "unify_groups";
    private static final String GET_GROUPS_NAME = "get_groups_name";
    private static final String DATA_IS_CHANGED = "data_is_changed";



    private LabelEmptyList mLabelEmptyList;
    private RecyclerView mRecyclerView;
    private WordAdapter mAdapter;
    private Group mGroup;
    private ArrayList<Word> mWords;
    private ArrayList<Group> mGroupsForMerge;

    private EditText nameGroup;
    //    private ImageButton addButton;
    private ImageView groupColor;
    private int[] colors;
    private ArrayList<String> allGroupsNames;
    private boolean isCreated;
    private boolean typeSort;
    private boolean sortIsNeed = true;
    private boolean selectAll;
    private int selectCount;
    private ArrayList<String> selectArray;
    private boolean selectable = false;
    private boolean isChanged;
    private TextView countWords;


    public GroupDetailFragment() {
        // Required empty public constructor
    }

    // TODO: Для MERGE необходимо добавить Dialog с цветами объединенных групп,
    //  возможно стоит создать новый фрагмент, либо добавить это возможность
    //  для всех GroupDetailFragment
    //  Идеи: добавить в ColorPicker подобие actionbar как в DialogMultiTranslate,
    //  перенести туда кнопку AddNewCase и добавить новую для выбора из уже существующих вариантов

    public static GroupDetailFragment newInstance(Group group) {

        Bundle args = new Bundle();
        if (group == null) {
            group = new Group(UUID.randomUUID(), new int[]{Color.RED,Color.GREEN,Color.BLUE}, "");
            args.putBoolean(IS_CREATED,true);
        }
        args.putParcelable(GROUP, group);
        GroupDetailFragment fragment = new GroupDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static GroupDetailFragment newInstance(@NonNull ArrayList<Group> groups) {
        Bundle args = new Bundle();

        Group group = new Group(UUID.randomUUID(), groups.get(0).getColors(), "");
        args.putParcelable(GROUP, group);
        args.putParcelableArrayList(GROUPS, groups);
        GroupDetailFragment fragment = new GroupDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(WORD_SAVE, mWords);
        outState.putIntArray(GRADIENT, colors);// key GRADIENT in other places
        outState.putBoolean(IS_CREATED,isCreated);
        outState.putBoolean(TYPE_SORT,typeSort);
        outState.putBoolean(SELECT_MODE, mAdapter.isSelectable);

        selectArray.clear();

        for (int i = 0; i < mAdapter.mSelectionsArray.size();i++) {
            if (mAdapter.mSelectionsArray.valueAt(i)) {
                selectArray.add(mAdapter.mSelectionsArray.keyAt(i));
            }
        }
        Log.i(TAG, "onSaveInstanceState: " + selectArray.size());
        outState.putStringArrayList(SELECT_LIST, selectArray);

        outState.putInt(SELECT_COUNT,selectCount);
        outState.putBoolean(SELECT_ALL,selectAll);
        outState.putBoolean(DATA_IS_CHANGED, isChanged);
        outState.putStringArrayList(NAMES_ALL_GROUPS, allGroupsNames);

//        outState.putBoolean(SUBTITLE,mySubtitleVisible);
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
        View view = inflater.inflate(R.layout.fragment_groups_detail, container, false);

        nameGroup = view.findViewById(R.id.name_group);
        groupColor = view.findViewById(R.id.group_color);

        mGroup = getArguments().getParcelable(GROUP);
        nameGroup.setText(mGroup.getName());
        nameGroup.setSelection(nameGroup.length());

        isCreated = getArguments().getBoolean(IS_CREATED);

        typeSort = true;
        colors = mGroup.getColors();


        if (savedInstanceState != null) {
            mWords = savedInstanceState.getParcelableArrayList(WORD_SAVE);
            colors = savedInstanceState.getIntArray(GRADIENT);
            isCreated = savedInstanceState.getBoolean(IS_CREATED);
            typeSort = savedInstanceState.getBoolean(TYPE_SORT);
            selectable = savedInstanceState.getBoolean(SELECT_MODE);
            allGroupsNames = savedInstanceState.getStringArrayList(NAMES_ALL_GROUPS);
            isChanged = savedInstanceState.getBoolean(DATA_IS_CHANGED);
            selectArray = savedInstanceState.getStringArrayList(SELECT_LIST);
            selectCount = savedInstanceState.getInt(SELECT_COUNT);
            selectAll = savedInstanceState.getBoolean(SELECT_ALL);
//            mySubtitleVisible = savedInstanceState.getBoolean(SUBTITLE);
        } else {
            mWords = new ArrayList<>();
            selectArray = new ArrayList<>();
            allGroupsNames = new ArrayList<>();
            isChanged = false;
            selectCount = 0;
            selectAll = false;
        }

        //For Merge, selected groups;
        mGroupsForMerge = getArguments().getParcelableArrayList(GROUPS);

        groupColor.setImageDrawable(mGroup.getGroupDrawable());

        groupColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DialogFragment dialog = ColorPicker.newInstance(colors);
                dialog.setTargetFragment(GroupDetailFragment.this, REQUEST_DRAWABLE);
                dialog.show(fm, DIALOG_COLOR);
            }
        });

// Recycler

        FrameLayout frameLayout = view.findViewById(R.id.recycler_container);
        mRecyclerView = frameLayout.findViewById(R.id.recycler_detail);


        //mWord передается адаптеру после завершения анимации (если она есть)
        mAdapter = new WordAdapter();
        mAdapter.setSelectable(selectable);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),DividerItemDecoration.VERTICAL));
/*
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
*/
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new WordItemHelperCallBack(mAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        mLabelEmptyList = new LabelEmptyList(
                getContext(),
                frameLayout,
                mWords);

        countWords = view.findViewById(R.id.count_text);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){
            addTransitionListener();
        }else {
            mAdapter.addList(mWords);
        }
    }

    @TargetApi(21)
    private void addTransitionListener() {
        final Transition transition = getActivity().getWindow().getSharedElementEnterTransition();
        if (transition != null){
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    transition.removeListener(this);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.addList(mWords);
                        }
                    },10);//Чтобы избежать небольшого тормоза в конце анимации
                }
                @Override
                public void onTransitionStart(Transition transition) {
                }
                @Override
                public void onTransitionCancel(Transition transition) {
                    transition.removeListener(this);
                }
                @Override
                public void onTransitionPause(Transition transition) {
                }
                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGroupsForMerge != null && mWords.isEmpty()){
            new WordBackground().execute(MERGE_GROUPS);

        }else if (!isCreated && mWords.isEmpty()) {
            new WordBackground().execute(GET_WORDS);

        } else {
            updateUI();//При поворотах
            mAdapter.setList(mWords);
        }

        if (allGroupsNames.isEmpty()){
            Log.i(TAG, "allGroupsNames.isEmpty()");
            new WordBackground().execute(GET_GROUPS_NAME);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (mode == MODE_NORMAL ) {
            inflater.inflate(R.menu.fragment_group_detail,menu);

            MenuItem play = menu.findItem(R.id.menu_start);
            if (mWords.size() < 1 || isCreated || mGroupsForMerge != null){
                play.setVisible(false);
            }else {
                play.setVisible(true);
            }

        } else if (mode == MODE_SELECT){
            inflater.inflate(R.menu.menu_group_selected_list,menu);
            MenuItem select = menu.findItem(R.id.menu_select_all);

            MenuItem merge = menu.findItem(R.id.menu_merge);
            merge.setVisible(false);

            MenuItem remove = menu.findItem(R.id.menu_remove);
            remove.setVisible(mGroupsForMerge == null);

            if (selectAll){
                select.setIcon(R.drawable.ic_menu_select_all_on);
            }else {
                select.setIcon(R.drawable.ic_menu_select_all_off);
            }
            updateActionBarTitle(true);
        }


       /* MenuItem sort = menuBottom.findItem(R.id.menu_sort);
        sort.setVisible(!selectMode);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){


            case R.id.menu_start:
                if (!(mWords.size() < 2)) {
                    createLearnFragment();
                }else {
                    Toast.makeText(getContext(),getString(R.string.lack_of_words),Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.menu_remove:
                removeWord();
                return true;

            case R.id.add_button:
                //TODO: добавление нескольких Words при помощи
                // Settings и SettingsPreference
                addNewWord();
                return true;

            case R.id.menu_select_all:
                selectAll = !selectAll;
                mAdapter.mSelectionsArray.clear();

                if (selectAll){
                    for (int i = 0; i < mAdapter.getList().size(); i++) {
                        Word word = mAdapter.getList().get(i);
                        mAdapter.mSelectionsArray.put(word.getIdString(),true);
                    }
                    selectCount = mAdapter.getList().size();
                }else {
                    for (int i = 0; i < mAdapter.getList().size(); i++) {
                        Word word = mAdapter.getList().get(i);
                        mAdapter.mSelectionsArray.put(word.getIdString(),false);
                    }
                    selectCount = 0;
                }
                getActivity().invalidateOptionsMenu();
                updateActionBarTitle(true);
                mAdapter.notifyDataSetChanged();
                return true;

            case R.id.menu_save:
                saveGroup();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onBackPressed() {
        switch (mode){
            /*case MODE_SEARCH:
                mode = MODE_NORMAL;
                getActivity().invalidateOptionsMenu();
                searchView.onActionViewCollapsed();
                return true;*/
            case MODE_SELECT:
                mode = MODE_NORMAL;
                modeSelectedTurnOff();
                mAdapter.mSelectionsArray.clear();
                for (int i = 0; i < mAdapter.getList().size(); i++) {
                    Word word = mAdapter.getList().get(i);
                    mAdapter.mSelectionsArray.put(word.getIdString(),false);
                }
                selectCount = 0;
                return true;
            default:
                return false;
        }
    }

    private void modeSelectedTurnOff() {
        mAdapter.setSelectable(false);
        mAdapter.notifyDataSetChanged();
        updateActionBarTitle(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode){// так мы понимаем что именно для этой активности предназначаются данные из интента
            case REQUEST_DRAWABLE:
                int[] newColors = data.getIntArrayExtra(ColorPicker.EXTRA_GRADIENT);
                colors = new int[3];
                int i = 0;
                for (int c :newColors){
                    colors[i] = c;
                    i++;
                }
                while (i != 3){
                    colors[i] = NON_COLOR;
                    i++;
                }

                mGroup.setColors(colors);
                groupColor.setImageDrawable(mGroup.getGroupDrawable());
                break;
            case REQUEST_TRANSLATE_RESULT:
            case REQUEST_MULTI_TRANSLATE:
                int p = data.getIntExtra(POSITION,0);
                mAdapter.notifyItemChanged(p);
                break;
        }

    }

    public void updateUI() {
        mLabelEmptyList.update();
        updateWordCount();
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.invalidateOptionsMenu();
    }

    public Intent dataIsChanged(){
        Intent intent = new Intent();
        intent.putExtra(IS_CHANGED,isChanged);
        intent.putExtra(CHANGED_ITEM,mGroup);
        return intent;
    }

    private void addNewWord() {
        mWords.add(0,new Word(
                UUID.randomUUID(),
                "",
                "",
                "",
                nameGroup.getText().toString(),
                "",
                Word.FALSE));
        mAdapter.notifyItemInserted(0);//Добавит ввод в начало ЛИСТА
        mRecyclerView.getLayoutManager().scrollToPosition(0);
        updateUI();
    }

    private void saveGroup() {
        String name = nameGroup.getText().toString();
        // Подразумевается, что групп с именим "" или " " быть не должно
        if (isCreated || mGroupsForMerge != null ||!name.equals(mGroup.getName())){
            if (name.equals("") || name.equals(" ")) {
                nameGroup.setError(getString(R.string.is_empty));
                return;
            }
            if (nameIsTaken(name)){
                nameGroup.setError(getString(R.string.name_is_taken));
                return;
            }
        }
        mGroup.setName(name);
        new WordBackground().execute(SAVE_GROUP);
        new WordBackground().execute(SAVE_WORDS);

    }

    private boolean nameIsTaken(String s){
        return allGroupsNames.contains(s);
    }

    private void createLearnFragment() {
        sortIsNeed = false;
        new WordBackground().execute(GET_WORDS);//Доделывает в Post
    }

    private void updateWordCount() {
        String stringCount = getResources().getQuantityString(R.plurals.word_items, mWords.size(),mWords.size());
        countWords.setText(getResources().getString(R.string.word_count,stringCount));
    }

    private void updateActionBarTitle(boolean selectMode){
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        if (!selectMode) {
            activity.getSupportActionBar().setTitle(getString(R.string.app_name));
        }else {
            activity.getSupportActionBar().setTitle(String.valueOf(selectCount));
        }
    }

    private void removeWord(){
        if (!isCreated) {
            new WordBackground().execute(REMOVE_WORD);
        }else {
            removeFromAdapter();
        }
    }

    private void removeFromAdapter() {
        ArrayList<Word> wordsRemove = new ArrayList<>();
        // Заполняю
        for (int i = 0; i < mAdapter.mSelectionsArray.size(); i++) {
            if (mAdapter.mSelectionsArray.valueAt(i)) {
                for (Word w : mWords) {
                    if (w.getIdString().equals(mAdapter.mSelectionsArray.keyAt(i))){
                        wordsRemove.add(w);
                    }
                }
            }
        }
        int j;
        //Удаляю
        for (Word w : wordsRemove){
            j = mAdapter.getList().indexOf(w);
            mAdapter.notifyItemRemoved(j);
            mWords.remove(w);
            Log.i(TAG, "Word removed: " + w.getOriginal());
        }
        mAdapter.mSelectionsArray.clear();
        // Обновляю
        for (int i = 0; i < mAdapter.getList().size(); i++) {
            Word w = mAdapter.getList().get(i);
            mAdapter.mSelectionsArray.put(w.getIdString(),false);
        }
        selectCount = 0;
        updateUI();

//        setSelectMode(false);
    }

    private void createDialogMultiTranslate(int position) {
        FragmentManager fm = getFragmentManager();
        DialogMultiTranslate dialogTranslate = DialogMultiTranslate.newInstance(mWords.get(position),position);
        dialogTranslate.setTargetFragment(GroupDetailFragment.this,REQUEST_MULTI_TRANSLATE);
        dialogTranslate.show(fm,RESULT);
    }

    private void createDialogTranscript(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();

        DialogTranscript dialogResult = DialogTranscript.newInstance(mWords.get(position),position);
        dialogResult.setTargetFragment(GroupDetailFragment.this, REQUEST_TRANSLATE_RESULT);
        FragmentManager fragmentManager = getFragmentManager();
        dialogResult.show(fragmentManager, RESULT);
    }

    private void removeEmptyWords(ArrayList<Word> words){
        ArrayList<Word> tempList = new ArrayList<>(words);
        for (Word w : tempList){
            int i = 0;
            if (w.getOriginal().equals("")){i++;}
            if (w.getTranslate().equals("")){i++;}
            if (i >= 1){
                words.remove(w);
            }
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

    public class WordHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{
        EditText original;
        EditText transcription;
        EditText translate;
        EditText comment;

        private boolean isSelectableMode = false; //default
        private boolean isSelectableItem = false; //default
        private WordAdapter myParentAdapter;

        public WordHolder(@NonNull View itemView, WordAdapter parentAdapter) {
            super(itemView);

            original = itemView.findViewById(R.id.text_original);
            transcription = itemView.findViewById(R.id.text_transcription);
            translate = itemView.findViewById(R.id.text_translate);
            comment = itemView.findViewById(R.id.edit_comment);

            original.addTextChangedListener(new MyTextWatch(this,MyTextWatch.ORIGINAL));
            translate.addTextChangedListener(new MyTextWatch(this,MyTextWatch.TRANSLATE));
            transcription.addTextChangedListener(new MyTextWatch(this,MyTextWatch.TRANSCRIPT));
            comment.addTextChangedListener(new MyTextWatch(this,MyTextWatch.COMMENT));

            myParentAdapter = parentAdapter;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        public void bind(Word word){
            int position = getAdapterPosition();

            String origStr = word.getOriginal();
            String transcriptStr = word.getTranscript();
            String tranStr;
            if (word.hasMultiTrans() == Word.FALSE) {
                tranStr = word.getTranslate();
            }else {
                tranStr = word.getMultiTranslate();
            }
            String commentStr = word.getComment();

            isSelectableMode = myParentAdapter.isSelectable;
            isSelectableItem = myParentAdapter.isItemSelectable(mAdapter.getList().get(position).getIdString());

            original.setText(origStr);
            transcription.setText(transcriptStr);
            translate.setText(tranStr);
            comment.setText(commentStr);

            original.setSelection(original.length());
            translate.setSelection(translate.length());
            comment.setSelection(comment.length());

            setEnabledAll(!isSelectableMode);
            translate.setEnabled(word.hasMultiTrans() == Word.FALSE && !isSelectableMode);

            if (isSelectableMode && isSelectableItem) {
                Resources resources = getResources();
                itemView.setBackground(new ColorDrawable(resources.getColor(R.color.colorAccent)));

            }else {
                itemView.setBackground(null);
            }


        }

        private void setEnabledAll(boolean b){
            original.setEnabled(b);
            transcription.setEnabled(b);
            translate.setEnabled(b);
            comment.setEnabled(b);
        }

        @Override
        public void onClick(View view) {
            int i = getAdapterPosition();
            if (i == RecyclerView.NO_POSITION) return;

            if (isSelectableMode){
                isSelectableItem = !isSelectableItem;
                myParentAdapter.setItemChecked((mAdapter.getList().get(i).getIdString()),isSelectableItem);
                Resources resources = getResources();
                if (isSelectableItem) {
                    // Here will be some Drawable
                    itemView.setBackground(new ColorDrawable(resources.getColor(R.color.colorAccent)));
                    selectCount++;
                }else {
                    itemView.setBackground(null);
                    selectCount--;
                }
                updateActionBarTitle(true);
            }

        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if (mGroupsForMerge != null) {
                myParentAdapter.notifyItemChanged(position);
                Toast.makeText(getContext(), getString(R.string.do_not_select), Toast.LENGTH_SHORT).show();
                return true;
            }

            if (!isSelectableMode) {
                myParentAdapter.setSelectable(true);
//                myParentAdapter.notifyItemChanged(position);
                myParentAdapter.notifyDataSetChanged();

                myParentAdapter.setItemChecked((mAdapter.getList().get(position).getIdString()), true);

                selectCount++;
                updateActionBarTitle(true);
//                    wordHolder.setEnabledAll(false);
            }else {
                isSelectableItem = !isSelectableItem;

                myParentAdapter.setItemChecked((mAdapter.getList().get(position).getIdString()), isSelectableItem);
                if (isSelectableItem) {
                    Resources resources = getResources();
                    itemView.setBackground(new ColorDrawable(resources.getColor(R.color.colorAccent)));
                    selectCount++;
                }else {
                    itemView.setBackground(null);
                    selectCount--;
                }
                myParentAdapter.notifyItemChanged(position);
            }
            Log.i(TAG, "onLongClick: true");
//                    notifyItemChanged(position);
            return true;
        }
    }

    public class WordAdapter extends RecyclerView.Adapter<WordHolder>
            implements ItemTouchHelperAdapter{

        private ArrayList<Word> mList;
        private ArrayMap<String,Boolean> mSelectionsArray = new ArrayMap<>();
        private boolean isSelectable = false;

        public WordAdapter(ArrayList<Word> list) {
            Collections.sort(list);
            mList = list;
        }

        public WordAdapter() {
            mList = new ArrayList<>();
        }

        public void addList(ArrayList<Word> list) {
            if (mList.isEmpty() && Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                addAnimation();
            }
            setList(list);
        }

        public void setList(ArrayList<Word> list) {
            Collections.sort(list);
            mList = list;
            notifyDataSetChanged();
            mLabelEmptyList.update();
            setSelectionsArray(selectArray);
        }

        public ArrayList<Word> getList() {
            return mList;
        }

        @NonNull
        @Override
        public WordHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_words_list,viewGroup,false);
            return new WordHolder(view,this);
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
            int position = viewHolder.getAdapterPosition();
            WordHolder wordHolder = (WordHolder)viewHolder;
            switch (flag){
                case ItemTouchHelper.START://Выделить

                    wordHolder.onLongClick(wordHolder.itemView);

                    break;
                case ItemTouchHelper.END://DialogTranslate
                    if (!isSelectable) {
                        createDialogMultiTranslate(position);
                    }else {
                        Toast.makeText(getActivity(), getString(R.string.close_select_mode) , Toast.LENGTH_SHORT).show();
                        notifyItemChanged(position);
                    }
                    break;
            }
        }

        private void setItemChecked(String id, boolean isChecked){
            mSelectionsArray.put(id,isChecked);
            int j = 0;
            for (int i = 0; i < mSelectionsArray.size(); i++) {
                if (mSelectionsArray.valueAt(i)) j++;
            }
            selectAll = (j == mSelectionsArray.size());
            getActivity().invalidateOptionsMenu();
        }

        private boolean isItemSelectable(String id){
            return mSelectionsArray.get(id) == null ? false : mSelectionsArray.get(id);
        }


        public void setSelectable(boolean selectable) {
            isSelectable = selectable;
            if (isSelectable){
                mode = MODE_SELECT;
            }else {
                mode = MODE_NORMAL;
            }

            getActivity().invalidateOptionsMenu();
//            addAlphaAnim();
        }

        public boolean isSelectable() {
            return isSelectable;
        }

        public void setSelectionsArray(ArrayList<String> selectionsArray) {
            if (selectionsArray == null )return;

            if (!selectionsArray.isEmpty()) {
                for (int i = 0; i < selectionsArray.size(); i++) {
                    mSelectionsArray.put(selectionsArray.get(i), true);
                }
                Log.i(TAG, "StringArray.size(): "+ selectionsArray.size());
            }
            for (int i = 0; i < mList.size(); i++) {
                Word word = mList.get(i);
                if (mSelectionsArray.get(word.getIdString()) == null) {
                    mSelectionsArray.put(word.getIdString(), false);
                }

            }
            Log.i(TAG, "mSelectionsArray.size(): "+ mSelectionsArray.size());

        }
    }

    public class WordBackground extends DoInBackground {
        private String cmd;
        private MyCursorWrapper cursor;

        private SQLiteDatabase db;

        private ArrayList<Word> mWordsToLearn;
        private ArrayList<Word> wordsRemove;

        @Override
        public Boolean doIn(String command) {
            ArrayList<Word> words;
            cmd = command;
            try {
                db = new DatabaseHelper(getContext()).getWritableDatabase();
//                System.out.println("NAME " + mGroup.getName());
                switch (command) {
                    case SAVE_GROUP:
                        cursor = queryTable(db,
                                GROUPS,
                                null,
                                Cols.UUID + " = ?",
                                new String[]{mGroup.getIdString()});

                        ContentValues values = createGroupValue(mGroup.getId(), mGroup.getName().trim(), colors[0], colors[1],colors[2]);
                        if (cursor.getCount() != 0) {
                            db.update(GROUPS,
                                    values,
                                    Cols.UUID + " = ?",
                                    new String[]{mGroup.getIdString()});
                        } else {
                            db.insert(
                                    GROUPS,
                                    null,
                                    values
                            );
                        }
                        return true;

                    case GET_GROUPS_NAME:
                        ArrayList<String> groupsNames = new ArrayList<>();
                        cursor = queryTable(
                                db,
                                GROUPS,
                                new String[]{NAME_GROUP},
                                null,
                                null
                        );

                        if (cursor.getCount() != 0){
                            cursor.moveToFirst();
                            while (!cursor.isAfterLast()) {
                                groupsNames.add(cursor.getString(cursor.getColumnIndex(NAME_GROUP)));
//                                groupsNames.add(cursor.getString(0));
                                cursor.moveToNext();
                            }
//                            Log.i(TAG,"" + groupsNames.size());
                            //Удаляю имена текущей группы/групп (Unify)

                            if (mGroupsForMerge != null){
                                for (Group g : mGroupsForMerge) {
                                    groupsNames.remove(g.getName());
                                }
                            }else groupsNames.remove(mGroup.getName());

                        }
                        allGroupsNames.addAll(groupsNames);

                        return true;

                    case SAVE_WORDS:
                        String orig;
                        String trans;
                        String transcript;
                        String comment;
                        int isMultiTrans;

                        for (int i = 0; i < mWords.size(); i++) {
                            UUID id = mWords.get(i).getId();
                            orig = mWords.get(i).getOriginal().toLowerCase().trim();
                            transcript = mWords.get(i).getTranscript().toLowerCase().trim();
                            trans = mWords.get(i).getTranslate().toLowerCase().trim();
                            comment = mWords.get(i).getComment().toLowerCase().trim();
                            isMultiTrans = mWords.get(i).hasMultiTrans();

                            System.out.println(trans);
                            cursor = queryTable(db,
                                    WORDS,
                                    null,
                                    Cols.UUID + " = ?",
                                    new String[]{id.toString()});
                            if (cursor.getCount() != 0) {
                                db.update(WORDS,
                                        createWordsValue(id, mGroup.getName(), orig, trans, transcript,comment, isMultiTrans),
                                        Cols.UUID + " = ?",
                                        new String[]{id.toString()});
                            } else {
                                db.insert(
                                        WORDS,
                                        null,
                                        createWordsValue(id, mGroup.getName(), orig, trans, transcript,comment, isMultiTrans)
                                );
                            }
                        }
                        if (mGroupsForMerge != null){// Removing before merge
                            for (Group g : mGroupsForMerge) {
                                db.delete(GROUPS,
                                        DbSchema.Tables.Cols.UUID + " = ?",
                                        new String[]{g.getId().toString()});
                            }
                        }
                        return true;

                    case GET_WORDS:
                        cursor = queryTable(
                                db,
                                WORDS,
                                null,
                                NAME_GROUP + " = ?",
                                new String[]{mGroup.getName()}
                        );

                        if (cursor.getCount() != 0) {
                            cursor.moveToFirst();
                            //TODO: реализовать порционную прогрузку
                            words = new ArrayList<>();
                            while (!cursor.isAfterLast()) {
                                words.add(cursor.getWord());
                                cursor.moveToNext();
                            }
                            if (sortIsNeed) {
                                Collections.sort(words);
                                mWords.addAll(words);
                            }else {
                                mWordsToLearn = new ArrayList<>();
                                mWordsToLearn.addAll(words);
                            }
                        }
                        return true;

                    case MERGE_GROUPS:
                        for (Group g : mGroupsForMerge) {
                            cursor = queryTable(
                                    db,
                                    WORDS,
                                    null,
                                    NAME_GROUP + " = ?",
                                    new String[]{g.getName()}
                            );

                            if (cursor.getCount() != 0) {

                                cursor.moveToFirst();
                                words = new ArrayList<>();
                                while (!cursor.isAfterLast()) {
                                    words.add(cursor.getWord());
                                    cursor.moveToNext();
                                }
                                mWords.addAll(words);
                            }
                            Collections.sort(mWords);
                        }

                        return true;

                    case REMOVE_WORD:
                        wordsRemove = new ArrayList<>();
                        for (int i = 0; i < mAdapter.mSelectionsArray.size(); i++) {
                            if (mAdapter.mSelectionsArray.valueAt(i)) {
                                for (Word w : mWords) {
                                    if (w.getIdString().equals(mAdapter.mSelectionsArray.keyAt(i))){
                                        wordsRemove.add(w);
                                    }
                                }
                            }
                        }
                        for(Word w : wordsRemove) {
                            int i = mWords.indexOf(w);
                            cursor = queryTable(db,
                                    WORDS,
                                    null,
                                    Cols.UUID + " = ?",
                                    new String[]{mWords.get(i).getIdString()});
                            if (cursor.getCount() !=0) {
                                db.delete(WORDS,
                                        Cols.UUID + " = ?",
                                        new String[]{mWords.get(i).getIdString()});
                            }
                        }
                        return true;
                }
                cursor.close();
                db.close();
            }catch (Exception ex){
                ex.printStackTrace();
            }

            return false;
        }
        @Override
        public void onPost(boolean b) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            Intent intent = null;

            switch (cmd){
                case SAVE_WORDS:

                    if (!b){
                        Log.i(TAG,"ERROR in onPOST");
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getActivity(), getString(R.string.saved_succes_toast), Toast.LENGTH_SHORT).show();
                    }
                    isChanged = true;
                    if (isCreated || mGroupsForMerge != null){
                        getActivity().setResult(RESULT_OK, new Intent().putExtra(CHANGED_ITEM, mGroup));
                        activity.finish();
                    }

                    break;

                case REMOVE_WORD:
                    int j;
                    for (Word w : wordsRemove){
                        j = mAdapter.getList().indexOf(w);
                        mAdapter.notifyItemRemoved(j);
                        mWords.remove(w);
                        Log.i(TAG, "Word removed: " + w.getOriginal());
                    }
                    mAdapter.mSelectionsArray.clear();
                    for (int i = 0; i < mAdapter.getList().size(); i++) {
                        Word w = mAdapter.getList().get(i);
                        mAdapter.mSelectionsArray.put(w.getIdString(),false);
                    }
                    updateUI();
                    selectCount = 0;
                    break;

                case MERGE_GROUPS:
                    mAdapter.addList(mWords);
                case GET_WORDS:
                    if (sortIsNeed) {
                        updateUI();
                    }else {
                        sortIsNeed = true;
                        removeEmptyWords(mWordsToLearn);
                        if (mWordsToLearn.size() < 2){
                            String s = getString(R.string.min_word_list_size,2);
                            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        intent = LearnStartActivity.newIntent(getContext(),mGroup,mWordsToLearn);
                        mWordsToLearn = null; // Чтобы очистить память
                        startActivity(intent);
                    }
                    break;

            }
        }
    }

    public class WordItemHelperCallBack extends ItemTouchHelper.Callback{

        private ItemTouchHelperAdapter mHelperAdapter;

        public WordItemHelperCallBack(ItemTouchHelperAdapter helperAdapter) {
            mHelperAdapter = helperAdapter;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView mRecyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int swipeFlag = ItemTouchHelper.START|ItemTouchHelper.END;
            return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE,swipeFlag);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView mRecyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            mHelperAdapter.onItemDismiss(viewHolder,i);
        }

    }

    public class MyTextWatch implements TextWatcher{
        static final int ORIGINAL = 0;
        static final int TRANSLATE = 1;
        static final int TRANSCRIPT = 2;
        static final int COMMENT = 3;

        private RecyclerView.ViewHolder holder;
        private int type;

        public MyTextWatch(RecyclerView.ViewHolder holder, int type) {
            this.holder = holder;
            this.type = type;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int position = holder.getAdapterPosition();
            Word word = mWords.get(position);

            switch (type){
                case ORIGINAL:
                    if (!word.getOriginal().equals(s.toString())) {
                        word.setOriginal(s.toString().trim());
                    }
                    break;
                case TRANSLATE:
                    switch (word.hasMultiTrans()){
                        case Word.TRUE:
                            if (!word.getMultiTranslate().equals(s.toString())) {
                                word.setTranslate(s.toString().trim());
                            }
                            break;
                        case Word.FALSE:
                            if (!word.getTranslate().equals(s.toString())) {
                                word.setTranslate(s.toString().trim());
                            }
                            break;
                    }
                    break;
                case TRANSCRIPT:
                    if (!word.getTranscript().equals(s.toString())) {
                        word.setTranscript(s.toString().trim());
                    }
                    break;
                case COMMENT:
                    if (!word.getComment().equals(s.toString())) {
                        word.setComment(s.toString().trim());
                    }
                    break;
            }

        }
    }
}