package com.sillylife.plankhana.views.activities

import android.app.PendingIntent
import android.app.SearchableInfo
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sillylife.plankhana.R
import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager
import com.sillylife.plankhana.utils.ContextWrapper
import com.sillylife.plankhana.utils.rxevents.RxBus
import com.sillylife.plankhana.utils.rxevents.RxEvent
import com.theartofdev.edmodo.cropper.CropImage
import java.util.*

open class BaseActivity : AppCompatActivity() {

    private lateinit var mSearchable: SearchableInfo
    private var mVoiceWebSearchIntent: Intent? = null
    private var mVoiceAppSearchIntent: Intent? = null

    override fun attachBaseContext(newBase: Context?) {
        val defaultLang = getDefaultAppLanguage()
        var context = newBase
        if (!TextUtils.isEmpty(defaultLang) && !defaultLang.equals("en", ignoreCase = true)) {
            val locale = Locale(defaultLang)
            context = ContextWrapper.wrap(newBase!!, locale)
        }
        super.attachBaseContext(context)
    }

    private fun getDefaultAppLanguage(): String {
//        val remoteLang = FirebaseRemoteConfigManager.getString(RemoteConfigKeys.APP_LANGUAGE)
        val localLang = SharedPreferenceManager.getAppLanguage()
        return localLang!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = ContextCompat.getColor(this, R.color.colorAccent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            RxBus.publish(RxEvent.ActivityResult(requestCode, resultCode, data))
            return
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun setSearchableInfo(searchableInfo: SearchableInfo) {
        this.mSearchable = searchableInfo
    }

    fun onVoiceClicked() {
        // guard against possible race conditions
        mVoiceWebSearchIntent = Intent(RecognizerIntent.ACTION_WEB_SEARCH)
        mVoiceWebSearchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mVoiceWebSearchIntent?.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
        )
        mVoiceAppSearchIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mVoiceAppSearchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (mSearchable == null) {
            return
        }
        val searchable = mSearchable
        try {
            if (searchable.voiceSearchLaunchWebSearch) {
                val webSearchIntent = createVoiceWebSearchIntent(mVoiceWebSearchIntent!!, searchable)
                startActivity(webSearchIntent)
            } else if (searchable.voiceSearchLaunchRecognizer) {
                val appSearchIntent = createVoiceAppSearchIntent(mVoiceAppSearchIntent!!, searchable)
                startActivity(appSearchIntent)
            }
        } catch (e: ActivityNotFoundException) {
        }

    }

    /**
     * Create and return an Intent that can launch the voice search activity for web search.
     */
    private fun createVoiceWebSearchIntent(baseIntent: Intent, searchable: SearchableInfo): Intent {
        val voiceIntent = Intent(baseIntent)
        val searchActivity = searchable.searchActivity
        voiceIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, searchActivity?.flattenToShortString())
        return voiceIntent
    }

    /**
     * Create and return an Intent that can launch the voice search activity, perform a specific
     * voice transcription, and forward the results to the searchable activity.
     *
     * @param baseIntent The voice app search intent to start from
     * @return A completely-configured intent ready to send to the voice search activity
     */
    private fun createVoiceAppSearchIntent(baseIntent: Intent, searchable: SearchableInfo): Intent {
        val searchActivity = searchable.searchActivity

        // create the necessary intent to set up a search-and-forward operation
        // in the voice search system.   We have to keep the bundle separate,
        // because it becomes immutable once it enters the PendingIntent
        val queryIntent = Intent(Intent.ACTION_SEARCH)
        queryIntent.component = searchActivity
        val pending = PendingIntent.getActivity(
            this@BaseActivity, 0, queryIntent,
            PendingIntent.FLAG_ONE_SHOT
        )

        // Now set up the bundle that will be inserted into the pending intent
        // when it's time to do the search.  We always build it here (even if empty)
        // because the voice search activity will always need to insert "QUERY" into
        // it anyway.
        val queryExtras = Bundle()

        // Now build the intent to launch the voice search.  Add all necessary
        // extras to launch the voice recognizer, and then all the necessary extras
        // to forward the results to the searchable activity
        val voiceIntent = Intent(baseIntent)

        // Add all of the configuration options supplied by the searchable's metadata
        var languageModel = RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        var prompt: String? = null
        var language: String? = null
        var maxResults = 1

        val resources = resources
        if (searchable.voiceLanguageModeId != 0) {
            languageModel = resources.getString(searchable.voiceLanguageModeId)
        }
        if (searchable.voicePromptTextId != 0) {
            prompt = resources.getString(searchable.voicePromptTextId)
        }
        if (searchable.voiceLanguageId != 0) {
            language = resources.getString(searchable.voiceLanguageId)
        }
        if (searchable.voiceMaxResults != 0) {
            maxResults = searchable.voiceMaxResults
        }

        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, languageModel)
        voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
        voiceIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, maxResults)
        voiceIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, searchActivity?.flattenToShortString())

        // Add the values that configure forwarding the results
        voiceIntent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT, pending)
        voiceIntent.putExtra(RecognizerIntent.EXTRA_RESULTS_PENDINGINTENT_BUNDLE, queryExtras)

        return voiceIntent
    }

    fun showToast(message: String, length: Int) {
        if (this != null && !isFinishing) {
            Toast.makeText(this, message, length).show()
        }
    }
}