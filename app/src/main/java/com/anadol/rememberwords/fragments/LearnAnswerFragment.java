package com.anadol.rememberwords.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.model.Word;

import java.util.ArrayList;

import static com.anadol.rememberwords.activities.LearnDetailActivity.*;
import static com.anadol.rememberwords.fragments.DialogResult.RESULT;
import static com.anadol.rememberwords.fragments.LearnStartFragment.OBJECT;
import static com.anadol.rememberwords.fragments.LearnStartFragment.ORIGINAL;
import static com.anadol.rememberwords.fragments.LearnStartFragment.TRANSLATE;
import static com.anadol.rememberwords.fragments.LearnStartFragment.USE;
import static com.anadol.rememberwords.fragments.LearnStartFragment.WORDS;

public class LearnAnswerFragment extends Fragment implements TextView.OnEditorActionListener {
    private static final String RANDOM = "random";
    private static final String CORRECT_WRONG = "correct_wrong";
    private static final String COUNT = "count";
    //    private ArrayList<Word> mWords;

    private ArrayList<Integer> random;
    private int count;
    private boolean[] correctWrong;

    private ArrayList<Word> mWords;
    private int object;
    int usedObject;

    private EditText mEditText;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private String[] myAnswersList;
    private String[] myQuestionList;

    public static LearnAnswerFragment newInstance(ArrayList<Word> words, int object, int used) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(WORDS,words);
        args.putInt(OBJECT,object);
        args.putInt(USE,used);

        LearnAnswerFragment fragment = new LearnAnswerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(CORRECT_WRONG,correctWrong);
        outState.putIntegerArrayList(RANDOM,random);
        outState.putStringArray(ANSWER_LIST,myAnswersList);
        outState.putStringArray(QUESTION_LIST,myQuestionList);
        outState.putInt(COUNT,count);
        outState.putString(QUESTION,mTextView.getText().toString());
        outState.putString(ANSWER,mEditText.getText().toString());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View v = inflater.inflate(R.layout.fragment_learn_answer_question,container,false);

        mWords = getArguments().getParcelableArrayList(WORDS);
        object = getArguments().getInt(OBJECT);
        usedObject = getArguments().getInt(USE);

        String q;
        String a="";

        if (savedInstanceState!=null){
            random = savedInstanceState.getIntegerArrayList(RANDOM);
            correctWrong = savedInstanceState.getBooleanArray(CORRECT_WRONG);
            count = savedInstanceState.getInt(COUNT);
            myAnswersList = savedInstanceState.getStringArray(ANSWER_LIST);
            myQuestionList = savedInstanceState.getStringArray(QUESTION_LIST);
            q = savedInstanceState.getString(QUESTION);
            a = savedInstanceState.getString(ANSWER);
        }else {
            random = getNonRepRandomInts(mWords.size());
            correctWrong = new boolean[mWords.size()];
            count = 0;
            myAnswersList = new String[mWords.size()];
            myQuestionList = new String[mWords.size()];
            q = addTextQuestion(mWords,object,random.get(count));
            myQuestionList[count]=q;
        }

        mProgressBar = v.findViewById(R.id.progressBar);
        mProgressBar.setMax(mWords.size());
        mTextView = v.findViewById(R.id.word_qestion);
        mTextView.setText(q);

        mEditText = v.findViewById(R.id.word_answer);
        mEditText.setText(a);
        mEditText.setOnEditorActionListener(this);

        return v;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_NEXT){
            mProgressBar.incrementProgressBy(1);//plus method correct or wrong Answer

            String myAnswer =  mEditText.getText().toString().toLowerCase().trim();
            myAnswersList[count] = myAnswer;
            Word word = mWords.get(random.get(count));

            //TODO: update method verify Strings
            if (myAnswer.equals("")){
                correctWrong[count] = false;
            }else {
//                correctWrong[count] = false; // Default

                switch (usedObject){
                    case ORIGINAL:
                        correctWrong[count] = myAnswer.equals(word.getOriginal());
                        break;
                    case TRANSLATE:
                        correctWrong[count] = word.isExistTranslate(myAnswer);
                    break;
                }
            }

            count++;

            if (count != mWords.size()) {
                String q = addTextQuestion(mWords, object, random.get(count));
                mTextView.setText(q);
                myQuestionList[count] = q;
                mEditText.setText("");
                //TODO: было бы неплохо обновлять hint в клавиатуре при landscape,
                // но пока не знаю как. Инфу не нашел
            } else{

                DialogResult dialogResult = DialogResult.newInstance(myQuestionList,myAnswersList,correctWrong);
                dialogResult.setTargetFragment(this,REQUEST_RESULT);
                FragmentManager fragmentManager = getFragmentManager();
                dialogResult.show(fragmentManager,RESULT);
            }
        }

        return false;
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
