package com.anadol.rememberwords.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.ScrollView;
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

import java.util.ArrayList;

import static com.anadol.rememberwords.fragments.GroupListFragment.SELECT_LIST;
import static com.anadol.rememberwords.fragments.GroupListFragment.SELECT_MODE;


/**
 * A simple {@link Fragment} subclass.
 */
public class LearnStartFragment extends MyFragment {
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

    private Button start;
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

    @Override
    public void updateUI() {
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
        /*switch (type){
            case TRUE_FALSE:
                typeOne.setEnabled(false);
                break;
            case ANSWER_QUESTION:
                typeTwo.setEnabled(false);
                break;
            case QUIZ:
                typeThree.setEnabled(false);
                break;
        }*/

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_group_detail,menu);

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
                updateOptions(mWords);
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
        /*typeOne = view.findViewById(R.id.true_false_button);
        typeTwo = view.findViewById(R.id.answer_question_button);
        typeThree = view.findViewById(R.id.quiz_button);*/

        switchOriginal = view.findViewById(R.id.switch_original);
        switchTranscript = view.findViewById(R.id.switch_transcription);
        switchTranslate = view.findViewById(R.id.switch_translate);

        // TODO: Добавить строку состояния как в WhatsApp для фрагментов
        mViewPagerType = view.findViewById(R.id.view_pager_type);
        mViewPagerType.setAdapter(new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return CardTypeTestFragment.newInstance(i,mWords.size());
            }

