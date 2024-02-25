package com.buljat.playerlistproject.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.buljat.playerlistproject.model.PlayerDbEntity
import com.buljat.playerlistproject.model.database.PlayerDao
import com.buljat.playerlistproject.model.database.PlayerDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PlayersListViewModel(application: Application) : AndroidViewModel(application) {
    private val playerDao: PlayerDao

    private var _player: MutableLiveData<PlayerDbEntity?> = MutableLiveData()
    val player: LiveData<PlayerDbEntity?> = _player

    private var _playerList: MutableLiveData<List<PlayerDbEntity>?> = MutableLiveData()
    val playerList: LiveData<List<PlayerDbEntity>?> = _playerList

    init {
        playerDao = PlayerDatabase.getDatabase(application.applicationContext).playerDao()
        players()
    }

    fun players() = viewModelScope.launch(Dispatchers.IO) {
        _playerList.postValue(playerDao.players())
    }

    fun getPlayer(playerId: Int) = viewModelScope.launch(Dispatchers.IO) {
        if (playerId != -1) {
            _player.postValue(playerDao.getPlayer(playerId))
        } else _player.postValue(null)
    }

    fun insertPlayer(playerDbEntity: PlayerDbEntity) = viewModelScope.launch(Dispatchers.IO) {
        playerDao.insertPlayer(playerDbEntity)
        Log.i("XXX", "inserted player id: ${playerDbEntity.id}")
    }

    fun deletePlayer() = viewModelScope.launch(Dispatchers.IO) {
        _player.value?.let { playerDao.deletePlayer(it) }
    }

    fun updatePlayer(playerDbEntity: PlayerDbEntity) = viewModelScope.launch(Dispatchers.IO) {
        playerDao.updatePlayer(playerDbEntity)
    }
}