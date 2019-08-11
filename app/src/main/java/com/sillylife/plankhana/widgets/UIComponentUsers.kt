package com.sillylife.plankhana.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.sillylife.plankhana.R
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.utils.ImageManager

class UIComponentUsers : FrameLayout {

    private var mFanCountTv: TextView? = null
    private var mRootLyt: LinearLayout? = null
    private var mContainer: LinearLayout? = null

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
        initAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
        initAttributes(attrs)
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    private fun initAttributes(attrs: AttributeSet?) {
        val styleAttrs = context.obtainStyledAttributes(
                attrs, R.styleable.UIComponentUsers
        )
        styleAttrs.recycle()
    }

    private fun initView() {
        val view: View? = View.inflate(context, R.layout.ui_component_users, null)
        mFanCountTv = view?.findViewById(R.id.fansCountTv)
        mRootLyt = view?.findViewById(R.id.lrLyt)
        mContainer = view?.findViewById(R.id.container)
        addView(view)
    }

    fun setUserImage(list: ArrayList<User>?) {
        for (i in 0 until list?.size!!) {
            val frameLayout = FrameLayout(context)
            frameLayout.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            frameLayout.setBackgroundResource(R.drawable.d_circle_white)
            val imageView = AppCompatImageView(context)
            imageView.layoutParams = LinearLayout.LayoutParams(CommonUtil.dpToPx(20),CommonUtil.dpToPx(20))
            ImageManager.loadImageCircular(imageView, list[i].imageUrl)

            frameLayout.addView(imageView)
            val p = context.resources.getDimensionPixelSize(R.dimen.dp_1_5)
            frameLayout.setPadding(p, p, p, p)
            mContainer?.addView(frameLayout, i)
        }
    }
}