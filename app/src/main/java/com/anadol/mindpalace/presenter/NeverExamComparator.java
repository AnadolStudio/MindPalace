package com.anadol.mindpalace.presenter;

import com.anadol.mindpalace.model.Word;

import java.util.Comparator;

public class NeverExamComparator implements Comparator<Word> {
    private static final String TAG = NeverExamComparator.class.getName();

    @Override
    public int compare(Word o1, Word o2) {
        boolean b1 = o1.isExam();
        boolean b2 = o2.isExam();
        // Возрастание = false > true, убывание = true > false
        //        Log.i(TAG, "compareTime: " + b1 + " " + b2);
        return Boolean.compare(b1, b2);
    }
}
