package com.sillylife.plankhana.views.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sillylife.plankhana.R
import com.sillylife.plankhana.models.Dish
import com.sillylife.plankhana.utils.ImageManager
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_add_bhaiya.*
import kotlinx.android.synthetic.main.item_dish_horizontal.*


class DishesAdapter(val context: Context, list: ArrayList<Dish>, val listener: (Any, Int) -> Unit) : RecyclerView.Adapter<DishesAdapter.ViewHolder>() {

    private val commonItemLists = ArrayList<Any>()
    private var selectedId: Int = -1
    private val TAG = DishesAdapter::class.java.simpleName

    companion object {
        const val DISH_VIEW = 0
        const val ADD_DISH_BTN = 1
    }

    init {
        commonItemLists.addAll(list)
        commonItemLists.add(ADD_DISH_BTN)
    }

    override fun getItemViewType(position: Int): Int {
        return if (commonItemLists[position] is Dish) {
            DISH_VIEW
        } else {
            ADD_DISH_BTN
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = if (viewType == DISH_VIEW) {
            LayoutInflater.from(context).inflate(R.layout.item_dish_horizontal, parent, false)
        } else {
            LayoutInflater.from(context).inflate(R.layout.item_add_bhaiya, parent, false)
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return commonItemLists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType == DISH_VIEW) {
            setDishView(holder)
        } else if (holder.itemViewType == ADD_DISH_BTN) {
            holder.addBhaiyaBtn.text = context.getString(R.string.add_a_dish)
            holder.addBhaiyaBtn.setOnClickListener {
                listener(ADD_DISH_BTN, holder.adapterPosition)
            }
        }
    }

    private fun setDishView(holder: ViewHolder) {
        val item = commonItemLists[holder.adapterPosition] as Dish
        ImageManager.loadImage(holder.dishPhotoIv, item.dishImage)
        holder.dishNameTv.text = item.dishName
    }

    fun setId(id: Int) {
        selectedId = id
        notifyDataSetChanged()
    }

    fun addDishData(dish: Dish) {
        val oldSize = itemCount
        commonItemLists.remove(ADD_DISH_BTN)
        this.commonItemLists.add(dish)
        this.commonItemLists.add(ADD_DISH_BTN)
        if (oldSize > itemCount) {
            notifyItemRangeRemoved(itemCount, oldSize)
        } else {
            notifyItemRangeInserted(oldSize, itemCount)
        }
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

}