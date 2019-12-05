package com.anadol.rememberwords.myList;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.anadol.rememberwords.fragments.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> implements ItemTouchHelperAdapter {
    private static final String TAG = "my_recycler_adapter";
    private ArrayList mList;
    private ArrayList mFilterList;
    private @LayoutRes int mLayoutRes;
    private Listeners mListener;
    private CreatorAdapter mCreator;
    private SortItems mSortItems;


    public MyRecyclerAdapter(ArrayList list,@LayoutRes int layoutRes) {
        setList(list);
        mLayoutRes = layoutRes;
    }
    

    public interface Listeners{
        void onClick(View view, int position);
        boolean onLongClick(View view, int position);
    }

    public void setListener(Listeners listener) {
        mListener = listener;
    }


    public interface CreatorAdapter{
        void createHolderItems(MyViewHolder holder);
        void bindHolderItems(MyViewHolder holder);
        void myOnItemDismiss(int position, int flag);
    }

    public void setCreatorAdapter(CreatorAdapter creatorAdapter){
        mCreator = creatorAdapter;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        MyViewHolder viewHolder = new MyViewHolder(inflater,viewGroup,mLayoutRes);
        if (mCreator != null){
            mCreator.createHolderItems(viewHolder);
        }else System.out.println(true);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, int i) {


        if (mCreator != null){
            mCreator.bindHolderItems(viewHolder);
        }else System.out.println(true);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null && viewHolder.getAdapterPosition()!=RecyclerView.NO_POSITION){
                    mListener.onClick(v,viewHolder.getAdapterPosition());
                }else {
                    Log.e(TAG, "Listener == null or position == RecyclerView.NO_POSITION");
                }
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mListener != null){
                    return mListener.onLongClick(v,viewHolder.getAdapterPosition());
                }
                return false;
            }
        });
            /*
            TextView group = viewHolder.textGroup;
            ImageView imageView = viewHolder.mImageView;
            group.setText(mList.get(i).toString().toUpperCase());
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
*/

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public interface SortItems {
        void sortList();

    }

    public void setSortItems(SortItems sortItems){
        mSortItems = sortItems;
    }

    public void sortList(){
        mSortItems.sortList();
        notifyDataSetChanged();
    }


    public ArrayList getList() {
        return mFilterList;
    }

    public void setList(ArrayList list) {
        mList = list;
        mFilterList = list;
        if (!mList.isEmpty()&& mSortItems!=null) sortList();
    }

    @Override
    public void onItemDismiss(RecyclerView.ViewHolder viewHolder, int flag) {
        int position = viewHolder.getAdapterPosition();

        if (mCreator != null){
            mCreator.myOnItemDismiss(position, flag);
        }
    }
}
