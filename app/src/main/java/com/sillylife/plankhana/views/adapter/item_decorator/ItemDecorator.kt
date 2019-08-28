package com.sillylife.plankhana.views.adapter.item_decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecorator(
        private val verticalMargin: Int,
        private val topMargin: Int,
        private val bottomMargin: Int,
        private val leftMargin: Int,
        private val rightMargin: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = topMargin
        }
        outRect.left = leftMargin
        outRect.right = rightMargin

        if (bottomMargin != 0 && parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
            outRect.bottom = bottomMargin
        } else {
            outRect.bottom = verticalMargin
        }
    }
}