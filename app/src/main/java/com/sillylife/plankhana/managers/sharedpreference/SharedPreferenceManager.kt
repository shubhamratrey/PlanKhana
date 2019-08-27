package com.sillylife.plankhana.managers.sharedpreference

import com.google.gson.Gson
import com.sillylife.plankhana.enums.LanguageEnum
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.utils.CommonUtil

object SharedPreferenceManager {

    private val mSharedPreferences = SharedPreferences
    private const val APP_LANGUAGE = "app_language"
    private const val HOUSE_ID = "house_id"
    private const val USER = "user"


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

    fun setUser(user: User) {
        mSharedPreferences.setString(USER, Gson().toJson(user))
    }

    fun getUser(): User? {
        val raw: String = mSharedPreferences.getString(USER, "")!!
        if (!CommonUtil.textIsEmpty(raw)) {
            return Gson().fromJson(raw, User::class.java)
        }
        return null
    }
}