package com.anadol.mindpalace.presenter;

import android.content.Context;
import android.content.res.Resources;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.anadol.mindpalace.model.SimpleParent;
import com.anadol.rememberwords.R;
import com.anadol.mindpalace.view.Fragments.FragmentAdapter;

import java.util.ArrayList;

public class MyListAdapter<T extends SimpleParent> extends RecyclerView.Adapter<MySimpleHolder> implements Filterable, ItemTouchHelperAdapter {
    public static final String TAG = MyListAdapter.class.getName();
    public static final int GROUP_HOLDER = R.layout.item_group_list;
    public static final int WORD_HOLDER = R.layout.item_words_list;

    private FragmentAdapter mFragment;
    private ArrayList<T> mList;
    private ArrayList<T> mFilterList;
    private ArrayMap<String, Boolean> mSelectionsArray;
    private int layout;
    private boolean isSelectableMode;
    private Context mContext;
    private int mTypeGroup;

    public MyListAdapter(Context context, FragmentAdapter fragment, ArrayList<T> arrayList, @LayoutRes int layout,
                         @Nullable ArrayList<String> selectedItems, boolean isSelectableMode) {
        Log.i(TAG, "MyListAdapter: was created");
        mList = arrayList;
        mFilterList = mList;
        mFragment = fragment;
        mContext = context;
        setSelectionsArray(selectedItems);
        this.isSelectableMode = isSelectableMode;
        this.layout = layout;
    }

    public MyListAdapter(Context context, FragmentAdapter fragment, ArrayList<T> arrayList, @LayoutRes int layout,
                         @Nullable ArrayList<String> selectedItems, boolean isSelectableMode, int typeGroup) {
        this(context, fragment, arrayList, layout, selectedItems, isSelectableMode);
        mTypeGroup = typeGroup;
    }

    @Override
    public void onItemDismiss(RecyclerView.ViewHolder viewHolder, int flag) {
        MySimpleHolder holder = (MySimpleHolder) viewHolder;
        holder.itemTouch(flag);
        switch (flag) {
            case ItemTouchHelper.START:

                notifyItemChanged(holder.getAdapterPosition());
                break;
            case ItemTouchHelper.END:
                break;
        }
    }

    private void setSelectionsArray(@Nullable ArrayList<String> selectedItems) {
        if (mSelectionsArray == null) {
            mSelectionsArray = new ArrayMap<>();
        }

        if (selectedItems != null && !selectedItems.isEmpty()) {
            for (String item : selectedItems) {
                mSelectionsArray.put(item, true);
            }
        }
    }

    public ArrayList<String> getSelectedStringArray() {
        ArrayList<String> strings = new ArrayList<>();

        for (int i = 0; i < mSelectionsArray.size(); i++) {
            strings.add(mSelectionsArray.keyAt(i));
        }
        return strings;
    }

    @NonNull
    @Override
    public MySimpleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        MySimpleHolder holder;
//        Log.i(TAG, "onCreateViewHolder: was created " + temp++);
        switch (layout) {
            case GROUP_HOLDER:
                view = LayoutInflater.from(parent.getContext()).inflate(GROUP_HOLDER, parent, false);
                holder = new GroupListHolder(view, this);
                break;
            case WORD_HOLDER:
                view = LayoutInflater.from(parent.getContext()).inflate(WORD_HOLDER, parent, false);
                holder = new WordListHolder(view, this);
                break;
            default:
                throw new NullPointerException("Holder is null");
        }
        Log.i(TAG, "onCreateViewHolder");

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MySimpleHolder holder, int position) {
        SimpleParent item = mFilterList.get(position);
        Log.i(TAG, "onBindViewHolder: position " + position + " mSelectionsArray.size(): " + mSelectionsArray.size() + " mList.size(): " + mList.size());

        holder.onBind(item, mSelectionsArray.containsKey(item.getUUIDString()));
    }

    public int getCountSelectedItems() {
        return mSelectionsArray.size();
    }

    public boolean isAllItemSelected() {
        // getItemCount возращает size() у mFilterList, но в если используется MODE_SELECT, то
        // MODE_SEARCH отключается, а значит mFilterList = mList
        Log.i(TAG, "isAllItemSelected: mSelectionsArray.size(): " + mSelectionsArray.size() + " mList.size(): " + mList.size());
        return getItemCount() == getCountSelectedItems();
    }

    public int setAllItemSelected(boolean select) {
        int size = mList.size();

        if (select) {
            T item;
            for (int i = 0; i < size; i++) {
                item = mList.get(i);
                mSelectionsArray.put(item.getUUIDString(), true);
            }
        } else {
            mSelectionsArray.clear();

        }
        notifyDataSetChanged();
        Log.i(TAG, "notifyDataSetChanged");
        Log.i(TAG, "mSelectionsArray.size(): " + mSelectionsArray.size() + " mList.size(): " + mList.size());
        return mSelectionsArray.size();
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

            if (mSelectionsArray.containsKey(id)) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    public void remove(SimpleParent item) {
        int index = mList.indexOf(item);
        if (index != -1) {
            notifyItemRemoved(index);
        }
        Log.i(TAG, "SimpleParent " + item.toString() + "was removed");
        mSelectionsArray.remove(item.getUUIDString());
        mList.remove(item);
    }

    public void remove(ArrayList<? extends SimpleParent> groups) {
        for (SimpleParent g : groups) {
            remove(g);
        }
    }

    public boolean add(T item) {
        return mList.add(item);
    }

    public void add(int position, T item) {
        mList.add(position, item);
    }

    public FragmentAdapter getFragment() {
        return mFragment;
    }

    public void putSelectedItem(String key, boolean value) {
        if (value) {
            mSelectionsArray.put(key, true);
        } else {
            mSelectionsArray.remove(key);
        }
        mFragment.updateUI();
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }

    public Resources getResources() {
        return mContext.getResources();
    }

    @Override
    public Filter getFilter() {
        return new MyFilter();
    }

    public boolean isSelectableMode() {
        return isSelectableMode;
    }

    public void setSelectableMode(boolean selectableMode) {
        isSelectableMode = selectableMode;
        if (!selectableMode) {
//            setSelectedValueToAllItems(selectableMode);
            setAllItemSelected(false);
        } else {
            mFragment.changeSelectableMode(true);
        }
    }

    public void setSelectableMode(boolean selectableMode, int position) {
        setSelectableMode(selectableMode);
        if (selectableMode) {
//            int count = getItemCount();
            Log.i(TAG, "setSelectableMode: position " + position);
            if (position > 0) {
                notifyItemRangeChanged(0, position);
            }
            notifyItemRangeChanged(position, mFilterList.size());
        }
    }

    public int getTypeGroup() {
        return mTypeGroup;
    }

    public void setTypeGroup(int type) {
        mTypeGroup = type;
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
            Log.i(TAG, "publishResults");
            notifyDataSetChanged();
        }
    }
}
