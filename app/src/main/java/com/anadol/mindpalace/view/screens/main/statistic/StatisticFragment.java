package com.anadol.mindpalace.view.screens.main.statistic;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anadol.mindpalace.R;
import com.anadol.mindpalace.data.group.BackgroundSingleton;
import com.anadol.mindpalace.domain.formatters.NameAxisFormatter;
import com.anadol.mindpalace.domain.formatters.IntegerFormatter;
import com.anadol.mindpalace.data.statistic.GroupStatisticItem;
import com.anadol.mindpalace.view.screens.SimpleFragment;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends SimpleFragment {
    private static final Typeface TYPEFACE = Typeface.SERIF;
    private static final String TAG = StatisticFragment.class.getName();

    private static final String ITEMS = "items";
    private PieChart mChartGeneral;
    private BarChart mChartDetail;
    private ScrollView mScrollView;
    private TextView needToLearnText;
    private TextView learningText;
    private TextView learnedText;
    private TextView allLearnText;
    private ArrayList<GroupStatisticItem> mStatisticItems;
    private Disposable mDisposable;

    public static StatisticFragment newInstance() {
        return new StatisticFragment();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SCROLL_POSITION, mScrollView.getScrollY());
        outState.putParcelableArrayList(ITEMS, mStatisticItems);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        bind(view);
        setupChartGeneral();
        setupChartDetail();

        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(SCROLL_POSITION);
            mScrollView.setScrollY(position);
            mStatisticItems = savedInstanceState.getParcelableArrayList(ITEMS);
            setData(mStatisticItems);
        } else {
            mStatisticItems = new ArrayList<>();
            StatisticBackground background = new StatisticBackground();
            background.getStatistic();
        }

        return view;
    }

    private void bind(View view) {
        mChartDetail = view.findViewById(R.id.chart_detail);
        mChartGeneral = view.findViewById(R.id.chart_general);
        mScrollView = view.findViewById(R.id.scrollView);
        needToLearnText = view.findViewById(R.id.examAssociation_textView);
        learningText = view.findViewById(R.id.learningAssociation_textView);
        learnedText = view.findViewById(R.id.needToLearnAssociation_textView);
        allLearnText = view.findViewById(R.id.allAssociation_textView);
    }

    private void setData(ArrayList<GroupStatisticItem> items) {
        int needToLearn = 0;
        int learning = 0;
        int learned = 0;
        for (int i = 0; i < items.size(); i++) {
            needToLearn += items.get(i).getNeedToLearn();
            learning += items.get(i).getLearning();
            learned += items.get(i).getLearned();
        }
        updateTexts(needToLearn, learning, learned, (needToLearn + learning + learned));
        if (!items.isEmpty()) {
            setDataChartGeneral(new int[]{needToLearn, learning, learned}, getWordsLabels());
            setDataChartDetail(items);
        }
    }

    private void updateTexts(int needToLearn, int learning, int learned, int total) {
        needToLearnText.setText(getString(R.string.need_to_learn_associations, needToLearn));
        learningText.setText(getString(R.string.learning_associations, learning));
        learnedText.setText(getString(R.string.learned_associations, learned));
        allLearnText.setText(getString(R.string.total_association, total));
    }

    @Override
    public void onStart() {
        super.onStart();
        ArrayMap<String, Observable> lastAction = BackgroundSingleton.get(getContext()).getStackActions();
        if (lastAction.size() > 0 && mDisposable == null) {
            StatisticBackground background = new StatisticBackground();
            if (lastAction.containsKey(BackgroundSingleton.DatabaseApiKeys.GET_GROUP_STATISTIC.name())) {
                background.getStatistic();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    private void setupChartDetail() {
        Resources resources = getResources();

        mChartDetail.getLayoutParams().height = (int) (getResources().getDisplayMetrics().heightPixels * 0.75);
        mChartDetail.setNoDataTextColor(getResources().getColor(R.color.colorPrimaryText));
        mChartDetail.setNoDataText(getString(R.string.no_data));
        mChartDetail.setNoDataTextTypeface(TYPEFACE);
        YAxis axisLeft = mChartDetail.getAxisLeft();
        axisLeft.setAxisMinimum(0f);
        axisLeft.setTextSize(12f);
        axisLeft.setTypeface(TYPEFACE);
        axisLeft.setTextColor(resources.getColor(R.color.colorPrimaryText));
        mChartDetail.getAxisRight().setEnabled(false);
        mChartDetail.getDescription().setEnabled(false);
        mChartDetail.setDrawValueAboveBar(false);
        mChartDetail.setScaleYEnabled(false);

        XAxis xAxis = mChartDetail.getXAxis();
        xAxis.setTypeface(TYPEFACE);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(resources.getColor(R.color.colorPrimaryText));

        Legend l = mChartDetail.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTextSize(14f);
        l.setTextColor(resources.getColor(R.color.colorPrimaryText));
        l.setTypeface(TYPEFACE);
        l.setFormSize(10f);
        l.setFormToTextSpace(6f);
        l.setXEntrySpace(21f);
        mChartDetail.animateY(1400);
    }

    private void setDataChartDetail(ArrayList<GroupStatisticItem> items) {
        XAxis xAxis = mChartDetail.getXAxis();
        ArrayList<String> names = new ArrayList<>();

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            names.add(items.get(i).getName());
            entries.add(new BarEntry(i,
                    items.get(i).getValues()));
        }
        xAxis.setValueFormatter(new NameAxisFormatter((names)));

        BarDataSet dataSet = null;

        dataSet = new BarDataSet(entries, "");
        dataSet.setStackLabels(getWordsLabels());
        dataSet.setColors(getColors());

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);
        data.setValueFormatter(new IntegerFormatter());
        data.setValueTypeface(TYPEFACE);
        data.setValueTextSize(12f);

        mChartDetail.setData(data);
        mChartDetail.setFitBars(true);
        mChartDetail.invalidate();
    }

    private void setupChartGeneral() {
        Resources resources = getResources();
        Description description = mChartGeneral.getDescription();
        description.setText(getString(R.string.general_statistic));
        description.setTextSize(14f);
        description.setTypeface(TYPEFACE);
        description.setTextColor(resources.getColor(R.color.colorPrimaryText));

        mChartGeneral.setNoDataTextColor(getResources().getColor(R.color.colorPrimaryText));
        mChartGeneral.setNoDataText(getString(R.string.no_data));
        mChartGeneral.setNoDataTextTypeface(TYPEFACE);
        mChartGeneral.setExtraOffsets(5, 10, 5, 5);
        mChartGeneral.setRotationAngle(0);
        mChartGeneral.getLayoutParams().height = (int) (getResources().getDisplayMetrics().heightPixels * 0.55);
        mChartGeneral.setHoleRadius(30f);
        mChartGeneral.setTransparentCircleRadius(36f);

        Legend l = mChartGeneral.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

        int orientation = resources.getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(true);
        } else {
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);
        }

        l.setTextSize(14f);
        l.setTextColor(getResources().getColor(R.color.colorPrimaryText));
        l.setTypeface(TYPEFACE);

        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setYOffset(-5f);
        l.setEnabled(true);

        mChartGeneral.setEntryLabelTextSize(14f);
        mChartGeneral.setEntryLabelTypeface(TYPEFACE);
        mChartGeneral.setDrawEntryLabels(false);

        mChartGeneral.animateY(1400, Easing.EaseInOutQuad);
    }

    private void setDataChartGeneral(int[] items, String[] labels) {

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            entries.add(new PieEntry(items[i], labels[i]));
        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(getColors());

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new IntegerFormatter());
        data.setValueTypeface(TYPEFACE);
        data.setValueTextSize(16f);

        mChartGeneral.setData(data);
        mChartGeneral.invalidate();
    }

    private String[] getWordsLabels() {
        String needToLearn = getString(R.string.not_learned);
        String learning = getString(R.string.learning);
        String learned = getString(R.string.learned);
        return new String[]{needToLearn, learning, learned};
    }

    private int[] getColors() {
        Resources resources = getResources();
        int needToLearn = resources.getColor(R.color.colorNeedToLearn);
        int learning = resources.getColor(R.color.colorLearning);
        int learned = resources.getColor(R.color.colorLearned);
        return new int[]{needToLearn, learning, learned};
    }

    public class StatisticBackground {

        public void getStatistic() {
            showLoadingDialog();
            Observable<ArrayList<GroupStatisticItem>> statisticList = BackgroundSingleton.get(getContext()).getGroupStatistic();
            mDisposable = statisticList.subscribe(groupStatisticItems -> {
                mStatisticItems = groupStatisticItems;
                setData(mStatisticItems);
                hideLoadingDialog();
            });
        }
    }
}