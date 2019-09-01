package com.sillylife.plankhana.views.fragments

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.karumi.dexter.PermissionToken
import com.sillylife.plankhana.AddResidentListMutation
import com.sillylife.plankhana.R
import com.sillylife.plankhana.constants.Constants
import com.sillylife.plankhana.managers.UploadUsersTask
import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.services.ApolloService
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.utils.DexterUtil
import com.sillylife.plankhana.views.activities.BhaiyaActivity
import com.sillylife.plankhana.views.activities.RegistrationActivity
import com.sillylife.plankhana.views.adapter.AddUsersAdapter
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.bs_dialog_add_user.view.*
import kotlinx.android.synthetic.main.fragment_add_users.*
import kotlinx.android.synthetic.main.layout_bottom_button.*
import org.jetbrains.annotations.NotNull

class AddBhaiyaFragment : BaseFragment() {

    companion object {
        var TAG = AddBhaiyaFragment::class.java.simpleName
        fun newInstance(): AddBhaiyaFragment {
            return AddBhaiyaFragment()
        }
    }

    var appDisposable: AppDisposable = AppDisposable()
    private var imageUri: Uri? = null
    private var adapter: AddUsersAdapter? = null
    private var isCommentDialogShown = false
    private var addResidentBottomSheet: Dialog? = null
    private var sheetView: View? = null
    private val userList: ArrayList<User> = ArrayList()

    var isPhoneNumberEntered = false
    var isImagePicked = false
    var isNameEntered = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_add_users, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toggleZeroCase(true)
        nextBtn?.text = getString(R.string.register)
        nextBtn?.setOnClickListener {
            if (adapter != null) {
                if (userList.size > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    addUsers(userList)
                    nextBtnProgress?.visibility = View.VISIBLE
                    nextBtn?.text = ""
                    SharedPreferenceManager.setUserRegistrationRequired(false)
                } else {
                    showToast("Please add user.", Toast.LENGTH_SHORT)
                }
            }
        }

