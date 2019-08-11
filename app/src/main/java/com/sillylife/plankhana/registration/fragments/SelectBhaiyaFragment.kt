package com.sillylife.plankhana.registration.fragments

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.view.*
import android.widget.Toast
import com.karumi.dexter.PermissionToken
import com.sillylife.plankhana.R
import com.sillylife.plankhana.bhaiya_side.activities.BhaiyaActivity
import com.sillylife.plankhana.constants.Constants
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.registration.activities.RegistrationActivity
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.utils.DexterUtil
import com.sillylife.plankhana.views.BaseFragment
import com.sillylife.plankhana.views.adapter.SelectBhaiyaAdapter
import com.sillylife.plankhana.widgets.CustomBottomSheetDialog
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.bs_dialog_add_user.view.*
import kotlinx.android.synthetic.main.fragment_select_bhaiya.*
import kotlinx.android.synthetic.main.item_bhaiya_layout.view.*
import kotlinx.android.synthetic.main.layout_bottom_button.*

class SelectBhaiyaFragment : BaseFragment() {

    companion object {
        var TAG = SelectBhaiyaFragment::class.java.simpleName
        fun newInstance() = SelectBhaiyaFragment()
    }

    var appDisposable: AppDisposable = AppDisposable()
    var adapter: SelectBhaiyaAdapter? = null
    private var isCommentDialogShown = false
    private var sheetView: View? = null
    private var imageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_select_bhaiya, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nextBtn.text = getString(R.string.string_continue)
        nextBtn.setOnClickListener {
            val intent = Intent(activity, BhaiyaActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        setAdapter(CommonUtil.userDummyData())
    }

    private fun setAdapter(list: ArrayList<User>) {
        if (rcv.adapter == null) {
            adapter = SelectBhaiyaAdapter(activity!!, list) { any, i ->
                if (any is User) {
                    adapter?.setId(any.id!!)
                } else if (any is Int && any == SelectBhaiyaAdapter.ADD_BHAIYA_BTN) {
                    showAddBhaiyaPopup()
                }
            }
            if (rcv.itemDecorationCount == 0) {
                rcv.addItemDecoration(SelectBhaiyaAdapter.GridItemDecoration(context?.resources?.getDimensionPixelSize(R.dimen.dp_8)!!, 3))
            }
            rcv.layoutManager = SelectBhaiyaAdapter.WrapContentGridLayoutManager(context!!, 3)
            rcv.adapter = adapter
        }
    }

    private fun showAddBhaiyaPopup() {
        if (isAdded && activity != null) {
            val dialog = Dialog(activity!!)
            sheetView = LayoutInflater.from(activity!!).inflate(R.layout.bs_dialog_add_user, null, false)
            dialog.setContentView(sheetView!!)
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setGravity(Gravity.BOTTOM)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

            sheetView?.input?.setTitleHint(context?.getString(R.string.bhaiya_number, (adapter?.itemCount!!).toString())!!)

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
                        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                        sheetView?.input?.mInputEt?.clearFocus()
                        view?.viewTreeObserver?.removeOnGlobalLayoutListener { }
                    }
                }
            }
            Handler().postDelayed({
                isCommentDialogShown = true
            }, 250)


            dialog.show()

            sheetView?.closeBtn?.setOnClickListener {
                dialog.dismiss()
            }

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

            sheetView?.nextBtn?.setOnClickListener {
                adapter?.addBhaiyaData(User(adapter?.itemCount!!, sheetView?.input?.mInputEt?.text.toString(), imageUri.toString()))
                dialog.dismiss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var mActivity: Activity? = null
        when (activity) {
            is RegistrationActivity -> mActivity = activity as RegistrationActivity
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
                        try {
                            if (imageUri?.path != null) {
                                sheetView?.bgImageIv?.setImageURI(imageUri)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showToast(getString(R.string.failed), Toast.LENGTH_SHORT)
                        }
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        val error = result.error
                        showToast(error.message!!, Toast.LENGTH_SHORT)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
    }
}
