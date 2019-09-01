package com.sillylife.plankhana.views.adapter

import android.content.Context
import android.graphics.Rect
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
import kotlinx.android.synthetic.main.item_add_bhaiya.*
import kotlinx.android.synthetic.main.item_bhaiya_layout.*


class AddUsersAdapter(val context: Context, user: User, val listener: (Any, Int) -> Unit) :
        RecyclerView.Adapter<AddUsersAdapter.ViewHolder>() {

    val commonItemLists = ArrayList<Any>()
    private val TAG = AddUsersAdapter::class.java.simpleName

    companion object {
        const val BHAIYA_VIEW = 0
        const val ADD_BHAIYA_BTN = 1
    }

    init {
        commonItemLists.add(user)
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
            LayoutInflater.from(context).inflate(R.layout.item_bhaiya_layout, parent, false)
        } else {
            LayoutInflater.from(context).inflate(R.layout.item_add_bhaiya, parent, false)
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
            ImageManager.loadImageCircular(holder.bgImageIv, item.imageUrl)
        } else {
            holder.bgImageIv.setImageBitmap(null)
        }

        if (!CommonUtil.textIsEmpty(item.name)) {
            holder.usernameTv.text = item.name!!
        } else {
            holder.usernameTv.text = ""
        }

        if (!CommonUtil.textIsEmpty(item.phone)) {
            holder.userPhoneNumberTv.text = item.phone!!
        } else {
            holder.userPhoneNumberTv.text = ""
        }
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

    fun updateItem(items: User) {
        for (i in commonItemLists.indices) {
            if (commonItemLists[i] is User && items.id == (commonItemLists[i] as User).id) {
                commonItemLists[i] = items
                notifyItemChanged(i)
                break
            }
        }
    }

    fun updateImageUrl(items: User) {
        for (i in commonItemLists.indices) {
            if (commonItemLists[i] is User && items.id == (commonItemLists[i] as User).id) {
                (commonItemLists[i] as User).imageUrl = items.imageUrl
                notifyItemChanged(i)
                break
            }
        }
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer

    class ItemDecoration : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val viewType = parent.adapter!!.getItemViewType(parent.getChildAdapterPosition(view))
            if (viewType == BHAIYA_VIEW) {
                outRect.left = CommonUtil.dpToPx(5)
                outRect.right = CommonUtil.dpToPx(5)

                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = CommonUtil.dpToPx(23)
                }
            } else {
                if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
                    outRect.bottom = CommonUtil.dpToPx(80)
                }
            }
        }

    }
}