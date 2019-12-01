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
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.myList.Word;

import java.util.ArrayList;
import java.util.Random;

import static com.anadol.rememberwords.activities.LearnDetailActivity.*;
import static com.anadol.rememberwords.fragments.DialogResult.RESULT;
import static com.anadol.rememberwords.fragments.LearnStartFragment.OBJECT;
import static com.anadol.rememberwords.fragments.LearnStartFragment.ORIGINAL;
import static com.anadol.rememberwords.fragments.LearnStartFragment.TRANSCRIPT;
import static com.anadol.rememberwords.fragments.LearnStartFragment.TRANSLATE;
import static com.anadol.rememberwords.fragments.LearnStartFragment.USE;
import static com.anadol.rememberwords.fragments.LearnStartFragment.WORDS;

public class LearnTrueFalseFragment extends Fragment implements View.OnClickListener{

    private static final String TRUE_FALSE = "true_false";

    private ArrayList<Word> mWords;
    private int object;
    private boolean[] use;

    private ArrayList<Integer> random;
    private int count;
    private boolean[] correctWrong;
    private String[] myAnswersList;
    private String[] myQuestionList;
    private boolean trueAnswer;

    private Button trueButton;
    private Button falseButton;
    private TextView question;
    private TextView answer;
    private ProgressBar mProgressBar;

    public static LearnTrueFalseFragment newInstance(ArrayList<Word> words, int object, boolean[] use) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(WORDS,words);
        args.putInt(OBJECT,object);
        args.putBooleanArray(USE,use);

        LearnTrueFalseFragment fragment = new LearnTrueFalseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(CORRECT_WRONG,correctWrong);
        outState.putStringArray(ANSWER_LIST,myAnswersList);
        outState.putStringArray(QUESTION_LIST,myQuestionList);
        outState.putIntegerArrayList(RANDOM,random);
        outState.putInt(COUNT,count);

        outState.putBoolean(TRUE_FALSE,trueAnswer);
        outState.putString(QUESTION,question.getText().toString());
        outState.putString(ANSWER,answer.getText().toString());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_learn_true_false,container,false);

        mWords = getArguments().getParcelableArrayList(WORDS);
        object = getArguments().getInt(OBJECT);
        use = getArguments().getBooleanArray(USE);

        mProgressBar = v.findViewById(R.id.progressBar);
        mProgressBar.setMax(mWords.size());
        trueButton = v.findViewById(R.id.true_button);
        falseButton = v.findViewById(R.id.false_button);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        question = v.findViewById(R.id.word_qestion);
        answer = v.findViewById(R.id.word_answer);
        String a;
        String q;

        if (savedInstanceState!=null){
            random = savedInstanceState.getIntegerArrayList(RANDOM);
            correctWrong = savedInstanceState.getBooleanArray(CORRECT_WRONG);
            myAnswersList = savedInstanceState.getStringArray(ANSWER_LIST);
            myQuestionList = savedInstanceState.getStringArray(QUESTION_LIST);
            count = savedInstanceState.getInt(COUNT);
            trueAnswer = savedInstanceState.getBoolean(TRUE_FALSE);
            q = savedInstanceState.getString(QUESTION);
            a = savedInstanceState.getString(ANSWER);
        }else {
            random = getNonRepRandomInts(mWords.size());
            correctWrong = new boolean[mWords.size()];
            count = 0;
            myAnswersList = new String[mWords.size()];
            q = addTextQuestion(mWords,object,random.get(count));
            a = addTextAnswer();
            myQuestionList = new String[mWords.size()];
        }
        question.setText(q);
        answer.setText(a);
        if (count < myQuestionList.length) {
            myQuestionList[count]=getQuestion();
        }


        return v;
    }

    private String getQuestion(){
        String sb;
        sb = (question.getText()+" = "+answer.getText());
        return sb;
    }

    @Override
    public void onClick(View v) {
        mProgressBar.incrementProgressBy(1);//plus method correct or wrong Answer
        boolean myAnswer = false;
        switch (v.getId()) {
            case R.id.true_button:
                myAnswer = true;
                break;
            case R.id.false_button:
                myAnswer = false;
                break;
        }


        if (myAnswer == trueAnswer) {
            correctWrong[count] = true;
        } else {
            correctWrong[count] = false;
        }
        myAnswersList[count] = Boolean.toString(myAnswer);
        count++;

        if (count != mWords.size()) {
            question.setText(addTextQuestion(mWords, object, random.get(count)));
            answer.setText(addTextAnswer());
            myQuestionList[count] = getQuestion();
        } else{
            trueButton.setEnabled(false);
            falseButton.setEnabled(false);
            DialogResult dialogResult = DialogResult.newInstance(myQuestionList,myAnswersList,correctWrong);
            dialogResult.setTargetFragment(this,REQUEST_RESULT);
            FragmentManager fragmentManager = getFragmentManager();
            dialogResult.show(fragmentManager,RESULT);
        }

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


    private String addTextAnswer(){
        StringBuilder builder = new StringBuilder();

        Random r = new Random();
        trueAnswer = r.nextBoolean();
        Word word;

        if (trueAnswer){
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

        return builder.toString();
    }

}
