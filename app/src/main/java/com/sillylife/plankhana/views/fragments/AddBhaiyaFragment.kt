package com.sillylife.plankhana.views.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.sillylife.plankhana.views.activities.RegistrationActivity
import com.sillylife.plankhana.services.ApolloService
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.utils.DexterUtil
import com.sillylife.plankhana.views.adapter.AddUsersAdapter
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
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
    private var tempUserId = 0
    private var adapter: AddUsersAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_add_users, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        nextBtn?.text = getString(R.string.register)
        nextBtn?.setOnClickListener {
            if (adapter != null) {
//                if (adapter?.getUserList()?.size!! > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    addUsers(adapter?.getUserList()!!)
//                    nextBtnProgress?.visibility = View.VISIBLE
//                    nextBtn?.text = ""
//                    SharedPreferenceManager.setUserRegistrationRequired(false)
//                } else {
//                    showToast("Please add user.", Toast.LENGTH_SHORT)
//                }
            }
        }
    }

    private fun setAdapter() {
        if (rcv?.adapter == null) {
            adapter = AddUsersAdapter(activity!!) { any, i ->
                if (any is User) {
                    tempUserId = any.id!!
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
        if (houseID > 0){
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
                            nextBtnProgress?.visibility = View.GONE
                            nextBtn?.text = getString(R.string.register)
                            replaceFragment(SelectRoleFragment.newInstance(), SelectRoleFragment.TAG)
                        }
                    }
                })
            })
        }
    }

    fun DCd(){
        /*holder.input.setTitleHint(context.getString(R.string.bhaiya_number, (holder.adapterPosition + 1).toString()))
        holder.input.mInputEt?.imeOptions = EditorInfo.IME_ACTION_DONE
        if (!CommonUtil.textIsEmpty(item.name)) {
            holder.usernameTv.text = item.name!!
        } else {
            holder.usernameTv.setTitle("")
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
        })*/
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
                            val adapter = rcv.adapter as AddUsersAdapter
                            adapter.updateImageUrl(User(tempUserId, imageUri.toString()))
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, getString(R.string.failed), Toast.LENGTH_SHORT).show()
                        }
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        val error = result.error
                        showToast(error.message!!, Toast.LENGTH_SHORT)
                    }
                }
            }
        }
    }
}
