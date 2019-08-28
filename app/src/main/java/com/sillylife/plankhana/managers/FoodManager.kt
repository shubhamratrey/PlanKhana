package com.sillylife.plankhana.managers

import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager
import com.sillylife.plankhana.models.Dish

object FoodManager {

    fun getMyFood(): ArrayList<Dish> {
        return SharedPreferenceManager.getMyFoods()
    }

    fun addFood(dish: Dish){
        var toAdd = true
        getMyFood().forEach {
            if (it.id == dish.id){
                toAdd = false
            }
        }

        if (toAdd){
            val dishes: ArrayList<Dish> = ArrayList()
            dishes.addAll(getMyFood())
            dishes.add(dish)
            SharedPreferenceManager.setMyFoods(dishes)
        }
    }

    fun removeFood(dish: Dish){
        val dishes: ArrayList<Dish> = getMyFood()
        dishes.forEach {
            if (it.id == dish.id){
                dishes.remove(it)
            }
        }
        SharedPreferenceManager.setMyFoods(dishes)
    }
}