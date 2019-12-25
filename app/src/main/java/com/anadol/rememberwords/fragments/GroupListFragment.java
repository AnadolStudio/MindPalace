package com.anadol.rememberwords.fragments;


import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.anadol.rememberwords.activities.SettingActivity;
import com.anadol.rememberwords.database.DatabaseHelper;
import com.anadol.rememberwords.database.DbSchema;
import com.anadol.rememberwords.database.SettingsPreference;
import com.anadol.rememberwords.database.MyCursorWrapper;
import com.anadol.rememberwords.myList.DoInBackground;
import com.anadol.rememberwords.myList.Group;
import com.anadol.rememberwords.R;
import com.anadol.rememberwords.activities.CreateGroupActivity;
import com.anadol.rememberwords.activities.GroupDetailActivity;
import com.anadol.rememberwords.myList.LabelEmptyList;
import com.dingmouren.layoutmanagergroup.echelon.EchelonLayoutManager;
import com.dingmouren.layoutmanagergroup.skidright.SkidRightLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.anadol.rememberwords.database.DbSchema.Tables.GROUPS;
import static com.anadol.rememberwords.database.DbSchema.Tables.WORDS;
import static com.anadol.rememberwords.fragments.GroupDetailFragment.IS_CHANGED;
import static com.anadol.rememberwords.fragments.GroupDetailFragment.POSITION;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupListFragment extends MyFragment {
    private static final String TAG = "GroupListFragment";

    public static final String SELECT_MODE = "select_mode";
    public static final String SELECT_LIST = "select_list";
    public static final String CHANGED_ITEM = "changed_item";
    public static final int REQUIRED_CHANGE = 1;
    private static final int REQUEST_SETTINGS = 2;
    private static final int REQUIRED_UNIFY = 3;
    private static final int REQUIRED_CREATE = 4;


    public static final String GROUP_SAVE = "group_save";
    public static final String NEW_GROUP = "new_group";

    private static final String GET_GROUPS = "groups";
    private static final String INVALIDATE_GROUP = "invalidate_groups";
    private static final String REMOVE_GROUP = "remove_group";

    private RecyclerView mRecyclerView;
    private /*static*/ LabelEmptyList mLabelEmptyList;

    private ArrayList<Group> mGroups;

    private GroupAdapter mAdapter;
    private Group[] changes;
    private ProgressBar mProgressBar;

//    private GroupBackground backgroundTask = new GroupBackground();

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
            new GroupBackground().execute(GET_GROUPS);
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
        mRecyclerView = frameLayout.findViewById(R.id.recycler);

        mAdapter = new GroupAdapter(mGroups);

        createRecyclerLayoutManager(SettingsPreference.getLayoutPreference(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mLabelEmptyList = new LabelEmptyList(
                getContext(),
                frameLayout,
                mGroups);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.INVISIBLE);
        Log.i(TAG, "onResume ");
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

    private void createActivitySettings() {
        //TODO: overridePendingTransition
        Intent intent = SettingActivity.newIntent(getActivity());
        // Задумка не удалась, можно заменить на обычный startActivity(Intent intent)
        startActivityForResult(intent, REQUEST_SETTINGS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i(TAG, "Result code: "+ resultCode+
                " RequestCode: "+ requestCode);
        if (resultCode != RESULT_OK){
            return;
        }
        int position;
        switch (requestCode){
            case REQUIRED_CHANGE:
                boolean b = data.getBooleanExtra(IS_CHANGED,false);
                position = data.getIntExtra(POSITION, 0);//для FilterList
                if (b) {

                    Group group = data.getParcelableExtra(CHANGED_ITEM);
                    mAdapter.getList().set(position, group);

                    Log.i(TAG, "Changed item: " + position);
//                    new GroupBackground().execute(INVALIDATE_GROUP);
                    mAdapter.notifyItemChanged(position);
                }

                break;
            case REQUIRED_CREATE:

                Group group = data.getParcelableExtra(CHANGED_ITEM);
                Log.i(TAG, "Create item: " + group.hashCode());
                GroupBackground groupBackground = new GroupBackground();
                groupBackground.setGroup(group);
                groupBackground.execute(INVALIDATE_GROUP);
                break;
        }
    }

    private void createGroup() {
        Intent intent = CreateGroupActivity.newIntent(getContext(), getNames());
        startActivityForResult(intent,REQUIRED_CREATE);
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
        startActivityForResult(intent,REQUIRED_UNIFY);
    }

    public void updateUI(){
        mAdapter.setList(mGroups);
        mAdapter.notifyDataSetChanged();
        mLabelEmptyList.update();
    }

    private void createRecyclerLayoutManager(int i){
        RecyclerView.LayoutManager manager = null;


        Log.i(TAG, "RecyclerLayoutManager item: " + i);
//        SettingsPreference.setLayoutPreference(getActivity(),i);


        switch (i){
            default:
            case 1:
                manager = new LinearLayoutManager(getActivity());
                break;
            case 2:
                Configuration configuration = getResources().getConfiguration();
                Log.i(TAG,"Configuration.screenWidthDp: "+ configuration.screenWidthDp);
                if (configuration.screenWidthDp < 500){
                    manager = new GridLayoutManager(getActivity(),2);
                }else {
                    manager = new GridLayoutManager(getActivity(),3);
                }
                break;
            case 3:
                manager = new EchelonLayoutManager(getActivity());
                break;
            case 4:
                manager = new SkidRightLayoutManager(1.65f,0.85f);
                break;
        }
        mRecyclerView.setLayoutManager(manager);

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
            if (itemView.getAlpha() != 1.0f) {
                itemView.animate()
                        .alpha(1.0f)
                        .setDuration(100)
                        .start();
                itemView.animate().setDuration(0);
            }
        }

        @Override
        public void onClick(View v) {
            int i = getAdapterPosition();

            if (i == RecyclerView.NO_POSITION) return;

            Intent intent = GroupDetailActivity.newIntent(getContext(),mAdapter.getList().get(i),i);

            String nameTranslation = getString(R.string.color_image_translation);
            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                            new Pair<View, String>(mImageView,nameTranslation));

//            ActivityCompat.startActivityForResult(getActivity(),intent,REQUIRED_CHANGE,activityOptions.toBundle());
            startActivityForResult(intent,REQUIRED_CHANGE,activityOptions.toBundle());
        }
    }

    public class GroupAdapter extends RecyclerView.Adapter<GroupHolder> implements Filterable {
        List<Group> mList;
        List<Group> mFilterList;

        public GroupAdapter(List<Group> list) {
            setList(list);
            addAnimation();
        }

        public void setList(List<Group> list) {
            Collections.sort(list);
            mList = list;
            mFilterList = list;
        }

        public void smoothUpdate(List<Group> list){
            Collections.sort(list);
            mList = list;
        }

        public List<Group> getList() {
            return mFilterList;
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

    private void addAnimation() {
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {

                        int parent = mRecyclerView.getBottom();

                        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                            View v = mRecyclerView.getChildAt(i);
//                                v.setAlpha(0.0f);
                            v.setY(parent);
                            v.animate().translationY(1.0f)
                                    .setDuration(400)
                                    .setStartDelay(i * 50)
                                    .start();
                            v.animate().setStartDelay(0);//возвращаю дефолтное значение
                        }

                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        Log.i(TAG, "Remove OnPreDrawListener");
                        return true;
                    }
                });
    }


    public  class GroupBackground extends DoInBackground{
        private ArrayList<Group> invalidate;
        private MyCursorWrapper cursor;
        private SQLiteDatabase db;
        private String c;
        private Group mGroupTemp;
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
                                null,
                                null
                        );

                        if (cursor.getCount() == 0) {
                            return null;
                        }
                        cursor.moveToFirst();

                        if (!mGroups.isEmpty())mGroups.clear();

                        while (!cursor.isAfterLast()) {
                            mGroups.add(cursor.getGroup());
                            cursor.moveToNext();
                        }
                        return true;

                    case INVALIDATE_GROUP:
                        cursor = queryTable(
                                db,
                                GROUPS,
                                null,
                                null,
                                null
                        );

                        invalidate = new ArrayList<>();

                        if (cursor.getCount() == 0) {
                            return null;
                        }
                        cursor.moveToFirst();


                        while (!cursor.isAfterLast()) {
                            invalidate.add(cursor.getGroup());
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
        public void onPost(boolean b) {
            switch (c){
                case REMOVE_GROUP:
                    setSelectMode(false);
                    break;
                case INVALIDATE_GROUP:
                    mGroups.clear();
                    mGroups.addAll(invalidate);
                    mAdapter.setList(mGroups);
                    mAdapter.notifyDataSetChanged();
                    if (mGroupTemp != null){
                        int position = -1;
                        for (Group g: mAdapter.getList()) {
                            // Потому что mGroupTemp.equals(g) == false
                            if (g.getName().equals(mGroupTemp.getName())){
                                position = mAdapter.getList().indexOf(g);
                            }
                        }
                        mRecyclerView.scrollToPosition(position);
                    }
                    break;
                case GET_GROUPS:
                    updateUI();
                    break;
            }

        }

        public void setGroup(Group group) {
            mGroupTemp = group;
        }
    }

}