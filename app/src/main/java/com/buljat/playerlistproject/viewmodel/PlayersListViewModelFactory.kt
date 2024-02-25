package com.buljat.playerlistproject.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlayersListViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayersListViewModel::class.java)) {
            return PlayersListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}