package com.sillylife.plankhana.views.adapter

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sillylife.plankhana.R
import com.sillylife.plankhana.models.Dish
import com.sillylife.plankhana.utils.ImageManager
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_home_dish.*

class HouseDishesAdapter(val context: Context,
                         val dishList: ArrayList<Dish>,
                         val listener: (Any, Int) -> Unit) : RecyclerView.Adapter<HouseDishesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_home_dish, parent, false)
        return ViewHolder(view, viewType)
    }

    override fun getItemCount(): Int {
        return dishList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishList[holder.adapterPosition]
        ImageManager.loadImage(holder.dishImageIv, dish.dishImage)
        holder.dishNameTv.text = dish.dishName
        if (dish.userList!= null && dish.userList?.size!!> 0 ){
            holder.bhaiyaPhotoIv.setUserImage(dish.userList)
            holder.bhaiyaPhotoIv.visibility = View.VISIBLE
        } else {
            holder.bhaiyaPhotoIv.visibility = View.GONE
        }
    }

    class ViewHolder(override val containerView: View?, val viewType: Int) : RecyclerView.ViewHolder(containerView!!), LayoutContainer

    class GridItemDecoration(gridSpacingPx: Int, gridSize: Int) : RecyclerView.ItemDecoration() {
        private var mSizeGridSpacingPx: Int = gridSpacingPx
        private var mGridSize: Int = gridSize

        private var mNeedLeftSpacing = false

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val frameWidth = ((parent.width - mSizeGridSpacingPx.toFloat() * (mGridSize - 1)) / mGridSize).toInt()
            val padding = parent.width / mGridSize - frameWidth
            val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewAdapterPosition
            if (itemPosition < mGridSize) {
                outRect.top = 0
            } else {
                outRect.top = mSizeGridSpacingPx / 2
            }
            if (itemPosition % mGridSize == 0) {
                outRect.left = 0
                outRect.right = padding
                mNeedLeftSpacing = true
            } else if ((itemPosition + 1) % mGridSize == 0) {
                mNeedLeftSpacing = false
                outRect.right = 0
                outRect.left = padding
            } else if (mNeedLeftSpacing) {
                mNeedLeftSpacing = false
                outRect.left = mSizeGridSpacingPx - padding
                if ((itemPosition + 2) % mGridSize == 0) {
                    outRect.right = mSizeGridSpacingPx - padding
                } else {
                    outRect.right = mSizeGridSpacingPx / 2
                }
            } else if ((itemPosition + 2) % mGridSize == 0) {
                mNeedLeftSpacing = false
                outRect.left = mSizeGridSpacingPx / 2
                outRect.right = mSizeGridSpacingPx - padding
            } else {
                mNeedLeftSpacing = false
                outRect.left = mSizeGridSpacingPx / 2
                outRect.right = mSizeGridSpacingPx / 2
            }
            outRect.bottom = 0
        }
    }

    class WrapContentGridLayoutManager(mContext: Context, spanCount: Int) : GridLayoutManager(mContext, spanCount) {
        override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
            try {
                super.onLayoutChildren(recycler, state)
            } catch (e: IndexOutOfBoundsException) {
                Log.e("Error", "IndexOutOfBoundsException in RecyclerView happens")
            }
        }
    }
}