package com.anadol.mindpalace.view.screens.main.lessons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.anadol.mindpalace.R;
import com.anadol.mindpalace.data.group.CreatorExampleGroup;
import com.anadol.mindpalace.data.group.GroupExample;


public class LessonDatesFragment extends LessonFragment {
    private ScrollView mScrollView;
    private Button uploadButton;

    public static LessonDatesFragment newInstance() {
        return new LessonDatesFragment();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SCROLL_POSITION, mScrollView.getScrollY());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_lesson_dates, container, false);

        bind(view);
        getData(savedInstanceState);
        View cards = view.findViewById(R.id.group_months);
        GroupExample groupMonth = new CreatorExampleGroup.Month();
        createGroup(cards, groupMonth);

        uploadButton.setOnClickListener(v->{
            CreatorExampleGroup.Creator creator = new CreatorExampleGroup.Creator();
            creator.setContext(getContext());
            creator.execute(groupMonth);
        });
        return view;
    }

    private void bind(View view) {
        mScrollView = view.findViewById(R.id.scrollView);

        uploadButton = view.findViewById(R.id.upload_button);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener((v) -> getActivity().onBackPressed());
    }

    private void getData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(SCROLL_POSITION);
            mScrollView.setScrollY(position);
        }
    }
}
