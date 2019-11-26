package com.anadol.rememberwords.fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.anadol.rememberwords.database.DatabaseHelper;
import com.anadol.rememberwords.database.DbSchema;
import com.anadol.rememberwords.database.MyCursorWrapper;
import com.anadol.rememberwords.myList.DoInBackground;
import com.anadol.rememberwords.myList.Group;
import com.anadol.rememberwords.R;
import com.anadol.rememberwords.activities.CreateGroupActivity;
import com.anadol.rememberwords.activities.GroupDetailActivity;
import com.anadol.rememberwords.myList.LabelEmptyList;
import com.anadol.rememberwords.myList.MyRecyclerAdapter;
import com.anadol.rememberwords.myList.MyViewHolder;
import com.dingmouren.layoutmanagergroup.echelon.EchelonLayoutManager;
import com.dingmouren.layoutmanagergroup.skidright.SkidRightLayoutManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static com.anadol.rememberwords.database.DbSchema.Tables.GROUPS;
import static com.anadol.rememberwords.database.DbSchema.Tables.WORDS;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupListFragment extends MyFragment {
    public static final String SELECT_MODE = "select_mode";
    public static final String SELECT_LIST = "select_list";
    private RecyclerView recyclerView;
    private /*static*/ LabelEmptyList sLabelEmptyList;
    private UUID idSelected;
    private int positionSelected;

    private ArrayList<Group> mGroups;

    public static final String GROUP_SAVE = "group_save";
    public static final String NEW_GROUP = "new_group";
    public static final int DATA_IS_CHANGED = 1;

    private static final String GET_GROUPS = "groups";
    private static final String REMOVE_GROUP = "remove_group";
    private Group[] changes;
    private ProgressBar mProgressBar;

    public String[] getNames() {
        String[] names = new String[mGroups.size()];
        for (int i = 0;i<mGroups.size();i++){
            names[i] = mGroups.get(i).getName();
        }
        return names;
    }



    public String[] getNames(String[] s) {
        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0;i<mGroups.size();i++){
            if (!namesEqual(mGroups.get(i).getName(),s)) {
                arrayList.add(mGroups.get(i).getName());
            }
        }
        String[] names = new String[arrayList.size()];
        names = arrayList.toArray(names);
        return names;
    }

    public static boolean namesEqual(String s, String[]strings){
        for (int i = 0; i<strings.length;i++){
            if (s.equals(strings[i])) return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SELECT_MODE, selectMode);
        outState.putIntegerArrayList(SELECT_LIST, selectedList);
        outState.putParcelableArrayList(GROUP_SAVE, mGroups);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        FrameLayout frameLayout = view.findViewById(R.id.recycler_container);

        if (savedInstanceState != null) {
            mGroups = savedInstanceState.getParcelableArrayList(GROUP_SAVE);
            selectMode = savedInstanceState.getBoolean(SELECT_MODE);
            selectedList = savedInstanceState.getIntegerArrayList(SELECT_LIST);
        }else {
            mGroups = new ArrayList<>();
            selectMode = false;
            selectedList = new ArrayList<>();
        }


        mProgressBar = view.findViewById(R.id.progressBar);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });
        recyclerView = frameLayout.findViewById(R.id.recycler);
        registerForContextMenu(recyclerView);

        createAdapter();

        createRecyclerLayoutManager();
        recyclerView.setAdapter(adapter);

        sLabelEmptyList = new LabelEmptyList(
                getContext(),
                frameLayout,
                adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.INVISIBLE);
        new GroupBackground().execute(GET_GROUPS);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!selectMode) {
            inflater.inflate(R.menu.menu_word_list, menu);
        } else {
            inflater.inflate(R.menu.menu_group_selected_list, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                return true;
            case R.id.menu_remove:
                new GroupBackground().execute(REMOVE_GROUP);
                return true;
            case R.id.menu_unify:
                unifyGroup();
                return true;
            case R.id.menu_cancel:
                cancel();
                return true;
            case R.id.menu_select_all:
//                System.out.println("selectedList.size() == mGroups.size()) " + selectedList.size() +" "+ mGroups.size());
                if (selectedList.size() == mGroups.size()){
                    selectedList.clear();
                }else {
                    selectedList.clear();
                    for (int i = 0; i < mGroups.size(); i++) {
                        selectedList.add(i);
                    }
                }
                updateActionBarTitle();
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void createGroup() {
        Intent intent = CreateGroupActivity.newIntent(getContext(), getNames());
        startActivity(intent);
    }

    private void unifyGroup() {
        ArrayList<Group> groupsUnify = new ArrayList<>();
        for(Integer j :selectedList) {
            int i = j;
            groupsUnify.add(mGroups.get(i));
        }

        String[] s = new String[groupsUnify.size()];
        for (int i = 0; i<groupsUnify.size(); i++){
            s[i] = groupsUnify.get(i).getName();
        }
        Intent intent = CreateGroupActivity.newIntent(getContext(), getNames(s));
        selectedList.clear();
        intent.putExtra(GROUPS,groupsUnify);
        setSelectMode(false);
        startActivity(intent);
    }

    private void groupDetail(int i) {

        mProgressBar.setVisibility(View.VISIBLE);
        Intent intent = GroupDetailActivity.newIntent(getContext(),mGroups, mGroups.get(i).getId());
        startActivity(intent);
    }

    private void createAdapter() {

        adapter = new MyRecyclerAdapter(mGroups, R.layout.item_group_list);
        adapter.setCreatorAdapter(new MyRecyclerAdapter.CreatorAdapter() {// ДЛЯ БОЛЬШЕЙ ГИБКОСТИ ТУТ Я РЕАЛИЗУЮ СЛУШАТЕЛЯ И МЕТОДЫ АДАПТЕРА
            @Override
            public void createHolderItems(MyViewHolder holder) {
                TextView textView = holder.itemView.findViewById(R.id.text_group);
                ImageView imageView = holder.itemView.findViewById(R.id.image_group);
                CheckBox checkBox = holder.itemView.findViewById(R.id.checkBox);
                checkBox.setEnabled(false);
                holder.setViews(new View[]{textView, imageView, checkBox});
            }

            @Override
            public void bindHolderItems(MyViewHolder holder) {
                View[] views = holder.getViews();
                int position = holder.getAdapterPosition();
                Group group = (Group) adapter.getList().get(position);

                TextView textView = (TextView) views[0];
                textView.setText(group.getName().toUpperCase());
                ImageView imageView = (ImageView) views[1];
                imageView.setImageDrawable(group.getGroupDrawable());
                CheckBox checkBox = (CheckBox) views[2];
                if (selectMode){
                    checkBox.setVisibility(View.VISIBLE);
                    if (selectedList.indexOf(position) != -1){
                        System.out.println("position " + position);
                        checkBox.setChecked(true);
                        holder.itemView.setBackgroundColor(getResources().getColor(R.color.colorSelected));
                    }else {
                        checkBox.setChecked(false);
                        holder.itemView.setBackgroundColor(getResources().getColor(R.color.colorDefaultBackground));
                    }
                }else {
                    checkBox.setChecked(false);
                    checkBox.setVisibility(View.GONE);
                    holder.itemView.setBackgroundColor(getResources().getColor(R.color.colorDefaultBackground));
                }

            }

            @Override
            public void myOnItemDismiss(int position, int flag) {

            }
        });
        adapter.setListener(new MyRecyclerAdapter.Listeners() {
            @Override
            public void onClick(View view, int position) {
                if (!selectMode) {
                    groupDetail(position);
                    positionSelected = position;

                } else {
                    View[] views = ((MyViewHolder)recyclerView.getChildViewHolder(view)).getViews();
                    CheckBox checkBox = (CheckBox) views[2];
                    Integer i = Integer.valueOf(position);
                    if (checkBox.isChecked()){
                        selectedList.remove(i);
                        view.setBackgroundColor(getResources().getColor(R.color.colorDefaultBackground));
                    }else {
                        selectedList.add(i);
                        view.setBackgroundColor(getResources().getColor(R.color.colorSelected));
                    }
                    checkBox.setChecked(!checkBox.isChecked());
                    updateActionBarTitle();
                }
            }

            @Override
            public boolean onLongClick(View view, int position) {
                /*idSelected = mGroups.get(position).getId();
                positionSelected = position;*/
                selectedList.add(position);
                setSelectMode(true);
                view.setBackgroundColor(getResources().getColor(R.color.colorSelected));

                View[] views = ((MyViewHolder)recyclerView.getChildViewHolder(view)).getViews();
                CheckBox checkBox = (CheckBox) views[2];
                checkBox.setChecked(true);
                return true;
            }
        });
        adapter.setSortItems(new MyRecyclerAdapter.SortItems() {
            @Override
            public void sortList() {
                Collections.sort(adapter.getList());
            }
        });

    }




    public void updateUI(){
        adapter.setList(mGroups);
        adapter.notifyDataSetChanged();
    }

    private void createRecyclerLayoutManager(){
        RecyclerView.LayoutManager manager = null;

        switch (getResources().getConfiguration().orientation){
            case Configuration.ORIENTATION_PORTRAIT:
                manager = new EchelonLayoutManager(getContext());
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                manager = new SkidRightLayoutManager(1.5f,0.85f);
                break;
        }
        recyclerView.setLayoutManager(manager);
    }

    public  class GroupBackground extends DoInBackground{
        MyCursorWrapper cursor;
        SQLiteDatabase db;
        String c;
        @Override
        public Boolean doIn(String command) {
            c = command;
            try {
                db = new DatabaseHelper(getContext()).getWritableDatabase();
//                System.out.println(db.getVersion());
                switch (command) {
                    case GET_GROUPS:
                        cursor = queryTable(
                                db,
                                GROUPS,
                                null,
                                null
                        );

                        if (cursor.getCount() == 0) {
                            return null;
                        }
                        cursor.moveToFirst();
                        mGroups = new ArrayList<>();
                        while (!cursor.isAfterLast()) {
                            mGroups.add(cursor.getGroup());
                            cursor.moveToNext();
                        }
                        return true;

                    case REMOVE_GROUP:
                        ArrayList<Group> groupsRemove = new ArrayList<>();
                            for(Integer j :selectedList) {
                            int i = j;
                            groupsRemove.add(mGroups.get(i));
                            db.delete(GROUPS,
                                    DbSchema.Tables.Cols.UUID + " = ?",
                                    new String[]{mGroups.get(i).getId().toString()});
                            db.delete(WORDS,
                                    DbSchema.Tables.Cols.NAME_GROUP + " = ?",
                                    new String[]{mGroups.get(i).getName()});
                        }
                        for (Group g :groupsRemove){
                            mGroups.remove(g);
                        }
                        selectedList.clear();
                        return true;


                }
                cursor.close();
                db.close();
            }catch (Exception ex){
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        public void onPost() {
            updateUI();
            sLabelEmptyList.update();
            switch (c){
                case REMOVE_GROUP:
                    setSelectMode(false);
                    break;
            }
        }
    }

}