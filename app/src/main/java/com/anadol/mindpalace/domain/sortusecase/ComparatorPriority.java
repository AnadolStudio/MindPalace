package com.anadol.mindpalace.domain.sortusecase;

import com.anadol.mindpalace.model.Word;

import java.util.Comparator;

public class ComparatorPriority implements Comparator<Word> {

    private static final String TAG = ComparatorPriority.class.getName();

    @Override
    public int compare(Word o1, Word o2) {
        boolean b1 = o1.isRepeatable();
        boolean b2 = o2.isRepeatable();
        // Сперва проверяю доступность для повторения
        // Возрастание = false > true, убывание = true > false
        int compare = Boolean.compare(b2, b1);
//        Log.i(TAG, "compareTime: " + b1 + " " + b2);

        if (compare == 0) {
            if (!b1){
                compare = Boolean.compare(o1.isExam(), o2.isExam());
            }

            if (compare == 0){
                compare = Integer.compare(o2.getCountLearn(), o1.getCountLearn());
            }

            if (compare == 0){
                compare = Long.compare(o1.getTime(), o2.getTime());
            }
//            Log.i(TAG, "compareCountLearn: " + o1.getCountLearn() + " " + o2.getCountLearn());
        }
        return compare;
    }
}
