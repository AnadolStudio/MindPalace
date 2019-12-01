package com.anadol.rememberwords.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.database.LayoutPreference;

public class SettingFragment extends Fragment {
    public static final String TAG = "SettingFragment";
    public static final String SETTINGS = "settings";
    public static final String CHANGED_ITEM = "changedItem";
    private RadioGroup group;
    public static SettingFragment newInstance() {

        Bundle args = new Bundle();

        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);
        group = view.findViewById(R.id.group);

        int i = getArguments().getInt(CHANGED_ITEM);
        System.out.println(i);
        switch (i){
            default:
            case 1:
                group.check(R.id.radio_one);
                break;
            case 2:
                group.check(R.id.radio_two);
                break;
            case 3:
                group.check(R.id.radio_three);
                break;
            case 4:
                group.check(R.id.radio_four);
                break;
        }

        return view;
    }

    public void sendResult(int resultCode){
        Intent intent = new Intent();
        intent.putExtra(CHANGED_ITEM,getChangedItem());
        Log.i(TAG,"Changed item: " + getChangedItem());
        Log.i(TAG,"Request code: " + getTargetRequestCode());
        onActivityResult(getTargetRequestCode(),resultCode,intent);
    }


    public int getChangedItem() {
        int i;

        switch (group.getCheckedRadioButtonId()){
            default:
            case R.id.radio_one:
                i = 1;
                break;
            case R.id.radio_two:
                i = 2;
                break;
            case R.id.radio_three:
                i = 3;
                break;
            case R.id.radio_four:
                i = 4;
                break;
        }

        return i;
    }

    @Override
    public void onPause() {
        super.onPause();
        sendResult(Activity.RESULT_OK);
    }
}
