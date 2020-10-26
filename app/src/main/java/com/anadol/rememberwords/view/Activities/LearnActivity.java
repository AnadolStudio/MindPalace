package com.anadol.rememberwords.view.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anadol.rememberwords.model.Group;
import com.anadol.rememberwords.model.Word;
import com.anadol.rememberwords.presenter.MyRandom;
import com.anadol.rememberwords.presenter.Question;
import com.anadol.rememberwords.presenter.QuestionMaker;
import com.anadol.rememberwords.view.Fragments.LearnAnswerFragment;
import com.anadol.rememberwords.view.Fragments.LearnPuzzleFragment;
import com.anadol.rememberwords.view.Fragments.LearnQuizFragment;

import java.util.ArrayList;

import static com.anadol.rememberwords.view.Dialogs.LearnStartBottomSheet.ANSWER;
import static com.anadol.rememberwords.view.Dialogs.LearnStartBottomSheet.EXAM;
import static com.anadol.rememberwords.view.Dialogs.LearnStartBottomSheet.PUZZLE;
import static com.anadol.rememberwords.view.Dialogs.LearnStartBottomSheet.QUIZ;
import static com.anadol.rememberwords.view.Dialogs.LearnStartBottomSheet.ROUTE_TEST;
import static com.anadol.rememberwords.view.Dialogs.LearnStartBottomSheet.TYPE_GROUP;
import static com.anadol.rememberwords.view.Dialogs.LearnStartBottomSheet.TYPE_TEST;
import static com.anadol.rememberwords.view.Dialogs.LearnStartBottomSheet.WORDS;

public class LearnActivity extends SimpleFragmentActivity {
    public static final int REQUEST_RESULT = 1;
    public static final String PROGRESS = "progress";
    public static final String QUESTIONS = "questions";
    private ArrayList<Word> mWords;

    public static Intent newIntent(Context context, ArrayList<Word> words, int typeGroup, String typeTest, int routeTest) {

        Intent intent = new Intent(context, LearnActivity.class);
        intent.putExtra(WORDS, words);
        intent.putExtra(TYPE_TEST, typeTest);
        intent.putExtra(TYPE_GROUP, typeGroup);
        intent.putExtra(ROUTE_TEST, routeTest);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return buildLearnFragment();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mWords = getIntent().getParcelableArrayListExtra(WORDS);
        } else {
            mWords = savedInstanceState.getParcelableArrayList(WORDS);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(WORDS, mWords);
    }

    private Fragment buildLearnFragment() {
        return buildLearnFragment(null, mWords, -1);
    }

    private Fragment buildLearnFragment(Question[] questions, ArrayList<Word> mWords, int progress) {
        if (mWords == null) mWords = getIntent().getParcelableArrayListExtra(WORDS);


        String typeTest = getIntent().getStringExtra(TYPE_TEST);
        int typeGroup = getIntent().getIntExtra(TYPE_GROUP, Group.TYPE_NUMBERS);
        int route = getIntent().getIntExtra(ROUTE_TEST, 0);

        Fragment fragment;

        if (questions == null) {
            questions = new QuestionMaker().makeQuestions(mWords, typeGroup, route);
        }

        if (typeTest.equals(EXAM)) {
            typeTest = getRandomTypeTest(MyRandom.nextInt(3));
        }

        switch (typeTest) {

            default:
            case QUIZ:
                fragment = LearnQuizFragment.newInstance(questions, progress);
                break;
            case PUZZLE:
                fragment = LearnPuzzleFragment.newInstance(questions, progress);
                break;
            case ANSWER:
                fragment = LearnAnswerFragment.newInstance(questions, progress);
                break;
        }
        return fragment;
    }

    public void newTypeTestForExam(@NonNull Question[] questions, int progress) {
        replaceFragment(buildLearnFragment(questions, mWords, progress));
    }

    public boolean isExamType() {
        String typeTest = getIntent().getStringExtra(TYPE_TEST);
        return typeTest.equals(EXAM);
    }

    private String getRandomTypeTest(int random) {
        String[] types = new String[]{QUIZ, PUZZLE, ANSWER};
        return types[random];
    }


}
