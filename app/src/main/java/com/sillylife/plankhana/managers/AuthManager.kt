package com.sillylife.plankhana.managers

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.sillylife.plankhana.PlanKhana
import java.util.concurrent.TimeUnit

object AuthManager {

    val activity = PlanKhana.getInstance()
    val anonymousUserId = FirebaseAuth.getInstance().currentUser?.uid

    interface IAuthCredentialCallback {
        fun onAuthCompleted()
        fun onAuthError(error: String)
        fun onCodeSent(verificationId: String)
    }

    interface IAuthCredentialLogoutCallback {
        fun onUserSignedOutSuccessfully()
    }

    interface IAuthCredentialAnonymouslyLoginCallback {
        fun onSignInAnonymously(userId:String)
    }

    private val TAG = AuthManager::class.java.simpleName
    private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    private const val AUTO_RETRIEVAL_TIMEOUT_SECONDS: Long = 60
    private const val VERIFICATION_ID_KEY = "verification_id"
    private var mVerificationId: String? = null

    fun signInWithGoogle(credential: AuthCredential, mListener: IAuthCredentialCallback) {
        FirebaseAuth.getInstance().currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "linkWithCredential:success")
                    } else {
                        Log.d(TAG, "linkWithCredential:failure" + task.exception)
                    }
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                setTask(task, mListener, anonymousUserId!!)
                            }
                }
    }

    fun signInWithPhone(mobile: String, mListener: IAuthCredentialCallback) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(mobile, AUTO_RETRIEVAL_TIMEOUT_SECONDS, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, phoneCallbacks(mListener))
    }

    fun resendVerificationCode(mobile: String, mListener: IAuthCredentialCallback) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(mobile, AUTO_RETRIEVAL_TIMEOUT_SECONDS, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, phoneCallbacks(mListener), mResendToken)
    }

    fun signInAnonymously(mListener: IAuthCredentialAnonymouslyLoginCallback) {
        FirebaseAuth.getInstance().signInAnonymously()
                .addOnCompleteListener { task ->
                    Log.d(TAG, "Inside onComplete of signInAnonymously: issuccess - " + task.isSuccessful + " and user - " + task.result?.user?.uid!!)
                    if (task.isSuccessful) {
                        mListener.onSignInAnonymously(task.result?.user?.uid!!)
                    }
                }
    }

    fun logoutUser(activity: Activity, mListener: IAuthCredentialLogoutCallback) {
        FirebaseAuth.getInstance().signOut()
        val mGoogleSignInClient = GoogleSignIn.getClient(
                activity,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        )
        mGoogleSignInClient.signOut()?.addOnCompleteListener(activity) {
            if (it.isSuccessful) {
                mListener.onUserSignedOutSuccessfully()
            }
        }
    }

    private fun setTask(@NonNull task: Task<AuthResult>, mListener: IAuthCredentialCallback, anonymousUserId: String) {
        Log.d(TAG, "inside onComplete of signInWithCredential. isSuccessful - " + task.isSuccessful)
        if (task.isSuccessful) {
            mListener.onAuthCompleted()
        } else {
            when {
                task.exception is FirebaseAuthInvalidCredentialsException -> mListener.onAuthError("Invalid credentials.")
                task.exception is FirebaseAuthUserCollisionException -> mListener.onAuthError("User with this email already exists. Please try logging in with Google.")
                else -> Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun phoneCallbacks(mListener: IAuthCredentialCallback): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        return object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneCredential(credential, mListener)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.d(TAG, "onVerificationFailed: FirebaseAuthInvalidCredentialsException")
                    mListener.onAuthError(e.toString())

                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.d(TAG, "onVerificationFailed: FirebaseTooManyRequestsException")
                    mListener.onAuthError((e.toString()))
                }
            }

            override fun onCodeSent(p0: String, token: PhoneAuthProvider.ForceResendingToken) {
                mVerificationId = p0
                mResendToken = token
                Log.d(TAG, "onCodeSent: $p0\n$token")
                mListener.onCodeSent(p0!!)

            }


        }

    }

    fun signInWithPhoneCredential(credential: PhoneAuthCredential, mListener: IAuthCredentialCallback) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { _task ->
                    if (_task.isSuccessful) {
                        Log.d(TAG, "signInWithCredential:success")
                    } else {
                        Log.d(TAG, "signInWithCredential:failure" + _task.exception)
                    }
                    setTask(_task, mListener, anonymousUserId!!)
                }
    }

    fun onSaveInstanceState(@NonNull outState: Bundle) {
        outState.putString(VERIFICATION_ID_KEY, mVerificationId)
    }

    fun onRestoreInstanceState(@Nullable savedInstanceState: Bundle?) {
        if (mVerificationId == null && savedInstanceState != null) {
            mVerificationId = savedInstanceState.getString(VERIFICATION_ID_KEY)
        }
    }
}