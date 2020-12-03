package com.anadol.mindpalace.view.Dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.anadol.mindpalace.R;
import com.anadol.mindpalace.model.Group;
import com.anadol.mindpalace.view.Fragments.GroupDetailFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;

import static android.app.Activity.RESULT_OK;

public class SettingsBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = SettingsBottomSheet.class.getName();

    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    private static final String COLORS = "colors";
    private static final String URI = "uri";
    private static final String[] STORAGE_PERMISSION = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE};


    private NestedScrollView mScrollView;
    private ImageButton cancelButton;
    private EditText mEditText;
    private ChipGroup typeChipGroup;
    private ChipGroup colorsChipGroup;
    private ImageView mImageView;
    private ImageButton gradientButton;
    private ImageButton photoButton;
    private Button applyButton;
    private LinearLayout llColorPicker;
    private Slider red;
    private Slider green;
    private Slider blue;

    private Group mGroup;
    private int type;
    private Uri uriPhoto;
    private int[] colorsGradient;


    public static SettingsBottomSheet newInstance(Group group) {

        Bundle args = new Bundle();
        Group g = new Group(group);
        args.putParcelable(GroupDetailFragment.GROUP, g);
        SettingsBottomSheet fragment = new SettingsBottomSheet();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveGroup(mEditText.getText().toString());
        outState.putParcelable(GroupDetailFragment.GROUP, mGroup);
        if (colorsGradient != null) {
            outState.putIntArray(COLORS, colorsGradient);
        }
        if (uriPhoto != null) {
            outState.putString(URI, uriPhoto.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_settings, container, false);
        bind(view);
        setListeners();
        getData(savedInstanceState);
        bindDataWithView();

        return view;
    }

    private void bind(View view) {
        cancelButton = view.findViewById(R.id.cancel_button);
        mEditText = view.findViewById(R.id.editText);
        typeChipGroup = view.findViewById(R.id.type_group);
        colorsChipGroup = view.findViewById(R.id.colors_for_gradient);
        mImageView = view.findViewById(R.id.image_view);
        gradientButton = view.findViewById(R.id.imageGradient);
        photoButton = view.findViewById(R.id.imagePhoto);
        applyButton = view.findViewById(R.id.applyButton);
        llColorPicker = view.findViewById(R.id.ll_color_picker);
        red = view.findViewById(R.id.red_slider);
        green = view.findViewById(R.id.green_slider);
        blue = view.findViewById(R.id.blue_slider);
        mScrollView = view.findViewById(R.id.scrollView);
    }

    private void setListeners() {
        cancelButton.setOnClickListener(v -> dismiss());
        typeChipGroup.setOnCheckedChangeListener((chipGroup, i) -> {
            switch (i) {
                case R.id.numbers_chip:
                    type = Group.TYPE_NUMBERS;
                    break;
                case R.id.dates_chip:
                    type = Group.TYPE_DATES;
                    break;
                case R.id.texts_chip:
                    type = Group.TYPE_TEXTS;
                    break;
                case R.id.foreign_words_chip:
                    type = Group.TYPE_LINK;
                    break;
            }
        });
        colorsChipGroup.setOnCheckedChangeListener((chipGroup, i) -> {
            Log.i(TAG, "setListeners: chip id" + i);

            if (colorsGradient == null){
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                return;// Однажды была ошибка, выявить не смог
            }

            switch (i) {
                case R.id.color_one:
                    setValueSeekBars(colorsGradient[0]);
                    break;
                case R.id.color_two:
                    setValueSeekBars(colorsGradient[1]);
                    break;
                case R.id.color_three:
                    setValueSeekBars(colorsGradient[2]);
                    break;
            }
        });
        gradientButton.setOnClickListener(v -> {
            if (colorsGradient == null) {
                setVisibleColorPicker();

                uriPhoto = null;
                colorsGradient = mGroup.getColors();
                mImageView.setImageDrawable(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colorsGradient));

                if (colorsChipGroup.getCheckedChipId() == -1) {
                    updateAllColorChips();
                    colorsChipGroup.check(R.id.color_one);
                }
            }
        });
        photoButton.setOnClickListener(v -> {
            if (hasStoragePermission()) {
                llColorPicker.setVisibility(View.GONE);
                colorsGradient = null;
                createPhotoPickerIntent();
            } else {
                requestPermissions(STORAGE_PERMISSION, REQUEST_STORAGE_PERMISSION);
            }

        });
        applyButton.setOnClickListener(v -> save());

        MySeekBarChangeListener mSeekBarChangeListener = new MySeekBarChangeListener();

        red.addOnChangeListener(mSeekBarChangeListener);
        green.addOnChangeListener(mSeekBarChangeListener);
        blue.addOnChangeListener(mSeekBarChangeListener);
    }

    private void createPhotoPickerIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_GALLERY);
    }

    private boolean hasStoragePermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), STORAGE_PERMISSION[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void updateAllColorChips() {
        for (int i = 0; i < colorsChipGroup.getChildCount(); i++) {
            updateChip((Chip) colorsChipGroup.getChildAt(i), colorsGradient[i]);
        }
    }

    private void updateChip(Chip chip, int color) {
        chip.setChipBackgroundColor(ColorStateList.valueOf(color));// Работает только это вариант, остальные выдают ошибку
    }

    private void setValueSeekBars(int color) {
        red.setValue(Color.red(color));
        green.setValue(Color.green(color));
        blue.setValue(Color.blue(color));
    }

    private void getData(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mGroup = getArguments().getParcelable(GroupDetailFragment.GROUP);
        } else {
            mGroup = savedInstanceState.getParcelable(GroupDetailFragment.GROUP);
            colorsGradient = savedInstanceState.getIntArray(COLORS);
            String uri = savedInstanceState.getString(URI);
            if (uri != null) uriPhoto = Uri.parse(uri);
        }
    }

    private void bindDataWithView() {
        mEditText.setText(mGroup.getName());
        mEditText.setSelection(mEditText.length());
        mGroup.getImage(mImageView);
        int type = mGroup.getType();
        switch (type) {
            default:
            case Group.TYPE_NUMBERS:
                typeChipGroup.check(R.id.numbers_chip);
                break;
            case Group.TYPE_DATES:
                typeChipGroup.check(R.id.dates_chip);
                break;
            case Group.TYPE_TEXTS:
                typeChipGroup.check(R.id.texts_chip);
                break;
            case Group.TYPE_LINK:
                typeChipGroup.check(R.id.foreign_words_chip);
                break;
        }
        if (colorsGradient == null) {
            llColorPicker.setVisibility(View.GONE);
        } else {
            setVisibleColorPicker();
            updateAllColorChips();
        }

        RealFormatter formatter = new RealFormatter();

        red.setLabelFormatter(formatter);
        green.setLabelFormatter(formatter);
        blue.setLabelFormatter(formatter);
    }

    private void setVisibleColorPicker() {
        llColorPicker.setVisibility(View.VISIBLE);
        //TODO опускание вниз scrollView
    }

    private void save() {
        String name = mEditText.getText().toString().trim();
        // Групп с именем "" быть не должно
        if (!name.equals(mGroup.getName())) {
            if (name.equals("")) {
                mEditText.setError(getString(R.string.is_empty));
                return;
            }
        }
        saveGroup(name);

        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(GroupDetailFragment.GROUP, mGroup);
        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
        dismiss();
    }

    private void saveGroup(String name) {
        mGroup.setName(name);
        if (uriPhoto != null) {
            mGroup.setPathPhoto(uriPhoto);
        } else if (colorsGradient != null) {
            mGroup.setColors(colorsGradient);
        }
        mGroup.setType(type);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle(STYLE_NORMAL, R.style.BottomSheetModalTheme);
//        setCancelable(false);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_GALLERY:

                uriPhoto = data.getData();
                Log.i(TAG, "onActivityResult: " + uriPhoto.toString());
                mImageView.setImageURI(null);
                mImageView.setImageURI(uriPhoto);

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                if (hasStoragePermission()) {
                    createPhotoPickerIntent();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private static class RealFormatter implements LabelFormatter {
        @NonNull
        @Override
        public String getFormattedValue(float value) {
            int i = Math.round(value);
            return Integer.toString(i);
        }
    }

    class MySeekBarChangeListener implements Slider.OnChangeListener {
        int i = 0;

        @Override
        public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
            int iRed = Color.red(colorsGradient[i]);
            int iGreen = Color.green(colorsGradient[i]);
            int iBlue = Color.blue(colorsGradient[i]);

            int id = colorsChipGroup.getCheckedChipId();
            switch (id) {
                default:
                case R.id.color_one:
                    i = 0;
                    break;
                case R.id.color_two:
                    i = 1;
                    break;
                case R.id.color_three:
                    i = 2;
                    break;
            }

            switch (slider.getId()) {
                case R.id.red_slider:
                    iRed = Math.round(value);
                    break;
                case R.id.green_slider:
                    iGreen = Math.round(value);
                    break;
                case R.id.blue_slider:
                    iBlue = Math.round(value);
                    break;
            }
            colorsGradient[i] = Color.rgb(iRed, iGreen, iBlue);
            updateChip((Chip) colorsChipGroup.getChildAt(i), colorsGradient[i]);
            mImageView.setImageDrawable(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colorsGradient));

            mGroup.setColors(colorsGradient);
        }

    }
}
