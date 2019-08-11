package com.sillylife.plankhana.views.adapter

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.RecyclerView
import com.sillylife.plankhana.R
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.utils.ImageManager
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_add_bhaiya.*
import kotlinx.android.synthetic.main.item_bhaiya_layout.*


class AddUsersAdapter(val context: Context, val listener: (Any, Int) -> Unit) :
    RecyclerView.Adapter<AddUsersAdapter.ViewHolder>() {

    val commonItemLists = ArrayList<Any>()
    private val TAG = AddUsersAdapter::class.java.simpleName
    private var tempId = 0
    private var tempName: String? = null
    private var tempImageUrl: String? = null

    companion object {
        const val BHAIYA_VIEW = 0
        const val ADD_BHAIYA_BTN = 1
    }

    init {
        commonItemLists.add(User(tempId))
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
                if (tempName != null && !CommonUtil.textIsEmpty(tempName) && tempImageUrl != null && !CommonUtil.textIsEmpty(tempImageUrl)) {
//                    updateItem(User(tempId, tempName, tempImageUrl))
                    tempName = null
                    tempImageUrl = null

                    tempId += 1
                    addBhaiyaData(User(tempId))
                }
            }
        }
    }

    private fun setBhaiyaView(holder: ViewHolder) {
        val item = commonItemLists[holder.adapterPosition] as User

        if (item.imageUrl != null && !CommonUtil.textIsEmpty(item.imageUrl)) {
            holder.bgImageIv.setImageURI(Uri.parse(item.imageUrl))
            ImageManager.loadImage(holder.bgImageIv, item.imageUrl)
        } else {
            holder.bgImageIv.setImageBitmap(null)
        }

        holder.input.setTitleHint(context.getString(R.string.bhaiya_number, (holder.adapterPosition + 1).toString()))
        holder.input.mInputEt?.imeOptions = EditorInfo.IME_ACTION_DONE
        if (!CommonUtil.textIsEmpty(item.name)) {
            holder.input.setTitle(item.name!!)
        } else {
            holder.input.setTitle("")
        }

        holder.changeImage.setOnClickListener {
            listener(item, holder.adapterPosition)
        }

        holder.input.mInputEt?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                CommonUtil.hideKeyboard(context)
            }
            false
        }

        holder.input.mInputEt?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                (commonItemLists[holder.adapterPosition] as User).name = editable.toString()
                tempName = editable.toString()
            }
        })

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
                tempImageUrl = items.imageUrl
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
                outRect.left = CommonUtil.dpToPx(17)
                outRect.right = CommonUtil.dpToPx(7)

                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = CommonUtil.dpToPx(17)
                }
            } else {
                if (parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
                    outRect.bottom = CommonUtil.dpToPx(80)
                }
            }
        }

    }
}