package com.buljat.playerlistproject.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.buljat.playerlistproject.model.PlayerDbEntity

@Dao
interface PlayerDao {

    @Query("SELECT * FROM players")
    fun players(): List<PlayerDbEntity>

    @Query("SELECT * FROM players WHERE id = :id")
    fun getPlayer(id: Int): PlayerDbEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerDbEntity)

    @Delete
    suspend fun deletePlayer(player: PlayerDbEntity)

    @Update
    suspend fun updatePlayer(player: PlayerDbEntity)
}