package com.anadol.mindpalace.view.screens.learn;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.anadol.mindpalace.R;
import com.anadol.mindpalace.presenter.Question;
import com.anadol.mindpalace.view.Activities.LearnActivity;
import com.anadol.mindpalace.view.Dialogs.DialogResultBottomSheet;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import static com.anadol.mindpalace.view.Activities.LearnActivity.PROGRESS;
import static com.anadol.mindpalace.view.Activities.LearnActivity.QUESTIONS;
import static com.anadol.mindpalace.view.Activities.LearnActivity.REQUEST_RESULT;

public class LearnQuizFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = LearnQuizFragment.class.getName();

    private TextView countTextView;
    private TextView mQuestionTextView;
    private Button nextButton;
    private ProgressBar mProgressBar;
    private ChipGroup mChipGroup;

    private Question[] mQuestions;

    public static LearnQuizFragment newInstance(Question[] questions, int progress) {

        Bundle args = new Bundle();
        args.putParcelableArray(QUESTIONS, questions);
        if (progress != -1) args.putInt(PROGRESS, progress);
        LearnQuizFragment fragment = new LearnQuizFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn_quiz, container, false);

        bind(view);
        mQuestions = (Question[]) getArguments().getParcelableArray(QUESTIONS);
        setListeners();
        bindDataWithView(savedInstanceState);

        return view;
    }

    private void bind(View view) {
        mProgressBar = view.findViewById(R.id.progressBar);
        mQuestionTextView = view.findViewById(R.id.text_question);
        countTextView = view.findViewById(R.id.count_text);
        mChipGroup = view.findViewById(R.id.answer_group);
        nextButton = view.findViewById(R.id.next_button);
        TextView titleQuestion = view.findViewById(R.id.question_title);
        titleQuestion.setText(R.string.choice_answer);
    }

    private void setListeners() {
        nextButton.setOnClickListener(this);
        mChipGroup.setOnCheckedChangeListener((chipGroup, i) -> {
            int id;
            Chip chip;
            for (int j = 0; j < chipGroup.getChildCount(); j++) {
                chip = (Chip) chipGroup.getChildAt(j);
                id = chip.getId();
                if (i == id && chip.isChecked()) {
                    mQuestions[mProgressBar.getProgress()]
                            .setUserAnswer(((Chip) chipGroup.getChildAt(j)).getText().toString());
                    break;
                }
            }
            updateUI();
        });
    }

    private void bindDataWithView(Bundle savedInstanceState) {
        mQuestionTextView.setMovementMethod(new ScrollingMovementMethod());
        mProgressBar.setMax(mQuestions.length);
        if (savedInstanceState == null) {
            int progress = getArguments().getInt(PROGRESS, 0);
            mProgressBar.setProgress(progress);
        }
        updateUI();
        updateCountText();
        addQuestion(mQuestions[mProgressBar.getProgress()]);
    }

    private void updateCountText() {
        countTextView.setText(getString(R.string.associations_count, mProgressBar.getProgress(), mQuestions.length));
    }

    private void updateUI() {
        nextButton.setEnabled(mChipGroup.getCheckedChipId() != -1);
    }

    private void addQuestion(Question question) {
        String q = question.getQuestion();
        mQuestionTextView.setText(q);
        addAnswers(question);
    }

    private void addAnswers(Question question) {
        Chip chip;

        String[] strings = question.getAllAnswersRandomOrder();

        for (int i = 0; i < strings.length; i++) {
            chip = createNewChip(R.layout.chip_choice, mChipGroup);
            chip.setText(strings[i]);
            mChipGroup.addView(chip);
        }
    }

    private Chip createNewChip(@LayoutRes int layout, ViewGroup group) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        Chip chip = (Chip) inflater.inflate(layout, group, false);
        ChipGroup.LayoutParams chipParams = new ChipGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        chip.setLayoutParams(chipParams);
        return chip;
    }


    @Override
    public void onClick(View view) {
        nextQuestion();
    }

    private void nextQuestion() {
        Log.i(TAG, "onClick: userAnswer " + mQuestions[mProgressBar.getProgress()].getUserAnswer());
        mProgressBar.incrementProgressBy(1);
        updateCountText();

        if (mProgressBar.getMax() != mProgressBar.getProgress()) {
            LearnActivity activity = (LearnActivity) getActivity();
            if (activity.isExamType()) {
                activity.newTypeTestForExam(mQuestions, mProgressBar.getProgress());
            } else {
                // updateUI не подходиит, ибо все равно возращется id Chip'a
                mChipGroup.removeAllViews();
                nextButton.setEnabled(false);
                addQuestion(mQuestions[mProgressBar.getProgress()]);
            }
        } else {
            finish();
        }
    }

    private void finish() {
        DialogResultBottomSheet dialogResult = DialogResultBottomSheet.newInstance(mQuestions);
        dialogResult.setTargetFragment(this, REQUEST_RESULT);
        FragmentManager fragmentManager = getFragmentManager();
        dialogResult.show(fragmentManager, dialogResult.getClass().getName());
    }
}
