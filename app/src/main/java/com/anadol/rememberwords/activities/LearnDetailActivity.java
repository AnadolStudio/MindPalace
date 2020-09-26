package com.anadol.rememberwords.activities;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.fragments.LearnAnswerFragment;
import com.anadol.rememberwords.fragments.LearnQuizFragment;
import com.anadol.rememberwords.fragments.LearnTrueFalseFragment;
import com.anadol.rememberwords.model.Group;
import com.anadol.rememberwords.model.Word;
import com.anadol.rememberwords.view.Activities.SimpleFragmentActivity;

import java.util.ArrayList;
import java.util.Random;

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

    public static Intent newIntent(Context context, ArrayList<Word> words, int type, int object){
        Intent intent = new Intent(context, LearnDetailActivity.class);
        intent.putExtra(WORDS, words);
        intent.putExtra(TYPE,type);
        intent.putExtra(OBJECT,object);
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        Fragment fragment = null;

        int t = getIntent().getIntExtra(TYPE,-1);
        int o = getIntent().getIntExtra(OBJECT,-1);

        ArrayList<Word> words = getIntent().getParcelableArrayListExtra(WORDS);

        int usedObject;
        switch (o){
            case ORIGINAL:
                usedObject = TRANSLATE;
                break;
            default:
            case TRANSLATE:
                usedObject = ORIGINAL;
                break;
        }

        switch (t){
            case TRUE_FALSE:
                fragment = LearnTrueFalseFragment.newInstance(words,o,usedObject);
                break;
            case ANSWER_QUESTION:
                fragment = LearnAnswerFragment.newInstance(words,o,usedObject);
                break;
            case QUIZ:
                fragment = LearnQuizFragment.newInstance(words,o,usedObject);
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
            String title = getString(R.string.testing_group,mGroup.getName());
            getSupportActionBar().setTitle(title);
//            getSupportActionBar().setBackgroundDrawable( mGroup.getGroupDrawable());
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
                b = true;
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
                if (!word.getAssociation().equals("")){
                    builder.append("\n").append(word.getAssociation());
                }
                break;

            case TRANSLATE:
                if (word.isMultiTranslate()) {
                    int bound = word.getCountTranslates();
                    builder.append(word.getOneOfMultiTranslates(new Random().nextInt(bound)));
                }else {
                    builder.append(word.getTranslate());
                }
                break;
        }

        return builder.toString();
    }




}