        addYourDetailsBtn?.setOnClickListener {
            showAddBhaiyaPopup()
        }
    }

    private fun toggleZeroCase(visibility: Boolean) {
        zeroCaseLl?.visibility = if (visibility) View.VISIBLE else View.GONE
        rcv?.visibility = if (visibility) View.GONE else View.VISIBLE
    }

    private fun setAdapter(user: User) {
        if (rcv?.adapter == null) {
            toggleZeroCase(false)
            adapter = AddUsersAdapter(activity!!, user) { any, i ->
                if (any is Int && any == AddUsersAdapter.ADD_BHAIYA_BTN) {
                    showAddBhaiyaPopup()
                }
            }
            val layoutManager = LinearLayoutManager(activity)
            if (rcv?.itemDecorationCount == 0) {
                rcv?.addItemDecoration(AddUsersAdapter.ItemDecoration())
            }
            rcv?.layoutManager = layoutManager
            rcv?.adapter = adapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun addUsers(userList: ArrayList<User>) {
        val houseID = SharedPreferenceManager.getHouseId()!!
        if (houseID > 0) {
            appDisposable.add(UploadUsersTask(userList, houseID).callable {
                val keyMutation = AddResidentListMutation.builder().houseUser(it).build()
                ApolloService.buildApollo().mutate(keyMutation)?.enqueue(object :
                        ApolloCall.Callback<AddResidentListMutation.Data>() {
                    override fun onFailure(error: ApolloException) {
                        nextBtnProgress?.visibility = View.GONE
                        nextBtn?.text = getString(R.string.register)
                        Log.d(SelectBhaiyaFragment.TAG, error.toString())
                    }

                    override fun onResponse(@NotNull response: Response<AddResidentListMutation.Data>) {
                        if (isAdded) {
                            replaceFragment(SelectRoleFragment.newInstance(), SelectRoleFragment.TAG)
                        }
                    }
                })
            })
        }
    }

    private fun showAddBhaiyaPopup() {
        if (isAdded && activity != null) {
            addResidentBottomSheet = Dialog(activity!!)
            sheetView = LayoutInflater.from(activity!!).inflate(R.layout.bs_dialog_add_user, null, false)
            addResidentBottomSheet?.setContentView(sheetView!!)
            addResidentBottomSheet?.setCancelable(false)
            addResidentBottomSheet?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            addResidentBottomSheet?.window?.setGravity(Gravity.BOTTOM)
            addResidentBottomSheet?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            addResidentBottomSheet?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

            if (sheetView?.input?.mInputEt != null && sheetView?.input?.mInputEt?.hasFocus()!!) {
                sheetView?.input?.mInputEt?.clearFocus()
            }

            view?.viewTreeObserver?.addOnGlobalLayoutListener {
                if (isAdded && activity != null) {
                    val location = IntArray(2)
                    sheetView?.getLocationOnScreen(location)
                    val display = activity?.windowManager?.defaultDisplay
                    val size = Point()
                    display?.getSize(size)
                    val height = size.y
                    if (isCommentDialogShown && (location[1] + sheetView?.height!!) >= (height - resources.getDimensionPixelSize(R.dimen.dp_90))) {
                        isCommentDialogShown = false
                        addResidentBottomSheet?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                        sheetView?.input?.mInputEt?.clearFocus()
                        view?.viewTreeObserver?.removeOnGlobalLayoutListener { }
                    }
                }
            }
            Handler().postDelayed({
                isCommentDialogShown = true
            }, 250)

            sheetView?.input?.setNormalStateAll()
            sheetView?.inputPhone?.setNormalStateAll()
            sheetView?.input?.mInputEt?.setText("")
            sheetView?.inputPhone?.mInputEt?.setText("")
            sheetView?.inputPhone?.setInputType(InputType.TYPE_CLASS_PHONE)
            imageUri = null

            isNameEntered = false
            isImagePicked = false
            isPhoneNumberEntered = false

            addResidentBottomSheet?.show()

            sheetView?.closeBtn?.setOnClickListener {
                addResidentBottomSheet?.dismiss()
            }

            sheetView?.input?.mInputEt?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun afterTextChanged(editable: Editable) {
                    if (editable.length > 3) {
                        isNameEntered = true
                    }
                }
            })

            sheetView?.inputPhone?.mInputEt?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                }

                override fun afterTextChanged(editable: Editable) {
                    if (editable.length > 8) {
                        isPhoneNumberEntered = true
                    }
                }
            })

            sheetView?.changeImage?.setOnClickListener {
                DexterUtil.with(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE).setListener(object :
                        DexterUtil.DexterUtilListener {
                    override fun permissionGranted() {
                        CommonUtil.openPhoneGallery(activity!!)
                    }

                    override fun permissionDenied(token: PermissionToken?) {
                        // some time token can be null
                        if (token != null) {
                            showPermissionRequiredDialog(getString(R.string.files_permission_message))
                        }
                    }
                }).check()
            }

            sheetView?.nextBtnProgress?.visibility = View.GONE
            sheetView?.nextBtn?.setOnClickListener {
                if (validate()) {
                    val name = sheetView?.input?.mInputEt?.text.toString()
                    val phone = sheetView?.inputPhone?.mInputEt?.text.toString()
                    val user = User(name = name, imageUrl = imageUri.toString(), phone = phone)
                    userList.add(user)
                    if (adapter == null) {
                        setAdapter(user)
                    } else {
                        adapter?.addBhaiyaData(user)
                    }

                    sheetView?.input?.mInputEt?.setText("")
                    sheetView?.inputPhone?.mInputEt?.setText("")
                    imageUri = null

                    addResidentBottomSheet?.dismiss()
                }
            }
        }
    }

    fun validate(): Boolean {
        return if (!isImagePicked && !isNameEntered && !isPhoneNumberEntered) {
            sheetView?.input?.setErrorState()
            sheetView?.inputPhone?.setErrorState()
            showToast("Please upload Photo", Toast.LENGTH_SHORT)
            false
        } else if (!isImagePicked && !isNameEntered) {
            sheetView?.input?.setErrorState()
            showToast("Please upload Photo", Toast.LENGTH_SHORT)
            false
        } else if (!isPhoneNumberEntered && !isNameEntered) {
            sheetView?.input?.setErrorState()
            sheetView?.inputPhone?.setErrorState()
            false
        } else if (!isPhoneNumberEntered && !isImagePicked) {
            sheetView?.inputPhone?.setErrorState()
            showToast("Please upload Photo", Toast.LENGTH_SHORT)
            false
        } else if (!isImagePicked) {
            showToast("Please upload Photo", Toast.LENGTH_SHORT)
            false
        } else if (!isPhoneNumberEntered) {
            sheetView?.inputPhone?.setErrorState()
            false
        } else if (!isNameEntered) {
            sheetView?.input?.setErrorState()
            false
        } else {
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var mActivity: Activity? = null
        when (activity) {
            is RegistrationActivity -> mActivity = activity as RegistrationActivity
            is BhaiyaActivity -> mActivity = activity as BhaiyaActivity
        }

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.RC_EPISODE_GALLERY -> if (data != null) {
                    imageUri = data.data
                    if (this == null || imageUri == null) {
                        return
                    }
                    CropImage.activity(imageUri).setFixAspectRatio(true).setBackgroundColor(R.color.colorPrimary)
                            .setMaxZoom(4).setGuidelines(CropImageView.Guidelines.OFF).setAutoZoomEnabled(true)
                            .setAllowFlipping(false).start(mActivity!!)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {

                    val result = CropImage.getActivityResult(data)
                    if (resultCode == Activity.RESULT_OK) {
                        imageUri = result.uri
                        sheetView?.bgImageIv?.setImageURI(imageUri)
                        isImagePicked = true
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        val error = result.error
                        showToast(error.message!!, Toast.LENGTH_SHORT)
                    }
                }
            }
        }
    }
}
