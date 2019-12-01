package com.anadol.rememberwords.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.fragments.LearnAnswerFragment;
import com.anadol.rememberwords.fragments.LearnQuizFragment;
import com.anadol.rememberwords.fragments.LearnTrueFalseFragment;
import com.anadol.rememberwords.myList.Group;
import com.anadol.rememberwords.myList.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.anadol.rememberwords.activities.LearnStartActivity.isBrightColor;
import static com.anadol.rememberwords.fragments.LearnStartFragment.*;

public class LearnDetailActivity extends SimpleFragmentActivity {
    public static final String RANDOM = "random";
    public static final String CORRECT_WRONG = "correct_wrong";
    public static final String ANSWER_LIST = "answers";
    public static final String QUESTION_LIST = "questions";
    public static final String ANSWER = "answer";
    public static final String COUNT = "count";
    public static final String QUESTION = "question";


    public static final int REQUEST_RESULT = 1;

    public static Intent newIntent(Context context, ArrayList<Word> words, Group group, int type, int object, boolean[] use){
        Intent intent = new Intent(context, LearnDetailActivity.class);
        intent.putExtra(WORDS, words);
        intent.putExtra(GROUP,group);
        intent.putExtra(TYPE,type);
        intent.putExtra(OBJECT,object);
        intent.putExtra(USE,use);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        Group mGroup = getIntent().getParcelableExtra(GROUP);

        int[] gradient = mGroup.getColors();

        int i = 0;

        if (gradient.length==3){i = 1;}
        int iRed = Color.red(gradient[i]);
        int iGreen = Color.green(gradient[i]);
        int iBlue = Color.blue(gradient[i]);

        if (isBrightColor(iRed,iGreen,iBlue)) setTheme(R.style.LightTheme);

/*
        if (getIntent().getIntExtra(OBJECT,1)==ANSWER_QUESTION){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }
*/
        super.onCreate(savedInstanceState);

    }

    @Override
    protected Fragment createFragment() {
        Fragment fragment = null;

        int t = getIntent().getIntExtra(TYPE,-1);
        int o = getIntent().getIntExtra(OBJECT,-1);

        boolean[] use = getIntent().getBooleanArrayExtra(USE);
        ArrayList<Word> words = getIntent().getParcelableArrayListExtra(WORDS);

        switch (t){
            case TRUE_FALSE:
                fragment = LearnTrueFalseFragment.newInstance(words,o,use);
                break;
            case ANSWER_QUESTION:
                fragment = LearnAnswerFragment.newInstance(words,o,use);
                break;
            case QUIZ:
                fragment = LearnQuizFragment.newInstance(words,o,use);
                break;
        }

        return fragment;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateActionBar();
    }
    private void updateActionBar(){
        Group mGroup = getIntent().getParcelableExtra(GROUP);
        if (mGroup != null){
            getSupportActionBar().setTitle(mGroup.getName());
            getSupportActionBar().setBackgroundDrawable( mGroup.getGroupDrawable());
        }
    }

    public static ArrayList getNonRepRandomInts(int size){
        ArrayList<Integer> rtnArray = new ArrayList<>();
        boolean b = false;
        Random random = new Random();
        while (!b){
            int i = random.nextInt(size);
            if (rtnArray.indexOf(i) == -1){
                rtnArray.add(i);
            }
            if (rtnArray.size() == size){
                b =true;
            }
        }
        return rtnArray;
    }

    public static String addTextQuestion(ArrayList<Word> mWord,int object,int randomDigital){
        StringBuilder builder = new StringBuilder();
        Word word = mWord.get(randomDigital);
        switch (object){
            case ORIGINAL:
                builder.append(word.getOriginal());
                break;
            case TRANSCRIPT:
                builder.append(word.getTranscript());
                break;
            case TRANSLATE:
                if (word.hasMultiTrans() == Word.TRUE) {
                    int bound = word.getCountTranslates();
                    builder.append(word.getOneTranslate(new Random().nextInt(bound)));
                }else {
                    builder.append(word.getTranslate());
                }
                break;
        }

        return builder.toString();
    }




}
