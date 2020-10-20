package com.anadol.rememberwords.presenter;

import com.anadol.rememberwords.model.SimpleParent;

import java.util.Comparator;

public class StringIntegerComparator implements Comparator<SimpleParent> {
    @Override
    public int compare(SimpleParent o1, SimpleParent o2) {

        Integer i1;
        Integer i2;
        if (isInt(o1.toString()) && isInt(o2.toString())) {
            i1 = Integer.parseInt(o1.toString());
            i2 = Integer.parseInt(o2.toString());
            return i1.compareTo(i2);
        }else if (!(isInt(o1.toString()) || isInt(o2.toString()))){
            return o1.toString().compareTo(o2.toString());
        }else {
            return -1;
        }
    }

    private boolean isInt(String s){
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}