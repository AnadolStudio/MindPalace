package com.anadol.mindpalace.presenter;

import android.util.Log;

import com.anadol.mindpalace.model.Word;

import java.util.Comparator;

import static com.anadol.mindpalace.model.Word.isRepeatable;

public class PriorityComparator implements Comparator<Word> {

    private static final String TAG = PriorityComparator.class.getName();
    private final long currentTime = System.currentTimeMillis();

    @Override
    public int compare(Word o1, Word o2) {
        boolean b1 = isRepeatable(o1.getTime(), currentTime, o1.getCountLearn());
        boolean b2 = isRepeatable(o2.getTime(), currentTime, o2.getCountLearn());
        // Сперва проверяю доступность для повторения
        // Возрастание = false > true, убывание = true > false
        int compare = Boolean.compare(b2, b1);
//        Log.i(TAG, "compareTime: " + b1 + " " + b2);


        if (compare == 0 && b1) {
            compare = Integer.compare(o2.getCountLearn(), o1.getCountLearn());
            Log.i(TAG, "compareCountLearn: " + o1.getCountLearn() + " " + o2.getCountLearn());
        }

        return compare;
    }
}
