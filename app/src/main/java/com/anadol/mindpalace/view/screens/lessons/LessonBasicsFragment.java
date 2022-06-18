package com.anadol.mindpalace.view.screens.lessons;

import android.content.res.Resources;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.anadol.mindpalace.R;

import java.util.concurrent.TimeUnit;


public class LessonBasicsFragment extends LessonFragment {
    private ScrollView mScrollView;
    private TextView additionalText;
    private TextView originalText;
    private TextView associationText;
    private TextView translateText;
    private TextView countRepsText;

    public static LessonBasicsFragment newInstance() {
        LessonBasicsFragment fragment = new LessonBasicsFragment();
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SCROLL_POSITION, mScrollView.getScrollY());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson_basics, container, false);

        bind(view);
        getData(savedInstanceState);
        bindDataWithView();
        return view;
    }

    private void bind(View view) {
        mScrollView = view.findViewById(R.id.scrollView);
        originalText = view.findViewById(R.id.original_textView);
        associationText = view.findViewById(R.id.association_textView);
        translateText = view.findViewById(R.id.translate_textView);
        additionalText = view.findViewById(R.id.additional_textView);
        countRepsText = view.findViewById(R.id.count_reps);
//        mChart = view.findViewById(R.id.chart_forget);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener((v) -> getActivity().onBackPressed());
    }

    private void bindDataWithView() {
        originalText.setText(getString(R.string.exaple_original));
        associationText.setText(getString(R.string.exaple_association));
        translateText.setText(getString(R.string.exaple_translate));

        String isLearned = getString(R.string.learning);
        String date = getDate();
        int countLearned = 1;

        additionalText.setText(getString(R.string.additional, isLearned, countLearned, date));
        countRepsText.setText(getStatus(isLearned, date, countLearned));
    }

    private SpannableString getStatus(String isLearned, String date, int countLearned) {
        Resources resources = getResources();

        String s = resources.getString(R.string.reps,
                isLearned, countLearned, date);

        SpannableString info = new SpannableString(s);

        int colorTimeRepeat = resources.getColor(R.color.colorNotReadyRepeat);
        int colorStatus = resources.getColor(R.color.colorLearning);

        int indexDate = s.indexOf(date);
        int indexStatus = s.indexOf(isLearned);
        info.setSpan(new ForegroundColorSpan(colorTimeRepeat), indexDate, indexDate + date.length(), 0);
        info.setSpan(new ForegroundColorSpan(colorStatus), indexStatus, indexStatus + isLearned.length(), 0);
        return info;
    }

    private String getDate() {
        long time = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2);
        TimeZone timeZone = TimeZone.getDefault();
        SimpleDateFormat format = new SimpleDateFormat("d MMM H:mm");
        format.setTimeZone(timeZone);
        return format.format(time);
    }

    private void getData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(SCROLL_POSITION);
            mScrollView.setScrollY(position);
        }
    }
}
