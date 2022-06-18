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

import com.anadol.mindpalace.data.question.Question;
import com.anadol.mindpalace.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Arrays;

import static com.anadol.mindpalace.view.screens.learn.LearnActivity.PROGRESS;
import static com.anadol.mindpalace.view.screens.learn.LearnActivity.QUESTIONS;
import static com.anadol.mindpalace.view.screens.learn.LearnActivity.REQUEST_RESULT;

public class LearnPuzzleFragment extends Fragment {

    private static final String TAG = LearnPuzzleFragment.class.getName();
    private TextView countTextView;
    private TextView mQuestionTextView;
    private Button nextButton;
    private ProgressBar mProgressBar;
    private ChipGroup mPuzzleGroup;
    private ChipGroup mSentenceGroup;

    private Question[] mQuestions;

    public static LearnPuzzleFragment newInstance(Question[] questions, int progress) {

        Bundle args = new Bundle();
        args.putParcelableArray(QUESTIONS, questions);
        if (progress != -1) args.putInt(PROGRESS, progress);
        LearnPuzzleFragment fragment = new LearnPuzzleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn_puzzle, container, false);

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
        mSentenceGroup = view.findViewById(R.id.answer_sentence);
        nextButton = view.findViewById(R.id.next_button);
        mPuzzleGroup = view.findViewById(R.id.answer_group);
        TextView titleQuestion = view.findViewById(R.id.question_title);
        titleQuestion.setText(R.string.collect_the_answer);
    }

    private void setListeners() {
        nextButton.setOnClickListener((v -> nextQuestion()));
    }

    private void nextQuestion() {
        Log.i(TAG, "onClick: userAnswer " + mQuestions[mProgressBar.getProgress()].getUserAnswer());
        String answer = getAnswerForSentence();
        mQuestions[mProgressBar.getProgress()].setUserAnswer(answer);

        mProgressBar.incrementProgressBy(1);
        updateCountText();

        if (mProgressBar.getMax() != mProgressBar.getProgress()) {
            LearnActivity activity = (LearnActivity) getActivity();
            if (activity.isExamType()) {
                activity.newTypeTestForExam(mQuestions, mProgressBar.getProgress());
            } else {
                addQuestion(mQuestions[mProgressBar.getProgress()]);
            }
        } else {
            finish();
        }
    }

    private String getAnswerForSentence() {
        Chip chip;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mSentenceGroup.getChildCount(); i++) {
            chip = (Chip) mSentenceGroup.getChildAt(i);
            builder.append(chip.getText());
        }
        return builder.toString();
    }

    private void updateCountText() {
        countTextView.setText(getString(R.string.associations_count, mProgressBar.getProgress(), mQuestions.length));
    }

    private void addQuestion(Question question) {

        String q = question.getQuestion();
        mQuestionTextView.setText(q);
        clearChipGroups();
        addAnswers(question);
        updateUI();
    }

    private void clearChipGroups() {
        mSentenceGroup.removeAllViews();
        mPuzzleGroup.removeAllViews();
    }

    private void addAnswers(Question question) {
        Chip chip;

        String[] puzzle = question.toPuzzle();
        Log.i(TAG, "addAnswers: puzzle" + Arrays.toString(puzzle));

        for (String s : puzzle) {
            chip = createNewChip(R.layout.chip_action, mPuzzleGroup);
            chip.setOnClickListener(new PuzzleClick());
            chip.setText(s);
            mPuzzleGroup.addView(chip);
        }
    }

    private Chip createNewChip(@LayoutRes int layout, ViewGroup group) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        Chip chip = (Chip) inflater.inflate(layout, group, false);
        ChipGroup.LayoutParams chipParams = new ChipGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        chip.setLayoutParams(chipParams);
        return chip;
    }


    private void updateUI() {
        nextButton.setEnabled(mSentenceGroup.getChildCount() != 0);
    }

    private void finish() {
        DialogResultBottomSheet dialogResult = DialogResultBottomSheet.newInstance(mQuestions);
        dialogResult.setTargetFragment(this, REQUEST_RESULT);
        FragmentManager fragmentManager = getFragmentManager();
        dialogResult.show(fragmentManager, dialogResult.getClass().getName());
    }

    private void bindDataWithView(Bundle savedInstanceState) {
        mQuestionTextView.setMovementMethod(new ScrollingMovementMethod());
        mProgressBar.setMax(mQuestions.length);
        if (savedInstanceState == null) {
            int progress = getArguments().getInt(PROGRESS, 0);
            mProgressBar.setProgress(progress);
        }

        updateCountText();
        addQuestion(mQuestions[mProgressBar.getProgress()]);
    }

    class PuzzleClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            Chip chip = createNewChip(R.layout.chip_action, mSentenceGroup);
            chip.setText(((Chip) v).getText());
            chip.setOnClickListener(new SentenceClick(v.getId()));

            mSentenceGroup.addView(chip);
            updateUI();
        }
    }

    class SentenceClick implements View.OnClickListener {
        int id;

        public SentenceClick(int id) {
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            mSentenceGroup.removeView(v);
            Chip chip;
            for (int i = 0; i < mPuzzleGroup.getChildCount(); i++) {
                chip = (Chip) mPuzzleGroup.getChildAt(i);
                if (id == chip.getId()) {
                    chip.setEnabled(true);
                }
            }
            updateUI();
        }
    }
}
