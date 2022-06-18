package com.anadol.mindpalace.view.screens.lessons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anadol.mindpalace.R;
import com.anadol.mindpalace.model.GroupExample;
import com.anadol.mindpalace.view.Fragments.SimpleFragment;

public class LessonFragment extends SimpleFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    protected void createGroup(View view, GroupExample groupExample) {
        ImageView imageView = view.findViewById(R.id.image_group);
        imageView.setImageDrawable(groupExample.getDrawable());

        TextView textView = view.findViewById(R.id.text_group);
        textView.setText(groupExample.getName());
    }

}
