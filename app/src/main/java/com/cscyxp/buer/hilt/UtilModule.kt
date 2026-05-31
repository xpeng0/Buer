package com.cscyxp.buer.hilt

import com.cscyxp.buer.db.AppDataBase
import com.cscyxp.finance.dao.WatchlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UtilModule {
    @Provides
    @Singleton
    fun provideFinanceWatchlistDao(): WatchlistDao {
        return AppDataBase.instance.watchlistDao()
    }
}