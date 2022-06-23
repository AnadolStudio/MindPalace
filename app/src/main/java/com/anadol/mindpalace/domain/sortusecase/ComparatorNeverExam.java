package com.anadol.mindpalace.domain.sortusecase;

import com.anadol.mindpalace.data.group.Word;

import java.util.Comparator;

public class ComparatorNeverExam implements Comparator<Word> {

    @Override
    public int compare(Word o1, Word o2) {
        boolean b1 = o1.readyToExam();
        boolean b2 = o2.readyToExam();

        // Возрастание = false > true, убывание = true > false
        int compare = Boolean.compare(b2, b1);

        if (compare == 0) {
            compare = Boolean.compare(o1.isExam(), o2.isExam());
        }

        if (compare == 0) {
            compare = Integer.compare(o2.getCountLearn(), o1.getCountLearn());
        }

        return compare;
    }
}
