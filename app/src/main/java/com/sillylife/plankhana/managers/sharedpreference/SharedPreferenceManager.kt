package com.sillylife.plankhana.managers.sharedpreference

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sillylife.plankhana.enums.LanguageEnum
import com.sillylife.plankhana.enums.UserType
import com.sillylife.plankhana.models.Dish
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.utils.CommonUtil

object SharedPreferenceManager {

    private val mSharedPreferences = SharedPreferences
    private const val APP_LANGUAGE = "app_language"
    private const val HOUSE_ID = "house_id"
    private const val USER = "user"
    private const val USER_REGISTRATION_REQUIRED = "user_registration_required"
    private const val IS_ROLE_SELECT = "is_role_select"
    private const val RESIDENT_FOOD_LIST = "resident_food_list"
    private const val TEMPORARY_DISH_LIST = "temporary_dish_list"
    private const val FAVOURITE_DISH_LIST = "favourite_dish_list"


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

    fun isUserRegistrationRequired(): Boolean? {
        return mSharedPreferences.getBoolean(USER_REGISTRATION_REQUIRED, false)
    }

    fun setUserRegistrationRequired(isRequired: Boolean) {
        mSharedPreferences.setBoolean(USER_REGISTRATION_REQUIRED, isRequired)
    }

    fun getUserType(): UserType? {
        val s = mSharedPreferences.getString(IS_ROLE_SELECT, "")
        return if (CommonUtil.textIsEmpty(s)) {
            null
        } else {
            UserType.getByType(s!!)
        }
    }

    fun setUserType(userType: UserType) {
        mSharedPreferences.setString(IS_ROLE_SELECT, userType.type)
    }

    fun setMyFoods(dishes: ArrayList<Dish>) {
        mSharedPreferences.setString(RESIDENT_FOOD_LIST, if (dishes.size == 0) "" else Gson().toJson(dishes))
    }

    fun getMyFoods(): ArrayList<Dish> {
        var dishes: ArrayList<Dish> = ArrayList()
        val raw: String? = mSharedPreferences.getString(RESIDENT_FOOD_LIST, "")
        if (!CommonUtil.textIsEmpty(raw)) {
            dishes = Gson().fromJson(raw, object : TypeToken<ArrayList<Dish>>() {
            }.type)
        }
        return dishes
    }

    fun setTemporaryDishList(dishes: ArrayList<Dish>) {
        mSharedPreferences.setString(TEMPORARY_DISH_LIST, if (dishes.size == 0) "" else Gson().toJson(dishes))
    }

    fun getTemporaryDishList(): ArrayList<Dish> {
        var dishes: ArrayList<Dish> = ArrayList()
        val raw: String? = mSharedPreferences.getString(TEMPORARY_DISH_LIST, "")
        if (!CommonUtil.textIsEmpty(raw)) {
            dishes = Gson().fromJson(raw, object : TypeToken<ArrayList<Dish>>() {
            }.type)
        }
        return dishes
    }

    fun setFavouriteDishList(dishes: ArrayList<Dish>) {
        mSharedPreferences.setString(FAVOURITE_DISH_LIST, if (dishes.size == 0) "" else Gson().toJson(dishes))
    }

    fun getFavouriteDishList(): ArrayList<Dish> {
        var dishes: ArrayList<Dish> = ArrayList()
        val raw: String? = mSharedPreferences.getString(FAVOURITE_DISH_LIST, "")
        if (!CommonUtil.textIsEmpty(raw)) {
            dishes = Gson().fromJson(raw, object : TypeToken<ArrayList<Dish>>() {}.type)
        }
        return dishes
    }
}