package com.anadol.rememberwords.view.Dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.model.Word;
import com.anadol.rememberwords.view.Activities.LearnActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

import static com.anadol.rememberwords.presenter.MyRandom.getRandomArrayList;


public class LearnBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener, ChipGroup.OnCheckedChangeListener {
    public static final String TYPE_TEST = "type_test";
    public static final String TYPE_GROUP = "type_group";
    public static final String OBJECT_TEST = "object";
    public static final String ROUTE_TEST = "route";
    public static final String QUIZ = "quiz";

    public static final String ANSWER_QUESTION = "answer_question";
    public static final String PUZZLE = "puzzle";
    public static final int FORWARD = 0;
    public static final int INVERSE = 1;
    public static final int MIN_COUNT_WORDS = 10;;
    public static final String WORDS = "words";

    private static final String TAG = LearnBottomSheet.class.getName();
    private static final String ALL = "all";
    private static final String DIAPASON = "diapason";
    private static final String RANDOM = "random";

    private TextView title;
    private MaterialButton startButton;
    private ImageButton cancelButton;
    private ChipGroup mChipGroupTypeTest;
    private ChipGroup mChipGroupRouteTest;
    private ChipGroup mChipGroupObjectTest;
    private EditText mEditText;

    private ArrayList<Word> mWords;
    private int typeGroup;

    public static LearnBottomSheet newInstance(int typeGroup, ArrayList<Word> mWords) {

        Bundle args = new Bundle();
        args.putInt(TYPE_GROUP, typeGroup);
        args.putParcelableArrayList(WORDS, mWords);
        LearnBottomSheet fragment = new LearnBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    public void updateUI() {
        startButton.setEnabled(isAllReady());
    }

    private boolean isAllReady() {
        boolean isAllReady = true;

        String typeTest = getTypeTest();
        int routeTest = getRouteTest();
        String objectTest = getObjectTest();

        while (isAllReady) {

            if (typeTest == null || routeTest == -1 || objectTest == null) {
                isAllReady = false;
                break;
            }
            // TODO temp
            if (typeTest.equals(PUZZLE)) {
                isAllReady = false;
                break;
            }

            String countWords = mEditText.getText().toString();
            if (mEditText.getVisibility() == View.VISIBLE && countWords.equals("")) {
                isAllReady = false;
                break;
            }

            switch (objectTest) {
                case DIAPASON:
                    break;
                case RANDOM:
                    isAllReady = !countWords.contains("-");
                    break;
            }
            break;
        }

        Log.i(TAG, "updateUI:"
                + "\ntypeTest " + typeTest
                + "\nrouteTest " + routeTest
                + "\nobjectTest " + objectTest
                + "\nisAllReady " + isAllReady);

        return isAllReady;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_learn_start, container, false);
        bind(view);
        typeGroup = getArguments().getInt(TYPE_GROUP);
        mWords = getArguments().getParcelableArrayList(WORDS);
        setListeners();
        bindDataWithView();

        updateUI();
        return view;
    }

    private void bind(View view) {
        title = view.findViewById(R.id.title_textView);
        cancelButton = view.findViewById(R.id.cancel_button);
        mChipGroupTypeTest = view.findViewById(R.id.type_test);
        mChipGroupRouteTest = view.findViewById(R.id.route_test);
        mChipGroupObjectTest = view.findViewById(R.id.object_test);
        mEditText = view.findViewById(R.id.count_word_edit_text);
        startButton = view.findViewById(R.id.button_start_to_learn_fragment);
    }

