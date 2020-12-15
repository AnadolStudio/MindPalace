package com.anadol.mindpalace.view.Dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.anadol.mindpalace.R;
import com.anadol.mindpalace.model.BackgroundSingleton;
import com.anadol.mindpalace.model.SettingsPreference;
import com.anadol.mindpalace.model.Word;
import com.anadol.mindpalace.presenter.ComparatorNeverExam;
import com.anadol.mindpalace.presenter.ComparatorPriority;
import com.anadol.mindpalace.presenter.MyRandom;
import com.anadol.mindpalace.view.Activities.LearnActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_OK;
import static com.anadol.mindpalace.model.BackgroundSingleton.UPDATE_WORD_EXAM;


public class LearnStartBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener, ChipGroup.OnCheckedChangeListener {
    public static final String TYPE_TEST = "type_test";
    public static final String TYPE_GROUP = "type_group";
    public static final String OBJECT_TEST = "object";
    public static final String ROUTE_TEST = "route";

    public static final String QUIZ = "quiz";
    public static final String ANSWER = "answer";
    public static final String PUZZLE = "puzzle";
    public static final String EXAM = "exam";
    public static final int FORWARD = 0;
    public static final int INVERSE = 1;
    public static final int MIN_COUNT_WORDS = 10;

    public static final String WORDS = "words";

    private static final String TAG = LearnStartBottomSheet.class.getName();
    private static final String AUTO = "auto";
    private static final String DIAPASON = "diapason";
    private static final String RANDOM = "random";

    private TextView title;
    private Button startButton;
    private ImageButton cancelButton;
    private ChipGroup mChipGroupTypeTest;
    private ChipGroup mChipGroupRouteTest;
    private ChipGroup mChipGroupObjectTest;
    private Chip examChip;
    private EditText mEditText;
    private SwitchCompat mSwitchAuto;
    private LinearLayout linearOptions;

    private ArrayList<Word> mWords;
    private int typeGroup;

    private String typeTest;
    private int routeTest;
    private String objectTest;
    private Disposable mDisposable;

