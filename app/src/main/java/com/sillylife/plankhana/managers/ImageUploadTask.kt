package com.sillylife.plankhana.managers


import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.sillylife.plankhana.enums.ImageType
import java.io.File
import java.util.*


class ImageUploadTask(imagePath: String, imageType: ImageType, val listener: Callback) {

    interface Callback {
        fun onUploadSuccess(imageUri: Uri)
        fun onUploadFailure(error: String)
        fun onProgress(progress: Double)
    }

    companion object {
        val TAG = ImageUploadTask::class.java.simpleName
    }

    private var reference: StorageReference? = null
    private var uploadTask: UploadTask? = null

    init {
        val name = Math.random().toString().substring(7) + Date().time
        reference = FirebaseStorage.getInstance().reference.child("${imageType.type}/$name.jpg")
        uploadTask = reference?.putFile(Uri.fromFile(File(imagePath)))
        startUploading()
    }

    private fun startUploading() {
        Log.d(TAG, "Upload Started")
        uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation reference?.downloadUrl
        })?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                listener.onUploadSuccess(task.result!!)
                Log.d(TAG, task.result?.toString())
            } else {
                Log.d(TAG, "Something went wrong")
            }
        }?.addOnFailureListener {
            listener.onUploadFailure(it.message.toString())
            Log.d(TAG, it.message.toString())
        }

        uploadTask?.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            Log.d(TAG, progress.toString())
            listener.onProgress(progress)
        }
    }

    fun cancel() {
        Log.d(TAG, "Upload Cancelled")
        // Cancel the upload
        uploadTask?.cancel()
    }

    fun pause() {
        // Pause the upload
        uploadTask?.pause()
    }

    fun resume() {
        // Resume the upload
        uploadTask?.resume()
    }
}