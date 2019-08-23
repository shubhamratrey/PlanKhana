package com.sillylife.plankhana.views.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sillylife.plankhana.views.BaseFragment
import com.sillylife.plankhana.views.viewmodel.FindOrRegisterViewModel

class FragmentViewModelFactory(private val fragment: BaseFragment) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(FindOrRegisterViewModel::class.java) -> return FindOrRegisterViewModel(fragment) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}