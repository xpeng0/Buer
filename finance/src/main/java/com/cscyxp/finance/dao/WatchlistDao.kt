package com.cscyxp.finance.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cscyxp.finance.entity.WatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist")
    fun getWatchlistFlow(): Flow<List<WatchlistEntity>>

    @Query("SELECT * FROM watchlist")
    suspend fun getWatchlist(): List<WatchlistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(entity: WatchlistEntity)

    @Delete
    suspend fun deleteOne(entity: WatchlistEntity)
}