package com.anadol.mindpalace.view.Fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.anadol.mindpalace.R;
import com.anadol.mindpalace.presenter.MyPercentFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class LessonRepeatingFragment extends LessonFragment {
    private ScrollView mScrollView;
    private LineChart mChart;

    public static LessonRepeatingFragment newInstance() {
        LessonRepeatingFragment fragment = new LessonRepeatingFragment();
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SCROLL_POSITION, mScrollView.getScrollY());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson_repeating, container, false);

        bind(view);
        getData(savedInstanceState);
        bindDataWithView();
        return view;
    }

    private void bind(View view) {
        mScrollView = view.findViewById(R.id.scrollView);
        mChart = view.findViewById(R.id.chart_forget);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener((v) -> getActivity().onBackPressed());
    }

    private void bindDataWithView() {
        setupChart(mChart, getLineData());
    }

    private LineData getLineData() {

        ArrayList<Entry> values = new ArrayList<>();
        int[] ints = new int[]{100, 58, 44, 36, 33, 28, 25, 21};
        for (int i = 0; i < ints.length; i++) {
            float val = (float) ints[i];
            values.add(new Entry(i, val));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        set1.setLineWidth(1.75f);
        set1.setCircleRadius(5f);
        set1.setCircleHoleRadius(2.5f);
        int colorAccent = getResources().getColor(R.color.colorSecondary);
        set1.setColor(colorAccent);
        set1.setCircleColor(colorAccent);
        set1.setHighLightColor(colorAccent);
        set1.setDrawValues(false);

        // create a data object with the data sets
        return new LineData(set1);
    }

    private void setupChart(LineChart chart, LineData data) {

        ((LineDataSet) data.getDataSetByIndex(0)).setCircleHoleColor(Color.WHITE);

//        chart.getDescription().setEnabled(false);
        chart.getDescription().setTextSize(14);
        chart.getDescription().setTypeface(Typeface.SERIF);
        chart.getDescription().setText(getString(R.string.forgetting_curve));

//        chart.setDrawGridBackground(false);

//        chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        // set custom chart offsets (automatic offset calculation is hereby disabled)
//        chart.setViewPortOffsets(10, 0, 10, 0);

        // add data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);

        YAxis axisLeft = chart.getAxisLeft();
        axisLeft.setSpaceTop(40);
        axisLeft.setSpaceBottom(40);
        axisLeft.setValueFormatter(new MyPercentFormatter());
        axisLeft.setTypeface(Typeface.SERIF);
        axisLeft.setTextSize(12);
        axisLeft.setAxisMaximum(100f);
        axisLeft.setAxisMinimum(0f);

        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setEnabled(false);

    }

    private void getData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(SCROLL_POSITION);
            mScrollView.setScrollY(position);
        }
    }
}
