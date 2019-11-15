package com.anadol.rememberwords.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.myList.MyRecyclerAdapter;
import com.anadol.rememberwords.myList.MyViewHolder;

import java.util.ArrayList;

import static com.anadol.rememberwords.myList.Group.NON_COLOR;

public class ColorPicker extends AppCompatDialogFragment implements SeekBar.OnSeekBarChangeListener {
    private static final String CURRENT = "current";
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
    private MyRecyclerAdapter mAdapter;
    private Button addItemButton;

    private ArrayList<Integer> mList;
    private final int maxCountItems = 3;

    public static final String GRADIENT = "gradient";
    public static final String EXTRA_GRADIENT = "create_group_fragment";
    private static final String LIST = "list";
    private static final String MAX_COUNT = "max";
    private static final String TYPE_ITEM = "type_item";

    public static final int COLOR_START = Color.BLACK;
    public static final int COLOR_END = Color.WHITE;




    public static ColorPicker newInstance(int[] colors) {

        Bundle args = new Bundle();

        args.putIntArray(GRADIENT,colors);
        ColorPicker fragment = new ColorPicker();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putIntegerArrayList(LIST, mList);
        outState.putInt(CURRENT,currentItem);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        colors = getArguments().getIntArray(GRADIENT);
        if (savedInstanceState !=null){
            mList = savedInstanceState.getIntegerArrayList(LIST);
            currentItem = savedInstanceState.getInt(CURRENT);
        }else {
            mList = new ArrayList<>();
            currentItem = 0;
        }
        View v = LayoutInflater.from(getContext()).inflate(R.layout.color_picker,null);

        /*mToggleColor = v.findViewById(R.id.toggleColor);
        mToggleColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked) {
                    iRed = Color.red(colors[0]);
                    iGreen = Color.green(colors[0]);
                    iBlue = Color.blue(colors[0]);
                }else {
                    iRed = Color.red(colors[1]);
                    iGreen = Color.green(colors[1]);
                    iBlue = Color.blue(colors[1]);
                }

                red.setProgress(iRed);
                green.setProgress(iGreen);
                blue.setProgress(iBlue);
            }
        });*/

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
            if (colors[i] != NON_COLOR && mList.size()<3) {
                mList.add(colors[i]);
            }
            if (mList.size() == maxCountItems){
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


        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maxCountItems > mList.size()){
                    mList.add(Color.WHITE);
                    mAdapter.notifyDataSetChanged();
                    setDrawable();
                    currentItem = mList.size()-1;
                }
                if (maxCountItems == mList.size()) v.setEnabled(false);
                setDrawable();
                if (!red.isEnabled()){// достаточно провить только один seekBar
                    seekBarsEnabled(true);
                    currentItem = 0;
                }
                setValueSeekBars(mList.get(currentItem));
            }
        });

        createAdapter();
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SimpleItemHelperCallback(mAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(),DividerItemDecoration.VERTICAL));


        return new AlertDialog.Builder(getContext(),R.style.DialogStyle)
                .setView(v)
                .create();
    }


    private void createAdapter() {

        mAdapter = new MyRecyclerAdapter(mList, R.layout.item_color_list);
        mAdapter.setCreatorAdapter(new MyRecyclerAdapter.CreatorAdapter() {// ДЛЯ БОЛЬШЕЙ ГИБКОСТИ ТУТ Я РЕАЛИЗУЮ СЛУШАТЕЛЯ И МЕТОДЫ АДАПТЕРА
            @Override
            public void createHolderItems(MyViewHolder holder) {
                TextView number = holder.itemView.findViewById(R.id.number_text);
                ImageView imageView = holder.itemView.findViewById(R.id.image_item);
                holder.setViews(new View[]{number, imageView});
            }

            @Override
            public void bindHolderItems(final MyViewHolder holder) {
                int position = holder.getAdapterPosition();

                View[] views = holder.getViews();
                TextView number = (TextView) views[0];
                String s = Integer.toString(position + 1);
                number.setText(s);

                ImageView imageView = (ImageView) views[1];
                int color = mList.get(position);
                imageView.setImageDrawable(new ColorDrawable(color));
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentItem = holder.getAdapterPosition();
                        setValueSeekBars(mList.get(currentItem));
                    }
                });
            }
            @Override
            public void myOnItemDismiss(int position, int flag) {
                int size = mList.size()-1;// это для того чтобы при удалении последнего итема фокус прередавлся нынешнему последнему итему
                mList.remove(position);
                if (currentItem == position && currentItem == size) {
                    currentItem = mList.size()-1;
                }
                mAdapter.notifyItemRemoved(position);
//                mAdapter.notifyDataSetChanged();
                if (!addItemButton.isEnabled()){
                    addItemButton.setEnabled(true);
                }
                setDrawable();
                if (!mList.isEmpty()) {
                    setValueSeekBars(mList.get(currentItem));
                }
            }
        });
    }



    private void settingSeekBars(){
        red.setOnSeekBarChangeListener(this);
        green.setOnSeekBarChangeListener(this);
        blue.setOnSeekBarChangeListener(this);
    }

    private void setValueSeekBars(int c){

        red.setProgress(Color.red(c));
        green.setProgress(Color.green(c));
        blue.setProgress(Color.blue(c));
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        updateColors();
        sendResult(Activity.RESULT_OK, colors);
    }

    private GradientDrawable createGradient(){

        /*if (!mToggleColor.isChecked()){//Start color
            colors[0] = Color.rgb(iRed,iGreen,iBlue);
        }else {//End color
            colors[1] = Color.rgb(iRed,iGreen,iBlue);
        }*/
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
        if (mList.size() == 0){
            colors = new int[]{-1};
        }
    }

    private ColorDrawable createColor(){
        mColorDrawable.setColor(mList.get(0));
        return mColorDrawable;
    }

    private void sendResult(int resultCode, int[] colors){
        if (getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_GRADIENT,colors);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int color = mList.get(currentItem);
        int iRed = Color.red(color);
        int iGreen = Color.green(color);
        int iBlue = Color.blue(color);

        switch (seekBar.getId()){
            case R.id.red:
                iRed = progress;
                mTextRed.setText(Integer.toString(iRed));
                break;
            case R.id.green:
                iGreen = progress;
                mTextGreen.setText(Integer.toString(iGreen));
                break;
            case R.id.blue:
                iBlue= progress;
                mTextBlue.setText(Integer.toString(iBlue));
                break;
        }
        color = Color.rgb(iRed,iGreen,iBlue);
        mList.set(currentItem,color);
//        System.out.println(iRed+" "+iGreen+" "+iBlue);
        setDrawable();
    }

    private void setDrawable() {
        if (mList.size() > 1) {
            gradient.setImageDrawable(createGradient());
        } else if (mList.size()==0){
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

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mAdapter.notifyDataSetChanged();
    }
}
