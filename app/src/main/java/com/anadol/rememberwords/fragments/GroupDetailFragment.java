package com.anadol.rememberwords.fragments;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static com.anadol.rememberwords.database.DbSchema.Tables.GROUPS;
import static com.anadol.rememberwords.database.DbSchema.Tables.WORDS;
import static com.anadol.rememberwords.fragments.DialogMultiTranslate.MULTI_TEXT;
import static com.anadol.rememberwords.fragments.DialogResult.RESULT;
import static com.anadol.rememberwords.fragments.DialogTranscript.TRANSCRIPT;
import static com.anadol.rememberwords.fragments.GroupListFragment.*;
import static com.anadol.rememberwords.myList.Group.NON_COLOR;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupDetailFragment extends MyFragment {
    public static final String GROUP = "group";
    public static final String WORD_SAVE = "word_save";
    public static final String NAMES_ALL_GROUPS = "names_all_groups";
    public static final String POSITION = "position";
    public static final String IS_CREATED = "is_created";
    private static final String DIALOG_COLOR = "color";
    private static final String GRADIENT = "gradient";
    private static final int REQUEST_DRAWABLE = 1;
    private static final int POSITION_NULL = 0;
    private static final int REQUEST_TRANSLATE_RESULT = 4;
    private static final int REQUEST_MULTI_TRANSLATE = 2;

    private static final String GET_WORDS = "words";
    private static final String ADD_GROUP = "add_group";
    private static final String ADD_WORDS = "add_words";
    private static final String REMOVE_WORD = "remove_words";
    private static final String TYPE_SORT = "type_sort";
    private static final String UNIFY_GROUPS = "unify_groups";
    private static final String FOCUS_EDIT_SAVE = "focus_edit_save";


    private LabelEmptyList sLabelEmptyList;
    private RecyclerView recyclerView;
    private Group mGroup;
    private ArrayList<Word> mWords;
    private ArrayList<Group> mGroups;

    private EditText nameGroup;
    private EditText tempEdit;
    //    private ImageButton addButton;
    private ImageView groupColor;
    private int[] colors;
    private int focusTranscriptEdit;
    private String[] allNames;
    private boolean isCreated;
    private boolean typeSort;
    private boolean sortIsNeed = true;
    private TextView countWords;

    public GroupDetailFragment() {
        // Required empty public constructor
    }

    public static GroupDetailFragment newInstance(Group group, String[] names) {

        Bundle args = new Bundle();
        if (group == null) {
            group = new Group(UUID.randomUUID(), new int[]{Color.RED,Color.GREEN,Color.BLUE}, "");
            args.putBoolean(IS_CREATED,true);
        }
        args.putParcelable(GROUP, group);
        args.putStringArray(NAMES_ALL_GROUPS, names);
        GroupDetailFragment fragment = new GroupDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static GroupDetailFragment newInstance(@NonNull ArrayList<Group> groups, String[] names) {
        Bundle args = new Bundle();

        Group group = new Group(UUID.randomUUID(), new int[]{ColorPicker.COLOR_START}, "");
        args.putBoolean(IS_CREATED,true);
        args.putParcelable(GROUP, group);
        args.putStringArray(NAMES_ALL_GROUPS, names);
        args.putParcelableArrayList(GROUPS, groups);
        GroupDetailFragment fragment = new GroupDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(WORD_SAVE, mWords);
        outState.putInt(FOCUS_EDIT_SAVE,focusTranscriptEdit);
        outState.putIntArray(GRADIENT, colors);// key GRADIENT in other places
        outState.putBoolean(IS_CREATED,isCreated);
        outState.putBoolean(TYPE_SORT,typeSort);
        outState.putBoolean(SELECT_MODE, selectMode);
        outState.putIntegerArrayList(SELECT_LIST, selectedList);
        outState.putStringArray(NAMES_ALL_GROUPS, allNames);

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
        nameGroup.setSelection(mGroup.getName().length());
        /*nameGroup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mGroup.setName(nameGroup.getText().toString());
            }
        });*/

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
            allNames = savedInstanceState.getStringArray(NAMES_ALL_GROUPS);
            focusTranscriptEdit = savedInstanceState.getInt(FOCUS_EDIT_SAVE);
//            mySubtitleVisible = savedInstanceState.getBoolean(SUBTITLE);
        } else {
            mWords = new ArrayList<>();
            selectMode = false;
            selectedList = new ArrayList<>();
            allNames = getArguments().getStringArray(NAMES_ALL_GROUPS);
            focusTranscriptEdit = -1;
        }


        /** For UNIFY selected groups;
         *
         */
        mGroups = getArguments().getParcelableArrayList(GROUPS);
        if (mWords.isEmpty() && mGroups!=null){
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
        recyclerView = frameLayout.findViewById(R.id.recycler_detail);


        createAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        registerForContextMenu(recyclerView);
        recyclerView.setLongClickable(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SimpleItemHelperCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);


        sLabelEmptyList = new LabelEmptyList(
                getContext(),
                frameLayout,
                adapter);

        countWords = view.findViewById(R.id.count_text);
        return view;
    }

    private boolean nameIsTaken(String s){
        String[] names = allNames;
        for (String n :names){
            if (s.equals(n)){
                return true;
            }
        }

        return false;
    }

    private void saveGroup() {
        String name = nameGroup.getText().toString();// тут вставить проверку на существование группы с таким именем
        if (!name.equals("") && !name.equals(" ") ) {
            if (!nameIsTaken(name)) {
                new WordBackground().execute(ADD_GROUP);
                new WordBackground().execute(ADD_WORDS);
            }else {nameGroup.setError("The name is already taken");}
//                    sendResult(Activity.RESULT_OK,new Group[]{mGroup});
        } else {
            nameGroup.setError("Is Empty");
        }
    }
    @Override
    public void onPause() {
        super.onPause();
//        System.out.println("onPause "+mGroup.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isCreated && mWords.isEmpty()) {
            new WordBackground().execute(GET_WORDS);
        }else {
            updateUI();
        }
//        System.out.println("updateUI "+mGroup.getName());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_group_detail,menu);

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
                adapter.sortList();
                return true;

            case R.id.menu_start:
                if (!(mWords.size()<=1)) {
                    createLearnFragment();
                }else {
                    Toast.makeText(getContext(),"Words list is empty",Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.menu_remove:
                removeWord();
                return true;

            case R.id.menu_cancel:
                cancel();
                return true;

            case R.id.add_button:
                mWords.add(new Word(
                        UUID.randomUUID(),
                        "",
                        "",
                        "",
                        nameGroup.getText().toString(),
                        "",
                        Word.FALSE));
                adapter.sortList();
                adapter.notifyDataSetChanged();//Вобавит ввод в начало ЛИСТА
                sLabelEmptyList.update();
                getActivity().invalidateOptionsMenu();
                updateWordCount();
                return true;

            case R.id.menu_select_all:
//                System.out.println("selectedList.size() == mGroups.size()) " + selectedList.size() +" "+ mGroups.size());
                if (selectedList.size() == mWords.size()){
                    selectedList.clear();
                }else {
                    selectedList.clear();
                    for (int i = 0; i < mWords.size(); i++) {
                        selectedList.add(i);
                    }
                }
                updateActionBarTitle();
                adapter.notifyDataSetChanged();
                return true;

            case R.id.menu_save:
                mGroup.setName(nameGroup.getText().toString());
                saveGroup();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createLearnFragment() {
        /*FragmentManager fm = getFragmentManager();
        DialogFragment dialog = new DialogLearn();
        dialog.setTargetFragment(GroupDetailFragment.this, REQUEST_LEARN);
        dialog.show(fm, DIALOG_LEARN);*/
        mWords = new ArrayList<>();
        sortIsNeed = false;
        WordBackground wordBackground = new WordBackground();
        wordBackground.execute(GET_WORDS);//Доделывает в Post
    }

    public void updateUI() {
        adapter.setList(mWords);
        adapter.notifyDataSetChanged();
        sLabelEmptyList.update();
        updateWordCount();
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.invalidateOptionsMenu();
    }

    private void updateWordCount() {
        String stringCount = getResources().getQuantityString(R.plurals.word_items, mWords.size(),mWords.size());
        countWords.setText(getResources().getString(R.string.word_count,stringCount));
    }

    private void removeWord(){
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.invalidateOptionsMenu();
        if (!isCreated) {new WordBackground().execute(REMOVE_WORD);
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
            adapter.notifyItemRemoved(mWords.indexOf(w));
            mWords.remove(w);
        }
        selectedList.clear();
        updateWordCount();
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
                tempEdit.setText(data.getStringExtra(TRANSCRIPT));
                tempEdit.setSelection(tempEdit.length());
                tempEdit = null;
                focusTranscriptEdit = -1;
                break;
            case REQUEST_MULTI_TRANSLATE:
                int p = data.getIntExtra(POSITION,0);
                mWords.set(p,(Word)data.getParcelableExtra(MULTI_TEXT));
//                System.out.println(mWords.get(p).getTranslate());
                adapter.notifyItemChanged(p);
//                Toast.makeText(getContext(), "It is work", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void createAdapter() {

        adapter = new MyRecyclerAdapter(mWords, R.layout.item_words_list);
        adapter.setCreatorAdapter(new MyRecyclerAdapter.CreatorAdapter() {// ДЛЯ БОЛЬШЕЙ ГИБКОСТИ ТУТ Я РЕАЛИЗУЮ СЛУШАТЕЛЯ И МЕТОДЫ АДАПТЕРА
            @Override
            public void createHolderItems(final MyViewHolder holder) { // TODO: Появиться ли тут утечка памяти?
                EditText original = holder.itemView.findViewById(R.id.text_question);
                EditText translate = holder.itemView.findViewById(R.id.text_answer);
                EditText transcription = holder.itemView.findViewById(R.id.text_transcription);
                EditText comment = holder.itemView.findViewById(R.id.edit_comment);

                original.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        mWords.get(holder.getAdapterPosition()).setOriginal(s.toString().trim());
//                        System.out.println(POSITION +" "+ holder.getAdapterPosition() +" "+ s);
                    }
                });
                translate.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        mWords.get(holder.getAdapterPosition()).setTranslate(s.toString().trim());
//                        System.out.println(s);
                    }
                });
                transcription.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        mWords.get(holder.getAdapterPosition()).setTranscript(s.toString().trim());
//                        System.out.println( (s.toString().trim()));
                    }
                });
                comment.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        mWords.get(holder.getAdapterPosition()).setComment(s.toString().trim());
