package com.anadol.rememberwords.fragments;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.activities.LearnDetailActivity;
import com.anadol.rememberwords.myList.Group;
import com.anadol.rememberwords.myList.MyRecyclerAdapter;
import com.anadol.rememberwords.myList.MyViewHolder;
import com.anadol.rememberwords.myList.Word;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import static com.anadol.rememberwords.fragments.GroupListFragment.SELECT_LIST;
import static com.anadol.rememberwords.fragments.GroupListFragment.SELECT_MODE;


/**
 * A simple {@link Fragment} subclass.
 */
public class LearnStartFragment extends MyFragment implements View.OnClickListener{
    private static final String TAG = "learn_start";
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
    public static final int TRANSCRIPT = 1;
    public static final int TRANSLATE = 2;

    private Group mGroup;
    private ArrayList<Word> mWords;
    private ArrayList<Word> mSelectedWords;

    private ViewPager mViewPagerType;
    private ViewPager mViewPagerObject;

    /*private Button typeOne;
    private Button typeTwo;
    private Button typeThree;
*/
    private Switch switchOriginal;
    private Switch switchTranslate;
    private Switch switchTranscript;

    private Button startButton;
    private Spinner mSpinner;

    private int type;
    private int object;
    private boolean originalIsEmpty = false;
    private boolean transcriptIsEmpty = false;
    private boolean translateIsEmpty = false;
    private RecyclerView recyclerView;
    private EditText mEditText;
    private TextView countWords;


    public LearnStartFragment() {
        // Required empty public constructor
    }

    public static LearnStartFragment newInstance(Group group, ArrayList<Word> mWords) {

        Bundle args = new Bundle();
        args.putParcelable(GROUP,group);
        args.putParcelableArrayList(WORDS,mWords);
        LearnStartFragment fragment = new LearnStartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TYPE,type);
        outState.putInt(OBJECT,object);
        outState.putBoolean(SELECT_MODE, selectMode);
        outState.putIntegerArrayList(SELECT_LIST, selectedList);
        outState.putParcelableArrayList(WORD_SELECTED,mSelectedWords);
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
        View view = inflater.inflate(R.layout.fragment_learn_start,container,false);

        mGroup = getArguments().getParcelable(GROUP);// возможно нигде не понадобиться
        mWords = getArguments().getParcelableArrayList(WORDS);



        if (savedInstanceState != null){
            /*type = savedInstanceState.getInt(TYPE);*/
            object = savedInstanceState.getInt(OBJECT);
            selectMode = savedInstanceState.getBoolean(SELECT_MODE);
            selectedList = savedInstanceState.getIntegerArrayList(SELECT_LIST);
            mSelectedWords = savedInstanceState.getParcelableArrayList(WORD_SELECTED);
        }else {
            type = TRUE_FALSE;
            object = ORIGINAL;
            selectMode = false;
            selectedList = new ArrayList<>();
            mSelectedWords = new ArrayList<>();
        }

        inflateMyView(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_group_detail,menu);

        menu.setGroupVisible(R.id.group_one,false);
        menu.setGroupVisible(R.id.group_two,selectMode);

