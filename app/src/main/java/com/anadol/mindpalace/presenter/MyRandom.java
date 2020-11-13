package com.anadol.mindpalace.presenter;

import android.util.Log;

import java.util.ArrayList;

public class MyRandom {
    public static final String TAG = MyRandom.class.getName();

    public static ArrayList<Integer> getRandomInts(int count, int bound) {
        return getRandomInts(count, bound, null);
    }

    public static ArrayList<Integer> getRandomInts(int count, int bound, int[] ints) {

        ArrayList<Integer> randomArrayList = new ArrayList<>();
        ArrayList<Integer> exclusion = new ArrayList<>();
        if (ints != null) {
            for (int anInt : ints) {
                exclusion.add(anInt);
            }
        }

        int j;
        int countTries;
        for (int i = 0; i < count; i++) {
            countTries = 0;
            do {
                countTries++;
                j = nextInt(bound);
                if (countTries > 1000) {
                    Log.i(TAG, "getRandomInts: countTries" + countTries);
                    break;
                }
            } while (randomArrayList.contains(j) || exclusion.contains(j));
            randomArrayList.add(j);
        }
        return randomArrayList;
    }

    // Не включая max
    public static int nextInt(int max) {
        return (int) (Math.random() * max);
    }

    // Не включая max
    public static int nextInt(int min, int max) {
        max -= min;
        return (int) (Math.random() * max) + min;
    }

    public static <T> ArrayList<T> getRandomArrayList(ArrayList<? extends T> arrayList, int countItems) {

        ArrayList<T> randomArrayList = new ArrayList<>();

        ArrayList<Integer> integers = getRandomInts(countItems, arrayList.size());
        for (int i = 0; i < integers.size(); i++) {
            randomArrayList.add(arrayList.get(integers.get(i)));
        }
        return randomArrayList;
    }
}
