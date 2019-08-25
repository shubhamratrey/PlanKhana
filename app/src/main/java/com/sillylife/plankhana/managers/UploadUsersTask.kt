package com.sillylife.plankhana.managers

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.sillylife.plankhana.enums.ImageType
import com.sillylife.plankhana.enums.UserType
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.type.Plankhana_houses_houseuser_insert_input
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.utils.FileUtils
import com.sillylife.plankhana.utils.MapObjects
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.CountDownLatch

class UploadUsersTask(var userList: ArrayList<User>, val houseId: Int) {

    companion object {
        val TAG = UploadUsersTask::class.java.simpleName
    }

    private var latch: CountDownLatch = CountDownLatch(1)

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun callable(listener: (ArrayList<Plankhana_houses_houseuser_insert_input>) -> Unit): Disposable {
        return Observable.fromCallable {
            val list: ArrayList<Plankhana_houses_houseuser_insert_input> = ArrayList()
            userList.forEachIndexed { index, user ->
                ImageUploadTask(FileUtils.getPath(CommonUtil.context, Uri.parse(user.imageUrl!!))!!, ImageType.USER_IMAGE, object : ImageUploadTask.Callback {
                    override fun onUploadSuccess(imageUri: Uri) {
                        Log.d(TAG, "onUploadSuccess  ${imageUri.toString()}")
                        list.add(MapObjects.addResident(houseId, user.name!!, imageUri.toString(), Math.random().toString(), UserType.RESIDENT))
                        if (userList.size - 1 == index) {
                            latch.countDown()
                        }
                    }

                    override fun onUploadFailure(error: String) {

                    }

                    override fun onProgress(progress: Double) {

                    }

                })
            }
            try {
                latch.await()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            list
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (listener != null) {
                        listener(it)
                    }
                }
    }
}