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
import com.sillylife.plankhana.utils.ImageManager

class UIComponentFans : FrameLayout {

    private var mFanCountTv: TextView? = null
    private var mFanOneImageIv: AppCompatImageView? = null
    private var mFanTwoImageIv: AppCompatImageView? = null
    private var mFanThirdImageIv: AppCompatImageView? = null
    private var mFanOneLyt: FrameLayout? = null
    private var mFanTwoLyt: FrameLayout? = null
    private var mFanThirdLyt: FrameLayout? = null
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
                attrs, R.styleable.UIComponentFans
        )
        styleAttrs.recycle()
    }

    private fun initView() {
        val view: View? = View.inflate(context, R.layout.ui_component_fans, null)
        mFanOneImageIv = view?.findViewById(R.id.fanOneImageIv)
        mFanTwoImageIv = view?.findViewById(R.id.fanTwoImageIv)
        mFanThirdImageIv = view?.findViewById(R.id.fanThridImageIv)

        mFanOneLyt = view?.findViewById(R.id.fanOneFl)
        mFanTwoLyt = view?.findViewById(R.id.fanTwoFl)
        mFanThirdLyt = view?.findViewById(R.id.fanThirdFl)

        mFanCountTv = view?.findViewById(R.id.fansCountTv)
        mRootLyt = view?.findViewById(R.id.lrLyt)
        mContainer = view?.findViewById(R.id.container)
        addView(view)
    }


    fun setUsers(values: ArrayList<User>?) {
        val ivList: ArrayList<AppCompatImageView> = ArrayList()
        ivList.add(mFanOneImageIv!!)
        ivList.add(mFanTwoImageIv!!)
        ivList.add(mFanThirdImageIv!!)

        ivList.forEachIndexed { index, appCompatImageView ->
            appCompatImageView.visibility = View.GONE
        }

        if (values != null && values.size > 0) {
            for (i in 0 until values.size.coerceIn(0,3)) {
                if (values[i].imageUrl != null) {
                    ImageManager.loadImageCircular(ivList[i], values[i].imageUrl)
                    ivList[i].visibility = View.VISIBLE
                }
            }

            if (values.size >3){
                mFanCountTv?.text = "+${values.size-3}"
                mFanCountTv?.visibility = View.VISIBLE
            } else {
                mFanCountTv?.visibility = View.GONE
            }
        }
    }
}