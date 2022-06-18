package com.anadolstudio.core.adapters.selectablecontroller

import androidx.recyclerview.widget.RecyclerView
import com.anadolstudio.core.adapters.AbstractSelectableViewHolder

interface SelectableController<Holder : RecyclerView.ViewHolder> {

    fun setCurrentSelectedItem(holder: Holder?)

    fun updateView(holder: Holder, isSelected: Boolean, state: Int)

    fun savePosition(holder: Holder): Int

    fun getCurrentPosition(): Int

    fun selectableItemIsExist(): Boolean

    fun setStartItem(holder: Holder)

    fun clear()

    abstract class Abstract<Data : Any, Holder : AbstractSelectableViewHolder<Data>> :
        SelectableController<Holder> {

        protected var selectedItem: Holder? = null
        protected var state = -1

        abstract override fun updateView(holder: Holder, isSelected: Boolean, state: Int)

        override fun savePosition(holder: Holder): Int = holder.adapterPosition

        override fun getCurrentPosition(): Int = state

        override fun setCurrentSelectedItem(holder: Holder?) {
            selectedItem?.also { updateView(it, false, state) }

            selectedItem = holder
            selectedItem ?: also {
                state = -1
                return
            }

            selectedItem?.also {
                state = savePosition(it)
                updateView(it, true, state)
            }
        }

        override fun clear() {
            setCurrentSelectedItem(null)
        }

        override fun setStartItem(holder: Holder) {
            selectedItem = holder
            state = savePosition(holder)
        }

        override fun selectableItemIsExist() = selectedItem != null
    }

    class Base<Data : Any, Holder : AbstractSelectableViewHolder<Data>> : Abstract<Data, Holder>() {

        override fun updateView(holder: Holder, isSelected: Boolean, state: Int) {
            holder.onBind(isSelected)
        }

    }
}