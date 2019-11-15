package com.anadol.rememberwords.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anadol.rememberwords.R;

public class CardObjectTestFragment extends Fragment {
    public static final int ORIGINAL_TYPE = 0;
    public static final int TRANSCRIPT_TYPE = 1;
    public static final int TRANSLATE_TYPE = 2;

    private static final String TYPE_TEST = "type_test";


    private TextView nameTest;

    public static CardObjectTestFragment newInstance(int type) {

        Bundle args = new Bundle();
        args.putInt(TYPE_TEST,type);
        CardObjectTestFragment fragment = new CardObjectTestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_object_test,container,false);


        nameTest = view.findViewById(R.id.name_test);

        int type = getArguments().getInt(TYPE_TEST);
        switch (type){
            case ORIGINAL_TYPE:
                nameTest.setText(getResources().getString(R.string.original));
                break;
            case TRANSCRIPT_TYPE:
                nameTest.setText(getResources().getString(R.string.transcription));
                break;
            case TRANSLATE_TYPE:
                nameTest.setText(getResources().getString(R.string.translate));
                break;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Object", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }
}
