package com.anadol.rememberwords.activities;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.fragments.IOnBackPressed;
import com.anadol.rememberwords.fragments.LearnStartFragment;
import com.anadol.rememberwords.model.Group;
import com.anadol.rememberwords.model.Word;
import com.anadol.rememberwords.view.Activities.SimpleFragmentActivity;

import java.util.ArrayList;

public class LearnStartActivity extends SimpleFragmentActivity {

    private static final String GROUP = "group";
    private static final String WORDS = "words";
    private static final String TYPE = "type";

    private Group mGroup;

    public static Intent newIntent(Context context,  Group group , ArrayList<Word> mWords){
        Intent intent = new Intent(context, LearnStartActivity.class);
        intent.putExtra(GROUP, group);
        intent.putExtra(WORDS,mWords);
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        mGroup = getIntent().getParcelableExtra(GROUP);
        ArrayList<Word> words = getIntent().getParcelableArrayListExtra(WORDS);

        return LearnStartFragment.newInstance(mGroup ,words);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed)fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }

    /*@Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mGroup = getIntent().getParcelableExtra(GROUP);

        int[] gradient = mGroup.getColors();
        int i = 0;
        for (int j:gradient){
            if (j != NON_COLOR) i++;
        }
        if (i==3){i = 1;}else {i = 0;}

        int iRed = Color.red(gradient[i]);
        int iGreen = Color.green(gradient[i]);
        int iBlue = Color.blue(gradient[i]);

        if (isBrightColor(iRed,iGreen,iBlue)) setTheme(R.style.LightTheme);

        super.onCreate(savedInstanceState);

        if (savedInstanceState!= null){
            mGroup = savedInstanceState.getParcelable(GROUP);
        }
    }*/

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(GROUP,mGroup);
    }*/



   /* @Override
    protected void onResume() {
        super.onResume();
        LearnStartFragment fragment = (LearnStartFragment) getFragment();
        if (fragment.isSelectMode()){
            fragment.updateActionBarTitle();
        }else {
            updateActionBar();
        }
    }*/
    private void updateActionBar(){
        if (mGroup != null){
            getSupportActionBar().setTitle(mGroup.getName());
//            getSupportActionBar().setBackgroundDrawable( mGroup.getGroupDrawable());
        }
    }

}
