package com.sillylife.plankhana.views.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.sillylife.plankhana.R

class UIComponentCircularGradientButton : FrameLayout {

    private var iconPath: Int = -1
    private var mImageIv: ImageView? = null
    private var view: View? = null

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
        val styleAttrs = context.obtainStyledAttributes(
                attrs, R.styleable.UIComponentCircularGradientButton
        )

        if (styleAttrs.hasValue(R.styleable.UIComponentCircularGradientButton_setImage)) {
            iconPath = styleAttrs.getResourceId(R.styleable.UIComponentCircularGradientButton_setImage, iconPath)
        }
        styleAttrs.recycle()
    }

    private fun initView() {
        val view: View? = View.inflate(context, R.layout.ui_component_circular_gradient_icon, null)
        mImageIv = view?.findViewById(R.id.image)
        this.view = view
        addView(view)
    }

    fun setImageResource(resId: Int) {
        mImageIv?.setImageResource(resId)
    }

    fun getView(): View {
        return this.view!!
    }

}