package com.anadol.rememberwords.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.model.Word;

import java.util.ArrayList;

public class CardObjectTestFragment extends Fragment {
    public static final int ORIGINAL_TYPE = 0;
    public static final int TRANSLATE_TYPE = 1;

    private static final String OBJECT_TEST = "object_test";
    private static final String COUNT_OBJECTS_TEST = "count_objects_test";


    private TextView nameTest;

    public static CardObjectTestFragment newInstance(int type, ArrayList<Word> mWords) {

        Bundle args = new Bundle();
        args.putInt(OBJECT_TEST,type);
        int count = 0;
        switch (type) {
            case ORIGINAL_TYPE:
                for (Word w : mWords){
                    if (!w.getOriginal().equals("")){
                        count++;
                    }
                }
                break;
            case TRANSLATE_TYPE:
                for (Word w : mWords){
                    if (!w.getTranslate().equals("")){
                        count++;
                    }
                }
                break;
        }
        args.putInt(COUNT_OBJECTS_TEST,count);
        CardObjectTestFragment fragment = new CardObjectTestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_object_test,container,false);

        nameTest = view.findViewById(R.id.name_test);

        int type = getArguments().getInt(OBJECT_TEST);
        int count = getArguments().getInt(COUNT_OBJECTS_TEST);
        switch (type){
            case ORIGINAL_TYPE:
                nameTest.setText(getResources().getString(R.string.original));
                break;
            case TRANSLATE_TYPE:
                nameTest.setText(getResources().getString(R.string.translate));
                break;
        }
        if (count < 2){
            CardView cardView = view.findViewById(R.id.card_view);
            cardView.setCardBackgroundColor(getResources().getColor(R.color.colorBackgroundDisableCardTypeTest));
            view.setEnabled(false);
//            Toast.makeText(getContext(),"Lack of Objects", Toast.LENGTH_SHORT).show();
        }

        return view;
    }
}
