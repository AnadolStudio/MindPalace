package com.anadolstudio.core.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class AbstractViewHolder<Data : Any>(
    view: View,
    val detailable: ActionClick<Data>?,
) : RecyclerView.ViewHolder(view) {

    val clickView: View by lazy { initClickView() }

    init {
        clickView.setOnClickListener(::onClick)
    }

    protected abstract fun initClickView(): View

    protected var data: Data? = null

    open fun onBind(data: Data) {
        this.data = data
    }

    open fun onClick(view: View) {
        data?.also { detailable?.action(it) }
    }

    open class Base<Data : Any>(view: View, detailable: ActionClick<Data>?) :
        AbstractViewHolder<Data>(view, detailable) {

        override fun initClickView(): View = itemView
    }
}