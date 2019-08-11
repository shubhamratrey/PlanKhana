package com.sillylife.plankhana.widgets

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.sillylife.plankhana.R
import com.sillylife.plankhana.utils.ImageManager
import java.io.File

class UIComponentAddPhoto : FrameLayout {

    private var mBgImageIv: ImageView? = null
    private var mBottomImageIv: ImageView? = null
    var mImageView: ImageView? = null

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
            attrs, R.styleable.UIComponentAddPhoto
        )
        styleAttrs.recycle()
    }

    private fun initView() {
        val view: View? = View.inflate(context, R.layout.ui_component_add_image, null)
        mBgImageIv = view?.findViewById(R.id.bgImageIv)
        mImageView = view?.findViewById(R.id.centerCameraIv)
        mBottomImageIv = view?.findViewById(R.id.bottomCameraIv)

        addView(view)
    }

    var isPictureSet: Boolean = false
    var imageUri: Uri? = null

    fun setPicture(path:String){
        isPictureSet = true
        imageUri = Uri.parse(path)
        ImageManager.loadImage(mBgImageIv!!,path)
        mImageView?.visibility = View.GONE
        mBottomImageIv?.visibility = View.VISIBLE
    }

    fun getFile(): File? {
        var file:File? = null
        if(imageUri != null){
            file = File(imageUri?.path)
        }
        return file
    }
}