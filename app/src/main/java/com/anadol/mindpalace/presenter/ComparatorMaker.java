package com.anadol.mindpalace.presenter;

import com.anadol.mindpalace.model.SimpleParent;

import java.util.Comparator;

public class ComparatorMaker {
    public static final int TYPE_NAME = 101;
    public static final int TYPE_DATE = 102;
    public static final int ORDER_DESC = 201;
    public static final int ORDER_ASC = 202;

    // TODO Sort для слов в процессе и уже изученых
    public static Comparator<SimpleParent> getComparator(int type, int order) {
        Comparator<SimpleParent> comparator;
        switch (type) {
            default:
            case TYPE_NAME:
                comparator = new StringIntegerComparator(order);
                break;
            case TYPE_DATE:
                comparator = new IdComparator(order);
                break;
        }
        return comparator;
    }

    static class IdComparator implements Comparator<SimpleParent> {
        int order;

        public IdComparator(int order) {
            this.order = order;
        }

        @Override
        public int compare(SimpleParent o1, SimpleParent o2) {
            SimpleParent temp;
            if (order == ORDER_DESC) {
                temp = o1;

                o1 = o2;
                o2 = temp;
            }

            Integer i1 = o1.getTableId();
            Integer i2 = o2.getTableId();
            return i1.compareTo(i2);
        }
    }

    static class StringIntegerComparator implements Comparator<SimpleParent> {
        int order;

        public StringIntegerComparator(int order) {
            this.order = order;
        }

        @Override
        public int compare(SimpleParent o1, SimpleParent o2) {
            SimpleParent temp;
            if (order == ORDER_DESC) {
                temp = o1;

                o1 = o2;
                o2 = temp;
            }
            Integer i1;
            Integer i2;

            boolean b1 = isInt(o1.getName());
            boolean b2 = isInt(o2.getName());
            if (b1 && b2) {
                i1 = Integer.parseInt(o1.getName());
                i2 = Integer.parseInt(o2.getName());
                return i1.compareTo(i2);

            } else if (!(b1 || b2)) {
                return o1.getName().compareTo(o2.getName());
            } else {
                return b1 ? -1 : 1;
            }
        }

        private boolean isInt(String s) {
            // TODO это считается плохим тоном
            try {
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
}
