package com.anadol.rememberwords.view.Fragments;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.view.Dialogs.DialogResultBottomSheet;
import com.anadol.rememberwords.presenter.Question;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import static com.anadol.rememberwords.view.Activities.LearnActivity.QUESTIONS;
import static com.anadol.rememberwords.view.Activities.LearnActivity.REQUEST_RESULT;

public class LearnQuizFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = LearnQuizFragment.class.getName();

    private TextView mQuestionTextView;
    private Button nextButton;
    private ProgressBar mProgressBar;
    private ChipGroup mChipGroup;

    private Question[] mQuestions;

    public static LearnQuizFragment newInstance(Question[] questions) {

        Bundle args = new Bundle();
        args.putParcelableArray(QUESTIONS, questions);
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
        mChipGroup = view.findViewById(R.id.answer_group);
        nextButton = view.findViewById(R.id.next_button);
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
        updateUI();
        addQuestion(mQuestions[mProgressBar.getProgress()]);
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
        ChipGroup.LayoutParams chipParams = new ChipGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        String[] strings = question.getAllAnswersRandomOrder();

        for (int i = 0; i < strings.length; i++) {
            chip = new Chip(getContext(), null, R.attr.chipStyle);
            chip.setLayoutParams(chipParams);
            chip.setText(strings[i]);
            mChipGroup.addView(chip);
        }
    }

    @Override
    public void onClick(View view) {
        nextQuestion();
    }

    private void nextQuestion() {
        Log.i(TAG, "onClick: userAnswer " + mQuestions[mProgressBar.getProgress()].getUserAnswer());
        mProgressBar.incrementProgressBy(1);
        if (mProgressBar.getMax() != mProgressBar.getProgress()) {
            // updateUI не подходиит, ибо все равно возращется id Chip'a
            mChipGroup.removeAllViews();
            nextButton.setEnabled(false);
            addQuestion(mQuestions[mProgressBar.getProgress()]);
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