        MenuItem remove = menu.findItem(R.id.menu_remove);
        remove.setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.menu_cancel:
                cancel();
                mSelectedWords.clear();
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
        }
        return super.onOptionsItemSelected(item);
    }


    private void inflateMyView(View view) {
//      System.out.println(mWords.size());

        mSpinner = view.findViewById(R.id.spinner);

        TextView textView = view.findViewById(R.id.type_test);
        textView.requestFocus();

        switchOriginal = view.findViewById(R.id.switch_original);
        switchTranscript = view.findViewById(R.id.switch_transcription);
        switchTranslate = view.findViewById(R.id.switch_translate);

        mViewPagerType = view.findViewById(R.id.view_pager_type);
        mViewPagerType.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return CardTypeTestFragment.newInstance(i,mWords.size());
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
                return 3;
            }
        });
        mViewPagerObject.setOffscreenPageLimit(5);
        switchEnabled(mViewPagerObject.getCurrentItem());

        startButton = view.findViewById(R.id.start_button);
        mEditText = view.findViewById(R.id.count_word_edit_text);
        setListeners();

        /*if (!isSelectMode()) {
            updateOptions(mWords);
        }*/

        FrameLayout frameLayout = view.findViewById(R.id.recycler_container);
        recyclerView = frameLayout.findViewById(R.id.recycler_detail);

        createAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        registerForContextMenu(recyclerView);
        recyclerView.setLongClickable(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));

        if (mSpinner.getSelectedItemPosition()==0 || mSpinner.getSelectedItemPosition() == 1){
            mEditText.setEnabled(false);
        }
        countWords = view.findViewById(R.id.count_text);
        String stringCount = getResources().getQuantityString(R.plurals.word_items, mWords.size(),mWords.size());
        countWords.setText(getResources().getString(R.string.word_count,stringCount));
    }

    private void switchEnabled(int i){
        switchOriginal.setEnabled(true);
        switchTranscript.setEnabled(true);
        switchTranslate.setEnabled(true);

        switch (i){
            case 0:
                switchOriginal.setEnabled(false);
                if (switchOriginal.isChecked())switchOriginal.setChecked(false);
                break;
            case 1:
                switchTranscript.setEnabled(false);
                if (switchTranscript.isChecked())switchTranscript.setChecked(false);
                break;
            case 2:
                switchTranslate.setEnabled(false);
                if (switchTranslate.isChecked())switchTranslate.setChecked(false);
                break;
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
                switchEnabled(i);
                updateUI();
            }
            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int count = 0;
                if (!mEditText.getText().toString().equals("")) {
                    count = Integer.valueOf(mEditText.getText().toString());
                }
                switch (position) {
                    case 0:
                        mEditText.setText("");
                        mEditText.setEnabled(false);
                        break;
                    case 1:
                        mEditText.setText("");
                        mEditText.setEnabled(false);
                        if (!isSelectMode()) {
                            setSelectMode(true);
                        }
                        break;
                    case 2:
                    case 3:
                        mEditText.setEnabled(true);
                        if (count > mWords.size()) {
                            count = mWords.size();
                            mEditText.setText(Integer.toString(count));
                        }
                        break;
                }
                if (mEditText.isEnabled()){
                    mEditText.requestFocus();
                }
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) return;

                int i = Integer.valueOf(s.toString());
                int min = 2;
                if (mViewPagerType.getCurrentItem() == QUIZ) min = 4;
                startButton.setEnabled(i >= min);
            }
        });

        CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateUI();
            }
        };

        switchOriginal.setOnCheckedChangeListener(switchListener);
        switchTranscript.setOnCheckedChangeListener(switchListener);
        switchTranslate.setOnCheckedChangeListener(switchListener);

        startButton.setOnClickListener(this);
    }

    @Override
    public void updateUI() {
        boolean isAllReady = true;
        View currentType = mViewPagerType.getChildAt(mViewPagerType.getCurrentItem());
        if (currentType == null){
            Toast.makeText(getContext(),"On a null object reference (for developers)",Toast.LENGTH_SHORT).show(); // Temp
        }else if (!currentType.isEnabled()){
            isAllReady = false;
        }

        View currentObject = mViewPagerObject.getChildAt(mViewPagerObject.getCurrentItem());

        if (currentObject == null){
            Toast.makeText(getContext(),"On a null object reference (for developers)",Toast.LENGTH_SHORT).show(); // Temp
        }else if (!currentObject.isEnabled()){
            isAllReady = false;
        }

        //Если ни один не выбран
        if (!switchOriginal.isChecked() &&
                !switchTranslate.isChecked() &&
                !switchTranscript.isChecked()) {
            isAllReady = false;
        }

        startButton.setEnabled(isAllReady);
    }

    private void removeWordsWithEmptyCells(int objectTests, ArrayList<Word> words){
        ArrayList<Word> tempList = new ArrayList<>(words);
        for (Word w : tempList){
            switch (objectTests){
                case ORIGINAL:
                    if (w.getOriginal().equals("")){words.remove(w);}
                    break;
                case TRANSCRIPT:
                    if (w.getTranscript().equals("")){words.remove(w);}
                    break;
                case TRANSLATE:
                    if (w.getTranslate().equals("")){words.remove(w);}
                    break;
            }
        }
    }

    private void createAdapter() {
        adapter = new MyRecyclerAdapter(mWords, R.layout.item_words_list_preview);
        adapter.setCreatorAdapter(new MyRecyclerAdapter.CreatorAdapter() {// ДЛЯ БОЛЬШЕЙ ГИБКОСТИ ТУТ Я РЕАЛИЗУЮ СЛУШАТЕЛЯ И МЕТОДЫ АДАПТЕРА
            @Override
            public void createHolderItems(MyViewHolder holder) {
                TextView original = holder.itemView.findViewById(R.id.text_question);
                TextView translate = holder.itemView.findViewById(R.id.text_answer);
                TextView transcription = holder.itemView.findViewById(R.id.text_transcription);
                CheckBox checkBox = holder.itemView.findViewById(R.id.checkBox);
                checkBox.setEnabled(false);
                holder.setViews(new View[]{original, translate, transcription,checkBox});
            }

            @Override
            public void bindHolderItems(MyViewHolder holder) {
                View[] views = holder.getViews();
                final int position = holder.getAdapterPosition();
                String origStr = ((ArrayList<Word>)adapter.getList()).get(position).getOriginal();
                String tranStr = ((ArrayList<Word>)adapter.getList()).get(position).getTranslate();
                String transcriptStr = ((ArrayList<Word>)adapter.getList()).get(position).getTranscript();

                TextView original = (TextView) views[0];
                original.setText(origStr);

                TextView translate = (TextView) views[1];
                translate.setText(tranStr);
                TextView transcription = (TextView) views[2];
                transcription.setText(transcriptStr);


                CheckBox checkBox = (CheckBox) views[3];
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
             }
        });
        adapter.setListener(new MyRecyclerAdapter.Listeners() {
            @Override
            public void onClick(View view, int position) {
                if (selectMode) {
                    View[] views = ((MyViewHolder)recyclerView.getChildViewHolder(view)).getViews();
                    CheckBox checkBox = (CheckBox) views[3];
                    Integer i = Integer.valueOf(position);
                    if (checkBox.isChecked()){
                        mSelectedWords.remove(mWords.get(position));
                        selectedList.remove(i);
                        view.setBackgroundColor(getResources().getColor(R.color.colorDefaultBackground));
                    }else {
                        selectedList.add(i);
                        mSelectedWords.add(mWords.get(position));
                        view.setBackgroundColor(getResources().getColor(R.color.colorSelected));
                    }
                    checkBox.setChecked(!checkBox.isChecked());
                    updateActionBarTitle();
                }
            }

            @Override
            public boolean onLongClick(View view, int position) {
                if (!isSelectMode()) {
                    selectedList.add(position);
                    mSelectedWords.add(mWords.get(position));
                    setSelectMode(true);
                    view.setBackgroundColor(getResources().getColor(R.color.colorSelected));
                    mSpinner.setSelection(1);
                    View[] views = ((MyViewHolder) recyclerView.getChildViewHolder(view)).getViews();
                    CheckBox checkBox = (CheckBox) views[3];
                    checkBox.setChecked(true);
                }
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

            /* На краях ViewPager getChildCount == 2
             * Пофиксил увеличением лимита загружаемых страниц setOffscreenPage(5)
             */

            Log.d(TAG, "Current " + mViewPagerType.getCurrentItem()+
                    " Count " + mViewPagerType.getChildCount());

            Intent intent = LearnDetailActivity.newIntent( // Тут предаются выбратнные атрибуты для начала теста
                    getContext(),
                    learnList,
                    mGroup,
                    mViewPagerType.getCurrentItem(),
                    mViewPagerObject.getCurrentItem(),
                    new boolean[]{switchOriginal.isChecked(),switchTranscript.isChecked(),switchTranslate.isChecked()});

            int count = 0;
            if (!mEditText.getText().toString().equals("")) {
                count = Integer.valueOf(mEditText.getText().toString());
                if (count > mWords.size()){
                    count = mWords.size();
                    mEditText.setText(Integer.toString(count));
                    mEditText.setSelection(mEditText.length());
                }
            }

            switch (mSpinner.getSelectedItemPosition()) {
                case 0:
                    learnList.addAll(mWords);
                    break;
                case 1:
                    if (selectedList.isEmpty() || selectedList.size() == 1) {
                        s = getString(R.string.select_some_word_else);
                        Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
                    } else {
                        learnList.addAll(mSelectedWords);
                    }
                    break;
                case 2:
                    if (count == 0) {
                        isAllReady = false;
                        s = getString(R.string.is_empty);
                        mEditText.setError(s);
                    } else {
                        for (int i = 0; i < count; i++) {
                            learnList.add(mWords.get(i));
                        }
                    }
                    break;
                case 3:
                    if (count == 0) {
                        isAllReady = false;
                        s = getString(R.string.is_empty);
                        mEditText.setError(s);
                    }else {
                        for (int i = 1; i <= count; i++) {
                            learnList.add(mWords.get(mWords.size() - i));
                        }
                        break;
                    }
            }


            // Если выбран один, но имеет пустые ячейки
            int i = 0;
            boolean[] booleans = {switchOriginal.isChecked(), switchTranslate.isChecked(), switchTranscript.isChecked()};
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
                    if (switchTranscript.isChecked()) {
                        if (w.getTranscript().equals("")) j++;
                    }
                }
                if (j != 0){
                    isAllReady = false;
                    Toast.makeText(getContext(),getString(R.string.your_used_object_have_empty_cells) , Toast.LENGTH_LONG).show();
                }
            }

            if (mViewPagerObject.getCurrentItem() == TRANSCRIPT){
                int learnSize = learnList.size();
                removeWordsWithEmptyCells(TRANSCRIPT,learnList);
                int min = 2;
                if (mViewPagerType.getCurrentItem() == QUIZ) min = 4;

                if (learnList.size() < min){
                    isAllReady = false;
                    s = getString(R.string.min_transcript_list_size,min);
                    Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
                }else if (isAllReady && learnList.size() != learnSize){
                    s = getString(R.string.word_without_transcription);
                    Toast toast = Toast.makeText(getContext(),s , Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP,toast.getXOffset(),toast.getYOffset());
                    toast.show();
//                    Snackbar.make(getView(),s,Snackbar.LENGTH_LONG).show();
                }
            }

            if (isAllReady) { // Выполнять, если все требования соблюдены
                startActivity(intent);
            }
        }
    }

}