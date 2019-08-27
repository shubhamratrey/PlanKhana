package com.sillylife.plankhana.views.adapter.item_decorator

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WrapContentGridLayoutManager(mContext: Context, spanCount: Int) : GridLayoutManager(mContext, spanCount) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("Error", "IndexOutOfBoundsException in RecyclerView happens")
        }
    }
}