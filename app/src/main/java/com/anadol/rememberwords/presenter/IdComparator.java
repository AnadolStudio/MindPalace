package com.anadol.rememberwords.presenter;

import com.anadol.rememberwords.model.SimpleParent;

import java.util.Comparator;

public class IdComparator implements Comparator<SimpleParent> {
    @Override
    public int compare(SimpleParent o1, SimpleParent o2) {
        Integer i1 = o1.getTableId();
        Integer i2 = o2.getTableId();
        return i1.compareTo(i2);
    }
}
