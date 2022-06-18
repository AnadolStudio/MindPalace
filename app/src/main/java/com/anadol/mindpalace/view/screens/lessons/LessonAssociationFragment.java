package com.anadol.mindpalace.view.screens.lessons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.anadol.mindpalace.R;


public class LessonAssociationFragment extends LessonFragment {
    private ScrollView mScrollView;

    public static LessonAssociationFragment newInstance() {
        LessonAssociationFragment fragment = new LessonAssociationFragment();
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
        View view = inflater.inflate(R.layout.fragment_lesson_association, container, false);

        bind(view);
        getData(savedInstanceState);
        bindDataWithView();
        return view;
    }

    private void bind(View view) {
        mScrollView = view.findViewById(R.id.scrollView);


        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener((v) -> getActivity().onBackPressed());
    }

    private void bindDataWithView() {
    }

    private void getData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(SCROLL_POSITION);
            mScrollView.setScrollY(position);
        }
    }
}
