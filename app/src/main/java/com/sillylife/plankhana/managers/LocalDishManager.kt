package com.sillylife.plankhana.managers

import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager
import com.sillylife.plankhana.models.Dish

object LocalDishManager {

    fun getResidentDishes(): ArrayList<Dish> {
        return SharedPreferenceManager.getMyFoods()
    }

    fun addDish(dish: Dish) {
        var toAdd = true
        getResidentDishes().forEach {
            if (it.id == dish.id) {
                toAdd = false
            }
        }

        if (toAdd) {
            val dishes: ArrayList<Dish> = ArrayList()
            dishes.addAll(getResidentDishes())
            dishes.add(dish)
            SharedPreferenceManager.setMyFoods(dishes)
        }
    }

    fun removeDish(dish: Dish) {
        val dishes: ArrayList<Dish> = ArrayList()
        getResidentDishes().forEach {
            if (it.id != dish.id) {
                dishes.add(it)
            }
        }
        SharedPreferenceManager.setMyFoods(dishes)
    }

    fun getSavedDishesIds(): ArrayList<Int> {
        val list: ArrayList<Int> = ArrayList()
        getResidentDishes().forEach {
            list.add(it.id!!)
        }
        return list
    }

    fun getTempSavedDishesIds(): ArrayList<Int> {
        val list: ArrayList<Int> = ArrayList()
        getTempDishList().forEach {
            list.add(it.id!!)
        }
        return list
    }

    fun getFavouriteDishes(): ArrayList<Dish> {
        return SharedPreferenceManager.getFavouriteDishList()
    }

    fun saveFavouriteDishes(list: ArrayList<Dish>) {
        val hs = HashSet<Dish>()
        hs.addAll(getFavouriteDishes())
        hs.addAll(list)
        val dishes: ArrayList<Dish> = ArrayList()
        dishes.addAll(hs)
        SharedPreferenceManager.setFavouriteDishList(dishes)
    }

    fun getTempDishList(): ArrayList<Dish> {
        return SharedPreferenceManager.getTemporaryDishList()
    }

    fun setTempDishList(list: ArrayList<Dish>) {
        SharedPreferenceManager.setTemporaryDishList(list)
    }

    fun addTempDish(dish: Dish) {
        getTempDishList().forEach {
            if (it.id == dish.id) {
                return
            }
        }
        val dishes: ArrayList<Dish> = ArrayList()
        dishes.add(dish)
        dishes.addAll(getTempDishList())
        SharedPreferenceManager.setTemporaryDishList(dishes)
    }

    fun removeTempDish(dish: Dish) {
        val dishes: ArrayList<Dish> = ArrayList()
        getTempDishList().forEach {
            if (it.id != dish.id) {
                dishes.add(it)
            }
        }
        SharedPreferenceManager.setTemporaryDishList(dishes)
    }

    fun clearTempDishList() {
        SharedPreferenceManager.setTemporaryDishList(ArrayList())
    }
}