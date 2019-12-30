package com.anadol.rememberwords.fragments;


import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
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
public class GroupListFragment extends MyFragment implements IOnBackPressed {
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
    private static final String SELECT_ALL = "select_all";
    private static final String SELECT_COUNT = "select_count";
    private static final String QUERY = "query";

    private RecyclerView mRecyclerView;
    private SearchView searchView;
    private FloatingActionButton fab;
    private /*static*/ LabelEmptyList mLabelEmptyList;

    private ArrayList<Group> mGroups;

    private GroupAdapter mAdapter;
    private ProgressBar mProgressBar;
    private boolean selectAll;
    private int selectCount;
    private String query;
    private ArrayList<String> selectArray;
    private boolean selectable = false;


    //    private GroupBackground backgroundTask = new GroupBackground();

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SELECT_MODE, mAdapter.isSelectable);
        outState.putParcelableArrayList(GROUP_SAVE, mGroups);

        selectArray = new ArrayList<>();

        for (int i = 0; i < mAdapter.mSelectionsArray.size();i++) {
            if (mAdapter.mSelectionsArray.valueAt(i)) {
                selectArray.add(mAdapter.mSelectionsArray.keyAt(i));
            }
        }
        Log.i(TAG, "onSaveInstanceState: " + selectArray.size());
        outState.putStringArrayList(SELECT_LIST, selectArray);
        outState.putBoolean(SELECT_ALL,selectAll);
        outState.putInt(SELECT_COUNT,selectCount);
        outState.putString(QUERY,query);
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
        FrameLayout frameLayout = view.findViewById(R.id.group_list_container);

        if (savedInstanceState != null) {
            mGroups = savedInstanceState.getParcelableArrayList(GROUP_SAVE);
            selectAll = savedInstanceState.getBoolean(SELECT_ALL);
            selectCount = savedInstanceState.getInt(SELECT_COUNT);
            query = savedInstanceState.getString(QUERY);
            selectArray = savedInstanceState.getStringArrayList(SELECT_LIST);
            selectable = savedInstanceState.getBoolean(SELECT_MODE);
        }else {
            mGroups = new ArrayList<>();
            selectArray = new ArrayList<>();
            new GroupBackground().execute(GET_GROUPS);
            selectAll = false;
            selectCount = 0;
            query = "";
        }


        mProgressBar = view.findViewById(R.id.progressBar);

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });
        mRecyclerView = frameLayout.findViewById(R.id.recycler);

        mAdapter = new GroupAdapter(mGroups);

        mAdapter.isSelectable = selectable;

        createRecyclerLayoutManager(SettingsPreference.getLayoutPreference(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.setLongClickable(true);

        mLabelEmptyList = new LabelEmptyList(
                getContext(),
                frameLayout,
                mGroups);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        mProgressBar.setVisibility(View.INVISIBLE);
//        Log.i(TAG, "onResume ");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mode == MODE_NORMAL || mode == MODE_SEARCH) {
            inflater.inflate(R.menu.fragment_group_list,menu);
            MenuItem item = menu.findItem(R.id.menu_search);

            searchView = (SearchView) item.getActionView();
            searchView.setQueryHint(getResources().getString(R.string.search));
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mode = MODE_SEARCH;
                }
            });
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    mode = MODE_NORMAL;
                    return false;
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    query = s;
                    mAdapter.getFilter().filter(s);
                    return true;
                }
            });
            if (!query.equals("")){
                searchView.setIconified(false);
                searchView.setQuery(query,true);
            }
        } else if (mode == MODE_SELECT){
            inflater.inflate(R.menu.menu_group_selected_list,menu);
            MenuItem item = menu.findItem(R.id.menu_select_all);
            if (selectAll){
                item.setIcon(R.drawable.ic_menu_select_all_on);
            }else {
                item.setIcon(R.drawable.ic_menu_select_all_off);
            }
            updateActionBarTitle(true);
        }
    }

    @Override
    public boolean onBackPressed() {

        switch (mode){
            case MODE_SEARCH:
                mode = MODE_NORMAL;
                getActivity().invalidateOptionsMenu();
                searchView.onActionViewCollapsed();
                return true;
            case MODE_SELECT:
                modeSelectedTurnOff();
                mAdapter.mSelectionsArray.clear();
                for (int i = 0; i < mAdapter.getList().size(); i++) {
                    Group group = mAdapter.getList().get(i);
                    mAdapter.mSelectionsArray.put(group.getIdString(),false);
                }
                selectCount = 0;
                return true;
            default:
                return false;
        }
    }

    private void modeSelectedTurnOff() {
        mAdapter.setSelectable(false);
        mAdapter.notifyDataSetChanged();
        updateActionBarTitle(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                createActivitySettings();
                return true;
            case R.id.menu_remove:
                modeSelectedTurnOff();
                new GroupBackground().execute(REMOVE_GROUP);
                return true;
            case R.id.menu_merge:
                mergeGroup();
                return true;
            case R.id.menu_cancel:
//                mAdapter.setSelectable(false);
//                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_select_all:
//                System.out.println("selectedList.size() == mGroups.size()) " + selectedList.size() +" "+ mGroups.size());
                selectAll = !selectAll;
                mAdapter.mSelectionsArray.clear();
                if (selectAll){
                    for (int i = 0; i < mAdapter.getList().size(); i++) {
                        Group group = mAdapter.getList().get(i);
                        mAdapter.mSelectionsArray.put(group.getIdString(),true);
                    }
                    selectCount = mAdapter.getList().size();
                }else {
                    for (int i = 0; i < mAdapter.getList().size(); i++) {
                        Group group = mAdapter.getList().get(i);
                        mAdapter.mSelectionsArray.put(group.getIdString(),false);
                    }
                    selectCount = 0;
                }
                getActivity().invalidateOptionsMenu();
                updateActionBarTitle(true);
                mAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void updateActionBarTitle(boolean selectMode){
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        if (!selectMode) {
            activity.getSupportActionBar().setTitle(getString(R.string.app_name));
        }else {
            activity.getSupportActionBar().setTitle(String.valueOf(selectCount));
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

    private void mergeGroup() {
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

    public class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView mTextView;
        private ImageView mImageView;
        private boolean isSelectableMode = false; //default
        private boolean isSelectableItem = false; //default
        private GroupAdapter myParentAdapter;

        public GroupHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text_group);
            mImageView = itemView.findViewById(R.id.image_group);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void onBind(String name, Drawable drawable, GroupAdapter parentAdapter){
            mTextView.setText(name);
            mImageView.setImageDrawable(drawable);
            myParentAdapter = parentAdapter;
            isSelectableMode = myParentAdapter.isSelectable;
            int position = getAdapterPosition();
            isSelectableItem = myParentAdapter.isItemSelectable(mAdapter.getList().get(position).getIdString());

            if (isSelectableMode) {

                Resources resources = getResources();
                if (isSelectableItem) {
                    // Here will be some Drawable
                    mImageView.setImageDrawable(new ColorDrawable(resources.getColor(R.color.colorAccent)));
                }else {
                    mImageView.setImageDrawable(new ColorDrawable(resources.getColor(R.color.colorSecondary)));
                }
            }
        }

        @Override
        public void onClick(View v) {
            int i = getAdapterPosition();
            if (i == RecyclerView.NO_POSITION) return;

            if (!isSelectableMode) {
                Intent intent = GroupDetailActivity.newIntent(getContext(), mAdapter.getList().get(i),i);

                String nameTranslation = getString(R.string.color_image_translation);
                ActivityOptionsCompat activityOptions =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                new Pair<View, String>(mImageView,nameTranslation));

//            ActivityCompat.startActivityForResult(getActivity(),intent,REQUIRED_CHANGE,activityOptions.toBundle());
                startActivityForResult(intent,REQUIRED_CHANGE,activityOptions.toBundle());
            } else {
                isSelectableItem = !isSelectableItem;
                myParentAdapter.setItemChecked((mAdapter.getList().get(i).getIdString()),isSelectableItem);
                Resources resources = getResources();
                if (isSelectableItem) {
                    // Here will be some Drawable
                    mImageView.setImageDrawable(new ColorDrawable(resources.getColor(R.color.colorAccent)));
                    selectCount++;
                }else {
                    mImageView.setImageDrawable(new ColorDrawable(resources.getColor(R.color.colorSecondary)));
                    selectCount--;
                }
                updateActionBarTitle(true);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (!myParentAdapter.isSelectable) {
                myParentAdapter.setSelectable(true);
                int i = getAdapterPosition();
                myParentAdapter.setItemChecked((mAdapter.getList().get(i).getIdString()),true);
                mAdapter.notifyDataSetChanged();
                selectCount++;
                updateActionBarTitle(true);
            }
            return true;
        }
    }
    public class GroupAdapter extends RecyclerView.Adapter<GroupHolder> implements Filterable {
        private List<Group> mList;
        private List<Group> mFilterList;
        private ArrayMap<String,Boolean> mSelectionsArray = new ArrayMap<>();
        private boolean isSelectable = false;

        public GroupAdapter(List<Group> list) {
            setList(list);
            addTranslationAnim();
        }

        public void setList(List<Group> list) {
            Collections.sort(list);
            mList = list;
            mFilterList = list;
            setSelectionsArray(selectArray);

            for (int i = 0; i < mList.size(); i++) {
                Group g = mList.get(i);
                Log.i(TAG, "mList: " + g.getIdString() + " mSelectionsArray: " +mSelectionsArray.keyAt(i));
            }
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
            groupHolder.onBind(group.getName(),group.getGroupDrawable(),this);
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

        private void setItemChecked(String name, boolean isChecked){
            mSelectionsArray.put(name,isChecked);
            int j = 0;
            for (int i = 0; i < mSelectionsArray.size(); i++) {
                if (mSelectionsArray.valueAt(i)) j++;
            }
            selectAll = (j == mSelectionsArray.size());
            getActivity().invalidateOptionsMenu();
        }

        private boolean isItemSelectable(String id){
            return mSelectionsArray.get(id) == null ? false : mSelectionsArray.get(id);
        }


        public void setSelectable(boolean selectable) {
            isSelectable = selectable;
            if (isSelectable){
                mode = MODE_SELECT;
                fab.hide();
            }else {
                mode = MODE_NORMAL;
                fab.show();
            }

            getActivity().invalidateOptionsMenu();
//            addAlphaAnim();
        }

        public boolean isSelectable() {
            return isSelectable;
        }

        public void setSelectionsArray(ArrayList<String> selectionsArray) {
            if (!selectionsArray.isEmpty()) {
                for (int i = 0; i < selectionsArray.size(); i++) {
                    mSelectionsArray.put(selectionsArray.get(i), true);
                }
                Log.i(TAG, "StringArray.size(): "+ selectionsArray.size());
            }
            for (int i = 0; i < mList.size(); i++) {
                Group group = mList.get(i);
                if (mSelectionsArray.get(group.getIdString()) == null) {
                    mSelectionsArray.put(group.getIdString(), false);
                }

            }
            Log.i(TAG, "mSelectionsArray.size(): "+ mSelectionsArray.size());

        }
    }

    private void addTranslationAnim() {
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
                        return true;
                    }
                });
    }

    private void addAlphaAnim() {
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {

                        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
                            View v = mRecyclerView.getChildAt(i);
                            v.setAlpha(0.0f);
                            v.animate().alpha(1.0f)
                                    .setDuration(200)
                                    .start();
                        }

                        mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
    }

    public  class GroupBackground extends DoInBackground{
        private ArrayList<Group> invalidate;
        ArrayList<Group> groupsRemove;
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
                        groupsRemove = new ArrayList<>();
                        for (int i = 0; i < mAdapter.mSelectionsArray.size(); i++) {
                            if (mAdapter.mSelectionsArray.valueAt(i)) {
                                for (Group g : mGroups) {
                                    if (g.getIdString().equals(mAdapter.mSelectionsArray.keyAt(i))){
                                        groupsRemove.add(g);
                                    }
                                }
                            }
                        }
                        for(Group g : groupsRemove) {
                            int i = mGroups.indexOf(g);
                            db.delete(GROUPS,
                                    DbSchema.Tables.Cols.UUID + " = ?",
                                    new String[]{mGroups.get(i).getIdString()});
                            db.delete(WORDS,
                                    DbSchema.Tables.Cols.NAME_GROUP + " = ?",
                                    new String[]{mGroups.get(i).getName()});
                        }

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
                    int j;
                    for (Group g : groupsRemove){
                        j = mAdapter.getList().indexOf(g);
                        mAdapter.notifyItemRemoved(j);
                        mGroups.remove(g);
                        Log.i(TAG, "Group removed: " + g.getName());
                    }
                    mAdapter.mSelectionsArray.clear();
                    for (int i = 0; i < mAdapter.getList().size(); i++) {
                        Group group = mAdapter.getList().get(i);
                        mAdapter.mSelectionsArray.put(group.getIdString(),false);
                    }
                    selectCount = 0;
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
                                Log.i(TAG,"" + g.hashCode());
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