//                        System.out.println( (s.toString().trim()));
                    }
                });

                //При первом нажатии на EditText
                transcription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus){
                            focusTranscriptEdit = holder.getAdapterPosition();
                            tempEdit = (EditText)v;
//                            System.out.println("Focus +");
                            DialogTranscript dialogResult = DialogTranscript.newInstance(((EditText) v).getText().toString());
                            dialogResult.setTargetFragment(GroupDetailFragment.this,REQUEST_TRANSLATE_RESULT);
                            FragmentManager fragmentManager = getFragmentManager();
                            dialogResult.show(fragmentManager,RESULT);
                        }
                    }
                });
                //При втором нажатии на EditText
                transcription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        focusTranscriptEdit = holder.getAdapterPosition();
                        tempEdit = (EditText)v;
//                        System.out.println("Click +");
                        DialogTranscript dialogResult = DialogTranscript.newInstance(((EditText) v).getText().toString());
                        dialogResult.setTargetFragment(GroupDetailFragment.this,REQUEST_TRANSLATE_RESULT);
                        FragmentManager fragmentManager = getFragmentManager();
                        dialogResult.show(fragmentManager,RESULT);
                    }
                });
                CheckBox checkBox = holder.itemView.findViewById(R.id.checkBox);
                checkBox.setEnabled(false);
                holder.setViews(new View[]{original, translate, transcription, comment,checkBox});
            }

            @Override
            public void bindHolderItems(final MyViewHolder holder) {
                // TODO: если слово имеет множественныей перевод translate.setEnabled(false);
                View[] views = holder.getViews();
                int position = holder.getAdapterPosition();
                Word word = ((ArrayList<Word>)adapter.getList()).get(position);
                String origStr = word.getOriginal();
                String transcriptStr = word.getTranscript();
                String tranStr;
                if (word.getIsMultiTrans() == Word.FALSE) {
                    tranStr = word.getTranslate();
                }else {
                    tranStr = word.getMultiTranslate();
                }
                String commentStr = word.getComment();

                EditText original = (EditText) views[0];
                EditText translate = (EditText) views[1];
                EditText transcription = (EditText) views[2];
                EditText comment = (EditText) views[3];


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    transcription.setShowSoftInputOnFocus(false);
                } else {
                    transcription.setTextIsSelectable(true);
                    //N.B. Accepting the case when non editable text will be selectable
                }
                if (focusTranscriptEdit == position) {
                    transcription.requestFocus();
                    tempEdit = transcription;
//                    System.out.println("Request focus +");
                }

                original.setText(origStr);
                original.setSelection(original.length());
                original.setEnabled(!selectMode);

                translate.setText(tranStr);
                translate.setSelection(translate.length());
                translate.setEnabled(!selectMode);

                transcription.setText(transcriptStr);
                transcription.setSelection(transcription.length());
                transcription.setEnabled(!selectMode);

                comment.setText(commentStr);
                comment.setSelection(comment.length());
                comment.setEnabled(!selectMode);

                CheckBox checkBox = (CheckBox) views[4];
                if (selectMode){
                    checkBox.setVisibility(View.VISIBLE);
                    if (selectedList.indexOf(position) != -1){
//                        System.out.println("position " + position);
                        checkBox.setChecked(true);
                        holder.itemView.setBackgroundColor(getResources().getColor(R.color.colorSelected));
                    }else {
                        checkBox.setChecked(false);
                        holder.itemView.setBackgroundColor(getResources().getColor(R.color.colorDefaultBackground));
                    }
                }else {
                    checkBox.setChecked(false);
                    checkBox.setVisibility(View.GONE);
                    holder.itemView.setBackgroundColor(getResources().getColor(R.color.colorDefaultBackground));
                }
            }
            @Override
            public void myOnItemDismiss(int position, int flag) {
                switch (flag){
                    case ItemTouchHelper.START://Выделить
                        
                        selectedList.add(position);
                        setSelectMode(true);
                        View view = recyclerView.getChildAt(position);
                        view.setBackgroundColor(getResources().getColor(R.color.colorSelected));

                        View[] views = ((MyViewHolder)recyclerView.getChildViewHolder(view)).getViews();
                        EditText original = (EditText) views[0];
                        EditText translate = (EditText) views[1];
                        EditText transcription = (EditText) views[2];
                        EditText comment = (EditText) views[3];

                        original.setEnabled(!selectMode);
                        transcription.setEnabled(!selectMode);
                        translate.setEnabled(!selectMode);
                        comment.setEnabled(!selectMode);

                        CheckBox checkBox = (CheckBox) views[4];
                        checkBox.setChecked(true);
                        break;
                    case ItemTouchHelper.END://DialogTranslate
                        FragmentManager fm = getFragmentManager();
                        DialogMultiTranslate dialogTranslate = DialogMultiTranslate.newInstance(mWords.get(position),position);
                        dialogTranslate.setTargetFragment(GroupDetailFragment.this,REQUEST_MULTI_TRANSLATE);
                        dialogTranslate.show(fm,RESULT);
                        break;
                }
                adapter.notifyDataSetChanged();//tmp
            }
        });
        adapter.setListener(new MyRecyclerAdapter.Listeners() {
            @Override
            public void onClick(View view, int position) {
                if (selectMode) {
                    View[] views = ((MyViewHolder)recyclerView.getChildViewHolder(view)).getViews();
                    CheckBox checkBox = (CheckBox) views[4];
                    Integer i = Integer.valueOf(position);
                    if (checkBox.isChecked()){
                        selectedList.remove(i);
                        view.setBackgroundColor(getResources().getColor(R.color.colorDefaultBackground));
                    }else {
                        selectedList.add(i);
                        view.setBackgroundColor(getResources().getColor(R.color.colorSelected));
                    }
                    checkBox.setChecked(!checkBox.isChecked());
                    updateActionBarTitle();
                }
            }

            @Override
            public boolean onLongClick(View view, int position) {
                return false;

            }
        });
        adapter.setSortItems(new MyRecyclerAdapter.SortItems() {
            @Override
            public void sortList() {
                if (typeSort) {
                    Collections.sort(mWords,new Word.OriginalCompare());
                }else {
                    Collections.sort(mWords,new Word.TranslateCompare());
                }
            }
        });
    }

    public class WordBackground extends DoInBackground {// TODO: Возможная утечка памяти
        String cmd;
        MyCursorWrapper cursor;
        SQLiteDatabase db;

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
                            }
                            mWords.addAll(words);
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
                            isMultiTrans = mWords.get(i).getIsMultiTrans();

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
                        if (mGroups !=null){
                            for (Group g : mGroups) {
                                db.delete(GROUPS,
                                        DbSchema.Tables.Cols.UUID + " = ?",
                                        new String[]{g.getId().toString()});
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
                        Intent intent = LearnStartActivity.newIntent(getContext(),mGroup,mWords);
                        startActivity(intent);
                    }
                    break;

            }
        }
    }

}