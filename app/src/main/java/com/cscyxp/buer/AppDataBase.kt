package com.cscyxp.buer

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
abstract class AppDataBase: RoomDatabase() {
    companion object {
        val instance: AppDataBase by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(
                MyApp.appContext, // 全局 Application Context
                AppDataBase::class.java,
                "buer_database"
            ).build()
        }
    }

    abstract fun transactionDao(): TransactionDao
}