    private void setListeners() {
        cancelButton.setOnClickListener(v -> dismiss());
        mChipGroupTypeTest.setOnCheckedChangeListener(this);
        mChipGroupRouteTest.setOnCheckedChangeListener(this);
        mChipGroupObjectTest.setOnCheckedChangeListener(this);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateUI();
            }
        });
        mEditText.setOnEditorActionListener((v, actionId, event) -> {
            switch (actionId) {
                default:
                    return false;
                case EditorInfo
                        .IME_ACTION_DONE:
                    if (isAllReady()) {
                        startLearn();
                    }
                    return true;
            }
        });

        startButton.setOnClickListener(this);
    }

    private void bindDataWithView() {
        title.setText(R.string.learn);
        mEditText.setHint(Integer.toString(mWords.size()));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle(STYLE_NORMAL, R.style.BottomSheetModalTheme);
//        setCancelable(false);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Override
    public void onCheckedChanged(ChipGroup chipGroup, int i) {
        updateUI();
    }

    @Override
    public void onClick(View v) {
        startLearn();
    }

    private void startLearn() {
        ArrayList<Word> learnList = new ArrayList<>();
        String s;


        String typeTest = getTypeTest();
        int routeTest = getRouteTest();
        String objectTest = getObjectTest();


        switch (objectTest) {
            // TODO хочу сделать ограничени на 30 слов
            case ALL:
                learnList = getRandomArrayList(mWords, mWords.size());
                break;
            case RANDOM:
                s = mEditText.getText().toString();
                int count = Integer.parseInt(s);

                if (findingRandomError(MIN_COUNT_WORDS, count)) return;

                learnList = getRandomArrayList(mWords, count);

                break;

            case DIAPASON:
                s = mEditText.getText().toString();
                String[] diapason;

                if (!s.contains("-")) {
                    makeToast(getString(R.string.diapason_error));
                    return;
                }

                diapason = s.split("-", 2);

                if (diapason.equals("")) {
                    makeToast(getString(R.string.diapason_error));
                    return;
                }
                int one = Integer.parseInt(diapason[0]);
                int two = Integer.parseInt(diapason[1]);

                int max = Math.max(one, two);
                int min = Math.min(one, two);

                if (findingDiapasonError(MIN_COUNT_WORDS, max, min)) return;

                for (int i = min - 1; i < max; i++) {
                    learnList.add(mWords.get(i));
                }
                // Затем перемешиваю слова
                learnList = getRandomArrayList(learnList, learnList.size());
                break;
        }

//        makeToast("Запускаем LearnFragment");
        Intent intent = LearnActivity.newIntent( // Тут предаются выбратнные атрибуты для начала теста
                getContext(),
                learnList,
                typeGroup,
                typeTest,
                routeTest);

        startActivity(intent);
    }

    private boolean findingRandomError(int minCount, int count) {
        if (count == 0 || count < minCount) {
            makeToast(getString(R.string.min_word_list_size, minCount));
            return true;
        }
        if (count > mWords.size()) {
            makeToast(getString(R.string.override_number_words));
            return true;
        }
        return false;
    }

    private boolean findingDiapasonError(int minCount, int max, int min) {
        if (min == 0) {
            makeToast(getString(R.string.diapason_error));
            return true;
        }
        if (max - min + 1 < minCount) {
            makeToast(getString(R.string.min_word_list_size, minCount));
            return true;
        }
        if (max > mWords.size()) {
            makeToast(getString(R.string.override_number_words));
            return true;
        }
        return false;
    }

    private void makeToast(String string) {
        Toast.makeText(getContext(), string, Toast.LENGTH_SHORT).show();
    }

    private String getTypeTest() {
        String typeTest;

        switch (mChipGroupTypeTest.getCheckedChipId()) {
            case R.id.quiz_chip:
                typeTest = QUIZ;
                break;

            case R.id.answerQuestion_chip:
                typeTest = ANSWER_QUESTION;
                break;
            case R.id.puzzle_chip:
                typeTest = PUZZLE;
                break;
            default:
                typeTest = null;
        }
        return typeTest;
    }

    private int getRouteTest() {
        int routeTest;

        switch (mChipGroupRouteTest.getCheckedChipId()) {
            case R.id.forward_chip:
                routeTest = FORWARD;
                break;
            case R.id.inverse_chip:
                routeTest = INVERSE;
                break;
            default:
                routeTest = -1;
        }
        return routeTest;
    }

    private String getObjectTest() {
        String objectTest;

        switch (mChipGroupObjectTest.getCheckedChipId()) {
            case R.id.all_chip:
                objectTest = ALL;
                break;
            case R.id.diapason_chip:
                objectTest = DIAPASON;
                break;
            case R.id.random_chip:
                objectTest = RANDOM;
                break;
            default:
                objectTest = null;
                break;
        }
        return objectTest;
    }
}
