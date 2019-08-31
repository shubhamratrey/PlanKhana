package com.sillylife.plankhana.views.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sillylife.plankhana.R
import com.sillylife.plankhana.managers.LocalDishManager
import com.sillylife.plankhana.models.Dish
import com.sillylife.plankhana.models.DishStatus
import com.sillylife.plankhana.utils.ImageManager
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_add_bhaiya.*
import kotlinx.android.synthetic.main.item_dish_horizontal.*


class DishesAdapter(val context: Context, list: ArrayList<Dish>, val listener: (Any, String, Int) -> Unit) : RecyclerView.Adapter<DishesAdapter.ViewHolder>() {

    private val commonItemLists = ArrayList<Any>()
    private val TAG = DishesAdapter::class.java.simpleName
    private var type = ""

    companion object {
        const val Add_A_DISH: String = "add_a_dish"
        const val REMOVE: String = "remove"
        const val ADD: String = "add_a_dish"
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
            if (type.contentEquals(Add_A_DISH)) {
                holder.addBhaiyaBtn.text = context.getString(R.string.add_a_dish)
            } else {
                holder.addBhaiyaBtn.text = context.getString(R.string.request_a_new_dish)
            }
            holder.addBhaiyaBtn.setOnClickListener {
                if (type.contentEquals(Add_A_DISH)) {
                    listener(ADD_DISH_BTN, "", holder.adapterPosition)
                } else {
                    listener(Add_A_DISH, "", holder.adapterPosition)
                }
            }
        }
    }

    private fun setDishView(holder: ViewHolder) {
        val item = commonItemLists[holder.adapterPosition] as Dish
        ImageManager.loadImage(holder.dishPhotoIv, item.dishImage)
        holder.dishNameTv.text = item.dishName
        holder.removeIv.visibility = View.GONE
        holder.addIv.visibility = View.GONE
        holder.addedIv.visibility = View.GONE

        if (type.equals(Add_A_DISH, true)) {
            holder.removeIv.visibility = View.VISIBLE
        } else {
            if (item.dishStatus != null) {
                when {
                    item.dishStatus!!.alreadyAdded -> {
                        holder.addedIv.setColorFilter(ContextCompat.getColor(context, R.color.textSubHeading), android.graphics.PorterDuff.Mode.SRC_IN)
                        holder.addedIv.visibility = View.VISIBLE
                        holder.dishPhotoIv.alpha = 0.5f
                        holder.dishNameTv.alpha = 0.5f
                        holder.addedIv.alpha = 0.5f
                    }
                    item.dishStatus!!.added -> {
                        holder.addedIv.setColorFilter(ContextCompat.getColor(context, R.color.tick_green), android.graphics.PorterDuff.Mode.SRC_IN)
                        holder.addedIv.visibility = View.VISIBLE
                        holder.dishPhotoIv.alpha = 1f
                        holder.dishNameTv.alpha = 1f
                        holder.addedIv.alpha = 1f
                    }
                    item.dishStatus!!.add -> {
                        holder.addIv.visibility = View.VISIBLE
                        holder.dishPhotoIv.alpha = 1f
                        holder.dishNameTv.alpha = 1f
                    }
                }
            }
        }

        holder.dishStatusFl.setOnClickListener {
            if (type.equals(Add_A_DISH, true)) {
                listener(item, REMOVE, holder.adapterPosition)
            } else {
                when {
                    item.dishStatus!!.added -> {
                        listener(item, REMOVE, holder.adapterPosition)
                    }
                    item.dishStatus!!.add -> {
                        listener(item, ADD, holder.adapterPosition)
                    }
                }
            }
        }
    }

    fun setType(type: String) {
        this.type = type
    }

    fun changeDishStatus(dish: Dish) {
        if (commonItemLists.size > 0) {
            for (position in commonItemLists.indices) {
                if (commonItemLists[position] is Dish && (commonItemLists[position] as Dish).id == dish.id && dish.dishStatus != null) {
                    when {
                        dish.dishStatus!!.added -> {
                            (commonItemLists[position] as Dish).dishStatus = DishStatus(add = true, added = false)
                            notifyItemChanged(position)
                        }
                        dish.dishStatus!!.add -> {
                            (commonItemLists[position] as Dish).dishStatus = DishStatus(add = false, added = true)
                            notifyItemChanged(position)
                        }
                    }
                    break
                }
            }
        }
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

    fun removeItem(dish: Dish) {
        if (commonItemLists.size > 0) {
            for (i in commonItemLists.indices) {
                if (commonItemLists[i] is Dish && (commonItemLists[i] as Dish).id == dish.id) {
                    commonItemLists.removeAt(i)
                    notifyItemRemoved(i)
                    notifyItemRangeChanged(i, commonItemLists.size)
                    break
                }
            }
        }
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

}