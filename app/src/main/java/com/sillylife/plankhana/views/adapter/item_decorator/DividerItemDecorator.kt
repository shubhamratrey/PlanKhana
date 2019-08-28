package com.sillylife.plankhana.views.adapter.item_decorator

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sillylife.plankhana.utils.CommonUtil

class DividerItemDecorator(private val mDivider: Drawable?,
                           private val verticalMargin: Int,
                           private val topMargin: Int,
                           private val bottomMargin: Int,
                           private val leftMargin: Int,
                           private val rightMargin: Int) : RecyclerView.ItemDecoration() {

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val dividerLeft = parent.paddingLeft + CommonUtil.dpToPx(20)
        val dividerRight = parent.width - (parent.paddingRight + CommonUtil.dpToPx(20))

        val childCount = parent.childCount
        for (i in 0..childCount - 2) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val dividerTop = child.bottom + params.bottomMargin + (verticalMargin / 2)
            val dividerBottom = dividerTop + mDivider!!.intrinsicHeight

            mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            mDivider.draw(canvas)
        }
    }

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