            @Override
            public int getCount() {
                return 3;
            }
        });

        mViewPagerObject = view.findViewById(R.id.view_pager_object);
        mViewPagerObject.setAdapter(new FragmentStatePagerAdapter(getActivity().getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return CardObjectTestFragment.newInstance(i, mWords);
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        switchEnabled(mViewPagerObject.getCurrentItem());
        mViewPagerObject.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switchEnabled(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        start = view.findViewById(R.id.start_button);
        setListeners();

        if (!isSelectMode()) {
            updateOptions(mWords);
        }else {
//            updateOptions();
        }

        FrameLayout frameLayout = view.findViewById(R.id.recycler_container);
        recyclerView = frameLayout.findViewById(R.id.recycler_detail);

        createAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        registerForContextMenu(recyclerView);
        recyclerView.setLongClickable(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));


        mEditText = view.findViewById(R.id.count_word_edit_text);
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
//        ButtonListener buttonListener = new ButtonListener();
        /*typeOne.setOnClickListener(buttonListener);
        typeTwo.setOnClickListener(buttonListener);
        typeThree.setOnClickListener(buttonListener);*/

      /*  SwitchListener switchListener = new SwitchListener();
         switchOriginal.setOnCheckedChangeListener(switchListener);
         switchTranscript.setOnCheckedChangeListener(switchListener);
         switchTranslate.setOnCheckedChangeListener(switchListener);*/

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast toast = null;
                int count = 0;
                if (!mEditText.getText().toString().equals("")) {
                    count = Integer.valueOf(mEditText.getText().toString());
                }
                switch (position) {
                    case 0:
                        mEditText.setEnabled(false);
                        break;
                    case 1:
                        mEditText.setEnabled(false);
                        if (!isSelectMode()) {
                            setSelectMode(true);
                            updateOptions(mSelectedWords);
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAllReady = true;
                ArrayList<Word> learnList = new ArrayList<>();

                View currentType = mViewPagerType.getChildAt(mViewPagerObject.getCurrentItem());
                if (!currentType.isEnabled()){
                    Toast.makeText(getContext(), getString(R.string.enabled_type), Toast.LENGTH_SHORT).show();//temp
                    return;
                }

                View currentObject = mViewPagerObject.getChildAt(mViewPagerObject.getCurrentItem());
                if (!currentObject.isEnabled()){
                    Toast.makeText(getContext(), getString(R.string.enabled_object), Toast.LENGTH_SHORT).show();//temp
                    return;
                }
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
                    if (count>mWords.size()){
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
                            try {
                                Toast.makeText(getContext(), "Please select some words else", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            learnList.addAll(mSelectedWords);
                        }
                        break;
                    case 2:
                        if (count == 0) {
                            isAllReady = false;
                            mEditText.setError("is empty");
                        } else {
                            for (int i = 0; i <= count; i++) {
                                learnList.add(mWords.get(i));
                            }
                        }
                        break;
                    case 3:
                        if (count == 0) {
                            isAllReady = false;
                            mEditText.setError("is empty");
                        }else {
                            for (int i = 1; i <= count; i++) {
                                learnList.add(mWords.get(mWords.size() - i));
                            }
                            break;
                        }
                }

                if (!switchOriginal.isChecked() &&
                        !switchTranslate.isChecked() &&
                        !switchTranscript.isChecked())
                {
                    isAllReady = false;
                    Toast.makeText(getContext(), "No one used object is selected", Toast.LENGTH_LONG).show();
                }
                if (isAllReady) { // Выполнять если все требования соблюдены
                    startActivity(intent);
                }
            }
        });
    }

    private void updateOptions(ArrayList<Word> list){
        ArrayList<Word> removeWords = new ArrayList<>();
        originalIsEmpty = false;
        transcriptIsEmpty = false;
        translateIsEmpty = false;
        for (Word w :list){
            int i = 0;
            if (w.getOriginal()== null || w.getOriginal().equals("")){
                i++;
            }
            if (w.getTranscript()== null || w.getTranscript().equals("")){
                i++;
            }
            if (w.getTranslate()== null || w.getTranslate().equals("")){
                i++;
            }

            if (i>=2){
                removeWords.add(w);
            }else {
                Log.d(TAG,"before "+originalIsEmpty+" "+ translateIsEmpty +" "+ transcriptIsEmpty);
                Log.d(TAG,w.getOriginal()+" "+ w.getTranslate() +" "+ w.getTranscript());
                if (w.getOriginal()== null || w.getOriginal().equals("")){
                    originalIsEmpty = true;
                }
                if (w.getTranscript()== null || w.getTranscript().equals("")){
                    transcriptIsEmpty = true;
                }
                if (w.getTranslate()== null || w.getTranslate().equals("")){
                    translateIsEmpty = true;
                }
            }
        }
       /* for (Word r :removeWords){
            list.remove(r);
        }
        if (list.size()<3){
            typeThree.setVisibility(View.GONE);
        }else {
            typeThree.setVisibility(View.VISIBLE);
        }*/

       /* if (originalIsEmpty) {
            switchOne.setChecked(false);
        }

        Log.d(TAG,originalIsEmpty+" "+ translateIsEmpty +" "+ transcriptIsEmpty);
        switchOne.setEnabled(!originalIsEmpty);
        switchTwo.setEnabled(!translateIsEmpty);
        switchThree.setEnabled(!transcriptIsEmpty);

        if (originalIsEmpty && transcriptIsEmpty && translateIsEmpty) {
            start.setEnabled(false);
        }*/
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
                    updateOptions(mSelectedWords);
                    checkBox.setChecked(!checkBox.isChecked());
                    updateActionBarTitle();
                }
            }

            @Override
            public boolean onLongClick(View view, int position) {
                if (!isSelectMode()) {
                    selectedList.add(position);
                    mSelectedWords.add(mWords.get(position));
                    updateOptions(mSelectedWords);
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

/*
    class ButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.true_false_button:
                    v.setEnabled(false);
                    typeTwo.setEnabled(true);
                    typeThree.setEnabled(true);
                    type = TRUE_FALSE;
                    break;
                case R.id.answer_question_button:
                    v.setEnabled(false);
                    typeOne.setEnabled(true);
                    typeThree.setEnabled(true);
                    type = ANSWER_QUESTION;
                    break;
                case R.id.quiz_button:
                    v.setEnabled(false);
                    typeOne.setEnabled(true);
                    typeTwo.setEnabled(true);
                    type = QUIZ;
                    break;

                    default:
                        break;
            }
            if (mWords.size()<3){
                typeThree.setVisibility(View.INVISIBLE);
            }
        }

    }
*/
    class SwitchListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
           Toast.makeText(getContext(),"Написать слушателя",Toast.LENGTH_SHORT).show();
        }

    }


/*
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode){// так мы понимаем что именно для этой активности предназначаются данные из интента

            case REQUEST_RESULT:
                getActivity().finish();
                break;
        }

    }
*/
}