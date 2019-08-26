package com.sillylife.plankhana.managers.sharedpreference

import com.sillylife.plankhana.enums.LanguageEnum

object SharedPreferenceManager {

    private val mSharedPreferences = SharedPreferences
    private const val APP_LANGUAGE = "app_language"
    private const val HOUSE_ID = "house_id"


    fun getAppLanguage(): String? {
        return mSharedPreferences.getString(APP_LANGUAGE, "hi")
    }

    fun getAppLanguageEnum(): LanguageEnum {
        val raw = mSharedPreferences.getString(APP_LANGUAGE, "hi")
        return LanguageEnum.getLanguageByCode(raw!!)
    }

    fun setAppLanguage(language: String) {
        mSharedPreferences.setString(APP_LANGUAGE, language)
    }

    fun clear() {
        val appLanguage = getAppLanguage()
        mSharedPreferences.clearPrefs()
        setAppLanguage(appLanguage!!)
    }

    fun getHouseId(): Int? {
        return mSharedPreferences.getInt(HOUSE_ID, -1)
    }

    fun setHouseId(id: Int) {
        mSharedPreferences.setInt(HOUSE_ID, id)
    }
}