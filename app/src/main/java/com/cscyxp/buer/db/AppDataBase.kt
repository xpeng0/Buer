package com.cscyxp.buer.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cscyxp.buer.MyApp
import com.cscyxp.buer.RawUtil
import com.cscyxp.buer.Transaction
import com.cscyxp.buer.db.dao.CategoryDao
import com.cscyxp.buer.db.dao.TransactionDao
import com.cscyxp.buer.db.entity.CategoryEntity
import com.cscyxp.buer.db.entity.TransactionEntity
import com.cscyxp.finance.dao.WatchlistDao
import com.cscyxp.finance.entity.WatchlistEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TransactionEntity::class, CategoryEntity::class, WatchlistEntity::class],
    version = 3,
    exportSchema = false)
abstract class AppDataBase: RoomDatabase() {
    companion object {
        val instance: AppDataBase by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(
                MyApp.appContext, // 全局 Application Context
                AppDataBase::class.java,
                "buer_database"
            ).addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3
            ).addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        // 读取json文件中的默认分类
                        val defaultCategories = RawUtil.loadCategoriesFromRaw()
                        //插入数据库中
                        AppDataBase.instance.categoryDao().insertList(defaultCategories)
                    }
                }
            }).build()
        }
    }

    abstract fun transactionDao(): TransactionDao

    abstract fun categoryDao(): CategoryDao

    abstract fun watchlistDao(): WatchlistDao
}