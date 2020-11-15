package com.anadol.mindpalace.view.Fragments;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anadol.mindpalace.presenter.MyPercentFormatter;
import com.anadol.mindpalace.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends MyFragment {
    private ScrollView mScrollView;
    private TextView additionalText;
    private TextView originalText;
    private TextView associationText;
    private TextView translateText;
    private TextView countRepsText;
    private LineChart mChart;

    public static InfoFragment newInstance() {
        InfoFragment fragment = new InfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        bind(view);
        getData(savedInstanceState);
        bindDataWithView();
        return view;
    }

    private void bind(View view) {
        mScrollView = view.findViewById(R.id.scrollView);
        originalText = view.findViewById(R.id.original_textView);
        associationText = view.findViewById(R.id.association_textView);
        translateText = view.findViewById(R.id.translate_textView);
        additionalText = view.findViewById(R.id.additional_textView);
        countRepsText = view.findViewById(R.id.count_reps);
        mChart = view.findViewById(R.id.chart_forget);
    }

    private void bindDataWithView() {
        originalText.setText(getString(R.string.exaple_original));
        associationText.setText(getString(R.string.exaple_association));
        translateText.setText(getString(R.string.exaple_translate));

        String isLearned = getString(R.string.learning);
        String date = getDate();
        int countLearned = 1;

        additionalText.setText(getString(R.string.additional, isLearned, countLearned, date));
        countRepsText.setText(getStatus(isLearned, date, countLearned));

        setupChart(mChart, getLineData());
    }

    private SpannableString getStatus(String isLearned, String date, int countLearned) {
        Resources resources = getResources();

        String s = resources.getString(R.string.reps,
                isLearned, countLearned, date);

        SpannableString info = new SpannableString(s);

        int colorTimeRepeat = resources.getColor(R.color.colorNotReadyRepeat);
        int colorStatus = resources.getColor(R.color.colorLearning);

        int indexDate = s.indexOf(date);
        int indexStatus = s.indexOf(isLearned);
        info.setSpan(new ForegroundColorSpan(colorTimeRepeat), indexDate, indexDate + date.length(), 0);
        info.setSpan(new ForegroundColorSpan(colorStatus), indexStatus, indexStatus + isLearned.length(), 0);
        return info;
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
        int colorAccent = getResources().getColor(R.color.colorAccent);
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

    private String getDate() {
        long time = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2);
        TimeZone timeZone = TimeZone.getDefault();
        SimpleDateFormat format = new SimpleDateFormat("d MMM H:mm");
        format.setTimeZone(timeZone);
        return format.format(time);
    }

    private void getData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(SCROLL_POSITION);
            mScrollView.setScrollY(position);
        }
    }
}
