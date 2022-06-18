package com.anadol.mindpalace.view.screens.main.lessons;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;

import com.anadol.mindpalace.R;
import com.anadol.mindpalace.view.screens.main.SimpleFragmentActivity;

public class LessonActivity extends SimpleFragmentActivity {
    private static final String ID_FRAGMENT = "id";

    public static Intent newIntent(Context context, @LayoutRes int layout) {

        Intent intent = new Intent(context, LessonActivity.class);
        intent.putExtra(ID_FRAGMENT, layout);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Fragment fragment;
        int layout = getIntent().getIntExtra(ID_FRAGMENT, 0);

        switch (layout) {
            default:
            case R.layout.fragment_lesson_basics:
                fragment = LessonBasicsFragment.newInstance();
                break;
            case R.layout.fragment_lesson_association:
                fragment = LessonAssociationFragment.newInstance();
                break;
            case R.layout.fragment_lesson_repeating:
                fragment = LessonRepeatingFragment.newInstance();
                break;
            case R.layout.fragment_lesson_numbers:
                fragment = LessonNumbersFragment.newInstance();
                break;
            case R.layout.fragment_lesson_texts:
                fragment = LessonTextsFragment.newInstance();
                break;
            case R.layout.fragment_lesson_dates:
                fragment = LessonDatesFragment.newInstance();
                break;
            case R.layout.fragment_lesson_link:
                fragment = LessonLinkFragment.newInstance();
                break;
        }

        return fragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }
}
