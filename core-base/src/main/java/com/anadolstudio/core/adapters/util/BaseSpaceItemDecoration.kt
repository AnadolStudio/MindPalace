package com.anadolstudio.core.adapters.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class BaseSpaceItemDecoration(
    private val padding: Rect = Rect(NORMAL_SPACE, NORMAL_SPACE, NORMAL_SPACE, NORMAL_SPACE),
) : RecyclerView.ItemDecoration() {

    constructor(space: Int = NORMAL_SPACE) : this(Rect(space, space, space, space))

    companion object {
        private const val NORMAL_SPACE = 8
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(padding)
    }

    class Horizontal(space: Int = NORMAL_SPACE) : BaseSpaceItemDecoration(Rect(space, 0, space, 0))

    class Vertical(space: Int = NORMAL_SPACE) : BaseSpaceItemDecoration(Rect(0, space, 0, space))

    class All(space: Int = NORMAL_SPACE) : BaseSpaceItemDecoration(space)
}