package com.buljat.playerlistproject.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "players")
data class PlayerDbEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val sport: String = "",
    @ColumnInfo(name = "birth_date") val birthDate: Date?,
    val gender: String = "",
    val image: String = ""
)