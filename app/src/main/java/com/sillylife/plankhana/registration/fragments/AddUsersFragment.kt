package com.sillylife.plankhana.registration.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.sillylife.plankhana.R
import com.sillylife.plankhana.constants.Constants
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.views.BaseFragment
import com.sillylife.plankhana.registration.activities.RegistrationActivity
import com.sillylife.plankhana.views.adapter.AddUsersAdapter
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_add_users.*

class AddUsersFragment : BaseFragment() {

    companion object {
        var TAG = AddUsersFragment::class.java.simpleName
        fun newInstance(): AddUsersFragment {
            val fragment = AddUsersFragment()
            return fragment
        }

        private const val RC_GALLERY = 123
    }

    var appDisposable: AppDisposable = AppDisposable()
    private var tempId = 0
    private var imageUri: Uri? = null
    private var tempUserId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_add_users, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageUrl = "https://media-doselect.s3.amazonaws.com/avatar_image/1V4XB5PzwqAqJV2aoKw3QnVyM/download.png"
        val list: ArrayList<User> = ArrayList()
        list.add(User(tempId, "", ""))
        setAdapter(list)


        nextBtn.setOnClickListener {
            addFragment(SelectRoleFragment.newInstance(), SelectRoleFragment.TAG)
        }
    }

    private fun setAdapter(list: ArrayList<User>) {
        if (rcv.adapter == null) {
            val adapter = AddUsersAdapter(activity!!) { any, i ->
                if (any is User) {
                    choosePhotoFromGallery(any)
                }
            }
            val layoutManager = LinearLayoutManager(activity)
            if (rcv.itemDecorationCount == 0) {
                rcv.addItemDecoration(AddUsersAdapter.ItemDecoration())
            }
            rcv.layoutManager = layoutManager

            rcv.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
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

    private fun choosePhotoFromGallery(any: User) {
        tempUserId = any.id!!
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"

        try {
            activity?.startActivityForResult(galleryIntent, Constants.RC_EPISODE_GALLERY)
        } catch (e: ActivityNotFoundException) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(galleryIntent))
            activity?.startActivityForResult(chooserIntent, Constants.RC_EPISODE_GALLERY)
        }
    }
}
