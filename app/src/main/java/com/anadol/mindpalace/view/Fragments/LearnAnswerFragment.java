package com.anadol.mindpalace.view.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.anadol.mindpalace.presenter.Question;
import com.anadol.rememberwords.R;
import com.anadol.mindpalace.view.Activities.LearnActivity;
import com.anadol.mindpalace.view.Dialogs.DialogResultBottomSheet;

import static com.anadol.mindpalace.view.Activities.LearnActivity.PROGRESS;
import static com.anadol.mindpalace.view.Activities.LearnActivity.QUESTIONS;
import static com.anadol.mindpalace.view.Activities.LearnActivity.REQUEST_RESULT;

public class LearnAnswerFragment extends Fragment {
    private static final String TAG = LearnAnswerFragment.class.getName();
    private TextView countTextView;
    private TextView mQuestionTextView;
    private Button nextButton;
    private ProgressBar mProgressBar;
    private EditText answerEditText;

    private Question[] mQuestions;

    public static LearnAnswerFragment newInstance(Question[] questions, int progress) {

        Bundle args = new Bundle();
        args.putParcelableArray(QUESTIONS, questions);
        if (progress != -1) args.putInt(PROGRESS, progress);
        LearnAnswerFragment fragment = new LearnAnswerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn_answer_question, container, false);

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
        answerEditText = view.findViewById(R.id.answer_editText);
        nextButton = view.findViewById(R.id.next_button);

        TextView titleQuestion = view.findViewById(R.id.question_title);
        titleQuestion.setText(R.string.write_answer);
    }

    private void setListeners() {
        nextButton.setOnClickListener((v -> nextQuestion()));
        answerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateUI(s.toString());
            }
        });
    }

    private void nextQuestion() {
        Log.i(TAG, "onClick: userAnswer " + mQuestions[mProgressBar.getProgress()].getUserAnswer());
        String answer = clearAnswer(answerEditText.getText().toString());
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

    private void updateCountText() {
        countTextView.setText(getString(R.string.associations_count, mProgressBar.getProgress(), mQuestions.length));
    }

    private String clearAnswer(String s) {
        s = s.trim();
        String[] strings = s.split(" ");
        if (strings.length == 1) strings = s.split(";");
        if (strings.length == 1) strings = s.split(",");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i != 0) {
                builder.append(" ");
            }
            builder.append(
                    strings[i]
                            .replaceAll(";", "")
                            .replaceAll(",", ""));
        }
        return builder.toString();
    }

    private void addQuestion(Question question) {
        String q = question.getQuestion();
        mQuestionTextView.setText(q);
        answerEditText.setText("");
        updateUI();
    }

    private void updateUI() {
        String answer = answerEditText.getText().toString().trim();
        nextButton.setEnabled(!answer.isEmpty());
    }

    private void updateUI(String answer) {
        answer = answer.trim();
        nextButton.setEnabled(!answer.isEmpty());
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
        updateUI();
        addQuestion(mQuestions[mProgressBar.getProgress()]);
        updateCountText();
        // Поднимает клавиатуру
        answerEditText.requestFocus();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

}
