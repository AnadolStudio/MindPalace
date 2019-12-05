package com.anadol.rememberwords.fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.anadol.rememberwords.myList.MyRecyclerAdapter;
import com.anadol.rememberwords.myList.MyViewHolder;
import com.anadol.rememberwords.myList.Word;
import com.dingmouren.layoutmanagergroup.slide.ItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static com.anadol.rememberwords.database.DbSchema.Tables.GROUPS;
import static com.anadol.rememberwords.database.DbSchema.Tables.WORDS;
import static com.anadol.rememberwords.fragments.DialogResult.RESULT;
import static com.anadol.rememberwords.fragments.GroupListFragment.*;
import static com.anadol.rememberwords.myList.Group.NON_COLOR;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupDetailFragment extends Fragment {
    private static final String TAG = "GroupDetailFragment";

    public static final String GROUP = "group";
    public static final String WORD_SAVE = "word_save";
    public static final String NAMES_ALL_GROUPS = "names_all_groups";
    public static final String POSITION = "position";
    public static final String IS_CREATED = "is_created";

    private static final String DIALOG_COLOR = "color";
    private static final String GRADIENT = "gradient";
    private static final int REQUEST_DRAWABLE = 1;
    private static final int REQUEST_TRANSLATE_RESULT = 4;
    private static final int REQUEST_MULTI_TRANSLATE = 2;

    private static final String GET_WORDS = "words";
    private static final String ADD_GROUP = "add_group";
    private static final String ADD_WORDS = "add_words";
    private static final String REMOVE_WORD = "remove_words";
    private static final String TYPE_SORT = "type_sort";
    private static final String UNIFY_GROUPS = "unify_groups";
    private static final String GET_GROUPS_NAME = "get_groups_name";


    private LabelEmptyList mLabelEmptyList;
    private RecyclerView mRecyclerView;
    private WordAdapter mAdapter;
    private Group mGroup;
    private ArrayList<Word> mWords;
    private ArrayList<Group> mGroups;
    private ArrayList<Integer> selectedList;

    private EditText nameGroup;
    //    private ImageButton addButton;
    private ImageView groupColor;
    private int[] colors;
    private ArrayList<String> allGroupsNames;
    private boolean isCreated;
    private boolean typeSort;
    private boolean sortIsNeed = true;
    protected boolean selectMode = false;
    private TextView countWords;


    public GroupDetailFragment() {
        // Required empty public constructor
    }

    // TODO: Для UNIFY необходимо добавить Dialog с цветами объединенных групп,
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

        Group group = new Group(UUID.randomUUID(), new int[]{ColorPicker.COLOR_START}, "");
        args.putBoolean(IS_CREATED,true);
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
        outState.putBoolean(SELECT_MODE, selectMode);
        outState.putIntegerArrayList(SELECT_LIST, selectedList);
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
            selectMode = savedInstanceState.getBoolean(SELECT_MODE);
            selectedList = savedInstanceState.getIntegerArrayList(SELECT_LIST);
            allGroupsNames = savedInstanceState.getStringArrayList(NAMES_ALL_GROUPS);
//            mySubtitleVisible = savedInstanceState.getBoolean(SUBTITLE);
        } else {
            mWords = new ArrayList<>();
            selectMode = false;
            selectedList = new ArrayList<>();
            allGroupsNames = new ArrayList<>();
//
        }

        //For UNIFY, selected groups;
        mGroups = getArguments().getParcelableArrayList(GROUPS);
        if (mWords.isEmpty() && mGroups != null){
            new WordBackground().execute(UNIFY_GROUPS);
        }

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
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),DividerItemDecoration.VERTICAL));

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
            mAdapter.setList(mWords);
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
                            mAdapter.setList(mWords);
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
        if (!isCreated && mWords.isEmpty()) {
            new WordBackground().execute(GET_WORDS);
        }else {
            updateUI();//При поворотах
            mAdapter.setList(mWords);
        }

        if (allGroupsNames == null){
            new WordBackground().execute(GET_GROUPS_NAME);
        }
    }

    @Override
    public void onDestroy() {
        new WordBackground().cancel(false);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_group_detail,menu);

        menu.setGroupVisible(R.id.group_one,!selectMode);
        menu.setGroupVisible(R.id.group_two,selectMode);

        MenuItem play = menu.findItem(R.id.menu_start);
        if (selectMode || mWords.size()<1 ){
            play.setVisible(false);
        }else {
            play.setVisible(true);
        }

       /* MenuItem sort = menuBottom.findItem(R.id.menu_sort);
        sort.setVisible(!selectMode);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sort:
                typeSort = !typeSort;
                mAdapter.sortList();
                return true;

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

            case R.id.menu_cancel:
                cancel();
                return true;

            case R.id.add_button:
                mWords.add(0,new Word(
                        UUID.randomUUID(),
                        "",
                        "",
                        "",
                        nameGroup.getText().toString(),
                        "",
                        Word.FALSE));
                mAdapter.notifyDataSetChanged();//Добавит ввод в начало ЛИСТА
                mLabelEmptyList.update();
                getActivity().invalidateOptionsMenu();
                updateWordCount();
                return true;

            case R.id.menu_select_all:
                if (selectedList.size() == mWords.size()){
                    selectedList.clear();
                }else {
                    selectedList.clear();
                    for (int i = 0; i < mWords.size(); i++) {
                        selectedList.add(i);
                    }
                }
                updateActionBarTitle();
                mAdapter.notifyDataSetChanged();
                return true;

            case R.id.menu_save:
                mGroup.setName(nameGroup.getText().toString());
                saveGroup();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveGroup() {
        String name = nameGroup.getText().toString();// тут вставить проверку на существование группы с таким именем
        if (!name.equals("") && !name.equals(" ") ) {
            if (!nameIsTaken(name)) {
                new WordBackground().execute(ADD_GROUP);
                new WordBackground().execute(ADD_WORDS);
            }else {nameGroup.setError(getString(R.string.name_is_taken));}
//                    sendResult(Activity.RESULT_OK,new Group[]{mGroup});
        } else {
            nameGroup.setError(getString(R.string.is_empty));
        }
    }

    private boolean nameIsTaken(String s){
        // TODO: может не успеть догрузить
        return allGroupsNames.contains(s);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
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
            /*case REQUEST_LEARN:
                Intent intent = LearnStartActivity.newIntent(getContext(),mGroup,mWords);
                startActivity(intent);
                break;*/
            case REQUEST_TRANSLATE_RESULT:
            case REQUEST_MULTI_TRANSLATE:
                int p = data.getIntExtra(POSITION,0);
                mAdapter.notifyItemChanged(p);
                break;
        }

    }

    private void createLearnFragment() {
        sortIsNeed = false;

        new WordBackground().execute(GET_WORDS);//Доделывает в Post
    }

    public void updateUI() {
        mLabelEmptyList.update();
        updateWordCount();
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.invalidateOptionsMenu();
    }

    private void updateWordCount() {
        String stringCount = getResources().getQuantityString(R.plurals.word_items, mWords.size(),mWords.size());
        countWords.setText(getResources().getString(R.string.word_count,stringCount));
    }

    private void menuSelected(){
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.invalidateOptionsMenu();
        mAdapter.notifyDataSetChanged();
        if (!selectMode) {
            selectedList.clear();
        }
        updateActionBarTitle();
    }

    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
        menuSelected();
    }

    private void updateActionBarTitle(){
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        if (!selectMode) {
            activity.getSupportActionBar().setTitle(getString(R.string.app_name));
            activity.getSupportActionBar().setSubtitle(null);
        }else {
            activity.getSupportActionBar().setTitle(String.valueOf(selectedList.size()));
        }
    }

    protected void cancel(){
        setSelectMode(false);
    }

    private void removeWord(){
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.invalidateOptionsMenu();
        if (!isCreated) {
            new WordBackground().execute(REMOVE_WORD);
        }else {
            removeFromAdapter();
        }
    }

    private void removeFromAdapter() {
        ArrayList<Word> wordsRemove = new ArrayList<>();
        for(Integer j :selectedList) {
            int i  = j;
            wordsRemove.add(mWords.get(i));
        }
        for (Word w :wordsRemove){
            mAdapter.notifyItemRemoved(mWords.indexOf(w));
            mWords.remove(w);
        }
        selectedList.clear();
        updateWordCount();
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
        ArrayList<Word> duplicate = new ArrayList<>(words);
        for (Word w : duplicate){
            int i = 0;
            if (w.getOriginal().equals("")){i++;}
            if (w.getTranscript().equals("")){i++;}
            if (w.getTranslate().equals("")){i++;}
            if (i>=2){
                words.remove(w);
            }
        }
    }


    public class WordAdapter extends RecyclerView.Adapter<WordHolder>
    implements ItemTouchHelperAdapter{
        private ArrayList<Word> mList;

        public WordAdapter(ArrayList<Word> list) {
            sortList();
            mList = list;
        }

        public WordAdapter() {
            mList = new ArrayList<>();
        }

        public void setList(ArrayList<Word> list) {
            sortList();
            mList = list;
            mRecyclerView.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {

                        @Override
                        public boolean onPreDraw() {
                            mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                            for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                                View v = mRecyclerView.getChildAt(i);
                                v.setAlpha(0.0f);
                                v.animate().alpha(1.0f)
                                        .setDuration(120)
                                        .setStartDelay(i * 50)
                                        .start();
                            }

                            return true;
                        }
                    });

            notifyDataSetChanged();
            mLabelEmptyList.update();
        }

        @NonNull
        @Override
        public WordHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_words_list,viewGroup,false);
            return new WordHolder(view);
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

                    if (!selectedList.contains(Integer.valueOf(position))) {

                        selectedList.add(position);
                        setSelectMode(true);
                        wordHolder.itemView.setBackgroundColor(getResources().getColor(R.color.colorSelected));


                        wordHolder.original.setEnabled(!selectMode);
                        wordHolder.transcription.setEnabled(!selectMode);
                        wordHolder.translate.setEnabled(!selectMode);
                        wordHolder.comment.setEnabled(!selectMode);

                        wordHolder.mCheckBox.setChecked(true);
                    }
                    mAdapter.notifyItemChanged(position);
                    break;
                case ItemTouchHelper.END://DialogTranslate
                    createDialogMultiTranslate(position);
                    break;
            }
