package com.anadol.rememberwords.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.myList.Word;

import java.util.ArrayList;
import java.util.Random;

import static com.anadol.rememberwords.fragments.DialogResult.RESULT;
import static com.anadol.rememberwords.fragments.LearnStartFragment.*;
import static com.anadol.rememberwords.activities.LearnDetailActivity.*;

public class LearnQuizFragment extends Fragment implements View.OnClickListener {
    private static final String BUTTONS_TEXT = "button_text";
    private Button first;
    private Button second;
    private Button third;
    private Button fourth;
    private TextView mTextView;

    private ArrayList<Integer> random;
    private ArrayList<Word> mWords;
    private int object;
    private boolean[] use;
    private int count;
    private boolean[] correctWrong;
    private String[] myAnswersList;
    private String[] myQuestionList;
    private String[] buttonAnswers;
    private String answerStr;
    private ProgressBar mProgressBar;


    public static LearnQuizFragment newInstance(ArrayList<Word> words, int object, boolean[] use) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(WORDS,words);
        args.putInt(OBJECT,object);
        args.putBooleanArray(USE,use);

        LearnQuizFragment fragment = new LearnQuizFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(CORRECT_WRONG,correctWrong);
        outState.putStringArray(ANSWER_LIST,myAnswersList);
        outState.putStringArray(QUESTION_LIST,myQuestionList);
        outState.putStringArray(BUTTONS_TEXT,buttonAnswers);
        outState.putString(ANSWER,answerStr);
        outState.putIntegerArrayList(RANDOM,random);
        outState.putInt(COUNT,count);
        outState.putString(QUESTION,mTextView.getText().toString());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mWords = getArguments().getParcelableArrayList(WORDS);
        object = getArguments().getInt(OBJECT);
        use = getArguments().getBooleanArray(USE);

        View v = inflater.inflate(R.layout.fragment_learn_quiz,container,false);
        first = v.findViewById(R.id.first_button);
        second = v.findViewById(R.id.second_button);
        third = v.findViewById(R.id.third_button);
        fourth = v.findViewById(R.id.fourth_button);
        first.setOnClickListener(this);
        second.setOnClickListener(this);
        third.setOnClickListener(this);
        fourth.setOnClickListener(this);


        mProgressBar = v.findViewById(R.id.progressBar);
        mProgressBar.setMax(mWords.size());



        String q;

        if (savedInstanceState!=null){
            random = savedInstanceState.getIntegerArrayList(RANDOM);
            correctWrong = savedInstanceState.getBooleanArray(CORRECT_WRONG);
            myAnswersList = savedInstanceState.getStringArray(ANSWER_LIST);
            myQuestionList = savedInstanceState.getStringArray(QUESTION_LIST);
            buttonAnswers = savedInstanceState.getStringArray(BUTTONS_TEXT);
            answerStr= savedInstanceState.getString(ANSWER);
            count = savedInstanceState.getInt(COUNT);
            q = savedInstanceState.getString(QUESTION);

            setTextToButtons();

        }else {
            random = getNonRepRandomInts(mWords.size());
            correctWrong = new boolean[mWords.size()];
            myAnswersList = new String[mWords.size()];
            count = 0;
            q = addTextQuestion(mWords,object,random.get(count));
            addTextToButton();
            myQuestionList = new String[mWords.size()];
            myQuestionList[count]=q;
        }

        mTextView = v.findViewById(R.id.word_qestion);
        mTextView.setText(q);


        return v;
    }

    @Override
    public void onClick(View v) {
        mProgressBar.incrementProgressBy(1);
        Button button = (Button) v;
        String a = button.getText().toString().toLowerCase();
        if (a.equals(answerStr)){
            correctWrong[count] = true;
        }else {
            correctWrong[count] = false;
        }
        myAnswersList[count] = a;
        count++;

        if (count != mWords.size()) {
            mTextView.setText(addTextQuestion(mWords,object,random.get(count)));
            myQuestionList[count]=mTextView.getText().toString();
            addTextToButton();
        } else{
            disableButtons();
            DialogResult dialogResult = DialogResult.newInstance(myQuestionList,myAnswersList,correctWrong);
            dialogResult.setTargetFragment(this,REQUEST_RESULT);
            FragmentManager fragmentManager = getFragmentManager();
            dialogResult.show(fragmentManager,RESULT);
        }


    }

    private void disableButtons() {
        first.setEnabled(false);
        second.setEnabled(false);
        third.setEnabled(false);
        fourth.setEnabled(false);
    }

    private void addTextToButton(){
        int i = new Random().nextInt(4);
        buttonAnswers = new String[]{"","","",""};

        for (int j = 0; j<4;j++){
            if (j == i){
                buttonAnswers[j] = addTextAnswer(true);
            }else {
                buttonAnswers[j] = addTextAnswer(false);
                while (hasDuplicate(buttonAnswers)) {
                    buttonAnswers[j] = addTextAnswer(false);
                    System.out.println(buttonAnswers[3]+ "_"+ buttonAnswers[2]+ "_"+ buttonAnswers[2]+ "_"+ buttonAnswers[3]);
                }
            }
        }

        setTextToButtons();
    }

    private void setTextToButtons() {
        first.setText(buttonAnswers[0]);
        second.setText(buttonAnswers[1]);
        third.setText(buttonAnswers[2]);
        fourth.setText(buttonAnswers[3]);
    }

    private boolean hasDuplicate(String[]strings){
        boolean b = false;

        for (int j= 0;j<strings.length;j++){
            String s = strings[j];
            for (int n =j+1; n<strings.length;n++){
                if (s.equals(strings[n]) && !s.equals("")) {
                    b = true;
                }
            }
        }

        return b;
    }


    private String addTextAnswer(boolean trueFalse){
        StringBuilder builder = new StringBuilder();

        Random r = new Random();
        Word word;

        if (trueFalse){
            word = mWords.get(random.get(count));
        }else {
            int i = r.nextInt(mWords.size());

            //Это сделано для того чтобы случай не выдал слово,
            // которое нужно проверять
            while (i == random.get(count)){
                i = r.nextInt(mWords.size());
            }
            word = mWords.get(i);
        }



        int usedObject;
        int[] allUsedObjects = new int[2];
        int tmp = 0;
        for (int i = 0; i < use.length; i++) {
            if (use[i]){
                allUsedObjects[tmp] = i;
                tmp++;
            }
        }

        boolean b = r.nextBoolean();

        if (tmp == 1){
            usedObject = allUsedObjects[0];
        }else { // tmp == 2
            if (b){
                usedObject = allUsedObjects[0];
            }else {
                usedObject = allUsedObjects[1];
            }
        }

        String s = "";
        while (s.equals("")) {
            switch (usedObject){
                case ORIGINAL:
                    s = (word.getOriginal());
                    break;
                case TRANSCRIPT:
                    s = (word.getTranscript());
                    break;
                case TRANSLATE:
                    if (word.hasMultiTrans() == Word.TRUE) {
                        s = (word.getOneTranslate(r.nextInt(word.getCountTranslates())));
                    }else {
                        s = (word.getTranslate());
                    }
                    break;
            }
            System.out.println();
            b = !b;
            if (b){
                usedObject = allUsedObjects[0];
            }else {
                usedObject = allUsedObjects[1];
            }
        }
        builder.append(s);


        if (trueFalse){
            answerStr = builder.toString();
        }
        return builder.toString();
    }



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

}
