package com.anadol.rememberwords.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anadol.rememberwords.R;

public class CardTypeTestFragment extends Fragment {
    public static final int EASY = 0;
    public static final int MIDDLE = 1;
    public static final int HARD = 2;

    private static final String TYPE_TEST = "type_test";
    private static final String COUNT_WORDS = "count_words";


    private TextView nameTest;
    private ImageView star1;
    private ImageView star2;
    private ImageView star3;

    public static CardTypeTestFragment newInstance(int type, int countWords) {

        Bundle args = new Bundle();
        args.putInt(TYPE_TEST,type);
        args.putInt(COUNT_WORDS,countWords);
        CardTypeTestFragment fragment = new CardTypeTestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_type_test,container,false);


        nameTest = view.findViewById(R.id.name_test);
        star1 = view.findViewById(R.id.star1);
        star2 = view.findViewById(R.id.star2);
        star3 = view.findViewById(R.id.star3);

        int type = getArguments().getInt(TYPE_TEST);
        int count = getArguments().getInt(COUNT_WORDS);

        switch (type){
            case EASY:
                nameTest.setText(getResources().getString(R.string.quiz));
                star1.setImageDrawable(getResources().getDrawable(R.drawable.ic_star));
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border));

                if (count < 4) { // 4 - временная, возможно тут нужно другое значение
                    CardView cardView = view.findViewById(R.id.card_view);
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorBackgroundDisableCardTypeTest));
                    view.setEnabled(false);
                }
                break;
            case MIDDLE:
                nameTest.setText(getResources().getString(R.string.true_false));
                star1.setImageDrawable(getResources().getDrawable(R.drawable.ic_star));
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border));

                if (count < 2) {
                    CardView cardView = view.findViewById(R.id.card_view);
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorBackgroundDisableCardTypeTest));
                    view.setEnabled(false);
                }
                break;
            case HARD:
                nameTest.setText(getResources().getString(R.string.answer_question));
                star1.setImageDrawable(getResources().getDrawable(R.drawable.ic_star));
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star));
                if (count < 2) {
                    CardView cardView = view.findViewById(R.id.card_view);
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.colorBackgroundDisableCardTypeTest));
                    view.setEnabled(false);
                }
                break;
        }

        return view;
    }
}
