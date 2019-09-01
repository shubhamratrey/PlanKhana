package com.sillylife.plankhana.views.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sillylife.plankhana.R
import com.sillylife.plankhana.enums.UserType
import com.sillylife.plankhana.models.Dish
import com.sillylife.plankhana.utils.ImageManager
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_home_dish.*

class HouseDishesAdapter(val context: Context,
                         private val userType: UserType,
                         val dishList: ArrayList<Dish>,
                         val listener: (Any, View, Int) -> Unit) : RecyclerView.Adapter<HouseDishesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_home_dish, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dishList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishList[holder.adapterPosition]
        ImageManager.loadImage(holder.dishImageIv, dish.dishImage)
        holder.dishNameTv.text = dish.dishName
        if (userType == UserType.COOK && dish.userList != null && dish.userList?.size!! > 0) {
            holder.bhaiyaPhotoIv.setUsers(dish.userList)
            holder.bhaiyaPhotoIv.visibility = View.VISIBLE
        } else {
            holder.bhaiyaPhotoIv.visibility = View.GONE
        }

        holder.dishImageIv.setOnClickListener {
            listener(dish, it, holder.adapterPosition)
        }

        holder.dishImageIv?.isLongClickable = true;
        holder.dishImageIv?.setOnLongClickListener {
            Toast.makeText(context, dish.dishName, Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
    }

    class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer

}