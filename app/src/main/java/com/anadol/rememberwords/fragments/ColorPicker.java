package com.anadol.rememberwords.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.presenter.ItemTouchHelperAdapter;
import com.anadol.rememberwords.presenter.SimpleItemHelperCallback;

import java.util.ArrayList;

import static com.anadol.rememberwords.model.Group.NON_COLOR;

//TODO: потенциальный апгрейд - возможность выбирать в качестве фона
// не только цвет, но и картинку
public class ColorPicker extends AppCompatDialogFragment implements SeekBar.OnSeekBarChangeListener {
    public static final String GRADIENT = "gradient";
    public static final String EXTRA_GRADIENT = "create_group_fragment";
    private static final String TAG = "ColorPicker";
    private static final String CURRENT = "current";
    private static final String LIST = "list";
    private static final String MAX_COUNT = "max";
    private static final String TYPE_ITEM = "type_item";
    private final int maxCountItems = 3;
    private ImageView gradient;
    private SeekBar red;
    private SeekBar green;
    private SeekBar blue;
    private TextView mTextRed;
    private TextView mTextGreen;
    private TextView mTextBlue;
    private int[] colors;
    private int currentItem;
    private GradientDrawable mGradientDrawable;
    private ColorDrawable mColorDrawable;
    private RecyclerView mRecyclerView;
    private ColorAdapter mAdapter;
    private ImageButton addItemButton;
    private ArrayList<Integer> mList;

    public static ColorPicker newInstance(int[] colors) {

        Bundle args = new Bundle();

        args.putIntArray(GRADIENT, colors);
        ColorPicker fragment = new ColorPicker();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putIntegerArrayList(LIST, mList);
        outState.putInt(CURRENT, currentItem);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        colors = getArguments().getIntArray(GRADIENT);
        if (savedInstanceState != null) {
            mList = savedInstanceState.getIntegerArrayList(LIST);
            currentItem = savedInstanceState.getInt(CURRENT);
        } else {
            mList = new ArrayList<>();
            currentItem = 0;
        }
        View v = LayoutInflater.from(getContext()).inflate(R.layout.color_picker, null);


        gradient = v.findViewById(R.id.group_color);
        mGradientDrawable = new GradientDrawable();
        mColorDrawable = new ColorDrawable();


        red = v.findViewById(R.id.red);//seekBars
        green = v.findViewById(R.id.green);
        blue = v.findViewById(R.id.blue);

        mTextRed = v.findViewById(R.id.red_edit_text);
        mTextGreen = v.findViewById(R.id.green_edit_text);
        mTextBlue = v.findViewById(R.id.blue_edit_text);

        addItemButton = v.findViewById(R.id.add_button);
        for (int i = 0; i < colors.length; i++) { // Должен работать только при первом создании, но на данный момент это не так. Оставить как Фичу?
            if (colors[i] != NON_COLOR && mList.size() < 3) {
                mList.add(colors[i]);
            }
            if (mList.size() == maxCountItems) {
                addItemButton.setEnabled(false);
            }

        }
        setDrawable();
        settingSeekBars();
        setValueSeekBars(mList.get(currentItem));

        String textR = Integer.toString(red.getProgress());
        String textG = Integer.toString(green.getProgress());
        String textB = Integer.toString(blue.getProgress());
        mTextRed.setText(textR);//иначе принимает string res
        mTextGreen.setText(textG);
        mTextBlue.setText(textB);


        addItemButton.setOnClickListener(v1 -> {
            if (maxCountItems > mList.size()) {
                mList.add(Color.BLACK);
                mAdapter.notifyDataSetChanged();
                setDrawable();
                currentItem = mList.size() - 1;
            }
            if (maxCountItems == mList.size()) v1.setEnabled(false);
            setDrawable();
            if (!red.isEnabled()) {// достаточно провить только один seekBar
                seekBarsEnabled(true);
                currentItem = 0;
            }
            setValueSeekBars(mList.get(currentItem));
        });

        mRecyclerView = v.findViewById(R.id.recycler_view);

        mAdapter = new ColorAdapter(mList);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
//        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SimpleItemHelperCallback(mAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.HORIZONTAL));

        Dialog dialog = new AlertDialog.Builder(getActivity()/*,R.style.DialogStyle*/)
                .setView(v)
                .create();

        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        updateColors();
        sendResult(Activity.RESULT_OK, colors);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int color = mList.get(currentItem);
        int iRed = Color.red(color);
        int iGreen = Color.green(color);
        int iBlue = Color.blue(color);

        switch (seekBar.getId()) {
            case R.id.red:
                iRed = progress;
                mTextRed.setText(Integer.toString(iRed));
                break;
            case R.id.green:
                iGreen = progress;
                mTextGreen.setText(Integer.toString(iGreen));
                break;
            case R.id.blue:
                iBlue = progress;
                mTextBlue.setText(Integer.toString(iBlue));
                break;
        }
        color = Color.rgb(iRed, iGreen, iBlue);
        mList.set(currentItem, color);
//        System.out.println(iRed+" "+iGreen+" "+iBlue);
        setDrawable();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mAdapter.notifyDataSetChanged();
    }


