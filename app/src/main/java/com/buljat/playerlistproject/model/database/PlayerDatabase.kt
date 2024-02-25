package com.buljat.playerlistproject.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.buljat.playerlistproject.model.PlayerDbEntity

@Database(entities = [PlayerDbEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PlayerDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao

    companion object {

        // Object providing singleton instance of database
        @Volatile
        private var INSTANCE: PlayerDatabase? = null

        fun getDatabase(context: Context): PlayerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlayerDatabase::class.java,
                    "players.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}