package com.cscyxp.buer.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cscyxp.buer.MyApp
import com.cscyxp.finance.dao.WatchlistDao
import com.cscyxp.finance.entity.WatchlistEntity

@Database(
    entities = [WatchlistEntity::class],
    version = 5,
    exportSchema = false)
abstract class AppDataBase: RoomDatabase() {
    companion object {
        val instance: AppDataBase by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(
                MyApp.appContext,
                AppDataBase::class.java,
                "buer_database"
            ).addMigrations(
                MIGRATION_4_5
            ).build()
        }
    }

    abstract fun watchlistDao(): WatchlistDao
}