package com.anadol.mindpalace.view.Fragments;

import android.content.res.Resources;

public interface FragmentAdapter {
    void updateUI();

    void changeSelectableMode(boolean selected);

    Resources myResources();
}
