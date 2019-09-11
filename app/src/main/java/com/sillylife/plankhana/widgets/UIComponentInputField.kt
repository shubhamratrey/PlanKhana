package com.sillylife.plankhana.widgetsimport android.content.Contextimport android.content.res.ColorStateListimport android.graphics.drawable.Drawableimport android.os.Buildimport android.text.Editableimport android.text.TextUtilsimport android.text.TextWatcherimport android.util.AttributeSetimport android.view.Viewimport android.widget.FrameLayoutimport android.widget.TextViewimport androidx.core.content.ContextCompatimport androidx.vectordrawable.graphics.drawable.VectorDrawableCompatimport com.google.android.material.textfield.TextInputEditTextimport com.sillylife.plankhana.Rimport com.sillylife.plankhana.widgets.xcardview.CardViewclass UIComponentInputField : FrameLayout {    interface TextChangeListener {        fun onTextChanged()    }    private var titleHintColor: ColorStateList? = null    private var mTitleHint: String? = null    private var mTitleTv: TextView? = null    private var mInputHint: String? = null    private var mInputRightIcon: Int = -1    var mInputEt: TextInputEditText? = null    var mCard: CardView? = null    var mContext: Context? = null    private var textChangeListener: TextChangeListener? = null    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {        mContext = context        initView()        initAttributes(attrs)        setViews()    }    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {        mContext = context        initView()        initAttributes(attrs)        setViews()    }    constructor(context: Context) : super(context) {        mContext = context        initView()    }    private fun setViews() {        setDrawableRight()        if (mTitleHint != null && mTitleHint!!.isNotEmpty()) {            mTitleTv?.hint = mTitleHint        }        if (mInputHint != null && mInputHint!!.isNotEmpty()) {            mInputEt?.hint = mInputHint        }        //getText()    }    private fun initAttributes(attrs: AttributeSet?) {        val styleAttrs = context.obtainStyledAttributes(attrs, R.styleable.UIComponentInputField)        if (styleAttrs.hasValue(R.styleable.UIComponentInputField_titleHint))            mTitleHint = styleAttrs.getString(R.styleable.UIComponentInputField_titleHint)        if (styleAttrs.hasValue(R.styleable.UIComponentInputField_inputHint))            mInputHint = styleAttrs.getString(R.styleable.UIComponentInputField_inputHint)        if (styleAttrs.hasValue(R.styleable.UIComponentInputField_inputRightIcon)) {            mInputRightIcon =                    styleAttrs.getResourceId(R.styleable.UIComponentInputField_inputRightIcon, mInputRightIcon)        }        styleAttrs.recycle()    }    private fun initView() {        val view: View? = View.inflate(context, R.layout.ui_component_input_field, null)        mTitleTv = view?.findViewById(R.id.title)        mInputEt = view?.findViewById(R.id.input)        mCard = view?.findViewById(R.id.card)        addView(view)    }    fun setTitle(t: String) {        mInputEt?.setText(t)    }    fun setHint(t: String) {        mInputEt?.hint = t    }    fun setTitleHint(t: String) {        mTitleTv?.hint = t    }    fun getTitleHint(): String? {        return mTitleTv?.hint.toString()    }    fun setMaxLines(n: Int) {        mInputEt?.maxLines = n        mInputEt?.ellipsize = TextUtils.TruncateAt.END    }    fun setAsSelection() {        mInputEt?.hint = context.getString(R.string.click_here)        mInputEt?.isFocusable = false        mInputEt?.isClickable = true    }    fun getTitle(): String {        return mInputEt?.text.toString()    }    private fun setDrawableRight() {        if (mInputRightIcon > -1) {            try {                var icon: Drawable?                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {                    icon = VectorDrawableCompat.create(context?.resources!!, mInputRightIcon, mContext?.theme)                } else {                    icon = mContext?.resources?.getDrawable(mInputRightIcon, context.theme)                }                mInputEt?.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)            } catch (e: Exception) {                e.printStackTrace()            }            setAsSelection()        }    }    fun setOnClick(listener: (Any) -> Unit) {        setAsSelection()        mCard?.setOnClickListener {            listener(it)        }        mInputEt?.setOnClickListener {            listener(it)        }    }    fun setErrorState() {        titleHintColor = mTitleTv?.hintTextColors        mTitleTv?.setHintTextColor(ContextCompat.getColor(context!!, R.color.redAlert))        mInputEt?.isSelected = true    }    fun setNormalState() {        if (titleHintColor != null) {            mTitleTv?.setHintTextColor(titleHintColor)        }        mInputEt?.isSelected = false    }    fun setTextChangeListener(listener: TextChangeListener) {        this.textChangeListener = listener        mInputEt?.tag = true        mInputEt?.addTextChangedListener(object : TextWatcher {            override fun afterTextChanged(s: Editable?) {            }            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {            }            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {                if (mInputEt?.tag != null && mInputEt?.tag == true) {                    mInputEt?.tag = false                    return                }                textChangeListener?.onTextChanged()//                mTitleTv?.setHintTextColor(Color.parseColor("#ffff00"))//                mInputEt?.isSelected = false            }        })    }    fun setInputType(type: Int) {        mInputEt?.inputType = type    }}