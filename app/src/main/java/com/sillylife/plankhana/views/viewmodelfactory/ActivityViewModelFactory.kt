package com.sillylife.plankhana.views.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sillylife.plankhana.views.BaseActivity

class ActivityViewModelFactory(private val activity: BaseActivity) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {
//            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> return MainActivityViewModel(activity) as T
//            modelClass.isAssignableFrom(SuggestChannelActivityViewModel::class.java) -> return SuggestChannelActivityViewModel(activity) as T
//            modelClass.isAssignableFrom(ChannelEpisodeReorderActivityViewModel::class.java) -> return ChannelEpisodeReorderActivityViewModel(activity) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}