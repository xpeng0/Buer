package com.cscyxp.bookkeeping.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cscyxp.bookkeeping.data.dao.CategoryDao
import com.cscyxp.bookkeeping.data.dao.TransactionDao
import com.cscyxp.bookkeeping.db.BookkeepingDatabase
import com.cscyxp.bookkeeping.util.RawUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BookkeepingModule {

    private lateinit var database: BookkeepingDatabase

    @Provides
    @Singleton
    fun provideClock(): Clock {
        return Clock.systemDefaultZone()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BookkeepingDatabase {
        val db = Room.databaseBuilder(
            context,
            BookkeepingDatabase::class.java,
            "bookkeeping.db"
        ).fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        val categories = RawUtil.loadCategoriesFromRaw(context)
                        database.categoryDao().insertList(categories)
                    }
                }
            })
            .build()
        database = db
        return db
    }

    @Provides
    fun provideTransactionDao(db: BookkeepingDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideCategoryDao(db: BookkeepingDatabase): CategoryDao = db.categoryDao()
}
