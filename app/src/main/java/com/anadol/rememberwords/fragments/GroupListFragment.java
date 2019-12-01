package com.anadol.rememberwords.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.anadol.rememberwords.activities.SettingActivity;
import com.anadol.rememberwords.database.DatabaseHelper;
import com.anadol.rememberwords.database.DbSchema;
import com.anadol.rememberwords.database.LayoutPreference;
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
import java.util.List;
import java.util.UUID;

import static com.anadol.rememberwords.database.DbSchema.Tables.GROUPS;
import static com.anadol.rememberwords.database.DbSchema.Tables.WORDS;



/**
 * A simple {@link Fragment} subclass.
 */
public class GroupListFragment extends MyFragment {
    public static final String SELECT_MODE = "select_mode";
    public static final String SELECT_LIST = "select_list";
    private static final int REQUEST_SETTINGS = 0;
    private RecyclerView recyclerView;
    private /*static*/ LabelEmptyList sLabelEmptyList;

    private ArrayList<Group> mGroups;
    private GroupAdapter mAdapter;

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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });
        recyclerView = frameLayout.findViewById(R.id.recycler);

        mAdapter = new GroupAdapter(mGroups);

        createRecyclerLayoutManager(LayoutPreference.getLayoutPreference(getActivity()));
        recyclerView.setAdapter(mAdapter);

        sLabelEmptyList = new LabelEmptyList(
                getContext(),
                frameLayout,
                mAdapter);

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
        inflater.inflate(R.menu.fragment_group_list,menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                createActivitySettings();
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
                mAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createActivitySettings() {
        Intent intent = SettingActivity.newIntent(getActivity());
        startActivityForResult(intent, REQUEST_SETTINGS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println(requestCode+" ! "+ requestCode);
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        switch (requestCode){
            case REQUEST_SETTINGS:
                createRecyclerLayoutManager(LayoutPreference.getLayoutPreference(getActivity()));
                break;
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

    public void updateUI(){
        mAdapter.setList(mGroups);
        mAdapter.notifyDataSetChanged();
    }

    private void createRecyclerLayoutManager(int i){
        RecyclerView.LayoutManager manager = null;

        if (recyclerView.getLayoutManager() != null && i == LayoutPreference.getLayoutPreference(getActivity())){
            return;
        }

        LayoutPreference.setLayoutPreference(getActivity(),i);


        switch (i){
            case 1:
                manager = new LinearLayoutManager(getActivity());
                break;
            case 2:
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT){
                    manager = new GridLayoutManager(getActivity(),2);
                }else {
                    manager = new GridLayoutManager(getActivity(),3);
                }
                break;
            case 3:
                manager = new EchelonLayoutManager(getActivity());
                break;
            case 4:
                manager = new SkidRightLayoutManager(1.5f,0.85f);
                break;
        }
        recyclerView.setLayoutManager(manager);

    }

    public class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextView;
        private ImageView mImageView;

        public GroupHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text_group);
            mImageView = itemView.findViewById(R.id.image_group);
            itemView.setOnClickListener(this);
        }

        public void onBind(String name, Drawable drawable){
            mTextView.setText(name);
            mImageView.setImageDrawable(drawable);
        }

        @Override
        public void onClick(View v) {
            groupDetail(getAdapterPosition());
            System.out.println("onClick");
        }
    }

    public class GroupAdapter extends RecyclerView.Adapter<GroupHolder> implements Filterable {
        List<Group> mList;
        List<Group> mFilterList;

        public GroupAdapter(List<Group> list) {
            setList(list);
        }

        public void setList(List<Group> list) {
            Collections.sort(list);
            mList = list;
            mFilterList = list;
        }

        @NonNull
        @Override
        public GroupHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_group_list,viewGroup, false);
            return new GroupHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GroupHolder groupHolder, int i) {
            Group group = mFilterList.get(i);
            groupHolder.onBind(group.getName(),group.getGroupDrawable());
        }

        @Override
        public int getItemCount() {
            return mFilterList.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String query = constraint.toString().toLowerCase();
                    if (query.isEmpty()){
                        mFilterList = mList;
                    }else {
                        ArrayList<Group> filteredList = new ArrayList<>();
                        String name;
                        for (Group group : mList) {
                            name = group.getName().toLowerCase();
                            if (name.contains(query)){
                                filteredList.add(group);
                            }
                        }
                        mFilterList = filteredList;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mFilterList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    notifyDataSetChanged();
                }
            };
        }
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