package com.anadol.rememberwords.view.Activities;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.anadol.rememberwords.view.Dialogs.DialogResultBottomSheet;
import com.anadol.rememberwords.view.Fragments.LearnAnswerFragment;
import com.anadol.rememberwords.model.Group;
import com.anadol.rememberwords.model.Word;
import com.anadol.rememberwords.presenter.Question;
import com.anadol.rememberwords.presenter.QuestionMaker;
import com.anadol.rememberwords.view.Fragments.LearnQuizFragment;

import java.util.ArrayList;

import static com.anadol.rememberwords.view.Dialogs.LearnBottomSheet.ANSWER_QUESTION;
import static com.anadol.rememberwords.view.Dialogs.LearnBottomSheet.PUZZLE;
import static com.anadol.rememberwords.view.Dialogs.LearnBottomSheet.QUIZ;
import static com.anadol.rememberwords.view.Dialogs.LearnBottomSheet.ROUTE_TEST;
import static com.anadol.rememberwords.view.Dialogs.LearnBottomSheet.TYPE_GROUP;
import static com.anadol.rememberwords.view.Dialogs.LearnBottomSheet.TYPE_TEST;
import static com.anadol.rememberwords.view.Dialogs.LearnBottomSheet.WORDS;

public class LearnActivity extends SimpleFragmentActivity implements DialogResultBottomSheet.LearnCallback {
    public static final int REQUEST_RESULT = 1;
    public static final String REPEAT = "repeat";
    public static final String QUESTIONS = "questions";

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

    private Fragment buildLearnFragment() {
        return buildLearnFragment(null);
    }

    private Fragment buildLearnFragment(Question[] questions) {
        ArrayList<Word> mWords = getIntent().getParcelableArrayListExtra(WORDS);
        String typeTest = getIntent().getStringExtra(TYPE_TEST);
        int typeGroup = getIntent().getIntExtra(TYPE_GROUP, Group.TYPE_NUMBERS);
        int route = getIntent().getIntExtra(ROUTE_TEST, 0);

        Fragment fragment = null;

        if (questions == null) {
            questions = new QuestionMaker().makeQuestions(mWords, typeGroup, route);
        }

        switch (typeTest) {
            case QUIZ:
                fragment = LearnQuizFragment.newInstance(questions);
                break;
            case ANSWER_QUESTION:
                fragment = LearnAnswerFragment.newInstance(questions);
                break;
            case PUZZLE:
//                fragment = LearnPuzzleFragment.newInstance(questions);
                break;
        }
        return fragment;
    }


    @Override
    public void repeatTest(Boolean isTrue) {
        if (isTrue) {
            replaceFragment(buildLearnFragment());
        } else {
            finish();
        }
    }

}
