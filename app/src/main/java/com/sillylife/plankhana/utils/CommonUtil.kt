package com.sillylife.plankhana.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DimenRes
import com.sillylife.plankhana.PlanKhana
import com.sillylife.plankhana.constants.Constants
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.views.fragments.BhaiyaHomeFragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object CommonUtil {

    val context = PlanKhana.getInstance()
    var priorityAppList: MutableList<String> = ArrayList()

    /**
     * convert dimens to exact pixels
     */
    fun dpToPx(dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    /**
     * Checks whether text is null or empty or not
     */
    fun textIsEmpty(value: String?): Boolean {

        if (value == null)
            return true

        var empty = false

        val message = value.trim { it <= ' ' }

        if (message.isEmpty()) {
            empty = true
        }

        val isWhitespace = message.matches("^\\s*$".toRegex())

        if (isWhitespace) {
            empty = true
        }

        return empty
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    fun getDimensionPixelSize(@DimenRes dimenRes: Int): Int {
        return context.resources.getDimensionPixelSize(dimenRes)
    }

    fun showKeyboard(context: Context?) {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun hideKeyboard(context: Context) {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = (context as Activity).currentFocus ?: return
        inputManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

    fun isAppInstalled(context: Context, uri: String): Boolean {
        val pm = context.packageManager
        var app_installed: Boolean
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            app_installed = true
        } catch (e: PackageManager.NameNotFoundException) {
            app_installed = false
        } catch (e: RuntimeException) {
            app_installed = false
        }

        return app_installed
    }

    fun openPhoneGallery(activity: Activity?) {
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

    fun getDay(count: Int): String {
        val sdf = SimpleDateFormat("EEEE", Locale.US)
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DATE, count)
        val day = sdf.format(calendar.time)
        Log.d(BhaiyaHomeFragment.TAG, day.toString())
        return day
    }

    fun getDay(count: Int, locale: Locale): String {
        val sdf = SimpleDateFormat("EEEE", locale)
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DATE, count)
        val day = sdf.format(calendar.time)
        Log.d(BhaiyaHomeFragment.TAG, day.toString())
        return day
    }

    fun getShortDay(count: Int, locale: Locale): String {
        val sdf = SimpleDateFormat("EEE", locale)
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DATE, count)
        val day = sdf.format(calendar.time)
        Log.d(BhaiyaHomeFragment.TAG, day.toString())
        return day
    }

    fun userDummyData(): ArrayList<User> {
        val imageUrl = "https://media-doselect.s3.amazonaws.com/avatar_image/1V4XB5PzwqAqJV2aoKw3QnVyM/download.png"
        val list: ArrayList<User> = ArrayList()
        list.add(User(0, "Shubh", imageUrl))
        list.add(User(1, "Shivam", imageUrl))
        list.add(User(2, "Rohit", imageUrl))
        list.add(User(3, "Shashank", imageUrl))
        list.add(User(4, "Rajat", imageUrl))
        return list
    }
}