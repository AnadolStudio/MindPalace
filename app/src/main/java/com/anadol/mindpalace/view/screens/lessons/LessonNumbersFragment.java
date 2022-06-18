package com.anadol.mindpalace.view.screens.lessons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.anadol.mindpalace.R;
import com.anadol.mindpalace.model.CreatorExampleGroup;
import com.anadol.mindpalace.model.GroupExample;
import com.anadol.mindpalace.view.Fragments.SimpleFragment;


public class LessonNumbersFragment extends LessonFragment {
    private ScrollView mScrollView;
    private Button uploadButton;

    public static LessonNumbersFragment newInstance() {
        LessonNumbersFragment fragment = new LessonNumbersFragment();
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SimpleFragment.SCROLL_POSITION, mScrollView.getScrollY());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson_numbers, container, false);

        bind(view);
        getData(savedInstanceState);

        View code = view.findViewById(R.id.group_code);
        GroupExample groupCode = new CreatorExampleGroup.AlphaNumericCode();
        createGroup(code, groupCode);

        View cards = view.findViewById(R.id.group_cards);
        GroupExample groupCards = new CreatorExampleGroup.PlayingCard();
        createGroup(cards, groupCards);

        uploadButton.setOnClickListener(v->{
            CreatorExampleGroup.Creator creator = new CreatorExampleGroup.Creator();
            creator.setContext(getContext());
            creator.execute(groupCards, groupCode);
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
            int position = savedInstanceState.getInt(SimpleFragment.SCROLL_POSITION);
            mScrollView.setScrollY(position);
        }
    }
}
