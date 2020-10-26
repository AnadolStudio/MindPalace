package com.anadol.rememberwords.view.Dialogs;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.model.CreatorValues;
import com.anadol.rememberwords.model.DataBaseSchema;
import com.anadol.rememberwords.model.MyCursorWrapper;
import com.anadol.rememberwords.model.Word;
import com.anadol.rememberwords.presenter.Question;
import com.anadol.rememberwords.view.Activities.LearnActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Collections;

import static com.anadol.rememberwords.view.Activities.LearnActivity.QUESTIONS;

public class DialogResultBottomSheet extends BottomSheetDialogFragment {
    private static final String TAG = DialogResultBottomSheet.class.getName();
    private ImageButton cancelButton;
    private TextView resultText;
    private RecyclerView mRecyclerView;
    private Question[] mQuestions;
    private SetResultBackground background;

    public static DialogResultBottomSheet newInstance(Question[] question) {

        Bundle args = new Bundle();

        args.putParcelableArray(QUESTIONS, question);
        DialogResultBottomSheet fragment = new DialogResultBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_learn_result, container, false);

        bind(view);
        setListeners();
        bindDataWithView(savedInstanceState);

        return view;
    }

    private void bind(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        cancelButton = view.findViewById(R.id.cancel_button);
        resultText = view.findViewById(R.id.result_textView);
    }

    private void setListeners() {
        cancelButton.setOnClickListener(v -> finish());
    }

    private void finish() {
        getActivity().finish();
    }

    private void bindDataWithView(Bundle savedInstanceState) {
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayout);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        ArrayList<Question> questions1 = new ArrayList<>();
        mQuestions = (Question[]) getArguments().getParcelableArray(QUESTIONS);
        if (savedInstanceState == null) {
            background = new SetResultBackground();
            background.execute(SetResultBackground.UPDATE_WORDS);
        }

        Collections.addAll(questions1, mQuestions);

        ResultAdapter mAdapter = new ResultAdapter(questions1);
        mRecyclerView.setAdapter(mAdapter);

        int count = 0;
        for (int i = 0; i < questions1.size(); i++) {
            if (questions1.get(i).isUserAnswerCorrect()) {
                count++;
            }
        }
        resultText.setText(getString(R.string.associations_count, count, questions1.size()));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle(STYLE_NORMAL, R.style.BottomSheetModalTheme);
        setCancelable(false);
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (background != null && !background.isCancelled()) {
            background.cancel(false);
            Log.i(TAG, "onStop: background was canceled");
        }
    }

//    public interface LearnCallback {
//        void repeatTest(Boolean isTrue);
//    }

    private class ResultAdapter extends RecyclerView.Adapter<ResultItem> {
        ArrayList<Question> mQuestions;

        public ResultAdapter(ArrayList<Question> questions) {
            mQuestions = questions;
        }

        @NonNull
        @Override
        public ResultItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_learn_result, parent, false);
            return new ResultItem(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ResultItem holder, int position) {
            holder.onBind(mQuestions.get(position));
        }

        @Override
        public int getItemCount() {
            return mQuestions.size();
        }
    }

    private class ResultItem extends RecyclerView.ViewHolder {
        private CardView card;
        private TextView questionTextView;
        private TextView trueAnswerTextView;
        private TextView userAnswerTextView;
        private Resources mResources;

        public ResultItem(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            questionTextView = itemView.findViewById(R.id.question_TextView);
            trueAnswerTextView = itemView.findViewById(R.id.trueAnswer_TextView);
            userAnswerTextView = itemView.findViewById(R.id.userAnswer_TextView);
            mResources = getResources();
        }

        private void onBind(Question question) {
            int position = getAdapterPosition() + 1;
            questionTextView.setText(getString(
                    R.string.question_result,
                    position,
                    question.getQuestion()));
            trueAnswerTextView.setText(getString(
                    R.string.answer_result,
                    question.getTrueAnswer()));
            userAnswerTextView.setText(getString(
                    R.string.user_answer_result,
                    question.getUserAnswer()));

            if (question.isUserAnswerCorrect()) {
                card.setCardBackgroundColor(mResources.getColor(R.color.colorTrue));
            } else {
                card.setCardBackgroundColor(mResources.getColor(R.color.colorFalse));
            }
        }
    }

    public class SetResultBackground extends AsyncTask<String, Void, Boolean> {
        static final String UPDATE_WORDS = "update_words";
        int countLearn;
        long time;
        boolean isExamType;
        boolean isExam;
        private String cmd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LearnActivity activity = (LearnActivity) getActivity();
            isExamType = activity.isExamType();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            MyCursorWrapper cursor = null;
            cmd = strings[0];

            ContentResolver contentResolver = getActivity().getContentResolver();

            try {
                switch (cmd) {


                    case UPDATE_WORDS:
                        long currentTime = System.currentTimeMillis();

                        for (Question question : mQuestions) {

                            countLearn = question.getCountLearn();
                            time = question.getTime();
                            isExam = question.isExam();

                            if (question.isUserAnswerCorrect() && Word.isRepeatable(time, currentTime, countLearn)) {
                                countLearn++;
                                time = currentTime;
                                if (isExamType) isExam = true;
                            } else {
                                if (countLearn > 0) {
                                    countLearn--;
                                }
                            }

                            contentResolver.update(DataBaseSchema.Words.CONTENT_URI,
                                    CreatorValues.createWordsLearnValues(
                                            countLearn, time, isExam),
                                    DataBaseSchema.Words.UUID + " = ?",
                                    new String[]{question.getUUID()});
                        }
                        return true;
                }
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return false;
        }
    }

}
