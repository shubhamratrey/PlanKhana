package com.sillylife.plankhana.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.sillylife.plankhana.R


class UIComponentCloseBtn : FrameLayout {

    private var iconPath: Int = -1
    private var mImageIv: ImageView? = null
    var mRootLyt: FrameLayout? = null

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
        initAttributes(attrs)
        setViews()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
        initAttributes(attrs)
        setViews()
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    private fun setViews() {
        setImageResource(iconPath)
    }

    private fun initAttributes(attrs: AttributeSet?) {
        val styleAttrs = context.obtainStyledAttributes(attrs, R.styleable.UIComponentCloseBtn)
        if (styleAttrs.hasValue(R.styleable.UIComponentCloseBtn_setImageResource)) {
            iconPath = styleAttrs.getResourceId(R.styleable.UIComponentCloseBtn_setImageResource, iconPath)
        }
        styleAttrs.recycle()
    }

    private fun initView() {
        val view: View? = View.inflate(context, R.layout.ui_component_close_btn, null)
        mImageIv = view?.findViewById(R.id.image)
        mRootLyt = view?.findViewById(R.id.root)
        addView(view)
    }

    public fun setImageResource(resId: Int) {
        mImageIv?.setImageResource(resId)
    }
}