package com.anadol.rememberwords.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.database.SettingsPreference;

public class SettingListFragment extends Fragment {
    public static final String TAG = "SettingListFragment";
    public static final String SETTINGS = "settings";
    public static final String CHANGED_ITEM = "changedItem";
    private RadioGroup group;
    private Button applyButton;

    public static SettingListFragment newInstance(int layout) {

        Bundle args = new Bundle();
        args.putInt(CHANGED_ITEM,layout);
        Log.i(TAG,"Changed item: " +layout);
        SettingListFragment fragment = new SettingListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_list,container,false);
        group = view.findViewById(R.id.group);
        applyButton = view.findViewById(R.id.apply_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(Activity.RESULT_OK);
            }
        });

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
        Log.i(TAG,"Changed item: " + getChangedItem()+
                " Request code: " + getTargetRequestCode());
        SettingsPreference.setLayoutPreference(getActivity(),getChangedItem());

        /*AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.setResult(resultCode,intent);*/
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
    }
}
