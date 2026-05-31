package com.cscyxp.bookkeeping.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cscyxp.bookkeeping.data.dao.CategoryDao
import com.cscyxp.bookkeeping.data.dao.TransactionDao
import com.cscyxp.bookkeeping.data.entity.CategoryEntity
import com.cscyxp.bookkeeping.data.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BookkeepingDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
}
