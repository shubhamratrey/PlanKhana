package com.sillylife.plankhana.views.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sillylife.plankhana.R
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.utils.ImageManager
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_add_bhaiya_circular.*
import kotlinx.android.synthetic.main.item_bhaiya.*


class SelectBhaiyaAdapter(val context: Context, list: ArrayList<User>, val listener: (Any, Int) -> Unit) : RecyclerView.Adapter<SelectBhaiyaAdapter.ViewHolder>() {

    private val commonItemLists = ArrayList<Any>()
    private var selectedId: Int = -1
    private val TAG = SelectBhaiyaAdapter::class.java.simpleName

    companion object {
        const val BHAIYA_VIEW = 0
        const val ADD_BHAIYA_BTN = 1
    }

    init {
        commonItemLists.addAll(list)
        commonItemLists.add(ADD_BHAIYA_BTN)
    }

    override fun getItemViewType(position: Int): Int {
        return if (commonItemLists[position] is User) {
            BHAIYA_VIEW
        } else {
            ADD_BHAIYA_BTN
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = if (viewType == BHAIYA_VIEW) {
            LayoutInflater.from(context).inflate(R.layout.item_bhaiya, parent, false)
        } else {
            LayoutInflater.from(context).inflate(R.layout.item_add_bhaiya_circular, parent, false)
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return commonItemLists.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType == BHAIYA_VIEW) {
            setBhaiyaView(holder)
        } else if (holder.itemViewType == ADD_BHAIYA_BTN) {
            holder.addBhaiyaBtn.setOnClickListener {
                listener(ADD_BHAIYA_BTN, holder.adapterPosition)
            }
        }
    }

    private fun setBhaiyaView(holder: ViewHolder) {
        val item = commonItemLists[holder.adapterPosition] as User

        if (item.imageUrl != null && !CommonUtil.textIsEmpty(item.imageUrl)) {
            holder.bhaiyaPhotoIv.setImageURI(Uri.parse(item.imageUrl))
            ImageManager.loadImageCircular(holder.bhaiyaPhotoIv, item.imageUrl)
        }

        if (!CommonUtil.textIsEmpty(item.name)) {
            holder.bhaiyaNameTv.text = item.name!!
        }

        holder.bhaiyaPhotoIv.setOnClickListener {
            listener(item, holder.adapterPosition)
        }

        if (item.id == selectedId) {
            holder.tick.visibility = View.VISIBLE
        } else {
            holder.tick.visibility = View.GONE
        }
    }

    fun setId(id: Int) {
        selectedId = id
        notifyDataSetChanged()
    }

    fun addBhaiyaData(user: User) {
        val oldSize = itemCount
        commonItemLists.remove(ADD_BHAIYA_BTN)
        this.commonItemLists.add(user)
        this.commonItemLists.add(ADD_BHAIYA_BTN)
        if (oldSize > itemCount) {
            notifyItemRangeRemoved(itemCount, oldSize)
        } else {
            notifyItemRangeInserted(oldSize, itemCount)
        }
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

}