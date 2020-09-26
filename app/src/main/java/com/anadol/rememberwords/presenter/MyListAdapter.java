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
import androidx.recyclerview.widget.RecyclerView;

import com.anadol.rememberwords.R;
import com.anadol.rememberwords.model.SimpleParent;
import com.anadol.rememberwords.view.Fragments.MyFragment;

import java.util.ArrayList;

public class MyListAdapter<T extends SimpleParent> extends RecyclerView.Adapter<MySimpleHolder> implements Filterable, ItemTouchHelperAdapter {
    public static final String TAG = "MyListAdapter";
    public static final int GROUP_HOLDER = 1;
    public static final int WORD_HOLDER = 2;
    private MyFragment mFragment;
    private ArrayList<T> mList;
    private ArrayList<T> mFilterList;
    private ArrayMap<String, Boolean> mSelectionsArray;
    private int typeHolder;
    private boolean isSelectableMode;
    private int countSelectedGroups;
//    private int temp;

    public MyListAdapter(MyFragment fragment, ArrayList<T> arrayList, int typeHolder, @Nullable ArrayList<String> selectedItems, boolean isSelectableMode) {
//        Collections.sort(arrayList);
        mList = arrayList;
        mFilterList = mList;
        mFragment = fragment;
        setSelectionsArray(selectedItems);
        this.isSelectableMode = isSelectableMode;
        this.typeHolder = typeHolder;
//        temp = 0;
    }

    @Override
    public void onItemDismiss(RecyclerView.ViewHolder viewHolder, int flag) {
        MySimpleHolder holder = (MySimpleHolder) viewHolder;
        holder.itemTouch(flag);
        notifyItemChanged(holder.getAdapterPosition());
    }

    public void setList(ArrayList<T> list) {
        mList = list;
        mFilterList = mList;
    }

    private void setSelectionsArray(@Nullable ArrayList<String> selectedItems) {
        if (mSelectionsArray == null) {
            mSelectionsArray = new ArrayMap<>();
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
//            Log.i(TAG, "mSelectionsArray.size(): " + mSelectionsArray.size() + " mList.size(): " + mList.size());
        }
    }

    private void setSelectedValueToAllItems(boolean value) {
        for (int i = 0; i < mSelectionsArray.size(); i++) {
            mSelectionsArray.setValueAt(i, value);
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
        View view;
        MySimpleHolder holder;
//        Log.i(TAG, "onCreateViewHolder: was created " + temp++);
        switch (typeHolder) {
            case GROUP_HOLDER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_list, parent, false);
                holder = new GroupListHolder(view, this);
                break;
            case WORD_HOLDER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_words_list, parent, false);
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
//        Log.i(TAG, "onBindViewHolder. mSelectionsArray.size(): " + mSelectionsArray.size() + " mList.size(): " + mList.size());
//        boolean b = mSelectionsArray.containsKey(item.getUUIDString());
//        if (!b) mSelectionsArray.put(item.getUUIDString(), false);

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
        if (mFilterList != mList) {
            Log.i(TAG, "getAdapterPosition : mFilterList != mList");
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getTableId() == item.getTableId()) {
                    mList.set(position, item);
                }
            }
        } else {
            mList.set(position, item);
        }
        notifyItemChanged(position);
    }

    public int getAdapterPosition(int tableId) {
        int position = -1;

        for (int i = 0; i < mFilterList.size(); i++) {
            if (mFilterList.get(i).getTableId() == tableId) {
                position = i;
            }
        }
        Log.i(TAG, "getAdapterPosition :" + position);
        return position;
    }

    public ArrayList<T> getSelectedItems() {
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

    public MyFragment getFragment() {
        return mFragment;
    }

    public void putSelectedItem(String key, boolean value) {
        mSelectionsArray.put(key, value);
        counter(value);
        mFragment.updateUI();
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
//            setSelectedValueToAllItems(selectableMode);
            setSelectionsArray(null);
            notifyDataSetChanged();
        } else {
            mFragment.changeSelectableMode(true);
        }
    }

    public void setSelectableMode(boolean selectableMode, int position) {
        setSelectableMode(selectableMode);
        if (selectableMode) {
            int count = getItemCount();
            // TODO: решение проблемы резкой анимации
            notifyItemRangeChanged(0, position);
            notifyItemRangeChanged(position, count);
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