    private void settingSeekBars() {
        red.setOnSeekBarChangeListener(this);
        green.setOnSeekBarChangeListener(this);
        blue.setOnSeekBarChangeListener(this);
    }

    private void setValueSeekBars(int c) {

        red.setProgress(Color.red(c));
        green.setProgress(Color.green(c));
        blue.setProgress(Color.blue(c));
    }

    private GradientDrawable createGradient() {

        updateColors();
        mGradientDrawable.setColors(colors);
        mGradientDrawable.setOrientation(Orientation.LEFT_RIGHT);
        return mGradientDrawable;
    }

    private void updateColors() {
        colors = new int[mList.size()];
        for (int i = 0; i < mList.size(); i++) {
            colors[i] = mList.get(i);
        }
        if (mList.size() == 0) {
            colors = new int[]{-1};
        }
    }

    private ColorDrawable createColor() {
        mColorDrawable.setColor(mList.get(0));
        return mColorDrawable;
    }

    private void sendResult(int resultCode, int[] colors) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_GRADIENT, colors);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    private void setDrawable() {
        if (mList.size() > 1) {
            gradient.setImageDrawable(createGradient());
        } else if (mList.size() == 0) {
            gradient.setImageDrawable(null);
            seekBarsEnabled(false);
        } else {
            gradient.setImageDrawable(createColor());
        }
    }

    private void seekBarsEnabled(boolean b) {
        red.setEnabled(b);
        green.setEnabled(b);
        blue.setEnabled(b);
    }

    private void addAnimation() {
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {

                        int parent = mRecyclerView.getBottom();

                        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                            View v = mRecyclerView.getChildAt(i);
//                                v.setAlpha(0.0f);
                            v.setY(parent);
                            v.animate().translationY(1.0f)
                                    .setDuration(200)
                                    .setStartDelay(i * 50)
                                    .start();
                            v.animate().setStartDelay(0);//возвращаю дефолтное значение
                        }

                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        Log.i(TAG, "Remove OnPreDrawListener");
                        return true;
                    }
                });
    }

    public class ColorAdapter extends RecyclerView.Adapter<ColorHolder>
            implements ItemTouchHelperAdapter {

        private ArrayList<Integer> list;

        public ColorAdapter(ArrayList<Integer> list) {
            setList(list);
        }

        public ColorAdapter() {
            list = new ArrayList<>();
        }

        public void setList(ArrayList<Integer> list) {
            this.list = list;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                addAnimation();
            }
            notifyDataSetChanged();

        }

        @NonNull
        @Override
        public ColorHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_color_list, viewGroup, false);
            return new ColorHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ColorHolder colorHolder, int i) {
            colorHolder.bind(list.get(i));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder viewHolder, int flag) {
            int size = list.size() - 1;// это для того чтобы при удалении последнего итема фокус прередавлся нынешнему последнему итему
            int position = viewHolder.getAdapterPosition();
            list.remove(position);

            // Если фокус был на последнем item
            if (currentItem == position && currentItem == size) {
                currentItem = list.size() - 1;
            }
            mAdapter.notifyItemRemoved(position);
//                mAdapter.notifyDataSetChanged();
            if (!addItemButton.isEnabled()) {
                addItemButton.setEnabled(true);
            }
            setDrawable();

            if (!list.isEmpty()) {
                if (currentItem >= list.size()) {
                    currentItem = list.size() - 1;
                }
                setValueSeekBars(list.get(currentItem));
            }
        }

    }

    public class ColorHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        ImageView imageColor;

        public ColorHolder(@NonNull View itemView) {
            super(itemView);

            imageColor = itemView.findViewById(R.id.image_item);

            itemView.setOnClickListener(this);
//            itemView.setOnLongClickListener(this);
        }


        public void bind(int color) {
            imageColor.setImageDrawable(new ColorDrawable(color));
        }

        @Override
        public void onClick(View view) {
            currentItem = getAdapterPosition();
            setValueSeekBars(mList.get(currentItem));
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}