    public static LearnStartBottomSheet newInstance(int typeGroup, ArrayList<Word> mWords) {

        Bundle args = new Bundle();
        args.putInt(TYPE_GROUP, typeGroup);
        args.putParcelableArrayList(WORDS, mWords);
        LearnStartBottomSheet fragment = new LearnStartBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    public static int getRouteTest(ArrayList<Word> words) {
        /*int count = 0;
        for (Word w : words) {

//            if (w.getCountLearn() % 2 == 0) {
            if (w.getCountLearn() > 2) {
                count++;
            }
        }

        return count > words.size() / 2 ? FORWARD : INVERSE;*/
        boolean b = new Random().nextBoolean();
        return b ? FORWARD : INVERSE;
    }

    public static String getTypeTest(ArrayList<Word> words) {
        int easy = 0; // Quiz
        int medium = 0; //Puzzle
        int hard = 0; // Answer

        for (Word w : words) {
            switch (w.getCountLearn()) {
                case 0:
                case 1:
                    easy++;
                    break;
                case 2:
                    medium++;
                    break;
                default:
                case 3:
                    hard++;
                    break;
            }
        }
        if (hasWordToExam(words)) return EXAM;

        return (easy > medium) ? QUIZ : ((medium > hard) ? PUZZLE : ANSWER);
    }

    private static boolean hasWordToExam(ArrayList<Word> words) {
        return getWordsToExam(words).size() >= MIN_COUNT_WORDS;
    }

    private static ArrayList<Word> getWordsToExam(ArrayList<Word> words) {
        //Сортирует таким образом что ни разу не проходящие екзамен слова будут в начале списка
        Collections.sort(words, new ComparatorNeverExam());
        Log.i(TAG, "updateUI: words" + words);
        ArrayList<Word> arrayList = new ArrayList<>();
        Word w;
        for (int i = 0; i < Math.min(words.size(), 20); i++) {
            w = words.get(i);
            if (w.readyToExam()) arrayList.add(w);
        }
        Log.i(TAG, "getWordsToExam: " + arrayList);
        return arrayList;
    }

    public void updateUI() {
        startButton.setEnabled(isAllReady());
        examChip.setEnabled(hasWordToExam(mWords));
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
        /*if (savedInstanceState == null) {
            mChipGroupObjectTest.check(R.id.auto_chip);
        }*/
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
        mSwitchAuto = view.findViewById(R.id.auto_switch);
        linearOptions = view.findViewById(R.id.ll_options);
        examChip = view.findViewById(R.id.exam_chip);
    }

    private void routeAndObjectGroupSetEnabled(boolean enable) {
        Chip chip;
        for (int i = 0; i < mChipGroupRouteTest.getChildCount(); i++) {
            chip = (Chip) mChipGroupRouteTest.getChildAt(i);
            chip.setEnabled(enable);
            chip.setChecked(false);
        }
        for (int i = 0; i < mChipGroupObjectTest.getChildCount(); i++) {
            chip = (Chip) mChipGroupObjectTest.getChildAt(i);
            chip.setEnabled(enable);
            chip.setChecked(false);
        }
        mEditText.setEnabled(enable);
        updateUI();
    }

    private void setListeners() {
        cancelButton.setOnClickListener(v -> {
            getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, new Intent());
            dismiss();
        });

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
                case EditorInfo.IME_ACTION_DONE:
                    if (isAllReady()) {
                        manualStart();
                    }
                    return true;
            }
        });

        startButton.setOnClickListener(this);

        mSwitchAuto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SettingsPreference.setAuto(getContext(), isChecked);
            if (isChecked) {
                linearOptions.setVisibility(View.GONE);
            } else {
                linearOptions.setVisibility(View.VISIBLE);
            }
            updateUI();
        });
        examChip.setOnCheckedChangeListener((buttonView, isChecked) -> routeAndObjectGroupSetEnabled(!isChecked));
    }

    private void bindDataWithView() {
        title.setText(R.string.learn);
        mEditText.setHint(Integer.toString(mWords.size()));
        mSwitchAuto.setChecked(SettingsPreference.isAutoCreatorLearningTest(getContext()));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle(STYLE_NORMAL, R.style.BottomSheetModalTheme);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        setCancelable(false);
        return dialog;
    }

    @Override
    public void onCheckedChanged(ChipGroup chipGroup, int i) {
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayMap<String, Boolean> lastAction = BackgroundSingleton.get(getContext()).getStackActions();
        if (lastAction.size() > 0 && mDisposable == null) {
            for (int i = lastAction.size() - 1; i >= 0; i--) {
                if (lastAction.keyAt(i).equals(UPDATE_WORD_EXAM)) {
                    updateWords();
                }
            }
        }else {
            updateWords();
        }
    }

    private void updateWords() {
        Observable<ArrayList<Word>> observable = BackgroundSingleton.get(getContext()).updateWordsExam(mWords);
        mDisposable = observable.subscribe(words -> {
            mWords = words;
            updateUI();
            Log.i(TAG, "UpdateStatusWord is done");
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    private boolean isAllReady() {
        boolean isAllReady = true;

        if (mSwitchAuto.isChecked()) {
            autoOptions();
        } else {
            typeTest = getTypeTest();
            routeTest = getRouteTest();
            objectTest = getObjectTest();
        }

        while (isAllReady) {
            if (typeTest == EXAM) {
                break;
            }

            if (typeTest == null || routeTest == -1 || objectTest == null) {
                isAllReady = false;
                break;
            }

            String countWords = mEditText.getText().toString();
            if (linearOptions.getVisibility() == View.VISIBLE && countWords.equals("")) {
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

        Log.i(TAG, "updateUI:" + "typeTest " + typeTest + " routeTest " + routeTest
                + " objectTest " + objectTest + " isAllReady " + isAllReady);

        return isAllReady;
    }

    private void autoOptions() {
        ArrayList<Word> words = getWordsForPriority(mWords, Math.min(mWords.size(), 20));

        typeTest = getTypeTest(words);
        routeTest = getRouteTest(words);
        objectTest = AUTO;
    }

    @Override
    public void onClick(View v) {
        if (!mSwitchAuto.isChecked()) {
            manualStart();
        } else {
            autoStart();
        }
    }

    private void manualStart() {
        String s;
        int count;
        ArrayList<Word> learnList = new ArrayList<>();

        typeTest = getTypeTest();
        routeTest = getRouteTest();
        objectTest = getObjectTest();

        if (examChip.isChecked()) {
            //StartExam
            ArrayList<Word> toExam = getWordsToExam(mWords);
            startExam(toExam);
            return;
        }

        switch (objectTest) {
            case RANDOM:
                s = mEditText.getText().toString();
                count = Integer.parseInt(s);

                if (findingRandomError(count)) return;

                learnList = MyRandom.getRandomArrayList(mWords, count);

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
                learnList = MyRandom.getRandomArrayList(learnList, learnList.size());
                break;
        }
        startLearn(learnList, typeTest, routeTest);
    }

    private void autoStart() {
        autoOptions();
        int count;
        count = Math.min(mWords.size(), 20);

        ArrayList<Word> toExam = getWordsToExam(mWords);
        if (toExam.size() >= MIN_COUNT_WORDS) {
            startExam(toExam);
        } else {
            startLearn(getWordsForPriority(mWords, count), typeTest, routeTest);
        }
    }

    private void startLearn(ArrayList<Word> learnList, String type, int route) {

        Intent intent = LearnActivity.newIntent( // Тут предаются выбратнные атрибуты для начала теста
                getContext(),
                learnList,
                typeGroup,
                type,
                route);

        startActivity(intent);
    }

    private ArrayList<Word> getWordsForPriority(ArrayList<Word> words, int count) {
        ArrayList<Word> arrayList = new ArrayList<>(words);
        Collections.sort(arrayList, new ComparatorPriority());

        ArrayList<Word> priority = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            priority.add(arrayList.get(i));
            Log.i(TAG, "getWordsForPriority: " + arrayList.get(i).dataToString());
        }
        return priority;
    }

    private void startExam(ArrayList<Word> examList) {
        typeTest = getTypeTest();
        routeTest = getRouteTest();

        Intent intent = LearnActivity.newIntent( // Тут предаются выбратнные атрибуты для начала теста
                getContext(),
                examList,
                typeGroup,
                typeTest,
                routeTest);

        startActivity(intent);
    }

    private boolean findingRandomError(int count) {
        if (count < LearnStartBottomSheet.MIN_COUNT_WORDS) {
            makeToast(getString(R.string.min_word_list_size, LearnStartBottomSheet.MIN_COUNT_WORDS));
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
                typeTest = ANSWER;
                break;
            case R.id.puzzle_chip:
                typeTest = PUZZLE;
                break;
            case R.id.exam_chip:
                typeTest = EXAM;
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
/*
            case R.id.auto_chip:
                objectTest = AUTO;
                mEditText.setVisibility(View.GONE);
                break;
*/
            case R.id.diapason_chip:
                objectTest = DIAPASON;
                mEditText.setVisibility(View.VISIBLE);
                break;
            case R.id.random_chip:
                objectTest = RANDOM;
                mEditText.setVisibility(View.VISIBLE);
                break;
            default:
                objectTest = null;
                break;
        }
        return objectTest;
    }
}
