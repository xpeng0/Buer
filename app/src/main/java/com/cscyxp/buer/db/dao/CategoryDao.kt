package com.cscyxp.buer.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cscyxp.buer.CategoryEntityWithChildren
import com.cscyxp.buer.Transaction
import com.cscyxp.buer.db.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoryEntity: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(categoryEntities: List<CategoryEntity>)

    @Query("SELECT * FROM categories ORDER BY id")
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY id")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories " +
            "WHERE parent_id IS NULL " +
            "ORDER BY id")
    suspend fun getTopCategories(): List<CategoryEntityWithChildren>

    @Query("SELECT * FROM categories " +
            "WHERE parent_id = :parentId " +
            "ORDER BY id")
    suspend fun getSonCategories(parentId: Long): List<CategoryEntity>



}