package com.anadol.rememberwords.presenter;

import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.fragments.ItemTouchHelperAdapter;
import com.anadol.rememberwords.model.SimpleParent;

import java.util.ArrayList;

public class MyListAdapter<T extends SimpleParent> extends RecyclerView.Adapter<MySimpleHolder> implements Filterable, ItemTouchHelperAdapter {
    public static final String TAG = "MyListAdapter";
    public static final int GROUP_HOLDER = 1;
    public static final int WORD_HOLDER = 2;
    private Fragment mFragment;
    private ArrayList<T> mList;
    private ArrayList<T> mFilterList;
    private ArrayMap<String, Boolean> mSelectionsArray;
    private int typeHolder;
    private boolean isSelectableMode;
    private int countSelectedGroups;

    public MyListAdapter(Fragment fragment, ArrayList<T> arrayList, int typeHolder, @Nullable ArrayList<String> selectedItems, boolean isSelectableMode) {
//        Collections.sort(arrayList);
        mList = arrayList;
        mFilterList = mList;
        mFragment = fragment;
        setSelectionsArray(selectedItems);
        this.isSelectableMode = isSelectableMode;
        this.typeHolder = typeHolder;
    }

    @Override
    public void onItemDismiss(RecyclerView.ViewHolder viewHolder, int flag) {
        MySimpleHolder holder = (GroupListHolder) viewHolder;
        holder.itemTouch(flag);
/*
        switch (flag) {
            case ItemTouchHelper.START://Выделить

                break;
            case ItemTouchHelper.END://DialogTranslate
                if (!isSelectable) {
//                        createDialogMultiTranslate(position);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.close_select_mode), Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                }
                break;
        }
*/

    }

    public void setList(ArrayList<T> list) {
//        Collections.sort(list);
        mList = list;
        mFilterList = mList;
    }

    private void setSelectionsArray(@Nullable ArrayList<String> selectedItems) {
        if (mSelectionsArray == null) {
            mSelectionsArray = new ArrayMap<>();
        } else {
            mSelectionsArray.clear();
        }

        countSelectedGroups = 0;
        for (SimpleParent item : mList) {
            // Если имя/id содержиться в selectedItems, то значит эта группа была выделенна,
            // если null то это первое создание
            if (selectedItems != null
                    && selectedItems.contains(item.getUUIDString())) {
                mSelectionsArray.put(item.getUUIDString(), true);
                countSelectedGroups++;
            } else mSelectionsArray.put(item.getUUIDString(), false);
            Log.i(TAG, "mSelectionsArray.size(): " + mSelectionsArray.size() + " mList.size(): " + mList.size());
        }
    }

    public ArrayList<String> getSelectedStringArray() {
        ArrayList<String> strings = new ArrayList<>();

        for (int i = 0; i < mSelectionsArray.size(); i++) {
            if (mSelectionsArray.valueAt(i)) {
                strings.add(mSelectionsArray.keyAt(i));
            }
        }
        return strings;
    }

    @NonNull
    @Override
    public MySimpleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_list, parent, false);
        MySimpleHolder holder;
        switch (typeHolder) {
            case GROUP_HOLDER:
                holder = new GroupListHolder(view, this);
                break;
            case WORD_HOLDER:
                holder = new WordListHolder(view, this);
                break;
            default:
                throw new NullPointerException("Holder is null");
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MySimpleHolder holder, int position) {
        SimpleParent item = mFilterList.get(position);
        holder.onBind(item, mSelectionsArray.get(item.getUUIDString()));
    }

    public int getCountSelectedItems() {
        return countSelectedGroups;
    }

    public boolean isAllItemSelected() {
        // getItemCount возращает size() у mFilterList, но в если используется MODE_SELECT, то
        // MODE_SEARCH отключается, а значит mFilterList = mList
        return getItemCount() == getCountSelectedItems();
    }

    public int setAllItemSelected(boolean select) {
        int size = mSelectionsArray.size();

        if (select) {
            countSelectedGroups = 0;
        } else {
            countSelectedGroups = size;
        }

        for (int i = 0; i < size; i++) {
            setValueAt(i, select);
        }
        Log.i(TAG, "mSelectionsArray.size(): " + mSelectionsArray.size() + " mList.size(): " + mList.size());
        return countSelectedGroups;
    }

    public void updateItem(int position, T item) {
        mList.set(position, item);
    }

    public int getIndexGroup(int tableId) {
        int position = -1;
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getTableId() == tableId) {
                position = i;
            }
        }
        Log.i(TAG, "getIndexGroup :" + position);
        return position;
    }

    public ArrayList<T> getSelectedItem() {
        ArrayList<T> selectedItems = new ArrayList<>();
        for (T item : mList) {
            String id = item.getUUIDString();
            if (mSelectionsArray.get(id)) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    public boolean remove(SimpleParent item) {
        int index = mList.indexOf(item);
        if (index != -1) {
            notifyItemRemoved(index);
        }
        Log.i(TAG, "SimpleParent " + item.toString() + "was removed");
        return mList.remove(item);
    }

    public void remove(ArrayList<? extends SimpleParent> groups) {
        for (SimpleParent g : groups) {
            remove(g);
        }
    }

    public boolean add(T item) {
        mSelectionsArray.put(item.getUUIDString(), false);
        return mList.add(item);
    }

    public void add(int position, T item) {
        mSelectionsArray.put(item.getUUIDString(), false);
        mList.add(position, item);
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void putSelectedItem(String key, boolean value) {
        mSelectionsArray.put(key, value);
        counter(value);
    }

    private void setValueAt(int index, boolean value) {
        mSelectionsArray.setValueAt(index, value);
        counter(value);
    }

    private void counter(boolean value) {
        if (value) {
            countSelectedGroups++;
        } else {
            countSelectedGroups--;
        }
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }

    @Override
    public Filter getFilter() {
        return new MyFilter() {
        };
    }

    public boolean isSelectableMode() {
        return isSelectableMode;
    }

    public void setSelectableMode(boolean selectableMode) {
        isSelectableMode = selectableMode;
        if (!selectableMode) {
            setSelectionsArray(null);
            notifyDataSetChanged();
        }
    }


    private class MyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String query = constraint.toString().toLowerCase();

            if (query.isEmpty()) {
                mFilterList = mList;
            } else {
                ArrayList<T> filteredList = new ArrayList<>();
                String name;
                for (T item : mList) {
                    name = item.toString().toLowerCase();

                    if (name.contains(query)) {
                        filteredList.add(item);
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
    }
}
