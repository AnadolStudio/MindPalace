package com.anadol.mindpalace.view.Dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.anadol.mindpalace.model.SettingsPreference;
import com.anadol.mindpalace.domain.sortusecase.ComparatorMaker;
import com.anadol.mindpalace.R;

import static android.app.Activity.RESULT_OK;

public class SortDialog extends DialogFragment {
    public static final String TYPE_SORT = "type_sort";
    public static final String ORDER_SORT = "order_sort";
    public static final String TYPE_ITEMS = "type_items";
    private String typeItems;

    private Button mButton;
    private RadioGroup typeGroup;
    private RadioGroup orderGroup;

    private SortDialog() {
    }

    public static SortDialog newInstance(Types type) {
        Bundle args = new Bundle();
        args.putString(TYPE_ITEMS, type.name());
        SortDialog fragment = new SortDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_sort, null);
        mButton = view.findViewById(R.id.applyButton);
        typeGroup = view.findViewById(R.id.sortType_radioGroup);
        orderGroup = view.findViewById(R.id.sortOrder_radioGroup);

        typeItems = getArguments().getString(TYPE_ITEMS);
        int type = typeItems.equals(Types.GROUP.name()) ?
                SettingsPreference.getGroupTypeSort(getContext()) : SettingsPreference.getWordTypeSort(getContext());
        int order = typeItems.equals(Types.GROUP.name()) ?
                SettingsPreference.getGroupOrderSort(getContext()) : SettingsPreference.getWordOrderSort(getContext());

        typeGroup.check(type == ComparatorMaker.TYPE_NAME ? R.id.name_radio : R.id.date_radio);
        orderGroup.check(order == ComparatorMaker.ORDER_ASC ? R.id.asc_radio : R.id.desc_radio);

        mButton.setOnClickListener((v) -> apply());

        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    private void apply() {
        Intent intent = getSetting();
        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
        dismiss();
    }

    private Intent getSetting() {
        Intent intent = new Intent();

        int idType = typeGroup.getCheckedRadioButtonId();
        int type = idType == R.id.date_radio ? ComparatorMaker.TYPE_DATE : ComparatorMaker.TYPE_NAME;

        int idOrder = orderGroup.getCheckedRadioButtonId();
        int order = idOrder == R.id.asc_radio ? ComparatorMaker.ORDER_ASC : ComparatorMaker.ORDER_DESC;

        intent.putExtra(TYPE_SORT, type);
        intent.putExtra(ORDER_SORT, order);

        if(typeItems.equals(Types.GROUP.name())){
            SettingsPreference.setGroupTypeSort(getContext(), type);
            SettingsPreference.setGroupOrderSort(getContext(), order);
        }else {
            SettingsPreference.setWordTypeSort(getContext(), type);
            SettingsPreference.setWordOrderSort(getContext(), order);
        }

        return intent;
    }

    public enum Types {GROUP, WORD}
}
