package com.anadol.mindpalace.view.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anadol.mindpalace.model.Group;
import com.anadol.mindpalace.presenter.MyRandom;
import com.anadol.mindpalace.presenter.Question;
import com.anadol.mindpalace.presenter.QuestionMaker;
import com.anadol.mindpalace.view.Dialogs.LearnStartBottomSheet;
import com.anadol.mindpalace.view.screens.learn.LearnAnswerFragment;
import com.anadol.mindpalace.view.screens.learn.LearnPuzzleFragment;
import com.anadol.mindpalace.view.screens.learn.LearnQuizFragment;
import com.anadol.mindpalace.model.Word;

import java.util.ArrayList;

public class LearnActivity extends SimpleFragmentActivity {
    public static final int REQUEST_RESULT = 1;
    public static final String PROGRESS = "progress";
    public static final String QUESTIONS = "questions";
    private ArrayList<Word> mWords;

    public static Intent newIntent(Context context, ArrayList<Word> words, int typeGroup, String typeTest, int routeTest) {

        Intent intent = new Intent(context, LearnActivity.class);
        intent.putExtra(LearnStartBottomSheet.WORDS, words);
        intent.putExtra(LearnStartBottomSheet.TYPE_TEST, typeTest);
        intent.putExtra(LearnStartBottomSheet.TYPE_GROUP, typeGroup);
        intent.putExtra(LearnStartBottomSheet.ROUTE_TEST, routeTest);
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
            mWords = getIntent().getParcelableArrayListExtra(LearnStartBottomSheet.WORDS);
        } else {
            mWords = savedInstanceState.getParcelableArrayList(LearnStartBottomSheet.WORDS);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LearnStartBottomSheet.WORDS, mWords);
    }

    private Fragment buildLearnFragment() {
        return buildLearnFragment(null, mWords, -1);
    }

    private Fragment buildLearnFragment(Question[] questions, ArrayList<Word> mWords, int progress) {
        if (mWords == null) mWords = getIntent().getParcelableArrayListExtra(LearnStartBottomSheet.WORDS);


        String typeTest = getIntent().getStringExtra(LearnStartBottomSheet.TYPE_TEST);
        int typeGroup = getIntent().getIntExtra(LearnStartBottomSheet.TYPE_GROUP, Group.TYPE_NUMBERS);
        int route = getIntent().getIntExtra(LearnStartBottomSheet.ROUTE_TEST, 0);

        Fragment fragment;

        if (questions == null) {
            questions = new QuestionMaker().makeQuestions(mWords, typeGroup, route);
        }

        if (typeTest.equals(LearnStartBottomSheet.EXAM)) {
            typeTest = getRandomTypeTest(MyRandom.nextInt(3));
        }

        switch (typeTest) {

            default:
            case LearnStartBottomSheet.QUIZ:
                fragment = LearnQuizFragment.newInstance(questions, progress);
                break;
            case LearnStartBottomSheet.PUZZLE:
                fragment = LearnPuzzleFragment.newInstance(questions, progress);
                break;
            case LearnStartBottomSheet.ANSWER:
                fragment = LearnAnswerFragment.newInstance(questions, progress);
                break;
        }
        return fragment;
    }

    public void newTypeTestForExam(@NonNull Question[] questions, int progress) {
        replaceFragment(buildLearnFragment(questions, mWords, progress));
    }

    public boolean isExamType() {
        String typeTest = getIntent().getStringExtra(LearnStartBottomSheet.TYPE_TEST);
        return typeTest.equals(LearnStartBottomSheet.EXAM);
    }

    private String getRandomTypeTest(int random) {
        String[] types = new String[]{LearnStartBottomSheet.QUIZ, LearnStartBottomSheet.PUZZLE, LearnStartBottomSheet.ANSWER};
        return types[random];
    }


}
