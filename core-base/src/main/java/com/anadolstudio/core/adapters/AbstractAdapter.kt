package com.anadolstudio.core.adapters

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.anadolstudio.core.adapters.selectablecontroller.SelectableController
import com.anadolstudio.core.adapters.util.BaseDiffUtilCallback

abstract class AbstractAdapter<Data : Any, Holder : AbstractViewHolder<Data>>(
    protected var dataList: MutableList<Data> = mutableListOf(),
    protected val detailable: ActionClick<Data>?
) : RecyclerView.Adapter<Holder>() {

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(dataList[position])
    }

    abstract val diffUtilCallback: BaseDiffUtilCallback<Data>?

    open fun setData(list: MutableList<Data>) {
        diffUtilCallback
            ?.let { callback ->
                callback.updateData(dataList, list)
                val diffResult = DiffUtil.calculateDiff(callback, false)
                dataList = list
                diffResult.dispatchUpdatesTo(this)
            }
            ?: let { dataList = list }
    }

    open fun addData(list: List<Data>) {
        dataList.addAll(list)
        notifyItemRangeInserted(dataList.size - list.size, list.size)
    }

    override fun getItemCount(): Int = dataList.size

    abstract class Base<Data : Any, Holder : AbstractViewHolder<Data>>(
        dataList: MutableList<Data> = mutableListOf(),
        detailable: ActionClick<Data>?,
    ) : AbstractAdapter<Data, Holder>(dataList, detailable) {

        override val diffUtilCallback: BaseDiffUtilCallback<Data>?
            get() = BaseDiffUtilCallback()
    }

    abstract class Selectable<Data : Any, Holder : AbstractSelectableViewHolder<Data>>(
        dataList: MutableList<Data> = mutableListOf(),
        detailable: ActionClick<Data>?,
    ) : Base<Data, Holder>(dataList, detailable) {

        protected open val selectableController: SelectableController<Holder> =
            object : SelectableController.Abstract<Data, Holder>() {

                override fun updateView(holder: Holder, isSelected: Boolean, state: Int) =
                    notifyItemChanged(state)
            }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.onBind(dataList[position], position == selectableController.getCurrentPosition())
        }

        open fun clearSelectedItem() = selectableController.clear()

        override fun getItemCount(): Int = dataList.size
    }
}