//            mAdapter.notifyDataSetChanged();//tmp
        }

        public void sortList(){
            if (typeSort) {
                Collections.sort(mWords,new Word.OriginalCompare());
            }else {
                Collections.sort(mWords,new Word.TranslateCompare());
            }
        }
    }

    public class WordBackground extends DoInBackground {
        private String cmd;
        private MyCursorWrapper cursor;
        private SQLiteDatabase db;

        private ArrayList<Word> mWordsToLearn;

        @Override
        public Boolean doIn(String command) {
            ArrayList<Word> words;
            cmd = command;
            try {
                db = new DatabaseHelper(getContext()).getWritableDatabase();
//                System.out.println("NAME " + mGroup.getName());
                switch (command) {
                    case ADD_GROUP:
                        cursor = queryTable(db,
                                GROUPS,
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

                    case UNIFY_GROUPS:
                        for (Group g : mGroups) {
                            cursor = queryTable(
                                    db,
                                    WORDS,
                                    Cols.NAME_GROUP + " = ?",
                                    new String[]{g.getName()}
                            );

                            if (cursor.getCount() != 0) {

                                cursor.moveToFirst();
                                words = new ArrayList<>();
                                while (!cursor.isAfterLast()) {
                                    words.add(cursor.getWord());
                                    cursor.moveToNext();
                                }
                                if (sortIsNeed) {
                                    Collections.sort(words);
                                }
                                mWords.addAll(words);
                            }
                        }

                        return true;

                    case GET_GROUPS_NAME:
                        ArrayList<String> groupsNames = new ArrayList<>();
                        cursor = queryTable(
                                db,
                                GROUPS,
                                Cols.NAME_GROUP + " = ?",
                                null
                        );

                        if (cursor.getCount() != 0){
                            cursor.moveToFirst();
                            while (cursor.isAfterLast()) {
                                groupsNames.add(cursor.getString(0));
                                cursor.moveToNext();
                            }
                            //Удаляю имена текущей группы/групп (Unify)
                            groupsNames.remove(mGroup.getName());
                            if (mGroups != null){
                                for (Group g : mGroups) {
                                    groupsNames.remove(g);
                                }
                            }
                        }
                        allGroupsNames.addAll(groupsNames);

                        return true;

                    case ADD_WORDS:
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
//                            System.out.println(trans);
                            comment = mWords.get(i).getComment().toLowerCase().trim();
                            isMultiTrans = mWords.get(i).hasMultiTrans();

                            System.out.println(trans);
                            cursor = queryTable(db,
                                    WORDS,
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
                        if (mGroups != null){// Removing before unify
                            for (Group g : mGroups) {
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
                                Cols.NAME_GROUP + " = ?",
                                new String[]{mGroup.getName()}
                        );

                        if (cursor.getCount() != 0) {
                            cursor.moveToFirst();
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

                    case REMOVE_WORD:
                        ArrayList<Word> wordsRemove = new ArrayList<>();
                        for(Integer j :selectedList) {
                            int i = j;
                            wordsRemove.add(mWords.get(i));
                            cursor = queryTable(db,
                                    WORDS,
                                    Cols.UUID + " = ?",
                                    new String[]{mWords.get(i).getId().toString()});
                            if (cursor.getCount() !=0) {
                                db.delete(WORDS,
                                        Cols.UUID + " = ?",
                                        new String[]{mWords.get(i).getId().toString()});
                            }
                        }
                        return true;


                }
                cursor.close();
                db.close();
            }catch (Exception ex){
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        public void onPost() {
            switch (cmd){
                case ADD_WORDS:
                    try {
                        Toast toast = Toast.makeText(getActivity(), getString(R.string.saved_succes_toast), Toast.LENGTH_SHORT);// Ругается на эмуляторе (возможно из-за main шрифта)
                        toast.show();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    if (isCreated){
                        getActivity().finish();
                    }
                    break;

                case REMOVE_WORD:
                    removeFromAdapter();
                    break;

                case GET_WORDS:
                case UNIFY_GROUPS:
                    if (sortIsNeed) {
                        updateUI();
                    }else {
                        sortIsNeed = true;
                        removeEmptyWords(mWordsToLearn);
                        Intent intent = LearnStartActivity.newIntent(getContext(),mGroup,mWordsToLearn);
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

    public class WordHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        EditText original;
        TextView transcription;
        EditText translate;
        EditText comment;
        CheckBox mCheckBox;

        public WordHolder(@NonNull View itemView) {
            super(itemView);

            original = itemView.findViewById(R.id.text_original);
            transcription = itemView.findViewById(R.id.text_transcription);
            translate = itemView.findViewById(R.id.text_translate);
            comment = itemView.findViewById(R.id.edit_comment);
            mCheckBox = itemView.findViewById(R.id.checkBox);

            original.addTextChangedListener(new MyTextWatch(this,MyTextWatch.ORIGINAL));
            translate.addTextChangedListener(new MyTextWatch(this,MyTextWatch.TRANSLATE));
            comment.addTextChangedListener(new MyTextWatch(this,MyTextWatch.COMMENT));
            transcription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createDialogTranscript(WordHolder.this);
                }
            });

            itemView.setOnClickListener(this);
//            itemView.setOnLongClickListener(this);
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


            original.setText(origStr);
            transcription.setText(transcriptStr);
            translate.setText(tranStr);
            comment.setText(commentStr);

            original.setSelection(original.length());
            translate.setSelection(translate.length());
            comment.setSelection(comment.length());

            original.setEnabled(!selectMode);
            transcription.setEnabled(!selectMode);
            translate.setEnabled(word.hasMultiTrans() == Word.FALSE && !selectMode);
            comment.setEnabled(!selectMode);




            if (selectMode){
                mCheckBox.setVisibility(View.VISIBLE);
                if (selectedList.indexOf(position) != -1){
//                        System.out.println("position " + position);
                    mCheckBox.setChecked(true);
                    itemView.setBackgroundColor(getResources().getColor(R.color.colorSelected));
                }else {
                    mCheckBox.setChecked(false);
                    itemView.setBackgroundColor(getResources().getColor(R.color.colorDefaultBackground));
                }
            }else {
                mCheckBox.setChecked(false);
                mCheckBox.setVisibility(View.GONE);
                itemView.setBackgroundColor(getResources().getColor(R.color.colorDefaultBackground));
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (selectMode) {
                Integer i = Integer.valueOf(position);
                if (mCheckBox.isChecked()){
                    selectedList.remove(i);
                    view.setBackgroundColor(getResources().getColor(R.color.colorDefaultBackground));
                }else {
                    selectedList.add(i);
                    view.setBackgroundColor(getResources().getColor(R.color.colorSelected));
                }
                mCheckBox.setChecked(!mCheckBox.isChecked());
                updateActionBarTitle();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    public class MyTextWatch implements TextWatcher{
        static final int ORIGINAL = 0;
        static final int TRANSLATE = 1;
        static final int COMMENT = 2;

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

            switch (type){
                case ORIGINAL:
                    mWords.get(position).setOriginal(s.toString().trim());
                    break;
                case TRANSLATE:
                    mWords.get(position).setTranslate(s.toString().trim());
                    break;
                case COMMENT:
                    mWords.get(position).setComment(s.toString().trim());
                    break;
            }

        }
    }
}