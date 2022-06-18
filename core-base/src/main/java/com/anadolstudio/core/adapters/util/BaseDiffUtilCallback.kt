package com.anadolstudio.core.adapters.util

import androidx.recyclerview.widget.DiffUtil

open class BaseDiffUtilCallback<Data>(
    var oldList: List<Data>,
    var newList: List<Data>
) : DiffUtil.Callback() {

    constructor() : this(emptyList(), emptyList())

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    fun updateData(oldList: List<Data>, newList: List<Data>) {
        this.oldList = oldList
        this.newList = newList
    }

    /**
     * По умолчанию в обоих вариантах полностью сравнивает обьекты
     */
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        areContentsTheSame(oldItemPosition, newItemPosition)

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        equalsItems(oldItemPosition, newItemPosition)

    private fun equalsItems(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem?.equals(newItem) ?: false
    }

}