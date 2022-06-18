package com.anadol.mindpalace.view.screens.main.lessons;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.anadol.mindpalace.R;
import com.anadol.mindpalace.view.screens.SimpleFragment;

public class SettingsFragment extends SimpleFragment implements View.OnClickListener {
    private ScrollView mScrollView;
    private LinearLayout lesson1;
    private LinearLayout lesson2;
    private LinearLayout lesson3;
    private LinearLayout lesson4;
    private LinearLayout lesson5;
    private LinearLayout lesson6;
    private LinearLayout lesson7;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SCROLL_POSITION, mScrollView.getScrollY());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        bind(view);
        lesson1.setOnClickListener(this);
        lesson2.setOnClickListener(this);
        lesson3.setOnClickListener(this);
        lesson4.setOnClickListener(this);
        lesson5.setOnClickListener(this);
        lesson6.setOnClickListener(this);
        lesson7.setOnClickListener(this);
//        getData(savedInstanceState);
//        bindDataWithView();
        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.lesson_1:
                intent = LessonActivity.newIntent(getContext(), R.layout.fragment_lesson_basics);
                break;
            case R.id.lesson_2:
                intent = LessonActivity.newIntent(getContext(), R.layout.fragment_lesson_association);
                break;
            case R.id.lesson_3:
                intent = LessonActivity.newIntent(getContext(), R.layout.fragment_lesson_repeating);
                break;
            case R.id.lesson_4:
                intent = LessonActivity.newIntent(getContext(), R.layout.fragment_lesson_numbers);
                break;
            case R.id.lesson_5:
                intent = LessonActivity.newIntent(getContext(), R.layout.fragment_lesson_texts);
                break;
            case R.id.lesson_6:
                intent = LessonActivity.newIntent(getContext(), R.layout.fragment_lesson_dates);
                break;
            case R.id.lesson_7:
                intent = LessonActivity.newIntent(getContext(), R.layout.fragment_lesson_link);
                break;
            default:
                Toast.makeText(getContext(), "Click " + v.getId(), Toast.LENGTH_SHORT).show();
                break;
        }
        if (intent != null) {
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity);
        }
    }

    private void bind(View view) {
        mScrollView = view.findViewById(R.id.scrollView);
        lesson1 = view.findViewById(R.id.lesson_1);
        lesson2 = view.findViewById(R.id.lesson_2);
        lesson3 = view.findViewById(R.id.lesson_3);
        lesson4 = view.findViewById(R.id.lesson_4);
        lesson5 = view.findViewById(R.id.lesson_5);
        lesson6 = view.findViewById(R.id.lesson_6);
        lesson7 = view.findViewById(R.id.lesson_7);
    }

}
