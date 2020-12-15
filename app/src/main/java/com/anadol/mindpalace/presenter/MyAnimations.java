package com.anadol.mindpalace.presenter;

import android.view.View;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.RecyclerView;

public class MyAnimations {

    public static void addTranslationAnim(RecyclerView mRecyclerView) {

        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {

                        int parent = mRecyclerView.getBottom();

                        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                            View v = mRecyclerView.getChildAt(i);
                            v.setY(parent);
                            v.animate().translationY(1.0f)
                                    .setDuration(400)
                                    .setStartDelay(i * 50)
                                    .start();
                            v.animate().setStartDelay(0);//возвращаю дефолтное значение
                        }

                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
    }

    public static void addAlphaAnim(RecyclerView mRecyclerView) {

        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {

                        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                            View v = mRecyclerView.getChildAt(i);
                            v.setAlpha(0.0f);
                            v.animate().alpha(1.0f)
                                    .setDuration(300)
                                    .start();
                        }

                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
    }


}
