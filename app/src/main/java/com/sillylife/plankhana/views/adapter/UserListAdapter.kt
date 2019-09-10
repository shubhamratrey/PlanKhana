package com.sillylife.plankhana.views.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sillylife.plankhana.R
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.utils.ImageManager
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_user_call_list.*
import java.util.*

class UserListAdapter(val context: Context, var items: ArrayList<User>, val listener: (Any) -> Unit) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user_call_list, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setChannel(holder)
    }

    private fun setChannel(holder: ViewHolder) {
        val item = items[holder.adapterPosition]
        holder.bhaiyaNameTv.text = item.name
        holder.bhaiyaPhotoIv.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_placeholder_user))
        ImageManager.loadImageCircular(holder.bhaiyaPhotoIv, item.imageUrl)
        holder.containerView?.setOnClickListener {
            listener(item)
        }
    }

    class ViewHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer
}